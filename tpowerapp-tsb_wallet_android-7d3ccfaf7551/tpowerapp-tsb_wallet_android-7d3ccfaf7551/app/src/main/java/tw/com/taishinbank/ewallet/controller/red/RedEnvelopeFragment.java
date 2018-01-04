package tw.com.taishinbank.ewallet.controller.red;


import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import org.json.JSONException;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import tw.com.taishinbank.ewallet.R;
import tw.com.taishinbank.ewallet.adapter.red.RedEnvelopeAdapter;
import tw.com.taishinbank.ewallet.controller.ActivityBase;
import tw.com.taishinbank.ewallet.controller.SVLoginActivity;
import tw.com.taishinbank.ewallet.controller.sv.SVAccountDetailActivity;
import tw.com.taishinbank.ewallet.imagehelper.ImageLoader;
import tw.com.taishinbank.ewallet.interfaces.ResponseListener;
import tw.com.taishinbank.ewallet.model.ResponseResult;
import tw.com.taishinbank.ewallet.model.SVAccountInfo;
import tw.com.taishinbank.ewallet.model.red.RedEnvelopeHomeListData;
import tw.com.taishinbank.ewallet.model.red.RedEnvelopeReceivedHeader;
import tw.com.taishinbank.ewallet.model.red.RedEnvelopeSentHeader;
import tw.com.taishinbank.ewallet.util.FormatUtil;
import tw.com.taishinbank.ewallet.util.NetworkUtil;
import tw.com.taishinbank.ewallet.util.PreferenceUtil;
import tw.com.taishinbank.ewallet.util.http.HttpUtilBase;
import tw.com.taishinbank.ewallet.util.http.RedEnvelopeHttpUtil;
import tw.com.taishinbank.ewallet.util.responsebody.RedEnvelopeResponseBodyUtil;

/**
 * 紅包首頁
 */
public class RedEnvelopeFragment extends Fragment implements View.OnClickListener, RedEnvelopeAdapter.OnClickedListener {

    private static final String TAG = "RedEnvelopeFragment";
    private static final int REQUEST_LOGIN_SV_FROM_SEND = SVLoginActivity.REQUEST_LOGIN_SV + 1;
    private ImageButton buttonLocker;
    private TextView textBalance;
    private RedEnvelopeAdapter adapter;
    private ImageView emptyView;
    private ImageView imagePhoto;
    private SVAccountInfo svAccountInfo;
    private ImageLoader imageLoader;
    private ImageLoader imageLoaderForList;

