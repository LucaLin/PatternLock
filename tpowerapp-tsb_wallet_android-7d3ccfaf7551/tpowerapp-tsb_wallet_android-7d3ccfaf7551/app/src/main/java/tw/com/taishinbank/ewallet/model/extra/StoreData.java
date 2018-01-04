package tw.com.taishinbank.ewallet.model.extra;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Blake on 3/16/16.
 */
public class StoreData implements Parcelable {

    private String storeName;	    //		店家名稱
    private String storeAddress;	//		店家地址
    private String storePhone;	    //		店家電話

    protected StoreData(Parcel in) {
        storeName = in.readString();
        storeAddress = in.readString();
        storePhone = in.readString();
    }

    public static final Creator<StoreData> CREATOR = new Creator<StoreData>() {
        @Override
        public StoreData createFromParcel(Parcel in) {
            return new StoreData(in);
        }

        @Override
        public StoreData[] newArray(int size) {
            return new StoreData[size];
        }
    };

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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(storeName);
        dest.writeString(storeAddress);
        dest.writeString(storePhone);
    }
}
