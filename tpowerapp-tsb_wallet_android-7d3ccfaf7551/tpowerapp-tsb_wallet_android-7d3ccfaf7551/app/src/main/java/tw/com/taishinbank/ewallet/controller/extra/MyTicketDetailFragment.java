package tw.com.taishinbank.ewallet.controller.extra;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.text.style.URLSpan;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.json.JSONException;

import java.io.File;

import tw.com.taishinbank.ewallet.R;
import tw.com.taishinbank.ewallet.controller.ActivityBase;
import tw.com.taishinbank.ewallet.controller.setting.ApplicationSettedSubActivity;
import tw.com.taishinbank.ewallet.interfaces.ResponseListener;
import tw.com.taishinbank.ewallet.interfaces.TicketType;
import tw.com.taishinbank.ewallet.model.ResponseResult;
import tw.com.taishinbank.ewallet.model.extra.TicketDetailData;
import tw.com.taishinbank.ewallet.model.extra.TicketListData;
import tw.com.taishinbank.ewallet.util.ContactUtil;
import tw.com.taishinbank.ewallet.util.FormatUtil;
import tw.com.taishinbank.ewallet.util.NetworkUtil;
import tw.com.taishinbank.ewallet.util.http.ExtraHttpUtil;
import tw.com.taishinbank.ewallet.util.http.HttpUtilBase;
import tw.com.taishinbank.ewallet.util.responsebody.ExtraResponseBodyUtil;

public class MyTicketDetailFragment extends Fragment implements View.OnClickListener {

    public interface OnEventListener {
        void OnNextClickEvent(TicketDetailData ticketDetailData);

        void OnToGoClickEvent();
    }


    private static final String TAG = "MyTicketDetailFragment";
    public static final String EXTRA_TICKET = "EXTRA_TICKET";

    // -- View Hold --
    //  - Common -
    protected TextView txtSubtitle;
    protected TextView txtTime;
    protected ImageView imgBanner;
    protected TextView txtProd;
    protected TextView txtContent;
    protected TextView txtAmount; //text_amount
    protected TextView txtCaution;

    //  - Table View - 時間
    protected TextView txtTicketNumber;
    protected TextView txtCreateDate;
    protected TextView txtExchangeDate;
    protected TextView txtExchangeDateTitle;
    protected View lytExchangeDate;
    protected TextView txtValidDuration;

    //  - Table View - 優惠內容
    protected TextView txtDirectionContent;
    protected ImageButton btnExpandDirectionContent;
    protected TextView txtStoreInfo;
    protected ImageButton btnExpandStoreInfo;
    //
//    // -- Button Area -
    protected Button btnTrade;
    protected Button btnToLog;
    //
//    // -- Caution area --
    protected LinearLayout lytActionUnuse;
    protected LinearLayout lytActionReturn;

    // -- Data Model --
    protected TicketListData tickeItem;
    protected TicketDetailData ticketDetailData;
    protected boolean isContentExpanding = false;
    protected boolean isStoreInfoExpanding = false;

    // -- View helper --
    // protected ImageLoader imageLoader;

    // -- Event Listener --
    private OnEventListener onEventListener;

    public void setOnEventListener(OnEventListener listener) {
        this.onEventListener = listener;
    }

