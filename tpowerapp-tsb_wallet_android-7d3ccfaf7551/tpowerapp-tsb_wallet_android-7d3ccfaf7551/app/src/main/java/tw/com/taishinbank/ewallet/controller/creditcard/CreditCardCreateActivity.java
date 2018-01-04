package tw.com.taishinbank.ewallet.controller.creditcard;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

import io.card.payment.CardIOActivity;
import io.card.payment.CreditCard;
import tw.com.taishinbank.ewallet.R;
import tw.com.taishinbank.ewallet.controller.ActivityBase;
import tw.com.taishinbank.ewallet.interfaces.GlobalConst;
import tw.com.taishinbank.ewallet.model.creditcard.CreditCardData;
import tw.com.taishinbank.ewallet.model.log.SpecialEvent;
import tw.com.taishinbank.ewallet.util.CreditCardUtil;
import tw.com.taishinbank.ewallet.util.E7PayUtil;
import tw.com.taishinbank.ewallet.util.EventAnalyticsUtil;
import tw.com.taishinbank.ewallet.util.FormatUtil;
import tw.com.taishinbank.ewallet.util.NetworkUtil;
import tw.com.taishinbank.ewallet.util.PreferenceUtil;
import tw.com.taishinbank.ewallet.util.sharedMethods;

public class CreditCardCreateActivity extends ActivityBase implements View.OnClickListener{

    public static final String EXTRA_FROM_ORDER_PAGE = "extra_from_order_page";

    private static final int SCEN_CARD_REQUEST = 0;
    private static final int CARD_AUTH_REQUEST = 1;
    private static final int CARD_AUTH_TWO_REQUEST = 2;


    private EditText editText_surname, editText_firstname, editText_cardname, editText_phone;
    private EditText editText_cardnumber; //editText_cardnumber_2, editText_cardnumber_3, editText_cardnumber_4;
    private EditText editText_expirationdate, editText_securitycode;
    private CheckBox checkBox_setmaincard;
    private Button button_complete, button_scan_card;

    private String phoneNumber, cardType = CreditCardData.ENUM_CARD_TYPE.Empty.toString();

    private CreditCardData addCardData = null;
    private E7PayUtil.E7Pay_CardDataMode e7PayCardData = null;
    private E7PayUtil e7PayUtil;

    private boolean isGoBackPage = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_credit_card_new);

        // 設定置中的標題與返回鈕
        this.setCenterTitle(R.string.add_newcreditcard_title);
        this.getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        if(getIntent().hasExtra(EXTRA_FROM_ORDER_PAGE)) {
            isGoBackPage = getIntent().getBooleanExtra(EXTRA_FROM_ORDER_PAGE, false);
        }

        // find
        editText_surname = (EditText) findViewById(R.id.edittext_surname);
        editText_surname.addTextChangedListener(textWatcher);
        editText_firstname = (EditText) findViewById(R.id.edittext_firstname);
        editText_firstname.addTextChangedListener(textWatcher);
        editText_cardname = (EditText) findViewById(R.id.edittext_cardname);
        editText_cardname.addTextChangedListener(textWatcher);
        editText_phone = (EditText) findViewById(R.id.edittext_phone);

        editText_phone.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    String text = editText_phone.getText().toString();
                    text = text.replace("-", "");
                    if (text.length() == 10) {
                        editText_phone.setText(FormatUtil.getEncodedCellPhoneNumber(text));
                    }
                    //  editText_cardnumber.requestFocus();
                } else {
                    editText_phone.setText("");
                    phoneNumber = "";
                }
                checkInputItem();
            }
        });
        String phoneDecrypt = null;
        try {
            phoneDecrypt = sharedMethods.AESDecrypt(PreferenceUtil.getPhoneNumber(this));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        if(phoneDecrypt != null && !phoneDecrypt.equals("")) {
            phoneNumber = phoneDecrypt;
            String textPhone = FormatUtil.getEncodedCellPhoneNumber(phoneNumber);
            editText_phone.setText(textPhone);
        }

        editText_phone.addTextChangedListener(new TextWatcher() {
            int currInt = 0;
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                String text = editText_phone.getText().toString();
                text = text.replace("-", "");
                currInt = text.length();
                phoneNumber = text;
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                String text = editText_phone.getText().toString();
                text = text.replace("-", "");

                if ( text.length() < 5 &&(text.length() % 4 == 0) && ( text.length() > currInt )) {
                    editText_phone.setText(editText_phone.getText() + "-");
                    editText_phone.setSelection(editText_phone.length());
                }
                else if ( text.length() > 4 &&((text.length()-4) % 3 == 0) && ( text.length() > currInt )) {
                    editText_phone.setText(editText_phone.getText() + "-");
                    editText_phone.setSelection(editText_phone.length());
                }

                if(text.length() == 10)
                    editText_cardnumber.requestFocus();
            }
        });

        editText_expirationdate = (EditText) findViewById(R.id.edittext_expirationdate);
