#!/usr/bin/env python3
"""Prepare offline original Velthon branding and original synthesized ambient music.
No external sound/image downloads. Logo pixels decode from the user's supplied original mark.
"""
import pathlib,base64,wave,math
root=pathlib.Path("app/src/main")
drawable=root/"res/drawable-nodpi";raw=root/"res/raw"
drawable.mkdir(parents=True,exist_ok=True);raw.mkdir(parents=True,exist_ok=True)
encoded=(root/"assets/velthon_original.b64").read_text(encoding="ascii").strip()
logo=base64.b64decode(encoded,validate=True)
assert logo[:4]==b"RIFF" and logo[8:12]==b"WEBP"
(drawable/"velthon_original.webp").write_bytes(logo)
print("Bundled original Velthon logo",len(logo),"bytes with unchanged aspect ratio")
target=raw/"ambient_original.wav"
rate=22050;seconds=16
notes=[(130.81,164.81,196.0),(146.83,185.0,220.0),(174.61,220.0,261.63),(146.83,196.0,246.94)]
with wave.open(str(target),"wb") as out:
 out.setnchannels(1);out.setsampwidth(2);out.setframerate(rate)
 import struct
 batch=bytearray()
 for i in range(rate*seconds):
  t=i/rate
  chord=notes[int(t//4)%4]
  # Fade each voice, slow melody, and smooth overall loop edges.
  fade=min(1,t/0.4,(seconds-t)/0.4)
  chord_wave=sum(math.sin(2*math.pi*n*t)*weight for n,weight in zip(chord,[0.38,0.22,0.16]))
  melody=0.17*math.sin(2*math.pi*(chord[1]*2)*t)* (0.5+0.5*math.sin(2*math.pi*t/8))
  val=(chord_wave+melody)*0.43*max(0,fade)
  batch+=struct.pack("<h",max(-32767,min(32767,int(val*32767))))
 out.writeframes(batch)
print("Bundled original soft ambient recording",target.stat().st_size,"bytes")
