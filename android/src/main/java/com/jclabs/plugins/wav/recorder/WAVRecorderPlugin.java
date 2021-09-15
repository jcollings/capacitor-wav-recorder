package com.jclabs.plugins.wav.recorder;

import android.Manifest;
import android.util.Log;

import com.getcapacitor.JSArray;
import com.getcapacitor.JSObject;
import com.getcapacitor.PermissionState;
import com.getcapacitor.Plugin;
import com.getcapacitor.PluginCall;
import com.getcapacitor.PluginMethod;
import com.getcapacitor.annotation.CapacitorPlugin;
import com.getcapacitor.annotation.Permission;

import java.nio.charset.StandardCharsets;

interface WAVRecorderListeners{
    void onBufferCallback(byte[] buffer);
    void onSampleCallback(JSArray buffer);
    void onRecordingLengthCallback(int duration);
}

@CapacitorPlugin(
    name = "WAVRecorder",
    permissions = {@Permission(alias = "microphone", strings = {Manifest.permission.RECORD_AUDIO})}
)
public class WAVRecorderPlugin extends Plugin implements WAVRecorderListeners {

    private final String TAG = "WAVRecorderPlugin";
    private WAVRecorder wavRecorder;

    @PluginMethod
    public void canRecord(PluginCall call){
    }

    @PluginMethod
    public void init(PluginCall call){

        Log.d(TAG, "init");

        if(!getPermissionState("microphone").equals(PermissionState.GRANTED)){
            call.reject("Missing microphone permissions");
            return;
        }
        
        try {
            wavRecorder = new WAVRecorder(getContext());
            wavRecorder.init(call.getData());

            JSObject result = new JSObject();
            result.put("value", true);
            call.resolve(result);

            Log.d(TAG, "init success");
        }catch(Exception ex){
            call.reject("Failed to initialize WAVRecorder", ex);
        }
    }

    @PluginMethod
    public void startRecord(PluginCall call){

        Log.d(TAG, "startRecord");

        if(wavRecorder == null){
            call.reject("WAVRecorder has not been initialized");
            return;
        }

        try{
            Log.d(TAG, "startRecord path:" + call.getString("path"));
            wavRecorder.start(call.getString("path"), this);

            JSObject result = new JSObject();
            result.put("value", true);
            call.resolve(result);

        }catch(Exception ex){
            call.reject("Failed to start recording", ex);
        }
    }

    @PluginMethod
    public void stopRecord(PluginCall call){

        if(wavRecorder == null){
            call.reject("Recording has not been started");
            return;
        }

        try{
            wavRecorder.stop();

            JSObject result = new JSObject();
            result.put("value", true);
            call.resolve(result);

        }catch (Exception ex){
            call.reject("Failed to stop recording");
        }
    }

    @Override
    public void onBufferCallback(byte[] buffer) {
        JSObject output = new JSObject();
        output.put("value", new String(buffer, StandardCharsets.UTF_8));
        notifyListeners("recordingBuffer", output);
    }

    @Override
    public void onSampleCallback(JSArray samples){
        JSObject output = new JSObject();
        output.put("value", samples);
        notifyListeners("sampleBuffer", output);
    }

    @Override
    public void onRecordingLengthCallback(int duration){
        JSObject output = new JSObject();
        output.put("value", duration);
        notifyListeners("recordingLength", output);
    }
}
