package tw.com.taishinbank.ewallet.model.red;

public class RedEnvelopeHomeListData {
    // 發出者稱呼
    private String sender;
    // 紅包金額
    private String amount;
    // 收到紅包的時間
    private String createDate;
    // 發出者的訊息
    private String senderMessage;
    // 交易明細序號
    private String txfdSeq;
    // 交易序號
    private String txfSeq;

    // 回覆訊息
    private String replyMessage;
    // 回覆訊息時間(YYYYMMDDHHMISS)
    private String replyTime;
    // 最後更新(YYYYMMDDHHMISS)，倒序
    private String lastUpdate;
    // 發出者memSeq
    private String senderMem;
    // 收到者memSeq
    private String toMem;
    // 收到者稱呼(回覆訊息的人)
    private String toMemName;
    // 1:一般紅包 2:財神紅包
    private String txType;

    // 是否未讀取
    private String readFlag = "0";

    /**
     * @return 發出者稱呼
     */
    public String getSender() {
        return sender;
    }

    /**
     * @param sender 發出者稱呼
     */
    public void setSender(String sender) {
        this.sender = sender;
    }

    /**
     * @return 紅包金額
     */
    public String getAmount() {
        return amount;
    }

    /**
     * @param amount 紅包金額
     */
    public void setAmount(String amount) {
        this.amount = amount;
    }

    /**
     * @return 收到紅包的時間
     */
    public String getCreateDate() {
        return createDate;
    }

    /**
     * @param createDate 收到紅包的時間
     */
    public void setCreateDate(String createDate) {
        this.createDate = createDate;
    }

    /**
     * @return 發出者的訊息
     */
    public String getSenderMessage() {
        return senderMessage;
    }

    /**
     * @param senderMessage 發出者的訊息
     */
    public void setSenderMessage(String senderMessage) {
        this.senderMessage = senderMessage;
    }

    /**
     * @return 交易明細序號
     */
    public String getTxfdSeq() {
        return txfdSeq;
    }

    /**
     * @param txfdSeq 交易明細序號
     */
    public void setTxfdSeq(String txfdSeq) {
        this.txfdSeq = txfdSeq;
    }

    /**
     * @return 交易序號
     */
    public String getTxfSeq() {
        return txfSeq;
    }

    /**
     * @param txfSeq 交易序號
     */
    public void setTxfSeq(String txfSeq) {
        this.txfSeq = txfSeq;
    }

    public String getReplyMessage() {
        return replyMessage;
    }

    public void setReplyMessage(String replyMessage) {
        this.replyMessage = replyMessage;
    }

    public String getReplyTime() {
        return replyTime;
    }

    public void setReplyTime(String replyTime) {
        this.replyTime = replyTime;
    }

    public String getLastUpdate() {
        return lastUpdate;
    }

    public void setLastUpdate(String lastUpdate) {
        this.lastUpdate = lastUpdate;
    }

    public String getSenderMem() {
        return senderMem;
    }

    public void setSenderMem(String senderMem) {
        this.senderMem = senderMem;
    }

    public String getToMem() {
        return toMem;
    }

    public void setToMem(String toMem) {
        this.toMem = toMem;
    }

    public String getToMemName() {
        return toMemName;
    }

    public void setToMemName(String toMemName) {
        this.toMemName = toMemName;
    }

    public String getTxType() {
        return txType;
    }

    public void setTxType(String txType) {
        this.txType = txType;
    }

    public String getReadFlag() {
        return readFlag;
    }

    public void setReadFlag(String readFlag) {
        this.readFlag = readFlag;
    }
}
