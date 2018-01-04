package tw.com.taishinbank.ewallet.controller.red;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import org.json.JSONException;

import java.util.ArrayList;

import tw.com.taishinbank.ewallet.R;
import tw.com.taishinbank.ewallet.adapter.red.MyRedEnvelopeAdapter;
import tw.com.taishinbank.ewallet.adapter.red.MyRedEnvelopeReceivedAdapter;
import tw.com.taishinbank.ewallet.adapter.red.MyRedEnvelopeSentAdapter;
import tw.com.taishinbank.ewallet.controller.ActivityBase;
import tw.com.taishinbank.ewallet.imagehelper.ImageLoader;
import tw.com.taishinbank.ewallet.interfaces.ResponseListener;
import tw.com.taishinbank.ewallet.model.ResponseResult;
import tw.com.taishinbank.ewallet.model.red.RedEnvelopeReceivedHeader;
import tw.com.taishinbank.ewallet.model.red.RedEnvelopeSentHeader;
import tw.com.taishinbank.ewallet.util.NetworkUtil;
import tw.com.taishinbank.ewallet.util.http.HttpUtilBase;
import tw.com.taishinbank.ewallet.util.http.RedEnvelopeHttpUtil;
import tw.com.taishinbank.ewallet.util.responsebody.RedEnvelopeResponseBodyUtil;

public class MyRedEnvelopeFragment extends Fragment {

    private static final String TAG = "MyRedEnvelopeFragment";

    private RecyclerView recyclerView;
    private LinearLayoutManager layoutManager;
    private RadioGroup radioGroup;
    private RadioButton radioButtonReceived;
    private RadioButton radioButtonSent;
    private ImageLoader imageLoader;
    private ArrayList<RedEnvelopeReceivedHeader> receivedSelfList = new ArrayList<>();
    private ArrayList<RedEnvelopeReceivedHeader> receivedDetailList = new ArrayList<>();
    private ArrayList<RedEnvelopeSentHeader> sentList = new ArrayList<>();

    public static final int TAB_RECEIVED = 1;
    public static final int TAB_SENT = 2;
    public static final String EXTRA_DEFAULT_TAB = "default_tab";
    private int defaultTab = TAB_RECEIVED;

