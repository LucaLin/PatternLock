package com.example.r30_a.testlayout;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by R30-A on 2018/1/19.
 */

public class Other_Fragment extends Fragment implements View.OnClickListener{

    ImageView imagePhoto;


    public Other_Fragment(){}


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setHasOptionsMenu(true);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_other,container,false);
        setButtonText(view,R.id.btn_home,R.string.frag_home,R.drawable.icons8_home_48);
        setButtonText(view,R.id.btn_setting,R.string.frag_setting,R.drawable.ic_settings_black_36dp);
        setButtonText(view,R.id.btn_info,R.string.frag_info,R.drawable.ic_info_black_24dp);
        setButtonText(view,R.id.btn_finger,R.string.frag_finger,R.drawable.ic_fingerprint_black_48dp);
        setButtonText(view,R.id.btn_star,R.string.frag_mylove,R.drawable.ic_stars_black_24dp);
        setButtonText(view,R.id.btn_store,R.string.frag_shop,R.drawable.ic_shop_black_24dp);

        imagePhoto = (ImageView)view.findViewById(R.id.imgPerson);
        imagePhoto.setImageResource(R.drawable.ic_person_black_24dp);


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
