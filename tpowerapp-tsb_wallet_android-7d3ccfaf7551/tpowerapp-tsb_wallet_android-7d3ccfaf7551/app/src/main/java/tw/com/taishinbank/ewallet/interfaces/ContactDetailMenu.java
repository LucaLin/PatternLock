package tw.com.taishinbank.ewallet.interfaces;

public enum ContactDetailMenu {
    SENDRED("0", "發紅包"),
    PAYMENT("1", "轉帳"),
    RECEIPT("2", "拆帳"),
    SENDCOUPON("3", "優惠券轉送"),
    MESSAGELOG("4", "歷史紀錄");

    ContactDetailMenu(String code, String description) {
        this.code = code;
        this.description = description;
    }

    private String code;
    private String description;

    public int getCode() {
        return Integer.valueOf(code);
    }

    public String getDescription() {
        return description;
    }

    public static ContactDetailMenu CodeToEnum(int code)
    {
        if(SENDRED.getCode() == code)
           return SENDRED;
        else if(PAYMENT.getCode() == code)
           return PAYMENT;
        else if(RECEIPT.getCode() == code)
            return RECEIPT;
        else if(SENDCOUPON.getCode() == code)
            return SENDCOUPON;
        else
            return MESSAGELOG;
    }

    public static ContactDetailMenu DescToEnum(String description)
    {
        if(SENDRED.getDescription().equals(description))
            return SENDRED;
        else if(PAYMENT.getDescription().equals(description))
            return PAYMENT;
        else if(RECEIPT.getDescription().equals(description))
            return RECEIPT;
        else if(SENDCOUPON.getDescription().equals(description))
            return SENDCOUPON;
        else
            return MESSAGELOG;
    }

    @Override
    public String toString() {
        return code + ' ' + description;
    }
}
