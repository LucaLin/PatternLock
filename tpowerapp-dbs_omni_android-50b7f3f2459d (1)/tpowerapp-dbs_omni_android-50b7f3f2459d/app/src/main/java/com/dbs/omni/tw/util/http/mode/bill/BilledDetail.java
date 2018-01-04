package com.dbs.omni.tw.util.http.mode.bill;

import android.os.Parcel;
import android.os.Parcelable;

import com.dbs.omni.tw.util.http.mode.home.BillListData;

import java.util.ArrayList;

/**
 * Created by siang on 2017/6/20.
 */

public class BilledDetail implements Parcelable {
    private String	ccNO;	    //信用卡卡號
    private String	ccFlag;	    //M：正卡S:附卡
    private String	cardName;	//卡片名稱
    private String	ccLogo;	    //信用卡的產品代碼(系統用來辨別哪一張卡片，與PWeb串接會用此代碼)
    private ArrayList<BillListData> currentTXList;

    protected BilledDetail(Parcel in) {
        ccNO = in.readString();
        ccFlag = in.readString();
        cardName = in.readString();
        ccLogo = in.readString();
        currentTXList = in.createTypedArrayList(BillListData.CREATOR);
    }

    public static final Creator<BilledDetail> CREATOR = new Creator<BilledDetail>() {
        @Override
        public BilledDetail createFromParcel(Parcel in) {
            return new BilledDetail(in);
        }

        @Override
        public BilledDetail[] newArray(int size) {
            return new BilledDetail[size];
        }
    };

    public String getCcNO() {
        return ccNO;
    }

    public void setCcNO(String ccNO) {
        this.ccNO = ccNO;
    }

    public String getCcFlag() {
        return ccFlag;
    }

    public void setCcFlag(String ccFlag) {
        this.ccFlag = ccFlag;
    }

    public String getCardName() {
        return cardName;
    }

    public void setCardName(String cardName) {
        this.cardName = cardName;
    }

    public String getCcLogo() {
        return ccLogo;
    }

    public void setCcLogo(String ccLogo) {
        this.ccLogo = ccLogo;
    }

    public ArrayList<BillListData> getCurrentTXList() {
        return currentTXList;
    }

    public void setCurrentTXList(ArrayList<BillListData> currentTXList) {
        this.currentTXList = currentTXList;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(ccNO);
        dest.writeString(ccFlag);
        dest.writeString(cardName);
        dest.writeString(ccLogo);
        dest.writeTypedList(currentTXList);
    }
}
