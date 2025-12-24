#!/bin/bash

echo "🧪 Testing ML Kit Direct Functionality"
echo "======================================"
echo ""

# Clear logcat
adb logcat -c

echo "📱 Testing ML Kit with synthetic image..."

# Test ML Kit directly with a synthetic image
adb shell am broadcast -a com.allot.TEST_MLKIT_DIRECT

echo "⏳ Waiting for ML Kit test to complete..."
sleep 5

echo ""
echo "📊 ML Kit Test Results:"
echo "======================"

# Check for ML Kit test logs
adb logcat -d | grep -E "(LocalTextExtractionModule|ML Kit|MLKit)" | tail -20

echo ""
echo "🔍 Looking for specific test results..."

# Look for test completion
test_results=$(adb logcat -d | grep "ML Kit test completed" | tail -1)
if [ -n "$test_results" ]; then
    echo "✅ ML Kit test found: $test_results"
else
    echo "❌ No ML Kit test completion found"
fi

# Look for extracted text
extracted_text=$(adb logcat -d | grep "ML Kit result:" | tail -1)
if [ -n "$extracted_text" ]; then
    echo "✅ Text extraction: $extracted_text"
else
    echo "❌ No text extraction results found"
fi

# Look for any ML Kit errors
ml_errors=$(adb logcat -d | grep -E "(ML Kit.*failed|MLKit.*error)" | tail -3)
if [ -n "$ml_errors" ]; then
    echo "⚠️ ML Kit errors found:"
    echo "$ml_errors"
else
    echo "✅ No ML Kit errors detected"
fi

echo ""
echo "📋 Summary:"
echo "----------"
echo "✅ Check the logs above to see if ML Kit is working properly"
echo "✅ Look for 'Hello World' and 'ML Kit Test' in the extracted text"
echo "✅ If you see extracted text, ML Kit is working!"