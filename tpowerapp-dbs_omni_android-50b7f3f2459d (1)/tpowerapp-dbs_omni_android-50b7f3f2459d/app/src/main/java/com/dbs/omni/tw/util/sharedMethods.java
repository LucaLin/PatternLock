package com.dbs.omni.tw.util;

/**
 * Created by peterliu on 2015/11/18.
 */

import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;

import com.dbs.omni.tw.setted.GlobalConst;

import java.io.File;
import java.io.FileFilter;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;


public class sharedMethods {

    private static final String FOLDER = GlobalConst.FolderPath;

//    //AES 128bit CBC,
//    private static String baseString = "" ;
//    private static String iString = "" ;
//    private static String kString = "";

    public static File base64ToFile(String baseString, String fileName) {
        if(TextUtils.isEmpty(baseString))
            return null;

        byte[] fileAsBytes = Base64.decode(baseString.getBytes(), Base64.DEFAULT);
        String saveFilePath = GlobalConst.PDFFileFolderPath + File.separator + fileName + ".pdf";

        isExistsFolder(GlobalConst.PDFFileFolderPath, true);

        File filePath = new File(saveFilePath);
        FileOutputStream fileOutputStream = null;
        try {
            fileOutputStream = new FileOutputStream(filePath, true);
            fileOutputStream.write(fileAsBytes);

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if(fileOutputStream != null) {
                try {
                    fileOutputStream.flush();
                    fileOutputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            return filePath;
        }

    }

    public static void isExistsFolder(String folderPath, boolean isMkdir) {
        File folder = new File(folderPath);
        if (!folder.exists()) {
            boolean ret = folder.mkdirs();
            if (isMkdir && !ret)
                folder.mkdir();
        }
    }

    public static boolean validateUserIDforROC(String userIdStr){

        //Peter@2016/02/23, TSB PM新增CR：身分證需驗證是否符合中華民國身分證規則。
        //前提假設第一碼大寫英文，後面都數字。
        int tmpSum = 0;

        //step1:大寫英文轉ASCII,e.g.A=65
        int alphaInt = userIdStr.codePointAt(0);
        //step2:查表轉換為身分證英文轉碼,
        //A(65)~H(72) = 10~17; I(73) = 34;
        //J(74)~N(78) = 18~22; O(79) = 35;
        //P(80)~V(86) = 23~29; W(87) = 32;
        //X(88)~Y(89) = 30,31; Z(90) = 33;
        if (alphaInt >= 65 && alphaInt <= 72)
        {
            alphaInt = alphaInt - 55;
        }
        else if (alphaInt >= 74 && alphaInt <=78)
        {
            alphaInt = alphaInt - 56;
        }
        else if (alphaInt >= 80 && alphaInt <=86)
        {
            alphaInt = alphaInt - 57;
        }
        else if (alphaInt >= 88 && alphaInt <=89)
        {
            alphaInt = alphaInt - 58;
        }
        else {
            switch (alphaInt) {
                case 73:
                    alphaInt = 34;
                    break;
                case 79:
                    alphaInt = 35;
                    break;
                case 87:
                    alphaInt = 32;
                    break;
                case 90:
                    alphaInt = 33;
                    break;
            }
        }

        //step3:英文轉碼後，十位數*1 + 個位數*9；
        tmpSum = (alphaInt/10) *1 + (alphaInt%10) *9;

        //step4:數字前8碼依序權重8,7,6...1；
        for (int i =1; i<= userIdStr.length()-2; i++) {
            String idd = userIdStr.substring(i, i+1);
            tmpSum = tmpSum + Integer.valueOf(idd)*(9-i);
        }

        //step5:數字第9碼權重=1；
        tmpSum = tmpSum + Integer.valueOf(userIdStr.substring(9));

        //step6:總和為10的倍數
        return tmpSum % 10 == 0;
    }

    public static String SHA256Encrypt(String strForEncrypt) {
        MessageDigest digest = null ;
        try {
            digest = MessageDigest.getInstance("SHA-256");
        } catch (NoSuchAlgorithmException e1) {
            e1.printStackTrace();
        }
        digest.reset();
        byte[] data = digest.digest(strForEncrypt.getBytes());

        return String.format("%0" + (data.length * 2) + "X", new BigInteger(1, data));
    }

    public static boolean validateUserIDforResidentPermit(String userIdStr){


        String[] pidCharArray = new String[] {"A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z"};
        // 原居留證第一碼英文字應轉換為10~33，十位數*1，個位數*9，這裡直接作[(十位數*1) mod 10] + [(個位數*9) mod 10]
        int[] pidResidentFirstInt = new int[] {1, 10, 9, 8, 7, 6, 5, 4, 9, 3, 2, 2, 11, 10, 8, 9, 8, 7, 6, 5, 4, 3, 11, 3, 12, 10};

        // 原居留證第二碼英文字應轉換為10~33，並僅取個位數*6，這裡直接取[(個位數*6) mod 10]
        int[] pidResidentSecondInt = new int[] {0, 8, 6, 4, 2, 0, 8, 6, 2, 4, 2, 0, 8, 6, 0, 4, 2, 0, 8, 6, 4, 2, 6, 0, 8, 4};

        int verifyNum = 0;

        String str = "";
        for(int index = 0; index < pidCharArray.length; index++) {
            str = pidCharArray[index];
            // 第一碼
            Log.d("第一碼", userIdStr.substring(0,1));
            if(str.equalsIgnoreCase(userIdStr.substring(0,1))) {
                verifyNum += pidResidentFirstInt[index];
            }

            // 第二碼
            Log.d("第二碼", userIdStr.substring(1,2));
            if(str.equalsIgnoreCase(userIdStr.substring(1,2))) {
                verifyNum += pidResidentSecondInt[index];
            }
        }

        // 第三~八碼
        for (int index = 2, j = 7; index < 9; index++, j--) {

            Log.d("第三~八碼", userIdStr.substring(index, index+1));
            str = userIdStr.substring(index, index+1);
            verifyNum += Integer.valueOf(str) * j;
        }

        // 檢查碼
        verifyNum = (10 - (verifyNum % 10)) % 10;

        str = userIdStr.substring(9, 10);

        return verifyNum == Integer.valueOf(str);
    }

//    public static String MD5Encrypt(String s) {
//        try {
//            // Create MD5 Hash
//            MessageDigest digest = MessageDigest
//                    .getInstance("MD5");
//            digest.update(s.getBytes());
//            byte messageDigest[] = digest.digest();
//
//            // Create Hex String
//            StringBuilder hexString = new StringBuilder();
//            for (byte aMessageDigest : messageDigest) {
//                String h = Integer.toHexString(0xFF & aMessageDigest);
//                while (h.length() < 2)
//                    h = "0" + h;
//                hexString.append(h);
//            }
//            return hexString.toString();
//
//        } catch (NoSuchAlgorithmException e) {
//            e.printStackTrace();
//        }
//        return "";
//    }

    //Find file
    public static File[] findIconPaths(String findTag, String findFolder) {
        final String findString = findTag.toUpperCase();
        File folder = new File(findFolder);
        File[] files = folder.listFiles(new FileFilter() {
            @Override
            public boolean accept(File pathname) {
                String filename = pathname.getName().toUpperCase();
                if (filename.indexOf(findString) == -1) {
                    return false;
                } else {
                    return true;
                }
            }
        });

        return files;
    }

//    /**
//     * Checks if the device is rooted.
//     *
//     * @return <code>100</code> if the device is rooted, <code>400</code> otherwise.
//     */
//    public static int isRTed() {
//
//        // get from build info
//        String buildTags = android.os.Build.TAGS;
//        if (buildTags != null && buildTags.contains("test-keys")) {
//            return 100;
//        }
//
//        // check if /system/app/Superuser.apk is present
//        try {
//            File file = new File("/system/app/Superuser.apk");
//            if (file.exists()) {
//                return 100;
//            }
//        } catch (Exception e1) {
//            // ignore
//        }
//
////        // try executing commands （找尋有無SU檔案）
////        if (canExecuteCommand("/system/xbin/which su")
////                || canExecuteCommand("/system/bin/which su") || canExecuteCommand("which su")) {
////            return 100;
////        } else {
////            return 400;
////        }
//
//        //try root command （執行SU指令，確認是否能夠執行）
//        int retCode = execRootCmdSilent("echo test");
//        if (retCode != -1) {
//            return 100;
//        }
//
//        return 400;
//    }
//
//    /**
//     *  測試執Root cmd，若能執行表示有，若不能執行表示沒有
//     **/
//    protected static int execRootCmdSilent(String paramString) {
//        try {
//            Process localProcess = Runtime.getRuntime().exec("su");
//            Object localObject = localProcess.getOutputStream();
//            DataOutputStream localDataOutputStream = new DataOutputStream(
//                    (OutputStream) localObject);
//            String str = String.valueOf(paramString);
//            localObject = str + "\n";
//            localDataOutputStream.writeBytes((String) localObject);
//            localDataOutputStream.flush();
//            localDataOutputStream.writeBytes("exit\n");
//            localDataOutputStream.flush();
//            localProcess.waitFor();
//            int result = localProcess.exitValue();
//            return (Integer) result;
//        } catch (Exception localException) {
//            localException.printStackTrace();
//            return -1;
//        }
//    }
//
//    /**
//     *  檢查url 有無包含 <, &, SCRIPT ,
//     * @param urlString
//     * @return
//     */
//
//    public static boolean isUrlXSSCheck(String urlString) {
//        if (TextUtils.isEmpty(urlString)) {
//            return false;
//        }
//
//        try {
//            urlString = URLDecoder.decode(urlString, "UTF-8");
//
//            urlString = urlString.replace(" ", "");
//            urlString = urlString.replace("　", "");
//            if(urlString.toLowerCase().startsWith("javascript:")) {
//                return true;
//            }
//
//            if(urlString.toLowerCase().indexOf("<script") != -1) {
//                return true;
//            }
//            if(urlString.indexOf("<%") != -1) {
//                return true;
//            }
//        } catch (UnsupportedEncodingException e) {
//
//        }
//
//        return  false;
//    }

    /**
     * 圖檔儲存時的縮放倍率（以註冊guildline上寫的裁切大小為基準），
     * 2016/4/8應客戶要求，圓形直徑符合圖案大小，
     * 整個圖案長寬為72dp，圓形直徑為72dp（有定義在dimens.xml）
     */
    public static final int PhotoSaveSizeScaleRate = 2;
    public static final String NOMEDIA = ".nomedia";

    public static void checkFolderExists(){
        File folder = new File(GlobalConst.FolderPath);
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


