package tw.com.taishinbank.ewallet.model.log;

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

    private int recordId;

    private String type;	//App自訂事件類型

    private String note;	//App自訂內容

    // 從DB取出來使用
    public SpecialEvent(int recordId, String type, String note) {

        this.recordId = recordId;
        this.type = type;
        this.note = note;
    }

    // 新增至DB時使用
    public SpecialEvent(String type, String note) {
        this.type = type;
        this.note = toFormatLog(note);
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = toFormatLog(note);
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
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
