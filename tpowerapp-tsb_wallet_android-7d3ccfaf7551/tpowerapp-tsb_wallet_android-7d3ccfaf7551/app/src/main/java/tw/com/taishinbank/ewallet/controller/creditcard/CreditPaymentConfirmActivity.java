package tw.com.taishinbank.ewallet.controller.creditcard;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.json.JSONException;

import java.io.File;

import tw.com.taishinbank.ewallet.R;
import tw.com.taishinbank.ewallet.adapter.CreditCardPagerAdapter;
import tw.com.taishinbank.ewallet.controller.ActivityBase;
import tw.com.taishinbank.ewallet.interfaces.ResponseListener;
import tw.com.taishinbank.ewallet.model.ResponseResult;
import tw.com.taishinbank.ewallet.model.creditcard.CreditCardData;
import tw.com.taishinbank.ewallet.model.extra.TicketOrderData;
import tw.com.taishinbank.ewallet.util.ContactUtil;
import tw.com.taishinbank.ewallet.util.CreditCardUtil;
import tw.com.taishinbank.ewallet.util.FormatUtil;
import tw.com.taishinbank.ewallet.util.NetworkUtil;
import tw.com.taishinbank.ewallet.util.http.CreditCardHttpUtil;
import tw.com.taishinbank.ewallet.util.http.GeneralHttpUtil;
import tw.com.taishinbank.ewallet.util.http.HttpUtilBase;
import tw.com.taishinbank.ewallet.util.http.event.DownloadEvent;
import tw.com.taishinbank.ewallet.util.responsebody.CreditCardResponseBodyUtil;

public class CreditPaymentConfirmActivity extends ActivityBase implements View.OnClickListener{

    private static final String TAG = "CreditPaymentConfirmActivity";
    public static final String EXTRA_ORDER_TOKEN = "extra_order_token";
    public static final String EXTRA_ORDER_ID = "extra_order_id";
    private enum PAGE_MODE
    {
        MAIN_PAGE,
        SELECT_CARD_PAGE
    }

    private PAGE_MODE currentPage = PAGE_MODE.MAIN_PAGE;

    private ViewPager viewPager;
    private LinearLayout linearLayout_viewpager_cards;
    private TextView text_cardname, text_select_cardname;
    private CreditCardPagerAdapter adapter;
    private Button button_selected, button_left_viewPage, button_right_viewPage;
    private ImageButton button_change_credit;
    private int index_mainCard = 0;
    private CreditCardData selectedCardData;
    private LinearLayout layout_detail;
    private ImageView imgBanner;
    private TextView txtStorename, txtCommodityName, txtCommodityNumber, txtCommodityMoney, txtOrderID;


    private String orderID;
    private String orderToken;

