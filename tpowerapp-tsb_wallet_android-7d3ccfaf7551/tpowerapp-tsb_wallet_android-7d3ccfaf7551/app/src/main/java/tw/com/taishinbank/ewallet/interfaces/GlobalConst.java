package tw.com.taishinbank.ewallet.interfaces;

public interface GlobalConst {//一些功能的開關
    boolean UseOfficialServer = true;//是否連到server
    boolean DISABLE_CREDIT_CARD = false;//信用卡項目功能
    boolean ENABLE_EXCEPTION_HANDLER = true;//是否開啟exception的helper
    boolean DISABLE_DOWNLOAD_COUPON_AGENCY_IMAGE = false; //AgencyImage
    String CODE_TAISHIN_BANK = "812";
    String NAME_TAISHIN_BANK = "台新國際商業銀行";
    int CODE_IS_TAISHIN = 1;
    int CODE_IS_NOT_TAISHIN = 2;
    int FORCE_MAX_SINGLE_LIMIT = 10000;

    String FILE_NAME_QR_CODE = "ewallet_qrcode";
    String APP_UPDATE_URL = "https://play.google.com/store/apps/details?id=tw.com.taishinbank.ewallet";
}
