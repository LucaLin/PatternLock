package tw.com.taishinbank.ewallet.model;


public class FileResponseResult extends ResponseResult {

    protected FileRawData rawData;

    public FileRawData getRawData() {
        return rawData;
    }

    public void setRawData(FileRawData rawData) {
        this.rawData = rawData;
    }
}
