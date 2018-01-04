package com.dbs.omni.tw.util.http.mode.bill;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

/**
 * Created by siang on 2017/6/22.
 */

public class BilledDetailList implements Parcelable {
    private String stmtYM; //帳單月份YYYYMM(來自statementDate)
    private ArrayList<BilledDetail> cardList;

    protected BilledDetailList(Parcel in) {
        stmtYM = in.readString();
        cardList = in.createTypedArrayList(BilledDetail.CREATOR);
    }

    public static final Creator<BilledDetailList> CREATOR = new Creator<BilledDetailList>() {
        @Override
        public BilledDetailList createFromParcel(Parcel in) {
            return new BilledDetailList(in);
        }

        @Override
        public BilledDetailList[] newArray(int size) {
            return new BilledDetailList[size];
        }
    };

    public String getStmtYM() {
        return stmtYM;
    }

    public void setStmtYM(String stmtYM) {
        this.stmtYM = stmtYM;
    }

    public ArrayList<BilledDetail> getCardList() {
        return cardList;
    }

    public void setCardList(ArrayList<BilledDetail> cardList) {
        this.cardList = cardList;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(stmtYM);
        dest.writeTypedList(cardList);
    }
}
