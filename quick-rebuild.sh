#!/bin/bash

echo "⚡ Quick Production Rebuild"
echo "==========================="
echo ""

# Check if ngrok URL is provided
if [ -z "$1" ]; then
    echo "Usage: ./quick-rebuild.sh <ngrok-url>"
    echo "Example: ./quick-rebuild.sh https://abc123.ngrok-free.dev"
    exit 1
fi

NGROK_URL="$1"

echo "📋 Configuration:"
echo "   Backend URL: $NGROK_URL"
echo ""

# Update app.json
echo "🔧 Updating app.json..."
node -e "
const fs = require('fs');
const appJson = JSON.parse(fs.readFileSync('app.json', 'utf8'));
appJson.expo.extra = appJson.expo.extra || {};
appJson.expo.extra.backendUrl = '$NGROK_URL';
fs.writeFileSync('app.json', JSON.stringify(appJson, null, 2));
console.log('✅ app.json updated');
"

echo ""
echo "🔨 Building release APK (incremental build)..."
echo "   This should be faster than a clean build (~3-5 minutes)"
echo ""

cd android
./gradlew assembleRelease --no-daemon

if [ $? -eq 0 ]; then
    cd ..
    echo ""
    echo "✅ Build successful!"
    echo ""
    
    # Run APK check
    ./check-apk.sh
    
    echo ""
    echo "📱 To install:"
    echo "   adb install -r android/app/build/outputs/apk/release/app-release.apk"
    echo ""
    echo "📊 To view logs:"
    echo "   adb logcat | grep -E '(Config|Backend|HTTP|Allot)'"
else
    cd ..
    echo ""
    echo "❌ Build failed!"
    exit 1
fi
