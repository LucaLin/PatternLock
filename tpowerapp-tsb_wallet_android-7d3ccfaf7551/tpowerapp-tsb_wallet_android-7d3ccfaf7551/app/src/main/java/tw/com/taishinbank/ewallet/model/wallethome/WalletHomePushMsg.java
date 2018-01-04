package tw.com.taishinbank.ewallet.model.wallethome;


public class WalletHomePushMsg {

    private int pushSender;
    private String senderNickName;
    private String pushMessage;
    private String senderMessage;
    private String pushDate;
    private double amount;
    private String couponTitle;
    private int recepient;
    private String countType;
    private String pushType;
    private String readFlag;
    private String createDate;
    private String url;

    /**
     *
     * @return
     * The pushSender
     */
    public int getPushSender() {
        return pushSender;
    }

    /**
     *
     * @param pushSender
     * The pushSender
     */
    public void setPushSender(int pushSender) {
        this.pushSender = pushSender;
    }

    /**
     *
     * @return
     * The senderNickName
     */
    public String getSenderNickName() {
        return senderNickName;
    }

    /**
     *
     * @param senderNickName
     * The senderNickName
     */
    public void setSenderNickName(String senderNickName) {
        this.senderNickName = senderNickName;
    }

    /**
     *
     * @return
     * The pushMessage
     */
    public String getPushMessage() {
        return pushMessage;
    }

    /**
     *
     * @param pushMessage
     * The pushMessage
     */
    public void setPushMessage(String pushMessage) {
        this.pushMessage = pushMessage;
    }

    /**
     *
     * @return
     * The senderMessage
     */
    public String getSenderMessage() {
        return senderMessage;
    }

    /**
     *
     * @param senderMessage
     * The senderMessage
     */
    public void setSenderMessage(String senderMessage) {
        this.senderMessage = senderMessage;
    }

    /**
     *
     * @return
     * The pushDate
     */
    public String getPushDate() {
        return pushDate;
    }

    /**
     *
     * @param pushDate
     * The pushDate
     */
    public void setPushDate(String pushDate) {
        this.pushDate = pushDate;
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
     * The couponTitle
     */
    public String getCouponTitle() {
        return couponTitle;
    }

    /**
     *
     * @param couponTitle
     * The couponTitle
     */
    public void setCouponTitle(String couponTitle) {
        this.couponTitle = couponTitle;
    }

    /**
     *
     * @return
     * The recepient
     */
    public int getRecepient() {
        return recepient;
    }

    /**
     *
     * @param recepient
     * The recepient
     */
    public void setRecepient(int recepient) {
        this.recepient = recepient;
    }

    /**
     *
     * @return
     * The countType
     */
    public String getCountType() {
        return countType;
    }

    /**
     *
     * @param countType
     * The countType
     */
    public void setCountType(String countType) {
        this.countType = countType;
    }


    public String getPushType() {
        return pushType;
    }

    public void setPushType(String pushType) {
        this.pushType = pushType;
    }


    public boolean hasRead(){
        if(readFlag == null){
            return true;
        }
        return readFlag.equals("1");
    }


    /**
     *
     * @return
     * The readFlag
     */
    public String getReadFlag() {
        return readFlag;
    }

    /**
     *
     * @param readFlag
     * The readFlag
     */
    public void setReadFlag(String readFlag) {
        this.readFlag = readFlag;
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
     * The url
     */
    public String getUrl() {
        return url;
    }

    /**
     *
     * @param url
     * The url
     */
    public void setUrl(String url) {
        this.url = url;
    }
}
