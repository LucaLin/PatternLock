package com.example.r30_a.testlayout;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;


/**
 * A simple {@link Fragment} subclass.
 */
public class BlankFragment extends DialogFragment {
     ImageButton btnOK,btnCancel;
    TextView txvAns;
    int answer;

    public BlankFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the cardfront for this fragment


        return inflater.inflate(R.layout.fragment_blank, container, false);

    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        btnCancel = (ImageButton)getView().findViewById(R.id.btnCancel);
        btnOK = (ImageButton)getView().findViewById(R.id.btnOK);
        txvAns = (TextView)getView().findViewById(R.id.txvAns);

        answer = (int)(Math.random()*9999)+1;
        txvAns.setText(String.valueOf("答案是："+answer));
    }

    @Override
    public void onStart() {
        super.onStart();


        Window window = getDialog().getWindow();
        WindowManager.LayoutParams layoutParams = window.getAttributes();

        //layoutParams.windowAnimations = R.style.animDialog;
        window.setAttributes(layoutParams);
    }


}
