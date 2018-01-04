package tw.com.taishinbank.ewallet.controller;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import tw.com.taishinbank.ewallet.R;
import tw.com.taishinbank.ewallet.util.PreferenceUtil;

public class RegisterFragment4 extends Fragment implements View.OnClickListener {

    private Button buttonStart;

    public static final int PAGE_INDEX = RegisterFragment3_2.PAGE_INDEX + 1;

    public RegisterFragment4() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_register4, container, false);
        buttonStart = (Button) view.findViewById(R.id.button_start);
        buttonStart.setOnClickListener(this);

        View headline = view.findViewById(R.id.headline);
        ((ActivityBase)getActivity()).setHeadline(headline, R.string.register_title4, R.string.register_subtitle4);

//        PreferenceUtil.checkLoginSameMember(getActivity());

        return view;
    }


    @Override
    public void onClick(View view){
        int viewId = view.getId();
        // 按下開始體驗，開啟主頁面
        if(viewId == R.id.button_start){
            Activity activity = getActivity();
            Intent intent = new Intent(activity, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        }
    }
}
