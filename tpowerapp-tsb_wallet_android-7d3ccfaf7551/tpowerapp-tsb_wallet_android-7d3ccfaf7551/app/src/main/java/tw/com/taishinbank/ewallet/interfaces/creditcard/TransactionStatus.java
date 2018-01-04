package tw.com.taishinbank.ewallet.interfaces.creditcard;

public enum TransactionStatus {
    READY ("0", "更新中"),
    SUCCESS ("1", "交易成功"),
    FAILURE("2", "交易失敗"),
    RETURN("3", "已退貨"),
    EMPTY("4", "未知");

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

    public static TransactionStatus CodeToEnum(String code)
    {
        if(READY.getCode().equals(code))
           return READY;
        else if(SUCCESS.getCode().equals(code))
           return SUCCESS;
        else if(FAILURE.getCode().equals(code))
            return FAILURE;
        else if(RETURN.getCode().equals(code))
            return RETURN;
        else
            return EMPTY;
    }

    public static TransactionStatus DescToEnum(String description)
    {
        if(READY.getDescription().equals(description))
            return READY;
        else if(SUCCESS.getDescription().equals(description))
            return SUCCESS;
        else if(FAILURE.getDescription().equals(description))
            return FAILURE;
        else if(RETURN.getDescription().equals(description))
            return RETURN;
        else
            return EMPTY;
    }

    @Override
    public String toString() {
        return code + ' ' + description;
    }
}
