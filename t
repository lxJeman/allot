## 🧍‍♂️ PHASE 4 — User Interaction Layer (Auto Scroll, Blur)

### 🎯 Objective:

When AI flags content → visually hide or skip it.

---

### 🧮 A. Auto-Scroll

* Kotlin `AccessibilityService`

* Simulate swipe gesture or `GLOBAL_ACTION_SCROLL_FORWARD`

* JS call:

```js

await NativeModules.InteractionModule.scrollDown();

```

* Delay between scrolls (avoid spam)

* Integrate with AI detection pipeline (if “blocked” → scroll)

✅ Deliverable: smooth auto-scroll triggered by AI.

---

### 🪟 B. Overlay System

* Kotlin `OverlayService` using `WindowManager`

* Show a floating icon or small status bar:

* 🟢 Active

* 🟡 Paused

* 🔴 Permission required

* Clicking opens main app or permission screen

✅ Deliverable: always-visible minimal UI, user feels in control.

---

### 🧼 C. Blur (Optional visual censorship)

Two options:

1. Overlay a semi-transparent black layer over the content region.

2. If not possible, scroll away immediately instead.

✅ Deliverable: at least one reliable method to “hide” flagged content.

---



ok its time to put backedn to the real test, well phase 4 i will be easy since we have all the pices existing alredy, key notice the mobile app should have all the componets alredy made look up at permision.tsx at the "test features" so just use all off that components and integrate it at the screen-captture.tsx since its alredy comunicates with backend, the only thing under question is that google vision api aka text extraction is found in react native not in backend so for better and more orginized code move the text regognition to backend and integrate with the whole screen-capture system so in the end we will have and mini-working version of watching the user screen capture and sending screenshots tobackend ,extracting text, and doing the ai anaalyzisis and if makred a s harmful scrolling.



lets take it at one step futher where we will check for what app user is using and only if we detected social media lets say tiktok only for now it will do the whole system to prevent unwanted requests.

### 1) Use `TYPE_WINDOW_STATE_CHANGED` events

These events fire **every time a new window becomes active**, i.e., when a different app comes to the foreground.

```kotlin
override fun onAccessibilityEvent(event: AccessibilityEvent?) {
    if (event == null) return
    if (event.eventType == AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED) {
        val foregroundPackage = event.packageName?.toString()
        Log.d("ForegroundApp", "Current app: $foregroundPackage")
        // Do something: trigger your background task, blur effect, etc.
    }
}
```

* `event.packageName` gives the **current foreground app’s package name**.
* Very low latency — almost instantaneous detection.

---

### 2) Optional: Filter for your use case

If you want only specific apps, you can filter:

```kotlin
val targetApps = setOf("com.instagram.android", "com.whatsapp")
if (foregroundPackage in targetApps) {
    // trigger blur, log, etc.
}
```

---

### 3) Make it efficient

* Don’t do heavy work in `onAccessibilityEvent` — dispatch tasks to a background thread or service.
* Minimize `Log` calls in production; they can slow down the event stream.

---

### 4) No extra permission needed

Since Accessibility is already granted, you **don’t need** `PACKAGE_USAGE_STATS` or user settings for usage access.

---

✅ **TL;DR:** With AccessibilityService permission already granted, you have **real-time, reliable foreground app detection**, and it’s the **fastest way** to know which app the user is using at any moment.

