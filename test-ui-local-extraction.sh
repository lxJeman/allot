#!/bin/bash

echo "🧪 Testing Local Text Extraction via UI"
echo "======================================="
echo ""

echo "📱 Instructions for manual testing:"
echo "1. Open the Allot app on your phone"
echo "2. Go to the 'Local Text Extraction Test' screen"
echo "3. Tap 'Start Local Text Extraction'"
echo "4. Watch the logs below for confirmation"
echo ""

echo "🔍 Monitoring logs for local text extraction activity..."
echo "Expected behavior:"
echo "✅ Should see LocalTextExtractionService logs"
echo "✅ Should see 'Native backend: DISABLED' messages"
echo "❌ Should NOT see any Rust backend requests"
echo ""

# Clear logcat and start monitoring
adb logcat -c

echo "📊 Live log monitoring (press Ctrl+C to stop):"
echo "==============================================="

# Monitor relevant logs
adb logcat | grep -E "(LocalTextExtractionService|Native backend|📸.*Received screen capture|ScreenCaptureService)" --line-buffered | while read line; do
    timestamp=$(date '+%H:%M:%S')
    
    # Color code different types of logs
    if echo "$line" | grep -q "LocalTextExtractionService"; then
        echo "🟢 [$timestamp] LOCAL: $line"
    elif echo "$line" | grep -q "Native backend.*DISABLED"; then
        echo "🔵 [$timestamp] CONFIG: $line"
    elif echo "$line" | grep -q "📸.*Received screen capture"; then
        echo "🔴 [$timestamp] RUST: $line"
    elif echo "$line" | grep -q "ScreenCaptureService"; then
        echo "🟡 [$timestamp] CAPTURE: $line"
    else
        echo "⚪ [$timestamp] OTHER: $line"
    fi
done