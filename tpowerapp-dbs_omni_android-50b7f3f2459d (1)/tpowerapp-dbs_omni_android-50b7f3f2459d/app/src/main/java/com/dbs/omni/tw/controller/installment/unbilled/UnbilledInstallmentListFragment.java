package com.dbs.omni.tw.controller.installment.unbilled;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;

import com.dbs.omni.tw.R;
import com.dbs.omni.tw.adapter.UnbilledInstallmentAdapter;
import com.dbs.omni.tw.controller.ActivityBase;
import com.dbs.omni.tw.model.home.ConsumptionData;
import com.dbs.omni.tw.model.home.ConsumptionItem;
import com.dbs.omni.tw.typeMapping.ItemType;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 */
public class UnbilledInstallmentListFragment extends Fragment {

    private Button btnNextStep;

    private UnbilledInstallmentListFragment.OnEventListener onEventListener;

    public interface OnEventListener {
        void OnNextEvent();
    }

    public void setOnEventListener (UnbilledInstallmentListFragment.OnEventListener listener) {
        this.onEventListener = listener;
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ((ActivityBase) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ((ActivityBase) getActivity()).setHeadHide(false);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_unbilled_installment_list, container, false);

        ListView listView_unbilled = (ListView)view.findViewById(R.id.listView_unbilled);
        UnbilledInstallmentAdapter unbilledInstallmentAdapter = new UnbilledInstallmentAdapter(getActivity());
        listView_unbilled.setAdapter(unbilledInstallmentAdapter);

        unbilledInstallmentAdapter.setList(getMockData());
        unbilledInstallmentAdapter.notifyDataSetChanged();

        btnNextStep = (Button)view.findViewById(R.id.btnNextStep);
        btnNextStep.setOnClickListener(btnNextStepListener);

        return view;
    }

    private Button.OnClickListener btnNextStepListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            onEventListener.OnNextEvent();
        }
    };

    //region Mock
    private ArrayList<ConsumptionItem> getMockData() {
        ArrayList<ConsumptionItem>  mocklist = new ArrayList<>();

        mocklist.add(new ConsumptionItem(ItemType.CONTENT, new ConsumptionData("長榮加油站", "NT$ 150", "交易日期 01/11 - 入帳日期 01/11",  "")));
        mocklist.add(new ConsumptionItem(ItemType.CONTENT, new ConsumptionData("新加坡教育發展會", "NT$ 150","消費日 01/11 - 入帳日", "SGD$ 3.24")));
        mocklist.add(new ConsumptionItem(ItemType.CONTENT, new ConsumptionData("長榮加油站","NT$ 150", "消費日 01/11 - 入帳日",  "")));

        mocklist.add(new ConsumptionItem(ItemType.CONTENT, new ConsumptionData("長榮加油站", "NT$ 150", "消費日 01/11 - 入帳日",  "")));
        mocklist.add(new ConsumptionItem(ItemType.CONTENT, new ConsumptionData("新加坡教育發展會", "NT$ 150","消費日 01/11 - 入帳日", "SGD$ 3.24")));
        mocklist.add(new ConsumptionItem(ItemType.CONTENT, new ConsumptionData("長榮加油站","NT$ 150", "消費日 01/11 - 入帳日",  "")));
        return mocklist;
    }
//endregion

}
