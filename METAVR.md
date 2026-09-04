# METAVR.md — The Blue Alliance on Meta Horizon OS

Design and status for shipping TBA to the Meta Horizon Store as a native 2D panel app,
built as the **`metavr` product flavor of `:app`**.

Researched 2026-08-31 against Meta's docs; revised 2026-09-02 to record what the
implementation and on-device testing settled. Anything still undocumented *and* untested is
flagged **UNVERIFIED** and collected in [§9](#9-still-open).

Naming is `metavr` / MetaVR throughout. "quest" survives only in literals Meta owns
(`com.oculus.supportedDevices`, `upload-quest-build`, `VRC.Quest.*`).

---

## 1. Where this stands

Horizon OS is AOSP (Android 14 / API 34 since v76) with **no Google Mobile Services**. Native
2D Android apps are first-class on the Horizon Store and run as movable, resizable panels;
Twitch, Discord, and Spotify already ship 2D apps there.

The GMS coupling in this repo turned out to be narrow. Per Firebase's own compatibility
table, `firebase-auth`, `firebase-config`, `firebase-crashlytics`, and `firebase-analytics`
all work **without** Play services — only FCM and the Google Sign-In *UI* are dead. So the
flavor swaps the sign-in front door, drops push and widgets, and shares everything else.

Built and verified on a Quest 3 (Horizon OS v207) and the Meta Spatial Simulator:

- the panel opens at exactly its declared size, landscape, no crashes, ~2.6 s cold start;
- Firebase Auth / Remote Config / Crashlytics / Analytics run fine with no GMS on the device;
- browser OAuth sign-in completes end to end, including Horizon's system passkey UI;
- **three TBA panels run side by side, all resumed** — the multi-panel mechanism works.

Not done: the store submission itself (org verification, artwork, first upload), a dedicated
release keystore, and the multi-panel webcast viewer that the multi-panel finding unblocks.

---

## 2. Why a flavor, not a module or a TWA

| Option | Verdict |
|---|---|
| Bubblewrap TWA of the PWA | Cheapest store presence, but no multi-panel, no push of any kind (Quest Browser has no web push), no offline. Independent of this repo. |
| **`metavr` flavor of `:app`** | **Chosen.** Panels are pointer-driven (laser ≈ mouse), so `:app`'s touch Compose UI works nearly as-is: all of `ui/`, `navigation/`, the Room cache, myTBA. The seam is three interfaces plus a manifest overlay. |
| A separate `:metavr` module (the `:tv` precedent) | `:tv` reimplemented its two screens from scratch and shares only `:core-network`. Full-featured this way means duplicating ~40 screens, or first extracting `:core-ui`/`:core-data` out of `:app` — a modularization project on top of the same seams. A flavor can be promoted to a module later if the UI ever diverges hard. |

`:tv`'s transferable lesson is discipline about hover and focus states and large hit targets,
not its D-pad plumbing: Horizon input maps to the touch model, not the TV focus model.

---

## 3. Platform facts

Hub: <https://developers.meta.com/horizon/documentation/android-apps/horizon-os-apps/>

### SDK levels and packaging

Horizon OS v76+ is Android 14 / API 34. The flavor sets `minSdk 32` (Meta's release table
permits 29–34 and recommends 32) and `targetSdk 34` (mandatory for apps created after
2026-03-01). Uploads are **APK only** — no AAB — 64-bit only, self-managed signing key, and
the build number must strictly increase on every upload, rollbacks included.

Meta's upload validator *rejects* rather than warns, and it runs on the first ALPHA upload,
not just at submission. `scripts/verify-metavr-apk.sh` asserts the locally checkable rules
(installLocation, `excludeFromRecents`, SDK levels, ABIs, v2 signature, permission
allowlist) and runs in CI against `assembleMetavrRelease`.

### Manifest

`app/src/metavr/AndroidManifest.xml` is the whole overlay and is commented rule by rule. The
load-bearing parts: `com.oculus.supportedDevices`; deliberately **no**
`com.oculus.intent.category.VR` (that marks an app immersive-only); headtracking
`required="false"`; `installLocation="auto"`; `excludeFromRecents="true"` on the launcher
activity; a `<layout>` declaring the panel size; and `tools:node="remove"` for
`POST_NOTIFICATIONS` and the five ads/GMS permissions `firebase-analytics` injects, which
VRC.Quest.Security.2 (minimum permissions) would otherwise invite questions about.

Panel size is **exactly what `<layout>` declares** — verified 1024×640dp at 200 dpi
(1280×800 px, ~0.88 m wide) on hardware. The shell lets the user resize down to its own
384×500dp minimum, so the activity stays resizeable and takes ordinary Android config
changes.

Meta's launcher does not render adaptive icons and reads only `mipmap-xxxhdpi`, so the
flavor ships a 512×512 raster there and declares the icon on the activity as well as the
application. (For a *store* install the library tile actually comes from the Cover Square
store asset, not the APK; the raster is what a sideload has.)

### Multi-panel — verified

`FLAG_ACTIVITY_LAUNCH_ADJACENT | NEW_TASK | MULTIPLE_TASK` (0x18001000) opens a second panel
with no extra SDK, exactly as Meta's features overview claims. On a Quest 3 this ran **three
MainActivity instances in one process as three side-by-side panels, with no manifest
changes** (standard `launchMode`), and — the important part — **all three were
`topResumedActivity` simultaneously**. Horizon runs multi-resume, so an unfocused but visible
panel stays RESUMED and keeps rendering. That is what makes a multi-panel webcast viewer
viable without per-panel foreground-service gymnastics.

Placement is the OS's and the user's: grab edges to move, corners to resize, hinged 2–3 panel
groups, theater mode to enlarge one panel. A plain 2D app gets no programmatic world-space
positioning — that is Spatial SDK territory (§7).

`excludeFromRecents="true"` is a Meta packaging requirement but sits in tension with this:
if Horizon's task switcher is how a put-down panel is re-summoned, the attribute may hide it.
It ships (omitting it is not an option) and is flagged for an in-headset check.

### Input and design bars

Controllers, hands, mouse, and stylus all arrive as standard Android motion events; keyboard
and gamepad go to the focused panel. Controller B/Y is Android back, but hands and mouse
users have no system back, so an in-app back affordance is required. Meta's bars: hit targets
≥48dp (60dp recommended), text ≥14px (18px+ recommended), 4.5:1 body contrast, no pure
white/black.

The laser pointer hovers constantly, so **hover states are load-bearing on this platform** in
a way they are not on a phone — several hover-affordance bugs found in-headset became
independent main-branch fixes.

### Toasts

Horizon composites toasts on its own shell surface (a `VolumetricToast` window), **outside**
the app's panel. Confirmed in-headset. Anything important enough to tell the user needs an
in-panel surface; see the UX debt in §8.

### WebView, media, DRM

WebView exists and appears in Meta's own Media Player sample; this repo already runs
WebView-in-Compose (`PitMapScreen.kt`). Widevine L1 and L3 work through standard `MediaDrm`
for native playback. Codecs: H.264, HEVC, VP9, AV1 (Quest 3+). Whether Widevine works *inside
WebView*, and how many hardware decoder instances run concurrently, are still open (§9).

Meta's media-app requirements may apply to us since TBA embeds streams: a keyboard-free login
flow (device code, QR, or companion app), a per-app mute control while audio plays, and
AudioFocus cooperation. The quality bars (ABR, time-to-1080p) target apps serving their own
video; Twitch and YouTube embeds carry their own — UNVERIFIED how review treats embeds.

---

## 4. GMS: what survives

Meta: "Horizon OS does not include Google Mobile Services, so calls into GMS APIs fail." No
microG or shim exists.

Per Firebase's [compatibility table](https://firebase.google.com/docs/android/android-play-services),
Auth, Remote Config, Crashlytics, Firestore/RTDB/Functions/Storage, and Performance work
without Play services; Analytics works minus ad-ID demographics; **FCM, App Check Play
Integrity, phone-number verification, Dynamic Links, and Firebase ML do not**. Confirmed on
hardware: no GMS packages on the device, warnings in logcat, no crashes, and Auth / Config /
Crashlytics / Analytics all functioning.

So exactly two things needed replacing:

1. **Sign-in UI.** `MainActivity` used Credential Manager + `GetGoogleIdOption`, both GMS.
   Only the front door changed — the back half
   (`signInWithCredential(GoogleAuthProvider.getCredential(idToken, null))`) works unchanged
   once *any* flow yields a Google ID token.
2. **FCM.** `TBAFirebaseMessagingService` and device-token registration are gms-only. myTBA
   falls back to pull-on-open, which already happens. No push exists on Horizon for anyone —
   web push is unsupported in the browser too, and Meta's User Notifications are
   dashboard-authored, reviewed templates with no per-user dynamic data. Fine for "Champs
   starts tomorrow", useless for match scores.

Glance widgets went to gms as well: Horizon has no widget host.

Meta's Platform SDK (entitlement check, IAP, deep linking, notifications) is skipped
entirely. The entitlement check is only "recommended" for 2D apps, but it is a mandatory
prerequisite for *any* other Platform SDK feature — so adopting one feature means adopting
the check. Nothing in Phase 1 needs it.

---

## 5. Sign-in

`AuthTokenInterceptor` attaches `Authorization: Bearer <firebase-id-token>` from
`authRepository.getIdToken()`; nothing else in the myTBA stack sees a token, and the backend
keeps validating Firebase ID tokens unchanged. So the seam is just how the credential is
obtained.

**`SignInLauncher`** (`app/src/main/.../auth/SignInLauncher.kt`) is that seam, bound per
flavor by a Hilt module in each source set: `CredentialManagerSignInLauncher` on gms,
`MetaVRSignInLauncher` on metavr. Its contract is two calls, and the shape matters:

- `register(activity, onSignedIn)` **must be called from `onCreate`, before STARTED**. A flow
  that hands off to the browser can outlive the process, and AndroidX only redelivers a
  restored result to an `ActivityResultLauncher` registered that early. Passing the callback
  at registration rather than at tap time is what lets a sign-in that finished while the
  process was dead still complete.
- `signIn()` launches from the registered activity.

That contract is why the sign-in seam and the AppAuth implementation are one change, not two.

**MetaVR implementation.** AppAuth for Android (`net.openid:appauth:0.11.1`), authorization
code + PKCE against Google's endpoints, asking for an OIDC ID token, then the shared
`signInWithCredential` path. The OAuth client is configured as `tba.oauth.client.id.metavr`
in `local.properties` → `BuildConfig.OAUTH_CLIENT_ID`; blank means sign-in reports itself
unavailable rather than opening a browser that cannot succeed. It has to be an installed-app
("iOS"-type) client, the only kind Google still allows custom-scheme redirects for, and the
redirect is the reversed client id (`com.googleusercontent.apps.<id>:/oauth2redirect`).
`MetaVROAuthConfig` derives that; `app/build.gradle.kts` spells the same rule out a second
time because AppAuth's `RedirectUriReceiverActivity` needs the scheme as a manifest literal.

The client must live in the **same Firebase project as the build type's
`google-services.json`** — `app/src/debug` is the dev project, `app/src/release` is prod — or
the token exchange succeeds and Firebase then rejects the audience. See
`local.properties.example`.

**Verified on hardware 2026-09-01.** Oculus Browser is the sole https handler, exports a
`CustomTabsService`, and the Custom Tab shares its cookie jar, so an existing browser session
completes sign-in without retyping anything. Google's login on Horizon routes through the
system passkey UI (`PasskeyAuthenticationActivity`); a stale passkey aborts the flow, which
AppAuth reports as `USER_CANCELED_AUTH_FLOW` — so deliberate and incidental cancels are
treated as "nothing went wrong" and stay quiet, while every other error keeps its loud path.

**Debug builds** use `BuildConfig.AUTH_EMULATOR` (`tba.auth.emulator`, default true in debug,
hard false in release). True signs a fake user into the local Firebase Auth emulator with no
OAuth at all; false runs the real flow, which is the only way to exercise AppAuth in a debug
build. The flag replaced a bare `BuildConfig.DEBUG` check for exactly that reason.

**Not built: a device-code / QR flow.** Meta's media-app rules want a keyboard-free login,
and it is the better in-headset UX regardless. Firebase has no RFC 8628 grant, so it needs a
small backend feature: device asks for a short code → user approves at
thebluealliance.com/activate on a phone → backend mints a Firebase custom token → device
calls `signInWithCustomToken()`. This shares machinery with the moderation-API auth work.
Deferred because browser OAuth turned out to work well in-headset.

---

## 6. The flavor

`gms` (Google Play) and `metavr` (Meta Horizon Store) on a `distribution` dimension. Both
keep `applicationId = com.thebluealliance.androidclient` (the stores are separate namespaces,
and sharing it lets metavr reuse the existing per-build-type `google-services.json` and the
deep-link setup); debug still gets `.development`.

The build wiring lives in `app/build.gradle.kts` and is commented there — flavor block,
gms-only dependencies (Glance, `firebase-messaging`, Credential Manager, googleid),
`metavrImplementation(libs.appauth)`, `playConfigs { register("metavr") { enabled = false } }`
so gradle-play-publisher never generates Play tasks for it, and a
`metavr.versioncode.offset` knob for re-cutting a Horizon upload from a commit that already
shipped one.

### Seams

| Seam | shared (`src/main`) | `src/gms/` | `src/metavr/` |
|---|---|---|---|
| Sign-in | `SignInLauncher` | `CredentialManagerSignInLauncher` | `MetaVRSignInLauncher` + `MetaVROAuthConfig` |
| Push | `PushRegistrar`, `DeviceIdProvider` | `DeviceRegistrationManager`, `TBAFirebaseMessagingService` + FCM manifest entries | `NoPushRegistrar` |
| Widgets | `WidgetRefresher` | `GlanceWidgetRefresher` + all of `widget/` | `NoWidgetRefresher` |
| Manifest | app-wide entries | FCM service, widget receiver/config activity | Horizon overlay (§3) |

Each is one Hilt `@Binds` module per source set, so nothing about the component graph
changes. Not seams, and verified fine as shared code: `AuthRepository`/`FirebaseAuth`,
`AuthTokenInterceptor`, Remote Config, Crashlytics, Analytics.

Extracting `SignInLauncher` also killed the `LocalActivity.current as MainActivity` cast in
`TBANavigation.kt`, which was a latent bug on any flavor.

### Webcasts (not yet built)

`WebcastDto` is already in `:core-network`; the phone UI is link-out only today. The plan:
hoist `:tv`'s tested `WebcastResolver` into `:core-network` (un-forking `:tv` in the
process), then a `WebcastActivity` hosting a Twitch or YouTube embed, launched once per
stream with the multi-panel flags from §3, with per-panel mute and a cap set by a decoder
probe. A cheap intermediate step exists: use the same flags on the *browser* intent so
webcast link-outs open as separate windows instead of tabs.

