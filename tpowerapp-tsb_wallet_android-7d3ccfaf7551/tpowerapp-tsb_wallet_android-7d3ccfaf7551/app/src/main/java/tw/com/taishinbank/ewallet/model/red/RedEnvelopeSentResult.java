package tw.com.taishinbank.ewallet.model.red;

import android.os.Parcel;
import android.os.Parcelable;


public class RedEnvelopeSentResult implements Parcelable{

    // (double)總金額
    String amount;
    // (long)帳號 (VS API 無轉帳帳號 11/25)，app自己從SVAccountInfo加入
    String account;
    // 轉出者稱呼
    String sender;
    // (double)儲值帳戶餘額
    String balance;
    // 交易時間(YYYYMMDDHHMISS)
    String createDate;
    // (int)交易序號
    String txfSeq;
    // (int)發出者memSeq
    String senderMem;
    // 每一筆交易結果
    RedEnvelopeSentResultEach[] txResult;

    public RedEnvelopeSentResult(){

    }

    protected RedEnvelopeSentResult(Parcel in) {
        amount = in.readString();
        account = in.readString();
        sender = in.readString();
        balance = in.readString();
        createDate = in.readString();
        txfSeq = in.readString();
        senderMem = in.readString();
        txResult = in.createTypedArray(RedEnvelopeSentResultEach.CREATOR);
    }

    public static final Creator<RedEnvelopeSentResult> CREATOR = new Creator<RedEnvelopeSentResult>() {
        @Override
        public RedEnvelopeSentResult createFromParcel(Parcel in) {
            return new RedEnvelopeSentResult(in);
        }

        @Override
        public RedEnvelopeSentResult[] newArray(int size) {
            return new RedEnvelopeSentResult[size];
        }
    };

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getBalance() {
        return balance;
    }

    public void setBalance(String balance) {
        this.balance = balance;
    }

    public String getCreateDate() {
        return createDate;
    }

    public void setCreateDate(String createDate) {
        this.createDate = createDate;
    }

    public String getTxfSeq() {
        return txfSeq;
    }

    public void setTxfSeq(String txfSeq) {
        this.txfSeq = txfSeq;
    }

    public RedEnvelopeSentResultEach[] getTxResult() {
        return txResult;
    }

    public void setTxResult(RedEnvelopeSentResultEach[] txResult) {
        this.txResult = txResult;
    }


    public String getSenderMem() {
        return senderMem;
    }

    public void setSenderMem(String senderMem) {
        this.senderMem = senderMem;
    }
    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(amount);
        dest.writeString(account);
        dest.writeString(sender);
        dest.writeString(balance);
        dest.writeString(createDate);
        dest.writeString(txfSeq);
        dest.writeString(senderMem);
        dest.writeTypedArray(txResult, flags);
    }
}
