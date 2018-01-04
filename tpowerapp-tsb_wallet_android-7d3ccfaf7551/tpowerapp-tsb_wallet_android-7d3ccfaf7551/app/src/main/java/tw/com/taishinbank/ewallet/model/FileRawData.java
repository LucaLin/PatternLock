package tw.com.taishinbank.ewallet.model;

/**
 * Created by oster on 2016/1/22.
 */
public class FileRawData {

    private String memNo;

    private String imagePath;

    private byte[] fileContent;

    public String getMemNo() {
        return memNo;
    }

    public void setMemNo(String memNo) {
        this.memNo = memNo;
    }

    public byte[] getFileContent() {
        return fileContent;
    }


    public void setFileContent(byte[] fileContent) {
        this.fileContent = fileContent;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }
}
