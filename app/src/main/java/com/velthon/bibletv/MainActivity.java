package com.velthon.bibletv;
import android.app.Activity;
import android.os.Bundle;
import android.os.Build;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.provider.Settings;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
public class MainActivity extends Activity {
 private SharedPreferences prefs;
 private LinearLayout panel;
 private TextView summary;
 private int gold=0xffffd16b;
 private TextView text(String s,int sz,int color){TextView t=new TextView(this);t.setText(s);t.setTextSize(sz);t.setTextColor(color);t.setGravity(Gravity.CENTER);t.setPadding(12,8,12,8);return t;}
 private void action(String s,Runnable run){Button b=new Button(this);b.setText(s);b.setTextSize(17);b.setAllCaps(false);b.setTextColor(Color.WHITE);GradientDrawable bg=new GradientDrawable();bg.setColor(0xff263d53);bg.setCornerRadius(18);bg.setStroke(2,0xffcba74f);b.setBackground(bg);LinearLayout.LayoutParams p=new LinearLayout.LayoutParams(-1,56);p.setMargins(0,5,0,5);panel.addView(b,p);b.setOnClickListener(v->run.run());}
 @Override public void onCreate(Bundle b){super.onCreate(b);prefs=getSharedPreferences("settings",MODE_PRIVATE);
 ScrollView scroll=new ScrollView(this);scroll.setFillViewport(true);scroll.setBackgroundColor(0xff0d1a29);
 LinearLayout outer=new LinearLayout(this);outer.setGravity(Gravity.CENTER);outer.setPadding(18,16,18,22);scroll.addView(outer);
 panel=new LinearLayout(this);panel.setOrientation(LinearLayout.VERTICAL);panel.setGravity(Gravity.CENTER);panel.setPadding(20,10,20,12);outer.addView(panel,new LinearLayout.LayoutParams(720,-2));
 TextView title=text("✝  BIBLE TV  |  الكتاب المقدس",31,gold);title.setTypeface(null,Typeface.BOLD);panel.addView(title);
 TextView sub=text("Beautiful Bible verses • Living biblical artwork",17,0xffc5d2dc);panel.addView(sub);
 summary=text("",15,Color.WHITE);panel.addView(summary);
 action("🌐  Bible language / لغة الكتاب: عربي · English · Both",()->{String x=prefs.getString("lang","ar");prefs.edit().putString("lang",x.equals("ar")?"en":x.equals("en")?"both":"ar").apply();refresh();});
 action("⏱  Change verse: 15 · 30 · 60 · 120 seconds",()->{int n=prefs.getInt("interval",30);prefs.edit().putInt("interval",n==15?30:n==30?60:n==60?120:15).apply();refresh();});
 action("🖼  Scenes: Jesus & Bible · Nature · Mixed",()->{int n=prefs.getInt("style",0);prefs.edit().putInt("style",(n+1)%3).apply();refresh();});
 action("✨  Gentle moving backgrounds: ON / OFF",()->{prefs.edit().putBoolean("motion",!prefs.getBoolean("motion",true)).apply();refresh();});
 action("🎞  Animation speed: Slow · Medium · Faster",()->{int v=prefs.getInt("motion_speed",0);prefs.edit().putInt("motion_speed",(v+1)%3).apply();refresh();});
 action("♫  Soft background music: OFF / ON",()->{prefs.edit().putBoolean("music",!prefs.getBoolean("music",false)).apply();refresh();});
 action("♫  Music volume: 10 · 25 · 40 · 60%",()->{int v=prefs.getInt("music_volume",25);prefs.edit().putInt("music_volume",v==10?25:v==25?40:v==40?60:10).apply();refresh();});
 action("▶  PREVIEW SCREENSAVER NOW",()->startActivity(new Intent(this,PreviewActivity.class)));
 action("⚙  SELECT AS TV SCREENSAVER",()->openTvSettings());
 TextView note=text("For Android TV, choose Bible TV in your TV's Screen saver / Ambient settings if supported. Google TV and Fire OS may restrict external screensavers.",14,0xffa9bac8);panel.addView(note);
 panel.addView(text("© Velthon Design & Development",15,gold));setContentView(scroll);refresh();
 }
 private void refresh(){String l=prefs.getString("lang","ar");String ls=l.equals("ar")?"العربية":l.equals("en")?"English":"Arabic + English";String st=new String[]{"Biblical scenes","Nature","Mixed"}[prefs.getInt("style",0)];summary.setText("LANGUAGE  "+ls+"    •    VERSE  "+prefs.getInt("interval",30)+" sec\nIMAGERY  "+st+"    •    ANIMATION  "+(prefs.getBoolean("motion",true)?"ON":"OFF")+"    •    MUSIC "+(prefs.getBoolean("music",false)?"ON "+prefs.getInt("music_volume",25)+"%":"OFF"));}
 private void openTvSettings(){String[] actions={ "android.settings.DREAM_SETTINGS",Settings.ACTION_DISPLAY_SETTINGS,Settings.ACTION_SETTINGS};for(String a:actions){try{Intent i=new Intent(a);if(i.resolveActivity(getPackageManager())!=null){startActivity(i);return;}}catch(Exception ignored){}}
 new android.app.AlertDialog.Builder(this).setTitle("TV screensaver settings").setMessage("This TV did not expose a screen saver settings page. Open TV Settings → Device Preferences → Screen saver / Ambient Mode. Some Google TV and Fire TV models block third-party screensavers even when the app is installed.").setPositiveButton("OK",null).show();
 }
}
