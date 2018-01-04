package tw.com.taishinbank.ewallet.controller.sv;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

import tw.com.taishinbank.ewallet.R;
import tw.com.taishinbank.ewallet.controller.ActivityBase;
import tw.com.taishinbank.ewallet.controller.FriendListFragment;
import tw.com.taishinbank.ewallet.interfaces.ResponseListener;
import tw.com.taishinbank.ewallet.model.LocalContact;
import tw.com.taishinbank.ewallet.model.ResponseResult;
import tw.com.taishinbank.ewallet.model.Selectable;
import tw.com.taishinbank.ewallet.model.log.SpecialEvent;
import tw.com.taishinbank.ewallet.model.sv.ReceiptResult;
import tw.com.taishinbank.ewallet.model.sv.TxOne;
import tw.com.taishinbank.ewallet.util.EventAnalyticsUtil;
import tw.com.taishinbank.ewallet.util.FormatUtil;
import tw.com.taishinbank.ewallet.util.PreferenceUtil;
import tw.com.taishinbank.ewallet.util.http.HttpUtilBase;
import tw.com.taishinbank.ewallet.util.http.SVHttpUtil;
import tw.com.taishinbank.ewallet.util.responsebody.SVResponseBodyUtil;

public class SharedReceiptActivity extends ActivityBase {

