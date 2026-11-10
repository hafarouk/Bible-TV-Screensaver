package com.velthon.bibletv;
import android.media.AudioAttributes;
import android.media.AudioFormat;
import android.media.AudioTrack;
import android.os.Process;
import java.util.concurrent.atomic.AtomicBoolean;
/** Original synthesized, quiet ambient tones. No copyrighted recordings or network access. */
final class AmbientMusic {
 private static final int RATE=22050;
 private final AtomicBoolean playing=new AtomicBoolean(false);
 private Thread worker;
 void start(int volume){
   stop();if(volume<=0)return;playing.set(true);
   final float gain=Math.min(volume,100)/100f*0.035f;
   worker=new Thread(()->{
     Process.setThreadPriority(Process.THREAD_PRIORITY_BACKGROUND);
     AudioTrack track=null;
     try{
       int size=Math.max(4096,AudioTrack.getMinBufferSize(RATE,AudioFormat.CHANNEL_OUT_MONO,AudioFormat.ENCODING_PCM_16BIT));
       track=new AudioTrack.Builder().setAudioAttributes(new AudioAttributes.Builder().setUsage(AudioAttributes.USAGE_MEDIA).setContentType(AudioAttributes.CONTENT_TYPE_MUSIC).build())
        .setAudioFormat(new AudioFormat.Builder().setSampleRate(RATE).setEncoding(AudioFormat.ENCODING_PCM_16BIT).setChannelMask(AudioFormat.CHANNEL_OUT_MONO).build())
        .setTransferMode(AudioTrack.MODE_STREAM).setBufferSizeInBytes(size).build();
       track.play();
       short[] audio=new short[2048];long time=0;
       double[] notes={130.81,146.83,174.61,196.00};
       while(playing.get()&&!Thread.currentThread().isInterrupted()){
         for(int i=0;i<audio.length;i++,time++){
           double sec=time/(double)RATE;int chord=(int)(sec/12)%notes.length;
           double root=notes[chord];
           double smooth=0.32+0.12*Math.sin(2*Math.PI*sec/24);
           double wave=0.35*Math.sin(2*Math.PI*root*sec)+0.22*Math.sin(2*Math.PI*root*1.25*sec)+0.18*Math.sin(2*Math.PI*root*1.5*sec);
           audio[i]=(short)(wave*gain*smooth*Short.MAX_VALUE);
         }
         track.write(audio,0,audio.length);
       }
     }catch(Exception ignored){}finally{if(track!=null){try{track.pause();track.flush();track.release();}catch(Exception ignored){}}}
   },"bible-tv-soft-music");worker.setDaemon(true);worker.start();
 }
 void stop(){playing.set(false);if(worker!=null){worker.interrupt();worker=null;}}
}
