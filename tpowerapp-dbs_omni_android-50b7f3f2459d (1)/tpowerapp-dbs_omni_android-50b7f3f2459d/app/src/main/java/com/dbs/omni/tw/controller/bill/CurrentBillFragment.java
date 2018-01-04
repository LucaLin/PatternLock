package com.dbs.omni.tw.controller.bill;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.dbs.omni.tw.R;
import com.dbs.omni.tw.adapter.CreditCardUnBilledItemAdapter;
import com.dbs.omni.tw.adapter.FilterAdapter;
import com.dbs.omni.tw.controller.ActivityBase;
import com.dbs.omni.tw.controller.MainActivity;
import com.dbs.omni.tw.model.element.FilterItemData;
import com.dbs.omni.tw.model.home.ConsumptionData;
import com.dbs.omni.tw.model.home.ConsumptionItem;
import com.dbs.omni.tw.typeMapping.ItemType;
import com.dbs.omni.tw.util.FormatUtil;
import com.dbs.omni.tw.util.NetworkUtil;
import com.dbs.omni.tw.util.http.BillHttpUtil;
import com.dbs.omni.tw.util.http.listener.ResponseListener;
import com.dbs.omni.tw.util.http.mode.bill.BillOverview;
import com.dbs.omni.tw.util.http.mode.home.BillListData;
import com.dbs.omni.tw.util.http.mode.bill.BilledDetail;
import com.dbs.omni.tw.util.http.mode.bill.BilledDetailList;
import com.dbs.omni.tw.util.http.mode.register.ResponseResult;
import com.dbs.omni.tw.util.http.responsebody.BillResponseBodyUtil;

import org.json.JSONException;

import java.util.ArrayList;

public class CurrentBillFragment extends Fragment {

    public static final String TAG = "CurrentBillFragment";
    public static final String ARG_BILL_DATA = "ARG_BILL_DATA";

    private LayoutInflater mInflater;
    private View mView;
    private TextView buttonFilter;
    private ArrayList<FilterItemData> mFilterItemDataList;

    private RelativeLayout mRelativeLayoutFilter;

    private int mSelectedIndex = 0;
    private BillOverview mBillOverview;
    private BilledDetailList mBilledDetailList;

    private OnEventListener onEventListener;

    public void setOnEventListener(OnEventListener listener) {
        onEventListener = listener;
    }

    public interface OnEventListener {
        void OnHeaderRightClick();
    }

    public static CurrentBillFragment newInstance(BillOverview billOverview) {

        Bundle args = new Bundle();

        args.putParcelable(ARG_BILL_DATA, billOverview);

        CurrentBillFragment fragment = new CurrentBillFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        ((ActivityBase) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        ((ActivityBase) getActivity()).setHeadHide(false);

        if(getArguments() != null && getArguments().containsKey(ARG_BILL_DATA)) {
            mBillOverview = getArguments().getParcelable(ARG_BILL_DATA);
        }


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mInflater = inflater;
        mView = inflater.inflate(R.layout.fragment_current_bill, container, false);


        if(mBilledDetailList != null) {
            setView(mBilledDetailList);
        } else {
            toGetCurrentBilledDetail();
        }

        return mView;
    }

    @Override
    public void onResume() {
        super.onResume();

    }

    @Override
    public void onPause() {
        if(buttonFilter != null)
            buttonFilter.setVisibility(View.GONE);

        if(getActivity() != null)
            ((ActivityBase) getActivity()).dismissProgressLoading();
        super.onPause();
    }

    private void setView(BilledDetailList billedDetailList) {
        ListView listBill = (ListView) mView.findViewById(R.id.list_bill);
        listBill.addHeaderView(createHeaderView(mInflater));


        CreditCardUnBilledItemAdapter creditCardUnBilledItemAdapter = new CreditCardUnBilledItemAdapter(getContext());
        listBill.setAdapter(creditCardUnBilledItemAdapter);

        creditCardUnBilledItemAdapter.setList(mappingToShowData(billedDetailList));
        creditCardUnBilledItemAdapter.notifyDataSetChanged();

//        setFilterView(mInflater, mView);
    }

    private View createHeaderView(LayoutInflater inflater) {
        View headerView = inflater.inflate(R.layout.element_current_bill_header, null);

        TextView textAllAmount = (TextView) headerView.findViewById(R.id.text_current_all_amount);
        TextView textDeadlineDate = (TextView) headerView.findViewById(R.id.text_deadline);

        if(mBillOverview != null) {
            textAllAmount.setText(FormatUtil.toDecimalFormat(getContext(), mBillOverview.getAmtCurrDue(), true));

            String deadline = String.format("%1$s %2$s", getString(R.string.deadline_of_payment), FormatUtil.toDateFormatted(mBillOverview.getPaymentDueDate()));
            textDeadlineDate.setText(deadline);
        }

        TextView textImmediatelPay = (TextView) headerView.findViewById(R.id.text_payment);
        textImmediatelPay.setPaintFlags(textImmediatelPay.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        textImmediatelPay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToHome(MainActivity.TAB_PAYMENT);
            }
        });

