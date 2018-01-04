package com.dbs.omni.tw.util.http.mode;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by siang on 2017/6/1.
 */

public class SSOData implements Parcelable{

    private String pKey;	//SSO相關功能使用
    private String rKey;	//SSO相關功能使用


    protected SSOData(Parcel in) {
        pKey = in.readString();
        rKey = in.readString();
    }

    public static final Creator<SSOData> CREATOR = new Creator<SSOData>() {
        @Override
        public SSOData createFromParcel(Parcel in) {
            return new SSOData(in);
        }

        @Override
        public SSOData[] newArray(int size) {
            return new SSOData[size];
        }
    };

    public String getpKey() {
        return pKey;
    }

    public void setpKey(String pKey) {
        this.pKey = pKey;
    }

    public String getrKey() {
        return rKey;
    }

    public void setrKey(String rKey) {
        this.rKey = rKey;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(pKey);
        dest.writeString(rKey);
    }
}
