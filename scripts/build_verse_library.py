#!/usr/bin/env python3
"""Build a bilingual, offline, paired public-domain Bible verse library.
Input: Midvash bible-data / Arabic Smith-Van Dyck (1865) and English KJV.
Output: Android app assets bible_verses.json.
"""
import json, pathlib, urllib.request
BASE="https://raw.githubusercontent.com/midvash/bible-data/main/versions"
BOOKS=["Ps","Prov","Matt","Luke","John","Rom","Phil","Isa"]
# Verified OSIS slugs may differ by source; fail build if any download fails.
def fetch(language,version,book):
    url=f"{BASE}/{language}/{version}/books/{book}.json"
    with urllib.request.urlopen(url,timeout=45) as response:
        return json.load(response)
def main():
    result=[]
    for name in BOOKS:
        a=fetch("ar","svd",name); e=fetch("en","kjv",name)
        translations={}
        for book in (a,e):
            for chapter in book["chapters"]:
                number=chapter["chapter"]
                for verse in chapter["verses"]:
                    translations[(book["version"],number,verse["number"])]=verse["text"]
        for (version,chapter,number),ar in translations.items():
            if version!="svd": continue
            en=translations.get(("kjv",chapter,number))
            if not en or not (35<=len(ar)<=265 and 24<=len(en)<=310): continue
            result.append({"ar":ar,"en":en,"ref":f"{name} {chapter}:{number}"})
    if len(result)<300: raise RuntimeError(f"Not enough unique verses: {len(result)}")
    target=pathlib.Path("app/src/main/assets/bible_verses.json")
    target.parent.mkdir(parents=True,exist_ok=True)
    target.write_text(json.dumps(result,ensure_ascii=False,separators=(",",":")),encoding="utf8")
    print("Created",len(result),"paired bilingual verses (offline); library",target.stat().st_size,"bytes")
if __name__=="__main__": main()
