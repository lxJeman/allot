#!/bin/bash

# Get current IP
CURRENT_IP=$(ip route get 1 | awk '{print $7}' | head -1)
OLD_IP_PATTERN="192\.168\.100\.[0-9]+"

echo "🔧 IP Address Updater"
echo "===================="
echo "💻 Current IP: $CURRENT_IP"

# Find all files with old IP addresses
echo "🔍 Searching for old IP addresses..."

FILES_TO_UPDATE=(
    "services/aiDetectionService.ts"
    "app/phase4-demo.tsx"
    "app/screen-capture.tsx"
    "android/app/src/main/java/com/allot/ScreenCaptureService.kt"
    "rust-backend/src/main.rs"
)

UPDATED_COUNT=0

for file in "${FILES_TO_UPDATE[@]}"; do
    if [ -f "$file" ]; then
        # Check if file contains old IP pattern
        if grep -q "$OLD_IP_PATTERN" "$file"; then
            echo "📝 Updating $file..."
            
            # Update the IP address
            sed -i "s/$OLD_IP_PATTERN/$CURRENT_IP/g" "$file"
            
            UPDATED_COUNT=$((UPDATED_COUNT + 1))
        fi
    fi
done

if [ $UPDATED_COUNT -gt 0 ]; then
    echo "✅ Updated $UPDATED_COUNT files with new IP: $CURRENT_IP"
    echo ""
    echo "🔄 Next steps:"
    echo "1. Restart backend: cd rust-backend && cargo run"
    echo "2. Rebuild app: ./quick-build-usb.sh"
    echo "3. Test connection: curl http://$CURRENT_IP:3000/health"
else
    echo "✅ All files already have correct IP: $CURRENT_IP"
fi

echo ""
echo "🧪 Testing backend connection..."
if curl -s "http://$CURRENT_IP:3000/health" > /dev/null; then
    echo "✅ Backend is accessible at http://$CURRENT_IP:3000"
else
    echo "❌ Backend not accessible. Make sure it's running:"
    echo "   cd rust-backend && cargo run"
fi