package tw.com.taishinbank.ewallet.interfaces.creditcard;

public enum CreditCardTradeType {
    STORE("2", "顯示付款碼"),
    TICKET("3", "掃描付款");

    CreditCardTradeType(String code, String description) {
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

    public static CreditCardTradeType CodeToEnum(String code)
    {
        if(STORE.getCode().equals(code))
            return STORE;
        else if(TICKET.getCode().equals(code))
            return TICKET;
        else
            return null;
    }

    public static CreditCardTradeType DescToEnum(String description)
    {
        if(STORE.getDescription().equals(description))
            return STORE;
        else if(TICKET.getDescription().equals(description))
            return TICKET;
        else
            return null;
    }

    @Override
    public String toString() {
        return code + ' ' + description;
    }
}
