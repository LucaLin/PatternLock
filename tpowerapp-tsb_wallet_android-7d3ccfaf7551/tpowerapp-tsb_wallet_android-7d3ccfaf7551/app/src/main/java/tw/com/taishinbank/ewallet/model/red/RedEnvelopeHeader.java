package tw.com.taishinbank.ewallet.model.red;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

public class RedEnvelopeHeader<T> implements Parcelable {

    // 總金額
    private String amount;
    // 交易時間
    private String createDate;
    // 交易序號
    private String txfSeq;

    protected ArrayList<T> txDetailList = new ArrayList<>();

    protected RedEnvelopeHeader(Parcel in) {
        amount = in.readString();
        createDate = in.readString();
        txfSeq = in.readString();
    }

    public static final Creator<RedEnvelopeHeader> CREATOR = new Creator<RedEnvelopeHeader>() {
        @Override
        public RedEnvelopeHeader createFromParcel(Parcel in) {
            return new RedEnvelopeHeader(in);
        }

        @Override
        public RedEnvelopeHeader[] newArray(int size) {
            return new RedEnvelopeHeader[size];
        }
    };

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
     * The txfSeq
     */
    public String getTxfSeq() {
        return txfSeq;
    }

    /**
     *
     * @param txfSeq
     * The txfSeq
     */
    public void setTxfSeq(String txfSeq) {
        this.txfSeq = txfSeq;
    }


    /**
     *
     * @return
     * The txDetailList
     */
    public ArrayList<T> getTxDetailList() {
        return txDetailList;
    }

    /**
     *
     * @param txDetailList
     * The txDetailList
     */
    public void setTxDetailList(ArrayList<T> txDetailList) {
        this.txDetailList = txDetailList;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(amount);
        dest.writeString(createDate);
        dest.writeString(txfSeq);
    }
}
