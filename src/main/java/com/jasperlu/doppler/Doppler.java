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
    public static final int PRELIM_FREQ = 20000;
    public static final int DEFAULT_SAMPLE_RATE = 44100;
    public static final int RELEVANT_FREQ_WINDOW = 33;
    //in milliseconds
    public static final int INTERVAL = 3000;
    public static final int READ_INTERVAL = 200;
    private static final int MILLI_PER_SECOND = 1000;

    private AudioRecord microphone;
    private FrequencyPlayer frequencyPlayer;
    private int SAMPLE_RATE = DEFAULT_SAMPLE_RATE;

    private int frequency;


    private short[] buffer;
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

        //fft = new com.jasperlu.doppler.FFT2.FFT(bufferSize, SAMPLE_RATE);
    }

    public boolean start() {
        frequencyPlayer.play();
        try {
            //you might get an error here if another app hasn't released the microphone
            microphone.startRecording();

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    attemptRead();
                }
            }, 200);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            Log.d("DOPPLER", "start recording error");
            return false;
        }
    }

    public boolean pause() {
        try {
            microphone.stop();
            frequencyPlayer.pause();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    private void attemptRead() {
        int bufferReadResult = microphone.read(buffer, 0, bufferSize);

        //volume is used later
        float volume = 0;
        //get higher p2 because buffer needs to be "filled out" for FFT
        float[] fftRealArray = new float[getHigherP2(bufferReadResult)];
        for (int i = 0; i < bufferReadResult; i++) {
            fftRealArray[i] = (float) buffer[i] / Short.MAX_VALUE; //32768.0
            volume += Math.abs(fftRealArray[i]);
        }
        volume = (float) Math.log10(volume/bufferReadResult);

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

        if (fft == null) {
            fft = new FFT(getHigherP2(bufferReadResult), SAMPLE_RATE);
        }
        fft.forward(fftRealArray);

        //size is 1025 i may need to try to get it to fill out to 2048
        Log.d("DOPPLER", "FFT SIZE IS " + fft.specSize());

        int primaryTone = fft.freqToIndex(PRELIM_FREQ);

        Log.d("DOPPLER", "PRIMARY TONE INDEX IS " + primaryTone);
        double primaryVolume = fft.getBand(primaryTone);
        //taken from the original doppler js. this ratios is empirical
        double maxVolumeRatio = 0.001;

        int leftBandwidth = 0;
        int normalizedVolume = 0;


        Log.d("Doppler", "Primary volume: " + primaryVolume + "");
        Log.d("Doppler", "10k volume: " + fft.getBand(fft.freqToIndex(773))+ "");
        /*
        do {
            leftBandwidth++;
            short volume = recorded[primaryTone - leftBandwidth];
            normalizedVolume = volume/primaryVolume;
        } while (normalizedVolume > maxVolumeRatio && leftBandwidth < RELEVANT_FREQ_WINDOW);
        */
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
