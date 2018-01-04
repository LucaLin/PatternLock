package tw.com.taishinbank.ewallet.interfaces;

public enum TransactionTypeOrStatus {
    WITHDRAW ("提領"),
    DEPOSITE ("儲值"),
    TRANSFER ("轉帳"),
    REQUEST  ("付款請求"),
    REQUEST_MULTIPLE("均分收款"),
    CANCEL   ("已取消"),
    ;

    TransactionTypeOrStatus(String description) {
        this.description = description;
    }

    private String description;

    public String getDescription() {
        return description;
    }
}
