package tw.com.taishinbank.ewallet.controller.sv;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.json.JSONException;

import tw.com.taishinbank.ewallet.R;
import tw.com.taishinbank.ewallet.controller.ActivityBase;
import tw.com.taishinbank.ewallet.imagehelper.ImageLoader;
import tw.com.taishinbank.ewallet.interfaces.ResponseListener;
import tw.com.taishinbank.ewallet.interfaces.TransactionStatus;
import tw.com.taishinbank.ewallet.model.LocalContact;
import tw.com.taishinbank.ewallet.model.ResponseResult;
import tw.com.taishinbank.ewallet.model.SVAccountInfo;
import tw.com.taishinbank.ewallet.model.log.SpecialEvent;
import tw.com.taishinbank.ewallet.model.sv.SVTransactionOut;
import tw.com.taishinbank.ewallet.util.EventAnalyticsUtil;
import tw.com.taishinbank.ewallet.util.FormatUtil;
import tw.com.taishinbank.ewallet.util.NetworkUtil;
import tw.com.taishinbank.ewallet.util.PreferenceUtil;
import tw.com.taishinbank.ewallet.util.http.HttpUtilBase;
import tw.com.taishinbank.ewallet.util.http.SVHttpUtil;

public class ReceiveRequestFragment extends ResultFragmentBase {

    private static final String TAG = "ReceiveRequestFragment";
    private SVTransactionOut svTransactionOut;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        svTransactionOut = getArguments().getParcelable(ReceiveRequestActivity.EXTRA_SV_TRX_OUT);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // 改inflate自己的View
        View view = inflater.inflate(R.layout.fragment_sv_result_scrollable, container, false);

        setViewHold(view);

        setViewContent();

        setListener();

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onStop() {
        super.onStop();
        HttpUtilBase.cancelQueue(TAG);
        if(getActivity() != null)
            ((ActivityBase) getActivity()).dismissProgressLoading();
    }

