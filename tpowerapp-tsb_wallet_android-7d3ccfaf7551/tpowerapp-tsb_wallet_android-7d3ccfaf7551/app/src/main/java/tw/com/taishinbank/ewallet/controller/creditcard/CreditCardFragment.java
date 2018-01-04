package tw.com.taishinbank.ewallet.controller.creditcard;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.json.JSONException;

import tw.com.taishinbank.ewallet.R;
import tw.com.taishinbank.ewallet.adapter.CreditCardPagerAdapter;
import tw.com.taishinbank.ewallet.controller.ActivityBase;
import tw.com.taishinbank.ewallet.controller.ContactListActivity;
import tw.com.taishinbank.ewallet.controller.WalletApplication;
import tw.com.taishinbank.ewallet.interfaces.ResponseListener;
import tw.com.taishinbank.ewallet.model.AppUpdateInfo;
import tw.com.taishinbank.ewallet.model.ResponseResult;
import tw.com.taishinbank.ewallet.util.CreditCardUtil;
import tw.com.taishinbank.ewallet.util.NetworkUtil;
import tw.com.taishinbank.ewallet.util.PermissionUtil;
import tw.com.taishinbank.ewallet.util.PreferenceUtil;
import tw.com.taishinbank.ewallet.util.http.GeneralHttpUtil;
import tw.com.taishinbank.ewallet.util.http.HttpUtilBase;
import tw.com.taishinbank.ewallet.util.responsebody.GeneralResponseBodyUtil;


public class CreditCardFragment extends Fragment implements View.OnClickListener{

    private static final String TAG = "CreditCardFragment";

    private enum CARD_VIEW_MODE
    {
        HAS_CARD,
        NOT_CARD
    }

    private ViewPager viewPager;
    private ImageButton button_addCard;
    private RelativeLayout relativeLayout_havecredcard, relativeLayout_notcredcard;
    private Button button_manage_cards, button_scan, button_show_barcode, button_left_viewPage, button_right_viewPage;
    private WalletApplication globalVariable;
    private TextView text_cardname;
    private CreditCardPagerAdapter adapter;

