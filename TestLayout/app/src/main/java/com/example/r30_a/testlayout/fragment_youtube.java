package com.example.r30_a.testlayout;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.MediaController;
import android.widget.VideoView;

import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerFragment;
import com.google.android.youtube.player.YouTubePlayerSupportFragment;
import com.google.android.youtube.player.YouTubePlayerView;

import java.util.zip.Inflater;


/**
 * A simple {@link Fragment} subclass.
 */
public class fragment_youtube extends YouTubePlayerFragment implements YouTubePlayer.OnInitializedListener{
    private YouTubePlayerFragment youTubePlayerFragment;
    private YouTubePlayerView youTubePlayer;
    /*    <uses-permission android:name="android.permission.INTERNET" />
    <uses-permission android:name="android.permission.WAKE_LOCK"/>*/

    public fragment_youtube() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_fragment_youtube,container,false);

        youTubePlayerFragment = new YouTubePlayerFragment();
        youTubePlayerFragment.initialize("api_key",this);
        android.app.FragmentManager fragmentManager = getFragmentManager();
        android.app.FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.youtubefrag,youTubePlayerFragment);
        fragmentTransaction.commit();
        youTubePlayer = (YouTubePlayerView) view.findViewById(R.id.youtubefrag);
        youTubePlayer.initialize("abc",this);



        // Inflate the layout for this fragment
        return view;
    }

    private void setVideo() {


    }

    @Override
    public void onInitializationSuccess(YouTubePlayer.Provider provider, YouTubePlayer youTubePlayer, boolean b) {
        youTubePlayer.cueVideo("r-lNSEGkQAY");
    }

    @Override
    public void onInitializationFailure(YouTubePlayer.Provider provider, YouTubeInitializationResult youTubeInitializationResult) {

    }
}
