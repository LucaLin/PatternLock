package tw.com.taishinbank.ewallet.model.red;

import android.os.Parcel;
import android.os.Parcelable;

public class RedEnvelopeReceivedHeader extends RedEnvelopeHeader<RedEnvelopeReceivedDetail> implements Parcelable{

    // 發出紅包的人(稱呼)
    private String sender;
    // 發出紅包的人(ID)
    private String senderMem;
    // 收到的訊息
    private String message;

    protected RedEnvelopeReceivedHeader(Parcel in) {
        super(in);
        sender = in.readString();
        senderMem = in.readString();
        message = in.readString();
        txDetailList = in.createTypedArrayList(RedEnvelopeReceivedDetail.CREATOR);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<RedEnvelopeReceivedHeader> CREATOR = new Creator<RedEnvelopeReceivedHeader>() {
        @Override
        public RedEnvelopeReceivedHeader createFromParcel(Parcel in) {
            return new RedEnvelopeReceivedHeader(in);
        }

        @Override
        public RedEnvelopeReceivedHeader[] newArray(int size) {
            return new RedEnvelopeReceivedHeader[size];
        }
    };

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeString(sender);
        dest.writeString(senderMem);
        dest.writeString(message);
        dest.writeTypedList(txDetailList);
    }

    /**
     *
     * @return
     * The sender
     */
    public String getSender() {
        return sender;
    }

    /**
     *
     * @param sender
     * The sender
     */
    public void setSender(String sender) {
        this.sender = sender;
    }


    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getSenderMem() {return senderMem;}

    public void setSenderMem(String senderMem) {this.senderMem = senderMem;}


}
