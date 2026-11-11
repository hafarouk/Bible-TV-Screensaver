#!/usr/bin/env python3
"""Package only CC0 Open Access Christian artwork from The Metropolitan Museum of Art.
Every included image must have isPublicDomain=true and an official Met image URL.
Fail closed rather than reusing the old Commons/Tissot library.
"""
import io,json,time,urllib.request,urllib.parse
from pathlib import Path
from PIL import Image
BASE="https://collectionapi.metmuseum.org/public/collection"
ROOT=Path("app/src/main/assets")
OUT=ROOT/"jesus_art";OUT.mkdir(parents=True,exist_ok=True)
for old in OUT.glob("jesus_*.jpg"):old.unlink()
TERMS=["Jesus Christ","Crucifixion","Nativity","Adoration of the Magi","Resurrection of Christ","Madonna and Child","Christ healing","Christ and disciples","Last Supper","Baptism of Christ","Christ carrying cross","Ascension of Christ","Christ in the Garden","Christ preaching"]
def fetch(url):
 req=urllib.request.Request(url,headers={"User-Agent":"BibleTV/1.12 (CC0 open-access Christian art project)"})
 with urllib.request.urlopen(req,timeout=30) as r:return r.read()
def get_json(url):return json.loads(fetch(url))
records=[];seen=set()
for term in TERMS:
 if len(records)>=75:break
 try:
  params=urllib.parse.urlencode({"q":term,"hasImages":"true"})
  result=get_json(BASE+"/v1/search?"+params)
  for oid in (result.get("objectIDs") or [])[:90]:
   if len(records)>=75:break
   if oid in seen:continue
   seen.add(oid)
   try:
    obj=get_json(BASE+"/v1/objects/"+str(oid))
    if obj.get("isPublicDomain") is not True:continue
    url=obj.get("primaryImage") or ""
    if not url.startswith("https://images.metmuseum.org/"):continue
    title=(obj.get("title") or "").lower()
    tags=(obj.get("tags") or [])
    searchable=title+" "+(obj.get("objectName") or "").lower()+" "+" ".join((t.get("term") or "").lower() for t in tags)
    if not any(w in searchable for w in ("christ","jesus","madonna","virgin","nativity","crucifix","resurrection","adoration","baptism","saint","holy family","last supper")):continue
    raw=fetch(url)
    if len(raw)>16000000:continue
    im=Image.open(io.BytesIO(raw))
    if im.width<900 or im.height<650:continue
    im=im.convert("RGB");im.thumbnail((1920,1080))
    name="jesus_%03d.jpg"%len(records)
    im.save(OUT/name,quality=86,optimize=True)
    records.append({"file":name,"title":obj.get("title"),"artist":obj.get("artistDisplayName"),"source":obj.get("objectURL"),"objectID":oid,"license":"CC0 / The Met Open Access","isPublicDomain":True})
    print(name,obj.get("title"),flush=True)
    time.sleep(0.15)
   except Exception as e:print("Skipped object",oid,str(e),flush=True)
 except Exception as e:print("Search failed",term,str(e),flush=True)
if len(records)<20:raise RuntimeError("Only %d confirmed Met CC0 images; refusing release"%len(records))
(ROOT/"jesus_art_manifest.json").write_text(json.dumps(records,ensure_ascii=False,indent=2),encoding="utf8")
print("Packaged",len(records),"Met Open Access CC0 paintings")
