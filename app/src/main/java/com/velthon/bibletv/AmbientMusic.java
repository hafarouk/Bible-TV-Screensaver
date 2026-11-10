package com.velthon.bibletv;
import android.content.Context;
import android.media.MediaPlayer;
/** Original, offline, optional soft background tracks. */
final class AmbientMusic {
 private MediaPlayer player;
 void start(Context context,int volume,String choice) {
   stop();
   if(volume<=0 || "off".equals(choice))return;
   int track="strings".equals(choice)?R.raw.music_strings:
     "ambient".equals(choice)?R.raw.music_ambient:R.raw.music_piano;
   try {
     player=MediaPlayer.create(context,track);
     if(player==null){android.util.Log.e("BibleTV","Music player was not created for track "+track);return;}
     player.setLooping(true);
     // 10-60% setting maps to subtle background levels to avoid overpowering verses.
     float level=Math.min(0.65f,Math.max(0f,volume/100f)*1.0f);
     player.setVolume(level,level);
     player.start();
   } catch(Exception e){stop();android.util.Log.w("BibleTV","Unable to start selected music",e);}
 }
 void stop(){
   if(player!=null){
     try{if(player.isPlaying())player.stop();}catch(Exception ignored){}
     try{player.release();}catch(Exception ignored){}player=null;
   }
 }
}
