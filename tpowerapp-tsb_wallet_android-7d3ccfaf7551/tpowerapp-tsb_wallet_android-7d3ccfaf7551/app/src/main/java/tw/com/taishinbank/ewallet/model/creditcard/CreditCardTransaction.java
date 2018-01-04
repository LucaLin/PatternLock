package tw.com.taishinbank.ewallet.model.creditcard;


import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

public class CreditCardTransaction implements Parcelable {

    private String storeName;
    private String merchantTradeDate; //交易
    private String serviceTradeDate;  //購買時間
    private String rtnTradeDate;      //退貨時間
    private String platform;             //交易類型 2=反向 3=正向
    private double tradeAmount;
    private int tradeStatus;
    private String tradeStatusName;
    private String cardNumberShelter;
    private String cardName;
    private String bankServicePhone;
    private String bankName;
    private String storeTel;
    private String storeAddress;
    private String orderNo;             //訂單編號(墨攻)
    private String authIdResp;          //授權碼(退)
    private String serviceTradeNO;      //交易編號(退)
    private String merchantID;          //通路商代碼(判別明細欄位)
    private String merchantGroup;       //通路商群組(保留)
    private String remark1;             //(保留)

    private ArrayList<tradeDetail> tradeDetail = new ArrayList<>();

    public CreditCardTransaction() {
    }

    protected CreditCardTransaction(Parcel in) {
        storeName = in.readString();
        merchantTradeDate = in.readString();
        serviceTradeDate = in.readString();
        rtnTradeDate = in.readString();
        platform = in.readString();
        tradeAmount = in.readDouble();
        tradeStatus = in.readInt();
        tradeStatusName = in.readString();
        cardNumberShelter = in.readString();
        cardName = in.readString();
        bankServicePhone = in.readString();
        bankName = in.readString();
        storeTel = in.readString();
        storeAddress = in.readString();
        orderNo = in.readString();
        authIdResp = in.readString();
        serviceTradeNO = in.readString();
        merchantID = in.readString();
        merchantGroup = in.readString();
        remark1 = in.readString();
        tradeDetail = in.createTypedArrayList(tw.com.taishinbank.ewallet.model.creditcard.tradeDetail.CREATOR);
    }

    public static final Creator<CreditCardTransaction> CREATOR = new Creator<CreditCardTransaction>() {
        @Override
        public CreditCardTransaction createFromParcel(Parcel in) {
            return new CreditCardTransaction(in);
        }

        @Override
        public CreditCardTransaction[] newArray(int size) {
            return new CreditCardTransaction[size];
        }
    };

    public String getServiceTradeDate() {
        return serviceTradeDate;
    }

    public void setServiceTradeDate(String serviceTradeDate) {
        this.serviceTradeDate = serviceTradeDate;
    }

    public String getRtnTradeDate() {
        return rtnTradeDate;
    }

    public void setRtnTradeDate(String rtnTradeDate) {
        this.rtnTradeDate = rtnTradeDate;
    }

    public String getPlatform() {
        return platform;
    }

    public void setPlatform(String platform) {
        this.platform = platform;
    }

    public String getOrderNo() {
        return orderNo;
    }

    public void setOrderNo(String orderNo) {
        this.orderNo = orderNo;
    }

    public ArrayList<tw.com.taishinbank.ewallet.model.creditcard.tradeDetail> getTradeDetail() {
        return tradeDetail;
    }

    public void setTradeDetail(ArrayList<tw.com.taishinbank.ewallet.model.creditcard.tradeDetail> tradeDetail) {
        this.tradeDetail = tradeDetail;
    }

    public String getStoreName() {
        return storeName;
    }

    public void setStoreName(String storeName) {
        this.storeName = storeName;
    }

    public String getMerchantTradeDate() {
        return merchantTradeDate;
    }

    public void setMerchantTradeDate(String merchantTradeDate) {
        this.merchantTradeDate = merchantTradeDate;
    }

    public double getTradeAmount() {
        return tradeAmount;
    }

    public void setTradeAmount(double tradeAmount) {
        this.tradeAmount = tradeAmount;
    }

    public int getTradeStatus() {
        return tradeStatus;
    }

    public void setTradeStatus(int tradeStatus) {
        this.tradeStatus = tradeStatus;
    }

    public String getTradeStatusName() {
        return tradeStatusName;
    }

    public void setTradeStatusName(String tradeStatusName) {
        this.tradeStatusName = tradeStatusName;
    }

    public String getCardNumberShelter() {
        return cardNumberShelter;
    }

    public void setCardNumberShelter(String cardNumberShelter) {
        this.cardNumberShelter = cardNumberShelter;
    }

    public String getCardName() {
        return cardName;
    }

    public void setCardName(String cardName) {
        this.cardName = cardName;
    }

    public String getBankServicePhone() {
        return bankServicePhone;
    }

    public void setBankServicePhone(String bankServicePhone) {
        this.bankServicePhone = bankServicePhone;
    }

    public String getBankName() {
        return bankName;
    }


    public void setBankName(String bankName) {
        this.bankName = bankName;
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

    public String getAuthIdResp() {
        return authIdResp;
    }

    public void setAuthIdResp(String authIdResp) {
        this.authIdResp = authIdResp;
    }

    public String getServiceTradeNO() {
        return serviceTradeNO;
    }

    public void setServiceTradeNO(String serviceTradeNO) {
        this.serviceTradeNO = serviceTradeNO;
    }

    public String getMerchantID() {
        return merchantID;
    }

    public void setMerchantID(String merchantID) {
        this.merchantID = merchantID;
    }

    public String getMerchantGroup() {
        return merchantGroup;
    }

    public void setMerchantGroup(String merchantGroup) {
        this.merchantGroup = merchantGroup;
    }

    public String getRemark1() {
        return remark1;
    }

    public void setRemark1(String remark1) {
        this.remark1 = remark1;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(storeName);
        dest.writeString(merchantTradeDate);
        dest.writeString(serviceTradeDate);
        dest.writeString(rtnTradeDate);
        dest.writeString(platform);
        dest.writeDouble(tradeAmount);
        dest.writeInt(tradeStatus);
        dest.writeString(tradeStatusName);
        dest.writeString(cardNumberShelter);
        dest.writeString(cardName);
        dest.writeString(bankServicePhone);
        dest.writeString(bankName);
        dest.writeString(storeTel);
        dest.writeString(storeAddress);
        dest.writeString(orderNo);
        dest.writeString(authIdResp);
        dest.writeString(serviceTradeNO);
        dest.writeString(merchantID);
        dest.writeString(merchantGroup);
        dest.writeString(remark1);
        dest.writeTypedList(tradeDetail);
    }

}
