#!/usr/bin/env python3
"""Package offline biblical images without a fragile external API.
The three Tissot biblical paintings are public-domain Wikimedia images bundled
by download_artwork.py. Scenic backgrounds are original project artwork.
Never label scenic variants as additional historic paintings.
"""
import json
from pathlib import Path
from PIL import Image,ImageEnhance
root=Path("app/src/main")
source=root/"res/drawable-nodpi"
out=root/"assets/jesus_art"
out.mkdir(parents=True,exist_ok=True)
for old in out.glob("jesus_*.jpg"): old.unlink()
items=[
 ("shepherd","The Good Shepherd — James Tissot","shepherd","Public domain / Brooklyn Museum / Wikimedia Commons"),
 ("storm","Jesus Stilling the Tempest — James Tissot","storm","Public domain / Brooklyn Museum / Wikimedia Commons"),
 ("welcome","Suffer the Little Children — James Tissot","welcome","Public domain / Brooklyn Museum / Wikimedia Commons"),
 ("scene_sunrise","Original sunrise landscape","light","Original Velthon project artwork"),
 ("scene_ocean","Original ocean landscape","water","Original Velthon project artwork"),
 ("scene_mountain","Original mountain landscape","strength","Original Velthon project artwork"),
 ("scene_garden","Original garden landscape","peace","Original Velthon project artwork"),
 ("scene_stars","Original starry sky landscape","heaven","Original Velthon project artwork")
]
records=[]
for source_name,title,category,license_name in items:
 path=source/(source_name+".jpg")
 if not path.exists(): raise RuntimeError("Missing required offline artwork: "+str(path))
 with Image.open(path) as raw:
  image=raw.convert("RGB")
  image.thumbnail((1600,900))
  filename="jesus_%03d.jpg"%len(records)
  image.save(out/filename,quality=86,optimize=True)
  records.append({"file":filename,"title":title,"category":category,"license":license_name,"source":"bundled-offline"})
  print("Packaged",filename,title,flush=True)
if len(records)<8:raise RuntimeError("Incomplete offline image collection")
(root/"assets/jesus_art_manifest.json").write_text(json.dumps(records,ensure_ascii=False,indent=2),encoding="utf8")
print("Packaged",len(records),"verified offline images: 3 public-domain paintings and 5 original landscapes")
