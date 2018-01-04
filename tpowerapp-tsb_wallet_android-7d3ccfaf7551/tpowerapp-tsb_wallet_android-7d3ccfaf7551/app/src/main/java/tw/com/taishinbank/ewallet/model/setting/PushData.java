package tw.com.taishinbank.ewallet.model.setting;


import android.os.Parcel;
import android.os.Parcelable;

public class PushData implements Parcelable {
    private String psSeq;
    private String pushType;
    private String switchFlag;

    protected PushData(Parcel in) {
        psSeq = in.readString();
        pushType = in.readString();
        switchFlag = in.readString();
    }

    public static final Creator<PushData> CREATOR = new Creator<PushData>() {
        @Override
        public PushData createFromParcel(Parcel in) {
            return new PushData(in);
        }

        @Override
        public PushData[] newArray(int size) {
            return new PushData[size];
        }
    };

    public PushData(String  psSeq, String pushType, String switchFlag) {
        this.psSeq = psSeq;
        this.pushType = pushType;
        this.switchFlag = switchFlag;
    }

    public String getPsSeq() {
        return psSeq;
    }

    public void setPsSeq(String psSeq) {
        if(psSeq == null)
            this.psSeq = "null";
        else
            this.psSeq = psSeq;
    }

    public String getPushType() {
        return pushType;
    }

    public void setPushType(String pushType) {
        this.pushType = pushType;
    }

    public String getSwitchFlag() {
        return switchFlag;
    }

    public void setSwitchFlag(String switchFlag) {
        this.switchFlag = switchFlag;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(psSeq);
        dest.writeString(pushType);
        dest.writeString(switchFlag);
    }
}
