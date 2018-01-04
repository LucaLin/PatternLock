package com.dbs.omni.tw.util.http.mode.bill;

import android.os.Parcel;
import android.os.Parcelable;

import com.dbs.omni.tw.util.http.mode.home.BillListData;

import java.util.ArrayList;

/**
 * Created by siang on 2017/6/20.
 */

public class BillOverview implements Parcelable {

    private String	stmtYM = "";	//帳單月份YYYYMM
    private String	stmtCycleDate = "";	//帳單結帳日 YYYYMMDD
    private String	paymentDueDate = "";//繳款截止日 YYYYMMDD
    private String	creditLine = "0";	//信用額度 (creditLimit)
    private String	paymentPeriodStart = "";	//入帳期間起YYYYMMDD
    private String	paymentPeriodEnd = "";	//入帳期間迄YYYYMMDD
    private String	amtPastDue = "0";	//前期應繳金額
    private String	amtPayment = "0";	//前期繳款金額
    private String	amtNewPurchases = "0";	//本期新增金額 = (前期應繳-前期已繳+本期應繳)
    private String	amtCurrDue = "0";	//本期全部應繳金額
    private String	amtCurrPayment = "0";	//本期累積已繳金額  (amountPastDue)
    private String	amtMinPayment = "0";	//本期最低應繳金額
    private String	domCashAvail = "0";	//預借現金額度  (cashAdvanceLimit)
    private String	creditRate = "0";	//信用年利率 11.33  (annualPercentageRate)
    private String	rPoints = "0";	//Reward points 紅利點數(沒有塞0)
    private String	mPoints = "0";	//Miles points飛行積金(沒有塞0)
    private String	crPoints = "0";	//Cash rebate points 現金點數(沒有塞0)
    private ArrayList<BillListData> paymentTXList = new ArrayList<>();
    private String cycleDueCode = "0";    //繳費逾期代碼(0：未逾期，1：已逾期)

    public BillOverview() {
    }

    protected BillOverview(Parcel in) {
        stmtYM = in.readString();
        stmtCycleDate = in.readString();
        paymentDueDate = in.readString();
        creditLine = in.readString();
        paymentPeriodStart = in.readString();
        paymentPeriodEnd = in.readString();
        amtPastDue = in.readString();
        amtPayment = in.readString();
        amtNewPurchases = in.readString();
        amtCurrDue = in.readString();
        amtCurrPayment = in.readString();
        amtMinPayment = in.readString();
        domCashAvail = in.readString();
        creditRate = in.readString();
        rPoints = in.readString();
        mPoints = in.readString();
        crPoints = in.readString();
        paymentTXList = in.createTypedArrayList(BillListData.CREATOR);
        cycleDueCode = in.readString();
    }

    public static final Creator<BillOverview> CREATOR = new Creator<BillOverview>() {
        @Override
        public BillOverview createFromParcel(Parcel in) {
            return new BillOverview(in);
        }

        @Override
        public BillOverview[] newArray(int size) {
            return new BillOverview[size];
        }
    };

    public String getStmtYM() {
        return stmtYM;
    }

    public void setStmtYM(String stmtYM) {
        this.stmtYM = stmtYM;
    }

    public String getStmtCycleDate() {
        return stmtCycleDate;
    }

    public void setStmtCycleDate(String stmtCycleDate) {
        this.stmtCycleDate = stmtCycleDate;
    }

    public String getPaymentDueDate() {
        return paymentDueDate;
    }

    public void setPaymentDueDate(String paymentDueDate) {
        this.paymentDueDate = paymentDueDate;
    }

    public String getCreditLine() {
        return creditLine;
    }

    public void setCreditLine(String creditLine) {
        this.creditLine = creditLine;
    }

    public String getPaymentPeriodStart() {
        return paymentPeriodStart;
    }

    public void setPaymentPeriodStart(String paymentPeriodStart) {
        this.paymentPeriodStart = paymentPeriodStart;
    }

    public String getPaymentPeriodEnd() {
        return paymentPeriodEnd;
    }

    public void setPaymentPeriodEnd(String paymentPeriodEnd) {
        this.paymentPeriodEnd = paymentPeriodEnd;
    }

    public String getAmtPastDue() {
        return amtPastDue;
    }

    public void setAmtPastDue(String amtPastDue) {
        this.amtPastDue = amtPastDue;
    }

    public String getAmtPayment() {
        return amtPayment;
    }

    public void setAmtPayment(String amtPayment) {
        this.amtPayment = amtPayment;
    }

    public String getAmtNewPurchases() {
        return amtNewPurchases;
    }

    public void setAmtNewPurchases(String amtNewPurchases) {
        this.amtNewPurchases = amtNewPurchases;
    }

    public String getAmtCurrDue() {
        return amtCurrDue;
    }

    public void setAmtCurrDue(String amtCurrDue) {
        this.amtCurrDue = amtCurrDue;
    }

    public String getAmtCurrPayment() {
        return amtCurrPayment;
    }

    public void setAmtCurrPayment(String amtCurrPayment) {
        this.amtCurrPayment = amtCurrPayment;
    }

    public String getAmtMinPayment() {
        return amtMinPayment;
    }

    public double getDoubleAmtMinPayment() {
        return Double.valueOf(amtMinPayment);
    }

    public double getDoubleAmtCurrDue() {
        return Double.valueOf(amtCurrDue);
    }

    public void setAmtMinPayment(String amtMinPayment) {
        this.amtMinPayment = amtMinPayment;
    }

    public String getDomCashAvail() {
        return domCashAvail;
    }

    public void setDomCashAvail(String domCashAvail) {
        this.domCashAvail = domCashAvail;
    }

    public String getCreditRate() {
        return creditRate;
    }

    public void setCreditRate(String creditRate) {
        this.creditRate = creditRate;
    }

    public String getrPoints() {
        return rPoints;
    }

    public void setrPoints(String rPoints) {
        this.rPoints = rPoints;
    }

    public String getmPoints() {
        return mPoints;
    }

    public void setmPoints(String mPoints) {
        this.mPoints = mPoints;
    }

    public String getCrPoints() {
        return crPoints;
    }

    public void setCrPoints(String crPoints) {
        this.crPoints = crPoints;
    }

    public ArrayList<BillListData> getPaymentTXList() {
        return paymentTXList;
    }

    public void setPaymentTXList(ArrayList<BillListData> paymentTXList) {
        this.paymentTXList = paymentTXList;
    }

    public String getCycleDueCode() {
        return cycleDueCode;
    }

    public void setCycleDueCode(String cycleDueCode) {
        this.cycleDueCode = cycleDueCode;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(stmtYM);
        dest.writeString(stmtCycleDate);
        dest.writeString(paymentDueDate);
        dest.writeString(creditLine);
        dest.writeString(paymentPeriodStart);
        dest.writeString(paymentPeriodEnd);
        dest.writeString(amtPastDue);
        dest.writeString(amtPayment);
        dest.writeString(amtNewPurchases);
        dest.writeString(amtCurrDue);
        dest.writeString(amtCurrPayment);
        dest.writeString(amtMinPayment);
        dest.writeString(domCashAvail);
        dest.writeString(creditRate);
        dest.writeString(rPoints);
        dest.writeString(mPoints);
        dest.writeString(crPoints);
        dest.writeTypedList(paymentTXList);
        dest.writeString(cycleDueCode);
    }
}
