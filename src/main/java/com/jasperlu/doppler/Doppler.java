package com.jasperlu.doppler;

import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.os.Handler;
import android.util.Log;

import com.jasperlu.doppler.FFT.FFT;

/**
 * Created by Jasper on 3/11/2015.
 *
 * To find frequency, check:
 * http://stackoverflow.com/questions/18652000/record-audio-in-java-and-determine-real-time-if-a-tone-of-x-frequency-was-played
 */
public class Doppler {
    public interface OnReadCallback {
        //bandwidths are the number to the left/to the right from the pilot tone the shift was
        public void onBandwidthRead(int leftBandwidth, int rightBandwidth);
        //for testing/graphing as well
        public void onBinsRead(double[] bins);
    }

    //prelimiary frequency stuff
    public static final float PRELIM_FREQ = 20000;
    public static final int PRELIM_FREQ_INDEX = 20000;
    public static final int MIN_FREQ = 19000;
    public static final int MAX_FREQ = 21000;

    //precalculated to optimize
    public static final int MIN_FREQ_INDEX = 882;
    public static final int MAX_FREQ_INDEX = 975;

    public static final int RELEVANT_FREQ_WINDOW = 33;
    public static final int DEFAULT_SAMPLE_RATE = 44100;

    //modded from the soundwave paper. frequency bins are scanned until the amp drops below
    // 1% of the primary tone peak
    private static final double maxVolumeRatio = 0.01;

    private AudioRecord microphone;
    private FrequencyPlayer frequencyPlayer;
    private int SAMPLE_RATE = DEFAULT_SAMPLE_RATE;

    private float frequency;
    private int freqIndex;

    private short[] buffer;
    private float[] fftRealArray;
    private int bufferSize = 2048;

    private Handler mHandler;
    private boolean repeat;

    FFT fft;
    //to smooth out the top
    private float primaryMinima;

    public Doppler() {
        //write a check to see if stereo is supported
        bufferSize = AudioRecord.getMinBufferSize(SAMPLE_RATE, AudioFormat.CHANNEL_IN_MONO, AudioFormat.ENCODING_PCM_16BIT);
        buffer = new short[bufferSize];
        Log.d("BUEFFER SIZE IS ", bufferSize + "");
        frequency = PRELIM_FREQ;
        freqIndex = PRELIM_FREQ_INDEX;

        frequencyPlayer = new FrequencyPlayer(PRELIM_FREQ);

        microphone = new AudioRecord(MediaRecorder.AudioSource.VOICE_RECOGNITION, DEFAULT_SAMPLE_RATE,
                AudioFormat.CHANNEL_IN_MONO, AudioFormat.ENCODING_PCM_16BIT, bufferSize);

        mHandler = new Handler();
    }

    private void setFrequency(float frequency) {
        this.frequency = frequency;
        this.freqIndex = fft.freqToIndex(frequency);
    }

