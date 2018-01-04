package tw.com.taishinbank.ewallet.controller.sv;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

import tw.com.taishinbank.ewallet.R;
import tw.com.taishinbank.ewallet.adapter.sv.AccountListRecyclerAdapter;
import tw.com.taishinbank.ewallet.controller.ActivityBase;
import tw.com.taishinbank.ewallet.interfaces.ResponseListener;
import tw.com.taishinbank.ewallet.model.ResponseResult;
import tw.com.taishinbank.ewallet.model.sv.DesignateAccount;
import tw.com.taishinbank.ewallet.util.FormatUtil;
import tw.com.taishinbank.ewallet.util.NetworkUtil;
import tw.com.taishinbank.ewallet.util.http.HttpUtilBase;
import tw.com.taishinbank.ewallet.util.http.SVHttpUtil;
import tw.com.taishinbank.ewallet.util.responsebody.SVResponseBodyUtil;

public class DesignateAccountFragment extends Fragment {

    private static final String TAG = "DesignateAccountFragment";
    // -- View Hold --
    private TextView title;
    private RecyclerView lstAccounts;

    private LinearLayout lytNoAccount;
    private TextView txtCautionTitle;
    private TextView txtCautionContent;

    private ImageButton btnAddDesignateAccount;
    private Button btnNext;

    // -- Data Model --
    private int fromPage;
    private DesignateAccount bankAccount;

    // -- Sub Controller --
    private AccountListRecyclerAdapter accountListRecyclerAdapter;

    public DesignateAccountFragment() {

    }

    private static final String ARG_FROM_PAGE  = "arg_from_page";

    /**
     * 用來建立Fragment
     */
    public static DesignateAccountFragment newInstance(int fromPage) {
        DesignateAccountFragment f = new DesignateAccountFragment();

        Bundle args = new Bundle();
        args.putInt(ARG_FROM_PAGE, fromPage);
        f.setArguments(args);

        return f;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        if(getArguments() != null) {
            fromPage = getArguments().getInt(ARG_FROM_PAGE);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_sv_bk_account_list, container, false);

        // Set view hold
        setViewHold(view);

        // Set view content, value, listener
        btnNext.setEnabled(false);
        title.setText(R.string.designate_bank_account);

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();

        // 呼叫查詢約定帳戶的API
        callApiGetAccountList();
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
    protected void setViewHold(View parentView) {
        title = (TextView) parentView.findViewById(android.R.id.title);
        TextView text1 = (TextView) parentView.findViewById(android.R.id.text1);
        text1.setVisibility(View.GONE);
        lstAccounts = (RecyclerView) parentView.findViewById(R.id.lst_accounts);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        lstAccounts.setLayoutManager(layoutManager);
        lytNoAccount = (LinearLayout) parentView.findViewById(R.id.lyt_no_account);
        txtCautionTitle = (TextView) parentView.findViewById(R.id.txt_caution_title);
        txtCautionContent = (TextView) parentView.findViewById(R.id.txt_caution_content);
        btnAddDesignateAccount = (ImageButton) parentView.findViewById(R.id.btn_add_designate_account);
        btnNext = (Button) parentView.findViewById(R.id.btn_next);
    }

    protected void setContentByScenario() {
        // **** 無約定帳戶，則需要引導至 新增約定帳戶 ****
        if (bankAccount == null) {
            title.setText(R.string.new_designate_bank_account_title);
            lstAccounts.setVisibility(View.GONE);
            lytNoAccount.setVisibility(View.VISIBLE);
            txtCautionTitle.setVisibility(View.VISIBLE);
            txtCautionContent.setVisibility(View.VISIBLE);
            txtCautionTitle.setText(R.string.sv_caution_title);
            txtCautionContent.setText(R.string.new_designate_bank_account_caution_content);

            btnAddDesignateAccount.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    gotoNewDesignateAccount();
                }
            });
            if (fromPage == DesignateAccountActivity.FROM_PROFILE) {
                btnNext.setVisibility(View.GONE);

            } else if (fromPage == DesignateAccountActivity.FROM_WITHDRAW) {
                btnNext.setVisibility(View.VISIBLE);
                btnNext.setEnabled(false);
                btnNext.setText(R.string.button_next_step);
            }