    // ----
    // HTTP Request
    // ----
    private void cancelTransactionOut() {
        // 如果沒有網路連線，顯示提示對話框
        if (!NetworkUtil.isConnected(getActivity())) {
            ((ActivityBase)getActivity()).showAlertDialog(getString(R.string.msg_no_available_network), android.R.string.ok, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            }, true);
        } else {
            // 呼叫api取消付款請求
            try {
                SVHttpUtil.updatePaymentRequest(svTransactionOut.getTxfSeq(), svTransactionOut.getTxfdSeq(), "1", cancelTransactionOutListener, getActivity(), TAG);
                ((ActivityBase) getActivity()).showProgressLoading();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    // 呼叫取消付款api的listener
    private ResponseListener cancelTransactionOutListener = new ResponseListener() {

        @Override
        public void onResponse(ResponseResult result) {
            if(getActivity() == null)
                return;

            ((ActivityBase) getActivity()).dismissProgressLoading();
            String returnCode = result.getReturnCode();

            EventAnalyticsUtil.addSpecialEvent(getActivity(), new SpecialEvent(SpecialEvent.TYPE_SERVER_API, EventAnalyticsUtil.logFormatToAPI(result.getApiName(), String.format("Return code: %1$s, Message: %2$s", returnCode, result.getReturnMessage()))));

            // 如果returnCode是成功
            if (returnCode.equals(ResponseResult.RESULT_SUCCESS)) {
                getActivity().finish();
            } else {
                // 執行預設的錯誤處理 
                handleResponseError(result, (ActivityBase) getActivity());
            }
        }
    };


    // ----
    // Private method
    // ----

    protected void setViewHold(View view) {
        super.setViewHold(view);

    }


    protected void setViewContent() {
        txtFromToWho.setText(svTransactionOut.getName());

        txtDate.setText(FormatUtil.toTimeFormatted(svTransactionOut.getCreateDate(), false));

        txtAmount.setText(FormatUtil.toDecimalFormatFromString(svTransactionOut.getAmount()));

        {//將圖片改成文字格式訊息，且有底線
            // 設定有底線的TextView行數、底線顏色、底線寬度
            lytSendingMsg.setVisibility(View.VISIBLE);
            lytSvResult.setVisibility(View.GONE);

            Resources resources = getResources();
            txtSendingMessageLine.setNumberOfLines(resources.getInteger(R.integer.red_envelope_sent_result_blessing_lines));
            txtSendingMessageLine.setLineColor(resources.getColor(R.color.red_envelope_divider));
            txtSendingMessageLine.setLineWidth(resources.getDimensionPixelSize(R.dimen.red_envelope_sent_result_divider_height));

            txtSendingMessage.setText(svTransactionOut.getReplyMessage());
        }

        // 如果尚未完成
        if(TransactionStatus.AWAITING.getCode().equals(svTransactionOut.getTxStatus())){
            txtResultTitle.setText(R.string.sv_receive_request);
            lytCautionArea.setVisibility(View.VISIBLE);
            btnAction1.setText(R.string.sv_trx_detail_req_out_action1);
            btnAction2.setText(R.string.sv_trx_detail_req_out_action2);
        }else {
            // 如果已取消
            if (TransactionStatus.CANCELLED.getCode().equals(svTransactionOut.getTxStatus())) {
                txtResultTitle.setText(R.string.sv_receive_request_canceled);
            // 如果已完成
            } else if (TransactionStatus.FINISHED.getCode().equals(svTransactionOut.getTxStatus())) {
                txtResultTitle.setText(R.string.sv_receive_request_finished);
            }
            lytCautionArea.setVisibility(View.GONE);
            btnAction1.setVisibility(View.GONE);
            btnAction2.setVisibility(View.GONE);
        }

        ImageLoader imageLoader = new ImageLoader(getActivity(), getResources().getDimensionPixelSize(R.dimen.list_photo_size));
        imageLoader.loadImage(String.valueOf(svTransactionOut.getTxToMemNO()), imgPhoto);
    }

    protected void setListener() {
        //Cancel Request
        btnAction1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 顯示對話框確認是否執行取消的動作
                String msg = String.format(getString(R.string.receive_payment_request_cancel_request_confirm), svTransactionOut.getName());
                ((ActivityBase)getActivity()).showAlertDialog(msg, R.string.button_confirm, android.R.string.cancel,
                        // 按下確定
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                                // 取消付款
                                cancelTransactionOut();
                            }
                        }, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        }, false);
            }
        });
        //Go to pay
        btnAction2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SVAccountInfo svAccountInfo = PreferenceUtil.getSVAccountInfo(getActivity());
                // 如果餘額不夠付款
                if(Integer.parseInt(svTransactionOut.getAmount()) > Integer.parseInt(svAccountInfo.getBalance())){
                    ((ActivityBase) getActivity()).showAlertDialog(getString(R.string.receive_payment_request_balance_not_enough),
                            R.string.sv_account_detail_deposit, android.R.string.cancel,
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    Intent intent = new Intent(getActivity(), DepositActivity.class);
                                    intent.putExtra(DepositActivity.EXTRA_GO_ORIGINAL_PROCESS_AFTER_SUCCESS, getString(R.string.sv_menu_pay));
                                    startActivity(intent);
                                    dialog.dismiss();
                                }
                            },
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            }, true);
                }else {
                    // 移動到轉帳流程（D-T-43）
                    Intent intent = new Intent();
                    intent.setClass(getActivity(), PaymentActivity.class);
                    LocalContact localContact = new LocalContact();
                    localContact.setNickname(svTransactionOut.getName());
                    localContact.setMemNO(Integer.toString(svTransactionOut.getTxToMemNO()));
                    intent.putExtra(PaymentActivity.EXTRA_RECEIVER, localContact);
                    intent.putExtra(PaymentActivity.EXTRA_FIXED_AMOUNT, svTransactionOut.getAmount());
                    intent.putExtra(PaymentActivity.EXTRA_TXFSEQ, svTransactionOut.getTxfSeq());
                    intent.putExtra(PaymentActivity.EXTRA_TXFDSEQ, svTransactionOut.getTxfdSeq());
                    startActivity(intent);
                }
            }
        });

        btnSvResultAction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }

    // ----
    // Class, Interface, enum
    // ----


}