    public boolean start(final OnReadCallback callback) {
        frequencyPlayer.play();
        boolean startedRecording = false;
        try {
            //you might get an error here if another app hasn't released the microphone
            microphone.startRecording();
            //startReading();
            repeat = true;

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    optimizeFrequency(MIN_FREQ, MAX_FREQ);
                    //assuming fft.forward was already called;
                    primaryMinima = fft.getBand(freqIndex);
                    startReading(callback);
                }
            }, 1000);

            startedRecording = true;
        } catch (Exception e) {
            e.printStackTrace();
            Log.d("DOPPLER", "start recording error");
            return false;
        }
        if (startedRecording) {
            int bufferReadResult = microphone.read(buffer, 0, bufferSize);
            bufferReadResult = getHigherP2(bufferReadResult);
            //get higher p2 because buffer needs to be "filled out" for FFT
            fftRealArray = new float[getHigherP2(bufferReadResult)];
            fft = new FFT(getHigherP2(bufferReadResult), SAMPLE_RATE);
        }
        return true;
    }

    public void startReading(final OnReadCallback callback) {
        readAndFFT();

        int primaryTone = fft.freqToIndex(frequency);


        int leftBandwidth = 0, rightBandwidth = 0;
        double normalizedVolume = 0;

        //flatten peak to minima to make detection more consistent
        flattenToMinima();

        double primaryVolume = fft.getBand(primaryTone);
        do {
            leftBandwidth++;
            double volume = fft.getBand(primaryTone - leftBandwidth);
            normalizedVolume = volume/primaryVolume;
        } while (normalizedVolume > maxVolumeRatio && leftBandwidth < RELEVANT_FREQ_WINDOW);

        do {
            rightBandwidth++;
            double volume = fft.getBand(primaryTone + rightBandwidth);
            normalizedVolume = volume/primaryVolume;
        } while (normalizedVolume > maxVolumeRatio && rightBandwidth < RELEVANT_FREQ_WINDOW);

        callback.onBandwidthRead(leftBandwidth, rightBandwidth);

        double[] array = new double[fft.specSize()];
        for (int i = 0; i < fft.specSize(); ++i) {
            array[i] = fft.getBand(i);
        }

        callback.onBinsRead(array);

        if (repeat) {
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    startReading(callback);
                }
            });
        }
    }

    public void flattenToMinima() {
        if (fft.getBand(freqIndex) < primaryMinima && fft.getBand(freqIndex) >= 10) {
            primaryMinima = fft.getBand(freqIndex);
            Log.d("NEW PRIMARY MINIMA", primaryMinima + "");
        }
        for (int i = MIN_FREQ_INDEX; i < MAX_FREQ_INDEX; ++i) {
            //apply smoothing
            if (fft.getBand(i) > primaryMinima) {
                fft.setBand(i, primaryMinima);
            }
        }
    }

    public boolean pause() {
        try {
            microphone.stop();
            frequencyPlayer.pause();
            repeat = false;
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public void optimizeFrequency(int minFreq, int maxFreq) {
        readAndFFT();
        int minInd = fft.freqToIndex(minFreq);
        int maxInd = fft.freqToIndex(maxFreq);

        int primaryInd = fft.freqToIndex(frequency);
        for (int i = minInd; i <= maxInd; ++i) {
            if (fft.getBand(i) > fft.getBand(primaryInd)) {
                primaryInd = i;
            }
        }
        setFrequency(fft.indexToFreq(primaryInd));
        Log.d("NEW PRIMARY IND", fft.indexToFreq(primaryInd) + "");
    }

    public void readAndFFT() {

        int bufferReadResult = microphone.read(buffer, 0, bufferSize);

        for (int i = 0; i < bufferReadResult; i++) {
            fftRealArray[i] = (float) buffer[i] / Short.MAX_VALUE; //32768.0
        }

        //apply windowing
        for (int i = 0; i < bufferReadResult/2; ++i) {
            // Calculate & apply window symmetrically around center point
            // Hanning (raised cosine) window
            float winval = (float)(0.5+0.5*Math.cos(Math.PI*(float)i/(float)(bufferReadResult/2)));
            if (i > bufferReadResult/2)  winval = 0;
            fftRealArray[bufferReadResult/2 + i] *= winval;
            fftRealArray[bufferReadResult/2 - i] *= winval;
        }

        // zero out first point (not touched by odd-length window)
        fftRealArray[0] = 0;

        fft.forward(fftRealArray);
    }

    //mostly here for testing purposes at this point
    public double[] attemptRead() {
        readAndFFT();

        int primaryTone = fft.freqToIndex(frequency);

        double primaryVolume = fft.getBand(primaryTone);
        //taken from the soundwave paper. frequency bins are scanned until the amp drops below
        // 10% of the primary tone peak
        double maxVolumeRatio = 0.01;

        int leftBandwidth = 0, rightBandwidth = 0;
        double normalizedVolume = 0;


        //Log.d("Doppler", "Primary volume: " + primaryVolume + "");

        int max = 0;
        for (int i = 0; i < fft.specSize(); ++i) {
            if (fft.getBand(i) > fft.getBand(max)) {
                max = i;
            }
        }
        if (max != 929) {
            Log.d("Doppler", "Highest Freq Bin Mag is " + fft.getBand(max));
            Log.d("Doppler", "Highest Freq Bin " + max);
        }

        do {
            leftBandwidth++;
            double volume = fft.getBand(primaryTone - leftBandwidth);
            normalizedVolume = volume/primaryVolume;
        } while (normalizedVolume > maxVolumeRatio && leftBandwidth < RELEVANT_FREQ_WINDOW);


        do {
            rightBandwidth++;
            double volume = fft.getBand(primaryTone + rightBandwidth);
            normalizedVolume = volume/primaryVolume;
        } while (normalizedVolume > maxVolumeRatio && rightBandwidth < RELEVANT_FREQ_WINDOW);


        if (leftBandwidth > 4 && rightBandwidth > 4) {
            Log.d("Bandwidth", "MOTION DETECTED");
            Log.d("Left Bandwidth", leftBandwidth+ " : " + fft.getBand(primaryTone - leftBandwidth));
            Log.d("Right Bandwidth", rightBandwidth+ " : " + fft.getBand(primaryTone + rightBandwidth));
            int movement = Math.min(10, Math.max(-10, rightBandwidth - leftBandwidth));

            Log.d("Move by", "BANDWIDTH DIFF " + movement);
        }

        double[] array = new double[1025];
        for (int i = 0; i < fft.specSize(); ++i) {
            array[i] = fft.getBand(i);
        }

        return array;
    }
    // compute nearest higher power of two
    // see: graphics.stanford.edu/~seander/bithacks.html
    int getHigherP2(int val)
    {
        val--;
        val |= val >> 1;
        val |= val >> 2;
        val |= val >> 8;
        val |= val >> 16;
        val++;
        return(val);
    }
}
