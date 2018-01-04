package tw.com.taishinbank.ewallet.model.wallethome;


import android.os.Parcel;
import android.os.Parcelable;

public class WalletSystemMsg implements Parcelable {
/**
 * title	    String		主標
 * content	    String		公告內容
 * bbType	    String		公告類別
 * onLineDate   String      上線時間(yyyyMMdd)
 * downLineDate String      下線時間(yyyyMMdd)
 * createDate   String      建立日期(yyyyMMddHHmmss)
 * modifyDate   String      變更日期(yyyyMMddHHmmss)
 * bbSeq        int     公告序號
 */

    private String title;
    private String content;
    private String bbType;
    private String onLineDate;
    private String downLineDate;
    private String createDate;
    private String modifyDate;
    private int bbSeq;

    protected WalletSystemMsg(Parcel in) {
        title = in.readString();
        content = in.readString();
        bbType = in.readString();
        onLineDate = in.readString();
        downLineDate = in.readString();
        createDate = in.readString();
        modifyDate = in.readString();
        bbSeq = in.readInt();
    }

    public static final Creator<WalletSystemMsg> CREATOR = new Creator<WalletSystemMsg>() {
        @Override
        public WalletSystemMsg createFromParcel(Parcel in) {
            return new WalletSystemMsg(in);
        }

        @Override
        public WalletSystemMsg[] newArray(int size) {
            return new WalletSystemMsg[size];
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

    public String getBbType() {
        return bbType;
    }

    public void setBbType(String bbType) {
        this.bbType = bbType;
    }

    public String getOnLineDate() {
        return onLineDate;
    }

    public void setOnLineDate(String onLineDate) {
        this.onLineDate = onLineDate;
    }

    public String getDownLineDate() {
        return downLineDate;
    }

    public void setDownLineDate(String downLineDate) {
        this.downLineDate = downLineDate;
    }

    public String getCreateDate() {
        return createDate;
    }

    public void setCreateDate(String createDate) {
        this.createDate = createDate;
    }

    public String getModifyDate() {
        return modifyDate;
    }

    public void setModifyDate(String modifyDate) {
        this.modifyDate = modifyDate;
    }

    public int getBbSeq() {
        return bbSeq;
    }

    public void setBbSeq(int bbSeq) {
        this.bbSeq = bbSeq;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(title);
        dest.writeString(content);
        dest.writeString(bbType);
        dest.writeString(onLineDate);
        dest.writeString(downLineDate);
        dest.writeString(createDate);
        dest.writeString(modifyDate);
        dest.writeInt(bbSeq);
    }
}
