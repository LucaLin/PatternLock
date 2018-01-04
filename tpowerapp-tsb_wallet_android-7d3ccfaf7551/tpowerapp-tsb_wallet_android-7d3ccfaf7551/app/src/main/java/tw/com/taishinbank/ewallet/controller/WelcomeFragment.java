package tw.com.taishinbank.ewallet.controller;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;

import tw.com.taishinbank.ewallet.R;
import tw.com.taishinbank.ewallet.util.PreferenceUtil;

/**
 * A placeholder fragment containing a simple view.
 */
public class WelcomeFragment extends Fragment implements View.OnClickListener{
    /**
     * The fragment argument representing the section number for this
     * fragment.
     */
    private static final String ARG_PAGE_NUMBER = "page_number";
    public static int MAX_PAGE_NUMBER = 3;

    /**
     * Returns a new instance of this fragment for the given section
     * number.
     */
    public static WelcomeFragment newInstance(int pageNumber) {
        WelcomeFragment fragment = new WelcomeFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_PAGE_NUMBER, pageNumber);
        fragment.setArguments(args);
        return fragment;
    }

    public WelcomeFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_welcome, container, false);
        int pageNumber = getArguments().getInt(ARG_PAGE_NUMBER);
        ImageView image_welcome = (ImageView) rootView.findViewById(R.id.image_wecome);

        image_welcome.setImageResource(((WelcomeActivity) getActivity()).getImage(pageNumber - 1));

        if(pageNumber == MAX_PAGE_NUMBER){
            Button buttonRegister = (Button) rootView.findViewById(R.id.button_register);
            LinearLayout buttonLogin = (LinearLayout) rootView.findViewById(R.id.button_login);

            buttonRegister.setVisibility(View.VISIBLE);
            buttonLogin.setVisibility(View.VISIBLE);
            buttonRegister.setOnClickListener(this);
            buttonLogin.setOnClickListener(this);
        }

        return rootView;
    }

    @Override
    public void onClick(View view) {
        int viewId = view.getId();

        Intent intent = null;
        // 按下註冊
        if(viewId == R.id.button_register){
            intent = new Intent(getActivity(), RegisterActivity.class);

        // 按下登入
        }else if(viewId == R.id.button_login){
            intent = new Intent(getActivity(), LoginActivity.class);
        }

        if(intent != null){
            // TODO 確認什麼時候要改變這個flag
            PreferenceUtil.setFirstTimeUse(getActivity(), false);
            startActivity(intent);
        }
    }
}
