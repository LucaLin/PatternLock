package tw.com.taishinbank.ewallet.model.sv;

import android.os.Parcel;
import android.os.Parcelable;

public class DepositWithdrawResult implements Parcelable{

    // Y: 提領/加值成功
    // N: 提領/加值失敗
    private String result;
    // 提領/加值金額
    private double amount;
    // 提領/加值後帳戶餘額
    private double balance;
    // 手續費
    private int fee;
    // SV交易時間
    private String txTime;
    // 交易明細序號
    private int txfdSeq;
    // 交易序號
    private int txfSeq;
    // 當result=N時，帶入SV rtnMsg
    private String rtnMsg;
    // 提領後本月交易額度
    private int monthlyCurr;

    public boolean isSuccess(){
        if(result != null) {
            return result.equalsIgnoreCase("Y");
        }
        return false;
    }

    public DepositWithdrawResult(){

    }

    protected DepositWithdrawResult(Parcel in) {
        result = in.readString();
        amount = in.readDouble();
        balance = in.readDouble();
        fee = in.readInt();
        txTime = in.readString();
        txfdSeq = in.readInt();
        txfSeq = in.readInt();
        rtnMsg = in.readString();
        monthlyCurr = in.readInt();
    }

    public static final Creator<DepositWithdrawResult> CREATOR = new Creator<DepositWithdrawResult>() {
        @Override
        public DepositWithdrawResult createFromParcel(Parcel in) {
            return new DepositWithdrawResult(in);
        }

        @Override
        public DepositWithdrawResult[] newArray(int size) {
            return new DepositWithdrawResult[size];
        }
    };

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
     * The balance
     */
    public double getBalance() {
        return balance;
    }

    /**
     *
     * @param balance
     * The balance
     */
    public void setBalance(double balance) {
        this.balance = balance;
    }

    /**
     *
     * @return
     * The fee
     */
    public int getFee() {
        return fee;
    }

    /**
     *
     * @param fee
     * The fee
     */
    public void setFee(int fee) {
        this.fee = fee;
    }

    /**
     *
     * @return
     * The txTime
     */
    public String getTxTime() {
        return txTime;
    }

    /**
     *
     * @param txTime
     * The txTime
     */
    public void setTxTime(String txTime) {
        this.txTime = txTime;
    }

    /**
     *
     * @return
     * The txfdSeq
     */
    public int getTxfdSeq() {
        return txfdSeq;
    }

    /**
     *
     * @param txfdSeq
     * The txfdSeq
     */
    public void setTxfdSeq(int txfdSeq) {
        this.txfdSeq = txfdSeq;
    }

    /**
     *
     * @return
     * The txfSeq
     */
    public int getTxfSeq() {
        return txfSeq;
    }

    /**
     *
     * @param txfSeq
     * The txfSeq
     */
    public void setTxfSeq(int txfSeq) {
        this.txfSeq = txfSeq;
    }

    /**
     *
     * @return
     * The rtnMsg
     */
    public String getRtnMsg() {
        return rtnMsg;
    }

    /**
     *
     * @param rtnMsg
     * The rtnMsg
     */
    public void setRtnMsg(String rtnMsg) {
        this.rtnMsg = rtnMsg;
    }

    /**
     *
     * @return
     * The monthlyCurr
     */
    public int getMonthlyCurr() {
        return monthlyCurr;
    }

    /**
     *
     * @param monthlyCurr
     * The monthlyCurr
     */
    public void setMonthlyCurr(int monthlyCurr) {
        this.monthlyCurr = monthlyCurr;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(result);
        dest.writeDouble(amount);
        dest.writeDouble(balance);
        dest.writeInt(fee);
        dest.writeString(txTime);
        dest.writeInt(txfdSeq);
        dest.writeInt(txfSeq);
        dest.writeString(rtnMsg);
        dest.writeInt(monthlyCurr);
    }
}
