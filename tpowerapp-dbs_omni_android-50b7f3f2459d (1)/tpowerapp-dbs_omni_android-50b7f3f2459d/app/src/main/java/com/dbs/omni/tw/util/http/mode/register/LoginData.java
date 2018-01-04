package com.dbs.omni.tw.util.http.mode.register;

import android.os.Parcel;
import android.os.Parcelable;

import com.dbs.omni.tw.util.http.mode.home.CreditCardData;

import java.util.ArrayList;

/**
 * Created by siang on 2017/6/1.
 */

public class LoginData implements Parcelable{

   private String  channelsAllowed;		//0 - Both CCDS & RIB
								        //1 - RIB Only (保留)
   	 							        //2 - CCDS Only
    private String  userType;		     //0 - Both CCDS & RIB
                                        //1 - RIB Only (保留)
								        //2 - CCDS Only
    private ArrayList<CreditCardData> ccList;

    private String dcFlag;               //是否變更裝置(for CCDTM)
                                        //1：未變更裝置
                                        //2：初次使用APP登入(跳出提醒約定裝置)
                                        //3：裝置變更(跳出提醒約定裝置)
    private String fpcFlag;		        //是否強制更換密碼(Y/N，預設為N)
    private String rmsFlag;		        //是否提醒設定email(Y/N，預設為N)

    protected LoginData(Parcel in) {
        channelsAllowed = in.readString();
        userType = in.readString();
        ccList = in.createTypedArrayList(CreditCardData.CREATOR);
        dcFlag = in.readString();
        fpcFlag = in.readString();
        rmsFlag = in.readString();
    }

    public static final Creator<LoginData> CREATOR = new Creator<LoginData>() {
        @Override
        public LoginData createFromParcel(Parcel in) {
            return new LoginData(in);
        }

        @Override
        public LoginData[] newArray(int size) {
            return new LoginData[size];
        }
    };

    public String getChannelsAllowed() {
        return channelsAllowed;
    }

    public void setChannelsAllowed(String channelsAllowed) {
        this.channelsAllowed = channelsAllowed;
    }

    public String getUserType() {
        return userType;
    }

    public void setUserType(String userType) {
        this.userType = userType;
    }

    public ArrayList<CreditCardData> getCcList() {
        return ccList;
    }

    public void setCcList(ArrayList<CreditCardData> ccList) {
        this.ccList = ccList;
    }

    public String getDcFlag() {
        return dcFlag;
    }

    public void setDcFlag(String dcFlag) {
        this.dcFlag = dcFlag;
    }

    public String getFpcFlag() {
        return fpcFlag;
    }

    public void setFpcFlag(String fpcFlag) {
        this.fpcFlag = fpcFlag;
    }

    public String getRmsFlag() {
        return rmsFlag;
    }

    public void setRmsFlag(String rmsFlag) {
        this.rmsFlag = rmsFlag;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(channelsAllowed);
        dest.writeString(userType);
        dest.writeTypedList(ccList);
        dest.writeString(dcFlag);
        dest.writeString(fpcFlag);
        dest.writeString(rmsFlag);
    }
}