Two link-out findings from hardware, both worth fixing on main and both benefiting phones:
`twitch://stream/<channel>` opens the Twitch app directly on the channel, while the
https → browser → "open in app" interstitial loses the path and lands on Twitch home; and
YouTube VR declares youtube.com handlers but has no app-links verification, so the browser
always wins.

---

## 7. Dev loop, and Spatial SDK later

The **Meta Spatial Simulator** is a Horizon-flavored AVD for 2D panel apps and carries the
daily loop; `.claude/CLAUDE.md` has the exact commands. It is a normal adb device (API 34,
arm64, 200 dpi), so `scripts/emu` and Gradle work against it, but it is single-instance, cold
boots every time, ships **no browser** (sideload one to test sign-in) and **no GMS**. Panel
apps get exactly their declared `<layout>` size there, matching hardware.

For real hardware, `adb tcpip 5555` once over USB then `adb connect <headset-ip>:5555` gives
a wireless device that every existing script drives. The **`metavr` CLI** adds screenshots,
logcat, Perfetto traces, doc search, and — undocumented on developers.meta.com, discoverable
only from `metavr store --help` — first-class store commands (`store dist
upload|channels|apps|copy-build`, `store test-user`) with `--json` and `HZDB_TOKEN` env auth.
That is a better CI upload path than `ovr-platform-util`, which remains the fallback.
(Meta XR Operator, the agentic VR test loop, is Unity-only and irrelevant here.)

