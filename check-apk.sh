#!/bin/bash

echo "🔍 APK Analysis Tool"
echo "===================="
echo ""

APK_PATH="android/app/build/outputs/apk/release/app-release.apk"

if [ ! -f "$APK_PATH" ]; then
    echo "❌ APK not found at: $APK_PATH"
    echo "💡 Build the APK first with: ./build.sh (option 6)"
    exit 1
fi

echo "📦 APK Found: $APK_PATH"
echo ""

# Check APK size
APK_SIZE=$(du -h "$APK_PATH" | cut -f1)
echo "📊 APK Size: $APK_SIZE"
echo ""

# Extract and check app.json in the APK
echo "🔍 Checking bundled configuration..."
echo ""

# Create temp directory
TEMP_DIR=$(mktemp -d)
echo "📁 Extracting APK to: $TEMP_DIR"

# Extract APK
unzip -q "$APK_PATH" -d "$TEMP_DIR"

# Check for JavaScript bundle
if [ -f "$TEMP_DIR/assets/index.android.bundle" ]; then
    BUNDLE_SIZE=$(du -h "$TEMP_DIR/assets/index.android.bundle" | cut -f1)
    echo "✅ JavaScript bundle found: $BUNDLE_SIZE"
    
    # Check if config is in the bundle
    if grep -q "backendUrl" "$TEMP_DIR/assets/index.android.bundle" 2>/dev/null; then
        echo "✅ Backend URL config found in bundle"
        
        # Try to extract the URL (this is minified so might not work perfectly)
        echo ""
        echo "🔍 Searching for backend URLs in bundle..."
        grep -o 'https://[^"]*ngrok[^"]*' "$TEMP_DIR/assets/index.android.bundle" | head -5 || echo "   (URLs are minified)"
        grep -o 'http://192\.168\.[^"]*' "$TEMP_DIR/assets/index.android.bundle" | head -5 || echo "   (No local IPs found - good!)"
    else
        echo "⚠️  Backend URL config not found in bundle"
    fi
else
    echo "❌ JavaScript bundle NOT found!"
    echo "   This means the APK won't work without Metro"
fi

echo ""

# Check for native libraries
echo "🔍 Checking native libraries..."
for arch in arm64-v8a armeabi-v7a x86 x86_64; do
    if [ -d "$TEMP_DIR/lib/$arch" ]; then
        LIB_COUNT=$(ls "$TEMP_DIR/lib/$arch" | wc -l)
        LIB_SIZE=$(du -sh "$TEMP_DIR/lib/$arch" | cut -f1)
        echo "✅ $arch: $LIB_COUNT libraries ($LIB_SIZE)"
    else
        echo "❌ $arch: NOT FOUND"
    fi
done

echo ""

# Check for app modules
echo "🔍 Checking for compiled code..."
DEX_COUNT=$(unzip -l "$APK_PATH" | grep "\.dex" | wc -l)
DEX_SIZE=$(unzip -l "$APK_PATH" | grep "\.dex" | awk '{sum+=$1} END {print sum/1024/1024 " MB"}')
echo "✅ Found $DEX_COUNT dex files (${DEX_SIZE})"
echo "   (Kotlin native modules are compiled into these dex files)"

echo ""

# Cleanup
rm -rf "$TEMP_DIR"

echo "📝 Summary:"
echo "   APK Size: $APK_SIZE"
echo "   Location: $APK_PATH"
echo ""
echo "💡 To view logs from installed app:"
echo "   adb logcat | grep -E '(Config|Backend|HTTP)'"
echo ""
echo "💡 To install:"
echo "   adb install -r $APK_PATH"
