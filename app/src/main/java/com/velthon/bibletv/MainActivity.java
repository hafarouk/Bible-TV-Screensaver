package com.velthon.bibletv;
import android.app.Activity;
import android.os.Bundle;
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
import android.widget.ImageView;
import android.widget.HorizontalScrollView;

/** A D-pad accessible two-column TV settings layout. Choices are visible inline, not hidden in a cycling button. */
public class MainActivity extends Activity {
 @Override protected void onPause(){previewMusic.stop();super.onPause();}
 private SharedPreferences prefs;
 private LinearLayout panel;
 private final AmbientMusic previewMusic=new AmbientMusic();
 private final int BG=0xff101b2b, GOLD=0xffffc828, WHITE=0xfff8f8f8, SUB=0xffadbac9;

 @Override public void onCreate(Bundle b) {
  super.onCreate(b);prefs=getSharedPreferences("settings",MODE_PRIVATE);
  ScrollView scroll=new ScrollView(this);scroll.setFillViewport(true);scroll.setBackgroundColor(BG);
  panel=new LinearLayout(this);panel.setPadding(28,22,28,22);panel.setOrientation(LinearLayout.VERTICAL);
  scroll.addView(panel);setContentView(scroll);render();
 }
 private TextView text(String s,int size,int colour){TextView t=new TextView(this);t.setText(s);t.setTextSize(size);t.setTextColor(colour);t.setGravity(Gravity.CENTER_VERTICAL);return t;}
 private GradientDrawable bg(boolean selected,boolean focus){
   GradientDrawable d=new GradientDrawable();d.setColor(selected?0xff725417:focus?0xff244963:0xff203145);
   d.setStroke(focus?3:1,selected||focus?GOLD:0xff536478);d.setCornerRadius(12);return d;
 }
 private void row(String label, String key, String[] choices, String[] values, String def){
   LinearLayout line=new LinearLayout(this);line.setOrientation(LinearLayout.HORIZONTAL);line.setGravity(Gravity.CENTER_VERTICAL);
   LinearLayout.LayoutParams lp=new LinearLayout.LayoutParams(-1,68);lp.setMargins(0,2,0,2);panel.addView(line,lp);
   TextView t=text(label,16,WHITE);t.setTypeface(null,Typeface.BOLD);t.setSingleLine(true);
   line.addView(t,new LinearLayout.LayoutParams(190,-1));
   HorizontalScrollView strip=new HorizontalScrollView(this);strip.setHorizontalScrollBarEnabled(false);strip.setFillViewport(false);
   line.addView(strip,new LinearLayout.LayoutParams(0,-1,1));
   LinearLayout controls=new LinearLayout(this);controls.setOrientation(LinearLayout.HORIZONTAL);strip.addView(controls);
   String current=prefs.getString(key,def);
   for(int i=0;i<choices.length;i++){
     final String choice=values[i];final boolean selected=choice.equals(current);
     Button bt=new Button(this);bt.setText(choices[i]);bt.setTextSize(14);bt.setAllCaps(false);
     bt.setSingleLine(true);bt.setTextColor(WHITE);bt.setPadding(8,0,8,0);bt.setBackground(bg(selected,false));
     LinearLayout.LayoutParams bp=new LinearLayout.LayoutParams(-2,54);bp.setMargins(3,3,3,3);controls.addView(bt,bp);
     bt.setOnFocusChangeListener((v,focused)->{
       bt.setBackground(bg(choice.equals(prefs.getString(key,def)),focused));
       if(focused)strip.post(()->strip.smoothScrollTo(Math.max(0,bt.getLeft()-20),0));
     });
     bt.setOnClickListener(v->{prefs.edit().putString(key,choice).apply();render();});
   }
 }
 private void action(String label,Runnable r){
   TextView control=text(label,20,WHITE);
   control.setGravity(Gravity.CENTER);
   control.setTypeface(Typeface.DEFAULT,Typeface.BOLD);
   control.setSingleLine(false);
   control.setIncludeFontPadding(true);
   control.setPadding(20,12,20,12);
   control.setFocusable(true);
   control.setFocusableInTouchMode(false);
   control.setClickable(true);
   control.setBackground(bg(false,false));
   LinearLayout.LayoutParams p=new LinearLayout.LayoutParams(-1,-2);
   p.setMargins(0,10,0,0);
   panel.addView(control,p);
   control.setMinHeight(76);
   control.setOnClickListener(v->r.run());
   control.setOnFocusChangeListener((v,focused)->control.setBackground(bg(false,focused)));
 }
 private void render(){
   panel.removeAllViews();
   LinearLayout titleRow=new LinearLayout(this);titleRow.setGravity(Gravity.CENTER_VERTICAL);titleRow.setOrientation(LinearLayout.HORIZONTAL);
   TextView originalLogo=text("✝",30,GOLD);originalLogo.setGravity(Gravity.CENTER);
   LinearLayout.LayoutParams originalParams=new LinearLayout.LayoutParams(43,44);originalParams.rightMargin=14;
   titleRow.addView(originalLogo,originalParams);
   TextView title=text("BIBLE TV  •  Settings",23,GOLD);title.setTypeface(null,Typeface.BOLD);titleRow.addView(title);
   panel.addView(titleRow);
   TextView sub=text("Use remote arrows to select an option",14,SUB);sub.setPadding(0,0,0,8);panel.addView(sub);
   row("Language / اللغة","lang",new String[]{"العربية","English","Both"},new String[]{"ar","en","both"},"ar");
   row("Verse time","interval_choice",new String[]{"15 sec","30 sec","60 sec","120 sec"},new String[]{"15","30","60","120"},"15");
   row("Transition","transition_choice",new String[]{"Page turn","Soft fade","None"},new String[]{"page","fade","none"},"page");
   row("Music","music_track",new String[]{"Off","Piano","Strings","Ambient"},new String[]{"off","piano","strings","ambient"},"piano");
   row("Volume","music_volume_choice",new String[]{"15%","25%","35%","50%"},new String[]{"15","25","35","50"},"25");
   action("Test music",()->{
      String choice=prefs.getString("music_track","piano");
      if("off".equals(choice)){previewMusic.stop();new android.app.AlertDialog.Builder(this).setMessage("Select a music option first.").setPositiveButton("OK",null).show();}
      else {previewMusic.start(this,Integer.parseInt(prefs.getString("music_volume_choice","25")),choice);android.widget.Toast.makeText(this,"Now playing "+choice+" • adjust TV volume to hear",android.widget.Toast.LENGTH_LONG).show();}
   });
   action("Preview screensaver",()->startActivity(new Intent(this,PreviewActivity.class)));
   action("TV screensaver settings",this::openTvSettings);
   TextView help=text("If TV settings do not open, select Screen saver from your TV settings.",13,SUB);
   help.setPadding(6,10,0,4);panel.addView(help);
   
   prefs.edit()
     .putInt("interval",Integer.parseInt(prefs.getString("interval_choice","15")))
     .putInt("style",Integer.parseInt(prefs.getString("style_choice","0")))
     .putBoolean("motion",Boolean.parseBoolean(prefs.getString("motion_choice","true")))
     .putInt("motion_speed",Integer.parseInt(prefs.getString("motion_speed_choice","0")))
     .putBoolean("music",!"off".equals(prefs.getString("music_track","off")) )
     .putInt("music_volume",Integer.parseInt(prefs.getString("music_volume_choice","25")))
     .apply();
 }
 private void openTvSettings(){
   try {Intent i=new Intent("android.settings.DREAM_SETTINGS");i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);startActivity(i);return;}catch(Exception ignored){}
   new android.app.AlertDialog.Builder(this).setTitle("TV screensaver settings unavailable")
    .setMessage("Your TV does not expose the Android screen saver selection page to apps. Please check your TV's own Settings → Device Preferences → Screen saver. Manufacturer restrictions cannot be bypassed through this button.")
    .setPositiveButton("OK",null).show();
 }
}
