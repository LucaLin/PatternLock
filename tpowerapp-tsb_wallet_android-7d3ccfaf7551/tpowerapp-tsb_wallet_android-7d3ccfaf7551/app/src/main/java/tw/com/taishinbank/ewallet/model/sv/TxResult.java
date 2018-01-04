package tw.com.taishinbank.ewallet.model.sv;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * 付款要求的發送結果（逐筆）
 */
public class TxResult implements Parcelable{

    private double perAmount;
    private String name;
    private int txMemNO;
    private long txfdSeq;

    /**
     *
     * @return
     * The perAmount
     */
    public double getPerAmount() {
        return perAmount;
    }

    /**
     *
     * @param perAmount
     * The perAmount
     */
    public void setPerAmount(double perAmount) {
        this.perAmount = perAmount;
    }

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
     * The txMemNO
     */
    public int getTxMemNO() {
        return txMemNO;
    }

    /**
     *
     * @param txMemNO
     * The txMemNO
     */
    public void setTxMemNO(int txMemNO) {
        this.txMemNO = txMemNO;
    }

    /**
     *
     * @return
     * The txfdSeq
     */
    public long getTxfdSeq() {
        return txfdSeq;
    }

    /**
     *
     * @param txfdSeq
     * The txfdSeq
     */
    public void setTxfdSeq(long txfdSeq) {
        this.txfdSeq = txfdSeq;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeDouble(perAmount);
        dest.writeString(name);
        dest.writeInt(txMemNO);
        dest.writeLong(txfdSeq);
    }

    protected TxResult(Parcel in) {
        perAmount = in.readDouble();
        name = in.readString();
        txMemNO = in.readInt();
        txfdSeq = in.readLong();
    }

    public static final Creator<TxResult> CREATOR = new Creator<TxResult>() {
        @Override
        public TxResult createFromParcel(Parcel in) {
            return new TxResult(in);
        }

        @Override
        public TxResult[] newArray(int size) {
            return new TxResult[size];
        }
    };

}
