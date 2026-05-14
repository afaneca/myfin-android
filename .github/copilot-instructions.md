# Copilot instructions for MyFin Android

This repository contains the Android client for the MyFin personal finance platform. The Android project lives under `android\`; the repo root mainly holds docs, release metadata, Fastlane metadata, and images.

## Build, test, and lint commands

Run these from `android\`.

| Task | Command |
| --- | --- |
| Build the local dev APK | `.\gradlew.bat :app:assembleDevDebug` |
| Build the prod release APK | `.\gradlew.bat :app:assembleProdRelease` |
| Build the prod release bundle | `.\gradlew.bat :app:bundleProdRelease` |
| Lint the dev variant | `.\gradlew.bat :app:lintDevDebug` |
| Run JVM unit tests for the dev variant | `.\gradlew.bat :app:testDevDebugUnitTest` |
| Run one JVM test class | `.\gradlew.bat :app:testDevDebugUnitTest --tests "com.afaneca.myfin.ui.common.nav.NavigatorLogicTest"` |
| Run connected instrumentation tests | `.\gradlew.bat :app:connectedDevDebugAndroidTest` |
| Run one connected instrumentation test class | `.\gradlew.bat :app:connectedDevDebugAndroidTest -Pandroid.testInstrumentationRunnerArguments.class=com.afaneca.myfin.ExampleInstrumentedTest` |

The app uses `dev` and `prod` product flavors. `prod` pulls its signing config from `android\keystore.properties` when that file is present. The module is configured for Java 11 (`jvmToolchain(11)` and Java 11 `compileOptions`).

## High-level architecture

- `android\app\src\main\java\com\afaneca\myfin\ui\main\MainActivity.kt` is the app shell. It declares the sealed `AppRoute` navigation keys, owns the drawer/snackbar/loading shell, and wires public and private destinations into the custom Navigation 3 setup in `ui\common\nav\`.
- The app is a single `:app` module with clear layer boundaries:
  - `ui\` contains feature packages such as `login`, `dashboard`, `transactions`, and `settings`.
  - `domain\` contains use cases, gateway interfaces, domain models, and the project-wide `Result`/`DomainError` types.
  - `data\` contains Retrofit service definitions, DTOs plus `toDomain()` mappers, repository implementations, biometric helpers, and data-source abstractions.
  - `di\` contains the Hilt modules that bind repositories/data sources and provide the Retrofit/OkHttp/Moshi stack.
- Persistence is split deliberately:
  - `TransientDataSource` is in-memory only and holds session data, auth state, and the current `BalanceDigest`.
  - `PersistentDataSource` is backed by DataStore and persists the base API URL, dynamic-theme preference, saved username, and biometric credentials.
- Networking is centralized in `NetworkModule` and `RetrofitNetworkDataSource`. Every request gets auth headers and app metadata headers, and the base URL is rewritten per request from the saved settings value instead of being hard-coded at call sites.
- The repository also includes the broader backend contract in `android\app\src\main\java\com\afaneca\myfin\docs\openapi-spec.json`. Use that file when you need endpoint details beyond the subset currently declared in `data\network\MyFinApiServices.kt`.
- Session expiry is also centralized. `safeApiCall(...)` maps HTTP 401/403 to `setUnauthenticated()` on the transient data source, and `MainViewModel` observes that auth state to force the user back to login. Process death is treated as logged out as well: `HasSessionDataUseCase` checks the in-memory session, and `MainViewModel` redirects to login if that session is gone.

## Key conventions

- Feature packages follow a consistent `Contract` + `ViewModel` + `Screen` + `Content` split. `Contract` defines `MainState`, `Event`, and `Effect`; `Screen` owns `hiltViewModel()`, lifecycle-aware effect collection, and navigation callbacks; `Content` stays mostly presentational.
- One-off UI events use `EffectMutableSharedFlow(...)`, not state flags. Follow the existing pattern of keeping persistent UI state in `MainState` and navigation/snackbar/dialog work in `Effect`.
- Use cases are the boundary between UI and data. New screen logic should usually call a `domain\usecase\*UseCase`, which depends on a `domain\repository\*Gateway`; the concrete repository implementation belongs in `data\repository\`.
- DTO-to-domain mapping is colocated with the DTOs as `toDomain()` extension functions in `data\model\*.kt`. Repositories use those mappers before returning domain models.
- When adding or changing API integration, cross-check `android\app\src\main\java\com\afaneca\myfin\docs\openapi-spec.json` before editing Retrofit services or DTOs. It documents the wider REST API, including the custom `authusername` and `sessionkey` header scheme used by authenticated endpoints.
- Navigation does not use a standard `NavHost`. Add new destinations by extending the sealed `AppRoute` set in `MainActivity.kt`, then register the destination in `publicNav()` or `privateNav()`. Sheet-style destinations use `BottomSheetSceneStrategy.bottomSheet()`.
- Global loading is exposed through `MainContract.LocalLoadingState` and rendered by `GlobalLoadingOverlay`. Reuse that mechanism for app-wide blocking loading instead of creating another top-level spinner path.
- Settings normalize API URLs before saving them by adding `https://` when missing and trimming a trailing slash. Keep that behavior intact if you touch settings or request routing.
