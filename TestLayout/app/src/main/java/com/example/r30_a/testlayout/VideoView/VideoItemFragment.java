package com.example.r30_a.testlayout.VideoView;

import android.media.MediaPlayer;
import android.support.v4.app.FragmentActivity;
import android.widget.ImageView;

import com.example.r30_a.testlayout.FullScreenVideoActivity;
import com.example.r30_a.testlayout.R;

/**
 * 裝載fullscreenvideoview的fragment on 2018/1/30.
 */

public class VideoItemFragment extends LazyLoadFragment
implements MediaPlayer.OnCompletionListener,
            MediaPlayer.OnErrorListener,
            MediaPlayer.OnPreparedListener{

    private boolean isPaused;
    private int videoPosition;
    private int position;
    private int videoRes;
    private int imgRes;
    private FullScreenVideoView videoView;
    private ImageView slogan;

    public VideoItemFragment(){
        isPaused = false;
        videoPosition = 0;
    }


    @Override
    protected void stopLoad() {
       // super.stopLoad();
        if(videoView !=null){
            videoView.stopPlayback();
        }
    }
//頁面重啟時繼續播放
    @Override
    public void onResume() {
        super.onResume();
        if(isPaused){
            if(videoView != null){
                videoView.seekTo(videoPosition);
                videoView.resume();
            }
        }
        return;
    }
//頁面暫停時留在當前位置
    @Override
    public void onPause() {
        super.onPause();
        if(videoView != null){
            videoPosition = videoView.getCurrentPosition();
        }
        isPaused = true;
    }
//離開頁面的時候結束影片播放
    @Override
    public void onDestroy() {
        super.onDestroy();
        if(videoView  != null){
            videoView.stopPlayback();
        }
        return;
    }
//加載資料來源的地方，使用取得bundle的方法
    @Override
    protected void lazyload() {
        if(getArguments() == null){
            return;
        }
        position = getArguments().getInt("position");
        videoRes = getArguments().getInt("videoRes");
        imgRes = getArguments().getInt("imgRes");

        videoView = findViewById(R.id.splash);
        slogan = findViewById(R.id.Slogan);
        videoView.setOnErrorListener(this);
        videoView.setOnPreparedListener(this);
        videoView.setVideoPath("android.resource://"+getActivity().getPackageName()+"/"+videoRes);
        slogan.setImageResource(imgRes);

    }

    @Override
    protected int setContentView() {

        return R.layout.video_viewpager_item;
    }
    //準備影片的播放
    @Override
    public void onPrepared(MediaPlayer mp) {
        if(videoView != null){
            videoView.requestFocus();
            videoView.setOnCompletionListener(this);
            videoView.seekTo(0);
            videoView.start();
        }
        return;

    }
    //一個影片播完的時候，切到下一個影片的position
    @Override
    public void onCompletion(MediaPlayer mp) {
        FragmentActivity localFragmentActivity = getActivity();
        if((localFragmentActivity != null) && ((localFragmentActivity instanceof FullScreenVideoActivity))){
            ((FullScreenVideoActivity)localFragmentActivity).next(position);
        }
    }

    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        FragmentActivity localFragmentActivity = getActivity();
        if((localFragmentActivity != null) && (localFragmentActivity instanceof FullScreenVideoActivity)){
            ((FullScreenVideoActivity)localFragmentActivity).next(position);
        }
        return true;
    }

//播放功能
    public void play(){
        if(videoView != null){
            videoView.requestFocus();
            videoView.setOnCompletionListener(this);
            videoView.seekTo(0);
        }else {
            return;
        }
        videoView.start();
    }
}
