#!/usr/bin/env python3
"""Download verified public-domain historical paintings of events in Jesus' life.
All images and license metadata are bundled for offline use; fail closed on license.
"""
import json,urllib.parse,urllib.request,time,io
from pathlib import Path
from PIL import Image,ImageOps
ROOT=Path("app/src/main/assets");OUT=ROOT/"jesus_art";OUT.mkdir(parents=True,exist_ok=True)
SEARCHES=["James Tissot Jesus","James Tissot Christ","Jesus Christ biblical paintings"]
API="https://commons.wikimedia.org/w/api.php"
def query(params):
 url=API+"?"+urllib.parse.urlencode(params)
 req=urllib.request.Request(url,headers={"User-Agent":"BibleTV/1.11 (open source artwork attribution; contact: github.com/hafarouk/Bible-TV-Screensaver)"})
 with urllib.request.urlopen(req,timeout=35) as r:return json.load(r)
def clean(t):
 import re,html
 return html.unescape(re.sub("<[^>]+>","",t or "")).strip()
seen=set();records=[]
for term in SEARCHES:
 try:
  data=query({"action":"query","generator":"search","gsrsearch":term+" filetype:bitmap","gsrnamespace":6,"gsrlimit":50,"prop":"imageinfo","iiprop":"url|extmetadata|size","iiurlwidth":1280,"format":"json"})
  pages=list(data.get("query",{}).get("pages",{}).values())
  time.sleep(3)
  accepted=0
  for page in pages:
   if accepted>=22:break
   if len(records)>=45:break
   info=(page.get("imageinfo") or [{}])[0];meta=info.get("extmetadata",{})
   license_code=clean(meta.get("LicenseShortName",{}).get("value","")).lower()
   usage=clean(meta.get("UsageTerms",{}).get("value","")).lower()
   if not (license_code in ("public domain","pd-old","cc0","cc-zero") or "public domain" in usage):continue
   if info.get("width",0)<700 or info.get("height",0)<500:continue
   title=page.get("title","");url=info.get("thumburl") or info.get("url")
   if not url or title in seen:continue
   seen.add(title)
   req=urllib.request.Request(url,headers={"User-Agent":"BibleTV/1.11 (public domain artwork)"})
   with urllib.request.urlopen(req,timeout=35) as r:raw=r.read(8_000_000)
   img=Image.open(io.BytesIO(raw)).convert("RGB")
   if img.width<640 or img.height<400:continue
   name="jesus_%03d.jpg"%len(records)
   img.thumbnail((1280,900));img.save(OUT/name,quality=82,optimize=True)
   time.sleep(0.8)
   accepted+=1
   records.append({"file":name,"title":title,"source":info.get("descriptionurl",""),"license":license_code,"artist":clean(meta.get("Artist",{}).get("value",""))[:160]})
   print(name,title,license_code,flush=True)
   if len(records)>=65:break
 except Exception as e:print("Search unavailable:",term,str(e),flush=True)
if len(records)<15:raise RuntimeError("Only %d verified paintings; refusing to ship limited image library"%len(records))
(ROOT/"jesus_art_manifest.json").write_text(json.dumps(records,ensure_ascii=False,indent=2))
print("Verified public domain images:",len(records))
