#!/bin/bash

echo "⚡ Quick Build & Install (USB)"
echo "============================="

# Check USB device
if ! adb devices | grep -q "device$"; then
    echo "❌ No USB device connected!"
    echo "Please connect your Android device via USB"
    exit 1
fi

DEVICE_ID=$(adb devices | grep "device$" | cut -f1)
echo "✅ USB device: $DEVICE_ID"

# Quick build (skip cleaning for speed)
echo "🚀 Quick building..."
cd android
./gradlew assembleDebug --quiet

if [ $? -eq 0 ]; then
    echo "✅ Build complete! Installing..."
    adb install -r app/build/outputs/apk/debug/app-debug.apk
    
    if [ $? -eq 0 ]; then
        echo "🎉 APK installed successfully!"
        echo "📱 Check your device - app should be updated"
    else
        echo "❌ Install failed"
    fi
else
    echo "❌ Build failed"
fi

cd ..