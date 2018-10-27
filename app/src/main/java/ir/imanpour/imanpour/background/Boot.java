package ir.imanpour.imanpour.background;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;

import ir.imanpour.imanpour.main.G;


public class Boot extends BroadcastReceiver {

  @Override
  public void onReceive(Context context, Intent intent) {
    //Tools.Toast("Boot");
    String action=intent.getAction();
    if(action.equals(Intent.ACTION_BOOT_COMPLETED)|action.equals(Intent.ACTION_LOCKED_BOOT_COMPLETED)) {
      //Tools.log("Boot");
      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        if (PackageManager.PERMISSION_GRANTED == context.checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
          //Tools.log("Boot_yes_23");
          G.runAlarm();
        }
      } else {
        //Tools.log("Boot_yes");
        G.runAlarm();
      }
    }
  }
}
