package com.example.r30_a.testlayout.fragment;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.example.r30_a.testlayout.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class fragment1 extends Fragment {
    ImageView sample1;

    public fragment1() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment1,null);
        sample1 = (ImageView)view.findViewById(R.id.sample1);
        sample1.setImageResource(R.drawable.sky);

        // Inflate the layout for this fragment
        return view;
    }

}
