#!/bin/bash

# Test script to verify SEQUENTIAL processing (not overwhelming)
# This monitors the timing between cycles to ensure they're truly sequential

echo "🔍 ═══════════════════════════════════════"
echo "🔍 SEQUENTIAL PROCESSING VERIFICATION"
echo "🔍 Monitoring cycle timing and frequency"
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

# Clear logs
adb logcat -c

echo ""
echo "📋 Instructions:"
echo "1. Open the Allot app on your device"
echo "2. Navigate to Tests tab → Local Text Extraction"
echo "3. Tap 'Start Live Capture' and grant permissions"
echo "4. Watch the timing between cycles below"
echo ""
echo "🎯 What to Look For:"
echo "   • 'STARTING NEW CYCLE' should appear every 2-5 seconds (not constantly)"
echo "   • 'CYCLE COMPLETE' should show total time for each cycle"
echo "   • 'Waiting Xms before next cycle' should show delays between cycles"
echo "   • NO rapid-fire captures (that would indicate multiple systems running)"
echo ""
echo "📊 Monitoring SEQUENTIAL timing (press Ctrl+C to stop):"
echo "════════════════════════════════════════════════════════"

# Monitor logs specifically for cycle timing
adb logcat | grep -E "(STARTING NEW CYCLE|CYCLE COMPLETE|Waiting.*before next cycle|Frame processing completed|Sequential processing|🔄|✅|⏳)" --line-buffered | while read line; do
    timestamp=$(date '+%H:%M:%S')
    echo "[$timestamp] $line"
done