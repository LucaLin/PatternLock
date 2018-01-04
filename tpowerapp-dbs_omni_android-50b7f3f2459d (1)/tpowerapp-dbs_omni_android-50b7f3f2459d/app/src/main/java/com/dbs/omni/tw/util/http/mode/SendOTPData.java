package com.dbs.omni.tw.util.http.mode;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by siang on 2017/5/25.
 */

public class SendOTPData implements Parcelable{
    private String opaque;
    private String phoneNumber;

    protected SendOTPData(Parcel in) {
        opaque = in.readString();
        phoneNumber = in.readString();
    }

    public static final Creator<SendOTPData> CREATOR = new Creator<SendOTPData>() {
        @Override
        public SendOTPData createFromParcel(Parcel in) {
            return new SendOTPData(in);
        }

        @Override
        public SendOTPData[] newArray(int size) {
            return new SendOTPData[size];
        }
    };

    public String getOpaque() {
        return opaque;
    }

    public void setOpaque(String opaque) {
        this.opaque = opaque;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(opaque);
        dest.writeString(phoneNumber);
    }
}
