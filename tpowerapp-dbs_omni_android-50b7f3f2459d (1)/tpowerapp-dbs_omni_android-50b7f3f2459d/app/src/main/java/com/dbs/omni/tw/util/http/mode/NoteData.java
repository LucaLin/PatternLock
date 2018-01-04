package com.dbs.omni.tw.util.http.mode;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

/**
 * Created by sherman-thinkpower on 2017/7/5.
 */

public class NoteData implements Parcelable{
    private String conStart;
    private String conEnd;
    private ArrayList<NoteListDetail> noteList;

    protected NoteData(Parcel in) {
        conStart = in.readString();
        conEnd = in.readString();
//        noteList = in.readArrayList(String.class.getClassLoader());
        noteList = in.createTypedArrayList(NoteListDetail.CREATOR);
    }

    public static final Parcelable.Creator<NoteData> CREATOR = new Parcelable.Creator<NoteData>() {
        @Override
        public NoteData createFromParcel(Parcel in) {
            return new NoteData(in);
        }

        @Override
        public NoteData[] newArray(int size) {
            return new NoteData[size];
        }
    };

    public NoteData(String conStart, String conEnd, ArrayList<NoteListDetail> noteList) {
        this.conStart = conStart;
        this.conEnd = conEnd;
        this.noteList = noteList;
    }

    public String getConStart() {
        return conStart;
    }

    public void setConStart(String conStart) {
        this.conStart = conStart;
    }

    public String getConEnd() {
        return conEnd;
    }

    public void setConEnd(String conEnd) {
        this.conEnd = conEnd;
    }

    public ArrayList<NoteListDetail> getNoteList() {
        return noteList;
    }

    public void setNoteList(ArrayList<NoteListDetail> noteList) {
        this.noteList = noteList;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(conStart);
        dest.writeString(conEnd);
        dest.writeTypedList(noteList);
    }
}
