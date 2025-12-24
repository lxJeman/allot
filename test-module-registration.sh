#!/bin/bash

echo "🔍 Testing LocalTextExtractionModule Registration"
echo "================================================"
echo ""

echo "📱 Instructions:"
echo "1. Open the Allot app on your phone"
echo "2. Go to 'Local Text Extraction Test' tab"
echo "3. Check if 'SmartDetection Module' now shows 'Available' instead of 'Not Found'"
echo ""

echo "🔍 Monitoring logs for module registration..."
echo "============================================="

# Clear logcat
adb logcat -c

echo "⏳ Waiting for app to load and module to register..."
echo "Look for logs indicating module registration or availability..."
echo ""

# Monitor for module-related logs
timeout 15s adb logcat | grep -E "(LocalTextExtractionModule|SmartDetection|Module.*available|Module.*not found)" --line-buffered | while read line; do
    timestamp=$(date '+%H:%M:%S')
    echo "📋 [$timestamp] $line"
done

echo ""
echo "✅ Monitoring complete!"
echo ""
echo "📋 Expected Results:"
echo "==================="
echo "✅ In the app UI: 'SmartDetection Module: ✓ Available'"
echo "✅ No 'Module not found' errors in logs"
echo "✅ LocalTextExtractionModule should be accessible"
echo ""
echo "🧪 Next: Try starting local text extraction to verify full functionality"