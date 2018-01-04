package tw.com.taishinbank.ewallet.model.extra;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Blake on 3/8/16.
 */
public class TicketOrderData implements Parcelable {
    private String	platformId;	       //票券商城平台的 ID
    private String	mid;	            //用來表示商城中的特定店家
    private String	orderId;	        //商城平台自訂的訂單編號
    private String	price;	            //訂單總價格
    private String	isRefundable;   	//訂單是否可退貨(1/0)
    private String	isPayable;	        //可否再次進行交易(1.可 / 0. 不可)
    private String	memo;	            //備註
    private String	title;	            //商品名稱
    private String	unitPrice;	        //票券單價
    private String	iconUrl;	        //圖示網址
    private String	note;	            //電子票券須知
    private String	storeTel;	        //商家客服電話
    private String	storeAddress;	    //商家位置
    private String	storeName;	        //商家名稱
    private String	count;	            //數量
    private String	status;	            //訂單狀態
                                    //    0. 新增
                                    //    1. 確認中
                                    //    2. 付款中
                                    //    27.付款失敗
                                    //    3. 產生票券中
                                    //    37.票券產生失敗
                                    //    4. 交易完成
    private String	createDate;	        //產生日期
    private String	lastUpdate;	        //最後修改日期

    private String orderToken;

    protected TicketOrderData(Parcel in) {
        platformId = in.readString();
        mid = in.readString();
        orderId = in.readString();
        price = in.readString();
        isRefundable = in.readString();
        isPayable = in.readString();
        memo = in.readString();
        title = in.readString();
        unitPrice = in.readString();
        iconUrl = in.readString();
        note = in.readString();
        storeTel = in.readString();
        storeAddress = in.readString();
        storeName = in.readString();
        count = in.readString();
        status = in.readString();
        createDate = in.readString();
        lastUpdate = in.readString();
        orderToken = in.readString();
    }

    public static final Creator<TicketOrderData> CREATOR = new Creator<TicketOrderData>() {
        @Override
        public TicketOrderData createFromParcel(Parcel in) {
            return new TicketOrderData(in);
        }

        @Override
        public TicketOrderData[] newArray(int size) {
            return new TicketOrderData[size];
        }
    };

    public String getPlatformId() {
        return platformId;
    }

    public void setPlatformId(String platformId) {
        this.platformId = platformId;
    }

    public String getMid() {
        return mid;
    }

    public void setMid(String mid) {
        this.mid = mid;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getIsRefundable() {
        return isRefundable;
    }

    public void setIsRefundable(String isRefundable) {
        this.isRefundable = isRefundable;
    }

    public String getIsPayable() {
        return isPayable;
    }

    public void setIsPayable(String isPayable) {
        this.isPayable = isPayable;
    }

    public String getMemo() {
        return memo;
    }

    public void setMemo(String memo) {
        this.memo = memo;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(String unitPrice) {
        this.unitPrice = unitPrice;
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

    public String getCount() {
        return count;
    }

    public void setCount(String count) {
        this.count = count;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
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

    public String getOrderToken() {
        return orderToken;
    }

    public void setOrderToken(String orderToken) {
        this.orderToken = orderToken;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(platformId);
        dest.writeString(mid);
        dest.writeString(orderId);
        dest.writeString(price);
        dest.writeString(isRefundable);
        dest.writeString(isPayable);
        dest.writeString(memo);
        dest.writeString(title);
        dest.writeString(unitPrice);
        dest.writeString(iconUrl);
        dest.writeString(note);
        dest.writeString(storeTel);
        dest.writeString(storeAddress);
        dest.writeString(storeName);
        dest.writeString(count);
        dest.writeString(status);
        dest.writeString(createDate);
        dest.writeString(lastUpdate);
        dest.writeString(orderToken);
    }
}
