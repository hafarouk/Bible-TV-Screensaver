#!/usr/bin/env python3
"""Create 60 original offline animated nature scenes; all artwork generated locally."""
from PIL import Image,ImageDraw
from pathlib import Path
import math,random
out=Path("app/src/main/assets/scene_loops");out.mkdir(parents=True,exist_ok=True)
W,H=480,270
for n in range(60):
 rng=random.Random(n)
 theme=n%6
 base=[(13,32,66),(11,53,83),(45,31,75),(10,51,42),(8,12,40),(73,42,57)][theme]
 horizon=135+(n%7)*9
 stars=[(rng.randrange(W),rng.randrange(145),rng.randrange(1,3)) for _ in range(48)]
 frames=[]
 for k in range(10):
  im=Image.new("RGB",(W,H));px=im.load()
  for y in range(H):
   q=y/H
   for x in range(W):
    glow=max(0,1-abs(x/W-(.25+(n%6)*.11))*2)*max(0,1-abs(q-.43)*2)
    px[x,y]=tuple(min(255,int(base[c]+q*(65 if c==0 else 55)+glow*(65 if c==0 else 32))) for c in range(3))
  d=ImageDraw.Draw(im,"RGBA")
  if theme==4:
   for j,(x,y,r) in enumerate(stars):
    alpha=int(95+135*(.5+.5*math.sin(k*math.pi/5+j)))
    d.ellipse((x-r,y-r,x+r,y+r),fill=(255,245,202,alpha))
  else:
   sunx=int(W*(.25+(n%6)*.11));suny=90+(n%4)*7
   d.ellipse((sunx-26,suny-26,sunx+26,suny+26),fill=(255,219,157,190))
   for j in range(6):
    cx=int((j*110+n*19+k*3*(1+j%2))%(W+180))-90
    cy=22+(j*29+n*7)%90
    d.ellipse((cx,cy,cx+115,cy+20),fill=(240,228,210,40))
  for layer in range(3):
   yy=horizon+layer*32
   poly=[(0,H)]+[(x,int(yy+math.sin(x/52+n+layer)* (12+layer*9))) for x in range(0,W+16,16)]+[(W,H)]
   col=(12+layer*7,35+layer*12,47+layer*11,255)
   d.polygon(poly,fill=col)
  if theme in (1,2,5):
   for j in range(22):
    y=horizon+45+j*4
    x=(j*47+n*17+k*9)%W
    d.line((x,y,min(W,x+25+j%5*8),y),fill=(224,217,178,65),width=1)
  if theme==3:
   for j in range(18):
    x=(j*37+n*31)%W;y=horizon+40+(j*17)%70
    d.ellipse((x-2,y-2,x+2,y+2),fill=(242,183+(k+j)%60,120,210))
  frames.append(im.quantize(colors=96))
 target=out/("motion_%02d.gif"%n)
 frames[0].save(target,save_all=True,append_images=frames[1:],duration=180,loop=0,optimize=True,disposal=2)
 print(target.name,target.stat().st_size)
