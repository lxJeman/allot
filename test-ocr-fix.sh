#!/bin/bash

echo "🔍 OCR FIX TEST - Testing Higher Quality Image Capture"
echo "=================================================="
echo ""
echo "📋 WHAT WAS FIXED:"
echo "1. ❌ OLD: JPEG 60% quality (too blurry for OCR)"
echo "2. ✅ NEW: JPEG 85% quality (better for text detection)"
echo "3. ❌ OLD: 720x1600 resolution (too small)"
echo "4. ✅ NEW: 1080p+ resolution (better text clarity)"
echo ""
echo "📋 TEST PROCEDURE:"
echo "1. Build and install the updated app"
echo "2. Open Allot app and start screen capture"
echo "3. Press HOME button (don't close app)"
echo "4. Open TikTok Lite"
echo "5. Watch logs for OCR improvements"
echo ""
echo "🔍 What to look for:"
echo "✅ 'OCR complete: X chars extracted' (X > 0)"
echo "✅ 'Classification complete: category (confidence)'"
echo "✅ 'Tokens: X in, Y out, Z total' (Groq API usage)"
echo "❌ 'No text detected in image' (should be rare now)"
echo ""
echo "📊 Starting OCR fix test..."
echo "========================================"

# Build and install
echo "🔨 Building updated app..."
cd android
./gradlew assembleDebug

if [ $? -eq 0 ]; then
    echo "✅ Build successful"
    echo "📱 Installing on device..."
    adb install -r app/build/outputs/apk/debug/app-debug.apk
    
    if [ $? -eq 0 ]; then
        echo "✅ Installation successful"
        echo ""
        echo "🚀 Starting enhanced logging..."
        echo "========================================"
        
        # Clear logs and start monitoring
        adb logcat -c
        adb logcat | grep -E "(ScreenCaptureService|OCR|Vision|Groq|chars extracted|Classification|Tokens)"
    else
        echo "❌ Installation failed"
        exit 1
    fi
else
    echo "❌ Build failed"
    exit 1
fi