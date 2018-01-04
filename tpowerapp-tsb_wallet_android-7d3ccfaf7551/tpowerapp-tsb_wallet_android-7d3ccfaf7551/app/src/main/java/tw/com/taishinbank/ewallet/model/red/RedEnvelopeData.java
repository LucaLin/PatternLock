package tw.com.taishinbank.ewallet.model.red;


public class RedEnvelopeData {
    // 名字
    String name;
    // 金額
    String amount;
    // 頭像
    String photoPath;
    // ID
    String memNo;

    public RedEnvelopeData(String memNo, String name, String amount, String photoPath){
        this.memNo = memNo;
        this.name = name;
        this.amount = amount;
        this.photoPath = photoPath;
    }

    public String getPhotoPath() {
        return photoPath;
    }

    public void setPhotoPath(String photoPath) {
        this.photoPath = photoPath;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }


    public String getMemNo() {
        return memNo;
    }

    public void setMemNo(String memNo) {
        this.memNo = memNo;
    }


}
