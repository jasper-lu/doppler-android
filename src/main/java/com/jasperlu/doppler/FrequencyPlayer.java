package com.jasperlu.doppler;

import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.os.Handler;
import android.provider.MediaStore;
import android.renderscript.Sampler;
import android.util.Log;

/**
 * Created by Jasper on 3/12/2015.
 */
public class FrequencyPlayer {
    private AudioTrack audioTrack;

    private final int duration = 5000; // milliseconds
    private final int sampleRate = 44100;
    private int numSamples;
    private double sample[] = new double[numSamples];
    private double freqOfTone = 10000; // hz

    private byte generatedSound[] = new byte[2 * numSamples];

    private static int MILLIS_PER_SECOND = 1000;

    Handler handler = new Handler();

    FrequencyPlayer(double frequency) {
        numSamples = sampleRate * duration / MILLIS_PER_SECOND;
        generatedSound = new byte[2 * numSamples];
        sample = new double[numSamples];

        audioTrack = new AudioTrack(AudioManager.STREAM_MUSIC, sampleRate, AudioFormat.CHANNEL_OUT_MONO,
                AudioFormat.ENCODING_PCM_16BIT, generatedSound.length, AudioTrack.MODE_STATIC);

        setFrequency(frequency);
    }

    //sets to new frequency and continues playing
    public void changeFrequency(double frequency) {
        setFrequency(frequency);
        play();
    }

    //sets frequency and stops sound
    public void setFrequency(double frequency) {
        freqOfTone = frequency;
        genTone();

        Log.d("FreqPlayer", "" +audioTrack.write(generatedSound, 0, generatedSound.length));
        audioTrack.setLoopPoints(0, generatedSound.length / 4, -1);
    }

    public void play() {
        //16 bit because it's supported by all phones
        audioTrack.play();
    }
    public void pause() {
        audioTrack.pause();
    }

    void genTone(){
        // fill out the array
        for (int i = 0; i < numSamples; ++i) {
            sample[i] = Math.sin(2 * Math.PI * i / (sampleRate/freqOfTone));
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
