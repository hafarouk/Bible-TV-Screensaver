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
 private ImageView picture;private View fallback;private TextView verse,arabicVerse,reference,caption;
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
 TextView separator=new TextView(c);separator.setText("━━━━  ✝  ━━━━");separator.setGravity(Gravity.CENTER);separator.setTextColor(0xffffce70);separator.setTextSize(19);separator.setPadding(0,8,0,8);card.addView(separator);
 arabicVerse=new TextView(c);arabicVerse.setGravity(Gravity.CENTER);arabicVerse.setTextDirection(View.TEXT_DIRECTION_RTL);arabicVerse.setTextColor(0xff153b65);arabicVerse.setTextSize(33);arabicVerse.setTypeface(Typeface.DEFAULT,Typeface.BOLD);arabicVerse.setShadowLayer(0,0,0,Color.TRANSPARENT);card.addView(arabicVerse);
 LinearLayout footer=new LinearLayout(c);footer.setGravity(Gravity.CENTER);footer.setOrientation(LinearLayout.HORIZONTAL);

 caption=new TextView(c);caption.setText("VELTHON");caption.setTextColor(0xff173b65);caption.setTextSize(13);caption.setShadowLayer(4,1,2,Color.BLACK);caption.setGravity(Gravity.RIGHT|Gravity.CENTER_VERTICAL);footer.addView(caption,new LinearLayout.LayoutParams(-1,-1));
 LayoutParams cp=new LayoutParams(-1,52,Gravity.BOTTOM);cp.bottomMargin=12;cp.leftMargin=250;cp.rightMargin=30;addView(footer,cp);

