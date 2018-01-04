package com.dbs.omni.tw.controller.result;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.dbs.omni.tw.R;
import com.dbs.omni.tw.controller.ActivityBase;
import com.dbs.omni.tw.element.SymbolItem;
import com.dbs.omni.tw.model.ShowTextData;

import java.util.ArrayList;

public class ResultPassFragment extends Fragment {

    private static final String TAG = "ResultPassFragment";
//    public static final String ARG_PAGE_TYPE = "ARG_PAGE_TYPE";
//    public static final String ARG_TITLE = "ARG_TITLE";
    public static final String ARG_TEXTUP = "ARG_TEXTUP";
    public static final String ARG_TEXTDOWN = "ARG_TEXTDOWN";
    public static final String ARG_TEXTBUTTON = "ARG_TEXTBUTTON";
    public static final String ARG_OBJECT = "ARG_OBJECT";

    // input type 1
    public static final String ARG_TITLE_ARRAY = "ARG_TITLE_ARRAY";
    public static final String ARG_CONTENT_ARRAY = "ARG_CONTENT_ARRAY";
    // input type 2
    public static final String ARG_CONTENT_HASMAP = "ARG_CONTENT_HASMAP";
    // 特殊case for 登出 更改密碼 加辦信用卡
    public static final String ARG_SPECIAL_CASE = "ARG_SPECIAL_CASE";
    public static final String IS_LOG_OUT = "IS_LOG_OUT";
    public static final String IS_MODIFY_PASSWORD = "IS_MODIFY_PASSWORD";
    public static final String IS_APPLY_CREDIT_CARD = "IS_APPLY_CREDIT_CARD";
    public static final String IS_MODIFY_PHONE = "IS_MODIFY_PHONE";
    public static final String IS_MODIFY_ADDRESS = "IS_MODIFY_ADDRESS";


    private Button btnFinished;
    private LayoutInflater inflater;
    private LinearLayout linearLayout;
    private String titleArray[];
    private String contentArray[];
    private ArrayList<ShowTextData> contents = new ArrayList<>();

    private OnModifyListener onModifyListener;

    public void setOnModifyListener(OnModifyListener listener) {
        this.onModifyListener = listener;
    }
    public interface OnModifyListener {
        void OnModifyPhone();
    }

    public interface OnResultPassListener {
        void OnEnd();
    }

    private OnResultPassListener onResultPassListener;
    public void setOnResultPassListener(OnResultPassListener listener) {
        this.onResultPassListener = listener;
    }


    public static ResultPassFragment newInstance(String textUp, String textDown, String textButton, ArrayList<ShowTextData> contents) {
        
        Bundle args = new Bundle();

//        args.putString(ARG_PAGE_TYPE, pageType.toString());
//        args.putString(ConfirmFragment.ARG_TITLE, title);
        args.putString(ARG_TEXTUP, textUp);
        args.putString(ARG_TEXTDOWN, textDown);
        args.putString(ARG_TEXTBUTTON, textButton);
        args.putSerializable(ARG_CONTENT_HASMAP, contents);

        ResultPassFragment fragment = new ResultPassFragment();
        fragment.setArguments(args);
        return fragment;
    }

    public static ResultPassFragment newInstance(String textUp, String textDown, String textButton, String[] titleArray, String[] contentArray) {

        Bundle args = new Bundle();

        args.putString(ARG_TEXTUP, textUp);
        args.putString(ARG_TEXTDOWN, textDown);
        args.putString(ARG_TEXTBUTTON, textButton);
        args.putStringArray(ARG_TITLE_ARRAY, titleArray);
        args.putStringArray(ARG_CONTENT_ARRAY, contentArray);

        ResultPassFragment fragment = new ResultPassFragment();
        fragment.setArguments(args);
        return fragment;
    }

    public static ResultPassFragment newInstance(String textUp, String textDown, String textButton, ArrayList<ShowTextData> contents, String specialCase) {

        Bundle args = new Bundle();

//        args.putString(ARG_PAGE_TYPE, pageType.toString());
//        args.putString(ConfirmFragment.ARG_TITLE, title);
        args.putString(ARG_TEXTUP, textUp);
        args.putString(ARG_TEXTDOWN, textDown);
        args.putString(ARG_TEXTBUTTON, textButton);
        args.putParcelableArrayList(ARG_CONTENT_HASMAP, contents);
        args.putString(ARG_SPECIAL_CASE, specialCase);

        ResultPassFragment fragment = new ResultPassFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ((ActivityBase) getActivity()).setHeadNoTransparentBackground();
        ((ActivityBase) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(false);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        this.inflater = inflater;
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_result_pass, container, false);
        init(view);

        return view;
    }

