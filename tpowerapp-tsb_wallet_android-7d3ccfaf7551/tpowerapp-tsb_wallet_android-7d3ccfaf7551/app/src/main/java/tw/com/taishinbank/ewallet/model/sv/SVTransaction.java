package tw.com.taishinbank.ewallet.model.sv;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Data Model
 *
 * Created by oster on 2016/1/14.
 */
public class SVTransaction  implements Parcelable {

    /**
     * 交易類型 5:轉帳付款 6:轉帳收款(邀請付款) 7.均分收款(邀請付款) 9:提領
     */
    /**
     * 5:轉帳付款6:轉帳收款(邀請付款) 7.均分收款(邀請付款) 8:加值
     */
    protected String txType         ;
    /**
     * txType=6 or 7 需一併帶入此狀態，此狀態來自
     * WLT_TX_transfer_detail
     *  0:已交易
     *  1:尚未交易
     *  2.交易取消
     */
    protected String txStatus       ;
    protected String amount         ;
    protected String createDate     ;
//    protected int    txMemNO        ;
//    protected String txMemName      ;
    /**
     * Transaction ID -
     *  another purpose is to used as separator if txfSeq < 0
     */
    protected int    txfSeq         ;
    protected int    txfdSeq        ;
    protected String senderMessage  ;
    protected String replyMessage   ;
    protected String replyDate      ;

    protected String readFlag       ;

    public SVTransaction() {

    }

    /**
     * Constructor 主要留給 section seperator
     * @param pTxfSeq transaction detail id
     */
    public SVTransaction(int pTxfSeq) {
        this.txfSeq = pTxfSeq;
    }

    protected SVTransaction(Parcel in) {
        txType = in.readString();
        txStatus = in.readString();
        amount = in.readString();
        createDate = in.readString();
//        txMemNO = in.readInt();
//        txMemName = in.readString();
        txfSeq = in.readInt();
        txfdSeq = in.readInt();
        senderMessage = in.readString();
        replyMessage = in.readString();
        replyDate = in.readString();
        readFlag = in.readString();
    }

    public String getTxType() {
        return txType;
    }

    public void setTxType(String txType) {
        this.txType = txType;
    }

    public String getTxStatus() {
        return txStatus;
    }

    public void setTxStatus(String txStatus) {
        this.txStatus = txStatus;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getCreateDate() {
        return createDate;
    }

    public void setCreateDate(String createDate) {
        this.createDate = createDate;
    }

    public int getTxfSeq() {
        return txfSeq;
    }

    public void setTxfSeq(int txfSeq) {
        this.txfSeq = txfSeq;
    }

    public int getTxfdSeq() {
        return txfdSeq;
    }

    public void setTxfdSeq(int txfdSeq) {
        this.txfdSeq = txfdSeq;
    }

    public String getSenderMessage() {
        return senderMessage;
    }

    public void setSenderMessage(String senderMessage) {
        this.senderMessage = senderMessage;
    }

    public String getReplyMessage() {
        return replyMessage;
    }

    public void setReplyMessage(String replyMessage) {
        this.replyMessage = replyMessage;
    }

    public String getReplyDate() {
        return replyDate;
    }

    public void setReplyDate(String replyDate) {
        this.replyDate = replyDate;
    }

    @Override
    public int describeContents() {
        return 0;
    }


    public String getReadFlag() {
        return readFlag;
    }

    public void setReadFlag(String readFlag) {
        this.readFlag = readFlag;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(txType);
        dest.writeString(txStatus);
        dest.writeString(amount);
        dest.writeString(createDate);
        dest.writeInt(txfSeq);
        dest.writeInt(txfdSeq);
        dest.writeString(senderMessage);
        dest.writeString(replyMessage);
        dest.writeString(replyDate);
        dest.writeString(readFlag);
    }


    public static final Creator<SVTransaction> CREATOR = new Creator<SVTransaction>() {
        @Override
        public SVTransaction createFromParcel(Parcel in) {
            return new SVTransaction(in);
        }

        @Override
        public SVTransaction[] newArray(int size) {
            return new SVTransaction[size];
        }
    };

}
