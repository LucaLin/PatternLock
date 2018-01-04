package tw.com.taishinbank.ewallet.controller.sv;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

import tw.com.taishinbank.ewallet.R;
import tw.com.taishinbank.ewallet.controller.ActivityBase;
import tw.com.taishinbank.ewallet.controller.FriendListFragment;
import tw.com.taishinbank.ewallet.interfaces.ResponseListener;
import tw.com.taishinbank.ewallet.model.LocalContact;
import tw.com.taishinbank.ewallet.model.ResponseResult;
import tw.com.taishinbank.ewallet.model.SVAccountInfo;
import tw.com.taishinbank.ewallet.model.Selectable;
import tw.com.taishinbank.ewallet.model.log.SpecialEvent;
import tw.com.taishinbank.ewallet.model.red.RedEnvelopeSentResult;
import tw.com.taishinbank.ewallet.model.red.RedEnvelopeSentResultEach;
import tw.com.taishinbank.ewallet.model.sv.ReceiveRequestPaymentResult;
import tw.com.taishinbank.ewallet.model.sv.TxOne;
import tw.com.taishinbank.ewallet.util.EventAnalyticsUtil;
import tw.com.taishinbank.ewallet.util.PreferenceUtil;
import tw.com.taishinbank.ewallet.util.http.HttpUtilBase;
import tw.com.taishinbank.ewallet.util.http.SVHttpUtil;
import tw.com.taishinbank.ewallet.util.responsebody.RedEnvelopeResponseBodyUtil;
import tw.com.taishinbank.ewallet.util.responsebody.SVResponseBodyUtil;

/**
 * 用於轉帳付款、掃碼付款流程
 */
public class PaymentActivity extends ActivityBase {

    private static final String TAG = "PaymentActivity";
    public final static String EXTRA_RECEIVER = "EXTRA_RECEIVER";

    // 來自付款請求的確認付款鈕
    public final static String EXTRA_FIXED_AMOUNT = "EXTRA_FIXED_AMOUNT";
    public final static String EXTRA_TXFSEQ = "EXTRA_TXFSEQ";
    public final static String EXTRA_TXFDSEQ = "EXTRA_TXFDSEQ";

    private String inputtedAmount;
    private boolean allowGoingBack = true;
    private ArrayList<LocalContact> friendList;
    private String message;
    private boolean showMessageInputBefore = false;
    private static final int MAX_SELECTION_NUM = 1;

    // 來自付款請求的確認付款鈕
    private String fixedInputAmount = null;
    private int txfSeq;
    private int txfdSeq;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Set Layout
        setContentView(R.layout.activity_sv_flow);
        setCenterTitle(R.string.sv_menu_pay);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //Set Fragment
        LocalContact localContact = null;
        if(getIntent() != null){
            localContact = getIntent().getParcelableExtra(EXTRA_RECEIVER);
            fixedInputAmount = getIntent().getStringExtra(EXTRA_FIXED_AMOUNT);
            txfSeq = getIntent().getIntExtra(EXTRA_TXFSEQ, -1);
            txfdSeq = getIntent().getIntExtra(EXTRA_TXFDSEQ, -1);
        }

        if (localContact == null) {
            //Set Fragment
            final FriendListFragment fragment = FriendListFragment.newSelectableInstance(PreferenceUtil.ENUM_USE_FRIEND.PAYMENT);
            fragment.setFriendListListener(new FriendListFragment.FriendListListener() {
                @Override
                public void onNext(ArrayList<LocalContact> list) {
                    friendList = list;
                    gotoAmountInput(true);
                }

                @Override
                public boolean shouldContinueUpdateList(ArrayList<Selectable<LocalContact>> selectedContacts) {
                    if (selectedContacts.size() > MAX_SELECTION_NUM) {
                        fragment.resetLastSelection();
                        String msg = String.format(getString(R.string.friend_selection_exceed_limit), getString(R.string.sv_menu_pay), MAX_SELECTION_NUM);
                        showAlertDialog(msg);
                        return false;
                    }
                    return true;
                }
            });

            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.fg_container, fragment);
            ft.commit();

