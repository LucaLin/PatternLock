package tw.com.taishinbank.ewallet.controller.wallethome;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import tw.com.taishinbank.ewallet.R;
import tw.com.taishinbank.ewallet.controller.ActivityBase;
import tw.com.taishinbank.ewallet.controller.SVLoginActivity;
import tw.com.taishinbank.ewallet.controller.creditcard.CreditCardCreateActivity;
import tw.com.taishinbank.ewallet.controller.extra.EarnActivity;
import tw.com.taishinbank.ewallet.interfaces.SystemMessageType;
import tw.com.taishinbank.ewallet.model.wallethome.WalletSystemMsg;
import tw.com.taishinbank.ewallet.util.FormatUtil;
import tw.com.taishinbank.ewallet.util.http.HttpUtilBase;

import static tw.com.taishinbank.ewallet.interfaces.SystemMessageType.getType;

public class SystemMessageDetailFragment extends Fragment {

    private TextView textName;
    private TextView textTime;
    private TextView textMessage;
    private ImageView imagePhoto;
    private Button button_left, button_right_or_fill;
    private WalletSystemMsg walletSystemMsg;
    private static final String ARG_MSG = "arg_msg";

    public static SystemMessageDetailFragment newInstance(WalletSystemMsg msg) {
        SystemMessageDetailFragment fragment = new SystemMessageDetailFragment();
        Bundle args = new Bundle();
        args.putParcelable(ARG_MSG, msg);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        if(getActivity() != null) {
            ((ActivityBase) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        walletSystemMsg = getArguments().getParcelable(ARG_MSG);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_system_message_detail, container, false);

        textName = (TextView) view.findViewById(R.id.text_name);
        textTime = (TextView) view.findViewById(R.id.text_time);
        textMessage = (TextView) view.findViewById(R.id.text_message);
        button_left = (Button) view.findViewById(R.id.button_left);
        button_right_or_fill = (Button) view.findViewById(R.id.button_right_or_fill);
        textName.setText(walletSystemMsg.getTitle());
        textMessage.setText(walletSystemMsg.getContent());
        // 顯示上線時間
        String formattedTime = null;
        if(!TextUtils.isEmpty(walletSystemMsg.getOnLineDate())) {
            formattedTime = FormatUtil.toDateFormatted(walletSystemMsg.getOnLineDate());
        }
        textTime.setText(formattedTime);
        button_left.setVisibility(View.GONE);
        button_right_or_fill.setVisibility(View.GONE);
        SystemMessageType systemMessageType = getType(walletSystemMsg.getBbType());
        switch (systemMessageType)
        {
            case COUPON: //優惠公告
                button_right_or_fill.setVisibility(View.VISIBLE);
                button_right_or_fill.setText(R.string.system_message_earn_extra);
                button_right_or_fill.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(getActivity(), EarnActivity.class);
                        intent.putExtra(EarnActivity.EXTRA_GO_PAGE, EarnActivity.PAGE_EARN_BY_ENTER);
                        startActivity(intent);
                    }
                });
                break;

            case SYSTEM: //系統公告
            case WALLET_HOME_SHOW_ALERT:
                break;

            case INVITE_REGISTER_SV: //邀請註冊儲值
                button_left.setVisibility(View.VISIBLE);
                button_left.setText(R.string.login);
                button_right_or_fill.setVisibility(View.VISIBLE);
                button_right_or_fill.setText(R.string.registered);
                button_left.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(getActivity(), SVLoginActivity.class);
                        startActivity(intent);

                    }
                });

                button_right_or_fill.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // 取得註冊連結
                        String url = HttpUtilBase.getSvRegisterUrl(getContext());

                        Intent intent = new Intent(getActivity(), WebViewActivity.class);
                        intent.putExtra(WebViewActivity.EXTRA_URL, url);
                        startActivity(intent);

                    }
                });
                break;

            case INVITE_BINDING_CREDIT_CARD: //邀請綁定信用卡
                button_right_or_fill.setVisibility(View.VISIBLE);
                button_right_or_fill.setText(R.string.system_message_bind_creditcard);
                button_right_or_fill.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(getActivity(), CreditCardCreateActivity.class);
                        startActivity(intent);

                    }
                });

                break;
        }


        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        // 設定置中的title
        ActivityBase activityBase = ((ActivityBase) getActivity());
        activityBase.setCenterTitle(R.string.drawer_item_system_message);
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


    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        for (int i = 0; i < menu.size(); i++) {
            MenuItem menuItem = menu.getItem(i);
            // 返回上一頁
            if (menuItem.getItemId() == android.R.id.home) {
                menuItem.setVisible(true);
            } else {
                menuItem.setVisible(false);
            }
        }
    }

    @Override
    public void onStop() {
        super.onStop();
    }


}