**Spatial SDK is a later, optional upgrade, not the entry fee.** It adds panels as textured
3D objects with programmatic placement, Compose panel registration, `VideoSurfacePanel`
(including DRM to secure surfaces), and per-entity spatial audio; a hybrid APK can carry both
2D panel activities and an immersive activity. It is still 0.x with hard toolchain pins and a
breaking-change cadence, activity-based panels are its expensive kind, and testing it needs
hardware. Revisit if users ask for spatial arrangement after the 2D shell (hinged pairs,
theater mode, user placement) proves insufficient.

---

## 8. Store, and known debt

The Horizon Store app page exists: **App ID 1288576881005575**, org 1288576324338964, with
the distribution agreement signed and a build-upload section present. There is no "2D app"
type to choose — 2D-ness is decided entirely by the manifest. Org verification (admin, via
gov ID, ~minutes; or business verification for the 501(c)(3), ~48 h) is mandatory before
publishing. No fee is documented anywhere.

Review is a VRC subset: packaging, stability, minimum permissions, privacy policy with a
stated deletion path, assets, content, publishing URLs. Budget 2 weeks, 4–5 if there is a
hard date. After approval, new binaries copied to Production ship with no further review;
metadata changes get 1–2 days.

Corrections to earlier research worth carrying forward:

