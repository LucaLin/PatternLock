package tw.com.taishinbank.ewallet.util.http.event;

public class DownloadEvent {

    private FinishDownloadListener finishDownloadListener;
    public void setFinishDownloadListener(FinishDownloadListener finishDownloadListener) {
        this.finishDownloadListener = finishDownloadListener;
    }
    // ----
    // Interface, inner class
    // ----
    public interface FinishDownloadListener {
        void onFinishDownload();
    }
}
