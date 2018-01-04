package com.dbs.omni.tw.util.http.mode.home;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

/**
 * Created by siang on 2017/6/20.
 */

public class UnBilledDetail implements Parcelable {
    private String	ccNO;	    //信用卡卡號
    private String	ccFlag;	    //M：正卡S:附卡
    private String	cardName;	//卡片名稱
    private String	ccLogo;	    //信用卡的產品代碼(系統用來辨別哪一張卡片，與PWeb串接會用此代碼)
    private ArrayList<BillListData> unbillTXList;

    protected UnBilledDetail(Parcel in) {
        ccNO = in.readString();
        ccFlag = in.readString();
        cardName = in.readString();
        ccLogo = in.readString();
        unbillTXList = in.createTypedArrayList(BillListData.CREATOR);
    }

    public static final Creator<UnBilledDetail> CREATOR = new Creator<UnBilledDetail>() {
        @Override
        public UnBilledDetail createFromParcel(Parcel in) {
            return new UnBilledDetail(in);
        }

        @Override
        public UnBilledDetail[] newArray(int size) {
            return new UnBilledDetail[size];
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

    public ArrayList<BillListData> getUnbillTXList() {
        return unbillTXList;
    }

    public void setUnbillTXList(ArrayList<BillListData> unbillTXList) {
        this.unbillTXList = unbillTXList;
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
        dest.writeTypedList(unbillTXList);
    }
}
