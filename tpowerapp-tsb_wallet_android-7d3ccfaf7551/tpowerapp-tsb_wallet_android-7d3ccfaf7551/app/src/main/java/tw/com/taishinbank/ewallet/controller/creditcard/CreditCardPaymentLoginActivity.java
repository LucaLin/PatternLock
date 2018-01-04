package tw.com.taishinbank.ewallet.controller.creditcard;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.json.JSONException;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

import tw.com.taishinbank.ewallet.R;
import tw.com.taishinbank.ewallet.adapter.CreditCardPagerAdapter;
import tw.com.taishinbank.ewallet.controller.ActivityBase;
import tw.com.taishinbank.ewallet.controller.ResetPasswordActivity;
import tw.com.taishinbank.ewallet.interfaces.GlobalConst;
import tw.com.taishinbank.ewallet.interfaces.ResponseListener;
import tw.com.taishinbank.ewallet.listener.BasicEditTextWatcher;
import tw.com.taishinbank.ewallet.model.ResponseResult;
import tw.com.taishinbank.ewallet.model.creditcard.CreditCardData;
import tw.com.taishinbank.ewallet.model.extra.TicketOrderData;
import tw.com.taishinbank.ewallet.model.log.SpecialEvent;
import tw.com.taishinbank.ewallet.util.CreditCardUtil;
import tw.com.taishinbank.ewallet.util.EventAnalyticsUtil;
import tw.com.taishinbank.ewallet.util.FormatUtil;
import tw.com.taishinbank.ewallet.util.NetworkUtil;
import tw.com.taishinbank.ewallet.util.PreferenceUtil;
import tw.com.taishinbank.ewallet.util.http.CreditCardHttpUtil;
import tw.com.taishinbank.ewallet.util.http.GeneralHttpUtil;
import tw.com.taishinbank.ewallet.util.http.HttpUtilBase;
import tw.com.taishinbank.ewallet.util.responsebody.GeneralResponseBodyUtil;
import tw.com.taishinbank.ewallet.util.sharedMethods;

public class CreditCardPaymentLoginActivity extends ActivityBase implements View.OnClickListener{

    public static final String EXTRA_PAYMENT_TICKET_DATA = "extra_payment_ticket_data";
    public static final String EXTRA_CARD_DATA = "extra_card_data";

    private static final String TAG = "CreditCardPaymentLoginActivity";

    private enum PAGE_MODE
    {
        CHECK_PASSWORD_PAGE,
        SELECT_CARD_PAGE
    }

    private PAGE_MODE currentPage = PAGE_MODE.CHECK_PASSWORD_PAGE;

    private ViewPager viewPager;
    private RelativeLayout relativeLayout_viewpager_cards;
    private LinearLayout linearLayout_mima;
    private TextView text_cardname, text_selected_title, text_mimaError;
    private EditText editMima;
    private CreditCardPagerAdapter adapter;
    private Button button_selected, button_forgot_mima, button_left_viewPage, button_right_viewPage;
    private int index_mainCard = 0;
    private CreditCardData selectedCardData;

    private int mimaErrorLimit = 4;

    private boolean IsForPaymenteTicketFlow = false;
    private TicketOrderData orderTicketData;

