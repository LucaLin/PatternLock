package com.dbs.omni.tw.model.payment;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by siang on 2017/4/21.
 */

public class PaidBankData implements Parcelable {

    private String bankName;
    private String bankNo;
    private String bankIcon;

    protected PaidBankData(Parcel in) {
        bankName = in.readString();
        bankNo = in.readString();
        bankIcon = in.readString();
    }

    public static final Creator<PaidBankData> CREATOR = new Creator<PaidBankData>() {
        @Override
        public PaidBankData createFromParcel(Parcel in) {
            return new PaidBankData(in);
        }

        @Override
        public PaidBankData[] newArray(int size) {
            return new PaidBankData[size];
        }
    };

    public String getBankName() {
        return bankName;
    }

    public void setBankName(String bankName) {
        this.bankName = bankName;
    }

    public String getBankNo() {
        return bankNo;
    }

    public void setBankNo(String bankNo) {
        this.bankNo = bankNo;
    }

    public String getBankIcon() {
        return bankIcon;
    }

    public void setBankIcon(String bankIcon) {
        this.bankIcon = bankIcon;
    }

    public PaidBankData(String bankName, String bankNo, String bankIcon) {
        this.bankName = bankName;
        this.bankNo = bankNo;
        this.bankIcon = bankIcon;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(bankName);
        dest.writeString(bankNo);
        dest.writeString(bankIcon);
    }
}
