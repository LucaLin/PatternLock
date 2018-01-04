package tw.com.taishinbank.ewallet.model.red;

import android.os.Parcel;
import android.os.Parcelable;

public class RedEnvelopeReceivedDetail extends RedEnvelopeDetail implements Parcelable {

    // 交易時間
    private String createDate;
    // 回覆時間
    private String replyTime;

    private String txfdSeq;

    protected RedEnvelopeReceivedDetail(Parcel in) {
        super(in);
        createDate = in.readString();
        replyTime = in.readString();
        txfdSeq = in.readString();
    }

    public static final Creator<RedEnvelopeReceivedDetail> CREATOR = new Creator<RedEnvelopeReceivedDetail>() {
        @Override
        public RedEnvelopeReceivedDetail createFromParcel(Parcel in) {
            return new RedEnvelopeReceivedDetail(in);
        }

        @Override
        public RedEnvelopeReceivedDetail[] newArray(int size) {
            return new RedEnvelopeReceivedDetail[size];
        }
    };

    /**
     *
     * @return
     * The createDate
     */
    public String getCreateDate() {
        return createDate;
    }

    /**
     *
     * @param createDate
     * The createDate
     */
    public void setCreateDate(String createDate) {
        this.createDate = createDate;
    }

    /**
     *
     * @return
     * The txfdSeq
     */
    public String getTxfdSeq() {
        return txfdSeq;
    }

    /**
     *
     * @param txfdSeq
     * The txfdSeq
     */
    public void setTxfdSeq(String txfdSeq) {
        this.txfdSeq = txfdSeq;
    }


    public String getReplyTime() {
        return replyTime;
    }

    public void setReplyTime(String replyTime) {
        this.replyTime = replyTime;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeString(createDate);
        dest.writeString(replyTime);
        dest.writeString(txfdSeq);
    }
}
