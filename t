alex@alex-IdeaPad-Slim-3-16IAH8:~/Projects/allot2$ cd rust-backend

alex@alex-IdeaPad-Slim-3-16IAH8:~/Projects/allot2/rust-backend$ cargo run

    Finished `dev` profile [unoptimized + debuginfo] target(s) in 0.13s

     Running `target/debug/allot-backend`

2025-12-16T17:54:22.566099Z  INFO 🚀 Starting Allot AI Detection Backend (Phase 3 - Ver 1.0)

2025-12-16T17:54:22.566161Z  INFO 🧠 Model: openai/gpt-oss-20b

2025-12-16T17:54:22.566172Z  INFO 👁️  OCR: Google Vision API

2025-12-16T17:54:22.570276Z  INFO 🌐 Server listening on http://0.0.0.0:3000

2025-12-16T17:54:22.570302Z  INFO 📱 Device should connect to http://192.168.100.47:3000

2025-12-16T17:54:22.570315Z  INFO 📡 Ready to receive screen captures

2025-12-16T18:12:34.865925Z  INFO 📸 [e9a4fd83-a139-4f19-9f64-d088c9c6f4b2] Received screen capture: 720x1600

2025-12-16T18:12:35.787059Z  INFO 👁️  [e9a4fd83-a139-4f19-9f64-d088c9c6f4b2] OCR complete: 324 chars extracted (921ms)

2025-12-16T18:12:35.787204Z  INFO 🔄 [e9a4fd83-a139-4f19-9f64-d088c9c6f4b2] Text filtered & normalized: 324 -> 324 -> 291 chars

2025-12-16T18:12:35.787266Z  INFO 🔑 [e9a4fd83-a139-4f19-9f64-d088c9c6f4b2] Text hash: a07e8a6a374146e6

2025-12-16T18:12:35.787285Z  INFO 🔍 [e9a4fd83-a139-4f19-9f64-d088c9c6f4b2] Cache MISS - analyzing with AI

2025-12-16T18:12:36.193317Z  INFO 🧠 [e9a4fd83-a139-4f19-9f64-d088c9c6f4b2] Classification complete: safe_content (99.00% confidence) (405ms) | Tokens: 324 in, 74 out, 398 total

2025-12-16T18:12:36.193464Z  INFO 💾 [e9a4fd83-a139-4f19-9f64-d088c9c6f4b2] Result cached for future requests

2025-12-16T18:12:36.193494Z  INFO ✅ [e9a4fd83-a139-4f19-9f64-d088c9c6f4b2] ═══════════════════════════════════════

2025-12-16T18:12:36.193519Z  INFO ✅ [e9a4fd83-a139-4f19-9f64-d088c9c6f4b2] ANALYSIS COMPLETE

2025-12-16T18:12:36.193538Z  INFO ✅ [e9a4fd83-a139-4f19-9f64-d088c9c6f4b2] ═══════════════════════════════════════

2025-12-16T18:12:36.193562Z  INFO 📝 [e9a4fd83-a139-4f19-9f64-d088c9c6f4b2] Extracted Text (324 chars):

2025-12-16T18:12:36.193614Z  INFO    "2012rge Permission Module

Available

LocalTextExtraction Module: ☑

Available

Configuration

Enable Validation & Fallback

Enable Caching

Auto-scroll on Text Detection

Capture Interval: 1000ms | Validatio..."

2025-12-16T18:12:36.193662Z  INFO 🏷️  [e9a4fd83-a139-4f19-9f64-d088c9c6f4b2] Category: safe_content

2025-12-16T18:12:36.193705Z  INFO 📊 [e9a4fd83-a139-4f19-9f64-d088c9c6f4b2] Confidence: 99.0%

2025-12-16T18:12:36.193727Z  INFO ⚠️  [e9a4fd83-a139-4f19-9f64-d088c9c6f4b2] Harmful: NO ✅

2025-12-16T18:12:36.193747Z  INFO 🎯 [e9a4fd83-a139-4f19-9f64-d088c9c6f4b2] Action: CONTINUE

2025-12-16T18:12:36.193769Z  INFO 💡 [e9a4fd83-a139-4f19-9f64-d088c9c6f4b2] Recommendation: No war-related content detected.

2025-12-16T18:12:36.193791Z  INFO ⏱️  [e9a4fd83-a139-4f19-9f64-d088c9c6f4b2] Timing: Total=1327ms (OCR=921ms, LLM=406ms)

2025-12-16T18:12:36.193814Z  INFO ✅ [e9a4fd83-a139-4f19-9f64-d088c9c6f4b2] ═══════════════════════════════════════

2025-12-16T18:12:36.575901Z  INFO 📸 [938aa776-ccdb-4e5d-bc17-68604f603b6a] Received screen capture: 720x1600

2025-12-16T18:12:37.376940Z  INFO 👁️  [938aa776-ccdb-4e5d-bc17-68604f603b6a] OCR complete: 324 chars extracted (800ms)

2025-12-16T18:12:37.377001Z  INFO 🔄 [938aa776-ccdb-4e5d-bc17-68604f603b6a] Text filtered & normalized: 324 -> 324 -> 291 chars

2025-12-16T18:12:37.377023Z  INFO 🔑 [938aa776-ccdb-4e5d-bc17-68604f603b6a] Text hash: a07e8a6a374146e6

2025-12-16T18:12:37.377035Z  INFO 💾 [938aa776-ccdb-4e5d-bc17-68604f603b6a] Cache HIT - returning cached result

2025-12-16T18:12:37.793426Z  INFO 📸 [d4aac044-49ff-4556-a10d-3e62eec2211b] Received screen capture: 720x1600

2025-12-16T18:12:38.715251Z  INFO 👁️  [d4aac044-49ff-4556-a10d-3e62eec2211b] OCR complete: 324 chars extracted (921ms)

2025-12-16T18:12:38.715310Z  INFO 🔄 [d4aac044-49ff-4556-a10d-3e62eec2211b] Text filtered & normalized: 324 -> 324 -> 291 chars

2025-12-16T18:12:38.715333Z  INFO 🔑 [d4aac044-49ff-4556-a10d-3e62eec2211b] Text hash: a07e8a6a374146e6

2025-12-16T18:12:38.715342Z  INFO 💾 [d4aac044-49ff-4556-a10d-3e62eec2211b] Cache HIT - returning cached result

2025-12-16T18:12:39.044114Z  INFO 📸 [2148d3df-5479-42bc-87e6-57826cff2be5] Received screen capture: 720x1600

2025-12-16T18:12:39.794933Z  INFO 👁️  [2148d3df-5479-42bc-87e6-57826cff2be5] OCR complete: 324 chars extracted (750ms)

2025-12-16T18:12:39.795035Z  INFO 🔄 [2148d3df-5479-42bc-87e6-57826cff2be5] Text filtered & normalized: 324 -> 324 -> 291 chars

2025-12-16T18:12:39.795073Z  INFO 🔑 [2148d3df-5479-42bc-87e6-57826cff2be5] Text hash: a07e8a6a374146e6

2025-12-16T18:12:39.795092Z  INFO 💾 [2148d3df-5479-42bc-87e6-57826cff2be5] Cache HIT - returning cached result

2025-12-16T18:12:40.178479Z  INFO 📸 [0d66f73b-4c2b-4a6f-96da-2a639955f2fa] Received screen capture: 720x1600

2025-12-16T18:12:40.973432Z  INFO 👁️  [0d66f73b-4c2b-4a6f-96da-2a639955f2fa] OCR complete: 324 chars extracted (794ms)

2025-12-16T18:12:40.973641Z  INFO 🔄 [0d66f73b-4c2b-4a6f-96da-2a639955f2fa] Text filtered & normalized: 324 -> 324 -> 291 chars

2025-12-16T18:12:40.973666Z  INFO 🔑 [0d66f73b-4c2b-4a6f-96da-2a639955f2fa] Text hash: a07e8a6a374146e6

2025-12-16T18:12:40.973680Z  INFO 💾 [0d66f73b-4c2b-4a6f-96da-2a639955f2fa] Cache HIT - returning cached result

2025-12-16T18:12:41.410831Z  INFO 📸 [62c75b6f-482c-4d8b-90f0-4aac8cc048de] Received screen capture: 720x1600

2025-12-16T18:12:42.303224Z  INFO 👁️  [62c75b6f-482c-4d8b-90f0-4aac8cc048de] OCR complete: 324 chars extracted (892ms)

2025-12-16T18:12:42.303310Z  INFO 🔄 [62c75b6f-482c-4d8b-90f0-4aac8cc048de] Text filtered & normalized: 324 -> 324 -> 291 chars

2025-12-16T18:12:42.303342Z  INFO 🔑 [62c75b6f-482c-4d8b-90f0-4aac8cc048de] Text hash: a07e8a6a374146e6

2025-12-16T18:12:42.303357Z  INFO 💾 [62c75b6f-482c-4d8b-90f0-4aac8cc048de] Cache HIT - returning cached result

2025-12-16T18:12:42.533415Z  INFO 📸 [ea199301-4cb4-44c8-b638-92c837a158b5] Received screen capture: 720x1600

2025-12-16T18:12:43.752905Z  INFO 👁️  [ea199301-4cb4-44c8-b638-92c837a158b5] OCR complete: 324 chars extracted (1219ms)

2025-12-16T18:12:43.752963Z  INFO 🔄 [ea199301-4cb4-44c8-b638-92c837a158b5] Text filtered & normalized: 324 -> 324 -> 291 chars

2025-12-16T18:12:43.752985Z  INFO 🔑 [ea199301-4cb4-44c8-b638-92c837a158b5] Text hash: a07e8a6a374146e6

2025-12-16T18:12:43.752994Z  INFO 💾 [ea199301-4cb4-44c8-b638-92c837a158b5] Cache HIT - returning cached result

2025-12-16T18:12:44.332702Z  INFO 📸 [032e98eb-63fa-43b6-9f04-5970aa5f25a9] Received screen capture: 720x1600

2025-12-16T18:12:45.555814Z  INFO 👁️  [032e98eb-63fa-43b6-9f04-5970aa5f25a9] OCR complete: 324 chars extracted (1223ms)

2025-12-16T18:12:45.555906Z  INFO 🔄 [032e98eb-63fa-43b6-9f04-5970aa5f25a9] Text filtered & normalized: 324 -> 324 -> 291 chars

2025-12-16T18:12:45.555940Z  INFO 🔑 [032e98eb-63fa-43b6-9f04-5970aa5f25a9] Text hash: a07e8a6a374146e6

2025-12-16T18:12:45.555953Z  INFO 💾 [032e98eb-63fa-43b6-9f04-5970aa5f25a9] Cache HIT - returning cached result

2025-12-16T18:12:46.081543Z  INFO 📸 [051585b4-5281-4838-a9eb-e995fc078070] Received screen capture: 720x1600

2025-12-16T18:12:47.079605Z  INFO 👁️  [051585b4-5281-4838-a9eb-e995fc078070] OCR complete: 324 chars extracted (997ms)

2025-12-16T18:12:47.079747Z  INFO 🔄 [051585b4-5281-4838-a9eb-e995fc078070] Text filtered & normalized: 324 -> 324 -> 291 chars

2025-12-16T18:12:47.079798Z  INFO 🔑 [051585b4-5281-4838-a9eb-e995fc078070] Text hash: a07e8a6a374146e6

2025-12-16T18:12:47.079824Z  INFO 💾 [051585b4-5281-4838-a9eb-e995fc078070] Cache HIT - returning cached result

2025-12-16T18:12:47.431513Z  INFO 📸 [21b2e368-4671-4466-bb64-c6e1c04c3024] Received screen capture: 720x1600

2025-12-16T18:12:48.248509Z  INFO 👁️  [21b2e368-4671-4466-bb64-c6e1c04c3024] OCR complete: 324 chars extracted (816ms)

2025-12-16T18:12:48.248568Z  INFO 🔄 [21b2e368-4671-4466-bb64-c6e1c04c3024] Text filtered & normalized: 324 -> 324 -> 291 chars

2025-12-16T18:12:48.248591Z  INFO 🔑 [21b2e368-4671-4466-bb64-c6e1c04c3024] Text hash: a07e8a6a374146e6

2025-12-16T18:12:48.248600Z  INFO 💾 [21b2e368-4671-4466-bb64-c6e1c04c3024] Cache HIT - returning cached result

2025-12-16T18:12:48.865054Z  INFO 📸 [9df4a3aa-f338-477d-982f-7eaeb64d903a] Received screen capture: 720x1600

2025-12-16T18:12:49.682652Z  INFO 👁️  [9df4a3aa-f338-477d-982f-7eaeb64d903a] OCR complete: 324 chars extracted (817ms)

2025-12-16T18:12:49.682712Z  INFO 🔄 [9df4a3aa-f338-477d-982f-7eaeb64d903a] Text filtered & normalized: 324 -> 324 -> 291 chars

2025-12-16T18:12:49.682736Z  INFO 🔑 [9df4a3aa-f338-477d-982f-7eaeb64d903a] Text hash: a07e8a6a374146e6

2025-12-16T18:12:49.682746Z  INFO 💾 [9df4a3aa-f338-477d-982f-7eaeb64d903a] Cache HIT - returning cached result

2025-12-16T18:12:49.949203Z  INFO 📸 [b4bf2b0f-9c1f-4851-98c9-4b1f89165482] Received screen capture: 720x1600

2025-12-16T18:12:50.775451Z  INFO 👁️  [b4bf2b0f-9c1f-4851-98c9-4b1f89165482] OCR complete: 324 chars extracted (826ms)

2025-12-16T18:12:50.775591Z  INFO 🔄 [b4bf2b0f-9c1f-4851-98c9-4b1f89165482] Text filtered & normalized: 324 -> 324 -> 291 chars

2025-12-16T18:12:50.775648Z  INFO 🔑 [b4bf2b0f-9c1f-4851-98c9-4b1f89165482] Text hash: a07e8a6a374146e6

2025-12-16T18:12:50.775671Z  INFO 💾 [b4bf2b0f-9c1f-4851-98c9-4b1f89165482] Cache HIT - returning cached result

2025-12-16T18:12:51.083534Z  INFO 📸 [be9b1e2b-b486-4694-b11c-f95768f51dd2] Received screen capture: 720x1600

2025-12-16T18:12:51.944318Z  INFO 👁️  [be9b1e2b-b486-4694-b11c-f95768f51dd2] OCR complete: 324 chars extracted (860ms)

2025-12-16T18:12:51.944387Z  INFO 🔄 [be9b1e2b-b486-4694-b11c-f95768f51dd2] Text filtered & normalized: 324 -> 324 -> 291 chars

2025-12-16T18:12:51.944417Z  INFO 🔑 [be9b1e2b-b486-4694-b11c-f95768f51dd2] Text hash: a07e8a6a374146e6

2025-12-16T18:12:51.944430Z  INFO 💾 [be9b1e2b-b486-4694-b11c-f95768f51dd2] Cache HIT - returning cached result

2025-12-16T18:12:52.345494Z  INFO 📸 [52bc73a0-3080-4022-98e5-6a125650feaa] Received screen capture: 720x1600

2025-12-16T18:12:53.101844Z  INFO 👁️  [52bc73a0-3080-4022-98e5-6a125650feaa] OCR complete: 324 chars extracted (756ms)

2025-12-16T18:12:53.101957Z  INFO 🔄 [52bc73a0-3080-4022-98e5-6a125650feaa] Text filtered & normalized: 324 -> 324 -> 291 chars

2025-12-16T18:12:53.102006Z  INFO 🔑 [52bc73a0-3080-4022-98e5-6a125650feaa] Text hash: a07e8a6a374146e6

2025-12-16T18:12:53.102024Z  INFO 💾 [52bc73a0-3080-4022-98e5-6a125650feaa] Cache HIT - returning cached result

2025-12-16T18:12:53.685183Z  INFO 📸 [6e08a42b-d73f-4ab1-b147-0bb9832944cd] Received screen capture: 720x1600

2025-12-16T18:12:54.558123Z  INFO 👁️  [6e08a42b-d73f-4ab1-b147-0bb9832944cd] OCR complete: 324 chars extracted (872ms)

2025-12-16T18:12:54.558258Z  INFO 🔄 [6e08a42b-d73f-4ab1-b147-0bb9832944cd] Text filtered & normalized: 324 -> 324 -> 291 chars

2025-12-16T18:12:54.558308Z  INFO 🔑 [6e08a42b-d73f-4ab1-b147-0bb9832944cd] Text hash: a07e8a6a374146e6

2025-12-16T18:12:54.558328Z  INFO 💾 [6e08a42b-d73f-4ab1-b147-0bb9832944cd] Cache HIT - returning cached result

2025-12-16T18:12:54.987579Z  INFO 📸 [36025ba6-7d14-4f1e-a3e8-624c19e0872e] Received screen capture: 720x1600

2025-12-16T18:12:55.879089Z  INFO 👁️  [36025ba6-7d14-4f1e-a3e8-624c19e0872e] OCR complete: 324 chars extracted (891ms)

2025-12-16T18:12:55.879149Z  INFO 🔄 [36025ba6-7d14-4f1e-a3e8-624c19e0872e] Text filtered & normalized: 324 -> 324 -> 291 chars

2025-12-16T18:12:55.879173Z  INFO 🔑 [36025ba6-7d14-4f1e-a3e8-624c19e0872e] Text hash: a07e8a6a374146e6

2025-12-16T18:12:55.879183Z  INFO 💾 [36025ba6-7d14-4f1e-a3e8-624c19e0872e] Cache HIT - returning cached result

2025-12-16T18:12:56.317698Z  INFO 📸 [9edc9edd-b491-40ba-86e8-bf932476dbb8] Received screen capture: 720x1600

2025-12-16T18:12:57.096724Z  INFO 👁️  [9edc9edd-b491-40ba-86e8-bf932476dbb8] OCR complete: 324 chars extracted (778ms)

2025-12-16T18:12:57.096789Z  INFO 🔄 [9edc9edd-b491-40ba-86e8-bf932476dbb8] Text filtered & normalized: 324 -> 324 -> 291 chars

2025-12-16T18:12:57.096814Z  INFO 🔑 [9edc9edd-b491-40ba-86e8-bf932476dbb8] Text hash: a07e8a6a374146e6

2025-12-16T18:12:57.096824Z  INFO 💾 [9edc9edd-b491-40ba-86e8-bf932476dbb8] Cache HIT - returning cached result

2025-12-16T18:12:57.523637Z  INFO 📸 [d4c18ae5-4208-4474-a256-b4725f9bfcf6] Received screen capture: 720x1600

2025-12-16T18:12:58.249249Z  INFO 👁️  [d4c18ae5-4208-4474-a256-b4725f9bfcf6] OCR complete: 324 chars extracted (725ms)

2025-12-16T18:12:58.249315Z  INFO 🔄 [d4c18ae5-4208-4474-a256-b4725f9bfcf6] Text filtered & normalized: 324 -> 324 -> 291 chars

2025-12-16T18:12:58.249339Z  INFO 🔑 [d4c18ae5-4208-4474-a256-b4725f9bfcf6] Text hash: a07e8a6a374146e6

2025-12-16T18:12:58.249349Z  INFO 💾 [d4c18ae5-4208-4474-a256-b4725f9bfcf6] Cache HIT - returning cached result

2025-12-16T18:12:58.926909Z  INFO 📸 [9b610dac-af12-4e11-9c05-f5f49892a57f] Received screen capture: 720x1600

2025-12-16T18:12:59.808804Z  INFO 👁️  [9b610dac-af12-4e11-9c05-f5f49892a57f] OCR complete: 324 chars extracted (881ms)

2025-12-16T18:12:59.808917Z  INFO 🔄 [9b610dac-af12-4e11-9c05-f5f49892a57f] Text filtered & normalized: 324 -> 324 -> 291 chars

2025-12-16T18:12:59.808963Z  INFO 🔑 [9b610dac-af12-4e11-9c05-f5f49892a57f] Text hash: a07e8a6a374146e6

2025-12-16T18:12:59.808983Z  INFO 💾 [9b610dac-af12-4e11-9c05-f5f49892a57f] Cache HIT - returning cached result

2025-12-16T18:13:00.120872Z  INFO 📸 [865bfd9f-691d-464f-9124-5e55fe27963b] Received screen capture: 720x1600

2025-12-16T18:13:00.901881Z  INFO 👁️  [865bfd9f-691d-464f-9124-5e55fe27963b] OCR complete: 324 chars extracted (780ms)

2025-12-16T18:13:00.901953Z  INFO 🔄 [865bfd9f-691d-464f-9124-5e55fe27963b] Text filtered & normalized: 324 -> 324 -> 291 chars

2025-12-16T18:13:00.901989Z  INFO 🔑 [865bfd9f-691d-464f-9124-5e55fe27963b] Text hash: a07e8a6a374146e6

2025-12-16T18:13:00.902001Z  INFO 💾 [865bfd9f-691d-464f-9124-5e55fe27963b] Cache HIT - returning cached result

2025-12-16T18:13:01.341494Z  INFO 📸 [32896e51-b39f-4f12-bc00-980b9cc4c1b7] Received screen capture: 720x1600

2025-12-16T18:13:02.122798Z  INFO 👁️  [32896e51-b39f-4f12-bc00-980b9cc4c1b7] OCR complete: 324 chars extracted (781ms)

2025-12-16T18:13:02.122920Z  INFO 🔄 [32896e51-b39f-4f12-bc00-980b9cc4c1b7] Text filtered & normalized: 324 -> 324 -> 291 chars

2025-12-16T18:13:02.122968Z  INFO 🔑 [32896e51-b39f-4f12-bc00-980b9cc4c1b7] Text hash: a07e8a6a374146e6

2025-12-16T18:13:02.122986Z  INFO 💾 [32896e51-b39f-4f12-bc00-980b9cc4c1b7] Cache HIT - returning cached result

2025-12-16T18:13:02.478900Z  INFO 📸 [1c2adebd-866d-4d02-983e-0cfaae2176d3] Received screen capture: 720x1600

2025-12-16T18:13:03.269983Z  INFO 👁️  [1c2adebd-866d-4d02-983e-0cfaae2176d3] OCR complete: 324 chars extracted (791ms)

2025-12-16T18:13:03.270070Z  INFO 🔄 [1c2adebd-866d-4d02-983e-0cfaae2176d3] Text filtered & normalized: 324 -> 324 -> 291 chars

2025-12-16T18:13:03.270108Z  INFO 🔑 [1c2adebd-866d-4d02-983e-0cfaae2176d3] Text hash: a07e8a6a374146e6

2025-12-16T18:13:03.270126Z  INFO 💾 [1c2adebd-866d-4d02-983e-0cfaae2176d3] Cache HIT - returning cached result

2025-12-16T18:13:03.593669Z  INFO 📸 [ffd27a06-a0ab-4fea-a809-c1cbd3532356] Received screen capture: 720x1600

2025-12-16T18:13:04.325226Z  INFO 👁️  [ffd27a06-a0ab-4fea-a809-c1cbd3532356] OCR complete: 324 chars extracted (731ms)

2025-12-16T18:13:04.325291Z  INFO 🔄 [ffd27a06-a0ab-4fea-a809-c1cbd3532356] Text filtered & normalized: 324 -> 324 -> 291 chars

2025-12-16T18:13:04.325314Z  INFO 🔑 [ffd27a06-a0ab-4fea-a809-c1cbd3532356] Text hash: a07e8a6a374146e6

2025-12-16T18:13:04.325324Z  INFO 💾 [ffd27a06-a0ab-4fea-a809-c1cbd3532356] Cache HIT - returning cached result

2025-12-16T18:13:04.597986Z  INFO 📸 [df4a893e-1d20-4cc8-914b-059b457e75a2] Received screen capture: 720x1600

2025-12-16T18:13:05.369977Z  INFO 👁️  [df4a893e-1d20-4cc8-914b-059b457e75a2] OCR complete: 324 chars extracted (771ms)

2025-12-16T18:13:05.370067Z  INFO 🔄 [df4a893e-1d20-4cc8-914b-059b457e75a2] Text filtered & normalized: 324 -> 324 -> 291 chars

2025-12-16T18:13:05.370099Z  INFO 🔑 [df4a893e-1d20-4cc8-914b-059b457e75a2] Text hash: a07e8a6a374146e6

2025-12-16T18:13:05.370111Z  INFO 💾 [df4a893e-1d20-4cc8-914b-059b457e75a2] Cache HIT - returning cached result

2025-12-16T18:13:05.769017Z  INFO 📸 [3b786807-36b5-4709-a30d-037407318e24] Received screen capture: 720x1600

2025-12-16T18:13:06.848574Z  INFO 👁️  [3b786807-36b5-4709-a30d-037407318e24] OCR complete: 324 chars extracted (1079ms)

2025-12-16T18:13:06.848647Z  INFO 🔄 [3b786807-36b5-4709-a30d-037407318e24] Text filtered & normalized: 324 -> 324 -> 291 chars

2025-12-16T18:13:06.848676Z  INFO 🔑 [3b786807-36b5-4709-a30d-037407318e24] Text hash: a07e8a6a374146e6

2025-12-16T18:13:06.848687Z  INFO 💾 [3b786807-36b5-4709-a30d-037407318e24] Cache HIT - returning cached result

2025-12-16T18:13:07.305199Z  INFO 📸 [21166520-ad69-4c98-a70f-b08798f46214] Received screen capture: 720x1600

2025-12-16T18:13:08.095404Z  INFO 👁️  [21166520-ad69-4c98-a70f-b08798f46214] OCR complete: 324 chars extracted (790ms)

2025-12-16T18:13:08.095462Z  INFO 🔄 [21166520-ad69-4c98-a70f-b08798f46214] Text filtered & normalized: 324 -> 324 -> 291 chars

2025-12-16T18:13:08.095484Z  INFO 🔑 [21166520-ad69-4c98-a70f-b08798f46214] Text hash: a07e8a6a374146e6

2025-12-16T18:13:08.095492Z  INFO 💾 [21166520-ad69-4c98-a70f-b08798f46214] Cache HIT - returning cached result

2025-12-16T18:13:08.617904Z  INFO 📸 [32ba4789-692d-45af-bf93-4196487f7e4c] Received screen capture: 720x1600

2025-12-16T18:13:09.403334Z  INFO 👁️  [32ba4789-692d-45af-bf93-4196487f7e4c] OCR complete: 324 chars extracted (785ms)

2025-12-16T18:13:09.403434Z  INFO 🔄 [32ba4789-692d-45af-bf93-4196487f7e4c] Text filtered & normalized: 324 -> 324 -> 291 chars

2025-12-16T18:13:09.403472Z  INFO 🔑 [32ba4789-692d-45af-bf93-4196487f7e4c] Text hash: a07e8a6a374146e6

2025-12-16T18:13:09.403488Z  INFO 💾 [32ba4789-692d-45af-bf93-4196487f7e4c] Cache HIT - returning cached result

2025-12-16T18:14:42.626077Z  INFO 📸 [a346304c-d12c-4faa-a9e8-d99dd85868dc] Received screen capture: 720x1600

2025-12-16T18:14:43.401554Z  INFO 👁️  [a346304c-d12c-4faa-a9e8-d99dd85868dc] OCR complete: 324 chars extracted (775ms)

2025-12-16T18:14:43.401653Z  INFO 🔄 [a346304c-d12c-4faa-a9e8-d99dd85868dc] Text filtered & normalized: 324 -> 324 -> 291 chars

2025-12-16T18:14:43.401697Z  INFO 🔑 [a346304c-d12c-4faa-a9e8-d99dd85868dc] Text hash: a07e8a6a374146e6

2025-12-16T18:14:43.401714Z  INFO 💾 [a346304c-d12c-4faa-a9e8-d99dd85868dc] Cache HIT - returning cached result

2025-12-16T18:14:43.967083Z  INFO 📸 [f7f7eeb5-2413-4d55-b6fe-0a744ea25535] Received screen capture: 720x1600

2025-12-16T18:14:44.755350Z  INFO 👁️  [f7f7eeb5-2413-4d55-b6fe-0a744ea25535] OCR complete: 277 chars extracted (788ms)

2025-12-16T18:14:44.755408Z  INFO 🔄 [f7f7eeb5-2413-4d55-b6fe-0a744ea25535] Text filtered & normalized: 277 -> 277 -> 248 chars

2025-12-16T18:14:44.755433Z  INFO 🔑 [f7f7eeb5-2413-4d55-b6fe-0a744ea25535] Text hash: 9bf89b4283b395af

2025-12-16T18:14:44.755441Z  INFO 🔍 [f7f7eeb5-2413-4d55-b6fe-0a744ea25535] Cache MISS - analyzing with AI

2025-12-16T18:14:45.321564Z  INFO 🧠 [f7f7eeb5-2413-4d55-b6fe-0a744ea25535] Classification complete: safe_content (99.00% confidence) (566ms) | Tokens: 315 in, 143 out, 458 total

2025-12-16T18:14:45.321660Z  INFO 💾 [f7f7eeb5-2413-4d55-b6fe-0a744ea25535] Result cached for future requests

2025-12-16T18:14:45.321695Z  INFO ✅ [f7f7eeb5-2413-4d55-b6fe-0a744ea25535] ═══════════════════════════════════════

2025-12-16T18:14:45.321713Z  INFO ✅ [f7f7eeb5-2413-4d55-b6fe-0a744ea25535] ANALYSIS COMPLETE

2025-12-16T18:14:45.321726Z  INFO ✅ [f7f7eeb5-2413-4d55-b6fe-0a744ea25535] ═══════════════════════════════════════

2025-12-16T18:14:45.321744Z  INFO 📝 [f7f7eeb5-2413-4d55-b6fe-0a744ea25535] Extracted Text (277 chars):

2025-12-16T18:14:45.321782Z  INFO    "20Aчailable

Configuration

Enable Validation & Fallback

Enable Caching

Auto-scroll on Text Detection

Capture Interval: 1000ms | Validation: ON |

Cache: ON

Testing...

Processing...

Clear All Data

Proces..."

2025-12-16T18:14:45.321815Z  INFO 🏷️  [f7f7eeb5-2413-4d55-b6fe-0a744ea25535] Category: safe_content

2025-12-16T18:14:45.321836Z  INFO 📊 [f7f7eeb5-2413-4d55-b6fe-0a744ea25535] Confidence: 99.0%

2025-12-16T18:14:45.321853Z  INFO ⚠️  [f7f7eeb5-2413-4d55-b6fe-0a744ea25535] Harmful: NO ✅

2025-12-16T18:14:45.321868Z  INFO 🎯 [f7f7eeb5-2413-4d55-b6fe-0a744ea25535] Action: CONTINUE

2025-12-16T18:14:45.321885Z  INFO 💡 [f7f7eeb5-2413-4d55-b6fe-0a744ea25535] Recommendation: No war-related keywords or hashtags detected; content is purely technical.

2025-12-16T18:14:45.321906Z  INFO ⏱️  [f7f7eeb5-2413-4d55-b6fe-0a744ea25535] Timing: Total=1354ms (OCR=788ms, LLM=566ms)

2025-12-16T18:14:45.321923Z  INFO ✅ [f7f7eeb5-2413-4d55-b6fe-0a744ea25535] ═══════════════════════════════════════

2025-12-16T18:14:45.800795Z  INFO 📸 [902641e5-4b0b-40e1-84b6-22b9e35d4ea4] Received screen capture: 720x1600

2025-12-16T18:14:46.684741Z  INFO 👁️  [902641e5-4b0b-40e1-84b6-22b9e35d4ea4] OCR complete: 281 chars extracted (883ms)

2025-12-16T18:14:46.684827Z  INFO 🔄 [902641e5-4b0b-40e1-84b6-22b9e35d4ea4] Text filtered & normalized: 281 -> 281 -> 252 chars

2025-12-16T18:14:46.684864Z  INFO 🔑 [902641e5-4b0b-40e1-84b6-22b9e35d4ea4] Text hash: 116414733281b22a

2025-12-16T18:14:46.684875Z  INFO 🔍 [902641e5-4b0b-40e1-84b6-22b9e35d4ea4] Cache MISS - analyzing with AI

2025-12-16T18:14:47.060151Z  INFO 🧠 [902641e5-4b0b-40e1-84b6-22b9e35d4ea4] Classification complete: safe_content (99.00% confidence) (375ms) | Tokens: 317 in, 103 out, 420 total

2025-12-16T18:14:47.060192Z  INFO 💾 [902641e5-4b0b-40e1-84b6-22b9e35d4ea4] Result cached for future requests

2025-12-16T18:14:47.060197Z  INFO ✅ [902641e5-4b0b-40e1-84b6-22b9e35d4ea4] ═══════════════════════════════════════

2025-12-16T18:14:47.060202Z  INFO ✅ [902641e5-4b0b-40e1-84b6-22b9e35d4ea4] ANALYSIS COMPLETE

2025-12-16T18:14:47.060206Z  INFO ✅ [902641e5-4b0b-40e1-84b6-22b9e35d4ea4] ═══════════════════════════════════════

2025-12-16T18:14:47.060211Z  INFO 📝 [902641e5-4b0b-40e1-84b6-22b9e35d4ea4] Extracted Text (281 chars):

2025-12-16T18:14:47.060222Z  INFO    "20Aчailable

Configuration

Enable Validation & Fallback

Enable Caching

Auto-scroll on Text Detection

Capture Interval: 1000ms | Validation: ON |

Cache: ON

Testing...

Processing...

Clear All Data

၁

Proc..."

2025-12-16T18:14:47.060231Z  INFO 🏷️  [902641e5-4b0b-40e1-84b6-22b9e35d4ea4] Category: safe_content

2025-12-16T18:14:47.060236Z  INFO 📊 [902641e5-4b0b-40e1-84b6-22b9e35d4ea4] Confidence: 99.0%

2025-12-16T18:14:47.060240Z  INFO ⚠️  [902641e5-4b0b-40e1-84b6-22b9e35d4ea4] Harmful: NO ✅

2025-12-16T18:14:47.060244Z  INFO 🎯 [902641e5-4b0b-40e1-84b6-22b9e35d4ea4] Action: CONTINUE

2025-12-16T18:14:47.060248Z  INFO 💡 [902641e5-4b0b-40e1-84b6-22b9e35d4ea4] Recommendation: No war content detected.

2025-12-16T18:14:47.060253Z  INFO ⏱️  [902641e5-4b0b-40e1-84b6-22b9e35d4ea4] Timing: Total=1259ms (OCR=883ms, LLM=375ms)

2025-12-16T18:14:47.060257Z  INFO ✅ [902641e5-4b0b-40e1-84b6-22b9e35d4ea4] ═══════════════════════════════════════

2025-12-16T18:14:47.422786Z  INFO 📸 [a54db535-f55f-463d-a253-83c981aa257e] Received screen capture: 720x1600

2025-12-16T18:14:48.114696Z  INFO 👁️  [a54db535-f55f-463d-a253-83c981aa257e] OCR complete: 308 chars extracted (691ms)

2025-12-16T18:14:48.114786Z  INFO 🔄 [a54db535-f55f-463d-a253-83c981aa257e] Text filtered & normalized: 308 -> 308 -> 288 chars

2025-12-16T18:14:48.114827Z  INFO 🔑 [a54db535-f55f-463d-a253-83c981aa257e] Text hash: 45652f65e116fccf

2025-12-16T18:14:48.114841Z  INFO 🔍 [a54db535-f55f-463d-a253-83c981aa257e] Cache MISS - analyzing with AI

2025-12-16T18:14:48.598241Z  INFO 🧠 [a54db535-f55f-463d-a253-83c981aa257e] Classification complete: safe_content (99.00% confidence) (483ms) | Tokens: 330 in, 78 out, 408 total

2025-12-16T18:14:48.598301Z  INFO 💾 [a54db535-f55f-463d-a253-83c981aa257e] Result cached for future requests

2025-12-16T18:14:48.598309Z  INFO ✅ [a54db535-f55f-463d-a253-83c981aa257e] ═══════════════════════════════════════

2025-12-16T18:14:48.598318Z  INFO ✅ [a54db535-f55f-463d-a253-83c981aa257e] ANALYSIS COMPLETE

2025-12-16T18:14:48.598325Z  INFO ✅ [a54db535-f55f-463d-a253-83c981aa257e] ═══════════════════════════════════════

2025-12-16T18:14:48.598336Z  INFO 📝 [a54db535-f55f-463d-a253-83c981aa257e] Extracted Text (308 chars):

2025-12-16T18:14:48.598361Z  INFO    "20:14a21ble

777

Configuration

Enable Validation & Fallback

Enable Caching

Auto-scroll on Text Detection

Capture Interval: 1000ms | Validation: ON |

Cache: ON

Error

Failed to capture screen

Clear All D..."

2025-12-16T18:14:48.598379Z  INFO 🏷️  [a54db535-f55f-463d-a253-83c981aa257e] Category: safe_content

2025-12-16T18:14:48.598389Z  INFO 📊 [a54db535-f55f-463d-a253-83c981aa257e] Confidence: 99.0%

2025-12-16T18:14:48.598400Z  INFO ⚠️  [a54db535-f55f-463d-a253-83c981aa257e] Harmful: NO ✅

2025-12-16T18:14:48.598411Z  INFO 🎯 [a54db535-f55f-463d-a253-83c981aa257e] Action: CONTINUE

2025-12-16T18:14:48.598422Z  INFO 💡 [a54db535-f55f-463d-a253-83c981aa257e] Recommendation: No war-related content detected.

2025-12-16T18:14:48.598433Z  INFO ⏱️  [a54db535-f55f-463d-a253-83c981aa257e] Timing: Total=1175ms (OCR=691ms, LLM=483ms)

2025-12-16T18:14:48.598443Z  INFO ✅ [a54db535-f55f-463d-a253-83c981aa257e] ═══════════════════════════════════════

2025-12-16T18:14:48.953494Z  INFO 📸 [2df76031-56cc-4070-a7d1-6ec4d3932162] Received screen capture: 720x1600

2025-12-16T18:14:49.651881Z  INFO 👁️  [2df76031-56cc-4070-a7d1-6ec4d3932162] OCR complete: 308 chars extracted (698ms)

2025-12-16T18:14:49.651947Z  INFO 🔄 [2df76031-56cc-4070-a7d1-6ec4d3932162] Text filtered & normalized: 308 -> 308 -> 288 chars

2025-12-16T18:14:49.651974Z  INFO 🔑 [2df76031-56cc-4070-a7d1-6ec4d3932162] Text hash: 45652f65e116fccf

2025-12-16T18:14:49.651987Z  INFO 💾 [2df76031-56cc-4070-a7d1-6ec4d3932162] Cache HIT - returning cached result

2025-12-16T18:14:50.290851Z  INFO 📸 [6ef52d75-1741-45ec-8d53-c5c2e1c64fc3] Received screen capture: 720x1600

2025-12-16T18:14:51.115257Z  INFO 👁️  [6ef52d75-1741-45ec-8d53-c5c2e1c64fc3] OCR complete: 308 chars extracted (824ms)

2025-12-16T18:14:51.115313Z  INFO 🔄 [6ef52d75-1741-45ec-8d53-c5c2e1c64fc3] Text filtered & normalized: 308 -> 308 -> 288 chars

2025-12-16T18:14:51.115335Z  INFO 🔑 [6ef52d75-1741-45ec-8d53-c5c2e1c64fc3] Text hash: 45652f65e116fccf

2025-12-16T18:14:51.115344Z  INFO 💾 [6ef52d75-1741-45ec-8d53-c5c2e1c64fc3] Cache HIT - returning cached result

2025-12-16T18:14:51.402876Z  INFO 📸 [2c923a02-64a6-4b7b-9bbf-2d9d3d90b1cc] Received screen capture: 720x1600

2025-12-16T18:14:52.247303Z  INFO 👁️  [2c923a02-64a6-4b7b-9bbf-2d9d3d90b1cc] OCR complete: 308 chars extracted (844ms)

2025-12-16T18:14:52.247366Z  INFO 🔄 [2c923a02-64a6-4b7b-9bbf-2d9d3d90b1cc] Text filtered & normalized: 308 -> 308 -> 288 chars

2025-12-16T18:14:52.247390Z  INFO 🔑 [2c923a02-64a6-4b7b-9bbf-2d9d3d90b1cc] Text hash: 45652f65e116fccf

2025-12-16T18:14:52.247399Z  INFO 💾 [2c923a02-64a6-4b7b-9bbf-2d9d3d90b1cc] Cache HIT - returning cached result

2025-12-16T18:14:52.547092Z  INFO 📸 [0fdafee4-2b4d-48a6-bf20-e2125320983a] Received screen capture: 720x1600

2025-12-16T18:14:53.226525Z  INFO 👁️  [0fdafee4-2b4d-48a6-bf20-e2125320983a] OCR complete: 304 chars extracted (679ms)

2025-12-16T18:14:53.226608Z  INFO 🔄 [0fdafee4-2b4d-48a6-bf20-e2125320983a] Text filtered & normalized: 304 -> 304 -> 284 chars

2025-12-16T18:14:53.226639Z  INFO 🔑 [0fdafee4-2b4d-48a6-bf20-e2125320983a] Text hash: fa22456ee608a666

2025-12-16T18:14:53.226649Z  INFO 🔍 [0fdafee4-2b4d-48a6-bf20-e2125320983a] Cache MISS - analyzing with AI

2025-12-16T18:14:53.614999Z  INFO 🧠 [0fdafee4-2b4d-48a6-bf20-e2125320983a] Classification complete: safe_content (99.00% confidence) (388ms) | Tokens: 328 in, 102 out, 430 total

2025-12-16T18:14:53.615093Z  INFO 💾 [0fdafee4-2b4d-48a6-bf20-e2125320983a] Result cached for future requests

2025-12-16T18:14:53.615112Z  INFO ✅ [0fdafee4-2b4d-48a6-bf20-e2125320983a] ═══════════════════════════════════════

2025-12-16T18:14:53.615131Z  INFO ✅ [0fdafee4-2b4d-48a6-bf20-e2125320983a] ANALYSIS COMPLETE

2025-12-16T18:14:53.615145Z  INFO ✅ [0fdafee4-2b4d-48a6-bf20-e2125320983a] ═══════════════════════════════════════

2025-12-16T18:14:53.615164Z  INFO 📝 [0fdafee4-2b4d-48a6-bf20-e2125320983a] Extracted Text (304 chars):

2025-12-16T18:14:53.615203Z  INFO    "20:14a21ble

Configuration

Enable Validation & Fallback

Enable Caching

Auto-scroll on Text Detection

Capture Interval: 1000ms | Validation: ON |

Cache: ON

Error

Failed to capture screen

Clear All Data

..."

2025-12-16T18:14:53.615236Z  INFO 🏷️  [0fdafee4-2b4d-48a6-bf20-e2125320983a] Category: safe_content

2025-12-16T18:14:53.615255Z  INFO 📊 [0fdafee4-2b4d-48a6-bf20-e2125320983a] Confidence: 99.0%

2025-12-16T18:14:53.615273Z  INFO ⚠️  [0fdafee4-2b4d-48a6-bf20-e2125320983a] Harmful: NO ✅

2025-12-16T18:14:53.615292Z  INFO 🎯 [0fdafee4-2b4d-48a6-bf20-e2125320983a] Action: CONTINUE

2025-12-16T18:14:53.615310Z  INFO 💡 [0fdafee4-2b4d-48a6-bf20-e2125320983a] Recommendation: No war content detected.

2025-12-16T18:14:53.615329Z  INFO ⏱️  [0fdafee4-2b4d-48a6-bf20-e2125320983a] Timing: Total=1068ms (OCR=679ms, LLM=388ms)

2025-12-16T18:14:53.615348Z  INFO ✅ [0fdafee4-2b4d-48a6-bf20-e2125320983a] ═══════════════════════════════════════

2025-12-16T18:14:53.871424Z  INFO 📸 [a1039a23-3192-4d2e-bbef-03491d9cef94] Received screen capture: 720x1600

2025-12-16T18:14:54.600177Z  INFO 👁️  [a1039a23-3192-4d2e-bbef-03491d9cef94] OCR complete: 304 chars extracted (728ms)

2025-12-16T18:14:54.600285Z  INFO 🔄 [a1039a23-3192-4d2e-bbef-03491d9cef94] Text filtered & normalized: 304 -> 304 -> 284 chars

2025-12-16T18:14:54.600329Z  INFO 🔑 [a1039a23-3192-4d2e-bbef-03491d9cef94] Text hash: fa22456ee608a666

2025-12-16T18:14:54.600345Z  INFO 💾 [a1039a23-3192-4d2e-bbef-03491d9cef94] Cache HIT - returning cached result

2025-12-16T18:14:54.899713Z  INFO 📸 [a43f1af3-8d8a-49fb-a8f7-e95e57eaf8f2] Received screen capture: 720x1600

2025-12-16T18:14:55.656106Z  INFO 👁️  [a43f1af3-8d8a-49fb-a8f7-e95e57eaf8f2] OCR complete: 304 chars extracted (756ms)

2025-12-16T18:14:55.656205Z  INFO 🔄 [a43f1af3-8d8a-49fb-a8f7-e95e57eaf8f2] Text filtered & normalized: 304 -> 304 -> 284 chars

2025-12-16T18:14:55.656240Z  INFO 🔑 [a43f1af3-8d8a-49fb-a8f7-e95e57eaf8f2] Text hash: fa22456ee608a666

2025-12-16T18:14:55.656255Z  INFO 💾 [a43f1af3-8d8a-49fb-a8f7-e95e57eaf8f2] Cache HIT - returning cached result

2025-12-16T18:14:55.984530Z  INFO 📸 [77200ddf-76d4-4470-9209-82e1807e90ea] Received screen capture: 720x1600

2025-12-16T18:14:56.799238Z  INFO 👁️  [77200ddf-76d4-4470-9209-82e1807e90ea] OCR complete: 308 chars extracted (814ms)

2025-12-16T18:14:56.799349Z  INFO 🔄 [77200ddf-76d4-4470-9209-82e1807e90ea] Text filtered & normalized: 308 -> 308 -> 288 chars

2025-12-16T18:14:56.799398Z  INFO 🔑 [77200ddf-76d4-4470-9209-82e1807e90ea] Text hash: 45652f65e116fccf

2025-12-16T18:14:56.799421Z  INFO 💾 [77200ddf-76d4-4470-9209-82e1807e90ea] Cache HIT - returning cached result

2025-12-16T18:14:57.042191Z  INFO 📸 [c15ec28d-af53-4343-afea-4113814480e7] Received screen capture: 720x1600

2025-12-16T18:14:57.868697Z  INFO 👁️  [c15ec28d-af53-4343-afea-4113814480e7] OCR complete: 308 chars extracted (826ms)

2025-12-16T18:14:57.868764Z  INFO 🔄 [c15ec28d-af53-4343-afea-4113814480e7] Text filtered & normalized: 308 -> 308 -> 288 chars

2025-12-16T18:14:57.868788Z  INFO 🔑 [c15ec28d-af53-4343-afea-4113814480e7] Text hash: 45652f65e116fccf

2025-12-16T18:14:57.868802Z  INFO 💾 [c15ec28d-af53-4343-afea-4113814480e7] Cache HIT - returning cached result

2025-12-16T18:14:58.364338Z  INFO 📸 [5337a37d-0752-49cc-823e-57add0282541] Received screen capture: 720x1600

2025-12-16T18:14:59.098015Z  INFO 👁️  [5337a37d-0752-49cc-823e-57add0282541] OCR complete: 308 chars extracted (733ms)

2025-12-16T18:14:59.098074Z  INFO 🔄 [5337a37d-0752-49cc-823e-57add0282541] Text filtered & normalized: 308 -> 308 -> 288 chars

2025-12-16T18:14:59.098098Z  INFO 🔑 [5337a37d-0752-49cc-823e-57add0282541] Text hash: 45652f65e116fccf

2025-12-16T18:14:59.098107Z  INFO 💾 [5337a37d-0752-49cc-823e-57add0282541] Cache HIT - returning cached result

2025-12-16T18:14:59.493085Z  INFO 📸 [efdc65c6-7416-49b2-87c3-0f74a1227ec5] Received screen capture: 720x1600

2025-12-16T18:15:00.305689Z  INFO 👁️  [efdc65c6-7416-49b2-87c3-0f74a1227ec5] OCR complete: 308 chars extracted (812ms)

2025-12-16T18:15:00.305777Z  INFO 🔄 [efdc65c6-7416-49b2-87c3-0f74a1227ec5] Text filtered & normalized: 308 -> 308 -> 288 chars

2025-12-16T18:15:00.305812Z  INFO 🔑 [efdc65c6-7416-49b2-87c3-0f74a1227ec5] Text hash: 45652f65e116fccf

2025-12-16T18:15:00.305825Z  INFO 💾 [efdc65c6-7416-49b2-87c3-0f74a1227ec5] Cache HIT - returning cached result

2025-12-16T18:15:00.726618Z  INFO 📸 [c127f153-5e92-4da1-bd6b-06aa3dfad0df] Received screen capture: 720x1600

2025-12-16T18:15:01.433663Z  INFO 👁️  [c127f153-5e92-4da1-bd6b-06aa3dfad0df] OCR complete: 309 chars extracted (706ms)

2025-12-16T18:15:01.433759Z  INFO 🔄 [c127f153-5e92-4da1-bd6b-06aa3dfad0df] Text filtered & normalized: 309 -> 309 -> 289 chars

2025-12-16T18:15:01.433797Z  INFO 🔑 [c127f153-5e92-4da1-bd6b-06aa3dfad0df] Text hash: 5ea3d315a406a726

2025-12-16T18:15:01.433812Z  INFO 🔍 [c127f153-5e92-4da1-bd6b-06aa3dfad0df] Cache MISS - analyzing with AI

2025-12-16T18:15:01.807911Z  INFO 🧠 [c127f153-5e92-4da1-bd6b-06aa3dfad0df] Classification complete: safe_content (95.00% confidence) (374ms) | Tokens: 330 in, 87 out, 417 total

2025-12-16T18:15:01.808046Z  INFO 💾 [c127f153-5e92-4da1-bd6b-06aa3dfad0df] Result cached for future requests

2025-12-16T18:15:01.808074Z  INFO ✅ [c127f153-5e92-4da1-bd6b-06aa3dfad0df] ═══════════════════════════════════════

2025-12-16T18:15:01.808101Z  INFO ✅ [c127f153-5e92-4da1-bd6b-06aa3dfad0df] ANALYSIS COMPLETE

2025-12-16T18:15:01.808123Z  INFO ✅ [c127f153-5e92-4da1-bd6b-06aa3dfad0df] ═══════════════════════════════════════

2025-12-16T18:15:01.808154Z  INFO 📝 [c127f153-5e92-4da1-bd6b-06aa3dfad0df] Extracted Text (309 chars):

2025-12-16T18:15:01.808215Z  INFO    "20:15a21 ble

777

Configuration

Enable Validation & Fallback

Enable Caching

Auto-scroll on Text Detection

Capture Interval: 1000ms | Validation: ON |

Cache: ON

Error

Failed to capture screen

Clear All ..."

2025-12-16T18:15:01.808263Z  INFO 🏷️  [c127f153-5e92-4da1-bd6b-06aa3dfad0df] Category: safe_content

2025-12-16T18:15:01.808287Z  INFO 📊 [c127f153-5e92-4da1-bd6b-06aa3dfad0df] Confidence: 95.0%

2025-12-16T18:15:01.808310Z  INFO ⚠️  [c127f153-5e92-4da1-bd6b-06aa3dfad0df] Harmful: NO ✅

2025-12-16T18:15:01.808338Z  INFO 🎯 [c127f153-5e92-4da1-bd6b-06aa3dfad0df] Action: CONTINUE

2025-12-16T18:15:01.808364Z  INFO 💡 [c127f153-5e92-4da1-bd6b-06aa3dfad0df] Recommendation: No war content detected.

2025-12-16T18:15:01.808391Z  INFO ⏱️  [c127f153-5e92-4da1-bd6b-06aa3dfad0df] Timing: Total=1081ms (OCR=706ms, LLM=374ms)

2025-12-16T18:15:01.808420Z  INFO ✅ [c127f153-5e92-4da1-bd6b-06aa3dfad0df] ═══════════════════════════════════════

2025-12-16T18:15:02.143101Z  INFO 📸 [aadec5a7-e788-4fd2-b5ec-f1bb746f06cd] Received screen capture: 720x1600

2025-12-16T18:15:02.962924Z  INFO 👁️  [aadec5a7-e788-4fd2-b5ec-f1bb746f06cd] OCR complete: 309 chars extracted (819ms)

2025-12-16T18:15:02.963039Z  INFO 🔄 [aadec5a7-e788-4fd2-b5ec-f1bb746f06cd] Text filtered & normalized: 309 -> 309 -> 289 chars

2025-12-16T18:15:02.963085Z  INFO 🔑 [aadec5a7-e788-4fd2-b5ec-f1bb746f06cd] Text hash: 5ea3d315a406a726

2025-12-16T18:15:02.963104Z  INFO 💾 [aadec5a7-e788-4fd2-b5ec-f1bb746f06cd] Cache HIT - returning cached result

2025-12-16T18:15:03.390915Z  INFO 📸 [634a355d-6c4c-4732-ab1b-a43c89e5ade5] Received screen capture: 720x1600

2025-12-16T18:15:04.204049Z  INFO 👁️  [634a355d-6c4c-4732-ab1b-a43c89e5ade5] OCR complete: 308 chars extracted (813ms)

2025-12-16T18:15:04.204161Z  INFO 🔄 [634a355d-6c4c-4732-ab1b-a43c89e5ade5] Text filtered & normalized: 308 -> 308 -> 288 chars

2025-12-16T18:15:04.204204Z  INFO 🔑 [634a355d-6c4c-4732-ab1b-a43c89e5ade5] Text hash: e1d89355ae81a757

2025-12-16T18:15:04.204219Z  INFO 🔍 [634a355d-6c4c-4732-ab1b-a43c89e5ade5] Cache MISS - analyzing with AI

2025-12-16T18:15:04.571797Z  INFO 🧠 [634a355d-6c4c-4732-ab1b-a43c89e5ade5] Classification complete: safe_content (99.00% confidence) (367ms) | Tokens: 330 in, 74 out, 404 total

2025-12-16T18:15:04.571885Z  INFO 💾 [634a355d-6c4c-4732-ab1b-a43c89e5ade5] Result cached for future requests

2025-12-16T18:15:04.571905Z  INFO ✅ [634a355d-6c4c-4732-ab1b-a43c89e5ade5] ═══════════════════════════════════════

2025-12-16T18:15:04.571922Z  INFO ✅ [634a355d-6c4c-4732-ab1b-a43c89e5ade5] ANALYSIS COMPLETE

2025-12-16T18:15:04.571937Z  INFO ✅ [634a355d-6c4c-4732-ab1b-a43c89e5ade5] ═══════════════════════════════════════

2025-12-16T18:15:04.571954Z  INFO 📝 [634a355d-6c4c-4732-ab1b-a43c89e5ade5] Extracted Text (308 chars):

2025-12-16T18:15:04.572007Z  INFO    "20:15a21 ble

77

Configuration

Enable Validation & Fallback

Enable Caching

Auto-scroll on Text Detection

Capture Interval: 1000ms | Validation: ON |

Cache: ON

Error

Failed to capture screen

Clear All D..."

2025-12-16T18:15:04.572037Z  INFO 🏷️  [634a355d-6c4c-4732-ab1b-a43c89e5ade5] Category: safe_content

2025-12-16T18:15:04.572050Z  INFO 📊 [634a355d-6c4c-4732-ab1b-a43c89e5ade5] Confidence: 99.0%

2025-12-16T18:15:04.572065Z  INFO ⚠️  [634a355d-6c4c-4732-ab1b-a43c89e5ade5] Harmful: NO ✅

2025-12-16T18:15:04.572079Z  INFO 🎯 [634a355d-6c4c-4732-ab1b-a43c89e5ade5] Action: CONTINUE

2025-12-16T18:15:04.572092Z  INFO 💡 [634a355d-6c4c-4732-ab1b-a43c89e5ade5] Recommendation: No war-related content detected

2025-12-16T18:15:04.572110Z  INFO ⏱️  [634a355d-6c4c-4732-ab1b-a43c89e5ade5] Timing: Total=1181ms (OCR=813ms, LLM=367ms)

2025-12-16T18:15:04.572128Z  INFO ✅ [634a355d-6c4c-4732-ab1b-a43c89e5ade5] ═══════════════════════════════════════

2025-12-16T18:15:04.942593Z  INFO 📸 [0831bd0d-762e-4fea-8f05-58e410adc5a9] Received screen capture: 720x1600

2025-12-16T18:15:05.771333Z  INFO 👁️  [0831bd0d-762e-4fea-8f05-58e410adc5a9] OCR complete: 308 chars extracted (828ms)

2025-12-16T18:15:05.771487Z  INFO 🔄 [0831bd0d-762e-4fea-8f05-58e410adc5a9] Text filtered & normalized: 308 -> 308 -> 288 chars

2025-12-16T18:15:05.771537Z  INFO 🔑 [0831bd0d-762e-4fea-8f05-58e410adc5a9] Text hash: e1d89355ae81a757

2025-12-16T18:15:05.771556Z  INFO 💾 [0831bd0d-762e-4fea-8f05-58e410adc5a9] Cache HIT - returning cached result

2025-12-16T18:15:06.185234Z  INFO 📸 [a4a3b825-1e7b-4858-89d2-53ffd726090c] Received screen capture: 720x1600

2025-12-16T18:15:07.183933Z  INFO 👁️  [a4a3b825-1e7b-4858-89d2-53ffd726090c] OCR complete: 308 chars extracted (998ms)

2025-12-16T18:15:07.184040Z  INFO 🔄 [a4a3b825-1e7b-4858-89d2-53ffd726090c] Text filtered & normalized: 308 -> 308 -> 288 chars

2025-12-16T18:15:07.184085Z  INFO 🔑 [a4a3b825-1e7b-4858-89d2-53ffd726090c] Text hash: e1d89355ae81a757

2025-12-16T18:15:07.184104Z  INFO 💾 [a4a3b825-1e7b-4858-89d2-53ffd726090c] Cache HIT - returning cached result

2025-12-16T18:15:07.616003Z  INFO 📸 [820d6f6b-d2a2-432a-bf6d-47e088fbe115] Received screen capture: 720x1600

2025-12-16T18:15:08.449733Z  INFO 👁️  [820d6f6b-d2a2-432a-bf6d-47e088fbe115] OCR complete: 308 chars extracted (833ms)

2025-12-16T18:15:08.449861Z  INFO 🔄 [820d6f6b-d2a2-432a-bf6d-47e088fbe115] Text filtered & normalized: 308 -> 308 -> 288 chars

2025-12-16T18:15:08.449906Z  INFO 🔑 [820d6f6b-d2a2-432a-bf6d-47e088fbe115] Text hash: e1d89355ae81a757

2025-12-16T18:15:08.449927Z  INFO 💾 [820d6f6b-d2a2-432a-bf6d-47e088fbe115] Cache HIT - returning cached result

2025-12-16T18:15:08.651243Z  INFO 📸 [99b93034-dc49-4b26-ba57-33695e91d9e8] Received screen capture: 720x1600

2025-12-16T18:15:09.439694Z  INFO 👁️  [99b93034-dc49-4b26-ba57-33695e91d9e8] OCR complete: 308 chars extracted (788ms)

2025-12-16T18:15:09.439754Z  INFO 🔄 [99b93034-dc49-4b26-ba57-33695e91d9e8] Text filtered & normalized: 308 -> 308 -> 288 chars

2025-12-16T18:15:09.439776Z  INFO 🔑 [99b93034-dc49-4b26-ba57-33695e91d9e8] Text hash: e1d89355ae81a757

2025-12-16T18:15:09.439785Z  INFO 💾 [99b93034-dc49-4b26-ba57-33695e91d9e8] Cache HIT - returning cached result

2025-12-16T18:15:09.834735Z  INFO 📸 [00864121-fdff-4687-a8aa-815fa0db07ff] Received screen capture: 720x1600

2025-12-16T18:15:10.646882Z  INFO 👁️  [00864121-fdff-4687-a8aa-815fa0db07ff] OCR complete: 308 chars extracted (812ms)

2025-12-16T18:15:10.646953Z  INFO 🔄 [00864121-fdff-4687-a8aa-815fa0db07ff] Text filtered & normalized: 308 -> 308 -> 288 chars

2025-12-16T18:15:10.646981Z  INFO 🔑 [00864121-fdff-4687-a8aa-815fa0db07ff] Text hash: e1d89355ae81a757

2025-12-16T18:15:10.646993Z  INFO 💾 [00864121-fdff-4687-a8aa-815fa0db07ff] Cache HIT - returning cached result

2025-12-16T18:15:11.006925Z  INFO 📸 [68a402ef-18e1-476a-972b-6d89aa246626] Received screen capture: 720x1600

2025-12-16T18:15:11.830133Z  INFO 👁️  [68a402ef-18e1-476a-972b-6d89aa246626] OCR complete: 308 chars extracted (823ms)

2025-12-16T18:15:11.830253Z  INFO 🔄 [68a402ef-18e1-476a-972b-6d89aa246626] Text filtered & normalized: 308 -> 308 -> 288 chars

2025-12-16T18:15:11.830303Z  INFO 🔑 [68a402ef-18e1-476a-972b-6d89aa246626] Text hash: e1d89355ae81a757

2025-12-16T18:15:11.830320Z  INFO 💾 [68a402ef-18e1-476a-972b-6d89aa246626] Cache HIT - returning cached result

2025-12-16T18:15:12.198292Z  INFO 📸 [ee46e2b2-8575-4df2-ae26-9aff9c4c9c65] Received screen capture: 720x1600

2025-12-16T18:15:12.879850Z  INFO 👁️  [ee46e2b2-8575-4df2-ae26-9aff9c4c9c65] OCR complete: 310 chars extracted (681ms)

2025-12-16T18:15:12.879913Z  INFO 🔄 [ee46e2b2-8575-4df2-ae26-9aff9c4c9c65] Text filtered & normalized: 310 -> 310 -> 290 chars

2025-12-16T18:15:12.879938Z  INFO 🔑 [ee46e2b2-8575-4df2-ae26-9aff9c4c9c65] Text hash: 3ff474051e1d3b4a

2025-12-16T18:15:12.879946Z  INFO 🔍 [ee46e2b2-8575-4df2-ae26-9aff9c4c9c65] Cache MISS - analyzing with AI

2025-12-16T18:15:13.276472Z  INFO 🧠 [ee46e2b2-8575-4df2-ae26-9aff9c4c9c65] Classification complete: safe_content (99.00% confidence) (396ms) | Tokens: 329 in, 76 out, 405 total

2025-12-16T18:15:13.276608Z  INFO 💾 [ee46e2b2-8575-4df2-ae26-9aff9c4c9c65] Result cached for future requests

2025-12-16T18:15:13.276635Z  INFO ✅ [ee46e2b2-8575-4df2-ae26-9aff9c4c9c65] ═══════════════════════════════════════

2025-12-16T18:15:13.276666Z  INFO ✅ [ee46e2b2-8575-4df2-ae26-9aff9c4c9c65] ANALYSIS COMPLETE

2025-12-16T18:15:13.276707Z  INFO ✅ [ee46e2b2-8575-4df2-ae26-9aff9c4c9c65] ═══════════════════════════════════════

2025-12-16T18:15:13.276734Z  INFO 📝 [ee46e2b2-8575-4df2-ae26-9aff9c4c9c65] Extracted Text (310 chars):

2025-12-16T18:15:13.276788Z  INFO    "20:15a21 ble

ΔΕ

Configuration

Enable Validation & Fallback

Enable Caching

Auto-scroll on Text Detection

Capture Interval: 1000ms | Validation: ON |

Cache: ON

Error

Failed to capture screen

Clear All D..."

2025-12-16T18:15:13.276840Z  INFO 🏷️  [ee46e2b2-8575-4df2-ae26-9aff9c4c9c65] Category: safe_content

2025-12-16T18:15:13.276860Z  INFO 📊 [ee46e2b2-8575-4df2-ae26-9aff9c4c9c65] Confidence: 99.0%

2025-12-16T18:15:13.276881Z  INFO ⚠️  [ee46e2b2-8575-4df2-ae26-9aff9c4c9c65] Harmful: NO ✅

2025-12-16T18:15:13.276903Z  INFO 🎯 [ee46e2b2-8575-4df2-ae26-9aff9c4c9c65] Action: CONTINUE

2025-12-16T18:15:13.276923Z  INFO 💡 [ee46e2b2-8575-4df2-ae26-9aff9c4c9c65] Recommendation: No war-related content detected.

2025-12-16T18:15:13.276945Z  INFO ⏱️  [ee46e2b2-8575-4df2-ae26-9aff9c4c9c65] Timing: Total=1078ms (OCR=681ms, LLM=396ms)

2025-12-16T18:15:13.276966Z  INFO ✅ [ee46e2b2-8575-4df2-ae26-9aff9c4c9c65] ═══════════════════════════════════════

2025-12-16T18:15:13.443863Z  INFO 📸 [468dc877-5a91-4f85-9a02-6163dde82fcb] Received screen capture: 720x1600

2025-12-16T18:15:14.042303Z  INFO 👁️  [468dc877-5a91-4f85-9a02-6163dde82fcb] OCR complete: 305 chars extracted (598ms)

2025-12-16T18:15:14.042366Z  INFO 🔄 [468dc877-5a91-4f85-9a02-6163dde82fcb] Text filtered & normalized: 305 -> 305 -> 285 chars

2025-12-16T18:15:14.042389Z  INFO 🔑 [468dc877-5a91-4f85-9a02-6163dde82fcb] Text hash: bb7108edd8ba29be

2025-12-16T18:15:14.042397Z  INFO 🔍 [468dc877-5a91-4f85-9a02-6163dde82fcb] Cache MISS - analyzing with AI

2025-12-16T18:15:14.410162Z  INFO 🧠 [468dc877-5a91-4f85-9a02-6163dde82fcb] Classification complete: safe_content (99.00% confidence) (367ms) | Tokens: 328 in, 84 out, 412 total

2025-12-16T18:15:14.410230Z  INFO 💾 [468dc877-5a91-4f85-9a02-6163dde82fcb] Result cached for future requests

2025-12-16T18:15:14.410243Z  INFO ✅ [468dc877-5a91-4f85-9a02-6163dde82fcb] ═══════════════════════════════════════

2025-12-16T18:15:14.410256Z  INFO ✅ [468dc877-5a91-4f85-9a02-6163dde82fcb] ANALYSIS COMPLETE

2025-12-16T18:15:14.410267Z  INFO ✅ [468dc877-5a91-4f85-9a02-6163dde82fcb] ═══════════════════════════════════════

2025-12-16T18:15:14.410280Z  INFO 📝 [468dc877-5a91-4f85-9a02-6163dde82fcb] Extracted Text (305 chars):

2025-12-16T18:15:14.410307Z  INFO    "20:15a21 ble

Configuration

Enable Validation & Fallback

Enable Caching

Auto-scroll on Text Detection

Capture Interval: 1000ms | Validation: ON |

Cache: ON

Error

Failed to capture screen

Clear All Data..."

2025-12-16T18:15:14.410330Z  INFO 🏷️  [468dc877-5a91-4f85-9a02-6163dde82fcb] Category: safe_content

2025-12-16T18:15:14.410343Z  INFO 📊 [468dc877-5a91-4f85-9a02-6163dde82fcb] Confidence: 99.0%

2025-12-16T18:15:14.410357Z  INFO ⚠️  [468dc877-5a91-4f85-9a02-6163dde82fcb] Harmful: NO ✅

2025-12-16T18:15:14.410369Z  INFO 🎯 [468dc877-5a91-4f85-9a02-6163dde82fcb] Action: CONTINUE

2025-12-16T18:15:14.410381Z  INFO 💡 [468dc877-5a91-4f85-9a02-6163dde82fcb] Recommendation: No war-related content detected.

2025-12-16T18:15:14.410395Z  INFO ⏱️  [468dc877-5a91-4f85-9a02-6163dde82fcb] Timing: Total=966ms (OCR=598ms, LLM=367ms)

2025-12-16T18:15:14.410408Z  INFO ✅ [468dc877-5a91-4f85-9a02-6163dde82fcb] ═══════════════════════════════════════

2025-12-16T18:15:14.644120Z  INFO 📸 [84a89ab3-a85a-48d6-8486-9bab43ae9295] Received screen capture: 720x1600

2025-12-16T18:15:15.454147Z  INFO 👁️  [84a89ab3-a85a-48d6-8486-9bab43ae9295] OCR complete: 305 chars extracted (809ms)

2025-12-16T18:15:15.454205Z  INFO 🔄 [84a89ab3-a85a-48d6-8486-9bab43ae9295] Text filtered & normalized: 305 -> 305 -> 285 chars

2025-12-16T18:15:15.454229Z  INFO 🔑 [84a89ab3-a85a-48d6-8486-9bab43ae9295] Text hash: bb7108edd8ba29be

2025-12-16T18:15:15.454239Z  INFO 💾 [84a89ab3-a85a-48d6-8486-9bab43ae9295] Cache HIT - returning cached result

2025-12-16T18:15:15.775346Z  INFO 📸 [d51c5cc9-a130-445c-bce1-4208028bfb5c] Received screen capture: 720x1600

2025-12-16T18:15:16.459650Z  INFO 👁️  [d51c5cc9-a130-445c-bce1-4208028bfb5c] OCR complete: 305 chars extracted (684ms)

2025-12-16T18:15:16.459755Z  INFO 🔄 [d51c5cc9-a130-445c-bce1-4208028bfb5c] Text filtered & normalized: 305 -> 305 -> 285 chars

2025-12-16T18:15:16.459795Z  INFO 🔑 [d51c5cc9-a130-445c-bce1-4208028bfb5c] Text hash: bb7108edd8ba29be

2025-12-16T18:15:16.459815Z  INFO 💾 [d51c5cc9-a130-445c-bce1-4208028bfb5c] Cache HIT - returning cached result

2025-12-16T18:15:16.668859Z  INFO 📸 [4d953514-c1f5-4120-b8fb-4bf884ded425] Received screen capture: 720x1600

2025-12-16T18:15:17.391560Z  INFO 👁️  [4d953514-c1f5-4120-b8fb-4bf884ded425] OCR complete: 305 chars extracted (722ms)

2025-12-16T18:15:17.391669Z  INFO 🔄 [4d953514-c1f5-4120-b8fb-4bf884ded425] Text filtered & normalized: 305 -> 305 -> 285 chars

2025-12-16T18:15:17.391722Z  INFO 🔑 [4d953514-c1f5-4120-b8fb-4bf884ded425] Text hash: bb7108edd8ba29be

2025-12-16T18:15:17.391740Z  INFO 💾 [4d953514-c1f5-4120-b8fb-4bf884ded425] Cache HIT - returning cached result

2025-12-16T18:15:17.732663Z  INFO 📸 [9c0768e5-ee83-4369-b626-3523b11a1956] Received screen capture: 720x1600

2025-12-16T18:15:18.548427Z  INFO 👁️  [9c0768e5-ee83-4369-b626-3523b11a1956] OCR complete: 309 chars extracted (815ms)

2025-12-16T18:15:18.548540Z  INFO 🔄 [9c0768e5-ee83-4369-b626-3523b11a1956] Text filtered & normalized: 309 -> 309 -> 289 chars

2025-12-16T18:15:18.548585Z  INFO 🔑 [9c0768e5-ee83-4369-b626-3523b11a1956] Text hash: 5ea3d315a406a726

2025-12-16T18:15:18.548603Z  INFO 💾 [9c0768e5-ee83-4369-b626-3523b11a1956] Cache HIT - returning cached result

2025-12-16T18:15:19.194001Z  INFO 📸 [45a90c5f-441d-4556-8f6e-6a3d9af775af] Received screen capture: 720x1600

2025-12-16T18:15:20.263024Z  INFO 👁️  [45a90c5f-441d-4556-8f6e-6a3d9af775af] OCR complete: 309 chars extracted (1068ms)

2025-12-16T18:15:20.263107Z  INFO 🔄 [45a90c5f-441d-4556-8f6e-6a3d9af775af] Text filtered & normalized: 309 -> 309 -> 289 chars

2025-12-16T18:15:20.263145Z  INFO 🔑 [45a90c5f-441d-4556-8f6e-6a3d9af775af] Text hash: 5ea3d315a406a726

2025-12-16T18:15:20.263158Z  INFO 💾 [45a90c5f-441d-4556-8f6e-6a3d9af775af] Cache HIT - returning cached result

2025-12-16T18:15:20.607584Z  INFO 📸 [791dfe67-5135-4274-92f0-0b2e4257fc2d] Received screen capture: 720x1600

2025-12-16T18:15:21.798183Z  INFO 👁️  [791dfe67-5135-4274-92f0-0b2e4257fc2d] OCR complete: 309 chars extracted (1190ms)

2025-12-16T18:15:21.798243Z  INFO 🔄 [791dfe67-5135-4274-92f0-0b2e4257fc2d] Text filtered & normalized: 309 -> 309 -> 289 chars

2025-12-16T18:15:21.798266Z  INFO 🔑 [791dfe67-5135-4274-92f0-0b2e4257fc2d] Text hash: 5ea3d315a406a726

2025-12-16T18:15:21.798275Z  INFO 💾 [791dfe67-5135-4274-92f0-0b2e4257fc2d] Cache HIT - returning cached result

2025-12-16T18:15:22.237689Z  INFO 📸 [7d7c3894-9514-430b-accb-c272a974c3f6] Received screen capture: 720x1600

2025-12-16T18:15:23.700495Z  INFO 👁️  [7d7c3894-9514-430b-accb-c272a974c3f6] OCR complete: 309 chars extracted (1462ms)

2025-12-16T18:15:23.700574Z  INFO 🔄 [7d7c3894-9514-430b-accb-c272a974c3f6] Text filtered & normalized: 309 -> 309 -> 289 chars

2025-12-16T18:15:23.700606Z  INFO 🔑 [7d7c3894-9514-430b-accb-c272a974c3f6] Text hash: 5ea3d315a406a726

2025-12-16T18:15:23.700618Z  INFO 💾 [7d7c3894-9514-430b-accb-c272a974c3f6] Cache HIT - returning cached result

2025-12-16T18:15:24.084313Z  INFO 📸 [5eb41493-7d0b-4890-a717-70cb1c46a965] Received screen capture: 720x1600

2025-12-16T18:15:24.989671Z  INFO 👁️  [5eb41493-7d0b-4890-a717-70cb1c46a965] OCR complete: 309 chars extracted (905ms)

2025-12-16T18:15:24.989739Z  INFO 🔄 [5eb41493-7d0b-4890-a717-70cb1c46a965] Text filtered & normalized: 309 -> 309 -> 289 chars

2025-12-16T18:15:24.989763Z  INFO 🔑 [5eb41493-7d0b-4890-a717-70cb1c46a965] Text hash: 5ea3d315a406a726

2025-12-16T18:15:24.989774Z  INFO 💾 [5eb41493-7d0b-4890-a717-70cb1c46a965] Cache HIT - returning cached result

2025-12-16T18:15:25.404437Z  INFO 📸 [bdb49875-d6da-46da-8dcf-b6e3ac080351] Received screen capture: 720x1600

2025-12-16T18:15:26.443774Z  INFO 👁️  [bdb49875-d6da-46da-8dcf-b6e3ac080351] OCR complete: 309 chars extracted (1039ms)

2025-12-16T18:15:26.443893Z  INFO 🔄 [bdb49875-d6da-46da-8dcf-b6e3ac080351] Text filtered & normalized: 309 -> 309 -> 289 chars

2025-12-16T18:15:26.443940Z  INFO 🔑 [bdb49875-d6da-46da-8dcf-b6e3ac080351] Text hash: 5ea3d315a406a726

2025-12-16T18:15:26.443959Z  INFO 💾 [bdb49875-d6da-46da-8dcf-b6e3ac080351] Cache HIT - returning cached result

2025-12-16T18:15:26.804435Z  INFO 📸 [9a80c017-0f1c-4462-9d8b-c61304665f08] Received screen capture: 720x1600

2025-12-16T18:15:27.620389Z  INFO 👁️  [9a80c017-0f1c-4462-9d8b-c61304665f08] OCR complete: 309 chars extracted (815ms)

2025-12-16T18:15:27.620480Z  INFO 🔄 [9a80c017-0f1c-4462-9d8b-c61304665f08] Text filtered & normalized: 309 -> 309 -> 290 chars

2025-12-16T18:15:27.620514Z  INFO 🔑 [9a80c017-0f1c-4462-9d8b-c61304665f08] Text hash: e887f7d36945c164

2025-12-16T18:15:27.620525Z  INFO 🔍 [9a80c017-0f1c-4462-9d8b-c61304665f08] Cache MISS - analyzing with AI

2025-12-16T18:15:28.022109Z  INFO 🧠 [9a80c017-0f1c-4462-9d8b-c61304665f08] Classification complete: safe_content (99.00% confidence) (401ms) | Tokens: 323 in, 85 out, 408 total

2025-12-16T18:15:28.022203Z  INFO 💾 [9a80c017-0f1c-4462-9d8b-c61304665f08] Result cached for future requests

2025-12-16T18:15:28.022222Z  INFO ✅ [9a80c017-0f1c-4462-9d8b-c61304665f08] ═══════════════════════════════════════

2025-12-16T18:15:28.022242Z  INFO ✅ [9a80c017-0f1c-4462-9d8b-c61304665f08] ANALYSIS COMPLETE

2025-12-16T18:15:28.022256Z  INFO ✅ [9a80c017-0f1c-4462-9d8b-c61304665f08] ═══════════════════════════════════════

2025-12-16T18:15:28.022274Z  INFO 📝 [9a80c017-0f1c-4462-9d8b-c61304665f08] Extracted Text (309 chars):

2025-12-16T18:15:28.022318Z  INFO    "20 Available

Configuration

Enable Validation & Fallback

Enable Caching

Auto-scroll on Text Detection

Capture Interval: 1000ms | Validation: ON|

Cache: ON

Test Single Extraction

Start Live Capture

Clea..."

2025-12-16T18:15:28.022374Z  INFO 🏷️  [9a80c017-0f1c-4462-9d8b-c61304665f08] Category: safe_content

2025-12-16T18:15:28.022389Z  INFO 📊 [9a80c017-0f1c-4462-9d8b-c61304665f08] Confidence: 99.0%

2025-12-16T18:15:28.022405Z  INFO ⚠️  [9a80c017-0f1c-4462-9d8b-c61304665f08] Harmful: NO ✅

2025-12-16T18:15:28.022420Z  INFO 🎯 [9a80c017-0f1c-4462-9d8b-c61304665f08] Action: CONTINUE

2025-12-16T18:15:28.022441Z  INFO 💡 [9a80c017-0f1c-4462-9d8b-c61304665f08] Recommendation: No war-related content detected.

2025-12-16T18:15:28.022461Z  INFO ⏱️  [9a80c017-0f1c-4462-9d8b-c61304665f08] Timing: Total=1217ms (OCR=815ms, LLM=401ms)

2025-12-16T18:15:28.022481Z  INFO ✅ [9a80c017-0f1c-4462-9d8b-c61304665f08] ═══════════════════════════════════════

2025-12-16T18:15:28.401790Z  INFO 📸 [221da2f4-a565-4720-b6c8-874d6c5f990e] Received screen capture: 720x1600

2025-12-16T18:15:29.187659Z  INFO 👁️  [221da2f4-a565-4720-b6c8-874d6c5f990e] OCR complete: 276 chars extracted (785ms)

2025-12-16T18:15:29.187786Z  INFO 🔄 [221da2f4-a565-4720-b6c8-874d6c5f990e] Text filtered & normalized: 276 -> 276 -> 247 chars

2025-12-16T18:15:29.187835Z  INFO 🔑 [221da2f4-a565-4720-b6c8-874d6c5f990e] Text hash: de81b38fe9c17f16

2025-12-16T18:15:29.187853Z  INFO 🔍 [221da2f4-a565-4720-b6c8-874d6c5f990e] Cache MISS - analyzing with AI

2025-12-16T18:15:29.558374Z  INFO 🧠 [221da2f4-a565-4720-b6c8-874d6c5f990e] Classification complete: safe_content (99.00% confidence) (370ms) | Tokens: 317 in, 72 out, 389 total

2025-12-16T18:15:29.558500Z  INFO 💾 [221da2f4-a565-4720-b6c8-874d6c5f990e] Result cached for future requests

2025-12-16T18:15:29.558535Z  INFO ✅ [221da2f4-a565-4720-b6c8-874d6c5f990e] ═══════════════════════════════════════

2025-12-16T18:15:29.558565Z  INFO ✅ [221da2f4-a565-4720-b6c8-874d6c5f990e] ANALYSIS COMPLETE

2025-12-16T18:15:29.558589Z  INFO ✅ [221da2f4-a565-4720-b6c8-874d6c5f990e] ═══════════════════════════════════════

2025-12-16T18:15:29.558618Z  INFO 📝 [221da2f4-a565-4720-b6c8-874d6c5f990e] Extracted Text (276 chars):

2025-12-16T18:15:29.558694Z  INFO    "20Aчalabde

Configuration

Enable Validation & Fallback

Enable Caching

Auto-scroll on Text Detection

Capture Interval: 1000ms | Validation: ON |

Cache: ON

Testing...

Processing...

Clear All Data

Process..."

2025-12-16T18:15:29.558760Z  INFO 🏷️  [221da2f4-a565-4720-b6c8-874d6c5f990e] Category: safe_content

2025-12-16T18:15:29.558790Z  INFO 📊 [221da2f4-a565-4720-b6c8-874d6c5f990e] Confidence: 99.0%

2025-12-16T18:15:29.558822Z  INFO ⚠️  [221da2f4-a565-4720-b6c8-874d6c5f990e] Harmful: NO ✅

2025-12-16T18:15:29.558849Z  INFO 🎯 [221da2f4-a565-4720-b6c8-874d6c5f990e] Action: CONTINUE

2025-12-16T18:15:29.558878Z  INFO 💡 [221da2f4-a565-4720-b6c8-874d6c5f990e] Recommendation: No war-related content detected.

2025-12-16T18:15:29.558902Z  INFO ⏱️  [221da2f4-a565-4720-b6c8-874d6c5f990e] Timing: Total=1156ms (OCR=785ms, LLM=370ms)

2025-12-16T18:15:29.558929Z  INFO ✅ [221da2f4-a565-4720-b6c8-874d6c5f990e] ═══════════════════════════════════════

2025-12-16T18:15:29.827153Z  INFO 📸 [c53d70b8-ed32-4258-9f39-2ab702843c61] Received screen capture: 720x1600

2025-12-16T18:15:30.647724Z  INFO 👁️  [c53d70b8-ed32-4258-9f39-2ab702843c61] OCR complete: 432 chars extracted (820ms)

2025-12-16T18:15:30.647799Z  INFO 🔄 [c53d70b8-ed32-4258-9f39-2ab702843c61] Text filtered & normalized: 432 -> 432 -> 409 chars

2025-12-16T18:15:30.647828Z  INFO 🔑 [c53d70b8-ed32-4258-9f39-2ab702843c61] Text hash: f9e5969936ed57a4

2025-12-16T18:15:30.647837Z  INFO 🔍 [c53d70b8-ed32-4258-9f39-2ab702843c61] Cache MISS - analyzing with AI

2025-12-16T18:15:30.990528Z  INFO 🧠 [c53d70b8-ed32-4258-9f39-2ab702843c61] Classification complete: safe_content (99.00% confidence) (342ms) | Tokens: 349 in, 86 out, 435 total

2025-12-16T18:15:30.990560Z  INFO 💾 [c53d70b8-ed32-4258-9f39-2ab702843c61] Result cached for future requests

2025-12-16T18:15:30.990566Z  INFO ✅ [c53d70b8-ed32-4258-9f39-2ab702843c61] ═══════════════════════════════════════

2025-12-16T18:15:30.990571Z  INFO ✅ [c53d70b8-ed32-4258-9f39-2ab702843c61] ANALYSIS COMPLETE

2025-12-16T18:15:30.990575Z  INFO ✅ [c53d70b8-ed32-4258-9f39-2ab702843c61] ═══════════════════════════════════════

2025-12-16T18:15:30.990580Z  INFO 📝 [c53d70b8-ed32-4258-9f39-2ab702843c61] Extracted Text (432 chars):

2025-12-16T18:15:30.990590Z  INFO    "20:15a21 ble

777

Configuration

Enable Validation & Fallback

Enable Caching

Auto-scroll on Text Detection

Capture Interval: 1000ms | Validation: ON|

Cache: ON

Local Text Extraction Started

Capturing sc..."

2025-12-16T18:15:30.990598Z  INFO 🏷️  [c53d70b8-ed32-4258-9f39-2ab702843c61] Category: safe_content

2025-12-16T18:15:30.990603Z  INFO 📊 [c53d70b8-ed32-4258-9f39-2ab702843c61] Confidence: 99.0%

2025-12-16T18:15:30.990607Z  INFO ⚠️  [c53d70b8-ed32-4258-9f39-2ab702843c61] Harmful: NO ✅

2025-12-16T18:15:30.990612Z  INFO 🎯 [c53d70b8-ed32-4258-9f39-2ab702843c61] Action: CONTINUE

2025-12-16T18:15:30.990616Z  INFO 💡 [c53d70b8-ed32-4258-9f39-2ab702843c61] Recommendation: No war-related content detected.

2025-12-16T18:15:30.990621Z  INFO ⏱️  [c53d70b8-ed32-4258-9f39-2ab702843c61] Timing: Total=1163ms (OCR=820ms, LLM=342ms)

2025-12-16T18:15:30.990625Z  INFO ✅ [c53d70b8-ed32-4258-9f39-2ab702843c61] ═══════════════════════════════════════

2025-12-16T18:15:31.365387Z  INFO 📸 [e30fa61d-6edb-427e-bed0-d5f25943fbf6] Received screen capture: 720x1600

2025-12-16T18:15:32.175263Z  INFO 👁️  [e30fa61d-6edb-427e-bed0-d5f25943fbf6] OCR complete: 432 chars extracted (809ms)

2025-12-16T18:15:32.175368Z  INFO 🔄 [e30fa61d-6edb-427e-bed0-d5f25943fbf6] Text filtered & normalized: 432 -> 432 -> 409 chars

2025-12-16T18:15:32.175405Z  INFO 🔑 [e30fa61d-6edb-427e-bed0-d5f25943fbf6] Text hash: f9e5969936ed57a4

2025-12-16T18:15:32.175419Z  INFO 💾 [e30fa61d-6edb-427e-bed0-d5f25943fbf6] Cache HIT - returning cached result

2025-12-16T18:15:43.047602Z  INFO 📸 [2a41ec38-f038-4387-9fa6-95bf59ec4d5f] Received screen capture: 720x1600

2025-12-16T18:15:43.818835Z  INFO 👁️  [2a41ec38-f038-4387-9fa6-95bf59ec4d5f] OCR complete: 303 chars extracted (771ms)

2025-12-16T18:15:43.818924Z  INFO 🔄 [2a41ec38-f038-4387-9fa6-95bf59ec4d5f] Text filtered & normalized: 303 -> 303 -> 284 chars

2025-12-16T18:15:43.818960Z  INFO 🔑 [2a41ec38-f038-4387-9fa6-95bf59ec4d5f] Text hash: 8081d3db1cbbaf8d

2025-12-16T18:15:43.818972Z  INFO 🔍 [2a41ec38-f038-4387-9fa6-95bf59ec4d5f] Cache MISS - analyzing with AI

2025-12-16T18:15:44.202040Z  INFO 🧠 [2a41ec38-f038-4387-9fa6-95bf59ec4d5f] Classification complete: safe_content (99.00% confidence) (383ms) | Tokens: 322 in, 75 out, 397 total

2025-12-16T18:15:44.202147Z  INFO 💾 [2a41ec38-f038-4387-9fa6-95bf59ec4d5f] Result cached for future requests

2025-12-16T18:15:44.202169Z  INFO ✅ [2a41ec38-f038-4387-9fa6-95bf59ec4d5f] ═══════════════════════════════════════

2025-12-16T18:15:44.202190Z  INFO ✅ [2a41ec38-f038-4387-9fa6-95bf59ec4d5f] ANALYSIS COMPLETE

2025-12-16T18:15:44.202207Z  INFO ✅ [2a41ec38-f038-4387-9fa6-95bf59ec4d5f] ═══════════════════════════════════════

2025-12-16T18:15:44.202227Z  INFO 📝 [2a41ec38-f038-4387-9fa6-95bf59ec4d5f] Extracted Text (303 chars):

2025-12-16T18:15:44.202274Z  INFO    "20 Available

Configuration

Enable Validation & Fallback

Enable Caching

Auto-scroll on Text Detection

Capture Interval: 1000ms | Validation: ON|

Cache: ON

Test Single Extraction

Stop Capture

Clear All ..."

2025-12-16T18:15:44.202313Z  INFO 🏷️  [2a41ec38-f038-4387-9fa6-95bf59ec4d5f] Category: safe_content

2025-12-16T18:15:44.202333Z  INFO 📊 [2a41ec38-f038-4387-9fa6-95bf59ec4d5f] Confidence: 99.0%

2025-12-16T18:15:44.202353Z  INFO ⚠️  [2a41ec38-f038-4387-9fa6-95bf59ec4d5f] Harmful: NO ✅

2025-12-16T18:15:44.202374Z  INFO 🎯 [2a41ec38-f038-4387-9fa6-95bf59ec4d5f] Action: CONTINUE

2025-12-16T18:15:44.202395Z  INFO 💡 [2a41ec38-f038-4387-9fa6-95bf59ec4d5f] Recommendation: No war-related content detected.

2025-12-16T18:15:44.202416Z  INFO ⏱️  [2a41ec38-f038-4387-9fa6-95bf59ec4d5f] Timing: Total=1154ms (OCR=771ms, LLM=383ms)

2025-12-16T18:15:44.202436Z  INFO ✅ [2a41ec38-f038-4387-9fa6-95bf59ec4d5f] ═══════════════════════════════════════

2025-12-16T18:15:44.480108Z  INFO 📸 [fbe78ae7-22af-4a96-82a4-19feafeae301] Received screen capture: 720x1600

2025-12-16T18:15:45.278284Z  INFO 👁️  [fbe78ae7-22af-4a96-82a4-19feafeae301] OCR complete: 346 chars extracted (798ms)

2025-12-16T18:15:45.278406Z  INFO 🔄 [fbe78ae7-22af-4a96-82a4-19feafeae301] Text filtered & normalized: 346 -> 346 -> 309 chars

2025-12-16T18:15:45.278460Z  INFO 🔑 [fbe78ae7-22af-4a96-82a4-19feafeae301] Text hash: b076e65f4e11d071

2025-12-16T18:15:45.278475Z  INFO 🔍 [fbe78ae7-22af-4a96-82a4-19feafeae301] Cache MISS - analyzing with AI

2025-12-16T18:15:45.634985Z  INFO 🧠 [fbe78ae7-22af-4a96-82a4-19feafeae301] Classification complete: safe_content (98.00% confidence) (356ms) | Tokens: 327 in, 76 out, 403 total

2025-12-16T18:15:45.635081Z  INFO 💾 [fbe78ae7-22af-4a96-82a4-19feafeae301] Result cached for future requests

2025-12-16T18:15:45.635100Z  INFO ✅ [fbe78ae7-22af-4a96-82a4-19feafeae301] ═══════════════════════════════════════

2025-12-16T18:15:45.635119Z  INFO ✅ [fbe78ae7-22af-4a96-82a4-19feafeae301] ANALYSIS COMPLETE

2025-12-16T18:15:45.635137Z  INFO ✅ [fbe78ae7-22af-4a96-82a4-19feafeae301] ═══════════════════════════════════════

2025-12-16T18:15:45.635157Z  INFO 📝 [fbe78ae7-22af-4a96-82a4-19feafeae301] Extracted Text (346 chars):

2025-12-16T18:15:45.635198Z  INFO    "2015rgenCaptureModule:

Available

ScreenPermission Module: ☑

Available

LocalTextExtraction Module: ☑

Available

Configuration

Enable Validation & Fallback

Enable Caching

Auto-scroll on Text Detection

Ca..."

2025-12-16T18:15:45.635232Z  INFO 🏷️  [fbe78ae7-22af-4a96-82a4-19feafeae301] Category: safe_content

2025-12-16T18:15:45.635252Z  INFO 📊 [fbe78ae7-22af-4a96-82a4-19feafeae301] Confidence: 98.0%

2025-12-16T18:15:45.635271Z  INFO ⚠️  [fbe78ae7-22af-4a96-82a4-19feafeae301] Harmful: NO ✅

2025-12-16T18:15:45.635289Z  INFO 🎯 [fbe78ae7-22af-4a96-82a4-19feafeae301] Action: CONTINUE

2025-12-16T18:15:45.635308Z  INFO 💡 [fbe78ae7-22af-4a96-82a4-19feafeae301] Recommendation: No war-related content detected.

2025-12-16T18:15:45.635327Z  INFO ⏱️  [fbe78ae7-22af-4a96-82a4-19feafeae301] Timing: Total=1155ms (OCR=798ms, LLM=356ms)

2025-12-16T18:15:45.635347Z  INFO ✅ [fbe78ae7-22af-4a96-82a4-19feafeae301] ═══════════════════════════════════════

2025-12-16T18:15:45.903965Z  INFO 📸 [75b17c3a-437e-456b-9d49-ad320feeabc3] Received screen capture: 720x1600

2025-12-16T18:15:46.875928Z  INFO 👁️  [75b17c3a-437e-456b-9d49-ad320feeabc3] OCR complete: 453 chars extracted (971ms)

2025-12-16T18:15:46.876028Z  INFO 🔄 [75b17c3a-437e-456b-9d49-ad320feeabc3] Text filtered & normalized: 453 -> 453 -> 429 chars

2025-12-16T18:15:46.876065Z  INFO 🔑 [75b17c3a-437e-456b-9d49-ad320feeabc3] Text hash: 4e116bdce6be944f

2025-12-16T18:15:46.876077Z  INFO 🔍 [75b17c3a-437e-456b-9d49-ad320feeabc3] Cache MISS - analyzing with AI

2025-12-16T18:15:47.274669Z  INFO 🧠 [75b17c3a-437e-456b-9d49-ad320feeabc3] Classification complete: safe_content (99.00% confidence) (398ms) | Tokens: 349 in, 117 out, 466 total

2025-12-16T18:15:47.274872Z  INFO 💾 [75b17c3a-437e-456b-9d49-ad320feeabc3] Result cached for future requests

2025-12-16T18:15:47.274905Z  INFO ✅ [75b17c3a-437e-456b-9d49-ad320feeabc3] ═══════════════════════════════════════

2025-12-16T18:15:47.274935Z  INFO ✅ [75b17c3a-437e-456b-9d49-ad320feeabc3] ANALYSIS COMPLETE

2025-12-16T18:15:47.274960Z  INFO ✅ [75b17c3a-437e-456b-9d49-ad320feeabc3] ═══════════════════════════════════════

2025-12-16T18:15:47.274989Z  INFO 📝 [75b17c3a-437e-456b-9d49-ad320feeabc3] Extracted Text (453 chars):

2025-12-16T18:15:47.275062Z  INFO    "20 Available

Configuration

Enable Validation & Fallback

Enable Caching

Auto-scroll on Text Detection

Capture Interval: 1000ms | Validation: ON |

Cache: ON

Local Text Extraction Started

Captur Test Sin..."

2025-12-16T18:15:47.275110Z  INFO 🏷️  [75b17c3a-437e-456b-9d49-ad320feeabc3] Category: safe_content

2025-12-16T18:15:47.275138Z  INFO 📊 [75b17c3a-437e-456b-9d49-ad320feeabc3] Confidence: 99.0%

2025-12-16T18:15:47.275164Z  INFO ⚠️  [75b17c3a-437e-456b-9d49-ad320feeabc3] Harmful: NO ✅

2025-12-16T18:15:47.275189Z  INFO 🎯 [75b17c3a-437e-456b-9d49-ad320feeabc3] Action: CONTINUE

2025-12-16T18:15:47.275215Z  INFO 💡 [75b17c3a-437e-456b-9d49-ad320feeabc3] Recommendation: No war-related keywords or hashtags detected; content is purely technical and unrelated to conflict.

2025-12-16T18:15:47.275253Z  INFO ⏱️  [75b17c3a-437e-456b-9d49-ad320feeabc3] Timing: Total=1370ms (OCR=971ms, LLM=398ms)

2025-12-16T18:15:47.275281Z  INFO ✅ [75b17c3a-437e-456b-9d49-ad320feeabc3] ═══════════════════════════════════════

2025-12-16T18:15:47.534098Z  INFO 📸 [cc0b51d4-f557-483f-9bd2-dbb7db022f67] Received screen capture: 720x1600

2025-12-16T18:15:48.552591Z  INFO 👁️  [cc0b51d4-f557-483f-9bd2-dbb7db022f67] OCR complete: 328 chars extracted (1018ms)

2025-12-16T18:15:48.552657Z  INFO 🔄 [cc0b51d4-f557-483f-9bd2-dbb7db022f67] Text filtered & normalized: 328 -> 328 -> 306 chars

2025-12-16T18:15:48.552685Z  INFO 🔑 [cc0b51d4-f557-483f-9bd2-dbb7db022f67] Text hash: c187e22b1507c400

2025-12-16T18:15:48.552693Z  INFO 🔍 [cc0b51d4-f557-483f-9bd2-dbb7db022f67] Cache MISS - analyzing with AI

2025-12-16T18:15:48.910838Z  INFO 🧠 [cc0b51d4-f557-483f-9bd2-dbb7db022f67] Classification complete: safe_content (95.00% confidence) (358ms) | Tokens: 339 in, 92 out, 431 total

2025-12-16T18:15:48.910877Z  INFO 💾 [cc0b51d4-f557-483f-9bd2-dbb7db022f67] Result cached for future requests

2025-12-16T18:15:48.910884Z  INFO ✅ [cc0b51d4-f557-483f-9bd2-dbb7db022f67] ═══════════════════════════════════════

2025-12-16T18:15:48.910891Z  INFO ✅ [cc0b51d4-f557-483f-9bd2-dbb7db022f67] ANALYSIS COMPLETE

2025-12-16T18:15:48.910897Z  INFO ✅ [cc0b51d4-f557-483f-9bd2-dbb7db022f67] ═══════════════════════════════════════

2025-12-16T18:15:48.910904Z  INFO 📝 [cc0b51d4-f557-483f-9bd2-dbb7db022f67] Extracted Text (328 chars):

2025-12-16T18:15:48.910919Z  INFO    "20:15 2

Enable Caching

Auto-scroll on Text Detection

Capture Interval: 1000ms | Validation: ON|

Cache: ON

Test Single Extraction

63530

Stop Capture

Clear All Data

Live Performance Stats

0

Total Captur..."

2025-12-16T18:15:48.910930Z  INFO 🏷️  [cc0b51d4-f557-483f-9bd2-dbb7db022f67] Category: safe_content

2025-12-16T18:15:48.910935Z  INFO 📊 [cc0b51d4-f557-483f-9bd2-dbb7db022f67] Confidence: 95.0%

2025-12-16T18:15:48.910939Z  INFO ⚠️  [cc0b51d4-f557-483f-9bd2-dbb7db022f67] Harmful: NO ✅

2025-12-16T18:15:48.910944Z  INFO 🎯 [cc0b51d4-f557-483f-9bd2-dbb7db022f67] Action: CONTINUE

2025-12-16T18:15:48.910949Z  INFO 💡 [cc0b51d4-f557-483f-9bd2-dbb7db022f67] Recommendation: No war content detected.

2025-12-16T18:15:48.910954Z  INFO ⏱️  [cc0b51d4-f557-483f-9bd2-dbb7db022f67] Timing: Total=1376ms (OCR=1018ms, LLM=358ms)

2025-12-16T18:15:48.910959Z  INFO ✅ [cc0b51d4-f557-483f-9bd2-dbb7db022f67] ═══════════════════════════════════════

2025-12-16T18:15:49.202882Z  INFO 📸 [f7221327-0a32-46e0-bfa6-a6f0aefd99bd] Received screen capture: 720x1600

2025-12-16T18:15:49.951531Z  INFO 👁️  [f7221327-0a32-46e0-bfa6-a6f0aefd99bd] OCR complete: 300 chars extracted (748ms)

2025-12-16T18:15:49.951646Z  INFO 🔄 [f7221327-0a32-46e0-bfa6-a6f0aefd99bd] Text filtered & normalized: 300 -> 300 -> 280 chars

2025-12-16T18:15:49.951693Z  INFO 🔑 [f7221327-0a32-46e0-bfa6-a6f0aefd99bd] Text hash: 92443746662102a7

2025-12-16T18:15:49.951710Z  INFO 🔍 [f7221327-0a32-46e0-bfa6-a6f0aefd99bd] Cache MISS - analyzing with AI

2025-12-16T18:15:50.351206Z  INFO 🧠 [f7221327-0a32-46e0-bfa6-a6f0aefd99bd] Classification complete: safe_content (95.00% confidence) (399ms) | Tokens: 335 in, 98 out, 433 total

2025-12-16T18:15:50.351288Z  INFO 💾 [f7221327-0a32-46e0-bfa6-a6f0aefd99bd] Result cached for future requests

2025-12-16T18:15:50.351303Z  INFO ✅ [f7221327-0a32-46e0-bfa6-a6f0aefd99bd] ═══════════════════════════════════════

2025-12-16T18:15:50.351319Z  INFO ✅ [f7221327-0a32-46e0-bfa6-a6f0aefd99bd] ANALYSIS COMPLETE

2025-12-16T18:15:50.351331Z  INFO ✅ [f7221327-0a32-46e0-bfa6-a6f0aefd99bd] ═══════════════════════════════════════

2025-12-16T18:15:50.351346Z  INFO 📝 [f7221327-0a32-46e0-bfa6-a6f0aefd99bd] Extracted Text (300 chars):

2025-12-16T18:15:50.351378Z  INFO    "on Text Detection

20:15 2

Capture Interval: 1000ms | Validation: ON|

Cache: ON

Test Single Extraction

18859

Stop Capture

Clear All Data

Live Performance Stats

0

Total Captures

0

Successful

Oms

Avg Tim..."

2025-12-16T18:15:50.351408Z  INFO 🏷️  [f7221327-0a32-46e0-bfa6-a6f0aefd99bd] Category: safe_content

2025-12-16T18:15:50.351423Z  INFO 📊 [f7221327-0a32-46e0-bfa6-a6f0aefd99bd] Confidence: 95.0%

2025-12-16T18:15:50.351438Z  INFO ⚠️  [f7221327-0a32-46e0-bfa6-a6f0aefd99bd] Harmful: NO ✅

2025-12-16T18:15:50.351453Z  INFO 🎯 [f7221327-0a32-46e0-bfa6-a6f0aefd99bd] Action: CONTINUE

2025-12-16T18:15:50.351469Z  INFO 💡 [f7221327-0a32-46e0-bfa6-a6f0aefd99bd] Recommendation: No war content detected.

2025-12-16T18:15:50.351484Z  INFO ⏱️  [f7221327-0a32-46e0-bfa6-a6f0aefd99bd] Timing: Total=1148ms (OCR=748ms, LLM=399ms)

2025-12-16T18:15:50.351502Z  INFO ✅ [f7221327-0a32-46e0-bfa6-a6f0aefd99bd] ═══════════════════════════════════════

2025-12-16T18:15:50.706920Z  INFO 📸 [a80aa006-3338-4236-9c12-217cd01c3f4a] Received screen capture: 720x1600

2025-12-16T18:15:51.656641Z  INFO 👁️  [a80aa006-3338-4236-9c12-217cd01c3f4a] OCR complete: 300 chars extracted (949ms)

2025-12-16T18:15:51.656759Z  INFO 🔄 [a80aa006-3338-4236-9c12-217cd01c3f4a] Text filtered & normalized: 300 -> 300 -> 280 chars

2025-12-16T18:15:51.656805Z  INFO 🔑 [a80aa006-3338-4236-9c12-217cd01c3f4a] Text hash: 92443746662102a7

2025-12-16T18:15:51.656823Z  INFO 💾 [a80aa006-3338-4236-9c12-217cd01c3f4a] Cache HIT - returning cached result

2025-12-16T18:15:52.035014Z  INFO 📸 [ca972ea4-9fcc-47db-8fd1-587b7e8c9395] Received screen capture: 720x1600

2025-12-16T18:15:52.882370Z  INFO 👁️  [ca972ea4-9fcc-47db-8fd1-587b7e8c9395] OCR complete: 300 chars extracted (847ms)

2025-12-16T18:15:52.882436Z  INFO 🔄 [ca972ea4-9fcc-47db-8fd1-587b7e8c9395] Text filtered & normalized: 300 -> 300 -> 280 chars

2025-12-16T18:15:52.882463Z  INFO 🔑 [ca972ea4-9fcc-47db-8fd1-587b7e8c9395] Text hash: 92443746662102a7

2025-12-16T18:15:52.882474Z  INFO 💾 [ca972ea4-9fcc-47db-8fd1-587b7e8c9395] Cache HIT - returning cached result

2025-12-16T18:15:53.278827Z  INFO 📸 [6ee5bd6f-7300-4b82-b38b-01a495e6f3fa] Received screen capture: 720x1600

2025-12-16T18:15:54.122175Z  INFO 👁️  [6ee5bd6f-7300-4b82-b38b-01a495e6f3fa] OCR complete: 300 chars extracted (843ms)

2025-12-16T18:15:54.122241Z  INFO 🔄 [6ee5bd6f-7300-4b82-b38b-01a495e6f3fa] Text filtered & normalized: 300 -> 300 -> 280 chars

2025-12-16T18:15:54.122266Z  INFO 🔑 [6ee5bd6f-7300-4b82-b38b-01a495e6f3fa] Text hash: 92443746662102a7

2025-12-16T18:15:54.122276Z  INFO 💾 [6ee5bd6f-7300-4b82-b38b-01a495e6f3fa] Cache HIT - returning cached result

2025-12-16T18:15:54.499571Z  INFO 📸 [c6286df0-49f0-4cec-9d10-ac9015012f0c] Received screen capture: 720x1600

2025-12-16T18:15:55.921628Z  INFO 👁️  [c6286df0-49f0-4cec-9d10-ac9015012f0c] OCR complete: 300 chars extracted (1421ms)

2025-12-16T18:15:55.921694Z  INFO 🔄 [c6286df0-49f0-4cec-9d10-ac9015012f0c] Text filtered & normalized: 300 -> 300 -> 280 chars

2025-12-16T18:15:55.921718Z  INFO 🔑 [c6286df0-49f0-4cec-9d10-ac9015012f0c] Text hash: 92443746662102a7

2025-12-16T18:15:55.921726Z  INFO 💾 [c6286df0-49f0-4cec-9d10-ac9015012f0c] Cache HIT - returning cached result

2025-12-16T18:15:56.217496Z  INFO 📸 [0ef17964-0c85-4d57-afa1-52935b202289] Received screen capture: 720x1600

2025-12-16T18:15:56.907895Z  INFO 👁️  [0ef17964-0c85-4d57-afa1-52935b202289] OCR complete: 300 chars extracted (690ms)

2025-12-16T18:15:56.907970Z  INFO 🔄 [0ef17964-0c85-4d57-afa1-52935b202289] Text filtered & normalized: 300 -> 300 -> 280 chars

2025-12-16T18:15:56.907996Z  INFO 🔑 [0ef17964-0c85-4d57-afa1-52935b202289] Text hash: 92443746662102a7

2025-12-16T18:15:56.908006Z  INFO 💾 [0ef17964-0c85-4d57-afa1-52935b202289] Cache HIT - returning cached result

2025-12-16T18:15:57.355480Z  INFO 📸 [d8c60efb-d580-42f0-a5a6-772e67db7395] Received screen capture: 720x1600

2025-12-16T18:15:58.767574Z  INFO 👁️  [d8c60efb-d580-42f0-a5a6-772e67db7395] OCR complete: 300 chars extracted (1412ms)

2025-12-16T18:15:58.767715Z  INFO 🔄 [d8c60efb-d580-42f0-a5a6-772e67db7395] Text filtered & normalized: 300 -> 300 -> 280 chars

2025-12-16T18:15:58.767766Z  INFO 🔑 [d8c60efb-d580-42f0-a5a6-772e67db7395] Text hash: 92443746662102a7

2025-12-16T18:15:58.767787Z  INFO 💾 [d8c60efb-d580-42f0-a5a6-772e67db7395] Cache HIT - returning cached result

2025-12-16T18:15:59.192420Z  INFO 📸 [d6c5a3a0-d75e-4c4c-a036-050babd0b551] Received screen capture: 720x1600

2025-12-16T18:15:59.889874Z  INFO 👁️  [d6c5a3a0-d75e-4c4c-a036-050babd0b551] OCR complete: 300 chars extracted (697ms)

2025-12-16T18:15:59.890021Z  INFO 🔄 [d6c5a3a0-d75e-4c4c-a036-050babd0b551] Text filtered & normalized: 300 -> 300 -> 280 chars

2025-12-16T18:15:59.890065Z  INFO 🔑 [d6c5a3a0-d75e-4c4c-a036-050babd0b551] Text hash: 92443746662102a7

2025-12-16T18:15:59.890082Z  INFO 💾 [d6c5a3a0-d75e-4c4c-a036-050babd0b551] Cache HIT - returning cached result

2025-12-16T18:16:00.327855Z  INFO 📸 [c05645a8-89f8-4178-b1dc-d3e6e3cb07f4] Received screen capture: 720x1600

2025-12-16T18:16:01.049323Z  INFO 👁️  [c05645a8-89f8-4178-b1dc-d3e6e3cb07f4] OCR complete: 300 chars extracted (721ms)

2025-12-16T18:16:01.049460Z  INFO 🔄 [c05645a8-89f8-4178-b1dc-d3e6e3cb07f4] Text filtered & normalized: 300 -> 300 -> 280 chars

2025-12-16T18:16:01.049521Z  INFO 🔑 [c05645a8-89f8-4178-b1dc-d3e6e3cb07f4] Text hash: 92443746662102a7

2025-12-16T18:16:01.049544Z  INFO 💾 [c05645a8-89f8-4178-b1dc-d3e6e3cb07f4] Cache HIT - returning cached result

2025-12-16T18:16:01.471489Z  INFO 📸 [c90a7f90-6592-485c-8119-e5ac6cd954e2] Received screen capture: 720x1600

2025-12-16T18:16:02.180424Z  INFO 👁️  [c90a7f90-6592-485c-8119-e5ac6cd954e2] OCR complete: 300 chars extracted (708ms)

2025-12-16T18:16:02.180542Z  INFO 🔄 [c90a7f90-6592-485c-8119-e5ac6cd954e2] Text filtered & normalized: 300 -> 300 -> 280 chars

2025-12-16T18:16:02.180587Z  INFO 🔑 [c90a7f90-6592-485c-8119-e5ac6cd954e2] Text hash: 92443746662102a7

2025-12-16T18:16:02.180606Z  INFO 💾 [c90a7f90-6592-485c-8119-e5ac6cd954e2] Cache HIT - returning cached result

2025-12-16T18:16:02.573221Z  INFO 📸 [902383dd-5db7-48d7-a8f8-6b089cd09bf3] Received screen capture: 720x1600

2025-12-16T18:16:03.294622Z  INFO 👁️  [902383dd-5db7-48d7-a8f8-6b089cd09bf3] OCR complete: 300 chars extracted (721ms)

2025-12-16T18:16:03.294764Z  INFO 🔄 [902383dd-5db7-48d7-a8f8-6b089cd09bf3] Text filtered & normalized: 300 -> 300 -> 280 chars

2025-12-16T18:16:03.294818Z  INFO 🔑 [902383dd-5db7-48d7-a8f8-6b089cd09bf3] Text hash: 92443746662102a7

2025-12-16T18:16:03.294841Z  INFO 💾 [902383dd-5db7-48d7-a8f8-6b089cd09bf3] Cache HIT - returning cached result

2025-12-16T18:16:03.497799Z  INFO 📸 [5a7432cc-8a76-4980-a50c-766775726e20] Received screen capture: 720x1600

2025-12-16T18:16:04.378553Z  INFO 👁️  [5a7432cc-8a76-4980-a50c-766775726e20] OCR complete: 300 chars extracted (880ms)

2025-12-16T18:16:04.378611Z  INFO 🔄 [5a7432cc-8a76-4980-a50c-766775726e20] Text filtered & normalized: 300 -> 300 -> 280 chars

2025-12-16T18:16:04.378633Z  INFO 🔑 [5a7432cc-8a76-4980-a50c-766775726e20] Text hash: 92443746662102a7

2025-12-16T18:16:04.378642Z  INFO 💾 [5a7432cc-8a76-4980-a50c-766775726e20] Cache HIT - returning cached result

2025-12-16T18:16:04.622581Z  INFO 📸 [c2bcb249-0ed3-4945-a3bd-db54748df4f1] Received screen capture: 720x1600

2025-12-16T18:16:05.320880Z  INFO 👁️  [c2bcb249-0ed3-4945-a3bd-db54748df4f1] OCR complete: 300 chars extracted (698ms)

2025-12-16T18:16:05.320974Z  INFO 🔄 [c2bcb249-0ed3-4945-a3bd-db54748df4f1] Text filtered & normalized: 300 -> 300 -> 280 chars

2025-12-16T18:16:05.321009Z  INFO 🔑 [c2bcb249-0ed3-4945-a3bd-db54748df4f1] Text hash: 92443746662102a7

2025-12-16T18:16:05.321023Z  INFO 💾 [c2bcb249-0ed3-4945-a3bd-db54748df4f1] Cache HIT - returning cached result

2025-12-16T18:16:05.704504Z  INFO 📸 [2641b51f-b282-40a7-bc46-aa9494248037] Received screen capture: 720x1600

2025-12-16T18:16:06.496313Z  INFO 👁️  [2641b51f-b282-40a7-bc46-aa9494248037] OCR complete: 300 chars extracted (791ms)

2025-12-16T18:16:06.496409Z  INFO 🔄 [2641b51f-b282-40a7-bc46-aa9494248037] Text filtered & normalized: 300 -> 300 -> 280 chars

2025-12-16T18:16:06.496445Z  INFO 🔑 [2641b51f-b282-40a7-bc46-aa9494248037] Text hash: 92443746662102a7

2025-12-16T18:16:06.496468Z  INFO 💾 [2641b51f-b282-40a7-bc46-aa9494248037] Cache HIT - returning cached result

2025-12-16T18:16:06.899100Z  INFO 📸 [6a5139c5-612e-4fe8-a542-ea8a6a76b407] Received screen capture: 720x1600

2025-12-16T18:16:07.754570Z  INFO 👁️  [6a5139c5-612e-4fe8-a542-ea8a6a76b407] OCR complete: 300 chars extracted (855ms)

2025-12-16T18:16:07.754707Z  INFO 🔄 [6a5139c5-612e-4fe8-a542-ea8a6a76b407] Text filtered & normalized: 300 -> 300 -> 280 chars

2025-12-16T18:16:07.754756Z  INFO 🔑 [6a5139c5-612e-4fe8-a542-ea8a6a76b407] Text hash: 92443746662102a7

2025-12-16T18:16:07.754777Z  INFO 💾 [6a5139c5-612e-4fe8-a542-ea8a6a76b407] Cache HIT - returning cached result

2025-12-16T18:16:08.027641Z  INFO 📸 [2969f2fd-33c4-4c21-bf84-450c6b8b7f9f] Received screen capture: 720x1600

2025-12-16T18:16:09.054998Z  INFO 👁️  [2969f2fd-33c4-4c21-bf84-450c6b8b7f9f] OCR complete: 300 chars extracted (1027ms)

2025-12-16T18:16:09.055058Z  INFO 🔄 [2969f2fd-33c4-4c21-bf84-450c6b8b7f9f] Text filtered & normalized: 300 -> 300 -> 280 chars

2025-12-16T18:16:09.055082Z  INFO 🔑 [2969f2fd-33c4-4c21-bf84-450c6b8b7f9f] Text hash: 92443746662102a7

2025-12-16T18:16:09.055092Z  INFO 💾 [2969f2fd-33c4-4c21-bf84-450c6b8b7f9f] Cache HIT - returning cached result

2025-12-16T18:16:09.345164Z  INFO 📸 [5811d2af-240e-42bf-8efa-3baedf3e87c1] Received screen capture: 720x1600

2025-12-16T18:16:10.172521Z  INFO 👁️  [5811d2af-240e-42bf-8efa-3baedf3e87c1] OCR complete: 300 chars extracted (827ms)

2025-12-16T18:16:10.172684Z  INFO 🔄 [5811d2af-240e-42bf-8efa-3baedf3e87c1] Text filtered & normalized: 300 -> 300 -> 280 chars

2025-12-16T18:16:10.172744Z  INFO 🔑 [5811d2af-240e-42bf-8efa-3baedf3e87c1] Text hash: 92443746662102a7

2025-12-16T18:16:10.172766Z  INFO 💾 [5811d2af-240e-42bf-8efa-3baedf3e87c1] Cache HIT - returning cached result

2025-12-16T18:16:10.484589Z  INFO 📸 [74084f62-6fba-41f9-8923-20802a0eb8eb] Received screen capture: 720x1600

2025-12-16T18:16:11.299994Z  INFO 👁️  [74084f62-6fba-41f9-8923-20802a0eb8eb] OCR complete: 300 chars extracted (815ms)

2025-12-16T18:16:11.300052Z  INFO 🔄 [74084f62-6fba-41f9-8923-20802a0eb8eb] Text filtered & normalized: 300 -> 300 -> 280 chars

2025-12-16T18:16:11.300075Z  INFO 🔑 [74084f62-6fba-41f9-8923-20802a0eb8eb] Text hash: 92443746662102a7

2025-12-16T18:16:11.300085Z  INFO 💾 [74084f62-6fba-41f9-8923-20802a0eb8eb] Cache HIT - returning cached result

2025-12-16T18:16:11.623403Z  INFO 📸 [e92448d6-f899-4d30-a89c-cf6b04c49796] Received screen capture: 720x1600

2025-12-16T18:16:12.363495Z  INFO 👁️  [e92448d6-f899-4d30-a89c-cf6b04c49796] OCR complete: 300 chars extracted (740ms)

2025-12-16T18:16:12.363606Z  INFO 🔄 [e92448d6-f899-4d30-a89c-cf6b04c49796] Text filtered & normalized: 300 -> 300 -> 280 chars

2025-12-16T18:16:12.363645Z  INFO 🔑 [e92448d6-f899-4d30-a89c-cf6b04c49796] Text hash: 92443746662102a7

2025-12-16T18:16:12.363662Z  INFO 💾 [e92448d6-f899-4d30-a89c-cf6b04c49796] Cache HIT - returning cached result

2025-12-16T18:16:12.734941Z  INFO 📸 [570bec8d-e99d-4003-9e46-9a87c32cde51] Received screen capture: 720x1600

2025-12-16T18:16:13.520157Z  INFO 👁️  [570bec8d-e99d-4003-9e46-9a87c32cde51] OCR complete: 300 chars extracted (785ms)

2025-12-16T18:16:13.520276Z  INFO 🔄 [570bec8d-e99d-4003-9e46-9a87c32cde51] Text filtered & normalized: 300 -> 300 -> 280 chars

2025-12-16T18:16:13.520324Z  INFO 🔑 [570bec8d-e99d-4003-9e46-9a87c32cde51] Text hash: 92443746662102a7

2025-12-16T18:16:13.520343Z  INFO 💾 [570bec8d-e99d-4003-9e46-9a87c32cde51] Cache HIT - returning cached result

2025-12-16T18:16:13.861060Z  INFO 📸 [9cb11bc3-4276-4d40-8a9d-21cfcc661532] Received screen capture: 720x1600

2025-12-16T18:16:14.534606Z  INFO 👁️  [9cb11bc3-4276-4d40-8a9d-21cfcc661532] OCR complete: 300 chars extracted (673ms)

2025-12-16T18:16:14.534718Z  INFO 🔄 [9cb11bc3-4276-4d40-8a9d-21cfcc661532] Text filtered & normalized: 300 -> 300 -> 280 chars

2025-12-16T18:16:14.534760Z  INFO 🔑 [9cb11bc3-4276-4d40-8a9d-21cfcc661532] Text hash: 92443746662102a7

2025-12-16T18:16:14.534777Z  INFO 💾 [9cb11bc3-4276-4d40-8a9d-21cfcc661532] Cache HIT - returning cached result

2025-12-16T18:16:14.879938Z  INFO 📸 [ae341e48-f2d9-4196-bc38-6e01f2f94406] Received screen capture: 720x1600

2025-12-16T18:16:15.628722Z  INFO 👁️  [ae341e48-f2d9-4196-bc38-6e01f2f94406] OCR complete: 300 chars extracted (748ms)

2025-12-16T18:16:15.628829Z  INFO 🔄 [ae341e48-f2d9-4196-bc38-6e01f2f94406] Text filtered & normalized: 300 -> 300 -> 280 chars

2025-12-16T18:16:15.628872Z  INFO 🔑 [ae341e48-f2d9-4196-bc38-6e01f2f94406] Text hash: 92443746662102a7

2025-12-16T18:16:15.628894Z  INFO 💾 [ae341e48-f2d9-4196-bc38-6e01f2f94406] Cache HIT - returning cached result

2025-12-16T18:16:15.888358Z  INFO 📸 [995d9172-a9a4-40c0-a5e9-94156e0f322b] Received screen capture: 720x1600

2025-12-16T18:16:16.614091Z  INFO 👁️  [995d9172-a9a4-40c0-a5e9-94156e0f322b] OCR complete: 300 chars extracted (725ms)

2025-12-16T18:16:16.614151Z  INFO 🔄 [995d9172-a9a4-40c0-a5e9-94156e0f322b] Text filtered & normalized: 300 -> 300 -> 280 chars

2025-12-16T18:16:16.614174Z  INFO 🔑 [995d9172-a9a4-40c0-a5e9-94156e0f322b] Text hash: 92443746662102a7

2025-12-16T18:16:16.614182Z  INFO 💾 [995d9172-a9a4-40c0-a5e9-94156e0f322b] Cache HIT - returning cached result

2025-12-16T18:16:17.034506Z  INFO 📸 [a12c1132-cac4-4a0b-b4fe-6e8ab713a101] Received screen capture: 720x1600

2025-12-16T18:16:17.918272Z  INFO 👁️  [a12c1132-cac4-4a0b-b4fe-6e8ab713a101] OCR complete: 300 chars extracted (883ms)

2025-12-16T18:16:17.918389Z  INFO 🔄 [a12c1132-cac4-4a0b-b4fe-6e8ab713a101] Text filtered & normalized: 300 -> 300 -> 280 chars

2025-12-16T18:16:17.918439Z  INFO 🔑 [a12c1132-cac4-4a0b-b4fe-6e8ab713a101] Text hash: 92443746662102a7

2025-12-16T18:16:17.918463Z  INFO 💾 [a12c1132-cac4-4a0b-b4fe-6e8ab713a101] Cache HIT - returning cached result

2025-12-16T18:16:18.651065Z  INFO 📸 [80e665d5-0fbd-4484-ae2e-52018ed68502] Received screen capture: 720x1600

2025-12-16T18:16:19.481651Z  INFO 👁️  [80e665d5-0fbd-4484-ae2e-52018ed68502] OCR complete: 300 chars extracted (830ms)

2025-12-16T18:16:19.481713Z  INFO 🔄 [80e665d5-0fbd-4484-ae2e-52018ed68502] Text filtered & normalized: 300 -> 300 -> 280 chars

2025-12-16T18:16:19.481736Z  INFO 🔑 [80e665d5-0fbd-4484-ae2e-52018ed68502] Text hash: 92443746662102a7

2025-12-16T18:16:19.481746Z  INFO 💾 [80e665d5-0fbd-4484-ae2e-52018ed68502] Cache HIT - returning cached result

2025-12-16T18:16:19.886512Z  INFO 📸 [02b086ba-58dc-4a44-b7ea-1efb3611eef5] Received screen capture: 720x1600

2025-12-16T18:16:20.585858Z  INFO 👁️  [02b086ba-58dc-4a44-b7ea-1efb3611eef5] OCR complete: 300 chars extracted (699ms)

2025-12-16T18:16:20.585938Z  INFO 🔄 [02b086ba-58dc-4a44-b7ea-1efb3611eef5] Text filtered & normalized: 300 -> 300 -> 280 chars

2025-12-16T18:16:20.585968Z  INFO 🔑 [02b086ba-58dc-4a44-b7ea-1efb3611eef5] Text hash: 92443746662102a7

2025-12-16T18:16:20.585981Z  INFO 💾 [02b086ba-58dc-4a44-b7ea-1efb3611eef5] Cache HIT - returning cached result

2025-12-16T18:16:20.939123Z  INFO 📸 [ffc19f12-7c4c-42f7-9290-368e4542abd3] Received screen capture: 720x1600

2025-12-16T18:16:21.716168Z  INFO 👁️  [ffc19f12-7c4c-42f7-9290-368e4542abd3] OCR complete: 300 chars extracted (776ms)

2025-12-16T18:16:21.716253Z  INFO 🔄 [ffc19f12-7c4c-42f7-9290-368e4542abd3] Text filtered & normalized: 300 -> 300 -> 280 chars

2025-12-16T18:16:21.716288Z  INFO 🔑 [ffc19f12-7c4c-42f7-9290-368e4542abd3] Text hash: 92443746662102a7

2025-12-16T18:16:21.716303Z  INFO 💾 [ffc19f12-7c4c-42f7-9290-368e4542abd3] Cache HIT - returning cached result

2025-12-16T18:16:22.042572Z  INFO 📸 [5325a1de-2141-4aea-aa89-8bda1db5a284] Received screen capture: 720x1600

2025-12-16T18:16:22.717420Z  INFO 👁️  [5325a1de-2141-4aea-aa89-8bda1db5a284] OCR complete: 300 chars extracted (674ms)

2025-12-16T18:16:22.717502Z  INFO 🔄 [5325a1de-2141-4aea-aa89-8bda1db5a284] Text filtered & normalized: 300 -> 300 -> 280 chars

2025-12-16T18:16:22.717532Z  INFO 🔑 [5325a1de-2141-4aea-aa89-8bda1db5a284] Text hash: 92443746662102a7

2025-12-16T18:16:22.717545Z  INFO 💾 [5325a1de-2141-4aea-aa89-8bda1db5a284] Cache HIT - returning cached result

2025-12-16T18:16:22.981814Z  INFO 📸 [0d7a76c4-96e7-40b3-9426-38e073d91af4] Received screen capture: 720x1600

2025-12-16T18:16:23.986719Z  INFO 👁️  [0d7a76c4-96e7-40b3-9426-38e073d91af4] OCR complete: 300 chars extracted (1004ms)

2025-12-16T18:16:23.986782Z  INFO 🔄 [0d7a76c4-96e7-40b3-9426-38e073d91af4] Text filtered & normalized: 300 -> 300 -> 280 chars

2025-12-16T18:16:23.986806Z  INFO 🔑 [0d7a76c4-96e7-40b3-9426-38e073d91af4] Text hash: 92443746662102a7

2025-12-16T18:16:23.986816Z  INFO 💾 [0d7a76c4-96e7-40b3-9426-38e073d91af4] Cache HIT - returning cached result

2025-12-16T18:16:24.440248Z  INFO 📸 [7bfa31ba-bee9-4e7d-963f-263c8bc7ad6e] Received screen capture: 720x1600

2025-12-16T18:16:25.199936Z  INFO 👁️  [7bfa31ba-bee9-4e7d-963f-263c8bc7ad6e] OCR complete: 300 chars extracted (759ms)

2025-12-16T18:16:25.200042Z  INFO 🔄 [7bfa31ba-bee9-4e7d-963f-263c8bc7ad6e] Text filtered & normalized: 300 -> 300 -> 280 chars

2025-12-16T18:16:25.200081Z  INFO 🔑 [7bfa31ba-bee9-4e7d-963f-263c8bc7ad6e] Text hash: 92443746662102a7

2025-12-16T18:16:25.200097Z  INFO 💾 [7bfa31ba-bee9-4e7d-963f-263c8bc7ad6e] Cache HIT - returning cached result

2025-12-16T18:16:25.533930Z  INFO 📸 [f5b281e4-94f9-43d6-aca7-87c92f410ce6] Received screen capture: 720x1600

2025-12-16T18:16:26.591166Z  INFO 👁️  [f5b281e4-94f9-43d6-aca7-87c92f410ce6] OCR complete: 300 chars extracted (1057ms)

2025-12-16T18:16:26.591271Z  INFO 🔄 [f5b281e4-94f9-43d6-aca7-87c92f410ce6] Text filtered & normalized: 300 -> 300 -> 280 chars

2025-12-16T18:16:26.591313Z  INFO 🔑 [f5b281e4-94f9-43d6-aca7-87c92f410ce6] Text hash: 92443746662102a7

2025-12-16T18:16:26.591333Z  INFO 💾 [f5b281e4-94f9-43d6-aca7-87c92f410ce6] Cache HIT - returning cached result

2025-12-16T18:16:26.857667Z  INFO 📸 [a10de747-fcd2-41d5-b8f7-75c964d609a1] Received screen capture: 720x1600

2025-12-16T18:16:27.566616Z  INFO 👁️  [a10de747-fcd2-41d5-b8f7-75c964d609a1] OCR complete: 300 chars extracted (708ms)

2025-12-16T18:16:27.566675Z  INFO 🔄 [a10de747-fcd2-41d5-b8f7-75c964d609a1] Text filtered & normalized: 300 -> 300 -> 280 chars

2025-12-16T18:16:27.566698Z  INFO 🔑 [a10de747-fcd2-41d5-b8f7-75c964d609a1] Text hash: 92443746662102a7

2025-12-16T18:16:27.566707Z  INFO 💾 [a10de747-fcd2-41d5-b8f7-75c964d609a1] Cache HIT - returning cached result

2025-12-16T18:16:27.715494Z  INFO 📸 [26c82c19-0146-4392-b25b-498177d53aaa] Received screen capture: 720x1600

2025-12-16T18:16:28.497788Z  INFO 👁️  [26c82c19-0146-4392-b25b-498177d53aaa] OCR complete: 300 chars extracted (782ms)

2025-12-16T18:16:28.497915Z  INFO 🔄 [26c82c19-0146-4392-b25b-498177d53aaa] Text filtered & normalized: 300 -> 300 -> 280 chars

2025-12-16T18:16:28.497963Z  INFO 🔑 [26c82c19-0146-4392-b25b-498177d53aaa] Text hash: 92443746662102a7

2025-12-16T18:16:28.497984Z  INFO 💾 [26c82c19-0146-4392-b25b-498177d53aaa] Cache HIT - returning cached result

2025-12-16T18:16:28.785672Z  INFO 📸 [09aa0f36-9dfc-44f8-8e40-89bf08d67992] Received screen capture: 720x1600

2025-12-16T18:16:29.477810Z  INFO 👁️  [09aa0f36-9dfc-44f8-8e40-89bf08d67992] OCR complete: 300 chars extracted (692ms)

2025-12-16T18:16:29.477926Z  INFO 🔄 [09aa0f36-9dfc-44f8-8e40-89bf08d67992] Text filtered & normalized: 300 -> 300 -> 280 chars

2025-12-16T18:16:29.477967Z  INFO 🔑 [09aa0f36-9dfc-44f8-8e40-89bf08d67992] Text hash: 92443746662102a7

2025-12-16T18:16:29.477984Z  INFO 💾 [09aa0f36-9dfc-44f8-8e40-89bf08d67992] Cache HIT - returning cached result



📊 Monitoring logs (press Ctrl+C to stop):

════════════════════════════════════════

12-16 20:11:58.296 17816 17816 D AllotAccessibility: ✅ ═══════════════════════════════════════

12-16 20:11:58.296 17816 17816 D AllotAccessibility: ✅ ACCESSIBILITY SERVICE CONNECTED

12-16 20:11:58.296 17816 17816 D AllotAccessibility: ✅ ═══════════════════════════════════════

12-16 20:11:58.296 17816 17816 D AllotAccessibility: ✅ Service instance set: true

12-16 20:11:58.296 17816 17816 D AllotAccessibility: ✅ WindowManager ready: true

12-16 20:11:58.296 17816 17816 D AllotAccessibility: ✅ Service is now available for content blocking

12-16 20:11:58.296 17816 17816 D AllotAccessibility: ✅ ═══════════════════════════════════════

12-16 20:12:01.244 17816 17946 I ExpoModulesCore: ✅ AppContext was initialized

12-16 20:12:04.866 17816 18174 I ExpoModulesCore: ✅ JSI interop was installed

12-16 20:12:04.876 17816 18174 I ExpoModulesCore: ✅ Constants were exported

12-16 20:12:33.558 17816 17816 D AllotPermissions: ✅ Screen capture permission GRANTED - stored in holder

12-16 20:12:33.582 17816 19292 D ScreenCaptureModule: 🚀 Starting foreground service...

12-16 20:12:33.601 17816 17816 D ScreenCaptureService: ✅ ScreenCaptureService created

12-16 20:12:33.611 17816 17816 D ScreenCaptureService: 🚀 ScreenCaptureService started

12-16 20:12:33.642 17816 17816 D ScreenCaptureService: 🔄 Background processing started

12-16 20:12:34.100 17816 19292 D ScreenCaptureModule: ✅ MediaProjection created successfully

12-16 20:12:34.229 17816 17816 D ScreenCaptureModule: ✅ VirtualDisplay created successfully

12-16 20:12:34.229 17816 17816 D ScreenCaptureModule: ✅ Image capture setup completed

12-16 20:12:34.230 17816 17816 D ScreenCaptureModule: ✅ Service callbacks connected

12-16 20:12:34.232 17816 17816 D ScreenCaptureModule: ✅ Screen capture started successfully

12-16 20:12:34.240 17816 17816 V ScreenCaptureModule: 🎯 Frame requested by service

12-16 20:12:34.337 17816 17816 V ScreenCaptureModule: 🎯 Frame requested by service

12-16 20:12:34.349 17816 17816 D ScreenCaptureModule: 🎯 Processing requested frame

12-16 20:12:34.446 17816 17816 V ScreenCaptureModule: 🎯 Frame requested by service

12-16 20:12:34.459 17816 17816 D ScreenCaptureModule: 🎯 Processing requested frame

12-16 20:12:34.542 17816 17816 V ScreenCaptureModule: 🎯 Frame requested by service

12-16 20:12:34.594 17816 18181 D ScreenCaptureService: 📸 Frame captured: 720x1600

12-16 20:12:35.241 17816 18175 D ScreenCaptureModule: 🎯 Requesting next frame capture...

12-16 20:12:36.276 17816 17816 V ScreenCaptureModule: 🎯 Frame requested by service

12-16 20:12:36.330 17816 18182 D ScreenCaptureService: 📸 Frame captured: 720x1600

12-16 20:12:37.503 17816 17816 V ScreenCaptureModule: 🎯 Frame requested by service

12-16 20:12:37.555 17816 19298 D ScreenCaptureService: 📸 Frame captured: 720x1600

12-16 20:12:38.743 17816 17816 V ScreenCaptureModule: 🎯 Frame requested by service

12-16 20:12:38.798 17816 18181 D ScreenCaptureService: 📸 Frame captured: 720x1600

12-16 20:12:39.864 17816 17816 V ScreenCaptureModule: 🎯 Frame requested by service

12-16 20:12:39.919 17816 18182 D ScreenCaptureService: 📸 Frame captured: 720x1600

12-16 20:12:41.085 17816 17816 V ScreenCaptureModule: 🎯 Frame requested by service

12-16 20:12:41.137 17816 19298 D ScreenCaptureService: 📸 Frame captured: 720x1600

12-16 20:12:42.307 17816 17816 V ScreenCaptureModule: 🎯 Frame requested by service

12-16 20:12:42.359 17816 19298 D ScreenCaptureService: 📸 Frame captured: 720x1600

12-16 20:12:43.836 17816 17816 V ScreenCaptureModule: 🎯 Frame requested by service

12-16 20:12:43.891 17816 18182 D ScreenCaptureService: 📸 Frame captured: 720x1600

12-16 20:12:45.688 17816 17816 V ScreenCaptureModule: 🎯 Frame requested by service

12-16 20:12:45.742 17816 18182 D ScreenCaptureService: 📸 Frame captured: 720x1600

12-16 20:12:47.117 17816 17816 V ScreenCaptureModule: 🎯 Frame requested by service

12-16 20:12:47.170 17816 18181 D ScreenCaptureService: 📸 Frame captured: 720x1600

12-16 20:12:48.337 17816 17816 V ScreenCaptureModule: 🎯 Frame requested by service

12-16 20:12:48.388 17816 19298 D ScreenCaptureService: 📸 Frame captured: 720x1600

12-16 20:12:49.662 17816 17816 V ScreenCaptureModule: 🎯 Frame requested by service

12-16 20:12:49.715 17816 18182 D ScreenCaptureService: 📸 Frame captured: 720x1600

12-16 20:12:50.787 17816 17816 V ScreenCaptureModule: 🎯 Frame requested by service

12-16 20:12:50.841 17816 18182 D ScreenCaptureService: 📸 Frame captured: 720x1600

12-16 20:12:52.026 17816 17816 V ScreenCaptureModule: 🎯 Frame requested by service

12-16 20:12:52.081 17816 19298 D ScreenCaptureService: 📸 Frame captured: 720x1600

12-16 20:12:53.147 17816 17816 D AllotPermissions: ✅ Screen capture permission GRANTED - stored in holder

12-16 20:12:53.149 17816 18174 I ReactNativeJS: 📸 Starting screen capture...

12-16 20:12:53.187 17816 17816 D LocalTextExtractionService: ✅ LocalTextExtractionService created

12-16 20:12:53.198 17816 17816 D LocalTextExtractionService: 🔋 WakeLock acquired

12-16 20:12:53.200 17816 17816 D LocalTextExtractionService: 🚀 LocalTextExtractionService started

12-16 20:12:53.208 17816 17816 D LocalTextExtractionService: 🔧 Configuration: validation=true, caching=true, roi=true, interval=1000ms

12-16 20:12:53.208 17816 17816 D LocalTextExtractionService: 🔄 Background processing started

12-16 20:12:53.317 17816 17816 V ScreenCaptureModule: 🎯 Frame requested by service

12-16 20:12:53.369 17816 19298 D ScreenCaptureService: 📸 Frame captured: 720x1600

12-16 20:12:53.671 17816 18182 D LocalTextExtractionModule: ✅ Screen capture callbacks connected to LocalTextExtractionService

12-16 20:12:53.671 17816 18182 D LocalTextExtractionService: 🎬 Starting local text extraction loop (1000ms interval)

12-16 20:12:53.671 17816 18182 D LocalTextExtractionService: 🤖 Local ML Kit: ENABLED

12-16 20:12:53.671 17816 18182 D LocalTextExtractionService: 🛡️ Validation: ENABLED

12-16 20:12:53.671 17816 18182 D LocalTextExtractionService: 💾 Caching: ENABLED

12-16 20:12:53.677 17816 18174 I ReactNativeJS: ✅ Local text extraction service started

12-16 20:12:53.677 17816 17816 V ScreenCaptureModule: 🎯 Frame requested by service

12-16 20:12:53.858 17816 18182 D LocalTextExtractionService: 📸 Frame captured: 720x1600

12-16 20:12:54.619 17816 19298 D LocalTextExtractionService: 

12-16 20:12:54.619 17816 19298 D LocalTextExtractionService: 🔍 ═══════════════════════════════════════

12-16 20:12:54.620 17816 19298 D LocalTextExtractionService: 🔍 LOCAL ML TEXT EXTRACTION RESULT

12-16 20:12:54.620 17816 19298 D LocalTextExtractionService: 🔍 ═══════════════════════════════════════

12-16 20:12:54.620 17816 19298 D LocalTextExtractionService: 📱 Frame: 720x1600

12-16 20:12:54.620 17816 19298 D LocalTextExtractionService: 📝 Extracted Text: ""

12-16 20:12:54.620 17816 19298 D LocalTextExtractionService: 📊 Confidence: 0%

12-16 20:12:54.621 17816 19298 D LocalTextExtractionService: 📏 Text Density: 0%

12-16 20:12:54.621 17816 19298 D LocalTextExtractionService: ⏱️ ML Processing Time: 678ms

12-16 20:12:54.621 17816 19298 D LocalTextExtractionService: ⏱️ Total Time (capture + ML): 760ms

12-16 20:12:54.621 17816 19298 D LocalTextExtractionService: 🎯 Text Regions: 0

12-16 20:12:54.622 17816 19298 D LocalTextExtractionService: 💾 Used Cache: No

12-16 20:12:54.622 17816 19298 D LocalTextExtractionService: 🎯 ROI Detected: No

12-16 20:12:54.622 17816 19298 D LocalTextExtractionService: ✅ Validation Passed: No

12-16 20:12:54.623 17816 19298 D LocalTextExtractionService: 🔄 Fallback Used: No

12-16 20:12:54.623 17816 19298 D LocalTextExtractionService: ⭐ High Quality: No

12-16 20:12:54.623 17816 19298 D LocalTextExtractionService: 🚀 Performance: 760ms (vs 200-500ms for Google Vision API)

12-16 20:12:54.623 17816 19298 D LocalTextExtractionService: 💰 Cost: FREE (vs ~$1.50 per 1000 requests for Google Vision)

12-16 20:12:54.623 17816 19298 D LocalTextExtractionService: 🔒 Privacy: ON-DEVICE (vs cloud processing)

12-16 20:12:54.623 17816 19298 D LocalTextExtractionService: 🔍 ═══════════════════════════════════════

12-16 20:12:54.624 17816 19298 D LocalTextExtractionService: 

12-16 20:12:54.668 17816 17816 V ScreenCaptureModule: 🎯 Frame requested by service

12-16 20:12:54.680 17816 17816 V ScreenCaptureModule: 🎯 Frame requested by service

12-16 20:12:54.721 17816 20004 D ScreenCaptureService: 📸 Frame captured: 720x1600

12-16 20:12:54.732 17816 19298 D LocalTextExtractionService: 📸 Frame captured: 720x1600

12-16 20:12:54.811 17816 19298 D LocalTextExtractionService: 

12-16 20:12:54.811 17816 19298 D LocalTextExtractionService: 🔍 ═══════════════════════════════════════

12-16 20:12:54.812 17816 19298 D LocalTextExtractionService: 🔍 LOCAL ML TEXT EXTRACTION RESULT

12-16 20:12:54.812 17816 19298 D LocalTextExtractionService: 🔍 ═══════════════════════════════════════

12-16 20:12:54.812 17816 19298 D LocalTextExtractionService: 📱 Frame: 720x1600

12-16 20:12:54.812 17816 19298 D LocalTextExtractionService: 📝 Extracted Text: ""

12-16 20:12:54.812 17816 19298 D LocalTextExtractionService: 📊 Confidence: 0%

12-16 20:12:54.812 17816 19298 D LocalTextExtractionService: 📏 Text Density: 0%

12-16 20:12:54.812 17816 19298 D LocalTextExtractionService: ⏱️ ML Processing Time: 59ms

12-16 20:12:54.812 17816 19298 D LocalTextExtractionService: ⏱️ Total Time (capture + ML): 79ms

12-16 20:12:54.812 17816 19298 D LocalTextExtractionService: 🎯 Text Regions: 0

12-16 20:12:54.812 17816 19298 D LocalTextExtractionService: 💾 Used Cache: No

12-16 20:12:54.812 17816 19298 D LocalTextExtractionService: 🎯 ROI Detected: No

12-16 20:12:54.812 17816 19298 D LocalTextExtractionService: ✅ Validation Passed: No

12-16 20:12:54.812 17816 19298 D LocalTextExtractionService: 🔄 Fallback Used: No

12-16 20:12:54.812 17816 19298 D LocalTextExtractionService: ⭐ High Quality: No

12-16 20:12:54.812 17816 19298 D LocalTextExtractionService: 🚀 Performance: 79ms (vs 200-500ms for Google Vision API)

12-16 20:12:54.812 17816 19298 D LocalTextExtractionService: 💰 Cost: FREE (vs ~$1.50 per 1000 requests for Google Vision)

12-16 20:12:54.812 17816 19298 D LocalTextExtractionService: 🔒 Privacy: ON-DEVICE (vs cloud processing)

12-16 20:12:54.812 17816 19298 D LocalTextExtractionService: 🔍 ═══════════════════════════════════════

12-16 20:12:54.812 17816 19298 D LocalTextExtractionService: 

12-16 20:12:55.685 17816 17816 V ScreenCaptureModule: 🎯 Frame requested by service

12-16 20:12:55.738 17816 18182 D LocalTextExtractionService: 📸 Frame captured: 720x1600

12-16 20:12:55.838 17816 18182 D LocalTextExtractionService: 

12-16 20:12:55.838 17816 18182 D LocalTextExtractionService: 🔍 ═══════════════════════════════════════

12-16 20:12:55.838 17816 18182 D LocalTextExtractionService: 🔍 LOCAL ML TEXT EXTRACTION RESULT

12-16 20:12:55.838 17816 18182 D LocalTextExtractionService: 🔍 ═══════════════════════════════════════

12-16 20:12:55.838 17816 18182 D LocalTextExtractionService: 📱 Frame: 720x1600

12-16 20:12:55.839 17816 18182 D LocalTextExtractionService: 📝 Extracted Text: ""

12-16 20:12:55.839 17816 18182 D LocalTextExtractionService: 📊 Confidence: 0%

12-16 20:12:55.839 17816 18182 D LocalTextExtractionService: 📏 Text Density: 0%

12-16 20:12:55.839 17816 18182 D LocalTextExtractionService: ⏱️ ML Processing Time: 66ms

12-16 20:12:55.839 17816 18182 D LocalTextExtractionService: ⏱️ Total Time (capture + ML): 100ms

12-16 20:12:55.839 17816 18182 D LocalTextExtractionService: 🎯 Text Regions: 0

12-16 20:12:55.839 17816 18182 D LocalTextExtractionService: 💾 Used Cache: No

12-16 20:12:55.839 17816 18182 D LocalTextExtractionService: 🎯 ROI Detected: No

12-16 20:12:55.839 17816 18182 D LocalTextExtractionService: ✅ Validation Passed: No

12-16 20:12:55.839 17816 18182 D LocalTextExtractionService: 🔄 Fallback Used: No

12-16 20:12:55.839 17816 18182 D LocalTextExtractionService: ⭐ High Quality: No

12-16 20:12:55.839 17816 18182 D LocalTextExtractionService: 🚀 Performance: 100ms (vs 200-500ms for Google Vision API)

12-16 20:12:55.839 17816 18182 D LocalTextExtractionService: 💰 Cost: FREE (vs ~$1.50 per 1000 requests for Google Vision)

12-16 20:12:55.839 17816 18182 D LocalTextExtractionService: 🔒 Privacy: ON-DEVICE (vs cloud processing)

12-16 20:12:55.839 17816 18182 D LocalTextExtractionService: 🔍 ═══════════════════════════════════════

12-16 20:12:55.839 17816 18182 D LocalTextExtractionService: 

12-16 20:12:55.998 17816 17816 V ScreenCaptureModule: 🎯 Frame requested by service

12-16 20:12:56.052 17816 18182 D ScreenCaptureService: 📸 Frame captured: 720x1600

12-16 20:12:56.689 17816 17816 V ScreenCaptureModule: 🎯 Frame requested by service

12-16 20:12:56.742 17816 20004 D LocalTextExtractionService: 📸 Frame captured: 720x1600

12-16 20:12:56.838 17816 20004 D LocalTextExtractionService: 

12-16 20:12:56.838 17816 20004 D LocalTextExtractionService: 🔍 ═══════════════════════════════════════

12-16 20:12:56.838 17816 20004 D LocalTextExtractionService: 🔍 LOCAL ML TEXT EXTRACTION RESULT

12-16 20:12:56.838 17816 20004 D LocalTextExtractionService: 🔍 ═══════════════════════════════════════

12-16 20:12:56.839 17816 20004 D LocalTextExtractionService: 📱 Frame: 720x1600

12-16 20:12:56.839 17816 20004 D LocalTextExtractionService: 📝 Extracted Text: ""

12-16 20:12:56.839 17816 20004 D LocalTextExtractionService: 📊 Confidence: 0%

12-16 20:12:56.839 17816 20004 D LocalTextExtractionService: 📏 Text Density: 0%

12-16 20:12:56.839 17816 20004 D LocalTextExtractionService: ⏱️ ML Processing Time: 61ms

12-16 20:12:56.839 17816 20004 D LocalTextExtractionService: ⏱️ Total Time (capture + ML): 96ms

12-16 20:12:56.839 17816 20004 D LocalTextExtractionService: 🎯 Text Regions: 0

12-16 20:12:56.839 17816 20004 D LocalTextExtractionService: 💾 Used Cache: No

12-16 20:12:56.839 17816 20004 D LocalTextExtractionService: 🎯 ROI Detected: No

12-16 20:12:56.840 17816 20004 D LocalTextExtractionService: ✅ Validation Passed: No

12-16 20:12:56.840 17816 20004 D LocalTextExtractionService: 🔄 Fallback Used: No

12-16 20:12:56.840 17816 20004 D LocalTextExtractionService: ⭐ High Quality: No

12-16 20:12:56.840 17816 20004 D LocalTextExtractionService: 🚀 Performance: 96ms (vs 200-500ms for Google Vision API)

12-16 20:12:56.840 17816 20004 D LocalTextExtractionService: 💰 Cost: FREE (vs ~$1.50 per 1000 requests for Google Vision)

12-16 20:12:56.840 17816 20004 D LocalTextExtractionService: 🔒 Privacy: ON-DEVICE (vs cloud processing)

12-16 20:12:56.840 17816 20004 D LocalTextExtractionService: 🔍 ═══════════════════════════════════════

12-16 20:12:56.840 17816 20004 D LocalTextExtractionService: 

12-16 20:12:57.227 17816 17816 V ScreenCaptureModule: 🎯 Frame requested by service

12-16 20:12:57.280 17816 20004 D ScreenCaptureService: 📸 Frame captured: 720x1600

12-16 20:12:57.698 17816 17816 V ScreenCaptureModule: 🎯 Frame requested by service

12-16 20:12:57.751 17816 18182 D LocalTextExtractionService: 📸 Frame captured: 720x1600

12-16 20:12:57.857 17816 18182 D LocalTextExtractionService: 

12-16 20:12:57.857 17816 18182 D LocalTextExtractionService: 🔍 ═══════════════════════════════════════

12-16 20:12:57.857 17816 18182 D LocalTextExtractionService: 🔍 LOCAL ML TEXT EXTRACTION RESULT

12-16 20:12:57.858 17816 18182 D LocalTextExtractionService: 🔍 ═══════════════════════════════════════

12-16 20:12:57.858 17816 18182 D LocalTextExtractionService: 📱 Frame: 720x1600

12-16 20:12:57.858 17816 18182 D LocalTextExtractionService: 📝 Extracted Text: ""

12-16 20:12:57.858 17816 18182 D LocalTextExtractionService: 📊 Confidence: 0%

12-16 20:12:57.858 17816 18182 D LocalTextExtractionService: 📏 Text Density: 0%

12-16 20:12:57.858 17816 18182 D LocalTextExtractionService: ⏱️ ML Processing Time: 68ms

12-16 20:12:57.858 17816 18182 D LocalTextExtractionService: ⏱️ Total Time (capture + ML): 105ms

12-16 20:12:57.858 17816 18182 D LocalTextExtractionService: 🎯 Text Regions: 0

12-16 20:12:57.858 17816 18182 D LocalTextExtractionService: 💾 Used Cache: No

12-16 20:12:57.858 17816 18182 D LocalTextExtractionService: 🎯 ROI Detected: No

12-16 20:12:57.858 17816 18182 D LocalTextExtractionService: ✅ Validation Passed: No

12-16 20:12:57.858 17816 18182 D LocalTextExtractionService: 🔄 Fallback Used: No

12-16 20:12:57.858 17816 18182 D LocalTextExtractionService: ⭐ High Quality: No

12-16 20:12:57.859 17816 18182 D LocalTextExtractionService: 🚀 Performance: 105ms (vs 200-500ms for Google Vision API)

12-16 20:12:57.859 17816 18182 D LocalTextExtractionService: 💰 Cost: FREE (vs ~$1.50 per 1000 requests for Google Vision)

12-16 20:12:57.859 17816 18182 D LocalTextExtractionService: 🔒 Privacy: ON-DEVICE (vs cloud processing)

12-16 20:12:57.859 17816 18182 D LocalTextExtractionService: 🔍 ═══════════════════════════════════════

12-16 20:12:57.859 17816 18182 D LocalTextExtractionService: 

12-16 20:12:58.358 17816 17816 V ScreenCaptureModule: 🎯 Frame requested by service

12-16 20:12:58.410 17816 18182 D ScreenCaptureService: 📸 Frame captured: 720x1600

12-16 20:12:58.700 17816 17816 V ScreenCaptureModule: 🎯 Frame requested by service

12-16 20:12:58.753 17816 20004 D LocalTextExtractionService: 📸 Frame captured: 720x1600

12-16 20:12:58.853 17816 20004 D LocalTextExtractionService: 

12-16 20:12:58.854 17816 20004 D LocalTextExtractionService: 🔍 ═══════════════════════════════════════

12-16 20:12:58.854 17816 20004 D LocalTextExtractionService: 🔍 LOCAL ML TEXT EXTRACTION RESULT

12-16 20:12:58.854 17816 20004 D LocalTextExtractionService: 🔍 ═══════════════════════════════════════

12-16 20:12:58.854 17816 20004 D LocalTextExtractionService: 📱 Frame: 720x1600

12-16 20:12:58.854 17816 20004 D LocalTextExtractionService: 📝 Extracted Text: ""

12-16 20:12:58.854 17816 20004 D LocalTextExtractionService: 📊 Confidence: 0%

12-16 20:12:58.854 17816 20004 D LocalTextExtractionService: 📏 Text Density: 0%

12-16 20:12:58.854 17816 20004 D LocalTextExtractionService: ⏱️ ML Processing Time: 63ms

12-16 20:12:58.855 17816 20004 D LocalTextExtractionService: ⏱️ Total Time (capture + ML): 99ms

12-16 20:12:58.855 17816 20004 D LocalTextExtractionService: 🎯 Text Regions: 0

12-16 20:12:58.855 17816 20004 D LocalTextExtractionService: 💾 Used Cache: No

12-16 20:12:58.855 17816 20004 D LocalTextExtractionService: 🎯 ROI Detected: No

12-16 20:12:58.856 17816 20004 D LocalTextExtractionService: ✅ Validation Passed: No

12-16 20:12:58.856 17816 20004 D LocalTextExtractionService: 🔄 Fallback Used: No

12-16 20:12:58.856 17816 20004 D LocalTextExtractionService: ⭐ High Quality: No

12-16 20:12:58.857 17816 20004 D LocalTextExtractionService: 🚀 Performance: 99ms (vs 200-500ms for Google Vision API)

12-16 20:12:58.857 17816 20004 D LocalTextExtractionService: 💰 Cost: FREE (vs ~$1.50 per 1000 requests for Google Vision)

12-16 20:12:58.857 17816 20004 D LocalTextExtractionService: 🔒 Privacy: ON-DEVICE (vs cloud processing)

12-16 20:12:58.857 17816 20004 D LocalTextExtractionService: 🔍 ═══════════════════════════════════════

12-16 20:12:58.857 17816 20004 D LocalTextExtractionService: 

12-16 20:12:59.706 17816 17816 V ScreenCaptureModule: 🎯 Frame requested by service

12-16 20:12:59.760 17816 19298 D LocalTextExtractionService: 📸 Frame captured: 720x1600

12-16 20:12:59.870 17816 19298 D LocalTextExtractionService: 

12-16 20:12:59.870 17816 19298 D LocalTextExtractionService: 🔍 ═══════════════════════════════════════

12-16 20:12:59.870 17816 19298 D LocalTextExtractionService: 🔍 LOCAL ML TEXT EXTRACTION RESULT

12-16 20:12:59.870 17816 19298 D LocalTextExtractionService: 🔍 ═══════════════════════════════════════

12-16 20:12:59.870 17816 19298 D LocalTextExtractionService: 📱 Frame: 720x1600

12-16 20:12:59.870 17816 19298 D LocalTextExtractionService: 📝 Extracted Text: ""

12-16 20:12:59.871 17816 19298 D LocalTextExtractionService: 📊 Confidence: 0%

12-16 20:12:59.871 17816 19298 D LocalTextExtractionService: 📏 Text Density: 0%

12-16 20:12:59.871 17816 19298 D LocalTextExtractionService: ⏱️ ML Processing Time: 70ms

12-16 20:12:59.871 17816 19298 D LocalTextExtractionService: ⏱️ Total Time (capture + ML): 109ms

12-16 20:12:59.871 17816 19298 D LocalTextExtractionService: 🎯 Text Regions: 0

12-16 20:12:59.871 17816 19298 D LocalTextExtractionService: 💾 Used Cache: No

12-16 20:12:59.871 17816 19298 D LocalTextExtractionService: 🎯 ROI Detected: No

12-16 20:12:59.871 17816 19298 D LocalTextExtractionService: ✅ Validation Passed: No

12-16 20:12:59.871 17816 19298 D LocalTextExtractionService: 🔄 Fallback Used: No

12-16 20:12:59.871 17816 19298 D LocalTextExtractionService: ⭐ High Quality: No

12-16 20:12:59.871 17816 19298 D LocalTextExtractionService: 🚀 Performance: 109ms (vs 200-500ms for Google Vision API)

12-16 20:12:59.871 17816 19298 D LocalTextExtractionService: 💰 Cost: FREE (vs ~$1.50 per 1000 requests for Google Vision)

12-16 20:12:59.871 17816 19298 D LocalTextExtractionService: 🔒 Privacy: ON-DEVICE (vs cloud processing)

12-16 20:12:59.871 17816 19298 D LocalTextExtractionService: 🔍 ═══════════════════════════════════════

12-16 20:12:59.871 17816 19298 D LocalTextExtractionService: 

12-16 20:12:59.897 17816 17816 V ScreenCaptureModule: 🎯 Frame requested by service

12-16 20:12:59.948 17816 19298 D ScreenCaptureService: 📸 Frame captured: 720x1600

12-16 20:13:00.710 17816 17816 V ScreenCaptureModule: 🎯 Frame requested by service

12-16 20:13:00.763 17816 18182 D LocalTextExtractionService: 📸 Frame captured: 720x1600

12-16 20:13:00.850 17816 18182 D LocalTextExtractionService: 

12-16 20:13:00.851 17816 18182 D LocalTextExtractionService: 🔍 ═══════════════════════════════════════

12-16 20:13:00.851 17816 18182 D LocalTextExtractionService: 🔍 LOCAL ML TEXT EXTRACTION RESULT

12-16 20:13:00.851 17816 18182 D LocalTextExtractionService: 🔍 ═══════════════════════════════════════

12-16 20:13:00.851 17816 18182 D LocalTextExtractionService: 📱 Frame: 720x1600

12-16 20:13:00.851 17816 18182 D LocalTextExtractionService: 📝 Extracted Text: ""

12-16 20:13:00.852 17816 18182 D LocalTextExtractionService: 📊 Confidence: 0%

12-16 20:13:00.852 17816 18182 D LocalTextExtractionService: 📏 Text Density: 0%

12-16 20:13:00.852 17816 18182 D LocalTextExtractionService: ⏱️ ML Processing Time: 69ms

12-16 20:13:00.852 17816 18182 D LocalTextExtractionService: ⏱️ Total Time (capture + ML): 87ms

12-16 20:13:00.852 17816 18182 D LocalTextExtractionService: 🎯 Text Regions: 0

12-16 20:13:00.852 17816 18182 D LocalTextExtractionService: 💾 Used Cache: No

12-16 20:13:00.852 17816 18182 D LocalTextExtractionService: 🎯 ROI Detected: No

12-16 20:13:00.852 17816 18182 D LocalTextExtractionService: ✅ Validation Passed: No

12-16 20:13:00.853 17816 18182 D LocalTextExtractionService: 🔄 Fallback Used: No

12-16 20:13:00.853 17816 18182 D LocalTextExtractionService: ⭐ High Quality: No

12-16 20:13:00.853 17816 18182 D LocalTextExtractionService: 🚀 Performance: 87ms (vs 200-500ms for Google Vision API)

12-16 20:13:00.853 17816 18182 D LocalTextExtractionService: 💰 Cost: FREE (vs ~$1.50 per 1000 requests for Google Vision)

12-16 20:13:00.853 17816 18182 D LocalTextExtractionService: 🔒 Privacy: ON-DEVICE (vs cloud processing)

12-16 20:13:00.853 17816 18182 D LocalTextExtractionService: 🔍 ═══════════════════════════════════════

12-16 20:13:00.853 17816 18182 D LocalTextExtractionService: 

12-16 20:13:01.137 17816 17816 V ScreenCaptureModule: 🎯 Frame requested by service

12-16 20:13:01.188 17816 18182 D ScreenCaptureService: 📸 Frame captured: 720x1600

12-16 20:13:01.718 17816 17816 V ScreenCaptureModule: 🎯 Frame requested by service

12-16 20:13:01.770 17816 20004 D LocalTextExtractionService: 📸 Frame captured: 720x1600

12-16 20:13:01.858 17816 20004 D LocalTextExtractionService: 

12-16 20:13:01.858 17816 20004 D LocalTextExtractionService: 🔍 ═══════════════════════════════════════

12-16 20:13:01.858 17816 20004 D LocalTextExtractionService: 🔍 LOCAL ML TEXT EXTRACTION RESULT

12-16 20:13:01.858 17816 20004 D LocalTextExtractionService: 🔍 ═══════════════════════════════════════

12-16 20:13:01.859 17816 20004 D LocalTextExtractionService: 📱 Frame: 720x1600

12-16 20:13:01.859 17816 20004 D LocalTextExtractionService: 📝 Extracted Text: ""

12-16 20:13:01.859 17816 20004 D LocalTextExtractionService: 📊 Confidence: 0%

12-16 20:13:01.859 17816 20004 D LocalTextExtractionService: 📏 Text Density: 0%

12-16 20:13:01.859 17816 20004 D LocalTextExtractionService: ⏱️ ML Processing Time: 62ms

12-16 20:13:01.859 17816 20004 D LocalTextExtractionService: ⏱️ Total Time (capture + ML): 87ms

12-16 20:13:01.859 17816 20004 D LocalTextExtractionService: 🎯 Text Regions: 0

12-16 20:13:01.859 17816 20004 D LocalTextExtractionService: 💾 Used Cache: No

12-16 20:13:01.859 17816 20004 D LocalTextExtractionService: 🎯 ROI Detected: No

12-16 20:13:01.859 17816 20004 D LocalTextExtractionService: ✅ Validation Passed: No

12-16 20:13:01.859 17816 20004 D LocalTextExtractionService: 🔄 Fallback Used: No

12-16 20:13:01.859 17816 20004 D LocalTextExtractionService: ⭐ High Quality: No

12-16 20:13:01.859 17816 20004 D LocalTextExtractionService: 🚀 Performance: 87ms (vs 200-500ms for Google Vision API)

12-16 20:13:01.859 17816 20004 D LocalTextExtractionService: 💰 Cost: FREE (vs ~$1.50 per 1000 requests for Google Vision)

12-16 20:13:01.859 17816 20004 D LocalTextExtractionService: 🔒 Privacy: ON-DEVICE (vs cloud processing)

12-16 20:13:01.859 17816 20004 D LocalTextExtractionService: 🔍 ═══════════════════════════════════════

12-16 20:13:01.859 17816 20004 D LocalTextExtractionService: 

12-16 20:13:02.245 17816 17816 V ScreenCaptureModule: 🎯 Frame requested by service

12-16 20:13:02.317 17816 20004 D ScreenCaptureService: 📸 Frame captured: 720x1600

12-16 20:13:02.716 17816 17816 V ScreenCaptureModule: 🎯 Frame requested by service

12-16 20:13:02.770 17816 18182 D LocalTextExtractionService: 📸 Frame captured: 720x1600

12-16 20:13:02.873 17816 18182 D LocalTextExtractionService: 

12-16 20:13:02.873 17816 18182 D LocalTextExtractionService: 🔍 ═══════════════════════════════════════

12-16 20:13:02.873 17816 18182 D LocalTextExtractionService: 🔍 LOCAL ML TEXT EXTRACTION RESULT

12-16 20:13:02.873 17816 18182 D LocalTextExtractionService: 🔍 ═══════════════════════════════════════

12-16 20:13:02.873 17816 18182 D LocalTextExtractionService: 📱 Frame: 720x1600

12-16 20:13:02.874 17816 18182 D LocalTextExtractionService: 📝 Extracted Text: ""

12-16 20:13:02.874 17816 18182 D LocalTextExtractionService: 📊 Confidence: 0%

12-16 20:13:02.874 17816 18182 D LocalTextExtractionService: 📏 Text Density: 0%

12-16 20:13:02.874 17816 18182 D LocalTextExtractionService: ⏱️ ML Processing Time: 68ms

12-16 20:13:02.874 17816 18182 D LocalTextExtractionService: ⏱️ Total Time (capture + ML): 103ms

12-16 20:13:02.874 17816 18182 D LocalTextExtractionService: 🎯 Text Regions: 0

12-16 20:13:02.874 17816 18182 D LocalTextExtractionService: 💾 Used Cache: No

12-16 20:13:02.874 17816 18182 D LocalTextExtractionService: 🎯 ROI Detected: No

12-16 20:13:02.874 17816 18182 D LocalTextExtractionService: ✅ Validation Passed: No

12-16 20:13:02.874 17816 18182 D LocalTextExtractionService: 🔄 Fallback Used: No

12-16 20:13:02.874 17816 18182 D LocalTextExtractionService: ⭐ High Quality: No

12-16 20:13:02.874 17816 18182 D LocalTextExtractionService: 🚀 Performance: 103ms (vs 200-500ms for Google Vision API)

12-16 20:13:02.874 17816 18182 D LocalTextExtractionService: 💰 Cost: FREE (vs ~$1.50 per 1000 requests for Google Vision)

12-16 20:13:02.874 17816 18182 D LocalTextExtractionService: 🔒 Privacy: ON-DEVICE (vs cloud processing)

12-16 20:13:02.874 17816 18182 D LocalTextExtractionService: 🔍 ═══════════════════════════════════════

12-16 20:13:02.874 17816 18182 D LocalTextExtractionService: 

12-16 20:13:03.267 17816 17816 V ScreenCaptureModule: 🎯 Frame requested by service

12-16 20:13:03.323 17816 18182 D ScreenCaptureService: 📸 Frame captured: 720x1600

12-16 20:13:03.720 17816 17816 V ScreenCaptureModule: 🎯 Frame requested by service

12-16 20:13:03.773 17816 20004 D LocalTextExtractionService: 📸 Frame captured: 720x1600

12-16 20:13:03.877 17816 20004 D LocalTextExtractionService: 

12-16 20:13:03.878 17816 20004 D LocalTextExtractionService: 🔍 ═══════════════════════════════════════

12-16 20:13:03.878 17816 20004 D LocalTextExtractionService: 🔍 LOCAL ML TEXT EXTRACTION RESULT

12-16 20:13:03.878 17816 20004 D LocalTextExtractionService: 🔍 ═══════════════════════════════════════

12-16 20:13:03.878 17816 20004 D LocalTextExtractionService: 📱 Frame: 720x1600

12-16 20:13:03.878 17816 20004 D LocalTextExtractionService: 📝 Extracted Text: ""

12-16 20:13:03.878 17816 20004 D LocalTextExtractionService: 📊 Confidence: 0%

12-16 20:13:03.879 17816 20004 D LocalTextExtractionService: 📏 Text Density: 0%

12-16 20:13:03.879 17816 20004 D LocalTextExtractionService: ⏱️ ML Processing Time: 68ms

12-16 20:13:03.879 17816 20004 D LocalTextExtractionService: ⏱️ Total Time (capture + ML): 104ms

12-16 20:13:03.879 17816 20004 D LocalTextExtractionService: 🎯 Text Regions: 0

12-16 20:13:03.879 17816 20004 D LocalTextExtractionService: 💾 Used Cache: No

12-16 20:13:03.879 17816 20004 D LocalTextExtractionService: 🎯 ROI Detected: No

12-16 20:13:03.879 17816 20004 D LocalTextExtractionService: ✅ Validation Passed: No

12-16 20:13:03.879 17816 20004 D LocalTextExtractionService: 🔄 Fallback Used: No

12-16 20:13:03.879 17816 20004 D LocalTextExtractionService: ⭐ High Quality: No

12-16 20:13:03.879 17816 20004 D LocalTextExtractionService: 🚀 Performance: 104ms (vs 200-500ms for Google Vision API)

12-16 20:13:03.880 17816 20004 D LocalTextExtractionService: 💰 Cost: FREE (vs ~$1.50 per 1000 requests for Google Vision)

12-16 20:13:03.880 17816 20004 D LocalTextExtractionService: 🔒 Privacy: ON-DEVICE (vs cloud processing)

12-16 20:13:03.880 17816 20004 D LocalTextExtractionService: 🔍 ═══════════════════════════════════════

12-16 20:13:03.880 17816 20004 D LocalTextExtractionService: 

12-16 20:13:04.386 17816 17816 V ScreenCaptureModule: 🎯 Frame requested by service

12-16 20:13:04.438 17816 20004 D ScreenCaptureService: 📸 Frame captured: 720x1600

12-16 20:13:04.723 17816 17816 V ScreenCaptureModule: 🎯 Frame requested by service

12-16 20:13:04.790 17816 19298 D LocalTextExtractionService: 📸 Frame captured: 720x1600

12-16 20:13:04.932 17816 19298 D LocalTextExtractionService: 

12-16 20:13:04.933 17816 19298 D LocalTextExtractionService: 🔍 ═══════════════════════════════════════

12-16 20:13:04.933 17816 19298 D LocalTextExtractionService: 🔍 LOCAL ML TEXT EXTRACTION RESULT

12-16 20:13:04.933 17816 19298 D LocalTextExtractionService: 🔍 ═══════════════════════════════════════

12-16 20:13:04.933 17816 19298 D LocalTextExtractionService: 📱 Frame: 720x1600

12-16 20:13:04.933 17816 19298 D LocalTextExtractionService: 📝 Extracted Text: ""

12-16 20:13:04.934 17816 19298 D LocalTextExtractionService: 📊 Confidence: 0%

12-16 20:13:04.934 17816 19298 D LocalTextExtractionService: 📏 Text Density: 0%

12-16 20:13:04.934 17816 19298 D LocalTextExtractionService: ⏱️ ML Processing Time: 105ms

12-16 20:13:04.934 17816 19298 D LocalTextExtractionService: ⏱️ Total Time (capture + ML): 142ms

12-16 20:13:04.934 17816 19298 D LocalTextExtractionService: 🎯 Text Regions: 0

12-16 20:13:04.934 17816 19298 D LocalTextExtractionService: 💾 Used Cache: No

12-16 20:13:04.934 17816 19298 D LocalTextExtractionService: 🎯 ROI Detected: No

12-16 20:13:04.935 17816 19298 D LocalTextExtractionService: ✅ Validation Passed: No

12-16 20:13:04.935 17816 19298 D LocalTextExtractionService: 🔄 Fallback Used: No

12-16 20:13:04.935 17816 19298 D LocalTextExtractionService: ⭐ High Quality: No

12-16 20:13:04.935 17816 19298 D LocalTextExtractionService: 🚀 Performance: 142ms (vs 200-500ms for Google Vision API)

12-16 20:13:04.935 17816 19298 D LocalTextExtractionService: 💰 Cost: FREE (vs ~$1.50 per 1000 requests for Google Vision)

12-16 20:13:04.935 17816 19298 D LocalTextExtractionService: 🔒 Privacy: ON-DEVICE (vs cloud processing)

12-16 20:13:04.935 17816 19298 D LocalTextExtractionService: 🔍 ═══════════════════════════════════════

12-16 20:13:04.935 17816 19298 D LocalTextExtractionService: 

12-16 20:13:05.409 17816 17816 V ScreenCaptureModule: 🎯 Frame requested by service

12-16 20:13:05.462 17816 19298 D ScreenCaptureService: 📸 Frame captured: 720x1600

12-16 20:13:05.727 17816 17816 V ScreenCaptureModule: 🎯 Frame requested by service

12-16 20:13:05.781 17816 20004 D LocalTextExtractionService: 📸 Frame captured: 720x1600

12-16 20:13:05.879 17816 20004 D LocalTextExtractionService: 

12-16 20:13:05.879 17816 20004 D LocalTextExtractionService: 🔍 ═══════════════════════════════════════

12-16 20:13:05.879 17816 20004 D LocalTextExtractionService: 🔍 LOCAL ML TEXT EXTRACTION RESULT

12-16 20:13:05.880 17816 20004 D LocalTextExtractionService: 🔍 ═══════════════════════════════════════

12-16 20:13:05.880 17816 20004 D LocalTextExtractionService: 📱 Frame: 720x1600

12-16 20:13:05.880 17816 20004 D LocalTextExtractionService: 📝 Extracted Text: ""

12-16 20:13:05.880 17816 20004 D LocalTextExtractionService: 📊 Confidence: 0%

12-16 20:13:05.880 17816 20004 D LocalTextExtractionService: 📏 Text Density: 0%

12-16 20:13:05.880 17816 20004 D LocalTextExtractionService: ⏱️ ML Processing Time: 63ms

12-16 20:13:05.880 17816 20004 D LocalTextExtractionService: ⏱️ Total Time (capture + ML): 98ms

12-16 20:13:05.880 17816 20004 D LocalTextExtractionService: 🎯 Text Regions: 0

12-16 20:13:05.880 17816 20004 D LocalTextExtractionService: 💾 Used Cache: No

12-16 20:13:05.880 17816 20004 D LocalTextExtractionService: 🎯 ROI Detected: No

12-16 20:13:05.881 17816 20004 D LocalTextExtractionService: ✅ Validation Passed: No

12-16 20:13:05.881 17816 20004 D LocalTextExtractionService: 🔄 Fallback Used: No

12-16 20:13:05.881 17816 20004 D LocalTextExtractionService: ⭐ High Quality: No

12-16 20:13:05.881 17816 20004 D LocalTextExtractionService: 🚀 Performance: 98ms (vs 200-500ms for Google Vision API)

12-16 20:13:05.881 17816 20004 D LocalTextExtractionService: 💰 Cost: FREE (vs ~$1.50 per 1000 requests for Google Vision)

12-16 20:13:05.881 17816 20004 D LocalTextExtractionService: 🔒 Privacy: ON-DEVICE (vs cloud processing)

12-16 20:13:05.881 17816 20004 D LocalTextExtractionService: 🔍 ═══════════════════════════════════════

12-16 20:13:05.881 17816 20004 D LocalTextExtractionService: 

12-16 20:13:06.731 17816 17816 V ScreenCaptureModule: 🎯 Frame requested by service

12-16 20:13:06.783 17816 20004 D LocalTextExtractionService: 📸 Frame captured: 720x1600

12-16 20:13:06.869 17816 20004 D LocalTextExtractionService: 

12-16 20:13:06.869 17816 20004 D LocalTextExtractionService: 🔍 ═══════════════════════════════════════

12-16 20:13:06.869 17816 20004 D LocalTextExtractionService: 🔍 LOCAL ML TEXT EXTRACTION RESULT

12-16 20:13:06.869 17816 20004 D LocalTextExtractionService: 🔍 ═══════════════════════════════════════

12-16 20:13:06.869 17816 20004 D LocalTextExtractionService: 📱 Frame: 720x1600

12-16 20:13:06.869 17816 20004 D LocalTextExtractionService: 📝 Extracted Text: ""

12-16 20:13:06.869 17816 20004 D LocalTextExtractionService: 📊 Confidence: 0%

12-16 20:13:06.869 17816 20004 D LocalTextExtractionService: 📏 Text Density: 0%

12-16 20:13:06.869 17816 20004 D LocalTextExtractionService: ⏱️ ML Processing Time: 64ms

12-16 20:13:06.869 17816 20004 D LocalTextExtractionService: ⏱️ Total Time (capture + ML): 86ms

12-16 20:13:06.869 17816 20004 D LocalTextExtractionService: 🎯 Text Regions: 0

12-16 20:13:06.869 17816 20004 D LocalTextExtractionService: 💾 Used Cache: No

12-16 20:13:06.869 17816 20004 D LocalTextExtractionService: 🎯 ROI Detected: No

12-16 20:13:06.869 17816 20004 D LocalTextExtractionService: ✅ Validation Passed: No

12-16 20:13:06.870 17816 20004 D LocalTextExtractionService: 🔄 Fallback Used: No

12-16 20:13:06.870 17816 20004 D LocalTextExtractionService: ⭐ High Quality: No

12-16 20:13:06.870 17816 20004 D LocalTextExtractionService: 🚀 Performance: 86ms (vs 200-500ms for Google Vision API)

12-16 20:13:06.870 17816 20004 D LocalTextExtractionService: 💰 Cost: FREE (vs ~$1.50 per 1000 requests for Google Vision)

12-16 20:13:06.870 17816 20004 D LocalTextExtractionService: 🔒 Privacy: ON-DEVICE (vs cloud processing)

12-16 20:13:06.870 17816 20004 D LocalTextExtractionService: 🔍 ═══════════════════════════════════════

12-16 20:13:06.870 17816 20004 D LocalTextExtractionService: 

12-16 20:13:06.935 17816 17816 V ScreenCaptureModule: 🎯 Frame requested by service

12-16 20:13:06.987 17816 20004 D ScreenCaptureService: 📸 Frame captured: 720x1600

12-16 20:13:07.740 17816 17816 V ScreenCaptureModule: 🎯 Frame requested by service

12-16 20:13:07.794 17816 19298 D LocalTextExtractionService: 📸 Frame captured: 720x1600

12-16 20:13:07.881 17816 19298 D LocalTextExtractionService: 

12-16 20:13:07.881 17816 19298 D LocalTextExtractionService: 🔍 ═══════════════════════════════════════

12-16 20:13:07.881 17816 19298 D LocalTextExtractionService: 🔍 LOCAL ML TEXT EXTRACTION RESULT

12-16 20:13:07.881 17816 19298 D LocalTextExtractionService: 🔍 ═══════════════════════════════════════

12-16 20:13:07.881 17816 19298 D LocalTextExtractionService: 📱 Frame: 720x1600

12-16 20:13:07.881 17816 19298 D LocalTextExtractionService: 📝 Extracted Text: ""

12-16 20:13:07.881 17816 19298 D LocalTextExtractionService: 📊 Confidence: 0%

12-16 20:13:07.881 17816 19298 D LocalTextExtractionService: 📏 Text Density: 0%

12-16 20:13:07.881 17816 19298 D LocalTextExtractionService: ⏱️ ML Processing Time: 67ms

12-16 20:13:07.881 17816 19298 D LocalTextExtractionService: ⏱️ Total Time (capture + ML): 86ms

12-16 20:13:07.881 17816 19298 D LocalTextExtractionService: 🎯 Text Regions: 0

12-16 20:13:07.881 17816 19298 D LocalTextExtractionService: 💾 Used Cache: No

12-16 20:13:07.881 17816 19298 D LocalTextExtractionService: 🎯 ROI Detected: No

12-16 20:13:07.881 17816 19298 D LocalTextExtractionService: ✅ Validation Passed: No

12-16 20:13:07.881 17816 19298 D LocalTextExtractionService: 🔄 Fallback Used: No

12-16 20:13:07.881 17816 19298 D LocalTextExtractionService: ⭐ High Quality: No

12-16 20:13:07.881 17816 19298 D LocalTextExtractionService: 🚀 Performance: 86ms (vs 200-500ms for Google Vision API)

12-16 20:13:07.881 17816 19298 D LocalTextExtractionService: 💰 Cost: FREE (vs ~$1.50 per 1000 requests for Google Vision)

12-16 20:13:07.881 17816 19298 D LocalTextExtractionService: 🔒 Privacy: ON-DEVICE (vs cloud processing)

12-16 20:13:07.881 17816 19298 D LocalTextExtractionService: 🔍 ═══════════════════════════════════════

12-16 20:13:07.881 17816 19298 D LocalTextExtractionService: 

12-16 20:13:08.153 17816 17816 V ScreenCaptureModule: 🎯 Frame requested by service

12-16 20:13:08.206 17816 19298 D ScreenCaptureService: 📸 Frame captured: 720x1600

12-16 20:13:08.735 17816 17816 V ScreenCaptureModule: 🎯 Frame requested by service

12-16 20:13:08.789 17816 18181 D LocalTextExtractionService: 📸 Frame captured: 720x1600

12-16 20:13:08.890 17816 18181 D LocalTextExtractionService: 

12-16 20:13:08.890 17816 18181 D LocalTextExtractionService: 🔍 ═══════════════════════════════════════

12-16 20:13:08.890 17816 18181 D LocalTextExtractionService: 🔍 LOCAL ML TEXT EXTRACTION RESULT

12-16 20:13:08.890 17816 18181 D LocalTextExtractionService: 🔍 ═══════════════════════════════════════

12-16 20:13:08.891 17816 18181 D LocalTextExtractionService: 📱 Frame: 720x1600

12-16 20:13:08.891 17816 18181 D LocalTextExtractionService: 📝 Extracted Text: ""

12-16 20:13:08.892 17816 18181 D LocalTextExtractionService: 📊 Confidence: 0%

12-16 20:13:08.892 17816 18181 D LocalTextExtractionService: 📏 Text Density: 0%

12-16 20:13:08.892 17816 18181 D LocalTextExtractionService: ⏱️ ML Processing Time: 65ms

12-16 20:13:08.893 17816 18181 D LocalTextExtractionService: ⏱️ Total Time (capture + ML): 100ms

12-16 20:13:08.893 17816 18181 D LocalTextExtractionService: 🎯 Text Regions: 0

12-16 20:13:08.893 17816 18181 D LocalTextExtractionService: 💾 Used Cache: No

12-16 20:13:08.893 17816 18181 D LocalTextExtractionService: 🎯 ROI Detected: No

12-16 20:13:08.893 17816 18181 D LocalTextExtractionService: ✅ Validation Passed: No

12-16 20:13:08.893 17816 18181 D LocalTextExtractionService: 🔄 Fallback Used: No

12-16 20:13:08.893 17816 18181 D LocalTextExtractionService: ⭐ High Quality: No

12-16 20:13:08.893 17816 18181 D LocalTextExtractionService: 🚀 Performance: 100ms (vs 200-500ms for Google Vision API)

12-16 20:13:08.893 17816 18181 D LocalTextExtractionService: 💰 Cost: FREE (vs ~$1.50 per 1000 requests for Google Vision)

12-16 20:13:08.893 17816 18181 D LocalTextExtractionService: 🔒 Privacy: ON-DEVICE (vs cloud processing)

12-16 20:13:08.893 17816 18181 D LocalTextExtractionService: 🔍 ═══════════════════════════════════════

12-16 20:13:08.893 17816 18181 D LocalTextExtractionService: 

12-16 20:13:09.008 17816 18175 D LocalTextExtractionService: 🛑 Stopping local text extraction loop

12-16 20:13:09.020 17816 18182 E LocalTextExtractionService: ❌ Error in local text extraction loop: Job was cancelled

12-16 20:13:09.020 17816 18182 D LocalTextExtractionService: ⏹️ Local text extraction loop stopped

12-16 20:13:09.022 17816 17816 D LocalTextExtractionService: 🔋 WakeLock released

12-16 20:13:09.022 17816 17816 D LocalTextExtractionService: 

12-16 20:13:09.022 17816 17816 D LocalTextExtractionService: 📊 ═══════════════════════════════════════

12-16 20:13:09.022 17816 17816 D LocalTextExtractionService: 📊 FINAL LOCAL ML EXTRACTION STATISTICS

12-16 20:13:09.022 17816 17816 D LocalTextExtractionService: 📊 ═══════════════════════════════════════

12-16 20:13:09.022 17816 17816 D LocalTextExtractionService: 📸 Total Captures: 16

12-16 20:13:09.022 17816 17816 D LocalTextExtractionService: ✅ Successful Extractions: 0

12-16 20:13:09.023 17816 17816 D LocalTextExtractionService: 📈 Success Rate: 0%

12-16 20:13:09.023 17816 17816 D LocalTextExtractionService: ⏱️ Average Processing Time: 205ms

12-16 20:13:09.023 17816 17816 D LocalTextExtractionService: 📝 Total Text Extracted: 0 characters

12-16 20:13:09.023 17816 17816 D LocalTextExtractionService: 📊 Average Text per Extraction: 0 characters

12-16 20:13:09.023 17816 17816 D LocalTextExtractionService: 🚀 Performance Advantage: 4-10x faster than Google Vision API

12-16 20:13:09.023 17816 17816 D LocalTextExtractionService: 💰 Cost Savings: 100% (no API costs)

12-16 20:13:09.023 17816 17816 D LocalTextExtractionService: 🔒 Privacy: 100% on-device processing

12-16 20:13:09.023 17816 17816 D LocalTextExtractionService: 📊 ═══════════════════════════════════════

12-16 20:13:09.023 17816 17816 D LocalTextExtractionService: 

12-16 20:13:09.023 17816 17816 D LocalTextExtractionService: ❌ LocalTextExtractionService destroyed

12-16 20:13:09.203 17816 18174 I ReactNativeJS: ✅ Local text extraction service stopped

12-16 20:13:09.413 17816 18174 I ReactNativeJS: 📸 Total Captures: 0

12-16 20:13:09.415 17816 18174 I ReactNativeJS: ✅ Successful Extractions: 0

12-16 20:13:09.418 17816 18174 I ReactNativeJS: ⏱️ Average Total Time: 0.0ms

12-16 20:13:09.421 17816 18174 I ReactNativeJS: 💾 Cache Hit Rate: 0.0%

12-16 20:13:09.422 17816 18174 I ReactNativeJS: 📝 Total Text Extracted: 0 characters

12-16 20:13:09.424 17816 18174 I ReactNativeJS: 💾 Cache Performance: 0 hits, 0 misses

12-16 20:13:09.426 17816 18174 I ReactNativeJS: 💾 Cache Efficiency: 0%

12-16 20:14:41.657 17816 17816 D AllotPermissions: ✅ Screen capture permission GRANTED - stored in holder

12-16 20:14:41.693 17816 20928 D ScreenCaptureModule: 🚀 Starting foreground service...

12-16 20:14:41.817 17816 17816 D ScreenCaptureService: ✅ ScreenCaptureService created

12-16 20:14:41.825 17816 17816 D ScreenCaptureService: 🚀 ScreenCaptureService started

12-16 20:14:41.834 17816 17816 D ScreenCaptureService: 🔄 Background processing started

12-16 20:14:42.220 17816 20928 D ScreenCaptureModule: ✅ MediaProjection created successfully

12-16 20:14:42.335 17816 17816 D ScreenCaptureModule: ✅ VirtualDisplay created successfully

12-16 20:14:42.336 17816 17816 D ScreenCaptureModule: ✅ Image capture setup completed

12-16 20:14:42.336 17816 17816 D ScreenCaptureModule: ✅ Service callbacks connected

12-16 20:14:42.338 17816 17816 D ScreenCaptureModule: ✅ Screen capture started successfully

12-16 20:14:42.343 17816 17816 V ScreenCaptureModule: 🎯 Frame requested by service

12-16 20:14:42.395 17816 19298 D ScreenCaptureService: 📸 Frame captured: 720x1600

12-16 20:14:42.446 17816 17816 D ScreenCaptureModule: 🎯 Processing requested frame

12-16 20:14:43.354 17816 18175 D ScreenCaptureModule: 🎯 Requesting next frame capture...

12-16 20:14:43.357 17816 17816 D ScreenCaptureModule: 🎯 Processing requested frame

12-16 20:14:43.617 17816 17816 V ScreenCaptureModule: 🎯 Frame requested by service

12-16 20:14:43.645 17816 17816 D ScreenCaptureModule: 🎯 Processing requested frame

12-16 20:14:43.696 17816 18181 D ScreenCaptureService: 📸 Frame captured: 720x1600

12-16 20:14:45.397 17816 17816 V ScreenCaptureModule: 🎯 Frame requested by service

12-16 20:14:45.404 17816 17816 D ScreenCaptureModule: 🎯 Processing requested frame

12-16 20:14:45.449 17816 18182 D ScreenCaptureService: 📸 Frame captured: 720x1600

12-16 20:14:47.141 17816 17816 V ScreenCaptureModule: 🎯 Frame requested by service

12-16 20:14:47.196 17816 18181 D ScreenCaptureService: 📸 Frame captured: 720x1600

12-16 20:14:48.688 17816 17816 V ScreenCaptureModule: 🎯 Frame requested by service

12-16 20:14:48.745 17816 19298 D ScreenCaptureService: 📸 Frame captured: 720x1600

12-16 20:14:49.718 17816 17816 V ScreenCaptureModule: 🎯 Frame requested by service

12-16 20:14:49.772 17816 18181 D ScreenCaptureService: 📸 Frame captured: 720x1600

12-16 20:14:51.163 17816 17816 V ScreenCaptureModule: 🎯 Frame requested by service

12-16 20:14:51.217 17816 18182 D ScreenCaptureService: 📸 Frame captured: 720x1600

12-16 20:14:51.505 17816 17816 D ScreenCaptureModule: 🎯 Processing requested frame

12-16 20:14:52.294 17816 17816 V ScreenCaptureModule: 🎯 Frame requested by service

12-16 20:14:52.347 17816 19298 D ScreenCaptureService: 📸 Frame captured: 720x1600

12-16 20:14:53.640 17816 17816 V ScreenCaptureModule: 🎯 Frame requested by service

12-16 20:14:53.694 17816 18182 D ScreenCaptureService: 📸 Frame captured: 720x1600

12-16 20:14:54.673 17816 17816 V ScreenCaptureModule: 🎯 Frame requested by service

12-16 20:14:54.729 17816 19298 D ScreenCaptureService: 📸 Frame captured: 720x1600

12-16 20:14:55.016 17816 17816 D ScreenCaptureModule: 🎯 Processing requested frame

12-16 20:14:55.705 17816 17816 V ScreenCaptureModule: 🎯 Frame requested by service

12-16 20:14:55.759 17816 18182 D ScreenCaptureService: 📸 Frame captured: 720x1600

12-16 20:14:56.838 17816 17816 V ScreenCaptureModule: 🎯 Frame requested by service

12-16 20:14:56.893 17816 18181 D ScreenCaptureService: 📸 Frame captured: 720x1600

12-16 20:14:57.971 17816 17816 V ScreenCaptureModule: 🎯 Frame requested by service

12-16 20:14:58.025 17816 18182 D ScreenCaptureService: 📸 Frame captured: 720x1600

12-16 20:14:59.212 17816 17816 V ScreenCaptureModule: 🎯 Frame requested by service

12-16 20:14:59.266 17816 19298 D ScreenCaptureService: 📸 Frame captured: 720x1600

12-16 20:15:00.089 17816 17816 D ScreenCaptureModule: 🎯 Processing requested frame

12-16 20:15:00.447 17816 17816 V ScreenCaptureModule: 🎯 Frame requested by service

12-16 20:15:00.500 17816 18181 D ScreenCaptureService: 📸 Frame captured: 720x1600

12-16 20:15:01.503 17816 17816 D ScreenCaptureModule: 🎯 Processing requested frame

12-16 20:15:01.888 17816 17816 V ScreenCaptureModule: 🎯 Frame requested by service

12-16 20:15:01.941 17816 19298 D ScreenCaptureService: 📸 Frame captured: 720x1600

12-16 20:15:02.093 17816 17816 D ScreenCaptureModule: 🎯 Processing requested frame

12-16 20:15:03.110 17816 17816 V ScreenCaptureModule: 🎯 Frame requested by service

12-16 20:15:03.162 17816 18182 D ScreenCaptureService: 📸 Frame captured: 720x1600

12-16 20:15:04.637 17816 17816 V ScreenCaptureModule: 🎯 Frame requested by service

12-16 20:15:04.691 17816 19298 D ScreenCaptureService: 📸 Frame captured: 720x1600

12-16 20:15:05.871 17816 17816 V ScreenCaptureModule: 🎯 Frame requested by service

12-16 20:15:05.926 17816 18181 D ScreenCaptureService: 📸 Frame captured: 720x1600

12-16 20:15:07.311 17816 17816 V ScreenCaptureModule: 🎯 Frame requested by service

12-16 20:15:07.364 17816 19298 D ScreenCaptureService: 📸 Frame captured: 720x1600

12-16 20:15:08.433 17816 17816 V ScreenCaptureModule: 🎯 Frame requested by service

12-16 20:15:08.487 17816 18181 D ScreenCaptureService: 📸 Frame captured: 720x1600

12-16 20:15:09.566 17816 17816 V ScreenCaptureModule: 🎯 Frame requested by service

12-16 20:15:09.620 17816 18181 D ScreenCaptureService: 📸 Frame captured: 720x1600

12-16 20:15:10.004 17816 17816 D ScreenCaptureModule: 🎯 Processing requested frame

12-16 20:15:10.703 17816 17816 V ScreenCaptureModule: 🎯 Frame requested by service

12-16 20:15:10.757 17816 18181 D ScreenCaptureService: 📸 Frame captured: 720x1600

12-16 20:15:11.173 17816 17816 D ScreenCaptureModule: 🎯 Processing requested frame

12-16 20:15:11.935 17816 17816 V ScreenCaptureModule: 🎯 Frame requested by service

12-16 20:15:11.987 17816 18182 D ScreenCaptureService: 📸 Frame captured: 720x1600

12-16 20:15:13.137 17816 17816 D ScreenCaptureModule: 🎯 Processing requested frame

12-16 20:15:13.262 17816 17816 V ScreenCaptureModule: 🎯 Frame requested by service

12-16 20:15:13.314 17816 19298 D ScreenCaptureService: 📸 Frame captured: 720x1600

12-16 20:15:14.393 17816 17816 V ScreenCaptureModule: 🎯 Frame requested by service

12-16 20:15:14.447 17816 18182 D ScreenCaptureService: 📸 Frame captured: 720x1600

12-16 20:15:15.527 17816 17816 V ScreenCaptureModule: 🎯 Frame requested by service

12-16 20:15:15.581 17816 18181 D ScreenCaptureService: 📸 Frame captured: 720x1600

12-16 20:15:16.458 17816 17816 V ScreenCaptureModule: 🎯 Frame requested by service

12-16 20:15:16.512 17816 18181 D ScreenCaptureService: 📸 Frame captured: 720x1600

12-16 20:15:16.957 17816 17816 D ScreenCaptureModule: 🎯 Processing requested frame

12-16 20:15:17.490 17816 17816 V ScreenCaptureModule: 🎯 Frame requested by service

12-16 20:15:17.545 17816 18182 D ScreenCaptureService: 📸 Frame captured: 720x1600

12-16 20:15:18.623 17816 17816 V ScreenCaptureModule: 🎯 Frame requested by service

12-16 20:15:18.678 17816 19298 D ScreenCaptureService: 📸 Frame captured: 720x1600

12-16 20:15:20.376 17816 17816 V ScreenCaptureModule: 🎯 Frame requested by service

12-16 20:15:20.430 17816 18182 D ScreenCaptureService: 📸 Frame captured: 720x1600

12-16 20:15:21.923 17816 17816 V ScreenCaptureModule: 🎯 Frame requested by service

12-16 20:15:21.977 17816 19298 D ScreenCaptureService: 📸 Frame captured: 720x1600

12-16 20:15:23.781 17816 17816 V ScreenCaptureModule: 🎯 Frame requested by service

12-16 20:15:23.836 17816 18181 D ScreenCaptureService: 📸 Frame captured: 720x1600

12-16 20:15:24.811 17816 17816 D ScreenCaptureModule: 🎯 Processing requested frame

12-16 20:15:25.114 17816 17816 V ScreenCaptureModule: 🎯 Frame requested by service

12-16 20:15:25.167 17816 19298 D ScreenCaptureService: 📸 Frame captured: 720x1600

12-16 20:15:25.774 17816 17816 D ScreenCaptureModule: 🎯 Processing requested frame

12-16 20:15:26.542 17816 17816 V ScreenCaptureModule: 🎯 Frame requested by service

12-16 20:15:26.594 17816 18182 D ScreenCaptureService: 📸 Frame captured: 720x1600

12-16 20:15:26.664 17816 17816 D ScreenCaptureModule: 🎯 Processing requested frame

12-16 20:15:26.699 17816 17816 D AllotPermissions: ✅ Screen capture permission GRANTED - stored in holder

12-16 20:15:26.701 17816 18174 I ReactNativeJS: 📸 Starting screen capture...

12-16 20:15:26.742 17816 17816 D LocalTextExtractionService: ✅ LocalTextExtractionService created

12-16 20:15:26.747 17816 17816 D LocalTextExtractionService: 🔋 WakeLock acquired

12-16 20:15:26.749 17816 17816 D LocalTextExtractionService: 🚀 LocalTextExtractionService started

12-16 20:15:26.754 17816 17816 D LocalTextExtractionService: 🔧 Configuration: validation=true, caching=true, roi=true, interval=1000ms

12-16 20:15:26.754 17816 17816 D LocalTextExtractionService: 🔄 Background processing started

12-16 20:15:27.227 17816 19298 D LocalTextExtractionModule: ✅ Screen capture callbacks connected to LocalTextExtractionService

12-16 20:15:27.227 17816 19298 D LocalTextExtractionService: 🎬 Starting local text extraction loop (1000ms interval)

12-16 20:15:27.227 17816 19298 D LocalTextExtractionService: 🤖 Local ML Kit: ENABLED

12-16 20:15:27.227 17816 19298 D LocalTextExtractionService: 🛡️ Validation: ENABLED

12-16 20:15:27.227 17816 19298 D LocalTextExtractionService: 💾 Caching: ENABLED

12-16 20:15:27.232 17816 17816 V ScreenCaptureModule: 🎯 Frame requested by service

12-16 20:15:27.233 17816 18174 I ReactNativeJS: ✅ Local text extraction service started

12-16 20:15:27.242 17816 17816 D ScreenCaptureModule: 🎯 Processing requested frame

12-16 20:15:27.467 17816 19298 D LocalTextExtractionService: 📸 Frame captured: 720x1600

12-16 20:15:27.591 17816 19298 D LocalTextExtractionService: 

12-16 20:15:27.591 17816 19298 D LocalTextExtractionService: 🔍 ═══════════════════════════════════════

12-16 20:15:27.591 17816 19298 D LocalTextExtractionService: 🔍 LOCAL ML TEXT EXTRACTION RESULT

12-16 20:15:27.591 17816 19298 D LocalTextExtractionService: 🔍 ═══════════════════════════════════════

12-16 20:15:27.591 17816 19298 D LocalTextExtractionService: 📱 Frame: 720x1600

12-16 20:15:27.592 17816 19298 D LocalTextExtractionService: 📝 Extracted Text: ""

12-16 20:15:27.592 17816 19298 D LocalTextExtractionService: 📊 Confidence: 0%

12-16 20:15:27.592 17816 19298 D LocalTextExtractionService: 📏 Text Density: 0%

12-16 20:15:27.592 17816 19298 D LocalTextExtractionService: ⏱️ ML Processing Time: 92ms

12-16 20:15:27.592 17816 19298 D LocalTextExtractionService: ⏱️ Total Time (capture + ML): 124ms

12-16 20:15:27.592 17816 19298 D LocalTextExtractionService: 🎯 Text Regions: 0

12-16 20:15:27.592 17816 19298 D LocalTextExtractionService: 💾 Used Cache: No

12-16 20:15:27.592 17816 19298 D LocalTextExtractionService: 🎯 ROI Detected: No

12-16 20:15:27.592 17816 19298 D LocalTextExtractionService: ✅ Validation Passed: No

12-16 20:15:27.592 17816 19298 D LocalTextExtractionService: 🔄 Fallback Used: No

12-16 20:15:27.592 17816 19298 D LocalTextExtractionService: ⭐ High Quality: No

12-16 20:15:27.592 17816 19298 D LocalTextExtractionService: 🚀 Performance: 124ms (vs 200-500ms for Google Vision API)

12-16 20:15:27.592 17816 19298 D LocalTextExtractionService: 💰 Cost: FREE (vs ~$1.50 per 1000 requests for Google Vision)

12-16 20:15:27.592 17816 19298 D LocalTextExtractionService: 🔒 Privacy: ON-DEVICE (vs cloud processing)

12-16 20:15:27.592 17816 19298 D LocalTextExtractionService: 🔍 ═══════════════════════════════════════

12-16 20:15:27.592 17816 19298 D LocalTextExtractionService: 

12-16 20:15:28.070 17816 17816 V ScreenCaptureModule: 🎯 Frame requested by service

12-16 20:15:28.122 17816 19298 D ScreenCaptureService: 📸 Frame captured: 720x1600

12-16 20:15:28.236 17816 17816 V ScreenCaptureModule: 🎯 Frame requested by service

12-16 20:15:28.288 17816 18182 D LocalTextExtractionService: 📸 Frame captured: 720x1600

12-16 20:15:28.383 17816 18182 D LocalTextExtractionService: 

12-16 20:15:28.383 17816 18182 D LocalTextExtractionService: 🔍 ═══════════════════════════════════════

12-16 20:15:28.384 17816 18182 D LocalTextExtractionService: 🔍 LOCAL ML TEXT EXTRACTION RESULT

12-16 20:15:28.384 17816 18182 D LocalTextExtractionService: 🔍 ═══════════════════════════════════════

12-16 20:15:28.384 17816 18182 D LocalTextExtractionService: 📱 Frame: 720x1600

12-16 20:15:28.384 17816 18182 D LocalTextExtractionService: 📝 Extracted Text: ""

12-16 20:15:28.384 17816 18182 D LocalTextExtractionService: 📊 Confidence: 0%

12-16 20:15:28.384 17816 18182 D LocalTextExtractionService: 📏 Text Density: 0%

12-16 20:15:28.384 17816 18182 D LocalTextExtractionService: ⏱️ ML Processing Time: 61ms

12-16 20:15:28.385 17816 18182 D LocalTextExtractionService: ⏱️ Total Time (capture + ML): 94ms

12-16 20:15:28.385 17816 18182 D LocalTextExtractionService: 🎯 Text Regions: 0

12-16 20:15:28.385 17816 18182 D LocalTextExtractionService: 💾 Used Cache: No

12-16 20:15:28.385 17816 18182 D LocalTextExtractionService: 🎯 ROI Detected: No

12-16 20:15:28.385 17816 18182 D LocalTextExtractionService: ✅ Validation Passed: No

12-16 20:15:28.385 17816 18182 D LocalTextExtractionService: 🔄 Fallback Used: No

12-16 20:15:28.385 17816 18182 D LocalTextExtractionService: ⭐ High Quality: No

12-16 20:15:28.385 17816 18182 D LocalTextExtractionService: 🚀 Performance: 94ms (vs 200-500ms for Google Vision API)

12-16 20:15:28.385 17816 18182 D LocalTextExtractionService: 💰 Cost: FREE (vs ~$1.50 per 1000 requests for Google Vision)

12-16 20:15:28.385 17816 18182 D LocalTextExtractionService: 🔒 Privacy: ON-DEVICE (vs cloud processing)

12-16 20:15:28.385 17816 18182 D LocalTextExtractionService: 🔍 ═══════════════════════════════════════

12-16 20:15:28.385 17816 18182 D LocalTextExtractionService: 

12-16 20:15:29.037 17816 17816 D ScreenCaptureModule: 🎯 Processing requested frame

12-16 20:15:29.254 17816 17816 V ScreenCaptureModule: 🎯 Frame requested by service

12-16 20:15:29.328 17816 17816 D ScreenCaptureModule: 🎯 Processing requested frame

12-16 20:15:29.335 17816 18181 D LocalTextExtractionService: 📸 Frame captured: 720x1600

12-16 20:15:29.472 17816 18181 D LocalTextExtractionService: 

12-16 20:15:29.472 17816 18181 D LocalTextExtractionService: 🔍 ═══════════════════════════════════════

12-16 20:15:29.472 17816 18181 D LocalTextExtractionService: 🔍 LOCAL ML TEXT EXTRACTION RESULT

12-16 20:15:29.472 17816 18181 D LocalTextExtractionService: 🔍 ═══════════════════════════════════════

12-16 20:15:29.472 17816 18181 D LocalTextExtractionService: 📱 Frame: 720x1600

12-16 20:15:29.473 17816 18181 D LocalTextExtractionService: 📝 Extracted Text: ""

12-16 20:15:29.473 17816 18181 D LocalTextExtractionService: 📊 Confidence: 0%

12-16 20:15:29.473 17816 18181 D LocalTextExtractionService: 📏 Text Density: 0%

12-16 20:15:29.473 17816 18181 D LocalTextExtractionService: ⏱️ ML Processing Time: 98ms

12-16 20:15:29.473 17816 18181 D LocalTextExtractionService: ⏱️ Total Time (capture + ML): 135ms

12-16 20:15:29.473 17816 18181 D LocalTextExtractionService: 🎯 Text Regions: 0

12-16 20:15:29.473 17816 18181 D LocalTextExtractionService: 💾 Used Cache: No

12-16 20:15:29.473 17816 18181 D LocalTextExtractionService: 🎯 ROI Detected: No

12-16 20:15:29.473 17816 18181 D LocalTextExtractionService: ✅ Validation Passed: No

12-16 20:15:29.473 17816 18181 D LocalTextExtractionService: 🔄 Fallback Used: No

12-16 20:15:29.473 17816 18181 D LocalTextExtractionService: ⭐ High Quality: No

12-16 20:15:29.473 17816 18181 D LocalTextExtractionService: 🚀 Performance: 135ms (vs 200-500ms for Google Vision API)

12-16 20:15:29.473 17816 18181 D LocalTextExtractionService: 💰 Cost: FREE (vs ~$1.50 per 1000 requests for Google Vision)

12-16 20:15:29.473 17816 18181 D LocalTextExtractionService: 🔒 Privacy: ON-DEVICE (vs cloud processing)

12-16 20:15:29.473 17816 18181 D LocalTextExtractionService: 🔍 ═══════════════════════════════════════

12-16 20:15:29.473 17816 18181 D LocalTextExtractionService: 

12-16 20:15:29.599 17816 17816 V ScreenCaptureModule: 🎯 Frame requested by service

12-16 20:15:29.652 17816 18181 D ScreenCaptureService: 📸 Frame captured: 720x1600

12-16 20:15:30.244 17816 17816 V ScreenCaptureModule: 🎯 Frame requested by service

12-16 20:15:30.296 17816 19298 D LocalTextExtractionService: 📸 Frame captured: 720x1600

12-16 20:15:30.388 17816 19298 D LocalTextExtractionService: 

12-16 20:15:30.388 17816 19298 D LocalTextExtractionService: 🔍 ═══════════════════════════════════════

12-16 20:15:30.388 17816 19298 D LocalTextExtractionService: 🔍 LOCAL ML TEXT EXTRACTION RESULT

12-16 20:15:30.388 17816 19298 D LocalTextExtractionService: 🔍 ═══════════════════════════════════════

12-16 20:15:30.388 17816 19298 D LocalTextExtractionService: 📱 Frame: 720x1600

12-16 20:15:30.388 17816 19298 D LocalTextExtractionService: 📝 Extracted Text: ""

12-16 20:15:30.388 17816 19298 D LocalTextExtractionService: 📊 Confidence: 0%

12-16 20:15:30.388 17816 19298 D LocalTextExtractionService: 📏 Text Density: 0%

12-16 20:15:30.389 17816 19298 D LocalTextExtractionService: ⏱️ ML Processing Time: 64ms

12-16 20:15:30.389 17816 19298 D LocalTextExtractionService: ⏱️ Total Time (capture + ML): 91ms

12-16 20:15:30.389 17816 19298 D LocalTextExtractionService: 🎯 Text Regions: 0

12-16 20:15:30.389 17816 19298 D LocalTextExtractionService: 💾 Used Cache: No

12-16 20:15:30.389 17816 19298 D LocalTextExtractionService: 🎯 ROI Detected: No

12-16 20:15:30.389 17816 19298 D LocalTextExtractionService: ✅ Validation Passed: No

12-16 20:15:30.389 17816 19298 D LocalTextExtractionService: 🔄 Fallback Used: No

12-16 20:15:30.390 17816 19298 D LocalTextExtractionService: ⭐ High Quality: No

12-16 20:15:30.390 17816 19298 D LocalTextExtractionService: 🚀 Performance: 91ms (vs 200-500ms for Google Vision API)

12-16 20:15:30.390 17816 19298 D LocalTextExtractionService: 💰 Cost: FREE (vs ~$1.50 per 1000 requests for Google Vision)

12-16 20:15:30.390 17816 19298 D LocalTextExtractionService: 🔒 Privacy: ON-DEVICE (vs cloud processing)

12-16 20:15:30.390 17816 19298 D LocalTextExtractionService: 🔍 ═══════════════════════════════════════

12-16 20:15:30.390 17816 19298 D LocalTextExtractionService: 

12-16 20:15:31.034 17816 17816 V ScreenCaptureModule: 🎯 Frame requested by service

12-16 20:15:31.090 17816 19298 D ScreenCaptureService: 📸 Frame captured: 720x1600

12-16 20:15:31.249 17816 17816 V ScreenCaptureModule: 🎯 Frame requested by service

12-16 20:15:31.303 17816 20004 D LocalTextExtractionService: 📸 Frame captured: 720x1600

12-16 20:15:31.414 17816 20004 D LocalTextExtractionService: 

12-16 20:15:31.414 17816 20004 D LocalTextExtractionService: 🔍 ═══════════════════════════════════════

12-16 20:15:31.414 17816 20004 D LocalTextExtractionService: 🔍 LOCAL ML TEXT EXTRACTION RESULT

12-16 20:15:31.414 17816 20004 D LocalTextExtractionService: 🔍 ═══════════════════════════════════════

12-16 20:15:31.414 17816 20004 D LocalTextExtractionService: 📱 Frame: 720x1600

12-16 20:15:31.414 17816 20004 D LocalTextExtractionService: 📝 Extracted Text: ""

12-16 20:15:31.414 17816 20004 D LocalTextExtractionService: 📊 Confidence: 0%

12-16 20:15:31.414 17816 20004 D LocalTextExtractionService: 📏 Text Density: 0%

12-16 20:15:31.414 17816 20004 D LocalTextExtractionService: ⏱️ ML Processing Time: 68ms

12-16 20:15:31.414 17816 20004 D LocalTextExtractionService: ⏱️ Total Time (capture + ML): 108ms

12-16 20:15:31.415 17816 20004 D LocalTextExtractionService: 🎯 Text Regions: 0

12-16 20:15:31.415 17816 20004 D LocalTextExtractionService: 💾 Used Cache: No

12-16 20:15:31.415 17816 20004 D LocalTextExtractionService: 🎯 ROI Detected: No

12-16 20:15:31.415 17816 20004 D LocalTextExtractionService: ✅ Validation Passed: No

12-16 20:15:31.415 17816 20004 D LocalTextExtractionService: 🔄 Fallback Used: No

12-16 20:15:31.415 17816 20004 D LocalTextExtractionService: ⭐ High Quality: No

12-16 20:15:31.415 17816 20004 D LocalTextExtractionService: 🚀 Performance: 108ms (vs 200-500ms for Google Vision API)

12-16 20:15:31.415 17816 20004 D LocalTextExtractionService: 💰 Cost: FREE (vs ~$1.50 per 1000 requests for Google Vision)

12-16 20:15:31.415 17816 20004 D LocalTextExtractionService: 🔒 Privacy: ON-DEVICE (vs cloud processing)

12-16 20:15:31.416 17816 20004 D LocalTextExtractionService: 🔍 ═══════════════════════════════════════

12-16 20:15:31.416 17816 20004 D LocalTextExtractionService: 

12-16 20:15:31.632 17816 17816 D ScreenCaptureModule: 🎯 Processing requested frame

12-16 20:15:31.777 17816 18175 D LocalTextExtractionService: 🛑 Stopping local text extraction loop

12-16 20:15:31.789 17816 18182 E LocalTextExtractionService: ❌ Error in local text extraction loop: Job was cancelled

12-16 20:15:31.790 17816 18182 D LocalTextExtractionService: ⏹️ Local text extraction loop stopped

12-16 20:15:31.791 17816 17816 D LocalTextExtractionService: 🔋 WakeLock released

12-16 20:15:31.791 17816 17816 D LocalTextExtractionService: 

12-16 20:15:31.791 17816 17816 D LocalTextExtractionService: 📊 ═══════════════════════════════════════

12-16 20:15:31.791 17816 17816 D LocalTextExtractionService: 📊 FINAL LOCAL ML EXTRACTION STATISTICS

12-16 20:15:31.791 17816 17816 D LocalTextExtractionService: 📊 ═══════════════════════════════════════

12-16 20:15:31.791 17816 17816 D LocalTextExtractionService: 📸 Total Captures: 5

12-16 20:15:31.791 17816 17816 D LocalTextExtractionService: ✅ Successful Extractions: 0

12-16 20:15:31.791 17816 17816 D LocalTextExtractionService: 📈 Success Rate: 0%

12-16 20:15:31.791 17816 17816 D LocalTextExtractionService: ⏱️ Average Processing Time: 211ms

12-16 20:15:31.791 17816 17816 D LocalTextExtractionService: 📝 Total Text Extracted: 0 characters

12-16 20:15:31.792 17816 17816 D LocalTextExtractionService: 📊 Average Text per Extraction: 0 characters

12-16 20:15:31.792 17816 17816 D LocalTextExtractionService: 🚀 Performance Advantage: 4-10x faster than Google Vision API

12-16 20:15:31.792 17816 17816 D LocalTextExtractionService: 💰 Cost Savings: 100% (no API costs)

12-16 20:15:31.792 17816 17816 D LocalTextExtractionService: 🔒 Privacy: 100% on-device processing

12-16 20:15:31.792 17816 17816 D LocalTextExtractionService: 📊 ═══════════════════════════════════════

12-16 20:15:31.792 17816 17816 D LocalTextExtractionService: 

12-16 20:15:31.792 17816 17816 D LocalTextExtractionService: ❌ LocalTextExtractionService destroyed

12-16 20:15:31.991 17816 18174 I ReactNativeJS: ✅ Local text extraction service stopped

12-16 20:15:32.237 17816 18174 I ReactNativeJS: 📸 Total Captures: 0

12-16 20:15:32.239 17816 18174 I ReactNativeJS: ✅ Successful Extractions: 0

12-16 20:15:32.243 17816 18174 I ReactNativeJS: ⏱️ Average Total Time: 0.0ms

12-16 20:15:32.246 17816 18174 I ReactNativeJS: 💾 Cache Hit Rate: 0.0%

12-16 20:15:32.248 17816 18174 I ReactNativeJS: 📝 Total Text Extracted: 0 characters

12-16 20:15:32.250 17816 18174 I ReactNativeJS: 💾 Cache Performance: 0 hits, 0 misses

12-16 20:15:32.251 17816 18174 I ReactNativeJS: 💾 Cache Efficiency: 0%

12-16 20:15:42.131 17816 17816 D AllotPermissions: ✅ Screen capture permission GRANTED - stored in holder

12-16 20:15:42.133 17816 18174 I ReactNativeJS: 📸 Starting screen capture...

12-16 20:15:42.155 17816 21411 D ScreenCaptureModule: 🚀 Starting foreground service...

12-16 20:15:42.213 17816 17816 D ScreenCaptureService: ✅ ScreenCaptureService created

12-16 20:15:42.219 17816 17816 D ScreenCaptureService: 🚀 ScreenCaptureService started

12-16 20:15:42.224 17816 17816 D ScreenCaptureService: 🔄 Background processing started

12-16 20:15:42.662 17816 21411 D ScreenCaptureModule: ✅ MediaProjection created successfully

12-16 20:15:42.766 17816 17816 D ScreenCaptureModule: ✅ VirtualDisplay created successfully

12-16 20:15:42.766 17816 17816 D ScreenCaptureModule: ✅ Image capture setup completed

12-16 20:15:42.766 17816 17816 D ScreenCaptureModule: ✅ Service callbacks connected

12-16 20:15:42.770 17816 17816 D ScreenCaptureModule: ✅ Screen capture started successfully

12-16 20:15:42.778 17816 17816 V ScreenCaptureModule: 🎯 Frame requested by service

12-16 20:15:42.787 17816 17816 D LocalTextExtractionService: ✅ LocalTextExtractionService created

12-16 20:15:42.797 17816 17816 D LocalTextExtractionService: 🔋 WakeLock acquired

12-16 20:15:42.798 17816 17816 D LocalTextExtractionService: 🚀 LocalTextExtractionService started

12-16 20:15:42.804 17816 17816 D LocalTextExtractionService: 🔧 Configuration: validation=true, caching=true, roi=true, interval=1000ms

12-16 20:15:42.804 17816 17816 D LocalTextExtractionService: 🔄 Background processing started

12-16 20:15:42.828 17816 17816 D ScreenCaptureModule: 🎯 Processing requested frame

12-16 20:15:42.831 17816 18182 D ScreenCaptureService: 📸 Frame captured: 720x1600

12-16 20:15:43.288 17816 20004 D LocalTextExtractionModule: ✅ Screen capture callbacks connected to LocalTextExtractionService

12-16 20:15:43.288 17816 20004 D LocalTextExtractionService: 🎬 Starting local text extraction loop (1000ms interval)

12-16 20:15:43.288 17816 20004 D LocalTextExtractionService: 🤖 Local ML Kit: ENABLED

12-16 20:15:43.288 17816 20004 D LocalTextExtractionService: 🛡️ Validation: ENABLED

12-16 20:15:43.288 17816 20004 D LocalTextExtractionService: 💾 Caching: ENABLED

12-16 20:15:43.313 17816 17816 V ScreenCaptureModule: 🎯 Frame requested by service

12-16 20:15:43.314 17816 17816 D ScreenCaptureModule: 🎯 Processing requested frame

12-16 20:15:43.316 17816 18174 I ReactNativeJS: ✅ Local text extraction service started

12-16 20:15:43.542 17816 20004 D LocalTextExtractionService: 📸 Frame captured: 720x1600

12-16 20:15:43.657 17816 20004 D LocalTextExtractionService: 

12-16 20:15:43.658 17816 20004 D LocalTextExtractionService: 🔍 ═══════════════════════════════════════

12-16 20:15:43.658 17816 20004 D LocalTextExtractionService: 🔍 LOCAL ML TEXT EXTRACTION RESULT

12-16 20:15:43.658 17816 20004 D LocalTextExtractionService: 🔍 ═══════════════════════════════════════

12-16 20:15:43.658 17816 20004 D LocalTextExtractionService: 📱 Frame: 720x1600

12-16 20:15:43.658 17816 20004 D LocalTextExtractionService: 📝 Extracted Text: ""

12-16 20:15:43.658 17816 20004 D LocalTextExtractionService: 📊 Confidence: 0%

12-16 20:15:43.658 17816 20004 D LocalTextExtractionService: 📏 Text Density: 0%

12-16 20:15:43.659 17816 20004 D LocalTextExtractionService: ⏱️ ML Processing Time: 88ms

12-16 20:15:43.659 17816 20004 D LocalTextExtractionService: ⏱️ Total Time (capture + ML): 114ms

12-16 20:15:43.659 17816 20004 D LocalTextExtractionService: 🎯 Text Regions: 0

12-16 20:15:43.659 17816 20004 D LocalTextExtractionService: 💾 Used Cache: No

12-16 20:15:43.659 17816 20004 D LocalTextExtractionService: 🎯 ROI Detected: No

12-16 20:15:43.659 17816 20004 D LocalTextExtractionService: ✅ Validation Passed: No

12-16 20:15:43.659 17816 20004 D LocalTextExtractionService: 🔄 Fallback Used: No

12-16 20:15:43.659 17816 20004 D LocalTextExtractionService: ⭐ High Quality: No

12-16 20:15:43.659 17816 20004 D LocalTextExtractionService: 🚀 Performance: 114ms (vs 200-500ms for Google Vision API)

12-16 20:15:43.659 17816 20004 D LocalTextExtractionService: 💰 Cost: FREE (vs ~$1.50 per 1000 requests for Google Vision)

12-16 20:15:43.659 17816 20004 D LocalTextExtractionService: 🔒 Privacy: ON-DEVICE (vs cloud processing)

12-16 20:15:43.659 17816 20004 D LocalTextExtractionService: 🔍 ═══════════════════════════════════════

12-16 20:15:43.659 17816 20004 D LocalTextExtractionService: 

12-16 20:15:44.198 17816 17816 V ScreenCaptureModule: 🎯 Frame requested by service

12-16 20:15:44.250 17816 20004 D ScreenCaptureService: 📸 Frame captured: 720x1600

12-16 20:15:44.293 17816 17816 V ScreenCaptureModule: 🎯 Frame requested by service

12-16 20:15:44.344 17816 18182 D LocalTextExtractionService: 📸 Frame captured: 720x1600

12-16 20:15:44.410 17816 17816 D ScreenCaptureModule: 🎯 Processing requested frame

12-16 20:15:44.430 17816 18182 D LocalTextExtractionService: 

12-16 20:15:44.430 17816 18182 D LocalTextExtractionService: 🔍 ═══════════════════════════════════════

12-16 20:15:44.430 17816 18182 D LocalTextExtractionService: 🔍 LOCAL ML TEXT EXTRACTION RESULT

12-16 20:15:44.430 17816 18182 D LocalTextExtractionService: 🔍 ═══════════════════════════════════════

12-16 20:15:44.430 17816 18182 D LocalTextExtractionService: 📱 Frame: 720x1600

12-16 20:15:44.431 17816 18182 D LocalTextExtractionService: 📝 Extracted Text: ""

12-16 20:15:44.431 17816 18182 D LocalTextExtractionService: 📊 Confidence: 0%

12-16 20:15:44.431 17816 18182 D LocalTextExtractionService: 📏 Text Density: 0%

12-16 20:15:44.431 17816 18182 D LocalTextExtractionService: ⏱️ ML Processing Time: 66ms

12-16 20:15:44.431 17816 18182 D LocalTextExtractionService: ⏱️ Total Time (capture + ML): 85ms

12-16 20:15:44.431 17816 18182 D LocalTextExtractionService: 🎯 Text Regions: 0

12-16 20:15:44.431 17816 18182 D LocalTextExtractionService: 💾 Used Cache: No

12-16 20:15:44.431 17816 18182 D LocalTextExtractionService: 🎯 ROI Detected: No

12-16 20:15:44.431 17816 18182 D LocalTextExtractionService: ✅ Validation Passed: No

12-16 20:15:44.431 17816 18182 D LocalTextExtractionService: 🔄 Fallback Used: No

12-16 20:15:44.431 17816 18182 D LocalTextExtractionService: ⭐ High Quality: No

12-16 20:15:44.431 17816 18182 D LocalTextExtractionService: 🚀 Performance: 85ms (vs 200-500ms for Google Vision API)

12-16 20:15:44.431 17816 18182 D LocalTextExtractionService: 💰 Cost: FREE (vs ~$1.50 per 1000 requests for Google Vision)

12-16 20:15:44.431 17816 18182 D LocalTextExtractionService: 🔒 Privacy: ON-DEVICE (vs cloud processing)

12-16 20:15:44.431 17816 18182 D LocalTextExtractionService: 🔍 ═══════════════════════════════════════

12-16 20:15:44.431 17816 18182 D LocalTextExtractionService: 

12-16 20:15:45.304 17816 17816 V ScreenCaptureModule: 🎯 Frame requested by service

12-16 20:15:45.324 17816 17816 D ScreenCaptureModule: 🎯 Processing requested frame

12-16 20:15:45.357 17816 19298 D LocalTextExtractionService: 📸 Frame captured: 720x1600

12-16 20:15:45.451 17816 19298 D LocalTextExtractionService: 

12-16 20:15:45.451 17816 19298 D LocalTextExtractionService: 🔍 ═══════════════════════════════════════

12-16 20:15:45.451 17816 19298 D LocalTextExtractionService: 🔍 LOCAL ML TEXT EXTRACTION RESULT

12-16 20:15:45.451 17816 19298 D LocalTextExtractionService: 🔍 ═══════════════════════════════════════

12-16 20:15:45.451 17816 19298 D LocalTextExtractionService: 📱 Frame: 720x1600

12-16 20:15:45.451 17816 19298 D LocalTextExtractionService: 📝 Extracted Text: ""

12-16 20:15:45.451 17816 19298 D LocalTextExtractionService: 📊 Confidence: 0%

12-16 20:15:45.452 17816 19298 D LocalTextExtractionService: 📏 Text Density: 0%

12-16 20:15:45.452 17816 19298 D LocalTextExtractionService: ⏱️ ML Processing Time: 62ms

12-16 20:15:45.452 17816 19298 D LocalTextExtractionService: ⏱️ Total Time (capture + ML): 94ms

12-16 20:15:45.452 17816 19298 D LocalTextExtractionService: 🎯 Text Regions: 0

12-16 20:15:45.452 17816 19298 D LocalTextExtractionService: 💾 Used Cache: No

12-16 20:15:45.452 17816 19298 D LocalTextExtractionService: 🎯 ROI Detected: No

12-16 20:15:45.452 17816 19298 D LocalTextExtractionService: ✅ Validation Passed: No

12-16 20:15:45.452 17816 19298 D LocalTextExtractionService: 🔄 Fallback Used: No

12-16 20:15:45.452 17816 19298 D LocalTextExtractionService: ⭐ High Quality: No

12-16 20:15:45.452 17816 19298 D LocalTextExtractionService: 🚀 Performance: 94ms (vs 200-500ms for Google Vision API)

12-16 20:15:45.452 17816 19298 D LocalTextExtractionService: 💰 Cost: FREE (vs ~$1.50 per 1000 requests for Google Vision)

12-16 20:15:45.452 17816 19298 D LocalTextExtractionService: 🔒 Privacy: ON-DEVICE (vs cloud processing)

12-16 20:15:45.452 17816 19298 D LocalTextExtractionService: 🔍 ═══════════════════════════════════════

12-16 20:15:45.452 17816 19298 D LocalTextExtractionService: 

12-16 20:15:45.619 17816 17816 V ScreenCaptureModule: 🎯 Frame requested by service

12-16 20:15:45.638 17816 17816 D ScreenCaptureModule: 🎯 Processing requested frame

12-16 20:15:45.691 17816 19298 D ScreenCaptureService: 📸 Frame captured: 720x1600

12-16 20:15:46.323 17816 17816 V ScreenCaptureModule: 🎯 Frame requested by service

12-16 20:15:46.366 17816 17816 D ScreenCaptureModule: 🎯 Processing requested frame

12-16 20:15:46.396 17816 20004 D LocalTextExtractionService: 📸 Frame captured: 720x1600

12-16 20:15:46.598 17816 20004 D LocalTextExtractionService: 

12-16 20:15:46.598 17816 20004 D LocalTextExtractionService: 🔍 ═══════════════════════════════════════

12-16 20:15:46.598 17816 20004 D LocalTextExtractionService: 🔍 LOCAL ML TEXT EXTRACTION RESULT

12-16 20:15:46.598 17816 20004 D LocalTextExtractionService: 🔍 ═══════════════════════════════════════

12-16 20:15:46.599 17816 20004 D LocalTextExtractionService: 📱 Frame: 720x1600

12-16 20:15:46.601 17816 20004 D LocalTextExtractionService: 📝 Extracted Text: ""

12-16 20:15:46.601 17816 20004 D LocalTextExtractionService: 📊 Confidence: 0%

12-16 20:15:46.601 17816 20004 D LocalTextExtractionService: 📏 Text Density: 0%

12-16 20:15:46.601 17816 20004 D LocalTextExtractionService: ⏱️ ML Processing Time: 167ms

12-16 20:15:46.601 17816 20004 D LocalTextExtractionService: ⏱️ Total Time (capture + ML): 201ms

12-16 20:15:46.601 17816 20004 D LocalTextExtractionService: 🎯 Text Regions: 0

12-16 20:15:46.601 17816 20004 D LocalTextExtractionService: 💾 Used Cache: No

12-16 20:15:46.601 17816 20004 D LocalTextExtractionService: 🎯 ROI Detected: No

12-16 20:15:46.603 17816 20004 D LocalTextExtractionService: ✅ Validation Passed: No

12-16 20:15:46.603 17816 20004 D LocalTextExtractionService: 🔄 Fallback Used: No

12-16 20:15:46.603 17816 20004 D LocalTextExtractionService: ⭐ High Quality: No

12-16 20:15:46.603 17816 20004 D LocalTextExtractionService: 🚀 Performance: 201ms (vs 200-500ms for Google Vision API)

12-16 20:15:46.603 17816 20004 D LocalTextExtractionService: 💰 Cost: FREE (vs ~$1.50 per 1000 requests for Google Vision)

12-16 20:15:46.603 17816 20004 D LocalTextExtractionService: 🔒 Privacy: ON-DEVICE (vs cloud processing)

12-16 20:15:46.603 17816 20004 D LocalTextExtractionService: 🔍 ═══════════════════════════════════════

12-16 20:15:46.603 17816 20004 D LocalTextExtractionService: 

12-16 20:15:47.257 17816 17816 V ScreenCaptureModule: 🎯 Frame requested by service

12-16 20:15:47.269 17816 17816 D ScreenCaptureModule: 🎯 Processing requested frame

12-16 20:15:47.309 17816 17816 V ScreenCaptureModule: 🎯 Frame requested by service

12-16 20:15:47.312 17816 19298 D ScreenCaptureService: 📸 Frame captured: 720x1600

12-16 20:15:47.326 17816 17816 D ScreenCaptureModule: 🎯 Processing requested frame

12-16 20:15:47.363 17816 18182 D LocalTextExtractionService: 📸 Frame captured: 720x1600

12-16 20:15:47.502 17816 18182 D LocalTextExtractionService: 

12-16 20:15:47.503 17816 18182 D LocalTextExtractionService: 🔍 ═══════════════════════════════════════

12-16 20:15:47.503 17816 18182 D LocalTextExtractionService: 🔍 LOCAL ML TEXT EXTRACTION RESULT

12-16 20:15:47.503 17816 18182 D LocalTextExtractionService: 🔍 ═══════════════════════════════════════

12-16 20:15:47.503 17816 18182 D LocalTextExtractionService: 📱 Frame: 720x1600

12-16 20:15:47.504 17816 18182 D LocalTextExtractionService: 📝 Extracted Text: ""

12-16 20:15:47.504 17816 18182 D LocalTextExtractionService: 📊 Confidence: 0%

12-16 20:15:47.505 17816 18182 D LocalTextExtractionService: 📏 Text Density: 0%

12-16 20:15:47.505 17816 18182 D LocalTextExtractionService: ⏱️ ML Processing Time: 102ms

12-16 20:15:47.505 17816 18182 D LocalTextExtractionService: ⏱️ Total Time (capture + ML): 139ms

12-16 20:15:47.505 17816 18182 D LocalTextExtractionService: 🎯 Text Regions: 0

12-16 20:15:47.506 17816 18182 D LocalTextExtractionService: 💾 Used Cache: No

12-16 20:15:47.507 17816 18182 D LocalTextExtractionService: 🎯 ROI Detected: No

12-16 20:15:47.507 17816 18182 D LocalTextExtractionService: ✅ Validation Passed: No

12-16 20:15:47.507 17816 18182 D LocalTextExtractionService: 🔄 Fallback Used: No

12-16 20:15:47.507 17816 18182 D LocalTextExtractionService: ⭐ High Quality: No

12-16 20:15:47.507 17816 18182 D LocalTextExtractionService: 🚀 Performance: 139ms (vs 200-500ms for Google Vision API)

12-16 20:15:47.508 17816 18182 D LocalTextExtractionService: 💰 Cost: FREE (vs ~$1.50 per 1000 requests for Google Vision)

12-16 20:15:47.508 17816 18182 D LocalTextExtractionService: 🔒 Privacy: ON-DEVICE (vs cloud processing)

12-16 20:15:47.508 17816 18182 D LocalTextExtractionService: 🔍 ═══════════════════════════════════════

12-16 20:15:47.510 17816 18182 D LocalTextExtractionService: 

12-16 20:15:48.307 17816 17816 V ScreenCaptureModule: 🎯 Frame requested by service

12-16 20:15:48.359 17816 20004 D LocalTextExtractionService: 📸 Frame captured: 720x1600

12-16 20:15:48.441 17816 20004 D LocalTextExtractionService: 

12-16 20:15:48.441 17816 20004 D LocalTextExtractionService: 🔍 ═══════════════════════════════════════

12-16 20:15:48.441 17816 20004 D LocalTextExtractionService: 🔍 LOCAL ML TEXT EXTRACTION RESULT

12-16 20:15:48.441 17816 20004 D LocalTextExtractionService: 🔍 ═══════════════════════════════════════

12-16 20:15:48.441 17816 20004 D LocalTextExtractionService: 📱 Frame: 720x1600

12-16 20:15:48.442 17816 20004 D LocalTextExtractionService: 📝 Extracted Text: ""

12-16 20:15:48.442 17816 20004 D LocalTextExtractionService: 📊 Confidence: 0%

12-16 20:15:48.442 17816 20004 D LocalTextExtractionService: 📏 Text Density: 0%

12-16 20:15:48.442 17816 20004 D LocalTextExtractionService: ⏱️ ML Processing Time: 59ms

12-16 20:15:48.442 17816 20004 D LocalTextExtractionService: ⏱️ Total Time (capture + ML): 82ms

12-16 20:15:48.442 17816 20004 D LocalTextExtractionService: 🎯 Text Regions: 0

12-16 20:15:48.443 17816 20004 D LocalTextExtractionService: 💾 Used Cache: No

12-16 20:15:48.443 17816 20004 D LocalTextExtractionService: 🎯 ROI Detected: No

12-16 20:15:48.443 17816 20004 D LocalTextExtractionService: ✅ Validation Passed: No

12-16 20:15:48.443 17816 20004 D LocalTextExtractionService: 🔄 Fallback Used: No

12-16 20:15:48.443 17816 20004 D LocalTextExtractionService: ⭐ High Quality: No

12-16 20:15:48.443 17816 20004 D LocalTextExtractionService: 🚀 Performance: 82ms (vs 200-500ms for Google Vision API)

12-16 20:15:48.443 17816 20004 D LocalTextExtractionService: 💰 Cost: FREE (vs ~$1.50 per 1000 requests for Google Vision)

12-16 20:15:48.443 17816 20004 D LocalTextExtractionService: 🔒 Privacy: ON-DEVICE (vs cloud processing)

12-16 20:15:48.443 17816 20004 D LocalTextExtractionService: 🔍 ═══════════════════════════════════════

12-16 20:15:48.444 17816 20004 D LocalTextExtractionService: 

12-16 20:15:48.883 17816 17816 V ScreenCaptureModule: 🎯 Frame requested by service

12-16 20:15:48.935 17816 20004 D ScreenCaptureService: 📸 Frame captured: 720x1600

12-16 20:15:49.310 17816 17816 V ScreenCaptureModule: 🎯 Frame requested by service

12-16 20:15:49.362 17816 18182 D LocalTextExtractionService: 📸 Frame captured: 720x1600

12-16 20:15:49.447 17816 18182 D LocalTextExtractionService: 

12-16 20:15:49.447 17816 18182 D LocalTextExtractionService: 🔍 ═══════════════════════════════════════

12-16 20:15:49.447 17816 18182 D LocalTextExtractionService: 🔍 LOCAL ML TEXT EXTRACTION RESULT

12-16 20:15:49.447 17816 18182 D LocalTextExtractionService: 🔍 ═══════════════════════════════════════

12-16 20:15:49.447 17816 18182 D LocalTextExtractionService: 📱 Frame: 720x1600

12-16 20:15:49.447 17816 18182 D LocalTextExtractionService: 📝 Extracted Text: ""

12-16 20:15:49.447 17816 18182 D LocalTextExtractionService: 📊 Confidence: 0%

12-16 20:15:49.447 17816 18182 D LocalTextExtractionService: 📏 Text Density: 0%

12-16 20:15:49.447 17816 18182 D LocalTextExtractionService: ⏱️ ML Processing Time: 57ms

12-16 20:15:49.447 17816 18182 D LocalTextExtractionService: ⏱️ Total Time (capture + ML): 83ms

12-16 20:15:49.447 17816 18182 D LocalTextExtractionService: 🎯 Text Regions: 0

12-16 20:15:49.447 17816 18182 D LocalTextExtractionService: 💾 Used Cache: No

12-16 20:15:49.447 17816 18182 D LocalTextExtractionService: 🎯 ROI Detected: No

12-16 20:15:49.447 17816 18182 D LocalTextExtractionService: ✅ Validation Passed: No

12-16 20:15:49.447 17816 18182 D LocalTextExtractionService: 🔄 Fallback Used: No

12-16 20:15:49.447 17816 18182 D LocalTextExtractionService: ⭐ High Quality: No

12-16 20:15:49.447 17816 18182 D LocalTextExtractionService: 🚀 Performance: 83ms (vs 200-500ms for Google Vision API)

12-16 20:15:49.447 17816 18182 D LocalTextExtractionService: 💰 Cost: FREE (vs ~$1.50 per 1000 requests for Google Vision)

12-16 20:15:49.447 17816 18182 D LocalTextExtractionService: 🔒 Privacy: ON-DEVICE (vs cloud processing)

12-16 20:15:49.447 17816 18182 D LocalTextExtractionService: 🔍 ═══════════════════════════════════════

12-16 20:15:49.447 17816 18182 D LocalTextExtractionService: 

12-16 20:15:50.317 17816 17816 V ScreenCaptureModule: 🎯 Frame requested by service

12-16 20:15:50.372 17816 19298 D LocalTextExtractionService: 📸 Frame captured: 720x1600

12-16 20:15:50.412 17816 17816 V ScreenCaptureModule: 🎯 Frame requested by service

12-16 20:15:50.466 17816 18181 D ScreenCaptureService: 📸 Frame captured: 720x1600

12-16 20:15:50.484 17816 19298 D LocalTextExtractionService: 

12-16 20:15:50.484 17816 19298 D LocalTextExtractionService: 🔍 ═══════════════════════════════════════

12-16 20:15:50.484 17816 19298 D LocalTextExtractionService: 🔍 LOCAL ML TEXT EXTRACTION RESULT

12-16 20:15:50.484 17816 19298 D LocalTextExtractionService: 🔍 ═══════════════════════════════════════

12-16 20:15:50.484 17816 19298 D LocalTextExtractionService: 📱 Frame: 720x1600

12-16 20:15:50.484 17816 19298 D LocalTextExtractionService: 📝 Extracted Text: ""

12-16 20:15:50.484 17816 19298 D LocalTextExtractionService: 📊 Confidence: 0%

12-16 20:15:50.484 17816 19298 D LocalTextExtractionService: 📏 Text Density: 0%

12-16 20:15:50.484 17816 19298 D LocalTextExtractionService: ⏱️ ML Processing Time: 69ms

12-16 20:15:50.484 17816 19298 D LocalTextExtractionService: ⏱️ Total Time (capture + ML): 110ms

12-16 20:15:50.484 17816 19298 D LocalTextExtractionService: 🎯 Text Regions: 0

12-16 20:15:50.484 17816 19298 D LocalTextExtractionService: 💾 Used Cache: No

12-16 20:15:50.484 17816 19298 D LocalTextExtractionService: 🎯 ROI Detected: No

12-16 20:15:50.484 17816 19298 D LocalTextExtractionService: ✅ Validation Passed: No

12-16 20:15:50.485 17816 19298 D LocalTextExtractionService: 🔄 Fallback Used: No

12-16 20:15:50.485 17816 19298 D LocalTextExtractionService: ⭐ High Quality: No

12-16 20:15:50.485 17816 19298 D LocalTextExtractionService: 🚀 Performance: 110ms (vs 200-500ms for Google Vision API)

12-16 20:15:50.485 17816 19298 D LocalTextExtractionService: 💰 Cost: FREE (vs ~$1.50 per 1000 requests for Google Vision)

12-16 20:15:50.485 17816 19298 D LocalTextExtractionService: 🔒 Privacy: ON-DEVICE (vs cloud processing)

12-16 20:15:50.485 17816 19298 D LocalTextExtractionService: 🔍 ═══════════════════════════════════════

12-16 20:15:50.485 17816 19298 D LocalTextExtractionService: 

12-16 20:15:51.321 17816 17816 V ScreenCaptureModule: 🎯 Frame requested by service

12-16 20:15:51.374 17816 18182 D LocalTextExtractionService: 📸 Frame captured: 720x1600

12-16 20:15:51.479 17816 18182 D LocalTextExtractionService: 

12-16 20:15:51.479 17816 18182 D LocalTextExtractionService: 🔍 ═══════════════════════════════════════

12-16 20:15:51.479 17816 18182 D LocalTextExtractionService: 🔍 LOCAL ML TEXT EXTRACTION RESULT

12-16 20:15:51.479 17816 18182 D LocalTextExtractionService: 🔍 ═══════════════════════════════════════

12-16 20:15:51.479 17816 18182 D LocalTextExtractionService: 📱 Frame: 720x1600

12-16 20:15:51.479 17816 18182 D LocalTextExtractionService: 📝 Extracted Text: ""

12-16 20:15:51.479 17816 18182 D LocalTextExtractionService: 📊 Confidence: 0%

12-16 20:15:51.479 17816 18182 D LocalTextExtractionService: 📏 Text Density: 0%

12-16 20:15:51.479 17816 18182 D LocalTextExtractionService: ⏱️ ML Processing Time: 63ms

12-16 20:15:51.479 17816 18182 D LocalTextExtractionService: ⏱️ Total Time (capture + ML): 103ms

12-16 20:15:51.479 17816 18182 D LocalTextExtractionService: 🎯 Text Regions: 0

12-16 20:15:51.479 17816 18182 D LocalTextExtractionService: 💾 Used Cache: No

12-16 20:15:51.479 17816 18182 D LocalTextExtractionService: 🎯 ROI Detected: No

12-16 20:15:51.479 17816 18182 D LocalTextExtractionService: ✅ Validation Passed: No

12-16 20:15:51.480 17816 18182 D LocalTextExtractionService: 🔄 Fallback Used: No

12-16 20:15:51.480 17816 18182 D LocalTextExtractionService: ⭐ High Quality: No

12-16 20:15:51.480 17816 18182 D LocalTextExtractionService: 🚀 Performance: 103ms (vs 200-500ms for Google Vision API)

12-16 20:15:51.480 17816 18182 D LocalTextExtractionService: 💰 Cost: FREE (vs ~$1.50 per 1000 requests for Google Vision)

12-16 20:15:51.480 17816 18182 D LocalTextExtractionService: 🔒 Privacy: ON-DEVICE (vs cloud processing)

12-16 20:15:51.480 17816 18182 D LocalTextExtractionService: 🔍 ═══════════════════════════════════════

12-16 20:15:51.480 17816 18182 D LocalTextExtractionService: 

12-16 20:15:51.744 17816 17816 V ScreenCaptureModule: 🎯 Frame requested by service

12-16 20:15:51.799 17816 18182 D ScreenCaptureService: 📸 Frame captured: 720x1600

12-16 20:15:52.327 17816 17816 V ScreenCaptureModule: 🎯 Frame requested by service

12-16 20:15:52.382 17816 18181 D LocalTextExtractionService: 📸 Frame captured: 720x1600

12-16 20:15:52.494 17816 18181 D LocalTextExtractionService: 

12-16 20:15:52.494 17816 18181 D LocalTextExtractionService: 🔍 ═══════════════════════════════════════

12-16 20:15:52.494 17816 18181 D LocalTextExtractionService: 🔍 LOCAL ML TEXT EXTRACTION RESULT

12-16 20:15:52.494 17816 18181 D LocalTextExtractionService: 🔍 ═══════════════════════════════════════

12-16 20:15:52.494 17816 18181 D LocalTextExtractionService: 📱 Frame: 720x1600

12-16 20:15:52.494 17816 18181 D LocalTextExtractionService: 📝 Extracted Text: ""

12-16 20:15:52.494 17816 18181 D LocalTextExtractionService: 📊 Confidence: 0%

12-16 20:15:52.494 17816 18181 D LocalTextExtractionService: 📏 Text Density: 0%

12-16 20:15:52.494 17816 18181 D LocalTextExtractionService: ⏱️ ML Processing Time: 73ms

12-16 20:15:52.494 17816 18181 D LocalTextExtractionService: ⏱️ Total Time (capture + ML): 112ms

12-16 20:15:52.494 17816 18181 D LocalTextExtractionService: 🎯 Text Regions: 0

12-16 20:15:52.495 17816 18181 D LocalTextExtractionService: 💾 Used Cache: No

12-16 20:15:52.495 17816 18181 D LocalTextExtractionService: 🎯 ROI Detected: No

12-16 20:15:52.495 17816 18181 D LocalTextExtractionService: ✅ Validation Passed: No

12-16 20:15:52.495 17816 18181 D LocalTextExtractionService: 🔄 Fallback Used: No

12-16 20:15:52.495 17816 18181 D LocalTextExtractionService: ⭐ High Quality: No

12-16 20:15:52.495 17816 18181 D LocalTextExtractionService: 🚀 Performance: 112ms (vs 200-500ms for Google Vision API)

12-16 20:15:52.495 17816 18181 D LocalTextExtractionService: 💰 Cost: FREE (vs ~$1.50 per 1000 requests for Google Vision)

12-16 20:15:52.495 17816 18181 D LocalTextExtractionService: 🔒 Privacy: ON-DEVICE (vs cloud processing)

12-16 20:15:52.495 17816 18181 D LocalTextExtractionService: 🔍 ═══════════════════════════════════════

12-16 20:15:52.495 17816 18181 D LocalTextExtractionService: 

12-16 20:15:52.981 17816 17816 V ScreenCaptureModule: 🎯 Frame requested by service

12-16 20:15:53.034 17816 18181 D ScreenCaptureService: 📸 Frame captured: 720x1600

12-16 20:15:53.333 17816 17816 V ScreenCaptureModule: 🎯 Frame requested by service

12-16 20:15:53.387 17816 19298 D LocalTextExtractionService: 📸 Frame captured: 720x1600

12-16 20:15:53.495 17816 18182 D LocalTextExtractionService: 

12-16 20:15:53.495 17816 18182 D LocalTextExtractionService: 🔍 ═══════════════════════════════════════

12-16 20:15:53.495 17816 18182 D LocalTextExtractionService: 🔍 LOCAL ML TEXT EXTRACTION RESULT

12-16 20:15:53.495 17816 18182 D LocalTextExtractionService: 🔍 ═══════════════════════════════════════

12-16 20:15:53.495 17816 18182 D LocalTextExtractionService: 📱 Frame: 720x1600

12-16 20:15:53.496 17816 18182 D LocalTextExtractionService: 📝 Extracted Text: ""

12-16 20:15:53.496 17816 18182 D LocalTextExtractionService: 📊 Confidence: 0%

12-16 20:15:53.496 17816 18182 D LocalTextExtractionService: 📏 Text Density: 0%

12-16 20:15:53.496 17816 18182 D LocalTextExtractionService: ⏱️ ML Processing Time: 63ms

12-16 20:15:53.496 17816 18182 D LocalTextExtractionService: ⏱️ Total Time (capture + ML): 106ms

12-16 20:15:53.496 17816 18182 D LocalTextExtractionService: 🎯 Text Regions: 0

12-16 20:15:53.496 17816 18182 D LocalTextExtractionService: 💾 Used Cache: No

12-16 20:15:53.496 17816 18182 D LocalTextExtractionService: 🎯 ROI Detected: No

12-16 20:15:53.496 17816 18182 D LocalTextExtractionService: ✅ Validation Passed: No

12-16 20:15:53.496 17816 18182 D LocalTextExtractionService: 🔄 Fallback Used: No

12-16 20:15:53.496 17816 18182 D LocalTextExtractionService: ⭐ High Quality: No

12-16 20:15:53.496 17816 18182 D LocalTextExtractionService: 🚀 Performance: 106ms (vs 200-500ms for Google Vision API)

12-16 20:15:53.496 17816 18182 D LocalTextExtractionService: 💰 Cost: FREE (vs ~$1.50 per 1000 requests for Google Vision)

12-16 20:15:53.496 17816 18182 D LocalTextExtractionService: 🔒 Privacy: ON-DEVICE (vs cloud processing)

12-16 20:15:53.496 17816 18182 D LocalTextExtractionService: 🔍 ═══════════════════════════════════════

12-16 20:15:53.496 17816 18182 D LocalTextExtractionService: 

12-16 20:15:54.213 17816 17816 V ScreenCaptureModule: 🎯 Frame requested by service

12-16 20:15:54.268 17816 18182 D ScreenCaptureService: 📸 Frame captured: 720x1600

12-16 20:15:54.336 17816 17816 V ScreenCaptureModule: 🎯 Frame requested by service

12-16 20:15:54.389 17816 20004 D LocalTextExtractionService: 📸 Frame captured: 720x1600

12-16 20:15:54.497 17816 20004 D LocalTextExtractionService: 

12-16 20:15:54.497 17816 20004 D LocalTextExtractionService: 🔍 ═══════════════════════════════════════

12-16 20:15:54.497 17816 20004 D LocalTextExtractionService: 🔍 LOCAL ML TEXT EXTRACTION RESULT

12-16 20:15:54.497 17816 20004 D LocalTextExtractionService: 🔍 ═══════════════════════════════════════

12-16 20:15:54.497 17816 20004 D LocalTextExtractionService: 📱 Frame: 720x1600

12-16 20:15:54.497 17816 20004 D LocalTextExtractionService: 📝 Extracted Text: ""

12-16 20:15:54.497 17816 20004 D LocalTextExtractionService: 📊 Confidence: 0%

12-16 20:15:54.497 17816 20004 D LocalTextExtractionService: 📏 Text Density: 0%

12-16 20:15:54.497 17816 20004 D LocalTextExtractionService: ⏱️ ML Processing Time: 70ms

12-16 20:15:54.498 17816 20004 D LocalTextExtractionService: ⏱️ Total Time (capture + ML): 107ms

12-16 20:15:54.498 17816 20004 D LocalTextExtractionService: 🎯 Text Regions: 0

12-16 20:15:54.498 17816 20004 D LocalTextExtractionService: 💾 Used Cache: No

12-16 20:15:54.498 17816 20004 D LocalTextExtractionService: 🎯 ROI Detected: No

12-16 20:15:54.498 17816 20004 D LocalTextExtractionService: ✅ Validation Passed: No

12-16 20:15:54.498 17816 20004 D LocalTextExtractionService: 🔄 Fallback Used: No

12-16 20:15:54.498 17816 20004 D LocalTextExtractionService: ⭐ High Quality: No

12-16 20:15:54.498 17816 20004 D LocalTextExtractionService: 🚀 Performance: 107ms (vs 200-500ms for Google Vision API)

12-16 20:15:54.498 17816 20004 D LocalTextExtractionService: 💰 Cost: FREE (vs ~$1.50 per 1000 requests for Google Vision)

12-16 20:15:54.498 17816 20004 D LocalTextExtractionService: 🔒 Privacy: ON-DEVICE (vs cloud processing)

12-16 20:15:54.498 17816 20004 D LocalTextExtractionService: 🔍 ═══════════════════════════════════════

12-16 20:15:54.498 17816 20004 D LocalTextExtractionService: 

12-16 20:15:55.341 17816 17816 V ScreenCaptureModule: 🎯 Frame requested by service

12-16 20:15:55.396 17816 21765 D LocalTextExtractionService: 📸 Frame captured: 720x1600

12-16 20:15:55.483 17816 21765 D LocalTextExtractionService: 

12-16 20:15:55.483 17816 21765 D LocalTextExtractionService: 🔍 ═══════════════════════════════════════

12-16 20:15:55.483 17816 21765 D LocalTextExtractionService: 🔍 LOCAL ML TEXT EXTRACTION RESULT

12-16 20:15:55.483 17816 21765 D LocalTextExtractionService: 🔍 ═══════════════════════════════════════

12-16 20:15:55.484 17816 21765 D LocalTextExtractionService: 📱 Frame: 720x1600

12-16 20:15:55.484 17816 21765 D LocalTextExtractionService: 📝 Extracted Text: ""

12-16 20:15:55.484 17816 21765 D LocalTextExtractionService: 📊 Confidence: 0%

12-16 20:15:55.484 17816 21765 D LocalTextExtractionService: 📏 Text Density: 0%

12-16 20:15:55.484 17816 21765 D LocalTextExtractionService: ⏱️ ML Processing Time: 60ms

12-16 20:15:55.484 17816 21765 D LocalTextExtractionService: ⏱️ Total Time (capture + ML): 87ms

12-16 20:15:55.484 17816 21765 D LocalTextExtractionService: 🎯 Text Regions: 0

12-16 20:15:55.484 17816 21765 D LocalTextExtractionService: 💾 Used Cache: No

12-16 20:15:55.484 17816 21765 D LocalTextExtractionService: 🎯 ROI Detected: No

12-16 20:15:55.484 17816 21765 D LocalTextExtractionService: ✅ Validation Passed: No

12-16 20:15:55.484 17816 21765 D LocalTextExtractionService: 🔄 Fallback Used: No

12-16 20:15:55.484 17816 21765 D LocalTextExtractionService: ⭐ High Quality: No

12-16 20:15:55.484 17816 21765 D LocalTextExtractionService: 🚀 Performance: 87ms (vs 200-500ms for Google Vision API)

12-16 20:15:55.484 17816 21765 D LocalTextExtractionService: 💰 Cost: FREE (vs ~$1.50 per 1000 requests for Google Vision)

12-16 20:15:55.484 17816 21765 D LocalTextExtractionService: 🔒 Privacy: ON-DEVICE (vs cloud processing)

12-16 20:15:55.484 17816 21765 D LocalTextExtractionService: 🔍 ═══════════════════════════════════════

12-16 20:15:55.484 17816 21765 D LocalTextExtractionService: 

12-16 20:15:55.958 17816 17816 V ScreenCaptureModule: 🎯 Frame requested by service

12-16 20:15:56.013 17816 21765 D ScreenCaptureService: 📸 Frame captured: 720x1600

12-16 20:15:56.346 17816 17816 V ScreenCaptureModule: 🎯 Frame requested by service

12-16 20:15:56.400 17816 18182 D LocalTextExtractionService: 📸 Frame captured: 720x1600

12-16 20:15:56.508 17816 18182 D LocalTextExtractionService: 

12-16 20:15:56.508 17816 18182 D LocalTextExtractionService: 🔍 ═══════════════════════════════════════

12-16 20:15:56.508 17816 18182 D LocalTextExtractionService: 🔍 LOCAL ML TEXT EXTRACTION RESULT

12-16 20:15:56.508 17816 18182 D LocalTextExtractionService: 🔍 ═══════════════════════════════════════

12-16 20:15:56.508 17816 18182 D LocalTextExtractionService: 📱 Frame: 720x1600

12-16 20:15:56.508 17816 18182 D LocalTextExtractionService: 📝 Extracted Text: ""

12-16 20:15:56.508 17816 18182 D LocalTextExtractionService: 📊 Confidence: 0%

12-16 20:15:56.508 17816 18182 D LocalTextExtractionService: 📏 Text Density: 0%

12-16 20:15:56.509 17816 18182 D LocalTextExtractionService: ⏱️ ML Processing Time: 67ms

12-16 20:15:56.509 17816 18182 D LocalTextExtractionService: ⏱️ Total Time (capture + ML): 106ms

12-16 20:15:56.509 17816 18182 D LocalTextExtractionService: 🎯 Text Regions: 0

12-16 20:15:56.509 17816 18182 D LocalTextExtractionService: 💾 Used Cache: No

12-16 20:15:56.509 17816 18182 D LocalTextExtractionService: 🎯 ROI Detected: No

12-16 20:15:56.509 17816 18182 D LocalTextExtractionService: ✅ Validation Passed: No

12-16 20:15:56.509 17816 18182 D LocalTextExtractionService: 🔄 Fallback Used: No

12-16 20:15:56.509 17816 18182 D LocalTextExtractionService: ⭐ High Quality: No

12-16 20:15:56.509 17816 18182 D LocalTextExtractionService: 🚀 Performance: 106ms (vs 200-500ms for Google Vision API)

12-16 20:15:56.509 17816 18182 D LocalTextExtractionService: 💰 Cost: FREE (vs ~$1.50 per 1000 requests for Google Vision)

12-16 20:15:56.510 17816 18182 D LocalTextExtractionService: 🔒 Privacy: ON-DEVICE (vs cloud processing)

12-16 20:15:56.510 17816 18182 D LocalTextExtractionService: 🔍 ═══════════════════════════════════════

12-16 20:15:56.510 17816 18182 D LocalTextExtractionService: 

12-16 20:15:57.081 17816 17816 V ScreenCaptureModule: 🎯 Frame requested by service

12-16 20:15:57.133 17816 18182 D ScreenCaptureService: 📸 Frame captured: 720x1600

12-16 20:15:57.349 17816 17816 V ScreenCaptureModule: 🎯 Frame requested by service

12-16 20:15:57.401 17816 18181 D LocalTextExtractionService: 📸 Frame captured: 720x1600

12-16 20:15:57.504 17816 18181 D LocalTextExtractionService: 

12-16 20:15:57.504 17816 18181 D LocalTextExtractionService: 🔍 ═══════════════════════════════════════

12-16 20:15:57.504 17816 18181 D LocalTextExtractionService: 🔍 LOCAL ML TEXT EXTRACTION RESULT

12-16 20:15:57.504 17816 18181 D LocalTextExtractionService: 🔍 ═══════════════════════════════════════

12-16 20:15:57.504 17816 18181 D LocalTextExtractionService: 📱 Frame: 720x1600

12-16 20:15:57.504 17816 18181 D LocalTextExtractionService: 📝 Extracted Text: ""

12-16 20:15:57.504 17816 18181 D LocalTextExtractionService: 📊 Confidence: 0%

12-16 20:15:57.504 17816 18181 D LocalTextExtractionService: 📏 Text Density: 0%

12-16 20:15:57.504 17816 18181 D LocalTextExtractionService: ⏱️ ML Processing Time: 78ms

12-16 20:15:57.504 17816 18181 D LocalTextExtractionService: ⏱️ Total Time (capture + ML): 101ms

12-16 20:15:57.504 17816 18181 D LocalTextExtractionService: 🎯 Text Regions: 0

12-16 20:15:57.504 17816 18181 D LocalTextExtractionService: 💾 Used Cache: No

12-16 20:15:57.504 17816 18181 D LocalTextExtractionService: 🎯 ROI Detected: No

12-16 20:15:57.504 17816 18181 D LocalTextExtractionService: ✅ Validation Passed: No

12-16 20:15:57.504 17816 18181 D LocalTextExtractionService: 🔄 Fallback Used: No

12-16 20:15:57.504 17816 18181 D LocalTextExtractionService: ⭐ High Quality: No

12-16 20:15:57.505 17816 18181 D LocalTextExtractionService: 🚀 Performance: 101ms (vs 200-500ms for Google Vision API)

12-16 20:15:57.505 17816 18181 D LocalTextExtractionService: 💰 Cost: FREE (vs ~$1.50 per 1000 requests for Google Vision)

12-16 20:15:57.505 17816 18181 D LocalTextExtractionService: 🔒 Privacy: ON-DEVICE (vs cloud processing)

12-16 20:15:57.505 17816 18181 D LocalTextExtractionService: 🔍 ═══════════════════════════════════════

12-16 20:15:57.505 17816 18181 D LocalTextExtractionService: 

12-16 20:15:58.350 17816 17816 V ScreenCaptureModule: 🎯 Frame requested by service

12-16 20:15:58.401 17816 20004 D LocalTextExtractionService: 📸 Frame captured: 720x1600

12-16 20:15:58.474 17816 20004 D LocalTextExtractionService: 

12-16 20:15:58.474 17816 20004 D LocalTextExtractionService: 🔍 ═══════════════════════════════════════

12-16 20:15:58.474 17816 20004 D LocalTextExtractionService: 🔍 LOCAL ML TEXT EXTRACTION RESULT

12-16 20:15:58.474 17816 20004 D LocalTextExtractionService: 🔍 ═══════════════════════════════════════

12-16 20:15:58.474 17816 20004 D LocalTextExtractionService: 📱 Frame: 720x1600

12-16 20:15:58.474 17816 20004 D LocalTextExtractionService: 📝 Extracted Text: ""

12-16 20:15:58.474 17816 20004 D LocalTextExtractionService: 📊 Confidence: 0%

12-16 20:15:58.474 17816 20004 D LocalTextExtractionService: 📏 Text Density: 0%

12-16 20:15:58.474 17816 20004 D LocalTextExtractionService: ⏱️ ML Processing Time: 55ms

12-16 20:15:58.474 17816 20004 D LocalTextExtractionService: ⏱️ Total Time (capture + ML): 72ms

12-16 20:15:58.474 17816 20004 D LocalTextExtractionService: 🎯 Text Regions: 0

12-16 20:15:58.475 17816 20004 D LocalTextExtractionService: 💾 Used Cache: No

12-16 20:15:58.475 17816 20004 D LocalTextExtractionService: 🎯 ROI Detected: No

12-16 20:15:58.475 17816 20004 D LocalTextExtractionService: ✅ Validation Passed: No

12-16 20:15:58.475 17816 20004 D LocalTextExtractionService: 🔄 Fallback Used: No

12-16 20:15:58.475 17816 20004 D LocalTextExtractionService: ⭐ High Quality: No

12-16 20:15:58.475 17816 20004 D LocalTextExtractionService: 🚀 Performance: 72ms (vs 200-500ms for Google Vision API)

12-16 20:15:58.475 17816 20004 D LocalTextExtractionService: 💰 Cost: FREE (vs ~$1.50 per 1000 requests for Google Vision)

12-16 20:15:58.475 17816 20004 D LocalTextExtractionService: 🔒 Privacy: ON-DEVICE (vs cloud processing)

12-16 20:15:58.475 17816 20004 D LocalTextExtractionService: 🔍 ═══════════════════════════════════════

12-16 20:15:58.475 17816 20004 D LocalTextExtractionService: 

12-16 20:15:58.908 17816 17816 V ScreenCaptureModule: 🎯 Frame requested by service

12-16 20:15:58.961 17816 20004 D ScreenCaptureService: 📸 Frame captured: 720x1600

12-16 20:15:59.354 17816 17816 V ScreenCaptureModule: 🎯 Frame requested by service

12-16 20:15:59.408 17816 18182 D LocalTextExtractionService: 📸 Frame captured: 720x1600

12-16 20:15:59.515 17816 18182 D LocalTextExtractionService: 

12-16 20:15:59.515 17816 18182 D LocalTextExtractionService: 🔍 ═══════════════════════════════════════

12-16 20:15:59.515 17816 18182 D LocalTextExtractionService: 🔍 LOCAL ML TEXT EXTRACTION RESULT

12-16 20:15:59.515 17816 18182 D LocalTextExtractionService: 🔍 ═══════════════════════════════════════

12-16 20:15:59.515 17816 18182 D LocalTextExtractionService: 📱 Frame: 720x1600

12-16 20:15:59.515 17816 18182 D LocalTextExtractionService: 📝 Extracted Text: ""

12-16 20:15:59.515 17816 18182 D LocalTextExtractionService: 📊 Confidence: 0%

12-16 20:15:59.515 17816 18182 D LocalTextExtractionService: 📏 Text Density: 0%

12-16 20:15:59.515 17816 18182 D LocalTextExtractionService: ⏱️ ML Processing Time: 73ms

12-16 20:15:59.515 17816 18182 D LocalTextExtractionService: ⏱️ Total Time (capture + ML): 106ms

12-16 20:15:59.515 17816 18182 D LocalTextExtractionService: 🎯 Text Regions: 0

12-16 20:15:59.516 17816 18182 D LocalTextExtractionService: 💾 Used Cache: No

12-16 20:15:59.516 17816 18182 D LocalTextExtractionService: 🎯 ROI Detected: No

12-16 20:15:59.516 17816 18182 D LocalTextExtractionService: ✅ Validation Passed: No

12-16 20:15:59.516 17816 18182 D LocalTextExtractionService: 🔄 Fallback Used: No

12-16 20:15:59.516 17816 18182 D LocalTextExtractionService: ⭐ High Quality: No

12-16 20:15:59.516 17816 18182 D LocalTextExtractionService: 🚀 Performance: 106ms (vs 200-500ms for Google Vision API)

12-16 20:15:59.516 17816 18182 D LocalTextExtractionService: 💰 Cost: FREE (vs ~$1.50 per 1000 requests for Google Vision)

12-16 20:15:59.516 17816 18182 D LocalTextExtractionService: 🔒 Privacy: ON-DEVICE (vs cloud processing)

12-16 20:15:59.516 17816 18182 D LocalTextExtractionService: 🔍 ═══════════════════════════════════════

12-16 20:15:59.516 17816 18182 D LocalTextExtractionService: 

12-16 20:16:00.035 17816 17816 V ScreenCaptureModule: 🎯 Frame requested by service

12-16 20:16:00.089 17816 18182 D ScreenCaptureService: 📸 Frame captured: 720x1600

12-16 20:16:00.356 17816 17816 V ScreenCaptureModule: 🎯 Frame requested by service

12-16 20:16:00.408 17816 20004 D LocalTextExtractionService: 📸 Frame captured: 720x1600

12-16 20:16:00.508 17816 20004 D LocalTextExtractionService: 

12-16 20:16:00.508 17816 20004 D LocalTextExtractionService: 🔍 ═══════════════════════════════════════

12-16 20:16:00.508 17816 20004 D LocalTextExtractionService: 🔍 LOCAL ML TEXT EXTRACTION RESULT

12-16 20:16:00.508 17816 20004 D LocalTextExtractionService: 🔍 ═══════════════════════════════════════

12-16 20:16:00.508 17816 20004 D LocalTextExtractionService: 📱 Frame: 720x1600

12-16 20:16:00.508 17816 20004 D LocalTextExtractionService: 📝 Extracted Text: ""

12-16 20:16:00.509 17816 20004 D LocalTextExtractionService: 📊 Confidence: 0%

12-16 20:16:00.509 17816 20004 D LocalTextExtractionService: 📏 Text Density: 0%

12-16 20:16:00.509 17816 20004 D LocalTextExtractionService: ⏱️ ML Processing Time: 77ms

12-16 20:16:00.509 17816 20004 D LocalTextExtractionService: ⏱️ Total Time (capture + ML): 99ms

12-16 20:16:00.509 17816 20004 D LocalTextExtractionService: 🎯 Text Regions: 0

12-16 20:16:00.509 17816 20004 D LocalTextExtractionService: 💾 Used Cache: No

12-16 20:16:00.509 17816 20004 D LocalTextExtractionService: 🎯 ROI Detected: No

12-16 20:16:00.510 17816 20004 D LocalTextExtractionService: ✅ Validation Passed: No

12-16 20:16:00.510 17816 20004 D LocalTextExtractionService: 🔄 Fallback Used: No

12-16 20:16:00.510 17816 20004 D LocalTextExtractionService: ⭐ High Quality: No

12-16 20:16:00.510 17816 20004 D LocalTextExtractionService: 🚀 Performance: 99ms (vs 200-500ms for Google Vision API)

12-16 20:16:00.510 17816 20004 D LocalTextExtractionService: 💰 Cost: FREE (vs ~$1.50 per 1000 requests for Google Vision)

12-16 20:16:00.510 17816 20004 D LocalTextExtractionService: 🔒 Privacy: ON-DEVICE (vs cloud processing)

12-16 20:16:00.510 17816 20004 D LocalTextExtractionService: 🔍 ═══════════════════════════════════════

12-16 20:16:00.510 17816 20004 D LocalTextExtractionService: 

12-16 20:16:01.154 17816 17816 V ScreenCaptureModule: 🎯 Frame requested by service

12-16 20:16:01.206 17816 20004 D ScreenCaptureService: 📸 Frame captured: 720x1600

12-16 20:16:01.362 17816 17816 V ScreenCaptureModule: 🎯 Frame requested by service

12-16 20:16:01.415 17816 18181 D LocalTextExtractionService: 📸 Frame captured: 720x1600

12-16 20:16:01.500 17816 18181 D LocalTextExtractionService: 

12-16 20:16:01.500 17816 18181 D LocalTextExtractionService: 🔍 ═══════════════════════════════════════

12-16 20:16:01.500 17816 18181 D LocalTextExtractionService: 🔍 LOCAL ML TEXT EXTRACTION RESULT

12-16 20:16:01.500 17816 18181 D LocalTextExtractionService: 🔍 ═══════════════════════════════════════

12-16 20:16:01.500 17816 18181 D LocalTextExtractionService: 📱 Frame: 720x1600

12-16 20:16:01.501 17816 18181 D LocalTextExtractionService: 📝 Extracted Text: ""

12-16 20:16:01.501 17816 18181 D LocalTextExtractionService: 📊 Confidence: 0%

12-16 20:16:01.501 17816 18181 D LocalTextExtractionService: 📏 Text Density: 0%

12-16 20:16:01.501 17816 18181 D LocalTextExtractionService: ⏱️ ML Processing Time: 60ms

12-16 20:16:01.501 17816 18181 D LocalTextExtractionService: ⏱️ Total Time (capture + ML): 85ms

12-16 20:16:01.501 17816 18181 D LocalTextExtractionService: 🎯 Text Regions: 0

12-16 20:16:01.501 17816 18181 D LocalTextExtractionService: 💾 Used Cache: No

12-16 20:16:01.501 17816 18181 D LocalTextExtractionService: 🎯 ROI Detected: No

12-16 20:16:01.501 17816 18181 D LocalTextExtractionService: ✅ Validation Passed: No

12-16 20:16:01.501 17816 18181 D LocalTextExtractionService: 🔄 Fallback Used: No

12-16 20:16:01.501 17816 18181 D LocalTextExtractionService: ⭐ High Quality: No

12-16 20:16:01.501 17816 18181 D LocalTextExtractionService: 🚀 Performance: 85ms (vs 200-500ms for Google Vision API)

12-16 20:16:01.501 17816 18181 D LocalTextExtractionService: 💰 Cost: FREE (vs ~$1.50 per 1000 requests for Google Vision)

12-16 20:16:01.501 17816 18181 D LocalTextExtractionService: 🔒 Privacy: ON-DEVICE (vs cloud processing)

12-16 20:16:01.501 17816 18181 D LocalTextExtractionService: 🔍 ═══════════════════════════════════════

12-16 20:16:01.501 17816 18181 D LocalTextExtractionService: 

12-16 20:16:02.273 17816 17816 V ScreenCaptureModule: 🎯 Frame requested by service

12-16 20:16:02.326 17816 18181 D ScreenCaptureService: 📸 Frame captured: 720x1600

12-16 20:16:02.361 17816 17816 V ScreenCaptureModule: 🎯 Frame requested by service

12-16 20:16:02.414 17816 20004 D LocalTextExtractionService: 📸 Frame captured: 720x1600

12-16 20:16:02.508 17816 20004 D LocalTextExtractionService: 

12-16 20:16:02.508 17816 20004 D LocalTextExtractionService: 🔍 ═══════════════════════════════════════

12-16 20:16:02.508 17816 20004 D LocalTextExtractionService: 🔍 LOCAL ML TEXT EXTRACTION RESULT

12-16 20:16:02.508 17816 20004 D LocalTextExtractionService: 🔍 ═══════════════════════════════════════

12-16 20:16:02.508 17816 20004 D LocalTextExtractionService: 📱 Frame: 720x1600

12-16 20:16:02.508 17816 20004 D LocalTextExtractionService: 📝 Extracted Text: ""

12-16 20:16:02.508 17816 20004 D LocalTextExtractionService: 📊 Confidence: 0%

12-16 20:16:02.509 17816 20004 D LocalTextExtractionService: 📏 Text Density: 0%

12-16 20:16:02.509 17816 20004 D LocalTextExtractionService: ⏱️ ML Processing Time: 60ms

12-16 20:16:02.509 17816 20004 D LocalTextExtractionService: ⏱️ Total Time (capture + ML): 94ms

12-16 20:16:02.509 17816 20004 D LocalTextExtractionService: 🎯 Text Regions: 0

12-16 20:16:02.509 17816 20004 D LocalTextExtractionService: 💾 Used Cache: No

12-16 20:16:02.509 17816 20004 D LocalTextExtractionService: 🎯 ROI Detected: No

12-16 20:16:02.509 17816 20004 D LocalTextExtractionService: ✅ Validation Passed: No

12-16 20:16:02.509 17816 20004 D LocalTextExtractionService: 🔄 Fallback Used: No

12-16 20:16:02.509 17816 20004 D LocalTextExtractionService: ⭐ High Quality: No

12-16 20:16:02.509 17816 20004 D LocalTextExtractionService: 🚀 Performance: 94ms (vs 200-500ms for Google Vision API)

12-16 20:16:02.509 17816 20004 D LocalTextExtractionService: 💰 Cost: FREE (vs ~$1.50 per 1000 requests for Google Vision)

12-16 20:16:02.509 17816 20004 D LocalTextExtractionService: 🔒 Privacy: ON-DEVICE (vs cloud processing)

12-16 20:16:02.509 17816 20004 D LocalTextExtractionService: 🔍 ═══════════════════════════════════════

12-16 20:16:02.509 17816 20004 D LocalTextExtractionService: 

12-16 20:16:03.293 17816 17816 V ScreenCaptureModule: 🎯 Frame requested by service

12-16 20:16:03.348 17816 20004 D ScreenCaptureService: 📸 Frame captured: 720x1600

12-16 20:16:03.365 17816 17816 V ScreenCaptureModule: 🎯 Frame requested by service

12-16 20:16:03.419 17816 18181 D LocalTextExtractionService: 📸 Frame captured: 720x1600

12-16 20:16:03.521 17816 18181 D LocalTextExtractionService: 

12-16 20:16:03.522 17816 18181 D LocalTextExtractionService: 🔍 ═══════════════════════════════════════

12-16 20:16:03.522 17816 18181 D LocalTextExtractionService: 🔍 LOCAL ML TEXT EXTRACTION RESULT

12-16 20:16:03.522 17816 18181 D LocalTextExtractionService: 🔍 ═══════════════════════════════════════

12-16 20:16:03.522 17816 18181 D LocalTextExtractionService: 📱 Frame: 720x1600

12-16 20:16:03.522 17816 18181 D LocalTextExtractionService: 📝 Extracted Text: ""

12-16 20:16:03.522 17816 18181 D LocalTextExtractionService: 📊 Confidence: 0%

12-16 20:16:03.522 17816 18181 D LocalTextExtractionService: 📏 Text Density: 0%

12-16 20:16:03.522 17816 18181 D LocalTextExtractionService: ⏱️ ML Processing Time: 65ms

12-16 20:16:03.522 17816 18181 D LocalTextExtractionService: ⏱️ Total Time (capture + ML): 100ms

12-16 20:16:03.522 17816 18181 D LocalTextExtractionService: 🎯 Text Regions: 0

12-16 20:16:03.522 17816 18181 D LocalTextExtractionService: 💾 Used Cache: No

12-16 20:16:03.522 17816 18181 D LocalTextExtractionService: 🎯 ROI Detected: No

12-16 20:16:03.522 17816 18181 D LocalTextExtractionService: ✅ Validation Passed: No

12-16 20:16:03.522 17816 18181 D LocalTextExtractionService: 🔄 Fallback Used: No

12-16 20:16:03.522 17816 18181 D LocalTextExtractionService: ⭐ High Quality: No

12-16 20:16:03.522 17816 18181 D LocalTextExtractionService: 🚀 Performance: 100ms (vs 200-500ms for Google Vision API)

12-16 20:16:03.522 17816 18181 D LocalTextExtractionService: 💰 Cost: FREE (vs ~$1.50 per 1000 requests for Google Vision)

12-16 20:16:03.522 17816 18181 D LocalTextExtractionService: 🔒 Privacy: ON-DEVICE (vs cloud processing)

12-16 20:16:03.522 17816 18181 D LocalTextExtractionService: 🔍 ═══════════════════════════════════════

12-16 20:16:03.522 17816 18181 D LocalTextExtractionService: 

12-16 20:16:04.366 17816 17816 V ScreenCaptureModule: 🎯 Frame requested by service

12-16 20:16:04.416 17816 17816 V ScreenCaptureModule: 🎯 Frame requested by service

12-16 20:16:04.419 17816 18182 D LocalTextExtractionService: 📸 Frame captured: 720x1600

12-16 20:16:04.469 17816 18181 D ScreenCaptureService: 📸 Frame captured: 720x1600

12-16 20:16:04.513 17816 18182 D LocalTextExtractionService: 

12-16 20:16:04.514 17816 18182 D LocalTextExtractionService: 🔍 ═══════════════════════════════════════

12-16 20:16:04.514 17816 18182 D LocalTextExtractionService: 🔍 LOCAL ML TEXT EXTRACTION RESULT

12-16 20:16:04.514 17816 18182 D LocalTextExtractionService: 🔍 ═══════════════════════════════════════

12-16 20:16:04.514 17816 18182 D LocalTextExtractionService: 📱 Frame: 720x1600

12-16 20:16:04.514 17816 18182 D LocalTextExtractionService: 📝 Extracted Text: ""

12-16 20:16:04.514 17816 18182 D LocalTextExtractionService: 📊 Confidence: 0%

12-16 20:16:04.514 17816 18182 D LocalTextExtractionService: 📏 Text Density: 0%

12-16 20:16:04.514 17816 18182 D LocalTextExtractionService: ⏱️ ML Processing Time: 66ms

12-16 20:16:04.514 17816 18182 D LocalTextExtractionService: ⏱️ Total Time (capture + ML): 94ms

12-16 20:16:04.514 17816 18182 D LocalTextExtractionService: 🎯 Text Regions: 0

12-16 20:16:04.514 17816 18182 D LocalTextExtractionService: 💾 Used Cache: No

12-16 20:16:04.514 17816 18182 D LocalTextExtractionService: 🎯 ROI Detected: No

12-16 20:16:04.514 17816 18182 D LocalTextExtractionService: ✅ Validation Passed: No

12-16 20:16:04.514 17816 18182 D LocalTextExtractionService: 🔄 Fallback Used: No

12-16 20:16:04.514 17816 18182 D LocalTextExtractionService: ⭐ High Quality: No

12-16 20:16:04.514 17816 18182 D LocalTextExtractionService: 🚀 Performance: 94ms (vs 200-500ms for Google Vision API)

12-16 20:16:04.514 17816 18182 D LocalTextExtractionService: 💰 Cost: FREE (vs ~$1.50 per 1000 requests for Google Vision)

12-16 20:16:04.514 17816 18182 D LocalTextExtractionService: 🔒 Privacy: ON-DEVICE (vs cloud processing)

12-16 20:16:04.514 17816 18182 D LocalTextExtractionService: 🔍 ═══════════════════════════════════════

12-16 20:16:04.514 17816 18182 D LocalTextExtractionService: 

12-16 20:16:05.370 17816 17816 V ScreenCaptureModule: 🎯 Frame requested by service

12-16 20:16:05.422 17816 18181 D LocalTextExtractionService: 📸 Frame captured: 720x1600

12-16 20:16:05.433 17816 17816 V ScreenCaptureModule: 🎯 Frame requested by service

12-16 20:16:05.484 17816 21765 D ScreenCaptureService: 📸 Frame captured: 720x1600

12-16 20:16:05.514 17816 18181 D LocalTextExtractionService: 

12-16 20:16:05.514 17816 18181 D LocalTextExtractionService: 🔍 ═══════════════════════════════════════

12-16 20:16:05.514 17816 18181 D LocalTextExtractionService: 🔍 LOCAL ML TEXT EXTRACTION RESULT

12-16 20:16:05.514 17816 18181 D LocalTextExtractionService: 🔍 ═══════════════════════════════════════

12-16 20:16:05.514 17816 18181 D LocalTextExtractionService: 📱 Frame: 720x1600

12-16 20:16:05.514 17816 18181 D LocalTextExtractionService: 📝 Extracted Text: ""

12-16 20:16:05.514 17816 18181 D LocalTextExtractionService: 📊 Confidence: 0%

12-16 20:16:05.514 17816 18181 D LocalTextExtractionService: 📏 Text Density: 0%

12-16 20:16:05.514 17816 18181 D LocalTextExtractionService: ⏱️ ML Processing Time: 61ms

12-16 20:16:05.515 17816 18181 D LocalTextExtractionService: ⏱️ Total Time (capture + ML): 92ms

12-16 20:16:05.515 17816 18181 D LocalTextExtractionService: 🎯 Text Regions: 0

12-16 20:16:05.515 17816 18181 D LocalTextExtractionService: 💾 Used Cache: No

12-16 20:16:05.515 17816 18181 D LocalTextExtractionService: 🎯 ROI Detected: No

12-16 20:16:05.515 17816 18181 D LocalTextExtractionService: ✅ Validation Passed: No

12-16 20:16:05.515 17816 18181 D LocalTextExtractionService: 🔄 Fallback Used: No

12-16 20:16:05.515 17816 18181 D LocalTextExtractionService: ⭐ High Quality: No

12-16 20:16:05.515 17816 18181 D LocalTextExtractionService: 🚀 Performance: 92ms (vs 200-500ms for Google Vision API)

12-16 20:16:05.515 17816 18181 D LocalTextExtractionService: 💰 Cost: FREE (vs ~$1.50 per 1000 requests for Google Vision)

12-16 20:16:05.515 17816 18181 D LocalTextExtractionService: 🔒 Privacy: ON-DEVICE (vs cloud processing)

12-16 20:16:05.515 17816 18181 D LocalTextExtractionService: 🔍 ═══════════════════════════════════════

12-16 20:16:05.515 17816 18181 D LocalTextExtractionService: 

12-16 20:16:06.373 17816 17816 V ScreenCaptureModule: 🎯 Frame requested by service

12-16 20:16:06.428 17816 18182 D LocalTextExtractionService: 📸 Frame captured: 720x1600

12-16 20:16:06.549 17816 18182 D LocalTextExtractionService: 

12-16 20:16:06.549 17816 18182 D LocalTextExtractionService: 🔍 ═══════════════════════════════════════

12-16 20:16:06.549 17816 18182 D LocalTextExtractionService: 🔍 LOCAL ML TEXT EXTRACTION RESULT

12-16 20:16:06.549 17816 18182 D LocalTextExtractionService: 🔍 ═══════════════════════════════════════

12-16 20:16:06.550 17816 18182 D LocalTextExtractionService: 📱 Frame: 720x1600

12-16 20:16:06.550 17816 18182 D LocalTextExtractionService: 📝 Extracted Text: ""

12-16 20:16:06.550 17816 18182 D LocalTextExtractionService: 📊 Confidence: 0%

12-16 20:16:06.550 17816 18182 D LocalTextExtractionService: 📏 Text Density: 0%

12-16 20:16:06.550 17816 18182 D LocalTextExtractionService: ⏱️ ML Processing Time: 80ms

12-16 20:16:06.550 17816 18182 D LocalTextExtractionService: ⏱️ Total Time (capture + ML): 120ms

12-16 20:16:06.550 17816 18182 D LocalTextExtractionService: 🎯 Text Regions: 0

12-16 20:16:06.550 17816 18182 D LocalTextExtractionService: 💾 Used Cache: No

12-16 20:16:06.550 17816 18182 D LocalTextExtractionService: 🎯 ROI Detected: No

12-16 20:16:06.550 17816 18182 D LocalTextExtractionService: ✅ Validation Passed: No

12-16 20:16:06.550 17816 18182 D LocalTextExtractionService: 🔄 Fallback Used: No

12-16 20:16:06.550 17816 18182 D LocalTextExtractionService: ⭐ High Quality: No

12-16 20:16:06.551 17816 18182 D LocalTextExtractionService: 🚀 Performance: 120ms (vs 200-500ms for Google Vision API)

12-16 20:16:06.551 17816 18182 D LocalTextExtractionService: 💰 Cost: FREE (vs ~$1.50 per 1000 requests for Google Vision)

12-16 20:16:06.551 17816 18182 D LocalTextExtractionService: 🔒 Privacy: ON-DEVICE (vs cloud processing)

12-16 20:16:06.551 17816 18182 D LocalTextExtractionService: 🔍 ═══════════════════════════════════════

12-16 20:16:06.552 17816 18182 D LocalTextExtractionService: 

12-16 20:16:06.553 17816 17816 V ScreenCaptureModule: 🎯 Frame requested by service

12-16 20:16:06.607 17816 18182 D ScreenCaptureService: 📸 Frame captured: 720x1600

12-16 20:16:07.381 17816 17816 V ScreenCaptureModule: 🎯 Frame requested by service

12-16 20:16:07.435 17816 20004 D LocalTextExtractionService: 📸 Frame captured: 720x1600

12-16 20:16:07.547 17816 20004 D LocalTextExtractionService: 

12-16 20:16:07.547 17816 20004 D LocalTextExtractionService: 🔍 ═══════════════════════════════════════

12-16 20:16:07.547 17816 20004 D LocalTextExtractionService: 🔍 LOCAL ML TEXT EXTRACTION RESULT

12-16 20:16:07.547 17816 20004 D LocalTextExtractionService: 🔍 ═══════════════════════════════════════

12-16 20:16:07.547 17816 20004 D LocalTextExtractionService: 📱 Frame: 720x1600

12-16 20:16:07.548 17816 20004 D LocalTextExtractionService: 📝 Extracted Text: ""

12-16 20:16:07.548 17816 20004 D LocalTextExtractionService: 📊 Confidence: 0%

12-16 20:16:07.548 17816 20004 D LocalTextExtractionService: 📏 Text Density: 0%

12-16 20:16:07.548 17816 20004 D LocalTextExtractionService: ⏱️ ML Processing Time: 72ms

12-16 20:16:07.548 17816 20004 D LocalTextExtractionService: ⏱️ Total Time (capture + ML): 111ms

12-16 20:16:07.548 17816 20004 D LocalTextExtractionService: 🎯 Text Regions: 0

12-16 20:16:07.548 17816 20004 D LocalTextExtractionService: 💾 Used Cache: No

12-16 20:16:07.548 17816 20004 D LocalTextExtractionService: 🎯 ROI Detected: No

12-16 20:16:07.548 17816 20004 D LocalTextExtractionService: ✅ Validation Passed: No

12-16 20:16:07.548 17816 20004 D LocalTextExtractionService: 🔄 Fallback Used: No

12-16 20:16:07.548 17816 20004 D LocalTextExtractionService: ⭐ High Quality: No

12-16 20:16:07.548 17816 20004 D LocalTextExtractionService: 🚀 Performance: 111ms (vs 200-500ms for Google Vision API)

12-16 20:16:07.548 17816 20004 D LocalTextExtractionService: 💰 Cost: FREE (vs ~$1.50 per 1000 requests for Google Vision)

12-16 20:16:07.549 17816 20004 D LocalTextExtractionService: 🔒 Privacy: ON-DEVICE (vs cloud processing)

12-16 20:16:07.549 17816 20004 D LocalTextExtractionService: 🔍 ═══════════════════════════════════════

12-16 20:16:07.549 17816 20004 D LocalTextExtractionService: 

12-16 20:16:07.786 17816 17816 V ScreenCaptureModule: 🎯 Frame requested by service

12-16 20:16:07.840 17816 20004 D ScreenCaptureService: 📸 Frame captured: 720x1600

12-16 20:16:08.383 17816 17816 V ScreenCaptureModule: 🎯 Frame requested by service

12-16 20:16:08.437 17816 18182 D LocalTextExtractionService: 📸 Frame captured: 720x1600

12-16 20:16:08.551 17816 18182 D LocalTextExtractionService: 

12-16 20:16:08.552 17816 18182 D LocalTextExtractionService: 🔍 ═══════════════════════════════════════

12-16 20:16:08.552 17816 18182 D LocalTextExtractionService: 🔍 LOCAL ML TEXT EXTRACTION RESULT

12-16 20:16:08.552 17816 18182 D LocalTextExtractionService: 🔍 ═══════════════════════════════════════

12-16 20:16:08.552 17816 18182 D LocalTextExtractionService: 📱 Frame: 720x1600

12-16 20:16:08.552 17816 18182 D LocalTextExtractionService: 📝 Extracted Text: ""

12-16 20:16:08.552 17816 18182 D LocalTextExtractionService: 📊 Confidence: 0%

12-16 20:16:08.552 17816 18182 D LocalTextExtractionService: 📏 Text Density: 0%

12-16 20:16:08.552 17816 18182 D LocalTextExtractionService: ⏱️ ML Processing Time: 75ms

12-16 20:16:08.552 17816 18182 D LocalTextExtractionService: ⏱️ Total Time (capture + ML): 113ms

12-16 20:16:08.553 17816 18182 D LocalTextExtractionService: 🎯 Text Regions: 0

12-16 20:16:08.553 17816 18182 D LocalTextExtractionService: 💾 Used Cache: No

12-16 20:16:08.553 17816 18182 D LocalTextExtractionService: 🎯 ROI Detected: No

12-16 20:16:08.553 17816 18182 D LocalTextExtractionService: ✅ Validation Passed: No

12-16 20:16:08.553 17816 18182 D LocalTextExtractionService: 🔄 Fallback Used: No

12-16 20:16:08.553 17816 18182 D LocalTextExtractionService: ⭐ High Quality: No

12-16 20:16:08.553 17816 18182 D LocalTextExtractionService: 🚀 Performance: 113ms (vs 200-500ms for Google Vision API)

12-16 20:16:08.553 17816 18182 D LocalTextExtractionService: 💰 Cost: FREE (vs ~$1.50 per 1000 requests for Google Vision)

12-16 20:16:08.553 17816 18182 D LocalTextExtractionService: 🔒 Privacy: ON-DEVICE (vs cloud processing)

12-16 20:16:08.553 17816 18182 D LocalTextExtractionService: 🔍 ═══════════════════════════════════════

12-16 20:16:08.553 17816 18182 D LocalTextExtractionService: 

12-16 20:16:09.122 17816 17816 V ScreenCaptureModule: 🎯 Frame requested by service

12-16 20:16:09.176 17816 18182 D ScreenCaptureService: 📸 Frame captured: 720x1600

12-16 20:16:09.389 17816 17816 V ScreenCaptureModule: 🎯 Frame requested by service

12-16 20:16:09.442 17816 20004 D LocalTextExtractionService: 📸 Frame captured: 720x1600

12-16 20:16:09.549 17816 20004 D LocalTextExtractionService: 

12-16 20:16:09.549 17816 20004 D LocalTextExtractionService: 🔍 ═══════════════════════════════════════

12-16 20:16:09.549 17816 20004 D LocalTextExtractionService: 🔍 LOCAL ML TEXT EXTRACTION RESULT

12-16 20:16:09.549 17816 20004 D LocalTextExtractionService: 🔍 ═══════════════════════════════════════

12-16 20:16:09.549 17816 20004 D LocalTextExtractionService: 📱 Frame: 720x1600

12-16 20:16:09.549 17816 20004 D LocalTextExtractionService: 📝 Extracted Text: ""

12-16 20:16:09.549 17816 20004 D LocalTextExtractionService: 📊 Confidence: 0%

12-16 20:16:09.549 17816 20004 D LocalTextExtractionService: 📏 Text Density: 0%

12-16 20:16:09.549 17816 20004 D LocalTextExtractionService: ⏱️ ML Processing Time: 68ms

12-16 20:16:09.549 17816 20004 D LocalTextExtractionService: ⏱️ Total Time (capture + ML): 107ms

12-16 20:16:09.549 17816 20004 D LocalTextExtractionService: 🎯 Text Regions: 0

12-16 20:16:09.549 17816 20004 D LocalTextExtractionService: 💾 Used Cache: No

12-16 20:16:09.549 17816 20004 D LocalTextExtractionService: 🎯 ROI Detected: No

12-16 20:16:09.549 17816 20004 D LocalTextExtractionService: ✅ Validation Passed: No

12-16 20:16:09.549 17816 20004 D LocalTextExtractionService: 🔄 Fallback Used: No

12-16 20:16:09.549 17816 20004 D LocalTextExtractionService: ⭐ High Quality: No

12-16 20:16:09.549 17816 20004 D LocalTextExtractionService: 🚀 Performance: 107ms (vs 200-500ms for Google Vision API)

12-16 20:16:09.549 17816 20004 D LocalTextExtractionService: 💰 Cost: FREE (vs ~$1.50 per 1000 requests for Google Vision)

12-16 20:16:09.549 17816 20004 D LocalTextExtractionService: 🔒 Privacy: ON-DEVICE (vs cloud processing)

12-16 20:16:09.549 17816 20004 D LocalTextExtractionService: 🔍 ═══════════════════════════════════════

12-16 20:16:09.549 17816 20004 D LocalTextExtractionService: 

12-16 20:16:10.250 17816 17816 V ScreenCaptureModule: 🎯 Frame requested by service

12-16 20:16:10.302 17816 20004 D ScreenCaptureService: 📸 Frame captured: 720x1600

12-16 20:16:10.393 17816 17816 V ScreenCaptureModule: 🎯 Frame requested by service

12-16 20:16:10.446 17816 21765 D LocalTextExtractionService: 📸 Frame captured: 720x1600

12-16 20:16:10.548 17816 21765 D LocalTextExtractionService: 

12-16 20:16:10.548 17816 21765 D LocalTextExtractionService: 🔍 ═══════════════════════════════════════

12-16 20:16:10.548 17816 21765 D LocalTextExtractionService: 🔍 LOCAL ML TEXT EXTRACTION RESULT

12-16 20:16:10.548 17816 21765 D LocalTextExtractionService: 🔍 ═══════════════════════════════════════

12-16 20:16:10.549 17816 21765 D LocalTextExtractionService: 📱 Frame: 720x1600

12-16 20:16:10.549 17816 21765 D LocalTextExtractionService: 📝 Extracted Text: ""

12-16 20:16:10.549 17816 21765 D LocalTextExtractionService: 📊 Confidence: 0%

12-16 20:16:10.549 17816 21765 D LocalTextExtractionService: 📏 Text Density: 0%

12-16 20:16:10.549 17816 21765 D LocalTextExtractionService: ⏱️ ML Processing Time: 63ms

12-16 20:16:10.549 17816 21765 D LocalTextExtractionService: ⏱️ Total Time (capture + ML): 102ms

12-16 20:16:10.549 17816 21765 D LocalTextExtractionService: 🎯 Text Regions: 0

12-16 20:16:10.549 17816 21765 D LocalTextExtractionService: 💾 Used Cache: No

12-16 20:16:10.549 17816 21765 D LocalTextExtractionService: 🎯 ROI Detected: No

12-16 20:16:10.549 17816 21765 D LocalTextExtractionService: ✅ Validation Passed: No

12-16 20:16:10.550 17816 21765 D LocalTextExtractionService: 🔄 Fallback Used: No

12-16 20:16:10.550 17816 21765 D LocalTextExtractionService: ⭐ High Quality: No

12-16 20:16:10.550 17816 21765 D LocalTextExtractionService: 🚀 Performance: 102ms (vs 200-500ms for Google Vision API)

12-16 20:16:10.550 17816 21765 D LocalTextExtractionService: 💰 Cost: FREE (vs ~$1.50 per 1000 requests for Google Vision)

12-16 20:16:10.550 17816 21765 D LocalTextExtractionService: 🔒 Privacy: ON-DEVICE (vs cloud processing)

12-16 20:16:10.550 17816 21765 D LocalTextExtractionService: 🔍 ═══════════════════════════════════════

12-16 20:16:10.550 17816 21765 D LocalTextExtractionService: 

12-16 20:16:11.374 17816 17816 V ScreenCaptureModule: 🎯 Frame requested by service

12-16 20:16:11.396 17816 17816 V ScreenCaptureModule: 🎯 Frame requested by service

12-16 20:16:11.427 17816 18181 D ScreenCaptureService: 📸 Frame captured: 720x1600

12-16 20:16:11.450 17816 20004 D LocalTextExtractionService: 📸 Frame captured: 720x1600

12-16 20:16:11.541 17816 20004 D LocalTextExtractionService: 

12-16 20:16:11.542 17816 20004 D LocalTextExtractionService: 🔍 ═══════════════════════════════════════

12-16 20:16:11.542 17816 20004 D LocalTextExtractionService: 🔍 LOCAL ML TEXT EXTRACTION RESULT

12-16 20:16:11.542 17816 20004 D LocalTextExtractionService: 🔍 ═══════════════════════════════════════

12-16 20:16:11.542 17816 20004 D LocalTextExtractionService: 📱 Frame: 720x1600

12-16 20:16:11.542 17816 20004 D LocalTextExtractionService: 📝 Extracted Text: ""

12-16 20:16:11.542 17816 20004 D LocalTextExtractionService: 📊 Confidence: 0%

12-16 20:16:11.542 17816 20004 D LocalTextExtractionService: 📏 Text Density: 0%

12-16 20:16:11.542 17816 20004 D LocalTextExtractionService: ⏱️ ML Processing Time: 64ms

12-16 20:16:11.542 17816 20004 D LocalTextExtractionService: ⏱️ Total Time (capture + ML): 91ms

12-16 20:16:11.542 17816 20004 D LocalTextExtractionService: 🎯 Text Regions: 0

12-16 20:16:11.542 17816 20004 D LocalTextExtractionService: 💾 Used Cache: No

12-16 20:16:11.542 17816 20004 D LocalTextExtractionService: 🎯 ROI Detected: No

12-16 20:16:11.542 17816 20004 D LocalTextExtractionService: ✅ Validation Passed: No

12-16 20:16:11.542 17816 20004 D LocalTextExtractionService: 🔄 Fallback Used: No

12-16 20:16:11.542 17816 20004 D LocalTextExtractionService: ⭐ High Quality: No

12-16 20:16:11.542 17816 20004 D LocalTextExtractionService: 🚀 Performance: 91ms (vs 200-500ms for Google Vision API)

12-16 20:16:11.542 17816 20004 D LocalTextExtractionService: 💰 Cost: FREE (vs ~$1.50 per 1000 requests for Google Vision)

12-16 20:16:11.542 17816 20004 D LocalTextExtractionService: 🔒 Privacy: ON-DEVICE (vs cloud processing)

12-16 20:16:11.542 17816 20004 D LocalTextExtractionService: 🔍 ═══════════════════════════════════════

12-16 20:16:11.542 17816 20004 D LocalTextExtractionService: 

12-16 20:16:12.423 17816 17816 V ScreenCaptureModule: 🎯 Frame requested by service

12-16 20:16:12.424 17816 17816 V ScreenCaptureModule: 🎯 Frame requested by service

12-16 20:16:12.475 17816 21765 D ScreenCaptureService: 📸 Frame captured: 720x1600

12-16 20:16:12.476 17816 18181 D LocalTextExtractionService: 📸 Frame captured: 720x1600

12-16 20:16:12.570 17816 18181 D LocalTextExtractionService: 

12-16 20:16:12.570 17816 18181 D LocalTextExtractionService: 🔍 ═══════════════════════════════════════

12-16 20:16:12.570 17816 18181 D LocalTextExtractionService: 🔍 LOCAL ML TEXT EXTRACTION RESULT

12-16 20:16:12.571 17816 18181 D LocalTextExtractionService: 🔍 ═══════════════════════════════════════

12-16 20:16:12.571 17816 18181 D LocalTextExtractionService: 📱 Frame: 720x1600

12-16 20:16:12.571 17816 18181 D LocalTextExtractionService: 📝 Extracted Text: ""

12-16 20:16:12.571 17816 18181 D LocalTextExtractionService: 📊 Confidence: 0%

12-16 20:16:12.571 17816 18181 D LocalTextExtractionService: 📏 Text Density: 0%

12-16 20:16:12.571 17816 18181 D LocalTextExtractionService: ⏱️ ML Processing Time: 62ms

12-16 20:16:12.571 17816 18181 D LocalTextExtractionService: ⏱️ Total Time (capture + ML): 94ms

12-16 20:16:12.571 17816 18181 D LocalTextExtractionService: 🎯 Text Regions: 0

12-16 20:16:12.571 17816 18181 D LocalTextExtractionService: 💾 Used Cache: No

12-16 20:16:12.571 17816 18181 D LocalTextExtractionService: 🎯 ROI Detected: No

12-16 20:16:12.571 17816 18181 D LocalTextExtractionService: ✅ Validation Passed: No

12-16 20:16:12.571 17816 18181 D LocalTextExtractionService: 🔄 Fallback Used: No

12-16 20:16:12.571 17816 18181 D LocalTextExtractionService: ⭐ High Quality: No

12-16 20:16:12.571 17816 18181 D LocalTextExtractionService: 🚀 Performance: 94ms (vs 200-500ms for Google Vision API)

12-16 20:16:12.571 17816 18181 D LocalTextExtractionService: 💰 Cost: FREE (vs ~$1.50 per 1000 requests for Google Vision)

12-16 20:16:12.571 17816 18181 D LocalTextExtractionService: 🔒 Privacy: ON-DEVICE (vs cloud processing)

12-16 20:16:12.571 17816 18181 D LocalTextExtractionService: 🔍 ═══════════════════════════════════════

12-16 20:16:12.571 17816 18181 D LocalTextExtractionService: 

12-16 20:16:12.808 17816 18181 V LocalTextExtractionService: 💓 Service heartbeat - still alive

12-16 20:16:13.403 17816 17816 V ScreenCaptureModule: 🎯 Frame requested by service

12-16 20:16:13.456 17816 20004 D LocalTextExtractionService: 📸 Frame captured: 720x1600

12-16 20:16:13.560 17816 20004 D LocalTextExtractionService: 

12-16 20:16:13.560 17816 20004 D LocalTextExtractionService: 🔍 ═══════════════════════════════════════

12-16 20:16:13.560 17816 20004 D LocalTextExtractionService: 🔍 LOCAL ML TEXT EXTRACTION RESULT

12-16 20:16:13.560 17816 20004 D LocalTextExtractionService: 🔍 ═══════════════════════════════════════

12-16 20:16:13.560 17816 20004 D LocalTextExtractionService: 📱 Frame: 720x1600

12-16 20:16:13.561 17816 20004 D LocalTextExtractionService: 📝 Extracted Text: ""

12-16 20:16:13.561 17816 20004 D LocalTextExtractionService: 📊 Confidence: 0%

12-16 20:16:13.561 17816 20004 D LocalTextExtractionService: 📏 Text Density: 0%

12-16 20:16:13.561 17816 20004 D LocalTextExtractionService: ⏱️ ML Processing Time: 68ms

12-16 20:16:13.561 17816 20004 D LocalTextExtractionService: ⏱️ Total Time (capture + ML): 103ms

12-16 20:16:13.561 17816 20004 D LocalTextExtractionService: 🎯 Text Regions: 0

12-16 20:16:13.561 17816 20004 D LocalTextExtractionService: 💾 Used Cache: No

12-16 20:16:13.562 17816 20004 D LocalTextExtractionService: 🎯 ROI Detected: No

12-16 20:16:13.562 17816 20004 D LocalTextExtractionService: ✅ Validation Passed: No

12-16 20:16:13.562 17816 20004 D LocalTextExtractionService: 🔄 Fallback Used: No

12-16 20:16:13.562 17816 20004 D LocalTextExtractionService: ⭐ High Quality: No

12-16 20:16:13.562 17816 20004 D LocalTextExtractionService: 🚀 Performance: 103ms (vs 200-500ms for Google Vision API)

12-16 20:16:13.563 17816 20004 D LocalTextExtractionService: 💰 Cost: FREE (vs ~$1.50 per 1000 requests for Google Vision)

12-16 20:16:13.563 17816 20004 D LocalTextExtractionService: 🔒 Privacy: ON-DEVICE (vs cloud processing)

12-16 20:16:13.563 17816 20004 D LocalTextExtractionService: 🔍 ═══════════════════════════════════════

12-16 20:16:13.563 17816 20004 D LocalTextExtractionService: 

12-16 20:16:13.623 17816 17816 V ScreenCaptureModule: 🎯 Frame requested by service

12-16 20:16:13.675 17816 20004 D ScreenCaptureService: 📸 Frame captured: 720x1600

12-16 20:16:14.409 17816 17816 V ScreenCaptureModule: 🎯 Frame requested by service

12-16 20:16:14.462 17816 21765 D LocalTextExtractionService: 📸 Frame captured: 720x1600

12-16 20:16:14.572 17816 21765 D LocalTextExtractionService: 

12-16 20:16:14.572 17816 21765 D LocalTextExtractionService: 🔍 ═══════════════════════════════════════

12-16 20:16:14.572 17816 21765 D LocalTextExtractionService: 🔍 LOCAL ML TEXT EXTRACTION RESULT

12-16 20:16:14.572 17816 21765 D LocalTextExtractionService: 🔍 ═══════════════════════════════════════

12-16 20:16:14.572 17816 21765 D LocalTextExtractionService: 📱 Frame: 720x1600

12-16 20:16:14.572 17816 21765 D LocalTextExtractionService: 📝 Extracted Text: ""

12-16 20:16:14.572 17816 21765 D LocalTextExtractionService: 📊 Confidence: 0%

12-16 20:16:14.572 17816 21765 D LocalTextExtractionService: 📏 Text Density: 0%

12-16 20:16:14.572 17816 21765 D LocalTextExtractionService: ⏱️ ML Processing Time: 74ms

12-16 20:16:14.572 17816 21765 D LocalTextExtractionService: ⏱️ Total Time (capture + ML): 109ms

12-16 20:16:14.572 17816 21765 D LocalTextExtractionService: 🎯 Text Regions: 0

12-16 20:16:14.572 17816 21765 D LocalTextExtractionService: 💾 Used Cache: No

12-16 20:16:14.572 17816 21765 D LocalTextExtractionService: 🎯 ROI Detected: No

12-16 20:16:14.573 17816 21765 D LocalTextExtractionService: ✅ Validation Passed: No

12-16 20:16:14.573 17816 21765 D LocalTextExtractionService: 🔄 Fallback Used: No

12-16 20:16:14.573 17816 21765 D LocalTextExtractionService: ⭐ High Quality: No

12-16 20:16:14.573 17816 21765 D LocalTextExtractionService: 🚀 Performance: 109ms (vs 200-500ms for Google Vision API)

12-16 20:16:14.573 17816 21765 D LocalTextExtractionService: 💰 Cost: FREE (vs ~$1.50 per 1000 requests for Google Vision)

12-16 20:16:14.573 17816 21765 D LocalTextExtractionService: 🔒 Privacy: ON-DEVICE (vs cloud processing)

12-16 20:16:14.573 17816 21765 D LocalTextExtractionService: 🔍 ═══════════════════════════════════════

12-16 20:16:14.573 17816 21765 D LocalTextExtractionService: 

12-16 20:16:14.653 17816 17816 V ScreenCaptureModule: 🎯 Frame requested by service

12-16 20:16:14.705 17816 21765 D ScreenCaptureService: 📸 Frame captured: 720x1600

12-16 20:16:15.417 17816 17816 V ScreenCaptureModule: 🎯 Frame requested by service

12-16 20:16:15.471 17816 20004 D LocalTextExtractionService: 📸 Frame captured: 720x1600

12-16 20:16:15.584 17816 20004 D LocalTextExtractionService: 

12-16 20:16:15.584 17816 20004 D LocalTextExtractionService: 🔍 ═══════════════════════════════════════

12-16 20:16:15.584 17816 20004 D LocalTextExtractionService: 🔍 LOCAL ML TEXT EXTRACTION RESULT

12-16 20:16:15.584 17816 20004 D LocalTextExtractionService: 🔍 ═══════════════════════════════════════

12-16 20:16:15.584 17816 20004 D LocalTextExtractionService: 📱 Frame: 720x1600

12-16 20:16:15.584 17816 20004 D LocalTextExtractionService: 📝 Extracted Text: ""

12-16 20:16:15.584 17816 20004 D LocalTextExtractionService: 📊 Confidence: 0%

12-16 20:16:15.584 17816 20004 D LocalTextExtractionService: 📏 Text Density: 0%

12-16 20:16:15.584 17816 20004 D LocalTextExtractionService: ⏱️ ML Processing Time: 76ms

12-16 20:16:15.584 17816 20004 D LocalTextExtractionService: ⏱️ Total Time (capture + ML): 112ms

12-16 20:16:15.584 17816 20004 D LocalTextExtractionService: 🎯 Text Regions: 0

12-16 20:16:15.584 17816 20004 D LocalTextExtractionService: 💾 Used Cache: No

12-16 20:16:15.584 17816 20004 D LocalTextExtractionService: 🎯 ROI Detected: No

12-16 20:16:15.584 17816 20004 D LocalTextExtractionService: ✅ Validation Passed: No

12-16 20:16:15.584 17816 20004 D LocalTextExtractionService: 🔄 Fallback Used: No

12-16 20:16:15.584 17816 20004 D LocalTextExtractionService: ⭐ High Quality: No

12-16 20:16:15.584 17816 20004 D LocalTextExtractionService: 🚀 Performance: 112ms (vs 200-500ms for Google Vision API)

12-16 20:16:15.584 17816 20004 D LocalTextExtractionService: 💰 Cost: FREE (vs ~$1.50 per 1000 requests for Google Vision)

12-16 20:16:15.584 17816 20004 D LocalTextExtractionService: 🔒 Privacy: ON-DEVICE (vs cloud processing)

12-16 20:16:15.584 17816 20004 D LocalTextExtractionService: 🔍 ═══════════════════════════════════════

12-16 20:16:15.584 17816 20004 D LocalTextExtractionService: 

12-16 20:16:15.683 17816 17816 V ScreenCaptureModule: 🎯 Frame requested by service

12-16 20:16:15.738 17816 20004 D ScreenCaptureService: 📸 Frame captured: 720x1600

12-16 20:16:16.421 17816 17816 V ScreenCaptureModule: 🎯 Frame requested by service

12-16 20:16:16.474 17816 21765 D LocalTextExtractionService: 📸 Frame captured: 720x1600

12-16 20:16:16.591 17816 21765 D LocalTextExtractionService: 

12-16 20:16:16.591 17816 21765 D LocalTextExtractionService: 🔍 ═══════════════════════════════════════

12-16 20:16:16.591 17816 21765 D LocalTextExtractionService: 🔍 LOCAL ML TEXT EXTRACTION RESULT

12-16 20:16:16.592 17816 21765 D LocalTextExtractionService: 🔍 ═══════════════════════════════════════

12-16 20:16:16.592 17816 21765 D LocalTextExtractionService: 📱 Frame: 720x1600

12-16 20:16:16.592 17816 21765 D LocalTextExtractionService: 📝 Extracted Text: ""

12-16 20:16:16.592 17816 21765 D LocalTextExtractionService: 📊 Confidence: 0%

12-16 20:16:16.592 17816 21765 D LocalTextExtractionService: 📏 Text Density: 0%

12-16 20:16:16.592 17816 21765 D LocalTextExtractionService: ⏱️ ML Processing Time: 80ms

12-16 20:16:16.592 17816 21765 D LocalTextExtractionService: ⏱️ Total Time (capture + ML): 116ms

12-16 20:16:16.593 17816 21765 D LocalTextExtractionService: 🎯 Text Regions: 0

12-16 20:16:16.593 17816 21765 D LocalTextExtractionService: 💾 Used Cache: No

12-16 20:16:16.593 17816 21765 D LocalTextExtractionService: 🎯 ROI Detected: No

12-16 20:16:16.593 17816 21765 D LocalTextExtractionService: ✅ Validation Passed: No

12-16 20:16:16.593 17816 21765 D LocalTextExtractionService: 🔄 Fallback Used: No

12-16 20:16:16.593 17816 21765 D LocalTextExtractionService: ⭐ High Quality: No

12-16 20:16:16.593 17816 21765 D LocalTextExtractionService: 🚀 Performance: 116ms (vs 200-500ms for Google Vision API)

12-16 20:16:16.593 17816 21765 D LocalTextExtractionService: 💰 Cost: FREE (vs ~$1.50 per 1000 requests for Google Vision)

12-16 20:16:16.593 17816 21765 D LocalTextExtractionService: 🔒 Privacy: ON-DEVICE (vs cloud processing)

12-16 20:16:16.593 17816 21765 D LocalTextExtractionService: 🔍 ═══════════════════════════════════════

12-16 20:16:16.593 17816 21765 D LocalTextExtractionService: 

12-16 20:16:16.713 17816 17816 V ScreenCaptureModule: 🎯 Frame requested by service

12-16 20:16:16.765 17816 20004 D ScreenCaptureService: 📸 Frame captured: 720x1600

12-16 20:16:17.426 17816 17816 V ScreenCaptureModule: 🎯 Frame requested by service

12-16 20:16:17.482 17816 21765 D LocalTextExtractionService: 📸 Frame captured: 720x1600

12-16 20:16:17.588 17816 21765 D LocalTextExtractionService: 

12-16 20:16:17.589 17816 21765 D LocalTextExtractionService: 🔍 ═══════════════════════════════════════

12-16 20:16:17.589 17816 21765 D LocalTextExtractionService: 🔍 LOCAL ML TEXT EXTRACTION RESULT

12-16 20:16:17.589 17816 21765 D LocalTextExtractionService: 🔍 ═══════════════════════════════════════

12-16 20:16:17.589 17816 21765 D LocalTextExtractionService: 📱 Frame: 720x1600

12-16 20:16:17.589 17816 21765 D LocalTextExtractionService: 📝 Extracted Text: ""

12-16 20:16:17.589 17816 21765 D LocalTextExtractionService: 📊 Confidence: 0%

12-16 20:16:17.589 17816 21765 D LocalTextExtractionService: 📏 Text Density: 0%

12-16 20:16:17.589 17816 21765 D LocalTextExtractionService: ⏱️ ML Processing Time: 76ms

12-16 20:16:17.589 17816 21765 D LocalTextExtractionService: ⏱️ Total Time (capture + ML): 105ms

12-16 20:16:17.589 17816 21765 D LocalTextExtractionService: 🎯 Text Regions: 0

12-16 20:16:17.589 17816 21765 D LocalTextExtractionService: 💾 Used Cache: No

12-16 20:16:17.589 17816 21765 D LocalTextExtractionService: 🎯 ROI Detected: No

12-16 20:16:17.589 17816 21765 D LocalTextExtractionService: ✅ Validation Passed: No

12-16 20:16:17.589 17816 21765 D LocalTextExtractionService: 🔄 Fallback Used: No

12-16 20:16:17.589 17816 21765 D LocalTextExtractionService: ⭐ High Quality: No

12-16 20:16:17.589 17816 21765 D LocalTextExtractionService: 🚀 Performance: 105ms (vs 200-500ms for Google Vision API)

12-16 20:16:17.589 17816 21765 D LocalTextExtractionService: 💰 Cost: FREE (vs ~$1.50 per 1000 requests for Google Vision)

12-16 20:16:17.589 17816 21765 D LocalTextExtractionService: 🔒 Privacy: ON-DEVICE (vs cloud processing)

12-16 20:16:17.589 17816 21765 D LocalTextExtractionService: 🔍 ═══════════════════════════════════════

12-16 20:16:17.589 17816 21765 D LocalTextExtractionService: 

12-16 20:16:18.050 17816 17816 V ScreenCaptureModule: 🎯 Frame requested by service

12-16 20:16:18.104 17816 21765 D ScreenCaptureService: 📸 Frame captured: 720x1600

12-16 20:16:18.432 17816 17816 V ScreenCaptureModule: 🎯 Frame requested by service

12-16 20:16:18.485 17816 18182 D LocalTextExtractionService: 📸 Frame captured: 720x1600

12-16 20:16:18.594 17816 18182 D LocalTextExtractionService: 

12-16 20:16:18.594 17816 18182 D LocalTextExtractionService: 🔍 ═══════════════════════════════════════

12-16 20:16:18.594 17816 18182 D LocalTextExtractionService: 🔍 LOCAL ML TEXT EXTRACTION RESULT

12-16 20:16:18.594 17816 18182 D LocalTextExtractionService: 🔍 ═══════════════════════════════════════

12-16 20:16:18.594 17816 18182 D LocalTextExtractionService: 📱 Frame: 720x1600

12-16 20:16:18.594 17816 18182 D LocalTextExtractionService: 📝 Extracted Text: ""

12-16 20:16:18.594 17816 18182 D LocalTextExtractionService: 📊 Confidence: 0%

12-16 20:16:18.594 17816 18182 D LocalTextExtractionService: 📏 Text Density: 0%

12-16 20:16:18.594 17816 18182 D LocalTextExtractionService: ⏱️ ML Processing Time: 68ms

12-16 20:16:18.594 17816 18182 D LocalTextExtractionService: ⏱️ Total Time (capture + ML): 108ms

12-16 20:16:18.594 17816 18182 D LocalTextExtractionService: 🎯 Text Regions: 0

12-16 20:16:18.594 17816 18182 D LocalTextExtractionService: 💾 Used Cache: No

12-16 20:16:18.594 17816 18182 D LocalTextExtractionService: 🎯 ROI Detected: No

12-16 20:16:18.594 17816 18182 D LocalTextExtractionService: ✅ Validation Passed: No

12-16 20:16:18.595 17816 18182 D LocalTextExtractionService: 🔄 Fallback Used: No

12-16 20:16:18.595 17816 18182 D LocalTextExtractionService: ⭐ High Quality: No

12-16 20:16:18.595 17816 18182 D LocalTextExtractionService: 🚀 Performance: 108ms (vs 200-500ms for Google Vision API)

12-16 20:16:18.595 17816 18182 D LocalTextExtractionService: 💰 Cost: FREE (vs ~$1.50 per 1000 requests for Google Vision)

12-16 20:16:18.595 17816 18182 D LocalTextExtractionService: 🔒 Privacy: ON-DEVICE (vs cloud processing)

12-16 20:16:18.595 17816 18182 D LocalTextExtractionService: 🔍 ═══════════════════════════════════════

12-16 20:16:18.595 17816 18182 D LocalTextExtractionService: 

12-16 20:16:19.435 17816 17816 V ScreenCaptureModule: 🎯 Frame requested by service

12-16 20:16:19.490 17816 18181 D LocalTextExtractionService: 📸 Frame captured: 720x1600

12-16 20:16:19.589 17816 17816 V ScreenCaptureModule: 🎯 Frame requested by service

12-16 20:16:19.601 17816 18181 D LocalTextExtractionService: 

12-16 20:16:19.601 17816 18181 D LocalTextExtractionService: 🔍 ═══════════════════════════════════════

12-16 20:16:19.601 17816 18181 D LocalTextExtractionService: 🔍 LOCAL ML TEXT EXTRACTION RESULT

12-16 20:16:19.601 17816 18181 D LocalTextExtractionService: 🔍 ═══════════════════════════════════════

12-16 20:16:19.601 17816 18181 D LocalTextExtractionService: 📱 Frame: 720x1600

12-16 20:16:19.602 17816 18181 D LocalTextExtractionService: 📝 Extracted Text: ""

12-16 20:16:19.602 17816 18181 D LocalTextExtractionService: 📊 Confidence: 0%

12-16 20:16:19.602 17816 18181 D LocalTextExtractionService: 📏 Text Density: 0%

12-16 20:16:19.602 17816 18181 D LocalTextExtractionService: ⏱️ ML Processing Time: 70ms

12-16 20:16:19.602 17816 18181 D LocalTextExtractionService: ⏱️ Total Time (capture + ML): 110ms

12-16 20:16:19.602 17816 18181 D LocalTextExtractionService: 🎯 Text Regions: 0

12-16 20:16:19.602 17816 18181 D LocalTextExtractionService: 💾 Used Cache: No

12-16 20:16:19.602 17816 18181 D LocalTextExtractionService: 🎯 ROI Detected: No

12-16 20:16:19.602 17816 18181 D LocalTextExtractionService: ✅ Validation Passed: No

12-16 20:16:19.602 17816 18181 D LocalTextExtractionService: 🔄 Fallback Used: No

12-16 20:16:19.602 17816 18181 D LocalTextExtractionService: ⭐ High Quality: No

12-16 20:16:19.602 17816 18181 D LocalTextExtractionService: 🚀 Performance: 110ms (vs 200-500ms for Google Vision API)

12-16 20:16:19.602 17816 18181 D LocalTextExtractionService: 💰 Cost: FREE (vs ~$1.50 per 1000 requests for Google Vision)

12-16 20:16:19.602 17816 18181 D LocalTextExtractionService: 🔒 Privacy: ON-DEVICE (vs cloud processing)

12-16 20:16:19.602 17816 18181 D LocalTextExtractionService: 🔍 ═══════════════════════════════════════

12-16 20:16:19.602 17816 18181 D LocalTextExtractionService: 

12-16 20:16:19.645 17816 18181 D ScreenCaptureService: 📸 Frame captured: 720x1600

12-16 20:16:20.449 17816 17816 V ScreenCaptureModule: 🎯 Frame requested by service

12-16 20:16:20.501 17816 21765 D LocalTextExtractionService: 📸 Frame captured: 720x1600

12-16 20:16:20.578 17816 21765 D LocalTextExtractionService: 

12-16 20:16:20.578 17816 21765 D LocalTextExtractionService: 🔍 ═══════════════════════════════════════

12-16 20:16:20.578 17816 21765 D LocalTextExtractionService: 🔍 LOCAL ML TEXT EXTRACTION RESULT

12-16 20:16:20.578 17816 21765 D LocalTextExtractionService: 🔍 ═══════════════════════════════════════

12-16 20:16:20.578 17816 21765 D LocalTextExtractionService: 📱 Frame: 720x1600

12-16 20:16:20.578 17816 21765 D LocalTextExtractionService: 📝 Extracted Text: ""

12-16 20:16:20.578 17816 21765 D LocalTextExtractionService: 📊 Confidence: 0%

12-16 20:16:20.578 17816 21765 D LocalTextExtractionService: 📏 Text Density: 0%

12-16 20:16:20.578 17816 21765 D LocalTextExtractionService: ⏱️ ML Processing Time: 58ms

12-16 20:16:20.578 17816 21765 D LocalTextExtractionService: ⏱️ Total Time (capture + ML): 76ms

12-16 20:16:20.578 17816 21765 D LocalTextExtractionService: 🎯 Text Regions: 0

12-16 20:16:20.578 17816 21765 D LocalTextExtractionService: 💾 Used Cache: No

12-16 20:16:20.578 17816 21765 D LocalTextExtractionService: 🎯 ROI Detected: No

12-16 20:16:20.578 17816 21765 D LocalTextExtractionService: ✅ Validation Passed: No

12-16 20:16:20.578 17816 21765 D LocalTextExtractionService: 🔄 Fallback Used: No

12-16 20:16:20.578 17816 21765 D LocalTextExtractionService: ⭐ High Quality: No

12-16 20:16:20.578 17816 21765 D LocalTextExtractionService: 🚀 Performance: 76ms (vs 200-500ms for Google Vision API)

12-16 20:16:20.578 17816 21765 D LocalTextExtractionService: 💰 Cost: FREE (vs ~$1.50 per 1000 requests for Google Vision)

12-16 20:16:20.578 17816 21765 D LocalTextExtractionService: 🔒 Privacy: ON-DEVICE (vs cloud processing)

12-16 20:16:20.578 17816 21765 D LocalTextExtractionService: 🔍 ═══════════════════════════════════════

12-16 20:16:20.578 17816 21765 D LocalTextExtractionService: 

12-16 20:16:20.715 17816 17816 V ScreenCaptureModule: 🎯 Frame requested by service

12-16 20:16:20.768 17816 21765 D ScreenCaptureService: 📸 Frame captured: 720x1600

12-16 20:16:21.437 17816 17816 V ScreenCaptureModule: 🎯 Frame requested by service

12-16 20:16:21.490 17816 18182 D LocalTextExtractionService: 📸 Frame captured: 720x1600

12-16 20:16:21.576 17816 18182 D LocalTextExtractionService: 

12-16 20:16:21.576 17816 18182 D LocalTextExtractionService: 🔍 ═══════════════════════════════════════

12-16 20:16:21.576 17816 18182 D LocalTextExtractionService: 🔍 LOCAL ML TEXT EXTRACTION RESULT

12-16 20:16:21.576 17816 18182 D LocalTextExtractionService: 🔍 ═══════════════════════════════════════

12-16 20:16:21.576 17816 18182 D LocalTextExtractionService: 📱 Frame: 720x1600

12-16 20:16:21.577 17816 18182 D LocalTextExtractionService: 📝 Extracted Text: ""

12-16 20:16:21.577 17816 18182 D LocalTextExtractionService: 📊 Confidence: 0%

12-16 20:16:21.577 17816 18182 D LocalTextExtractionService: 📏 Text Density: 0%

12-16 20:16:21.577 17816 18182 D LocalTextExtractionService: ⏱️ ML Processing Time: 63ms

12-16 20:16:21.577 17816 18182 D LocalTextExtractionService: ⏱️ Total Time (capture + ML): 85ms

12-16 20:16:21.577 17816 18182 D LocalTextExtractionService: 🎯 Text Regions: 0

12-16 20:16:21.577 17816 18182 D LocalTextExtractionService: 💾 Used Cache: No

12-16 20:16:21.577 17816 18182 D LocalTextExtractionService: 🎯 ROI Detected: No

12-16 20:16:21.577 17816 18182 D LocalTextExtractionService: ✅ Validation Passed: No

12-16 20:16:21.577 17816 18182 D LocalTextExtractionService: 🔄 Fallback Used: No

12-16 20:16:21.577 17816 18182 D LocalTextExtractionService: ⭐ High Quality: No

12-16 20:16:21.577 17816 18182 D LocalTextExtractionService: 🚀 Performance: 85ms (vs 200-500ms for Google Vision API)

12-16 20:16:21.577 17816 18182 D LocalTextExtractionService: 💰 Cost: FREE (vs ~$1.50 per 1000 requests for Google Vision)

12-16 20:16:21.578 17816 18182 D LocalTextExtractionService: 🔒 Privacy: ON-DEVICE (vs cloud processing)

12-16 20:16:21.578 17816 18182 D LocalTextExtractionService: 🔍 ═══════════════════════════════════════

12-16 20:16:21.578 17816 18182 D LocalTextExtractionService: 

12-16 20:16:21.826 17816 17816 V ScreenCaptureModule: 🎯 Frame requested by service

12-16 20:16:21.878 17816 18182 D ScreenCaptureService: 📸 Frame captured: 720x1600

12-16 20:16:22.442 17816 17816 V ScreenCaptureModule: 🎯 Frame requested by service

12-16 20:16:22.496 17816 20004 D LocalTextExtractionService: 📸 Frame captured: 720x1600

12-16 20:16:22.611 17816 20004 D LocalTextExtractionService: 

12-16 20:16:22.611 17816 20004 D LocalTextExtractionService: 🔍 ═══════════════════════════════════════

12-16 20:16:22.611 17816 20004 D LocalTextExtractionService: 🔍 LOCAL ML TEXT EXTRACTION RESULT

12-16 20:16:22.611 17816 20004 D LocalTextExtractionService: 🔍 ═══════════════════════════════════════

12-16 20:16:22.612 17816 20004 D LocalTextExtractionService: 📱 Frame: 720x1600

12-16 20:16:22.612 17816 20004 D LocalTextExtractionService: 📝 Extracted Text: ""

12-16 20:16:22.612 17816 20004 D LocalTextExtractionService: 📊 Confidence: 0%

12-16 20:16:22.613 17816 20004 D LocalTextExtractionService: 📏 Text Density: 0%

12-16 20:16:22.613 17816 20004 D LocalTextExtractionService: ⏱️ ML Processing Time: 76ms

12-16 20:16:22.613 17816 20004 D LocalTextExtractionService: ⏱️ Total Time (capture + ML): 115ms

12-16 20:16:22.614 17816 20004 D LocalTextExtractionService: 🎯 Text Regions: 0

12-16 20:16:22.614 17816 20004 D LocalTextExtractionService: 💾 Used Cache: No

12-16 20:16:22.614 17816 20004 D LocalTextExtractionService: 🎯 ROI Detected: No

12-16 20:16:22.614 17816 20004 D LocalTextExtractionService: ✅ Validation Passed: No

12-16 20:16:22.614 17816 20004 D LocalTextExtractionService: 🔄 Fallback Used: No

12-16 20:16:22.614 17816 20004 D LocalTextExtractionService: ⭐ High Quality: No

12-16 20:16:22.614 17816 20004 D LocalTextExtractionService: 🚀 Performance: 115ms (vs 200-500ms for Google Vision API)

12-16 20:16:22.614 17816 20004 D LocalTextExtractionService: 💰 Cost: FREE (vs ~$1.50 per 1000 requests for Google Vision)

12-16 20:16:22.614 17816 20004 D LocalTextExtractionService: 🔒 Privacy: ON-DEVICE (vs cloud processing)

12-16 20:16:22.614 17816 20004 D LocalTextExtractionService: 🔍 ═══════════════════════════════════════

12-16 20:16:22.614 17816 20004 D LocalTextExtractionService: 

12-16 20:16:22.745 17816 17816 V ScreenCaptureModule: 🎯 Frame requested by service

12-16 20:16:22.797 17816 20004 D ScreenCaptureService: 📸 Frame captured: 720x1600

12-16 20:16:23.442 17816 17816 V ScreenCaptureModule: 🎯 Frame requested by service

12-16 20:16:23.494 17816 18182 D LocalTextExtractionService: 📸 Frame captured: 720x1600

12-16 20:16:23.565 17816 18182 D LocalTextExtractionService: 

12-16 20:16:23.566 17816 18182 D LocalTextExtractionService: 🔍 ═══════════════════════════════════════

12-16 20:16:23.566 17816 18182 D LocalTextExtractionService: 🔍 LOCAL ML TEXT EXTRACTION RESULT

12-16 20:16:23.566 17816 18182 D LocalTextExtractionService: 🔍 ═══════════════════════════════════════

12-16 20:16:23.566 17816 18182 D LocalTextExtractionService: 📱 Frame: 720x1600

12-16 20:16:23.566 17816 18182 D LocalTextExtractionService: 📝 Extracted Text: ""

12-16 20:16:23.566 17816 18182 D LocalTextExtractionService: 📊 Confidence: 0%

12-16 20:16:23.566 17816 18182 D LocalTextExtractionService: 📏 Text Density: 0%

12-16 20:16:23.566 17816 18182 D LocalTextExtractionService: ⏱️ ML Processing Time: 52ms

12-16 20:16:23.566 17816 18182 D LocalTextExtractionService: ⏱️ Total Time (capture + ML): 71ms

12-16 20:16:23.566 17816 18182 D LocalTextExtractionService: 🎯 Text Regions: 0

12-16 20:16:23.566 17816 18182 D LocalTextExtractionService: 💾 Used Cache: No

12-16 20:16:23.566 17816 18182 D LocalTextExtractionService: 🎯 ROI Detected: No

12-16 20:16:23.567 17816 18182 D LocalTextExtractionService: ✅ Validation Passed: No

12-16 20:16:23.567 17816 18182 D LocalTextExtractionService: 🔄 Fallback Used: No

12-16 20:16:23.567 17816 18182 D LocalTextExtractionService: ⭐ High Quality: No

12-16 20:16:23.567 17816 18182 D LocalTextExtractionService: 🚀 Performance: 71ms (vs 200-500ms for Google Vision API)

12-16 20:16:23.567 17816 18182 D LocalTextExtractionService: 💰 Cost: FREE (vs ~$1.50 per 1000 requests for Google Vision)

12-16 20:16:23.567 17816 18182 D LocalTextExtractionService: 🔒 Privacy: ON-DEVICE (vs cloud processing)

12-16 20:16:23.567 17816 18182 D LocalTextExtractionService: 🔍 ═══════════════════════════════════════

12-16 20:16:23.567 17816 18182 D LocalTextExtractionService: 

12-16 20:16:24.056 17816 17816 V ScreenCaptureModule: 🎯 Frame requested by service

12-16 20:16:24.110 17816 18182 D ScreenCaptureService: 📸 Frame captured: 720x1600

12-16 20:16:24.449 17816 17816 V ScreenCaptureModule: 🎯 Frame requested by service

12-16 20:16:24.502 17816 21765 D LocalTextExtractionService: 📸 Frame captured: 720x1600

12-16 20:16:24.614 17816 21765 D LocalTextExtractionService: 

12-16 20:16:24.614 17816 21765 D LocalTextExtractionService: 🔍 ═══════════════════════════════════════

12-16 20:16:24.614 17816 21765 D LocalTextExtractionService: 🔍 LOCAL ML TEXT EXTRACTION RESULT

12-16 20:16:24.614 17816 21765 D LocalTextExtractionService: 🔍 ═══════════════════════════════════════

12-16 20:16:24.614 17816 21765 D LocalTextExtractionService: 📱 Frame: 720x1600

12-16 20:16:24.614 17816 21765 D LocalTextExtractionService: 📝 Extracted Text: ""

12-16 20:16:24.615 17816 21765 D LocalTextExtractionService: 📊 Confidence: 0%

12-16 20:16:24.615 17816 21765 D LocalTextExtractionService: 📏 Text Density: 0%

12-16 20:16:24.615 17816 21765 D LocalTextExtractionService: ⏱️ ML Processing Time: 77ms

12-16 20:16:24.615 17816 21765 D LocalTextExtractionService: ⏱️ Total Time (capture + ML): 112ms

12-16 20:16:24.615 17816 21765 D LocalTextExtractionService: 🎯 Text Regions: 0

12-16 20:16:24.615 17816 21765 D LocalTextExtractionService: 💾 Used Cache: No

12-16 20:16:24.615 17816 21765 D LocalTextExtractionService: 🎯 ROI Detected: No

12-16 20:16:24.615 17816 21765 D LocalTextExtractionService: ✅ Validation Passed: No

12-16 20:16:24.615 17816 21765 D LocalTextExtractionService: 🔄 Fallback Used: No

12-16 20:16:24.616 17816 21765 D LocalTextExtractionService: ⭐ High Quality: No

12-16 20:16:24.616 17816 21765 D LocalTextExtractionService: 🚀 Performance: 112ms (vs 200-500ms for Google Vision API)

12-16 20:16:24.616 17816 21765 D LocalTextExtractionService: 💰 Cost: FREE (vs ~$1.50 per 1000 requests for Google Vision)

12-16 20:16:24.616 17816 21765 D LocalTextExtractionService: 🔒 Privacy: ON-DEVICE (vs cloud processing)

12-16 20:16:24.616 17816 21765 D LocalTextExtractionService: 🔍 ═══════════════════════════════════════

12-16 20:16:24.617 17816 21765 D LocalTextExtractionService: 

12-16 20:16:25.283 17816 17816 V ScreenCaptureModule: 🎯 Frame requested by service

12-16 20:16:25.337 17816 18182 D ScreenCaptureService: 📸 Frame captured: 720x1600

12-16 20:16:25.452 17816 17816 V ScreenCaptureModule: 🎯 Frame requested by service

12-16 20:16:25.505 17816 21765 D LocalTextExtractionService: 📸 Frame captured: 720x1600

12-16 20:16:25.618 17816 21765 D LocalTextExtractionService: 

12-16 20:16:25.618 17816 21765 D LocalTextExtractionService: 🔍 ═══════════════════════════════════════

12-16 20:16:25.619 17816 21765 D LocalTextExtractionService: 🔍 LOCAL ML TEXT EXTRACTION RESULT

12-16 20:16:25.619 17816 21765 D LocalTextExtractionService: 🔍 ═══════════════════════════════════════

12-16 20:16:25.619 17816 21765 D LocalTextExtractionService: 📱 Frame: 720x1600

12-16 20:16:25.619 17816 21765 D LocalTextExtractionService: 📝 Extracted Text: ""

12-16 20:16:25.619 17816 21765 D LocalTextExtractionService: 📊 Confidence: 0%

12-16 20:16:25.619 17816 21765 D LocalTextExtractionService: 📏 Text Density: 0%

12-16 20:16:25.619 17816 21765 D LocalTextExtractionService: ⏱️ ML Processing Time: 74ms

12-16 20:16:25.619 17816 21765 D LocalTextExtractionService: ⏱️ Total Time (capture + ML): 112ms

12-16 20:16:25.619 17816 21765 D LocalTextExtractionService: 🎯 Text Regions: 0

12-16 20:16:25.619 17816 21765 D LocalTextExtractionService: 💾 Used Cache: No

12-16 20:16:25.619 17816 21765 D LocalTextExtractionService: 🎯 ROI Detected: No

12-16 20:16:25.619 17816 21765 D LocalTextExtractionService: ✅ Validation Passed: No

12-16 20:16:25.619 17816 21765 D LocalTextExtractionService: 🔄 Fallback Used: No

12-16 20:16:25.619 17816 21765 D LocalTextExtractionService: ⭐ High Quality: No

12-16 20:16:25.619 17816 21765 D LocalTextExtractionService: 🚀 Performance: 112ms (vs 200-500ms for Google Vision API)

12-16 20:16:25.619 17816 21765 D LocalTextExtractionService: 💰 Cost: FREE (vs ~$1.50 per 1000 requests for Google Vision)

12-16 20:16:25.619 17816 21765 D LocalTextExtractionService: 🔒 Privacy: ON-DEVICE (vs cloud processing)

12-16 20:16:25.619 17816 21765 D LocalTextExtractionService: 🔍 ═══════════════════════════════════════

12-16 20:16:25.619 17816 21765 D LocalTextExtractionService: 

12-16 20:16:26.457 17816 17816 V ScreenCaptureModule: 🎯 Frame requested by service

12-16 20:16:26.512 17816 20004 D LocalTextExtractionService: 📸 Frame captured: 720x1600

12-16 20:16:26.619 17816 17816 V ScreenCaptureModule: 🎯 Frame requested by service

12-16 20:16:26.629 17816 18182 D LocalTextExtractionService: 

12-16 20:16:26.629 17816 18182 D LocalTextExtractionService: 🔍 ═══════════════════════════════════════

12-16 20:16:26.629 17816 18182 D LocalTextExtractionService: 🔍 LOCAL ML TEXT EXTRACTION RESULT

12-16 20:16:26.629 17816 18182 D LocalTextExtractionService: 🔍 ═══════════════════════════════════════

12-16 20:16:26.629 17816 18182 D LocalTextExtractionService: 📱 Frame: 720x1600

12-16 20:16:26.629 17816 18182 D LocalTextExtractionService: 📝 Extracted Text: ""

12-16 20:16:26.630 17816 18182 D LocalTextExtractionService: 📊 Confidence: 0%

12-16 20:16:26.630 17816 18182 D LocalTextExtractionService: 📏 Text Density: 0%

12-16 20:16:26.630 17816 18182 D LocalTextExtractionService: ⏱️ ML Processing Time: 73ms

12-16 20:16:26.630 17816 18182 D LocalTextExtractionService: ⏱️ Total Time (capture + ML): 116ms

12-16 20:16:26.630 17816 18182 D LocalTextExtractionService: 🎯 Text Regions: 0

12-16 20:16:26.630 17816 18182 D LocalTextExtractionService: 💾 Used Cache: No

12-16 20:16:26.630 17816 18182 D LocalTextExtractionService: 🎯 ROI Detected: No

12-16 20:16:26.630 17816 18182 D LocalTextExtractionService: ✅ Validation Passed: No

12-16 20:16:26.630 17816 18182 D LocalTextExtractionService: 🔄 Fallback Used: No

12-16 20:16:26.630 17816 18182 D LocalTextExtractionService: ⭐ High Quality: No

12-16 20:16:26.630 17816 18182 D LocalTextExtractionService: 🚀 Performance: 116ms (vs 200-500ms for Google Vision API)

12-16 20:16:26.630 17816 18182 D LocalTextExtractionService: 💰 Cost: FREE (vs ~$1.50 per 1000 requests for Google Vision)

12-16 20:16:26.630 17816 18182 D LocalTextExtractionService: 🔒 Privacy: ON-DEVICE (vs cloud processing)

12-16 20:16:26.630 17816 18182 D LocalTextExtractionService: 🔍 ═══════════════════════════════════════

12-16 20:16:26.630 17816 18182 D LocalTextExtractionService: 

12-16 20:16:26.671 17816 18182 D ScreenCaptureService: 📸 Frame captured: 720x1600

12-16 20:16:27.465 17816 17816 V ScreenCaptureModule: 🎯 Frame requested by service

12-16 20:16:27.517 17816 20004 D LocalTextExtractionService: 📸 Frame captured: 720x1600

12-16 20:16:27.539 17816 17816 V ScreenCaptureModule: 🎯 Frame requested by service

12-16 20:16:27.591 17816 18181 D ScreenCaptureService: 📸 Frame captured: 720x1600

12-16 20:16:27.592 17816 20004 D LocalTextExtractionService: 

12-16 20:16:27.592 17816 20004 D LocalTextExtractionService: 🔍 ═══════════════════════════════════════

12-16 20:16:27.592 17816 20004 D LocalTextExtractionService: 🔍 LOCAL ML TEXT EXTRACTION RESULT

12-16 20:16:27.592 17816 20004 D LocalTextExtractionService: 🔍 ═══════════════════════════════════════

12-16 20:16:27.592 17816 20004 D LocalTextExtractionService: 📱 Frame: 720x1600

12-16 20:16:27.592 17816 20004 D LocalTextExtractionService: 📝 Extracted Text: ""

12-16 20:16:27.592 17816 20004 D LocalTextExtractionService: 📊 Confidence: 0%

12-16 20:16:27.592 17816 20004 D LocalTextExtractionService: 📏 Text Density: 0%

12-16 20:16:27.592 17816 20004 D LocalTextExtractionService: ⏱️ ML Processing Time: 55ms

12-16 20:16:27.592 17816 20004 D LocalTextExtractionService: ⏱️ Total Time (capture + ML): 74ms

12-16 20:16:27.592 17816 20004 D LocalTextExtractionService: 🎯 Text Regions: 0

12-16 20:16:27.592 17816 20004 D LocalTextExtractionService: 💾 Used Cache: No

12-16 20:16:27.592 17816 20004 D LocalTextExtractionService: 🎯 ROI Detected: No

12-16 20:16:27.592 17816 20004 D LocalTextExtractionService: ✅ Validation Passed: No

12-16 20:16:27.592 17816 20004 D LocalTextExtractionService: 🔄 Fallback Used: No

12-16 20:16:27.592 17816 20004 D LocalTextExtractionService: ⭐ High Quality: No

12-16 20:16:27.592 17816 20004 D LocalTextExtractionService: 🚀 Performance: 74ms (vs 200-500ms for Google Vision API)

12-16 20:16:27.592 17816 20004 D LocalTextExtractionService: 💰 Cost: FREE (vs ~$1.50 per 1000 requests for Google Vision)

12-16 20:16:27.592 17816 20004 D LocalTextExtractionService: 🔒 Privacy: ON-DEVICE (vs cloud processing)

12-16 20:16:27.592 17816 20004 D LocalTextExtractionService: 🔍 ═══════════════════════════════════════

12-16 20:16:27.592 17816 20004 D LocalTextExtractionService: 

12-16 20:16:28.461 17816 17816 V ScreenCaptureModule: 🎯 Frame requested by service

12-16 20:16:28.512 17816 18181 D LocalTextExtractionService: 📸 Frame captured: 720x1600

12-16 20:16:28.534 17816 18175 D LocalTextExtractionService: 🛑 Stopping local text extraction loop

12-16 20:16:28.546 17816 21765 E LocalTextExtractionService: ❌ Error in local text extraction loop: Job was cancelled

12-16 20:16:28.546 17816 21765 D LocalTextExtractionService: ⏹️ Local text extraction loop stopped

12-16 20:16:28.547 17816 17816 D LocalTextExtractionService: 🔋 WakeLock released

12-16 20:16:28.547 17816 17816 D LocalTextExtractionService: 

12-16 20:16:28.547 17816 17816 D LocalTextExtractionService: 📊 ═══════════════════════════════════════

12-16 20:16:28.547 17816 17816 D LocalTextExtractionService: 📊 FINAL LOCAL ML EXTRACTION STATISTICS

12-16 20:16:28.547 17816 17816 D LocalTextExtractionService: 📊 ═══════════════════════════════════════

12-16 20:16:28.548 17816 17816 D LocalTextExtractionService: 📸 Total Captures: 46

12-16 20:16:28.548 17816 17816 D LocalTextExtractionService: ✅ Successful Extractions: 0

12-16 20:16:28.548 17816 17816 D LocalTextExtractionService: 📈 Success Rate: 0%

12-16 20:16:28.548 17816 17816 D LocalTextExtractionService: ⏱️ Average Processing Time: 161ms

12-16 20:16:28.548 17816 17816 D LocalTextExtractionService: 📝 Total Text Extracted: 0 characters

12-16 20:16:28.548 17816 17816 D LocalTextExtractionService: 📊 Average Text per Extraction: 0 characters

12-16 20:16:28.548 17816 17816 D LocalTextExtractionService: 🚀 Performance Advantage: 4-10x faster than Google Vision API

12-16 20:16:28.548 17816 17816 D LocalTextExtractionService: 💰 Cost Savings: 100% (no API costs)

12-16 20:16:28.548 17816 17816 D LocalTextExtractionService: 🔒 Privacy: 100% on-device processing

12-16 20:16:28.548 17816 17816 D LocalTextExtractionService: 📊 ═══════════════════════════════════════

12-16 20:16:28.548 17816 17816 D LocalTextExtractionService: 

12-16 20:16:28.548 17816 17816 D LocalTextExtractionService: ❌ LocalTextExtractionService destroyed

12-16 20:16:28.554 17816 17816 V ScreenCaptureModule: 🎯 Frame requested by service

12-16 20:16:28.605 17816 18181 D LocalTextExtractionService: 

12-16 20:16:28.605 17816 18181 D LocalTextExtractionService: 🔍 ═══════════════════════════════════════

12-16 20:16:28.605 17816 18181 D LocalTextExtractionService: 🔍 LOCAL ML TEXT EXTRACTION RESULT

12-16 20:16:28.605 17816 18181 D LocalTextExtractionService: 🔍 ═══════════════════════════════════════

12-16 20:16:28.606 17816 18181 D LocalTextExtractionService: 📱 Frame: 720x1600

12-16 20:16:28.606 17816 18182 D ScreenCaptureService: 📸 Frame captured: 720x1600

12-16 20:16:28.606 17816 18181 D LocalTextExtractionService: 📝 Extracted Text: ""

12-16 20:16:28.606 17816 18181 D LocalTextExtractionService: 📊 Confidence: 0%

12-16 20:16:28.606 17816 18181 D LocalTextExtractionService: 📏 Text Density: 0%

12-16 20:16:28.606 17816 18181 D LocalTextExtractionService: ⏱️ ML Processing Time: 70ms

12-16 20:16:28.606 17816 18181 D LocalTextExtractionService: ⏱️ Total Time (capture + ML): 93ms

12-16 20:16:28.606 17816 18181 D LocalTextExtractionService: 🎯 Text Regions: 0

12-16 20:16:28.606 17816 18181 D LocalTextExtractionService: 💾 Used Cache: No

12-16 20:16:28.606 17816 18181 D LocalTextExtractionService: 🎯 ROI Detected: No

12-16 20:16:28.606 17816 18181 D LocalTextExtractionService: ✅ Validation Passed: No

12-16 20:16:28.606 17816 18181 D LocalTextExtractionService: 🔄 Fallback Used: No

12-16 20:16:28.606 17816 18181 D LocalTextExtractionService: ⭐ High Quality: No

12-16 20:16:28.606 17816 18181 D LocalTextExtractionService: 🚀 Performance: 93ms (vs 200-500ms for Google Vision API)

12-16 20:16:28.606 17816 18181 D LocalTextExtractionService: 💰 Cost: FREE (vs ~$1.50 per 1000 requests for Google Vision)

12-16 20:16:28.606 17816 18181 D LocalTextExtractionService: 🔒 Privacy: ON-DEVICE (vs cloud processing)

12-16 20:16:28.606 17816 18181 D LocalTextExtractionService: 🔍 ═══════════════════════════════════════

12-16 20:16:28.606 17816 18181 D LocalTextExtractionService: 

12-16 20:16:28.786 17816 18174 I ReactNativeJS: ✅ Local text extraction service stopped

12-16 20:16:28.988 17816 18174 I ReactNativeJS: 📸 Total Captures: 0

12-16 20:16:28.989 17816 18174 I ReactNativeJS: ✅ Successful Extractions: 0

12-16 20:16:28.991 17816 18174 I ReactNativeJS: ⏱️ Average Total Time: 0.0ms

12-16 20:16:28.994 17816 18174 I ReactNativeJS: 💾 Cache Hit Rate: 0.0%

12-16 20:16:28.995 17816 18174 I ReactNativeJS: 📝 Total Text Extracted: 0 characters

12-16 20:16:28.996 17816 18174 I ReactNativeJS: 💾 Cache Performance: 0 hits, 0 misses

12-16 20:16:28.997 17816 18174 I ReactNativeJS: 💾 Cache Efficiency: 0%





› Press ? │ show all commands

Android Bundled 46ms node_modules/expo-router/entry.js (1 module)

 LOG  📊 Cache stats loaded: {"hitRate": 0, "hits": 0, "isEffective": false, "maxSize": 50, "memoryUsageKB": 0, "misses": 0, "size": 0}

 LOG  🧪 Testing single screen capture and text extraction...

 LOG  🎬 Requesting screen capture permission...

 LOG  📸 Starting screen capture...

 LOG  🤖 Starting local text extraction service...

 LOG  ✅ Local text extraction service started

 LOG  🛑 Stopping local text capture...

 LOG  ✅ Local text extraction service stopped

 LOG  

 LOG  📊 ═══════════════════════════════════════

 LOG  📊 FINAL PERFORMANCE REPORT

 LOG  📊 ═══════════════════════════════════════

 LOG  📸 Total Captures: 0

 LOG  ✅ Successful Extractions: 0

 LOG  📈 Success Rate: 0%

 LOG  ⏱️ Average Total Time: 0.0ms

 LOG  📊 Average Confidence: 0.0%

 LOG  💾 Cache Hit Rate: 0.0%

 LOG  📝 Total Text Extracted: 0 characters

 LOG  💾 Cache Performance: 0 hits, 0 misses

 LOG  💾 Cache Efficiency: 0%

 LOG  📊 ═══════════════════════════════════════

 LOG  

 LOG  🧪 Testing single screen capture and text extraction...

 LOG  🎬 Requesting screen capture permission...

 LOG  📸 Starting screen capture...

 LOG  🤖 Starting local text extraction service...

 LOG  ✅ Local text extraction service started

 LOG  🛑 Stopping local text capture...

 LOG  ✅ Local text extraction service stopped

 LOG  

 LOG  📊 ═══════════════════════════════════════

 LOG  📊 FINAL PERFORMANCE REPORT

 LOG  📊 ═══════════════════════════════════════

 LOG  📸 Total Captures: 0

 LOG  ✅ Successful Extractions: 0

 LOG  📈 Success Rate: 0%

 LOG  ⏱️ Average Total Time: 0.0ms

 LOG  📊 Average Confidence: 0.0%

 LOG  💾 Cache Hit Rate: 0.0%

 LOG  📝 Total Text Extracted: 0 characters

 LOG  💾 Cache Performance: 0 hits, 0 misses

 LOG  💾 Cache Efficiency: 0%

 LOG  📊 ═══════════════════════════════════════

 LOG  

 LOG  🎬 Requesting screen capture permission...

 LOG  📸 Starting screen capture...

 LOG  🤖 Starting local text extraction service...

 LOG  ✅ Local text extraction service started

 LOG  🛑 Stopping local text capture...

 LOG  ✅ Local text extraction service stopped

 LOG  

 LOG  📊 ═══════════════════════════════════════

 LOG  📊 FINAL PERFORMANCE REPORT

 LOG  📊 ═══════════════════════════════════════

 LOG  📸 Total Captures: 0

 LOG  ✅ Successful Extractions: 0

 LOG  📈 Success Rate: 0%

 LOG  ⏱️ Average Total Time: 0.0ms

 LOG  📊 Average Confidence: 0.0%

 LOG  💾 Cache Hit Rate: 0.0%

 LOG  📝 Total Text Extracted: 0 characters

 LOG  💾 Cache Performance: 0 hits, 0 misses

 LOG  💾 Cache Efficiency: 0%

 LOG  📊 ═══════════════════════════════════════

 LOG  



sorry but the logs sem messy:



when i press  test single extraction i get Error Failed to capture screen, but in console i see the text being extracted but its reapeating the loop aka it should have done one time but its dooining multiple times

2. when i click start live capture at frist yes it extracts the text live but then it stops i think bc it was on the same image (same ) text and it was reapeating so it entered an safe mode to save power but then it couldent exit it (this is what i think happened maybe soemthign different happened)

3. live performance stats arent shaowing real date it is at 0



so conclusion as proof of concpet that you can do local text extract we can clasicfy but as soemthing stable no, its unstable + bc of its instability i cant test how fast it is, so if you could fix that that would be amazing. aditional logs