        // 由QR 掃碼、好友詳細頁啟動
        } else {
            friendList = new ArrayList<>();
            friendList.add(localContact);
            gotoAmountInput(false);
        }
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

    public void gotoAmountInput(boolean addToBackStack) {
        final PaymentAmountInputFragment fragment = (fixedInputAmount == null)
                ? PaymentAmountInputFragment.newInstance(friendList) : PaymentAmountInputFragment.newInstance(friendList, fixedInputAmount);
        fragment.setListener(new AmountInputFragmentBase.AmountInputListener() {
            @Override
            public void onNextClicked(String inputtedAmount) {
                setInputtedAmount(inputtedAmount);
                if (showMessageInputBefore) {
                    gotoMessageEnter(message);
                } else {
                    gotoMessageEnter(null);
                }
            }

            @Override
            public void onInfoClicked() {
                /*  點擊箭頭進入帳戶詳情：
                    帳戶類型：第一類、第二類、第三類
                    當日可轉帳餘額：$xx,xxx
                    單筆轉帳上限：$xx,xxx
                    儲值帳戶餘額：$xx,xxx
                    本次轉帳可用餘額：$xx,xxx (紅色字) */
                fragment.showSVDetail(SVAccountDetailFragment.ENUM_TYPE.PAY,
                        -1,
                        true);
            }

            @Override
            public void onInputChanged(long inputtedAmount) {

            }
        });

        if (addToBackStack) {
            gotoFragment(fragment);
        } else {
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.fg_container, fragment);
            ft.commit();
        }
    }

    public void gotoMessageEnter(String messageInputBefore) {
        MessageEnterFragment.Parameters params = new MessageEnterFragment.Parameters(friendList);
        params.setInputHint(getString(R.string.pay_msg_input_hint));
        params.setMsgInputBefore(messageInputBefore);
        MessageEnterFragment fragment = MessageEnterFragment.createNewInstanceWithParams(params);
        fragment.setButtonsClickListener(new MessageEnterFragment.ButtonsClickListener() {
            @Override
            public void onButton1Click(String inputMessage) {
                message = inputMessage;
                gotoPasswordInput();
            }
        });
        gotoFragment(fragment);
    }

    public void gotoPasswordInput() {
        PaymentAuthFragment fragment = PaymentAuthFragment.newInstance(friendList.get(0), inputtedAmount, message);
        fragment.setListener(new PaymentAuthFragment.OnSendRequestListener() {
            @Override
            public void onSendRequest(String userPwInAES) {
                // 如果有交易序號，為從付款請求來，呼叫付款請求的確認付款
                if (txfSeq >= 0 || txfdSeq >= 0) {
                    try {
                        SVHttpUtil.transferForPaymentReq(txfSeq, txfdSeq, message, userPwInAES,
                                Integer.parseInt(inputtedAmount), Integer.parseInt(friendList.get(0).getMemNO()),
                                responseListener, PaymentActivity.this, TAG);
                        showProgressLoading();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                // 否則呼叫付款的api
                } else {
                    try {
                        List<TxOne> list = new ArrayList<>();
                        list.add(new TxOne(Integer.parseInt(friendList.get(0).getMemNO()), Double.parseDouble(inputtedAmount)));
                        SVHttpUtil.trasnferFromSVAccountTo(message, "5", Double.parseDouble(inputtedAmount),
                                userPwInAES, -1, list, responseListener, PaymentActivity.this, TAG);
                        showProgressLoading();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        gotoFragment(fragment);
    }

    public void gotoResult(RedEnvelopeSentResult result) {
        PaymentResultFragment fragment = PaymentResultFragment.newInstance(result);
        gotoResult(fragment);
    }

    public void gotoResult(String errorMessage) {
        PaymentResultFragment fragment = PaymentResultFragment.newInstance(errorMessage);
        gotoResult(fragment);
    }

    private void gotoResult(PaymentResultFragment fragment){
        fragment.setListener(new PaymentResultFragment.OnRetryClickedListener() {
            @Override
            public void onRetryClicked() {
                getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                allowGoingBack = true;
                onBackPressed();
            }
        });
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

    // -------------------
    //  Getter and setter
    // -------------------

    public void setInputtedAmount(String inputtedAmount) {
        this.inputtedAmount = inputtedAmount;
    }

    // -------------------
    //  Listeners
    // -------------------

    // 儲值的response listener
    private ResponseListener responseListener = new ResponseListener() {

        @Override
        public void onResponse(ResponseResult result) {
            dismissProgressLoading();

            String returnCode = result.getReturnCode();

            EventAnalyticsUtil.addSpecialEvent(PaymentActivity.this, new SpecialEvent(SpecialEvent.TYPE_SERVER_API, EventAnalyticsUtil.logFormatToAPI(result.getApiName(), String.format("Return code: %1$s, Message: %2$s", returnCode, result.getReturnMessage()))));

            // 如果returnCode是成功
            if(returnCode.equals(ResponseResult.RESULT_SUCCESS)) {
                RedEnvelopeSentResult sentResult;
                // 如果有交易序號，為從付款請求來
                if (txfSeq >= 0 || txfdSeq >= 0) {
                    ReceiveRequestPaymentResult paymentResult = SVResponseBodyUtil.parseReceiveRequestPaymentResult(result.getBody());
                    // 轉成跟轉帳付款結果的物件
                    sentResult = new RedEnvelopeSentResult();
                    sentResult.setAmount(String.valueOf(paymentResult.getAmount()));
                    sentResult.setSender(paymentResult.getSender());
                    sentResult.setSenderMem(String.valueOf(paymentResult.getSenderSeq()));
                    sentResult.setBalance(String.valueOf(paymentResult.getBalance()));
                    sentResult.setCreateDate(paymentResult.getCreateDate());
                    sentResult.setTxfSeq(String.valueOf(paymentResult.getTxfSeq()));
                    // 設置詳情
                    RedEnvelopeSentResultEach eachResult = new RedEnvelopeSentResultEach();
                    eachResult.setPerAmount(String.valueOf(paymentResult.getAmount()));
                    eachResult.setName(paymentResult.getName());
                    eachResult.setToMem(String.valueOf(paymentResult.getToMemNO()));
                    eachResult.setResult(paymentResult.getResult());
                    eachResult.setBancsMsg(paymentResult.getBancsMsg());
                    eachResult.setTxfdSeq(String.valueOf(paymentResult.getTxfdSeq()));
                    eachResult.setAccount(String.valueOf(paymentResult.getToAccount()));
                    sentResult.setTxResult(new RedEnvelopeSentResultEach[]{eachResult});

                }else {
                    sentResult = RedEnvelopeResponseBodyUtil.getRedEnvelopSentResult(result.getBody());

                }

                // 塞入轉出帳號資訊
                SVAccountInfo svAccountInfo = PreferenceUtil.getSVAccountInfo(PaymentActivity.this);
                sentResult.setAccount(svAccountInfo.getPrepaidAccount());

                gotoResult(sentResult);
            }else{
                // 如果是儲值密碼錯誤
                if (returnCode.equals(ResponseResult.RESULT_INCORRECT_SV_MIMA)) {
                    showAlertDialog(result.getReturnMessage(),
                            android.R.string.ok,
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            }, true);

                // 其他則帶到下一頁
                } else if(!handleCommonError(result, PaymentActivity.this)){
                    gotoResult(result.getReturnMessage());
                }

            }
        }
    };
}