package tw.com.taishinbank.ewallet.model;

/**
 * 030301的回傳結果
 */
public class AppUpdateInfo {

    private String appDesc;                // 版本說明
    private String appVersion;             // 最新版號
    private String forceAppVersion;        // 強制更新版本號
    private String appUrl;                 // 下載網址

    public String getAppDesc() {
        return appDesc;
    }

    public void setAppDesc(String appDesc) {
        this.appDesc = appDesc;
    }

    public String getAppVersion() {
        return appVersion;
    }

    public void setAppVersion(String appVersion) {
        this.appVersion = appVersion;
    }

    public String getForceAppVersion() {
        return forceAppVersion;
    }

    public void setForceAppVersion(String forceAppVersion) {
        this.forceAppVersion = forceAppVersion;
    }

    public String getAppUrl() {
        return appUrl;
    }

    public void setAppUrl(String appUrl) {
        this.appUrl = appUrl;
    }
}
