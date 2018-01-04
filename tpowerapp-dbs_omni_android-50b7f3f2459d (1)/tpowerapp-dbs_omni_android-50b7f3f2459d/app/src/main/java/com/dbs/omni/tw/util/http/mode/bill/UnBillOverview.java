package com.dbs.omni.tw.util.http.mode.bill;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by siang on 2017/6/20.
 */

public class UnBillOverview implements Parcelable {

    private String	unbillStart;			//未出帳起日(billingCycle)
    private String	ccLimit;				//信用額度(totalCreditLimit)
    private String	unbillAmt;				//未出帳金額(retailPurchaseAmountToDate)
    private String	avlBalance;				//available balance可用餘額(totalRemainingLimit)

    protected UnBillOverview(Parcel in) {
        unbillStart = in.readString();
        ccLimit = in.readString();
        unbillAmt = in.readString();
        avlBalance = in.readString();
    }

    public static final Creator<UnBillOverview> CREATOR = new Creator<UnBillOverview>() {
        @Override
        public UnBillOverview createFromParcel(Parcel in) {
            return new UnBillOverview(in);
        }

        @Override
        public UnBillOverview[] newArray(int size) {
            return new UnBillOverview[size];
        }
    };

    public String getUnbillStart() {
        return unbillStart;
    }

    public void setUnbillStart(String unbillStart) {
        this.unbillStart = unbillStart;
    }

    public String getCcLimit() {
        return ccLimit;
    }

    public void setCcLimit(String ccLimit) {
        this.ccLimit = ccLimit;
    }

    public String getUnbillAmt() {
        return unbillAmt;
    }

    public void setUnbillAmt(String unbillAmt) {
        this.unbillAmt = unbillAmt;
    }

    public String getAvlBalance() {
        return avlBalance;
    }

    public void setAvlBalance(String avlBalance) {
        this.avlBalance = avlBalance;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(unbillStart);
        dest.writeString(ccLimit);
        dest.writeString(unbillAmt);
        dest.writeString(avlBalance);
    }
}
