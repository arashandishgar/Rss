package ir.imanpour.imanpour;

import android.util.Log;
import android.widget.Toast;

import ir.imanpour.imanpour.main.G;


public class Tools {
  public static void log(Object text){
    Log.i(G.test,""+text);
  }
  public static void log(Object tag,Object text){
    Log.i(""+tag,""+""+text);
  }
  public static void Toast(Object objects){
    Toast.makeText(G.context,""+objects,Toast.LENGTH_SHORT).show();
  }
}
