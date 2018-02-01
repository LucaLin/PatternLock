package com.example.r30_a.testlayout;


import android.content.Context;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.VideoView;

import com.ToxicBakery.viewpager.transforms.AccordionTransformer;
import com.ToxicBakery.viewpager.transforms.BackgroundToForegroundTransformer;
import com.ToxicBakery.viewpager.transforms.CubeInTransformer;
import com.ToxicBakery.viewpager.transforms.CubeOutTransformer;
import com.ToxicBakery.viewpager.transforms.DefaultTransformer;
import com.ToxicBakery.viewpager.transforms.DepthPageTransformer;
import com.ToxicBakery.viewpager.transforms.FlipHorizontalTransformer;
import com.ToxicBakery.viewpager.transforms.FlipVerticalTransformer;
import com.ToxicBakery.viewpager.transforms.ForegroundToBackgroundTransformer;
import com.ToxicBakery.viewpager.transforms.RotateDownTransformer;
import com.ToxicBakery.viewpager.transforms.RotateUpTransformer;
import com.ToxicBakery.viewpager.transforms.ScaleInOutTransformer;
import com.ToxicBakery.viewpager.transforms.StackTransformer;
import com.ToxicBakery.viewpager.transforms.TabletTransformer;
import com.ToxicBakery.viewpager.transforms.ZoomInTransformer;
import com.ToxicBakery.viewpager.transforms.ZoomOutSlideTransformer;
import com.ToxicBakery.viewpager.transforms.ZoomOutTranformer;
import com.bigkoo.convenientbanner.ConvenientBanner;
import com.bigkoo.convenientbanner.holder.CBViewHolderCreator;
import com.bigkoo.convenientbanner.holder.Holder;

import java.util.ArrayList;


/**
 * compile 'com.bigkoo:convenientbanner:2.0.5'
 * https://github.com/saiwu-bigkoo/Android-ConvenientBanner
 */
public class homeFragment extends Fragment {
    ConvenientBanner banner,banner2;
    TextView txvTransForm;
    ArrayList list,TransList,videoList;
    Button btnchange;
    int number;
    ViewPager.PageTransformer transformer;
    VideoView videoView;
    Context context;

    public homeFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_home, container, false);
        btnchange = (Button)v.findViewById(R.id.btnchange);
        txvTransForm = (TextView)v.findViewById(R.id.txvTransForm);
        banner = (ConvenientBanner) v.findViewById(R.id.cbPager);
        banner2 = ((ConvenientBanner) v.findViewById(R.id.cbPager2));
        initList();


        banner.setPages(new CBViewHolderCreator() {
            @Override
            public Object createHolder() {
                return new ImageHolder();
            }
        },list)
                .setPageIndicator(new int[] {
            R.drawable.shape_point_unselect, R.drawable.shape_point_select})
                .setPageIndicatorAlign(ConvenientBanner.PageIndicatorAlign.CENTER_HORIZONTAL)

                .startTurning(6000)
                .setScrollDuration(1800);
        banner.setBackgroundColor(Color.BLACK);


        banner2.setPages(new CBViewHolderCreator() {
            @Override
            public Object createHolder() {
                return new VideoHolder();
            }
        },videoList)
                //.setPageIndicator(new int[]{R.drawable.shape_point_unselect, R.drawable.shape_point_select})

                //.setPageIndicatorAlign(ConvenientBanner.PageIndicatorAlign.ALIGN_PARENT_LEFT)
                .startTurning(6000)
                .setScrollDuration(1800);

        banner2.setVisibility(View.INVISIBLE);




        btnchange.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(number < TransList.size()){

                    transformer = (ViewPager.PageTransformer) TransList.get(number);
                    banner.setPageTransformer(transformer);
                    banner2.setPageTransformer(transformer);
                    String form = (TransList.get(number)).toString();
                    number ++;
                    txvTransForm.setText("這是"+form.substring(37,form.length()-8)+"的效果");
                }else {
                    number = 0;
                    transformer = new DefaultTransformer();
                    banner.setPageTransformer(transformer);
                    banner2.setPageTransformer(transformer);
                    txvTransForm.setText("這是DefaultTransformer的效果");
                }
            }
        });
        return v;
    }









    private void initList() {
        list = new ArrayList();
        list.add(R.drawable.slogan_1);
        list.add(R.drawable.slogan_2);
        list.add(R.drawable.slogan_3);
        list.add(R.drawable.slogan_4);
        list.add(R.drawable.slogan_1);
        list.add(R.drawable.slogan_2);
        list.add(R.drawable.slogan_3);
        list.add(R.drawable.slogan_4);

        videoList = new ArrayList();
        videoList.add(R.raw.splash_1);
        videoList.add(R.raw.splash_2);
        videoList.add(R.raw.splash_3);
        videoList.add(R.raw.splash_4);


        TransList = new ArrayList();
        TransList.add(new AccordionTransformer());
        TransList.add(new DepthPageTransformer());
        TransList.add(new FlipVerticalTransformer());
        TransList.add(new TabletTransformer());
        TransList.add(new BackgroundToForegroundTransformer());
        TransList.add(new CubeInTransformer());
        TransList.add(new CubeOutTransformer());
        TransList.add(new FlipHorizontalTransformer());
        TransList.add(new ForegroundToBackgroundTransformer());
        TransList.add(new RotateDownTransformer());
        TransList.add(new RotateUpTransformer());
        TransList.add(new ScaleInOutTransformer());
        TransList.add(new StackTransformer());
        TransList.add(new ZoomInTransformer());
        TransList.add(new ZoomOutSlideTransformer());
        TransList.add(new ZoomOutTranformer());

    }

    public class ImageHolder implements Holder<Integer>{
        private ImageView imageView;
        private VideoView videoView;
    @Override
    public View createView(Context context) {
        imageView = new ImageView(context);
        imageView.setScaleType(ImageView.ScaleType.FIT_XY);

        return imageView;
    }


    @Override
    public void UpdateUI(Context context, int position, Integer data) {
        imageView.setImageResource(data);
       // txvTransForm.setText(String.valueOf(position));
        if(position >4 || position ==0){
            banner.setVisibility(View.INVISIBLE);
            banner2.setVisibility(View.VISIBLE);
        }else{
            banner.setVisibility(View.VISIBLE);

            banner2.setVisibility(View.INVISIBLE);
        }

        }

    }




    public class VideoHolder implements Holder<Integer>{

        @Override
        public View createView(Context context) {
            videoView =  new VideoView(context);
            return videoView;
        }

        @Override
        public void UpdateUI(Context context, int position, Integer data) {
            videoView.setVideoPath("android.resource://"+getActivity().getPackageName()+"/"+videoList.get(position));

            videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {

                    mp.start();
                    mp.setLooping(true);
                }
            });
            videoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {



                }
            });
        }


    }

}
