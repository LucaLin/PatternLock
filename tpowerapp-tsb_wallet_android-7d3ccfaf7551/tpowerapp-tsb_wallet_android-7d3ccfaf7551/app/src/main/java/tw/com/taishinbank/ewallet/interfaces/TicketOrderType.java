package tw.com.taishinbank.ewallet.interfaces;

public enum TicketOrderType {
    CREATING_ORDER("0", "新增"),
    CONFIRMING("1", "確認中"),
    PAYING("2", "付款中"),
    PAYFAIL("27", "付款失敗"),
    CREATING_TICKET("3", "產生票券中"),
    CREATED_TICKET_FAIL("37", "票券產生失敗"),
    SUCCESS("4", "交易完成"),
    RETURN("5", "已退貨")
    ;

//    訂單狀態
//    0. 新增
//    1. 確認中
//    2. 付款中
//    27. 付款失敗
//    3. 產生票券中
//    37. 票券產生失敗
//    4. 交易完成
//    5. 已退貨


       TicketOrderType(String code, String description) {
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
