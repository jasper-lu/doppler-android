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
    //in milliseconds
    public static final int INTERVAL = 3000;
    private static final int MILLI_PER_SECOND = 1000;

    private AudioRecord microphone;
    private FrequencyPlayer frequencyPlayer;

    private byte[] generatedSound;
    private int SAMPLE_RATE = DEFAULT_SAMPLE_RATE;

    public Doppler() {
        //write a check to see if stereo is supported
        Integer bufferSize = getNumSamples() * 2;
        microphone = new AudioRecord(MediaRecorder.AudioSource.MIC, DEFAULT_SAMPLE_RATE,
                AudioFormat.CHANNEL_IN_STEREO, AudioFormat.ENCODING_PCM_16BIT, bufferSize);
        frequencyPlayer = new FrequencyPlayer(PRELIM_FREQ, INTERVAL);
    }

    public boolean start() {
        try {
            frequencyPlayer.play();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean pause() {
        try {
            frequencyPlayer.pause();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    private int getNumSamples() {
        return SAMPLE_RATE * INTERVAL / MILLI_PER_SECOND;
    }
}
