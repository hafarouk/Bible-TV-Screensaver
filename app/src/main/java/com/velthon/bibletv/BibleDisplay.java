package com.velthon.bibletv;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.os.Handler;
import android.os.Looper;
import android.view.Gravity;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.view.View;
import android.animation.ObjectAnimator;
import android.animation.Animator;
import android.animation.AnimatorSet;
import java.util.Random;
public class BibleDisplay extends FrameLayout {
    private final SharedPreferences prefs;
    private final Handler handler=new Handler(Looper.getMainLooper());
    private TextView verse,ref,emblem;
    private View scene;
    private ObjectAnimator drift;
    private int current=-1;
    private boolean running;
    // Carefully selected familiar verses; bilingual mode independently displays the matching translation.
    private static final String[][] VERSES={
      {"الرَّبُّ رَاعِيَّ فَلاَ يُعْوِزُنِي شَيْءٌ.","The LORD is my shepherd; I shall not want.","مزمور ٢٣:١ | Psalm 23:1"},
      {"كُلَّ شَيْءٍ أَسْتَطِيعُ فِي الْمَسِيحِ الَّذِي يُقَوِّينِي.","I can do all things through Christ which strengtheneth me.","فيلبي ٤:١٣ | Philippians 4:13"},
      {"لاَ يَضْطَرِبْ قَلْبُكُمْ. أَنْتُمْ تُؤْمِنُونَ بِاللهِ فَآمِنُوا بِي.","Let not your heart be troubled: ye believe in God, believe also in me.","يوحنا ١٤:١ | John 14:1"},
      {"لاَ تَخَفْ لأَنِّي مَعَكَ.","Fear thou not; for I am with thee.","إشعياء ٤١:١٠ | Isaiah 41:10"},
      {"أَنَا هُوَ نُورُ الْعَالَمِ.","I am the light of the world.","يوحنا ٨:١٢ | John 8:12"},
      {"تَعَالَوْا إِلَيَّ يَا جَمِيعَ الْمُتْعَبِينَ وَالثَّقِيلِي الأَحْمَالِ وَأَنَا أُرِيحُكُمْ.","Come unto me, all ye that labour and are heavy laden, and I will give you rest.","متى ١١:٢٨ | Matthew 11:28"},
      {"لأَنَّهُ هكَذَا أَحَبَّ اللهُ الْعَالَمَ حَتَّى بَذَلَ ابْنَهُ الْوَحِيدَ.","For God so loved the world, that he gave his only begotten Son.","يوحنا ٣:١٦ | John 3:16"},
      {"بِالسُّكُونِ وَالطُّمَأْنِينَةِ تَكُونُ قُوَّتُكُمْ.","In quietness and in confidence shall be your strength.","إشعياء ٣٠:١٥ | Isaiah 30:15"}
    };
    public BibleDisplay(Context c){super(c); prefs=c.getSharedPreferences("settings",Context.MODE_PRIVATE);
       setBackgroundColor(Color.BLACK);
       scene=new View(c);addView(scene,new FrameLayout.LayoutParams(-1,-1));
       View shade=new View(c);shade.setBackground(new GradientDrawable(GradientDrawable.Orientation.TOP_BOTTOM,new int[]{0x55000000,0x66000000,0xbb000000}));addView(shade,new FrameLayout.LayoutParams(-1,-1));
       LinearLayout overlay=new LinearLayout(c);overlay.setOrientation(LinearLayout.VERTICAL);overlay.setGravity(Gravity.CENTER);overlay.setPadding(60,25,60,25);
       addView(overlay,new FrameLayout.LayoutParams(-1,-1));
       emblem=new TextView(c);emblem.setTextSize(36);emblem.setTextColor(0xfff7dca5);emblem.setGravity(Gravity.CENTER);overlay.addView(emblem);
       verse=new TextView(c);verse.setGravity(Gravity.CENTER);verse.setTextDirection(View.TEXT_DIRECTION_FIRST_STRONG);verse.setTextColor(Color.WHITE);verse.setTextSize(36);verse.setTypeface(Typeface.create("sans-serif",Typeface.BOLD));verse.setShadowLayer(5,2,3,Color.BLACK);verse.setPadding(50,30,50,30);
       overlay.addView(verse);
       ref=new TextView(c);ref.setGravity(Gravity.CENTER);ref.setTextColor(0xfff8d89d);ref.setTextSize(24);ref.setShadowLayer(5,2,2,Color.BLACK);overlay.addView(ref);
    }
    public void start(){running=true;next();}
    public void stop(){running=false;handler.removeCallbacksAndMessages(null);if(drift!=null)drift.cancel();}
    public void next(){handler.removeCallbacksAndMessages(null);if(!running)return;
       current=(current+1)%VERSES.length;
       String lang=prefs.getString("lang","ar");String[] v=VERSES[current];
       verse.setText(lang.equals("en")?v[1]:lang.equals("both")?v[0]+"\n\n"+v[1]:v[0]);ref.setText(v[2]);
       int style=prefs.getInt("style",0),theme=style==1?2:style==2?current%3:current%2;
       int[][] gradients={{0xff0b2741,0xff31556a,0xffb97942},{0xff351f32,0xff8b533d,0xffdbab70},{0xff0e253b,0xff285e72,0xff85b3b1}};
       scene.setBackground(new GradientDrawable(GradientDrawable.Orientation.TL_BR,gradients[theme]));
       emblem.setText(theme==2?"☀  ✨  ☀":current%2==0?"✝   ♡   ✝":"✝   ✨   ✝");
       if(drift!=null)drift.cancel();scene.setScaleX(1.04f);scene.setScaleY(1.04f);
       if(prefs.getBoolean("motion",true)){
           drift=ObjectAnimator.ofFloat(scene,"scaleX",1.04f,1.18f);drift.setDuration(22000);drift.setRepeatCount(ObjectAnimator.INFINITE);drift.setRepeatMode(ObjectAnimator.REVERSE);drift.start();
       }
       handler.postDelayed(()->next(),prefs.getInt("interval",30)*1000L);
    }
}
