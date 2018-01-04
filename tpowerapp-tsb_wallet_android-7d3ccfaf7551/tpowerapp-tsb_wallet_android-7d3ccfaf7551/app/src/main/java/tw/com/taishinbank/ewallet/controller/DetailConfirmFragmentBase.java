package tw.com.taishinbank.ewallet.controller;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import java.util.List;

import tw.com.taishinbank.ewallet.R;
import tw.com.taishinbank.ewallet.model.LocalContact;

public class DetailConfirmFragmentBase extends Fragment {
    // -- View Hold --
    protected CheckBox checkBoxIsExpand;
    protected TextView textBlessing;
    protected TextView textLegend;
    protected Button buttonNext;
    protected TextView textTotalMoney;
    protected TextView textPeopleCount;
    protected RecyclerView recyclerView;
    protected RecyclerView.LayoutManager layoutManager;

    protected String message;
    protected List<LocalContact> friendList;

    protected static final String ARG_MESSAGE = "ARG_MESSAGE";
    protected static final String ARG_FRIEND_LIST = "ARG_FRIEND_LIST";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle args = getArguments();
        if(args != null) {
            message = args.getString(ARG_MESSAGE);
            friendList = args.getParcelableArrayList(ARG_FRIEND_LIST);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_detail_confirm_base, container, false);
        setViewHolder(view);
        return view;
    }

    protected void setViewHolder(View view){
        textBlessing = (TextView) view.findViewById(R.id.text_blessing);
        checkBoxIsExpand = (CheckBox) view.findViewById(R.id.checkbox_singleline);
        buttonNext = (Button) view.findViewById(R.id.button_next);

        recyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);
        // 設定layout manager
        layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);

        // 設定顯示的總金額
        textTotalMoney = (TextView) view.findViewById(R.id.text_total_money);

        // 設定總人數
        textPeopleCount = (TextView) view.findViewById(R.id.text_people_count);
        textLegend = (TextView) view.findViewById(R.id.text_legend);
    }

    protected void setViewContent() {
        // 設定訊息
        textBlessing.setText(message);

        // 根據訊息否為空，隱藏或顯示標示展開的圖示，訊息不為空才設定點擊事件處理
        if(TextUtils.isEmpty(message)) {
            checkBoxIsExpand.setVisibility(View.GONE);
        }else{
            checkBoxIsExpand.setOnCheckedChangeListener(checkedChangeListener);
            textBlessing.setOnClickListener(onTextMessageClickListener);
        }

        buttonNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onNextClick();
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    protected View.OnClickListener onTextMessageClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            // 折疊或打開訊息，變更按鈕樣式
            checkBoxIsExpand.setOnCheckedChangeListener(null);
            boolean becomeChecked = !checkBoxIsExpand.isChecked();
            textBlessing.setSingleLine(becomeChecked);
            checkBoxIsExpand.setChecked(becomeChecked);
            checkBoxIsExpand.setOnCheckedChangeListener(checkedChangeListener);
        }
    };

    protected CompoundButton.OnCheckedChangeListener checkedChangeListener = new CompoundButton.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            // 折疊或打開訊息
            textBlessing.setSingleLine(isChecked);
        }
    };

    protected void onNextClick() {

    }
}
