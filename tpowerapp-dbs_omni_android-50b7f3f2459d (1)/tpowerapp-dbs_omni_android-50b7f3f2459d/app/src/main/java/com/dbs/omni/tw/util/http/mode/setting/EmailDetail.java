package com.dbs.omni.tw.util.http.mode.setting;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by siang on 2017/6/23.
 */

public class EmailDetail implements Parcelable{

    private String	emailID;	//電子信箱識別碼
    private String	email;	//電子信箱

    protected EmailDetail(Parcel in) {
        emailID = in.readString();
        email = in.readString();
    }

    public static final Creator<EmailDetail> CREATOR = new Creator<EmailDetail>() {
        @Override
        public EmailDetail createFromParcel(Parcel in) {
            return new EmailDetail(in);
        }

        @Override
        public EmailDetail[] newArray(int size) {
            return new EmailDetail[size];
        }
    };

    public String getEmailID() {
        return emailID;
    }

    public void setEmailID(String emailID) {
        this.emailID = emailID;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(emailID);
        dest.writeString(email);
    }
}
