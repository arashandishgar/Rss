package ir.imanpour.imanpour.rss;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.media.RingtoneManager;
import android.os.AsyncTask;
import android.support.v4.app.NotificationCompat;
import android.widget.RemoteViews;

import com.example.arash.imanpour.R;

import java.util.ArrayList;

import ir.imanpour.imanpour.helper.DataBaseHelper;
import ir.imanpour.imanpour.main.ActivityMain;
import ir.imanpour.imanpour.main.G;

import static ir.imanpour.imanpour.main.G.sqLiteDatabase;


public class RssHandler extends AsyncTask<Object, Object, RssParser> {

  public RssHandler() {
  }

  @Override
  protected void onPreExecute() {
    super.onPreExecute();
  }

  @Override
  protected RssParser doInBackground(Object... voids) {
    return new RssParser("https://www.imanpour.ir/blog/feed/");
  }

  @Override
  protected void onPostExecute(RssParser rssParser) {
    if (G.unread != null) {
      Cursor cursor_unread = sqLiteDatabase.rawQuery("select * from Rss where unread =0", null);
      G.unread.read(cursor_unread.getCount());
      cursor_unread.close();
    }
    if (G.onDataChangeListenner != null) {
      Cursor cursor_home = sqLiteDatabase.rawQuery("select * from Rss", null);
      G.onDataChangeListenner.numberOfItemChangeListenner(cursor_home.getCount());
      cursor_home.close();
    }
    if (G.swipeListenner != null) {
      G.swipeListenner.onFinsh();
    }
    G.feedAapter.setNewlist();
    G.inNotProgress=true;
    G.needRefresh = false;
    SharedPreferences.Editor editor = G.sharedPreferences.edit();
    editor.putBoolean(G.refresh, G.needRefresh);
    editor.apply();
    super.onPostExecute(rssParser);
    NotificationManager notificationManager =
      (NotificationManager) G.context.getSystemService(Context.NOTIFICATION_SERVICE);
    Notification notification = null;
    int notifyID = 1;
    String CHANNEL_ID = "my_channel_01";// The id of the channel.
    CharSequence name = "imanpour";// The user-visible name of the channel.
    Cursor cursor1 = sqLiteDatabase.rawQuery("select * from Rss  where unread=0", null);
    final int count = cursor1.getCount();
    if (count >= 0) {
      final ArrayList<RssParser.Item> items = DataBaseHelper.convertCoursorToArray(cursor1);
      if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
        NotificationChannel mChannel = new NotificationChannel(CHANNEL_ID, name, NotificationManager.IMPORTANCE_DEFAULT);
        notificationManager.createNotificationChannel(mChannel);
      }
      final RemoteViews remoteView = new RemoteViews(G.context.getPackageName(), R.layout.notification);
      remoteView.setTextViewText(R.id.txt_description, " شما " + items.size() + " پیام" + " خوانده نشده دارید  ");
      remoteView.setImageViewResource(R.id.img_notification, R.drawable.notification);
      remoteView.setTextViewText(R.id.txt_counter, "" + items.size());
      PendingIntent pendingIntent = PendingIntent.getActivity(G.context, 100, new Intent(G.context, ActivityMain.class), PendingIntent.FLAG_UPDATE_CURRENT);
      NotificationCompat.Builder builder = new NotificationCompat.Builder(G.context, CHANNEL_ID)
        .setSmallIcon(R.drawable.arash)
        .setChannelId(CHANNEL_ID)
        .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
        .setContentIntent(pendingIntent)
        .setCustomContentView(remoteView)
        .setOngoing(true);
    notification=builder.build();
    }
      notification.flags = Notification.FLAG_NO_CLEAR;
      if (count == 0) {
        notificationManager.cancel(notifyID);
        return;
      } else if (count > 0) {
        notificationManager.cancel(notifyID);
        notificationManager.notify(notifyID, notification);
      }
    cursor1.close();
  }
}
