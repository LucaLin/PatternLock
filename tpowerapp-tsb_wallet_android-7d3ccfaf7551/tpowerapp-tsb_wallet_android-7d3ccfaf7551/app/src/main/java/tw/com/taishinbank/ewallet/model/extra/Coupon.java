package tw.com.taishinbank.ewallet.model.extra;

import android.os.Parcel;
import android.os.Parcelable;

public class Coupon implements Parcelable {

    private    int cpSeq             ;//優惠券序號
    private    int msmSeq            ;//會員/優惠券序號mapping序號

    private String status            ;//序號狀態(1:優惠活動 2:好友贈送) (3:已轉送4:已兌換)
    private String lastUpdate        ;//最後更新(YYYYMMDDHHMISS)，倒序
    private String readFlag          ;//讀取狀態(0:未讀1:已讀)(0要秀紅點)

    private String title             ;//優惠券主標
    private String subTitle          ;//優惠券副標

    private String imagePathS;//        優惠券圖片路徑(小)
    private String imagePathM;//        優惠券圖片路徑(中)
    private String imagePathL;//        優惠券圖片路徑(大)

    private String content;
    private String notes;

    private String createDate;//優惠券領取時間(YYYYMMDDHHMISS)
    private String startDate         ;//使用時間起日(YYYYMMDDHHMISS)
    private String endDate           ;//使用時間迄日(YYYYMMDDHHMISS)
    private String branchFlag;//        String 是否有分店(Y:有N:無)(Y要秀箭頭)
    private String serialDisplayType ;//序號顯示類型
    private String onLineDate        ;//上線日期(YYYYMMDDHHMISS)
    private String downLineDate      ;//下線日期(YYYYMMDDHHMISS)
    private String storeName         ;//店家名稱
    private String storeAddress      ;//店家地址
    private String storePhone        ;//店家電話

//    private String storeInfo;

    private String senderMemNO;
    private String senderNickName;
    private String senderMessage;
    private String senderDate;

    private String toMemNO;
    private String toMemNickName;
    private String replyMessage;
    private String replyDate;

    private String exchangeDate;    //優惠券兌換時間(YYYYMMDDHHMISS)
    private String serialNO;        //優惠券序號(看serialDisplayType要以哪種形式顯示)

    private int taSeq;                  //票券商系統編號，用來下載圖檔
    private String ticketAgencyPhone;   //票券商電話
    private String ticketAgencyAddress; //票券商地址
    private String iconUpdateTime;      //票券商圖片更新時間

    public Coupon() {
    }

    // -----
    // Parcelable
    // -----
    protected Coupon(Parcel in) {
        cpSeq = in.readInt();
        msmSeq = in.readInt();
        status = in.readString();
        lastUpdate = in.readString();
        readFlag = in.readString();
        title = in.readString();
        subTitle = in.readString();
        imagePathS = in.readString();
        imagePathM = in.readString();
        imagePathL = in.readString();
        content = in.readString();
        notes = in.readString();
        createDate = in.readString();
        startDate = in.readString();
        endDate = in.readString();
        branchFlag = in.readString();
        serialDisplayType = in.readString();
        onLineDate = in.readString();
        downLineDate = in.readString();
        storeName = in.readString();
        storeAddress = in.readString();
        storePhone = in.readString();
        senderMemNO = in.readString();
        senderNickName = in.readString();
        senderMessage = in.readString();
        senderDate = in.readString();
        toMemNO = in.readString();
        toMemNickName = in.readString();
        replyMessage = in.readString();
        replyDate = in.readString();
        exchangeDate = in.readString();
        serialNO = in.readString();
        taSeq = in.readInt();
        ticketAgencyPhone = in.readString();
        ticketAgencyAddress = in.readString();
        iconUpdateTime = in.readString();
    }

    public static final Creator<Coupon> CREATOR = new Creator<Coupon>() {
        @Override
        public Coupon createFromParcel(Parcel in) {
            return new Coupon(in);
        }

        @Override
        public Coupon[] newArray(int size) {
            return new Coupon[size];
        }
    };

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSubTitle() {
        return subTitle;
    }

    public void setSubTitle(String subTitle) {
        this.subTitle = subTitle;
    }

    public String getCreateDate() {
        return createDate;
    }

