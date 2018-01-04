package tw.com.taishinbank.ewallet.controller.sv;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TextView;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

import tw.com.taishinbank.ewallet.R;
import tw.com.taishinbank.ewallet.adapter.sv.TransactionLogDetailListRecyclerAdapter;
import tw.com.taishinbank.ewallet.controller.ActivityBase;
import tw.com.taishinbank.ewallet.imagehelper.ImageLoader;
import tw.com.taishinbank.ewallet.interfaces.ResponseListener;
import tw.com.taishinbank.ewallet.interfaces.TransactionStatus;
import tw.com.taishinbank.ewallet.interfaces.TransactionType;
import tw.com.taishinbank.ewallet.interfaces.TransactionTypeOrStatus;
import tw.com.taishinbank.ewallet.model.LocalContact;
import tw.com.taishinbank.ewallet.model.ResponseResult;
import tw.com.taishinbank.ewallet.model.SVAccountInfo;
import tw.com.taishinbank.ewallet.model.log.SpecialEvent;
import tw.com.taishinbank.ewallet.model.sv.SVTransaction;
import tw.com.taishinbank.ewallet.model.sv.SVTransactionIn;
import tw.com.taishinbank.ewallet.model.sv.SVTransactionOut;
import tw.com.taishinbank.ewallet.model.sv.SVTransactionPayer;
import tw.com.taishinbank.ewallet.util.EventAnalyticsUtil;
import tw.com.taishinbank.ewallet.util.FormatUtil;
import tw.com.taishinbank.ewallet.util.NetworkUtil;
import tw.com.taishinbank.ewallet.util.PreferenceUtil;
import tw.com.taishinbank.ewallet.util.http.SVHttpUtil;

public class TransactionLogDetailFragment extends Fragment {

    public static final String EXTRA_TRX = "EXTRA_TRX";
    private static final String TAG = "TransactionLogDetailFragment";

    // -- View Hold --
    //  - Common -
    private TextView txtSubtitle;
    private ImageView imgPhoto;
    private TextView textName;
    private TextView textAmount;
    private TextView textMessage;
    private TextView textNotPay;
    private TextView textTime;
    private RecyclerView listPayer;

    private Button btnAction1;
    private Button btnAction2;


    //  - Only for withdraw, deposit -
    private TableLayout lytTrxDestail;
    private TextView lblPreAmount;
    private TextView txtPreAmount;
    private TextView lblCharge;
    private TextView txtCharge;
    private TextView lblAmount;
    private TextView txtAmount;

    //  - Separator title -
    private LinearLayout lytPayer;
    private TextView txtLegend;

    // -- Data Model --
    private SVTransaction transaction;

    // -- View helper --
    private TransactionLogDetailListRecyclerAdapter adapter;
    private ImageLoader imageLoader;

    public TransactionLogDetailFragment() {

    }

