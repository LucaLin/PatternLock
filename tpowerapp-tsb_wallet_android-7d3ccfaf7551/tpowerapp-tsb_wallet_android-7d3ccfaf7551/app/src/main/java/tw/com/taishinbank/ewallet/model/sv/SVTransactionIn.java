package tw.com.taishinbank.ewallet.model.sv;

import android.os.Parcel;

/**
 * Data Model
 *
 * Created by oster on 2016/1/12.
 */
public class SVTransactionIn extends SVTransaction {

    protected int    txMemNO        ;
    protected String txMemName      ;

    public SVTransactionIn() {
        super();
    }

    /**
     * Constructor 主要留給 section seperator
     * @param pTxfSeq transaction detail id
     */
    public SVTransactionIn(int pTxfSeq) {
        super();
        this.txfSeq = pTxfSeq;
    }

    protected SVTransactionIn(Parcel in) {
        super(in);
        txMemNO = in.readInt();
        txMemName = in.readString();
    }

    public int getTxMemNO() {
        return txMemNO;
    }

    public void setTxMemNO(int txMemNO) {
        this.txMemNO = txMemNO;
    }

    public String getTxMemName() {
        return txMemName;
    }

    public void setTxMemName(String txMemName) {
        this.txMemName = txMemName;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeInt(txMemNO);
        dest.writeString(txMemName);
    }


    public static final Creator<SVTransactionIn> CREATOR = new Creator<SVTransactionIn>() {
        @Override
        public SVTransactionIn createFromParcel(Parcel in) {
            return new SVTransactionIn(in);
        }

        @Override
        public SVTransactionIn[] newArray(int size) {
            return new SVTransactionIn[size];
        }
    };
}
