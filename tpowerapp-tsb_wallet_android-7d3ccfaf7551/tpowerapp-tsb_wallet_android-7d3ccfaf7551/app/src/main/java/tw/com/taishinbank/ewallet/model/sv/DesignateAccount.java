package tw.com.taishinbank.ewallet.model.sv;


import android.os.Parcel;
import android.os.Parcelable;

public class DesignateAccount implements Parcelable{
    // 提領銀行代碼
    private String bankCode;
    // 提領銀行名稱
    private String bankName;
    // 提領帳號
    private String account;

    // 是否審查過（用在其他銀行帳號，目前尚未使用到，先保留欄位）
    private boolean isApproved = true;

    public DesignateAccount() {

    }

    public String getBankCode() {
        return bankCode;
    }

    public void setBankCode(String bankCode) {
        this.bankCode = bankCode;
    }

    public String getBankName() {
        return bankName;
    }

    public void setBankName(String bankName) {
        this.bankName = bankName;
    }

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public Boolean getIsApproved() {
        return isApproved;
    }

    public void setIsApproved(Boolean isApproved) {
        this.isApproved = isApproved;
    }

    public String getBankTitle(){
        return String.format("(%1$s %2$s)", bankCode, bankName);
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(bankCode);
        dest.writeString(bankName);
        dest.writeString(account);
        dest.writeByte((byte) (isApproved ? 1 : 0));
    }

    protected DesignateAccount(Parcel in) {
        bankCode = in.readString();
        bankName = in.readString();
        account = in.readString();
        isApproved = in.readByte() != 0;
    }

    public static final Creator<DesignateAccount> CREATOR = new Creator<DesignateAccount>() {
        @Override
        public DesignateAccount createFromParcel(Parcel in) {
            return new DesignateAccount(in);
        }

        @Override
        public DesignateAccount[] newArray(int size) {
            return new DesignateAccount[size];
        }
    };

}
