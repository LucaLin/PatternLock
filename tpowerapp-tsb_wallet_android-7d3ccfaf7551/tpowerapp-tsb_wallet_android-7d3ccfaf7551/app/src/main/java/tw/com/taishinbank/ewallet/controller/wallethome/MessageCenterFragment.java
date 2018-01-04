package tw.com.taishinbank.ewallet.controller.wallethome;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import org.json.JSONException;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import tw.com.taishinbank.ewallet.R;
import tw.com.taishinbank.ewallet.adapter.wallethome.WalletHomeAdapter;
import tw.com.taishinbank.ewallet.controller.ActivityBase;
import tw.com.taishinbank.ewallet.controller.FriendHistoryActivity;
import tw.com.taishinbank.ewallet.gcm.WalletGcmListenerService;
import tw.com.taishinbank.ewallet.imagehelper.ImageLoader;
import tw.com.taishinbank.ewallet.interfaces.ResponseListener;
import tw.com.taishinbank.ewallet.interfaces.setting.PushSettingStatus;
import tw.com.taishinbank.ewallet.model.LocalContact;
import tw.com.taishinbank.ewallet.model.ResponseResult;
import tw.com.taishinbank.ewallet.model.SVAccountInfo;
import tw.com.taishinbank.ewallet.model.wallethome.WalletHomePushMsg;
import tw.com.taishinbank.ewallet.util.NetworkUtil;
import tw.com.taishinbank.ewallet.util.PreferenceUtil;
import tw.com.taishinbank.ewallet.util.PushMsgHelper;
import tw.com.taishinbank.ewallet.util.http.GeneralHttpUtil;
import tw.com.taishinbank.ewallet.util.http.HttpUtilBase;
import tw.com.taishinbank.ewallet.util.responsebody.GeneralResponseBodyUtil;

public class MessageCenterFragment extends Fragment implements WalletHomeAdapter.OnItemClickedListener {

    public static final String EXTRA_SWITCH_TO = "EXTRA_SWITCH_TO";
    public static final String EXTRA_FRIEND_MODE = "EXTRA_FRIEND_MODE";
    private static final String TAG = "MessageCenterFragment";

    // -- View Hold --
    private AlertDialog dlgMonthCriteria;
    private TextView btnMonthCriteria;
    private TabLayout tabLayout;

    // -- List View Adapter --
    private ArrayAdapter<String> adpaterMonthCriteria;

    // -- Data Model --
    private int idxMonthMenuItem = 0;

    private ImageLoader imageLoader;
    private WalletHomeAdapter adapter;

    // NOTE: 變更這裡的順序時，也要變更filterList()裡pushMsgListArray對應的index
    private PushSettingStatus[] tabItem = {PushSettingStatus.ALL, PushSettingStatus.SVACCOUNT,
            PushSettingStatus.RED, PushSettingStatus.PERFER_TICKETS, PushSettingStatus.SYSTEMINFO};
    private HttpUtilBase.MonthOption monthOption = HttpUtilBase.MonthOption.LATEST_1_MONTH;
    private List<ArrayList<WalletHomePushMsg>> pushMsgListArray;
    private int selectedTabIndex = 0;

