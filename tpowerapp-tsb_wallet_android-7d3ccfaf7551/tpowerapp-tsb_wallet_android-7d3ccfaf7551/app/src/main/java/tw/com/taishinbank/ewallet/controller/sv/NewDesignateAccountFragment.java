package tw.com.taishinbank.ewallet.controller.sv;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

import tw.com.taishinbank.ewallet.R;
import tw.com.taishinbank.ewallet.controller.ActivityBase;
import tw.com.taishinbank.ewallet.interfaces.GlobalConst;
import tw.com.taishinbank.ewallet.interfaces.ResponseListener;
import tw.com.taishinbank.ewallet.model.ResponseResult;
import tw.com.taishinbank.ewallet.model.sv.Bank;
import tw.com.taishinbank.ewallet.model.sv.BankAccount;
import tw.com.taishinbank.ewallet.util.FormatUtil;
import tw.com.taishinbank.ewallet.util.http.HttpUtilBase;
import tw.com.taishinbank.ewallet.util.http.SVHttpUtil;
import tw.com.taishinbank.ewallet.util.responsebody.SVResponseBodyUtil;

public class NewDesignateAccountFragment extends Fragment {

    private static final String TAG = "NewDesignateAccountFragment";

    // -- View Hold --
    private TextView title;
    private TextView subTitle;

    private EditText txtBank;
    private EditText txtEnterAccount;
    private EditText txtChooseAccount;

    private Button btnNext;

    private AlertDialog dlgBankList;
    private AlertDialog dlgBankAccountList;

    // -- Data Model --
    private ArrayAdapter<Bank> bankArrayAdapter;
    private List<Bank> bankList = new ArrayList<>();
    private Bank selectedBank;

    private ArrayAdapter<BankAccount> bankAccountArrayAdapter;
    private List<BankAccount> bankAccountList = new ArrayList<>();
    private BankAccount selectedBankAccount;

