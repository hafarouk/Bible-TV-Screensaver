package com.velthon.bibletv;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Shader;
import android.view.View;

/** Animated curved page edge with a moving shadow and illuminated reverse surface. */
final class PageCurlView extends View {
 private final Bitmap oldPage;
 private final Paint imagePaint = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG);
 private final Paint shade = new Paint(Paint.ANTI_ALIAS_FLAG);
 private float progress;
 PageCurlView(Context context, Bitmap snapshot) { super(context); oldPage = snapshot; setLayerType(View.LAYER_TYPE_SOFTWARE, null); }
 void setProgress(float value) { progress = Math.max(0f, Math.min(1f, value)); invalidate(); }
 @Override protected void onDraw(Canvas canvas) {
  super.onDraw(canvas);
  if (oldPage == null || oldPage.isRecycled()) return;
  float w=getWidth(),h=getHeight(); if(w<=0||h<=0)return;
  float edge=w*(1f-progress);
  float fold=Math.min(w*.17f,Math.max(2f,w*.19f*(float)Math.sin(Math.PI*progress)));
  canvas.save();
  canvas.clipRect(0,0,Math.max(0,edge),h);
  canvas.drawBitmap(oldPage,null,new RectF(0,0,w,h),imagePaint);
  canvas.restore();
  if(progress<=0f||progress>=1f)return;
  // Soft moving contact shadow on the outgoing page.
  shade.setShader(new LinearGradient(edge-fold*2,0,edge+fold*.4f,0,
    new int[]{0x00000000,0x99000000,0x22000000},null,Shader.TileMode.CLAMP));
  canvas.drawRect(Math.max(0,edge-fold*2),0,Math.min(w,edge+fold),h,shade);
  shade.setShader(null);
  // Narrow curved reverse face; a highlighted ridge reads as paper rather than a rotating flat screen.
  Path curl=new Path();
  curl.moveTo(edge,0);
  curl.cubicTo(edge+fold*.95f,h*.22f,edge+fold*.2f,h*.72f,edge+fold*.85f,h);
  curl.lineTo(Math.max(0,edge-fold*.36f),h);
  curl.cubicTo(edge-fold*.65f,h*.67f,edge-fold*.65f,h*.28f,edge,0);
  curl.close();
  shade.setShader(new LinearGradient(edge-fold,0,edge+fold,0,
    new int[]{0xff6a625a,0xfff3e5cc,0xffa59b8b,0xff443e39},null,Shader.TileMode.CLAMP));
  canvas.drawPath(curl,shade);
  shade.setShader(null);
 }
}
