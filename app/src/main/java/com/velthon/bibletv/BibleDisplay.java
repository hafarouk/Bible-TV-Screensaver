package com.velthon.bibletv;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.os.Handler;
import android.os.Looper;
import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.animation.ObjectAnimator;
import android.animation.AnimatorSet;
import android.animation.Animator;
import java.io.File;
import java.io.InputStream;
import java.io.FileOutputStream;
import java.net.URL;
import javax.net.ssl.HttpsURLConnection;
public class BibleDisplay extends FrameLayout {
 private SharedPreferences prefs;private Handler handler=new Handler(Looper.getMainLooper());
 private ImageView picture;private View fallback;private TextView verse,reference,caption;
 private AnimatorSet motion;private int index=-1;private boolean running=false;
 private final AmbientMusic music=new AmbientMusic();
 private static final long ONE_HOUR_MS=3600000L;
 // Public-domain artwork from Wikimedia Commons. Loaded once and cached locally for repeat/offline viewing.
 private static final String[] IMAGES={
 "https://commons.wikimedia.org/wiki/Special:FilePath/GoodShepherdYoungJesus.jpg?width=1024",
 "https://commons.wikimedia.org/wiki/Special:FilePath/Brooklyn_Museum_-_Jesus_Stilling_the_Tempest_(J%C3%A9sus_calmant_la_temp%C3%AAte)_-_James_Tissot_-_overall.jpg?width=1024",
 "https://commons.wikimedia.org/wiki/Special:FilePath/The_Lord_is_my_Good_Shepherd.jpg?width=1024",
 "https://commons.wikimedia.org/wiki/Special:FilePath/Simon_de_Vlieger_-_Calming_the_storm.jpg?width=1024"};
 private static final String[][] VERSES={
 {"الرَّبُّ رَاعِيَّ فَلاَ يُعْوِزُنِي شَيْءٌ.","The LORD is my shepherd; I shall not want.","مزمور ٢٣:١ • Psalm 23:1"},
 {"لاَ يَضْطَرِبْ قَلْبُكُمْ. أَنْتُمْ تُؤْمِنُونَ بِاللهِ فَآمِنُوا بِي.","Let not your heart be troubled: ye believe in God, believe also in me.","يوحنا ١٤:١ • John 14:1"},
 {"تَعَالَوْا إِلَيَّ يَا جَمِيعَ الْمُتْعَبِينَ وَالثَّقِيلِي الأَحْمَالِ وَأَنَا أُرِيحُكُمْ.","Come unto me, all ye that labour and are heavy laden, and I will give you rest.","متى ١١:٢٨ • Matthew 11:28"},
 {"كُلَّ شَيْءٍ أَسْتَطِيعُ فِي الْمَسِيحِ الَّذِي يُقَوِّينِي.","I can do all things through Christ which strengtheneth me.","فيلبي ٤:١٣ • Philippians 4:13"},
 {"لاَ تَخَفْ لأَنِّي مَعَكَ.","Fear thou not; for I am with thee.","إشعياء ٤١:١٠ • Isaiah 41:10"},
 {"أَنَا هُوَ نُورُ الْعَالَمِ.","I am the light of the world.","يوحنا ٨:١٢ • John 8:12"},
 {"لأَنَّهُ هكَذَا أَحَبَّ اللهُ الْعَالَمَ حَتَّى بَذَلَ ابْنَهُ الْوَحِيدَ.","For God so loved the world, that he gave his only begotten Son.","يوحنا ٣:١٦ • John 3:16"},
 {"بِالسُّكُونِ وَالطُّمَأْنِينَةِ تَكُونُ قُوَّتُكُمْ.","In quietness and in confidence shall be your strength.","إشعياء ٣٠:١٥ • Isaiah 30:15"}
 };
 public BibleDisplay(Context c){super(c);prefs=c.getSharedPreferences("settings",Context.MODE_PRIVATE);
 fallback=new View(c);fallback.setBackground(new GradientDrawable(GradientDrawable.Orientation.TL_BR,new int[]{0xff0b1b31,0xff435971,0xffc38b53}));addView(fallback,new LayoutParams(-1,-1));
 picture=new ImageView(c);picture.setScaleType(ImageView.ScaleType.CENTER_CROP);addView(picture,new LayoutParams(-1,-1));
 View shade=new View(c);shade.setBackground(new GradientDrawable(GradientDrawable.Orientation.TOP_BOTTOM,new int[]{0x66000000,0x33000000,0xdd050c17}));addView(shade,new LayoutParams(-1,-1));
 LinearLayout card=new LinearLayout(c);card.setGravity(Gravity.CENTER);card.setOrientation(LinearLayout.VERTICAL);card.setPadding(40,25,40,25);addView(card,new LayoutParams(-1,-1));
 TextView mark=new TextView(c);mark.setText("✝   BIBLE TV");mark.setTextColor(0xffffd68a);mark.setTextSize(24);mark.setGravity(Gravity.CENTER);card.addView(mark);
 verse=new TextView(c);verse.setGravity(Gravity.CENTER);verse.setTextDirection(View.TEXT_DIRECTION_FIRST_STRONG);verse.setTextColor(Color.WHITE);verse.setTextSize(37);verse.setTypeface(Typeface.DEFAULT,Typeface.BOLD);verse.setShadowLayer(8,2,3,Color.BLACK);verse.setPadding(10,22,10,22);card.addView(verse);
 reference=new TextView(c);reference.setGravity(Gravity.CENTER);reference.setTextColor(0xffffdfa2);reference.setTextSize(22);reference.setShadowLayer(5,2,2,Color.BLACK);card.addView(reference);
 caption=new TextView(c);caption.setText("Velthon Design & Development");caption.setTextColor(0xffe8e0d0);caption.setTextSize(12);caption.setGravity(Gravity.CENTER);LayoutParams cp=new LayoutParams(-1,-2,Gravity.BOTTOM);cp.bottomMargin=18;addView(caption,cp);
 }
 public void start(){running=true;if(prefs.getBoolean("music",false))music.start(prefs.getInt("music_volume",25));next();}
 public void stop(){running=false;music.stop();handler.removeCallbacksAndMessages(null);if(motion!=null)motion.cancel();}
 public void next(){handler.removeCallbacksAndMessages(null);if(!running)return;index=chooseIndex();
 String[] v=VERSES[index];String l=prefs.getString("lang","ar");verse.setText(l.equals("en")?v[1]:l.equals("both")?v[0]+"\n\n"+v[1]:v[0]);reference.setText(v[2]);
 int theme=prefs.getInt("style",0);int photo=(theme==1?index%2+2:index%4);
 picture.setImageDrawable(null);loadPicture(photo,index);animatePicture();
 handler.postDelayed(()->next(),Math.max(15,prefs.getInt("interval",30))*1000L);
 }
 // Persist verse timestamps between sessions to reduce repetition.
 private int chooseIndex(){
   long now=System.currentTimeMillis(),cutoff=now-ONE_HOUR_MS;
   int choice=-1;long oldest=Long.MAX_VALUE;
   for(int i=0;i<VERSES.length;i++){
     long seen=prefs.getLong("verse_seen_"+i,0L);
     if(seen<cutoff){choice=i;break;}
     if(seen<oldest){oldest=seen;choice=i;}
   }
   if(choice<0)choice=0;
   // With eight verses this fallback can repeat early; expand the offline verse catalog before enforcing a strict cooldown.
   prefs.edit().putLong("verse_seen_"+choice,now).apply();
   return choice;
 }
 private void animatePicture(){if(motion!=null)motion.cancel();picture.setScaleX(1.0f);picture.setScaleY(1.0f);
 if(!prefs.getBoolean("motion",true))return;
 ObjectAnimator x=ObjectAnimator.ofFloat(picture,"scaleX",1.0f,1.16f);ObjectAnimator y=ObjectAnimator.ofFloat(picture,"scaleY",1.0f,1.16f);
 int[] speeds={24000,15000,8000};long duration=speeds[Math.max(0,Math.min(2,prefs.getInt("motion_speed",0)))];
 x.setDuration(duration);y.setDuration(duration);x.setRepeatCount(ObjectAnimator.INFINITE);y.setRepeatCount(ObjectAnimator.INFINITE);x.setRepeatMode(ObjectAnimator.REVERSE);y.setRepeatMode(ObjectAnimator.REVERSE);
 motion=new AnimatorSet();motion.playTogether(x,y);motion.start();
 }
 private void loadPicture(int num,int expected){new Thread(()->{Bitmap bmp=null;File cache=new File(getContext().getCacheDir(),"biblical_"+num+".jpg");
 try{if(cache.exists())bmp=BitmapFactory.decodeFile(cache.getAbsolutePath());
 if(bmp==null){HttpsURLConnection connection=(HttpsURLConnection)new URL(IMAGES[num]).openConnection();connection.setConnectTimeout(6500);connection.setReadTimeout(8000);connection.setInstanceFollowRedirects(true);connection.setRequestProperty("User-Agent","BibleTVScreensaver/1.1 (Android TV)");
 try(InputStream in=connection.getInputStream();FileOutputStream out=new FileOutputStream(cache)){byte[] b=new byte[8192];int n;while((n=in.read(b))>0)out.write(b,0,n);}
 connection.disconnect();bmp=BitmapFactory.decodeFile(cache.getAbsolutePath());
 }}catch(Exception ignored){}
 Bitmap finalBitmap=bmp;handler.post(()->{if(running&&expected==index&&finalBitmap!=null){picture.setImageBitmap(finalBitmap);picture.setAlpha(0);picture.animate().alpha(1).setDuration(900).start();}});
 }).start();}
}
