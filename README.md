alex@alex-ASUS-TUF-Gaming-F17-FX707VJ4-FX707VJ:~/Projects/allot/rust-backend$ cargo run

    Finished `dev` profile [unoptimized + debuginfo] target(s) in 0.07s

     Running `target/debug/allot-backend`

2026-01-31T18:21:41.461581Z  INFO 🚀 Starting Allot AI Detection Backend (Merged System - Ver 2.0)

2026-01-31T18:21:41.461626Z  INFO 🧠 Model: openai/gpt-oss-20b

2026-01-31T18:21:41.461631Z  INFO 🤖 OCR: Local ML Kit (on-device) + Google Vision API (legacy fallback)

2026-01-31T18:21:41.461890Z  INFO 🌐 Server listening on http://0.0.0.0:3000

2026-01-31T18:21:41.461896Z  INFO 📱 Device should connect to http://192.168.100.55:3000

2026-01-31T18:21:41.461902Z  INFO 📡 Ready to receive screen captures

2026-01-31T18:22:35.875958Z  INFO 📸 [62a9e671-cb55-4116-8920-1824b88cc049] Received request: 1080x2400 (source: local_ml_kit)

2026-01-31T18:22:35.876094Z  INFO 🤖 [62a9e671-cb55-4116-8920-1824b88cc049] Using pre-extracted text from Local ML Kit: 53 chars

2026-01-31T18:22:35.876220Z  INFO 🔄 [62a9e671-cb55-4116-8920-1824b88cc049] Text filtered & normalized: 53 -> 53 -> 49 chars

2026-01-31T18:22:35.876326Z  INFO 🔑 [62a9e671-cb55-4116-8920-1824b88cc049] Text hash: 8fe27d1e50c1617d

2026-01-31T18:22:35.876367Z  INFO 🔍 [62a9e671-cb55-4116-8920-1824b88cc049] Cache MISS - analyzing with AI

2026-01-31T18:22:36.680331Z  INFO 🧠 [62a9e671-cb55-4116-8920-1824b88cc049] Classification complete: safe_content (99.00% confidence) (803ms) | Tokens: 290 in, 88 out, 378 total

2026-01-31T18:22:36.680388Z  INFO 💾 [62a9e671-cb55-4116-8920-1824b88cc049] Result cached for future requests

2026-01-31T18:22:36.680396Z  INFO ✅ [62a9e671-cb55-4116-8920-1824b88cc049] ═══════════════════════════════════════

2026-01-31T18:22:36.680404Z  INFO ✅ [62a9e671-cb55-4116-8920-1824b88cc049] ANALYSIS COMPLETE

2026-01-31T18:22:36.680411Z  INFO ✅ [62a9e671-cb55-4116-8920-1824b88cc049] ═══════════════════════════════════════

2026-01-31T18:22:36.680419Z  INFO 📝 [62a9e671-cb55-4116-8920-1824b88cc049] Extracted Text (53 chars):

2026-01-31T18:22:36.680432Z  INFO    "8:22

INFOBUS

10

Paóota

TikTok

+

E

EasyWay

263_peak P"

2026-01-31T18:22:36.680446Z  INFO 🏷️  [62a9e671-cb55-4116-8920-1824b88cc049] Category: safe_content

2026-01-31T18:22:36.680454Z  INFO 📊 [62a9e671-cb55-4116-8920-1824b88cc049] Confidence: 99.0%

2026-01-31T18:22:36.680461Z  INFO ⚠️  [62a9e671-cb55-4116-8920-1824b88cc049] Harmful: NO ✅

2026-01-31T18:22:36.680467Z  INFO 🎯 [62a9e671-cb55-4116-8920-1824b88cc049] Action: CONTINUE

2026-01-31T18:22:36.680474Z  INFO 💡 [62a9e671-cb55-4116-8920-1824b88cc049] Recommendation: No war-related content detected.

2026-01-31T18:22:36.680482Z  INFO ⏱️  [62a9e671-cb55-4116-8920-1824b88cc049] Timing: Total=804ms (OCR=0ms, LLM=803ms)

2026-01-31T18:22:36.680491Z  INFO ✅ [62a9e671-cb55-4116-8920-1824b88cc049] ═══════════════════════════════════════

2026-01-31T18:23:23.462653Z  INFO 📸 [175205d2-e8ca-44b2-bbca-5259ee6867c7] Received request: 1080x2400 (source: local_ml_kit)

2026-01-31T18:23:23.462735Z  INFO 🤖 [175205d2-e8ca-44b2-bbca-5259ee6867c7] Using pre-extracted text from Local ML Kit: 761 chars

2026-01-31T18:23:23.463046Z  INFO 🔄 [175205d2-e8ca-44b2-bbca-5259ee6867c7] Text filtered & normalized: 761 -> 761 -> 723 chars

2026-01-31T18:23:23.463150Z  INFO 🔑 [175205d2-e8ca-44b2-bbca-5259ee6867c7] Text hash: 1553b721d5b6972f

2026-01-31T18:23:23.463177Z  INFO 🔍 [175205d2-e8ca-44b2-bbca-5259ee6867c7] Cache MISS - analyzing with AI

2026-01-31T18:23:24.028171Z  INFO 🧠 [175205d2-e8ca-44b2-bbca-5259ee6867c7] Classification complete: war_content (98.00% confidence) (564ms) | Tokens: 432 in, 386 out, 818 total

