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

import com.jasperlu.doppler.FFT.Complex;
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

    private byte[] recorded;
    private int bufferSize;

    public Doppler() {
        //write a check to see if stereo is supported
        bufferSize = AudioTrack.getMinBufferSize(DEFAULT_SAMPLE_RATE, AudioFormat.CHANNEL_IN_MONO, AudioFormat.ENCODING_PCM_16BIT);
        //bufferSize = 4096;
        Log.d("BUEFFER SIZE IS ", bufferSize + "");
        frequency = PRELIM_FREQ;

        frequencyPlayer = new FrequencyPlayer(PRELIM_FREQ, INTERVAL);

        microphone = new AudioRecord(MediaRecorder.AudioSource.MIC, DEFAULT_SAMPLE_RATE,
                AudioFormat.CHANNEL_IN_MONO, AudioFormat.ENCODING_PCM_16BIT, bufferSize);
    }

    public boolean start() {
        try {
            //microphone.startRecording();
            frequencyPlayer.play();
            //attemptRead();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean pause() {
        try {
            //microphone.stop();
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
        return Math.round(frequency/nyquist * fftSize/2);
    }

    private int getNumSamples() {
        return DEFAULT_SAMPLE_RATE * READ_INTERVAL / MILLI_PER_SECOND;
    }

    private void attemptRead() {
        recorded = new byte[bufferSize];
        int nbRead = microphone.read(recorded, 0, bufferSize);
        int newBufferSize = 1;
        while (newBufferSize < bufferSize) {
            newBufferSize*= 2;
        }


        double[] tempDoubleArray = new double[newBufferSize];
        final int bytesPerSample = 2; // As it is 16bit PCM
        final double amplification = 100.0; // choose a number as you like
        for (int index = 0, floatIndex = 0; index < nbRead - bytesPerSample + 1; index += bytesPerSample, floatIndex++) {
            double sample = 0;
            for (int b = 0; b < bytesPerSample; b++) {
                int v = recorded[index + b];
                if (b < bytesPerSample - 1 || bytesPerSample == 1) {
                    v &= 0xFF;
                }
                sample += v << (b * 8);
            }
            double sample32 = amplification * (sample / 32768.0);
            tempDoubleArray[floatIndex] = sample32;
        }

        //pad fft array to make its size 2^n
        Complex[] fftTempArray = new Complex[newBufferSize];
        for (int i = 0; i < newBufferSize; i++) {
            fftTempArray[i] = new Complex(tempDoubleArray[i], 0);
        }
        Complex[] fftArray = FFT.fft(fftTempArray);

        // 6 - Calculate magnitude
        double[] magnitude = new double[fftArray.length];
        for (int i = 0; i < fftArray.length; i++)
        {
            Complex fft = fftArray[i];
            magnitude[i] = Math.sqrt(fft.re() * fft.re() + fft.im() * fft.im());
        }

        // 7 - Get maximum magnitude
        double max_magnitude = -1;
        int max_magnitude_index = -1;
        for (int i = 0; i < bufferSize / 2; i++)
        {
            if (magnitude[i] > max_magnitude)
            {
                max_magnitude = magnitude[i];
                max_magnitude_index = i;
            }
        }


        // 8 - Calculate frequency
        int freq = (int)(max_magnitude_index * 44100 / bufferSize);

        for (int i = 0; i < newBufferSize; i++) {
           //Log.d("DOPPLER", i + ": " + magnitude[i]);
            Log.d("Temp double array", i + " : " + tempDoubleArray[i]);
        }

        int primaryTone = freqToIndex(2048);
        //Log.d("Doppler", "Primary tone index: " + primaryTone + "");
        double primaryVolume = fftArray[primaryTone].re();
        //taken from the original doppler js. this ratios is empirical
        double maxVolumeRatio = 0.001;

        int leftBandwidth = 0;
        int normalizedVolume = 0;


        Log.d("Doppler", "Primary volume: " + primaryVolume + "");
        /*
        do {
            leftBandwidth++;
            short volume = recorded[primaryTone - leftBandwidth];
            normalizedVolume = volume/primaryVolume;
        } while (normalizedVolume > maxVolumeRatio && leftBandwidth < RELEVANT_FREQ_WINDOW);
        */
    }


    private double[] doubleFromByteArray(byte[] audio, int read) {
        double[] micBufferData = new double[bufferSize];
        final int bytesPerSample = 2; // As it is 16bit PCM
        final double amplification = 100.0; // choose a number as you like
        for (int index = 0, floatIndex = 0; index < read - bytesPerSample + 1; index += bytesPerSample, floatIndex++) {
            double sample = 0;
            for (int b = 0; b < bytesPerSample; b++) {
                int v = audio[index + b];
                if (b < bytesPerSample - 1 || bytesPerSample == 1) {
                    v &= 0xFF;
                }
                sample += v << (b * 8);
            }
            double sample32 = amplification * (sample / 32768.0);
            micBufferData[floatIndex] = sample32;
        }
        return micBufferData;
    }
    //changes signed byte to unsigned
    private int maskByte(int b) {
        return b & 0xFF;
    }
}
