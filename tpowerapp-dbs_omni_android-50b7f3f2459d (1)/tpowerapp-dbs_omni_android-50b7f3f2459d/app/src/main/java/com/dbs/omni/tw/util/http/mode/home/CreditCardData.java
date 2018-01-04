package com.dbs.omni.tw.util.http.mode.home;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by siang on 2017/6/1.
 */

public class CreditCardData implements Parcelable {

    private String cardName;		        	//卡片名稱
    private String ccNO;			        	//信用卡卡號
    private String ccBrand;			    	//信用卡品牌
    private String ccLogo;			        	//信用卡的產品代碼(系統用來辨別哪一張卡片，與PWeb串接會用此代碼)
    private String ccDesc;			        	//卡片介紹
    private String isVirtualCard;	        	//是否為虛擬卡
    private String expDate;			    	//信用卡效期 MMYY
    private String ownerName;		        	//持有人名稱(本名)
    private String ccFlag;			        	///M：正卡, S：附卡
    private String ccStatus;		        	//卡片狀態說明
                                              //  1-Pending activation
                                              //  2-Active
                                              //  3-Hot-tagged
                                              //  4-Lost/Stolen
                                              //  5-Expired
                                              //  6-Cancelled/Closed
                                              //  7-Transferred
                                              //  8-Pending initial PIN change
                                              //  9-Pending PIN reset
                                              //  10-Pending re-issue of card
                                              //  11-Tagged for deletion
                                              //  12-Retained
    private String ccID;	 		            //卡片ID

    protected CreditCardData(Parcel in) {
        cardName = in.readString();
        ccNO = in.readString();
        ccBrand = in.readString();
        ccLogo = in.readString();
        ccDesc = in.readString();
        isVirtualCard = in.readString();
        expDate = in.readString();
        ownerName = in.readString();
        ccFlag = in.readString();
        ccStatus = in.readString();
        ccID = in.readString();
    }

    public static final Creator<CreditCardData> CREATOR = new Creator<CreditCardData>() {
        @Override
        public CreditCardData createFromParcel(Parcel in) {
            return new CreditCardData(in);
        }

        @Override
        public CreditCardData[] newArray(int size) {
            return new CreditCardData[size];
        }
    };

    public String getCardName() {
        return cardName;
    }

    public void setCardName(String cardName) {
        this.cardName = cardName;
    }

    public String getCcNO() {
        return ccNO;
    }

    public void setCcNO(String ccNO) {
        this.ccNO = ccNO;
    }

    public String getCcBrand() {
        return ccBrand;
    }

    public void setCcBrand(String ccBrand) {
        this.ccBrand = ccBrand;
    }

    public String getCcLogo() {
        return ccLogo;
    }

    public void setCcLogo(String ccLogo) {
        this.ccLogo = ccLogo;
    }

    public String getCcDesc() {
        return ccDesc;
    }

    public void setCcDesc(String ccDesc) {
        this.ccDesc = ccDesc;
    }

    public String getIsVirtualCard() {
        return isVirtualCard;
    }

    public void setIsVirtualCard(String isVirtualCard) {
        this.isVirtualCard = isVirtualCard;
    }

    public String getExpDate() {
        return expDate;
    }

    public void setExpDate(String expDate) {
        this.expDate = expDate;
    }

    public String getOwnerName() {
        return ownerName;
    }

    public void setOwnerName(String ownerName) {
        this.ownerName = ownerName;
    }

    public String getCcFlag() {
        return ccFlag;
    }

    public void setCcFlag(String ccFlag) {
        this.ccFlag = ccFlag;
    }

    public String getCcStatus() {
        return ccStatus;
    }

    public void setCcStatus(String ccStatus) {
        this.ccStatus = ccStatus;
    }

    public String getCcID() {
        return ccID;
    }

    public void setCcID(String ccID) {
        this.ccID = ccID;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(cardName);
        dest.writeString(ccNO);
        dest.writeString(ccBrand);
        dest.writeString(ccLogo);
        dest.writeString(ccDesc);
        dest.writeString(isVirtualCard);
        dest.writeString(expDate);
        dest.writeString(ownerName);
        dest.writeString(ccFlag);
        dest.writeString(ccStatus);
        dest.writeString(ccID);
    }
}