2026-01-31T18:23:24.028260Z  INFO 💾 [175205d2-e8ca-44b2-bbca-5259ee6867c7] Result cached for future requests

2026-01-31T18:23:24.028278Z  INFO ✅ [175205d2-e8ca-44b2-bbca-5259ee6867c7] ═══════════════════════════════════════

2026-01-31T18:23:24.028296Z  INFO ✅ [175205d2-e8ca-44b2-bbca-5259ee6867c7] ANALYSIS COMPLETE

2026-01-31T18:23:24.028310Z  INFO ✅ [175205d2-e8ca-44b2-bbca-5259ee6867c7] ═══════════════════════════════════════

2026-01-31T18:23:24.028328Z  INFO 📝 [175205d2-e8ca-44b2-bbca-5259ee6867c7] Extracted Text (761 chars):

2026-01-31T18:23:24.028363Z  INFO    "8:23

O Floating windows

Screen Capture

allot

UIUID

VOnly captures when social media apps are

active (saves resources)

O Smart Capture: ON

Package:

Status:

E App Detection Status

V Current: com.allot

S..."

2026-01-31T18:23:24.028396Z  INFO 🏷️  [175205d2-e8ca-44b2-bbca-5259ee6867c7] Category: war_content

2026-01-31T18:23:24.028408Z  INFO 📊 [175205d2-e8ca-44b2-bbca-5259ee6867c7] Confidence: 98.0%

2026-01-31T18:23:24.028422Z  INFO ⚠️  [175205d2-e8ca-44b2-bbca-5259ee6867c7] Harmful: YES ⚠️

2026-01-31T18:23:24.028435Z  INFO 🎯 [175205d2-e8ca-44b2-bbca-5259ee6867c7] Action: FLAG

2026-01-31T18:23:24.028448Z  INFO 🚨 [175205d2-e8ca-44b2-bbca-5259ee6867c7] Risk Factors:

2026-01-31T18:23:24.028459Z  INFO    • conflict

2026-01-31T18:23:24.028466Z  INFO    • military threat

2026-01-31T18:23:24.028475Z  INFO 💡 [175205d2-e8ca-44b2-bbca-5259ee6867c7] Recommendation: review

2026-01-31T18:23:24.028485Z  INFO ⏱️  [175205d2-e8ca-44b2-bbca-5259ee6867c7] Timing: Total=565ms (OCR=0ms, LLM=565ms)

2026-01-31T18:23:24.028498Z  INFO ✅ [175205d2-e8ca-44b2-bbca-5259ee6867c7] ═══════════════════════════════════════

7] Cache MISS - analyzing with AI

2026-01-31T18:23:24.028171Z  INFO 🧠 [175205d2-e8ca-44b2-bbca-5259ee6867c7] Classification complete: war_content (98.00% confidence) (564ms) | Tokens: 432 in, 386 out, 818 total

2026-01-31T18:23:24.028260Z  INFO 💾 [175205d2-e8ca-44b2-bbca-5259ee6867c7] Result cached for future requests

2026-01-31T18:23:24.028278Z  INFO ✅ [175205d2-e8ca-44b2-bbca-5259ee6867c7] ═══════════════════════════════════════

2026-01-31T18:23:24.028296Z  INFO ✅ [175205d2-e8ca-44b2-bbca-5259ee6867c7] ANALYSIS COMPLETE

2026-01-31T18:23:24.028310Z  INFO ✅ [175205d2-e8ca-44b2-bbca-5259ee6867c7] ═══════════════════════════════════════

2026-01-31T18:23:24.028328Z  INFO 📝 [175205d2-e8ca-44b2-bbca-5259ee6867c7] Extracted Text (761 chars):

2026-01-31T18:23:24.028363Z  INFO    "8:23

O Floating windows

