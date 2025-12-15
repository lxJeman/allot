#!/bin/bash
# Automated IP Update Script for Allot Backend

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

echo -e "${BLUE}🔄 Allot Backend IP Update Script${NC}"
echo "=================================="

# Get current IP
NEW_IP=$(ip route get 1.1.1.1 | grep -oP 'src \K\S+' 2>/dev/null || hostname -I | awk '{print $1}')

if [ -z "$NEW_IP" ]; then
    echo -e "${RED}❌ Could not detect IP address${NC}"
    exit 1
fi

echo -e "${YELLOW}📍 Detected IP: $NEW_IP${NC}"

# Ask for confirmation
read -p "Update all files to use this IP? (y/N): " -n 1 -r
echo
if [[ ! $REPLY =~ ^[Yy]$ ]]; then
    echo -e "${YELLOW}⏹️  Update cancelled${NC}"
    exit 0
fi

echo -e "${BLUE}🔄 Updating core application files...${NC}"

# Core files (CRITICAL)
FILES=(
    "android/app/src/main/java/com/allot/ScreenCaptureService.kt"
    "services/aiDetectionService.ts"
    "app/screen-capture.tsx"
    "app/phase4-demo.tsx"
    "rust-backend/src/main.rs"
    "rust-backend/test-backend.sh"
    "test-war-detection.sh"
)

# Update each file
for file in "${FILES[@]}"; do
    if [ -f "$file" ]; then
        # Find old IP in file
        OLD_IP=$(grep -oP '\d+\.\d+\.\d+\.\d+' "$file" | head -1)
        if [ ! -z "$OLD_IP" ] && [ "$OLD_IP" != "$NEW_IP" ]; then
            sed -i "s/$OLD_IP/$NEW_IP/g" "$file"
            echo -e "${GREEN}✅ Updated: $file ($OLD_IP → $NEW_IP)${NC}"
        else
            echo -e "${YELLOW}⏭️  Skipped: $file (already up to date)${NC}"
        fi
    else
        echo -e "${RED}❌ Missing: $file${NC}"
    fi
done

echo -e "\n${BLUE}🔨 Rebuilding Android app...${NC}"
cd android
if ./gradlew assembleDebug > /dev/null 2>&1; then
    echo -e "${GREEN}✅ Android app rebuilt successfully${NC}"
else
    echo -e "${RED}❌ Android build failed${NC}"
fi
cd ..

echo -e "\n${BLUE}🧪 Testing backend connection...${NC}"
if curl -s "http://$NEW_IP:3000/health" > /dev/null 2>&1; then
    echo -e "${GREEN}✅ Backend is accessible at http://$NEW_IP:3000${NC}"
    echo -e "${GREEN}🎉 IP update completed successfully!${NC}"
else
    echo -e "${YELLOW}⚠️  Backend not running. Start it with:${NC}"
    echo -e "${BLUE}   cd rust-backend && cargo run --release${NC}"
fi

echo -e "\n${BLUE}📋 Summary:${NC}"
echo -e "   New IP: ${GREEN}$NEW_IP${NC}"
echo -e "   Backend URL: ${GREEN}http://$NEW_IP:3000${NC}"
echo -e "   Health Check: ${GREEN}curl http://$NEW_IP:3000/health${NC}"