    private TicketOrderData ticketOrderData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_credit_card_payment_confirm);
        // 設定置中的標題與返回鈕
        this.setCenterTitle(R.string.credit_card_payment_confirm_title);
        this.getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        if(getIntent().hasExtra(EXTRA_ORDER_TOKEN)) {
            orderToken = getIntent().getStringExtra(EXTRA_ORDER_TOKEN);
        } else {
            showAlertDialog("沒有取得訂單授權");
        }

        if(getIntent().hasExtra(EXTRA_ORDER_ID))
        {
            orderID = getIntent().getStringExtra(EXTRA_ORDER_ID);
        } else {
            showAlertDialog("沒有取得訂單編號");
        }

        layout_detail = (LinearLayout) findViewById(R.id.layout_detail);
        imgBanner = (ImageView) findViewById(R.id.img_banner);
        txtStorename = (TextView) findViewById(R.id.text_storename);
        txtCommodityName = (TextView) findViewById(R.id.text_commodity_name);
        txtCommodityNumber = (TextView) findViewById(R.id.text_commodity_number);
        txtCommodityMoney = (TextView) findViewById(R.id.text_commodity_money);
        txtOrderID = (TextView) findViewById(R.id.text_order_id);

        // 選擇信用卡
        text_select_cardname = (TextView) findViewById(R.id.text_selected_cardname);
        text_cardname = (TextView) findViewById(R.id.text_cardname);
        text_cardname.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changPage(PAGE_MODE.SELECT_CARD_PAGE);
            }
        });
        button_change_credit = (ImageButton) findViewById(R.id.btn_change_credit);
        button_change_credit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changPage(PAGE_MODE.SELECT_CARD_PAGE);
            }
        });


        viewPager = (ViewPager) findViewById(R.id.viewpager_card);
        adapter = new CreditCardPagerAdapter(this, CreditCardUtil.GetCreditCardList(this));
        //adapter.EventCallBack(this);
        adapter.eventCallBack = new CreditCardPagerAdapter.EventCallBack() {
            @Override
            public void OnChangeCardPage(int index) {
                index_mainCard = index;
                setCardName(index_mainCard);
                viewPager.setCurrentItem(index_mainCard);

                if(CreditCardUtil.GetCreditCardList(CreditPaymentConfirmActivity.this).size() == 1) {
                    button_left_viewPage.setVisibility(View.GONE);
                    button_right_viewPage.setVisibility(View.GONE);
                }
                else if(index_mainCard == 0)
                {
                    button_left_viewPage.setVisibility(View.GONE);
                    button_right_viewPage.setVisibility(View.VISIBLE);
                }
                else if (adapter.getCount() == index_mainCard + 1)
                {
                    button_left_viewPage.setVisibility(View.VISIBLE);
                    button_right_viewPage.setVisibility(View.GONE);
                }
                else
                {
                    button_left_viewPage.setVisibility(View.VISIBLE);
                    button_right_viewPage.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void OnClick()
            {
                //hangPage(PAGE_MODE.CHECK_PASSWORD_PAGE);
            }
        };

        // Set up the ViewPager with the sections adapter.
        viewPager.setAdapter(adapter);

        int page_size = CreditCardUtil.GetCreditCardList(this).size();
        viewPager.setOffscreenPageLimit(page_size);

        //設定卡片間的距離
        viewPager.setPageMargin(getResources().getDimensionPixelSize(R.dimen.viewpages_card_spacing));

        CardsPageChangeListener cardsPageChangeListener = new CardsPageChangeListener();
        viewPager.addOnPageChangeListener(cardsPageChangeListener);
        linearLayout_viewpager_cards = (LinearLayout) findViewById(R.id.linearLayout_viewpager_cards);
        linearLayout_viewpager_cards.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return viewPager.dispatchTouchEvent(event);
            }
        });

        button_left_viewPage = (Button) findViewById(R.id.button_viewpager_left);
        button_right_viewPage = (Button) findViewById(R.id.button_viewpager_right);

        button_left_viewPage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (viewPager.getCurrentItem() > 0)
                    viewPager.setCurrentItem(viewPager.getCurrentItem() - 1);
            }
        });

        button_right_viewPage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (viewPager.getCurrentItem() < viewPager.getOffscreenPageLimit())
                    viewPager.setCurrentItem(viewPager.getCurrentItem() + 1);
            }
        });
      //  viewPager.setCurrentItem(index_mainCard);
        button_selected = (Button) findViewById(R.id.button_selected);
        button_selected.setOnClickListener(this);

        changPage(PAGE_MODE.MAIN_PAGE);


    }

    private void upateView()
    {
        txtStorename.setText(ticketOrderData.getStoreName());
        txtCommodityMoney.setText(FormatUtil.toDecimalFormatFromString(ticketOrderData.getPrice(), true));
        txtCommodityNumber.setText(ticketOrderData.getCount()+" 張");
        txtCommodityName.setText(ticketOrderData.getTitle());
        txtOrderID.setText(ticketOrderData.getOrderId());

        setBannerImage(true);
    }

    private void setBannerImage(boolean needDownload)
    {
        if(!(ticketOrderData.getIconUrl().startsWith("http://") || ticketOrderData.getIconUrl().startsWith("https://"))) {
            ticketOrderData.setIconUrl("http://" + ticketOrderData.getIconUrl());
        }
        if(!TextUtils.isEmpty(ticketOrderData.getIconUrl())) {
            String imgFilePath = "";
            File imgURL = new File(ticketOrderData.getIconUrl());
            imgFilePath = ContactUtil.TicketFolderPath + File.separator + imgURL.getName();

            File imgFile = new File(imgFilePath);
            if (imgFile.exists()) {
                Bitmap imgBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
                imgBanner.setImageBitmap(imgBitmap);
            } else {
                imgBanner.setImageResource(R.drawable.img_banner_default);
                if (!needDownload)
                    return;

                imageDownload(ticketOrderData.getIconUrl());
            }
        } else {
            imgBanner.setImageResource(R.drawable.img_banner_default);
        }


    }

    private void getPaymentData() {
        if (!NetworkUtil.isConnected(this)) {
                ((ActivityBase) this).showAlertDialog(getString(R.string.msg_no_available_network), android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }, true);
            } else {
                try {
                    this.showProgressLoading();
                    CreditCardHttpUtil.queryTickenOrderDetail(orderToken, orderID, responseListener, this, TAG);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

    }

    private void changPage(PAGE_MODE mode)
    {
        switch (mode)
        {
            case MAIN_PAGE:
                this.setCenterTitle(R.string.credit_card_payment_confirm_title);
                layout_detail.setVisibility(View.VISIBLE);
                linearLayout_viewpager_cards.setVisibility(View.INVISIBLE);
                button_selected.setText(R.string.button_next_step);
                break;

            case SELECT_CARD_PAGE:
                this.setCenterTitle(R.string.scan_payment_password_selectcard_title);
                linearLayout_viewpager_cards.setVisibility(View.VISIBLE);
                layout_detail.setVisibility(View.GONE);
                button_selected.setText(R.string.phone_alert_button_back);
                button_selected.setEnabled(true);
                break;

        }

        currentPage = mode;
    }

    @Override
    protected void onResume() {
        super.onResume();
        getPaymentData();
        updateCreditCardList();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
        HttpUtilBase.cancelQueue(TAG);
        dismissProgressLoading();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // 返回上一頁
        if(currentPage == PAGE_MODE.SELECT_CARD_PAGE){
            changPage(PAGE_MODE.MAIN_PAGE);
            return true;
        } else {
            onBackPressed();
            return true;
        }

        //return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        // 返回上一頁
        if(currentPage == PAGE_MODE.SELECT_CARD_PAGE){
            changPage(PAGE_MODE.MAIN_PAGE);
            return;
        } else {
            showAlertDialog(getString(R.string.order_cancel), android.R.string.ok, android.R.string.cancel,
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            CreditPaymentConfirmActivity.super.onBackPressed();
                        }
                    },
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            updateCreditCardList();
                            dialog.dismiss();
                        }
                    }, false);
        }