//        editText_expirationdate.addTextChangedListener(textWatcher);
        editText_securitycode = (EditText) findViewById(R.id.edittext_securitycode);
        editText_securitycode.addTextChangedListener(textWatcher);
        editText_cardnumber = (EditText) findViewById(R.id.edittext_cardnumber);
        editText_cardnumber.addTextChangedListener(new TextWatcher() {
            int currInt = 0;
            String beforeText = "";
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                String text = editText_cardnumber.getText().toString();
                beforeText = text;
                text = text.replace("-", "");
                currInt = text.length();
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                checkInputItem();
            }

            @Override
            public void afterTextChanged(Editable s) {
                String text = editText_cardnumber.getText().toString();
                String afterText = text;
                text = text.replace("-", "");
                if ((text.length() % 4 == 0) && ( text.length() > currInt )) {
                    editText_cardnumber.setText(editText_cardnumber.getText() + "-");
                    editText_cardnumber.setSelection(editText_cardnumber.length());
                }
                else if(( afterText.length() < beforeText.length() )) // 長度包含-
                {
                    String delText = beforeText.replace(s, "");
                    if(delText.equals("-"))
                    {
                        afterText =  afterText.substring(0, afterText.length()-1);
                        editText_cardnumber.setText(afterText);
                        editText_cardnumber.setSelection(editText_cardnumber.length());
                    }
                }
            }
        });


        editText_expirationdate.addTextChangedListener( new TextWatcher() {
            String beforeText = "";
            int currInt = 0;
            boolean isMonthS = false;
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                String text = editText_expirationdate.getText().toString();
                beforeText = text;
                if(beforeText.length() == 1 && beforeText.equals("1"))
                {
                    isMonthS = true;
                }


                text = text.replace("/", "");
                currInt = text.length();


            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                checkInputItem();
            }

            @Override
            public void afterTextChanged(Editable s) {
                String text = editText_expirationdate.getText().toString();
//                int afterInt = text.length();
                if(( text.length() > beforeText.length() )) {
                    if(beforeText.length() == 0 && (text.length() - beforeText.length() == 1))
                    {
                        if(Integer.valueOf(text) > 1)
                        {
                            text = "0" + text;
                            editText_expirationdate.setText(text);
                            return;
                        }

                    }
                    else if(isMonthS)
                    {
                        if(text.length() == 2 && Integer.valueOf(text) > 12)
                        {
                            editText_expirationdate.setText(beforeText);
                            editText_expirationdate.setSelection(editText_expirationdate.length());
                            return;
                        }
                    }

                }


                if(text.length() > currInt && text.length() == 2) {
                    editText_expirationdate.setText(text + "/");
                    editText_expirationdate.setSelection(editText_expirationdate.length());
                }
                else if(( text.length() < beforeText.length() )) // 長度包含-
                {
                    String delText = beforeText.replace(s, "");
                    if(delText.equals("/"))
                    {
                        text =  text.substring(0, text.length()-1);
                        editText_expirationdate.setText(text);
                        editText_expirationdate.setSelection(editText_expirationdate.length());
                    }
                }
            }
        });

        checkBox_setmaincard = (CheckBox) findViewById(R.id.checkbox_setmaincard);
        if(CreditCardUtil.GetCreditCardList(this).size() == 0) {
            checkBox_setmaincard.setChecked(true);
        }
        button_complete = (Button) findViewById(R.id.button_complete);
        button_complete.setOnClickListener(this);
        button_scan_card = (Button) findViewById(R.id.button_scan_card);
        button_scan_card.setOnClickListener(this);

        ImageView button_info = (ImageView) findViewById(R.id.icon_info_phone);
        button_info.setOnClickListener(this);
        TextView textAuthInfo = (TextView) findViewById(R.id.text_auth_info);
        textAuthInfo.setOnClickListener(this);

        button_complete.setEnabled(false);
    }

    TextWatcher textWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            checkInputItem();
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    };

    @Override
    protected void onResume() {
        super.onResume();
    }
    @Override
    public void onPause() {
        super.onPause();
        hideKeyboard();
    }

    @Override
    public void onClick(View v) {
        int viewId = v.getId();
        if(viewId == R.id.button_complete)
        {
            AddCreditCard();
        }
        else if(viewId == R.id.button_scan_card)
        {
            Intent scanIntent = new Intent(this, CardIOActivity.class);

            //scanIntent.putExtra(CardIOActivity.EXTRA_SCAN_RESULT, true);
            scanIntent.putExtra(CardIOActivity.EXTRA_SCAN_OVERLAY_LAYOUT_ID, R.layout.fragment_creditcard_scan_layout);
            scanIntent.putExtra(CardIOActivity.EXTRA_SCAN_EXPIRY, true);
            scanIntent.putExtra(CardIOActivity.EXTRA_REQUIRE_EXPIRY, true); // default: false
            scanIntent.putExtra(CardIOActivity.EXTRA_REQUIRE_CVV, false); // default: false
            scanIntent.putExtra(CardIOActivity.EXTRA_REQUIRE_POSTAL_CODE, false); // default: false
            scanIntent.putExtra(CardIOActivity.EXTRA_REQUIRE_CARDHOLDER_NAME, false); // default: false

            scanIntent.putExtra(CardIOActivity.EXTRA_HIDE_CARDIO_LOGO, true); //true : Hide paypal logo
            scanIntent.putExtra(CardIOActivity.EXTRA_SUPPRESS_CONFIRMATION, true); //true : Hide confirmation page
            scanIntent.putExtra(CardIOActivity.EXTRA_SCAN_INSTRUCTIONS, ""); // 插入掃描框中的文字

            // hides the manual entry button
            // if set, developers should provide their own manual entry mechanism in the app
            scanIntent.putExtra(CardIOActivity.EXTRA_SUPPRESS_MANUAL_ENTRY, true); // default: false

           // scanIntent.putExtra(CardIOActivity.EXTRA_SUPPRESS_SCAN, true);
            // matches the theme of your application
            scanIntent.putExtra(CardIOActivity.EXTRA_KEEP_APPLICATION_THEME, true); // default: false
            // MY_SCAN_REQUEST_CODE is arbitrary and is only used within this activity.
            startActivityForResult(scanIntent, SCEN_CARD_REQUEST);
        }
        else if(viewId == R.id.icon_info_phone)
        {
            this.showAlertDialog(getString(R.string.phone_alert_info), R.string.phone_alert_button_back, R.string.phone_alert_button_callservice,
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    }
                ,
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Intent callIntent = new Intent(Intent.ACTION_DIAL);
                            callIntent.setData(Uri.parse("tel:" + "02-26553355"));
                            startActivity(callIntent);
                        }
                    }

                , true);
        } else if(viewId == R.id.text_auth_info) {
            this.showAlertDialog(getString(R.string.title_auth_info), getString(R.string.title_auth_info_message), R.string.button_confirm, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            }, false);
        }
    }

    private void checkInputItem()
    {
        boolean checkLossStatus = false;
        boolean focusbleStatus = false;
        String toastString = "";
        if(editText_cardname.getText().toString().equals(""))
        {
            if(!toastString.equals(""))
                toastString += "\n";
            toastString += getString(R.string.title_input_card_name);
            checkLossStatus = true;
            focusbleStatus = true;

        }

        if(editText_cardnumber.getText().toString().equals(""))
        {
            checkLossStatus = true;
            if(!toastString.equals(""))
                toastString += "\n";
            toastString += getString(R.string.title_input_card_number);
            if(!focusbleStatus) {
                focusbleStatus = true;
            }
        }

        if(editText_expirationdate.getText().toString().equals(""))
        {
            checkLossStatus = true;
            if(!toastString.equals(""))
                toastString += "\n";
            toastString += getString(R.string.title_input_expiratdata);
            if(!focusbleStatus) {
                focusbleStatus = true;
            }
        }

        if(editText_securitycode.getText().toString().equals(""))
        {
            checkLossStatus = true;
            if(!toastString.equals(""))
                toastString += "\n";
            toastString += getString(R.string.title_input_cvv2);
            if(!focusbleStatus) {
                focusbleStatus = true;
            }
        }

        if(editText_phone.getText().toString().equals(""))
        {
            checkLossStatus = true;
            if(!toastString.equals(""))
                toastString += "\n";

            toastString += getString(R.string.edit_personal_phone_title);
            if(!focusbleStatus) {
                focusbleStatus = true;
            }
        }

        if(checkLossStatus) {
          //  Toast.makeText(this, toastString, Toast.LENGTH_SHORT).show();
            button_complete.setEnabled(false);
        }
        else
            button_complete.setEnabled(true);
    }

    private boolean checkLossInputData()
    {
        boolean checkLossStatus = false;
        boolean focusbleStatus = false;
        String toastString = "";
        if(editText_cardname.getText().toString().equals(""))
        {
            if(!toastString.equals(""))
                toastString += "\n";
            toastString += getString(R.string.title_input_card_name);
            editText_cardname.requestFocus();
            checkLossStatus = true;
            focusbleStatus = true;

        }

        if(editText_cardnumber.getText().toString().equals(""))
        {
            checkLossStatus = true;
            if(!toastString.equals(""))
                toastString += "\n";
            toastString += getString(R.string.title_input_card_number);
            if(!focusbleStatus) {
                editText_cardnumber.requestFocus();
                focusbleStatus = true;
            }
        }

        if(editText_expirationdate.getText().toString().equals(""))
        {
            checkLossStatus = true;
            if(!toastString.equals(""))
                toastString += "\n";
            toastString += getString(R.string.title_input_expiratdata);
            if(!focusbleStatus) {
                editText_expirationdate.requestFocus();
                focusbleStatus = true;
            }
        }

        if(editText_securitycode.getText().toString().equals(""))
        {
            checkLossStatus = true;
            if(!toastString.equals(""))
                toastString += "\n";
            toastString += getString(R.string.title_input_cvv2);
            if(!focusbleStatus) {
                editText_securitycode.requestFocus();
                focusbleStatus = true;
            }
        }

        if(editText_phone.getText().toString().equals(""))
        {
            checkLossStatus = true;
            if(!toastString.equals(""))
                toastString += "\n";

            toastString += getString(R.string.edit_personal_phone_title);
            if(!focusbleStatus) {
                editText_phone.requestFocus();
                focusbleStatus = true;
            }
        }

        if(checkLossStatus) {
            Toast.makeText(this, toastString, Toast.LENGTH_SHORT).show();
            return true;
        }
        else
            return false;
    }

    private void AddCreditCard() {

        // 如果沒有網路連線，顯示提示對話框
        if(!NetworkUtil.isConnected(this)){
            this.showAlertDialog(getString(R.string.msg_no_available_network), android.R.string.ok, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            }, true);
            return ;
        }

        addCardData = new CreditCardData();
        addCardData.setCardName(editText_cardname.getText().toString());
        addCardData.setCardNumber(editText_cardnumber.getText().toString());
        addCardData.setCardExpireDate(editText_expirationdate.getText().toString());
        addCardData.setCardType(CreditCardData.ENUM_CARD_TYPE.valueOf(this.cardType));
        addCardData.setCardBank("台新銀行");
        addCardData.setCardKey("");
        addCardData.setToken("");
        addCardData.setTokenExpire("");

        if(checkLossInputData() || IsExistCreditCard(addCardData))
            return;

        if(!GlobalConst.UseOfficialServer) {
            CreateCreditCardToDB();
            return;
        }
        this.showProgressLoading();


        //3D驗證
