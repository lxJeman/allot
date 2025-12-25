#!/bin/bash

echo "🔍 Virtual Display Debug Monitor"
echo "================================"
echo "This will show if the virtual display is working..."
echo ""
echo "Key indicators:"
echo "✅ '🔥 Image available listener triggered!' = Virtual display is generating frames"
echo "❌ No '🔥' logs = Virtual display is broken"
echo ""
echo "Press Ctrl+C to stop monitoring"
echo ""

# Clear logs and start monitoring
adb logcat -c
adb logcat | grep -E "(🔥|ScreenCaptureModule|VirtualDisplay|ImageReader)" --line-buffered | while read line; do
    echo "[$(date '+%H:%M:%S')] $line"
done