#!/usr/bin/env python3
"""Package licensed biblical artwork *inside* the APK during CI.
Fail closed on image errors so we never ship another background-only APK.
Public-domain paintings by James Tissot, Brooklyn Museum / Wikimedia Commons.
"""
import hashlib, pathlib, urllib.request, urllib.parse, time
files=[
 ("shepherd","Brooklyn Museum - The Good Shepherd (Le bon pasteur) - James Tissot.jpg"),
 ("storm","Brooklyn Museum - Jesus Stilling the Tempest (Jésus calmant la tempête) - James Tissot - overall.jpg"),
 ("welcome","Brooklyn Museum - Suffer the Little Children to Come unto Me (Laisser venir à moi les petits enfants) - James Tissot.jpg"),
 ("rest","Brooklyn Museum - Jesus Sleeping During the Tempest (Jésus dormant pendant la tempête) - James Tissot - overall.jpg"),
]
folder=pathlib.Path("app/src/main/res/drawable-nodpi");folder.mkdir(parents=True,exist_ok=True)
for output,name in files:
    h=hashlib.md5(name.replace(" ","_").encode()).hexdigest()
    url="https://upload.wikimedia.org/wikipedia/commons/"+h[0]+"/"+h[:2]+"/"+urllib.parse.quote(name.replace(" ","_"))
    target=folder/(output+".jpg")
    req=urllib.request.Request(url,headers={"User-Agent":"BibleTVScreensaver/1.3 (noncommercial; artwork build)"})
    for attempt in range(3):
        try:
            with urllib.request.urlopen(req,timeout=35) as response: data=response.read()
            if len(data)<10000 or not data.startswith(b"\\xff\\xd8\\xff"):
                raise ValueError("not a valid JPEG: "+str(len(data)))
            target.write_bytes(data)
            print("Bundled",output,len(data),"bytes")
            break
        except Exception as e:
            if attempt==2: raise RuntimeError("Artwork packaging failed for "+name) from e
            time.sleep(2)
