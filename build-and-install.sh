#!/bin/bash

echo "🔨 Building Android APK with latest changes..."

# Remove any cached bundles first
echo "🧹 Cleaning cached bundles..."
rm -f android/app/src/main/assets/index.android.bundle
rm -rf android/app/build/intermediates/assets/

echo "🔨 Building APK..."
cd android
./gradlew assembleDebug

if [ $? -eq 0 ]; then
    echo "✅ Build successful! Installing APK..."
    adb install app/build/outputs/apk/debug/app-debug.apk
    
    if [ $? -eq 0 ]; then
        echo "🚀 APK installed successfully!"
        echo "📱 You can now test the bundled version without needing the dev server"
        echo "🔔 The bundled version now includes all your latest JavaScript changes!"
    else
        echo "❌ Failed to install APK"
    fi
else
    echo "❌ Build failed"
fi