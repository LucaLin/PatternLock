package tw.com.taishinbank.ewallet.model.extra;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Blake on 3/8/16.
 */
public class TicketDetailData implements Parcelable {
    private	String	etkSeq;             //	電子票券 錢包系統ID
    private String  orderId;
    private	String	writeOffCode;       //	核銷碼
    private	String	providerId;         //	票券供應商 Id
    private	String	title;              //	標題
    private	Double	price;              //	單價
    private	String	status;             //	電子票券
    private	String	readFlag;           //	0:未讀(預設)、1:已讀
    private	String	effectiveStartDate; //	有效開始日 yyyyMMdd
    private	String	effectiveEndDate;   //	有效結束日 yyyyMMdd
    private	String	trustedStartDate;   //	信託開始日yyyyMMdd
    private	String	trustedEndDate;     //	信託結束日 yyyyMMdd
    private	String	iconUrl;            //	圖示網址
    private	String	note;               //	電子票券須知
    private	String	storeTel;           //	商家客服電話
    private	String	storeAddress;       //	商家位置
    private	String	storeName;          //	商家名稱
    private	String	buyDate;            //	購入時間 yyyyMMddHHmmss
    private	String	createDate;         //	產生日期 yyyyMMddHHmmss
    private	String	lastUpdate;         //	最後修改日期 yyyyMMddHHmmss

    private String  odrSeq;			    //訂單 錢包系統ID

    protected TicketDetailData(Parcel in) {
        etkSeq = in.readString();
        serialNo = in.readString();
        writeOffCode = in.readString();
        providerId = in.readString();
        title = in.readString();
        status = in.readString();
        readFlag = in.readString();
        effectiveStartDate = in.readString();
        effectiveEndDate = in.readString();
        trustedStartDate = in.readString();
        trustedEndDate = in.readString();
        iconUrl = in.readString();
        note = in.readString();
        storeTel = in.readString();
        storeAddress = in.readString();
        storeName = in.readString();
        buyDate = in.readString();
        createDate = in.readString();
        lastUpdate = in.readString();
        orderId = in.readString();
        odrSeq = in.readString();
    }




    public static final Creator<TicketDetailData> CREATOR = new Creator<TicketDetailData>() {
        @Override
        public TicketDetailData createFromParcel(Parcel in) {
            return new TicketDetailData(in);
        }

        @Override
        public TicketDetailData[] newArray(int size) {
            return new TicketDetailData[size];
        }
    };

    public String getEtkSeq() {
        return etkSeq;
    }

    public void setEtkSeq(String etkSeq) {
        this.etkSeq = etkSeq;
    }

    public String getSerialNo() {
        return serialNo;
    }

    public void setSerialNo(String serialNo) {
        this.serialNo = serialNo;
    }

    public String getWriteOffCode() {
        return writeOffCode;
    }

    public void setWriteOffCode(String writeOffCode) {
        this.writeOffCode = writeOffCode;
    }

    public String getProviderId() {
        return providerId;
    }

    public void setProviderId(String providerId) {
        this.providerId = providerId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getReadFlag() {
        return readFlag;
    }

    public void setReadFlag(String readFlag) {
        this.readFlag = readFlag;
    }

    public String getEffectiveStartDate() {
        return effectiveStartDate;
    }

    public void setEffectiveStartDate(String effectiveStartDate) {
        this.effectiveStartDate = effectiveStartDate;
    }

    public String getEffectiveEndDate() {
        return effectiveEndDate;
    }

    public void setEffectiveEndDate(String effectiveEndDate) {
        this.effectiveEndDate = effectiveEndDate;
    }

    public String getTrustedStartDate() {
        return trustedStartDate;
    }

    public void setTrustedStartDate(String trustedStartDate) {
        this.trustedStartDate = trustedStartDate;
    }

    public String getTrustedEndDate() {
        return trustedEndDate;
    }

    public void setTrustedEndDate(String trustedEndDate) {
        this.trustedEndDate = trustedEndDate;
    }

    public String getIconUrl() {
        return iconUrl;
    }

    public void setIconUrl(String iconUrl) {
        this.iconUrl = iconUrl;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public String getStoreTel() {
        return storeTel;
    }

    public void setStoreTel(String storeTel) {
        this.storeTel = storeTel;
    }

    public String getStoreAddress() {
        return storeAddress;
    }

    public void setStoreAddress(String storeAddress) {
        this.storeAddress = storeAddress;
    }

    public String getStoreName() {
        return storeName;
    }

    public void setStoreName(String storeName) {
        this.storeName = storeName;
    }

    public String getBuyDate() {
        return buyDate;
    }

    public void setBuyDate(String buyDate) {
        this.buyDate = buyDate;
    }

    public String getCreateDate() {
        return createDate;
    }

    public void setCreateDate(String createDate) {
        this.createDate = createDate;
    }

    public String getLastUpdate() {
        return lastUpdate;
    }

    public void setLastUpdate(String lastUpdate) {
        this.lastUpdate = lastUpdate;
    }

    private	String	serialNo;           //	電子票券序號

    public String getOrderId() {
        return orderId;
    }
    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getOdrSeq() {
        return odrSeq;
    }

    public void setOdrSeq(String odrSeq) {
        this.odrSeq = odrSeq;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(etkSeq);
        dest.writeString(serialNo);
        dest.writeString(writeOffCode);
        dest.writeString(providerId);
        dest.writeString(title);
        dest.writeString(status);
        dest.writeString(readFlag);
        dest.writeString(effectiveStartDate);
        dest.writeString(effectiveEndDate);
        dest.writeString(trustedStartDate);
        dest.writeString(trustedEndDate);
        dest.writeString(iconUrl);
        dest.writeString(note);
        dest.writeString(storeTel);
        dest.writeString(storeAddress);
        dest.writeString(storeName);
        dest.writeString(buyDate);
        dest.writeString(createDate);
        dest.writeString(lastUpdate);
        dest.writeString(orderId);
        dest.writeString(odrSeq);
    }
}
