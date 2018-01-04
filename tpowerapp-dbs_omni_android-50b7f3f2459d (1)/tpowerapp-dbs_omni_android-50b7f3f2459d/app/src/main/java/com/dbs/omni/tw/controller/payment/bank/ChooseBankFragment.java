package com.dbs.omni.tw.controller.payment.bank;


import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SearchView;

import com.dbs.omni.tw.R;
import com.dbs.omni.tw.adapter.PaidBankAdapter;
import com.dbs.omni.tw.controller.ActivityBase;
import com.dbs.omni.tw.model.payment.PaidBankData;
import com.dbs.omni.tw.util.NetworkUtil;
import com.dbs.omni.tw.util.PreferenceUtil;
import com.dbs.omni.tw.util.http.PaymentHttpUtil;
import com.dbs.omni.tw.util.http.listener.ResponseListener;
import com.dbs.omni.tw.util.http.mode.payment.OtherBankData;
import com.dbs.omni.tw.util.http.mode.register.ResponseResult;
import com.dbs.omni.tw.util.http.responsebody.PaymentResponseBodyUtil;

import org.json.JSONException;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 */
public class ChooseBankFragment extends Fragment {

    private ListView bankListView;
    private SearchView SearchView_bank;
    private PaidBankAdapter paidBankAdapter;
    private ArrayList<PaidBankData> paidBankList , paidBankSearchList;
    private ArrayList<OtherBankData> list = new ArrayList<>();
    private View view;

    public interface OnChooseBankListener {
        void onChoose(PaidBankData data);
    }

    public void setOnChooseBankListener(OnChooseBankListener listener ) {
        onChooseBankListener = listener;
    }

    private OnChooseBankListener onChooseBankListener;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ((ActivityBase) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ((ActivityBase) getActivity()).setHeadHide(false);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_choose_bank, container, false);

        bankListView = (ListView) view.findViewById(R.id.bankListView);
        paidBankAdapter = new PaidBankAdapter(getActivity());
        callQueryBankIcon();
//        setBankList(getMockData());

        SearchView_bank = (SearchView)view.findViewById(R.id.SearchView_bank);
        SearchView_bank.setIconifiedByDefault(false);
        SearchView_bank.setSubmitButtonEnabled(false);
        SearchView_bank.setQueryHint(getString(R.string.hint_choose_bank_searchview));
        SearchView_bank.setOnQueryTextListener(searchViewListener);


        return view;
    }

    private void setBankList(ArrayList<PaidBankData> list) {
        //清除listView畫面
        bankListView.setAdapter(null);

        paidBankAdapter.setList(list);
        bankListView.setAdapter(paidBankAdapter);
        bankListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                onAccountClick(position);
            }
        });

        paidBankAdapter.notifyDataSetChanged();
    }

    private ArrayList<PaidBankData> initDisplayList(ArrayList<OtherBankData> list) {
        paidBankList = new ArrayList<>();

        for(OtherBankData data : list) {
            paidBankList.add(new PaidBankData(data.getBankName(), data.getBankNO(), data.getBankIcon()));
        }

        return paidBankList;
    }

    private void onAccountClick(int position){
        PaidBankData data = paidBankList.get(position);
        onChooseBankListener.onChoose(data);
//        Intent intent = new Intent(getActivity(), CreditCardunBilledListActivity.class);
//        intent.putExtra(CreditCardunBilledListActivity.ARG_CARD_DATA, data);
//        startActivity(intent);
    }

    private SearchView.OnQueryTextListener searchViewListener = new SearchView.OnQueryTextListener() {
        @Override
        public boolean onQueryTextSubmit(String query) {
            return true;
        }

        @Override
        public boolean onQueryTextChange(String newText) {
            paidBankSearchList = new ArrayList<>();

            //搜尋內容並加入到搜尋List
            for(int i = 0 ; i < paidBankList.size() ; i++){
                if (paidBankList.get(i).getBankName().contains(newText) || paidBankList.get(i).getBankNo().contains(newText)){
                    paidBankSearchList.add(paidBankList.get(i));
                }
            }

            //若搜尋List不為空 , 則刷新listView
            if(paidBankSearchList != null && paidBankSearchList.size() != 0){
                setBankList(paidBankSearchList);
                return true;
            }
            return false;
        }
    };

    private void callQueryBankIcon(){
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

                PaymentHttpUtil.queryBankIcon(PreferenceUtil.getIsLogin(getContext()), null, responseListener_queryBankIcon, getActivity());
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
    };

    private ResponseListener responseListener_queryBankIcon = new ResponseListener() {
        @Override
        public void onResponse(ResponseResult result) {
            if(getActivity() == null)
                return;

            ((ActivityBase) getActivity()).dismissProgressLoading();
            String returnCode = result.getReturnCode();
            // 如果returnCode是成功
            if (returnCode.equals(ResponseResult.RESULT_SUCCESS)) {
                list = PaymentResponseBodyUtil.getOtherBankList(result.getBody());
                setBankList(initDisplayList(list));
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

    //region Mock
    private ArrayList<PaidBankData> getMockData() {
        paidBankList = new ArrayList<>();

        paidBankList.add(new PaidBankData("王道銀行", "048", null));
        paidBankList.add(new PaidBankData("永豐銀行", "807", null));
        paidBankList.add(new PaidBankData("花旗銀行", "021", null));
        paidBankList.add(new PaidBankData("新竹漁會", "910", null));
        paidBankList.add(new PaidBankData("二水農會", "954", null));
        paidBankList.add(new PaidBankData("山上農會", "964", null));


        return paidBankList;
    }
//endregion

}
