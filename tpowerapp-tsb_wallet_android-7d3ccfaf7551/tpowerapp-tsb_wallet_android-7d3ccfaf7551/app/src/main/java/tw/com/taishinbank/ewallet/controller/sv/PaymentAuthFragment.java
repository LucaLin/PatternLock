package tw.com.taishinbank.ewallet.controller.sv;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.UnsupportedEncodingException;

import tw.com.taishinbank.ewallet.R;
import tw.com.taishinbank.ewallet.controller.ActivityBase;
import tw.com.taishinbank.ewallet.imagehelper.ImageLoader;
import tw.com.taishinbank.ewallet.model.LocalContact;
import tw.com.taishinbank.ewallet.model.SVAccountInfo;
import tw.com.taishinbank.ewallet.util.FormatUtil;
import tw.com.taishinbank.ewallet.util.NetworkUtil;
import tw.com.taishinbank.ewallet.util.PreferenceUtil;
import tw.com.taishinbank.ewallet.util.sharedMethods;

public class PaymentAuthFragment extends Fragment {

    private SVAccountInfo svAccountInfo;

    private Button buttonNext;

    private PasswordCaptchaInputFragment mimaCaptchaInputFragment;

    private String amount;
    private LocalContact friendToSend;
    private String message;
    private OnSendRequestListener listener;

    private static final String ARG_AMOUNT = "arg_amount";
    private static final String ARG_FRIEND_TO_SEND = "arg_friend_to_send";
    private static final String ARG_MESSAGE = "arg_message";

    /**
     * 用來建立Fragment
     */
    public static PaymentAuthFragment newInstance(LocalContact friendToSend, String amount, String message) {
        PaymentAuthFragment f = new PaymentAuthFragment();
        Bundle args = new Bundle();
        args.putParcelable(ARG_FRIEND_TO_SEND, friendToSend);
        args.putString(ARG_AMOUNT, amount);
        args.putString(ARG_MESSAGE, message);
        f.setArguments(args);

        return f;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        if(getArguments() != null) {
            amount = getArguments().getString(ARG_AMOUNT);
            message = getArguments().getString(ARG_MESSAGE);
            friendToSend = getArguments().getParcelable(ARG_FRIEND_TO_SEND);
        }
    }

    public PaymentAuthFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_sv_pay_auth, container, false);

        mimaCaptchaInputFragment = (PasswordCaptchaInputFragment) getChildFragmentManager().findFragmentById(R.id.fragment_password_captcha);

        buttonNext = (Button) view.findViewById(R.id.button_next);
        buttonNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onNextClicked();
            }
        });

        // 設置接收紅包的人名
        TextView textNamesTo = (TextView) view.findViewById(R.id.text_names_to);
        textNamesTo.setText(friendToSend.getDisplayName());
//        TextView textNamesNumber = (TextView) view.findViewById(R.id.text_names_number);

        // 設置轉帳總金額
        TextView textAmount = (TextView) view.findViewById(R.id.text_amount);
        textAmount.setText(FormatUtil.toDecimalFormatFromString(amount));

        // 設置訊息
        TextView textMessage = (TextView) view.findViewById(R.id.text_message);
        textMessage.setText(message);

        ImageView imagePhoto = (ImageView) view.findViewById(R.id.image_photo);
        // 設定頭像
        ImageLoader imageLoader = new ImageLoader(getActivity(), getResources().getDimensionPixelSize(R.dimen.list_photo_size));
        imageLoader.loadImage(friendToSend.getMemNO(), imagePhoto);

        TextView textNameFrom = (TextView) view.findViewById(R.id.text_name_from);
        // 從preference取得會員暱稱，並設定會員暱稱
        String nickname = PreferenceUtil.getNickname(getActivity());
        textNameFrom.setText(nickname);

        // 設定帳戶
        TextView textAccount = (TextView) view.findViewById(R.id.text_account);
        svAccountInfo = PreferenceUtil.getSVAccountInfo(getActivity());
        String account = getString(R.string.transfer_from_account) + FormatUtil.toAccountFormat(svAccountInfo.getPrepaidAccount());
        textAccount.setText(account);

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        mimaCaptchaInputFragment.setInputsChangedListener(inputsChangedListener);
        updateNextButtonStatus();
    }

    @Override
    public void onPause() {
        super.onPause();
        mimaCaptchaInputFragment.setInputsChangedListener(null);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        // 預防再次onCreateView時有例外（說fragment id重複）
        mimaCaptchaInputFragment = (PasswordCaptchaInputFragment) getChildFragmentManager().findFragmentById(R.id.fragment_password_captcha);
        if(mimaCaptchaInputFragment != null) {
            getChildFragmentManager().beginTransaction().remove(mimaCaptchaInputFragment).commitAllowingStateLoss();
            mimaCaptchaInputFragment = null;
        }
    }

    // ---
    // Public
    // ---

    public void setListener(OnSendRequestListener listener) {
        this.listener = listener;
    }

    // ---
    // My methods
    // ---

    // 根據是否有輸入密碼跟正確驗證碼enable/disable按鈕
    private void updateNextButtonStatus() {
        if(mimaCaptchaInputFragment.hasValidInputs()){
            buttonNext.setEnabled(true);
        } else {
            buttonNext.setEnabled(false);
        }
    }

    // ----
    // User interaction
    // ----

    protected void onNextClicked() {
        //Check 1 - Captcha correct
        if(!mimaCaptchaInputFragment.validateCaptcha()){
            // 更新按鈕狀態
            updateNextButtonStatus();
            return ;
        }

        //Check 2 - Has network
        // 如果沒有網路連線，顯示提示對話框
        if(!NetworkUtil.isConnected(getActivity())){
            ((ActivityBase) getActivity()).showAlertDialog(getString(R.string.msg_no_available_network), android.R.string.ok, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            }, true);
            return ;
        }

        try {
            String userPwInAES = sharedMethods.AESEncrypt(mimaCaptchaInputFragment.getPassword());
            if(listener != null){
                listener.onSendRequest(userPwInAES);
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    // -------------------
    //  Listeners
    // -------------------

    private PasswordCaptchaInputFragment.InputsChangedListener
            inputsChangedListener = new PasswordCaptchaInputFragment.InputsChangedListener() {
        @Override
        public void onInputsChanged() {
            updateNextButtonStatus();
        }
    };

    public interface OnSendRequestListener{
        void onSendRequest(String userPwInAES);
    }
}
