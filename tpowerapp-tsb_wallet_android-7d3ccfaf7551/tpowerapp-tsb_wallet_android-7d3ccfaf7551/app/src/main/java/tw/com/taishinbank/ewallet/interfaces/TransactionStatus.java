package tw.com.taishinbank.ewallet.interfaces;

public enum TransactionStatus {
    FINISHED ("0", "已交易"),
    AWAITING ("1", "尚未交易"),
    CANCELLED("2", "交易取消"),
    ;

    TransactionStatus(String code, String description) {
        this.code = code;
        this.description = description;
    }

    private String code;
    private String description;

    public String getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }

    @Override
    public String toString() {
        return code + ' ' + description;
    }
}
