package com.dbs.omni.tw.model.element;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by siang on 2017/5/11.
 */

public class FilterItemData implements Parcelable {

    private String content;
    private boolean isSelect;

    public FilterItemData(String content, boolean isSelect) {
        this.content = content;
        this.isSelect = isSelect;
    }

    protected FilterItemData(Parcel in) {
        content = in.readString();
        isSelect = in.readByte() != 0;
    }

    public static final Creator<FilterItemData> CREATOR = new Creator<FilterItemData>() {
        @Override
        public FilterItemData createFromParcel(Parcel in) {
            return new FilterItemData(in);
        }

        @Override
        public FilterItemData[] newArray(int size) {
            return new FilterItemData[size];
        }
    };

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public boolean isSelect() {
        return isSelect;
    }

    public void setSelect(boolean select) {
        isSelect = select;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {

    }
}
