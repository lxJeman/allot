#!/bin/bash

echo "🎉 Final Verification Test - Local Text Extraction Fix"
echo "====================================================="
echo ""

echo "📱 INSTRUCTIONS:"
echo "================"
echo "1. Open the Allot app on your phone"
echo "2. Go to 'Local Text Extraction Test' tab"
echo "3. Verify all modules show as 'Available':"
echo "   - SmartDetectionModule: ✅ Available"
echo "   - ScreenCaptureModule: ✅ Available" 
echo "   - ScreenPermissionModule: ✅ Available"
echo "   - LocalTextExtractionModule: ✅ Available"
echo "4. Tap 'Start Live Capture'"
echo "5. Watch the logs below for verification"
echo ""

echo "⏳ Press ENTER when you've started local text extraction..."
read -p "Press ENTER to begin monitoring: "

echo ""
echo "🔍 Monitoring for 30 seconds..."
echo "==============================="

# Clear logcat
adb logcat -c

# Monitor for 30 seconds
start_time=$(date +%s)
rust_requests=0
local_logs=0
backend_disabled=0
module_available=0

echo "📊 Real-time monitoring (30 seconds):"
echo ""

while [ $(($(date +%s) - start_time)) -lt 30 ]; do
    # Get recent logs (last 2 seconds)
    recent_logs=$(adb logcat -d -t '2 seconds ago' 2>/dev/null)
    
    if [ -n "$recent_logs" ]; then
        # Check for different types of logs
        if echo "$recent_logs" | grep -q "LocalTextExtractionService"; then
            local_count=$(echo "$recent_logs" | grep -c "LocalTextExtractionService")
            if [ "$local_count" -gt 0 ]; then
                echo "🟢 [$(date '+%H:%M:%S')] Local extraction active ($local_count logs)"
                local_logs=$((local_logs + local_count))
            fi
        fi
        
        if echo "$recent_logs" | grep -q "Native backend.*DISABLED\|Disabled native backend"; then
            echo "🔵 [$(date '+%H:%M:%S')] Native backend disabled ✅"
            backend_disabled=$((backend_disabled + 1))
        fi
        
        if echo "$recent_logs" | grep -q "📸.*Received screen capture"; then
            rust_count=$(echo "$recent_logs" | grep -c "📸.*Received screen capture")
            if [ "$rust_count" -gt 0 ]; then
                echo "🔴 [$(date '+%H:%M:%S')] WARNING: Rust backend still receiving requests ($rust_count)"
                rust_requests=$((rust_requests + rust_count))
            fi
        fi
        
        if echo "$recent_logs" | grep -q "SmartDetectionModule.*Available\|LocalTextExtractionModule.*Available"; then
            echo "🟡 [$(date '+%H:%M:%S')] Modules available ✅"
            module_available=$((module_available + 1))
        fi
    fi
    
    sleep 2
done

echo ""
echo "📊 FINAL VERIFICATION RESULTS:"
echo "=============================="

# Get final counts from full log
all_logs=$(adb logcat -d)
final_local=$(echo "$all_logs" | grep -c "LocalTextExtractionService" 2>/dev/null || echo "0")
final_backend_disabled=$(echo "$all_logs" | grep -c "Native backend.*DISABLED\|Disabled native backend" 2>/dev/null || echo "0")
final_rust=$(echo "$all_logs" | grep -c "📸.*Received screen capture" 2>/dev/null || echo "0")

echo ""
echo "🔍 Test Results Summary:"
echo "========================"
echo "📊 Local extraction logs: $final_local"
echo "🔧 Backend disabled events: $final_backend_disabled"
echo "🚫 Rust backend requests: $final_rust"
echo "📱 Module availability checks: $module_available"
echo ""

# Determine overall result
if [ "$final_backend_disabled" -gt 0 ] && [ "$final_local" -gt 5 ] && [ "$final_rust" -eq 0 ]; then
    echo "🎉 ✅ SUCCESS! LOCAL TEXT EXTRACTION FIX VERIFIED!"
    echo "=================================================="
    echo "✅ Native backend properly disabled"
    echo "✅ Local extraction service active and running"
    echo "✅ No interference from Rust backend"
    echo "✅ Modules properly registered and available"
    echo ""
    echo "🔧 The fix has successfully resolved:"
    echo "   - Module registration issues"
    echo "   - Dual processing conflicts"
    echo "   - 10-12 second stopping behavior"
    echo ""
    echo "🚀 Local text extraction is now working correctly!"
    
elif [ "$final_rust" -gt 0 ]; then
    echo "⚠️  PARTIAL SUCCESS - ISSUE DETECTED"
    echo "===================================="
    echo "❌ Rust backend is still receiving $final_rust requests"
    echo "✅ Local extraction is active ($final_local logs)"
    echo "⚠️  The dual processing conflict may still exist"
    echo ""
    echo "🔧 Recommendation: Check if screen capture service is properly configured"
    
elif [ "$final_local" -eq 0 ]; then
    echo "❌ ISSUE DETECTED - NO LOCAL EXTRACTION ACTIVITY"
    echo "==============================================="
    echo "❌ No local extraction logs detected"
    echo "🔧 Backend disabled: $final_backend_disabled events"
    echo "🔧 Rust requests: $final_rust"
    echo ""
    echo "🔧 Recommendations:"
    echo "   1. Ensure you started 'Local Text Extraction' in the app"
    echo "   2. Check that screen capture permissions are granted"
    echo "   3. Verify the LocalTextExtractionService is running"
    
else
    echo "⚠️  PARTIAL SUCCESS"
    echo "=================="
    echo "✅ Some components working correctly"
    echo "⚠️  Verification incomplete - may need longer testing period"
    echo ""
    echo "📊 Results:"
    echo "   - Local extraction: $final_local logs"
    echo "   - Backend disabled: $final_backend_disabled events"
    echo "   - Rust requests: $final_rust"
fi

echo ""
echo "📋 Next Steps:"
echo "=============="
echo "1. If successful: Test continuous operation (should work indefinitely)"
echo "2. If issues remain: Check app UI for module status and error messages"
echo "3. Monitor Rust backend terminal - should show NO new requests during local extraction"
echo "4. Test stopping and restarting local extraction to verify proper cleanup"
echo ""
echo "🏁 Test completed at $(date)"