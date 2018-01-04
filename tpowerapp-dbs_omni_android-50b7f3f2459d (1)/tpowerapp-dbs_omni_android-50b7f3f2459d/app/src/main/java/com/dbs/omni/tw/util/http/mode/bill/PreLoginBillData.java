package com.dbs.omni.tw.util.http.mode.bill;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by siang on 2017/6/20.
 */

public class PreLoginBillData implements Parcelable {

    private String  nID;
    private String	amtNewPurchases;	//本期新增金額 = (前期應繳-前期已繳+本期應繳)
    private String	amtCurrDue;	//本期全部應繳金額
    private String	amtCurrPayment;	//本期累積已繳金額  (amountPastDue)
    private String	amtMinPayment;	//本期最低應繳金額

    protected PreLoginBillData(Parcel in) {
        nID = in.readString();
        amtNewPurchases = in.readString();
        amtCurrDue = in.readString();
        amtCurrPayment = in.readString();
        amtMinPayment = in.readString();
    }

    public static final Creator<PreLoginBillData> CREATOR = new Creator<PreLoginBillData>() {
        @Override
        public PreLoginBillData createFromParcel(Parcel in) {
            return new PreLoginBillData(in);
        }

        @Override
        public PreLoginBillData[] newArray(int size) {
            return new PreLoginBillData[size];
        }
    };

    public String getnID() {
        return nID;
    }

    public void setnID(String nID) {
        this.nID = nID;
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

    public void setAmtMinPayment(String amtMinPayment) {
        this.amtMinPayment = amtMinPayment;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(nID);
        dest.writeString(amtNewPurchases);
        dest.writeString(amtCurrDue);
        dest.writeString(amtCurrPayment);
        dest.writeString(amtMinPayment);
    }
}
