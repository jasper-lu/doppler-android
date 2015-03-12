package com.jasperlu.doppler;

import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.util.Log;

/**
 * Created by Jasper on 3/12/2015.
 */
public class FrequencyPlayer {
    private AudioTrack audioTrack;

    private int mFrequency;
    private int mInterval;

    private static int DEFAULT_SAMPLE_RATE = 44100;
    private static int MILLIS_PER_SECOND = 44100;


    private byte[] generatedSound;

    FrequencyPlayer(int frequency, int interval) {
        mInterval = interval;

        generatedSound = new byte[getNumSamples() * 2];

        //16 bit because it's supported by all phones
        audioTrack = new AudioTrack(AudioManager.STREAM_MUSIC, DEFAULT_SAMPLE_RATE, AudioFormat.CHANNEL_OUT_STEREO,
                AudioFormat.ENCODING_PCM_16BIT, getNumSamples() * 2, AudioTrack.MODE_STATIC);
        setFrequency(frequency);
    }

    public void changeFrequency(int frequency) {
        setFrequency(frequency);
        play();
    }

    //sets frequency and stops sound
    public void setFrequency(int frequency) {
        mFrequency = frequency;
        generateTone();
        audioTrack.write(generatedSound, 0, generatedSound.length);
        audioTrack.setLoopPoints(0, generatedSound.length / 4, -1);
    }

    public void play() {
        audioTrack.play();
    }

    private int getNumSamples() {
        return DEFAULT_SAMPLE_RATE * mInterval / MILLIS_PER_SECOND;
    }

    private void generateTone() {
        Log.d("Generate tone", "Sample Rate: " + DEFAULT_SAMPLE_RATE + " numSamples " + getNumSamples());
        // fill out the array
        int numSamples = getNumSamples();
        double[] sample = new double[numSamples];
        for (int i = 0; i < numSamples; ++i) {
            sample[i] = Math.sin(2 * Math.PI * i / (DEFAULT_SAMPLE_RATE/mFrequency));
        }

        // convert to 16 bit pcm sound array
        // assumes the sample buffer is normalised.
        int idx = 0;
        for (final double dVal : sample) {
            // scale to maximum amplitude
            final short val = (short) ((dVal * 32767));
            // in 16 bit wav PCM, first byte is the low order byte
            generatedSound[idx++] = (byte) (val & 0x00ff);
            generatedSound[idx++] = (byte) ((val & 0xff00) >>> 8);
        }
    }

}
