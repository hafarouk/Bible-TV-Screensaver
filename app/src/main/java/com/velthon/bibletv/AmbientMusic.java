package com.velthon.bibletv;
import android.content.Context;
import android.media.MediaPlayer;
/** Plays an original, pre-rendered loop from app resources, never a network stream. */
final class AmbientMusic {
 private MediaPlayer player;
 void start(Context context,int volume) {
  stop();
  if(volume<=0)return;
  try {
    player=MediaPlayer.create(context,R.raw.ambient_original);
    if(player==null)return;
    player.setLooping(true);
    float level=Math.min(1f,Math.max(0f,volume/100f))*0.55f;
    player.setVolume(level,level);
    player.start();
  } catch(Exception e){stop();android.util.Log.w("BibleTV","Unable to start ambient audio",e);}
 }
 void stop(){
  if(player!=null){try{if(player.isPlaying())player.stop();}catch(Exception ignored){}
    try{player.release();}catch(Exception ignored){}player=null;}
 }
}
