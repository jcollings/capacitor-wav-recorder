import { WebPlugin } from '@capacitor/core';

import type { WAVRecorderPlugin } from './definitions';

export class WAVRecorderWeb extends WebPlugin implements WAVRecorderPlugin {
  async echo(options: { value: string }): Promise<{ value: string }> {
    console.log('ECHO', options);
    return options;
  }
}
