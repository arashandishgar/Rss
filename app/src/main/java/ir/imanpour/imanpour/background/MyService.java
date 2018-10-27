package ir.imanpour.imanpour.background;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.annotation.NonNull;
import android.support.v4.app.JobIntentService;

import com.treebo.internetavailabilitychecker.InternetAvailabilityChecker;
import com.treebo.internetavailabilitychecker.InternetConnectivityListener;

import ir.imanpour.imanpour.main.G;
import ir.imanpour.imanpour.rss.RssHandler;

public class MyService extends JobIntentService implements InternetConnectivityListener {
  private static Boolean mustRun = true;
  private boolean isConnected;
  InternetAvailabilityChecker internetReciver;
  public static final int JOB_ID = 0x01;

  public static void enqueueWork(Context context, Intent work) {
    if(G.inNotProgress) {
      enqueueWork(context, MyService.class, JOB_ID, work);
    }
  }


  @Override
  protected void onHandleWork(@NonNull Intent intent) {
    G.inNotProgress=false;
    internetReciver = InternetAvailabilityChecker.init(getApplicationContext());
    internetReciver.addInternetConnectivityListener(this);
    sync(isNetworkOnline());

  }


  public boolean isNetworkOnline() {
    boolean status = false;
    try {
      ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
      NetworkInfo netInfo = cm.getNetworkInfo(0);
      if (netInfo != null && netInfo.getState() == NetworkInfo.State.CONNECTED) {
        status = true;
      } else {
        netInfo = cm.getNetworkInfo(1);
        if (netInfo != null && netInfo.getState() == NetworkInfo.State.CONNECTED)
          status = true;
      }
    } catch (Exception e) {
      e.printStackTrace();
      return false;
    }
    return status;

  }

  @Override
  public void onInternetConnectivityChanged(boolean isConnected) {
    this.isConnected = isConnected;
    if (!isConnected) {
      this.isConnected = isNetworkOnline();
    }
    //Tools.log("TEST");
    sync(this.isConnected);
  }

  private void sync(boolean isConnected) {
    //Tools.log(isConnected);
    if (G.needRefresh) {
      if (isConnected) {
        //Tools.log("worlk");
        new RssHandler().execute();
      } else {
        //Tools.log("not work");
        if (G.onRefereshFalse != null) {
          G.handler.post(new Runnable() {
            @Override
            public void run() {
              G.onRefereshFalse.run();
            }
          });
        }
      }
    }
  }
}
