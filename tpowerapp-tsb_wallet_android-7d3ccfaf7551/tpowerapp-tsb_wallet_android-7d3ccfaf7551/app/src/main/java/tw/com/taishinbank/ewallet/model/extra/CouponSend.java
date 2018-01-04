package tw.com.taishinbank.ewallet.model.extra;

import android.os.Parcel;
import android.os.Parcelable;

import tw.com.taishinbank.ewallet.model.LocalContact;

/**
 * Created by oster on 2016/2/1.
 */
public class CouponSend implements Parcelable {

    private LocalContact receiver;
    private Coupon coupon;
    private String sentDate;

    public CouponSend(LocalContact receiver, Coupon coupon) {
        this.receiver = receiver;
        this.coupon = coupon;
    }

    // ----
    // Getter and Setter
    // ----
    public LocalContact getReceiver() {
        return receiver;
    }

    public void setReceiver(LocalContact receiver) {
        this.receiver = receiver;
    }

    public Coupon getCoupon() {
        return coupon;
    }

    public void setCoupon(Coupon coupon) {
        this.coupon = coupon;
    }

    public String getSentDate() {
        return sentDate;
    }

    public void setSentDate(String sentDate) {
        this.sentDate = sentDate;
    }

    // ----
    // Parcellable
    // ----
    protected CouponSend(Parcel in) {
        receiver = in.readParcelable(LocalContact.class.getClassLoader());
        coupon = in.readParcelable(Coupon.class.getClassLoader());
        sentDate = in.readString();
    }

    public static final Creator<CouponSend> CREATOR = new Creator<CouponSend>() {
        @Override
        public CouponSend createFromParcel(Parcel in) {
            return new CouponSend(in);
        }

        @Override
        public CouponSend[] newArray(int size) {
            return new CouponSend[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(receiver, flags);
        dest.writeParcelable(coupon, flags);
        dest.writeString(sentDate);
    }
}