        ImageButton rightButton = (ImageButton) headerView.findViewById(R.id.button_left);
        rightButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onEventListener.OnHeaderRightClick();
            }
        });

        return headerView;
    }


    /**
     * 開啟首頁
     */
    private void goToHome(String page){
        Intent intent = new Intent(getContext(), MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        if(page != null) {
            intent.putExtra(MainActivity.EXTRA_GO_PAGE_TAG, page);
        }
        startActivity(intent);
    }



//region Mock
//    private ArrayList<ConsumptionItem> getMockData() {
//        ArrayList<ConsumptionItem>  mocklist = new ArrayList<>();
//
////        mocklist.add(new ConsumptionItem(ItemType.TITLE, "2017年1月"));
//        mocklist.add(new ConsumptionItem(ItemType.CONTENT, new ConsumptionData("長榮加油站", "NT$ 150", "交易日期 01/11 - 入帳日期 01/11",  "")));
//        mocklist.add(new ConsumptionItem(ItemType.CONTENT, new ConsumptionData("新加坡教育發展會", "NT$ 150","消費日 01/11 - 入帳日", "SGD$ 3.24")));
//        mocklist.add(new ConsumptionItem(ItemType.CONTENT, new ConsumptionData("長榮加油站","NT$ 150", "消費日 01/11 - 入帳日",  "")));
//
//        mocklist.add(new ConsumptionItem(ItemType.TITLE, "∙ VISA 豐盛御璽卡  ••••-7439"));
//        mocklist.add(new ConsumptionItem(ItemType.CONTENT, new ConsumptionData("長榮加油站", "NT$ 150", "消費日 01/11 - 入帳日",  "")));
//        mocklist.add(new ConsumptionItem(ItemType.CONTENT, new ConsumptionData("新加坡教育發展會", "NT$ 150","消費日 01/11 - 入帳日", "SGD$ 3.24")));
//        mocklist.add(new ConsumptionItem(ItemType.CONTENT, new ConsumptionData("長榮加油站","NT$ 150", "消費日 01/11 - 入帳日",  "")));
//        return mocklist;
//    }
//    private ArrayList<FilterItemData> getFilterMock(){
//             = new ArrayList<>();
//
//        filterArray.add(new FilterItemData("本期帳單消費明細", true));
//        filterArray.add(new FilterItemData("VISA 豐盛御璽卡  ••••-7439", false));
//        filterArray.add(new FilterItemData("VISA 飛行卡明細  ••••-8088", false));
//
//        return filterArray;
//    }
//endregion

//region Filter View
    private void setFilterView(LayoutInflater inflater, View view) {
        mRelativeLayoutFilter = (RelativeLayout) view.findViewById(R.id.relativeLayout_filter);
        mRelativeLayoutFilter.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                mRelativeLayoutFilter.setVisibility(View.GONE);
                return true;
            }
        });

        setFilterView(inflater, mRelativeLayoutFilter);
        setActionRightButton();
    }

    private void setActionRightButton() {
        Toolbar toolbar = (Toolbar) getActivity().findViewById(R.id.toolbar);
        if (toolbar != null) {
            buttonFilter = (TextView) toolbar.findViewById(R.id.custom_button_right);
            buttonFilter.setVisibility(View.VISIBLE);
            buttonFilter.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_filter_unselected, 0);
            buttonFilter.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mRelativeLayoutFilter.setVisibility(View.VISIBLE);
//
//                    Bitmap bitmap = BlurUtil.getBitmapFromView(rootView);
//                    bitmap = blurBitmap(bitmap, 20, getContext());
////                    RenderScript rs = RenderScript.create(getActivity());
////                    BlurUtil.RSBlurProcessor rsb = new BlurUtil.RSBlurProcessor(rs);
////                    bitmap = rsb.blur(bitmap, 100, 100);
//                    BitmapDrawable ob = new BitmapDrawable(getResources(), bitmap);
//                    mRelativeLayoutFilter.setBackground(ob);

                }
            });
        }
    }

    private void setFilterView(LayoutInflater inflater, RelativeLayout view) {
        View mFilterView = inflater.inflate(R.layout.element_filter, null);

        ListView listFilters = (ListView) mFilterView.findViewById(R.id.list_filters);
        final FilterAdapter filterAdapter = new FilterAdapter(getContext());
        listFilters.setAdapter(filterAdapter);

        listFilters.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                FilterItemData olderSelectData = mFilterItemDataList.get(mSelectedIndex);
                olderSelectData.setSelect(false);

                mSelectedIndex = position;
                FilterItemData data = mFilterItemDataList.get(mSelectedIndex);
                data.setSelect(true);
                filterAdapter.notifyDataSetChanged();
                mRelativeLayoutFilter.setVisibility(View.GONE);
            }
        });



