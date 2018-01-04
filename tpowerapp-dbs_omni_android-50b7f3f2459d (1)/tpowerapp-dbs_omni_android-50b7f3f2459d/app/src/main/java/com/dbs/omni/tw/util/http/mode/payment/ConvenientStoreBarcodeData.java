package com.dbs.omni.tw.util.http.mode.payment;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by sherman-thinkpower on 2017/6/21.
 */

public class ConvenientStoreBarcodeData implements Parcelable{
    private String barCodeNo1, barCodeNo2, barCodeNo3, barCodeNo4;

    protected ConvenientStoreBarcodeData(Parcel in) {
        barCodeNo1 = in.readString();
        barCodeNo2 = in.readString();
        barCodeNo3 = in.readString();
        barCodeNo4 = in.readString();
    }

    public static final Creator<ConvenientStoreBarcodeData> CREATOR = new Creator<ConvenientStoreBarcodeData>() {
        @Override
        public ConvenientStoreBarcodeData createFromParcel(Parcel in) {
            return new ConvenientStoreBarcodeData(in);
        }

        @Override
        public ConvenientStoreBarcodeData[] newArray(int size) {
            return new ConvenientStoreBarcodeData[size];
        }
    };

    public String getBarCodeNo1() {
        return barCodeNo1;
    }

    public void setBarCodeNo1(String barCodeNo1) {
        this.barCodeNo1 = barCodeNo1;
    }

    public String getBarCodeNo2() {
        return barCodeNo2;
    }

    public void setBarCodeNo2(String barCodeNo2) {
        this.barCodeNo2 = barCodeNo2;
    }

    public String getBarCodeNo3() {
        return barCodeNo3;
    }

    public void setBarCodeNo3(String barCodeNo3) {
        this.barCodeNo3 = barCodeNo3;
    }

    public String getBarCodeNo4() {
        return barCodeNo4;
    }

    public void setBarCodeNo4(String barCodeNo4) {
        this.barCodeNo4 = barCodeNo4;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(barCodeNo1);
        dest.writeString(barCodeNo2);
        dest.writeString(barCodeNo3);
        dest.writeString(barCodeNo4);
    }
}
