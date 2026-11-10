# Bible TV Screensaver

A Bible-verse screensaver for Android TV and Fire TV, with plans for Samsung Tizen and LG webOS.

Features planned and present in the source project:
- Arabic / English / bilingual verse display
- Adjustable verse interval, ambient animation and matching religious artwork
- Android DreamService plus manual preview
- Samsung Tizen and LG webOS full-screen player variants

The Android TV app builds using the GitHub Actions workflow once its Android source and image assets are committed.

**Build status:** Packaging is not yet complete. Do not treat this repository as containing an installable APK until the workflow succeeds.

## Installable formats
- Android TV / Fire TV: APK
- Samsung Tizen: WGT (must be signed appropriately)
- LG webOS: IPK

## v1.3 TV feedback fixes
- Offline biblical artwork bundled at build time (build must fail if files not found)
- Same-row D-pad-selectable settings
- Clear notice on screen saver system-settings restrictions
- Softer opt-in ambient soundtrack
