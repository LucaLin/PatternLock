package com.dbs.omni.tw.controller.result;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.dbs.omni.tw.R;
import com.dbs.omni.tw.controller.ActivityBase;
import com.dbs.omni.tw.element.SymbolItem;

public class ResultFailFragment extends Fragment {

    private static final String TAG = "ResultFailFragment";

//    public static final String ARG_TITLE = "ARG_TITLE";
    public static final String ARG_TEXTUP = "ARG_TEXTUP";
    public static final String ARG_TEXTDOWN = "ARG_TEXTDOWN";
    public static final String ARG_ERROR_MESSAGE = "ARG_ERROR_MESSAGE";
    private Button btnFinished;


    public interface OnResultFailListener {
        void OnFail();
    }

    private OnResultFailListener onResultFailListener;
    public void setOnResultFailListener(OnResultFailListener onResultFailListener) {
        this.onResultFailListener = onResultFailListener;
    }


    public static ResultFailFragment newInstance(String textUp, String textDown, String textErrorMsg) {

        Bundle args = new Bundle();
        args.putString(ARG_TEXTUP, textUp);
        args.putString(ARG_TEXTDOWN, textDown);
        args.putString(ARG_ERROR_MESSAGE, textErrorMsg);

        ResultFailFragment fragment = new ResultFailFragment();
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
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_result_fail, container, false);

        init(view);

        return view;
    }

    //設定各項顯示
    private void init(View view){

        Bundle arguments = getArguments();

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

        //設定server回傳的錯誤訊息
        if(arguments.containsKey(ARG_ERROR_MESSAGE)) {
            String errorMessage = arguments.getString(ARG_ERROR_MESSAGE);
            SymbolItem textView_error_message = (SymbolItem) view.findViewById(R.id.textView_error_message);
            textView_error_message.setTextContent(errorMessage);
        }

        //完成按鈕
        btnFinished = (Button) view.findViewById(R.id.btnFinished);
        btnFinished.setOnClickListener(btnListener);

    }

    //完成按鈕的function
    private Button.OnClickListener btnListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            onResultFailListener.OnFail();
//            Intent intent = getGoToHomeIntent(MainActivity.EXTRA_GO_SETTING, "");
//            startActivity(intent);
        }
    };

//    /**
//     * 開啟首頁
//     * @param extra 要包含的extra名稱，若無則傳null (需將EXTRA_SWITCH設起來)
//     */
//    private Intent getGoToHomeIntent(String extra, String value){
//        Intent intent = new Intent(this, MainActivity.class);
//        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//        if(extra != null) {
//            intent.putExtra(MainActivity.EXTRA_SWITCH, true);
//            intent.putExtra(extra, value);
//        }
//        return intent;
//    }
}
