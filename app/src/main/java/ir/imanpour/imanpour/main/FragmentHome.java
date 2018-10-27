package ir.imanpour.imanpour.main;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.arash.imanpour.R;

import ir.imanpour.imanpour.background.MyService;
import ir.imanpour.imanpour.listenner.OnRefereshFalse;
import ir.imanpour.imanpour.listenner.SwipeListenner;

public class FragmentHome extends Fragment {
  RecyclerView recyclerView;
  ProgressBar progressBar;
  SwipeRefreshLayout swipeRefreshLayout;
  ImageView img_isNotConnected;

  @Nullable
  @Override
  public View onCreateView(final LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.fragment_home, container, false);
    progressBar = (ProgressBar) view.findViewById(R.id.progressBar);
    progressBar.setVisibility(View.GONE);
    //new
   // Tools.log("first"+G.runFirst);
    G.runFirst= G.sharedPreferences.getBoolean(G.first, true);
    if(G.runFirst){
      if(isNetworkOnline()) {
        progressBar.setIndeterminate(true);
        progressBar.setVisibility(View.VISIBLE);
      }
        SharedPreferences.Editor edit = G.sharedPreferences.edit();
        edit.putBoolean(G.first, false);
        edit.commit();
    }
    swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swp_refresh);
    img_isNotConnected = (ImageView) view.findViewById(R.id.img_isNotConnected);
    if(!isNetworkOnline()){
      progressBar.setVisibility(View.GONE);
      progressBar.setIndeterminate(false);
      img_isNotConnected.setVisibility(View.VISIBLE);
    }
    img_isNotConnected.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        if(isNetworkOnline()) {
          swipeRefreshLayout.setRefreshing(true);
          progressBar.setIndeterminate(true);
          progressBar.setVisibility(View.VISIBLE);
          v.setVisibility(View.GONE);
          G.needRefresh = true;
          SharedPreferences.Editor editor = G.sharedPreferences.edit();
          editor.putBoolean(G.refresh, G.needRefresh);
          editor.apply();
          MyService.enqueueWork(getActivity(), new Intent(getActivity(), MyService.class));
        }else {
          Toast.makeText(G.context,"اینترنت خود را روشن کنید",Toast.LENGTH_LONG).show();
        }
      }
    });
    int resId = R.anim.layout_animation_fall_down;
    LayoutAnimationController animation = AnimationUtils.loadLayoutAnimation(getActivity(), resId);
    recyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);
    //feed adapter 45
    recyclerView.setLayoutAnimation(animation);
    //recyclerView.addItemDecoration(new VerticalSpace(20,2));
    recyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 2));
    recyclerView.setAdapter(G.feedAapter);
    swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
      @Override
      public void onRefresh() {
       // Tools.Toast("no");
        G.needRefresh = true;
        SharedPreferences.Editor editor = G.sharedPreferences.edit();
        editor.putBoolean(G.refresh, G.needRefresh);
        editor.apply();
        MyService.enqueueWork(getActivity(), new Intent(getActivity(), MyService.class));
        swipeRefreshLayout.setRefreshing(false);
        progressBar.setIndeterminate(true);
        progressBar.setVisibility(View.VISIBLE);
        img_isNotConnected.setVisibility(View.GONE);
        G.swipeListenner = new SwipeListenner() {
          @Override
          public void onFinsh() {
            swipeRefreshLayout.setRefreshing(false);
            swipeRefreshLayout.setRefreshing(false);
            progressBar.setIndeterminate(false);
            progressBar.setVisibility(View.GONE);
          }
        };
      }
    });
    G.onRefereshFalse = new OnRefereshFalse() {
      @Override
      public void run() {
        swipeRefreshLayout.setRefreshing(false);
        img_isNotConnected.setVisibility(View.VISIBLE);
        progressBar.setIndeterminate(false);
        progressBar.setVisibility(View.GONE);
      }
    };
    return view;
  }

  @Override
  public void onResume() {
    //Tools.Toast("home");
    super.onResume();
    if(G.needRefresh){
      progressBar.setVisibility(View.VISIBLE);
      progressBar.setIndeterminate(true);
    }
    if (getResources().getConfiguration().orientation== Configuration.ORIENTATION_LANDSCAPE) {
    recyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 2));
    } else {
      recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
    }
    recyclerView.setAdapter(G.feedAapter);
  }
  public boolean isNetworkOnline() {
    boolean status = false;
    try {
      ConnectivityManager cm = (ConnectivityManager) G.context.getSystemService(Context.CONNECTIVITY_SERVICE);
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
}
