import { registerPlugin } from '@capacitor/core';
const WAVRecorder = registerPlugin('WAVRecorder', {
    web: () => import('./web').then(m => new m.WAVRecorderWeb()),
});
export * from './definitions';
export { WAVRecorder };
//# sourceMappingURL=index.js.map