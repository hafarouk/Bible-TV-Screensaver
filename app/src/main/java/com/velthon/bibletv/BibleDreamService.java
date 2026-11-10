package com.velthon.bibletv;
import android.service.dreams.DreamService;
import android.view.KeyEvent;
public class BibleDreamService extends DreamService {
    private BibleDisplay display;
    @Override public void onAttachedToWindow() {
        super.onAttachedToWindow(); setFullscreen(true);
        // Interactive mode allows D-pad selection of the mute button.
        setInteractive(true); setScreenBright(true);
        display=new BibleDisplay(this);setContentView(display);
    }
    @Override public void onDreamingStarted() { super.onDreamingStarted(); display.start(); }
    @Override public void onDreamingStopped() { if(display!=null)display.stop(); super.onDreamingStopped(); }
    @Override public void onDetachedFromWindow() { if(display!=null)display.stop(); super.onDetachedFromWindow(); }
    @Override public boolean onKeyDown(int key, KeyEvent event) {
        if(key==KeyEvent.KEYCODE_BACK || key==KeyEvent.KEYCODE_HOME){finish();return true;}
        if(key==KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE || key==KeyEvent.KEYCODE_MEDIA_MUTE || key==KeyEvent.KEYCODE_M) {
            if(display!=null)display.toggleMute();return true;
        }
        return super.onKeyDown(key,event);
    }
}
