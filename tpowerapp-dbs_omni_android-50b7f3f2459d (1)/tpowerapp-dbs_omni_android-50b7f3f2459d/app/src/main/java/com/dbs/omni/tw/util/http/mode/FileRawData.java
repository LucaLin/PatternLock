package com.dbs.omni.tw.util.http.mode;

/**
 * Created by siang on 2017/4/6.
 */

public class FileRawData {

    private String filePath;

    private byte[] fileContent;

    public byte[] getFileContent() {
        return fileContent;
    }

    public void setFileContent(byte[] fileContent) {
        this.fileContent = fileContent;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }
}