    public static TransactionLogDetailFragment newInstance(SVTransaction transaction) {
        TransactionLogDetailFragment fragment = new TransactionLogDetailFragment();
        Bundle bundle = new Bundle();
        bundle.putParcelable(TransactionLogDetailFragment.EXTRA_TRX, transaction);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        if(getActivity() != null) {
            ((ActivityBase) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        transaction = getArguments().getParcelable(EXTRA_TRX);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_sv_transaction_log_detail, container, false);

        // Set View Hold
        setViewHold(view);
        listPayer.setLayoutManager(new LinearLayoutManager(getActivity()));

        // Helper
        imageLoader = new ImageLoader(getActivity(), getActivity().getResources().getDimensionPixelSize(R.dimen.list_photo_size));
        adapter = new TransactionLogDetailListRecyclerAdapter(imageLoader);

        listPayer.setAdapter(adapter);

        // Set view value, show/hide.
        setViewContent();

        // Set Listener
        setListener();

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();

        if (transaction instanceof SVTransactionOut)
            ((ActivityBase) getActivity()).setCenterTitle(R.string.sv_transaction_out);
        else if (transaction instanceof SVTransactionIn)
            ((ActivityBase) getActivity()).setCenterTitle(R.string.sv_transaction_in);
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

    // ----
    // User interaction
    // ----
    private void onButtonAction1Click() {
        if (TransactionStatus.AWAITING.getCode().equals(transaction.getTxStatus())) {
            // 顯示對話框確認是否執行取消的動作
            String msg = "";
            if (transaction instanceof SVTransactionOut) {
                msg = String.format(getString(R.string.receive_payment_request_cancel_request_confirm), ((SVTransactionOut) transaction).getName());
            } else if (transaction instanceof SVTransactionIn) {
                msg = String.format(getString(R.string.receive_payment_request_cancel_request_confirm), ((SVTransactionIn) transaction).getTxMemName());
            }
            ((ActivityBase)getActivity()).showAlertDialog(msg, R.string.button_confirm, android.R.string.cancel,
                    // 按下確定
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            // 取消付款請求
                            cancelPaymentRequest();
                        }
                    }, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    }, false);
        }
    }

    private void onButtonAction2Click() {
        // 確認付款
        if (transaction instanceof SVTransactionOut) {
            SVAccountInfo svAccountInfo = PreferenceUtil.getSVAccountInfo(getActivity());
            // 如果餘額不夠付款
            if(Integer.parseInt(transaction.getAmount()) > Integer.parseInt(svAccountInfo.getBalance())){
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
                localContact.setNickname(((SVTransactionOut) transaction).getName());
                localContact.setMemNO(Integer.toString(((SVTransactionOut) transaction).getTxToMemNO()));
                intent.putExtra(PaymentActivity.EXTRA_RECEIVER, localContact);
                intent.putExtra(PaymentActivity.EXTRA_FIXED_AMOUNT, transaction.getAmount());
                intent.putExtra(PaymentActivity.EXTRA_TXFSEQ, transaction.getTxfSeq());
                intent.putExtra(PaymentActivity.EXTRA_TXFDSEQ, transaction.getTxfdSeq());
                startActivity(intent);
            }
        // 再次發送
        } else if (transaction instanceof SVTransactionIn) {
            // 顯示對話框確認是否執行重新發送的動作
            String msg = String.format(getString(R.string.receive_payment_request_resend_request_confirm), ((SVTransactionIn) transaction).getTxMemName());
            ((ActivityBase)getActivity()).showAlertDialog(msg, R.string.button_confirm, android.R.string.cancel,
                    // 按下確定
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            // 重新發送
                            resendPaymentRequest();
                        }
                    }, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    }, false);
        }
    }


    // ----
    // Http
    // ----
    // 取消付款請求
    private void cancelPaymentRequest(){
        updatePaymentRequest("1");
    }
    // 重新發送付款請求
    private void resendPaymentRequest(){
        updatePaymentRequest("2");
    }
    private void updatePaymentRequest(String operateType) {
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
                SVHttpUtil.updatePaymentRequest(transaction.getTxfSeq(), transaction.getTxfdSeq(), operateType, updatePaymentRequestListener, getActivity(), TAG);
                ((ActivityBase) getActivity()).showProgressLoading();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    // 呼叫付款請求－取消付款、再次發送api的listener
    private ResponseListener updatePaymentRequestListener = new ResponseListener() {

        @Override
        public void onResponse(ResponseResult result) {
            if(getActivity() == null)
                return;

            ((ActivityBase) getActivity()).dismissProgressLoading();
            String returnCode = result.getReturnCode();

            EventAnalyticsUtil.addSpecialEvent(getActivity(), new SpecialEvent(SpecialEvent.TYPE_SERVER_API, EventAnalyticsUtil.logFormatToAPI(result.getApiName(), String.format("Return code: %1$s, Message: %2$s", returnCode, result.getReturnMessage()))));

            // 如果returnCode是成功
            if (returnCode.equals(ResponseResult.RESULT_SUCCESS)) {
                // 返回上一頁更新頁面
                getFragmentManager().popBackStack();
            }else{
                // 執行預設的錯誤處理 
                handleResponseError(result, (ActivityBase) getActivity());
            }
        }
    };

    // ----
    // Private method
    // ----
    private void setViewHold(View view) {
        txtSubtitle = (TextView)    view.findViewById(R.id.txt_subtitle);
        imgPhoto   = (ImageView)    view.findViewById(R.id.image_photo);
        textName   = (TextView)     view.findViewById(R.id.text_name);
        textAmount = (TextView)     view.findViewById(R.id.text_amount);
        textNotPay = (TextView)     view.findViewById(R.id.text_not_pay);
        textTime   = (TextView)     view.findViewById(R.id.text_time);

        textMessage = (TextView)    view.findViewById(R.id.text_message);

        lytPayer    = (LinearLayout) view.findViewById(R.id.lyt_payer);
        txtLegend   = (TextView)     view.findViewById(R.id.txt_legend);
        listPayer   = (RecyclerView) view.findViewById(R.id.recycler_view);

        btnAction1 = (Button)       view.findViewById(R.id.btn_action_1);
        btnAction2 = (Button)       view.findViewById(R.id.btn_action_2);

        // Only for withdraw, deposit
        lytTrxDestail = (TableLayout) view.findViewById(R.id.lyt_trx_detail);
        lblPreAmount  = (TextView) view.findViewById(R.id.lbl_pre_amount);
        txtPreAmount  = (TextView) view.findViewById(R.id.txt_pre_amount);
        lblCharge     = (TextView) view.findViewById(R.id.lbl_charge);
        txtCharge     = (TextView) view.findViewById(R.id.txt_charge);
        lblAmount     = (TextView) view.findViewById(R.id.lbl_amount);
        txtAmount     = (TextView) view.findViewById(R.id.txt_amount);
    }

    private void setViewContent() {
        //Subtitle - 付款請求, 轉帳, 提領, 儲值
        // txtSubtitle
        if (TransactionType.TRANSFER_TO.code.equals(transaction.getTxType())) {
            txtSubtitle.setText(R.string.sv_trx_detail_sub_title_transfer);
        } else if (TransactionType.DEPOSIT.code.equals(transaction.getTxType())) {
            txtSubtitle.setText(R.string.sv_trx_detail_sub_title_deposit);
        } else if (TransactionType.WITHDRAW.code.equals(transaction.getTxType())) {
            txtSubtitle.setText(R.string.sv_trx_detail_sub_title_withdraw);
        } else if (TransactionType.REQUEST_SINGLE.code.equals(transaction.getTxType())) {
            txtSubtitle.setText(R.string.sv_trx_detail_sub_title_request);
        } else if (TransactionType.REQUEST_MULTIPLE.code.equals(transaction.getTxType())) {
            txtSubtitle.setText(R.string.sv_trx_detail_sub_title_request_multiple);
        }

        //金額
        textAmount.setText(FormatUtil.toDecimalFormatFromString(transaction.getAmount(), true));

        //未完成字樣，由Status Code 來判斷。(非finished or cancel)
        //按鈕 - 未完成才需要顯示
        if (TransactionStatus.AWAITING.getCode().equals(transaction.getTxStatus())) {
            //未完成字樣
            textNotPay.setVisibility(View.VISIBLE);

            //按鈕
            btnAction1.setVisibility(View.VISIBLE);
            btnAction2.setVisibility(View.VISIBLE);
            if (transaction instanceof SVTransactionOut) {
                btnAction1.setText(R.string.sv_trx_detail_req_out_action1);
                btnAction2.setText(R.string.sv_trx_detail_req_out_action2);
            } else if (transaction instanceof SVTransactionIn) {
                btnAction1.setText(R.string.sv_trx_detail_req_in_action1);
                btnAction2.setText(R.string.sv_trx_detail_req_in_action2);
            }
        } else {
            if (TransactionStatus.CANCELLED.getCode().equals(transaction.getTxStatus())) {
                // 顯示已取消字樣
                textNotPay.setVisibility(View.VISIBLE);
                textNotPay.setText(TransactionTypeOrStatus.CANCEL.getDescription());
            }else {
                textNotPay.setVisibility(View.GONE);
            }
            btnAction1.setVisibility(View.GONE);
            btnAction2.setVisibility(View.GONE);
        }

        //時間
        textTime.setText(FormatUtil.toTimeFormatted(transaction.getCreateDate()));

        //是否顯示要付錢的人 - 提領、儲值不顯示
        //  lytTrxDestail, lytPayer, textMessage, list Payer
        if (TransactionType.WITHDRAW.code.equals(transaction.getTxType()) ||
                TransactionType.DEPOSIT.code.equals(transaction.getTxType())) {
            lytTrxDestail.setVisibility(View.VISIBLE);
            lytPayer.setVisibility(View.GONE);
            textMessage.setVisibility(View.GONE);
        } else {
            lytTrxDestail.setVisibility(View.GONE);
            lytPayer.setVisibility(View.VISIBLE);
            textMessage.setVisibility(View.VISIBLE);

            //訊息 / 提領、儲值明細 - 二選一
            textMessage.setText(transaction.getSenderMessage());

            // Create a new payer
            SVTransactionPayer payer = new SVTransactionPayer();
            payer.setAmount(transaction.getAmount());
            payer.setCreateDate(transaction.getCreateDate());
            // 如果是支出
            if (transaction instanceof SVTransactionOut) {
                int memNO = ((SVTransactionOut) transaction).getTxToMemNO();
                payer.setMemNO(Integer.toString(memNO));
                payer.setName(((SVTransactionOut) transaction).getName());
            // 如果是收入
            } else {
                String memNO = PreferenceUtil.getMemNO(getActivity());
                payer.setMemNO(memNO);
                String name = PreferenceUtil.getNickname(getActivity());
                payer.setName(name);
            }
            payer.setReplyMessage(transaction.getReplyMessage());
            payer.setReplyTime(transaction.getReplyDate());
            payer.setTxfdSeq(String.valueOf(transaction.getTxfdSeq()));

            List<SVTransactionPayer> payers = new ArrayList<>();
            payers.add(payer);

            adapter.setList(payers);
            adapter.notifyDataSetChanged();
        }

        //顯示　儲值、提領 詳情
//        if (TransactionType.DEPOSIT.code.equals(transaction.getTxType())) {
//            lytTrxDestail.setVisibility(View.VISIBLE);
//            lblPreAmount.setText(R.string.sv_trx_in_detail_pre_amount);
//            txtPreAmount.setText(FormatUtil.toDecimalFormatFromString(transaction.getAmount(), true)); //TODO read if from server fee or charge
//            txtCharge.setText(FormatUtil.toDecimalFormatFromString("0", true)); //TODO read if from server fee or charge
//            lblAmount.setText(R.string.sv_trx_in_detail_amount);
//            txtAmount.setText(FormatUtil.toDecimalFormatFromString(transaction.getAmount(), true));
//        }
        if (TransactionType.WITHDRAW.code.equals(transaction.getTxType())) {
            lytTrxDestail.setVisibility(View.VISIBLE);
            lblPreAmount.setText(R.string.sv_trx_out_detail_pre_amount);
            int fee = (int) ((SVTransactionOut) transaction).getFee();
            int original = Integer.parseInt(transaction.getAmount());
            int total = original + fee;
            txtPreAmount.setText(FormatUtil.toDecimalFormat(original, true));
            txtCharge.setText(FormatUtil.toDecimalFormat(fee, true));
            txtAmount.setText(FormatUtil.toDecimalFormat(total, true));
        }

        // 對方的留言, 你的留言, 你的付款請求(有留言的只有付款跟收款）
        //   txtLegend
        if (TransactionType.TRANSFER_TO.code.equals(transaction.getTxType())) {
            if (transaction instanceof SVTransactionOut) {
                txtLegend.setText(R.string.sv_trx_detail_legend_other_reply);
            } else {
                txtLegend.setText(R.string.sv_trx_detail_legend_my_reply);
            }
        } else {
            txtLegend.setText(R.string.sv_trx_detail_legend_my_request);
        }


        // Transaction of outcome
        //  imgPhoto, textName
        if (transaction instanceof SVTransactionOut) {
       //     SVTransactionOut transactionOut = (SVTransactionOut) transaction;
            imageLoader.loadImage(PreferenceUtil.getMemNO(getActivity()), imgPhoto);
            String myName = PreferenceUtil.getNickname(getActivity());
            textName.setText(myName);

        // Transaction of income
        } else {
            SVTransactionIn transactionIn = (SVTransactionIn) transaction;
            imageLoader.loadImage(Integer.toString(transactionIn.getTxMemNO()), imgPhoto);

            textName.setText(transactionIn.getTxMemName());
        }


    }

    private void setListener() {
        btnAction1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onButtonAction1Click();
            }
        });

        btnAction2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onButtonAction2Click();
            }
        });
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        // TODO 待重構作法
            if(menu != null) {
                MenuItem item = menu.findItem(R.id.action_contacts);
                if (item != null) {
                    item.setVisible(false);
                }
            }else {

                super.onPrepareOptionsMenu(menu);
            }
    }

    @Override
    public void onStop() {
        super.onStop();
        SVHttpUtil.cancelQueue(TAG);
    }
}
