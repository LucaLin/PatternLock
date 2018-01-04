package tw.com.taishinbank.ewallet.model;

/**
 * Created by oster on 2016/2/5.
 */
public enum InviteOption {

    Phone(1, "以手機號碼邀請"),
    Contact(2, "邀請通訊錄聯絡人"),
    Line(3, "發送Line邀請"),
    FBMessenger(4, "從Facebook Messenger邀請"),
    ;

    InviteOption(int id, String title) {
        this.id = id;
        this.title = title;
    }

    @Override
    public String toString() {
        return title;
    }

    private int id;
    private String title;

}
