package ir.imanpour.imanpour.main;

import android.app.PendingIntent;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;

import com.example.arash.imanpour.R;

import ir.imanpour.imanpour.background.InstallReciver;
import ir.imanpour.imanpour.listenner.LikeLitenner;
import ir.imanpour.imanpour.listenner.OnNumberOfItemChangeListenner;
import ir.imanpour.imanpour.listenner.OnShareListenner;
import ir.imanpour.imanpour.listenner.UnReadListenner;

import static ir.imanpour.imanpour.main.G.action;
import static ir.imanpour.imanpour.main.G.context;


public class ActivityMain extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
  FragmentManager fragmentManager;
  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setTheme(G.theme);
    setContentView(R.layout.activity_main);
    fragmentManager=getSupportFragmentManager();
    Intent intent = new Intent(context, InstallReciver.class);
    intent.setAction(action);
    boolean isWorking = (PendingIntent.getBroadcast(context, 1001, intent, PendingIntent.FLAG_NO_CREATE) == null);
    if (!isWorking) {
      G.runAlarm();
     // Tools.log("really");
    }
    Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
    setSupportActionBar(toolbar);
    final DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
    ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
      this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
    drawer.setDrawerListener(toggle);
    toggle.syncState();
    final NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
    navigationView.setNavigationItemSelectedListener(this);
    final TextView txt_like = (TextView) navigationView.getMenu().findItem(R.id.like_counter).getActionView();
    Cursor cursor = G.sqLiteDatabase.rawQuery("select * from Rss where like =1", null);
    txt_like.setText("" + cursor.getCount());
    cursor.close();
    final TextView txt_not_read = (TextView) navigationView.getMenu().findItem(R.id.unread_counter).getActionView();
    Cursor cursorUnRead = G.sqLiteDatabase.rawQuery("select * from Rss where unread =0", null);
    txt_not_read.setText("" + cursorUnRead.getCount());
    cursorUnRead.close();
    final TextView txt_home = (TextView) navigationView.getMenu().findItem(R.id.home).getActionView();
    Cursor cursor_home = G.sqLiteDatabase.rawQuery("select * from Rss ", null);
    txt_home.setText("" + cursor_home.getCount());
    cursor_home.close();

    G.likeLitenner = new LikeLitenner() {
      @Override
      public void likeListenner(int state) {
        txt_like.setText("" + state);
      }
    };
    G.unread = new UnReadListenner() {
      @Override
      public void read(int state) {
        txt_not_read.setText("" + state);
      }
    };
    G.onDataChangeListenner = new OnNumberOfItemChangeListenner() {
      @Override
      public void numberOfItemChangeListenner(int count) {
        txt_home.setText("" + count);
      }
    };
    Switch switch_mode = (Switch) navigationView.getMenu().findItem(R.id.switch_mode).getActionView();
    switch_mode.setChecked((G.theme == R.style.AppThemeDark));
    switch_mode.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
      @Override
      public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        drawer.closeDrawer(GravityCompat.START);
        if (isChecked) {
          //save this state
          G.theme = R.style.AppThemeDark;
        } else {
          G.theme = R.style.AppThemeBase_NoActionBar;
        }
        SharedPreferences.Editor editor = G.sharedPreferences.edit();
        editor.putInt(G.themeP, G.theme);
        editor.apply();
        finish();
        startActivity(getIntent());
      }
    });
    G.onShareListenner = new OnShareListenner() {
      @Override
      public void share() {
        if (G.canShare) {
          setTitle("یک آیتم بری اشتراک گذاری انتخاب کنید");
        } else {
          setTitle(R.string.app_name);
        }
      }
    };
    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
    fragmentTransaction.replace(R.id.fragment,new FragmentHome());
    fragmentTransaction.commit();
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    MenuInflater menuInflater = getMenuInflater();
    menuInflater.inflate(R.menu.menu_share, menu);
    return super.onCreateOptionsMenu(menu);
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    G.canShare = !G.canShare;
    if (G.canShare) {
      setTitle("یک آیتم بری اشتراک گذاری انتخاب کنید");
    } else {
      setTitle(R.string.app_name);
    }
    return true;
  }

  @Override
  public boolean onNavigationItemSelected(@NonNull MenuItem item) {
    switch (item.getItemId()) {
      case R.id.home:
     fragmentManager.beginTransaction().replace(R.id.fragment, new FragmentHome()).commit();
        break;
      case R.id.like_counter:
     fragmentManager.beginTransaction().replace(R.id.fragment, new FragmentLike()).commit();
        break;
      case R.id.unread_counter:
     fragmentManager.beginTransaction().replace(R.id.fragment, new FragmentRead()).commit();
        break;
      case R.id.contact:
        startActivity(new Intent(ActivityMain.this, ActivityContact.class));
        break;
    }
    try {
      // Set action bar title
      setTitle(item.getTitle());
    } catch (Exception e) {
      e.printStackTrace();
    }
    ((DrawerLayout) findViewById(R.id.drawer_layout)).closeDrawer(GravityCompat.START);
    return true;
  }

  @Override
  protected void onResume() {
    super.onResume();
    /*FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
    fragmentTransaction.replace(R.id.fragment,new FragmentHome());
    fragmentTransaction.commit();*/
  }
}
