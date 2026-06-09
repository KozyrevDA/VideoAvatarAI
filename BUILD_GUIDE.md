# Сборка APK — Нейросеть Видео

## Требования

| Инструмент | Версия | Скачать |
|---|---|---|
| Android Studio | Ladybug+ | [developer.android.com](https://developer.android.com/studio) |
| JDK | 17 или 21 | включён в Android Studio |
| Android SDK | API 34+ | через Android Studio SDK Manager |

---

## Способ 1 — Android Studio (рекомендуется)

```bash
# 1. Клонируй репозиторий
git clone https://github.com/KozyrevDA/VideoAvatarAI.git
cd VideoAvatarAI

# 2. Открой в Android Studio
#    File → Open → выбери папку VideoAvatarAI

# 3. Подожди синхронизацию Gradle (~5 минут первый раз)

# 4. Build → Build Bundle(s) / APK(s) → Build APK(s)
```

APK появится в: `composeApp/build/outputs/apk/debug/composeApp-debug.apk`

---

## Способ 2 — Командная строка

```bash
git clone https://github.com/KozyrevDA/VideoAvatarAI.git
cd VideoAvatarAI
bash build_apk.sh
```

---

## Установка на устройство

```bash
# Включи Developer options + USB Debugging на телефоне

# Установка Debug APK
adb install composeApp/build/outputs/apk/debug/composeApp-debug.apk

# Или просто скинь APK на телефон и открой
```

---

## Переменные окружения для подписи Release APK

```bash
export KEYSTORE_PATH="/path/to/keystore/release.keystore"
export KEYSTORE_PASSWORD="VideoAvatarAI2025"
export KEY_ALIAS="videoavataraii"
export KEY_PASSWORD="VideoAvatarAI2025"

./gradlew :composeApp:assembleRelease
```

> ⚠️ Для публикации в RuStore создай новый keystore со своим паролем!

---

## Частые проблемы

**SDK not found:**
```bash
export ANDROID_HOME=$HOME/Library/Android/sdk  # macOS
export ANDROID_HOME=$HOME/Android/Sdk           # Linux
```

**Gradle build failed — clean:**
```bash
./gradlew clean
./gradlew :composeApp:assembleDebug
```

**Kotlin version mismatch:**
- В Android Studio: File → Invalidate Caches → Restart
