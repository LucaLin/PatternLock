package tw.com.taishinbank.ewallet.imagehelper;

import android.graphics.Bitmap;

import java.io.File;

import tw.com.taishinbank.ewallet.util.BitmapUtil;
import tw.com.taishinbank.ewallet.util.ContactUtil;

public class DiskCache {
    private static final String FOLDER = ContactUtil.FolderPath;

    /**
     * 將bitmap存成圖檔
     */
    public static void put(String memNO, Bitmap bitmap){
        ContactUtil.checkFolderExists();
        String filePath = FOLDER + File.separator + memNO + ".png";
        BitmapUtil.bitmapToFile(bitmap, filePath);
    }

    /**
     * 如果有圖檔就回傳圖檔的bitmap，否則回傳null
     */
//    public static Bitmap get(String memNO){
//        String filePath = FOLDER + File.separator + memNO + ".png";
//        File file = new File(filePath);
//        if(file.exists()) {
//            return BitmapFactory.decodeFile(filePath);
//        }
//        return null;
//    }

    /**
     * 如果有圖檔就回傳被縮放過的圖檔bitmap，否則回傳null
     */
    public static Bitmap get(String memNO, int width, int height){
        String filePath = FOLDER + File.separator + memNO + ".png";
        return BitmapUtil.decodeSampledBitmap(filePath, width, height);
    }


    /**
     * 回傳目前頭像目錄下是否存在該檔名
     */
    public static boolean isExists(String phonuNumber){
        String filePath = FOLDER + File.separator + phonuNumber + ".png";
        File file = new File(filePath);
        return file.exists();
    }

    /**
     * 重新命名檔名
     */
    public static void rename(String oldName, String newName){
        String filePath = FOLDER + File.separator + oldName + ".png";
        String filePathNew = FOLDER + File.separator + newName + ".png";
        File file = new File(filePath);
        File fileNew = new File(filePathNew);
        if(file.exists()) {
            if(fileNew.exists()){
                fileNew.delete();
            }
            file.renameTo(fileNew);
        }
    }

    /**
     * 清除圖檔目錄下的所有檔案並刪除圖檔目錄
     */
    public static void clear(){
        File file = new File(FOLDER);
        delete(file);
    }

    /**
     * 刪除檔案或目錄
     */
    private static void delete(File fileOrDirectory){
        // 如果檔案或目錄存在
        if(fileOrDirectory != null && fileOrDirectory.exists()) {
            // 如果是目錄
            if (fileOrDirectory.isDirectory()) {
                // 如果有子目錄或檔案，先刪光
                File[] subFiles = fileOrDirectory.listFiles();
                if (subFiles != null) {
                    for (File child : subFiles) {
                        delete(child);
                    }
                }
            }
            // 刪除檔案或目錄
            fileOrDirectory.delete();
        }
    }
}
