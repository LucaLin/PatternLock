package tw.com.taishinbank.ewallet.controller.extra;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;

import java.util.ArrayList;

import tw.com.taishinbank.ewallet.R;
import tw.com.taishinbank.ewallet.controller.ActivityBase;
import tw.com.taishinbank.ewallet.controller.MainActivity;
import tw.com.taishinbank.ewallet.model.LocalContact;
import tw.com.taishinbank.ewallet.model.extra.TicketDetailData;
import tw.com.taishinbank.ewallet.model.extra.TicketListData;

/**
 * 共用的約定帳戶列表頁
 */
public class MyTicketActivity extends ActivityBase {

    private static final String TAG = "MyTicketTradeFragment";
    private final String EXTRA_TICKET = "EXTRA_TICKET";
    private final String EXTRA_CONTACT = "EXTRA_CONTACT";
    private final String EXTRA_MESSAGE = "EXTRA_MESSAGE";

    private final String GO_LIST = "GO_LIST";

    private TicketListData ticket;
    private TicketDetailData ticketDetailData;
    private ArrayList<LocalContact> localContacts;
    private String message;
    private boolean allowGoingBack = true;

    //public ENUM_IMAGE_SIZE imageSize = ENUM_IMAGE_SIZE.MEDIUM;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Set Layout
        setContentView(R.layout.activity_sv_flow);
        setCenterTitle(R.string.extra_menu_eticket);

//        Display display = getWindowManager().getDefaultDisplay();
//        Point size = new Point();
//        display.getSize(size);
//        int width = size.x;
//        if(width < 640)
//            imageSize = ENUM_IMAGE_SIZE.SMALL;
//        else if(width > 720)
//            imageSize = ENUM_IMAGE_SIZE.LARGE;

        //Set Fragment
        MyTicketFragment fragment = new MyTicketFragment();
        if(getIntent().hasExtra(MyTicketFragment.EXTRA_SWITCH_TO)) {
            Bundle args = new Bundle();
            args.putInt(MyTicketFragment.EXTRA_SWITCH_TO, getIntent().getIntExtra(MyTicketFragment.EXTRA_SWITCH_TO, MyTicketFragment.TICKET_UNUSED));
            fragment.setArguments(args);
        }
        fragment.setOnEventListener(new MyTicketFragment.OnEventListener() {
            @Override
            public void OnListItemClickEvent(TicketListData selectTicket) {
                goToTicketDetail(selectTicket);

            }
        });
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.fg_container, fragment);
        ft.commit();
    }


    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putParcelable(EXTRA_TICKET, ticket);
        outState.putParcelableArrayList(EXTRA_CONTACT, localContacts);
        outState.putString(EXTRA_MESSAGE, message);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        ticket = savedInstanceState.getParcelable(EXTRA_TICKET);
        localContacts = savedInstanceState.getParcelableArrayList(EXTRA_CONTACT);
        message = savedInstanceState.getString(EXTRA_MESSAGE);
    }

    @Override
    public void onBackPressed() {
        if(allowGoingBack) {
            super.onBackPressed();
        }
    }

    // ----
    //  Go to function
    // ----


    private void goToTicketDetail(TicketListData selectTicket) {

        this.ticket = selectTicket;

        MyTicketDetailFragment detailFragment = new MyTicketDetailFragment();
        detailFragment.setOnEventListener(new MyTicketDetailFragment.OnEventListener() {
            @Override
            public void OnNextClickEvent(TicketDetailData ticketDetailData) {
                goToTicketTrade(ticketDetailData);
            }

            @Override
            public void OnToGoClickEvent() {
                goToCreditCardLog();
            }
        });

        Bundle bundle = new Bundle();
        bundle.putParcelable(MyTicketDetailFragment.EXTRA_TICKET, selectTicket);
        detailFragment.setArguments(bundle);

        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.setCustomAnimations(R.anim.in_right_to_left, R.anim.out_right_to_left, R.anim.in_left_to_right, R.anim.out_left_to_right);
        ft.replace(R.id.fg_container, detailFragment);
        ft.addToBackStack(TAG);
        ft.commit();
    }

    private void goToTicketTrade(TicketDetailData ticketDetailData) {

        this.ticketDetailData = ticketDetailData;
        //MyTicketTradeFragment
        MyTicketTradeFragment tradeFragment = MyTicketTradeFragment.newInstance(this.ticketDetailData);
        tradeFragment.setOnEventListener(new MyTicketTradeFragment.OnEventListener() {
            @Override
            public void OnTradedEvent(TicketDetailData ticketData) {
                goToTradeResult(ticketData);
            }
        });
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.setCustomAnimations(R.anim.in_right_to_left, R.anim.out_right_to_left, R.anim.in_left_to_right, R.anim.out_left_to_right);
        ft.replace(R.id.fg_container, tradeFragment);
        ft.addToBackStack(TAG);
        ft.commit();

    }

    private void goToTradeResult(TicketDetailData ticketData) {

        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        allowGoingBack = false;
        MyTicketResultFragment myTicketResultFragment = MyTicketResultFragment.newInstance(MyTicketResultFragment.ENUM_MODE_TYPE.TRADE, ticketData);
        myTicketResultFragment.setOnEventListener(new MyTicketResultFragment.OnEventListener() {
            @Override
            public void OnResultNextClickEvent(int switchTo) {
                goToTicketList(switchTo);
            }
        });

        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.setCustomAnimations(R.anim.in_right_to_left, R.anim.out_right_to_left, R.anim.in_left_to_right, R.anim.out_left_to_right);
        ft.replace(R.id.fg_container, myTicketResultFragment);
        ft.addToBackStack(TAG);
        ft.commit();
    }

    private void goToCreditCardLog() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra(MainActivity.EXTRA_GO_CREDIT_HISTORY, "");
        startActivity(intent);
    }

    private void goToTicketList(int switchTo) {
        getSupportFragmentManager().popBackStack(TAG, FragmentManager.POP_BACK_STACK_INCLUSIVE);
        allowGoingBack = true;
        MyTicketFragment fragment = new MyTicketFragment();
        Bundle args = new Bundle();
        args.putInt(MyTicketFragment.EXTRA_SWITCH_TO, switchTo);
        fragment.setArguments(args);
        fragment.setOnEventListener(new MyTicketFragment.OnEventListener() {
            @Override
            public void OnListItemClickEvent(TicketListData selectTicket) {
                goToTicketDetail(selectTicket);

            }
        });
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.fg_container, fragment);
        ft.commit();
    }

}