//        super.onBackPressed();
    }

    @Override
    public void onClick(View v) {
        int viewId = v.getId();


        if(viewId == R.id.button_selected){
            switch (currentPage)
            {
                case MAIN_PAGE:
                    Intent intent = new Intent(CreditPaymentConfirmActivity.this, CreditCardPaymentLoginActivity.class);
                    intent.putExtra(CreditCardPaymentLoginActivity.EXTRA_CARD_DATA, selectedCardData);
                    intent.putExtra(CreditCardPaymentLoginActivity.EXTRA_PAYMENT_TICKET_DATA, ticketOrderData);
                    startActivity(intent);
                    break;

                case SELECT_CARD_PAGE:
                    changPage(PAGE_MODE.MAIN_PAGE);
                    break;
            }
        }
    }

    private void updateCreditCardList() {
        //def is not card mode
        if(CreditCardUtil.GetCreditCardList(this).size() > 0)
        {
            int page_size = CreditCardUtil.GetCreditCardList(this).size();

            adapter.updateAdapter(CreditCardUtil.GetCreditCardList(this));
            viewPager.setOffscreenPageLimit(page_size);

            if(page_size == 1)
            {
                button_left_viewPage.setVisibility(View.GONE);
                button_right_viewPage.setVisibility(View.GONE);
            }
        } else {
            this.showAlertDialog(getString(R.string.order_confirm_nocard_alert), R.string.credit_card_go_create, android.R.string.cancel,
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Intent intent = new Intent(CreditPaymentConfirmActivity.this, CreditCardCreateActivity.class);
                            intent.putExtra(CreditCardCreateActivity.EXTRA_FROM_ORDER_PAGE, true);
                            startActivity(intent);
                            dialog.dismiss();
                        }
                    }
                    ,
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            onBackPressed();
                            dialog.dismiss();
                        }
                    }

                    , false);
        }
    }

    /**
     * 檢查是否enable下一步按鈕
     */

    private void setCardName(int cardID)
    {
        String name = CreditCardUtil.GetCreditCardList(this).get(cardID).getCardName();
        String cardNumber = CreditCardUtil.GetCreditCardList(this).get(cardID).getCardNumber();


        text_cardname.setText(name + FormatUtil.toHideCardNumberString(cardNumber));
        text_select_cardname.setText(name + FormatUtil.toHideCardNumberString(cardNumber));
        selectedCardData = CreditCardUtil.GetCreditCardList(this).get(cardID);
    }

    public class CardsPageChangeListener implements ViewPager.OnPageChangeListener {

        @Override
        public void onPageSelected(int position) {
            setCardName(position);

            if(CreditCardUtil.GetCreditCardList(CreditPaymentConfirmActivity.this).size() == 1) {
                button_left_viewPage.setVisibility(View.GONE);
                button_right_viewPage.setVisibility(View.GONE);
            }
            else if(position == 0)
            {
                button_left_viewPage.setVisibility(View.GONE);
                button_right_viewPage.setVisibility(View.VISIBLE);
            }
            else if (adapter.getCount() == position + 1)
            {
                button_left_viewPage.setVisibility(View.VISIBLE);
                button_right_viewPage.setVisibility(View.GONE);
            }
            else
            {
                button_left_viewPage.setVisibility(View.VISIBLE);
                button_right_viewPage.setVisibility(View.VISIBLE);
            }
        }

        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            // to refresh frameLayout
            if (linearLayout_viewpager_cards != null) {
                linearLayout_viewpager_cards.invalidate();
            }
        }

        @Override
        public void onPageScrollStateChanged(int arg0) {
        }
    }


    private ResponseListener responseListener = new ResponseListener() {

        @Override
        public void onResponse(ResponseResult result) {

            dismissProgressLoading();
            String returnCode = result.getReturnCode();

            // 如果returnCode是成功
            if (returnCode.equals(ResponseResult.RESULT_SUCCESS)) {
                ticketOrderData = CreditCardResponseBodyUtil.getTicketOrderData(result.getBody());
                ticketOrderData.setOrderToken(orderToken);
                upateView();
            } else {
                // 如果是Token失效，中文是[不合法連線，請重新登入]
                // 如果是Token失效，中文是[不合法連線，請重新登入]
                if(!handleCommonError(result, CreditPaymentConfirmActivity.this)){
                    showAlertDialog(result.getReturnMessage());
                }
            }
        }
    };


    private void imageDownload(String imageURL)
    {
        // 如果沒有網路連線，顯示提示對話框
        if (!NetworkUtil.isConnected(this)) {
            showAlertDialog(getString(R.string.msg_no_available_network), android.R.string.ok, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            }, true);
        } else {
            try {
                GeneralHttpUtil.downloadTicket(imageURL, finishDownloadListener, this);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    private DownloadEvent.FinishDownloadListener finishDownloadListener = new DownloadEvent.FinishDownloadListener() {
        @Override
        public void onFinishDownload() {
            GeneralHttpUtil.stopDownloadCoupon();
            setBannerImage(false);
        }
    };
}