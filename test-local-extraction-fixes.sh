#!/bin/bash

echo "🔧 ═══════════════════════════════════════"
echo "🔧 LOCAL TEXT EXTRACTION FIXES TEST"
echo "🔧 ═══════════════════════════════════════"
echo ""

# Check if device is connected
if ! adb devices | grep -q "device$"; then
    echo "❌ No Android device connected"
    exit 1
fi

echo "📱 Device connected"
echo ""

# Clear logs
adb logcat -c

echo "📋 Testing Instructions:"
echo "1. Open the Allot app"
echo "2. Go to Tests → Local Text Extraction"
echo "3. Grant screen capture permissions"
echo "4. Test the following scenarios:"
echo ""
echo "   🧪 SINGLE EXTRACTION TEST:"
echo "   - Press 'Test Single Extraction'"
echo "   - Should see: 'Single extraction completed' in logs"
echo "   - Should NOT continue looping"
echo ""
echo "   🔄 LIVE CAPTURE TEST:"
echo "   - Press 'Start Live Capture'"
echo "   - Navigate to content with text"
echo "   - Should see continuous text extraction"
echo "   - Press 'Stop Capture' to end"
echo ""
echo "   📊 PERFORMANCE STATS:"
echo "   - Check that stats update with real numbers"
echo "   - Should show successful extractions > 0"
echo ""

echo "🔍 Monitoring logs (press Ctrl+C to stop):"
echo "════════════════════════════════════════"

# Monitor specific logs for the fixes
adb logcat | grep -E "(LocalTextExtractionService|LocalTextExtractionModule|🧪|🎬|📸|🔍|✅|❌|⚠️|Single extraction|Cache HIT|Cache MISS)" --line-buffered