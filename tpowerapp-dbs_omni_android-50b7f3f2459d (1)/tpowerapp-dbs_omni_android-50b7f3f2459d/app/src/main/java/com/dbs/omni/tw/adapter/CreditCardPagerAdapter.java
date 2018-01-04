package com.dbs.omni.tw.adapter;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

/**
 * Created by siang on 2017/4/12.
 */

public class CreditCardPagerAdapter extends PagerAdapter {

    private int pager_size = 3;

    private LayoutInflater layoutInflater;
    private ArrayList<View> pagerList;

    public CreditCardPagerAdapter(Context context, ArrayList<View> list) {
        layoutInflater = LayoutInflater.from(context);
        pager_size = list.size();
        pagerList = new ArrayList<>();
        pagerList.addAll(list);
    }

    public void updateAdapter(ArrayList<View> list)
    {
        pager_size = list.size();
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return pager_size;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
//        return super.instantiateItem(container, position);

        View view = pagerList.get(position);


        if(container.getChildCount() > position)
            container.addView(view, position);
        else
            container.addView(view);

        return view;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return (view == object);
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }
}
