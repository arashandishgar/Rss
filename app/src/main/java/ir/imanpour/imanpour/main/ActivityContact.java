package ir.imanpour.imanpour.main;

  import android.os.Bundle;
  import android.support.v7.app.AppCompatActivity;

  import com.example.arash.imanpour.R;


public class ActivityContact extends AppCompatActivity {

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setTheme(G.theme);
    setContentView(R.layout.activity_contact);
  }
}
