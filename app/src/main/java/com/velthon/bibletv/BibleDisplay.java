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
 private ImageView picture;private View fallback;private TextView verse,arabicVerse,reference,englishRef,arabicRef,caption;
 private AnimatorSet motion;private int index=-1;private boolean running=false;
 private View turningPage;
 private SceneLoopView sceneLoop;
 private TextView sceneTag;
 private int mediaIndex=-1;
 private final java.util.HashSet<Integer> seenArtwork=new java.util.HashSet<>();
 private JSONArray artworkManifest;
 private TextView muteButton;private TextView weatherHeader;private final Handler weatherHandler=new Handler(Looper.getMainLooper());
 private boolean temporaryMuted=false;
 private String[][] verses;
 private final Random random=new Random();
 private final AmbientMusic music=new AmbientMusic();
 private static final long ONE_HOUR_MS=3600000L;
 // Public-domain artwork from Wikimedia Commons. Loaded once and cached locally for repeat/offline viewing.
 // Real pictures are packaged offline during the APK build, so no network is needed.
 private static final int[] ART={R.drawable.shepherd,R.drawable.storm,R.drawable.welcome,R.drawable.scene_sunrise,R.drawable.scene_ocean,R.drawable.scene_mountain,R.drawable.scene_garden,R.drawable.scene_stars};
 private static final String[][] FALLBACK_VERSES={
 {"الرَّبُّ رَاعِيَّ فَلاَ يُعْوِزُنِي شَيْءٌ.","The LORD is my shepherd; I shall not want.","مزمور ٢٣:١ • Psalm 23:1","مزمور ٢٣:١","Psalm 23:1"},
 {"لاَ يَضْطَرِبْ قَلْبُكُمْ. أَنْتُمْ تُؤْمِنُونَ بِاللهِ فَآمِنُوا بِي.","Let not your heart be troubled: ye believe in God, believe also in me.","يوحنا ١٤:١ • John 14:1","يوحنا ١٤:١","John 14:1"},
 {"تَعَالَوْا إِلَيَّ يَا جَمِيعَ الْمُتْعَبِينَ وَالثَّقِيلِي الأَحْمَالِ وَأَنَا أُرِيحُكُمْ.","Come unto me, all ye that labour and are heavy laden, and I will give you rest.","متى ١١:٢٨ • Matthew 11:28","متى ١١:٢٨","Matthew 11:28"},
 {"كُلَّ شَيْءٍ أَسْتَطِيعُ فِي الْمَسِيحِ الَّذِي يُقَوِّينِي.","I can do all things through Christ which strengtheneth me.","فيلبي ٤:١٣ • Philippians 4:13","فيلبي ٤:١٣","Philippians 4:13"},
 {"لاَ تَخَفْ لأَنِّي مَعَكَ.","Fear thou not; for I am with thee.","إشعياء ٤١:١٠ • Isaiah 41:10","إشعياء ٤١:١٠","Isaiah 41:10"},
 {"أَنَا هُوَ نُورُ الْعَالَمِ.","I am the light of the world.","يوحنا ٨:١٢ • John 8:12","يوحنا ٨:١٢","John 8:12"},
 {"لأَنَّهُ هكَذَا أَحَبَّ اللهُ الْعَالَمَ حَتَّى بَذَلَ ابْنَهُ الْوَحِيدَ.","For God so loved the world, that he gave his only begotten Son.","يوحنا ٣:١٦ • John 3:16","يوحنا ٣:١٦","John 3:16"},
 {"بِالسُّكُونِ وَالطُّمَأْنِينَةِ تَكُونُ قُوَّتُكُمْ.","In quietness and in confidence shall be your strength.","إشعياء ٣٠:١٥ • Isaiah 30:15","إشعياء ٣٠:١٥","Isaiah 30:15"}
 };
 public BibleDisplay(Context c){super(c);prefs=c.getSharedPreferences("settings",Context.MODE_PRIVATE);verses=loadVerses(c);
 try(InputStream art=c.getAssets().open("jesus_art_manifest.json");ByteArrayOutputStream bytes=new ByteArrayOutputStream()){
 byte[] chunk=new byte[4096];int count;while((count=art.read(chunk))!=-1)bytes.write(chunk,0,count);
 artworkManifest=new JSONArray(bytes.toString("UTF-8"));
 }catch(Exception e){android.util.Log.e("BibleTV","Art library unavailable",e);artworkManifest=new JSONArray();}
 fallback=new View(c);fallback.setBackground(new GradientDrawable(GradientDrawable.Orientation.TL_BR,new int[]{0xffd9edff,0xfff9f5ee,0xfff5e7d3}));addView(fallback,new LayoutParams(-1,-1));
 picture=new ImageView(c);picture.setScaleType(ImageView.ScaleType.CENTER_CROP);addView(picture,new LayoutParams(-1,-1));picture.setVisibility(View.GONE);

 weatherHeader=new TextView(c);weatherHeader.setTextColor(0xff173b65);weatherHeader.setTextSize(18);weatherHeader.setGravity(Gravity.CENTER);weatherHeader.setPadding(18,9,18,9);weatherHeader.setBackgroundColor(0x66ffffff);LayoutParams wh=new LayoutParams(-2,-2,Gravity.TOP|Gravity.CENTER_HORIZONTAL);wh.topMargin=18;addView(weatherHeader,wh);weatherHeader.setText("Loading weather...");
 View shade=new View(c);shade.setBackgroundColor(0x00000000);addView(shade,new LayoutParams(-1,-1));
 
 reference=new TextView(c);reference.setGravity(Gravity.CENTER);reference.setTextColor(0xff173b65);reference.setTextSize(21);reference.setTypeface(Typeface.SERIF,Typeface.BOLD);reference.setPadding(22,8,22,8);GradientDrawable refBg=new GradientDrawable();refBg.setColor(0x99ffffff);refBg.setCornerRadius(38);refBg.setStroke(1,0x55d8a93c);reference.setBackground(refBg);LayoutParams refLp=new LayoutParams(-2,-2,Gravity.BOTTOM|Gravity.LEFT);refLp.setMargins(30,0,0,20);addView(reference,refLp);
 LinearLayout card=new LinearLayout(c);card.setGravity(Gravity.CENTER);card.setOrientation(LinearLayout.VERTICAL);card.setPadding(25,22,25,22);GradientDrawable cardBg=new GradientDrawable();cardBg.setColor(0x55ffffff);cardBg.setCornerRadius(24);cardBg.setStroke(0,0x00ffffff);card.setBackground(cardBg);LayoutParams cardLp=new LayoutParams(-1,-2,Gravity.CENTER);cardLp.leftMargin=70;cardLp.rightMargin=70;addView(card,cardLp);
 verse=new TextView(c);verse.setGravity(Gravity.CENTER);verse.setTextColor(0xff153b65);verse.setTextSize(34);verse.setTypeface(Typeface.SERIF,Typeface.BOLD_ITALIC);verse.setShadowLayer(0,0,0,Color.TRANSPARENT);card.addView(verse);
 englishRef=new TextView(c);englishRef.setGravity(Gravity.CENTER);englishRef.setTextColor(0xff173b65);englishRef.setTextSize(20);englishRef.setTypeface(Typeface.SERIF,Typeface.NORMAL);card.addView(englishRef);
 TextView separator=new TextView(c);separator.setText("━━━━  ✝  ━━━━");separator.setGravity(Gravity.CENTER);separator.setTextColor(0xffffce70);separator.setTextSize(19);separator.setPadding(0,8,0,8);card.addView(separator);
 arabicVerse=new TextView(c);arabicVerse.setGravity(Gravity.CENTER);arabicVerse.setTextDirection(View.TEXT_DIRECTION_RTL);arabicVerse.setTextColor(0xff153b65);arabicVerse.setTextSize(33);arabicVerse.setTypeface(Typeface.DEFAULT,Typeface.BOLD);arabicVerse.setShadowLayer(0,0,0,Color.TRANSPARENT);card.addView(arabicVerse);
 arabicRef=new TextView(c);arabicRef.setGravity(Gravity.CENTER);arabicRef.setTextColor(0xff173b65);arabicRef.setTextSize(20);card.addView(arabicRef);
 LinearLayout footer=new LinearLayout(c);footer.setGravity(Gravity.CENTER);footer.setOrientation(LinearLayout.HORIZONTAL);

 ImageView officialLogo=new ImageView(c);try(InputStream logoStream=c.getAssets().open("velthon_original.b64")){ByteArrayOutputStream lb=new ByteArrayOutputStream();byte[] bb=new byte[4096];int nn;while((nn=logoStream.read(bb))!=-1)lb.write(bb,0,nn);byte[] logoBytes=android.util.Base64.decode(lb.toByteArray(),android.util.Base64.DEFAULT);officialLogo.setImageBitmap(BitmapFactory.decodeByteArray(logoBytes,0,logoBytes.length));}catch(Exception ex){android.util.Log.e("BibleTV","Velthon logo unavailable",ex);}officialLogo.setScaleType(ImageView.ScaleType.FIT_CENTER);LayoutParams olp=new LayoutParams(105,105,Gravity.BOTTOM|Gravity.RIGHT);olp.rightMargin=16;olp.bottomMargin=12;addView(officialLogo,olp);
 caption=new TextView(c);caption.setText("");caption.setTextColor(0xffe8e0d0);caption.setTextSize(13);caption.setShadowLayer(4,1,2,Color.BLACK);caption.setGravity(Gravity.RIGHT|Gravity.CENTER_VERTICAL);footer.addView(caption,new LinearLayout.LayoutParams(-1,-1));
 LayoutParams cp=new LayoutParams(-1,52,Gravity.BOTTOM);cp.bottomMargin=12;cp.leftMargin=250;cp.rightMargin=30;addView(footer,cp);
 muteButton=new TextView(c);
 muteButton.setTextSize(23);muteButton.setGravity(Gravity.CENTER);
 muteButton.setTextColor(0xffffffff);muteButton.setFocusable(true);muteButton.setClickable(true);
 muteButton.setContentDescription("Mute or unmute background music");
 GradientDrawable pill=new GradientDrawable();pill.setColor(0x99081422);pill.setCornerRadius(36);pill.setStroke(1,0x77ffffff);
 muteButton.setBackground(pill);
 FrameLayout.LayoutParams mp=new FrameLayout.LayoutParams(64,52,Gravity.TOP|Gravity.RIGHT);mp.setMargins(0,22,28,0);
 muteButton.setVisibility(View.GONE);
 muteButton.setOnClickListener(v->toggleMute());
 updateMuteIcon();
 }
 public void start(){running=true;updateWeather();temporaryMuted=false;applyMusic();updateMuteIcon();next();}
 private void updateWeather(){
  if(!running)return;
  java.text.SimpleDateFormat clock=new java.text.SimpleDateFormat("h:mm a",java.util.Locale.CANADA);
  String city=prefs.getString("weather_city","Gravenhurst");
  weatherHeader.setText(city+"  |  "+clock.format(new java.util.Date())+"  |  Weather loading");
  new Thread(()->{try{
   String encoded=java.net.URLEncoder.encode(city,"UTF-8");
   java.net.HttpURLConnection geo=(java.net.HttpURLConnection)new URL("https://geocoding-api.open-meteo.com/v1/search?name="+encoded+"&count=1&language=en&format=json").openConnection();geo.setConnectTimeout(7000);geo.setReadTimeout(7000);
   JSONObject loc;try(InputStream in=geo.getInputStream()){loc=new JSONObject(readAll(in)).getJSONArray("results").getJSONObject(0);}finally{geo.disconnect();}
   String lat=String.valueOf(loc.getDouble("latitude")),lon=String.valueOf(loc.getDouble("longitude"));
   java.net.HttpURLConnection con=(java.net.HttpURLConnection)new URL("https://api.open-meteo.com/v1/forecast?latitude="+lat+"&longitude="+lon+"&current=temperature_2m,apparent_temperature,weather_code,wind_speed_10m&timezone=auto").openConnection();con.setConnectTimeout(7000);con.setReadTimeout(7000);
   JSONObject current;try(InputStream in=con.getInputStream()){current=new JSONObject(readAll(in)).getJSONObject("current");}finally{con.disconnect();}
   int code=current.getInt("weather_code");String condition=code==0?"Clear":code<=3?"Partly cloudy":code<=48?"Fog":code<=67?"Rain":code<=77?"Snow":code<=82?"Showers":code<=86?"Snow showers":code<=99?"Thunderstorm":"Cloudy";
   String result=loc.optString("name",city)+"  |  "+Math.round(current.getDouble("temperature_2m"))+"°C  |  "+condition+"  |  Feels "+Math.round(current.getDouble("apparent_temperature"))+"°  |  Wind "+Math.round(current.getDouble("wind_speed_10m"))+" km/h";
   post(()->{if(running){weatherHeader.setTag(result);weatherHeader.setText(result+"  |  "+clock.format(new java.util.Date()));}});
  }catch(Exception ex){post(()->{if(running)weatherHeader.setText(city+"  |  "+clock.format(new java.util.Date())+"  |  Weather unavailable");});}
  }).start();
  weatherHandler.postDelayed(()->{if(running){String data=(String)weatherHeader.getTag();if(data!=null)weatherHeader.setText(data+"  |  "+clock.format(new java.util.Date()));weatherHandler.postDelayed(()->updateWeather(),60000);}},60000);
 }
 private static String readAll(InputStream in)throws Exception{ByteArrayOutputStream b=new ByteArrayOutputStream();byte[] buf=new byte[4096];int n;while((n=in.read(buf))!=-1)b.write(buf,0,n);return b.toString("UTF-8");}
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
   muteButton.setVisibility(View.GONE);
 }
 public void stop(){weatherHandler.removeCallbacksAndMessages(null);running=false;music.stop();handler.removeCallbacksAndMessages(null);if(motion!=null)motion.cancel();if(turningPage!=null){turningPage.animate().cancel();removeView(turningPage);turningPage=null;}}
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
 String[] v=verses[index];String l=prefs.getString("lang","ar");verse.setText(v[1]);verse.setVisibility(l.equals("ar")?View.GONE:View.VISIBLE);arabicVerse.setText(v[0]);verse.setTextSize(v[1].length()>170?23:v[1].length()>100?27:34);arabicVerse.setTextSize(v[0].length()>140?23:v[0].length()>85?27:33);arabicVerse.setVisibility(l.equals("en")?View.GONE:View.VISIBLE);reference.setVisibility(View.GONE);englishRef.setText(v[4]);englishRef.setVisibility(l.equals("ar")?View.GONE:View.VISIBLE);arabicRef.setText(v[3]);arabicRef.setVisibility(l.equals("en")?View.GONE:View.VISIBLE);
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
 
 // Display only static, licensed Jesus artwork. No GIF player or artificial movement.
 picture.animate().cancel();
 if(motion!=null){motion.cancel();motion=null;}
 picture.setScaleX(1f);picture.setScaleY(1f);picture.setTranslationX(0);picture.setTranslationY(0);
 picture.setVisibility(View.GONE);picture.setAlpha(1f);
 try{
   if(artworkManifest.length()>0){
     String wanted=selectedScene;
     if(photo==3)wanted="light";
     else if(photo==4)wanted="water";
     else if(photo==5)wanted="strength";
     else if(photo==6)wanted="peace";
     else if(photo==7)wanted="heaven";
     java.util.ArrayList<Integer> matches=new java.util.ArrayList<>();
     for(int k=0;k<artworkManifest.length();k++){
       if(wanted.equals(artworkManifest.getJSONObject(k).optString("category")))matches.add(k);
     }
     if(matches.isEmpty()){
       for(int k=0;k<artworkManifest.length();k++)matches.add(k);
     }
     // Prefer unseen images; never reuse a picture until the entire 50-image library is shown.
     if(seenArtwork.size()>=artworkManifest.length())seenArtwork.clear();
     java.util.ArrayList<Integer> unseen=new java.util.ArrayList<>();
     for(Integer candidate:matches)if(!seenArtwork.contains(candidate))unseen.add(candidate);
     if(unseen.isEmpty()){
       for(int k=0;k<artworkManifest.length();k++)if(!seenArtwork.contains(k))unseen.add(k);
     }
     if(unseen.isEmpty()){seenArtwork.clear();unseen.addAll(matches);}
     int nextMedia=unseen.get(random.nextInt(unseen.size()));
     seenArtwork.add(nextMedia);
     mediaIndex=nextMedia;
     String filename=artworkManifest.getJSONObject(mediaIndex).getString("file");
     try(InputStream stream=getContext().getAssets().open("jesus_art/"+filename)){
       android.graphics.BitmapFactory.Options options=new android.graphics.BitmapFactory.Options();
       options.inPreferredConfig=Bitmap.Config.ARGB_8888;options.inScaled=false;
       Bitmap image=BitmapFactory.decodeStream(stream,null,options);
       if(image==null)throw new IllegalStateException("Invalid artwork "+filename);
       picture.setImageBitmap(image);
     }
   }else picture.setImageResource(ART[photo]);
 }catch(Exception error){android.util.Log.e("BibleTV","Could not display artwork",error);picture.setImageResource(ART[photo]);}
 if(previousScreen!=null)animateTransition(previousScreen,transition);
 handler.postDelayed(()->next(),Math.max(15,prefs.getInt("interval",30))*1000L);
 }
 // The translation pair comes from public-domain SVD and KJV, packaged for offline use.
 private String[][] loadVerses(Context context){
   try(InputStream input=context.getAssets().open("bible_verses.json");ByteArrayOutputStream buffer=new ByteArrayOutputStream()){
     byte[] bytes=new byte[8192];int n;while((n=input.read(bytes))!=-1)buffer.write(bytes,0,n);
     JSONArray data=new JSONArray(buffer.toString("UTF-8"));
     if(data.length()<250)throw new IllegalStateException("At least 250 verses required");
     String[][] all=new String[data.length()][5];
     for(int i=0;i<data.length();i++){
       JSONObject v=data.getJSONObject(i);
       all[i][0]=v.getString("ar");all[i][1]=v.getString("en");all[i][2]=v.getString("ref");
       all[i][3]=v.getString("ref_ar");all[i][4]=v.getString("ref_en");
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
