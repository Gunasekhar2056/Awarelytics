# Awarelytics

**A context-aware digital presence coach for Android that combats phubbing using AI, BLE proximity sensing, and on-device ML.**

Awarelytics detects when you're ignoring people around you in favor of your phone (*phubbing*) and gently nudges you back to the real world — powered by Gemini AI coaching, usage analytics, and Bluetooth-based social context detection.

---

## Features

- ** Usage Analytics** — Tracks app usage patterns via `UsageStatsManager`
- ** BLE Social Context** — Detects nearby people using Bluetooth Low Energy scanning
- ** On-Device ML** — TensorFlow Lite model classifies phubbing behavior in real-time
- ** Gemini AI Coach** — Personalized coaching insights powered by Google's Gemini API
- ** Dashboard** — Visual breakdown of your digital presence habits
- ** Smart Nudges** — Contextual notifications and haptic feedback when phubbing is detected
- ** Firebase Auth** — Email/password and Google Sign-In authentication
- ** Cloud Sync** — Firestore-based data synchronization across devices
- ** Weekly Summaries** — AI-generated weekly behavior reports

---

## Architecture

```
com.awarelytics.app/
├── ai/                    # Gemini AI coach & weekly summary aggregator
├── data/
│   ├── local/             # Room DB (TelemetryLog, DriftEvent, DAOs)
│   ├── remote/            # Firebase Auth & Firestore managers
│   └── repository/        # Single source of truth repository
├── di/                    # Hilt dependency injection modules
├── ml/                    # TFLite phubbing classifier & feature aggregator
├── nudge/                 # Notification & haptic nudge manager
├── service/               # Background services (BLE scan, usage stats)
├── ui/
│   ├── navigation/        # Compose Navigation graph
│   ├── screens/           # Dashboard, Login, Settings screens
│   ├── theme/             # Material 3 theme & colors
│   └── viewmodel/         # Auth & Dashboard ViewModels
└── util/                  # Permission helper utilities
```

### Tech Stack

| Layer | Technology |
|-------|-----------|
| UI | Jetpack Compose + Material 3 |
| Navigation | Compose Navigation |
| DI | Hilt (Dagger) |
| Database | Room |
| Auth | Firebase Auth + Google Sign-In |
| Cloud | Firestore |
| ML | TensorFlow Lite |
| AI | Gemini API (via OkHttp) |
| Background | Foreground Services + WorkManager |
| Build | Gradle 8.10 + KSP |

---

## Prerequisites

Before you begin, make sure you have:

- **Android Studio** Hedgehog (2023.1.1) or later
- **JDK 17**
- **Android SDK** with API level 34 (Android 14)
- **A physical Android device** (API 26+) — BLE scanning doesn't work reliably on emulators
- **A Google account** for Firebase and Gemini API setup

---

## Getting Started

### 1. Clone the repository

```bash
git clone https://github.com/Gunasekhar2056/Awarelytics.git
cd Awarelytics
```

### 2. Set up Firebase

1. Go to the [Firebase Console](https://console.firebase.google.com/)
2. Create a new project (or use an existing one)
3. Add an Android app with package name: `com.awarelytics.app`
4. Download the `google-services.json` file
5. Place it in the `app/` directory:
   ```
   Awarelytics/
   └── app/
       └── google-services.json   ← place here
   ```
6. Enable **Authentication** → Sign-in providers:
   - Email/Password
   - Google
7. Enable **Cloud Firestore** in the Firebase Console

### 3. Get a Gemini API Key

1. Go to [Google AI Studio](https://aistudio.google.com/app/apikey)
2. Create a new API key
3. Copy the key for the next step

### 4. Configure local properties

Copy the example config and fill in your values:

```bash
cp local.properties.example local.properties
```

Then edit `local.properties`:

```properties
sdk.dir=YOUR_ANDROID_SDK_PATH
gemini.api.key=YOUR_GEMINI_API_KEY
firebase.api.key=YOUR_FIREBASE_API_KEY
```

> **Note:** `sdk.dir` is usually auto-populated by Android Studio when you first open the project.

### 5. Open in Android Studio

1. Open Android Studio
2. Select **File → Open** and navigate to the cloned `Awarelytics` folder
3. Wait for Gradle sync to complete (this will download all dependencies automatically)
4. If prompted, install any missing SDK components

### 6. Build and Run

1. Connect a physical Android device via USB (with USB debugging enabled)
2. Select your device from the device dropdown in Android Studio
3. Click **Run ▶** or press `Shift + F10`

> ** Important:** The app requires several runtime permissions. Grant them when prompted:
> - Usage Access (redirects to system settings)
> - Bluetooth & Location (for BLE scanning)
> - Notifications

---

## Configuration

### Build Variants

| Variant | Description |
|---------|------------|
| `debug` | Development build with debugging enabled |
| `release` | Production build (requires signing config) |

### Key Build Properties

Defined in `app/build.gradle.kts`:

- `minSdk = 26` (Android 8.0)
- `targetSdk = 34` (Android 14)
- `compileSdk = 34`
- `JVM Target = 17`

---

## Project Structure

```
Awarelytics/
├── app/
│   ├── build.gradle.kts          # App-level dependencies & config
│   ├── proguard-rules.pro        # ProGuard rules
│   └── src/
│       └── main/
│           ├── AndroidManifest.xml
│           ├── assets/
│           │   └── phubbing_model.tflite   # On-device ML model
│           ├── java/com/awarelytics/app/   # Kotlin source code
│           └── res/                        # Resources (icons, themes, strings)
├── build.gradle.kts              # Root-level build config (plugin versions)
├── settings.gradle.kts           # Project settings & repositories
├── gradle.properties             # Gradle JVM & Android config
├── gradle/
│   └── wrapper/                  # Gradle wrapper (auto-downloads Gradle 8.10)
├── gradlew                       # Gradle wrapper script (Unix)
├── gradlew.bat                   # Gradle wrapper script (Windows)
├── local.properties.example      # Template for API keys (copy → local.properties)
└── README.md                     # You are here
```

---

## Permissions

The app requests the following permissions:

| Permission | Purpose |
|-----------|---------|
| `INTERNET` | Firebase & Gemini API communication |
| `PACKAGE_USAGE_STATS` | Monitoring app usage patterns |
| `BLUETOOTH_SCAN` / `BLUETOOTH_CONNECT` | BLE proximity detection |
| `ACCESS_FINE_LOCATION` | Required for BLE scanning on Android 12+ |
| `VIBRATE` | Haptic feedback nudges |
| `POST_NOTIFICATIONS` | Smart nudge notifications |
| `FOREGROUND_SERVICE` | Background BLE scanning service |

---

## Contributing

1. Fork the repository
2. Create a feature branch: `git checkout -b feature/my-feature`
3. Commit your changes: `git commit -m 'Add my feature'`
4. Push to the branch: `git push origin feature/my-feature`
5. Open a Pull Request

---

## License

This project is built as part of **HackCrux** hackathon.

---

## Team

- [Gunasekhar2056](https://github.com/Gunasekhar2056)
- [niket-ranjan](https://github.com/niket-ranjan)
