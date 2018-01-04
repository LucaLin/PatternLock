package tw.com.taishinbank.ewallet.util;

import android.os.Environment;

import java.io.File;
import java.io.IOException;
import java.util.List;

import tw.com.taishinbank.ewallet.model.LocalContact;


public class ContactUtil {
    /**
     * 用來儲存頭像圖檔的資料夾路徑
     */
    public static final String FolderPath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).getAbsolutePath() + File.separator + "eWallet";
    public static final String TicketFolderPath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).getAbsolutePath() + File.separator + "eWallet" + File.separator + "eTicketImage";
    public static final String CouponAgencyFolderPath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).getAbsolutePath() + File.separator + "eWallet" + File.separator + "CouponAgencyImage";

    public static final String NOMEDIA = ".nomedia";//避免圖片顯示在自身相簿

    /**
     * 圖檔儲存時的縮放倍率（以註冊guildline上寫的裁切大小為基準），
     * 2016/4/8應客戶要求，圓形直徑符合圖案大小，
     * 整個圖案長寬為72dp，圓形直徑為72dp（有定義在dimens.xml）
     */
    public static final int PhotoSaveSizeScaleRate = 2;

    public static void checkFolderExists(){
        File folder = new File(FolderPath);
        if (!folder.exists()) {
            // 如果建成功，建立.nomedia檔案
            if (folder.mkdirs()) {
                File nomediaFile = new File(folder, NOMEDIA);
                try {
                    nomediaFile.createNewFile();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static String concatNames(List<LocalContact> list) {
        StringBuilder sb = new StringBuilder("");

        boolean isFirst = true;
        for (LocalContact c : list) {
            if (isFirst) {
                isFirst = false;
            } else {
                sb.append(", ");
            }
            sb.append(c.getDisplayName());
        }

        return sb.toString();
    }

    public static String getNamesNumberString(List<LocalContact> list){
        if(list != null && list.size() > 1) {
            return "(" + list.size() + ")";
        }
        return "";
    }
}
