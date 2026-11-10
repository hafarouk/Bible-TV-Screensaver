package com.velthon.bibletv;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Canvas;
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
 private View turningPage;
 private SceneLoopView sceneLoop;
 private TextView sceneTag;
 private int mediaIndex=-1;
 private TextView muteButton;
 private boolean temporaryMuted=false;
 private String[][] verses;
 private final Random random=new Random();
 private final AmbientMusic music=new AmbientMusic();
 private static final long ONE_HOUR_MS=3600000L;
 // Public-domain artwork from Wikimedia Commons. Loaded once and cached locally for repeat/offline viewing.
 // Real pictures are packaged offline during the APK build, so no network is needed.
 private static final int[] ART={R.drawable.shepherd,R.drawable.storm,R.drawable.welcome,R.drawable.scene_sunrise,R.drawable.scene_ocean,R.drawable.scene_mountain,R.drawable.scene_garden,R.drawable.scene_stars};
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
 sceneLoop=new SceneLoopView(c);addView(sceneLoop,new LayoutParams(-1,-1));
 View shade=new View(c);shade.setBackground(new GradientDrawable(GradientDrawable.Orientation.TOP_BOTTOM,new int[]{0x66000000,0x33000000,0xdd050c17}));addView(shade,new LayoutParams(-1,-1));
 LinearLayout card=new LinearLayout(c);card.setGravity(Gravity.CENTER);card.setOrientation(LinearLayout.VERTICAL);card.setPadding(40,25,40,25);addView(card,new LayoutParams(-1,-1));

 verse=new TextView(c);verse.setGravity(Gravity.CENTER);verse.setTextDirection(View.TEXT_DIRECTION_FIRST_STRONG);verse.setTextColor(Color.WHITE);verse.setTextSize(37);verse.setTypeface(Typeface.DEFAULT,Typeface.BOLD);verse.setShadowLayer(8,2,3,Color.BLACK);verse.setPadding(10,22,10,22);card.addView(verse);
 reference=new TextView(c);reference.setGravity(Gravity.CENTER);reference.setTextColor(0xffffdfa2);reference.setTextSize(22);reference.setShadowLayer(5,2,2,Color.BLACK);card.addView(reference);
 LinearLayout footer=new LinearLayout(c);footer.setGravity(Gravity.CENTER);footer.setOrientation(LinearLayout.HORIZONTAL);

 caption=new TextView(c);caption.setText("By: VELTHON Design & Development");caption.setTextColor(0xffe8e0d0);caption.setTextSize(13);caption.setShadowLayer(4,1,2,Color.BLACK);caption.setGravity(Gravity.CENTER_VERTICAL);footer.addView(caption);
 LayoutParams cp=new LayoutParams(-1,42,Gravity.BOTTOM);cp.bottomMargin=12;addView(footer,cp);
 muteButton=new TextView(c);
 muteButton.setTextSize(23);muteButton.setGravity(Gravity.CENTER);
 muteButton.setTextColor(0xffffffff);muteButton.setFocusable(true);muteButton.setClickable(true);
 muteButton.setContentDescription("Mute or unmute background music");
 GradientDrawable pill=new GradientDrawable();pill.setColor(0x99081422);pill.setCornerRadius(36);pill.setStroke(1,0x77ffffff);
 muteButton.setBackground(pill);
 FrameLayout.LayoutParams mp=new FrameLayout.LayoutParams(64,52,Gravity.TOP|Gravity.RIGHT);mp.setMargins(0,22,28,0);
 addView(muteButton,mp);
 muteButton.setOnClickListener(v->toggleMute());
 updateMuteIcon();
 }
 public void start(){running=true;temporaryMuted=false;applyMusic();updateMuteIcon();next();}
 public void toggleMute(){temporaryMuted=!temporaryMuted;applyMusic();updateMuteIcon();}
 private void applyMusic(){
   String track=prefs.getString("music_track","piano");
   if(!running||temporaryMuted||"off".equals(track)){music.stop();return;}
   music.start(getContext(),prefs.getInt("music_volume",25),track);
 }
 private void updateMuteIcon(){
   if(muteButton==null)return;
   boolean disabled="off".equals(prefs.getString("music_track","piano"));
   muteButton.setText(disabled||temporaryMuted?"🔇":"🔊");
   muteButton.setContentDescription(disabled?"Music is turned off in settings":temporaryMuted?"Unmute music":"Mute music");
   muteButton.setVisibility(disabled?View.GONE:View.VISIBLE);
 }
 public void stop(){running=false;sceneLoop.setVisibility(View.GONE);music.stop();handler.removeCallbacksAndMessages(null);if(motion!=null)motion.cancel();if(turningPage!=null){turningPage.animate().cancel();removeView(turningPage);turningPage=null;}}
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
 // Link artwork to the meaning of the verse, not just the iteration number.
 String ar=v[0],en=v[1].toLowerCase(java.util.Locale.US),ref=v[2].toLowerCase(java.util.Locale.US);
 String selectedScene;
 if(en.contains("shepherd")||en.contains("sheep")||en.contains("pasture")||ref.startsWith("ps"))selectedScene="shepherd";
 else if(en.contains("storm")||en.contains("tempest")||en.contains("wind")||en.contains("sea")||en.contains("water")||en.contains("fear"))selectedScene="storm";
 else if(en.contains("child")||en.contains("children")||en.contains("come unto")||en.contains("love")||en.contains("peace")||en.contains("heart"))selectedScene="welcome";
 else selectedScene="shepherd";
 // Keep the verse-matched scene independent of the background-style preference.
 int photo=selectedScene.equals("storm")?1:selectedScene.equals("welcome")?2:0;
 if(en.contains("light")||en.contains("morning")||en.contains("sun"))photo=3;
 else if(en.contains("sea")||en.contains("water")||en.contains("river"))photo=4;
 else if(en.contains("mountain")||en.contains("strength")||en.contains("rock"))photo=5;
 else if(en.contains("flower")||en.contains("garden")||en.contains("grow"))photo=6;
 else if(en.contains("heaven")||en.contains("star")||en.contains("night"))photo=7;
 else if(photo==0 && !en.contains("shepherd") && !en.contains("sheep"))photo=3+Math.abs(index%5);
 
 String mode=prefs.getString("media_mode","animated");
 boolean animated=!"photos".equals(mode);
 sceneLoop.setVisibility(animated?View.VISIBLE:View.GONE);
 picture.setVisibility(animated?View.GONE:View.VISIBLE);
 if(animated){int nextMedia;do{nextMedia=random.nextInt(100);}while(nextMedia==mediaIndex);mediaIndex=nextMedia;sceneLoop.setScene(String.format(java.util.Locale.US,"motion_%02d",mediaIndex));}
 picture.animate().cancel();
 picture.setImageResource(ART[photo]);
 if(!animated){picture.setAlpha(0f);picture.animate().alpha(1f).setDuration(850).start();animatePicture();}
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
   if("fade".equals(type)){
     ImageView overlay=new ImageView(getContext());
     overlay.setImageBitmap(oldScreen);overlay.setScaleType(ImageView.ScaleType.FIT_XY);
     addView(overlay,new FrameLayout.LayoutParams(-1,-1));turningPage=overlay;
     overlay.animate().alpha(0f).setDuration(850).setListener(new AnimatorListenerAdapter(){
       @Override public void onAnimationEnd(Animator a){clearPage(overlay,oldScreen);}
       @Override public void onAnimationCancel(Animator a){clearPage(overlay,oldScreen);}
     }).start();
     return;
   }
   final PageCurlView page=new PageCurlView(getContext(),oldScreen);
   addView(page,new FrameLayout.LayoutParams(-1,-1));turningPage=page;
   android.animation.ValueAnimator curl=android.animation.ValueAnimator.ofFloat(0f,1f);
   curl.setDuration(1450);
   curl.setInterpolator(new android.view.animation.AccelerateDecelerateInterpolator());
   curl.addUpdateListener(a->page.setProgress((Float)a.getAnimatedValue()));
   curl.addListener(new AnimatorListenerAdapter(){
     @Override public void onAnimationEnd(Animator a){clearPage(page,oldScreen);}
     @Override public void onAnimationCancel(Animator a){clearPage(page,oldScreen);}
   });
   curl.start();
 }
 private void clearPage(View page,Bitmap image){
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
