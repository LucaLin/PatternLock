package tw.com.taishinbank.ewallet.util;

import android.text.TextUtils;
import android.util.Log;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FormatUtil {
    public static final String EMAIL_INVALID_FORMAT = "^[\\w!#$%&'*+/=?`{|}~^-]+(?:\\.[\\w!#$%&'*+/=?`{|}~^-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,6}$";

    /**
     * 將卡號遮蔽
     * @param creditCardNumber
     */
    public static String toCreditCardShelterFormat(String creditCardNumber) {
        if(!TextUtils.isEmpty(creditCardNumber)) {
            if(creditCardNumber.indexOf("-") != -1){
                String[] cardNumber = creditCardNumber.split("-");
                //(6)信用卡：保留卡號前六碼與後四碼，其餘皆隱藏(如：4344-11**-****-8970)
                return cardNumber[0] + "-" + cardNumber[1].substring(0, 2) + "**-****-" + cardNumber[3];
            } else {
                return String.format("%s-%s**-****-%s", creditCardNumber.substring(0,4), creditCardNumber.substring(4,6), creditCardNumber.substring(12,16));
            }
        } else {
            return creditCardNumber;
        }
    }

    /**
     * 將卡號 111122223333344444 -> 1111-2222-3333-4444
     * @param creditCardNumber
     */
    public static String toCreditCardFormat(String creditCardNumber) {
        if(!TextUtils.isEmpty(creditCardNumber) && creditCardNumber.length() == 16) {
            return String.format("%s-%s-%s-%s", creditCardNumber.substring(0,4), creditCardNumber.substring(4,8),creditCardNumber.substring(8,12), creditCardNumber.substring(12,16));
        } else {
            return creditCardNumber;
        }
    }

    /**
     * 將輸入數字轉成每三位數就加一個逗號的字串(不加＄符號)
     * @param number 需為integer、long、double等數值型態
     */
    public static String toDecimalFormat(Object number) {
        return toDecimalFormat(number, false);
    }

    /**
     * 將輸入數字轉成每三位數就加一個逗號的字串
     * @param number 需為integer、long、double等數值型態
     * @param withSymbol 是否加＄符號
     */
    public static String toDecimalFormat(Object number, boolean withSymbol) {
        String formatedString = "";
        if(withSymbol){
            formatedString += "$ ";
        }
        //運用DecimalFormat制定好金額顯示格式，每三位數顯示逗號
        DecimalFormat decimalFormat = new DecimalFormat("#,###");
        return formatedString + decimalFormat.format(number);
    }

    /**
     * 將輸入數字字串轉成每三位數就加一個逗號的字串
     * @param numberString 數字字串
     * @param withSymbol 是否加＄符號
     */
    public static String toDecimalFormatFromString(String numberString, boolean withSymbol) {
        String formatedString = "";
        if(withSymbol){
            formatedString += "$ ";
        }
        if(TextUtils.isEmpty(numberString)){
            return formatedString + "0";
        }

        long number = Long.parseLong(numberString);
        //運用DecimalFormat制定好金額顯示格式，每三位數顯示逗號
        DecimalFormat decimalFormat = new DecimalFormat("#,###");
        return formatedString + decimalFormat.format(number);
    }

    /**
     * 將輸入數字字串轉成每三位數就加一個逗號的字串(不加＄符號)
     * @param numberString 數字字串
     */
    public static String toDecimalFormatFromString(String numberString){
        return toDecimalFormatFromString(numberString, false);
    }

    /**
     * 回傳是否為正確的輸入格式
     */
    public static boolean isCorrectFormat(CharSequence userId, String inputFormatRegularExpression){
        // 如果格式是空的表示不檢查
        if(inputFormatRegularExpression == null || inputFormatRegularExpression.equals("")){
            return true;
        }

        Pattern pattern = Pattern.compile(inputFormatRegularExpression);
        Matcher matcher = pattern.matcher(userId);
        return matcher.matches();
    }

    /**
     * 回傳隱碼過的手機號碼字串，例如：****-**1-234
     * @param phoneNumber 長度為10個字元的電話號碼
     */
    public static String getEncodedCellPhoneNumber(String phoneNumber){
        if (TextUtils.isEmpty(phoneNumber)) {
            return null;
        }
        return "****-**" + phoneNumber.substring(6,7) + "-" + phoneNumber.substring(7, phoneNumber.length());
    }

    /**
     * 回傳隱碼過的email字串，@前面顯示最後一碼其餘皆隱藏，例如：XXXX3@taishinbank.com.tw
     * @param email 合法的email字串
     * @return string 格式不正確時，一律回傳null
     */
    public static String getEncodedEmail(String email){
        if (email == null) {
            return null;
        }

        int indexOfAt = email.indexOf("@");
        if (indexOfAt < 0) {
            return null;
        }

        return email.substring(0, indexOfAt-1).replaceAll(".", "*") + email.substring(indexOfAt-1, email.length());
    }

    /**
     * 回傳格式化過的手機號碼字串，例如：0912-345-678
     * @param phoneNumber 長度為10個字元的電話號碼
     */
    public static String toCellPhoneNumberFormat(String phoneNumber){
        int[] dashInsertPositions = {4, 7, 10};
        return insertDashOnPositions(phoneNumber, dashInsertPositions);
    }

    /**
     * Transfer date format from yyyyMMdd to yyyy/MM/dd
     * @param origDate
     * @return
     */
    public static String toDateFormatted(String origDate) {
        SimpleDateFormat sdfRead = new SimpleDateFormat("yyyyMMdd");
        SimpleDateFormat sdfWrite = new SimpleDateFormat("yyyy/MM/dd");

        try {
            Date origDateRead = sdfRead.parse(origDate);
            return sdfWrite.format(origDateRead);
        } catch (ParseException e) {
            Log.w(FormatUtil.class.getSimpleName(), e.getMessage());
            return "";
        }
    }

    /**
     * 取得格式化後的時間字串
     * @param date 要被格式化的
     */
    public static String toTimeFormatted(Date date){
        SimpleDateFormat dayFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm");
        return dayFormat.format(date);
    }

    /**
     * 取得格式化後的時間字串
     * 距離發生時間
     * 少於一小時：顯示x分鐘前
     * 少於一天：顯示x小時前
     * 超過一天：顯示日期+時間  格式：2015/12/09 16:31
     * @param timeStringToFormatted 要被格式化的字串（格式為yyyyMMddHHmmss）
     */
    public static String toTimeFormatted(String timeStringToFormatted){
        return toTimeFormatted(timeStringToFormatted, true);
    }

    /**
     * 取得格式化後的時間字串
     * 距離發生時間
     * 少於一小時：顯示x分鐘前
     * 少於一天：顯示x小時前
     * 超過一天：顯示日期+時間  格式：2015/12/09 16:31
     * @param timeStringToFormatted 要被格式化的字串（格式為yyyyMMddHHmmss）
     * @param isEllipse 是否要轉成　x分鐘/x小時
     */
    public static String toTimeFormatted(String timeStringToFormatted, boolean isEllipse) {
        if (TextUtils.isEmpty(timeStringToFormatted)) {
            return "";
        }
        final String TAG = "toTimeFormatted";
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");

        if (!isEllipse) {
            try {
                Date timeToFormatted = sdf.parse(timeStringToFormatted);

                SimpleDateFormat dayFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm");
                return dayFormat.format(timeToFormatted);
            } catch (Exception e) {
                e.printStackTrace();
                return "";
            }
        }


        try {
            // 將傳入時間字串轉成date物件
            Date timeToFormatted = sdf.parse(timeStringToFormatted);

            // 取得現在時間
            Calendar c = Calendar.getInstance();
            // 計算時間差異
            long diff = c.getTimeInMillis() - timeToFormatted.getTime();
            long secondsInMilli = 1000;
            long minutesInMilli = secondsInMilli * 60;
            long hoursInMilli = minutesInMilli * 60;
            long daysInMilli = hoursInMilli * 24;

            // 換算成天數
            long elapsedDays = diff / daysInMilli;
            // 超過一天或未來時間：顯示日期+時間  格式：2015/12/09 16:31
            if(elapsedDays > 0 || diff < 0){
                Log.d(TAG, timeStringToFormatted + " = " + elapsedDays + "day(s)");
                SimpleDateFormat dayFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm");
                return dayFormat.format(timeToFormatted);
            }

            // 換算成小時數
            diff = diff % daysInMilli;
            long elapsedHours = diff / hoursInMilli;
            // 少於一天：顯示x小時前
            if(elapsedHours > 0){
                Log.d(TAG, timeStringToFormatted + " = " + elapsedHours + "hour(s)");
                return elapsedHours + "小時前";
            }

            // 換算成分鐘數
            diff = diff % hoursInMilli;
            long elapsedMinutes = diff / minutesInMilli;
            Log.d(TAG, timeStringToFormatted + " = " + elapsedMinutes + "min(s)");
            // 少於一小時：顯示x分鐘前
            return elapsedMinutes + "分鐘前";

        } catch (ParseException e) {
            e.printStackTrace();
            return "";
        }
    }

    /**
     * 回傳隱碼過的手機號碼字串，例如：0000-000-000-0000
     * @param account 長度為14個字元的帳號
     */
    public static String toAccountFormat(String account){
        int[] dashInsertPositions = {4, 7, 10};
        return insertDashOnPositions(account, dashInsertPositions);
    }

    /**
     * 回傳在特定位置插入「-」的字串
     * @param originalString 原始字串
     * @param dashInsertPositions 要插入「-」的位置，例如電話格式為xxxx-xxx-xxx，就傳入{4, 7}
     */
    private static String insertDashOnPositions(String originalString, int[] dashInsertPositions){
        // 如果不是空的才做插入的判斷
        if(!TextUtils.isEmpty(originalString)) {
            // 初始上一個插入位置為0
            int lastDashInsertPosition = 0;
            String formattedAccount = "";
            // 用迴圈判斷與插入
            for (int i = 0; i < dashInsertPositions.length; i++) {
                // 如果字串長度 > 目前要插入的位置，表示可以插入
                if (originalString.length() > dashInsertPositions[i]) {
                    formattedAccount += originalString.substring(lastDashInsertPosition, dashInsertPositions[i]) + "-";
                    lastDashInsertPosition = dashInsertPositions[i];
                } else {
                    // 否則離開迴圈
                    break;
                }
            }
            // 加入上一個插入位置到字串結尾的字串
            formattedAccount += originalString.substring(lastDashInsertPosition, originalString.length());
            return formattedAccount;
        }
        return originalString;
    }

    /**
     * 將輸入卡號轉成顯示後四碼
     * @param numberString 數字字串
     */
    public static String toHideCardNumberString(String numberString) {
        String formatedString = "";

        String fNumber = numberString.substring(numberString.length()-4).toString();

        //運用DecimalFormat制定好金額顯示格式，每三位數顯示逗號
        return formatedString + "(..." + fNumber + ")";
    }


}
