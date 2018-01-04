package com.dbs.omni.tw.util.http.mode.payment;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by sherman-thinkpower on 2017/6/22.
 */

public class DBSPaymentData implements Parcelable {
    String txSEQ;
    String amt;
    String acctNO;

    protected DBSPaymentData(Parcel in) {
        txSEQ = in.readString();
        amt = in.readString();
        acctNO = in.readString();
    }

    public static final Creator<DBSPaymentData> CREATOR = new Creator<DBSPaymentData>() {
        @Override
        public DBSPaymentData createFromParcel(Parcel in) {
            return new DBSPaymentData(in);
        }

        @Override
        public DBSPaymentData[] newArray(int size) {
            return new DBSPaymentData[size];
        }
    };

    public String getTxSEQ() {
        return txSEQ;
    }

    public void setTxSEQ(String txSEQ) {
        this.txSEQ = txSEQ;
    }

    public String getAmt() {
        return amt;
    }

    public void setAmt(String amt) {
        this.amt = amt;
    }

    public String getAcctNO() {
        return acctNO;
    }

    public void setAcctNO(String acctNO) {
        this.acctNO = acctNO;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(txSEQ);
        dest.writeString(amt);
        dest.writeString(acctNO);
    }
}