- **ALPHA/BETA channels can precede the first Production review.** The Production channel
  has no subscribers until you submit, so pre-release channels are usable during development.
  The only gate on an ALPHA upload is the automated validator.
- **No CSV import/export** for testers — comma-separated emails or a shareable invite URL
  (auto- or manual-approval). Default 200 users per channel, raisable once to 2,500; the
  invite URL expires 90 days after the latest upload and a new upload reactivates it.
- **The library / task-switcher tile is the Cover Square (1440×1440) store asset**, not the
  APK icon; the 512×512 "Icon" asset is mobile-feed only. Budget the real design effort
  there.
- **Ads self-certification is a hard gate for every 2D app**, ad-free ones included.
- **VRC.Quest.Functional.12** requires the app to work for multiple entitled users on one
  headset — untested.
- **Functional.3 wants reviewer test credentials with 2FA disabled**, which for myTBA means
  provisioning a Google account we are willing to hand to Meta's reviewers.
- Horizon Start's supposed expansion to 2D/mobile devs is **not** supported by the current
  program page, and its terms want a "privately held" org — a poor fit for a nonprofit.

**UX debt found in-headset**, both worth fixing on main:

- Sign-in failures are effectively invisible, because toasts composite outside the panel and
  the app has no `SnackbarHost`. Needs an in-panel error surface.
