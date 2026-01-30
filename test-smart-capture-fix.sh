#!/bin/bash

echo "🎯 Testing Smart Capture Fix - Native Service Level"
echo "=================================================="

echo "📱 App installed successfully. Now testing..."
echo ""
echo "🔍 What to look for in logs:"
echo "✅ GOOD: '⏭️ SKIPPING FRAME: Not in monitored app (com.allot)'"
echo "✅ GOOD: '⏭️ SKIPPING LOCAL ML: Not in monitored app (com.allot)'"
echo "❌ BAD:  '📝 Text:' or '📝 Extracted Text:' (should NOT appear for non-monitored apps)"
echo ""
echo "🧪 Test Steps:"
echo "1. Open the app and start Smart Capture"
echo "2. You should see SKIPPING messages (no text extraction)"
echo "3. Open TikTok/Instagram - should see PROCESSING messages"
echo "4. Switch back to other apps - should see SKIPPING again"
echo ""
echo "📊 Starting log monitoring..."

# Clear logs and start monitoring
adb logcat -c
adb logcat | grep -E "(SKIPPING FRAME|SKIPPING LOCAL ML|PROCESSING FRAME|PROCESSING LOCAL ML|📝 Text|📝 Extracted Text)"