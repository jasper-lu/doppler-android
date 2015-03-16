package com.jasperlu.doppler;

import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.os.Handler;
import android.util.Log;

import com.jasperlu.doppler.FFT.FFT;

import java.util.Timer;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Created by Jasper on 3/11/2015.
 *
 * To find frequency, check:
 * http://stackoverflow.com/questions/18652000/record-audio-in-java-and-determine-real-time-if-a-tone-of-x-frequency-was-played
 */
public class Doppler {
    public static final float PRELIM_FREQ = 20000;
    public static final int DEFAULT_SAMPLE_RATE = 44100;
    public static final int RELEVANT_FREQ_WINDOW = 33;
    //in milliseconds
    public static final int INTERVAL = 3000;
    public static final int READ_INTERVAL = 200;
    private static final int MILLI_PER_SECOND = 1000;

    private AudioRecord microphone;
    private FrequencyPlayer frequencyPlayer;
    private int SAMPLE_RATE = DEFAULT_SAMPLE_RATE;

    private float frequency;
    private ScheduledExecutorService scheduler;


    private short[] buffer;
    private float[] fftRealArray;
    private int bufferSize = 2048;

    com.jasperlu.doppler.FFT.FFT fft;

    public Doppler() {
        //write a check to see if stereo is supported
        bufferSize = AudioRecord.getMinBufferSize(SAMPLE_RATE, AudioFormat.CHANNEL_IN_MONO, AudioFormat.ENCODING_PCM_16BIT);
        buffer = new short[bufferSize];
        Log.d("BUEFFER SIZE IS ", bufferSize + "");
        frequency = PRELIM_FREQ;

        frequencyPlayer = new FrequencyPlayer(PRELIM_FREQ, INTERVAL);

        microphone = new AudioRecord(MediaRecorder.AudioSource.VOICE_RECOGNITION, DEFAULT_SAMPLE_RATE,
                AudioFormat.CHANNEL_IN_MONO, AudioFormat.ENCODING_PCM_16BIT, bufferSize);

        //init fftrealarray
        //fft = new com.jasperlu.doppler.FFT2.FFT(bufferSize, SAMPLE_RATE);
    }

    public boolean start() {
        frequencyPlayer.play();
        boolean startedRecording = false;
        try {
            //you might get an error here if another app hasn't released the microphone
            microphone.startRecording();
            //startReading();

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    optimizeFrequency(19000, 21000);
                }
            }, 200);

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

    public void startReading() {
        scheduler = Executors.newSingleThreadScheduledExecutor();
        scheduler.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                attemptRead();
            }
        }, 0, 500, TimeUnit.MILLISECONDS);
    }

    public boolean pause() {
        try {
            //scheduler.shutdownNow();
            microphone.stop();
            frequencyPlayer.pause();
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
        frequency = fft.indexToFreq(primaryInd);
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


        if (leftBandwidth > 8 && rightBandwidth > 8) {
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
