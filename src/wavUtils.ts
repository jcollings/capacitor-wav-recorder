export const interleave = (buffers: Array<Float32Array>): Float32Array => {
    var channels = buffers.length;
    var result: Float32Array = new Float32Array(buffers[0].length);

    for (var i = 0; i < buffers[0].length; i++) {
        for (var channel = 0; channel < channels; channel++) {
            result[i * channels + channel] = buffers[channel][i];
        }
    }

    return result;
}

export const multiUint8ArrayToString = (bufferArray: Uint8Array[]): string => {
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

export const encode = (buffer: Float32Array, bytesPerSample: number): Uint8Array => {
    var length = buffer.length;
    var data = new Uint8Array(length * bytesPerSample);

    for (var i = 0; i < length; i++) {
        var index = i * bytesPerSample;
        var sample = buffer[i];

        // clip sample 1/-1
        if (sample > 1) {
            sample = 1;
        } else if (sample < -1) {
            sample = -1;
        }

        /**
         * Once a signal is digitized it is treated as a number (as you quite rightly point out)
         * and for 16bits the range of numbers are -32768 to +32767. The numbers are created by
         * an analogue to digital converter.
         **/

        // bit reduce and convert to uInt
        switch (bytesPerSample) {
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