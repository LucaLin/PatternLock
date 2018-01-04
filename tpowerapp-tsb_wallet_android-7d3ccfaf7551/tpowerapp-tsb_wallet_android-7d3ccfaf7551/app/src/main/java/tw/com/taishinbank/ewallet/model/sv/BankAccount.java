package tw.com.taishinbank.ewallet.model.sv;

import android.os.Parcel;

import tw.com.taishinbank.ewallet.interfaces.GlobalConst;
import tw.com.taishinbank.ewallet.util.FormatUtil;

public class BankAccount extends DesignateAccount{

    // 帳戶餘額
    private double balance;
    // 帳號資訊(產品代號)
    private long acctType;
    // 帳號資訊(產品子號)
    private long interestCate;

    public BankAccount(){
        super();
        // 設定預設為台新
        setBankCode(GlobalConst.CODE_TAISHIN_BANK);
        setBankName(GlobalConst.NAME_TAISHIN_BANK);
    }

    public double getBalance() {
        return balance;
    }

    public void setBalance(double balance) {
        this.balance = balance;
    }

    public long getAcctType() {
        return acctType;
    }

    public void setAcctType(long acctType) {
        this.acctType = acctType;
    }

    public long getInterestCate() {
        return interestCate;
    }

    public void setInterestCate(long interestCate) {
        this.interestCate = interestCate;
    }

    protected BankAccount(Parcel in) {
        super(in);
        balance = in.readDouble();
        acctType = in.readLong();
        interestCate = in.readLong();
    }

    public static final Creator<BankAccount> CREATOR = new Creator<BankAccount>() {
        @Override
        public BankAccount createFromParcel(Parcel in) {
            return new BankAccount(in);
        }

        @Override
        public BankAccount[] newArray(int size) {
            return new BankAccount[size];
        }
    };

    @Override
    public String toString() {
        return FormatUtil.toAccountFormat(getAccount()) + "\n" + getBankTitle();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeDouble(balance);
        dest.writeLong(acctType);
        dest.writeLong(interestCate);
    }
}
