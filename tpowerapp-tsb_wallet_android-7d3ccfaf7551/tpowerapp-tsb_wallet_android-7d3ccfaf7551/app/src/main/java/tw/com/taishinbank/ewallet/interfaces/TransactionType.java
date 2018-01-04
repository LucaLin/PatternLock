package tw.com.taishinbank.ewallet.interfaces;

public enum TransactionType {
    TRANSFER_TO("5", "轉帳付款"),
    REQUEST_SINGLE("6", "轉帳收款(付款請求)"),
    REQUEST_MULTIPLE("7", "均分收款(付款請求)"),
    DEPOSIT("8", "儲值"),
    WITHDRAW("9", "提領"),
    ;

    TransactionType(String code, String description) {
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
