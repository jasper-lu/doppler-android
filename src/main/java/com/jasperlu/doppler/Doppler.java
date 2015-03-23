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
    //base gestures. can extend to have more
    public interface OnGestureCallback {
        //swipe towards
        public void onPush();
        //swipe away
        public void onPull();
        //self-explanatory
        public void onTap();
        public void onDoubleTap();

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
    private static final double MAX_VOL_RATIO_DEFAULT = 0.1;
    private static final double SECOND_PEAK_RATIO = 0.3;
    public static double maxVolRatio = MAX_VOL_RATIO_DEFAULT;

    //for bandwidth positions in array
    private static final int LEFT_BANDWIDTH = 0;
    private static final int RIGHT_BANDWIDTH = 1;

    //I want to add smoothing
    private static final float SMOOTHING_TIME_CONSTANT = 0.5f;

    private AudioRecord microphone;
    private FrequencyPlayer frequencyPlayer;
    private int SAMPLE_RATE = DEFAULT_SAMPLE_RATE;

    private float frequency;
    private int freqIndex;

    private short[] buffer;
    private float[] fftRealArray;
    //holds the freqs of the previous iteration
    private float[] oldFreqs;
    private int bufferSize = 2048;

    private Handler mHandler;
    private boolean repeat;

    FFT fft;
    //to calibrate or not
    private boolean calibrate;
    Calibrator calibrator;

    //callbacks
    private boolean isGestureCallbackOn = false;
    private OnGestureCallback gestureCallback;
    private boolean isReadCallbackOn = false;
    private OnReadCallback readCallback;


    public Doppler() {
        //write a check to see if stereo is supported
        bufferSize = AudioRecord.getMinBufferSize(SAMPLE_RATE, AudioFormat.CHANNEL_IN_MONO, AudioFormat.ENCODING_PCM_16BIT);
        buffer = new short[bufferSize];

        frequency = PRELIM_FREQ;
        freqIndex = PRELIM_FREQ_INDEX;

        frequencyPlayer = new FrequencyPlayer(PRELIM_FREQ);

        microphone = new AudioRecord(MediaRecorder.AudioSource.VOICE_RECOGNITION, DEFAULT_SAMPLE_RATE,
                AudioFormat.CHANNEL_IN_MONO, AudioFormat.ENCODING_PCM_16BIT, bufferSize);

        mHandler = new Handler();

        calibrator = new Calibrator();
    }

    private void setFrequency(float frequency) {
        this.frequency = frequency;
        this.freqIndex = fft.freqToIndex(frequency);
    }

    public boolean start() {
        frequencyPlayer.play();
        boolean startedRecording = false;
        try {
            //you might get an error here if another app hasn't released the microphone
            microphone.startRecording();
            repeat = true;

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    optimizeFrequency(MIN_FREQ, MAX_FREQ);
                    //assuming fft.forward was already called;
                    readMic();
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

    public int[] getBandwidth() {
        readAndFFT();

        int primaryTone = freqIndex;

        double normalizedVolume = 0;

        double primaryVolume = fft.getBand(primaryTone);

        int leftBandwidth = 0;

        do {
            leftBandwidth++;
            double volume = fft.getBand(primaryTone - leftBandwidth);
            normalizedVolume = volume/primaryVolume;
        } while (normalizedVolume > maxVolRatio && leftBandwidth < RELEVANT_FREQ_WINDOW);


        //secondary bandwidths are for looking past the first minima to search for "split off" peaks, as per the paper
        int secondScanFlag = 0;
        int secondaryLeftBandwidth = leftBandwidth;

        //second scan
        do {
            secondaryLeftBandwidth++;
            double volume = fft.getBand(primaryTone - secondaryLeftBandwidth);
            normalizedVolume = volume/primaryVolume;

            if (normalizedVolume > SECOND_PEAK_RATIO) {
                secondScanFlag = 1;
            }

            if (secondScanFlag == 1 && normalizedVolume < maxVolRatio ) {
                break;
            }
        } while (secondaryLeftBandwidth < RELEVANT_FREQ_WINDOW);

        if (secondScanFlag == 1) {
            leftBandwidth = secondaryLeftBandwidth;
        }

        int rightBandwidth = 0;

        do {
            rightBandwidth++;
            double volume = fft.getBand(primaryTone + rightBandwidth);
            normalizedVolume = volume/primaryVolume;
        } while (normalizedVolume > maxVolRatio && rightBandwidth < RELEVANT_FREQ_WINDOW);

        secondScanFlag = 0;
        int secondaryRightBandwidth = 0;
        do {
            secondaryRightBandwidth++;
            double volume = fft.getBand(primaryTone + secondaryRightBandwidth);
            normalizedVolume = volume/primaryVolume;

            if (normalizedVolume > SECOND_PEAK_RATIO) {
                secondScanFlag = 1;
            }

            if (secondScanFlag == 1 && normalizedVolume < maxVolRatio) {
                break;
            }
        } while (secondaryRightBandwidth < RELEVANT_FREQ_WINDOW);

        if (secondScanFlag == 1) {
            rightBandwidth = secondaryRightBandwidth;
        }

        return new int[]{leftBandwidth, rightBandwidth};

    }

    public void readMic() {
        int[] bandwidths = getBandwidth();
        int leftBandwidth = bandwidths[LEFT_BANDWIDTH];
        int rightBandwidth = bandwidths[RIGHT_BANDWIDTH];

        if (isReadCallbackOn) {
            callReadCallback(leftBandwidth, rightBandwidth);
        }

        if (isGestureCallbackOn) {
            callGestureCallback(leftBandwidth, rightBandwidth);
        }

        if (repeat) {
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    readMic();
                }
            });
        }
    }

    public void setOnGestureCallback(OnGestureCallback callback) {
        gestureCallback = callback;
        isGestureCallbackOn = true;
    }

    public void callGestureCallback(int leftBandwidth, int rightBandwidth) {
        //implement gesture logic
    }

    public void setOnReadCallback(OnReadCallback callback) {
       readCallback = callback;
       isReadCallbackOn = true;
    }

    public void callReadCallback(int leftBandwidth, int rightBandwidth) {
        double[] array = new double[fft.specSize()];
        for (int i = 0; i < fft.specSize(); ++i) {
            array[i] = fft.getBand(i);
        }

        readCallback.onBandwidthRead(leftBandwidth, rightBandwidth);
        readCallback.onBinsRead(array);

        maxVolRatio = calibrator.calibrate(maxVolRatio, leftBandwidth, rightBandwidth);
    }

    public boolean setCalibrate(boolean bool) {
        calibrate = bool;
        return calibrate;
    }

    public void smoothOutFreqs() {
        for (int i = 0; i < fft.specSize(); ++i) {
            float smoothedOutMag = SMOOTHING_TIME_CONSTANT * fft.getBand(i) + (1 - SMOOTHING_TIME_CONSTANT) * oldFreqs[i];
            fft.setBand(i, smoothedOutMag);
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

        int primaryInd = freqIndex;
        for (int i = minInd; i <= maxInd; ++i) {
            if (fft.getBand(i) > fft.getBand(primaryInd)) {
                primaryInd = i;
            }
        }
        setFrequency(fft.indexToFreq(primaryInd));
        Log.d("NEW PRIMARY IND", fft.indexToFreq(primaryInd) + "");
    }

    //reads the buffer into fftrealarray, applies windowing, then fft and smoothing
    public void readAndFFT() {
        //copy into old freqs array
        if (fft.specSize() != 0 && oldFreqs == null) {
            oldFreqs = new float[fft.specSize()];
        }
        for (int i = 0; i < fft.specSize(); ++i) {
            oldFreqs[i] = fft.getBand(i);
        }

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

        //apply smoothing
        smoothOutFreqs();
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
