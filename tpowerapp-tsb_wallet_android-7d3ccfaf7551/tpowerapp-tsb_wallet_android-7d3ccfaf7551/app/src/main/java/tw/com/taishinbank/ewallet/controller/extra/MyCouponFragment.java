package tw.com.taishinbank.ewallet.controller.extra;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import org.json.JSONException;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import tw.com.taishinbank.ewallet.R;
import tw.com.taishinbank.ewallet.adapter.extra.MyCouponAdapter;
import tw.com.taishinbank.ewallet.controller.ActivityBase;
import tw.com.taishinbank.ewallet.interfaces.CouponType;
import tw.com.taishinbank.ewallet.interfaces.ResponseListener;
import tw.com.taishinbank.ewallet.model.ResponseResult;
import tw.com.taishinbank.ewallet.model.extra.Coupon;
import tw.com.taishinbank.ewallet.util.ContactUtil;
import tw.com.taishinbank.ewallet.util.NetworkUtil;
import tw.com.taishinbank.ewallet.util.http.ExtraHttpUtil;
import tw.com.taishinbank.ewallet.util.http.GeneralHttpUtil;
import tw.com.taishinbank.ewallet.util.http.HttpUtilBase;
import tw.com.taishinbank.ewallet.util.http.event.DownloadEvent;
import tw.com.taishinbank.ewallet.util.responsebody.ExtraResponseBodyUtil;

public class MyCouponFragment extends Fragment {

    private static final String TAG = "MyCouponFragment";
    public static final String EXTRA_SWITCH_TO = "EXTRA_SWITCH_TO";

    public static final int COUPON_UNUSED = 0;
    public static final int COUPON_USED = 1;

    // -- View Hold --
    private TextView txtEmptyView;
    private ListView lstMyCoupon;
    private FrameLayout btnUnused;
    private FrameLayout btnUsed;
    private TextView txtUnused;
    private TextView txtUsed;
    private ImageView imgUnusedNew, imgUsedNew;

    // -- List View Adapter --
    private MyCouponAdapter adapterCoupon;

    // -- Data Model --
    private int idxSwitchList = COUPON_UNUSED;
    private boolean isEditing = false;

    private List<Coupon> listCouponUnused;
    private List<Coupon> listCouponUsed;
    private ArrayList<String> downloadlist;
    private int downloadIndex = 0;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Log.d("CouponList", "onCreate()");

        setHasOptionsMenu(true);
        if(getActivity() != null) {
            ((ActivityBase) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        if (getArguments() != null)
            idxSwitchList = getArguments().getInt(EXTRA_SWITCH_TO, COUPON_UNUSED);

//        createSample();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.d("CouponList", "onCreateView()");

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_extra_my_coupon, container, false);

        // Set View Hold
        txtEmptyView = (TextView) view.findViewById(R.id.txt_empty_view);
        lstMyCoupon = (ListView) view.findViewById(R.id.lst_my_coupon);
        btnUnused = (FrameLayout) view.findViewById(R.id.btn_unused);
        btnUsed = (FrameLayout) view.findViewById(R.id.btn_used);
        txtUnused = (TextView) view.findViewById(R.id.txt_unused);
        txtUsed = (TextView) view.findViewById(R.id.txt_used);
        imgUnusedNew = (ImageView) view.findViewById(R.id.img_unused_new);
        imgUsedNew = (ImageView) view.findViewById(R.id.img_used_new);

        // List Adpater
        adapterCoupon = new MyCouponAdapter(getContext());

        // Set Listener
        btnUnused.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switchToUnused();
            }
        });
        btnUsed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switchToUsed();
            }
        });
        lstMyCoupon.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                onListItemClick(position);
            }
        });

        // Default: show the expend transaction...
        updateViewBySwitch();

        // Load the Data
        inquiryList();
        updateListView(); //TODO should process if after mock

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();

        Log.d("CouponList", "onResume()");
    }

    @Override
    public void onPause()
    {
        super.onPause();
    }

    @Override
    public void onStop()
    {
        super.onStop();
        GeneralHttpUtil.stopDownloadCoupon();
        HttpUtilBase.cancelQueue(TAG);
        if(getActivity() != null)
            ((ActivityBase) getActivity()).dismissProgressLoading();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        Log.d("CouponList", "onDestroyView()");

        if (getActivity() != null)
            ((ActivityBase) getActivity()).dismissProgressLoading();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
//        MenuItem menuItem = menu.add(0, android.R.id.edit, 3, R.string.edit);
//        menuItem.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
    }

