import AudioContext from "./AudioContext";
import { encode, interleave } from "./wavUtils";
// https://github.com/danrouse/react-audio-recorder/blob/master/src/waveEncoder.ts
export class WAVRecorder {
    static audioContext = new AudioContext();
    static bufferSize = 2048;

    buffers: Float32Array[][];

    async startRecording() {

        const stream = await navigator.mediaDevices.getUserMedia({ audio: true, video: false });
        const { audioContext, bufferSize } = WAVRecorder;
        const source = audioContext.createMediaStreamSource(stream);
        const processor = audioContext.createScriptProcessor(
            bufferSize,
            1,
            2
        );

        processor.onaudioprocess = async (event: AudioProcessingEvent) => {
            const channels = [];

            for (let i = 0; i < 2; i++) {
                const channel = event.inputBuffer.getChannelData(i);
                channels.push(channel);
                // this.buffers[i].push(new Float32Array(channel));
            }

            const interleaved = interleave(channels);
            const encoded = encode(interleaved, 1);

            this.notifyListeners('recordingBuffer', audio_data);
        };

        source.connect(processor);
        processor.connect(audioContext.destination);

    }

    async stopRecording() {

    }
}