Android Bundled 914ms node_modules/expo-router/entry.js (1439 modules)

 LOG  🔥 SCREEN CAPTURE COMPONENT LOADED - NEW VERSION WITH DEBUG

 LOG  🔄 Synchronizing captureLoop state: false -> captureLoopRef: false

 LOG  🎧 Setting up ALL event listeners - this should only happen ONCE per mount

 LOG  🔍 Initializing app detection...

 LOG  🔧 Accessibility service enabled: true

 LOG  🔥 SCREEN CAPTURE COMPONENT LOADED - NEW VERSION WITH DEBUG

 LOG  📱 Current app on startup: {"appName": "com.allot", "isMonitored": false, "packageName": "com.allot", "serviceAvailable": true, "timestamp": 1769883738441}

 LOG  ✅ App detection initialized successfully

 LOG  🔥 SCREEN CAPTURE COMPONENT LOADED - NEW VERSION WITH DEBUG

 LOG  🔐 Requesting screen capture permission...

 LOG  🔥 SCREEN CAPTURE COMPONENT LOADED - NEW VERSION WITH DEBUG

 LOG  📱 [2026-01-31T18:22:21.041Z] App changed: {"appName": "com.android.systemui", "isMonitored": false, "packageName": "com.android.systemui", "timestamp": 1769883741040}

 LOG  📱 [2026-01-31T18:22:21.041Z] Left monitored app, now in: com.android.systemui

 LOG     → Screen capture should be PAUSED

 LOG  🔥 SCREEN CAPTURE COMPONENT LOADED - NEW VERSION WITH DEBUG

 LOG  🔐 Permission result received: {"granted": true, "resultCode": -1}

 LOG  🔥 SCREEN CAPTURE COMPONENT LOADED - NEW VERSION WITH DEBUG

 LOG  📱 [2026-01-31T18:22:21.856Z] App changed: {"appName": "com.allot", "isMonitored": false, "packageName": "com.allot", "timestamp": 1769883741855}

 LOG  📱 [2026-01-31T18:22:21.856Z] Left monitored app, now in: com.allot

 LOG     → Screen capture should be PAUSED

 LOG  🔥 SCREEN CAPTURE COMPONENT LOADED - NEW VERSION WITH DEBUG

 LOG  🎬 Starting sequential screen capture...

 LOG  🔥 SCREEN CAPTURE COMPONENT LOADED - NEW VERSION WITH DEBUG

 LOG  🔄 Starting sequential loop...

 LOG  🎯 Triggering IMMEDIATE initial capture...

 LOG  🎯 [2026-01-31T18:22:24.275Z] Calling captureNextFrame...

 LOG  🔥 SCREEN CAPTURE COMPONENT LOADED - NEW VERSION WITH DEBUG

 LOG  🔄 Synchronizing captureLoop state: true -> captureLoopRef: true

 LOG  📸 [2026-01-31T18:22:24.383Z] Screen captured: 1080x2400

 LOG  🔍 [2026-01-31T18:22:24.383Z] captureLoop state: false

 LOG  🔍 [2026-01-31T18:22:24.383Z] captureLoopRef.current: true

 LOG  ✅ [2026-01-31T18:22:24.275Z] captureNextFrame completed

 LOG  🔥 SCREEN CAPTURE COMPONENT LOADED - NEW VERSION WITH DEBUG

 LOG  📱 [2026-01-31T18:22:24.383Z] App at capture time: com.allot (monitored: false)

 LOG  ⏭️ [2026-01-31T18:22:24.510Z] Skipping capture - not in monitored app (com.allot)

 LOG  🚫 [2026-01-31T18:22:24.510Z] CRITICAL: This capture should NOT reach processCapture

 LOG  🔄 [2026-01-31T18:22:24.510Z] → IMMEDIATELY continuing capture loop after skip

 LOG  🔄 [2026-01-31T18:22:24.510Z] → Processing state: false

 LOG  🎯 [2026-01-31T18:22:24.511Z] Calling captureNextFrame...

 LOG  🔥 SCREEN CAPTURE COMPONENT LOADED - NEW VERSION WITH DEBUG

 LOG  ✅ [2026-01-31T18:22:24.511Z] captureNextFrame completed

 LOG  📱 [2026-01-31T18:22:33.193Z] App changed: {"appName": "com.miui.home", "isMonitored": false, "packageName": "com.miui.home", "timestamp": 1769883753192}

 LOG  📱 [2026-01-31T18:22:33.193Z] Left monitored app, now in: com.miui.home

 LOG     → Screen capture should be PAUSED

 LOG  🔥 SCREEN CAPTURE COMPONENT LOADED - NEW VERSION WITH DEBUG

 LOG  📱 [2026-01-31T18:22:34.361Z] App changed: {"appName": "TikTok", "isMonitored": true, "packageName": "com.zhiliaoapp.musically", "timestamp": 1769883754360}

 LOG  🎯 [2026-01-31T18:22:34.361Z] ENTERED MONITORED APP: TikTok

 LOG     → Screen capture should be ACTIVE

 LOG  🔄 [2026-01-31T18:22:34.361Z] → IMMEDIATELY restarting capture for monitored app

 LOG  🎯 [2026-01-31T18:22:34.368Z] Calling captureNextFrame...

 LOG  🔥 SCREEN CAPTURE COMPONENT LOADED - NEW VERSION WITH DEBUG

 LOG  📸 [2026-01-31T18:22:34.556Z] Screen captured: 1080x2400

 LOG  🔍 [2026-01-31T18:22:34.556Z] captureLoop state: false

 LOG  🔍 [2026-01-31T18:22:34.556Z] captureLoopRef.current: true

 LOG  📜 [2026-01-31T18:22:34.558Z] SCROLL DETECTED: {"currentApp": "TikTok", "event": "scroll_detected", "isMonitoredApp": true, "scrollCount": 1, "timestamp": 1769883754557}

 LOG  📜 [2026-01-31T18:22:34.558Z] App: TikTok, Count: 1

 LOG  📜 [2026-01-31T18:22:34.558Z] → RESETTING CAPTURE PIPELINE

 LOG  🔄 [2026-01-31T18:22:34.561Z] RESETTING CAPTURE STATE

 LOG  🔄 [2026-01-31T18:22:34.561Z] → Clearing processing flags

 LOG  🔄 [2026-01-31T18:22:34.561Z] → Stopping any pending analysis

 LOG  🔄 [2026-01-31T18:22:34.561Z] → Cancelling backend requests

 LOG  🚫 [AI Detection] Cancelling 0 active requests due to scroll

 LOG  ✅ [AI Detection] All requests marked as cancelled (0 total)

 LOG  📊 [AI Detection] Cancelled requests will be ignored when they complete

 LOG  🚫 [2026-01-31T18:22:34.561Z] → Backend requests cancelled successfully

 LOG  🔄 [2026-01-31T18:22:34.561Z] → Checking if we should restart capture loop

 LOG  ✅ [2026-01-31T18:22:34.561Z] CAPTURE STATE RESET COMPLETE

 LOG  ✅ [2026-01-31T18:22:34.368Z] captureNextFrame completed

 LOG  📱 [2026-01-31T18:22:34.556Z] App at capture time: TikTok (monitored: true)

 LOG  🔄 [2026-01-31T18:22:34.556Z] Starting automatic processing for monitored app: TikTok

 LOG  ✅ [2026-01-31T18:22:34.556Z] CALLING processCapture - this should be the ONLY path to backend

 LOG  🔍 [2026-01-31T18:22:34.556Z] Processing state BEFORE: false

 LOG  🚪 [2026-01-31T18:22:34.592Z] processCapture ENTRY (ID: proc_1769883754592_gqqqx) - smartCaptureEnabled: true

 LOG  📍 [2026-01-31T18:22:34.592Z] processCapture called from: at next (native)

 LOG  ✅ [2026-01-31T18:22:34.592Z] Using provided app info: TikTok (monitored: true) (ID: proc_1769883754592_gqqqx)

 LOG  🤖 [2026-01-31T18:22:34.593Z] Starting Local ML Kit text extraction... (ID: proc_1769883754592_gqqqx)

 LOG  🤖 [2026-01-31T18:22:34.593Z] THIS SHOULD ONLY HAPPEN FOR MONITORED APPS

 LOG  🤖 [2026-01-31T18:22:34.593Z] If you see this for non-monitored apps, there's a bug!

 LOG  🔄 [2026-01-31T18:22:34.561Z] → Restarting capture loop - still in monitored app: TikTok

 LOG  🚫 [2026-01-31T18:22:34.561Z] → Not restarting - currently processing

 LOG  🔥 SCREEN CAPTURE COMPONENT LOADED - NEW VERSION WITH DEBUG

 LOG  📱 [2026-01-31T18:22:35.142Z] App changed: {"appName": "com.miui.securitycenter", "isMonitored": false, "packageName": "com.miui.securitycenter", "timestamp": 1769883755141}

 LOG  📱 [2026-01-31T18:22:35.142Z] Left monitored app, now in: com.miui.securitycenter

 LOG     → Screen capture should be PAUSED

 LOG  🔥 SCREEN CAPTURE COMPONENT LOADED - NEW VERSION WITH DEBUG

 LOG  📝 [2026-01-31T18:22:34.593Z] ML Kit extraction complete (676ms) (ID: proc_1769883754592_gqqqx): "8:22

