# 나의 이야기

[나의 이야기](https://play.google.com/store/apps/details?id=com.dhkim.timecapsule)는 타임캡슐처럼 사용자가 지정한 **장소 및 시간 이후에만 열람 가능한 피드**를 저장하여, 과거의 추억을 미래에 특별하게 확인할 수 있는 앱입니다.

## Getting Started

### 필수 설정 파일

#### 1. Firebase 설정
Firebase Cloud Messaging 및 Realtime Database를 사용합니다.
[Firebase 콘솔](https://console.firebase.google.com/)에서 Android 앱을 등록하고 발급받은 `google-services.json` 파일을 `app/` 디렉토리에 추가해주세요.

> 참고: [Firebase Cloud Messaging 가이드](https://firebase.google.com/docs/cloud-messaging?hl=ko)

#### 2. API 키 설정
Kakao API와 Naver Map API를 사용합니다. 아래 두 파일을 프로젝트 루트 경로에 생성해주세요.

**`local.properties`**
```properties
KAKAO_API_KEY="XXXXXXXXX"
KAKAO_ADMIN_KEY="XXXXXXXXX"
```

**`apikey.properties`**
```properties
NAVER_MAP_API_KEY=XXXXXXXXX
```

- Kakao API 키 발급: [Kakao Developers](https://developers.kakao.com/)
- Naver Map API 키 발급: [Naver Developers](https://developers.naver.com/main/)

---

## Tech Stack

### Language & Platform
| 항목 | 버전 |
|------|------|
| Kotlin | 2.0.20 |
| Android Gradle Plugin | 8.10.0 |
| Min SDK | 26 (Android 8.0) |
| Target SDK | 36 (Android 15) |

### UI
- **Jetpack Compose** (BOM 2024.06.00) — XML 없이 전면 선언형 UI
- **Material3** 1.2.1
- **Compose Navigation** 2.5.3 + Hilt Navigation Compose 1.0.0
- **Lottie Compose** 4.0.0 — 애니메이션
- **Glide Landscapist** 2.3.6 — 이미지 로딩
- **Accompanist Permissions** 0.35.1-alpha — 런타임 권한 처리

### Dependency Injection
- **Dagger Hilt** 2.51.1 (KSP 기반 컴파일 타임 코드 생성)

### Network
- **Retrofit** 2.9.0 + **OkHttp** 4.11.0 + Gson 2.8.6
- **Naver Map SDK** 3.19.1 / Naver Map Compose 1.6.0
- **Google Play Services Location** 21.2.0

### Local Storage
- **Room** 2.5.0 — 로컬 DB (스키마 v3, 자동 마이그레이션 지원)
- **DataStore** 1.0.0 — 사용자 환경설정 저장

### Firebase
- **Firebase Realtime Database** — 친구 목록 클라우드 동기화
- **Firebase Cloud Messaging (FCM)** 23.2.1 — 실시간 피드 공유 푸시 알림
- **Firebase Analytics** — 사용 통계

### Asynchronous & Background
- **Kotlin Coroutines** 1.10.2 + **Flow** — 반응형 상태 관리
- **WorkManager** 2.9.1 + Hilt WorkManager 1.2.0 — 24시간 주기로 오픈 가능한 타임캡슐 체크
- **Paging 3** 3.3.0 + Paging Compose — 목록 페이지네이션

### Camera
- **CameraX** 1.5.2
- **[DhCamera](https://github.com/kdh123/DhCamera)** 1.0.0-beta02

### Performance
- **Baseline Profile** 1.2.3 + Profile Installer 1.4.1 — 앱 시작 최적화
- **kotlinx-collections-immutable** 0.3.7 — Compose recomposition 최소화
- **Benchmark** 모듈 — 매크로벤치마크 성능 측정

### Test
- JUnit4, Robolectric 4.12, Espresso 3.5.1
- **Turbine** 1.2.0 — Flow 테스트
- Compose UI Test, Hilt Test

---

## Architecture

![Architecture](https://github.com/user-attachments/assets/a3e3bf58-d401-447c-bfcb-be283ce76612)

**Clean Architecture + Multi-Module + MVVM/MVI** 패턴을 기반으로 설계되었습니다.

```
Presentation Layer  (Feature Modules)
        ↓   UiState / SideEffect
Domain Layer        (UseCase + Model)
        ↓   Repository Interface
Data Layer          (Repository Impl + DataSource)
        ↓
Local (Room / DataStore)  |  Remote (Retrofit / Firebase)
```

### 모듈 구조

```
TimeCapsule/
├── app/                      # 앱 진입점, Application 클래스, 글로벌 DI
├── feature/
│   ├── main                  # 하단 탭 네비게이션, AppState
│   ├── home                  # 타임캡슐 목록·상세·추가 화면
│   ├── map                   # 지도 기반 타임캡슐 탐색
│   ├── trip                  # 여행 일정 관리
│   ├── friend                # 친구 관리 및 공유
│   ├── location              # 장소 검색
│   ├── notification          # 알림 목록
│   ├── setting               # 앱 설정
│   └── onboarding            # 온보딩
├── core/
│   ├── domain/               # UseCase + 도메인 모델 (story, user, trip, location, setting)
│   ├── data/                 # Repository 구현체 + DataSource (story, user, trip, location, setting)
│   ├── database/             # Room DB 엔티티·DAO (타임캡슐, 여행, 친구)
│   ├── network/              # Retrofit API 클라이언트
│   ├── datastore/            # DataStore 기반 설정 저장
│   ├── ui/                   # 공용 Composable 컴포넌트
│   ├── designsystem/         # 테마·컬러·타이포그래피
│   ├── common/               # 유틸리티, 확장 함수 (RestartableStateFlow 포함)
│   └── work/                 # WorkManager 태스크
├── benchmark/                # 매크로벤치마크 성능 측정
└── baselineprofile/          # Baseline Profile 생성
```

### 상태 관리 (MVI)

- ViewModel이 `StateFlow<UiState>`로 화면 상태를 노출
- 단발성 이벤트(네비게이션, 토스트)는 `Channel<SideEffect>` → `receiveAsFlow()`로 처리
- UI 상태 컬렉션을 `ImmutableList` / `ImmutableMap`으로 선언해 불필요한 recomposition 방지

### 네비게이션

- Compose Navigation `NavHost` 기반 (startDestination: 타임캡슐 메인)
- 하단 탭 5개 (타임캡슐 / 지도 / 추가 / 여행 / 친구)
- Feature별 중첩 NavGraph + `SavedStateHandle`로 복잡한 객체 전달
