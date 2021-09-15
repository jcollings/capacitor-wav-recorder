var capacitorWAVRecorder = (function (exports, core, filesystem) {
    'use strict';

    const WAVRecorder = core.registerPlugin('WAVRecorder', {
        web: () => Promise.resolve().then(function () { return web; }).then(m => new m.WAVRecorderWeb()),
    });

    class WAVRecorderWeb extends core.WebPlugin {
        constructor() {
            super(...arguments);
            this.bytes_per_sample = 1;
            this.number_of_channels = 1;
            this.sample_rate = 8000;
            this.buffer_size = 1024;
            this.base_path = "dictations";
            this.recorded = [];
            this.recording = -1;
            this.current_file = '';
        }
        async checkPermissions() {
            throw this.unimplemented('Not implemented on web.');
        }
        async requestPermissions() {
            try {
                await navigator.mediaDevices.getUserMedia({ audio: true, video: false });
                return { microphone: 'granted' };
            }
            catch (e) {
                return { microphone: 'denied' };
            }
        }
        async init(settings) {
            if (settings.sampleRate) {
                this.sample_rate = settings.sampleRate;
            }
            return { value: true };
        }
        async startRecord(settings) {
            console.log(settings);
            this.current_file = this.base_path + "/" + settings.path;
            try {
                await filesystem.Filesystem.deleteFile({ path: this.current_file, directory: filesystem.Directory.Data });
            }
            catch (e) {
            }
            this.stream = await navigator.mediaDevices.getUserMedia({ audio: true, video: false });
            var AudioContext = window.AudioContext;
            this.context = new AudioContext();
            const input = this.context.createMediaStreamSource(this.stream);
            // https://stackoverflow.com/questions/65447236/scriptnode-onaudioprocess-is-deprecated-any-alternative
            const processor = this.context.createScriptProcessor(this.buffer_size, this.number_of_channels);
            processor.onaudioprocess = async (audioProcessingEvent) => {
                if (this.recording === -1) {
                    return;
                }
                var channels = [];
                for (var i = 0; i < this.number_of_channels; i++) {
                    channels.push(audioProcessingEvent.inputBuffer.getChannelData(i));
                }
                const audio_data = this._encode(this._interleave(channels));
                this.recorded.push(audio_data);
                const data = this._multiUint8ArrayToString(this.recorded);
                this.notifyListeners('recordingBuffer', audio_data);
                await filesystem.Filesystem.writeFile({
                    path: this.current_file,
                    data: data,
                    directory: filesystem.Directory.Data,
                    encoding: filesystem.Encoding.UTF16,
                    recursive: true,
                });
                if (this.recording === 0) {
                    this.recording = -1;
                }
            };
            input.connect(processor);
            processor.connect(this.context.destination);
            this.recording = 1;
            // this.recorder?.start();
            return { value: true };
        }
        async stopRecord() {
            var _a;
            // this.recording = 0;
            // this.recorder?.stop();
            (_a = this.stream) === null || _a === void 0 ? void 0 : _a.getTracks()[0].stop();
            return { value: true };
        }
        _encode(buffer) {
            var length = buffer.length;
            var data = new Uint8Array(length * this.bytes_per_sample);
            for (var i = 0; i < length; i++) {
                var index = i * this.bytes_per_sample;
                var sample = buffer[i];
                // clip sample 1/-1
                if (sample > 1) {
                    sample = 1;
                }
                else if (sample < -1) {
                    sample = -1;
                }
                /**
                 * Once a signal is digitized it is treated as a number (as you quite rightly point out)
                 * and for 16bits the range of numbers are -32768 to +32767. The numbers are created by
                 * an analogue to digital converter.
                 **/
                // bit reduce and convert to uInt
                switch (this.bytes_per_sample) {
                    case 4:
                        sample = sample * 2147483648;
                        data[index] = sample;
                        data[index + 1] = sample >> 8;
                        data[index + 2] = sample >> 16;
                        data[index + 3] = sample >> 24;
                        break;
                    case 3:
                        sample = sample * 8388608;
                        data[index] = sample;
                        data[index + 1] = sample >> 8;
                        data[index + 2] = sample >> 16;
                        break;
                    case 2:
                        sample = sample * 32768;
                        data[index] = sample;
                        data[index + 1] = sample >> 8;
                        break;
                    case 1:
                        data[index] = (sample + 1) * 128;
                        break;
                    default:
                        throw 'Only 8, 16, 24 and 32 bits per sample are supported';
                }
            }
            return data;
        }
        _interleave(buffers) {
            var channels = buffers.length;
            var result = new Float32Array(buffers[0].length);
            for (var i = 0; i < buffers[0].length; i++) {
                for (var channel = 0; channel < channels; channel++) {
                    result[i * channels + channel] = buffers[channel][i];
                }
            }
            return result;
        }
        _multiUint8ArrayToString(bufferArray) {
            var binary = "";
            for (var i = 0; i < bufferArray.length; i++) {
                var bytes = bufferArray[i];
                var len = bytes.byteLength;
                for (var j = 0; j < len; j++) {
                    binary += String.fromCharCode(bytes[j]);
                }
            }
            return binary;
        }
    }

    var web = /*#__PURE__*/Object.freeze({
        __proto__: null,
        WAVRecorderWeb: WAVRecorderWeb
    });

    exports.WAVRecorder = WAVRecorder;

    Object.defineProperty(exports, '__esModule', { value: true });

    return exports;

}({}, capacitorExports, capacitorFilesystem));
//# sourceMappingURL=plugin.js.map
