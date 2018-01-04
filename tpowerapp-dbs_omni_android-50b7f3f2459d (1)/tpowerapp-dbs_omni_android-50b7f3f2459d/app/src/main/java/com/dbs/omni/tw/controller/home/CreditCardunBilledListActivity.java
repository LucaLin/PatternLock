package com.dbs.omni.tw.controller.home;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.dbs.omni.tw.R;
import com.dbs.omni.tw.adapter.CreditCardUnBilledRecyclerAdapter;
import com.dbs.omni.tw.controller.ActivityBase;
import com.dbs.omni.tw.model.home.ConsumptionData;
import com.dbs.omni.tw.model.home.ConsumptionItem;
import com.dbs.omni.tw.typeMapping.ItemType;
import com.dbs.omni.tw.util.FormatUtil;
import com.dbs.omni.tw.util.NetworkUtil;
import com.dbs.omni.tw.util.http.BillHttpUtil;
import com.dbs.omni.tw.util.http.listener.ResponseListener;
import com.dbs.omni.tw.util.http.mode.home.BillListData;
import com.dbs.omni.tw.util.http.mode.home.CreditCardData;
import com.dbs.omni.tw.util.http.mode.home.UnBilledDetail;
import com.dbs.omni.tw.util.http.mode.register.ResponseResult;
import com.dbs.omni.tw.util.http.responsebody.HomeResponseBodyUtil;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.Map;
import java.util.TreeMap;

public class CreditCardunBilledListActivity extends ActivityBase {

    public static final String TAG = "CreditCardunBilledListActivity";

    public static final String EXTRA_CARD_DATA = "extra_card_data";

    private CreditCardData data;

    private ArrayList<UnBilledDetail> unBilledDetails;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_credit_card_unbilled_list);
        setCenterTitle(R.string.credit_card_no_yet_bill_detail);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setStatusBarShow(false);

        // 因為要appbar往上滑 導致需要在此頁客制...

        ImageView statusBar = (ImageView) findViewById(R.id.image_status_bar);
        int statusBarHeight = getStatusBarHeight();
        ViewGroup.LayoutParams statusBarParams = statusBar.getLayoutParams();
        statusBarParams.height = statusBarHeight;
        statusBar.setLayoutParams(statusBarParams);

        if(getIntent().hasExtra(EXTRA_CARD_DATA)) {
            data = getIntent().getParcelableExtra(EXTRA_CARD_DATA);
        }



    }



    @Override
    protected void onResume() {
        super.onResume();

        toGetUnBilledDetail();

    }

//region set View

    private void setListView(ArrayList<ConsumptionItem> consumptionList) {
        View header = setListHeader();


        CreditCardUnBilledRecyclerAdapter creditCardUnBilledRecyclerAdapter = new CreditCardUnBilledRecyclerAdapter(consumptionList, this);
//        mAdapter = new MyAdapter(myDataset);
//        mRecyclerView = (RecyclerView) findViewById(R.id.list_view);
        final LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);

        RecyclerView recyclerConsuptions = (RecyclerView) findViewById(R.id.recyclerView_consumptions);
        recyclerConsuptions.setLayoutManager(layoutManager);
        recyclerConsuptions.setAdapter(creditCardUnBilledRecyclerAdapter);
    }

    private View setListHeader() {
        if(data == null) {
            return null;
        }

        View view = findViewById(R.id.header);
        TextView textCardType = (TextView) view.findViewById(R.id.text_type);
        TextView textTitle = (TextView) view.findViewById(R.id.text_title);

        if(data.getCcFlag().equalsIgnoreCase("M")) {
            textCardType.setVisibility(View.VISIBLE);
            textCardType.setText(R.string.card_type_main);
        } else if(data.getCcFlag().equalsIgnoreCase("S")) {
            textCardType.setVisibility(View.VISIBLE);
            textCardType.setText(R.string.card_type_attached);
        } else {
            textCardType.setVisibility(View.GONE);
        }

        String titleString = String.format("%1$s %2$s", data.getCardName(), FormatUtil.toHideCardNumberShortString(data.getCcNO(), true));
        textTitle.setText(titleString);

        return view;
    }