//    @Override
//    public void onPrepareOptionsMenu(Menu menu) {
//        for (int i = 0; i < menu.size(); i++) {
//            MenuItem menuItem = menu.getItem(i);
//            // 返回上一頁
//            if (menuItem.getItemId() == android.R.id.home) {
//                menuItem.setVisible(true);
//            } else if (menuItem.getItemId() == R.id.action_history_interval) {
//                menuItem.setIcon(R.drawable._btn_red_text_down);
//                menuItem.setShowAsAction(MenuItem.SHOW_AS_ACTION_WITH_TEXT);
//                menuItem.setVisible(true);
//            } else {
//                menuItem.setVisible(false);
//            }
//        }
//    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // 返回上一頁
        if (item.getItemId() == android.R.id.home){
            getFragmentManager().popBackStack();
            return true;
        } else if (item.getItemId() == android.R.id.edit) {
            isEditing = !isEditing;

            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    // ----
    // User interaction
    // ----
    public void switchToUnused() {
        idxSwitchList = COUPON_UNUSED;
        updateViewBySwitch();
        updateListView();
    }

    public void switchToUsed() {
        idxSwitchList = COUPON_USED;
        updateViewBySwitch();
        updateListView();
    }

    public void onListItemClick(int position) {
        Coupon coupon;
        if (idxSwitchList == COUPON_USED) {
            coupon = listCouponUsed.get(position);
        } else {
            coupon = listCouponUnused.get(position);
        }

        ((MyCouponActivity) getActivity()).gotoDetail(coupon);
    }

    // ----
    // Http
    // ----
    private void inquiryList() {
        // 如果沒有網路連線，顯示提示對話框
        if (!NetworkUtil.isConnected(getActivity())) {
            ((ActivityBase)getActivity()).showAlertDialog(getString(R.string.msg_no_available_network), android.R.string.ok, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            }, true);
        } else {
            try {
                ExtraHttpUtil.queryUnusedCoupon(responseListenerUnused, getActivity(), TAG);
                ((ActivityBase) getActivity()).showProgressLoading();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    // 呼叫收到紅包api的listener
    private ResponseListener responseListenerUnused = new ResponseListener() {

        @Override
        public void onResponse(ResponseResult result) {
            if (getActivity() == null)
                return;

            ((ActivityBase) getActivity()).dismissProgressLoading();
            String returnCode = result.getReturnCode();

            // 如果returnCode是成功
            if (returnCode.equals(ResponseResult.RESULT_SUCCESS)) {
                // 取得列表
                listCouponUnused = ExtraResponseBodyUtil.parseToCouponList(result.getBody());

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
                ExtraHttpUtil.queryUsedCoupon(responseListenerUsed, getActivity(), TAG);
                ((ActivityBase) getActivity()).showProgressLoading();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    };

    // 呼叫發出紅包api的listener
    private ResponseListener responseListenerUsed = new ResponseListener() {

        @Override
        public void onResponse(ResponseResult result) {
            if (getActivity() == null)
                return;

            ((ActivityBase) getActivity()).dismissProgressLoading();
            String returnCode = result.getReturnCode();

            // 如果returnCode是成功
            if (returnCode.equals(ResponseResult.RESULT_SUCCESS)) {
                // 取得列表
                listCouponUsed = ExtraResponseBodyUtil.parseToCouponList(result.getBody());
                if(lstMyCoupon.getEmptyView() == null) {
                    lstMyCoupon.setEmptyView(txtEmptyView);
                }
                updateListView();

            } else {
                // 執行預設的錯誤處理 
                handleResponseError(result, (ActivityBase) getActivity());
            }
        }
    };

    private void createSample() {
        //Create listCouponUnused sample
        listCouponUnused = new ArrayList<>(); {
            for (int i = 0; i < 13; i++) {
                Coupon coupon = new Coupon();
                if (i % 2 == 0)
                    coupon.setStatus(CouponType.ACT.code);
                else
                    coupon.setStatus(CouponType.RECEIVED.code);

                coupon.setContent("優惠券優惠內容。一二三四五六七八。" + i);
                coupon.setTitle("玉米濃湯" + i);
                coupon.setSubTitle("好幾碗" + i);
                if (i % 3 == 0) {
                    coupon.setReplyMessage(null);
                    coupon.setToMemNickName(null);
                    coupon.setReplyDate(null);
                } else {
                    coupon.setReplyMessage("Receiver Message " + i);
                    coupon.setToMemNickName("Receiver Name " + i);
                    coupon.setReplyDate("Receiver Time " + i);
                }

                if (i % 6 == 0) {
                    coupon.setSenderMessage(null);
                    coupon.setSenderNickName(null);
                    coupon.setSenderDate(null);
                } else {
                    coupon.setSenderMessage("Sender Message " + i);
                    coupon.setSenderNickName("Sender Name " + i);
                    coupon.setSenderDate("Sender Time " + i);
                }

                coupon.setStoreName("濃濃美食" + i);
                coupon.setStoreAddress("美食街２１２號１００F - " + i);
                coupon.setStorePhone("(02)2263004" + (i % 10));

                coupon.setCreateDate("2016020" + (i % 10) + "140315");
                coupon.setStartDate("2016030" + (i % 10) + "160715");
                coupon.setEndDate("2016040" + (i % 10) + "094315");
                coupon.setSerialNO("UNUSED-ABZN123456789" + i);

                listCouponUnused.add(coupon);
            }
        }

        //Create listCouponUsed sample
        listCouponUsed = new ArrayList<>(); {
            for (int i = 0; i < 16; i++) {
                Coupon coupon = new Coupon();
                if (i % 2 == 0)
                    coupon.setStatus(CouponType.SENT.code);
                else
                    coupon.setStatus(CouponType.TRADED.code);

                coupon.setContent("優惠券優惠內容。一二三四五六七八。" + i);
                coupon.setTitle("最後邊的玉米濃湯" + i);
                coupon.setSubTitle(i + "幾碗");
                if ((i + 1) % 3 == 0) {
                    coupon.setReplyMessage(null);
                    coupon.setToMemNickName(null);
                    coupon.setReplyDate(null);
                } else {
                    coupon.setReplyMessage("Receiver Message " + i);
                    coupon.setToMemNickName("Receiver Name " + i);
                    coupon.setReplyDate("2016010" + (i % 10) + "140321");
                }

                if ((i + 1) % 6 == 0) {
                    coupon.setSenderMessage(null);
                    coupon.setSenderNickName(null);
                    coupon.setSenderDate(null);
                } else {
                    coupon.setSenderMessage("Sender Message " + i);
                    coupon.setSenderNickName("Sender Name " + i);
                    coupon.setSenderDate("2016010" + (i % 10) + "080903");
                }

                coupon.setStoreName("濃濃美-IMF" + i);
                coupon.setStoreAddress("美食街２１２號88F - " + i);
                coupon.setStorePhone("(02)2263004" + (i % 10));
                coupon.setCreateDate("2016020" + (i % 10) + "170321");
                coupon.setStartDate("2016030" + (i % 10) + "200321");
                coupon.setEndDate("2016040" + (i % 10) + "231521");
                coupon.setSerialNO("USED-ABZN123456789" + i);

                listCouponUsed.add(coupon);
            }
        }
    }

    // ----
    // Private method
    // ----

    private void updateListView() {
        checkCouponImage();
        if (idxSwitchList == COUPON_UNUSED) {
            adapterCoupon.setCouponList(listCouponUnused);
        } else {
            adapterCoupon.setCouponList(listCouponUsed);
        }
        adapterCoupon.notifyDataSetChanged();

    }

    private void updateViewBySwitch() {
        if (idxSwitchList == COUPON_UNUSED) {
            btnUnused.setBackgroundResource(R.drawable.tab_history_p);
            btnUsed.setBackgroundDrawable(null);
            idxSwitchList = COUPON_UNUSED;
            lstMyCoupon.setAdapter(adapterCoupon);
            txtUnused.setTextColor(getResources().getColor(android.R.color.white));
            txtUsed.setTextColor(getResources().getColor(R.color.radio_tab_text));
        } else {
            btnUnused.setBackgroundDrawable(null);
            btnUsed.setBackgroundResource(R.drawable.tab_history_p_right);
            idxSwitchList = COUPON_USED;
            lstMyCoupon.setAdapter(adapterCoupon);
            txtUnused.setTextColor(getResources().getColor(R.color.radio_tab_text));
            txtUsed.setTextColor(getResources().getColor(android.R.color.white));
        }
    }


    private void checkCouponImage()
    {
        downloadlist = new ArrayList<>();

        File imgFile;
        if(listCouponUnused != null) {
            imgUnusedNew.setVisibility(View.GONE);
            for (Coupon coupon : listCouponUnused) {
                if (coupon.getReadFlag().equals("0")) {
                    imgUnusedNew.setVisibility(View.VISIBLE);
                }

                switch (((MyCouponActivity) getActivity()).imageSize.toString())
                {
                    case "LARGE":

                        if(!TextUtils.isEmpty(coupon.getImagePathL())){
                            imgFile = new File(ContactUtil.FolderPath + File.separator + coupon.getImagePathL());

                            if(!imgFile.exists() && !hasExistList(coupon.getImagePathL())) {
                                downloadlist.add(coupon.getImagePathL());
                        }}
                        break;

                    case "MEDIUM":
                        if(!TextUtils.isEmpty(coupon.getImagePathM())){
                            imgFile = new File(ContactUtil.FolderPath + File.separator + coupon.getImagePathM());
                            if(!imgFile.exists() && !hasExistList(coupon.getImagePathM())) {
                                downloadlist.add(coupon.getImagePathM());
                        }}
                        break;

                    case "SMALL":
                        if(!TextUtils.isEmpty(coupon.getImagePathS())){
                            imgFile = new File(ContactUtil.FolderPath + File.separator + coupon.getImagePathS());
                            if(!imgFile.exists() && !hasExistList(coupon.getImagePathS())) {
                                downloadlist.add(coupon.getImagePathS());
                        }}
                        break;

                }
            }
        }
        if(listCouponUsed != null) {
            imgUsedNew.setVisibility(View.GONE);
            for (Coupon coupon : listCouponUsed) {
                if (coupon.getReadFlag().equals("0")) {
                    imgUsedNew.setVisibility(View.VISIBLE);
                }

                switch (((MyCouponActivity) getActivity()).imageSize.toString())
                {
                    case "LARGE":
                        if(!TextUtils.isEmpty(coupon.getImagePathL())){
                            imgFile = new File(ContactUtil.FolderPath + File.separator + coupon.getImagePathL());
                            if(!imgFile.exists() && !hasExistList(coupon.getImagePathL())) {
                                downloadlist.add(coupon.getImagePathL());
                        }}
                        break;

                    case "MEDIUM":
                        if(!TextUtils.isEmpty(coupon.getImagePathM())){
                            imgFile = new File(ContactUtil.FolderPath + File.separator + coupon.getImagePathM());
                            if(!imgFile.exists() && !hasExistList(coupon.getImagePathM())) {
                                downloadlist.add(coupon.getImagePathM());
                        }}
                        break;

                    case "SMALL":
                        if(!TextUtils.isEmpty(coupon.getImagePathS())){
                            imgFile = new File(ContactUtil.FolderPath + File.separator + coupon.getImagePathS());
                            if(!imgFile.exists() && !hasExistList(coupon.getImagePathS())) {
                                downloadlist.add(coupon.getImagePathS());
                        }}
                        break;

                }
            }
        }

        if(downloadlist.size() != 0)
        {
            downloadIndex = 0;
            startImageDownload();

        }
    }

    private void startImageDownload() {
        File imgFile = new File(ContactUtil.FolderPath + File.separator + downloadlist.get(downloadIndex));
        if (!imgFile.exists()) {
            imageDownload(downloadlist.get(downloadIndex));
        }
    }

    private boolean hasExistList(String inputURL)
    {
        for(String url: downloadlist)
        {
            if(inputURL.equals(url))
            {
                return true;
            }
        }
        return false;

    }

    private void imageDownload(String fileName)
    {
        // 如果沒有網路連線，顯示提示對話框
        if (!NetworkUtil.isConnected(getActivity())) {
            ((ActivityBase)getActivity()).showAlertDialog(getString(R.string.msg_no_available_network), android.R.string.ok, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            }, true);
        } else {
            try {
               //  fileName = "WLTCouponUpload/wltcoupon_20160130145138/Image/wltcoupon_20160130145138_image_L.jpg";
               // ((ActivityBase) getActivity()).showProgressLoading();
                //GeneralHttpUtil generalHttpUtil = new GeneralHttpUtil();
                GeneralHttpUtil.downloadCoupon(fileName, finishDownloadListener, getActivity());

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    private DownloadEvent.FinishDownloadListener finishDownloadListener = new DownloadEvent.FinishDownloadListener() {
        @Override
        public void onFinishDownload() {

            GeneralHttpUtil.stopDownloadCoupon();
            if(getActivity() == null)
            {
                return;
            }

            downloadIndex++;
            if(downloadIndex < downloadlist.size() ) {
                startImageDownload();
            }
            else
            {
//                updateListView();
                if (idxSwitchList == COUPON_UNUSED) {
                    adapterCoupon.setCouponList(listCouponUnused);
                } else {
                    adapterCoupon.setCouponList(listCouponUsed);
                }
                adapterCoupon.notifyDataSetChanged();
             //   adapterCoupon.notifyDataSetInvalidated();
            }
        }
    };

}
