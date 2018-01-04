package com.dbs.omni.tw.util.http.mode.payment;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by sherman-thinkpower on 2017/6/23.
 */

public class OtherBankPaymentData implements Parcelable{
    String txSEQ;
    String nID;
    String amt;
    String bankNO;
    String acctNO;
    String feeAMT;

    protected OtherBankPaymentData(Parcel in) {
        txSEQ = in.readString();
        nID = in.readString();
        amt = in.readString();
        bankNO = in.readString();
        acctNO = in.readString();
        feeAMT = in.readString();
    }

    public static final Creator<OtherBankPaymentData> CREATOR = new Creator<OtherBankPaymentData>() {
        @Override
        public OtherBankPaymentData createFromParcel(Parcel in) {
            return new OtherBankPaymentData(in);
        }

        @Override
        public OtherBankPaymentData[] newArray(int size) {
            return new OtherBankPaymentData[size];
        }
    };

    public String getTxSEQ() {
        return txSEQ;
    }

    public void setTxSEQ(String txSEQ) {
        this.txSEQ = txSEQ;
    }

    public String getnID() {
        return nID;
    }

    public void setnID(String nID) {
        this.nID = nID;
    }

    public String getAmt() {
        return amt;
    }

    public void setAmt(String amt) {
        this.amt = amt;
    }

    public String getBankNO() {
        return bankNO;
    }

    public void setBankNO(String bankNO) {
        this.bankNO = bankNO;
    }

    public String getAcctNO() {
        return acctNO;
    }

    public void setAcctNO(String acctNO) {
        this.acctNO = acctNO;
    }

    public String getFeeAMT() {
        return feeAMT;
    }

    public void setFeeAMT(String feeAMT) {
        this.feeAMT = feeAMT;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(txSEQ);
        dest.writeString(nID);
        dest.writeString(amt);
        dest.writeString(bankNO);
        dest.writeString(acctNO);
        dest.writeString(feeAMT);
    }
}
