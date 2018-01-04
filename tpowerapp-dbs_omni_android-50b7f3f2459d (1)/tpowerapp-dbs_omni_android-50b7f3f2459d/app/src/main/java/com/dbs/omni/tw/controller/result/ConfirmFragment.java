package com.dbs.omni.tw.controller.result;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.dbs.omni.tw.R;
import com.dbs.omni.tw.model.ShowTextData;

import java.util.ArrayList;

public class ConfirmFragment extends Fragment {

    public static final String TAG = "ConfirmFragment";

    public static final String ARG_PAGE_TYPE = "ARG_PAGE_TYPE";
//    public static final String ARG_TITLE = "ARG_TITLE";
    public static final String ARG_TEXTUP = "ARG_TEXTUP";
    public static final String ARG_TEXTDOWN = "ARG_TEXTDOWN";
    public static final String ARG_CONTENT_HASMAP = "ARG_CONTENT_HASMAP";
    public static final String ARG_OBJECT = "ARG_OBJECT";

    private OnConfirmListener onConfirmListener;
    public void setOnConfirmListener(OnConfirmListener listener) {
        this.onConfirmListener = listener;
    }

    public interface OnConfirmListener {
        void OnNext();
//        void OnFail();
    }

    private LayoutInflater inflater;

    private Button btnNext;
    private LinearLayout linearLayout;

    private ArrayList<ShowTextData> contents = new ArrayList<>();

    public static ConfirmFragment newInstance(String textUp, String textDown, ArrayList<ShowTextData> contents) {

        Bundle args = new Bundle();

//        args.putString(ARG_PAGE_TYPE, pageType.toString());
//        args.putString(ConfirmFragment.ARG_TITLE, title);
        args.putString(ARG_TEXTUP, textUp);
        args.putString(ARG_TEXTDOWN, textDown);
        args.putSerializable(ARG_CONTENT_HASMAP, contents);

        ConfirmFragment fragment = new ConfirmFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        this.inflater = inflater;
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_result_confirm, container, false);

        init(view);
        return view;
    }

    //設定各項顯示
    private void init(View view){
        Bundle arguments = getArguments();

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

            for (ShowTextData data : contents) {
                addItemView(data);
            }
        }

        //完成按鈕
        btnNext = (Button) view.findViewById(R.id.btnNextStep);
        btnNext.setOnClickListener(btnListener);

//        Button btnFail = (Button) view.findViewById(R.id.btnFail);
//        btnFail.setVisibility(View.GONE);
//        btnNext.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                onConfirmListener.OnFail();
//            }
//        });

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
            onConfirmListener.OnNext();
        }
    };
}
