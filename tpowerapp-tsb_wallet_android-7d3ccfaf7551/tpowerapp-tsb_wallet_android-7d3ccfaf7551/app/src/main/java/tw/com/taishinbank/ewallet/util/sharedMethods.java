package tw.com.taishinbank.ewallet.util;

/**
 * Created by peterliu on 2015/11/18.
 */

import android.text.TextUtils;
import android.util.Base64;

import java.io.File;
import java.io.FileFilter;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.spec.AlgorithmParameterSpec;
import java.util.Arrays;
import java.util.Random;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import tw.com.taishinbank.ewallet.interfaces.GlobalConst;

public class sharedMethods {

    //AES 128bit CBC,
    private final static String ivString = "tsbewalletaes128" ;
    private final static String keyString = "aes128tsbewallet";

    public static String AESEncrypt(String textString) throws UnsupportedEncodingException {
        if(TextUtils.isEmpty(textString)){
            return null;
        }

        byte[] ivBytes = null;
        byte[] keyBytes = null;
        byte[] textBytes =null;

        ivBytes = ivString.getBytes("UTF-8");
        keyBytes = keyString.getBytes("UTF-8");
        textBytes = textString.getBytes("UTF-8");

        byte [] resultByte = AESEncryptFunc(ivBytes , keyBytes, textBytes);
        String resultString = null;
        if(resultByte != null) {
            resultString = Base64.encodeToString(resultByte, Base64.NO_WRAP);
        }
        return resultString;
    }

    //AES加密，帶入byte[]型態的16位英數組合文字、32位英數組合Key、需加密文字
    private static byte[] AESEncryptFunc(byte[] iv, byte[] key,byte[] text)
    {
        try
        {
            AlgorithmParameterSpec mAlgorithmParameterSpec = new IvParameterSpec(iv);
            SecretKeySpec mSecretKeySpec = new SecretKeySpec(key, "AES");
            Cipher mCipher = null;
            mCipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            mCipher.init(Cipher.ENCRYPT_MODE,mSecretKeySpec,mAlgorithmParameterSpec);

            return mCipher.doFinal(text);
        }
        catch(Exception ex)
        {
            return null;
        }
    }


    public static String AESDecrypt(String textString) throws UnsupportedEncodingException {
        if(TextUtils.isEmpty(textString)){
            return null;
        }

        byte[] ivBytes = null;
        byte[] keyBytes = null;
        byte[] textBytes =null;

        ivBytes = ivString.getBytes("UTF-8");
        keyBytes = keyString.getBytes("UTF-8");
        textBytes = Base64.decode(textString.getBytes("UTF-8"), Base64.NO_WRAP);

        byte[] resultByte = AESDecryptFunc(ivBytes, keyBytes, textBytes);
        String resultString = null;
        if(resultByte != null) {
            resultString = new String(resultByte, "UTF-8");
        }
        return resultString;
    }


    //AES解密，帶入byte[]型態的16位英數組合文字、32位英數組合Key、需解密文字
    private static byte[] AESDecryptFunc(byte[] iv,byte[] key,byte[] text)
    {
        try
        {
            AlgorithmParameterSpec mAlgorithmParameterSpec = new IvParameterSpec(iv);
            SecretKeySpec mSecretKeySpec = new SecretKeySpec(key, "AES");
            Cipher mCipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            mCipher.init(Cipher.DECRYPT_MODE,
                    mSecretKeySpec,
                    mAlgorithmParameterSpec);

            return mCipher.doFinal(text);
        }
        catch(Exception ex)
        {
            return null;
        }
    }


