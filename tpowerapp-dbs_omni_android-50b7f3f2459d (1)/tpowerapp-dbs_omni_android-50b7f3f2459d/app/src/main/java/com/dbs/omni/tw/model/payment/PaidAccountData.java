package com.dbs.omni.tw.model.payment;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by siang on 2017/4/21.
 */

public class PaidAccountData implements Parcelable {

    private String accountName;
    private String accountNumber;
    private double balance;
    private boolean isBalanceNotEnough;

    public PaidAccountData(String accountName, String accountNumber, double balance, boolean isBalanceNotEnough) {
        this.accountName = accountName;
        this.accountNumber = accountNumber;
        this.balance = balance;
        this.isBalanceNotEnough = isBalanceNotEnough;
    }

    protected PaidAccountData(Parcel in) {
        accountName = in.readString();
        accountNumber = in.readString();
        balance = in.readDouble();
        isBalanceNotEnough = in.readByte() != 0;
    }

    public static final Creator<PaidAccountData> CREATOR = new Creator<PaidAccountData>() {
        @Override
        public PaidAccountData createFromParcel(Parcel in) {
            return new PaidAccountData(in);
        }

        @Override
        public PaidAccountData[] newArray(int size) {
            return new PaidAccountData[size];
        }
    };

    public String getAccountName() {
        return accountName;
    }

    public void setAccountName(String accountName) {
        this.accountName = accountName;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }

    public double getBalance() {
        return balance;
    }

    public void setBalance(double balance) {
        this.balance = balance;
    }

    public boolean isBalanceNotEnough() {
        return isBalanceNotEnough;
    }

    public void setBalanceNotEnough(boolean balanceNotEnough) {
        isBalanceNotEnough = balanceNotEnough;
    }

    public boolean getBalanceNotEnough() {
        return isBalanceNotEnough;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(accountName);
        dest.writeString(accountNumber);
        dest.writeDouble(balance);
        dest.writeByte((byte) (isBalanceNotEnough ? 1 : 0));
    }
}
