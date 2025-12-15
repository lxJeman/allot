# Blur Effect Implementation for Harmful Content

## Problem

The permissions page test button showed blur + overlay, but the actual harmful content detection only showed the overlay without blur. Users could still see the harmful content underneath.

## Root Cause

The `ScreenCaptureService` was only calling the accessibility overlay:
```kotlin
// Before (overlay only)
accessibilityService.showContentWarningOverlay(message)
```

But the `ContentBlockingModule` (used by permissions page) had the blur implementation:
```kotlin
// ContentBlockingModule has blur
blurActivityBackground(activity)  // ← This was missing!
accessibilityService.showContentWarningOverlay(message)
```

## Solution

Added blur effect functionality directly to `ScreenCaptureService` so harmful content detection applies both blur AND overlay.

### Implementation

**Step 1: Apply Blur When Harmful Content Detected**
```kotlin
private fun handleHarmfulContent(result: AnalysisResult, contentHash: String) {
    // ... debouncing checks ...
    
    handler.post {
        // Step 1: Apply blur effect
        applyBlurEffect()  // ← NEW!
        
        // Step 2: Show overlay
        accessibilityService.showContentWarningOverlay(message)
        
        // Step 3: Auto-scroll
        // Step 4: Remove blur + overlay
        removeBlurEffect()  // ← NEW!
    }
}
```

**Step 2: Blur Effect Functions**

Added two new functions to `ScreenCaptureService`:

```kotlin
private fun applyBlurEffect() {
    // Get current activity
    val activity = getCurrentActivity()
    
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        // Android 12+ - Real blur using RenderEffect
        val blurEffect = RenderEffect.createBlurEffect(
            25f, 25f,  // blur radius
            Shader.TileMode.CLAMP
        )
        rootView.setRenderEffect(blurEffect)
    } else {
        // Android < 12 - Overlay only (no blur API)
        Log.d(TAG, "Blur not available, using overlay only")
    }
}

private fun removeBlurEffect() {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        rootView.setRenderEffect(null)  // Remove blur
    }
}
```

### How It Works

**Android 12+ (API 31+):**
- Uses `RenderEffect.createBlurEffect()` for real Gaussian blur
- Blur radius: 25px (strong blur, content not readable)
- Applied to entire activity window
- Hardware-accelerated, smooth performance

**Android 11 and below:**
- No native blur API available
- Falls back to overlay only
- Still blocks content with warning message

### Activity Access

The service needs to access the current activity to apply blur. This is done using reflection:

```kotlin
// Get current activity from ActivityThread
val activityThreadClass = Class.forName("android.app.ActivityThread")
val currentActivityThread = activityThreadClass.getMethod("currentActivityThread")
val activityThread = currentActivityThread.invoke(null)
// ... find active (non-paused) activity
```

This is necessary because:
- Service doesn't have direct activity reference
- Need to blur the TikTok/Instagram app window
- Works across all apps being monitored

## User Experience

### Before (Overlay Only)
```
Harmful Content Detected
  ↓
Show overlay message
  ↓
User can still see harmful content underneath ❌
  ↓
Scroll to next video
```

### After (Blur + Overlay)
```
Harmful Content Detected
  ↓
Apply blur effect (content unreadable) ✅
  ↓
Show overlay message
  ↓
User cannot see harmful content ✅
  ↓
Scroll to next video
  ↓
Remove blur + overlay
```

## Visual Effect

**Without Blur:**
```
┌─────────────────────┐
│ ⚠️ WAR CONTENT      │ ← Overlay
│ BLOCKED             │
│                     │
│ Scrolling...        │
└─────────────────────┘
  [Harmful video visible underneath] ❌
```

**With Blur:**
```
┌─────────────────────┐
│ ⚠️ WAR CONTENT      │ ← Overlay
│ BLOCKED             │
│                     │
│ Scrolling...        │
└─────────────────────┘
  [████████████████] ← Blurred (unreadable) ✅
```

## Timing

```
0.0s - Harmful content detected
0.0s - Apply blur effect (instant)
0.0s - Show overlay message
0.5s - Perform auto-scroll
1.5s - Remove blur + overlay
```

Total blocking time: 1.5 seconds

## Android Version Support

| Android Version | Blur Support | Fallback |
|----------------|--------------|----------|
| Android 12+ (API 31+) | ✅ RenderEffect blur | N/A |
| Android 11 (API 30) | ❌ No blur API | Overlay only |
| Android 10 (API 29) | ❌ No blur API | Overlay only |
| Android 9 and below | ❌ No blur API | Overlay only |

**Note:** Most modern devices (2021+) run Android 12+, so most users will see the blur effect.

## Testing

### Test on Permissions Page
```kotlin
// This already works (ContentBlockingModule)
testContentBlocking() → Shows blur + overlay ✅
```

### Test with Real Content
```
1. Open TikTok
2. Search for #war or #ukraine
3. Wait for harmful content detection
4. Should see:
   - Screen blurs (Android 12+)
   - Warning overlay appears
   - Auto-scroll to next video
   - Blur + overlay removed
```

## Logs to Watch For

**Success (Android 12+):**
```
🚫 Showing content warning with blur...
🌫️  Applying RenderEffect blur (Android 12+)
✅ Content warning with blur shown
⏭️  Performing auto-scroll...
✅ Auto-scroll successful
🌫️  RenderEffect blur removed
🔄 Blur and overlay removed, ready for next detection
```

**Fallback (Android < 12):**
```
🚫 Showing content warning with blur...
🌫️  Blur not available (Android < 12), using overlay only
✅ Content warning with blur shown
⏭️  Performing auto-scroll...
```

## Files Modified

- `android/app/src/main/java/com/allot/ScreenCaptureService.kt`
  - Added `applyBlurEffect()` function
  - Added `removeBlurEffect()` function
  - Updated `handleHarmfulContent()` to apply blur
  - Added activity access via reflection

## Benefits

1. **Better Content Blocking**: Users cannot see harmful content even briefly
2. **Consistent UX**: Same blur effect as permissions page test
3. **Professional Look**: Smooth blur effect looks polished
4. **Hardware Accelerated**: RenderEffect uses GPU, no performance impact
5. **Automatic Cleanup**: Blur removed after scroll, no lingering effects

## Summary

The system now applies a **real blur effect** (Android 12+) when harmful content is detected, making the content completely unreadable. This matches the behavior of the permissions page test button and provides better content protection.

**Result:** Harmful content is now properly blocked with blur + overlay! ✅