    private long lastClickTime = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_credit_card_scanpayment);
        // 設定置中的標題與返回鈕
        this.setCenterTitle(R.string.scan_payment_password_check_title);
        this.getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        if(getIntent().hasExtra(EXTRA_PAYMENT_TICKET_DATA))
        {
            IsForPaymenteTicketFlow = true;
            orderTicketData = getIntent().getParcelableExtra(EXTRA_PAYMENT_TICKET_DATA);
            selectedCardData = getIntent().getParcelableExtra(EXTRA_CARD_DATA);
        }

        linearLayout_mima = (LinearLayout) findViewById(R.id.linearLayout_password);

        text_selected_title = (TextView) findViewById(R.id.text_selected_title);
        text_cardname = (TextView) findViewById(R.id.text_cardname);
        text_cardname.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changPage(PAGE_MODE.SELECT_CARD_PAGE);
            }
        });

        text_mimaError = (TextView) findViewById(R.id.text_password_can_error_count);
        text_mimaError.setText(String.format(getString(R.string.password_can_error_count), mimaErrorLimit));


        viewPager = (ViewPager) findViewById(R.id.viewpager_card);
        adapter = new CreditCardPagerAdapter(this, CreditCardUtil.GetCreditCardList(this));
        //adapter.EventCallBack(this);
        adapter.eventCallBack = new CreditCardPagerAdapter.EventCallBack() {
            @Override
            public void OnChangeCardPage(int index) {

                if(IsForPaymenteTicketFlow) {
                    ArrayList<CreditCardData> list = CreditCardUtil.GetCreditCardList(CreditCardPaymentLoginActivity.this);
                    index_mainCard =  list.indexOf(selectedCardData);
                }
                else {
                    index_mainCard = index;
                }
                setCardName(index_mainCard);
                viewPager.setCurrentItem(index_mainCard);
                if(CreditCardUtil.GetCreditCardList(CreditCardPaymentLoginActivity.this).size() == 1) {
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
        relativeLayout_viewpager_cards = (RelativeLayout) findViewById(R.id.relativeLayout_viewpager_cards);
        relativeLayout_viewpager_cards.setOnTouchListener(new View.OnTouchListener() {
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
        button_forgot_mima = (Button) findViewById(R.id.button_forgot_password);
        button_forgot_mima.setOnClickListener(this);
        button_selected = (Button) findViewById(R.id.button_selected);
        button_selected.setOnClickListener(this);
        button_selected.setEnabled(false);

        editMima = (EditText) findViewById(R.id.edit_password);
        // 加入TextWatcher監看輸入字串的變化，並做對應的處理
        editMima.addTextChangedListener(new BasicEditTextWatcher(editMima, getString(R.string.password_format_regular_expression)) {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // 做父類別的欄位格式檢核
                super.onTextChanged(s, start, before, count);
                checkNextButtonEnable();
            }

        });

        changPage(PAGE_MODE.CHECK_PASSWORD_PAGE);
    }

    private void changPage(PAGE_MODE mode)
    {
        switch (mode)
        {
            case CHECK_PASSWORD_PAGE:
                this.setCenterTitle(R.string.scan_payment_password_check_title);
                linearLayout_mima.setVisibility(View.VISIBLE);
                button_forgot_mima.setVisibility(View.VISIBLE);
                relativeLayout_viewpager_cards.setVisibility(View.INVISIBLE);
                button_selected.setText(R.string.confirm_payment);
                checkNextButtonEnable();
                break;

            case SELECT_CARD_PAGE:
                this.setCenterTitle(R.string.scan_payment_password_selectcard_title);
                linearLayout_mima.setVisibility(View.GONE);
                button_forgot_mima.setVisibility(View.GONE);
                relativeLayout_viewpager_cards.setVisibility(View.VISIBLE);
                button_selected.setText(R.string.phone_alert_button_back);
                button_selected.setEnabled(true);
                break;

        }

        currentPage = mode;
    }

    @Override
    protected void onResume() {
        super.onResume();

        GetUserPasswordErrorCount();
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
        }
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
            changPage(PAGE_MODE.CHECK_PASSWORD_PAGE);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        // 返回上一頁
        if(currentPage == PAGE_MODE.SELECT_CARD_PAGE){
            changPage(PAGE_MODE.CHECK_PASSWORD_PAGE);
            return;
        }
        super.onBackPressed();
    }

    @Override
    public void onClick(View v) {
        if (SystemClock.elapsedRealtime() - lastClickTime < 1000){
            return;
        }

        lastClickTime = SystemClock.elapsedRealtime();
        int viewId = v.getId();

        if(viewId == R.id.button_forgot_password) {
            Intent intent = new Intent(this, ResetPasswordActivity.class);
            startActivity(intent);
        }
        else if(viewId == R.id.button_selected){
            switch (currentPage)
            {
                case CHECK_PASSWORD_PAGE:
                    checkPassword();
                    break;

                case SELECT_CARD_PAGE:
//                    selectedCardData = CreditCardUtil.GetCreditCardList(this).get(selectedID);
                    changPage(PAGE_MODE.CHECK_PASSWORD_PAGE);
                    break;


            }

        }
    }

    private void checkPassword(){
        // 如果沒有網路連線，顯示提示對話框
        showProgressLoading();
        if (!NetworkUtil.isConnected(this)) {
            dismissProgressLoading();
            showAlertDialog(getString(R.string.msg_no_available_network), android.R.string.ok, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            }, true);
        } else {
            // 呼叫api取得收到的紅包
            try {
                String aesMima = sharedMethods.AESEncrypt(editMima.getText().toString());
                String phoneDecrypt = sharedMethods.AESDecrypt(PreferenceUtil.getPhoneNumber(this));
                String emailDecrypt = sharedMethods.AESDecrypt(PreferenceUtil.getEmail(this));
                GeneralHttpUtil.CertificationUser(PreferenceUtil.getUserId(this),aesMima,
                        phoneDecrypt, emailDecrypt, "2",
                        responseListener, this, TAG);
            } catch (JSONException e) {
                e.printStackTrace();
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }

        }

    }

    /**
     * 檢查是否enable下一步按鈕
     */
    private void checkNextButtonEnable(){
        if(editMima.getBackground().getLevel() == BasicEditTextWatcher.EDIT_LEVEL_CORRECT){
            button_selected.setEnabled(true);
        }else{
            button_selected.setEnabled(false);
        }
    }

    private void setCardName(int cardID)
    {
        String name = CreditCardUtil.GetCreditCardList(this).get(cardID).getCardName();
        String cardNumber = CreditCardUtil.GetCreditCardList(this).get(cardID).getCardNumber();


        text_cardname.setText(name + FormatUtil.toHideCardNumberString(cardNumber));
        selectedCardData = CreditCardUtil.GetCreditCardList(this).get(cardID);
    }

    private void GetUserPasswordErrorCount()
    {
        // 如果沒有網路連線，顯示提示對話框
        if (!NetworkUtil.isConnected(this)) {
            dismissProgressLoading();
            showAlertDialog(getString(R.string.msg_no_available_network), android.R.string.ok, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            }, true);
        } else {
            // 呼叫api取得收到的紅包
            try {
                GeneralHttpUtil.queryPersonalData(responseListenerPersonalData, this, TAG);

                showProgressLoading();
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
    }


    private void ToNextPage()
    {

        if(IsForPaymenteTicketFlow)
        {
            useCreditCardPayment();
//            Intent intent = new Intent(this, CreditCardResultPageActivity.class);
//            intent.putExtra(CreditCardResultPageActivity.EXTRA_CURRENT_PAGE,CreditCardResultPageActivity.ENUM_RESULT_PAGE_TYPE.CREDIT_CARD_PAY_SUCCESS_FOR_ETICKET.toString());
//            startActivity(intent);
        }
        else
        {
            Intent intent = new Intent(this, CreditCardPaymentBarcodeActivity.class);
            intent.putExtra(CreditCardPaymentBarcodeActivity.EXTRA_CARD_DATA, selectedCardData);
            startActivity(intent);
        }
    }

    private void useCreditCardPayment() {
        // 如果沒有網路連線，顯示提示對話框
        if (!NetworkUtil.isConnected(this)) {
            dismissProgressLoading();
            showAlertDialog(getString(R.string.msg_no_available_network), android.R.string.ok, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            }, true);
        } else {
            // 呼叫api取得收到的紅包
            try {
                CreditCardHttpUtil.paymentOrderFormCreditCard(orderTicketData.getOrderToken(), orderTicketData.getOrderId(), selectedCardData.getToken(), responseListenerForPayment, this, TAG);
             //   showProgressLoading();
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
    }

    private void ToPaymentResultPage() {
        ToPaymentResultPage(true, null);
    }

    private void ToPaymentResultPage(boolean IsSuccess, ResponseResult result) {
        Intent intent = new Intent(this, CreditCardResultPageActivity.class);
        if(IsSuccess) {
            intent.putExtra(CreditCardResultPageActivity.EXTRA_CURRENT_PAGE, CreditCardResultPageActivity.ENUM_RESULT_PAGE_TYPE.CREDIT_CARD_PAY_SUCCESS_FOR_ETICKET.toString());
        } else {
            intent.putExtra(CreditCardResultPageActivity.EXTRA_CURRENT_PAGE, CreditCardResultPageActivity.ENUM_RESULT_PAGE_TYPE.CREDIT_CARD_PAY_FAIL_FOR_ETICKET.toString());
            intent.putExtra(CreditCardResultPageActivity.EXTRA_CARD_DATA, selectedCardData);
            if(result.getReturnCode().equals(ResponseResult.RESULT_TICKET_PAYMET_TRANS_RETRY)) {
                intent.putExtra(CreditCardResultPageActivity.EXTRA_TRY_AGAIN, true);
            } else {
                intent.putExtra(CreditCardResultPageActivity.EXTRA_TRY_AGAIN, false);
            }
            intent.putExtra(CreditCardResultPageActivity.EXTRA_ERROR_MESSAGE, result.getReturnMessage());
        }
        intent.putExtra(CreditCardResultPageActivity.EXTRA_PAYMENT_DATA, orderTicketData);
        startActivity(intent);
    }


    public class CardsPageChangeListener implements ViewPager.OnPageChangeListener {

        @Override
        public void onPageSelected(int position) {
            setCardName(position);
//            if(globalVariable.CreditCardList.get(position).getSettedMain())
//                text_selected_title.setVisibility(View.VISIBLE);
//            else
//                text_selected_title.setVisibility(View.INVISIBLE);

            if(CreditCardUtil.GetCreditCardList(CreditCardPaymentLoginActivity.this).size() == 1) {
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
            if (relativeLayout_viewpager_cards != null) {
                relativeLayout_viewpager_cards.invalidate();
            }
        }

        @Override
        public void onPageScrollStateChanged(int arg0) {
        }
    }

    private ResponseListener responseListener= new ResponseListener() {
        @Override
        public void onResponse(ResponseResult result) {
            if(!IsForPaymenteTicketFlow)
                dismissProgressLoading();

            String returnCode = result.getReturnCode();
            // 如果returnCode不是成功
            if(returnCode.equals(ResponseResult.RESULT_SUCCESS) || !GlobalConst.UseOfficialServer)
            {
                //成功
                ToNextPage();
            }
            else
            {
                dismissProgressLoading();
                // 如果不是共同error
                if(!handleCommonError(result, CreditCardPaymentLoginActivity.this)){
                    // TODO 其他不成功的判斷與處理
                    showAlertDialog(result.getReturnMessage());
                    GetUserPasswordErrorCount();

                }
            }
        }
    };

    private ResponseListener responseListenerPersonalData= new ResponseListener() {
        @Override
        public void onResponse(ResponseResult result) {
            dismissProgressLoading();

            String returnCode = result.getReturnCode();
            // 如果returnCode不是成功
            if(returnCode.equals(ResponseResult.RESULT_SUCCESS))
            {
                int errorCount = GeneralResponseBodyUtil.getPasswordErrorCount(result.getBody());
                text_mimaError.setText(String.format(getString(R.string.password_can_error_count), (mimaErrorLimit - errorCount)));
            }
            else
            {
                // 執行預設的錯誤處理
                handleResponseError(result, CreditCardPaymentLoginActivity.this);
            }
        }
    };

    private ResponseListener responseListenerForPayment = new ResponseListener() {
        @Override
        public void onResponse(ResponseResult result) {


            String returnCode = result.getReturnCode();

            EventAnalyticsUtil.addSpecialEvent(CreditCardPaymentLoginActivity.this, new SpecialEvent(SpecialEvent.TYPE_SERVER_API, EventAnalyticsUtil.logFormatToAPI(result.getApiName(), String.format("Return code: %1$s, Message: %2$s", returnCode, result.getReturnMessage()))));
            // 如果returnCode不是成功
            if(returnCode.equals(ResponseResult.RESULT_SUCCESS) || !GlobalConst.UseOfficialServer)
            {
                //成功
                dismissProgressLoading();
                ToPaymentResultPage();
            }
            else
            {

                // 如果returnCode是成功
                if (returnCode.equals(ResponseResult.RESULT_TICKET_PAYMET_TRANS_RETRY)) {
                    // 取得列表
                    dismissProgressLoading();
                    ToPaymentResultPage(false, result);
                    // showRetryAlert(result.getReturnMessage());
                } else if(!handleCommonError(result, CreditCardPaymentLoginActivity.this)){
                    // TODO 其他不成功的判斷與處理
//                    showRetryAlert(result.getReturnMessage());
//                    showAlertDialog(result.getReturnMessage(), android.R.string.ok, new DialogInterface.OnClickListener() {
//                        @Override
//                        public void onClick(DialogInterface dialog, int which) {
//                            dismissProgressLoading();
//                            dialog.dismiss();
//                        }
//                    }, false);
                    dismissProgressLoading();
                    ToPaymentResultPage(false, result);
                } else {
                    dismissProgressLoading();
                }
            }
        }
    };


    protected void showRetryAlert(String message) {
        new AlertDialog.Builder(this)
                .setMessage(message)
                .setPositiveButton(R.string.try_one, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        useCreditCardPayment();
                    }
                })
                .setNegativeButton(R.string.go_back, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dismissProgressLoading();
                        finish();
                    }
                }).setCancelable(false).show();
    }

}
