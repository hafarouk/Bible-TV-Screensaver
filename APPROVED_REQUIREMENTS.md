## Current interim logo decision (2026-11-11)
- User approved the most recently generated **white-background Bible TV logo** for temporary use in the Android TV app, despite earlier preference for exact Velthon logo. **Do not substitute a different design.**
- Exact saved image: ChatGPT Library `/Brand Assets/Bible_TV_Temporary_Approved_App_Logo.png`, library ID `libfile_36b8a263c83481919c0bda1c70e7763a`, file ID `file_000000002410822f983f40919f722ad8`.
- The original unmodified VELTHON emblem remains stored separately at `/Brand Assets/VELTHON_ORIGINAL_OFFICIAL_LOGO_UNMODIFIED.png`.
- **Not yet installed into Android drawable or APK**; must verify binary asset inclusion before reporting completion.

# Bible TV Screensaver — Approved Product Requirements

This file is the persistent source of truth for user-approved decisions. Do not replace, reinterpret, or silently drop any item. Mark implementation and device validation separately. Never claim a release includes an item merely because its source code was edited.

## Branding — MUST MATCH EXACT ORIGINAL ASSET
- **Approved Bible TV app icon:** user-confirmed black/yellow/white TV frame with cross and Bible, with **white background** and upright (non-italic) Bible TV lettering. The exact original **VELTHON** emblem to insert is the uploaded PNG saved unmodified at ChatGPT Library `/Brand Assets/VELTHON_ORIGINAL_OFFICIAL_LOGO_UNMODIFIED.png` (library file ID `libfile_85cf5a094e0481919dab1828a17f4d69`; source user-upload file ID `file_0000000052dc822fbe04c0f386f0641c`). **Never regenerate or redraw this emblem.**
- **Do not create a substitute logo.** Use the original user-uploaded Velthon emblem unchanged; do not substitute generated lion designs or the old Jesus-and-TV logo. The actual approved app icon is the user-uploaded TV image `file_00000000eb00822f8a6279a783a444b5`, but it requires the white-background/non-italic text adjustments and insertion of the exact original Velthon emblem.

- Screensaver footer must be **text only**: `By: VELTHON Design & Development`. No additional company logo or banner on the verse screen.
- Status: ORIGINAL LOGO NOT YET VERIFIED IN APK. Do not say complete.

## Artwork
- **No animated GIFs, generated landscape animations, small picture-in-picture boxes, or unattractive placeholder images.**
- Full-screen, high-quality **still Christian artwork depicting Jesus and biblical events**, e.g., Nativity, Baptism, miracles, teaching, Holy Week, Crucifixion, Resurrection and Ascension.
- Prefer a substantial varied library, not repeated 3–25 images; target 100+ if enough eligible images exist, never fabricate quantity.
- For commercial/public distribution, only bundle images with **verified CC0 or public-domain rights** and clear source/rights provenance. Avoid CC BY, CC BY-SA, CC BY-NC and unclear licenses. Prefer explicitly CC0 museum open-access sources (e.g., The Met); no source guarantees zero legal liability.
- Store image title, artist, source URL and license status in an offline manifest. Exclude uncertain images. Package offline.
- Fit/crop thoughtfully, preserving central subjects and text readability. No animation/motion on pictures.
- Status: CC0-only collection script changed, but new artwork APK NOT YET BUILT/VERIFIED.

## Bible verses and references
- Arabic setting: **Arabic verse AND Arabic book reference** with Arabic-Indic numerals, e.g. `يوحنا ٣:١٦`.
- English setting: English verse AND English reference, e.g. `John 3:16`.
- Both: Arabic and English verse text **AND both references** on screen, clearly separated.
- Paired public-domain Arabic SVD and English KJV, packaged offline. One-hour cooldown to avoid frequent verse repeats; 15/30/60/120-second intervals.
- Status: implementation reported in source; must verify behavior on TV.

## Android TV / Fire TV settings
- Simple, concise, high-contrast, D-pad navigable; no clipped/truncated text.
- Choices selected **on the same row**; scroll horizontally as needed. No hidden cycling-button interface.
- Language, verse duration, page-turn/fade/none transitions, background music on/off and selection, volume, preview, TV screensaver settings.
- No GIF/media mode or unused animation speed controls.
- Status: source updated; needs physical TV validation.

## Audio and transitions
- Soft original music that actually plays; option to disable, adjust volume, preview; test on TV.
- Page-curl transition and optional fade/none. No moving pictures.

## Release and verification gate
- Do not call a release ready until GitHub Actions succeeds **on the final intended commit**.
- Download the exact artifact, inspect APK contents (logo, artwork, offline verses), and confirm version/commit.
- A successful GitHub build alone does not prove actual Android TV rendering/audio or correct logo.
- Provide only verified download links; never reuse an older APK as though it includes newer changes.
- Track each requirement as: requested / committed / APK-verified / device-tested.
- Never silently remove approved features during future changes.

## Immediate blockers
1. Import the **exact** Library logo PNG into the Android project (GitHub text-only file tools cannot directly upload binary bytes; use a binary-capable GitHub workflow/connector or a safely encoded asset pipeline).
2. Verify CC0-only Christian artwork generator can build sufficient quality images and run without API rate limiting.
3. Trigger and inspect final workflow, then extract and validate APK; do not distribute previous v1.11 as a replacement.
