package com.example.r30_a.testlayout;


import android.Manifest;
import android.app.Activity;
import android.app.FragmentTransaction;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.LinearSnapHelper;
import android.support.v7.widget.PagerSnapHelper;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.r30_a.testlayout.VideoView.ExtendedViewPager;
import com.example.r30_a.testlayout.VideoView.VideoItemFragment;
import com.example.r30_a.testlayout.fragment.ChildFragment;
import com.example.r30_a.testlayout.fragment.fragment1;
import com.example.r30_a.testlayout.fragment.fragment2;
import com.example.r30_a.testlayout.fragment.fragment3;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerFragment;
import com.google.android.youtube.player.YouTubePlayerView;
import com.google.android.youtube.player.YouTubeThumbnailView;

import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class FullScreenVideoActivity extends FragmentActivity implements ViewPager.OnPageChangeListener{

    Context context;

    private ExtendedViewPager vpVideo;
    private TextView txvEnter;
    private boolean Isvisible;
    private ViewPagerAdapter vpAdapter;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_ad);
        Isvisible = true;
        vpVideo = (ExtendedViewPager)findViewById(R.id.vp_video);
        vpAdapter = new ViewPagerAdapter(getSupportFragmentManager());
        vpVideo.setAdapter(vpAdapter);
        vpVideo.setOffscreenPageLimit(4);
    }


    public FullScreenVideoActivity() {
        // Required empty public constructor
        }


//使用viepager來設定資料來源
    class ViewPagerAdapter extends FragmentStatePagerAdapter {
        private final int[] videoRes;
        private final int[] slogenImgRes;

        public ViewPagerAdapter(FragmentManager fm) {
            super(fm);
            this.videoRes = new int[]{  R.raw.splash_1,
                    R.raw.splash_2,
                    R.raw.splash_3,
                    R.raw.splash_4};
            this.slogenImgRes = new int[]{R.drawable.slogan_1,
                    R.drawable.slogan_2,
                    R.drawable.slogan_3,
                    R.drawable.slogan_4};
        }

        @Override
        public Fragment getItem(int position) {
            VideoItemFragment videoItemFragment = new VideoItemFragment();
            Bundle bundle = new Bundle();
            bundle.putInt("position",position);
            bundle.putInt("videoRes",this.videoRes[position]);
            bundle.putInt("imgRes",this.slogenImgRes[position]);
            videoItemFragment.setArguments(bundle);
            if(position < getCount()){
                return videoItemFragment;
            }

            throw new RuntimeException("Position out of range");
        }

        @Override
        public int getCount() {
            return this.videoRes.length;
        }
    }
//呼叫此方法時，就跳一格
    public void next(int position){
        int i = this.vpVideo.getCurrentItem();
        if(position == i){
            position +=1;

            this.vpVideo.setCurrentItem(position,true);
        }
    }
    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }
//選擇到的頁面就開始播放
    @Override
    public void onPageSelected(int position) {
        ((VideoItemFragment)(vpAdapter.getItem(position))).play();
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }
}

