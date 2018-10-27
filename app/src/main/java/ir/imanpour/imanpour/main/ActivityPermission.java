package ir.imanpour.imanpour.main;

import android.Manifest;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;

import com.example.arash.imanpour.R;

import static ir.imanpour.imanpour.main.G.alarmCan;


public class ActivityPermission extends Activity {

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    setTheme(G.sharedPreferences.getInt(G.themeP,G.theme));
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_permission);
    if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED) {
      ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 100);
    }else {
      if (alarmCan) {
        G.runAlarm();
        SharedPreferences.Editor editor = G.sharedPreferences.edit();
        editor.putBoolean(G.alarmCanRun, false);
        editor.apply();
      }
      G.runSql();
      finish();
      startActivity(new Intent(this,ActivityMain.class));

    }
  }

  @Override
  protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED) {
      ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 100);
    }else {
      if (alarmCan) {
        G.runAlarm();
        SharedPreferences.Editor editor = G.sharedPreferences.edit();
        editor.putBoolean(G.alarmCanRun, false);
        editor.apply();
      }
      G.runSql();
      finish();
      startActivity(new Intent(this,ActivityMain.class));
    }
    super.onActivityResult(requestCode, resultCode, data);
  }

  @Override
  public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
    if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
      //Tools.log("work true :"+alarmCan);
      if (alarmCan) {
        G.runAlarm();
        SharedPreferences.Editor editor = G.sharedPreferences.edit();
        editor.putBoolean(G.alarmCanRun, false);
        editor.apply();
      }
      G.runSql();
      finish();
      startActivity(new Intent(this,ActivityMain.class));
    } else {
      new AlertDialog.Builder(ActivityPermission.this)
        .setTitle("نمی توانیم برنامه را اجرا کنیم")
        .setMessage("ما برای اجرای هرچه بهتر برنامه نیاز به دسترسی حافطه حارجی داریم خواهشا بارفتن به تنظیمات در قسمت مجوز ها مجوز استفاده از حافظه به برنامه را بدهید")
        .setPositiveButton("باز کردن تنظیمات", new DialogInterface.OnClickListener() {
          @Override
          public void onClick(DialogInterface dialog, int which) {
            Intent intent = new Intent();
            intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
            Uri uri = Uri.fromParts("package",getPackageName(), null);
            intent.setData(uri);
            startActivityForResult(intent,50);
          }
        })
        .setCancelable(false)
        .create()
        .show();
    }
    super.onRequestPermissionsResult(requestCode, permissions, grantResults);
  }
}