    private int index_mainCard = 0;
  //  private CreditCardDBHelper cardDBHelper;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View view = inflater.inflate(R.layout.fragment_creditcard, container, false);
        // 如果沒有網路連線，顯示提示對話框
        if(!NetworkUtil.isConnected(getActivity())){
            ((ActivityBase)getActivity()).showAlertDialog(getString(R.string.msg_no_available_network), android.R.string.ok, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            }, true);
        }

        globalVariable = (WalletApplication)getContext().getApplicationContext();

        button_scan = (Button) view.findViewById(R.id.button_scan);
       // button_scan.setVisibility(View.GONE);
        button_scan.setOnClickListener(this);

        button_show_barcode = (Button) view.findViewById(R.id.button_show);
        button_show_barcode.setOnClickListener(this);


        button_addCard = (ImageButton) view.findViewById(R.id.button_addcard);
        button_addCard.setOnClickListener(this);

        relativeLayout_havecredcard = (RelativeLayout) view.findViewById(R.id.relativeLayout_havecredcard);
        relativeLayout_notcredcard = (RelativeLayout) view.findViewById(R.id.relativeLayout_notcredcard);

        viewPager = (ViewPager) view.findViewById(R.id.viewpager_card);
        adapter = new CreditCardPagerAdapter(getContext(), CreditCardUtil.GetCreditCardList(getContext()));
        //adapter.EventCallBack(this);
        adapter.eventCallBack = new CreditCardPagerAdapter.EventCallBack() {
            @Override
            public void OnChangeCardPage(int index) {
                index_mainCard = index;
                text_cardname.setText(globalVariable.CreditCardList.get(index).getCardName().toString());
                viewPager.setCurrentItem(index_mainCard);

                if(CreditCardUtil.GetCreditCardList(getContext()).size() == 1) {
                    button_left_viewPage.setVisibility(View.GONE);
                    button_right_viewPage.setVisibility(View.GONE);
                }
                else if(adapter.getCount() == 1) {
                    button_left_viewPage.setVisibility(View.GONE);
                    button_right_viewPage.setVisibility(View.VISIBLE);
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
                Intent intent = new Intent(getActivity(), CreditCardManageActivity.class);

                if(intent != null){
                    // TODO 確認什麼時候要改變這個flag
                    startActivity(intent);
                }
            }
        };

        // Set up the ViewPager with the sections adapter.
        viewPager.setAdapter(adapter);

        int page_size = CreditCardUtil.GetCreditCardList(getContext()).size();
        viewPager.setOffscreenPageLimit(page_size);

        //設定卡片間的距離
        viewPager.setPageMargin(getResources().getDimensionPixelSize(R.dimen.viewpages_card_spacing));

        CardsPageChangeListener cardsPageChangeListener = new CardsPageChangeListener();
        viewPager.addOnPageChangeListener(cardsPageChangeListener);
        relativeLayout_havecredcard.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return viewPager.dispatchTouchEvent(event);
            }
        });


        button_left_viewPage = (Button) view.findViewById(R.id.button_viewpager_left);
        button_right_viewPage = (Button) view.findViewById(R.id.button_viewpager_right);

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

        LinearLayout linearLayout_text = (LinearLayout) view.findViewById(R.id.linearLayout_text);

        linearLayout_text.setOnClickListener(this);
        //button_manage_cards.setOnClickListener(this);

        text_cardname = (TextView) view.findViewById(R.id.text_cardname);
       // text_cardname.setOnClickListener(this);

        // 下半部
        View view_even = view.findViewById(R.id.button_even);
        final TextView text_even = (TextView)view_even.findViewById(android.R.id.title);
        text_even.setText(R.string.preferential_even_button);

        View view_history = view.findViewById(R.id.button_history);
        TextView text_history = (TextView)view_history.findViewById(android.R.id.title);
        text_history.setText(R.string.pay_history_button);
        ImageButton button_history = (ImageButton) view_history.findViewById(android.R.id.button1);
        button_history.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Fragment fragment = new tw.com.taishinbank.ewallet.controller.creditcard.TransactionLogFragment();

                FragmentTransaction ft = getFragmentManager().beginTransaction();
                ft.setCustomAnimations(R.anim.in_right_to_left, R.anim.out_right_to_left, R.anim.in_left_to_right, R.anim.out_left_to_right);
                ft.replace(android.R.id.tabcontent, fragment);
                ft.addToBackStack(null);
                ft.commit();
            }
        });


        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        // 設定置中的title
        ActivityBase activityBase = ((ActivityBase) getActivity());
        activityBase.setCenterTitle(getString(R.string.title_creditcard_envelope), false);
        activityBase.getWindow().setBackgroundDrawableResource(R.drawable.bg_fragment_creditcard_home);
      //  activityBase.setLeftIcon(R.drawable.ic_e_home_menu);
       // activityBase.getSupportActionBar().setDisplayHomeAsUpEnabled(true);

