package tw.com.taishinbank.ewallet.interfaces;

public enum TicketType {
    UNUSE("0", "未使用"),
    USE("1", "已使用"),
    RETURN("2", "已退貨"),
    ;

    TicketType(String code, String description) {
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
