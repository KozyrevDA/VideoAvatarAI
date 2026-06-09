#!/bin/bash
# Сборка APK для Нейросеть Видео
# Запуск: bash build_apk.sh

set -e
echo "🔨 Собираем APK..."

# Проверяем окружение
if ! command -v java &> /dev/null; then
  echo "❌ Java не найдена. Установи JDK 17+: https://adoptium.net/"
  exit 1
fi

JAVA_VER=$(java -version 2>&1 | awk -F '"' '/version/ {print $2}' | cut -d'.' -f1)
if [ "$JAVA_VER" -lt 17 ]; then
  echo "❌ Нужна Java 17+. Текущая: $JAVA_VER"
  exit 1
fi

if [ -z "$ANDROID_HOME" ]; then
  echo "⚠️  ANDROID_HOME не задан. Попробуем стандартные пути..."
  if [ -d "$HOME/Library/Android/sdk" ]; then
    export ANDROID_HOME="$HOME/Library/Android/sdk"
  elif [ -d "$HOME/Android/Sdk" ]; then
    export ANDROID_HOME="$HOME/Android/Sdk"
  else
    echo "❌ Android SDK не найден. Установи Android Studio."
    exit 1
  fi
fi
echo "✅ Android SDK: $ANDROID_HOME"

# Переменные для подписи
export KEYSTORE_PATH="${KEYSTORE_PATH:-$(pwd)/keystore/release.keystore}"
export KEYSTORE_PASSWORD="${KEYSTORE_PASSWORD:-VideoAvatarAI2025}"
export KEY_ALIAS="${KEY_ALIAS:-videoavataraii}"
export KEY_PASSWORD="${KEY_PASSWORD:-VideoAvatarAI2025}"

echo ""
echo "📦 Сборка Debug APK (быстрая)..."
./gradlew :composeApp:assembleDebug --stacktrace 2>&1 | tail -20

echo ""
echo "📦 Сборка Release APK..."
./gradlew :composeApp:assembleRelease --stacktrace 2>&1 | tail -20

echo ""
echo "✅ ГОТОВО!"
echo ""
echo "APK файлы:"
find composeApp/build/outputs/apk -name "*.apk" 2>/dev/null | while read apk; do
  size=$(du -h "$apk" | cut -f1)
  echo "  📱 $apk ($size)"
done

echo ""
echo "Установка на устройство:"
echo "  adb install composeApp/build/outputs/apk/debug/composeApp-debug.apk"
