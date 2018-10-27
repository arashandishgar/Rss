package ir.imanpour.imanpour.main;

import android.annotation.SuppressLint;
import android.content.res.Configuration;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.example.arash.imanpour.R;

import java.util.ArrayList;

import ir.imanpour.imanpour.adpter.FeedAapter;
import ir.imanpour.imanpour.helper.DataBaseHelper;
import ir.imanpour.imanpour.rss.RssParser;

public class FragmentLike extends android.support.v4.app.Fragment {
  RecyclerView recyclerView;
  ArrayList<RssParser.Item> items;
  LinearLayout linearLayout;

  @Nullable
  @Override
  public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.fragment_unread, container, false);
    recyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);
    linearLayout=(LinearLayout)view.findViewById(R.id.ln_noitem);
    @SuppressLint("Recycle") Cursor cursor = G.sqLiteDatabase.rawQuery("select * from Rss where like=1", null);
    if(cursor.getCount()==0){
      linearLayout.setVisibility(View.VISIBLE);
      return view ;
    }
    items = DataBaseHelper.convertCoursorToArray(cursor);
    FeedAapter feedAapter = new FeedAapter(items);
    recyclerView.setAdapter(feedAapter);
    return view;
  }

  @Override
  public void onResume() {
    @SuppressLint("Recycle") Cursor cursor = G.sqLiteDatabase.rawQuery("select * from Rss where like=1", null);
    super.onResume();
    if(cursor.getCount()==0){
      linearLayout.setVisibility(View.VISIBLE);
      return  ;
    }else {
      linearLayout.setVisibility(View.GONE);
    }
    items = DataBaseHelper.convertCoursorToArray(cursor);
    recyclerView.setAdapter(new FeedAapter(items));
    if (getResources().getConfiguration().orientation== Configuration.ORIENTATION_LANDSCAPE) {
      recyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 2));
    } else {
      recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
    }
  }
}