INFOBUS

10

Paóota

TikTok

+

E

EasyWay

263_peak P"

 LOG  🧠 [2026-01-31T18:22:34.593Z] Sending extracted text to backend for classification... (ID: proc_1769883754592_gqqqx)

 LOG  🧠 [2026-01-31T18:22:34.593Z] THIS BACKEND CALL SHOULD ONLY HAPPEN FOR MONITORED APPS

 LOG  🧠 [2026-01-31T18:22:34.593Z] If you see this for non-monitored apps, there's a critical bug!

 LOG  🌐 [2026-01-31T18:22:34.593Z] About to call aiDetectionService.detectHarmfulContent

 LOG  📝 [2026-01-31T18:22:34.593Z] Text length: 52 chars

 LOG  🎯 [AI Detection] Starting pipeline with pre-extracted text... (ID: req_1769883755276_kvctfjnlb)

 LOG  📝 [AI Detection] Text length: 52 chars

 LOG  🔄 [AI Detection] Text normalized: 52 -> 48 chars

 LOG  🔥 SCREEN CAPTURE COMPONENT LOADED - NEW VERSION WITH DEBUG

 LOG  ❌ [AI Detection] Cache miss (1/1)

 LOG  🌐 [AI Detection] Making backend request (ID: req_1769883755276_kvctfjnlb)

 LOG  📱 [2026-01-31T18:22:44.836Z] App changed: {"appName": "com.miui.home", "isMonitored": false, "packageName": "com.miui.home", "timestamp": 1769883764835}

 LOG  📱 [2026-01-31T18:22:44.836Z] Left monitored app, now in: com.miui.home

 LOG     → Screen capture should be PAUSED

 LOG  🔥 SCREEN CAPTURE COMPONENT LOADED - NEW VERSION WITH DEBUG

 LOG  🧹 Cleared recent capture IDs for deduplication

 LOG  ✅ [AI Detection] Backend response received (ID: req_1769883755276_kvctfjnlb)

 LOG  🧠 [AI Detection] Classification: safe_content (99%) - continue

 LOG  ✅ [AI Detection] Pipeline complete: 13877ms (Classification: 13770ms)

 LOG  🎉 [2026-01-31T18:22:34.593Z] Backend request completed successfully! (ID: proc_1769883754592_gqqqx)

 LOG  🎉 [2026-01-31T18:22:34.593Z] Result received: safe_content (0.99)

 LOG  📊 [2026-01-31T18:22:34.593Z] Complete analysis (14563ms) (ID: proc_1769883754592_gqqqx):

 LOG     🏷️ Category: safe_content

 LOG     📊 Confidence: 99.0%

 LOG     🚨 Harmful: NO

 LOG     🎯 Action: continue

 LOG     ⏱️ ML Kit: 676ms | Backend: 13882ms | Total: 14563ms

 LOG     🚀 Advantage: 1.2x faster than Google Vision API

 LOG  ✅ [2026-01-31T18:22:34.593Z] Content safe - continuing (ID: proc_1769883754592_gqqqx)

 LOG  🔄 [2026-01-31T18:22:49.166Z] Processing complete - immediately triggering next capture (ID: proc_1769883754592_gqqqx)

 LOG  🔄 [2026-01-31T18:22:49.166Z] → captureLoopRef.current: true

 LOG  🔄 [2026-01-31T18:22:49.166Z] → captureLoop state: false

 LOG  🔄 [2026-01-31T18:22:49.166Z] → This should continue the monitoring loop

 LOG  🎯 [2026-01-31T18:22:49.166Z] → Triggering next capture NOW (ID: proc_1769883754592_gqqqx)

 LOG  🎯 [2026-01-31T18:22:49.168Z] Calling captureNextFrame...

 LOG  🔥 SCREEN CAPTURE COMPONENT LOADED - NEW VERSION WITH DEBUG

 LOG  📸 [2026-01-31T18:22:49.260Z] Screen captured: 1080x2400

 LOG  🔍 [2026-01-31T18:22:49.260Z] captureLoop state: false

 LOG  🔍 [2026-01-31T18:22:49.260Z] captureLoopRef.current: true

 LOG  ✅ [2026-01-31T18:22:49.168Z] captureNextFrame completed

 LOG  📱 [2026-01-31T18:22:49.260Z] App at capture time: com.miui.home (monitored: false)

 LOG  ⏭️ [2026-01-31T18:22:49.263Z] Skipping capture - not in monitored app (com.miui.home)

 LOG  🚫 [2026-01-31T18:22:49.263Z] CRITICAL: This capture should NOT reach processCapture

 LOG  🔄 [2026-01-31T18:22:49.263Z] → IMMEDIATELY continuing capture loop after skip

 LOG  🔄 [2026-01-31T18:22:49.263Z] → Processing state: false

 LOG  🎯 [2026-01-31T18:22:49.265Z] Calling captureNextFrame...

 LOG  🔥 SCREEN CAPTURE COMPONENT LOADED - NEW VERSION WITH DEBUG

 LOG  ✅ [2026-01-31T18:22:49.265Z] captureNextFrame completed

 LOG  📱 [2026-01-31T18:22:49.420Z] App changed: {"appName": "com.allot", "isMonitored": false, "packageName": "com.allot", "timestamp": 1769883769420}

 LOG  📱 [2026-01-31T18:22:49.420Z] Left monitored app, now in: com.allot

 LOG     → Screen capture should be PAUSED

 LOG  🔥 SCREEN CAPTURE COMPONENT LOADED - NEW VERSION WITH DEBUG

 LOG  📱 [2026-01-31T18:22:49.939Z] App changed: {"appName": "com.miui.home", "isMonitored": false, "packageName": "com.miui.home", "timestamp": 1769883769938}

 LOG  📱 [2026-01-31T18:22:49.939Z] Left monitored app, now in: com.miui.home

 LOG     → Screen capture should be PAUSED

 LOG  🔥 SCREEN CAPTURE COMPONENT LOADED - NEW VERSION WITH DEBUG

 LOG  🛑 Stopping sequential screen capture...

 LOG  🔥 SCREEN CAPTURE COMPONENT LOADED - NEW VERSION WITH DEBUG

 LOG  🔄 Synchronizing captureLoop state: false -> captureLoopRef: false

 LOG  🔥 SCREEN CAPTURE COMPONENT LOADED - NEW VERSION WITH DEBUG

 LOG  📱 [2026-01-31T18:22:52.796Z] App changed: {"appName": "com.allot", "isMonitored": false, "packageName": "com.allot", "timestamp": 1769883772795}

 LOG  📱 [2026-01-31T18:22:52.796Z] Left monitored app, now in: com.allot

 LOG     → Screen capture should be PAUSED

 LOG  🔥 SCREEN CAPTURE COMPONENT LOADED - NEW VERSION WITH DEBUG

 LOG  📱 [2026-01-31T18:22:59.397Z] App changed: {"appName": "com.android.systemui", "isMonitored": false, "packageName": "com.android.systemui", "timestamp": 1769883779395}

 LOG  📱 [2026-01-31T18:22:59.397Z] Left monitored app, now in: com.android.systemui

 LOG     → Screen capture should be PAUSED

 LOG  🔥 SCREEN CAPTURE COMPONENT LOADED - NEW VERSION WITH DEBUG

 LOG  📱 [2026-01-31T18:23:17.466Z] App changed: {"appName": "com.allot", "isMonitored": false, "packageName": "com.allot", "timestamp": 1769883797466}

 LOG  📱 [2026-01-31T18:23:17.466Z] Left monitored app, now in: com.allot

 LOG     → Screen capture should be PAUSED

 LOG  🔥 SCREEN CAPTURE COMPONENT LOADED - NEW VERSION WITH DEBUG

 LOG  🧹 Cleared recent capture IDs for deduplication

 LOG  🎬 Starting sequential screen capture...

 LOG  🔥 SCREEN CAPTURE COMPONENT LOADED - NEW VERSION WITH DEBUG

 LOG  🔄 Starting sequential loop...

 LOG  🎯 Triggering IMMEDIATE initial capture...

 LOG  🎯 [2026-01-31T18:23:19.979Z] Calling captureNextFrame...

 LOG  🔥 SCREEN CAPTURE COMPONENT LOADED - NEW VERSION WITH DEBUG

 LOG  📸 [2026-01-31T18:23:20.109Z] Screen captured: 1080x2400

 LOG  🔍 [2026-01-31T18:23:20.109Z] captureLoop state: false

 LOG  🔍 [2026-01-31T18:23:20.109Z] captureLoopRef.current: true

 LOG  ✅ [2026-01-31T18:23:19.979Z] captureNextFrame completed

 LOG  🔄 Synchronizing captureLoop state: true -> captureLoopRef: true

 LOG  📱 [2026-01-31T18:23:20.109Z] App at capture time: com.allot (monitored: false)

 LOG  ⏭️ [2026-01-31T18:23:20.119Z] Skipping capture - not in monitored app (com.allot)

 LOG  🚫 [2026-01-31T18:23:20.119Z] CRITICAL: This capture should NOT reach processCapture

 LOG  🔄 [2026-01-31T18:23:20.119Z] → IMMEDIATELY continuing capture loop after skip

 LOG  🔄 [2026-01-31T18:23:20.119Z] → Processing state: false

 LOG  🎯 [2026-01-31T18:23:20.121Z] Calling captureNextFrame...

 LOG  🔥 SCREEN CAPTURE COMPONENT LOADED - NEW VERSION WITH DEBUG

 LOG  ✅ [2026-01-31T18:23:20.121Z] captureNextFrame completed

 LOG  📱 [2026-01-31T18:23:21.551Z] App changed: {"appName": "com.miui.home", "isMonitored": false, "packageName": "com.miui.home", "timestamp": 1769883801549}

 LOG  📱 [2026-01-31T18:23:21.551Z] Left monitored app, now in: com.miui.home

 LOG     → Screen capture should be PAUSED

 LOG  🔥 SCREEN CAPTURE COMPONENT LOADED - NEW VERSION WITH DEBUG

 LOG  📱 [2026-01-31T18:23:22.240Z] App changed: {"appName": "TikTok", "isMonitored": true, "packageName": "com.zhiliaoapp.musically", "timestamp": 1769883802239}

 LOG  🎯 [2026-01-31T18:23:22.240Z] ENTERED MONITORED APP: TikTok

 LOG     → Screen capture should be ACTIVE

 LOG  🔄 [2026-01-31T18:23:22.240Z] → IMMEDIATELY restarting capture for monitored app

 LOG  🎯 [2026-01-31T18:23:22.242Z] Calling captureNextFrame...

 LOG  🔥 SCREEN CAPTURE COMPONENT LOADED - NEW VERSION WITH DEBUG

 LOG  📜 [2026-01-31T18:23:22.488Z] SCROLL DETECTED: {"currentApp": "TikTok", "event": "scroll_detected", "isMonitoredApp": true, "scrollCount": 2, "timestamp": 1769883802356}

 LOG  📜 [2026-01-31T18:23:22.488Z] App: TikTok, Count: 2

 LOG  📜 [2026-01-31T18:23:22.488Z] → RESETTING CAPTURE PIPELINE

 LOG  🔄 [2026-01-31T18:23:22.491Z] RESETTING CAPTURE STATE

 LOG  🔄 [2026-01-31T18:23:22.491Z] → Clearing processing flags

 LOG  🔄 [2026-01-31T18:23:22.491Z] → Stopping any pending analysis

 LOG  🔄 [2026-01-31T18:23:22.491Z] → Cancelling backend requests

 LOG  🚫 [AI Detection] Cancelling 0 active requests due to scroll

 LOG  ✅ [AI Detection] All requests marked as cancelled (0 total)

 LOG  📊 [AI Detection] Cancelled requests will be ignored when they complete

 LOG  🚫 [2026-01-31T18:23:22.491Z] → Backend requests cancelled successfully

 LOG  🔄 [2026-01-31T18:23:22.491Z] → Checking if we should restart capture loop

 LOG  ✅ [2026-01-31T18:23:22.491Z] CAPTURE STATE RESET COMPLETE

 LOG  📸 [2026-01-31T18:23:22.501Z] Screen captured: 1080x2400

 LOG  🔍 [2026-01-31T18:23:22.501Z] captureLoop state: false

 LOG  🔍 [2026-01-31T18:23:22.501Z] captureLoopRef.current: true

 LOG  ✅ [2026-01-31T18:23:22.242Z] captureNextFrame completed

 LOG  🔄 [2026-01-31T18:23:22.491Z] → Restarting capture loop - still in monitored app: TikTok

 LOG  🎯 [2026-01-31T18:23:22.491Z] → IMMEDIATELY triggering fresh capture after scroll reset

 LOG  🎯 [2026-01-31T18:23:22.508Z] Calling captureNextFrame...

 LOG  📱 [2026-01-31T18:23:22.501Z] App at capture time: TikTok (monitored: true)

 LOG  🔄 [2026-01-31T18:23:22.501Z] Starting automatic processing for monitored app: TikTok

 LOG  ✅ [2026-01-31T18:23:22.501Z] CALLING processCapture - this should be the ONLY path to backend

 LOG  🔍 [2026-01-31T18:23:22.501Z] Processing state BEFORE: false

 LOG  🚪 [2026-01-31T18:23:22.514Z] processCapture ENTRY (ID: proc_1769883802514_33zpy) - smartCaptureEnabled: true

 LOG  📍 [2026-01-31T18:23:22.514Z] processCapture called from: at next (native)

 LOG  ✅ [2026-01-31T18:23:22.514Z] Using provided app info: TikTok (monitored: true) (ID: proc_1769883802514_33zpy)

 LOG  🤖 [2026-01-31T18:23:22.517Z] Starting Local ML Kit text extraction... (ID: proc_1769883802514_33zpy)

 LOG  🤖 [2026-01-31T18:23:22.517Z] THIS SHOULD ONLY HAPPEN FOR MONITORED APPS

 LOG  🤖 [2026-01-31T18:23:22.517Z] If you see this for non-monitored apps, there's a bug!

 LOG  ✅ [2026-01-31T18:23:22.508Z] captureNextFrame completed

 LOG  🔥 SCREEN CAPTURE COMPONENT LOADED - NEW VERSION WITH DEBUG

 LOG  📱 [2026-01-31T18:23:22.876Z] App changed: {"appName": "com.miui.home", "isMonitored": false, "packageName": "com.miui.home", "timestamp": 1769883802875}

 LOG  📱 [2026-01-31T18:23:22.876Z] Left monitored app, now in: com.miui.home

 LOG     → Screen capture should be PAUSED

 LOG  🔥 SCREEN CAPTURE COMPONENT LOADED - NEW VERSION WITH DEBUG

 LOG  📱 [2026-01-31T18:23:23.076Z] App changed: {"appName": "com.miui.securitycenter", "isMonitored": false, "packageName": "com.miui.securitycenter", "timestamp": 1769883803075}

 LOG  📱 [2026-01-31T18:23:23.076Z] Left monitored app, now in: com.miui.securitycenter

 LOG     → Screen capture should be PAUSED

 LOG  🔥 SCREEN CAPTURE COMPONENT LOADED - NEW VERSION WITH DEBUG

 LOG  📝 [2026-01-31T18:23:22.517Z] ML Kit extraction complete (767ms) (ID: proc_1769883802514_33zpy): "8:23

