package tw.com.taishinbank.ewallet.controller.creditcard;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import tw.com.taishinbank.ewallet.R;
import tw.com.taishinbank.ewallet.adapter.CreditCardPagerAdapter;
import tw.com.taishinbank.ewallet.controller.ActivityBase;
import tw.com.taishinbank.ewallet.controller.WalletApplication;
import tw.com.taishinbank.ewallet.model.creditcard.CreditCardData;
import tw.com.taishinbank.ewallet.util.CreditCardUtil;
import tw.com.taishinbank.ewallet.util.E7PayUtil;

public class CreditCardManageActivity extends ActivityBase implements View.OnClickListener{

    private ViewPager viewPager;
    private RelativeLayout relativeLayout_viewpager_cards;
    private WalletApplication globalVariable;
    private TextView text_cardname, text_selected_title;
    private CreditCardPagerAdapter adapter;
    private Button button_delete, button_create, button_left_viewPage, button_right_viewPage;
    private int index_mainCard = 1;
    private CreditCardData cardData;

    // 17 API
    private E7PayUtil e7PayUtil;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_credit_card_manage);

        // 設定置中的標題與返回鈕
        this.setCenterTitle(R.string.title_credit_card_manage);
        this.getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        globalVariable = (WalletApplication)this.getApplicationContext();
        text_selected_title = (TextView) findViewById(R.id.text_selected_title);
        text_cardname = (TextView) findViewById(R.id.text_cardname);

        viewPager = (ViewPager) findViewById(R.id.viewpager_card);
        adapter = new CreditCardPagerAdapter(this, CreditCardUtil.GetCreditCardList(this));
        //adapter.EventCallBack(this);
        adapter.eventCallBack = new CreditCardPagerAdapter.EventCallBack() {
            @Override
            public void OnChangeCardPage(int index) {
                index_mainCard = index;
                text_cardname.setText(globalVariable.CreditCardList.get(index).getCardName().toString());
                viewPager.setCurrentItem(index_mainCard);

                if(CreditCardUtil.GetCreditCardList(CreditCardManageActivity.this).size() == 1) {
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
            {}
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

        button_delete = (Button) findViewById(R.id.button_delete_crad);
        button_create = (Button) findViewById(R.id.button_create_card);
        button_delete.setOnClickListener(this);
        button_create.setOnClickListener(this);

    }

    @Override
    protected void onResume() {
        super.onResume();
//
        int page_size = CreditCardUtil.GetCreditCardList(this).size();
        //def is not card mode
        if(page_size > 0)
        {
            adapter.updateAdapter(CreditCardUtil.GetCreditCardList(this));
            viewPager.setOffscreenPageLimit(page_size);
        }

        if(page_size < 2)
        {
            button_left_viewPage.setVisibility(View.GONE);
            button_right_viewPage.setVisibility(View.GONE);
        }

        if(page_size == 0)
        {
            text_selected_title.setVisibility(View.GONE);
            text_cardname.setText(getString(R.string.title_not_credit_card));
            button_delete.setEnabled(false);
        }
    }
    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onClick(View v) {
        int viewId = v.getId();

        if(viewId == R.id.button_delete_crad) {

            if(CreditCardUtil.GetCreditCardList(this).size() == 0)
                return;

            cardData = new CreditCardData(CreditCardUtil.GetCreditCardList(this).get(viewPager.getCurrentItem()));
            String text_card_message = "";
            if(!cardData.getCardNumber().equals("")) {
                String[] cardNumber = cardData.getCardNumber().split("-");
                text_card_message = cardData.getCardName() + " (⋯⋯" + cardNumber[3] + ")";
            }

            this.showAlertDialog(String.format(getString(R.string.delete_alert_info), text_card_message.toString()), R.string.button_confirm, R.string.button_cancel,
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            deleteCreditCard();
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
//
//            intent = new Intent(this, CreditCardResultPageActivity.class);
//
//            intent.putExtra(CreditCardResultPageActivity.EXTRA_CURRENT_PAGE,
//                    CreditCardResultPageActivity.ENUM_RESULT_PAGE_TYPE.DELETE__CREDIT_CARD_PAGE);
//            intent.putExtra(CreditCardData.EXTRA_CREDIT_CARD_DATA, cardData);

        }
        else if(viewId == R.id.button_create_card){
            Intent intent = new Intent(this, CreditCardCreateActivity.class);
            if(intent != null){
                // TODO 確認什麼時候要改變這個flag
                startActivity(intent);
            }
        }
    }



    public class CardsPageChangeListener implements ViewPager.OnPageChangeListener {

        @Override
        public void onPageSelected(int position) {
            text_cardname.setText(globalVariable.CreditCardList.get(position).getCardName().toString());
            if(globalVariable.CreditCardList.get(position).getSettedMain())
                text_selected_title.setVisibility(View.VISIBLE);
            else
                text_selected_title.setVisibility(View.INVISIBLE);


            if(CreditCardUtil.GetCreditCardList(CreditCardManageActivity.this).size() == 1) {
                button_left_viewPage.setVisibility(View.GONE);
                button_right_viewPage.setVisibility(View.GONE);
            }
            else  if(position == 0)
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



    private void deleteCreditCard(){
        this.showProgressLoading();
        e7PayUtil = new E7PayUtil(this);
        e7PayUtil.setOnCancelCardListener(onCancelCardListener);
        e7PayUtil.CancelCreditCardAuth(cardData.getToken());
    }

    private void deleteCreditCardForDB(){
        this.dismissProgressLoading();
        int ret = CreditCardUtil.DB_delete(getApplication(), cardData);
        if(ret == 1)
        {
            //17 取消綁定

            Intent intent = new Intent(getApplication(), CreditCardResultPageActivity.class);

            intent.putExtra(CreditCardResultPageActivity.EXTRA_CURRENT_PAGE,
                    CreditCardResultPageActivity.ENUM_RESULT_PAGE_TYPE.DELETE_CREDIT_CARD_PAGE.toString());
            intent.putExtra(CreditCardData.EXTRA_CREDIT_CARD_DATA, cardData);

            if (intent != null) {
                // TODO 確認什麼時候要改變這個flag
                startActivity(intent);
            }
        } else if (ret > 1) {
            showAlertDialog(">1", R.string.button_confirm, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            }, false);
        }else{
            showAlertDialog("刪除失敗", R.string.button_confirm, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            }, false);
        }
    }

    E7PayUtil.OnCancelCardListener onCancelCardListener = new E7PayUtil.OnCancelCardListener() {
        @Override
        public void CancelCardAuth(String CardToken) {
            deleteCreditCardForDB();
        }

        @Override
        public void CancelAllCardAuth() {

        }

        @Override
        public void Error(int errorCode, String message) {
            showAlertDialog( "錯誤代碼: " + errorCode + "\n" + message, R.string.button_confirm, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    finish();
                }
            }, false);
        }
    };

}
