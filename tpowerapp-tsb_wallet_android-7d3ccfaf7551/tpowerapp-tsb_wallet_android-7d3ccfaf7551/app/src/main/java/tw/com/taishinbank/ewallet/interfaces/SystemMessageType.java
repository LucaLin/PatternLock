package tw.com.taishinbank.ewallet.interfaces;

/**
 * 系統公告類型
 */
public enum SystemMessageType {
    COUPON("1"),                        // 優惠公告，(預設,目前只有這種1/19)
    SYSTEM("2"),                        // 系統公告 (維修…等)
    INVITE_REGISTER_SV("3"),            // 邀請註冊儲值
    INVITE_BINDING_CREDIT_CARD("4"),    // 邀請綁定信用卡
    WALLET_HOME_SHOW_ALERT("5"),            // 錢包首頁如果有這類的要跳alert
    ;

    public static SystemMessageType getType(String type){
        if(COUPON.getCode().equals(type)){
            return COUPON;
        }else if(SYSTEM.getCode().equals(type)) {
            return SYSTEM;
        }else if(INVITE_REGISTER_SV.getCode().equals(type)) {
            return INVITE_REGISTER_SV;
        }else if(INVITE_BINDING_CREDIT_CARD.getCode().equals(type)) {
            return INVITE_BINDING_CREDIT_CARD;
        }else if(WALLET_HOME_SHOW_ALERT.getCode().equals(type)) {
            return WALLET_HOME_SHOW_ALERT;
        }
        return SYSTEM;
    }

    SystemMessageType(String code) {
        this.code = code;
    }

    private String code;
    public String getCode(){
        return code;
    }

    @Override
    public String toString() {
        return code;
    }
}
