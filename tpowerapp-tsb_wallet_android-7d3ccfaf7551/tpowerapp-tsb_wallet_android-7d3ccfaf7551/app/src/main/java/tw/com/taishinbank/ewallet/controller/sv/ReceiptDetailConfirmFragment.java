package tw.com.taishinbank.ewallet.controller.sv;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import tw.com.taishinbank.ewallet.R;
import tw.com.taishinbank.ewallet.adapter.sv.ReceiptDetailConfirmAdapter;
import tw.com.taishinbank.ewallet.controller.DetailConfirmFragmentBase;
import tw.com.taishinbank.ewallet.model.LocalContact;
import tw.com.taishinbank.ewallet.model.red.RedEnvelopeData;
import tw.com.taishinbank.ewallet.util.FormatUtil;

public class ReceiptDetailConfirmFragment extends DetailConfirmFragmentBase {
    protected ReceiptDetailConfirmAdapter adapter;

    protected String totalAmount;

    protected static final String ARG_TOTAL_AMOUNT = "ARG_TOTAL_AMOUNT";

    protected DetailConfirmListener listener;


    public static ReceiptDetailConfirmFragment newInstance(String message, String totalAmount, ArrayList<LocalContact> friendList){
        ReceiptDetailConfirmFragment f = new ReceiptDetailConfirmFragment();
        Bundle args = new Bundle();
        args.putString(ARG_MESSAGE, message);
        args.putString(ARG_TOTAL_AMOUNT, totalAmount);
        args.putParcelableArrayList(ARG_FRIEND_LIST, friendList);
        f.setArguments(args);

        return f;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle args = getArguments();
        if(args != null) {
            totalAmount = args.getString(ARG_TOTAL_AMOUNT);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);
        setViewContent();
        return view;
    }

    @Override
    protected void setViewContent() {
        super.setViewContent();
        // 設定顯示的總金額
        if(friendList.size() > 1) {
            textTotalMoney.setText(String.format(getString(R.string.red_envelope_detail_total_money), FormatUtil.toDecimalFormatFromString(totalAmount)));
        }else{
            textTotalMoney.setVisibility(View.GONE);
        }
        // 設定總人數
        textPeopleCount.setText(String.format(getString(R.string.red_envelope_detail_total_people_count), friendList.size()));
        if(friendList.size() == 1) {
            textPeopleCount.setVisibility(View.GONE);
        }
        // 設定列表
        List<RedEnvelopeData> items = getAdapterData();
        adapter = new ReceiptDetailConfirmAdapter(getActivity(), items);
        recyclerView.setAdapter(adapter);
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    protected List<RedEnvelopeData> getAdapterData(){
        List<RedEnvelopeData> items = new ArrayList<>();

        String averageMoney = String.valueOf(Long.parseLong(totalAmount)/friendList.size());

        for(int i = 0; i < friendList.size(); i++){
            // 隨機挑人
            items.add(new RedEnvelopeData(friendList.get(i).getMemNO(), friendList.get(i).getDisplayName(), FormatUtil.toDecimalFormatFromString(averageMoney), null));
        }
        return items;
    }

    public void setListener(DetailConfirmListener listener) {
        this.listener = listener;
    }

    public interface DetailConfirmListener{
        void onNextClicked();
    }

    @Override
    protected void onNextClick() {
        if (listener != null) {
            listener.onNextClicked();
        }
    }
}
