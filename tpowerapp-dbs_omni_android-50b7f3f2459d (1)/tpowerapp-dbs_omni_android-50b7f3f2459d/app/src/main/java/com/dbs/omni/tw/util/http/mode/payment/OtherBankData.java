package com.dbs.omni.tw.util.http.mode.payment;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by sherman-thinkpower on 2017/7/3.
 */

public class OtherBankData implements Parcelable {
    private String bankNO;
    private String bankName;
    private String appDate;
    private String bankIcon;

    protected OtherBankData(Parcel in) {
        bankNO = in.readString();
        bankName = in.readString();
        appDate = in.readString();
        bankIcon = in.readString();
    }

    public static final Creator<OtherBankData> CREATOR = new Creator<OtherBankData>() {
        @Override
        public OtherBankData createFromParcel(Parcel in) {
            return new OtherBankData(in);
        }

        @Override
        public OtherBankData[] newArray(int size) {
            return new OtherBankData[size];
        }
    };

    public String getBankNO() {
        return bankNO;
    }

    public void setBankNO(String bankNO) {
        this.bankNO = bankNO;
    }

    public String getBankName() {
        return bankName;
    }

    public void setBankName(String bankName) {
        this.bankName = bankName;
    }

    public String getAppDate() {
        return appDate;
    }

    public void setAppDate(String appDate) {
        this.appDate = appDate;
    }

    public String getBankIcon() {
        return bankIcon;
    }

    public void setBankIcon(String bankIcon) {
        this.bankIcon = bankIcon;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(bankNO);
        dest.writeString(bankName);
        dest.writeString(appDate);
        dest.writeString(bankIcon);
    }
}
