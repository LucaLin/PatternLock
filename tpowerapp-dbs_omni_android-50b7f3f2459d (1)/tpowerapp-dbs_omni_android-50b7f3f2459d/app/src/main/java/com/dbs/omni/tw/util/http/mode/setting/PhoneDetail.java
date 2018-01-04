package com.dbs.omni.tw.util.http.mode.setting;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by siang on 2017/6/23.
 */

public class PhoneDetail implements Parcelable {

    private String	phoneID;	//行動電話識別碼
    private String	phoneCtryCode;	//國碼
    private String	phoneNumber;	//行動電話
    private String	updatedPhoneCtryCode;	//延緩更新國碼
    private String	updatedPhoneNumber;	//延緩更新行動電話

    protected PhoneDetail(Parcel in) {
        phoneID = in.readString();
        phoneCtryCode = in.readString();
        phoneNumber = in.readString();
        updatedPhoneCtryCode = in.readString();
        updatedPhoneNumber = in.readString();
    }

    public static final Creator<PhoneDetail> CREATOR = new Creator<PhoneDetail>() {
        @Override
        public PhoneDetail createFromParcel(Parcel in) {
            return new PhoneDetail(in);
        }

        @Override
        public PhoneDetail[] newArray(int size) {
            return new PhoneDetail[size];
        }
    };

    public String getPhoneID() {
        return phoneID;
    }

    public void setPhoneID(String phoneID) {
        this.phoneID = phoneID;
    }

    public String getPhoneCtryCode() {
        return phoneCtryCode;
    }

    public void setPhoneCtryCode(String phoneCtryCode) {
        this.phoneCtryCode = phoneCtryCode;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getUpdatedPhoneCtryCode() {
        return updatedPhoneCtryCode;
    }

    public void setUpdatedPhoneCtryCode(String updatedPhoneCtryCode) {
        this.updatedPhoneCtryCode = updatedPhoneCtryCode;
    }

    public String getUpdatedPhoneNumber() {
        return updatedPhoneNumber;
    }

    public void setUpdatedPhoneNumber(String updatedPhoneNumber) {
        this.updatedPhoneNumber = updatedPhoneNumber;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(phoneID);
        dest.writeString(phoneCtryCode);
        dest.writeString(phoneNumber);
        dest.writeString(updatedPhoneCtryCode);
        dest.writeString(updatedPhoneNumber);
    }
}
