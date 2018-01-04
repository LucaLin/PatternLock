package tw.com.taishinbank.ewallet.interfaces;

public enum CouponType {
    ACT("1", "優惠活動"),
    RECEIVED("2", "好友贈送"),
    SENT("3", "已轉送"),
    TRADED("4", "已兌換"),
    ;

    CouponType(String code, String description) {
        this.code = code;
        this.description = description;
    }

    final public String code;
    final public String description;

    @Override
    public String toString() {
        return code + ' ' + description;
    }
}
