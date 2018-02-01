package com.example.r30_a.testlayout;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.net.Uri;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.bumptech.glide.Glide;
import com.donkingliang.banner.CustomBanner;

import java.util.ArrayList;

/**
 * Created by R30-A on 2018/1/19.
 */

/*引入第三方套件Custombanner輪播
allprojects {maven { url 'https://jitpack.io' }
compile 'com.github.donkingliang:CustomBanner:1.1.2'
來源https://github.com/donkingliang/CustomBanner
 */

public class Other_Fragment extends Fragment implements View.OnClickListener{
    VideoView videoView;
    ImageView imagePhoto;
    CustomBanner customBanner;
    ArrayList imgList;
    Toast toast;
    public Other_Fragment(){}


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setHasOptionsMenu(true);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {

        final View view = inflater.inflate(R.layout.fragment_other,container,false);
        setButtonText(view,R.id.btn_home,R.string.frag_home,R.drawable.icons8_home_48);
        setButtonText(view,R.id.btn_setting,R.string.frag_setting,R.drawable.ic_settings_black_36dp);
        setButtonText(view,R.id.btn_info,R.string.frag_info,R.drawable.ic_info_black_24dp);
        setButtonText(view,R.id.btn_finger,R.string.frag_finger,R.drawable.ic_fingerprint_black_48dp);
        setButtonText(view,R.id.btn_star,R.string.frag_mylove,R.drawable.ic_stars_black_24dp);
        setButtonText(view,R.id.btn_store,R.string.frag_shop,R.drawable.ic_shop_black_24dp);
        toast = Toast.makeText(getContext(),"",Toast.LENGTH_SHORT);

        //加載輪播圖片
        imgList = new ArrayList();
        imgList.add(R.drawable.slogan_1);
        imgList.add(R.drawable.slogan_2);
        imgList.add(R.drawable.slogan_3);
        imgList.add(R.drawable.slogan_4);


       // imgList.add(Color.TRANSPARENT);


        //開始使用custombanner輪播功能
        customBanner = (CustomBanner)view.findViewById(R.id.customBanner);

        customBanner.setPages(new CustomBanner.ViewCreator() {
            @Override
            public View createView(Context context, int pos) {
                //要返回一個輪播圖的項目，支援任何的view
                //position代表第幾項
                ImageView view = new ImageView(context);

                return view;

            }
            @Override
            public void updateUI(Context context, View view, int pos, Object data) {
                //更新輪播的地方
                Glide.with(context).load(data).into((ImageView)view);
            }
        },imgList);
        customBanner.startTurning(3000);//轉換圖片的時間

        customBanner.setScrollDuration(500);//滾動的速度
        customBanner.setIndicatorRes(R.drawable.shape_point_select,R.drawable.shape_point_unselect);
        //customBanner.setIndicator()
        customBanner.setIndicatorInterval(20);//小球的間距
        customBanner.setIndicatorGravity(CustomBanner.IndicatorGravity.CENTER);//小球顯示的地方
        //設定點擊監聽
        customBanner.setOnPageClickListener(new CustomBanner.OnPageClickListener() {
            @Override
            public void onPageClick(int pos, Object data) {
                toast.setText("這是第"+(pos+1)+"張圖");
                toast.show();
            }
        });

        //imagePhoto = (ImageView)view.findViewById(R.id.imgPerson);
        //imagePhoto.setImageResource(R.drawable.ic_person_black_24dp);


        return view;
    }

    private void setButtonText(View rootView, int ViewId, int txvId, int drawableId){
        View view = rootView.findViewById(ViewId);
        TextView textView = (TextView)view.findViewById(android.R.id.title);
        textView.setText(txvId);
        ImageView imageView = (ImageView)view.findViewById(android.R.id.icon);
        imageView.setImageResource(drawableId);
        view.setOnClickListener(this);


    }

    @Override
    public void onClick(View v) {

        int viewId = v.getId();

        switch (viewId){
            case R.id.btn_home:break;
            case R.id.btn_setting:break;
            case R.id.btn_info:break;

        }
    }
}
