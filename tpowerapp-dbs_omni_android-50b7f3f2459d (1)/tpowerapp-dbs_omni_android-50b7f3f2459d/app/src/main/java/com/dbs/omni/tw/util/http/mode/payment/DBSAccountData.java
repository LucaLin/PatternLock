package com.dbs.omni.tw.util.http.mode.payment;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by sherman-thinkpower on 2017/6/22.
 */

public class DBSAccountData implements Parcelable{
    private String acctNO;
    private String acctBalance;
    private String acctName;

    protected DBSAccountData(Parcel in) {
        acctNO = in.readString();
        acctBalance = in.readString();
        acctName = in.readString();
    }

    public static final Creator<DBSAccountData> CREATOR = new Creator<DBSAccountData>() {
        @Override
        public DBSAccountData createFromParcel(Parcel in) {
            return new DBSAccountData(in);
        }

        @Override
        public DBSAccountData[] newArray(int size) {
            return new DBSAccountData[size];
        }
    };

    public String getAcctNO() {
        return acctNO;
    }

    public void setAcctNO(String acctNO) {
        this.acctNO = acctNO;
    }

    public String getAcctBalance() {
        return acctBalance;
    }

    public void setAcctBalance(String acctBalance) {
        this.acctBalance = acctBalance;
    }

    public String getAcctName() {
        return acctName;
    }

    public void setAcctName(String acctName) {
        this.acctName = acctName;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(acctNO);
        dest.writeString(acctBalance);
        dest.writeString(acctName);
    }
}