O Floating windows

Screen Capture

allot

UIUID

VOnly captures when social media apps are

active ..."

 LOG  🧠 [2026-01-31T18:23:22.517Z] Sending extracted text to backend for classification... (ID: proc_1769883802514_33zpy)

 LOG  🧠 [2026-01-31T18:23:22.517Z] THIS BACKEND CALL SHOULD ONLY HAPPEN FOR MONITORED APPS

 LOG  🧠 [2026-01-31T18:23:22.517Z] If you see this for non-monitored apps, there's a critical bug!

 LOG  🌐 [2026-01-31T18:23:22.517Z] About to call aiDetectionService.detectHarmfulContent

 LOG  📝 [2026-01-31T18:23:22.517Z] Text length: 761 chars

 LOG  🎯 [AI Detection] Starting pipeline with pre-extracted text... (ID: req_1769883803289_cu1qixa5i)

 LOG  📝 [AI Detection] Text length: 761 chars

 LOG  🔄 [AI Detection] Text normalized: 761 -> 723 chars

 LOG  🔥 SCREEN CAPTURE COMPONENT LOADED - NEW VERSION WITH DEBUG

 LOG  ❌ [AI Detection] Cache miss (2/2)

 LOG  🌐 [AI Detection] Making backend request (ID: req_1769883803289_cu1qixa5i)

 LOG  📱 [2026-01-31T18:23:44.627Z] App changed: {"appName": "com.miui.home", "isMonitored": false, "packageName": "com.miui.home", "timestamp": 1769883824626}

 LOG  📱 [2026-01-31T18:23:44.627Z] Left monitored app, now in: com.miui.home

 LOG     → Screen capture should be PAUSED

 LOG  🔥 SCREEN CAPTURE COMPONENT LOADED - NEW VERSION WITH DEBUG

 LOG  ✅ [AI Detection] Backend response received (ID: req_1769883803289_cu1qixa5i)

 LOG  🧠 [AI Detection] Classification: war_content (98%) - flag

 LOG  ✅ [AI Detection] Pipeline complete: 22272ms (Classification: 22174ms)

 LOG  🎉 [2026-01-31T18:23:22.517Z] Backend request completed successfully! (ID: proc_1769883802514_33zpy)

 LOG  🎉 [2026-01-31T18:23:22.517Z] Result received: war_content (0.98)

 LOG  📊 [2026-01-31T18:23:22.517Z] Complete analysis (23045ms) (ID: proc_1769883802514_33zpy):

 LOG     🏷️ Category: war_content

 LOG     📊 Confidence: 98.0%

 LOG     🚨 Harmful: YES

 LOG     🎯 Action: flag

 LOG     ⏱️ ML Kit: 767ms | Backend: 22273ms | Total: 23045ms

 LOG     🚀 Advantage: 1x faster than Google Vision API

 LOG  ⚠️ [2026-01-31T18:23:22.517Z] 🚫 HARMFUL CONTENT DETECTED (ID: proc_1769883802514_33zpy)

 LOG     📝 Text: "8:23

