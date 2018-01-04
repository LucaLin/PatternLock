package tw.com.taishinbank.ewallet.model.extra;

import android.os.Parcel;
import android.os.Parcelable;


public class CouponEnter implements Parcelable {
    private int cpSeq; // 優惠券序號
    private int msmSeq; // 錢包會員/優惠券序號Mapping序號

    protected CouponEnter(Parcel in) {
        cpSeq = in.readInt();
        msmSeq = in.readInt();
    }

    public static final Creator<CouponEnter> CREATOR = new Creator<CouponEnter>() {
        @Override
        public CouponEnter createFromParcel(Parcel in) {
            return new CouponEnter(in);
        }

        @Override
        public CouponEnter[] newArray(int size) {
            return new CouponEnter[size];
        }
    };

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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(cpSeq);
        dest.writeInt(msmSeq);
    }
}
