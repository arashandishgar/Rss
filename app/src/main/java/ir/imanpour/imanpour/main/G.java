package ir.imanpour.imanpour.main;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.Application;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v4.app.ActivityCompat;

import com.example.arash.imanpour.R;

import java.io.File;

import ir.imanpour.imanpour.adpter.FeedAapter;
import ir.imanpour.imanpour.background.InstallReciver;
import ir.imanpour.imanpour.background.MyService;
import ir.imanpour.imanpour.helper.DataBaseHelper;
import ir.imanpour.imanpour.listenner.LikeLitenner;
import ir.imanpour.imanpour.listenner.OnNumberOfItemChangeListenner;
import ir.imanpour.imanpour.listenner.OnRefereshFalse;
import ir.imanpour.imanpour.listenner.OnShareListenner;
import ir.imanpour.imanpour.listenner.SwipeListenner;
import ir.imanpour.imanpour.listenner.UnReadListenner;


public class G extends Application {
  public static final int DOWNLOAD_BUFFER_SIZE = 8 * 1024;
  public static AlarmManager alarmManager;
  public static String test = "TEST";
  public static String action = "arash";
  //you must wite from shared prefrence
  public static boolean needRefresh = true;
  //make true
  public static boolean inNotProgress = true;
  @SuppressLint("StaticFieldLeak")
  public static FeedAapter feedAapter;
  @SuppressLint("StaticFieldLeak")
  public static Context context;
  public static boolean runFirst;

  public static String APP_DIR = Environment.getExternalStorageDirectory().getAbsolutePath() + "/imanpour";
  public static String DATABASE_DIR = APP_DIR + "/rss.sqlite";
  public static DataBaseHelper sqLiteOpenHelper;
  public static SQLiteDatabase sqLiteDatabase;
  public static String download = "DOWNLOAD";
  public static LikeLitenner likeLitenner = null;
  public static UnReadListenner unread = null;
  public static OnNumberOfItemChangeListenner onDataChangeListenner = null;
  public static SwipeListenner swipeListenner = null;
  public static OnRefereshFalse onRefereshFalse = null;
  public static OnShareListenner onShareListenner = null;
  public static int theme = R.style.AppThemeBase_NoActionBar;
  public static boolean canShare = false;
  public static boolean alarmCan = true;
  public static Handler handler = new Handler();
  public static boolean canRunnotification;
  public static SharedPreferences sharedPreferences = null;
  //perference
  public static String alarmCanRun = "alarmCanRun";
  //perference
  public static String refresh = "refresh";
  //perference
  public static String themeP = "them";
  public static String first="first";

  @Override
  public void onCreate() {
    super.onCreate();
    context = getApplicationContext();
    sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
    set();
    G.theme = G.sharedPreferences.getInt(G.themeP,R.style.AppThemeBase_NoActionBar);
    needRefresh = sharedPreferences.getBoolean(refresh, true);
    alarmCan = sharedPreferences.getBoolean(alarmCanRun, true);
    runFirst= sharedPreferences.getBoolean(first, true);;
    if (ActivityCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
      if (alarmCan) {

        G.runAlarm();
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(alarmCanRun, false);
        editor.apply();
      }
      G.runSql();
    }
  }

  public static void runAlarm() {
    if (!new File(APP_DIR).exists())
      new File(APP_DIR).mkdirs();
    MyService.enqueueWork(context,new Intent(context, MyService.class));
    alarmManager = (AlarmManager) context.getSystemService(ALARM_SERVICE);
    Intent intent = new Intent(context, InstallReciver.class);
    intent.setAction(action);
    PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, intent, 0);
    //new Test
    if(Build.VERSION.SDK_INT<=Build.VERSION_CODES.JELLY_BEAN_MR2) {
      alarmManager.setRepeating(AlarmManager.RTC, System.currentTimeMillis(), AlarmManager.INTERVAL_HALF_HOUR, pendingIntent);
    }else {
    //Tools.log("Dont push me");
      alarmManager.setInexactRepeating(AlarmManager.RTC,  System.currentTimeMillis(), AlarmManager.INTERVAL_HALF_HOUR, pendingIntent);
    }
  }
  public static void runSql(){
    sqLiteOpenHelper = new DataBaseHelper(G.context, DATABASE_DIR);
    sqLiteDatabase = sqLiteOpenHelper.getWritableDatabase();
    feedAapter = new FeedAapter();
  }

  void set() {
    if (!sharedPreferences.contains(alarmCanRun)) {
      SharedPreferences.Editor editor = sharedPreferences.edit();
      editor.putBoolean(alarmCanRun, true);
      editor.commit();
    }
    if (!sharedPreferences.contains(refresh)) {
      SharedPreferences.Editor editor = sharedPreferences.edit();
      editor.putBoolean(refresh, true);
      editor.commit();
    }
    if (!sharedPreferences.contains(themeP)) {
      SharedPreferences.Editor editor = sharedPreferences.edit();
      editor.putInt(themeP, theme);
      editor.commit();
    }
    if (!sharedPreferences.contains(first)) {
      SharedPreferences.Editor editor = sharedPreferences.edit();
      editor.putBoolean(first, true);
      editor.commit();
    }
  }
}
