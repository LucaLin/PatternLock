package tw.com.taishinbank.ewallet.controller.sv;

import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.view.MenuItem;

import org.json.JSONException;

import java.util.ArrayList;

import tw.com.taishinbank.ewallet.R;
import tw.com.taishinbank.ewallet.controller.ActivityBase;
import tw.com.taishinbank.ewallet.interfaces.ResponseListener;
import tw.com.taishinbank.ewallet.model.LocalContact;
import tw.com.taishinbank.ewallet.model.ResponseResult;
import tw.com.taishinbank.ewallet.model.sv.SVTransactionIn;
import tw.com.taishinbank.ewallet.util.http.HttpUtilBase;
import tw.com.taishinbank.ewallet.util.http.RedEnvelopeHttpUtil;

/**
 *
 */
public class ReceivePaymentActivity extends ActivityBase {

    private static final String TAG = "ReceivePaymentActivity";
    public static final String EXTRA_SV_TRX_IN = "EXTRA_SV_TRX_IN";

    private SVTransactionIn svTransactionIn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Set Layout
        setContentView(R.layout.activity_sv_flow);
        setCenterTitle(R.string.sv_receive_payment_title);

        svTransactionIn = getIntent().getParcelableExtra(EXTRA_SV_TRX_IN);

        //Set Fragment
        ReceivePaymentFragment fragment = new ReceivePaymentFragment();
        Bundle bundle = new Bundle();
        bundle.putParcelable(EXTRA_SV_TRX_IN, svTransactionIn);
        fragment.setArguments(bundle);

        //Start Fragment
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.fg_container, fragment);
        ft.commit();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
//        switch (item.getItemId()) {
            // 返回鈕
//            case android.R.id.home:
//                // 做按下返回鍵的事，這樣動畫才會一致。
//                onBackPressed();
//                return false;
//        }
//        return super.onOptionsItemSelected(item);
        return false;
    }

    @Override
    protected void onStop() {
        super.onStop();
        HttpUtilBase.cancelQueue(TAG);
        dismissProgressLoading();
    }

    // ----
    // public
    // ----
    //Flow step 1
    public void replyMessage() {
        LocalContact localContact = new LocalContact();
        localContact.setMemNO(String.valueOf(svTransactionIn.getTxMemNO()));
        localContact.setNickname(svTransactionIn.getTxMemName());
        ArrayList<LocalContact> localContactsList = new ArrayList<>();
        localContactsList.add(localContact);

        MessageEnterFragment.Parameters params = new MessageEnterFragment.Parameters(localContactsList);
        params.setAmount(svTransactionIn.getAmount());
        params.setReceivedMessage(svTransactionIn.getSenderMessage());
        params.setButton1Text(getResources().getString(R.string.reply));
        params.setIsNeedInputMsg(true);
        MessageEnterFragment fragment = MessageEnterFragment.createNewInstanceWithParams(params);

        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.setCustomAnimations(R.anim.in_right_to_left, R.anim.out_right_to_left, R.anim.in_left_to_right, R.anim.out_left_to_right);
        ft.replace(R.id.fg_container, fragment);
        ft.addToBackStack("");
        ft.commit();

        fragment.setButtonsClickListener(new MessageEnterFragment.ButtonsClickListener() {
            @Override
            public void onButton1Click(String inputMessage) {
                try {
                    ReceivePaymentActivity.this.showProgressLoading();
                    RedEnvelopeHttpUtil.replyRedEnvelopeMsg(
                            String.valueOf(svTransactionIn.getTxfSeq()),
                            String.valueOf(svTransactionIn.getTxfdSeq()),
                            inputMessage,
                            replyMessageResponseListener,
                            ReceivePaymentActivity.this, TAG);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    //Flow step 2
    public void gotoSendingResult() {
        //Set Fragment
        ReceivePaymentResultFragment fragment = new ReceivePaymentResultFragment();
        Bundle bundle = new Bundle();
        bundle.putParcelable(EXTRA_SV_TRX_IN, svTransactionIn);
        fragment.setArguments(bundle);

        //Start Fragment
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.fg_container, fragment);
        ft.commit();

    }

    // ----
    // Http
    // ----
    protected ResponseListener replyMessageResponseListener = new ResponseListener() {
        @Override
        public void onResponse(ResponseResult result) {
            dismissProgressLoading();

            if (result.getReturnCode().equals(ResponseResult.RESULT_SUCCESS)) {
                gotoSendingResult();
            } else {
                // 執行預設的錯誤處理 
                handleResponseError(result, ReceivePaymentActivity.this);
            }
        }
    };

}
