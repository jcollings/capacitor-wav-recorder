declare var webkitAudioContext: typeof AudioContext;

export default AudioContext || webkitAudioContext;