    private static final String TAG = "SharedReceiptActivity";
    private String inputtedAmount;
    private boolean allowGoingBack = true;
    private ArrayList<LocalContact> friendList;
    private String message;
    private static final int MAX_SELECTION_NUM = 20;
    private static final int MIN_SELECTION_NUM = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Set Layout
        setContentView(R.layout.activity_sv_flow);
        setCenterTitle(R.string.sv_menu_share_cost);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //Set Fragment
        final FriendListFragment fragment = FriendListFragment.newSelectableInstance(PreferenceUtil.ENUM_USE_FRIEND.SHARE_RECEIPT);
        fragment.setFriendListListener(new FriendListFragment.FriendListListener() {
            @Override
            public void onNext(ArrayList<LocalContact> list) {
                // 如果人數不到最小應選人數
                if(list.size() < MIN_SELECTION_NUM){
                    showDialog(getString(R.string.shared_receipt_friend_selection_under_min_limit));
                }else {
                    friendList = list;
                    gotoAmountInput();
                }
            }

            @Override
            public boolean shouldContinueUpdateList(ArrayList<Selectable<LocalContact>> selectedContacts) {
                if(selectedContacts.size() > MAX_SELECTION_NUM){
                    fragment.resetLastSelection();
                    String msg = String.format(getString(R.string.shared_receipt_friend_selection_exceed_max_limit), MAX_SELECTION_NUM);
                    showDialog(msg);
                    return false;
                }
                return true;
            }
        });

        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.fg_container, fragment);
        ft.commit();

    }

    @Override
    protected void onStop() {
        super.onStop();
        HttpUtilBase.cancelQueue(TAG);
        dismissProgressLoading();
    }

    // --------
    //  public
    // --------

    public void gotoFragment(Fragment fragment) {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.setCustomAnimations(R.anim.in_right_to_left, R.anim.out_right_to_left, R.anim.in_left_to_right, R.anim.out_left_to_right);
        ft.replace(R.id.fg_container, fragment);
        ft.addToBackStack(null);
        ft.commit();
    }

    public void gotoAmountInput() {
        final ReceiptAmountInputFragment fragment = ReceiptAmountInputFragment.newInstance(R.string.sv_menu_share_cost, friendList);
        fragment.setListener(new AmountInputFragmentBase.AmountInputListener() {
            @Override
            public void onNextClicked(String inputtedAmount) {
                long input = Long.parseLong(inputtedAmount);
                if(input % friendList.size() != 0){
                    showCustomDialog(input, friendList.size());
                    return;
                }
                setInputtedAmount(inputtedAmount);
                gotoMessageEnter();
            }

            @Override
            public void onInfoClicked() {
            }

            @Override
            public void onInputChanged(long inputtedAmount) {

            }
        });
        gotoFragment(fragment);
    }

    public void gotoMessageEnter() {
        MessageEnterFragment.Parameters params = new MessageEnterFragment.Parameters(friendList);
        params.setInputHint(getString(R.string.receipt_msg_input_hint));
        MessageEnterFragment fragment = MessageEnterFragment.createNewInstanceWithParams(params);
        fragment.setButtonsClickListener(new MessageEnterFragment.ButtonsClickListener() {
            @Override
            public void onButton1Click(String inputMessage) {
                if (TextUtils.isEmpty(inputMessage)) {
                    showDialog(getString(R.string.receipt_msg_required_alert));
                    return;
                }
                message = inputMessage;
                gotoDetailConfirm();
            }
        });
        gotoFragment(fragment);
    }

    public void gotoDetailConfirm() {
        final ReceiptDetailConfirmFragment fragment = ReceiptDetailConfirmFragment.newInstance(message, inputtedAmount, friendList);
        fragment.setListener(new ReceiptDetailConfirmFragment.DetailConfirmListener() {
            @Override
            public void onNextClicked() {
                double averageAmount = Double.parseDouble(inputtedAmount) / friendList.size();
                List<TxOne> txOneList = new ArrayList<>();
                for (int i = 0; i < friendList.size(); i++) {
                    TxOne txOne = new TxOne(Integer.parseInt(friendList.get(i).getMemNO()), averageAmount);
                    txOneList.add(txOne);
                }

                try {
                    SVHttpUtil.sendPaymentRequest(message, "7", Double.parseDouble(inputtedAmount), txOneList, responseListener, SharedReceiptActivity.this, TAG);
                    showProgressLoading();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
        gotoFragment(fragment);
    }

    public void gotoResult(ReceiptResult result) {
        ReceiptResultFragment fragment = ReceiptResultFragment.newInstance(result);
        gotoFragment(fragment);
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        allowGoingBack = false;
    }

    @Override
    public void onBackPressed() {
        if (allowGoingBack) {
            super.onBackPressed();
        }
    }

    private void showDialog(String message) {
        showAlertDialog(message, android.R.string.ok,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }, true);
    }


    /**
     * 顯示客製的對話框
     */
    private void showCustomDialog(long totalAmount, int numOfPeople){

        final Dialog dialog = new Dialog(this, R.style.RedEnvelopeDialog);//指定自定義樣式
        dialog.setContentView(R.layout.dialog_sv_shared_receipt);//指定自定義layout

        totalAmount = totalAmount - (totalAmount % numOfPeople);
        String formattedAmount = FormatUtil.toDecimalFormat(totalAmount, true);
        TextView textTotalAmount = (TextView) dialog.findViewById(R.id.text_total_amount);
        textTotalAmount.setText(formattedAmount);

        TextView textAmountPerPerson = (TextView) dialog.findViewById(R.id.text_amount_per_person);
        formattedAmount = FormatUtil.toDecimalFormat(totalAmount/numOfPeople, true);
        textAmountPerPerson.setText(formattedAmount);

        //新增自定義按鈕點擊監聽
        final long finalTotalAmount = totalAmount;
        Button btn = (Button)dialog.findViewById(R.id.button_left);
        btn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                dialog.dismiss();
                inputtedAmount = String.valueOf(finalTotalAmount);
                setInputtedAmount(inputtedAmount);
                gotoMessageEnter();
            }
        });

        btn = (Button)dialog.findViewById(R.id.button_right);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        // 顯示dialog
        dialog.show();
    }

    // -------------------
    //  Getter and setter
    // -------------------

    public void setInputtedAmount(String inputtedAmount) {
        this.inputtedAmount = inputtedAmount;
    }


    // -------------------
    //  Response Listener
    // -------------------

    private ResponseListener responseListener = new ResponseListener() {
        @Override
        public void onResponse(ResponseResult result) {
            dismissProgressLoading();
            String returnCode = result.getReturnCode();

            EventAnalyticsUtil.addSpecialEvent(SharedReceiptActivity.this, new SpecialEvent(SpecialEvent.TYPE_SERVER_API, EventAnalyticsUtil.logFormatToAPI(result.getApiName(), String.format("Return code: %1$s, Message: %2$s", returnCode, result.getReturnMessage()))));

            // 如果returnCode是成功
            if(returnCode.equals(ResponseResult.RESULT_SUCCESS)) {
                ReceiptResult receiptResult = SVResponseBodyUtil.parseReceiptResult(result.getBody());
                gotoResult(receiptResult);
            }else{
                // 執行預設的錯誤處理 
                handleResponseError(result, SharedReceiptActivity.this);
            }
        }
    };

}