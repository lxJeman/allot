#!/bin/bash

echo "🔧 ═══════════════════════════════════════"
echo "🔧 LOCAL TEXT EXTRACTION FIXES V2 TEST"
echo "🔧 ═══════════════════════════════════════"

# Check if device is connected
if ! adb devices | grep -q "device$"; then
    echo "❌ No Android device connected"
    exit 1
fi

echo "📱 Device connected"

echo ""
echo "🔍 KEY INSIGHT DISCOVERED:"
echo "The issue was that BOTH systems were running simultaneously:"
echo "1. 🦀 Rust Backend - Processing frames via HTTP (what you saw in logs)"
echo "2. 🤖 Local ML Service - Also processing frames locally (hidden logs)"
echo ""
echo "🔧 FIX APPLIED:"
echo "- When starting Local Text Extraction, Rust backend is now DISABLED"
echo "- Only local ML Kit processing will run"
echo "- No more dual processing or eco mode conflicts"
echo ""
echo "📋 Testing Instructions:"
echo "1. Open the Allot app"
echo "2. Go to Tests → Local Text Extraction"
echo "3. Grant screen capture permissions"
echo "4. Test the following scenarios:"
echo ""
echo "🧪 SINGLE EXTRACTION TEST:"
echo "- Press 'Test Single Extraction'"
echo "- Should see: 'Single extraction completed' in logs"
echo "- Should NOT continue looping"
echo ""
echo "🔄 LIVE CAPTURE TEST (FIXED):"
echo "- Press 'Start Live Capture'"
echo "- Navigate to content with text (Instagram, TikTok, etc.)"
echo "- Should see ONLY local ML Kit processing (no Rust backend logs)"
echo "- Should continue indefinitely without stopping"
echo "- Press 'Stop Capture' to end"
echo ""
echo "🔧 FORCE RESTART TEST:"
echo "- If extraction seems stuck, use 'Force Restart' button"
echo "- Should restart the extraction loop"
echo ""
echo "📊 PERFORMANCE STATS:"
echo "- Check that stats update with real numbers"
echo "- Should show total captures > 0"
echo ""
echo "⚠️  IMPORTANT: When testing local extraction:"
echo "- You should NOT see Rust backend logs (🚀 Starting Allot AI Detection Backend)"
echo "- You should ONLY see LocalTextExtractionService logs"
echo "- This confirms the fix is working"
echo ""
echo "🔍 Monitoring logs (press Ctrl+C to stop):"
echo "════════════════════════════════════════"

# Monitor logs with better filtering
adb logcat -s LocalTextExtractionService:D LocalTextExtractionModule:D ScreenCaptureService:D ReactNativeJS:I ExpoModulesCore:I AllotPermissions:D ScreenCaptureModule:D | while read line; do
    # Color code different types of messages
    if [[ $line == *"❌"* ]]; then
        echo -e "\033[31m$line\033[0m"  # Red for errors
    elif [[ $line == *"⚠️"* ]]; then
        echo -e "\033[33m$line\033[0m"  # Yellow for warnings
    elif [[ $line == *"✅"* ]]; then
        echo -e "\033[32m$line\033[0m"  # Green for success
    elif [[ $line == *"🔄"* ]] || [[ $line == *"🎬"* ]]; then
        echo -e "\033[36m$line\033[0m"  # Cyan for process messages
    elif [[ $line == *"📸"* ]] || [[ $line == *"🔍"* ]]; then
        echo -e "\033[35m$line\033[0m"  # Magenta for capture/extraction
    else
        echo "$line"
    fi
done