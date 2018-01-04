package com.dbs.omni.tw.util.http.mode.payment;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by sherman-thinkpower on 2017/6/23.
 */

public class PreLoginPaymentData implements Parcelable {
    String txSEQ;
    String paymentNO;
    String amt;
    String bankNO;
    String acctNO;
    String feeAMT;

    protected PreLoginPaymentData(Parcel in) {
        txSEQ = in.readString();
        paymentNO = in.readString();
        amt = in.readString();
        bankNO = in.readString();
        acctNO = in.readString();
        feeAMT = in.readString();
    }

    public static final Creator<PreLoginPaymentData> CREATOR = new Creator<PreLoginPaymentData>() {
        @Override
        public PreLoginPaymentData createFromParcel(Parcel in) {
            return new PreLoginPaymentData(in);
        }

        @Override
        public PreLoginPaymentData[] newArray(int size) {
            return new PreLoginPaymentData[size];
        }
    };

    public String getTxSEQ() {
        return txSEQ;
    }

    public void setTxSEQ(String txSEQ) {
        this.txSEQ = txSEQ;
    }

    public String getPaymentNO() {
        return paymentNO;
    }

    public void setPaymentNO(String paymentNO) {
        this.paymentNO = paymentNO;
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
        dest.writeString(paymentNO);
        dest.writeString(amt);
        dest.writeString(bankNO);
        dest.writeString(acctNO);
        dest.writeString(feeAMT);
    }
}
