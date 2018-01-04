package com.dbs.omni.tw.util.http.mode;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by sherman-thinkpower on 2017/7/5.
 */

public class NoteListDetail implements Parcelable {

    private String content;		        	//卡片名稱

    protected NoteListDetail(Parcel in) {
        content = in.readString();
    }

    public static final Creator<NoteListDetail> CREATOR = new Creator<NoteListDetail>() {
        @Override
        public NoteListDetail createFromParcel(Parcel in) {
            return new NoteListDetail(in);
        }

        @Override
        public NoteListDetail[] newArray(int size) {
            return new NoteListDetail[size];
        }
    };

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(content);
    }
}

