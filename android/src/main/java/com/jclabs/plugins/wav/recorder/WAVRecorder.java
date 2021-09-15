package com.jclabs.plugins.wav.recorder;

import android.content.Context;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.util.Log;

import com.getcapacitor.JSArray;
import com.getcapacitor.JSObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

public class WAVRecorder {

    private final String TAG = "WAVRecorder";

    private final Context context;

    private int sampleRateInHz;
    private boolean voiceActivation;
    private int bitsPerSample;
    private int channelConfig;
    private int audioFormat;
    private int audioSource;
    private boolean isRecording;
    private int bufferSize;
    private int recordingBufferSize;
    private AudioRecord recorder;

    private String tmpFile;
    private String outFile;
    private String sampleFile;

    private FileOutputStream os;
    private FileOutputStream sampleOs;

    public WAVRecorder(Context context) {
        this.context = context;
    }

    public void init(JSObject data) {

        sampleRateInHz = data.getInteger("sampleRate", 16000);
        voiceActivation = data.getBoolean("voiceActivation", true);

        bitsPerSample = 16;
        channelConfig = AudioFormat.CHANNEL_IN_MONO;
        audioFormat = AudioFormat.ENCODING_PCM_16BIT;
        audioSource = MediaRecorder.AudioSource.VOICE_RECOGNITION;

        isRecording = false;

        bufferSize = AudioRecord.getMinBufferSize(sampleRateInHz, channelConfig, audioFormat);
        recordingBufferSize = bufferSize * 3;
        recorder = new AudioRecord(audioSource, sampleRateInHz, channelConfig, audioFormat, recordingBufferSize);
    }

