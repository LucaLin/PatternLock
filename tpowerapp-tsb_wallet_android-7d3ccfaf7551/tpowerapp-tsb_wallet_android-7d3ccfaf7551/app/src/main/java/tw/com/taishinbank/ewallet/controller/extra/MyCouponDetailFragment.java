package tw.com.taishinbank.ewallet.controller.extra;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.Gson;

import org.json.JSONException;

import java.io.File;
import java.util.List;

import tw.com.taishinbank.ewallet.R;
import tw.com.taishinbank.ewallet.adapter.sv.TransactionLogDetailListRecyclerAdapter;
import tw.com.taishinbank.ewallet.controller.ActivityBase;
import tw.com.taishinbank.ewallet.controller.setting.ApplicationSettedSubActivity;
import tw.com.taishinbank.ewallet.imagehelper.ImageLoader;
import tw.com.taishinbank.ewallet.interfaces.CouponType;
import tw.com.taishinbank.ewallet.interfaces.ResponseListener;
import tw.com.taishinbank.ewallet.model.ResponseResult;
import tw.com.taishinbank.ewallet.model.extra.Coupon;
import tw.com.taishinbank.ewallet.model.extra.StoreData;
import tw.com.taishinbank.ewallet.util.ContactUtil;
import tw.com.taishinbank.ewallet.util.FormatUtil;
import tw.com.taishinbank.ewallet.util.NetworkUtil;
import tw.com.taishinbank.ewallet.util.http.ExtraHttpUtil;
import tw.com.taishinbank.ewallet.util.http.HttpUtilBase;
import tw.com.taishinbank.ewallet.util.responsebody.ExtraResponseBodyUtil;

public class MyCouponDetailFragment extends Fragment {

    private static final String TAG = "MyCouponDetailFragment";
    public static final String EXTRA_COUPON = "EXTRA_COUPON";

    // -- View Hold --
    //  - Common -
    protected TextView     txtSubtitle;
    protected TextView     txtTime;
    protected ImageView    imgCouponBanner;
    protected TextView     txtCouponProd;
    protected TextView     txtCouponContent;
    protected TextView     txtType;

    //  - Table View - 時間
    protected TextView     txtCreateDate;
    protected TextView     txtExchangeDate;
    protected TextView     txtExchangeDateTitle;
    protected View         lytExchangeDate;
    protected TextView     txtValidDuration;

    //  - Table View - 優惠內容
    protected TextView     txtPromotionContent;
    protected ImageButton  btnExpandPromotionContent;
    protected TextView     txtStoreInfo;
    protected ImageButton  btnExpandStoreInfo;

    //  - 發出的人留言, 收到的人留言 -
    protected TextView     txtLegend1;
    protected TextView     txtLegend2;

    protected LinearLayout lytSender;
    protected LinearLayout lytReceiver;

    protected FrameLayout  lytSenderMsg;
    protected FrameLayout  lytReceiverMsg;

    protected TransactionLogDetailListRecyclerAdapter.ViewHolder viewHolderSender;
    protected TransactionLogDetailListRecyclerAdapter.ViewHolder viewHolderReceiver;

    // -- Button Area -
    protected LinearLayout lytActionArea;
    protected Button       btnGive;
    protected Button       btnTrade;

    // -- Caution area --
    protected LinearLayout lytCautionArea;
    protected TextView     txtCautionTitle;
    protected TextView     txtCaution;

    // -- Data Model --
    protected Coupon coupon;
    protected boolean isContentExpanding = false;
    protected boolean isStoreInfoExpanding = false;

    // -- View helper --
    protected ImageLoader imageLoader;

