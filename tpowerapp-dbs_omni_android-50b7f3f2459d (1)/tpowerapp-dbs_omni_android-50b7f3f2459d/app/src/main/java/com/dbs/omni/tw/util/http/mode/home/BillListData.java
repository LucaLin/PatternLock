package com.dbs.omni.tw.util.http.mode.home;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by siang on 2017/6/20.
 */

public class BillListData implements Parcelable {
    private String	txDate;		//交易日期YYYYMMDD
    private String  postDate;		//入帳日期YYYYMMDD
    private String	txDesc;		//消費明細
    private String	txAmt;		//消費金額(NTD)
    private String	orglAmt;		//原始消費金額
    private String	orglCry;		//原始消費幣別

    protected BillListData(Parcel in) {
        txDate = in.readString();
        postDate = in.readString();
        txDesc = in.readString();
        txAmt = in.readString();
        orglAmt = in.readString();
        orglCry = in.readString();
    }

    public static final Creator<BillListData> CREATOR = new Creator<BillListData>() {
        @Override
        public BillListData createFromParcel(Parcel in) {
            return new BillListData(in);
        }

        @Override
        public BillListData[] newArray(int size) {
            return new BillListData[size];
        }
    };

    public String getTxDate() {
        return txDate;
    }

    public void setTxDate(String txDate) {
        this.txDate = txDate;
    }

    public String getPostDate() {
        return postDate;
    }

    public void setPostDate(String postDate) {
        this.postDate = postDate;
    }

    public String getTxDesc() {
        return txDesc;
    }

    public void setTxDesc(String txDesc) {
        this.txDesc = txDesc;
    }

    public String getTxAmt() {
        return txAmt;
    }

    public void setTxAmt(String txAmt) {
        this.txAmt = txAmt;
    }

    public String getOrglAmt() {
        return orglAmt;
    }

    public void setOrglAmt(String orglAmt) {
        this.orglAmt = orglAmt;
    }

    public String getOrglCry() {
        return orglCry;
    }

    public void setOrglCry(String orglCry) {
        this.orglCry = orglCry;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(txDate);
        dest.writeString(postDate);
        dest.writeString(txDesc);
        dest.writeString(txAmt);
        dest.writeString(orglAmt);
        dest.writeString(orglCry);
    }
}
