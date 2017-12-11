package com.example.r30_a.testlayout;

import android.content.Context;
import android.media.MediaPlayer;
import android.media.SoundPool;

/**
 * Created by R30-A on 2017/11/30.
 */

public class music_player {



    private static MediaPlayer mediaPlayer = null;



    public static void playsound(Context context, int res){

        mediaPlayer = MediaPlayer.create(context, res);
        mediaPlayer.setLooping(false);
        mediaPlayer.stop();
    }

    public static void playmusic(Context context, int res){
        stop(context);
        mediaPlayer = MediaPlayer.create(context, res);
        mediaPlayer.setLooping(true);
        mediaPlayer.start();


    }

    public static void stop(Context context){

        if(mediaPlayer != null){
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }



}
