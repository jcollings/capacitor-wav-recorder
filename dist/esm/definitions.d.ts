import type { PluginListenerHandle, PermissionState } from '@capacitor/core';
export interface PermissionStatus {
    microphone: PermissionState;
}
export interface GenericResponse {
    value: boolean;
}
export interface RecordingData {
    value: {};
}
export interface RecordingInitSettings {
    voiceActivation?: boolean;
    sampleRate?: 8000 | 11025 | 16000 | 22050 | 44100;
}
export interface RecordingStartSettings {
    path: string;
}
export declare type RecordingBufferListener = (state: any) => void;
export interface WAVRecorderPlugin {
    checkPermissions(): Promise<PermissionStatus>;
    requestPermissions(): Promise<PermissionStatus>;
    init(settings: RecordingInitSettings): Promise<GenericResponse>;
    startRecord(settings: RecordingStartSettings): Promise<GenericResponse>;
    stopRecord(): Promise<RecordingData>;
    addListener(eventName: 'recordingBuffer', listenerFunc: RecordingBufferListener): Promise<PluginListenerHandle> & PluginListenerHandle;
    removeAllListeners(): Promise<void>;
}