    private SVAccountInfo svAccountInfo;
    private boolean IsFriendHistory = false;
    private LocalContact friendContact;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        if(getActivity() != null) {
            ((ActivityBase) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        if(getArguments() != null) {
            IsFriendHistory = getArguments().getBoolean(EXTRA_FRIEND_MODE, false);
            friendContact = getArguments().getParcelable(FriendHistoryActivity.EXTRA_FRIEND);
        }
        createMonthCriteriaAdapter();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_message_center, container, false);

        // 綁定TabLayout與ViewPager
        tabLayout = (TabLayout) view.findViewById(R.id.tablayout);

        for(int i = 0; i < tabItem.length; i++){
            TabLayout.Tab tab = tabLayout.newTab();
            tab.setText(tabItem[i].getDescription());
            tabLayout.addTab(tab);
        }

        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                selectedTabIndex = tab.getPosition();
                adapter.setList(pushMsgListArray.get(selectedTabIndex));
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);
        // 設定layout manager
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);

        imageLoader = new ImageLoader(getActivity(), getResources().getDimensionPixelSize(R.dimen.list_photo_size));
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

        adapter = new WalletHomeAdapter(getActivity(), imageLoader);
        adapter.setOnItemClickedListener(this);
        recyclerView.setAdapter(adapter);
        pushMsgListArray = new ArrayList<>(tabItem.length);
        for(int i = 0; i < tabItem.length; i++){
            pushMsgListArray.add(new ArrayList<WalletHomePushMsg>());
        }

        final SwipeRefreshLayout swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipe_refresh_layout);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                // 停止refresh的動畫
                swipeRefreshLayout.setRefreshing(false);
                updateList();
            }
        });

        // Create Dialog
        dlgMonthCriteria =  new AlertDialog.Builder(getContext())
            .setAdapter(adpaterMonthCriteria, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    onChangeMonthCriteria(which);
                    dialog.dismiss();
                }
            })
            .create();
        dlgMonthCriteria.setCancelable(true);
        dlgMonthCriteria.setCanceledOnTouchOutside(true);

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        // 設定置中的title
        ActivityBase activityBase = ((ActivityBase) getActivity());
        activityBase.setCenterTitle(R.string.wallethome_message_center);
        Toolbar toolbar = (Toolbar) activityBase.findViewById(R.id.toolbar);
        if (toolbar != null) {
            btnMonthCriteria = (TextView) toolbar.findViewById(R.id.custom_button_right);
            btnMonthCriteria.setVisibility(View.VISIBLE);
            btnMonthCriteria.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.btn_header_triangle, 0);
            btnMonthCriteria.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dlgMonthCriteria.show();
                }
            });
            updateMonthView();
        }

        if(adapter != null)
        {
            adapter.setShowAmount(!needSVLogin());
        }

        tabLayout.getTabAt(selectedTabIndex).select();
        updateList();
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        for (int i = 0; i < menu.size(); i++) {
            MenuItem menuItem = menu.getItem(i);
            // 返回上一頁
            if (menuItem.getItemId() == android.R.id.home) {
                menuItem.setVisible(true);
            } else {
                menuItem.setVisible(false);
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // 返回上一頁
        if (item.getItemId() == android.R.id.home){
            getFragmentManager().popBackStack();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    // ----
    // User interaction
    // ----

    public void onChangeMonthCriteria(int position) {
        idxMonthMenuItem = position;
        if(idxMonthMenuItem == HttpUtilBase.MonthOption.LATEST_2_MONTH.ordinal()){
            monthOption = HttpUtilBase.MonthOption.LATEST_2_MONTH;
        }else{
            monthOption = HttpUtilBase.MonthOption.LATEST_1_MONTH;
        }
        updateMonthView();
        updateList();
    }

    // ----
    // Http
    // ----

    private void updateList(){
        // 如果沒有網路連線，顯示提示對話框
        if(!NetworkUtil.isConnected(getActivity())){
            ((ActivityBase)getActivity()).showAlertDialog(getString(R.string.msg_no_available_network), android.R.string.ok, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            }, true);
        }else {
            // 呼叫api取得全部訊息
            try {
                if(IsFriendHistory)
                    GeneralHttpUtil.getMessageCenterListDataForFriend(friendContact.getMemNO(), monthOption, responseListener, getActivity(), TAG);
                else
                    GeneralHttpUtil.getMessageCenterListData(PushSettingStatus.ALL.getCode(), monthOption, responseListener, getActivity(), TAG);
                // TODO 改成正確日期與交易序號
                ((ActivityBase) getActivity()).showProgressLoading();
            } catch (JSONException e) {
                e.printStackTrace();
                // TODO
            }

        }
    }

    // ----
    // Private method
    // ----

    private void createMonthCriteriaAdapter() {
        adpaterMonthCriteria = new ArrayAdapter<>(getActivity(), android.R.layout.select_dialog_item);
        adpaterMonthCriteria.add(getString(R.string.sv_trx_latest_1_month));
        adpaterMonthCriteria.add(getString(R.string.sv_trx_latest_2_month));
    }

    private void updateMonthView() {
        if(adpaterMonthCriteria != null)
            btnMonthCriteria.setText(adpaterMonthCriteria.getItem(idxMonthMenuItem));
    }

    @Override
    public void onStop() {
        super.onStop();
        btnMonthCriteria.setVisibility(View.GONE);
        HttpUtilBase.cancelQueue(TAG);
        if(getActivity() != null)
            ((ActivityBase) getActivity()).dismissProgressLoading();
    }

    @Override
    public void onItemClicked(WalletHomePushMsg item) {
        PushMsgHelper helper = new PushMsgHelper((ActivityBase)getActivity(), TAG);
        helper.doActionAccordingUrl(item.getUrl());
    }

    // ----
    // Class, Interface, enum
    // ----

    // 呼叫取得全部訊息的api的listener
    private ResponseListener responseListener = new ResponseListener (){

        @Override
        public void onResponse(ResponseResult result) {
            if(getActivity() == null)
                return;

            String returnCode = result.getReturnCode();
            // 如果returnCode是成功
            if (returnCode.equals(ResponseResult.RESULT_SUCCESS)) {
                // 取得列表
                ArrayList<WalletHomePushMsg> list = GeneralResponseBodyUtil.getPushMsgList(result.getBody());
                // 清空原本所有列表
                for(int i = 0; i < pushMsgListArray.size(); i++){
                    pushMsgListArray.get(i).clear();
                }
                // 將取回的列表分類放入各類列表中
                for(int i = 0; i < list.size(); i++){
                    filterList(list.get(i));
                }
                pushMsgListArray.get(0).addAll(list);
                adapter.setShowAmount(!needSVLogin());
                adapter.setList(pushMsgListArray.get(selectedTabIndex));
                ((ActivityBase) getActivity()).dismissProgressLoading();
            } else {

                ((ActivityBase) getActivity()).dismissProgressLoading();
                // 執行預設的錯誤處理
                handleResponseError(result, (ActivityBase) getActivity());
            }
        }
    };

    private void filterList(WalletHomePushMsg item){
        String url = item.getUrl();
        //看是不是定義的type
        String urlAction;
        String urlData = null;
        int indexOfColon = url.indexOf(':');
        if(indexOfColon < 0){
            urlAction = url;
        }else{
            urlAction = url.substring(0, indexOfColon);
            urlData = url.substring(indexOfColon + 1);
        }
        Log.d(TAG, " Action is " + urlAction);
        Log.d(TAG, " Data is " + urlData);

        if (!isValidAction(urlAction)) {
            Log.d(TAG, " Action " + urlAction + " is not in our definition list.");
            return;
        }

        // 儲值帳戶
        if (urlAction.equals(WalletGcmListenerService.MyPayPushType.receive.name()) ||
                urlAction.equals(WalletGcmListenerService.MyPayPushType.outgoing.name()) ||
                urlAction.equals(WalletGcmListenerService.MyPayPushType.payreq.name()) ||
                urlAction.equals(WalletGcmListenerService.MyPayPushType.svin.name()) ||
                urlAction.equals(WalletGcmListenerService.MyPayPushType.svout.name())) {
            pushMsgListArray.get(1).add(item);

        // 紅包
        } else if (urlAction.equals(WalletGcmListenerService.MyPayPushType.redhome.name()) ||
                urlAction.equals(WalletGcmListenerService.MyPayPushType.redrecordin.name()) ||
                urlAction.equals(WalletGcmListenerService.MyPayPushType.redrecordout.name())) {
            pushMsgListArray.get(2).add(item);

        // 優惠券
        } else if (urlAction.equals(WalletGcmListenerService.MyPayPushType.coupon.name())) {
            pushMsgListArray.get(3).add(item);
        // 系統訊息
        } else if (urlAction.equals(WalletGcmListenerService.MyPayPushType.nc.name())) {
            pushMsgListArray.get(4).add(item);
        }
    }

    public boolean isValidAction(String urlAction){
        boolean hasInDefined = false;
        for (WalletGcmListenerService.MyPayPushType type : WalletGcmListenerService.MyPayPushType.values()) {
            if (urlAction.equals(type.name())) {
                hasInDefined = true;
                break;
            }
        }
        return hasInDefined;
    }

    private boolean needSVLogin(){
        svAccountInfo = PreferenceUtil.getSVAccountInfo(getActivity());
        return !(svAccountInfo != null && !isSVLoginTimeExpired());
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
}
