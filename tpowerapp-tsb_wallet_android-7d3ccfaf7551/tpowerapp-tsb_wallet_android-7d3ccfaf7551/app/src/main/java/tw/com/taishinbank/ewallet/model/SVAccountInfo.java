package tw.com.taishinbank.ewallet.model;

import android.os.Parcel;
import android.os.Parcelable;

public class SVAccountInfo implements Parcelable {

    // 儲值支付帳號
    private String prepaidAccount;
    // 帳戶等級
    private String accountLevel;
    // 儲值帳戶餘額
    private String balance;
    // 儲值限額
    private String depositLimCurr;
    // 單筆交易限額
    private String singleLimCurr;
    // 每日交易限額
    private String dailyLimCurr;
    // 每月交易限額
    private String monthlyLimCurr;
    // 法定儲值限額
    private String depositLimMax;
    // 法定單筆限額
    private String singleLimMax;
    // 法定每日限額
    private String dailyLimMax;
    // 法定每月限額
    private String monthlyLimMax;
    // 當日累積交易金額
    private String dailyAmount;
    // 當月累積交易金額
    private String monthlyAmount;

    public SVAccountInfo(){

    }

    protected SVAccountInfo(Parcel in) {
        prepaidAccount = in.readString();
        accountLevel = in.readString();
        balance = in.readString();
        depositLimCurr = in.readString();
        singleLimCurr = in.readString();
        dailyLimCurr = in.readString();
        monthlyLimCurr = in.readString();
        depositLimMax = in.readString();
        singleLimMax = in.readString();
        dailyLimMax = in.readString();
        monthlyLimMax = in.readString();
        dailyAmount = in.readString();
        monthlyAmount = in.readString();
    }

    public static final Creator<SVAccountInfo> CREATOR = new Creator<SVAccountInfo>() {
        @Override
        public SVAccountInfo createFromParcel(Parcel in) {
            return new SVAccountInfo(in);
        }

        @Override
        public SVAccountInfo[] newArray(int size) {
            return new SVAccountInfo[size];
        }
    };

    /**
     *
     * @return
     * The prepaidAccount
     */
    public String getPrepaidAccount() {
        return prepaidAccount;
    }

    /**
     *
     * @param prepaidAccount
     * The prepaidAccount
     */
    public void setPrepaidAccount(String prepaidAccount) {
        this.prepaidAccount = prepaidAccount;
    }

    /**
     *
     * @return
     * The accountLevel
     */
    public String getAccountLevel() {
        return accountLevel;
    }

    /**
     *
     * @param accountLevel
     * The accountLevel
     */
    public void setAccountLevel(String accountLevel) {
        this.accountLevel = accountLevel;
    }

    /**
     *
     * @return
     * The balance
     */
    public String getBalance() {
        return balance;
    }

    /**
     *
     * @param balance
     * The balance
     */
    public void setBalance(String balance) {
        this.balance = balance;
    }

    /**
     *
     * @return
     * The depositLimCurr
     */
    public String getDepositLimCurr() {
        return depositLimCurr;
    }

    /**
     *
     * @param depositLimCurr
     * The depositLimCurr
     */
    public void setDepositLimCurr(String depositLimCurr) {
        this.depositLimCurr = depositLimCurr;
    }

    /**
     *
     * @return
     * The singleLimCurr
     */
    public String getSingleLimCurr() {
        return singleLimCurr;
    }

    /**
     *
     * @param singleLimCurr
     * The singleLimCurr
     */
    public void setSingleLimCurr(String singleLimCurr) {
        this.singleLimCurr = singleLimCurr;
    }

    /**
     *
     * @return
     * The dailyLimCurr
     */
    public String getDailyLimCurr() {
        return dailyLimCurr;
    }

    /**
     *
     * @param dailyLimCurr
     * The dailyLimCurr
     */
    public void setDailyLimCurr(String dailyLimCurr) {
        this.dailyLimCurr = dailyLimCurr;
    }

    /**
     *
     * @return
     * The monthlyLimCurr
     */
    public String getMonthlyLimCurr() {
        return monthlyLimCurr;
    }

    /**
     *
     * @param monthlyLimCurr
     * The monthlyLimCurr
     */
    public void setMonthlyLimCurr(String monthlyLimCurr) {
        this.monthlyLimCurr = monthlyLimCurr;
    }

    /**
     *
     * @return
     * The depositLimMax
     */
    public String getDepositLimMax() {
        return depositLimMax;
    }

    /**
     *
     * @param depositLimMax
     * The depositLimMax
     */
    public void setDepositLimMax(String depositLimMax) {
        this.depositLimMax = depositLimMax;
    }

    /**
     *
     * @return
     * The singleLimMax
     */
    public String getSingleLimMax() {
        return singleLimMax;
    }

    /**
     *
     * @param singleLimMax
     * The singleLimMax
     */
    public void setSingleLimMax(String singleLimMax) {
        this.singleLimMax = singleLimMax;
    }

    /**
     *
     * @return
     * The dailyLimMax
     */
    public String getDailyLimMax() {
        return dailyLimMax;
    }

    /**
     *
     * @param dailyLimMax
     * The dailyLimMax
     */
    public void setDailyLimMax(String dailyLimMax) {
        this.dailyLimMax = dailyLimMax;
    }

    /**
     * @return
     * The monthlyLimMax
     */
    public String getMonthlyLimMax() {
        return monthlyLimMax;
    }

    /**
     *
     * @param monthlyLimMax
     * The monthlyLimMax
     */
    public void setMonthlyLimMax(String monthlyLimMax) {
        this.monthlyLimMax = monthlyLimMax;
    }

    /**
     * @return
     * The dailyAmount
     */
    public String getDailyAmount() {
        return dailyAmount;
    }

    /**
     *
     * @param dailyAmount
     * The dailyAmount
     */
    public void setDailyAmount(String dailyAmount) {
        this.dailyAmount = dailyAmount;
    }

    /**
     * @return
     * The monthlyAmount
     */
    public String getMonthlyAmount() {
        return monthlyAmount;
    }

    /**
     * @param monthlyAmount
     * The monthlyAmount
     */
    public void setMonthlyAmount(String monthlyAmount) {
        this.monthlyAmount = monthlyAmount;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(prepaidAccount);
        dest.writeString(accountLevel);
        dest.writeString(balance);
        dest.writeString(depositLimCurr);
        dest.writeString(singleLimCurr);
        dest.writeString(dailyLimCurr);
        dest.writeString(monthlyLimCurr);
        dest.writeString(depositLimMax);
        dest.writeString(singleLimMax);
        dest.writeString(dailyLimMax);
        dest.writeString(monthlyLimMax);
        dest.writeString(dailyAmount);
        dest.writeString(monthlyAmount);
    }
}
