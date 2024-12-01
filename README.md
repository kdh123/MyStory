# 나의 이야기
[나의 이야기](https://play.google.com/store/apps/details?id=com.dhkim.timecapsule)는 타임캡슐처럼 과거의 추억을 미래에 볼 수 있는 앱입니다. 

## Required
- IDE : Android Studio Koala | 2024.1.1 Patch 1
- JDK : 17
- Kotlin : 1.9.0 이상
- OS : 8.0 (Oreo) 이상

## Including in your project
- 나의 이야기는 Firebase Cloud Messaging을 사용합니다. [Firebase Cloud Messaging 가이드](https://firebase.google.com/docs/cloud-messaging?hl=ko)를 따라 google-services.json 파일을 추가해주세요.
- 나의 이야기는 Kakao API와 Naver Map API를 사용합니다. [Kakao](https://developers.kakao.com/) 및 [Naver](https://developers.naver.com/main/) 개발자 센터로부터 발급 받은 키를 다음 파일에 추가하여 Project 경로에 위치시켜 주세요.
- local.properties
```kotlin
KAKAO_API_KEY="XXXXXXXXX"
KAKAO_ADMIN_KEY="XXXXXXXXX"
```
- apikey.properties
```kotlin
NAVER_MAP_API_KEY=XXXXXXXXX
```

## Language
- Kotlin

## Libraries
- Jetpack
  - ViewModel
  - Compose
  - Data Store
  - Room
  - Hilt
  - WorkManager
  - Navigation
- Kotlin
  - Coroutine
  - Flow
  - Serialization
- Network
  - Retrofit
  - Okhttp
- Camera
  - [DhCamera](https://github.com/kdh123/DhCamera)
- Google
  - Firebase Cloud Message
  - Firebase Analytics
  - Firebase Database
  - Gson
- Image
  - Glide
- Baseline Profile
- Test
  - Junit4
  - Roboletric
  - Compose Test
  - Hilt Test
- Naver
  - Naver Map
 
## Architecture
![Untitled](https://github.com/user-attachments/assets/a3e3bf58-d401-447c-bfcb-be283ce76612)


