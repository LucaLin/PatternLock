package com.dbs.omni.tw.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by siang on 2017/5/10.
 */

public class ShowTextData implements Parcelable{
    private String title;
    private String content;
    private String subContent;

    public ShowTextData(String title, String content) {
        this.title = title;
        this.content = content;
    }

    public ShowTextData(String title, String content, String subContent) {
        this.title = title;
        this.content = content;
        this.subContent = subContent;
    }

    protected ShowTextData(Parcel in) {
        title = in.readString();
        content = in.readString();
        subContent = in.readString();
    }

    public static final Creator<ShowTextData> CREATOR = new Creator<ShowTextData>() {
        @Override
        public ShowTextData createFromParcel(Parcel in) {
            return new ShowTextData(in);
        }

        @Override
        public ShowTextData[] newArray(int size) {
            return new ShowTextData[size];
        }
    };

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getSubContent() {
        return subContent;
    }

    public void setSubContent(String subContent) {
        this.subContent = subContent;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(title);
        dest.writeString(content);
        dest.writeString(subContent);
    }
}
