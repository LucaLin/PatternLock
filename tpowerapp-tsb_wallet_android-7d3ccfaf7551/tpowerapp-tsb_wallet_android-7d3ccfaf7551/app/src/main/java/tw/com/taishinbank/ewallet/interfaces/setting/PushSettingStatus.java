package tw.com.taishinbank.ewallet.interfaces.setting;

public enum PushSettingStatus {
    ALL ("0", "全部"),
    RED ("1", "紅包"),
    ADD_FRIEND("2", "好友加入"),
    INVITE_SV("3", "邀請成為儲值會員"),
    SVACCOUNT("4", "儲值帳戶"),
    PERFER_TICKETS("5", "優惠券"),
    SYSTEMINFO("6", "公告訊息"),
    INVOICE_TICKETS("7", "電子票券"),
    EMTY("99", "空");

//    推播類別：
//    0:全部*
//    1:紅包*
//    2:好友加入
//    3:邀請成為儲值會員
//    4:儲值帳戶*
//    5:優惠券*
//    6:系統訊息*
//    7:電子票券*invoice_tickets


    PushSettingStatus(String code, String description) {
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

    public static PushSettingStatus CodeToEnum(String code)
    {
        if(ALL.getCode().equals(code))
           return ALL;
        else if(RED.getCode().equals(code))
           return RED;
        else if(ADD_FRIEND.getCode().equals(code))
            return ADD_FRIEND;
        else if(INVITE_SV.getCode().equals(code))
            return INVITE_SV;
        else if(SVACCOUNT.getCode().equals(code))
            return SVACCOUNT;
        else if(PERFER_TICKETS.getCode().equals(code))
            return PERFER_TICKETS;
        else if(SYSTEMINFO.getCode().equals(code))
            return SYSTEMINFO;
        else if(INVOICE_TICKETS.getCode().equals(code))
            return INVOICE_TICKETS;
        else
            return EMTY;
    }

    public static PushSettingStatus DescToEnum(String description)
    {
        if(ALL.getDescription().equals(description))
            return ALL;
        else if(RED.getDescription().equals(description))
            return RED;
        else if(ADD_FRIEND.getDescription().equals(description))
            return ADD_FRIEND;
        else if(INVITE_SV.getDescription().equals(description))
            return INVITE_SV;
        else if(SVACCOUNT.getDescription().equals(description))
            return SVACCOUNT;
        else if(PERFER_TICKETS.getDescription().equals(description))
            return PERFER_TICKETS;
        else if(SYSTEMINFO.getDescription().equals(description))
            return SYSTEMINFO;
        else if(INVOICE_TICKETS.getDescription().equals(description))
            return INVOICE_TICKETS;
        else
            return EMTY;
    }

    @Override
    public String toString() {
        return code + ' ' + description;
    }
}
