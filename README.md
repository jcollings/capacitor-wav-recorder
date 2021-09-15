# capacitor-wav-recorder

WAV Audio Recorder

## Install

```bash
npm install capacitor-wav-recorder
npx cap sync
```

## API

<docgen-index>

* [`checkPermissions()`](#checkpermissions)
* [`requestPermissions()`](#requestpermissions)
* [`init(...)`](#init)
* [`startRecord(...)`](#startrecord)
* [`stopRecord()`](#stoprecord)
* [`addListener(...)`](#addlistener)
* [`removeAllListeners()`](#removealllisteners)
* [Interfaces](#interfaces)

</docgen-index>

<docgen-api>
<!--Update the source file JSDoc comments and rerun docgen to update the docs below-->

### checkPermissions()

```typescript
checkPermissions() => Promise<PermissionStatus>
```

**Returns:** <code>Promise&lt;<a href="#permissionstatus">PermissionStatus</a>&gt;</code>

--------------------


### requestPermissions()

```typescript
requestPermissions() => Promise<PermissionStatus>
```

**Returns:** <code>Promise&lt;<a href="#permissionstatus">PermissionStatus</a>&gt;</code>

--------------------


### init(...)

```typescript
init(settings: RecordingInitSettings) => Promise<GenericResponse>
```

| Param          | Type                                                                    |
| -------------- | ----------------------------------------------------------------------- |
| **`settings`** | <code><a href="#recordinginitsettings">RecordingInitSettings</a></code> |

**Returns:** <code>Promise&lt;<a href="#genericresponse">GenericResponse</a>&gt;</code>

--------------------


### startRecord(...)

```typescript
startRecord(settings: RecordingStartSettings) => Promise<GenericResponse>
```

| Param          | Type                                                                      |
| -------------- | ------------------------------------------------------------------------- |
| **`settings`** | <code><a href="#recordingstartsettings">RecordingStartSettings</a></code> |

**Returns:** <code>Promise&lt;<a href="#genericresponse">GenericResponse</a>&gt;</code>

--------------------


### stopRecord()

```typescript
stopRecord() => Promise<RecordingData>
```

**Returns:** <code>Promise&lt;<a href="#recordingdata">RecordingData</a>&gt;</code>

--------------------


### addListener(...)

```typescript
addListener(eventName: 'recordingBuffer', listenerFunc: RecordingBufferListener) => Promise<PluginListenerHandle> & PluginListenerHandle
```

| Param              | Type                                 |
| ------------------ | ------------------------------------ |
| **`eventName`**    | <code>"recordingBuffer"</code>       |
| **`listenerFunc`** | <code>(state: any) =&gt; void</code> |

**Returns:** <code>Promise&lt;<a href="#pluginlistenerhandle">PluginListenerHandle</a>&gt; & <a href="#pluginlistenerhandle">PluginListenerHandle</a></code>

--------------------


### removeAllListeners()

```typescript
removeAllListeners() => Promise<void>
```

--------------------


### Interfaces


#### PermissionStatus

| Prop             | Type                                                                      |
| ---------------- | ------------------------------------------------------------------------- |
| **`microphone`** | <code>"prompt" \| "prompt-with-rationale" \| "granted" \| "denied"</code> |


#### GenericResponse

| Prop        | Type                 |
| ----------- | -------------------- |
| **`value`** | <code>boolean</code> |


#### RecordingInitSettings

| Prop                  | Type                                                  |
| --------------------- | ----------------------------------------------------- |
| **`voiceActivation`** | <code>boolean</code>                                  |
| **`sampleRate`**      | <code>8000 \| 11025 \| 16000 \| 22050 \| 44100</code> |


#### RecordingStartSettings

| Prop       | Type                |
| ---------- | ------------------- |
| **`path`** | <code>string</code> |


#### RecordingData

| Prop        | Type            |
| ----------- | --------------- |
| **`value`** | <code>{}</code> |


#### PluginListenerHandle

| Prop         | Type                                      |
| ------------ | ----------------------------------------- |
| **`remove`** | <code>() =&gt; Promise&lt;void&gt;</code> |

</docgen-api>
