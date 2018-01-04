package tw.com.taishinbank.ewallet.controller.creditcard;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;

import tw.com.taishinbank.ewallet.R;
import tw.com.taishinbank.ewallet.controller.ActivityBase;
import tw.com.taishinbank.ewallet.controller.MainActivity;
import tw.com.taishinbank.ewallet.model.creditcard.CreditCardData;
import tw.com.taishinbank.ewallet.model.creditcard.CreditCardTransaction;
import tw.com.taishinbank.ewallet.model.extra.TicketOrderData;
import tw.com.taishinbank.ewallet.util.ContactUtil;
import tw.com.taishinbank.ewallet.util.FormatUtil;

public class CreditCardResultPageActivity extends ActivityBase implements View.OnClickListener{

    public static final String EXTRA_CURRENT_PAGE = "extra_current_page";
    public static final String EXTRA_PAYMENT_DATA = "extra_payment_data";
    public static final String EXTRA_CARD_DATA = "extra_card_data";
    public static final String EXTRA_TRY_AGAIN = "extra_try_again";
    public static final String EXTRA_ERROR_MESSAGE = "extra_error_message";
    public static final String EXTRA_NEXT_TO_PAGE = "extra_next_next";

    public static final int PAGE_ORDER_CONFIRM = 11111;
    private int goPage = 0;
    private CreditCardData inputCardData;
    private CreditCardTransaction inputCreditCardTransaction;
    private CreditCardData selectedCardData;

    //For eTicket
    private TicketOrderData ticketOrderData;

    public enum  ENUM_RESULT_PAGE_TYPE
    {
        CREATE_CREDIT_CARD_PAGE,
        DELETE_CREDIT_CARD_PAGE,
        CREDIT_CARD_PAY_SUCCESS,
        CREDIT_CARD_PAY_FAIL,
        CREDIT_CARD_PAY_SUCCESS_FOR_ETICKET,
        CREDIT_CARD_PAY_FAIL_FOR_ETICKET
    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        ENUM_RESULT_PAGE_TYPE page = ENUM_RESULT_PAGE_TYPE.valueOf(getIntent().getStringExtra(EXTRA_CURRENT_PAGE));

        switch (page)
        {
            case CREATE_CREDIT_CARD_PAGE:
                CreateCreateCardPage();
                break;
            case DELETE_CREDIT_CARD_PAGE:
                CreateDeleteCardPage();
                break;
            case CREDIT_CARD_PAY_SUCCESS:
                CreditCardPaymentResultSuccess();
                break;
            case CREDIT_CARD_PAY_FAIL:
                CreditCardPaymentResultFail();
                break;
            case CREDIT_CARD_PAY_SUCCESS_FOR_ETICKET:
                CreditCardPaymentResultSuccessForeTicket();
                break;
            case CREDIT_CARD_PAY_FAIL_FOR_ETICKET:
                CreditCardPaymentResultFailForeTicket();
                break;
        }

    }