//        e7PayCardData = new E7PayUtil.E7Pay_CardDataMode(memberID,
//                "0901000000",
//                "4147635300014007", "0633", "967",
//                "AndroidTest", PreferenceUtil.getWalletToken(this));

       e7PayCardData = new E7PayUtil.E7Pay_CardDataMode(PreferenceUtil.getMemNO(this),
                 phoneNumber.toString(),
                 addCardData.getCardNumber().replace("-", ""),
                 addCardData.getCardExpireDate().replace("/",""),
                 editText_securitycode.getText().toString(),
                 addCardData.getCardName(),
                 PreferenceUtil.getDeviceID(this));
//
        e7PayUtil = new E7PayUtil(this);
        e7PayUtil.setOnCardAuthsListener(onCardAuthsListeren);
        e7PayUtil.GetCreditCardAuth(e7PayCardData, 1);

    }

    private void CreateCreditCardToDB()
    {
        ArrayList<CreditCardData> cardDatasList = new ArrayList<>();
        cardDatasList.addAll(CreditCardUtil.GetCreditCardList(this));
        //驗證完成
        if(checkBox_setmaincard.isChecked()) {
            addCardData.setSettedMain(true);

            if(cardDatasList.size() != 0) {
                for (CreditCardData card : cardDatasList) {
                    card.setSettedMain(false);
                    CreditCardUtil.DB_Updata(this, card);
                    // cardDBHelper.update(card, CreditCardEntry._ID + " = " + card.getCardID(), null);
                }
            }
        } else {
            if(cardDatasList.size() == 0) {
                addCardData.setSettedMain(true);
            } else {
                addCardData.setSettedMain(false);
            }
        }



        CreditCardUtil.DB_insert(this, addCardData);
        this.dismissProgressLoading();


        Intent intent = new Intent(getApplication(), CreditCardResultPageActivity.class);

        intent.putExtra(CreditCardResultPageActivity.EXTRA_CURRENT_PAGE,
                CreditCardResultPageActivity.ENUM_RESULT_PAGE_TYPE.CREATE_CREDIT_CARD_PAGE.toString());
        intent.putExtra(CreditCardData.EXTRA_CREDIT_CARD_DATA, addCardData);

        if(isGoBackPage) {
            intent.putExtra(CreditCardResultPageActivity.EXTRA_NEXT_TO_PAGE, CreditCardResultPageActivity.PAGE_ORDER_CONFIRM);
        }

        if (intent != null) {
            // TODO 確認什麼時候要改變這個flag
            startActivity(intent);
            finish();
        }


    }

    private boolean IsExistCreditCard(CreditCardData addCardData) {
        for (CreditCardData card : CreditCardUtil.GetCreditCardList(this)) {
            String cardBefore= card.getCardNumber().substring(0,7);
            String cardAfter= card.getCardNumber().substring(15,19);
            if(cardBefore.equals(addCardData.getCardNumber().substring(0,7))
            && cardAfter.equals(addCardData.getCardNumber().substring(15,19))
            && card.getCardExpireDate().equals(addCardData.getCardExpireDate())) {
                showAlertDialog(getString(R.string.card_hasexist));

                return true;

            }
        }
        return false;
    }

    @Override protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode)
        {
            case CARD_AUTH_REQUEST:
                if(resultCode == RESULT_OK)
                {
                    e7PayUtil.GetCreditCardAuth2(e7PayCardData);
                }
                else
                {
                    this.dismissProgressLoading();
                    showWebReturn(requestCode, data);

                }
                break;

            case CARD_AUTH_TWO_REQUEST:
                if(resultCode == RESULT_OK)
                {
                    e7PayUtil.GetCreditCardResponse(e7PayCardData, "00");
                }
                else
                {
                    this.dismissProgressLoading();
                    showWebReturn(requestCode, data);
                }
                break;


            case SCEN_CARD_REQUEST:
                ScanCardResult(data);
                break;

            default:
                break;
        }