    public static MyRedEnvelopeFragment newInstance(int defaultTab) {
        Bundle args = new Bundle();
        args.putInt(EXTRA_DEFAULT_TAB, defaultTab);
        MyRedEnvelopeFragment fragment = new MyRedEnvelopeFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        if(getActivity() != null) {
            ((ActivityBase) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        if(getArguments() != null){
            defaultTab = getArguments().getInt(EXTRA_DEFAULT_TAB, TAB_RECEIVED);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_my_envelope, container, false);
        recyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);
        // 設定layout manager
        layoutManager = new LinearLayoutManager(getActivity());
        imageLoader = new ImageLoader(getActivity(), getResources().getDimensionPixelSize(R.dimen.list_photo_size));
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                // Pause image loader to ensure smoother scrolling when flinging
                if (newState == RecyclerView.SCROLL_STATE_SETTLING) {
                    imageLoader.setPauseWork(true);
                } else {
                    imageLoader.setPauseWork(false);
                }
            }
        });

        radioGroup = (RadioGroup) view.findViewById(R.id.radio_group);
        radioButtonReceived = (RadioButton) view.findViewById(R.id.radio_button_received);
        radioButtonSent = (RadioButton) view.findViewById(R.id.radio_button_sent);

        if(defaultTab == TAB_SENT){
            radioGroup.check(R.id.radio_button_sent);
        }else{
            radioGroup.check(R.id.radio_button_received);
        }

        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                updateListWithCheckedType(checkedId);
            }
        });


        // 如果沒有網路連線，顯示提示對話框
        if(!NetworkUtil.isConnected(getActivity())){
            ((ActivityBase)getActivity()).showAlertDialog(getString(R.string.msg_no_available_network), android.R.string.ok, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            }, true);
        }else {
            // 呼叫api取得收到的紅包
            try {
                // TODO 改成正確日期與交易序號
                RedEnvelopeHttpUtil.getRedEnvelopeReceived("", "", "", "", responseListenerReceived, getActivity(), TAG);
                ((ActivityBase) getActivity()).showProgressLoading();
            } catch (JSONException e) {
                e.printStackTrace();
                // TODO
            }

        }

        // 初始tab文字
        String formattedTitle = String.format(getString(R.string.received_red_envelope), receivedSelfList.size());
        radioButtonReceived.setText(formattedTitle);
        formattedTitle = String.format(getString(R.string.sent_red_envelope), sentList.size());
        radioButtonSent.setText(formattedTitle);

        updateListWithCheckedType(radioGroup.getCheckedRadioButtonId());
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        // 設定置中的title
        ActivityBase activityBase = ((ActivityBase) getActivity());
        activityBase.setCenterTitle(R.string.my_red_envelope);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // 返回上一頁
        if(item.getItemId() == android.R.id.home){
            getFragmentManager().popBackStack();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * 根據目前選中的類型，更新tab背景與列表
     */
    private void updateListWithCheckedType(int checkedRadioButtonId){
        // 目前選擇的是收到的紅包
        if(checkedRadioButtonId == R.id.radio_button_received){
            radioButtonReceived.setBackgroundResource(R.drawable.tab_history_p);
            radioButtonSent.setBackgroundDrawable(null);
            final MyRedEnvelopeReceivedAdapter adapter = new MyRedEnvelopeReceivedAdapter(receivedSelfList, receivedDetailList, imageLoader);
            adapter.setOnItemClickedListener(
                    new MyRedEnvelopeAdapter.OnItemClickedListener() {
                        @Override
                        public void onItemClicked(int position) {
                            RedEnvelopeReceivedHeader item = adapter.getItem(position);
                            MyRedEnvelopeDetailFragment fragment = MyRedEnvelopeDetailFragment.newInstance(item);
                            gotoFragment(fragment);
                        }
                    });
            recyclerView.setAdapter(adapter);

        // 目前選擇的是發送的紅包
        }else{
            radioButtonSent.setBackgroundResource(R.drawable.tab_history_p_right);
            radioButtonReceived.setBackgroundDrawable(null);
            final MyRedEnvelopeSentAdapter adapter = new MyRedEnvelopeSentAdapter(sentList, imageLoader);
            adapter.setOnItemClickedListener(
                    new MyRedEnvelopeAdapter.OnItemClickedListener() {
                        @Override
                        public void onItemClicked(int position) {
                            RedEnvelopeSentHeader item = adapter.getItem(position);
                            MyRedEnvelopeDetailFragment fragment = MyRedEnvelopeDetailFragment.newInstance(item);
                            gotoFragment(fragment);
                        }
                    });
            recyclerView.setAdapter(adapter);
        }
    }

    // 呼叫收到紅包api的listener
    private ResponseListener responseListenerReceived = new ResponseListener() {

        @Override
        public void onResponse(ResponseResult result) {
            if(getActivity() == null)
                return;

            ((ActivityBase) getActivity()).dismissProgressLoading();
            String returnCode = result.getReturnCode();
            // 如果returnCode是成功
            if (returnCode.equals(ResponseResult.RESULT_SUCCESS)) {
                // 取得列表
                receivedSelfList = RedEnvelopeResponseBodyUtil.getRedEnvelopReceivedSelfList(result.getBody());
                receivedDetailList = RedEnvelopeResponseBodyUtil.getRedEnvelopReceivedList(result.getBody());

                // 設定tab顯示文字
                if(receivedSelfList != null){
                    String formattedTitle = String.format(getString(R.string.received_red_envelope), receivedSelfList.size());
                    radioButtonReceived.setText(formattedTitle);
                }

                // 更新列表
                updateListWithCheckedType(radioGroup.getCheckedRadioButtonId());
            } else {
                // 如果是共同error，不繼續呼叫另一個api
                if(handleCommonError(result, (ActivityBase) getActivity())){
                    return;
                }else{
                    showAlert((ActivityBase) getActivity(), result.getReturnMessage());
                }
            }

            // 呼叫api取得發送的紅包清單
            try {
                // TODO 改成正確日期與交易序號
                RedEnvelopeHttpUtil.getRedEnvelopeSend("", "", "", "", responseListenerSent, getActivity(), TAG);
                ((ActivityBase) getActivity()).showProgressLoading();
            } catch (JSONException e) {
                e.printStackTrace();
                // TODO
            }
        }
    };

    // 呼叫發出紅包api的listener
    private ResponseListener responseListenerSent = new ResponseListener() {

        @Override
        public void onResponse(ResponseResult result) {
            if(getActivity() == null)
                return;

            ((ActivityBase) getActivity()).dismissProgressLoading();
            String returnCode = result.getReturnCode();
            // 如果returnCode是成功
            if (returnCode.equals(ResponseResult.RESULT_SUCCESS)) {
                // 取得列表
                sentList = RedEnvelopeResponseBodyUtil.getRedEnvelopSentList(result.getBody());

                // 設定tab顯示文字
                if(sentList != null){
                    String formattedTitle = String.format(getString(R.string.sent_red_envelope), sentList.size());
                    radioButtonSent.setText(formattedTitle);
                }

                // 更新列表
                updateListWithCheckedType(radioGroup.getCheckedRadioButtonId());
            } else {
                // 執行預設的錯誤處理 
                handleResponseError(result, (ActivityBase) getActivity());
            }
        }
    };

    private void gotoFragment(Fragment fragment){
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.setCustomAnimations(R.anim.in_right_to_left, R.anim.out_right_to_left, R.anim.in_left_to_right, R.anim.out_left_to_right);
        ft.replace(android.R.id.tabcontent, fragment);
        ft.addToBackStack(null);
        ft.commit();
    }

    @Override
    public void onStop() {
        super.onStop();
        imageLoader.setPauseWork(false);
        HttpUtilBase.cancelQueue(TAG);
        if(getActivity() != null)
            ((ActivityBase) getActivity()).dismissProgressLoading();
    }
}
