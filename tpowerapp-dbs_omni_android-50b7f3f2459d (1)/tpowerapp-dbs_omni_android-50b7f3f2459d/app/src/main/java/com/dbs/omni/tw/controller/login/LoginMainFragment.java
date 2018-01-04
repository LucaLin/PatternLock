package com.dbs.omni.tw.controller.login;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.dbs.omni.tw.R;
import com.dbs.omni.tw.interfaces.FragmentListener;

public class LoginMainFragment extends Fragment {

    private FragmentListener.OnFragmentListener onFragmentListener;

    public void setOnFragmentListener (FragmentListener.OnFragmentListener onFragmentListener) {
        this.onFragmentListener = onFragmentListener;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_login_main, container, false);

        Button buttonLogin = (Button) view.findViewById(R.id.button_login);

        buttonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onFragmentListener.onNext();
            }
        });

//        this.onFragmentListener.onNext();

        return view;
    }
}
