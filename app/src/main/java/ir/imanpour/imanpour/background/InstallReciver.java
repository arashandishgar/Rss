package ir.imanpour.imanpour.background;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;

import ir.imanpour.imanpour.main.G;

public class InstallReciver extends BroadcastReceiver {
  @Override
  public void onReceive(Context context, Intent intent) {
    //Tools.log("yes it is");
    G.needRefresh = true;
    SharedPreferences.Editor editor = G.sharedPreferences.edit();
    editor.putBoolean(G.refresh, G.needRefresh);
    editor.commit();
    if (ActivityCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE)== PackageManager.PERMISSION_GRANTED) {
        MyService.enqueueWork(context, new Intent(context, MyService.class));
    }
  }
}
