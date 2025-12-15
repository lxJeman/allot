# IP Address Change Guide

## Overview

When switching networks (home ↔ hotspot ↔ office), the backend IP address needs to be updated in multiple files throughout the codebase.

## Step 1: Find Your Current IP Address

```bash
# Method 1: Primary command
ip route get 1.1.1.1 | grep -oP 'src \K\S+' 2>/dev/null || hostname -I | awk '{print $1}'

# Method 2: Alternative
hostname -I | awk '{print $1}'

# Method 3: Manual check
ifconfig | grep "inet " | grep -v 127.0.0.1
```

**Current IP:** `192.168.171.18`

## Step 2: Files That MUST Be Updated

### Core Application Files (CRITICAL)

1. **`android/app/src/main/java/com/allot/ScreenCaptureService.kt`**
   ```kotlin
   // Line ~42
   private var backendUrl = "http://192.168.171.18:3000/analyze"
   ```

2. **`services/aiDetectionService.ts`**
   ```typescript
   // Line ~58
   const BACKEND_URL = 'http://192.168.171.18:3000';
   ```

3. **`app/screen-capture.tsx`** (2 locations)
   ```typescript
   // Line ~218
   const response = await fetch('http://192.168.171.18:3000/analyze', {
   
   // Line ~286
   const response = await fetch('http://192.168.171.18:3000/analyze', {
   ```

4. **`app/phase4-demo.tsx`**
   ```typescript
   // Line ~152
   const response = await fetch('http://192.168.171.18:3000/analyze', {
   ```

5. **`rust-backend/src/main.rs`**
   ```rust
   // Line ~296 (log message only)
   info!("📱 Device should connect to http://192.168.171.18:3000");
   ```

### Documentation Files (OPTIONAL - for reference)

6. **Test Scripts:**
   - `rust-backend/test-backend.sh`
   - `test-war-detection.sh`

7. **Documentation Files:**
   - `TEST_GUIDE.md`
   - `QUICK_START_GUIDE.md`
   - Various phase documentation files

## Step 3: Quick Update Script

Create a script to automate IP changes:

```bash
#!/bin/bash
# update-ip.sh

OLD_IP="10.112.153.18"  # Previous IP
NEW_IP=$(ip route get 1.1.1.1 | grep -oP 'src \K\S+' 2>/dev/null || hostname -I | awk '{print $1}')

echo "🔄 Updating IP from $OLD_IP to $NEW_IP"

# Core files (CRITICAL)
sed -i "s/$OLD_IP/$NEW_IP/g" android/app/src/main/java/com/allot/ScreenCaptureService.kt
sed -i "s/$OLD_IP/$NEW_IP/g" services/aiDetectionService.ts
sed -i "s/$OLD_IP/$NEW_IP/g" app/screen-capture.tsx
sed -i "s/$OLD_IP/$NEW_IP/g" app/phase4-demo.tsx
sed -i "s/$OLD_IP/$NEW_IP/g" rust-backend/src/main.rs

# Test scripts
sed -i "s/$OLD_IP/$NEW_IP/g" rust-backend/test-backend.sh
sed -i "s/$OLD_IP/$NEW_IP/g" test-war-detection.sh

echo "✅ IP updated to $NEW_IP"
echo "🔄 Now rebuild Android app: ./gradlew assembleDebug"
echo "🚀 Restart backend: cargo run --release"
```

## Step 4: Rebuild & Test

### Rebuild Android App
```bash
cd android
./gradlew assembleDebug
```

### Restart Backend
```bash
cd rust-backend
cargo run --release
```

### Test Connection
```bash
# Health check
curl http://192.168.171.18:3000/health

# Expected response:
# {"status":"healthy","message":"Allot Backend is running"...}
```

## Step 5: Verification Checklist

- [ ] Android app rebuilt successfully
- [ ] Backend shows correct IP in logs: `📱 Device should connect to http://192.168.171.18:3000`
- [ ] Health check responds: `curl http://192.168.171.18:3000/health`
- [ ] App connects without "Network request failed" errors
- [ ] Screen captures are processed successfully

## Common Network Scenarios

### Home Network
```
Typical IP: 192.168.1.x or 192.168.0.x
Router: Home WiFi router
```

### Mobile Hotspot
```
Typical IP: 10.x.x.x or 192.168.43.x
Router: Phone's hotspot
```

### Office Network
```
Typical IP: 192.168.x.x or 10.x.x.x
Router: Corporate network
```

## Troubleshooting

### "Network request failed" Error
1. Check IP is correct: `ip route get 1.1.1.1 | grep -oP 'src \K\S+'`
2. Verify backend is running: `curl http://NEW_IP:3000/health`
3. Check Android app was rebuilt after IP change
4. Ensure phone and computer are on same network

### "Error processing capture" Error
1. Check backend logs for detailed error messages
2. Verify all 5 core files have been updated with new IP
3. Test backend endpoint manually:
   ```bash
   curl -X POST http://192.168.171.18:3000/analyze \
     -H "Content-Type: application/json" \
     -d '{"image":"test","width":100,"height":100}'
   ```

### Backend Not Accessible
1. Check firewall settings
2. Verify backend binds to `0.0.0.0:3000` (not `127.0.0.1`)
3. Test from another device on same network

## Network Change History

| Date | Network | IP Address | Status |
|------|---------|------------|--------|
| 2025-11-24 | Home | 192.168.100.47 | ✅ Working |
| 2025-11-24 | Hotspot | 10.112.153.18 | ✅ Working |
| 2025-11-24 | Current | 192.168.171.18 | ✅ Working |

## Future Improvements

Consider implementing dynamic IP detection to avoid manual updates:

```typescript
// Dynamic IP detection (future enhancement)
const findBackendUrl = async () => {
  const possibleIPs = [
    '192.168.171.18',  // Current
    '192.168.100.47',  // Home
    '10.112.153.18',   // Hotspot
    '192.168.1.100',   // Common home
  ];
  
  for (const ip of possibleIPs) {
    try {
      const response = await fetch(`http://${ip}:3000/health`, { timeout: 2000 });
      if (response.ok) return `http://${ip}:3000`;
    } catch (e) {
      continue;
    }
  }
  throw new Error('Backend not found on any known IP');
};
```