package com.jasperlu.doppler;

import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioRecord;
import android.media.AudioTrack;
import android.media.MediaRecorder;
import android.os.Handler;
import android.provider.MediaStore;
import android.util.Log;
import android.util.Xml;

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
    public static final int READ_INTERVAL = 3000;
    private static final int MILLI_PER_SECOND = 1000;

    private AudioRecord microphone;
    private FrequencyPlayer frequencyPlayer;
    private int SAMPLE_RATE = DEFAULT_SAMPLE_RATE;

    private int frequency;

    private short[] recorded;

    public Doppler() {
        //write a check to see if stereo is supported
        Integer bufferSize = getNumSamples() * 2;
        frequency = PRELIM_FREQ;
        recorded = new short[getNumSamples()];
        microphone = new AudioRecord(MediaRecorder.AudioSource.DEFAULT, DEFAULT_SAMPLE_RATE,
                AudioFormat.CHANNEL_IN_MONO, AudioFormat.ENCODING_PCM_16BIT, bufferSize);

        frequencyPlayer = new FrequencyPlayer(PRELIM_FREQ, INTERVAL);
    }

    public boolean start() {
        try {
            microphone.startRecording();
            frequencyPlayer.play();
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    attemptRead();
                }
            }, 200);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
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

    //returns the index of the current frequency
    //for my cases, pass along fftSIze
    private int freqToIndex(int fftSize) {
        Float nyquist = (float) SAMPLE_RATE / 2;
        Log.d("NYquist", nyquist + "");
        Log.d("freq/nyquist", frequency / nyquist + "");
        Log.d("fftsize/1" , fftSize / 2  +"");
        return Math.round(frequency/nyquist * fftSize/2);
    }

    private int getNumSamples() {
        return DEFAULT_SAMPLE_RATE * READ_INTERVAL / MILLI_PER_SECOND;
    }

    private void attemptRead() {
        microphone.read(recorded, 0, getNumSamples());
        int primaryTone = freqToIndex(2048);
        Log.d("Doppler", "Primary tone index: " + primaryTone + "");
        short primaryVolume = recorded[primaryTone];
        //taken from the original doppler js. this ratios is empirical
        double maxVolumeRatio = 0.001;

        int leftBandwidth = 0;
        int normalizedVolume = 0;
        do {
            leftBandwidth++;
            short volume = recorded[primaryTone - leftBandwidth];
            normalizedVolume = volume/primaryVolume;
        } while (normalizedVolume > maxVolumeRatio && leftBandwidth < RELEVANT_FREQ_WINDOW);

        Log.d("DOppler", leftBandwidth + "");
    }
}