//        resultTextView.setText(resultStr);
    }



    private E7PayUtil.OnCardAuthsListener onCardAuthsListeren  = new E7PayUtil.OnCardAuthsListener() {
        @Override
        public void CardAuthRedirect(String AuthKey, String RedirectUrl) {

            e7PayCardData.setAuthKey(AuthKey);
            Intent intent = new Intent(getApplication(), CreditCardAuthWebActivity.class);
            intent.putExtra(CreditCardAuthWebActivity.EXTRA_URL, RedirectUrl);
            startActivityForResult(intent, CARD_AUTH_REQUEST);
        }

        @Override
        public void CardAuth2Redirect(String AuthKey, String RedirectUrl) {
            if(!TextUtils.isEmpty(AuthKey)) {
                e7PayCardData.setAuthKey(AuthKey);
            }
            Intent intent = new Intent(getApplication(), CreditCardAuthWebActivity.class);
            intent.putExtra(CreditCardAuthWebActivity.EXTRA_URL, RedirectUrl);
            startActivityForResult(intent, CARD_AUTH_TWO_REQUEST);
        }

        @Override
        public void CreditCardResponse(String Cardkey, String CardToken, String CardNumberShelter, String CardName, int CardType, String ExpireDate) {
            addCardData.setCardKey(Cardkey);
            addCardData.setToken(CardToken);
            addCardData.setCardType(CreditCardData.ENUM_CARD_TYPE.valueOf(CardType));


            CreateCreditCardToDB();
        }

        @Override
        public void Error(int ErrorCode, String Message)
        {
            setDismissProgressLoading();
            showAlertDialog(Message);
        }
    };

    private void showWebReturn(int retcode, Intent data){

        if (data != null && data.hasExtra(CreditCardAuthWebActivity.EXTRA_RESULT_MSG)) {
            String returnCode = data.getStringExtra(CreditCardAuthWebActivity.EXTRA_RESULT_CODE);
            String message = data.getStringExtra(CreditCardAuthWebActivity.EXTRA_RESULT_MSG);
            String title = getString(R.string.title_auth_one);
            String note;
            if(retcode == CARD_AUTH_TWO_REQUEST) {
                title = getString(R.string.title_auth_two);
                note = "Credit Create Flow: Auth two request";
            } else {
                note = "Credit Create Flow: Auth one request";
            }

            note = note + String.format(", ReturnCode: %1$s, Message: %2$s", returnCode, message);
            EventAnalyticsUtil.addSpecialEvent(this, new SpecialEvent(SpecialEvent.TYPE_CREDIT_CARD, note));

            if(TextUtils.isEmpty(message)) {
                message = "None";
            }

            showAlertDialog(title, message, R.string.button_confirm, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            },false);

        }
    }


    private void ScanCardResult(Intent data){

        String resultStr;
        if (data != null && data.hasExtra(CardIOActivity.EXTRA_SCAN_RESULT)) {
            CreditCard scanResult = data.getParcelableExtra(CardIOActivity.EXTRA_SCAN_RESULT);

            // Never log a raw card number. Avoid displaying it, but if necessary use getFormattedCardNumber()
            resultStr = "Card Number: " + scanResult.getRedactedCardNumber() + "\n";

            // Do something with the raw number, e.g.:
            // myService.setCardNumber( scanResult.cardNumber );
            if (scanResult.getRedactedCardNumber() != null) {
                editText_cardnumber.setText("");
                char[] cardnumber = scanResult.cardNumber.toCharArray();
                for (char s : cardnumber) {
                    editText_cardnumber.setText(editText_cardnumber.getText().toString() + s);
                }
                cardType = scanResult.getCardType().toString();
            }

            if (scanResult.isExpiryValid()) {
                resultStr += "Expiration Date: " + scanResult.expiryMonth + "/" + scanResult.expiryYear + "\n";
                String strExpiryYear = String.valueOf(scanResult.expiryYear);
                String strExpiryMonth = String.valueOf(scanResult.expiryMonth);
                if (strExpiryYear.length() > 2)
                    strExpiryYear = strExpiryYear.substring(2);
                if (scanResult.expiryMonth < 10)
                    strExpiryMonth = "0" + strExpiryMonth;
                editText_expirationdate.setText(strExpiryMonth + "/" + strExpiryYear);
            }

            if (scanResult.cvv != null) {
                // Never log or display a CVV
                resultStr += "CVV has " + scanResult.cvv.length() + " digits.\n";
                editText_securitycode.setText(scanResult.cvv);
            }

            if (scanResult.postalCode != null) {
                resultStr += "Postal Code: " + scanResult.postalCode + "\n";
            }

            if (scanResult.cardholderName != null) {
                resultStr += "Cardholder Name : " + scanResult.cardholderName + "\n";
            }

        } else {
            resultStr = "Scan was canceled.";
        }
    }

    private void setDismissProgressLoading()
    {
        dismissProgressLoading();
    }
}
