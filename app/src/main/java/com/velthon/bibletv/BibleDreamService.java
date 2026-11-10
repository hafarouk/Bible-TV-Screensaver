package com.velthon.bibletv;
import android.service.dreams.DreamService;
import android.view.KeyEvent;
public class BibleDreamService extends DreamService {
    private BibleDisplay display;
    @Override public void onAttachedToWindow() {
        super.onAttachedToWindow(); setFullscreen(true);
        setInteractive(true); setScreenBright(true);
        display=new BibleDisplay(this);
        display.setFocusableInTouchMode(true);
        display.setOnKeyListener((view,key,event)->{
            if(event.getAction()!=KeyEvent.ACTION_DOWN)return false;
            if(key==KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE||key==KeyEvent.KEYCODE_VOLUME_MUTE){
                display.toggleMute();return true;
            }
            if(key==KeyEvent.KEYCODE_BACK){finish();return true;}
            return false;
        });
        setContentView(display);
        display.requestFocus();
    }
    @Override public void onDreamingStarted(){super.onDreamingStarted();display.start();}
    @Override public void onDreamingStopped(){if(display!=null)display.stop();super.onDreamingStopped();}
    @Override public void onDetachedFromWindow(){if(display!=null)display.stop();super.onDetachedFromWindow();}
}
