package com.velthon.bibletv;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Movie;
import android.graphics.Paint;
import android.os.SystemClock;
import android.view.View;
import java.io.InputStream;
/** A lightweight animated-GIF player for short, embedded public-domain painting loops. */
public final class SceneLoopView extends View {
 private Movie movie;
 private long started;
 private final Paint paint=new Paint(Paint.ANTI_ALIAS_FLAG|Paint.FILTER_BITMAP_FLAG);
 private String current="";
 public SceneLoopView(Context context){super(context);setLayerType(View.LAYER_TYPE_SOFTWARE,null);}
 public void setScene(String scene){
   if(scene.equals(current))return;
   current=scene;movie=null;
   try(InputStream input=getContext().getAssets().open("scene_loops/"+scene+".gif")){
     movie=Movie.decodeStream(input);
   }catch(Exception e){android.util.Log.w("BibleTV","Missing scene GIF: "+scene,e);}
   started=SystemClock.uptimeMillis();invalidate();
 }
 @Override protected void onDraw(Canvas canvas){
   super.onDraw(canvas);
   if(movie==null)return;
   long elapsed=SystemClock.uptimeMillis()-started;
   int dur=movie.duration();if(dur<1)dur=3000;
   movie.setTime((int)(elapsed%dur));
   int mw=movie.width(),mh=movie.height();if(mw<=0||mh<=0)return;
   float scale=Math.max(getWidth()/(float)mw,getHeight()/(float)mh);
   canvas.save();canvas.translate((getWidth()-mw*scale)/2,(getHeight()-mh*scale)/2);canvas.scale(scale,scale);
   movie.draw(canvas,0,0,paint);canvas.restore();
   postInvalidateDelayed(80);
 }
 @Override protected void onDetachedFromWindow(){movie=null;super.onDetachedFromWindow();}
}
