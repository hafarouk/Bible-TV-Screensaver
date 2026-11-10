#!/usr/bin/env python3
"""Generate five original offline full-screen landscape scenes (no external images)."""
from PIL import Image,ImageDraw,ImageFilter
from pathlib import Path
import math,random
out=Path("app/src/main/res/drawable-nodpi");out.mkdir(parents=True,exist_ok=True)
W,H=1280,720
themes={
"sunrise":((13,34,75),(244,147,80),(250,200,106)),
"ocean":((6,32,64),(26,122,155),(90,194,207)),
"mountain":((14,31,66),(78,119,154),(168,181,160)),
"garden":((16,47,50),(69,128,85),(184,181,101)),
"stars":((5,9,34),(22,37,79),(49,67,115))}
for name,(top,mid,bottom) in themes.items():
 random.seed(name)
 im=Image.new("RGB",(W,H));p=im.load()
 for y in range(H):
  f=y/(H-1);a,b=(top,mid) if f<.58 else (mid,bottom);t=f/.58 if f<.58 else (f-.58)/.42
  for x in range(W):
   glow=max(0,1-abs(x/W-.76)*2)*max(0,1-abs(f-.40)*2)
   p[x,y]=tuple(max(0,min(255,int(a[k]*(1-t)+b[k]*t+glow*(17 if name!="stars" else 0)))) for k in range(3))
 d=ImageDraw.Draw(im,"RGBA")
 if name=="stars":
  for i in range(350):
   x=random.randrange(W);y=random.randrange(int(H*.7));r=random.choice([1,1,1,2])
   d.ellipse((x-r,y-r,x+r,y+r),fill=(255,246,205,random.randrange(90,220)))
 else:
  d.ellipse((960,115,1090,245),fill=(255,225,155,140))
 for layer in range(4):
  base=int(H*(.55+layer*.11));amplitude=28+layer*21
  points=[(0,H)]
  points.extend((x,int(base+math.sin(x/130+layer*2)*amplitude+math.sin(x/47+layer)*amplitude*.35)) for x in range(0,W+20,20))
  points.append((W,H))
  col=(8+layer*5,29+layer*8,38+layer*8,240)
  if name=="garden":col=(9+layer*6,45+layer*11,31+layer*5,240)
  d.polygon(points,fill=col)
 if name in ("ocean","sunrise"):
  for i in range(100):
   x=random.randrange(W);y=random.randrange(460,H);w=random.randrange(12,110)
   d.line((x,y,x+w,y),fill=(225,211,174,random.randrange(20,95)),width=2)
 if name=="garden":
  for i in range(90):
   x=random.randrange(W);y=random.randrange(470,H);r=random.randrange(2,7)
   d.ellipse((x-r,y-r,x+r,y+r),fill=random.choice([(246,190,103,210),(230,137,171,200),(232,232,187,210)]))
 im=im.filter(ImageFilter.GaussianBlur(.65))
 target=out/("scene_"+name+".jpg");im.save(target,quality=88,optimize=True)
 print("Original offline scene",target,target.stat().st_size)

# Dedicated Bible TV launcher icon, never the VELTHON corporate mark.
icon=Image.new("RGB",(512,512),(13,32,61));d=ImageDraw.Draw(icon)
d.rounded_rectangle((35,35,477,477),radius=92,fill=(23,54,91),outline=(227,184,92),width=13)
d.polygon([(88,298),(244,315),(244,411),(88,393)],fill=(245,228,187))
d.polygon([(268,315),(424,298),(424,393),(268,411)],fill=(245,228,187))
d.line((256,301,256,416),fill=(220,160,75),width=12)
d.rounded_rectangle((238,98,274,285),radius=7,fill=(255,219,140))
d.rounded_rectangle((178,153,334,188),radius=7,fill=(255,219,140))
icon.save(out/"bible_tv_icon.png")
