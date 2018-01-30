package com.example.r30_a.testlayout;

import android.media.MediaPlayer;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.MediaController;
import android.widget.Toast;
import android.widget.VideoView;

import com.google.android.youtube.player.YouTubeApiServiceUtil;
import com.google.android.youtube.player.YouTubeBaseActivity;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerView;

public class YoutubePlayer extends YouTubeBaseActivity implements YouTubePlayer.OnInitializedListener {

private YouTubePlayerView youTubePlayerView;
public static final String VideoKey = "r-lNSEGkQAY";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.youtube_player);
       // videoAddress = "https://http://www.youtube.com/watch?v=W_sdEBKrJUE";

        //setVideo(videoAddress);

        youTubePlayerView = (YouTubePlayerView)findViewById(R.id.youtubeView);
        youTubePlayerView.initialize("abc",this);

    }


    @Override
    public void onInitializationSuccess(YouTubePlayer.Provider provider, YouTubePlayer youTubePlayer, boolean b) {
        if(youTubePlayer == null){
            return;
        }
        youTubePlayer.cueVideo(VideoKey);


    }

    @Override
    public void onInitializationFailure(YouTubePlayer.Provider provider, YouTubeInitializationResult youTubeInitializationResult) {
        Toast.makeText(this,"failed",Toast.LENGTH_SHORT).show();
    }
}