    public void start(String path, WAVRecorderListeners wavRecorderListeners) {

        String tmp = path;
        tmp = tmp.substring(0, tmp.lastIndexOf("."));
        outFile = tmp + ".wav";
        tmpFile = tmp + ".pcm";
        sampleFile = tmp + ".samples";

        isRecording = true;
        recorder.startRecording();

        Thread recordingThread = new Thread(new Runnable() {
            public void run() {
                try {
                    int bytesRead;
                    int count = 0;
                    String base64Data;
                    byte[] buffer = new byte[recordingBufferSize];
                    os = new FileOutputStream(tmpFile);
                    sampleOs = new FileOutputStream(sampleFile);
                    int cAmplitude= 0;
                    int cAmplitudeMin = 500;
                    int avgSampleLimit = 20;
                    int[] avg = new int[avgSampleLimit];

                    // Sample generation settings
                    // (num_channels * sample_rate * (bit_depth/8))

                    // bufferSize = e.g. 3584
                    // bytesPerSecond = 88200
                    // How many buffers per second
                    // = (3584 / 88200) * 128
                    // = 5.2012
                    int bytesPerSecond = (44100 * (bitsPerSample / 8));
                    int samplesPerSecond = 126;
                    int bytesPerSample = (int)Math.floor(bytesPerSecond / samplesPerSecond); // 689
                    int sampleBufferLength = (int)Math.floor((((double)bufferSize/(double)bytesPerSecond) * samplesPerSecond)/2);
//                    int[] sampleBuffer = new int[sampleBufferLength];
//                    JSArray samples = new JSArray();
                    ArrayList<String> samples = new ArrayList<String>();
                    // int bytesPerSample = (int)Math.floor((bufferSize/2) / sampleBufferLength);
                    short sampleTotal;
                    boolean samplesStarted = false;
                    int samplesRead = 0;
                    int totalBytesRead = 0;
                    long delay = 0;
                    int maxDelay = 100;
                    boolean updated = false;
//                    int sampleCounter = 0;

                    // Log.d(TAG, "Sample Buffer Length:" + Integer.toString(sampleBufferLength));

                    while (isRecording) {
                        bytesRead = recorder.read(buffer, 0, buffer.length);
                        sampleTotal = 0;
//                        sampleCounter = 0;

                        // skip first 2 buffers to eliminate "click sound"
                        if (bytesRead > 0 && ++count > 1) {

                            // amplitude test
                            for (int i=0; i<bytesRead/2; i++) {
                                short curSample = getShort(buffer[i*2], buffer[i*2+1]);

                                // Sample

                                samplesRead++;
                                if(samplesRead > 0 && (samplesRead % bytesPerSample) == 0){
//                                    Log.d(TAG, "AVG SAMPLE:" + sampleCounter +" - " + (sampleTotal /bytesPerSample));

                                    samples.add(generateSample(sampleTotal));
//                                    sampleBuffer[sampleCounter] = (byte)(sampleTotal /bytesPerSample);
//                                    sampleCounter++;
                                    sampleTotal = 0;
                                }
                                // End Sample

                                sampleTotal = sampleTotal > Math.abs(curSample) ? sampleTotal : curSample;
                                if (curSample > cAmplitude) {
                                    cAmplitude = curSample;
                                }
                            }

                            if(sampleTotal > 0){
//                                Log.d(TAG, "AVG SAMPLE:" + sampleCounter +" - " + (sampleTotal /bytesPerSample));
                                samples.add(generateSample(sampleTotal));
//                                sampleBuffer[sampleCounter] = (int)Math.floor(sampleTotal /bytesPerSample);
//                                sampleCounter++;
//                                sampleTotal = 0;
                            }

                            // end amplitude test

                            // Some kind of callback to capacitorjs
//                            base64Data = Base64.encodeToString(buffer, Base64.NO_WRAP);
//                            eventEmitter.emit("data", base64Data);

                            avg[count % avgSampleLimit] = cAmplitude;

                            if(!voiceActivation || getLargestValue(avg) > cAmplitudeMin){

                                totalBytesRead += buffer.length;

                                Log.d(TAG, "Buffer Length:" + Integer.toString(buffer.length));
                                wavRecorderListeners.onBufferCallback(buffer);
                                os.write(buffer, 0, bytesRead);

                                updated = true;

//                                String prefix = samplesStarted ? "," : "";

                                JSArray tmpSamples = new JSArray();
                                StringBuilder sb = new StringBuilder();
                                for(String s: samples){
                                    sb.append(s);
                                    tmpSamples.put(s);
                                }

                                // JSObject tmpSampleOutput = new JSObject();
                                // tmpSampleOutput.put('samples', tmpSamples);
                                // tmpSampleOutput.put('duration', totalBytesRead);

                                wavRecorderListeners.onRecordingLengthCallback(totalBytesRead);
                                wavRecorderListeners.onSampleCallback(tmpSamples);
                                sampleOs.write(sb.toString().getBytes(StandardCharsets.UTF_8));
                                samplesStarted = true;
                            }

                            samples.clear();

                            // amplitude test
                            Log.d(TAG, "Amplitude:" + Integer.toString(cAmplitude));
                            cAmplitude = 0;
                            // end amplitude test
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        recordingThread.start();
    }

    private short getShort(byte argB1, byte argB2) {
        return (short)(argB1 | (argB2 << 8));
    }

    private String generateSample(short sampleTotal) {
        double percent = (double)Math.abs(sampleTotal) / 32768;
        int result = (int) (round(percent,2) * 100);
        int val = Math.min(result, 99);
        return (val < 10 ? "0" : "") + Integer.toString(val);
    }

    private static double round (double value, int precision) {
        int scale = (int) Math.pow(10, precision);
        return (double) Math.round(value * scale) / scale;
    }

    public int getLargestValue(int[] array){
        int max = 0;
        for(int value : array){
            max = Math.max(max, value);
        }
        return max;
    }

    public void stop() {
        isRecording = false;

        try{
            recorder.stop();
            os.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        saveAsWav();
    }

    private void saveAsWav() {
        try {
            FileInputStream in = new FileInputStream(tmpFile);
            FileOutputStream out = new FileOutputStream(outFile);
            long totalAudioLen = in.getChannel().size();
            ;
            long totalDataLen = totalAudioLen + 36;

            addWavHeader(out, totalAudioLen, totalDataLen);

            byte[] data = new byte[bufferSize];
            int bytesRead;
            while ((bytesRead = in.read(data)) != -1) {
                out.write(data, 0, bytesRead);
            }
            Log.d(TAG, "file path:" + outFile);
            Log.d(TAG, "file size:" + out.getChannel().size());

            in.close();
            out.close();
            deleteTempFile();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void addWavHeader(FileOutputStream out, long totalAudioLen, long totalDataLen)
            throws Exception {

        long sampleRate = sampleRateInHz;
        int channels = channelConfig == AudioFormat.CHANNEL_IN_MONO ? 1 : 2;
        int bitsPerSample = audioFormat == AudioFormat.ENCODING_PCM_8BIT ? 8 : 16;
        long byteRate = sampleRate * channels * bitsPerSample / 8;
        int blockAlign = channels * bitsPerSample / 8;

        byte[] header = new byte[44];

        header[0] = 'R';                                    // RIFF chunk
        header[1] = 'I';
        header[2] = 'F';
        header[3] = 'F';
        header[4] = (byte) (totalDataLen & 0xff);           // how big is the rest of this file
        header[5] = (byte) ((totalDataLen >> 8) & 0xff);
        header[6] = (byte) ((totalDataLen >> 16) & 0xff);
        header[7] = (byte) ((totalDataLen >> 24) & 0xff);
        header[8] = 'W';                                    // WAVE chunk
        header[9] = 'A';
        header[10] = 'V';
        header[11] = 'E';
        header[12] = 'f';                                   // 'fmt ' chunk
        header[13] = 'm';
        header[14] = 't';
        header[15] = ' ';
        header[16] = 16;                                    // 4 bytes: size of 'fmt ' chunk
        header[17] = 0;
        header[18] = 0;
        header[19] = 0;
        header[20] = 1;                                     // format = 1 for PCM
        header[21] = 0;
        header[22] = (byte) channels;                       // mono or stereo
        header[23] = 0;
        header[24] = (byte) (sampleRate & 0xff);            // samples per second
        header[25] = (byte) ((sampleRate >> 8) & 0xff);
        header[26] = (byte) ((sampleRate >> 16) & 0xff);
        header[27] = (byte) ((sampleRate >> 24) & 0xff);
        header[28] = (byte) (byteRate & 0xff);              // bytes per second
        header[29] = (byte) ((byteRate >> 8) & 0xff);
        header[30] = (byte) ((byteRate >> 16) & 0xff);
        header[31] = (byte) ((byteRate >> 24) & 0xff);
        header[32] = (byte) blockAlign;                     // bytes in one sample, for all channels
        header[33] = 0;
        header[34] = (byte) bitsPerSample;                  // bits in a sample
        header[35] = 0;
        header[36] = 'd';                                   // beginning of the data chunk
        header[37] = 'a';
        header[38] = 't';
        header[39] = 'a';
        header[40] = (byte) (totalAudioLen & 0xff);         // how big is this data chunk
        header[41] = (byte) ((totalAudioLen >> 8) & 0xff);
        header[42] = (byte) ((totalAudioLen >> 16) & 0xff);
        header[43] = (byte) ((totalAudioLen >> 24) & 0xff);

        out.write(header, 0, 44);
    }

    private void deleteTempFile() {
        File file = new File(tmpFile);
        file.delete();
    }
}