//endregion

    //region api
    private void toGetUnBilledDetail() {
        // 如果沒有網路連線，顯示提示對話框
        if (!NetworkUtil.isConnected(this)) {
            showAlertDialog(getString(R.string.msg_no_available_network), android.R.string.ok, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            }, true);
        } else {
            try {
                BillHttpUtil.getUnBilledDetail(data.getCcID() ,responseListener_getUnBilledDetail, this);
                showProgressLoading();
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
    }

    private ResponseListener responseListener_getUnBilledDetail = new ResponseListener() {

        @Override
        public void onResponse(ResponseResult result) {

            dismissProgressLoading();

            String returnCode = result.getReturnCode();
            // 如果returnCode是成功
            if (returnCode.equals(ResponseResult.RESULT_SUCCESS)) {
                unBilledDetails = HomeResponseBodyUtil.getUnbillTXDetails(result.getBody());
//                setCardList(creditCardList);

                setListView(mappingToShowData(unBilledDetails.get(0).getUnbillTXList()));
            } else {
                handleResponseError(result, CreditCardunBilledListActivity.this);
            }

        }
    };

    private ArrayList<ConsumptionItem> mappingToShowData(ArrayList<BillListData> billList) {
        ArrayList<ConsumptionItem>  list = new ArrayList<>();
        Map<String, ArrayList<ConsumptionData>> consumptionsMap = new TreeMap<>();
        for(BillListData data :billList) {
            String titleString = FormatUtil.toDateHeaderFormatted(data.getTxDate().substring(0,6));
            ArrayList<ConsumptionData> consumptionDatas = consumptionsMap.get(titleString);
            if(consumptionDatas == null) {
                consumptionDatas = new ArrayList<>();
            }

            ConsumptionData consumptionData = new ConsumptionData(  data.getTxDesc(),
                    FormatUtil.toConsumptionDecimalFormat(this, data.getTxAmt()),
                    FormatUtil.toConsumptionData(this, data.getTxDate(), data.getPostDate()),
                    FormatUtil.toConsumptionDecimalFormat(this, data.getOrglAmt(), data.getOrglCry()));

            consumptionDatas.add(consumptionData);
            consumptionsMap.put(titleString, consumptionDatas);
        }


        for (String key: consumptionsMap.keySet()) {
            list.add(new ConsumptionItem(ItemType.TITLE, key));
            for (ConsumptionData content : consumptionsMap.get(key)) {
                list.add(new ConsumptionItem(ItemType.CONTENT, content));
            }

        }



        return list;
    }




//endregion


//region Mock
//    private ArrayList<ConsumptionItem> getMockData() {
//        ArrayList<ConsumptionItem>  mocklist = new ArrayList<>();
//
//        mocklist.add(new ConsumptionItem(ItemType.TITLE, "2017年1月"));
//        mocklist.add(new ConsumptionItem(ItemType.CONTENT, new ConsumptionData("長榮加油站", "NT$ 150", "交易日期 01/11 - 入帳日期 01/11",  "")));
//        mocklist.add(new ConsumptionItem(ItemType.CONTENT, new ConsumptionData("新加坡教育發展會", "NT$ 150","消費日 01/11 - 入帳日", "SGD$ 3.24")));
//        mocklist.add(new ConsumptionItem(ItemType.CONTENT, new ConsumptionData("長榮加油站","NT$ 150", "消費日 01/11 - 入帳日",  "")));
//
//        mocklist.add(new ConsumptionItem(ItemType.TITLE, "2017年2月"));
//        mocklist.add(new ConsumptionItem(ItemType.CONTENT, new ConsumptionData("長榮加油站", "NT$ 150", "消費日 01/11 - 入帳日",  "")));
//        mocklist.add(new ConsumptionItem(ItemType.CONTENT, new ConsumptionData("新加坡教育發展會", "NT$ 150","消費日 01/11 - 入帳日", "SGD$ 3.24")));
//        mocklist.add(new ConsumptionItem(ItemType.CONTENT, new ConsumptionData("長榮加油站","NT$ 150", "消費日 01/11 - 入帳日",  "")));
//        mocklist.add(new ConsumptionItem(ItemType.CONTENT, new ConsumptionData("長榮加油站", "NT$ 150", "消費日 01/11 - 入帳日",  "")));
//        mocklist.add(new ConsumptionItem(ItemType.CONTENT, new ConsumptionData("新加坡教育發展會", "NT$ 150","消費日 01/11 - 入帳日", "SGD$ 3.24")));
//        mocklist.add(new ConsumptionItem(ItemType.CONTENT, new ConsumptionData("長榮加油站","NT$ 150", "消費日 01/11 - 入帳日",  "")));
//        mocklist.add(new ConsumptionItem(ItemType.CONTENT, new ConsumptionData("長榮加油站", "NT$ 150", "消費日 01/11 - 入帳日",  "")));
//        mocklist.add(new ConsumptionItem(ItemType.CONTENT, new ConsumptionData("新加坡教育發展會", "NT$ 150","消費日 01/11 - 入帳日", "SGD$ 3.24")));
//        mocklist.add(new ConsumptionItem(ItemType.CONTENT, new ConsumptionData("長榮加油站","NT$ 150", "消費日 01/11 - 入帳日",  "")));
//        mocklist.add(new ConsumptionItem(ItemType.CONTENT, new ConsumptionData("長榮加油站", "NT$ 150", "消費日 01/11 - 入帳日",  "")));
//        mocklist.add(new ConsumptionItem(ItemType.CONTENT, new ConsumptionData("新加坡教育發展會", "NT$ 150","消費日 01/11 - 入帳日", "SGD$ 3.24")));
//        mocklist.add(new ConsumptionItem(ItemType.CONTENT, new ConsumptionData("長榮加油站","NT$ 150", "消費日 01/11 - 入帳日",  "")));
//        return mocklist;
//    }
//endregion


}
