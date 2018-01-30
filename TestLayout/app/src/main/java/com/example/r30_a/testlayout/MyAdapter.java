package com.example.r30_a.testlayout;


import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.google.android.youtube.player.YouTubeBaseActivity;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerFragment;
import com.google.android.youtube.player.YouTubePlayerView;

import java.util.List;



public class MyAdapter extends RecyclerView.Adapter <RecyclerView.ViewHolder>{
//這裡要繼承的泛型很重要，如果是自定義layout的話就要指定，後面的覆寫方法需要的物件才會正確

    public static final int ONE = 1;
    public static final int TWO = 2;
    private List<String> data;
    private Context context;
//建立建構式，在物件一建立時就把list要的資料放進去
    public MyAdapter(List<String>data){
        this.data = data;

    }

    @Override
    public int getItemViewType(int position) {
       // if(position %4 ==0){
            return TWO;
      //  }else
       //     return ONE;
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

            RecyclerView.ViewHolder holder = null;
           // View view = LayoutInflater.from(context).inflate(viewType,parent,false);
            if(viewType == TWO){
               // holder =  new OneHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.fragment_child,parent,false));
                holder =  new TwoHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.fragment_fragment_youtube,parent,false));
            }else {
//                holder =  new TwoHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.fragment_fragment_youtube,parent,false));
            }
            return holder;
        }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

    }






    //回傳清單數量
    @Override
    public int getItemCount() {
        return data.size();
    }

    //在此建立子view的每個物件的功能






        }

    class OneHolder extends RecyclerView.ViewHolder{
            TextView txvChild;
            public OneHolder(View itemView) {
                super(itemView);
                txvChild = (TextView)itemView.findViewById(R.id.txvChild);

            }
    }
        class TwoHolder extends RecyclerView.ViewHolder{
            public TwoHolder(View itemView){
                super(itemView);
                YouTubePlayerView youTubePlayerView = (YouTubePlayerView)itemView.findViewById(R.id.youtubefrag);

            }
        }



