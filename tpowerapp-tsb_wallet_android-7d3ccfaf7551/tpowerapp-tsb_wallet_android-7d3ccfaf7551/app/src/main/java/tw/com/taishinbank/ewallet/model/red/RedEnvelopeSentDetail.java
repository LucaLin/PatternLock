package tw.com.taishinbank.ewallet.model.red;

import android.os.Parcel;
import android.os.Parcelable;

public class RedEnvelopeSentDetail extends RedEnvelopeDetail implements Parcelable{

    // 交易時間
    private String replyDate;

    private String tsfdSeq;

    protected RedEnvelopeSentDetail(Parcel in) {
        super(in);
        replyDate = in.readString();
        tsfdSeq = in.readString();
    }


    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeString(replyDate);
        dest.writeString(tsfdSeq);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<RedEnvelopeSentDetail> CREATOR = new Creator<RedEnvelopeSentDetail>() {
        @Override
        public RedEnvelopeSentDetail createFromParcel(Parcel in) {
            return new RedEnvelopeSentDetail(in);
        }

        @Override
        public RedEnvelopeSentDetail[] newArray(int size) {
            return new RedEnvelopeSentDetail[size];
        }
    };

    /**
     *
     * @return
     * The replyDate
     */
    public String getReplyDate() {
        return replyDate;
    }

    /**
     *
     * @param replyDate
     * The replyDate
     */
    public void setReplyDate(String replyDate) {
        this.replyDate = replyDate;
    }

    /**
     *
     * @return
     * The tsfdSeq
     */
    public String getTsfdSeq() {
        return tsfdSeq;
    }

    /**
     *
     * @param tsfdSeq
     * The tsfdSeq
     */
    public void setTsfdSeq(String tsfdSeq) {
        this.tsfdSeq = tsfdSeq;
    }
}
