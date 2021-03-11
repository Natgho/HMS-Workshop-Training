# Audio Transcription Demo

Demo Project for HMS Audio Transcription

![screenshot](https://i.imgur.com/KZJ8uwv.jpg)


# Preparation:
If you're a beginner, this part might look a bit complicated, but don't worry, each step is very simple and clear. Let's start to:

1. Configure your Android Project and Integrate AppGallery resources. Follow the official guides of HMS: 
https://developer.huawei.com/consumer/en/doc/development/AppGallery-connect-Guides/agc-get-started

2. Enable ML Kit Services for your app at Step-6
https://developer.huawei.com/consumer/en/codelab/HMSPreparation/index.html#0

3. Add dependency into your build.gradle (app)
```gradle
dependencies{
  // other dependencies
  implementation 'com.huawei.hms:ml-computer-voice-aft:2.1.0.300'
  implementation 'com.huawei.hms:ml-computer-voice-realtimetranscription:2.1.0.300'
}
```

4. Sync gradle with your project.

# ⚠️Limitations:

## Audio File Transcription


| Engine | Max File Duration |            Max File Size             
|--------|-------------------|--------------------------------------
| Short  | Up to 1 minute    | Less than 4 MB after Base64 encoding 
| Long   | Up to 5 Hours     | 300 MB                            


| Audio File Format and Encoding Format |
|---------------------------------------|
| WAV (corresponding to PCM)            |
| M4A (corresponding to AAC)            |
| AMR (corresponding to AMR)            |


|    Languages     |
|------------------|
| English          |
| Mandarin Chinese |


---

## Real Time Transcription

| Location      | Chinese | English | French
|---------------|---------|---------|--------
| Europe        | √       | √       | √      
| Russia        | -       | √       | -      
| Asia          | √       | √       | √      
| Africa        | √       | √       | √      
| Latin America | √       | √       | √      
| China         | √       | √       | -      



| Engine    | Max Duration 
|-----------|--------------
| Real Time | 5 Hours      



| Supported Devices |
|-------------------|
| Huawei Phones     |
| Honor Phones      |


# Pricing
HMS mostly provides monthly free quota for cloud services, or even most services are fully free in trial period. Audio Transcription service is priced by February 11, 2021 with pay-as-you-go model.


|    Free Quota    |        Price        |
|------------------|---------------------|
| 60 minutes/month | EUR0.003/15 seconds |


# Development

https://medium.com/huawei-developers/convert-human-voice-to-text-with-audio-file-transcription-c8b5aca8abf4
