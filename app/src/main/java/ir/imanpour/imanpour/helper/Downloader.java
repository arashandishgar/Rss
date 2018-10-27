package ir.imanpour.imanpour.helper;

import android.graphics.BitmapFactory;
import android.widget.ImageView;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import ir.imanpour.imanpour.main.G;


public class Downloader {


  public static void download(final String downloadPath, final ImageView imageView) {
    String[] s = downloadPath.split("/");
    final String filepath = G.APP_DIR + "/" + s[s.length - 1];
    new Thread(new Runnable() {
      @Override
      public void run() {
        try {
          //Tools.log(downloadPath);
          URL url = new URL(downloadPath);
          HttpURLConnection connection = (HttpURLConnection) url.openConnection();
          connection.setRequestMethod("GET");
          connection.setDoOutput(true);
          connection.connect();
          File file = new File(filepath);
          if (file.exists()) {
            file.delete();
          }

          FileOutputStream outputStream = new FileOutputStream(filepath);

          InputStream inputStream = connection.getInputStream();
          byte[] buffer = new byte[G.DOWNLOAD_BUFFER_SIZE];
          int len;
          while ((len = inputStream.read(buffer)) > 0) {
            outputStream.write(buffer, 0, len);
          }
          outputStream.flush();
          outputStream.close();
          try {
            imageView.setImageBitmap(BitmapFactory.decodeFile(filepath));
          } catch (Exception e) {
            e.printStackTrace();
          }
        } catch (MalformedURLException e) {
          e.printStackTrace();
        } catch (IOException e) {
          new File(filepath).delete();
          e.printStackTrace();
        }


      }
    }).start();
  }

}