- A failed myTBA sync renders as "No favorites yet" — indistinguishable from an empty
  account.
- Warm deep links do not route: `am start VIEW <tba url>` at a running app does nothing while
  cold start works. `MainActivity` parses the intent in `onCreate` but not `onNewIntent`. This
  is a main-branch bug that also affects notification taps.
- The event list occupies only the left third of a 1024dp panel. The empty right two-thirds
  is the concrete argument for an adaptive two-pane layout.

---

## 9. Still open

Everything below is undocumented by Meta and not yet answered on hardware.

1. **Widevine and stream embeds inside WebView** — do Twitch/YouTube embeds play, at what
   quality, under what autoplay policy? The WebView provider version is unknown. If DRM is
   limited there, the fallback is native ExoPlayer over HLS, or a Spatial SDK video surface.
2. **Simultaneous hardware decoder instances** — probe `getMaxSupportedInstances()` on
   device; it sets the panel cap for the webcast grid.
3. **Max concurrent panels** the shell allows per app (three worked; no documented limit).
4. **Narrow-panel clipping.** After a narrow resize the top-bar search icon and the fourth
   bottom-bar tab disappear. Investigated and *not* an app bug — `TBABottomBar` is a stock
   weighted M3 `NavigationBar` with no code path that can drop a tab, and an emulator sweep
   at the exact Quest geometry shows all four tabs at every width. Evidence points to the
   Horizon shell clipping the window's right edge. Needs a `dumpsys` capture in the bad state
   to confirm; if so it is a Meta bug and our mitigation is raising the manifest `minWidth`.
