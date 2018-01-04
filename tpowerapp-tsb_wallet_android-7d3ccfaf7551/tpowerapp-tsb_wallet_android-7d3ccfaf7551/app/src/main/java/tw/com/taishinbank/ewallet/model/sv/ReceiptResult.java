package tw.com.taishinbank.ewallet.model.sv;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

/**
 * 發送付款要求的結果
 */
public class ReceiptResult implements Parcelable {

    private String result;
    private String message;
    private double amount;
    private String txfSeq;
    private String createDate;
    private List<TxResult> txResult = new ArrayList<>();

    /**
     *
     * @return
     * The result
     */
    public String getResult() {
        return result;
    }

    /**
     *
     * @param result
     * The result
     */
    public void setResult(String result) {
        this.result = result;
    }

    /**
     *
     * @return
     * The message
     */
    public String getMessage() {
        return message;
    }

    /**
     *
     * @param message
     * The message
     */
    public void setMessage(String message) {
        this.message = message;
    }

    /**
     *
     * @return
     * The amount
     */
    public double getAmount() {
        return amount;
    }

    /**
     *
     * @param amount
     * The amount
     */
    public void setAmount(double amount) {
        this.amount = amount;
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
     * The txResult
     */
    public List<TxResult> getTxResult() {
        return txResult;
    }

    /**
     *
     * @param txResult
     * The txResult
     */
    public void setTxResult(List<TxResult> txResult) {
        this.txResult = txResult;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(result);
        dest.writeString(message);
        dest.writeDouble(amount);
        dest.writeString(txfSeq);
        dest.writeString(createDate);
    }

    protected ReceiptResult(Parcel in) {
        result = in.readString();
        message = in.readString();
        amount = in.readDouble();
        txfSeq = in.readString();
        createDate = in.readString();
    }

    public static final Creator<ReceiptResult> CREATOR = new Creator<ReceiptResult>() {
        @Override
        public ReceiptResult createFromParcel(Parcel in) {
            return new ReceiptResult(in);
        }

        @Override
        public ReceiptResult[] newArray(int size) {
            return new ReceiptResult[size];
        }
    };

}

