package ir.imanpour.imanpour.componetofview;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.webkit.WebChromeClient;
import android.webkit.WebView;

public class JustifiedTextView extends WebView {
  private String core = "<html><body dir=\"rtl\"style='text-align:justify;color:rgba(%s);font-size:%dpx;margin: 0px 0px 0px 0px;'>%s</body></html>";
  private String textColor = "0,0,0,255";
  private String text = "";
  private int textSize = 12;
  private int backgroundColor = Color.TRANSPARENT;

  public JustifiedTextView(Context context, AttributeSet attrs) {
    super(context, attrs);
    this.setWebChromeClient(new WebChromeClient() {
    });
  }

  public JustifiedTextView(Context context) {
    super(context);
    this.setWebChromeClient(new WebChromeClient() {
    });
  }

  public JustifiedTextView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);
    this.setWebChromeClient(new WebChromeClient() {
    });
  }

  public void setText(String s) {
    this.text = s;
    reloadData();
  }

  @SuppressLint("DefaultLocale")
  private void reloadData() {

    // loadData(...) has a bug showing utf-8 correctly. That's why we need to set it first.
    this.getSettings().setDefaultTextEncodingName("utf-8");

    this.loadData(String.format(core, textColor, textSize, text), "text/html; charset=UTF-8", "utf-8");

    // set WebView's background color *after* data was loaded.
    super.setBackgroundColor(backgroundColor);

    // Hardware rendering breaks background color to work as expected.
    // Need to use software renderer in that case.
  }

  public void setTextColor(int hex) {
    String h = Integer.toHexString(hex);
    int a = Integer.parseInt(h.substring(0, 2), 16);
    int r = Integer.parseInt(h.substring(2, 4), 16);
    int g = Integer.parseInt(h.substring(4, 6), 16);
    int b = Integer.parseInt(h.substring(6, 8), 16);
    textColor = String.format("%d,%d,%d,%d", r, g, b, a);
    reloadData();
  }

  public void setBackgroundColor(int hex) {
    backgroundColor = hex;
    reloadData();
  }

  public void setTextSize(int textSize) {
    this.textSize = textSize;
    reloadData();
  }
}