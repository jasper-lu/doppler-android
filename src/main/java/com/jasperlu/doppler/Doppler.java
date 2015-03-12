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
 */
public class Doppler {
    public static final int PRELIM_FREQ = 10000;
    public static final int DEFAULT_SAMPLE_RATE = 44100;
    //in milliseconds
    public static final int INTERVAL = 10;
    private static final int MILLI_PER_SECOND = 1000;

    private AudioRecord microphone;
    private AudioTrack audioTrack;

    private byte[] generatedSound = new byte[getNumSamples()];
    private int SAMPLE_RATE = DEFAULT_SAMPLE_RATE;

    public Doppler() {
        Integer bufferSize = AudioRecord.getMinBufferSize(SAMPLE_RATE, AudioFormat.CHANNEL_IN_STEREO, AudioFormat.ENCODING_PCM_16BIT);
        microphone = new AudioRecord(MediaRecorder.AudioSource.MIC, DEFAULT_SAMPLE_RATE,
                AudioFormat.CHANNEL_IN_STEREO, AudioFormat.ENCODING_PCM_16BIT, bufferSize);
        audioTrack = new AudioTrack(AudioManager.STREAM_MUSIC, SAMPLE_RATE, AudioFormat.CHANNEL_IN_MONO,
                AudioFormat.ENCODING_PCM_16BIT, getNumSamples(), AudioTrack.MODE_STATIC);

        generateTone(generatedSound, SAMPLE_RATE, PRELIM_FREQ);
        audioTrack.write(generatedSound, 0, generatedSound.length);
    }

    public boolean start() {
        try {
            microphone.startRecording();
            new Handler().postDelayed(
                new Runnable() {
                    @Override
                    public void run() {
                        Log.d("DOPPLER", "Testing");
                        audioTrack.play();
                    }
            }, INTERVAL);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    private int getNumSamples() {
        return SAMPLE_RATE * INTERVAL / MILLI_PER_SECOND;
    }

    private void generateTone(byte[] track, int sampleRate, int frequency) {
        // fill out the array
        double[] sample = new double[getNumSamples()];
        for (int i = 0; i < sampleRate; ++i) {
            sample[i] = Math.sin(2 * Math.PI * i / (sampleRate/frequency));
        }

        // convert to 16 bit pcm sound array
        // assumes the sample buffer is normalised.
        int idx = 0;
        for (final double dVal : sample) {
            // scale to maximum amplitude
            final short val = (short) ((dVal * 32767));
            // in 16 bit wav PCM, first byte is the low order byte
            track[idx++] = (byte) (val & 0x00ff);
            track[idx++] = (byte) ((val & 0xff00) >>> 8);

        }
    }

    private void test() {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                short[] audioData = new short[getNumSamples()];
                microphone.read(audioData, 0, getNumSamples());
            }
        });
    }
}
