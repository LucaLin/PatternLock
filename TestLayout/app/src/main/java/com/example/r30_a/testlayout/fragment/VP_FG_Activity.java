package com.example.r30_a.testlayout.fragment;

import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;

import com.example.r30_a.testlayout.R;
import com.example.r30_a.testlayout.myViewPageradpter;

import java.util.ArrayList;
import java.util.List;

public class VP_FG_Activity extends FragmentActivity {
    private ViewPager viewPager;
    private View page1,page2,page3;
    private List<View> pagelist;
    private List<android.support.v4.app.Fragment> fglist;
    private fragment1 fg1;
    private fragment2 fg2;
    private fragment3 fg3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vp__fg_);
        viewPager = (ViewPager)findViewById(R.id.viewpagerforVP);
        init();
    }

    private void init() {
       // List<android.support.v4.app.Fragment> list = new ArrayList<>();
        LayoutInflater inflater = getLayoutInflater();
        page1 = inflater.inflate(R.layout.fragment1,null);
        page2 = inflater.inflate(R.layout.fragment2,null);
        page3 = inflater.inflate(R.layout.fragment3,null);

       // fg1 = new fragment1();
       // fg2 = new fragment2();
       // fg3 = new fragment3();
      //  fglist = new ArrayList<>();

      //  fglist.add(fg1);
      //  fglist.add(fg2);
      //  fglist.add(fg3);


        pagelist = new ArrayList<>();
        pagelist.add(page1);
        pagelist.add(page2);
        pagelist.add(page3);

        viewPager.setAdapter(new myViewPageradpter(getSupportFragmentManager(),pagelist));
    }
}
