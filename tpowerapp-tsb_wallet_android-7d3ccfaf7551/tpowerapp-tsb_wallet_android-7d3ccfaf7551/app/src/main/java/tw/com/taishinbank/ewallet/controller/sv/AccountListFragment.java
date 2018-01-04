package tw.com.taishinbank.ewallet.controller.sv;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

import tw.com.taishinbank.ewallet.R;
import tw.com.taishinbank.ewallet.adapter.sv.BankAccountListRecyclerAdapter;
import tw.com.taishinbank.ewallet.controller.ActivityBase;
import tw.com.taishinbank.ewallet.interfaces.ResponseListener;
import tw.com.taishinbank.ewallet.model.ResponseResult;
import tw.com.taishinbank.ewallet.model.sv.BankAccount;
import tw.com.taishinbank.ewallet.util.NetworkUtil;
import tw.com.taishinbank.ewallet.util.http.HttpUtilBase;
import tw.com.taishinbank.ewallet.util.http.SVHttpUtil;
import tw.com.taishinbank.ewallet.util.responsebody.SVResponseBodyUtil;

public class AccountListFragment extends Fragment {

    private static final String TAG = "AccountListFragment";
    private Button btnNext;
    // -- View Hold --
    private TextView title;
    private RecyclerView lstAccounts;

    // -- Data Model --
    private List<BankAccount> bankAccounts = new ArrayList<>();

    // -- Sub Controller --
    private BankAccountListRecyclerAdapter accountListRecyclerAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_sv_bk_account_list, container, false);

        View layoutNoAccountAndCaution = view.findViewById(R.id.lyt_no_account_and_caution);
        layoutNoAccountAndCaution.setVisibility(View.GONE);

        lstAccounts = (RecyclerView) view.findViewById(R.id.lst_accounts);
        // 設定layout manager
        LinearLayoutManager lstViewAccountLayoutManager = new LinearLayoutManager(getActivity());
        lstAccounts.setLayoutManager(lstViewAccountLayoutManager);

        btnNext = (Button) view.findViewById(R.id.btn_next);
        // Set view content, value, listener
        btnNext.setEnabled(false);
        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int checkedPosition = accountListRecyclerAdapter.getCheckedPosition();
                // 如果選中的是最後一個項目，為其他銀行
                if (checkedPosition == accountListRecyclerAdapter.getItemCount() - 1) {
                    // TODO 其他不成功的判斷與處理
                    ((ActivityBase) getActivity()).showAlertDialog(getString(R.string.sv_msg_other_bank_not_support),
                            android.R.string.ok,
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            }, true);

                // 如果是台新銀行的帳戶
                } else {
                    DepositActivity depositActivity = (DepositActivity) getActivity();
                    depositActivity.setBankAccount(accountListRecyclerAdapter.getItem(checkedPosition));
                    depositActivity.gotoAmountInput();
                }
            }
        });
        title = (TextView) view.findViewById(android.R.id.title);
        title.setText(R.string.sv_title_select_deposit_account);
        TextView text1 = (TextView) view.findViewById(android.R.id.text1);
        text1.setVisibility(View.GONE);

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();

        // 呼叫api取得已設定的帳戶列表
        callApiGetAccountList();
    }

    @Override
    public void onStop() {
        super.onStop();
        HttpUtilBase.cancelQueue(TAG);
        if(getActivity() != null)
            ((ActivityBase) getActivity()).dismissProgressLoading();
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
            SVHttpUtil.inquiryAccountList(responseListener, activityBase, TAG);
            activityBase.showProgressLoading();
        } catch (JSONException e) {
            e.printStackTrace();
            // TODO
        }
    }

    /**
     * 設定帳戶列表資料並更新顯示列表
     */
    private void setAccountList(List<BankAccount> bankAccounts){
        this.bankAccounts = bankAccounts;
        accountListRecyclerAdapter = new BankAccountListRecyclerAdapter();
        accountListRecyclerAdapter.setOtherTitle(getString(R.string.sv_other_bank));
        accountListRecyclerAdapter.setOnSelectedItemsChangedListener(new BankAccountListRecyclerAdapter.OnSelectedItemsChangedListener() {
            @Override
            public void OnSelectedItemsChanged(int checkedPosition) {
                btnNext.setEnabled(checkedPosition != BankAccountListRecyclerAdapter.NO_CHECKED_ITEM);
            }
        });
        if(bankAccounts != null) {
            accountListRecyclerAdapter.setAccounts(bankAccounts);
        }
        lstAccounts.setAdapter(accountListRecyclerAdapter);
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

            ActivityBase activityBase = (ActivityBase)getActivity();
            activityBase.dismissProgressLoading();
            String returnCode = result.getReturnCode();
            // 如果returnCode是成功
            if(returnCode.equals(ResponseResult.RESULT_SUCCESS)) {
                // 取得約定帳戶資訊
                List<BankAccount> bankAccountList = SVResponseBodyUtil.getBankAccountList(result.getBody());
                setAccountList(bankAccountList);
            }else{
                // 如果不是共同error
                if(!handleCommonError(result, activityBase)){
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
}
