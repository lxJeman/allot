#!/bin/bash

echo "🔥 HOT PATH TEST"
echo "================"
echo ""
echo "Starting app and monitoring for HOT PATH vs COLD PATH..."
echo ""

# Start the app
adb shell am start -n com.allot/.MainActivity > /dev/null 2>&1

echo "📱 App started. Please:"
echo "1. Go to 'Local Text Extraction' tab"
echo "2. Turn ON 'Background Operation'"
echo "3. Tap 'Start Live Capture'"
echo ""
echo "🔍 Monitoring logs for HOT PATH vs COLD PATH:"
echo "✅ HOT PATH: 'Direct bitmap captured: (HOT PATH)'"
echo "❌ COLD PATH: 'Converting base64 to bitmap'"
echo ""
echo "Press Ctrl+C to stop monitoring..."
echo "═══════════════════════════════════════════════════════════"

# Clear logs and monitor
adb logcat -c
adb logcat | grep -E "(HOT PATH|COLD PATH|Direct bitmap captured|Converting base64 to bitmap|LocalTextExtractionService.*📸)"