//        mFilterItemDataList = getFilterMock();
        if(mFilterItemDataList != null) {
            filterAdapter.setList(mFilterItemDataList);
            filterAdapter.notifyDataSetChanged();
            view.addView(mFilterView);
            view.setVisibility(View.GONE);
        }
    }


//endregion

//region api
    private void toGetCurrentBilledDetail() {
        // 如果沒有網路連線，顯示提示對話框
        if (!NetworkUtil.isConnected(getContext())) {
            ((ActivityBase)getActivity()).showAlertDialog(getString(R.string.msg_no_available_network), android.R.string.ok, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            }, true);
        } else {
            try {
                BillHttpUtil.getCurrentBilledDetail(responseListener, getActivity());
                ((ActivityBase)getActivity()).showProgressLoading();
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
    }

    private ResponseListener responseListener = new ResponseListener() {

        @Override
        public void onResponse(ResponseResult result) {
            if(getActivity() == null)
                return;

            ((ActivityBase)getActivity()).dismissProgressLoading();

            String returnCode = result.getReturnCode();
            // 如果returnCode是成功
            if (returnCode.equals(ResponseResult.RESULT_SUCCESS)) {
                mBilledDetailList = BillResponseBodyUtil.getBilledDetailList(result.getBody());
                setView(mBilledDetailList);
            } else {
                handleResponseError(result, ((ActivityBase)getActivity()));
            }

        }
    };

    private ArrayList<ConsumptionItem> mappingToShowData(BilledDetailList billedDetailList) {
        ArrayList<ConsumptionItem>  list = new ArrayList<>();
        mFilterItemDataList = new ArrayList<>(); //塞選項目
        mFilterItemDataList.add(new FilterItemData("本期帳單消費明細", true));

        //by mBillOverview
        for (BillListData billData: mBillOverview.getPaymentTXList()) {
            ConsumptionData consumptionData = new ConsumptionData(  billData.getTxDesc(),
                    FormatUtil.toConsumptionDecimalFormat(getContext(), billData.getTxAmt()),
                    FormatUtil.toConsumptionData(getContext(), billData.getTxDate(), billData.getPostDate()),
                    "");
            list.add(new ConsumptionItem(ItemType.CONTENT, consumptionData));
        }

        //by card detail
        for (BilledDetail detail : billedDetailList.getCardList()) {

            mFilterItemDataList.add(new FilterItemData(String.format("%1$s %2$s", detail.getCardName(), FormatUtil.toHideCardNumberShortString(detail.getCcNO())), false));

            list.add(new ConsumptionItem(ItemType.TITLE, String.format("∙ %1$s %2$s", detail.getCardName(), FormatUtil.toHideCardNumberShortString(detail.getCcNO()))));
            for (BillListData billData : detail.getCurrentTXList()) {
                ConsumptionData consumptionData = new ConsumptionData(  billData.getTxDesc(),
                        FormatUtil.toConsumptionDecimalFormat(getContext(), billData.getTxAmt()),
                        FormatUtil.toConsumptionData(getContext(), billData.getTxDate(), billData.getPostDate()),
                        FormatUtil.toConsumptionDecimalFormat(getContext(), billData.getOrglAmt(), billData.getOrglCry()));
                list.add(new ConsumptionItem(ItemType.CONTENT, consumptionData));

            }
        }

        return list;
    }
//endregion

}
