package tw.com.taishinbank.ewallet.model.creditcard;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Siang on 4/1/16.
 */
public class tradeDetail implements Parcelable{
    private String Price;
    private String Quantity;
    private String Name;
    private String remark1;


    protected tradeDetail(Parcel in) {
        Price = in.readString();
        Quantity = in.readString();
        Name = in.readString();
        remark1 = in.readString();
    }

    public static final Creator<tradeDetail> CREATOR = new Creator<tradeDetail>() {
        @Override
        public tradeDetail createFromParcel(Parcel in) {
            return new tradeDetail(in);
        }

        @Override
        public tradeDetail[] newArray(int size) {
            return new tradeDetail[size];
        }
    };

    public String getPrice() {
        return Price;
    }

    public void setPrice(String price) {
        Price = price;
    }

    public String getQuantity() {
        return Quantity;
    }

    public void setQuantity(String quantity) {
        Quantity = quantity;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
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
        dest.writeString(Price);
        dest.writeString(Quantity);
        dest.writeString(Name);
        dest.writeString(remark1);
    }
}
