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
    public static final int PRELIM_FREQ = 723;
    public static final int DEFAULT_SAMPLE_RATE = 44100;
    //in milliseconds
    public static final int INTERVAL = 3000;
    private static final int MILLI_PER_SECOND = 1000;

    private AudioRecord microphone;
    private AudioTrack audioTrack;

    private byte[] generatedSound;
    private int SAMPLE_RATE = DEFAULT_SAMPLE_RATE;

    public Doppler() {
        //write a check to see if stereo is supported
        Integer bufferSize = AudioRecord.getMinBufferSize(SAMPLE_RATE, AudioFormat.CHANNEL_IN_STEREO, AudioFormat.ENCODING_PCM_16BIT);
        microphone = new AudioRecord(MediaRecorder.AudioSource.MIC, DEFAULT_SAMPLE_RATE,
                AudioFormat.CHANNEL_IN_STEREO, AudioFormat.ENCODING_PCM_16BIT, bufferSize);
        audioTrack = new AudioTrack(AudioManager.STREAM_MUSIC, SAMPLE_RATE, AudioFormat.CHANNEL_OUT_STEREO,
                AudioFormat.ENCODING_PCM_16BIT, bufferSize, AudioTrack.MODE_STATIC);
        generatedSound = new byte[getNumSamples() * 2];

        generateTone(generatedSound, SAMPLE_RATE, getNumSamples(), PRELIM_FREQ);
        audioTrack.write(generatedSound, 0, generatedSound.length);
    }

    public boolean start() {
        try {
            //microphone.startRecording();
            final Handler handler = new Handler();
            final Runnable runnable = new Runnable() {
                @Override
                public void run() {
                    //Log.d("DOPPLER", "Testing");

                    audioTrack.play();
                    handler.postDelayed(this, INTERVAL);
                }
            };
            runnable.run();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    private int getNumSamples() {
        return SAMPLE_RATE * INTERVAL / MILLI_PER_SECOND;
    }

    private void generateTone(byte[] track, int sampleRate, int numSamples, int frequency) {
        Log.d("Generate tone", "Sample Rate: " + sampleRate + " numSamples " + numSamples);
        // fill out the array
        double[] sample = new double[numSamples];
        for (int i = 0; i < numSamples; ++i) {
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