    public void setCreateDate(String createDate) {
        this.createDate = createDate;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getStoreInfo() {
        return storeName + " " + storeAddress + " " + storePhone;
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

    public String getSenderNickName() {
        return senderNickName;
    }

    public void setSenderNickName(String senderNickName) {
        this.senderNickName = senderNickName;
    }

    public String getSenderDate() {
        return senderDate;
    }

    public void setSenderDate(String senderDate) {
        this.senderDate = senderDate;
    }

    public String getToMemNickName() {
        return toMemNickName;
    }

    public void setToMemNickName(String toMemNickName) {
        this.toMemNickName = toMemNickName;
    }

    public String getReplyDate() {
        return replyDate;
    }

    public void setReplyDate(String replyDate) {
        this.replyDate = replyDate;
    }

    public String getExchangeDate() {
        return exchangeDate;
    }

    public void setExchangeDate(String exchangeDate) {
        this.exchangeDate = exchangeDate;
    }

    public String getSerialNO() {
        return serialNO;
    }

    public void setSerialNO(String serialNO) {
        this.serialNO = serialNO;
    }

    public int getCpSeq() {
        return cpSeq;
    }

    public void setCpSeq(int cpSeq) {
        this.cpSeq = cpSeq;
    }

    public int getMsmSeq() {
        return msmSeq;
    }

    public void setMsmSeq(int msmSeq) {
        this.msmSeq = msmSeq;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getLastUpdate() {
        return lastUpdate;
    }

    public void setLastUpdate(String lastUpdate) {
        this.lastUpdate = lastUpdate;
    }

    public String getReadFlag() {
        return readFlag;
    }

    public void setReadFlag(String readFlag) {
        this.readFlag = readFlag;
    }

    public String getImagePathS() {
        return imagePathS;
    }

    public void setImagePathS(String imagePathS) {
        this.imagePathS = imagePathS;
    }

    public String getImagePathM() {
        return imagePathM;
    }

    public void setImagePathM(String imagePathM) {
        this.imagePathM = imagePathM;
    }

    public String getImagePathL() {
        return imagePathL;
    }

    public void setImagePathL(String imagePathL) {
        this.imagePathL = imagePathL;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public String getBranchFlag() {
        return branchFlag;
    }

    public void setBranchFlag(String branchFlag) {
        this.branchFlag = branchFlag;
    }

    public String getSerialDisplayType() {
        return serialDisplayType;
    }

    public void setSerialDisplayType(String serialDisplayType) {
        this.serialDisplayType = serialDisplayType;
    }

    public String getOnLineDate() {
        return onLineDate;
    }

    public void setOnLineDate(String onLineDate) {
        this.onLineDate = onLineDate;
    }

    public String getDownLineDate() {
        return downLineDate;
    }

    public void setDownLineDate(String downLineDate) {
        this.downLineDate = downLineDate;
    }

    public String getStoreName() {
        return storeName;
    }

    public void setStoreName(String storeName) {
        this.storeName = storeName;
    }

    public String getStoreAddress() {
        return storeAddress;
    }

    public void setStoreAddress(String storeAddress) {
        this.storeAddress = storeAddress;
    }

    public String getStorePhone() {
        return storePhone;
    }

    public void setStorePhone(String storePhone) {
        this.storePhone = storePhone;
    }

    public String getSenderMemNO() {
        return senderMemNO;
    }

    public void setSenderMemNO(String senderMemNO) {
        this.senderMemNO = senderMemNO;
    }

    public String getToMemNO() {
        return toMemNO;
    }

    public void setToMemNO(String toMemNO) {
        this.toMemNO = toMemNO;
    }

    public int getTaSeq() {
        return taSeq;
    }

    public void setTaSeq(int taSeq) {
        this.taSeq = taSeq;
    }

    public String getTicketAgencyPhone() {
        return ticketAgencyPhone;
    }

    public void setTicketAgencyPhone(String ticketAgencyPhone) {
        this.ticketAgencyPhone = ticketAgencyPhone;
    }

    public String getTicketAgencyAddress() {
        return ticketAgencyAddress;
    }

    public void setTicketAgencyAddress(String ticketAgencyAddress) {
        this.ticketAgencyAddress = ticketAgencyAddress;
    }

    public String getIconUpdateTime() {
        return iconUpdateTime;
    }

    public void setIconUpdateTime(String iconUpdateTime) {
        this.iconUpdateTime = iconUpdateTime;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(cpSeq);
        dest.writeInt(msmSeq);
        dest.writeString(status);
        dest.writeString(lastUpdate);
        dest.writeString(readFlag);
        dest.writeString(title);
        dest.writeString(subTitle);
        dest.writeString(imagePathS);
        dest.writeString(imagePathM);
        dest.writeString(imagePathL);
        dest.writeString(content);
        dest.writeString(notes);
        dest.writeString(createDate);
        dest.writeString(startDate);
        dest.writeString(endDate);
        dest.writeString(branchFlag);
        dest.writeString(serialDisplayType);
        dest.writeString(onLineDate);
        dest.writeString(downLineDate);
        dest.writeString(storeName);
        dest.writeString(storeAddress);
        dest.writeString(storePhone);
        dest.writeString(senderMemNO);
        dest.writeString(senderNickName);
        dest.writeString(senderMessage);
        dest.writeString(senderDate);
        dest.writeString(toMemNO);
        dest.writeString(toMemNickName);
        dest.writeString(replyMessage);
        dest.writeString(replyDate);
        dest.writeString(exchangeDate);
        dest.writeString(serialNO);
        dest.writeInt(taSeq);
        dest.writeString(ticketAgencyPhone);
        dest.writeString(ticketAgencyAddress);
        dest.writeString(iconUpdateTime);
    }
}
