package tw.com.taishinbank.ewallet.controller;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;

import tw.com.taishinbank.ewallet.R;
import tw.com.taishinbank.ewallet.interfaces.OnButtonNextClickedListener;

public class RegisterFragment_treaty extends Fragment implements View.OnClickListener {

    private static final String TAG = "RegisterFragment";
    private Button buttonNext;
    private TextView textTreatyContent;
    private CheckBox checkBox_1, checkBox_2, checkBox_3;
    private OnButtonNextClickedListener mListener;
    public static final int PAGE_INDEX = -1;
    public static final int PAGE_INDEX2 = 0;
    private int currentPage;


    private static final String ARG_CURRENT_PAGE = "arg_current_page";

    /**
     * 用來建立Fragment
     */
    public static RegisterFragment_treaty newInstance(int currentPage) {
        RegisterFragment_treaty f = new RegisterFragment_treaty();

        Bundle args = new Bundle();
        args.putInt(ARG_CURRENT_PAGE, currentPage);
        f.setArguments(args);

        return f;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        currentPage = getArguments().getInt(ARG_CURRENT_PAGE);
    }

    public RegisterFragment_treaty() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view;
        // 如果目前顯示第二頁
        if(currentPage == PAGE_INDEX2){

            view = inflater.inflate(R.layout.fragment_register_treaty_page2, container, false);
            buttonNext = (Button) view.findViewById(R.id.button_next);
            LinearLayout buttonLogin = (LinearLayout) view.findViewById(R.id.button_login);
            buttonNext.setOnClickListener(this);
            buttonLogin.setOnClickListener(this);
            View headline = view.findViewById(R.id.headline);
            ((ActivityBase)getActivity()).setHeadline(headline, R.string.register_treaty_title_page2);
            textTreatyContent = (TextView) view.findViewById(R.id.text_treatycontent);
            textTreatyContent.setText(getResources().getText(R.string.register_treaty_content_page2));
            checkBox_1 = (CheckBox) view.findViewById(R.id.checkBox_treaty);
            checkBox_2 = (CheckBox) view.findViewById(R.id.checkBox_treaty_2);
            checkBox_3 = (CheckBox) view.findViewById(R.id.checkBox_treaty_3);
            checkBox_1.setOnClickListener(this);
            checkBox_2.setOnClickListener(this);
            checkBox_3.setOnClickListener(this);
            checkBox_1.setText(getResources().getText(R.string.register_treaty_checkbox_1_page2));
            checkBox_2.setText(getResources().getText(R.string.register_treaty_checkbox_2_page2));
            checkBox_3.setText(getResources().getText(R.string.register_treaty_checkbox_3_page2));
        }
        else
        {
            // Inflate the layout for this fragment
            view = inflater.inflate(R.layout.fragment_register_treaty, container, false);
            buttonNext = (Button) view.findViewById(R.id.button_next);
            LinearLayout buttonLogin = (LinearLayout) view.findViewById(R.id.button_login);
            buttonNext.setOnClickListener(this);
            buttonLogin.setOnClickListener(this);
            View headline = view.findViewById(R.id.headline);
            ((ActivityBase)getActivity()).setHeadline(headline, R.string.register_treaty_title);
            textTreatyContent = (TextView) view.findViewById(R.id.text_treatycontent);
            textTreatyContent.setMovementMethod(new ScrollingMovementMethod());

            checkBox_1 = (CheckBox) view.findViewById(R.id.checkBox_treaty_p1_1);
            checkBox_2 = (CheckBox) view.findViewById(R.id.checkBox_treaty_p1_2);
            checkBox_1.setOnClickListener(this);
            checkBox_2.setOnClickListener(this);
            checkBox_1.setText(getResources().getText(R.string.register_treaty_checkbox_1_page1));
            checkBox_2.setText(getResources().getText(R.string.register_treaty_checkbox_2_page1));
        }


        //def Disable
        buttonNext.setEnabled(false);
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        checkNextButtonEnable();
    }

    @Override
    public void onClick(View view){
        int viewId = view.getId();
        // 按下登入的處理
        if(viewId == R.id.button_login){
            // 切換至登入頁
            Intent intent = new Intent(getActivity(), LoginActivity.class);
            startActivity(intent);
        }else if(viewId == R.id.button_next){
            if (mListener != null) {
                // 如果目前在第二頁，指定下一頁為輸入註冊資料頁
                if(currentPage == PAGE_INDEX2) {
                    mListener.onButtonNextClicked(RegisterFragment.PAGE_INDEX);
                // 否則下一頁為條款第二頁
                }else{
                    mListener.onButtonNextClicked(PAGE_INDEX2);
                }
            }
        }else if(viewId == R.id.checkBox_treaty || viewId == R.id.checkBox_treaty_2 || viewId == R.id.checkBox_treaty_3 || viewId == R.id.checkBox_treaty_p1_1 || viewId == R.id.checkBox_treaty_p1_2){
            if(currentPage == PAGE_INDEX2)
            {
                if (checkBox_1.isChecked() && checkBox_2.isChecked() && checkBox_3.isChecked() ) {
                    buttonNext.setEnabled(true);
                } else {
                    buttonNext.setEnabled(false);
                }
            }
            else {
                if (checkBox_1.isChecked() && checkBox_2.isChecked()) {
                    buttonNext.setEnabled(true);
                } else {
                    buttonNext.setEnabled(false);
                }
            }
        }

    }

    /**
     * 檢查是否enable下一步按鈕
     */
    private void checkNextButtonEnable(){
   }


    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnButtonNextClickedListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }


    @Override
    public void onPause() {
        super.onPause();
        ((ActivityBase)getActivity()).hideKeyboard();
    }
}
