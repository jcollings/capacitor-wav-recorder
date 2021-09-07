export interface WAVRecorderPlugin {
  echo(options: { value: string }): Promise<{ value: string }>;
}