    private void CreditCardPaymentResultSuccessForeTicket()
    {
        setContentView(R.layout.activity_payment_result);

        // 設定置中的標題與返回鈕
        this.setCenterTitle(R.string.card_payment_result_title);
        this.getSupportActionBar().setDisplayHomeAsUpEnabled(false);

        ticketOrderData = getIntent().getParcelableExtra(EXTRA_PAYMENT_DATA);
        TextView result_title = (TextView) findViewById(R.id.text_result_title);
        result_title.setText(getString(R.string.result_title_payment_success));

        TextView textDate = (TextView) findViewById(R.id.txt_date);
        ImageView imagePhoto =(ImageView) findViewById(R.id.img_photo);
        ImageView imageBanner =(ImageView) findViewById(R.id.img_banner);
        TextView textPaymentInfo = (TextView) findViewById(R.id.text_payment_info);
        TextView textPaymentContent = (TextView) findViewById(R.id.text_payment_content);
        TextView textAmount = (TextView) findViewById(R.id.txt_amount);

        textDate.setText(FormatUtil.toTimeFormatted(ticketOrderData.getLastUpdate(), false));

        textAmount.setText(FormatUtil.toDecimalFormatFromString(ticketOrderData.getPrice()));
        textPaymentInfo.setText(ticketOrderData.getStoreName());

        ImageView image_status = (ImageView) findViewById(R.id.image_result_status);
        image_status.setImageResource(R.drawable.ic_credit_card_shop_succeed);
        // Set image
        imagePhoto.setVisibility(View.GONE);

        textPaymentContent.setVisibility(View.VISIBLE);
        textPaymentContent.setText(ticketOrderData.getTitle());
        //TODO 確認連結
        imageBanner.setVisibility(View.VISIBLE);
        if(!TextUtils.isEmpty(ticketOrderData.getIconUrl())) {
            File imgURL = new File(ticketOrderData.getIconUrl());
            String imgFilePath = ContactUtil.TicketFolderPath + File.separator + imgURL.getName();

            File imgFile = new File(imgFilePath);
            if (imgFile.exists()) {
                Bitmap imgBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
                imageBanner.setImageBitmap(imgBitmap);
            } else {
                imageBanner.setImageResource(R.drawable.img_banner_default);
            }
        } else {
            imageBanner.setImageResource(R.drawable.img_banner_default);
        }

        Button button_left = (Button) findViewById(R.id.button_left);
        button_left.setText(R.string.credit_trans_history);
        button_left.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToCreditCardHistory();
            }
        });

        Button button_right = (Button) findViewById(R.id.button_right_or_fill);
        button_right.setText(getString(R.string.invoice_tickets));
        button_right.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToExtraTicketList();
            }
        });
    }

    private void CreditCardPaymentResultFailForeTicket()
    {
        setContentView(R.layout.activity_payment_result);

        // 設定置中的標題與返回鈕
        this.setCenterTitle(R.string.card_payment_result_title);
        this.getSupportActionBar().setDisplayHomeAsUpEnabled(false);

        ticketOrderData = getIntent().getParcelableExtra(EXTRA_PAYMENT_DATA);
        selectedCardData = getIntent().getParcelableExtra(EXTRA_CARD_DATA);
        boolean bTryAgain = getIntent().getBooleanExtra(EXTRA_TRY_AGAIN, false);
        String message = getIntent().getStringExtra(EXTRA_ERROR_MESSAGE);
        TextView result_title = (TextView) findViewById(R.id.text_result_title);
        result_title.setText(getString(R.string.result_title_payment_fail));
        result_title.setTextColor(getResources().getColor(R.color.sv_result_title_failure));

        TextView textDate = (TextView) findViewById(R.id.txt_date);
        ImageView imagePhoto =(ImageView) findViewById(R.id.img_photo);
        ImageView imageBanner =(ImageView) findViewById(R.id.img_banner);
        TextView textPaymentInfo = (TextView) findViewById(R.id.text_payment_info);
        TextView textPaymentContent = (TextView) findViewById(R.id.text_payment_content);
        TextView textAmount = (TextView) findViewById(R.id.txt_amount);
        TextView textDollarSign = (TextView) findViewById(R.id.txt_dollar_sign);
        textAmount.setVisibility(View.GONE);

        textDate.setText(FormatUtil.toTimeFormatted(ticketOrderData.getLastUpdate(), false));
//        String totalAmount = String.valueOf(Integer.valueOf(ticketOrderData.getPrice()) * Integer.valueOf(ticketOrderData.getCount()));
//        textAmount.setText(FormatUtil.toDecimalFormat(totalAmount));
        textDollarSign.setText(message);
        textPaymentInfo.setText(ticketOrderData.getStoreName());

        ImageView image_status = (ImageView) findViewById(R.id.image_result_status);
        image_status.setImageResource(R.drawable.ic_credit_card_shop_failed);

        // Set image
        imagePhoto.setVisibility(View.GONE);
        textPaymentContent.setVisibility(View.VISIBLE);
        textPaymentContent.setText(ticketOrderData.getTitle());
        //TODO 確認連結
        imageBanner.setVisibility(View.VISIBLE);
        if(!TextUtils.isEmpty(ticketOrderData.getIconUrl())) {
            File imgURL = new File(ticketOrderData.getIconUrl());
            String imgFilePath = ContactUtil.TicketFolderPath + File.separator + imgURL.getName();

            File imgFile = new File(imgFilePath);
            if (imgFile.exists()) {
                Bitmap imgBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
                imageBanner.setImageBitmap(imgBitmap);
            } else {
                imageBanner.setImageResource(R.drawable.img_banner_default);
            }
        } else {
            imageBanner.setImageResource(R.drawable.img_banner_default);
        }

        Button button_left = (Button) findViewById(R.id.button_left);
        button_left.setText(R.string.credit_trans_history);
        button_left.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToCreditCardHistory();
            }
        });

        Button button_right = (Button) findViewById(R.id.button_right_or_fill);
        if (!bTryAgain) {
            button_right.setEnabled(false);
        }

        button_right.setText(R.string.try_one);
        button_right.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToTryAgain(true);
                //finish();
            }
        });
    }

    private void CreditCardPaymentResultSuccess()
    {
        setContentView(R.layout.activity_payment_result);

        // 設定置中的標題與返回鈕
        this.setCenterTitle(R.string.card_payment_result_title);
        this.getSupportActionBar().setDisplayHomeAsUpEnabled(false);

        inputCreditCardTransaction = getIntent().getParcelableExtra(EXTRA_PAYMENT_DATA);
        TextView result_title = (TextView) findViewById(R.id.text_result_title);
        result_title.setText(getString(R.string.result_title_payment_success));

        TextView textDate = (TextView) findViewById(R.id.txt_date);
        ImageView imagePhoto =(ImageView) findViewById(R.id.img_photo);
        TextView textPaymentInfo = (TextView) findViewById(R.id.text_payment_info);
        TextView textAmount = (TextView) findViewById(R.id.txt_amount);

        textDate.setText(FormatUtil.toTimeFormatted(inputCreditCardTransaction.getMerchantTradeDate(), false));
        textAmount.setText(FormatUtil.toDecimalFormat(inputCreditCardTransaction.getTradeAmount()));
        textPaymentInfo.setText(inputCreditCardTransaction.getStoreName());

        ImageView image_status = (ImageView) findViewById(R.id.image_result_status);
        image_status.setImageResource(R.drawable.ic_credit_card_shop_succeed);

        // Set image
        imagePhoto.setBackgroundResource(R.drawable.img_default_photo_gary);
        imagePhoto.setImageResource(R.drawable.img_taishin_photo);


        Button button_left = (Button) findViewById(R.id.button_left);
        button_left.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToHome(MainActivity.TAB_CREDIT);
            }
        });

        Button button_right = (Button) findViewById(R.id.button_right_or_fill);
        button_right.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToCreditCardHistory();
            }
        });
    }

    private void CreditCardPaymentResultFail()
    {
        setContentView(R.layout.activity_payment_result);

        // 設定置中的標題與返回鈕
        this.setCenterTitle(R.string.card_payment_result_title);
        this.getSupportActionBar().setDisplayHomeAsUpEnabled(false);

        inputCreditCardTransaction = getIntent().getParcelableExtra(EXTRA_PAYMENT_DATA);
        TextView result_title = (TextView) findViewById(R.id.text_result_title);
        result_title.setText(getString(R.string.result_title_payment_fail));
        result_title.setTextColor(getResources().getColor(R.color.sv_result_title_failure));

        TextView textDate = (TextView) findViewById(R.id.txt_date);
        ImageView imagePhoto =(ImageView) findViewById(R.id.img_photo);
        TextView textPaymentInfo = (TextView) findViewById(R.id.text_payment_info);
        TextView textAmount = (TextView) findViewById(R.id.txt_amount);

        textDate.setText(FormatUtil.toTimeFormatted(inputCreditCardTransaction.getMerchantTradeDate(), false));
        textAmount.setText(FormatUtil.toDecimalFormat(inputCreditCardTransaction.getTradeAmount()));
        textPaymentInfo.setText(inputCreditCardTransaction.getStoreName());

        ImageView image_status = (ImageView) findViewById(R.id.image_result_status);
        image_status.setImageResource(R.drawable.ic_credit_card_shop_failed);

        // Set image
        imagePhoto.setBackgroundResource(R.drawable.img_default_photo_gary);
        imagePhoto.setImageResource(R.drawable.img_taishin_photo);


        Button button_left = (Button) findViewById(R.id.button_left);
        button_left.setText(R.string.credit_trans_history);
        button_left.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToCreditCardHistory();
            }
        });

        Button button_right = (Button) findViewById(R.id.button_right_or_fill);
        button_right.setText(R.string.try_one);
        button_right.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //finish();
                goToTryAgain(false);
            }
        });
    }

    private void CreateCreateCardPage(){
        setContentView(R.layout.activity_credit_card_result);

        // 設定置中的標題與返回鈕
        this.setCenterTitle(R.string.result_appbar_create);
        this.getSupportActionBar().setDisplayHomeAsUpEnabled(false);

        inputCardData = getIntent().getParcelableExtra(CreditCardData.EXTRA_CREDIT_CARD_DATA);
        if(getIntent().hasExtra(EXTRA_NEXT_TO_PAGE)) {
            goPage = getIntent().getIntExtra(EXTRA_NEXT_TO_PAGE, 0);
        }
        TextView result_title = (TextView) findViewById(R.id.text_result_title);
        result_title.setText(getString(R.string.result_title_create));

        TextView message_title = (TextView) findViewById(R.id.text_message_title);
        message_title.setText(getString(R.string.result_message_title_create));

        TextView card_bank = (TextView) findViewById(R.id.text_card_bank);
        card_bank.setText(getString(R.string.title_activity_welcome));

        TextView card_info = (TextView) findViewById(R.id.text_card_info);
        String text_info = "";
        if(!inputCardData.getCardNumber().equals("")) {
            String[] cardNumber = inputCardData.getCardNumber().split("-");
            text_info = inputCardData.getCardName() + "(⋯⋯" + cardNumber[3] + ")";
        }
        card_info.setText(text_info);

        ImageView image_status = (ImageView) findViewById(R.id.image_result_status);
        image_status.setImageResource(R.drawable.ic_credit_card_add_susseed);

        Button button_left = (Button) findViewById(R.id.button_left);
        button_left.setVisibility(View.GONE);
        if(goPage == PAGE_ORDER_CONFIRM) {
            Button button_goback_crad_maimpage = (Button) findViewById(R.id.button_right_or_fill);
            button_goback_crad_maimpage.setText(getString(R.string.button_goback_order_confirm));
            button_goback_crad_maimpage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    finish();
                }
            });
        } else {
            Button button_goback_crad_maimpage = (Button) findViewById(R.id.button_right_or_fill);
            button_goback_crad_maimpage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    goToHome(MainActivity.TAB_CREDIT);
                }
            });
        }
    }

    private void CreateDeleteCardPage(){
        setContentView(R.layout.activity_credit_card_result);

        // 設定置中的標題與返回鈕
        this.setCenterTitle(R.string.result_appbar_delete);
        this.getSupportActionBar().setDisplayHomeAsUpEnabled(false);


        inputCardData = getIntent().getParcelableExtra(CreditCardData.EXTRA_CREDIT_CARD_DATA);

        TextView result_title = (TextView) findViewById(R.id.text_result_title);
        result_title.setText(getString(R.string.result_title_delete));

        TextView message_title = (TextView) findViewById(R.id.text_message_title);
        message_title.setText(getString(R.string.result_message_title_delete));

        TextView card_bank = (TextView) findViewById(R.id.text_card_bank);
        card_bank.setText(getString(R.string.title_activity_welcome));

        TextView card_info = (TextView) findViewById(R.id.text_card_info);
        String text_info = "";
        if(!inputCardData.getCardNumber().equals("")) {
            String[] cardNumber = inputCardData.getCardNumber().split("-");
            text_info = inputCardData.getCardName() + "(⋯⋯" + cardNumber[3] + ")";
        }
        card_info.setText(text_info);

        ImageView image_status = (ImageView) findViewById(R.id.image_result_status);
        image_status.setImageResource(R.drawable.ic_credit_card_del_susseed);

        Button button_left = (Button) findViewById(R.id.button_left);
        button_left.setVisibility(View.GONE);
        Button button_goback_crad_maimpage = (Button) findViewById(R.id.button_right_or_fill);
        button_goback_crad_maimpage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToHome(MainActivity.TAB_CREDIT);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
//

    }
    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onClick(View v) {
        int viewId = v.getId();


    }

    @Override
    public void onBackPressed() {
//        Intent intent = new Intent(this, MainActivity.class);
//        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//        intent.putExtra(MainActivity.EXTRA_GO_PAGE_TAG, MainActivity.TAB_CREDIT);
//        startActivity(intent);
    }

    /**
     * 開啟首頁
     */
    private void goToHome(String page){
        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        if(page != null) {
            intent.putExtra(MainActivity.EXTRA_GO_PAGE_TAG, page);
        }
        startActivity(intent);
    }
    /**
     * 開啟信用卡內的子頁
     */
    private void goToCreditCardHistory(){
        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra(MainActivity.EXTRA_GO_CREDIT_HISTORY, "");
        startActivity(intent);
    }

    /**
     * 開啟加值內的子頁
     */
    private void goToExtraTicketList(){
        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra(MainActivity.EXTRA_GO_TICKET_LIST, "");
        startActivity(intent);
    }

    private void goToTryAgain(boolean isForTicket) {
        Intent intent = new Intent(CreditCardResultPageActivity.this, CreditCardPaymentLoginActivity.class);
        if(isForTicket) {
            intent.putExtra(CreditCardPaymentLoginActivity.EXTRA_CARD_DATA, selectedCardData);
            intent.putExtra(CreditCardPaymentLoginActivity.EXTRA_PAYMENT_TICKET_DATA, ticketOrderData);
        }
        startActivity(intent);
    }

}
