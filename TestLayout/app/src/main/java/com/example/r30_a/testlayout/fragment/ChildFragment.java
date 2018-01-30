package com.example.r30_a.testlayout.fragment;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.r30_a.testlayout.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class ChildFragment extends Fragment {
    public static final String POSITION_KEY = "FragmentPosition";
    private int position;

    public static ChildFragment newInstance(Bundle bundle){
        ChildFragment fragment = new ChildFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    public ChildFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        position = getArguments().getInt(POSITION_KEY);
        final View view = inflater.inflate(R.layout.fragment_child,container,false);
        TextView txvChild = (TextView)view.findViewById(R.id.txvChild);
        txvChild.setText(Integer.toString(position));
        txvChild.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(view.getContext(),"click at " + position,Toast.LENGTH_LONG).show();
            }
        });


        // Inflate the layout for this fragment
        return view;

    }

}
