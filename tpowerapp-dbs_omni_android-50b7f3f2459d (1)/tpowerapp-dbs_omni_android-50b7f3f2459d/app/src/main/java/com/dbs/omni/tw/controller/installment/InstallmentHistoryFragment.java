package com.dbs.omni.tw.controller.installment;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.dbs.omni.tw.R;
import com.dbs.omni.tw.adapter.CreditCardUnBilledItemAdapter;
import com.dbs.omni.tw.model.home.ConsumptionData;
import com.dbs.omni.tw.model.home.ConsumptionItem;
import com.dbs.omni.tw.typeMapping.ItemType;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 */
public class InstallmentHistoryFragment extends Fragment {

    private ListView list_installment_history;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_installment_history, container, false);

        list_installment_history = (ListView)view.findViewById(R.id.list_installment_history);

        CreditCardUnBilledItemAdapter creditCardUnBilledItemAdapter = new CreditCardUnBilledItemAdapter(getActivity());
        list_installment_history.setAdapter(creditCardUnBilledItemAdapter);

        creditCardUnBilledItemAdapter.setList(getMockData());
        return view;
    }


    //region Mock
    private ArrayList<ConsumptionItem> getMockData() {
        ArrayList<ConsumptionItem>  mocklist = new ArrayList<>();

        mocklist.add(new ConsumptionItem(ItemType.TITLE, "2017年1月"));
        mocklist.add(new ConsumptionItem(ItemType.CONTENT, new ConsumptionData("網路 - 分期數 3", "NT$ 1,750", "2017/01/11",  "成功")));
        mocklist.add(new ConsumptionItem(ItemType.CONTENT, new ConsumptionData("網路 - 分期數 3", "NT$ 3,750", "2017/01/11",  "成功")));
        mocklist.add(new ConsumptionItem(ItemType.CONTENT, new ConsumptionData("網路 - 分期數 6", "NT$ 6,750", "2017/01/11",  "失敗")));

        mocklist.add(new ConsumptionItem(ItemType.TITLE, "2017年2月"));
        mocklist.add(new ConsumptionItem(ItemType.CONTENT, new ConsumptionData("網路 - 分期數 12", "NT$ 2,750", "2017/02/11",  "成功")));
        mocklist.add(new ConsumptionItem(ItemType.CONTENT, new ConsumptionData("網路 - 分期數 3", "NT$ 5,750", "2017/02/11",  "成功")));
        mocklist.add(new ConsumptionItem(ItemType.CONTENT, new ConsumptionData("網路 - 分期數 24", "NT$ 6,750", "2017/02/11",  "失敗")));
        return mocklist;
    }
//endregion
}
