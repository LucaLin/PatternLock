package com.example.r30_a.testlayout;

import android.support.v4.app.Fragment;
import android.content.Context;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.example.r30_a.testlayout.fragment.fragment1;
import com.example.r30_a.testlayout.fragment.fragment2;
import com.example.r30_a.testlayout.fragment.fragment3;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by R30-A on 2018/1/22.
 */

public class myViewPageradpter extends FragmentPagerAdapter {
    private List<View> pagelist;
    private List<Fragment> fglist;
    private Context context;
    private LayoutInflater layoutInflater;

    FragmentManager fragmentManager;


    /* public myViewPageradpter(List<View> pagelist){
        this.pagelist = pagelist;
    }*/

    public myViewPageradpter(FragmentManager fm) {
        super(fm);
        fglist = new ArrayList<>();

    }

    public myViewPageradpter(FragmentManager fm,List<View> pagelist) {
        super(fm);
      // this.fglist = new ArrayList<>();
      // this.fglist = fglist;
        this.pagelist = new ArrayList<>();
        this.pagelist = pagelist;

    }
    @Override
    public int getCount() {
        return pagelist.size();
       // return fglist.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }


   /* @Override
    public Object instantiateItem(ViewGroup container, int position) {

        //Fragment fg = new fragment1();
       // FragmentTransaction ft = .beginTransaction();
       // ft.replace(android.R.id.tabcontent,fg);
       // ft.commit();
        return  fragment;

    }*/

    @Override
    public Fragment getItem(int position) {
        return null;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
       // container.addView(fglist.get(position));
        return pagelist.get(position);
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView(pagelist.get(position));
    }
}
