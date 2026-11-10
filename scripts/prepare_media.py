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
# Three independent original ambient compositions: distinct palettes, no licensed recordings.
import struct
rate=22050
def generate(name,seconds,chords,style):
 target=raw/(name+".wav")
 with wave.open(str(target),"wb") as out:
  out.setnchannels(1);out.setsampwidth(2);out.setframerate(rate)
  buff=bytearray()
  for i in range(rate*seconds):
   t=i/rate
   idx=int(t//8)%len(chords)
   notes=chords[idx]
   # The first and last second fade in/out, avoiding audible loop pops.
   fade=max(0,min(1,t/1.25,(seconds-t)/1.25))
   pulse=0.7+0.3*math.sin(2*math.pi*t/13)
   if style=="piano":
    # Bell-like plucked chord envelope and sparse two-note melody.
    beat=t%2
    attack=math.exp(-1.3*beat)
    val=sum((math.sin(2*math.pi*n*t)+0.12*math.sin(4*math.pi*n*t))*w for n,w in zip(notes,[0.28,0.19,0.13]))*attack
    val+=0.10*math.sin(2*math.pi*notes[2]*2*t)*math.exp(-1.5*(t%4))
   elif style=="strings":
    # Slow layered sustained organ/string-like harmonics.
    val=sum((math.sin(2*math.pi*n*t)+0.1*math.sin(2*math.pi*n*2*t))*w for n,w in zip(notes,[0.32,0.23,0.16]))
    val*=0.65+0.25*math.sin(2*math.pi*t/11)
   else:
    # Sparse airy pad, low and unhurried.
    val=sum(math.sin(2*math.pi*n*t)*w for n,w in zip(notes,[0.35,0.18,0.10]))
    val+=0.07*math.sin(2*math.pi*notes[0]*0.5*t)
   sample=int(32767*0.18*fade*pulse*val)
   buff+=struct.pack("<h",max(-32767,min(32767,sample)))
  out.writeframes(buff)
 print("Bundled original",name,target.stat().st_size,"bytes")
generate("music_piano",32,[(196,246.94,293.66),(174.61,220,261.63),(164.81,196,246.94),(174.61,220,293.66)],"piano")
generate("music_strings",32,[(130.81,164.81,196),(146.83,185,220),(174.61,220,261.63),(146.83,196,246.94)],"strings")
generate("music_ambient",32,[(110,164.81,220),(98,146.83,196),(130.81,164.81,261.63),(110,146.83,220)],"ambient")
