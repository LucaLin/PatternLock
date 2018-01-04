package com.dbs.omni.tw.util.http.mode.register;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by siang on 2017/6/1.
 */

public class LogoutDate implements Parcelable{

    private String loginTime;	    //登入時間YYYYMMDDHHMMSS
    private String logoutTime;		//登出時間YYYYMMDDHHMMSS
    private String stayTime;		//停留時間HRMM

    protected LogoutDate(Parcel in) {
        loginTime = in.readString();
        logoutTime = in.readString();
        stayTime = in.readString();
    }

    public static final Creator<LogoutDate> CREATOR = new Creator<LogoutDate>() {
        @Override
        public LogoutDate createFromParcel(Parcel in) {
            return new LogoutDate(in);
        }

        @Override
        public LogoutDate[] newArray(int size) {
            return new LogoutDate[size];
        }
    };

    public String getLoginTime() {
        return loginTime;
    }

    public void setLoginTime(String loginTime) {
        this.loginTime = loginTime;
    }

    public String getLogoutTime() {
        return logoutTime;
    }

    public void setLogoutTime(String logoutTime) {
        this.logoutTime = logoutTime;
    }

    public String getStayTime() {
        return stayTime;
    }

    public void setStayTime(String stayTime) {
        this.stayTime = stayTime;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(loginTime);
        dest.writeString(logoutTime);
        dest.writeString(stayTime);
    }
}
