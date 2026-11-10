#!/usr/bin/env python3
"""Build a copyright-safe offline bilingual collection (Arabic SVD 1865, English KJV 1769).
Source: https://github.com/midvash/bible-data (both public domain).
Exactly paired OSIS references; verify >= 300 distinct verses so 15s rotation can last > 1 hour.
"""
import json,urllib.request,pathlib
BOOKS=["Ps","Prov","Matt","Mark","Luke","John","Rom","Phil","Isa","1John","Eph"]
BASE="https://raw.githubusercontent.com/midvash/bible-data/main/versions"
NAMES={"Ps":("مزامير","Psalms"),"Prov":("أمثال","Proverbs"),"Matt":("متى","Matthew"),"Mark":("مرقس","Mark"),"Luke":("لوقا","Luke"),"John":("يوحنا","John"),"Rom":("رومية","Romans"),"Phil":("فيلبي","Philippians"),"Isa":("إشعياء","Isaiah"),"1John":("يوحنا الأولى","1 John"),"Eph":("أفسس","Ephesians")}
AR_DIGITS=str.maketrans("0123456789","٠١٢٣٤٥٦٧٨٩")
def load(lang,version,book):
 url=f"{BASE}/{lang}/{version}/books/{book}.json"
 req=urllib.request.Request(url,headers={"User-Agent":"VelthonBibleTV/1.4 public-domain-data"})
 with urllib.request.urlopen(req,timeout=45) as f:return json.load(f)
def flatten(book):
 return {(chapter["chapter"],v["number"]):v["text"] for chapter in book["chapters"] for v in chapter["verses"]}
def main():
 result=[]
 for book in BOOKS:
  a=flatten(load("ar","svd",book));e=flatten(load("en","kjv",book))
  for (ch,num),ar in a.items():
   en=e.get((ch,num))
   if en and 28<=len(ar)<=240 and 22<=len(en)<=250:
    arabic_book,english_book=NAMES[book]
    result.append({"ar":ar.strip(),"en":en.strip(),"ref":f"{book} {ch}:{num}","ref_ar":f"{arabic_book} {ch}:{num}".translate(AR_DIGITS),"ref_en":f"{english_book} {ch}:{num}"})
 if len(result)<300:raise RuntimeError("Not enough distinct verified verses: "+str(len(result)))
 target=pathlib.Path("app/src/main/assets/bible_verses.json")
 target.parent.mkdir(parents=True,exist_ok=True)
 target.write_text(json.dumps(result,ensure_ascii=False),encoding="utf-8")
 print("Bundled",len(result),"paired public-domain Bible verses, bytes:",target.stat().st_size)
if __name__=="__main__":main()
