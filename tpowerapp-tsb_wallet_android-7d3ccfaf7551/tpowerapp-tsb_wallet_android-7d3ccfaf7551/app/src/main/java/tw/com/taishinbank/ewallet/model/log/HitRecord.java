package tw.com.taishinbank.ewallet.model.log;

public class HitRecord {

    private int recordId;
    private final String hitEvent;
    private final String hitType;

    public HitRecord(int recordId, String hitEvent, String hitType) {
        this.recordId = recordId;
        this.hitEvent = hitEvent;
        this.hitType = hitType;
    }

    public HitRecord(String hitEvent, String hitType) {
        this.hitType = hitType;
        this.hitEvent = hitEvent;
    }

    public int getRecordId() {
        return recordId;
    }

    public String getHitEvent() {
        return hitEvent;
    }

    public String getHitType() {
        return hitType;
    }


    public enum HitEvent{
        INVITE_JOIN_WALLET  ("E01"),  // 邀請好友成為台新錢包成員
        INVITE_JOIN_SV      ("E02"),  // 邀請錢包好友成為儲值帳號
        SHARE_INVITE_CODE   ("E03"),  // 分享邀請代碼
        ;

        HitEvent(String code) {
            this.code = code;
        }

        private final String code;

        public String getCode() {
            return code;
        }

        @Override
        public String toString() {
            return code;
        }
    }

    public enum HitType{
        PHONENUMBER ("T01"), // 手機號碼
        CONTACTS    ("T02"), // 通訊錄聯絡人
        LINE        ("T03"), // Line
        FACEBOOK    ("T04"), // facebook
        PUSH        ("T05"), // 推播
        EMAIL       ("T06"), // email
        SMS         ("T07"), // 簡訊
        ;

        HitType(String code) {
            this.code = code;
        }

        private final String code;

        public String getCode() {
            return code;
        }

        @Override
        public String toString() {
            return code;
        }
    }

}