//
        //def is not card mode
        if(CreditCardUtil.GetCreditCardList(getContext()).size() == 0)
            CardViewMode(CARD_VIEW_MODE.NOT_CARD);
        else {
            CardViewMode(CARD_VIEW_MODE.HAS_CARD);
            int page_size = CreditCardUtil.GetCreditCardList(getContext()).size();

            adapter.updateAdapter(CreditCardUtil.GetCreditCardList(getContext()));
//            viewPager.setCurrentItem(index_mainCard);
            //adapter.notifyDataSetChanged();
            viewPager.setOffscreenPageLimit(page_size);

            if(page_size == 1)
            {
                button_left_viewPage.setVisibility(View.GONE);
                button_right_viewPage.setVisibility(View.GONE);
            }

        }

        if(PreferenceUtil.needCheckUpdate(activityBase)) {
            // 如果沒有網路連線，顯示提示對話框
            if (!NetworkUtil.isConnected(activityBase)) {
                activityBase.showAlertDialog(getString(R.string.msg_no_available_network), android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }, true);
                return;
            }

            // 呼叫確認app版本更新api
            try {
                GeneralHttpUtil.checkAppUpdate(responseListenerCheckUpdate, activityBase, TAG);
                activityBase.showProgressLoading();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.activity_main, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        // 如果是聯絡人圖示，開啟聯絡人列表
        if (id == R.id.action_contacts) {
            startActivity(new Intent(getActivity(), ContactListActivity.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onStop() {
        super.onStop();
        HttpUtilBase.cancelQueue(TAG);
        ((ActivityBase) getActivity()).dismissProgressLoading();
    }

    @Override
    public void onClick(View view) {
        int viewId = view.getId();

        Intent intent = null;
        if(viewId == R.id.button_addcard) {
            intent = new Intent(getActivity(), CreditCardCreateActivity.class);
        }
        else if (viewId == R.id.linearLayout_text) {
            intent = new Intent(getActivity(), CreditCardManageActivity.class);
        }else if(viewId == R.id.button_scan){
            launchScanner();
        }else if (viewId == R.id.button_show) {
            if(CreditCardUtil.GetCreditCardList(getContext()).size() == 0) {
                ((ActivityBase)getActivity()).showAlertDialog(getString(R.string.nocard_alert));
            }
            else {
                intent = new Intent(getActivity(), CreditCardPaymentLoginActivity.class);
            }
        }


        if(intent != null){
            startActivity(intent);
        }
    }

    public class CardsPageChangeListener implements ViewPager.OnPageChangeListener {

        @Override
        public void onPageSelected(int position) {
            text_cardname.setText(globalVariable.CreditCardList.get(position).getCardName().toString());

            if(CreditCardUtil.GetCreditCardList(getContext()).size() == 1) {
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
            if (relativeLayout_havecredcard != null) {
                relativeLayout_havecredcard.invalidate();
            }
        }

        @Override
        public void onPageScrollStateChanged(int arg0) {
        }
    }

    private void CardViewMode(CARD_VIEW_MODE mode)
    {
        if(mode == CARD_VIEW_MODE.HAS_CARD)
        {
            relativeLayout_havecredcard.setVisibility(View.VISIBLE);
            relativeLayout_notcredcard.setVisibility(View.GONE);
        }
        else
        {
            relativeLayout_havecredcard.setVisibility(View.GONE);
            relativeLayout_notcredcard.setVisibility(View.VISIBLE);
        }
    }

    // 確認app更新的api callback
    private ResponseListener responseListenerCheckUpdate = new ResponseListener() {

        @Override
        public void onResponse(ResponseResult result) {
            if(getActivity() == null){
                return;
            }

            final ActivityBase activityBase = ((ActivityBase) getActivity());
            activityBase.dismissProgressLoading();
            String returnCode = result.getReturnCode();
            // 如果returnCode是成功
            if(returnCode.equals(ResponseResult.RESULT_SUCCESS)) {
                AppUpdateInfo appUpdateInfo = GeneralResponseBodyUtil.getAppUpdateInfo(result.getBody());
                activityBase.showAlertIfNeedUpdate(appUpdateInfo);
            // 如果不是共同error
            }else if(!handleCommonError(result, (ActivityBase) getActivity())) {
                // 目前不做事
            }
        }
    };

    // ----
    // public
    // ----
    public void launchScanner() {
        String[] permissionsForScan = {Manifest.permission.CAMERA};
        if (!PermissionUtil.needGrantRuntimePermission(this, permissionsForScan,
                PermissionUtil.PERMISSION_REQUEST_CODE_SCAN)) {
            Intent intent = new Intent();
            intent.setClass(getActivity(), QRCodeScannerActivity.class);
            startActivity(intent);
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == PermissionUtil.PERMISSION_REQUEST_CODE_SCAN) {
            // 有權限存取
            if (PermissionUtil.verifyPermissions(grantResults)) {
                launchScanner();
            }else{
                PermissionUtil.showNeedPermissionDialog(getActivity(), permissions, grantResults);
            }
        }
    }


}
