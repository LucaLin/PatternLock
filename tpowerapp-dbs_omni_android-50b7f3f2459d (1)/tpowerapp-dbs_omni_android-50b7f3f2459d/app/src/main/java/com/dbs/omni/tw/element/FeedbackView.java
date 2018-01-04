package com.dbs.omni.tw.element;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.dbs.omni.tw.R;
import com.dbs.omni.tw.util.FormatUtil;

/**
 * Created by siang on 2017/4/17.
 */

public class FeedbackView extends LinearLayout {

    private LinearLayout linearLayoutDividend, linearLayoutCash, linearLayoutFlight;

    private TextView textDividend;
    private TextView textCash;
    private TextView textFlight;

    public FeedbackView(Context context) {
        super(context);
        init();
    }

    public FeedbackView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        View view = inflate(getContext(), R.layout.element_home_feedback_info, this);
        linearLayoutDividend = (LinearLayout) view.findViewById(R.id.linearLayout_dividend);
        linearLayoutCash = (LinearLayout) view.findViewById(R.id.linearLayout_cash);
        linearLayoutFlight = (LinearLayout) view.findViewById(R.id.linearLayout_flight);
        textDividend = (TextView) view.findViewById(R.id.text_dividend_points);
        textCash = (TextView) view.findViewById(R.id.text_cash_points);
        textFlight = (TextView) view.findViewById(R.id.text_flight_miles);
    }

    public void setDividendPoints(int points) {
        linearLayoutDividend.setVisibility(VISIBLE);
        textDividend.setText(FormatUtil.toDecimalFormat(getContext(), points));
    }

    public void setCashPoints(int points) {
        linearLayoutCash.setVisibility(VISIBLE);
        textCash.setText(FormatUtil.toDecimalFormat(getContext(), points));
    }

    public void setFlightMiles(int miles) {
        linearLayoutFlight.setVisibility(VISIBLE);
        textFlight.setText(FormatUtil.toDecimalFormat(getContext(), miles));
    }
}
