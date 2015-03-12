package com.jasperlu.doppler;

import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.provider.MediaStore;
import android.util.Xml;

/**
 * Created by Jasper on 3/11/2015.
 */
public class Doppler {
    public static final int PRELIM_FREQ = 20000;
    public static final int DEFAULT_SAMPLE_RATE = 44100;
    private AudioRecord microphone;

    public Doppler() {
        Integer bufferSize = AudioRecord.getMinBufferSize(DEFAULT_SAMPLE_RATE, AudioFormat.CHANNEL_IN_STEREO, AudioFormat.ENCODING_PCM_16BIT);
        microphone = new AudioRecord(MediaRecorder.AudioSource.MIC, DEFAULT_SAMPLE_RATE,
                AudioFormat.CHANNEL_IN_STEREO, AudioFormat.ENCODING_PCM_16BIT, bufferSize);
    }

    public boolean start() {
        try {
            microphone.startRecording();
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
