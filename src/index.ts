import { registerPlugin } from '@capacitor/core';

import type { WAVRecorderPlugin } from './definitions';

const WAVRecorder = registerPlugin<WAVRecorderPlugin>('WAVRecorder', {
  web: () => import('./web').then(m => new m.WAVRecorderWeb()),
});

export * from './definitions';
export { WAVRecorder };