O Floating windows

Screen Capture

allot

UIUID

VOnly captures when social media apps are

active (saves resources)

O Smart Capture: ON

Package:

Status:

E App Detection Status

V Current: com.allot

Status: O NOT MONITORED

App Detection Status

Current App:

O Trigger Capture Now

Stop Capture

App Changes:

Scrolls Detected:

Pipeline Resets:

Last Scroll:

com.allot

com.allot

Not Monitored

11

KOFAAMOMAETM

1

1

8:22:34 PM

c TikTok

Q Find related content

GLOBEWWIRE

IRAN THREATENS TO HIT

"HEART OF TEL AVIV"

Tehran warns U.S. strikes

Won't be limited

GLOBEWIRE News - 2d ago

Ali Shamkhani, a senior adviser to Iran's

Supreme Leader, warned that Iran w...more

Q Search tehran new update today

Add comment...

allot

Options

Settings

Use allot

Search

.7.687

70

504

262

@"

 LOG     🏷️ Category: war_content

 LOG     📊 Confidence: 98.0%

 LOG     🎯 Action: flag

 LOG  🚨 [2026-01-31T18:23:22.517Z] SHOWING HARMFUL CONTENT POPUP NOW (ID: proc_1769883802514_33zpy)

 LOG  ✅ [2026-01-31T18:23:22.517Z] POPUP DISPLAYED SUCCESSFULLY (ID: proc_1769883802514_33zpy)

 LOG  🔄 [2026-01-31T18:23:45.569Z] Processing complete - immediately triggering next capture (ID: proc_1769883802514_33zpy)

 LOG  🔄 [2026-01-31T18:23:45.569Z] → captureLoopRef.current: true

 LOG  🔄 [2026-01-31T18:23:45.569Z] → captureLoop state: false

 LOG  🔄 [2026-01-31T18:23:45.569Z] → This should continue the monitoring loop

 LOG  🎯 [2026-01-31T18:23:45.569Z] → Triggering next capture NOW (ID: proc_1769883802514_33zpy)

 LOG  🎯 [2026-01-31T18:23:45.571Z] Calling captureNextFrame...

 LOG  🔥 SCREEN CAPTURE COMPONENT LOADED - NEW VERSION WITH DEBUG

 LOG  📸 [2026-01-31T18:23:45.673Z] Screen captured: 1080x2400

 LOG  🔍 [2026-01-31T18:23:45.673Z] captureLoop state: false

 LOG  🔍 [2026-01-31T18:23:45.673Z] captureLoopRef.current: true

 LOG  ✅ [2026-01-31T18:23:45.571Z] captureNextFrame completed

 LOG  📱 [2026-01-31T18:23:45.673Z] App at capture time: com.miui.home (monitored: false)

 LOG  ⏭️ [2026-01-31T18:23:45.679Z] Skipping capture - not in monitored app (com.miui.home)

 LOG  🚫 [2026-01-31T18:23:45.679Z] CRITICAL: This capture should NOT reach processCapture

 LOG  🔄 [2026-01-31T18:23:45.679Z] → IMMEDIATELY continuing capture loop after skip

 LOG  🔄 [2026-01-31T18:23:45.679Z] → Processing state: false

 LOG  🎯 [2026-01-31T18:23:45.681Z] Calling captureNextFrame...

 LOG  🔥 SCREEN CAPTURE COMPONENT LOADED - NEW VERSION WITH DEBUG

 LOG  ✅ [2026-01-31T18:23:45.681Z] captureNextFrame completed

 LOG  📱 [2026-01-31T18:23:45.844Z] App changed: {"appName": "com.allot", "isMonitored": false, "packageName": "com.allot", "timestamp": 1769883825796}

 LOG  📱 [2026-01-31T18:23:45.844Z] Left monitored app, now in: com.allot

 LOG     → Screen capture should be PAUSED

 LOG  🔥 SCREEN CAPTURE COMPONENT LOADED - NEW VERSION WITH DEBUG

 LOG  📱 [2026-01-31T18:23:46.375Z] App changed: {"appName": "com.miui.home", "isMonitored": false, "packageName": "com.miui.home", "timestamp": 1769883826374}

 LOG  📱 [2026-01-31T18:23:46.375Z] Left monitored app, now in: com.miui.home

 LOG     → Screen capture should be PAUSED

 LOG  🔥 SCREEN CAPTURE COMPONENT LOADED - NEW VERSION WITH DEBUG

 LOG  🧹 Cleared recent capture IDs for deduplication

 LOG  🛑 [2026-01-31T18:23:22.517Z] User chose to stop capture (ID: proc_1769883802514_33zpy)

 LOG  🛑 Stopping sequential screen capture...

 LOG  🔥 SCREEN CAPTURE COMPONENT LOADED - NEW VERSION WITH DEBUG

 LOG  🔄 Synchronizing captureLoop state: false -> captureLoopRef: false

 LOG  🔥 SCREEN CAPTURE COMPONENT LOADED - NEW VERSION WITH DEBUG

 LOG  📱 [2026-01-31T18:23:55.122Z] App changed: {"appName": "com.allot", "isMonitored": false, "packageName": "com.allot", "timestamp": 1769883835121}

 LOG  📱 [2026-01-31T18:23:55.122Z] Left monitored app, now in: com.allot

 LOG     → Screen capture should be PAUSED

 LOG  🔥 SCREEN CAPTURE COMPONENT LOADED - NEW VERSION WITH DEBUG

 LOG  📱 [2026-01-31T18:23:57.881Z] App changed: {"appName": "com.android.systemui", "isMonitored": false, "packageName": "com.android.systemui", "timestamp": 1769883837879}

 LOG  📱 [2026-01-31T18:23:57.881Z] Left monitored app, now in: com.android.systemui

 LOG     → Screen capture should be PAUSED

 LOG  🔥 SCREEN CAPTURE COMPONENT LOADED - NEW VERSION WITH DEBUG







there is an issue it dosent repeat it dose one extraction then stops it should be a continues loop analyse why it stops