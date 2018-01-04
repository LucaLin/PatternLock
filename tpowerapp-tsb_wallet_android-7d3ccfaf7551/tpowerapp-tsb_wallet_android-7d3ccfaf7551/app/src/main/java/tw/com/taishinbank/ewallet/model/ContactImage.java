package tw.com.taishinbank.ewallet.model;


public class ContactImage {
    public String getMemNO() {
        return memNO;
    }

    public void setMemNO(String memNO) {
        this.memNO = memNO;
    }

    public String getPic() {
        return pic;
    }

    public void setPic(String pic) {
        this.pic = pic;
    }

    // memNO      錢包會員序號
    private String memNO;
    // pic        *圖片字串 (加密)
    private String pic;
}
