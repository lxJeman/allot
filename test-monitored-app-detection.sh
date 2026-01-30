#!/bin/bash

echo "🎯 Testing Monitored App Detection"
echo "=================================="

echo "📱 Current apps available for testing:"
adb shell pm list packages | grep -E "(tiktok|musically|instagram|facebook)" | head -5

echo ""
echo "🔍 Starting app detection monitoring..."
echo "Instructions:"
echo "1. Keep this terminal open"
echo "2. Open TikTok or Instagram on your phone"
echo "3. Watch for 'ENTERED MONITORED APP' messages"
echo "4. Switch back to other apps to see 'LEFT MONITORED APP'"
echo "5. Press Ctrl+C to stop"
echo ""

# Monitor specific app detection events
adb logcat -c
adb logcat | grep -E "(ENTERED MONITORED APP|LEFT MONITORED APP|APP CHANGE|isMonitoredApp.*true)"