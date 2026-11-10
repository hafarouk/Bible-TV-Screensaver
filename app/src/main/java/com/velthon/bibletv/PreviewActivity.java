package com.velthon.bibletv;
import android.app.Activity;
import android.os.Bundle;
import android.view.KeyEvent;
public class PreviewActivity extends Activity {
    private BibleDisplay display;
    @Override public void onCreate(Bundle b) {
        super.onCreate(b);
        getWindow().getDecorView().setSystemUiVisibility(5894|1024|512);
        display=new BibleDisplay(this);setContentView(display);
    }
    @Override public void onResume(){super.onResume();display.start();}
    @Override public void onPause(){display.stop();super.onPause();}
    @Override public boolean onKeyDown(int key,KeyEvent e) {
        if(key==KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE || key==KeyEvent.KEYCODE_MEDIA_MUTE || key==KeyEvent.KEYCODE_M){
            display.toggleMute();return true;
        }
        if(key==KeyEvent.KEYCODE_DPAD_RIGHT||key==KeyEvent.KEYCODE_DPAD_LEFT){display.next();return true;}
        return super.onKeyDown(key,e);
    }
}
