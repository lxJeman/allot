# Expo Bare React Native Project (`allot`)

## 1. Project Overview

This project is a **bare-minimum Expo React Native app** configured for:

* Native Android and iOS builds
* Hermes JS engine enabled
* New Architecture (Fabric + TurboModules) enabled
* Metro Dev Server
* USB/Network debugging for Android devices

---

## 2. Prerequisites

Make sure the following are installed:

* **Node.js**: ≥20.x
* **npm**: latest
* **Watchman**: optional, recommended for Mac/Linux
* **Android Studio + SDK** (for Android builds)
* **Java JDK 11** or higher
* **Gradle** (comes with Android Studio)
* **Expo CLI** (if you want Expo commands)

**Set environment variables:**

```bash
export ANDROID_HOME=$HOME/Android/Sdk
export PATH=$PATH:$ANDROID_HOME/emulator
export PATH=$PATH:$ANDROID_HOME/tools
export PATH=$PATH:$ANDROID_HOME/tools/bin
export PATH=$PATH:$ANDROID_HOME/platform-tools
```

---

## 3. Core Commands

### 3.1 Install Dependencies

```bash
# Inside project root
npm install
```

> ⚠️ If you ever delete `node_modules` or move the project:
>
> ```bash
> rm -rf node_modules package-lock.json
> npm install
> ```

---

### 3.2 Start Metro Dev Server

**For device debugging via USB / network:**

```bash
npx react-native start --host <your_PC_IP> --reset-cache
```

* Example: `--host 192.168.100.47`
* `--reset-cache` is recommended if JS updates don’t show up

---

### 3.3 Run Android App

```bash
# Make sure a device is connected or emulator is running
adb devices   # verify device
adb reverse tcp:8081 tcp:8081  # redirect Metro port

npx react-native run-android
```

---

### 3.4 Build Android APK

```bash
cd android
./gradlew assembleDebug   # builds debug APK
./gradlew assembleRelease # builds release APK
cd ..
```

* **Note**: Make sure `local.properties` exists with SDK path:

```properties
sdk.dir=/home/alex/Android/Sdk
```

---

### 3.5 Clean & Reset

```bash
# Clean Gradle build cache
cd android
./gradlew clean
cd ..

# Remove JS caches
rm -rf node_modules
rm package-lock.json
npm install
npx react-native start --reset-cache
```

---

### 3.6 iOS (if needed)

```bash
cd ios
pod install
cd ..
npx react-native run-ios
```

> Requires Mac with Xcode installed.

---

## 4. Common Issues & Fixes

| Issue                                | Fix                                                                                                  |
| ------------------------------------ | ---------------------------------------------------------------------------------------------------- |
| **Gray screen, no UI**               | Make sure `index.js` only has `registerRootComponent(App)` and all JSX is in `App.js`                |
| **Cannot find SDK**                  | Add `local.properties` with `sdk.dir=` or set `ANDROID_HOME`                                         |
| **Metro caching issues**             | Run `npx react-native start --reset-cache`                                                           |
| **Dependencies missing**             | Delete `node_modules`, `package-lock.json`, then `npm install`                                       |
| **Hermes / new architecture issues** | Check `android/gradle.properties` flags: `hermesEnabled=true`, `newArchEnabled=true`                 |
| **App not connecting to Metro**      | Run `adb reverse tcp:8081 tcp:8081` if using USB; ensure device IP matches Metro host if using Wi-Fi |

---

## 5. Recommended Tips

* Always **rebuild native code** after adding new native modules:

```bash
cd android
./gradlew assembleDebug
cd ..
```

* Use **USB debugging** for reliable Metro connection.
* If you move the project between drives, **always clear caches**:

```bash
watchman watch-del-all
rm -rf node_modules package-lock.json
npm install
npx react-native start --reset-cache
```

* Check device logs for JS errors:

```bash
npx react-native log-android
npx react-native log-ios
```

---

## 6. Project Structure

```
allot/
├── App.js             # Main UI component
├── index.js           # Entry point (registerRootComponent)
├── android/           # Android native code
├── ios/               # iOS native code
├── metro.config.js    # Metro bundler config
├── package.json       # Dependencies and scripts
└── package-lock.json
```

---

## 7. Quick Command Cheat Sheet

| Action           | Command                                                |
| ---------------- | ------------------------------------------------------ |
| Start Metro      | `npx react-native start --host <IP> --reset-cache`     |
| Run Android      | `npx react-native run-android`                         |
| Build APK        | `cd android && ./gradlew assembleDebug`                |
| Clean Gradle     | `cd android && ./gradlew clean`                        |
| Install deps     | `npm install`                                          |
| Reset caches     | `rm -rf node_modules package-lock.json && npm install` |
| Reverse adb port | `adb reverse tcp:8081 tcp:8081`                        |
| Check devices    | `adb devices`                                          |

---

This README will **save your future self** from gray screens, missing Metro connections, or lost build commands.