    public MyCouponDetailFragment() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        if(getActivity() != null) {
            ((ActivityBase) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        coupon = getArguments().getParcelable(EXTRA_COUPON);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_extra_my_coupon_detail, container, false);

        // Set View Hold
        setViewHold(view);

        // Helper
        imageLoader = new ImageLoader(getActivity(), getActivity().getResources().getDimensionPixelSize(R.dimen.list_photo_size));

        // Set view value, show/hide.
//        setHeaderViewContent();
//        setViewContent();
        inquiryEntity();

        // Set Listener
        btnGive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onButtonGiveClick();
            }
        });
        btnTrade.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onButtonTradeClick();
            }
        });

        btnExpandPromotionContent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //onExpandContentClick();
                Intent intent = new Intent(getActivity(), ApplicationSettedSubActivity.class);
                intent.putExtra(ApplicationSettedSubActivity.EXTRA_CURRENT_PAGE, ApplicationSettedSubActivity.ENUM_PAGE_TYPE.USE_TREATY_PAGE.toString());
                intent.putExtra(ApplicationSettedSubActivity.EXTRA_CENTER_TITLE, getString(R.string.extra_my_coupon_label_content_title));
                intent.putExtra(ApplicationSettedSubActivity.EXTRA_TREATY_CONTENT, coupon.getContent());
                startActivity(intent);
            }
        });


        btnExpandStoreInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                onExpandStoreInfoClick();
                ToStoreInfoe();
            }
        });

        txtCautionTitle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), ApplicationSettedSubActivity.class);
                intent.putExtra(ApplicationSettedSubActivity.EXTRA_CURRENT_PAGE, ApplicationSettedSubActivity.ENUM_PAGE_TYPE.USE_TREATY_PAGE.toString());
                intent.putExtra(ApplicationSettedSubActivity.EXTRA_CENTER_TITLE, getString(R.string.sv_caution_title));
                intent.putExtra(ApplicationSettedSubActivity.EXTRA_TREATY_CONTENT, coupon.getNotes());
                startActivity(intent);
            }
        });

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onStop() {
        super.onStop();
        HttpUtilBase.cancelQueue(TAG);
        if(getActivity() != null)
            ((ActivityBase) getActivity()).dismissProgressLoading();
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

    // ----
    // User interaction
    // ----
    protected void onButtonGiveClick() {

    }

    protected void onButtonTradeClick() {

    }

    protected void onExpandContentClick() {
        if (isContentExpanding) {
            txtPromotionContent.setMaxLines(2);
            isContentExpanding = false;
        } else {
            txtPromotionContent.setMaxLines(30);
            isContentExpanding = true;
        }
    }

    protected void onExpandStoreInfoClick() {
        if (isStoreInfoExpanding) {
            txtStoreInfo.setMaxLines(2);
            isStoreInfoExpanding = false;
        } else {
            txtStoreInfo.setMaxLines(30);
            isStoreInfoExpanding = true;
        }
    }

    // ----
    // Http
    // ----
    private void inquiryEntity() {
        // 如果沒有網路連線，顯示提示對話框
        if (!NetworkUtil.isConnected(getActivity())) {
            showRetryAlert(getString(R.string.msg_no_available_network));
        } else {
            try {
                ExtraHttpUtil.queryCouponDetail(
                        String.valueOf(coupon.getCpSeq()),
                        String.valueOf(coupon.getMsmSeq()),
                        responseListenerEntity, getActivity(), TAG);
                ((ActivityBase) getActivity()).showProgressLoading();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }


    private void ToStoreInfoe() {
        // 如果沒有網路連線，顯示提示對話框
        if (!NetworkUtil.isConnected(getActivity())) {
            showRetryAlert(getString(R.string.msg_no_available_network));
        } else {
            try {
                ExtraHttpUtil.queryStoreInformation(coupon.getCpSeq(),
                        responseListenerStoreList, getActivity(), TAG);
                ((ActivityBase) getActivity()).showProgressLoading();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    // 呼叫收到紅包api的listener
    private ResponseListener responseListenerEntity = new ResponseListener() {

        @Override
        public void onResponse(ResponseResult result) {
            if(getActivity() == null)
                return;

            ((ActivityBase) getActivity()).dismissProgressLoading();
            String returnCode = result.getReturnCode();

            // 如果returnCode是成功
            if (returnCode.equals(ResponseResult.RESULT_SUCCESS)) {
                // 取得列表
                coupon = new Gson().fromJson(result.getBody().toString(), Coupon.class);
                ((MyCouponActivity) getActivity()).updateCoupon(coupon);
                setHeaderViewContent();
                setViewContent();

            } else {
                // 如果不是共同error
                if(!handleCommonError(result, (ActivityBase) getActivity())){
                    showRetryAlert(result.getReturnMessage());
                }
            }
        }
    };

    // 呼叫收到紅包api的listener
    private ResponseListener responseListenerStoreList = new ResponseListener() {

        @Override
        public void onResponse(ResponseResult result) {
            if(getActivity() == null)
                return;

            ((ActivityBase) getActivity()).dismissProgressLoading();
            String returnCode = result.getReturnCode();

            // 如果returnCode是成功
            if (returnCode.equals(ResponseResult.RESULT_SUCCESS)) {
                // 取得列表
                List<StoreData> lstStoreInfor = ExtraResponseBodyUtil.parseToStoreList(result.getBody());

                String contentStore = "";
                int count = 1;
                for (StoreData store: lstStoreInfor) {
                    contentStore += String.valueOf(count) + ". " + store.getStoreName() + "\n";
                    contentStore += "－" + getString(R.string.extra_store_phone) + store.getStorePhone() + "\n";
                    contentStore += "－" + getString(R.string.extra_store_address) + store.getStoreAddress() + "\n\n";
                    count++;
                }

                Intent intent = new Intent(getActivity(), ApplicationSettedSubActivity.class);
                intent.putExtra(ApplicationSettedSubActivity.EXTRA_CURRENT_PAGE, ApplicationSettedSubActivity.ENUM_PAGE_TYPE.USE_TREATY_PAGE.toString());
                intent.putExtra(ApplicationSettedSubActivity.EXTRA_CENTER_TITLE, getString(R.string.extra_my_coupon_label_store_info_title));
                intent.putExtra(ApplicationSettedSubActivity.EXTRA_TREATY_CONTENT, contentStore);
                startActivity(intent);
            } else {
                // 如果不是共同error
                if(!handleCommonError(result, (ActivityBase) getActivity())){
                    showRetryAlert(result.getReturnMessage());
                }
            }
        }
    };

    // ----
    // protected method
    // ----
    protected void setViewHold(View view) {
        txtSubtitle                 = (TextView)     view.findViewById(R.id.txt_subtitle);
        txtTime                     = (TextView)     view.findViewById(R.id.txt_time);
        imgCouponBanner             = (ImageView)    view.findViewById(R.id.img_coupon_banner);
        txtCouponProd               = (TextView)     view.findViewById(R.id.txt_coupon_prod);
        txtCouponContent            = (TextView)     view.findViewById(R.id.txt_coupon_content);
        txtType                     = (TextView)     view.findViewById(R.id.txt_type);

        txtCreateDate               = (TextView)     view.findViewById(R.id.txt_create_date);
        txtExchangeDate             = (TextView)     view.findViewById(R.id.txt_exchange_date);
        txtExchangeDateTitle        = (TextView)     view.findViewById(R.id.txt_exchange_date_title);
        lytExchangeDate             =                view.findViewById(R.id.lyt_exchange_date);
        txtValidDuration            = (TextView)     view.findViewById(R.id.txt_valid_duration);

        txtPromotionContent         = (TextView)     view.findViewById(R.id.txt_promotion_content);
        btnExpandPromotionContent   = (ImageButton)  view.findViewById(R.id.btn_promotion_content);
        txtStoreInfo                = (TextView)     view.findViewById(R.id.txt_store_inf);
        btnExpandStoreInfo          = (ImageButton)  view.findViewById(R.id.btn_store_inf);

        txtLegend1                  = (TextView)     view.findViewById(R.id.txt_legend_1);
        txtLegend2                  = (TextView)     view.findViewById(R.id.txt_legend_2);

        // - 包含分隔線和list view item layout(lytSenderMsg) -
        lytSender                   = (LinearLayout) view.findViewById(R.id.lyt_sender);
        lytReceiver                 = (LinearLayout) view.findViewById(R.id.lyt_receiver);

        // - 由list view item重複使用而來 -
        lytSenderMsg                = (FrameLayout)  view.findViewById(R.id.lyt_sender_msg);
        lytReceiverMsg              = (FrameLayout)  view.findViewById(R.id.lyt_receiver_msg);

        // - 設置list view item 的view hold -
        viewHolderSender     = new TransactionLogDetailListRecyclerAdapter.ViewHolder(lytSenderMsg);
        viewHolderReceiver   = new TransactionLogDetailListRecyclerAdapter.ViewHolder(lytReceiverMsg);

        lytActionArea = (LinearLayout) view.findViewById(R.id.lyt_action_area);
        btnGive       = (Button) view.findViewById(R.id.btn_give);
        btnTrade      = (Button) view.findViewById(R.id.btn_trade);

        // - Caution area -
        lytCautionArea  = (LinearLayout) view.findViewById(R.id.lyt_caution_area);
        txtCautionTitle = (TextView) view.findViewById(R.id.txt_caution_title);
        txtCaution      = (TextView) view.findViewById(R.id.txt_caution_content);

    }

    protected void setHeaderViewContent() {
        // 設定右上角顯示最後更新時間
        txtTime.setText(FormatUtil.toTimeFormatted(coupon.getLastUpdate()));

        txtCouponProd.setText(coupon.getTitle());
        txtCouponContent.setText(coupon.getSubTitle());
        if (CouponType.ACT.code.equals(coupon.getStatus())) {
            txtType.setText(CouponType.ACT.description);
            txtType.setBackgroundResource(R.drawable.extra_my_coupon_type_act);

        } else if (CouponType.RECEIVED.code.equals(coupon.getStatus())) {
            txtType.setText(CouponType.RECEIVED.description);
            txtType.setBackgroundResource(R.drawable.extra_my_coupon_type_invite);

        } else if (CouponType.SENT.code.equals(coupon.getStatus())) {
            txtType.setText(CouponType.SENT.description);
            txtType.setBackgroundResource(R.drawable.extra_my_coupon_type_sent);
            txtExchangeDate.setText(FormatUtil.toTimeFormatted(coupon.getSenderDate()));
            txtExchangeDateTitle.setText(R.string.extra_my_coupon_label_sender_date);
            lytExchangeDate.setVisibility(View.VISIBLE);

        } else if (CouponType.TRADED.code.equals(coupon.getStatus())) {
            txtType.setText(CouponType.TRADED.description);
            txtType.setBackgroundResource(R.drawable.extra_my_coupon_type_traded);
            txtExchangeDate.setText(FormatUtil.toTimeFormatted(coupon.getExchangeDate()));
            txtExchangeDateTitle.setText(R.string.extra_my_coupon_label_exchange_date);
            lytExchangeDate.setVisibility(View.VISIBLE);
        }

        txtCreateDate.setText(FormatUtil.toTimeFormatted(coupon.getCreateDate()));
        txtValidDuration.setText(FormatUtil.toDateFormatted(coupon.getStartDate().substring(0,8)) + " - " + FormatUtil.toDateFormatted(coupon.getEndDate().substring(0, 8)));
        txtPromotionContent.setText(coupon.getContent());
        txtStoreInfo.setText(coupon.getStoreInfo());

        if(coupon.getBranchFlag().equals("N"))
            btnExpandStoreInfo.setVisibility(View.GONE);


        String imgFilePath = null;
        switch (((MyCouponActivity) getActivity()).imageSize.toString())
        {
            case "LARGE":
                if(!TextUtils.isEmpty(coupon.getImagePathL())) {
                    imgFilePath = ContactUtil.FolderPath + File.separator + coupon.getImagePathL();
                }
                break;

            case "MEDIUM":
                if(!TextUtils.isEmpty(coupon.getImagePathM())) {
                    imgFilePath = ContactUtil.FolderPath + File.separator + coupon.getImagePathM();
                }
                break;

            case "SMALL":
                if(!TextUtils.isEmpty(coupon.getImagePathS())) {
                    imgFilePath = ContactUtil.FolderPath + File.separator + coupon.getImagePathS();
                }
                break;

        }
        if(imgFilePath != null) {
            File imgFile = new File(imgFilePath);
            if (imgFile.exists()) {
                Bitmap imgBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
                imgCouponBanner.setImageBitmap(imgBitmap);
            } else {
                imgCouponBanner.setImageResource(R.drawable.img_banner_default);
            }
        } else {
            imgCouponBanner.setImageResource(R.drawable.img_banner_default);
        }

    }

    protected void setViewContent() {

    }

    protected void showRetryAlert(String message) {
        new AlertDialog.Builder(getActivity())
                .setMessage(message)
                .setPositiveButton(R.string.try_one, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        inquiryEntity();
                    }
                })
                .setNegativeButton(R.string.go_back, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        getFragmentManager().popBackStack();
                    }
                }).show();
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        // TODO 待重構作法
        if(menu != null) {
            MenuItem item = menu.findItem(R.id.action_contacts);
            if (item != null) {
                item.setVisible(false);
            }
        } else {

            super.onPrepareOptionsMenu(menu);
        }
    }

}
