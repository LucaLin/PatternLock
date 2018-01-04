package com.dbs.omni.tw.model.setting;

import android.os.Parcel;
import android.os.Parcelable;

import com.dbs.omni.tw.element.CreditCardItemView;

/**
 * Created by siang on 2017/4/21.
 */

public class ApplyCreditCardData implements Parcelable {

    private String cardName;
//    private int index;
    private CreditCardItemView.OnItemEventListener onItemEventListener;

    public ApplyCreditCardData(String cardName, CreditCardItemView.OnItemEventListener onItemEventListener) {
        this.cardName = cardName;
//        this.index = index;
        this.onItemEventListener = onItemEventListener;
    }

    protected ApplyCreditCardData(Parcel in) {
        cardName = in.readString();
//        index = in.readInt();
    }

    public static final Creator<ApplyCreditCardData> CREATOR = new Creator<ApplyCreditCardData>() {
        @Override
        public ApplyCreditCardData createFromParcel(Parcel in) {
            return new ApplyCreditCardData(in);
        }

        @Override
        public ApplyCreditCardData[] newArray(int size) {
            return new ApplyCreditCardData[size];
        }
    };

    public String getCardName() {
        return cardName;
    }

    public void setCardName(String cardName) {
        this.cardName = cardName;
    }
//
//    public int getSelectIndex() {
//        return index;
//    }
//
//    public void setSelectIndex(int index) {
//        this.index = index;
//    }

    public CreditCardItemView.OnItemEventListener getOnItemEventListener() {
        return onItemEventListener;
    }

    public void setOnItemEventListener(CreditCardItemView.OnItemEventListener onItemEventListener) {
        this.onItemEventListener = onItemEventListener;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(cardName);
//        dest.writeInt(index);
    }
}
