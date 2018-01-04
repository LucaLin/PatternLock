package tw.com.taishinbank.ewallet.model.red;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

public class RedEnvelopeSentHeader extends RedEnvelopeHeader<RedEnvelopeSentDetail> implements Parcelable{

    // 筆數
    private int size;
    // 發出的訊息
    private String senderMessage;

    protected RedEnvelopeSentHeader(Parcel in) {
        super(in);
        size = in.readInt();
        senderMessage = in.readString();
        txDetailList = in.createTypedArrayList(RedEnvelopeSentDetail.CREATOR);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<RedEnvelopeSentHeader> CREATOR = new Creator<RedEnvelopeSentHeader>() {
        @Override
        public RedEnvelopeSentHeader createFromParcel(Parcel in) {
            return new RedEnvelopeSentHeader(in);
        }

        @Override
        public RedEnvelopeSentHeader[] newArray(int size) {
            return new RedEnvelopeSentHeader[size];
        }
    };

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeInt(size);
        dest.writeString(senderMessage);
        dest.writeTypedList(txDetailList);
    }

    /**
     *
     * @return
     * The size
     */
    public int getSize() {
        return size;
    }

    /**
     *
     * @param size
     * The size
     */
    public void setSize(int size) {
        this.size = size;
    }

    /**
     *
     * @return
     * The senderMessage
     */
    public String getSenderMessage() {
        return senderMessage;
    }

    /**
     *
     * @param senderMessage
     * The senderMessage
     */
    public void setSenderMessage(String senderMessage) {
        this.senderMessage = senderMessage;
    }

    public String getMergedName(){
        String text = "";
        ArrayList<RedEnvelopeSentDetail> list = getTxDetailList();
        if(list != null && list.size() > 0){
            text = list.get(0).getName();
            for(int i = 1; i < list.size(); i++){
                text += ", " + list.get(i).getName();
            }
            if(list.size() > 1) {
                text += "(" + getSize() + ")";
            }
        }
        return text;
    }
}
