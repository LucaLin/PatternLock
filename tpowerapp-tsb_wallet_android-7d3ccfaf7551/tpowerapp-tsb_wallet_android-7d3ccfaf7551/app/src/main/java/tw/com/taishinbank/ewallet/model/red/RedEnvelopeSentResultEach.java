package tw.com.taishinbank.ewallet.model.red;

import android.os.Parcel;
import android.os.Parcelable;

public class RedEnvelopeSentResultEach implements Parcelable {
    // (double)金額
    String perAmount;
    // 稱呼
    String name;
    // Y: 轉帳成功 N: 轉帳失敗
    String result;
    // 轉帳訊息
    String bancsMsg;
    // (int)交易明細序號
    String txfdSeq;
    // (long)帳號 (轉入帳號)
    String account;
    // 收到者memSeq
    String toMem;

    public RedEnvelopeSentResultEach(){

    }

    protected RedEnvelopeSentResultEach(Parcel in) {
        perAmount = in.readString();
        name = in.readString();
        result = in.readString();
        bancsMsg = in.readString();
        txfdSeq = in.readString();
        account = in.readString();
        toMem = in.readString();
    }

    public static final Creator<RedEnvelopeSentResultEach> CREATOR = new Creator<RedEnvelopeSentResultEach>() {
        @Override
        public RedEnvelopeSentResultEach createFromParcel(Parcel in) {
            return new RedEnvelopeSentResultEach(in);
        }

        @Override
        public RedEnvelopeSentResultEach[] newArray(int size) {
            return new RedEnvelopeSentResultEach[size];
        }
    };

    public String getToMem() {
        return toMem;
    }

    public void setToMem(String toMem) {
        this.toMem = toMem;
    }

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public String getPerAmount() {
        return perAmount;
    }

    public void setPerAmount(String perAmount) {
        this.perAmount = perAmount;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public String getBancsMsg() {
        return bancsMsg;
    }

    public void setBancsMsg(String bancsMsg) {
        this.bancsMsg = bancsMsg;
    }

    public String getTxfdSeq() {
        return txfdSeq;
    }

    public void setTxfdSeq(String txfdSeq) {
        this.txfdSeq = txfdSeq;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(perAmount);
        dest.writeString(name);
        dest.writeString(result);
        dest.writeString(bancsMsg);
        dest.writeString(txfdSeq);
        dest.writeString(account);
        dest.writeString(toMem);
    }
}
