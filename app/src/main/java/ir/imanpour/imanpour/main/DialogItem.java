package ir.imanpour.imanpour.main;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.Html;
import android.text.Spanned;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.arash.imanpour.R;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import ir.imanpour.imanpour.helper.Downloader;
import ir.imanpour.imanpour.rss.RssParser;

import static ir.imanpour.imanpour.main.G.sqLiteDatabase;

public class DialogItem extends Dialog {
  private final RssParser.Item item;
  private final CheckBox mCheckBox;
  private final Context mContext;

  public DialogItem(@NonNull Context context, RssParser.Item item, CheckBox checkBox) {
    super(context);
    mContext = context;
    this.item = item;
    mCheckBox = checkBox;
  }


  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.dialog);
    String[] s = item.enclosure.split("/");
    String file = G.APP_DIR + "/" + s[s.length - 1];
    TextView txt_dialog_date = (TextView) findViewById(R.id.txt_dialog_date);
    TextView txt_dilog_description = (TextView) findViewById(R.id.txt_dilog_description);
    TextView txt_dialog_creator = (TextView) findViewById(R.id.txt_dialog_creator);
    TextView txt_dialog_link = (TextView) findViewById(R.id.txt_dialog_link);
    TextView txt_dialog_link1 = (TextView) findViewById(R.id.txt_dialog_link1);
    TextView txt_dialog_home = (TextView) findViewById(R.id.txt_dialog_home);
    txt_dialog_home.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        DialogItem.this.cancel();
      }
    });
    ImageView img_dialog = (ImageView) findViewById(R.id.img_dialog);
    final CheckBox chkb_dialog_like = (CheckBox) findViewById(R.id.chkb_dialog_like);
    txt_dialog_creator.setText(item.creator);
    String[] strings = item.pubDate.split(" ");
    try {
      Date date = new SimpleDateFormat("MMM", Locale.ENGLISH).parse(strings[2]);
      Calendar calendar = Calendar.getInstance();
      calendar.setTime(date);
      int month = calendar.get(Calendar.MONTH) + 1;
      strings[2] = "" + month;
    } catch (ParseException e) {
      e.printStackTrace();
    }
    txt_dialog_date.setText(strings[3] + "/" + strings[2] + "/" + strings[1]);
    txt_dilog_description.setText(fromHtml(item.description));
    try {
      if (new File(file).exists()) {
        Bitmap bitmap = BitmapFactory.decodeFile(file);
        img_dialog.setImageBitmap(bitmap);
      } else {
        img_dialog.setImageResource(0);
        Downloader.download(item.enclosure, img_dialog);
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
    @SuppressLint("Recycle") final Cursor cursor = sqLiteDatabase.rawQuery("select * from Rss where  TRIM(link) = '" + item.link + "'", null);
    cursor.moveToFirst();
    int i = cursor.getInt(cursor.getColumnIndex("like"));
    cursor.close();
    item.like = i;
    chkb_dialog_like.setChecked((item.like == 1));
    chkb_dialog_like.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        if (chkb_dialog_like.isChecked()) {
          chkb_dialog_like.setChecked(true);
          sqLiteDatabase.execSQL("UPDATE Rss SET like=1 where TRIM(link) = '" + item.link + "'");
        } else {
          chkb_dialog_like.setChecked(false);
          sqLiteDatabase.execSQL("UPDATE Rss SET like=0 where TRIM(link) = '" + item.link + "'");
        }
        Cursor num = sqLiteDatabase.rawQuery("select * from Rss where like=1", null);
        int n = num.getCount();
        cursor.close();
        if (G.likeLitenner != null) {
          G.likeLitenner.likeListenner(n);
        }
        mCheckBox.setChecked(chkb_dialog_like.isChecked());
      }
    });
    txt_dialog_link.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        Intent i = new Intent(Intent.ACTION_VIEW);
        i.setData(Uri.parse(item.link));
        mContext.startActivity(i);
      }
    });
    txt_dialog_link1.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        Intent i = new Intent(Intent.ACTION_VIEW);
        i.setData(Uri.parse(item.link));
        mContext.startActivity(i);
      }
    });
  }

  @SuppressWarnings("deprecation")
  public static Spanned fromHtml(String html) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
      return Html.fromHtml(html, Html.FROM_HTML_SEPARATOR_LINE_BREAK_PARAGRAPH);
    } else {
      return Html.fromHtml(html);
    }
  }

  @Override
  protected void onStart() {
    super.onStart();
  }
}
