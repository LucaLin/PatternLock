package com.dbs.omni.tw.util.http.mode.register;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by siang on 2017/6/14.
 */

public class RegisterData implements Parcelable{
    private String	nID = "";        //National ID 身分證號/居留證號
    private String	userCode = "";	//使用者帳號(6~15碼英數字)
    private String	pCode = "";	    //加密後密碼
    private String	rKey = "";	    //Random key for pCode
    private String	nickname = "";	//暱稱(中英文字，限定為10字，不可設定特殊符號或空白)
    private String	bDate = "";	    //Date of birth生日 yyyymmdd
    private String	ccNO = "";	    //Card NO 信用卡卡號 16碼數字
    private String	expDate = "";    //信用卡效期 mmyy
    private String	anzUserCode = "";//ANZ使用者代號(6~15碼英數字)
    private boolean isEanbleTouchID = false;



    public RegisterData() {}

    protected RegisterData(Parcel in) {
        nID = in.readString();
        userCode = in.readString();
        pCode = in.readString();
        rKey = in.readString();
        nickname = in.readString();
        bDate = in.readString();
        ccNO = in.readString();
        expDate = in.readString();
        anzUserCode = in.readString();
        isEanbleTouchID = in.readByte() != 0;
    }

    public static final Creator<RegisterData> CREATOR = new Creator<RegisterData>() {
        @Override
        public RegisterData createFromParcel(Parcel in) {
            return new RegisterData(in);
        }

        @Override
        public RegisterData[] newArray(int size) {
            return new RegisterData[size];
        }
    };

    public String getnID() {
        return nID;
    }

    public void setnID(String nID) {
        this.nID = nID;
    }

    public String getUserCode() {
        return userCode;
    }

    public void setUserCode(String userCode) {
        this.userCode = userCode;
    }

    public String getpCode() {
        return pCode;
    }

    public void setpCode(String pCode) {
        this.pCode = pCode;
    }

    public String getrKey() {
        return rKey;
    }

    public void setrKey(String rKey) {
        this.rKey = rKey;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getbDate() {
        return bDate;
    }

    public void setbDate(String bDate) {
        this.bDate = bDate;
    }

    public String getCcNO() {
        return ccNO;
    }

    public void setCcNO(String ccNO) {
        this.ccNO = ccNO;
    }

    public String getExpDate() {
        return expDate;
    }

    public void setExpDate(String expDate) {
        this.expDate = expDate;
    }

    public String getAnzUserCode() {
        return anzUserCode;
    }

    public void setAnzUserCode(String anzUserCode) {
        this.anzUserCode = anzUserCode;
    }

    public boolean isEanbleTouchID() {
        return isEanbleTouchID;
    }

    public void setEanbleTouchID(boolean eanbleTouchID) {
        isEanbleTouchID = eanbleTouchID;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(nID);
        dest.writeString(userCode);
        dest.writeString(pCode);
        dest.writeString(rKey);
        dest.writeString(nickname);
        dest.writeString(bDate);
        dest.writeString(ccNO);
        dest.writeString(expDate);
        dest.writeString(anzUserCode);
        dest.writeByte((byte) (isEanbleTouchID ? 1 : 0));
    }
}
