package com.dbs.omni.tw.adapter;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.dbs.omni.tw.R;

import java.util.ArrayList;

/**
 * Created by siang on 2017/4/12.
 */

public class HomePagerAdapter extends PagerAdapter {

    private LayoutInflater layoutInflater;
    private ArrayList<Integer> imageList;
    private ArrayList<String> urlList;
//
    public HomePagerAdapter(Context context, ArrayList<Integer> list) {
        layoutInflater = LayoutInflater.from(context);
        imageList = new ArrayList<>();
        imageList.addAll(list);
    }

//    public HomePagerAdapter(Context context, ArrayList<String> list) {
//        layoutInflater = LayoutInflater.from(context);
//        urlList = new ArrayList<>();
//        urlList.addAll(list);
//    }

    @Override
    public int getCount() {
        return 3;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
//        return super.instantiateItem(container, position);

        View view = layoutInflater.inflate(R.layout.home_pager_item,  null);
        ImageView image = (ImageView) view.findViewById(android.R.id.background);
        image.setBackgroundResource(imageList.get(position));


// Web
//        WebView webView = (WebView) view.findViewById(R.id.webview);
//        webView.getSettings().setJavaScriptEnabled(true);
//
//        // 使用內建縮放功能
//        webView.getSettings().setBuiltInZoomControls(true);
//        webView.getSettings().setDisplayZoomControls(false);
//        // 讓圖片調整到適合WebView大小
//        webView.getSettings().setUseWideViewPort(true);
//        webView.getSettings().setLoadWithOverviewMode(true); // 自動縮放畫面整個顯示
//
//        webView.getSettings().setAllowFileAccessFromFileURLs(true);
//        webView.getSettings().setAllowUniversalAccessFromFileURLs(true);
//
//        webView.loadUrl(urlList.get(position));


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
