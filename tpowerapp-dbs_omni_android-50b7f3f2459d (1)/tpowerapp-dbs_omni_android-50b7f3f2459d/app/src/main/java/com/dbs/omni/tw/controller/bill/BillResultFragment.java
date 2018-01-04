package com.dbs.omni.tw.controller.bill;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.dbs.omni.tw.R;
import com.dbs.omni.tw.controller.ActivityBase;
import com.dbs.omni.tw.util.FormatUtil;
import com.dbs.omni.tw.util.http.mode.bill.BillOverview;

public class BillResultFragment extends Fragment {

    public static final String TAG = "BillResultFragment";
    public static final String ARG_BILL_DATA = "ARG_BILL_DATA";

    private BillOverview mBillOverview;

    private OnEventListener onEventListener;

    public void setOnEventListener(OnEventListener listener) {
        onEventListener = listener;
    }

    public interface OnEventListener {
        void OnHeaderLeftClick();
    }

    public static BillResultFragment newInstance(BillOverview billOverview) {

        Bundle args = new Bundle();
        args.putParcelable(ARG_BILL_DATA, billOverview);

        BillResultFragment fragment = new BillResultFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
        View view = inflater.inflate(R.layout.fragment_bill_result, container, false);

        //Add Header
        RelativeLayout headerView = (RelativeLayout) view.findViewById(R.id.relativeLayout_header);
        headerView.addView(createHeaderView(inflater));

        //Content
        TextView textCheckoutDate = (TextView) view.findViewById(R.id.text_checkout_date);
        TextView textDeadlineDate = (TextView) view.findViewById(R.id.text_deadline);
        TextView textMinAmount = (TextView) view.findViewById(R.id.text_min_amount);
        TextView textCredits = (TextView) view.findViewById(R.id.text_credits);
        TextView textRecyckingRate = (TextView) view.findViewById(R.id.text_recycling_credit_annual_interest_rate);
        TextView textEntryDate = (TextView) view.findViewById(R.id.text_entry_date);

        if(mBillOverview != null) {
            textCheckoutDate.setText(FormatUtil.toDateFormatted(mBillOverview.getStmtCycleDate()));
            textDeadlineDate.setText(FormatUtil.toDateFormatted(mBillOverview.getPaymentDueDate()));
            textMinAmount.setText(FormatUtil.toDecimalFormat(getContext(), mBillOverview.getAmtMinPayment(), true));
            textCredits.setText(FormatUtil.toDecimalFormat(getContext(), mBillOverview.getCreditLine(), true));
            textRecyckingRate.setText(mBillOverview.getCreditRate()+"%");
            textEntryDate.setText(String.format("%1$s %2$s - %3$s", getContext().getString(R.string.entry_date),
                    FormatUtil.toDateFormatted(mBillOverview.getPaymentPeriodStart()),
                    FormatUtil.toDateFormatted(mBillOverview.getPaymentPeriodEnd())));
        }



        return view;
    }

    private View createHeaderView(LayoutInflater inflater) {
        View headerView = inflater.inflate(R.layout.element_bill_result_header, null);

        TextView textPrePeriodPaymentAmount = (TextView) headerView.findViewById(R.id.text_pre_period_payment_amount);
        TextView textPaidAmount = (TextView) headerView.findViewById(R.id.text_paid_amount);
        TextView textAddAmount = (TextView) headerView.findViewById(R.id.text_current_add_amount);
        TextView textAllAmount = (TextView) headerView.findViewById(R.id.text_current_all_amount);

        if(mBillOverview != null) {
            textPrePeriodPaymentAmount.setText(FormatUtil.toDecimalFormat(getContext(), mBillOverview.getAmtPastDue(), true));
            //- NT$ 37,619

            Double amtCurrPayment = 0.0;
            Double amtPayment = 0.0;
            if(!TextUtils.isEmpty(mBillOverview.getAmtCurrPayment())) {
                amtCurrPayment = Double.valueOf(mBillOverview.getAmtCurrPayment());
            }
            if(!TextUtils.isEmpty(mBillOverview.getAmtPayment())) {
                amtPayment = Double.valueOf(mBillOverview.getAmtPayment());
            }
            textPaidAmount.setText(String.format("- %s",
                    FormatUtil.toDecimalFormat(getContext(), amtCurrPayment + amtPayment, true)));
            //"+ NT$ 26,835"
            textAddAmount.setText(String.format("+ %s",
                    FormatUtil.toDecimalFormat(getContext(), mBillOverview.getAmtNewPurchases(), true)));
            //"= NT$ 26,456"
            textAllAmount.setText(String.format("= %s",
                    FormatUtil.toDecimalFormat(getContext(), mBillOverview.getAmtCurrDue(), true)));

        }

        ImageButton leftButton = (ImageButton) headerView.findViewById(R.id.button_left);
        leftButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onEventListener.OnHeaderLeftClick();
            }
        });

        return headerView;
    }


}