    //設定各項顯示與按鈕
    private void init(View view){
        Bundle arguments = getArguments();
//
//        if(arguments.containsKey(ARG_PAGE_TYPE)) {
//            pageType = CommonPageType.valueOf(arguments.getString(ARG_PAGE_TYPE));
//        }
//        centerTitle = arguments.getString(ARG_TITLE);

        //設定顯示結果訊息
        if(arguments.containsKey(ARG_TEXTUP)) {
            String textUp = arguments.getString(ARG_TEXTUP);
            TextView textView_up = (TextView) view.findViewById(R.id.textView_up);
            textView_up.setText(textUp);
        }
        if(arguments.containsKey(ARG_TEXTDOWN)) {
            String textDown = arguments.getString(ARG_TEXTDOWN);
            TextView textView_down = (TextView) view.findViewById(R.id.textView_down);
            textView_down.setText(textDown);
        }

        //設定顯示條目
        linearLayout = (LinearLayout) view.findViewById(R.id.linearLayout);

        if(arguments.containsKey(ARG_CONTENT_HASMAP)) {
            contents = (ArrayList<ShowTextData>) arguments.getSerializable(ARG_CONTENT_HASMAP);
            if(contents != null) {
                for (ShowTextData data : contents) {
                    addItemView(data);
                }
            }
        }

        //特殊case的畫面
        SymbolItem textView_special_case_message = (SymbolItem) view.findViewById(R.id.textView_special_case_message);

        if(arguments.containsKey(ARG_SPECIAL_CASE)){
            String strSpecialCase = arguments.getString(ARG_SPECIAL_CASE);

            //登出
            if (strSpecialCase.equals(IS_LOG_OUT)) {
                ImageView imageView_success = (ImageView) view.findViewById(R.id.imageView_success);
                imageView_success.setVisibility(View.GONE);
                textView_special_case_message.setVisibility(View.GONE);
            } else if(strSpecialCase.equals(IS_MODIFY_PASSWORD)) { //更改密碼
                textView_special_case_message.setTextContent(getString(R.string.textView_modify_password_message));
            } else if(strSpecialCase.equals(IS_APPLY_CREDIT_CARD)) { //加辦信用卡
                textView_special_case_message.setTextContent(getString(R.string.textView_apply_credit_card_message));

                toURLFormattedCallStyle(textView_special_case_message.getView(), textView_special_case_message.getTextContent());
            }else if(strSpecialCase.equals(IS_MODIFY_PHONE)) { //更改手機號碼
                textView_special_case_message.setTextContent(getString(R.string.textView_modify_phone_message));
            }else if(strSpecialCase.equals(IS_MODIFY_ADDRESS)) { //更改地址
                textView_special_case_message.setTextContent(getString(R.string.textView_modify_address_message));
            }


        }else{
            textView_special_case_message.setVisibility(View.GONE);
        }

        //完成按鈕
        btnFinished = (Button) view.findViewById(R.id.btnFinished);
        btnFinished.setOnClickListener(btnListener);
        if(arguments.containsKey(ARG_TEXTBUTTON)) {
            String textButton = arguments.getString(ARG_TEXTBUTTON);
            btnFinished.setText(textButton);
        }
    }


    //region add scroll view item的method
    private void addItemView(ShowTextData data){

        View itemView = inflater.inflate(R.layout.element_show_text, null);

        //設定title
        TextView textTitle = (TextView) itemView.findViewById(R.id.text_title);
        textTitle.setText(data.getTitle());

        //設定content
        TextView textContent = (TextView) itemView.findViewById(R.id.text_content);
        textContent.setText(data.getContent());

        //依照傳值的內容決定要不要show sub content
        if(!TextUtils.isEmpty(data.getSubContent())) {
            TextView textSubContent = (TextView) itemView.findViewById(R.id.text_sub_content);
            textSubContent.setText(data.getSubContent());
            textSubContent.setVisibility(View.VISIBLE);
        }

        linearLayout.addView(itemView);
    }
    //endregion

    //完成按鈕的function
    private Button.OnClickListener btnListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            onResultPassListener.OnEnd();
        }
    };

    private void toURLFormattedCallStyle(TextView view, String content) {

        ClickableSpan forgetAccountClickable = new ClickableSpan() {
            @Override
            public void onClick(View widget) {
                onModifyListener.OnModifyPhone();
            }
        };

        // 帳號
        SpannableString spannableString = new SpannableString(content);
        spannableString.setSpan(forgetAccountClickable,
                22,
                30,
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        spannableString.setSpan(new ForegroundColorSpan(Color.RED) ,
                22,
                30,
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        view.setHighlightColor(android.R.color.white);
        view.setText(spannableString);
        view.setMovementMethod(LinkMovementMethod.getInstance());
    }


//    private void geToPages() {
//        Intent intent;
//        switch (pageType) {
//            case REGISTER:
//                intent = getGoToHomeIntent(null, "");
//                break;
//            default:
//                intent = getGoToHomeIntent(MainActivity.EXTRA_GO_SETTING, "");
//                break;
//        }
//
//        startActivity(intent);
//    }

//    /**
//     * 開啟首頁
//     * @param extra 要包含的extra名稱，若無則傳null (需將EXTRA_SWITCH設起來)
//     */
//    private Intent getGoToHomeIntent(String extra, String value){
//        Intent intent = new Intent(this, MainActivity.class);
//        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//        if(TextUtils.isEmpty(extra)) {
//            intent.putExtra(MainActivity.EXTRA_SWITCH, true);
//            intent.putExtra(extra, value);
//        }
//        return intent;
//    }
}
