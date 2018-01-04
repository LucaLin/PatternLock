package com.dbs.omni.tw.model.log;

import android.text.format.DateFormat;

import java.util.Calendar;

/**
 * Created by Siang on 4/18/16.
 */
public class SpecialEvent {
    public static final String TYPE_CRASH = "type_crash";
    public static final String TYPE_SERVER_API = "type_server_api";
    public static final String TYPE_GCM = "type_gcm";
    public static final String TYPE_CREDIT_CARD = "type_credit_card";
    public static final String TYPE_ROOT = "type_root";

    private int recordId;

    private String eventType;	//App自訂事件類型

    private String eventNote;	//App自訂內容

    // 從DB取出來使用
    public SpecialEvent(int recordId, String eventType, String note) {

        this.recordId = recordId;
        this.eventType = eventType;
        this.eventNote = note;
    }

    // 新增至DB時使用
    public SpecialEvent(String eventType, String note) {
        this.eventType = eventType;
        this.eventNote = toFormatLog(note);
    }

    public String getEventNote() {
        return eventNote;
    }

    public void setEventNote(String eventNote) {
        this.eventNote = toFormatLog(eventNote);
    }

    public String getEventType() {
        return eventType;
    }

    public void setEventType(String eventType) {
        this.eventType = eventType;
    }

    public int getRecordId() {
        return recordId;
    }

    public void setRecordId(int recordId) {
        this.recordId = recordId;
    }

    private String toFormatLog(String log) {
        Calendar calendar = Calendar.getInstance();
        CharSequence currentTime = DateFormat.format("yyyy-MM-dd kk:mm:ss", calendar.getTime());
        return String.format("%1$s - %2$s", currentTime.toString(), log);
    }

}
