package com.dbs.omni.tw.util.http.mode.bill;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by siang on 2017/6/22.
 */

public class EBillFileData implements Parcelable {
    private String 	eBillNO;	//帳單流水號
    private String 	eBillYYYY;	//帳單日期-年 YYYY
    private String 	eBillMM;	//帳單日期-月 MM
    private String 	eBillFromDate;	//帳單起日YYYYMMDD
    private String 	eBillToDate;	//帳單迄日YYYYMMDD
    private String 	eBillFileName;	//帳單PDF檔名

    protected EBillFileData(Parcel in) {
        eBillNO = in.readString();
        eBillYYYY = in.readString();
        eBillMM = in.readString();
        eBillFromDate = in.readString();
        eBillToDate = in.readString();
        eBillFileName = in.readString();
    }

    public static final Creator<EBillFileData> CREATOR = new Creator<EBillFileData>() {
        @Override
        public EBillFileData createFromParcel(Parcel in) {
            return new EBillFileData(in);
        }

        @Override
        public EBillFileData[] newArray(int size) {
            return new EBillFileData[size];
        }
    };

    public String geteBillNO() {
        return eBillNO;
    }

    public void seteBillNO(String eBillNO) {
        this.eBillNO = eBillNO;
    }

    public String geteBillYYYY() {
        return eBillYYYY;
    }

    public void seteBillYYYY(String eBillYYYY) {
        this.eBillYYYY = eBillYYYY;
    }

    public String geteBillMM() {
        return eBillMM;
    }

    public void seteBillMM(String eBillMM) {
        this.eBillMM = eBillMM;
    }

    public String geteBillFromDate() {
        return eBillFromDate;
    }

    public void seteBillFromDate(String eBillFromDate) {
        this.eBillFromDate = eBillFromDate;
    }

    public String geteBillToDate() {
        return eBillToDate;
    }

    public void seteBillToDate(String eBillToDate) {
        this.eBillToDate = eBillToDate;
    }

    public String geteBillFileName() {
        return eBillFileName;
    }

    public void seteBillFileName(String eBillFileName) {
        this.eBillFileName = eBillFileName;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(eBillNO);
        dest.writeString(eBillYYYY);
        dest.writeString(eBillMM);
        dest.writeString(eBillFromDate);
        dest.writeString(eBillToDate);
        dest.writeString(eBillFileName);
    }
}