        // **** 有約定帳戶 ****
        // 若為提領流程而，則引導至原流程。按鈕為－下一步
        // 若為約定帳戶管理，則主扭鈕會刪除現在的帳戶。 按鈕為－變更約定提領帳戶
        } else if (bankAccount.getIsApproved()){
            lstAccounts.setVisibility(View.VISIBLE);
            lytNoAccount.setVisibility(View.GONE);
            btnNext.setVisibility(View.VISIBLE);
            btnNext.setEnabled(true);

            if (fromPage == DesignateAccountActivity.FROM_PROFILE) {
                txtCautionTitle.setVisibility(View.GONE);
                txtCautionContent.setVisibility(View.GONE);
                btnNext.setText(R.string.designate_bank_account_change);
                btnNext.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        changeDesignateAccount();
                    }
                });

            } else if (fromPage == DesignateAccountActivity.FROM_WITHDRAW) {
                txtCautionTitle.setVisibility(View.VISIBLE);
                txtCautionContent.setVisibility(View.VISIBLE);
                title.setText(R.string.designate_bank_account_title);
                txtCautionContent.setText(R.string.new_designate_bank_account_withdraw_content);
                btnNext.setText(R.string.button_next_step);
                btnNext.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        goNext();
                    }
                });
            }

        // **** 約定帳戶送審中 ****
        } else {
            lstAccounts.setVisibility(View.VISIBLE);
            lytNoAccount.setVisibility(View.GONE);
            txtCautionTitle.setVisibility(View.GONE);
            txtCautionContent.setVisibility(View.GONE);
            btnNext.setVisibility(View.VISIBLE);
            btnNext.setEnabled(false);

            if (fromPage ==  DesignateAccountActivity.FROM_PROFILE) {
                btnNext.setText(R.string.designate_bank_account_change);
            } else if (fromPage == DesignateAccountActivity.FROM_WITHDRAW) {
                btnNext.setText(R.string.button_next_step);
            }
        }
    }

    protected void confirmToClearDesignateAccount() {
        bankAccount = null;
        setContentByScenario();
    }

    // ----
    // User interaction
    // ----
    protected void goNext() {
        // Go to next
        //TODO 提領流程下一步
        WithdrawActivity parentActivity = (WithdrawActivity) getActivity();
        parentActivity.setBankAccount(bankAccount);
        parentActivity.gotoAmountInput();
    }

    protected void gotoNewDesignateAccount() {
        Intent intent = new Intent();
        intent.setClass(getActivity(), NewDesignateAccountActivity.class);
        startActivity(intent);
    }

    protected void changeDesignateAccount() {
        String confirm_change_designate_account = getResources().getString(R.string.confirm_change_designate_account);
        String formattedAccount = FormatUtil.toAccountFormat(bankAccount.getAccount());
        confirm_change_designate_account = String.format(confirm_change_designate_account,
                bankAccount.getBankTitle(),formattedAccount);
        TextView txtMessage = new TextView(getContext());
        txtMessage.setText(confirm_change_designate_account);
        txtMessage.setGravity(Gravity.CENTER);

        new AlertDialog.Builder(getActivity())
            .setMessage(confirm_change_designate_account)
//            .setView(txtMessage)
            .setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            })
            .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    // 進入新增約定帳戶流程
                    gotoNewDesignateAccount();
                    dialog.dismiss();
                }
            }).show();
    }

    // ----
    // Getter and Setter
    // ----

    public int getFromPage() {
        return fromPage;
    }

    public void setFromPage(int fromPage) {
        this.fromPage = fromPage;
    }


    // -------------------
    //  Response Listener
    // -------------------

    // 取得約定帳戶的response listener
    private ResponseListener responseListener = new ResponseListener() {

        @Override
        public void onResponse(ResponseResult result) {
            if(getActivity() == null)
                return;

            ActivityBase activityBase = (ActivityBase) getActivity();
            activityBase.dismissProgressLoading();
            String returnCode = result.getReturnCode();
            // 如果returnCode是成功
            if(returnCode.equals(ResponseResult.RESULT_SUCCESS)) {
                // 取得約定帳戶資訊
                DesignateAccount designateAccount = SVResponseBodyUtil.getDesignateAccount(result.getBody());
                setAccountList(designateAccount);
                setContentByScenario();
            // 如果returnCode是尚未綁定帳戶
            }else if(returnCode.equals(ResponseResult.RESULT_NO_DESIGNATE_ACCOUNT)) {
                // 將約定帳戶清除掉
                setAccountList(null);
                setContentByScenario();
            }else{
                // 如果不是共同error
                if(!handleCommonError(result, activityBase)){
                    // TODO 其他不成功的判斷與處理
                    showGetListFailedDialog(result.getReturnMessage());
                }

            }
        }
    };

    /**
     * 顯示不可按旁邊取消的提示對話框，讓使用者選擇重試或回上一頁
     */
    private void showGetListFailedDialog(String message){
        final ActivityBase activityBase = (ActivityBase)getActivity();
        activityBase.showAlertDialog(message, R.string.sv_retry, R.string.sv_back_to_parent_page,
                // 按下重試
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        callApiGetAccountList();
                    }
                    // 按下回上一頁
                }, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        activityBase.finish();
                    }
                }, false);
    }

    /**
     * 呼叫api取得已設定的帳戶列表
     */
    private void callApiGetAccountList(){
        ActivityBase activityBase = ((ActivityBase) getActivity());
        // 如果沒有網路連線，顯示提示對話框
        if(!NetworkUtil.isConnected(getActivity())){
            showGetListFailedDialog(getString(R.string.msg_no_available_network));
            return;
        }

        // 呼叫查詢帳戶列表的API
        try {
            SVHttpUtil.inquiryDesignateAccount(responseListener, activityBase, TAG);
            activityBase.showProgressLoading();
        } catch (JSONException e) {
            e.printStackTrace();
            // TODO
        }
    }


    /**
     * 設定帳戶列表資料並更新顯示列表
     */
    private void setAccountList(DesignateAccount bankAccount){
        this.bankAccount = bankAccount;
        // Set data
        if (bankAccount != null) {
            List<DesignateAccount> bankAccounts = new ArrayList<>();
            bankAccounts.add(bankAccount);

            accountListRecyclerAdapter = new AccountListRecyclerAdapter();
            accountListRecyclerAdapter.setAccounts(bankAccounts);

            lstAccounts.setAdapter(accountListRecyclerAdapter);
            accountListRecyclerAdapter.notifyDataSetChanged();
        }
    }
}
