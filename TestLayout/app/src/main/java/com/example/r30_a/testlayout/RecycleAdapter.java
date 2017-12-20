package com.example.r30_a.testlayout;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.ScaleAnimation;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

public class RecycleAdapter extends RecyclerView.Adapter <RecycleAdapter.Viewholder>{
//這裡要繼承的泛型很重要，如果是自定義layout的話就要指定，後面的覆寫方法需要的物件才會正確
    public boolean ischoose;
    private List<String> data;
//建立建構式，在物件一建立時就把list要的資料放進去
    public RecycleAdapter(List<String>data){
        this.data = data;
    }


    @Override
    public Viewholder onCreateViewHolder(ViewGroup parent, int viewType) {

            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.activity_recycle_adapter,parent,false);
            Viewholder viewholder = new Viewholder(view);
            //回傳自訂的viewholder物件給layout
            return viewholder;

    }

    @Override
    public void onBindViewHolder(final Viewholder holder, int position) {
        holder.txvlist.setText("測試用："+data.get(position)+"號");
        holder.imgOK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(!ischoose) {
                    holder.txvChoose.setText("已選取");
                    ischoose = true;

                }else {
                    holder.txvChoose.setText("");
                    ischoose = false;

                }
                ScaleAnimation scaleAnimation = new ScaleAnimation(1.0f,0.7f,1.0f,0.7f,
                        Animation.RELATIVE_TO_PARENT, Animation.RELATIVE_TO_PARENT);
                scaleAnimation.setDuration(100);
                holder.imgOK.startAnimation(scaleAnimation);
            }

        });
    }



    //回傳清單數量
    @Override
    public int getItemCount() {
        return data.size();
    }

    //在此建立子view的每個物件的功能
    public class Viewholder extends RecyclerView.ViewHolder {
        public TextView txvlist,txvChoose;
        public ImageButton imgOK;
        public ImageView imgandroid;

        public Viewholder(View v) {
            super(v);
            txvlist = (TextView)v.findViewById(R.id.txvReAdap);
            txvChoose = (TextView)v.findViewById(R.id.txvChoose);
            imgOK = (ImageButton)v.findViewById(R.id.imgOK);
            imgandroid = (ImageView)v.findViewById(R.id.imgAndroid);
        }
    }
}
