#!/bin/bash

# Test script for the HYBRID system: Local ML Kit + Groq Backend
# This tests the complete flow: Screen Capture → Local ML Kit → Groq LLM Backend

echo "🔍 ═══════════════════════════════════════"
echo "🔍 HYBRID SYSTEM TEST SCRIPT"
echo "🔍 Screen Capture → Local ML Kit → Groq Backend"
echo "🔍 ═══════════════════════════════════════"
echo ""

# Check if device is connected
if ! adb devices | grep -q "device$"; then
    echo "❌ No Android device connected"
    echo "Please connect your device and enable USB debugging"
    exit 1
fi

echo "📱 Android device detected"
echo ""

# Check if backend is running
echo "🌐 Checking Groq backend status..."
if curl -s http://192.168.100.47:3000/health > /dev/null; then
    echo "✅ Groq backend is running"
else
    echo "❌ Groq backend is not running"
    echo "Please start the backend first:"
    echo "  cd rust-backend && cargo run"
    exit 1
fi

# Build and install the app
echo ""
echo "🔨 Building and installing app with HYBRID system..."
cd android
./gradlew assembleDebug
if [ $? -eq 0 ]; then
    adb install -r app/build/outputs/apk/debug/app-debug.apk
    if [ $? -eq 0 ]; then
        echo "✅ App installed successfully"
    else
        echo "❌ App installation failed"
        exit 1
    fi
else
    echo "❌ App build failed"
    exit 1
fi
cd ..

echo ""
echo "🚀 Starting hybrid system test..."
echo ""

# Clear logs
adb logcat -c

echo "� Test xInstructions:"
echo "1. Open the Allot app on your device"
echo "2. Navigate to Tests tab → Local Text Extraction"
echo "3. Make sure 'Background Operation' is ON"
echo "4. Tap 'Start Live Capture' and grant permissions"
echo "5. Open apps with text content (Instagram, Settings, Chrome, etc.)"
echo "6. Watch the logs below for the complete HYBRID flow"
echo ""
echo "🔍 Expected Flow (SEQUENTIAL):"
echo "1. 📸 Screen captured by ScreenCaptureService"
echo "2. 🤖 Text extracted by Local ML Kit (REPLACES Google Vision API)"
echo "3. 📤 Extracted text sent to Groq LLM Backend"
echo "4. 🧠 Text classified by Groq LLM"
echo "5. 📊 Analysis result returned"
echo "6. 🔄 Next cycle starts (no fixed interval)"
echo ""
echo "🎯 Key Features:"
echo "   • 🤖 LOCAL text extraction (replaces Google Vision API)"
echo "   • 🧠 GROQ LLM analysis (keeps intelligent classification)"
echo "   • ⚡ Sequential processing (capture → extract → analyze → repeat)"
echo "   • 🔒 Privacy-enhanced: only text sent to backend, not images"
echo "   • 💰 Cost savings: no Google Vision API fees"
echo "   • 🚀 Fast cycles (~1-2 seconds) for responsive content"
echo ""
echo "📊 Monitoring HYBRID flow (press Ctrl+C to stop):"
echo "════════════════════════════════════════════════════════"

# Monitor logs for the complete hybrid system
adb logcat | grep -E "(ScreenCaptureService|LocalTextExtractor|🔍|📸|📝|🤖|🧠|📊|⏱️|💾|🎯|✅|🔄|🛠️|⭐|🚀|💰|🔒|HOT PATH|COLD PATH|HYBRID ANALYSIS|Sequential|Groq Backend|extracted_text)" --line-buffered