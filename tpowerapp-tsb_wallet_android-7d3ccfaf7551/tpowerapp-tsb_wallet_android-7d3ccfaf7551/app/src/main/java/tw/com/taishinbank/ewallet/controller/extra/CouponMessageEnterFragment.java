package tw.com.taishinbank.ewallet.controller.extra;


import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import org.json.JSONException;

import tw.com.taishinbank.ewallet.R;
import tw.com.taishinbank.ewallet.controller.ActivityBase;
import tw.com.taishinbank.ewallet.imagehelper.ImageLoader;
import tw.com.taishinbank.ewallet.interfaces.ResponseListener;
import tw.com.taishinbank.ewallet.model.ResponseResult;
import tw.com.taishinbank.ewallet.model.extra.Coupon;
import tw.com.taishinbank.ewallet.util.InputFilterUtil;
import tw.com.taishinbank.ewallet.util.NetworkUtil;
import tw.com.taishinbank.ewallet.util.http.ExtraHttpUtil;
import tw.com.taishinbank.ewallet.util.http.HttpUtilBase;

public class CouponMessageEnterFragment extends Fragment {

    private static final String TAG = "CouponMessageEnterFragment";

    protected ImageView imgPhoto;
    protected TextView  txtToSend;
    protected TextView  txtTitle1;
    protected TextView  txtTitle2;

    protected TextView  txtReceivedMessage;
    protected TextView  txtCount;
    protected EditText  txtMessage;

    protected Button    btnReply;

    protected String numberOfChars;
    protected int lengthLimit = 50;

    // -- Data Model --
    protected Coupon coupon;

    // -- Listener --
    private TextWatcher textWatcher;
    private ButtonsClickListener buttonsClickListener;

    // -- Helper --

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ((ReceiveCouponActivity)getActivity()).setCenterTitle(R.string.sv_result_action_reply);
        setHasOptionsMenu(true);

        coupon = getArguments().getParcelable(ReceiveCouponActivity.EXTRA_COUPON);

        numberOfChars = getString(R.string.current_text_count);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_extra_message_enter, container, false);

        // Set View Hold
        setViewHold(view);

        // Set View Content
        // 依據傳入的參入，來決定畫面呈現
        setViewContent();

        //
        setListener();

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        if(txtMessage.getText().length() == 0 )
        {
            btnReply.setEnabled(false);
        }
        else if(txtMessage.getText().length() > 0 )
        {
            btnReply.setEnabled(true);
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        HttpUtilBase.cancelQueue(TAG);
        if(getActivity() != null) {
            ((ActivityBase) getActivity()).dismissProgressLoading();
            ((ActivityBase) getActivity()).hideKeyboard();
        }
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

    public static CouponMessageEnterFragment createNewInstanceWithParams(Coupon coupon) {
        CouponMessageEnterFragment fragment = new CouponMessageEnterFragment();

        Bundle args = new Bundle();
        args.putParcelable(ReceiveCouponActivity.EXTRA_COUPON, coupon);

        fragment.setArguments(args);

        return fragment;
    }

    // ----
    //  Private method
    // ----
    protected void setViewHold(View view) {
        imgPhoto           = (ImageView    ) view.findViewById(R.id.img_photo);
        txtToSend          = (TextView     ) view.findViewById(R.id.txt_to_send);
        txtTitle1          = (TextView     ) view.findViewById(R.id.txt_title1);
        txtTitle2          = (TextView     ) view.findViewById(R.id.txt_title2);

        txtReceivedMessage = (TextView     ) view.findViewById(R.id.txt_received_message);
        txtCount           = (TextView     ) view.findViewById(R.id.txt_count);
        txtMessage         = (EditText     ) view.findViewById(R.id.txt_message);

        btnReply           = (Button       ) view.findViewById(R.id.btn_reply);

        txtMessage.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if(txtMessage.getText().length() == 0 )
                {
                    btnReply.setEnabled(false);
                }
                else if(txtMessage.getText().length() > 0 )
                {
                    btnReply.setEnabled(true);
                }
            }
        });

        // 20160322 加入下列兩行，搭配layout xml的修改
        // 讓虛擬鍵盤的enter可顯示為Done，且不會變成singleline
        txtMessage.setHorizontallyScrolling(false);
        txtMessage.setMaxLines(Integer.MAX_VALUE);
    }

    protected void setViewContent() {
        txtToSend.setText(coupon.getSenderNickName());

        // TODO 設定頭像

        txtTitle1.setText(coupon.getTitle());
        txtTitle2.setText(coupon.getSubTitle());

        txtReceivedMessage.setText(coupon.getSenderMessage());

        ImageLoader imageLoader = new ImageLoader(getActivity(), getResources().getDimensionPixelSize(R.dimen.list_photo_size));
        imageLoader.loadImage(String.valueOf(coupon.getSenderMemNO()), imgPhoto);
    }

    protected void setListener() {
        // 偵測文字變更
        textWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String countShow;
                // 根據輸入的字串更新輸入字數與按鈕狀態
                if (TextUtils.isEmpty(s)) {
                    countShow = String.format(numberOfChars, 0, lengthLimit);
                } else {
                    countShow = String.format(numberOfChars, s.length(), lengthLimit);
                }
                txtCount.setText(countShow);
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        };
        txtMessage.addTextChangedListener(textWatcher);
        InputFilterUtil.setFilter(txtMessage, new InputFilterUtil.ENUM_FILTER_TYPE[]{InputFilterUtil.ENUM_FILTER_TYPE.FILTER_WRAP, InputFilterUtil.ENUM_FILTER_TYPE.FILTER_SPACE});

        btnReply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            onNextClick();
//                if (buttonsClickListener != null)
//                    buttonsClickListener.onButton1Click(txtMessage.getText().toString());
            }
        });

    }

    public void setButtonsClickListener(ButtonsClickListener buttonsClickListener) {
        this.buttonsClickListener = buttonsClickListener;
    }

    // ----
    // Interface, inner class
    // ----
    public interface ButtonsClickListener {
        void onButton1Click(String inputMessage);
    }

    // ----
    // User interaction
    // ----
    protected void onNextClick() {
        if (!NetworkUtil.isConnected(getActivity())) {
            ((ActivityBase)getActivity()).showAlertDialog(getString(R.string.msg_no_available_network), android.R.string.ok, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            }, true);
        } else {
            try {
                ExtraHttpUtil.replyMessage(coupon.getCpSeq(), coupon.getMsmSeq(), txtMessage.getText().toString(), messageEnterResponseListener, getActivity(), TAG);
                ((ActivityBase) getActivity()).showProgressLoading();
            } catch (JSONException e) {
                Log.w("MyCoupon", e.getMessage(), e);
                return;
            }
        }
    }


    // ----
    // Http
    // ----
    private ResponseListener messageEnterResponseListener = new ResponseListener() {
        @Override
        public void onResponse(ResponseResult result) {
            if (getActivity() == null)
                return;

            ((ActivityBase) getActivity()).dismissProgressLoading();
            String returnCode = result.getReturnCode();

            // 如果returnCode是成功
            if (returnCode.equals(ResponseResult.RESULT_SUCCESS)) {
                // 往下一步走
                if (buttonsClickListener != null)
                    buttonsClickListener.onButton1Click(txtMessage.getText().toString());
            } else {
                // 執行預設的錯誤處理
                handleResponseError(result, (ActivityBase) getActivity());
            }
        }
    };

}
