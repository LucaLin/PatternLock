package com.dbs.omni.tw.util.http.mode.setting;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by siang on 2017/6/23.
 */

public class AddressDetail implements Parcelable {

    private String	addressID;	//住址識別碼
    private String	zipCode;	//郵遞區號
    private String	address;	//地址
    private String	updatedZipCode;	//延緩更新郵遞區號
    private String	updatedAddress;	//延緩更新地址

    protected AddressDetail(Parcel in) {
        addressID = in.readString();
        zipCode = in.readString();
        address = in.readString();
        updatedZipCode = in.readString();
        updatedAddress = in.readString();
    }

    public static final Creator<AddressDetail> CREATOR = new Creator<AddressDetail>() {
        @Override
        public AddressDetail createFromParcel(Parcel in) {
            return new AddressDetail(in);
        }

        @Override
        public AddressDetail[] newArray(int size) {
            return new AddressDetail[size];
        }
    };

    public String getAddressID() {
        return addressID;
    }

    public void setAddressID(String addressID) {
        this.addressID = addressID;
    }

    public String getZipCode() {
        return zipCode;
    }

    public void setZipCode(String zipCode) {
        this.zipCode = zipCode;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getUpdatedZipCode() {
        return updatedZipCode;
    }

    public void setUpdatedZipCode(String updatedZipCode) {
        this.updatedZipCode = updatedZipCode;
    }

    public String getUpdatedAddress() {
        return updatedAddress;
    }

    public void setUpdatedAddress(String updatedAddress) {
        this.updatedAddress = updatedAddress;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(addressID);
        dest.writeString(zipCode);
        dest.writeString(address);
        dest.writeString(updatedZipCode);
        dest.writeString(updatedAddress);
    }
}
