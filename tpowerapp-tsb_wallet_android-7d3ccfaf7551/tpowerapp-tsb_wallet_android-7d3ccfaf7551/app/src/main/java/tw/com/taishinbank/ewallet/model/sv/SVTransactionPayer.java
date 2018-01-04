package tw.com.taishinbank.ewallet.model.sv;

/**
 * Data Model
 *
 * Created by oster on 2016/1/14.
 */
public class SVTransactionPayer {

    // 付款人(稱呼)
    private String name;

    // 付款人(system ID)
    private String memNO;

    // 金額
    private String amount;

    // 回覆訊息
    private String replyMessage;

    // 交易時間
    private String createDate;
    // 回覆時間
    private String replyTime;
    // 交易明細ID
    private String txfdSeq;


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMemNO() {
        return memNO;
    }

    public void setMemNO(String memNO) {
        this.memNO = memNO;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getReplyMessage() {
        return replyMessage;
    }

    public void setReplyMessage(String replyMessage) {
        this.replyMessage = replyMessage;
    }

    public String getCreateDate() {
        return createDate;
    }

    public void setCreateDate(String createDate) {
        this.createDate = createDate;
    }

    public String getReplyTime() {
        return replyTime;
    }

    public void setReplyTime(String replyTime) {
        this.replyTime = replyTime;
    }

    public String getTxfdSeq() {
        return txfdSeq;
    }

    public void setTxfdSeq(String txfdSeq) {
        this.txfdSeq = txfdSeq;
    }
}