    public RedEnvelopeFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_red_envelope, container, false);

        Button buttonSend = (Button) view.findViewById(R.id.button_send);
        buttonSend.setOnClickListener(this);

        buttonLocker = (ImageButton) view.findViewById(R.id.button_locker);
        buttonLocker.setOnClickListener(this);
        ImageButton buttonSVInfo = (ImageButton) view.findViewById(R.id.button_sv_info);
        buttonSVInfo.setOnClickListener(this);

        textBalance = (TextView) view.findViewById(R.id.text_balance);

        emptyView = (ImageView) view.findViewById(R.id.empty_view);

        imagePhoto = (ImageView) view.findViewById(R.id.image_photo);

        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);
        // 設定layout manager
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);

        imageLoaderForList = new ImageLoader(getActivity(), getResources().getDimensionPixelSize(R.dimen.list_photo_size));
        adapter = new RedEnvelopeAdapter(getActivity(), imageLoaderForList);
        adapter.setOnClickedListener(this);
        adapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onChanged() {
                super.onChanged();
                // 確認是否顯示empty view
                checkIfListEmpty();
            }
        });
        recyclerView.setAdapter(adapter);
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                // Pause image loader to ensure smoother scrolling when flinging
                if (newState == RecyclerView.SCROLL_STATE_SETTLING) {
                    imageLoaderForList.setPauseWork(true);
                } else {
                    imageLoaderForList.setPauseWork(false);
                }
            }
        });


        final SwipeRefreshLayout swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipe_refresh_layout);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                // 停止refresh的動畫
                swipeRefreshLayout.setRefreshing(false);
                // 如果沒有網路連線，顯示提示對話框
                if (!NetworkUtil.isConnected(getActivity())) {
                    ((ActivityBase) getActivity()).showAlertDialog(getString(R.string.msg_no_available_network), android.R.string.ok, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    }, true);
                    return;
                }

                // 呼叫web service api 取得紅包列表
                try {
                    // TODO 改成真正的日期
                    RedEnvelopeHttpUtil.getRedEnvelopeHomePage("", "", responseListener, getActivity(), TAG);
                    ((ActivityBase) getActivity()).showProgressLoading();
                } catch (JSONException e) {
                    e.printStackTrace();
                    // TODO
                }
            }
        });

        // 確認是否顯示empty view
        checkIfListEmpty();


        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        ActivityBase activityBase = ((ActivityBase) getActivity());
        activityBase.setCenterTitle(R.string.title_red_envelope);
        activityBase.getWindow().setBackgroundDrawableResource(R.drawable.bg_fragment_red_home);

        loadPhoto();

        // 更新帳戶資訊如果登入過儲值帳戶且儲值帳戶登入時間還沒過期，隱藏鎖頭顯示餘額
        updateAccountInfo();
        adapter.setShowAmount(!needSVLogin());

        // 如果沒有網路連線，顯示提示對話框
        if(!NetworkUtil.isConnected(getActivity())){
            ((ActivityBase)getActivity()).showAlertDialog(getString(R.string.msg_no_available_network), android.R.string.ok, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            }, true);
            return ;
        }

        // 呼叫web service取得儲值帳戶資訊
        try {
            RedEnvelopeHttpUtil.getSVAccountInfo(responseListenerSV, activityBase, TAG);
            activityBase.showProgressLoading();
        } catch (JSONException e) {
            e.printStackTrace();
            // TODO
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        HttpUtilBase.cancelQueue(TAG);
        if(getActivity() != null)
            ((ActivityBase) getActivity()).dismissProgressLoading();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.fragment_red_envelope, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();
        // 我的紅包
        if(itemId == R.id.action_my_red_envelope){
            // 如果儲值帳戶登入時間沒有過期，也有儲值帳戶資訊，開啟我的紅包
            if(!needSVLogin()) {
                gotoFragment(new MyRedEnvelopeFragment(), true);
            }else{
                // 否則連到儲值帳戶登入頁
                startActivityForResult(new Intent(getActivity(), SVLoginActivity.class), SVLoginActivity.REQUEST_LOGIN_SV);
            }
            return true;

        // 聯絡人圖示
        } else if(itemId == android.R.id.home){
            getFragmentManager().popBackStack();
            // 開啟聯絡人列表
            //startActivity(new Intent(getActivity(), ContactListActivity.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {
        int viewId = v.getId();
        // 如果是發紅包按鈕
        if(viewId == R.id.button_send){
            // 如果儲值帳戶登入時間沒有過期，也有儲值帳戶資訊，開啟紅包類型選擇頁
            if(!needSVLogin()) {
                gotoFragment(RedEnvelopeTypeFragment.newInstance(svAccountInfo), true);
            }else{
                // 否則連到儲值帳戶登入頁
                startActivityForResult(new Intent(getActivity(), SVLoginActivity.class), REQUEST_LOGIN_SV_FROM_SEND);
            }
        // 如果是鎖頭按鈕
        }else if(viewId == R.id.button_locker){
            // 連到儲值帳戶登入頁
            startActivityForResult(new Intent(getActivity(), SVLoginActivity.class), SVLoginActivity.REQUEST_LOGIN_SV);

        // 如果是鎖頭旁的箭頭，開啟儲值帳戶詳情
        }else if(viewId == R.id.button_sv_info){
            // 如果需要登入儲值
            if(needSVLogin()) {
                // 連到儲值帳戶登入頁
                startActivityForResult(new Intent(getActivity(), SVLoginActivity.class), SVLoginActivity.REQUEST_LOGIN_SV);
                return ;
            }
            startActivity(new Intent(getActivity(), SVAccountDetailActivity.class));
        }
    }

    /**
     * 確認是否顯示empty view
     */
    private void checkIfListEmpty(){
        if(adapter.getItemCount() > 0){
            emptyView.setVisibility(View.GONE);
        }else{
            emptyView.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        // 判斷是否登入成功
        if(resultCode == Activity.RESULT_OK) {
            if (requestCode == REQUEST_LOGIN_SV_FROM_SEND) {
                gotoFragment(RedEnvelopeTypeFragment.newInstance(svAccountInfo), false);
            }
        }
    }

    private void updateAccountInfo() {
        svAccountInfo = PreferenceUtil.getSVAccountInfo(getActivity());
        if(svAccountInfo != null && !isSVLoginTimeExpired()) {
            buttonLocker.setVisibility(View.GONE);
            textBalance.setVisibility(View.VISIBLE);
            String formattedBalance = FormatUtil.toDecimalFormatFromString(svAccountInfo.getBalance(), true);
            textBalance.setText(formattedBalance);
        }else{
            buttonLocker.setVisibility(View.VISIBLE);
            textBalance.setVisibility(View.GONE);
        }
    }

    // 呼叫取得首頁紅包列表api的listener
    private ResponseListener responseListener = new ResponseListener() {

        @Override
        public void onResponse(ResponseResult result) {
            if(getActivity() == null)
                return;

            ((ActivityBase)getActivity()).dismissProgressLoading();
            // 如果returnCode是成功
            String returnCode = result.getReturnCode();
            if(returnCode.equals(ResponseResult.RESULT_SUCCESS)) {
                // 成功的話，更新畫面列表
                ArrayList<RedEnvelopeHomeListData> list = RedEnvelopeResponseBodyUtil.getRedEnvelopeHomeList(result.getBody());
                // 更新列表資料
                adapter.setShowAmount(!needSVLogin());
                adapter.setList(list);
            }else{
                // 執行預設的錯誤處理 
                handleResponseError(result, (ActivityBase) getActivity());
            }
        }
    };

    /**
     * 切換到指定的fragment
     */
    private void gotoFragment(Fragment fragment, boolean withAnimation){
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        if(withAnimation) {
            ft.setCustomAnimations(R.anim.in_right_to_left, R.anim.out_right_to_left, R.anim.in_left_to_right, R.anim.out_left_to_right);
        }
        ft.replace(android.R.id.tabcontent, fragment);
        ft.addToBackStack(null);
        ft.commit();
    }

    /**
     * 回傳距離上次登入儲值帳戶是否超過指定時間（10分鐘）
     */
    private boolean isSVLoginTimeExpired(){
        String svLoginTime = PreferenceUtil.getSVLoginTime(getActivity());
        if(!TextUtils.isEmpty(svLoginTime)){
            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
            try {
                // 將上次登入時間字串轉成date物件
                Date lastLoginTime = sdf.parse(svLoginTime);

                // 取得現在時間
                Calendar c = Calendar.getInstance();
                // 判斷是否已經超過上次登入時間10分鐘
                c.add(Calendar.MINUTE, -10);
                if(lastLoginTime.after(c.getTime())){
                    return false;
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        return true;
    }


    @Override
    public void onSentItemClicked(String txfSeq, String txfdSeq) {
        // 如果需要儲值登入
        if(needSVLogin()) {
            startActivityForResult(new Intent(getActivity(), SVLoginActivity.class), SVLoginActivity.REQUEST_LOGIN_SV);
            return ;
        }

        // 如果沒有網路連線，顯示提示對話框
        if (!NetworkUtil.isConnected(getActivity())){
            ((ActivityBase)getActivity()).showAlertDialog(getString(R.string.msg_no_available_network), android.R.string.ok, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            }, true);
            return ;
        }

        // 呼叫api查單筆，直接顯示該則紅包的發送詳情
        try {
            RedEnvelopeHttpUtil.getRedEnvelopeSend("", "", txfdSeq, txfSeq, responseListenerSent, getActivity(), TAG);
            ((ActivityBase)getActivity()).showProgressLoading();
        } catch (JSONException e) {
            e.printStackTrace();
            // TODO
        }
    }

    @Override
    public void onReceivedItemClicked(String txfSeq, String txfdSeq) {
        queryAndProcessReceivedData(txfSeq, txfdSeq, responseListenerReceivedByItemClick);
    }

    @Override
    public void onReplyClicked(RedEnvelopeHomeListData data) {
        queryAndProcessReceivedData(data.getTxfSeq(), data.getTxfdSeq(), responseListenerReceivedByReplyClick);
    }

    public void queryAndProcessReceivedData(String txfSeq, String txfdSeq, ResponseListener listener){
        // 如果需要儲值登入
        if(needSVLogin()) {
            startActivityForResult(new Intent(getActivity(), SVLoginActivity.class), SVLoginActivity.REQUEST_LOGIN_SV);
            return ;
        }

        // 如果沒有網路連線，顯示提示對話框
        if(!NetworkUtil.isConnected(getActivity())){
            ((ActivityBase)getActivity()).showAlertDialog(getString(R.string.msg_no_available_network), android.R.string.ok, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            }, true);
            return ;
        }

        // 呼叫api查單筆，直接顯示該則紅包的收到詳情
        try {
            RedEnvelopeHttpUtil.getRedEnvelopeReceived("", "", txfdSeq, txfSeq, listener, getActivity(), TAG);
            ((ActivityBase)getActivity()).showProgressLoading();
        } catch (JSONException e) {
            e.printStackTrace();
            // TODO
        }
    }

    // 呼叫查詢收到紅包api的listener
    private ResponseListener responseListenerReceivedByItemClick = new ReceivedResponseListener(false);
    private ResponseListener responseListenerReceivedByReplyClick = new ReceivedResponseListener(true);

    private class ReceivedResponseListener extends ResponseListener{
        private boolean isReplyClicked = false;
        public ReceivedResponseListener(boolean isReplyClicked){
            this.isReplyClicked = isReplyClicked;
        }

        @Override
        public void onResponse(ResponseResult result) {
            if(getActivity() == null)
                return;

            ((ActivityBase)getActivity()).dismissProgressLoading();
            String returnCode = result.getReturnCode();
            // 如果returnCode是成功
            if(returnCode.equals(ResponseResult.RESULT_SUCCESS)) {
                // 開啟收到的紅包詳情
                ArrayList<RedEnvelopeReceivedHeader> list = RedEnvelopeResponseBodyUtil.getRedEnvelopReceivedList(result.getBody());
                if(list != null && list.size() > 0) {
                    RedEnvelopeReceivedHeader item = list.get(0);
                    if(isReplyClicked){
                        // 開啟收到的紅包詳情
                        Intent intent = new Intent(getActivity(), ReceiveRedEnvelopeActivity.class);
                        intent.putExtra(ReceiveRedEnvelopeActivity.EXTRA_RECEIVED_HEADER, item);
                        startActivity(intent);
                    }else {
                        MyRedEnvelopeDetailFragment fragment = MyRedEnvelopeDetailFragment.newInstance(item);
                        gotoFragment(fragment, true);
                    }
                }else{
                    // TODO parse data 或 取資料有問題？
                }
            }else{
                // 執行預設的錯誤處理 
                handleResponseError(result, (ActivityBase) getActivity());
            }
        }
    }

    // 呼叫查詢發送紅包api的listener
    private ResponseListener responseListenerSent = new ResponseListener() {

        @Override
        public void onResponse(ResponseResult result) {
            if(getActivity() == null)
                return;

            ((ActivityBase)getActivity()).dismissProgressLoading();
            String returnCode = result.getReturnCode();
            // 如果returnCode是成功
            if(returnCode.equals(ResponseResult.RESULT_SUCCESS)) {
                // 開啟發送的紅包詳情
                ArrayList<RedEnvelopeSentHeader> list = RedEnvelopeResponseBodyUtil.getRedEnvelopSentList(result.getBody());
                if(list != null && list.size() > 0){
                    RedEnvelopeSentHeader item = list.get(0);
                    MyRedEnvelopeDetailFragment fragment = MyRedEnvelopeDetailFragment.newInstance(item);
                    gotoFragment(fragment, true);
                }else{
                    // TODO parse data 或 取資料有問題？
                }
            }else{
                // 執行預設的錯誤處理 
                handleResponseError(result, (ActivityBase) getActivity());
            }
        }
    };

    // 呼叫取得儲值帳戶資訊api的listener
    private ResponseListener responseListenerSV = new ResponseListener() {

        @Override
        public void onResponse(ResponseResult result) {
            if(getActivity() == null)
                return;

            ((ActivityBase)getActivity()).dismissProgressLoading();

            String returnCode = result.getReturnCode();

            // 如果returnCode是成功
            if(returnCode.equals(ResponseResult.RESULT_SUCCESS)) {
                // 儲值帳戶資訊存sharedpreference
                PreferenceUtil.setSVAccountInfo(getActivity(), result.getBody().toString());
                updateAccountInfo();

            // 跟iOS同步，如果是token或儲值帳戶的svToken過期，變更為鎖頭
            } else if(returnCode.equals(ResponseResult.RESULT_TOKEN_EXPIRED)
                    || returnCode.equals(ResponseResult.RESULT_SV_TOKEN_EXPIRED)){
                PreferenceUtil.setSVAccountInfo(getActivity(), null);
                updateAccountInfo();

            } else {
                // TODO 不成功的判斷與處理
            }

            // 呼叫web service api 取得紅包列表
            try {
                // TODO 改成真正的日期
                RedEnvelopeHttpUtil.getRedEnvelopeHomePage("", "", responseListener, getActivity(), TAG);
                ((ActivityBase)getActivity()).showProgressLoading();
            } catch (JSONException e) {
                e.printStackTrace();
                // TODO
            }
        }
    };

    private void loadPhoto()
    {
        if(imageLoader == null){
            imageLoader = new ImageLoader(getActivity(), getResources().getDimensionPixelSize(R.dimen.photo_size));
        }
        // 設定頭像
        imageLoader.loadImage(PreferenceUtil.getMemNO(this.getActivity()), imagePhoto);
    }

    private boolean needSVLogin(){
        svAccountInfo = PreferenceUtil.getSVAccountInfo(getActivity());
        return !(svAccountInfo != null && !isSVLoginTimeExpired());
    }

}