    public NewDesignateAccountFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_sv_new_bk_designate_account, container, false);

        // Prepare data
        prepareBankList();
        prepareTaishinBankAccountList();

        // Set view hold
        setViewHold(view);
        dlgBankList = createBankListDialog();

        // Set view content, value, listener
        setViewListener();
        btnNext.setEnabled(false);
        title.setText(R.string.new_designate_bank_account);
        subTitle.setText(R.string.please_confirm_the_account_owner);

        // 先寫死設定台新銀行為預設選擇
        setBankItemSelected(0);

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        ((ActivityBase)getActivity()).hideKeyboard();
    }

    @Override
    public void onStop() {
        super.onStop();
        HttpUtilBase.cancelQueue(TAG);
        if(getActivity() != null)
            ((ActivityBase) getActivity()).dismissProgressLoading();
    }

    // ---
    // My methods
    // ---
    protected AlertDialog createBankListDialog () {
        AlertDialog dialog = new AlertDialog.Builder(getContext())
            .setAdapter(bankArrayAdapter, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    setBankItemSelected(which);
                    dlgBankList.dismiss();
                }
            })
            .create();
        dialog.setCancelable(true);
        dialog.setCanceledOnTouchOutside(true);

        return dialog;
    }

    protected AlertDialog createBankAccountListDialog () {
        AlertDialog dialog = new AlertDialog.Builder(getContext())
                .setAdapter(bankAccountArrayAdapter, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        onBankAccountItemSelected(which);
                    }
                })
                .create();
        dialog.setCancelable(true);
        dialog.setCanceledOnTouchOutside(true);

        return dialog;
    }

    protected void setViewHold(View parentView) {
        title = (TextView) parentView.findViewById(android.R.id.title);
        subTitle = (TextView) parentView.findViewById(android.R.id.text1);
        txtBank = (EditText) parentView.findViewById(R.id.txt_bank);
        txtEnterAccount = (EditText) parentView.findViewById(R.id.txt_enter_account);
        txtChooseAccount = (EditText) parentView.findViewById(R.id.txt_choose_account);
        btnNext = (Button) parentView.findViewById(R.id.btn_next);
    }

    protected void setViewListener() {
        txtBank.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickOnTxtBank();
            }
        });
        txtChooseAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickOnTxtChooseAccount();
            }
        });
        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickOnNextButton();
            }
        });
    }

    protected void prepareBankList() {
        bankList.add(new Bank(GlobalConst.CODE_TAISHIN_BANK, GlobalConst.NAME_TAISHIN_BANK));
        bankArrayAdapter = new ArrayAdapter<>(getContext(), android.R.layout.select_dialog_singlechoice, bankList);
    }

    protected void prepareTaishinBankAccountList() {
        try {
            SVHttpUtil.inquiryAccountList(responseListener, getActivity(), TAG);
            ((ActivityBase)getActivity()).showProgressLoading();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    // ----
    // Update View
    // ----
    //Call it on initial, select bank
    protected void updateView() {
        if (selectedBank != null) {
            txtBank.setText(selectedBank.toString());
            txtEnterAccount.setText("");
            txtChooseAccount.setText("");

            if (GlobalConst.CODE_TAISHIN_BANK.equals(selectedBank.getCode())) {
                txtEnterAccount.setFocusable(false);
                txtEnterAccount.setClickable(true);

                txtEnterAccount.setVisibility(View.GONE);
                txtChooseAccount.setVisibility(View.VISIBLE);

            } else {
                txtEnterAccount.setFocusable(true);
                txtEnterAccount.setClickable(true);
                txtEnterAccount.setVisibility(View.VISIBLE);
                txtChooseAccount.setVisibility(View.GONE);
            }


        } else {
            txtBank.setText("");
            txtEnterAccount.setText("");
            txtChooseAccount.setText("");
            txtEnterAccount.setFocusable(false);
            txtEnterAccount.setClickable(true);

            txtEnterAccount.setVisibility(View.VISIBLE);
            txtChooseAccount.setVisibility(View.GONE);
        }

        btnNext.setEnabled(false);
    }


    // ----
    // User interaction
    // ----
    protected void clickOnTxtBank() {
        dlgBankList.show();
    }

    protected void setBankItemSelected(int which) {
        selectedBank = bankList.get(which);
        txtEnterAccount.setText("");
        updateView();
    }

    protected void clickOnTxtChooseAccount () {
        //Only Tai-shin Bank will call it
        if(dlgBankAccountList == null){
            dlgBankAccountList = createBankAccountListDialog();
        }
        dlgBankAccountList.show();
    }

    protected void onBankAccountItemSelected(int which) {
        // 取得選中的帳戶
        selectedBankAccount = bankAccountList.get(which);
        // 設定格式化的帳號
        String formattedAccount = FormatUtil.toAccountFormat(selectedBankAccount.getAccount());
        txtChooseAccount.setText(formattedAccount);
        // 讓下一步可以按
        btnNext.setEnabled(true);
        dlgBankAccountList.dismiss();
    }

    protected void clickOnNextButton() {
        NewDesignateAccountActivity parentAct = (NewDesignateAccountActivity) getActivity();
        parentAct.gotoAuth(selectedBankAccount);
    }

    // ----
    // Getter and Setter
    // ----


    // -------------------
    //  Response Listener
    // -------------------

    // 取得帳戶列表的response listener
    private ResponseListener responseListener = new ResponseListener() {

        @Override
        public void onResponse(ResponseResult result) {
            if(getActivity() == null)
                return;

            ActivityBase activityBase = (ActivityBase)getActivity();
            activityBase.dismissProgressLoading();

            String returnCode = result.getReturnCode();
            // 如果returnCode是成功
            if(returnCode.equals(ResponseResult.RESULT_SUCCESS)) {
                // 取得帳戶列表
                bankAccountList.clear();
                bankAccountList = SVResponseBodyUtil.getBankAccountList(result.getBody());
                bankAccountArrayAdapter = new ArrayAdapter<>(getContext(), android.R.layout.select_dialog_singlechoice, bankAccountList);
            }else{
                // 執行預設的錯誤處理 
                handleResponseError(result, (ActivityBase) getActivity());
            }
        }
    };
}
