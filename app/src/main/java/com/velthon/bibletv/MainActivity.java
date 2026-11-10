package com.velthon.bibletv;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.graphics.Color;
import android.view.Gravity;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Button;
import android.content.SharedPreferences;
public class MainActivity extends Activity {
    private LinearLayout panel;
    private SharedPreferences prefs;
    private TextView status;
    private TextView text(String s,int size){TextView t=new TextView(this);t.setText(s);t.setTextSize(size);t.setTextColor(Color.WHITE);t.setGravity(Gravity.CENTER);t.setPadding(12,8,12,8);return t;}
    private void action(String label,Runnable r){
       Button b=new Button(this);b.setText(label);b.setAllCaps(false);b.setOnClickListener(v->r.run());
       panel.addView(b,new LinearLayout.LayoutParams(540,53));
    }
    @Override public void onCreate(Bundle b){super.onCreate(b);prefs=getSharedPreferences("settings",MODE_PRIVATE);
       panel=new LinearLayout(this);panel.setOrientation(LinearLayout.VERTICAL);panel.setGravity(Gravity.CENTER);panel.setBackgroundColor(0xff13243b);
       setContentView(panel);
       panel.addView(text("✝  Bible TV Screensaver | آيات الكتاب المقدس",27));
       status=text("",17);panel.addView(status);
       action("Language / اللغة: Arabic → English → Both",()->{String s=prefs.getString("lang","ar");prefs.edit().putString("lang",s.equals("ar")?"en":s.equals("en")?"both":"ar").apply();update();});
       action("Verse time / مدة الآية: 15 / 30 / 60 / 120 seconds",()->{int x=prefs.getInt("interval",30);prefs.edit().putInt("interval",x==15?30:x==30?60:x==60?120:15).apply();update();});
       action("Visual style / الخلفية: Biblical → Nature → Mixed",()->{int x=prefs.getInt("style",0);prefs.edit().putInt("style",(x+1)%3).apply();update();});
       action("Animated / الحركة: On or Off",()->{prefs.edit().putBoolean("motion",!prefs.getBoolean("motion",true)).apply();update();});
       action("▶  Preview / معاينة",()->startActivity(new Intent(this,PreviewActivity.class)));
       action("TV screensaver start delay / بدء شاشة التوقف",()->{try{startActivity(new Intent(Settings.ACTION_DREAM_SETTINGS));}catch(Exception e){status.setText("Use your TV's screensaver settings / إعدادات التلفزيون");}});
       update();
    }
    private void update(){status.setText("Language: "+prefs.getString("lang","ar")+"  |  Verse: "+prefs.getInt("interval",30)+" sec  |  Theme: "+prefs.getInt("style",0)+"  |  Motion: "+(prefs.getBoolean("motion",true)?"On":"Off"));}
}