    /*財神紅包分配邏輯：
    1.tmp總額 = 總額-最低金額*人數
    2.計算總額 = tmp總額 / 10（避免尾數為4，後面再乘回來）
    3.從計算總額產一個亂數 *10 +最低金額 = 一個紅包
    4.計算總額 - 紅包 若不足，剩下的人均拿最低金額
    其他處理：總額尾數不為0、
    */
    public static String[] getRandomMoney(String totalMoney, String minMoney, String manCount, String perMoneyLimit) {
        int totalMoneyInt = Integer.valueOf(totalMoney);
        int tmpMinMoneyInt = Integer.valueOf(minMoney);
        int minMoneyInt;
        int manCountInt = Integer.valueOf(manCount);
        int perMoneyLimitInt = Integer.valueOf(perMoneyLimit);
        Random dice = new Random();
        int[] result = new int[manCountInt];
        boolean needArrangeSingleDigitNumber = false;

        if (perMoneyLimitInt > GlobalConst.FORCE_MAX_SINGLE_LIMIT){
            perMoneyLimitInt = GlobalConst.FORCE_MAX_SINGLE_LIMIT;
        }
        //總額 >= 人數＊單筆限額，大家都發限額
        if (manCountInt *perMoneyLimitInt <= totalMoneyInt) {
            for (int i =0 ; i<manCountInt ; i++){
                result[i] = perMoneyLimitInt;
            }
            return Arrays.toString(result).split("[\\[\\]]")[1].split(", ");
        }
        else if(manCountInt == 1){
            result[0] = totalMoneyInt;
            return Arrays.toString(result).split("[\\[\\]]")[1].split(", ");
        }

        if (tmpMinMoneyInt < 10){
            tmpMinMoneyInt = 10;
        }

        if (totalMoneyInt <= manCountInt*(tmpMinMoneyInt/10 +1)*10){
            minMoneyInt = Integer.valueOf(minMoney);
            int countBase10 = (totalMoneyInt-(minMoneyInt*manCountInt));
            int restBase = countBase10;
            int money10 = 0;
            int tmpResult10 = 0;
            int restMoney10 = totalMoneyInt;

            for (int i =0 ; i<manCountInt ; i++) {
                if (i == manCountInt-1) {
                    //獎金歸給剩下的最後一個人
                    tmpResult10 = restMoney10;
                }
                else if (restBase == 0){
                    tmpResult10 = minMoneyInt;
                    restMoney10 = restMoney10 - tmpResult10;
                }
                else {
                    money10 = dice.nextInt(countBase10);//抽到的金額基數
                    while ((money10 + minMoneyInt) % 10 == 4){
                        //避開尾數4
                        money10 = dice.nextInt(countBase10);
                    }
                    if (restBase < money10){
                        tmpResult10 = restBase + minMoneyInt;
                        restBase = 0;
                    }
                    else {
                        tmpResult10 = money10 + minMoneyInt;
                        restBase = restBase - money10;
                    }
                    restMoney10 = restMoney10 - tmpResult10;
                }
                result[i] = tmpResult10;
            }
            Arrays.sort(result);
            return Arrays.toString(result).split("[\\[\\]]")[1].split(", ");
        }

        //最低金額尾數不為零的處理(無條件捨棄個位數)
        if (tmpMinMoneyInt%10 != 0){
            minMoneyInt = (tmpMinMoneyInt/10 +1)*10;
        }
        else{
            minMoneyInt = tmpMinMoneyInt;
        }

        //原始總額尾數不為零的處理
        int restOnSingleDigitNumber = totalMoneyInt%10;
        if (restOnSingleDigitNumber != 0)
            needArrangeSingleDigitNumber = true;


        int countBase = (totalMoneyInt-(minMoneyInt*manCountInt))/10;
        int money = 0;
        int tmpResult = 0;
        int restMoney = countBase;

        for (int i =0 ; i<manCountInt ; i++) {
            if (i == manCountInt-1 && restMoney >0) {
                //獎金歸給剩下的最後一個人
                tmpResult = restMoney *10 + minMoneyInt;
            }
            else if(restMoney <= 0) {
            //獎金已被抽完，大家都拿基數
                tmpResult = minMoneyInt ;
            }
            else {
                money = dice.nextInt(countBase);//抽到的基數
                while(money * 10 + minMoneyInt > perMoneyLimitInt){
                    //超過單筆上限就重抽
                    money = dice.nextInt(countBase);
                }
                if ((restMoney - money) <0){
                    //抽到的基數大於剩餘基數
                    tmpResult = restMoney *10 + minMoneyInt;
                    restMoney = 0;
                }
                else{
                    tmpResult = money *10 + minMoneyInt;
                    restMoney = restMoney - money;//餘額的獎金基數
                }
            }
            //處理原始總額尾數不為零（每筆+1直到為0）
            if (needArrangeSingleDigitNumber && restOnSingleDigitNumber > 0) {
                if (i == manCountInt -2 && restOnSingleDigitNumber == 5){
                    tmpResult = tmpResult + 2;
                    restOnSingleDigitNumber = restOnSingleDigitNumber - 2;
                }
                else if (i == manCountInt -1){
                    tmpResult = tmpResult + restOnSingleDigitNumber;
                }
                else{
                    tmpResult = tmpResult + 1;
                    restOnSingleDigitNumber = restOnSingleDigitNumber - 1;
                }
            } else {
                needArrangeSingleDigitNumber = false;
            }
            result[i] = tmpResult;
        }
        Arrays.sort(result);

        //若最高金額超過單筆限額，最高一筆用限額，剩下的重配
        int biggestNumbert = result[result.length -1];
        if (biggestNumbert > perMoneyLimitInt){
            int[] newResult = new int[manCountInt];
            String[] newTmp = new String[manCountInt-1];
            //第一筆加限額
            newResult[0] = perMoneyLimitInt;
            //剩下的重配
            String newTotal = String.valueOf(totalMoneyInt - perMoneyLimitInt);
            String newManCount = String.valueOf(manCountInt - 1);
            newTmp = sharedMethods.getRandomMoney(newTotal, minMoney, newManCount, perMoneyLimit);
            for (int i =0; i < newTmp.length; i ++){
                int moneyInt = Integer.valueOf(newTmp[i]);
                newResult[i+1] = moneyInt;
            }
            Arrays.sort(newResult);
            return Arrays.toString(newResult).split("[\\[\\]]")[1].split(", ");
        }
        else {
            return Arrays.toString(result).split("[\\[\\]]")[1].split(", ");
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

    public static String MD5Encrypt(String s) {
        try {
            // Create MD5 Hash
            MessageDigest digest = MessageDigest
                    .getInstance("MD5");
            digest.update(s.getBytes());
            byte messageDigest[] = digest.digest();

            // Create Hex String
            StringBuilder hexString = new StringBuilder();
            for (byte aMessageDigest : messageDigest) {
                String h = Integer.toHexString(0xFF & aMessageDigest);
                while (h.length() < 2)
                    h = "0" + h;
                hexString.append(h);
            }
            return hexString.toString();

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return "";
    }

    //Find Merchant Group Logo icon file
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
}