5. **`excludeFromRecents` vs. multi-panel** — can put-down panels still be re-summoned? (§3.)
6. **The sideloaded launcher icon.** The raster fix follows Meta's *Portal* launcher doc, not
   a Horizon-specific one; if it comes up empty the next lever is the undocumented
   `com.oculus.application_type="panel"` meta-data. Store installs resolve this regardless
   via Cover Square.
7. **Multi-user headsets** (VRC.Quest.Functional.12) and **panel resize mid-sign-in** (a
   config change cancels the flow — acceptable?).
8. Whether review treats TBA as a streaming/media app, which is what makes the keyboard-free
   login mandatory rather than merely nice.

Answered since the first draft, recorded so nobody re-opens them: unfocused panels stay
resumed (multi-resume); Firebase-without-GMS works; the panel is exactly its declared size;
the OAuth custom-scheme dance works end to end through Oculus Browser; toasts land outside
the panel; Glance widgets have no host and are gms-only.

---

## 10. Sources

Meta: [Android apps hub](https://developers.meta.com/horizon/documentation/android-apps/horizon-os-apps/) ·
[Features overview](https://developers.meta.com/horizon/documentation/android-apps/features-overview/) ·
[Port an existing app](https://developers.meta.com/horizon/documentation/android-apps/port-an-existing-app) ·
[Media requirements](https://developers.meta.com/horizon/documentation/android-apps/media-requirements) ·
[Design requirements](https://developers.meta.com/horizon/documentation/android-apps/design-requirements) ·
[Mobile manifest requirements](https://developers.meta.com/horizon/resources/publish-mobile-manifest/) ·
[VRC requirements](https://developers.meta.com/horizon/resources/publish-quest-req/) ·
[Prohibited permissions](https://developers.meta.com/horizon/resources/permissions-prohibited/) ·
[Asset guidelines](https://developers.meta.com/horizon/resources/asset-guidelines/) ·
[Release channels](https://developers.meta.com/horizon/resources/publish-release-channels/) ·
[ovr-platform-util](https://developers.meta.com/horizon/resources/publish-reference-platform-command-line-utility/) ·
[Spatial Simulator](https://developers.meta.com/horizon/blog/meta-spatial-simulator-android-horizon-os) ·
[metavr CLI / MCP](https://developers.meta.com/horizon/essentials/ai-tooling-mcp/) ·
[Hybrid apps](https://developers.meta.com/horizon/documentation/spatial-sdk/hybrid-apps-overview/)

Google: [Firebase × Play services compatibility](https://firebase.google.com/docs/android/android-play-services)
