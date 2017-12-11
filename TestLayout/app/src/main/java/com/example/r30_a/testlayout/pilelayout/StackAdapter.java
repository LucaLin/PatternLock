package com.example.r30_a.testlayout.pilelayout;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.r30_a.testlayout.R;

import java.util.Arrays;
import java.util.List;

/**
 * Created by R30-A on 2017/12/7.
 *
 *
 *     compile 'com.makeramen:roundedimageview:2.3.0'
 compile 'com.github.bumptech.glide:glide:3.8.0'
 */

public class StackAdapter extends RecyclerView.Adapter<StackAdapter.ViewHolder>{

    private LayoutInflater inflater;
    private List<String> datas;
    private Context context;
    private List<Integer> imageUrls = Arrays.asList(
            R.drawable.xm2,
            R.drawable.xm3,
            R.drawable.xm4,
            R.drawable.xm5,
            R.drawable.xm6,
            R.drawable.xm7,
            R.drawable.xm1,
            R.drawable.xm8,
            R.drawable.xm9,
            R.drawable.xm1,
            R.drawable.xm2

    );

    public StackAdapter(List<String> datas){
        this.datas = datas;
    }



    @Override
    public int getItemCount() {
        return datas == null?  0  :datas.size();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        if(inflater ==null){
            context = parent.getContext();
            inflater = LayoutInflater.from(parent.getContext());
        }
        return new ViewHolder(inflater.inflate(R.layout.item_card,parent,false));
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Glide.with(context).load(imageUrls.get(position)).into(holder.cover);
        holder.index.setText(String.valueOf(position));
    }


    class ViewHolder extends RecyclerView.ViewHolder{
        ImageView cover;
        TextView index;

        public ViewHolder(View itemView){
            super(itemView);
            cover = (ImageView)itemView.findViewById(R.id.cover);
            index = (TextView)itemView.findViewById(R.id.index);

        }
    }
}
