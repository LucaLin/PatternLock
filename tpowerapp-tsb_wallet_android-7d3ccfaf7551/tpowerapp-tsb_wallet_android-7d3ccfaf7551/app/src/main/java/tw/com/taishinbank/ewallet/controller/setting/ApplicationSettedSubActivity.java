package tw.com.taishinbank.ewallet.controller.setting;

import android.content.Intent;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import tw.com.taishinbank.ewallet.R;
import tw.com.taishinbank.ewallet.controller.ActivityBase;
import tw.com.taishinbank.ewallet.model.creditcard.CreditCardData;
import tw.com.taishinbank.ewallet.model.creditcard.CreditCardTransaction;

public class ApplicationSettedSubActivity extends ActivityBase implements View.OnClickListener{

    public static final String EXTRA_CURRENT_PAGE = "extra_current_page";
    public static final String EXTRA_CENTER_TITLE = "extra_center_title";
    public static final String EXTRA_TREATY_CONTENT = "extra_treaty_content";

    private CreditCardData inputCardData;
    private CreditCardTransaction inputCreditCardTransaction;
    public enum  ENUM_PAGE_TYPE
    {
        CHANGE_SV_PASSWORD_PAGE,
        CHANGE_WALLET_PASSWORD_PAGE,
        USE_TREATY_PAGE

    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        ENUM_PAGE_TYPE page = ENUM_PAGE_TYPE.valueOf(getIntent().getStringExtra(EXTRA_CURRENT_PAGE));

        switch (page)
        {
            case CHANGE_SV_PASSWORD_PAGE:
                changeSVPassword();
                break;
            case CHANGE_WALLET_PASSWORD_PAGE:
                changeWalletPassword();
                break;
            case USE_TREATY_PAGE:
                useTreatyPage();
                break;
        }

    }


    private void useTreatyPage(){
        setContentView(R.layout.activity_setteing_use_treaty);
        // 設定置中的標題與返回鈕
        if(getIntent().hasExtra(EXTRA_CENTER_TITLE)) {
            String centerTitle = getIntent().getStringExtra(EXTRA_CENTER_TITLE);
            this.setCenterTitle(centerTitle);
        }else{
            this.setCenterTitle(R.string.use_provision);
        }
        this.getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        TextView textTreatyContent = (TextView) findViewById(R.id.text_treatycontent);
        if(getIntent().hasExtra(EXTRA_TREATY_CONTENT)){
            String treatyContent = getIntent().getStringExtra(EXTRA_TREATY_CONTENT);
            textTreatyContent.setText(treatyContent);
        }
//        textTreatyContent.setMovementMethod(new ScrollingMovementMethod());
    }

    private void changeSVPassword(){
        setContentView(R.layout.activity_setteing_change_password_alert);

        // 設定置中的標題與返回鈕
        this.setCenterTitle(R.string.change_sv_password_title);
        this.getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        LinearLayout linearLayout = (LinearLayout) findViewById(R.id.linearLayout_background);
        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) linearLayout.getLayoutParams();
        params.height = params.height + (params.height/4) ;
    }

    private void changeWalletPassword()
    {
        setContentView(R.layout.activity_setteing_change_password_alert);

        // 設定置中的標題與返回鈕
        this.setCenterTitle(R.string.credit_card_modify_password);
        this.getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        ImageView imageView = (ImageView) findViewById(R.id.image_result_status);
        imageView.setImageResource(R.drawable.ic_red_title_send_warning);

        TextView text_message = (TextView) findViewById(R.id.text_message);
        text_message.setText(R.string.text_change_wallet_password);

        Button button = (Button) findViewById(R.id.button_complete);
        button.setVisibility(View.VISIBLE);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ApplicationSettedSubActivity.this, EditPersonalInfoActivity.class);
                startActivity(intent);

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



}
