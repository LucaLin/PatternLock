package com.dbs.omni.tw.util.http.mode.setting;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by siang on 2017/6/23.
 */

public class UserNameDetail implements Parcelable{
    private String firstName;   //名
    private String middleName;	//中間名(字、號)
    private String lastName;	//姓
    private String fullName;	//全名
    private String salutation;	//稱謂

    protected UserNameDetail(Parcel in) {
        firstName = in.readString();
        middleName = in.readString();
        lastName = in.readString();
        fullName = in.readString();
        salutation = in.readString();
    }

    public static final Creator<UserNameDetail> CREATOR = new Creator<UserNameDetail>() {
        @Override
        public UserNameDetail createFromParcel(Parcel in) {
            return new UserNameDetail(in);
        }

        @Override
        public UserNameDetail[] newArray(int size) {
            return new UserNameDetail[size];
        }
    };

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getMiddleName() {
        return middleName;
    }

    public void setMiddleName(String middleName) {
        this.middleName = middleName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getSalutation() {
        return salutation;
    }

    public void setSalutation(String salutation) {
        this.salutation = salutation;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(firstName);
        dest.writeString(middleName);
        dest.writeString(lastName);
        dest.writeString(fullName);
        dest.writeString(salutation);
    }
}
