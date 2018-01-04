package com.dbs.omni.tw.util.http.listener;

public class FileDownloadEvent {

    private FinishDownloadListener finishDownloadListener;
    public void setFinishDownloadListener(FinishDownloadListener finishDownloadListener) {
        this.finishDownloadListener = finishDownloadListener;
    }
    // ----
    // Interface, inner class
    // ----
    public interface FinishDownloadListener {
        void onFinishDownload(String filePath);
        void onErrorDownload();
    }
}
