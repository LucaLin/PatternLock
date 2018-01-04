package com.dbs.omni.tw.controller.payment.bank;


import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.dbs.omni.tw.R;
import com.dbs.omni.tw.adapter.PaidAccountAdapter;
import com.dbs.omni.tw.controller.ActivityBase;
import com.dbs.omni.tw.model.payment.PaidAccountData;
import com.dbs.omni.tw.util.NetworkUtil;
import com.dbs.omni.tw.util.http.PaymentHttpUtil;
import com.dbs.omni.tw.util.http.listener.ResponseListener;
import com.dbs.omni.tw.util.http.mode.payment.DBSAccountData;
import com.dbs.omni.tw.util.http.mode.register.ResponseResult;
import com.dbs.omni.tw.util.http.responsebody.PaymentResponseBodyUtil;

import org.json.JSONException;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 */
public class ChooseAccountFragment extends Fragment {

    private static final String TAG = "ChooseAccountFragment";
    public static final String ARG_DOUBLE_AMOUNT = "ARG_DOUBLE_AMOUNT";

    private ListView accountListView;
    private PaidAccountAdapter paidAccountAdapter;
    private ArrayList<PaidAccountData> paidAccountList;

    private double doubleAmount;
    private View view;
    private ArrayList<DBSAccountData> list = new ArrayList();

    public interface OnChooseAccountListener {
        void onChoose(DBSAccountData data);
    }

    public void setOnChooseAccountListener(OnChooseAccountListener listener ) {
        onChooseAccountListener = listener;
    }

    private OnChooseAccountListener onChooseAccountListener;

    public static ChooseAccountFragment newInstance(double doubleAmount) {

        Bundle args = new Bundle();

        args.putDouble(ARG_DOUBLE_AMOUNT, doubleAmount);

        ChooseAccountFragment fragment = new ChooseAccountFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ((ActivityBase) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ((ActivityBase) getActivity()).setHeadHide(false);

        if(getArguments().containsKey(ARG_DOUBLE_AMOUNT)) {
            doubleAmount = getArguments().getDouble(ARG_DOUBLE_AMOUNT , 0);
        }
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_choose_account, container, false);

        callGetDSBAccount();

        return view;
    }

    @Override
    public void onPause() {
        super.onPause();

        if(getActivity() != null)
            ((ActivityBase) getActivity()).dismissProgressLoading();
    }

    private void setAccountList(View view) {
        accountListView = (ListView) view.findViewById(R.id.accountListView);

        paidAccountAdapter = new PaidAccountAdapter(getActivity());
        accountListView.setAdapter(paidAccountAdapter);
        accountListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                onAccountClick(position);
            }
        });

        paidAccountAdapter.setList(initDisplayList(list));
        paidAccountAdapter.notifyDataSetChanged();
    }

    private void onAccountClick(int position){
        DBSAccountData data = list.get(position);
        onChooseAccountListener.onChoose(data);
//        Intent intent = new Intent(getActivity(), CreditCardunBilledListActivity.class);
//        intent.putExtra(CreditCardunBilledListActivity.ARG_CARD_DATA, data);
//        startActivity(intent);
    }

    private ArrayList<PaidAccountData> initDisplayList(ArrayList<DBSAccountData> list) {
        paidAccountList = new ArrayList<>();

        for(DBSAccountData data : list) {
            Double balance = Double.valueOf(data.getAcctBalance());
            boolean isBalanceNotEnough = false;
            if (balance < doubleAmount){
                isBalanceNotEnough = true;
            }
            paidAccountList.add(new PaidAccountData(data.getAcctName(), data.getAcctNO(), balance, isBalanceNotEnough));
        }

        return paidAccountList;
    }

    private void callGetDSBAccount(){
        // 如果沒有網路連線，顯示提示對話框
        if (!NetworkUtil.isConnected(getActivity())) {
            ((ActivityBase) getActivity()).showAlertDialog(getString(R.string.msg_no_available_network), android.R.string.ok, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            }, true);
        } else {
            try {
                PaymentHttpUtil.getDSBAccount(responseListener_getDSBAccount, getActivity());
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
    };

    private ResponseListener responseListener_getDSBAccount = new ResponseListener() {
        @Override
        public void onResponse(ResponseResult result) {
            if(getActivity() == null)
                return;

            ((ActivityBase) getActivity()).dismissProgressLoading();
            String returnCode = result.getReturnCode();
            // 如果returnCode是成功
            if (returnCode.equals(ResponseResult.RESULT_SUCCESS)) {
                list = PaymentResponseBodyUtil.getDBSAccount(result.getBody());
                setAccountList(view);
            } else {
                // 如果是共同error，不繼續呼叫另一個api
                if (handleCommonError(result, (ActivityBase) getActivity())) {

                    return;
                } else {
                    showAlert((ActivityBase) getActivity(), result.getReturnMessage());
                }
            }
        }
    };

}
