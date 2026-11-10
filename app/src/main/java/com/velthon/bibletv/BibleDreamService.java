package com.velthon.bibletv;
import android.service.dreams.DreamService;
public class BibleDreamService extends DreamService {
    private BibleDisplay display;
    @Override public void onAttachedToWindow() {
        super.onAttachedToWindow(); setFullscreen(true); setInteractive(false); setScreenBright(true);
        display=new BibleDisplay(this); setContentView(display);
    }
    @Override public void onDreamingStarted() { super.onDreamingStarted(); display.start(); }
    @Override public void onDreamingStopped() { display.stop(); super.onDreamingStopped(); }
    @Override public void onDetachedFromWindow() { if(display!=null)display.stop(); super.onDetachedFromWindow(); }
}
