import { WebPlugin } from '@capacitor/core';
import { GenericResponse, RecordingData, RecordingInitSettings, RecordingStartSettings, WAVRecorderPlugin, PermissionStatus } from './definitions';
export declare class WAVRecorderWeb extends WebPlugin implements WAVRecorderPlugin {
    bytes_per_sample: number;
    number_of_channels: number;
    sample_rate: number;
    buffer_size: number;
    stream: MediaStream | undefined;
    context: AudioContext | undefined;
    recording: number;
    current_file: string;
    checkPermissions(): Promise<PermissionStatus>;
    requestPermissions(): Promise<PermissionStatus>;
    init(settings: RecordingInitSettings): Promise<GenericResponse>;
    startRecord(settings: RecordingStartSettings): Promise<GenericResponse>;
    stopRecord(): Promise<RecordingData>;
    _encode(buffer: Float32Array): Uint8Array;
    _interleave(buffers: Array<Float32Array>): Float32Array;
    _multiUint8ArrayToString(bufferArray: Uint8Array[]): string;
}
