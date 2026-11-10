# Asset provenance and licensing — Bible TV v1.4

## Images
The four background paintings are *public-domain historical works* by James Tissot (1836–1902), obtained as digital reproductions from Wikimedia Commons / Brooklyn Museum in `scripts/download_artwork.py`. Their underlying artwork is public domain due to copyright term expiration. Build will fail if images cannot be fetched as valid JPEGs.
- **Good Shepherd** — James Tissot, Brooklyn Museum / Wikimedia Commons
- **Jesus calming the tempest** — James Tissot, Brooklyn Museum / Wikimedia Commons
- **Let the little children come to me** — James Tissot, Brooklyn Museum / Wikimedia Commons
- **Jesus sleeping during the tempest** — James Tissot, Brooklyn Museum / Wikimedia Commons

Artwork downloads are embedded in the build APK. No network access is required to show them on TV. Before public app-store distribution, review each individual source-page rights statement and metadata (as distinct from merely checking the underlying artist's lifetime). Do not substitute modern copyrighted film stills, photos, or artwork.

## Bible texts
The Arabic Smith–Van Dyck (1865) and English King James Version (1769), both public-domain translations. Machine-readable source: https://github.com/midvash/bible-data ; `SOURCES.md` documents licenses. Verses are paired by book/chapter/verse and built offline in the APK.

## Original branding
`velthon_original.b64` is a proportionally downscaled, pixel-preserving rendition of the original uploaded Velthon lion V logo (including black V, lion, yellow stripe, inverted yellow triangle, yellow brackets, and VELTHON text). No altered redraw. Trademark belongs to the user. We must never use the generated lookalike versions as brand masters.

## Audio
`scripts/prepare_media.py` synthesizes a brand-new instrumental ambient WAV generated algorithmically from basic sine wave harmonies. No external sound recordings or borrowed compositions. Opt-in playback only, off by default, stopped on exit.

## Distribution
Current debug APK is for device testing. Build success does not constitute security certification, manufacturer approval, or rights clearance of third-party source-hosted reproductions for worldwide distribution.
