package tw.com.taishinbank.ewallet.model.red;

import android.os.Parcel;
import android.os.Parcelable;

public class RedEnvelopeDetail implements Parcelable{

    // 收到紅包的人(稱呼)
    private String name;

    // 收到的人的ID
    private String toMem;

    // 金額(只有本人才會有值)
    private String amount;

    // 回覆訊息
    private String replyMessage;

    //讀取狀態
    private String readFlag;

    // 回覆金額
    private String replyAmount;

    protected RedEnvelopeDetail(Parcel in) {
        name = in.readString();
        toMem = in.readString();
        amount = in.readString();
        replyMessage = in.readString();
        readFlag = in.readString();
        replyAmount = in.readString();
    }

    public static final Creator<RedEnvelopeDetail> CREATOR = new Creator<RedEnvelopeDetail>() {
        @Override
        public RedEnvelopeDetail createFromParcel(Parcel in) {
            return new RedEnvelopeDetail(in);
        }

        @Override
        public RedEnvelopeDetail[] newArray(int size) {
            return new RedEnvelopeDetail[size];
        }
    };

    /**
     *
     * @return
     * The name
     */
    public String getName() {
        return name;
    }
    /**
     *
     * @param name
     * The name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     *
     * @return
     * The amount
     */
    public String getAmount() {
        return amount;
    }

    /**
     *
     * @param amount
     * The amount
     */
    public void setAmount(String amount) {
        this.amount = amount;
    }

    /**
     *
     * @return
     * The replyMessage
     */
    public String getReplyMessage() {
        return replyMessage;
    }

    /**
     *
     * @param replyMessage
     * The replyMessage
     */
    public void setReplyMessage(String replyMessage) {
        this.replyMessage = replyMessage;
    }

    public String getToMem() {
        return toMem;
    }


    public void setToMem(String toMem) {
        this.toMem = toMem;
    }

    public String getReadFlag() {
        return readFlag;
    }

    /**
     *
     * @param readFlag
     * The readFlag
     */
    public void setReadFlag(String readFlag) {
        this.readFlag = readFlag;
    }

    public String getReplyAmount() {
        return replyAmount;
    }

    public void setReplyAmount(String replyAmount) {
        this.replyAmount = replyAmount;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeString(toMem);
        dest.writeString(amount);
        dest.writeString(replyMessage);
        dest.writeString(readFlag);
        dest.writeString(replyAmount);
    }
}
