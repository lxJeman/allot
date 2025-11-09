# Testing Guide - Fixes Verification

## Prerequisites

1. **Start Rust Backend:**
```bash
cd rust-backend
cargo run --release
```

2. **Install Android App:**
```bash
./build-and-install.sh
```

## Test 1: War Content Detection (Text Only = Safe)

### Expected Behavior
Text mentioning war should be **SAFE**, only graphic images should be **HARMFUL**.

### Test Steps
1. Open TikTok or Instagram
2. Find a post with text: "The front lines in Ukraine look like a scene from a movie"
3. Let the app capture the screen

### Expected Logs
```
📝 Extracted Text: "The front lines in Ukraine look like a scene from a movie..."
🏷️  Category: safe_content
⚠️  Harmful: NO ✅
🎯 Action: CONTINUE
💡 Recommendation: Text-only content about war is safe
```

### ❌ Old Behavior (WRONG)
```
🏷️  Category: harmful_content
⚠️  Harmful: YES ⚠️
🎯 Action: BLUR
🚨 Risk Factors: political content
```

## Test 2: Accessibility Service Detection

### Expected Behavior
Permission page should accurately show if accessibility service is running.

### Test Steps
1. Open Allot app
2. Go to Permissions tab
3. Enable Accessibility Service
4. Return to app
5. Tap "Refresh Status"

### Expected Logs
```
Accessibility check:
  - Enabled in Settings: true
  - Service Running: true
  - Final Status: true
```

### Expected UI
- Accessibility Service card shows: **"Granted"** (green badge)

### ❌ Old Behavior (WRONG)
- UI shows: "Granted"
- Logs show: `Accessibility service running: false`

## Test 3: Caching (No Duplicate Analysis)

### Expected Behavior
Same content should be analyzed once, then cached.

### Test Steps
1. Open TikTok
2. Stay on same video for 5+ seconds
3. Watch the logs

### Expected Logs (First Capture)
```
📸 Received screen capture: 720x1600
👁️  OCR complete: 269 chars extracted
🔑 Text hash: 8b873b2acdb85dc2
🔍 Cache MISS - analyzing with AI
🧠 Classification complete: safe_content
💾 Result cached for future requests
```

### Expected Logs (Second Capture - Same Content)
```
📸 Received screen capture: 720x1600
👁️  OCR complete: 269 chars extracted
🔑 Text hash: 8b873b2acdb85dc2
💾 Cache HIT - returning cached result
```

### ❌ Old Behavior (WRONG)
- Same text analyzed multiple times
- Different results for same content:
  - First: "safe_content (95%)"
  - Second: "harmful_content (92%)"
  - Third: "safe_content (92%)"

## Test 4: Consistency Check

### Expected Behavior
Same text should ALWAYS get same classification.

### Test Steps
1. Capture same screen 3 times
2. Compare results

### Expected Results
All 3 captures should have:
- ✅ Same category
- ✅ Same confidence
- ✅ Same action
- ✅ Same recommendation

### ❌ Old Behavior (WRONG)
Results varied randomly:
- Capture 1: safe_content (95%)
- Capture 2: harmful_content (92%)
- Capture 3: safe_content (92%)

## Verification Checklist

- [ ] Text about war is marked as SAFE
- [ ] Only graphic war images are marked as HARMFUL
- [ ] Accessibility status matches between UI and logs
- [ ] Same content is cached (no duplicate analysis)
- [ ] Same text always gets same classification
- [ ] Cache hit logs appear for repeated content
- [ ] No more inconsistent results

## Troubleshooting

### Issue: Backend not receiving requests
```bash
# Check backend is running
curl http://192.168.100.47:3000/health
```

### Issue: Accessibility still shows false
1. Go to Android Settings → Accessibility
2. Find "Allot" service
3. Toggle OFF then ON
4. Return to app and refresh

### Issue: Cache not working
- Check logs for "Cache HIT" messages
- If not appearing, check text hash is same
- Cache expires after 24 hours

## Success Criteria

✅ **War Content:** Text mentions = safe, graphic images = harmful
✅ **Accessibility:** UI and logs match
✅ **Caching:** Same content analyzed once
✅ **Consistency:** Same text = same result every time
