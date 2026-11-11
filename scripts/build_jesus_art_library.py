#!/usr/bin/env python3
"""Bundle 50 NEW, originally AI-generated biblical and nature scenes, offline.
No remote art API, no old Tissot artwork, and no old scenic images.
The supplied atlas is a 10-column x 5-row original illustration sheet.
"""
import json
from pathlib import Path
from PIL import Image,ImageOps
root=Path("app/src/main")
source=root/"assets/bible_tv_original_50_atlas.jpg"
out=root/"assets/jesus_art";out.mkdir(parents=True,exist_ok=True)
if not source.exists():raise RuntimeError("Missing original 50-scene atlas")
for old in out.glob("jesus_*.jpg"):old.unlink()
# Per-scene semantic labels, in row-major order (10 across, five down).
categories=[
 "teaching","resurrection","cross","shepherd","storm","storm","light","spirit","cross","cross",
 "communion","healing","teaching","teaching","shepherd","moses","moses","commandments","noah","noah",
 "david","david","daniel","prayer","samson","prayer","jonah","elijah","daniel","scripture",
 "harvest","queen","joseph","king","moses","bread","water","cross","elijah","water",
 "resurrection","spirit","teaching","scripture","mother","nativity","cross","light","water","mountain",
]
assert len(categories)==50
records=[]
with Image.open(source) as atlas:
 atlas=atlas.convert("RGB")
 w,h=atlas.size
 if w<1200 or h<600:raise RuntimeError("Original art atlas too small")
 for i,category in enumerate(categories):
  row,col=divmod(i,10)
  left=round(col*w/10)+3;right=round((col+1)*w/10)-3
  top=round(row*h/5)+3;bottom=round((row+1)*h/5)-3
  tile=atlas.crop((left,top,right,bottom))
  # Upscale for Android TV decoding, preserving the source's actual detail.
  tile=ImageOps.fit(tile,(960,540),method=Image.Resampling.LANCZOS)
  name="jesus_%03d.jpg"%i
  tile.save(out/name,quality=86,optimize=True)
  records.append({"file":name,"title":"Original Bible TV scene %02d"%(i+1),
    "category":category,"license":"Original AI-generated artwork for Bible TV",
    "source":"bundled-offline","original":True})
(root/"assets/jesus_art_manifest.json").write_text(json.dumps(records,indent=2),encoding="utf8")
# Second original artwork atlas: 50 additional illustrations (10 columns × 5 rows).
second=root/"assets/bible_tv_additional_50_atlas.jpg"
if not second.exists():raise RuntimeError("Missing second original 50-image atlas")
second_categories=[
 "spirit","mountain","harvest","nature","light","water","nature","nature","water","mountain",
 "mountain","nature","harvest","nature","water","nature","light","water","nature","nature",
 "nature","light","nature","water","nature","nature","mountain","nature","light","nature",
 "nature","light","nature","nature","water","light","nature","water","nature","nature",
 "nature","nature","light","nature","nature","mountain","water","nature","nature","light"
]
assert len(second_categories)==50
with Image.open(second) as atlas:
 atlas=atlas.convert("RGB");w,h=atlas.size
 for i,category in enumerate(second_categories):
  row,col=divmod(i,10)
  tile=atlas.crop((round(col*w/10)+2,round(row*h/5)+2,round((col+1)*w/10)-2,round((row+1)*h/5)-2))
  tile=ImageOps.fit(tile,(960,540),method=Image.Resampling.LANCZOS)
  name="jesus_%03d.jpg"%(50+i)
  tile.save(out/name,quality=86,optimize=True)
  records.append({"file":name,"title":"Additional original Bible TV scene %02d"%(i+51),
   "category":category,"license":"Original AI-generated artwork for Bible TV",
   "source":"bundled-offline","original":True})
(root/"assets/jesus_art_manifest.json").write_text(json.dumps(records,indent=2),encoding="utf8")
print("Bundled 100 original biblical and nature scene tiles",flush=True)