    public MyTicketDetailFragment() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        if (getActivity() != null) {
            ((ActivityBase) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        tickeItem = getArguments().getParcelable(EXTRA_TICKET);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_extra_my_ticket_detail, container, false);

        // Set View Hold
        setViewHold(view);

        // Helper
        //imageLoader = new ImageLoader(getActivity(), getActivity().getResources().getDimensionPixelSize(R.dimen.list_photo_size));

        // Set view value, show/hide.
//        setHeaderViewContent();
//        setViewContent();
        inquiryEntity();

        btnExpandDirectionContent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                onExpandContentClick();
                Intent intent = new Intent(getActivity(), ApplicationSettedSubActivity.class);
                intent.putExtra(ApplicationSettedSubActivity.EXTRA_CURRENT_PAGE, ApplicationSettedSubActivity.ENUM_PAGE_TYPE.USE_TREATY_PAGE.toString());
                intent.putExtra(ApplicationSettedSubActivity.EXTRA_CENTER_TITLE, getString(R.string.extra_my_ticket_label_use_directions));
                intent.putExtra(ApplicationSettedSubActivity.EXTRA_TREATY_CONTENT, ticketDetailData.getNote());
                startActivity(intent);
            }
        });
        btnExpandStoreInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                onExpandStoreInfoClick();
                String contentStore = "";

                if (ticketDetailData.getStoreName() != null)
                    contentStore += ticketDetailData.getStoreName() + "\n";

                contentStore += "－" + getString(R.string.extra_store_phone) + ticketDetailData.getStoreTel() + "\n";
                contentStore += "－" + getString(R.string.extra_store_address) + ticketDetailData.getStoreAddress() + "\n";

                Intent intent = new Intent(getActivity(), ApplicationSettedSubActivity.class);
                intent.putExtra(ApplicationSettedSubActivity.EXTRA_CURRENT_PAGE, ApplicationSettedSubActivity.ENUM_PAGE_TYPE.USE_TREATY_PAGE.toString());
                intent.putExtra(ApplicationSettedSubActivity.EXTRA_CENTER_TITLE, getString(R.string.extra_my_coupon_label_store_info));
                intent.putExtra(ApplicationSettedSubActivity.EXTRA_TREATY_CONTENT, contentStore);
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
        if (getActivity() != null)
            ((ActivityBase) getActivity()).dismissProgressLoading();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // 返回上一頁
        if (item.getItemId() == android.R.id.home) {
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
            txtDirectionContent.setMaxLines(2);
            isContentExpanding = false;
        } else {
            txtDirectionContent.setMaxLines(30);
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
                ((ActivityBase) getActivity()).showProgressLoading();
                ExtraHttpUtil.queryTicketDetail(
                        tickeItem.getEtkSeq(),
                        responseListenerEntity, getActivity(), TAG);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    // 呼叫收到紅包api的listener
    private ResponseListener responseListenerEntity = new ResponseListener() {

        @Override
        public void onResponse(ResponseResult result) {
            if (getActivity() == null)
                return;

            ((ActivityBase) getActivity()).dismissProgressLoading();
            String returnCode = result.getReturnCode();

            // 如果returnCode是成功
            if (returnCode.equals(ResponseResult.RESULT_SUCCESS)) {
                // 取得列表
                ticketDetailData = ExtraResponseBodyUtil.getTicketDetail(result.getBody());
                setHeaderViewContent();
                setViewContent();

            } else {
                // 如果是Token失效，中文是[不合法連線，請重新登入]
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
        txtSubtitle = (TextView) view.findViewById(R.id.txt_subtitle);
        txtTime = (TextView) view.findViewById(R.id.txt_time);
        imgBanner = (ImageView) view.findViewById(R.id.img_banner);
        txtProd = (TextView) view.findViewById(R.id.txt_prod);
        txtContent = (TextView) view.findViewById(R.id.txt_content);
        txtAmount = (TextView) view.findViewById(R.id.txt_amount);

        txtTicketNumber = (TextView) view.findViewById(R.id.txt_ticket_id);
        txtCreateDate = (TextView) view.findViewById(R.id.txt_create_date);
        txtExchangeDate = (TextView) view.findViewById(R.id.txt_exchange_date);
        txtExchangeDateTitle = (TextView) view.findViewById(R.id.txt_exchange_date_title);
        lytExchangeDate = view.findViewById(R.id.lyt_exchange_date);
        txtValidDuration = (TextView) view.findViewById(R.id.txt_valid_duration);

        txtDirectionContent = (TextView) view.findViewById(R.id.txt_use_directions);
        btnExpandDirectionContent = (ImageButton) view.findViewById(R.id.btn_use_directions);
        txtStoreInfo = (TextView) view.findViewById(R.id.txt_store_inf);
        btnExpandStoreInfo = (ImageButton) view.findViewById(R.id.btn_store_inf);

        lytActionUnuse = (LinearLayout) view.findViewById(R.id.lyt_action_unuse);
        lytActionReturn = (LinearLayout) view.findViewById(R.id.lyt_action_return);

        btnTrade = (Button) view.findViewById(R.id.btn_trade);
        btnToLog = (Button) view.findViewById(R.id.btn_tolog);

        txtCaution = (TextView) view.findViewById(R.id.txt_caution_content);
    }

    protected void setHeaderViewContent() {
        // 設定右上角顯示最後更新時間
        txtTime.setText(FormatUtil.toTimeFormatted(ticketDetailData.getLastUpdate()));

        txtTicketNumber.setText(ticketDetailData.getSerialNo());
        txtProd.setText(ticketDetailData.getTitle());
        txtAmount.setText(FormatUtil.toDecimalFormat(ticketDetailData.getPrice(), true));
        txtContent.setText(ticketDetailData.getStoreName());
        if (TicketType.UNUSE.code.equals(ticketDetailData.getStatus())) {
            txtSubtitle.setText(TicketType.UNUSE.description);
            lytActionUnuse.setVisibility(View.VISIBLE);
            btnTrade.setOnClickListener(this);

        } else if (TicketType.USE.code.equals(ticketDetailData.getStatus())) {
            txtSubtitle.setText(TicketType.USE.description);
            txtExchangeDate.setText(FormatUtil.toTimeFormatted(ticketDetailData.getLastUpdate(), false));
            txtExchangeDateTitle.setText(R.string.extra_my_ticket_label_exchange_date);
            lytExchangeDate.setVisibility(View.VISIBLE);

        } else if (TicketType.RETURN.code.equals(ticketDetailData.getStatus())) {
            txtSubtitle.setText(TicketType.RETURN.description);
            txtExchangeDate.setText(FormatUtil.toTimeFormatted(ticketDetailData.getLastUpdate(), false));
            txtExchangeDateTitle.setText(R.string.extra_my_ticket_label_return_date);
            lytExchangeDate.setVisibility(View.VISIBLE);
            lytActionReturn.setVisibility(View.VISIBLE);
            btnToLog.setOnClickListener(this);
        }

        txtCreateDate.setText(FormatUtil.toTimeFormatted(ticketDetailData.getBuyDate(), false));
        txtValidDuration.setText(FormatUtil.toDateFormatted(ticketDetailData.getEffectiveStartDate().substring(0, 8)) + " - " + FormatUtil.toDateFormatted(ticketDetailData.getEffectiveEndDate().substring(0, 8)));
        txtDirectionContent.setText(ticketDetailData.getNote());
        txtStoreInfo.setText(ticketDetailData.getStoreAddress() + "\n" + ticketDetailData.getStoreTel());

        if (!TextUtils.isEmpty(ticketDetailData.getIconUrl())) {
            File imgURL = new File(ticketDetailData.getIconUrl());
            String imgFilePath =  ContactUtil.TicketFolderPath + File.separator + imgURL.getName();

            File imgFile = new File(imgFilePath);
            if (imgFile.exists()) {
                Bitmap imgBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
                imgBanner.setImageBitmap(imgBitmap);
            } else {
                imgBanner.setImageResource(R.drawable.img_banner_default);
            }
        } else {
            imgBanner.setImageResource(R.drawable.img_banner_default);
        }
    }

    protected void setViewContent() {
        // 設定馬上註冊的連結文字
        SpannableString spannableString = new SpannableString(getString(R.string.extra_my_ticket_caution));

        spannableString.setSpan(new OpenPhoneViewURLSpan(ticketDetailData.getStoreTel(), getContext()),
                spannableString.length() - 6, /// 最後邊的六個字 - 請聯絡店家。
                spannableString.length() - 1, /// 句點不要加連結
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        txtCaution.setText(spannableString);
        txtCaution.setMovementMethod(LinkMovementMethod.getInstance());
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

//    protected void showDialog(String message) {
//        ((ActivityBase) getActivity()).showAlertDialog(message, android.R.string.ok,
//                new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        dialog.dismiss();
//                    }
//                }, true);
//    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        // TODO 待重構作法
        if (menu != null) {
            MenuItem item = menu.findItem(R.id.action_contacts);
            if (item != null) {
                item.setVisible(false);
            }
        } else {
            super.onPrepareOptionsMenu(menu);
        }
    }

    @Override
    public void onClick(View v) {
        int viewId = v.getId();
        if (viewId == R.id.btn_trade) {
            onEventListener.OnNextClickEvent(ticketDetailData);
        } else if (viewId == R.id.btn_tolog) {
            onEventListener.OnToGoClickEvent();
//            Intent intent = new Intent(getActivity(), MainActivity.class);
//            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//            intent.putExtra(MainActivity.EXTRA_GO_PAGE_TAG, MainActivity.TAB_CREDIT);
//            intent.putExtra(CreditCardFragment.EXTRA_GO_PAGE_TAG, CreditCardFragment.TAB_CREDIT_LOG);
//            startActivity(intent);
        }
    }

    private class OpenPhoneViewURLSpan extends URLSpan {
        private Context context;

        public OpenPhoneViewURLSpan(String phone, Context context) {
            super(phone);
            this.context = context;
        }

        @Override
        public void onClick(View widget) {

            ((ActivityBase) getActivity()).showAlertDialog(String.format(getString(R.string.extra_my_ticket_return_alert_info), ticketDetailData.getOrderId()), R.string.extra_my_ticket_return_call_store, R.string.button_cancel,
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + getURL()));
                            context.startActivity(intent);
                        }
                    }
                    ,
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    }
                    , true);

        }
    }
}
