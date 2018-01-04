package tw.com.taishinbank.ewallet.model.sv;

/**
 * 付款請求的確認付款結果
 */
public class ReceiveRequestPaymentResult {
    private String result;           // Y: 轉帳成功 N: 轉帳失敗
    private String bancsMsg;         // 轉帳訊息
    private int amount;              // 轉帳金額
    private String sender;           // 轉出者稱呼
    private int senderSeq;           // 轉出者memSeq
    private int balance;             // 儲值帳戶餘額
    private String createDate;       // 交易時間(YYYYMMDDHHMISS)
    private int txfSeq;              // 交易序號
    private int txfdSeq;             // 交易明細序號
    private long toAccount;          // 帳號(轉入帳號)
    private int toMemNO;             // 收到者memSeq
    private String name;             // 收到者稱呼

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
     * The bancsMsg
     */
    public String getBancsMsg() {
        return bancsMsg;
    }

    /**
     *
     * @param bancsMsg
     * The bancsMsg
     */
    public void setBancsMsg(String bancsMsg) {
        this.bancsMsg = bancsMsg;
    }

    /**
     *
     * @return
     * The amount
     */
    public int getAmount() {
        return amount;
    }

    /**
     *
     * @param amount
     * The amount
     */
    public void setAmount(int amount) {
        this.amount = amount;
    }

    /**
     *
     * @return
     * The sender
     */
    public String getSender() {
        return sender;
    }

    /**
     *
     * @param sender
     * The sender
     */
    public void setSender(String sender) {
        this.sender = sender;
    }

    /**
     *
     * @return
     * The senderSeq
     */
    public int getSenderSeq() {
        return senderSeq;
    }

    /**
     *
     * @param senderSeq
     * The senderSeq
     */
    public void setSenderSeq(int senderSeq) {
        this.senderSeq = senderSeq;
    }

    /**
     *
     * @return
     * The balance
     */
    public int getBalance() {
        return balance;
    }

    /**
     *
     * @param balance
     * The balance
     */
    public void setBalance(int balance) {
        this.balance = balance;
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
     * The toAccount
     */
    public long getToAccount() {
        return toAccount;
    }

    /**
     *
     * @param toAccount
     * The toAccount
     */
    public void setToAccount(long toAccount) {
        this.toAccount = toAccount;
    }

    /**
     *
     * @return
     * The toMemNO
     */
    public int getToMemNO() {
        return toMemNO;
    }

    /**
     *
     * @param toMemNO
     * The toMemNO
     */
    public void setToMemNO(int toMemNO) {
        this.toMemNO = toMemNO;
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

}
