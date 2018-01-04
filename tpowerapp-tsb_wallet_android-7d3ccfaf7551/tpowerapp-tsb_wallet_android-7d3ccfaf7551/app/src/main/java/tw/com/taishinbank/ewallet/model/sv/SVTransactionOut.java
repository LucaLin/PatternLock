package tw.com.taishinbank.ewallet.model.sv;

import android.os.Parcel;

/**
 * Data Model
 *
 * Created by oster on 2016/1/12.
 */
public class SVTransactionOut extends SVTransaction {

    protected int    txToMemNO      ;
    protected String name           ;
    protected double fee            ;

    public SVTransactionOut() {

    }

    /**
     * Constructor 主要留給 section seperator
     * @param pTxfSeq transaction detail ID
     */
    public SVTransactionOut(int pTxfSeq) {
        super(pTxfSeq);
    }

    protected SVTransactionOut(Parcel in) {
        super(in);
        txToMemNO = in.readInt();
        name = in.readString();
        fee = in.readDouble();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getTxToMemNO() {
        return txToMemNO;
    }

    public void setTxToMemNO(int txToMemNO) {
        this.txToMemNO = txToMemNO;
    }

    public double getFee() {
        return fee;
    }

    public void setFee(double fee) {
        this.fee = fee;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeInt(txToMemNO);
        dest.writeString(name);
        dest.writeDouble(fee);
    }


    public static final Creator<SVTransactionOut> CREATOR = new Creator<SVTransactionOut>() {
        @Override
        public SVTransactionOut createFromParcel(Parcel in) {
            return new SVTransactionOut(in);
        }

        @Override
        public SVTransactionOut[] newArray(int size) {
            return new SVTransactionOut[size];
        }
    };
}
