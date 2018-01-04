package tw.com.taishinbank.ewallet.model.extra;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Blake on 3/8/16.
 */
public class TicketListData implements Parcelable {

    private String	etkSeq;     //	電子票券 錢包系統ID
    private String	serialNo;   //	電子票券序號
    private String	title;      //	標題
    private String	status;     //	電子票券
    private String	readFlag;   //	0:未讀(預設)、1:已讀
    private String	iconUrl;    //	圖示網址
    private String	note;       // 	電子票券須知
    private String	buyDate;    //	購入時間 yyyyMMddHHmmss
    private String  lastUpdate;

    protected TicketListData(Parcel in) {
        etkSeq = in.readString();
        serialNo = in.readString();
        title = in.readString();
        status = in.readString();
        readFlag = in.readString();
        iconUrl = in.readString();
        note = in.readString();
        buyDate = in.readString();
        lastUpdate = in.readString();
    }

    public static final Creator<TicketListData> CREATOR = new Creator<TicketListData>() {
        @Override
        public TicketListData createFromParcel(Parcel in) {
            return new TicketListData(in);
        }

        @Override
        public TicketListData[] newArray(int size) {
            return new TicketListData[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(etkSeq);
        dest.writeString(serialNo);
        dest.writeString(title);
        dest.writeString(status);
        dest.writeString(readFlag);
        dest.writeString(iconUrl);
        dest.writeString(note);
        dest.writeString(buyDate);
        dest.writeString(lastUpdate);
    }

    public String getLastUpdate() {
        return lastUpdate;
    }

    public void setLastUpdate(String lastUpdate) {
        this.lastUpdate = lastUpdate;
    }

    public String getEtkSeq() {
        return etkSeq;
    }

    public void setEtkSeq(String etkSeq) {
        this.etkSeq = etkSeq;
    }

    public String getSerialNo() {
        return serialNo;
    }

    public void setSerialNo(String serialNo) {
        this.serialNo = serialNo;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getReadFlag() {
        return readFlag;
    }

    public void setReadFlag(String readFlag) {
        this.readFlag = readFlag;
    }

    public String getIconUrl() {
        return iconUrl;
    }

    public void setIconUrl(String iconUrl) {
        this.iconUrl = iconUrl;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public String getBuyDate() {
        return buyDate;
    }

    public void setBuyDate(String buyDate) {
        this.buyDate = buyDate;
    }
}
