#!/bin/bash

echo "🧪 Complete Local Text Extraction Test Flow"
echo "==========================================="
echo ""

echo "📱 STEP-BY-STEP INSTRUCTIONS:"
echo "=============================="
echo ""
echo "1. 🚀 Open the Allot app on your phone"
echo "2. 📱 Go to 'Screen Capture' tab"
echo "3. 🎬 Tap 'Open Screen Capture' to start screen capture service"
echo "4. ✅ Grant permissions if prompted"
echo "5. 🧪 Go to 'Local Text Extraction Test' tab"
echo "6. 🎯 Tap 'Start Local Text Extraction'"
echo ""
echo "⏳ After completing steps 1-6, press ENTER to start monitoring..."
read -p "Press ENTER when you've completed the steps above: "

echo ""
echo "🔍 Monitoring logs for local text extraction..."
echo "=============================================="

# Clear logcat
adb logcat -c

# Monitor for 30 seconds
echo "📊 Monitoring for 30 seconds (you should see local extraction logs)..."
echo ""

start_time=$(date +%s)
rust_requests=0
local_logs=0
backend_disabled=0

while [ $(($(date +%s) - start_time)) -lt 30 ]; do
    # Get recent logs (last 2 seconds)
    recent_logs=$(adb logcat -d -t '2 seconds ago' 2>/dev/null | grep -E "(LocalTextExtractionService|Native backend.*DISABLED|📸.*Received screen capture)" 2>/dev/null)
    
    if [ -n "$recent_logs" ]; then
        echo "$recent_logs" | while read line; do
            timestamp=$(date '+%H:%M:%S')
            
            if echo "$line" | grep -q "LocalTextExtractionService"; then
                echo "🟢 [$timestamp] LOCAL: $line"
                local_logs=$((local_logs + 1))
            elif echo "$line" | grep -q "Native backend.*DISABLED"; then
                echo "🔵 [$timestamp] CONFIG: $line"
                backend_disabled=$((backend_disabled + 1))
            elif echo "$line" | grep -q "📸.*Received screen capture"; then
                echo "🔴 [$timestamp] RUST: $line"
                rust_requests=$((rust_requests + 1))
            fi
        done
    fi
    
    sleep 2
done

echo ""
echo "📊 Final Analysis:"
echo "=================="

# Get final counts from full log
all_logs=$(adb logcat -d)
final_local=$(echo "$all_logs" | grep -c "LocalTextExtractionService" 2>/dev/null || echo "0")
final_backend_disabled=$(echo "$all_logs" | grep -c "Native backend.*DISABLED\|Disabled native backend" 2>/dev/null || echo "0")
final_rust=$(echo "$all_logs" | grep -c "📸.*Received screen capture" 2>/dev/null || echo "0")

echo "🔍 Local extraction logs: $final_local"
echo "🔍 Backend disabled logs: $final_backend_disabled"
echo "🔍 Rust backend requests: $final_rust"
echo ""

if [ "$final_backend_disabled" -gt 0 ] && [ "$final_local" -gt 3 ] && [ "$final_rust" -eq 0 ]; then
    echo "🎉 SUCCESS! Local text extraction is working correctly!"
    echo "   ✅ Native backend was disabled"
    echo "   ✅ Local extraction is active"
    echo "   ✅ No interference from Rust backend"
    echo ""
    echo "🔧 The fix has resolved the 10-12 second stopping issue!"
elif [ "$final_rust" -gt 0 ]; then
    echo "❌ ISSUE: Rust backend is still receiving requests"
    echo "   This means the fix didn't work completely"
elif [ "$final_local" -eq 0 ]; then
    echo "❌ ISSUE: No local extraction activity detected"
    echo "   Make sure you started local text extraction in the app"
else
    echo "⚠️  PARTIAL: Some components working, but verification incomplete"
fi

echo ""
echo "🛑 Don't forget to stop local text extraction in the app when done testing!"
echo ""
echo "📋 Next steps:"
echo "=============="
echo "1. If successful: The app should now work continuously without stopping"
echo "2. If issues: Check that screen capture service is running first"
echo "3. Monitor your Rust backend terminal - it should show NO new requests"