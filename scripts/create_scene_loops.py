#!/usr/bin/env python3
"""Generate original-motion GIF loops from public-domain paintings packaged into the APK.
Only motion is synthesized here; the source images remain public domain.
"""
from pathlib import Path
from PIL import Image,ImageEnhance,ImageDraw
root=Path("app/src/main")
inputdir=root/"res/drawable-nodpi"
out=root/"assets/scene_loops"
out.mkdir(parents=True,exist_ok=True)
# Motion composition from verified public-domain stills.
for name in ("shepherd","storm","welcome"):
    source=inputdir/(name+".jpg")
    if not source.exists():raise FileNotFoundError(source)
    im=Image.open(source).convert("RGB")
    width,height=im.size
    frames=[]
    for step in range(12):
        # Slow pan and zoom in 12-step 3-second seamless loop, alternate movement direction.
        phase=step if step<=6 else 12-step
        zoom=1.07+0.035*phase/6
        cropw=int(width/zoom);croph=int(height/zoom)
        dx=int((width-cropw)*(0.40+0.16*phase/6))
        dy=int((height-croph)*(0.45+0.07*phase/6))
        crop=im.crop((dx,dy,dx+cropw,dy+croph)).resize((256,144),Image.Resampling.LANCZOS)
        frames.append(crop)
    path=out/(name+".gif")
    frames[0].save(path,save_all=True,append_images=frames[1:],duration=250,loop=0,optimize=True)
    if path.stat().st_size<1000:raise RuntimeError("Empty scene loop "+name)
    print("Created original-motion public-domain loop",path,path.stat().st_size,"bytes")
