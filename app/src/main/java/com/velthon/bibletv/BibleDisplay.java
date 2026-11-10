package com.velthon.bibletv;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Canvas;
import android.graphics.Bitmap;
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
import android.animation.AnimatorListenerAdapter;
import java.util.ArrayList;
import java.util.Random;
import org.json.JSONArray;
import org.json.JSONObject;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.InputStream;
import java.io.FileOutputStream;
import java.net.URL;
import javax.net.ssl.HttpsURLConnection;
public class BibleDisplay extends FrameLayout {
 private SharedPreferences prefs;private Handler handler=new Handler(Looper.getMainLooper());
 private ImageView picture;private View fallback;private TextView verse,reference,caption;
 private AnimatorSet motion;private int index=-1;private boolean running=false;
 private ImageView turningPage;
 private String[][] verses;
 private final Random random=new Random();
 private final AmbientMusic music=new AmbientMusic();
 private static final long ONE_HOUR_MS=3600000L;
 // Public-domain artwork from Wikimedia Commons. Loaded once and cached locally for repeat/offline viewing.
 // Real pictures are packaged offline during the APK build, so no network is needed.
 private static final int[] ART={R.drawable.shepherd,R.drawable.storm,R.drawable.welcome,R.drawable.rest};
 private static final String[][] FALLBACK_VERSES={
 {"الرَّبُّ رَاعِيَّ فَلاَ يُعْوِزُنِي شَيْءٌ.","The LORD is my shepherd; I shall not want.","مزمور ٢٣:١ • Psalm 23:1"},
 {"لاَ يَضْطَرِبْ قَلْبُكُمْ. أَنْتُمْ تُؤْمِنُونَ بِاللهِ فَآمِنُوا بِي.","Let not your heart be troubled: ye believe in God, believe also in me.","يوحنا ١٤:١ • John 14:1"},
 {"تَعَالَوْا إِلَيَّ يَا جَمِيعَ الْمُتْعَبِينَ وَالثَّقِيلِي الأَحْمَالِ وَأَنَا أُرِيحُكُمْ.","Come unto me, all ye that labour and are heavy laden, and I will give you rest.","متى ١١:٢٨ • Matthew 11:28"},
 {"كُلَّ شَيْءٍ أَسْتَطِيعُ فِي الْمَسِيحِ الَّذِي يُقَوِّينِي.","I can do all things through Christ which strengtheneth me.","فيلبي ٤:١٣ • Philippians 4:13"},
 {"لاَ تَخَفْ لأَنِّي مَعَكَ.","Fear thou not; for I am with thee.","إشعياء ٤١:١٠ • Isaiah 41:10"},
 {"أَنَا هُوَ نُورُ الْعَالَمِ.","I am the light of the world.","يوحنا ٨:١٢ • John 8:12"},
 {"لأَنَّهُ هكَذَا أَحَبَّ اللهُ الْعَالَمَ حَتَّى بَذَلَ ابْنَهُ الْوَحِيدَ.","For God so loved the world, that he gave his only begotten Son.","يوحنا ٣:١٦ • John 3:16"},
 {"بِالسُّكُونِ وَالطُّمَأْنِينَةِ تَكُونُ قُوَّتُكُمْ.","In quietness and in confidence shall be your strength.","إشعياء ٣٠:١٥ • Isaiah 30:15"}
 };
 public BibleDisplay(Context c){super(c);prefs=c.getSharedPreferences("settings",Context.MODE_PRIVATE);verses=loadVerses(c);
 fallback=new View(c);fallback.setBackground(new GradientDrawable(GradientDrawable.Orientation.TL_BR,new int[]{0xff0b1b31,0xff435971,0xffc38b53}));addView(fallback,new LayoutParams(-1,-1));
 picture=new ImageView(c);picture.setScaleType(ImageView.ScaleType.CENTER_CROP);addView(picture,new LayoutParams(-1,-1));
 View shade=new View(c);shade.setBackground(new GradientDrawable(GradientDrawable.Orientation.TOP_BOTTOM,new int[]{0x66000000,0x33000000,0xdd050c17}));addView(shade,new LayoutParams(-1,-1));
 LinearLayout card=new LinearLayout(c);card.setGravity(Gravity.CENTER);card.setOrientation(LinearLayout.VERTICAL);card.setPadding(40,25,40,25);addView(card,new LayoutParams(-1,-1));
 LinearLayout brand=new LinearLayout(c);brand.setGravity(Gravity.CENTER);brand.setOrientation(LinearLayout.HORIZONTAL);
 ImageView velthon=new ImageView(c);velthon.setImageResource(R.drawable.velthon_original);velthon.setScaleType(ImageView.ScaleType.FIT_CENTER);
 LinearLayout.LayoutParams logoParams=new LinearLayout.LayoutParams(46,47);logoParams.rightMargin=12;brand.addView(velthon,logoParams);
 TextView mark=new TextView(c);mark.setText("✝  BIBLE TV");mark.setTextColor(0xffffd68a);mark.setTextSize(24);mark.setGravity(Gravity.CENTER_VERTICAL);brand.addView(mark);
 card.addView(brand);
 verse=new TextView(c);verse.setGravity(Gravity.CENTER);verse.setTextDirection(View.TEXT_DIRECTION_FIRST_STRONG);verse.setTextColor(Color.WHITE);verse.setTextSize(37);verse.setTypeface(Typeface.DEFAULT,Typeface.BOLD);verse.setShadowLayer(8,2,3,Color.BLACK);verse.setPadding(10,22,10,22);card.addView(verse);
 reference=new TextView(c);reference.setGravity(Gravity.CENTER);reference.setTextColor(0xffffdfa2);reference.setTextSize(22);reference.setShadowLayer(5,2,2,Color.BLACK);card.addView(reference);
 caption=new TextView(c);caption.setText("Velthon Design & Development");caption.setTextColor(0xffe8e0d0);caption.setTextSize(12);caption.setGravity(Gravity.CENTER);LayoutParams cp=new LayoutParams(-1,-2,Gravity.BOTTOM);cp.bottomMargin=18;addView(caption,cp);
 }
 public void start(){running=true;music.start(getContext(),prefs.getInt("music_volume",10),prefs.getString("music_track","off"));next();}
 public void stop(){running=false;music.stop();handler.removeCallbacksAndMessages(null);if(motion!=null)motion.cancel();if(turningPage!=null){turningPage.animate().cancel();removeView(turningPage);turningPage=null;}}
 public void next(){handler.removeCallbacksAndMessages(null);if(!running)return;int chosen=chooseIndex();
 if(chosen<0){handler.postDelayed(()->next(),1000L);return;}
 // Capture the previous screen before changing its verse, for an animated Bible page turn.
 Bitmap previousScreen=null;
 String transition=prefs.getString("transition_choice","page");
 if(index>=0 && getWidth()>0 && getHeight()>0 && !"none".equals(transition)){
   try{previousScreen=Bitmap.createBitmap(getWidth(),getHeight(),Bitmap.Config.ARGB_8888);draw(new Canvas(previousScreen));}
   catch(OutOfMemoryError ignored){previousScreen=null;}
 }
 if(turningPage!=null){turningPage.animate().cancel();removeView(turningPage);turningPage=null;}
 index=chosen;
 String[] v=verses[index];String l=prefs.getString("lang","ar");verse.setText(l.equals("en")?v[1]:l.equals("both")?v[0]+"\n\n"+v[1]:v[0]);reference.setText(v[2]);
 int theme=prefs.getInt("style",0);
 int photo=theme==1? (index%2==0?1:3) : theme==2? (index*3+1)%4 : index%4;
 picture.animate().cancel();
 picture.setImageResource(ART[photo]);
 picture.setAlpha(0f);picture.animate().alpha(1f).setDuration(850).start();
 animatePicture();
 if(previousScreen!=null)animateTransition(previousScreen,transition);
 handler.postDelayed(()->next(),Math.max(15,prefs.getInt("interval",30))*1000L);
 }
 // The translation pair comes from public-domain SVD and KJV, packaged for offline use.
 private String[][] loadVerses(Context context){
   try(InputStream input=context.getAssets().open("bible_verses.json");ByteArrayOutputStream buffer=new ByteArrayOutputStream()){
     byte[] bytes=new byte[8192];int n;while((n=input.read(bytes))!=-1)buffer.write(bytes,0,n);
     JSONArray data=new JSONArray(buffer.toString("UTF-8"));
     if(data.length()<250)throw new IllegalStateException("At least 250 verses required");
     String[][] all=new String[data.length()][3];
     for(int i=0;i<data.length();i++){
       JSONObject v=data.getJSONObject(i);
       all[i][0]=v.getString("ar");all[i][1]=v.getString("en");all[i][2]=v.getString("ref");
     }
     return all;
   }catch(Exception e){android.util.Log.e("BibleTV","Offline verse library unavailable",e);return FALLBACK_VERSES;}
 }
 // One-hour cooldown survives app restarts and never intentionally selects an ineligible verse.
 private int chooseIndex(){
   long now=System.currentTimeMillis(),cutoff=now-ONE_HOUR_MS;
   ArrayList<Integer> eligible=new ArrayList<>();
   for(int i=0;i<verses.length;i++){
     long last=prefs.getLong("verse_seen_"+verses[i][2],0L);
     if(last<=cutoff && i!=index)eligible.add(i);
   }
   if(eligible.isEmpty())return -1;
   int chosen=eligible.get(random.nextInt(eligible.size()));
   prefs.edit().putLong("verse_seen_"+verses[chosen][2],now).apply();
   return chosen;
 }
 private void animateTransition(Bitmap oldScreen,String type){
   ImageView page=new ImageView(getContext());
   page.setImageBitmap(oldScreen);
   page.setScaleType(ImageView.ScaleType.FIT_XY);
   page.setLayoutParams(new FrameLayout.LayoutParams(-1,-1));
   addView(page);turningPage=page;
   page.setPivotX(0f);page.setPivotY(getHeight()/2f);
   page.setCameraDistance(8000f*getResources().getDisplayMetrics().density);
   if("fade".equals(type)){
     page.animate().alpha(0f).setDuration(850).setListener(new AnimatorListenerAdapter(){
       @Override public void onAnimationEnd(Animator a){clearPage(page,oldScreen);}
       @Override public void onAnimationCancel(Animator a){clearPage(page,oldScreen);}
     }).start();
   }else{
     // A lightweight 3D Bible-page-turn approximation, optimized for low-power Android TVs.
     page.animate().rotationY(-86f).translationX(-getWidth()*0.03f).alpha(0.08f).setDuration(950).setListener(new AnimatorListenerAdapter(){
       @Override public void onAnimationEnd(Animator a){clearPage(page,oldScreen);}
       @Override public void onAnimationCancel(Animator a){clearPage(page,oldScreen);}
     }).start();
   }
 }
 private void clearPage(ImageView page,Bitmap image){
   if(page.getParent()==this)removeView(page);
   if(turningPage==page)turningPage=null;
   if(!image.isRecycled())image.recycle();
 }
 private void animatePicture(){if(motion!=null)motion.cancel();picture.setScaleX(1.0f);picture.setScaleY(1.0f);
 if(!prefs.getBoolean("motion",true))return;
 ObjectAnimator x=ObjectAnimator.ofFloat(picture,"scaleX",1.0f,1.22f);ObjectAnimator y=ObjectAnimator.ofFloat(picture,"scaleY",1.0f,1.22f);
 int[] speeds={12000,8000,5000};long duration=speeds[Math.max(0,Math.min(2,prefs.getInt("motion_speed",0)))];
 x.setDuration(duration);y.setDuration(duration);x.setRepeatCount(ObjectAnimator.INFINITE);y.setRepeatCount(ObjectAnimator.INFINITE);x.setRepeatMode(ObjectAnimator.REVERSE);y.setRepeatMode(ObjectAnimator.REVERSE);
 motion=new AnimatorSet();motion.playTogether(x,y);motion.start();
 }
}
