package tw.com.taishinbank.ewallet.model.sv;

/**
 * Data Model
 *
 * Created by oster on 2016/1/18.
 */
public class ShareToOption {

    public final static int SHARE_TO_LINE = 1;
    public final static int SHARE_TO_FB_MESSENGER = 2;
    public final static int SHARE_TO_BY_EMAIL = 4;
    public final static int SHARE_TO_BY_SMS = 8;

    private int id;
    private String title;

    public ShareToOption(int id, String title) {
        this.id = id;
        this.title = title;
    }

    @Override
    public String toString() {
        return title;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
