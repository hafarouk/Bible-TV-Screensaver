#!/usr/bin/env python3
"""Bundle only independently confirmed public-domain Christian art from Art Institute of Chicago.
Fail closed if too few works qualify; never treat search matches as proof of license.
"""
import io,json,time,urllib.request,urllib.parse
from pathlib import Path
from PIL import Image
ROOT=Path("app/src/main/assets")
OUT=ROOT/"jesus_art";OUT.mkdir(parents=True,exist_ok=True)
for old in OUT.glob("jesus_*.jpg"):old.unlink()
API="https://api.artic.edu/api/v1/artworks/search"
TERMS=["Jesus Christ","Crucifixion","Nativity","Adoration of the Magi","Resurrection of Christ","Madonna and Child","Christ healing","Last Supper","Baptism of Christ","Christ and disciples","Christ carrying the cross","Ascension","Holy Family","Virgin and Child"]
KEYWORDS=("christ","jesus","madonna","virgin","nativity","crucifix","resurrection","adoration","baptism","holy family","last supper","savior","saviour","infant jesus","lamb of god")
def fetch(url):
 req=urllib.request.Request(url,headers={"User-Agent":"BibleTV/1.13 (noncommercial public-domain artwork packaging)","Accept":"application/json,image/jpeg,*/*"})
 with urllib.request.urlopen(req,timeout=35) as response:return response.read()
def get_json(url):return json.loads(fetch(url))
records=[];seen=set()
for term in TERMS:
 if len(records)>=75:break
 try:
  params=urllib.parse.urlencode({"q":term,"limit":60,"fields":"id,title,image_id,is_public_domain,artist_title,thumbnail"})
  response=get_json(API+"?"+params)
  iiif=(response.get("config") or {}).get("iiif_url","https://www.artic.edu/iiif/2")
  if not iiif.startswith("https://"):raise ValueError("Untrusted IIIF endpoint")
  for obj in response.get("data",[]):
   if len(records)>=75:break
   oid=obj.get("id")
   if not oid or oid in seen:continue
   seen.add(oid)
   if obj.get("is_public_domain") is not True:continue
   title=(obj.get("title") or "").lower()
   if not any(word in title for word in KEYWORDS):continue
   image_id=obj.get("image_id")
   if not image_id:continue
   # The official Art Institute IIIF image is derived from a verified public-domain work.
   url=iiif.rstrip("/")+"/"+urllib.parse.quote(image_id,safe="")+"/full/1200,/0/default.jpg"
   try:
    raw=fetch(url)
    if len(raw)>16000000:continue
    im=Image.open(io.BytesIO(raw))
    if im.width<650 or im.height<500:continue
    im=im.convert("RGB");im.thumbnail((1920,1080))
    filename="jesus_%03d.jpg"%len(records)
    im.save(OUT/filename,quality=86,optimize=True)
    records.append({"file":filename,"title":obj.get("title"),"artist":obj.get("artist_title"),"source":"https://www.artic.edu/artworks/"+str(oid),"objectID":oid,"license":"Public Domain / Art Institute of Chicago Open Access","isPublicDomain":True})
    print(filename,obj.get("title"),flush=True)
    time.sleep(.08)
   except Exception as error:print("Skipped image",oid,str(error),flush=True)
 except Exception as error:print("Search failed",term,str(error),flush=True)
if len(records)<20:raise RuntimeError("Only %d verified Art Institute public-domain images; refusing release"%len(records))
(ROOT/"jesus_art_manifest.json").write_text(json.dumps(records,ensure_ascii=False,indent=2),encoding="utf8")
print("Packaged",len(records),"verified Art Institute public-domain paintings")
