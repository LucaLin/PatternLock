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

public class ResetPasswordFragment4 extends Fragment implements View.OnClickListener {

    private Button buttonStart;

    public static final int PAGE_INDEX = ResetPasswordFragment3.PAGE_INDEX + 1;

    public ResetPasswordFragment4() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_reset_password_4, container, false);
        buttonStart = (Button) view.findViewById(R.id.button_start);
        buttonStart.setOnClickListener(this);

        View headline = view.findViewById(R.id.headline);
        ((ActivityBase)getActivity()).setHeadline(headline, R.string.reset_password, R.string.reset_password_subtitle_4);

        return view;
    }


    @Override
    public void onClick(View view){
        int viewId = view.getId();
        // 按下重新登入
        if(viewId == R.id.button_start){
            Activity activity = getActivity();
//            PreferenceUtil.manualLogout(activity);
            PreferenceUtil.clearAllPreferences(activity);
            Intent intent = new Intent(activity, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        }
    }
}
