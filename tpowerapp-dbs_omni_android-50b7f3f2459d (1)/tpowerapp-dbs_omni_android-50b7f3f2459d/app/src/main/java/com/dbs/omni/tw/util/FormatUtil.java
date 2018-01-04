package com.dbs.omni.tw.util;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import com.dbs.omni.tw.R;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FormatUtil {
    public static final String EMAIL_INVALID_FORMAT = "^[\\w!#$%&'*+/=?`{|}~^-]+(?:\\.[\\w!#$%&'*+/=?`{|}~^-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,6}$";

    //清除金錢格式
    public static String cleanDecimalFormat(String number) {
        return number.replace(",", "");
    }

    /**
     * 將輸入數字轉成每三位數就加一個逗號的字串(不加＄符號)
     * @param number 需為integer、long、double等數值型態
     */
    public static String toDecimalFormat(Context context, Object number) {
        return toDecimalFormat(context, number, false);
    }

    /**
     * 將輸入數字轉成每三位數就加一個逗號的字串
     * @param numberString 需為String等數值型態
     * @param withSymbol 是否加＄符號
     */
    public static String toDecimalFormat(Context context, String numberString, boolean withSymbol) {
        if(TextUtils.isEmpty(numberString)) {
            return "";
        }

        Double number = Double.valueOf(numberString);
        String formatedString = "";
        if(withSymbol){
            formatedString += context.getString(R.string.amount_sign) + " ";
        }
        //運用DecimalFormat制定好金額顯示格式，每三位數顯示逗號
        DecimalFormat decimalFormat = new DecimalFormat("#,###");
        return formatedString + decimalFormat.format(number);
    }

    /**
     * 將輸入數字轉成每三位數就加一個逗號的字串
     * @param number 需為integer、long、double等數值型態
     * @param withSymbol 是否加＄符號
     */
    public static String toDecimalFormat(Context context, Object number, boolean withSymbol) {
        String formatedString = "";
        if(withSymbol){
            formatedString += context.getString(R.string.amount_sign) + " ";
        }
        //運用DecimalFormat制定好金額顯示格式，每三位數顯示逗號
        DecimalFormat decimalFormat = new DecimalFormat("#,###");
        return formatedString + decimalFormat.format(number);
    }

    /**
     * 自訂金錢單位
     * 將輸入數字轉成每三位數就加一個逗號的字串
     * @param numberString 需為integer、long、double等數值型態
     * @param symbol 是否加＄符號
     */
    public static String toDecimalFormat(Context context, String numberString, String symbol) {
        Double number = Double.valueOf(numberString);
        String formatedString = "";
        formatedString += symbol + "$ ";
        //運用DecimalFormat制定好金額顯示格式，每三位數顯示逗號
        DecimalFormat decimalFormat = new DecimalFormat("#,###");
        return formatedString + decimalFormat.format(number);
    }

    /**
     * 自訂金錢單位
     * 將輸入數字轉成每三位數就加一個逗號的字串
     * @param number 需為integer、long、double等數值型態
     * @param symbol 是否加＄符號
     */
    public static String toDecimalFormat(Context context, Object number, String symbol) {
        String formatedString = "";
        formatedString += symbol + "$ ";
        //運用DecimalFormat制定好金額顯示格式，每三位數顯示逗號
        DecimalFormat decimalFormat = new DecimalFormat("#,###");
        return formatedString + decimalFormat.format(number);
    }

//    /**
//     * 將輸入數字字串轉成每三位數就加一個逗號的字串
//     * @param numberString 數字字串
//     * @param withSymbol 是否加＄符號
//     */
//    public static String toDecimalFormatFromString(String numberString, boolean withSymbol) {
//        String formatedString = "";
//        if(withSymbol){
//            formatedString += "$ ";
//        }
//        if(TextUtils.isEmpty(numberString)){
//            return formatedString + "0";
//        }
//
//        long number = Long.parseLong(numberString);
//        //運用DecimalFormat制定好金額顯示格式，每三位數顯示逗號
//        DecimalFormat decimalFormat = new DecimalFormat("#,###");
//        return formatedString + decimalFormat.format(number);
//    }
//
//    /**
//     * 將輸入數字字串轉成每三位數就加一個逗號的字串(不加＄符號)
//     * @param numberString 數字字串
//     */
//    public static String toDecimalFormatFromString(String numberString){
//        return toDecimalFormatFromString(numberString, false);
//    }

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
     * 回傳隱碼過的手機號碼字串，例如：0922••••56
     * @param phoneNumber 電話號碼
     */
    public static String getHiddenPhoneNumber(String phoneNumber){
        if (TextUtils.isEmpty(phoneNumber)) {
            return null;
        }
        return phoneNumber.substring(0, phoneNumber.length()-6)
                + phoneNumber.substring(phoneNumber.length()-6, phoneNumber.length()-2).replaceAll(".","•")
                + phoneNumber.substring(phoneNumber.length()-2, phoneNumber.length());
    }

    /**
     * 回傳隱碼過的email字串，@前面顯示第一碼與最後一碼其餘皆隱藏，例如：G••••3@taishinbank.com.tw
     * @param email 合法的email字串
     * @return string 格式不正確時，一律回傳null
     */
    public static String getHiddenEmail(String email){
        if (email == null) {
            return null;
        }

        int indexOfAt = email.indexOf("@");
        if (indexOfAt < 3) {
            return null;
        }

        return email.substring(0, 1)
                + email.substring(1, indexOfAt-1).replaceAll(".", "•")
                + email.substring(indexOfAt-1, email.length());
    }

    /**
     * 回傳隱碼過的身分證字號，顯示前三後二，例如：A11•••••55
     * @param NID 合法的NID字串
     * @return string 格式不正確時，一律回傳null
     */
    public static String getHiddenNID(String NID){
        if (NID == null) {
            return null;
        }

        return NID.substring(0, 3)
                + NID.substring(3, 8).replaceAll(".", "•")
                + NID.substring(8, 10);
    }

    /**
     * 回傳隱碼過的密碼字串，一律回傳••••••••
     */
    public static String getHiddenPassword(){
        return "••••••••";
    }

    /**
     * 回傳隱碼過的地址
     * @param address 地址字串
     */
    public static String getHiddenAddress(String address){
        String result = "";
        String addrTemp1 = "";
        String addrTemp2 = "";

        if(address.indexOf("段") != -1){
            addrTemp1 = address.substring(0,address.indexOf("段") + 1);
            addrTemp2 = address.substring(address.indexOf("段") + 1, address.length());
        }else {
            if(address.indexOf("大道") != -1){
                addrTemp1 = address.substring(0,address.indexOf("大道") + 1);
                addrTemp2 = address.substring(address.indexOf("大道") + 1, address.length());
            }else {
                if(address.indexOf("路") != -1){
                    addrTemp1 = address.substring(0,address.indexOf("路") + 1);
                    addrTemp2 = address.substring(address.indexOf("路") + 1, address.length());
                }else {
                    if(address.indexOf("街") != -1){
                        addrTemp1 = address.substring(0,address.indexOf("街") + 1);
                        addrTemp2 = address.substring(address.indexOf("街") + 1, address.length());
                    }else {
                        if(address.indexOf("巷") != -1){
                            addrTemp1 = address.substring(0,address.indexOf("巷") + 1);
                            addrTemp2 = address.substring(address.indexOf("巷") + 1, address.length());
                        }else {
                            if (result.length() == 0) {
                                result = convertStringToStarsignEx(address);
                            }
                        }
                    }
                }
            }
        }

        if (address.length() > 0 && addrTemp2.length() > 0 ){
            result = String.format("%S%S", addrTemp1, convertStringToStarsign(addrTemp2));
        }

        return result;
    }

    private static String convertStringToStarsign(String address){
        String result = "";
        int lastLocation = -1;
        String[] keyword = {"巷", "衖", "弄", "號", "樓", "室"};

        for(int i = 0; i < keyword.length; i++){
            if(address.indexOf(keyword[i]) != -1){
                lastLocation = address.indexOf(keyword[i]);
                result = String.format("%S＊＊%S", result, keyword[i]);
            }
        }

        String lastWord = address.substring(lastLocation + 1, address.length());
        if(lastWord.length() > 0){
            result = String.format("%S＊＊", result);
        }

        return result;
    }

    private static String convertStringToStarsignEx(String address){
        String result = "";

        for(int i = 0; i < address.length(); i++){
            String tmp = address.substring(i, i+1);
            if(isNum(tmp)){
                result = String.format("%S＊＊", result);
                result.replace("＊＊＊＊", "＊＊");
            }else {
                result = String.format("%S%S", result, tmp);
            }
        }

        return result;
    }

    private static boolean isNum(String strNum) {
        boolean ret = true;
        try {
            Integer.parseInt(strNum);
        }catch (NumberFormatException e) {
            ret = false;
        }catch (Exception e){
            ret = false;
        }

        return ret;
    }

    /**
     * Transfer date format from yyyy/MM/dd to yyyyMMdd
     * @param origDate
     * @return
     */
    public static String removeDateFormatted(String origDate) {
        return origDate.replace("/", "");
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
     * Transfer date format from MMyy to MM/yy
     * @param origDate
     * @return
     */
    public static String toEffectiveDateFormatted(String origDate) {
        SimpleDateFormat sdfRead = new SimpleDateFormat("MMyy");
        SimpleDateFormat sdfWrite = new SimpleDateFormat("MM/yy");

        try {
            Date origDateRead = sdfRead.parse(origDate);
            return sdfWrite.format(origDateRead);
        } catch (ParseException e) {
            Log.w(FormatUtil.class.getSimpleName(), e.getMessage());
            return "";
        }
    }

    /**
     * Transfer date format from yyyyMM to yyyy年MM月
     * @param origDate
     * @return
     */
    public static String toDateHeaderFormatted(String origDate) {
        return toDateHeaderFormatted(origDate, true);
    }
    public static String toDateHeaderFormatted(String origDate, boolean isHeader) {
        SimpleDateFormat sdfRead = new SimpleDateFormat("yyyyMM");
        SimpleDateFormat sdfWrite;
        if(isHeader) {
            sdfWrite = new SimpleDateFormat("∙ yyyy年MM月");
        } else {
            sdfWrite = new SimpleDateFormat("yyyy年MM月");
        }

        try {
            Date origDateRead = sdfRead.parse(origDate);
            return sdfWrite.format(origDateRead);
        } catch (ParseException e) {
            Log.w(FormatUtil.class.getSimpleName(), e.getMessage());
            return "";
        }
    }

    /**
     * Transfer date format from yyyyMMdd to yyyy年MM月dd日
     * @param origDate
     * @return
     */
    public static String toDateCNFormatted(String origDate) {
        SimpleDateFormat sdfRead = new SimpleDateFormat("yyyyMMdd");
        SimpleDateFormat sdfWrite = new SimpleDateFormat("yyyy年MM月dd日");

        try {
            Date origDateRead = sdfRead.parse(origDate);
            return sdfWrite.format(origDateRead);
        } catch (ParseException e) {
            Log.w(FormatUtil.class.getSimpleName(), e.getMessage());
            return "";
        }
    }

    /**
     * Transfer date format from MMdd to MM/dd
     * @param origDate
     * @return
     */
    public static String toMonthAndDateFormatted(String origDate) {
        SimpleDateFormat sdfRead = new SimpleDateFormat("MMdd");
        SimpleDateFormat sdfWrite = new SimpleDateFormat("MM/dd");

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
     * 時間字串轉為date物件
     * @param timeStringToFormatted yyyy/MM/dd HH:mm"
     */
    public static Date TimeFormattedToDate(String timeStringToFormatted){
        SimpleDateFormat dayFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm");
        try {
            Date date = dayFormat.parse(timeStringToFormatted);
            return date;
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }

    }

    /**
     * 時間字串轉為Calendar物件
     * @param timeStringToFormatted yyyyMMdd
     */
    public static Calendar TimeFormattedToCalendar(String timeStringToFormatted){
        SimpleDateFormat dayFormat = new SimpleDateFormat("yyyyMMdd");
        try {
            Date date = dayFormat.parse(timeStringToFormatted);
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);
            return calendar;
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }

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
     * 取得格式化後的時間字串為國語格式
     * 格式：2015年12月09日 上午6:31
     * @param timeStringToFormatted 要被格式化的字串（格式為yyyyMMddHHmmss）
     */
    public static String toTimeFormattedForChinese(Context context, String timeStringToFormatted) {
        return toTimeFormattedForChinese(context, timeStringToFormatted, false);
    }

    /**
     * 取得格式化後的時間字串為國語格式
     * 格式：2015年12月09日 上午6:31
     * @param timeStringToFormatted 要被格式化的字串（格式為yyyyMMddHHmmss）若為停留時間格式為HH:mm
     * @param isStayTime 輸入是否為停留的時間 輸出格式為：20小時05分
     */
    public static String toTimeFormattedForChinese(Context context, String timeStringToFormatted, boolean isStayTime) {
        if (TextUtils.isEmpty(timeStringToFormatted)) {
            return "";
        }
        if(isStayTime) {
            return String.format("%s分", timeStringToFormatted.replace(":","小時"));
        } else {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");

            try {
                Calendar calendar = sdf.getCalendar();
                int am = calendar.get(Calendar.AM_PM);

                Date timeToFormatted = sdf.parse(timeStringToFormatted);
                SimpleDateFormat dayFormat = new SimpleDateFormat(context.getString(R.string.date_time_format_for_region));

                return dayFormat.format(timeToFormatted);
            } catch (Exception e) {
                e.printStackTrace();
                return "";
            }
        }



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
     * 將卡號遮蔽
     * @param huma
     */
    public static String toHideCardNumberString(String huma) {
        if(!TextUtils.isEmpty(huma)) {
            if(huma.indexOf("-") != -1){
                String[] humaArray = huma.split("-");
                //(6)信用卡：保留卡號前六碼與後四碼，其餘皆隱藏(如：4344-11**-****-8970)
                return humaArray[0] + "-" + humaArray[1].substring(0, 2) + "••-••••-" + humaArray[3];
            } else {
                return String.format("%s-%s••-••••-%s", huma.substring(0,4), huma.substring(4,6), huma.substring(12,16));
            }
        } else {
            return huma;
        }
    }

    /**
     * 將卡號 1111-2222-3333-4444 -> 111122223333344444
     * @param creditCardNumber
     */
    public static String removeCreditCardFormat(String creditCardNumber) {
        if(!TextUtils.isEmpty(creditCardNumber) && creditCardNumber.length() >= 16) {
            return creditCardNumber.replace("-", "");
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
     * 將輸入卡號轉成顯示後四碼  ••••-1111
     * @param humaString 數字字串
     */
    public static String toHideCardNumberShortString(String humaString) {
        return toHideCardNumberShortString(humaString, false);
    }

    public static String toHideCardNumberShortString(String humaString , boolean hasBrackets) {
        String formatedString = "";

        String humaHide = humaString.substring(humaString.length()-4).toString();

        if(hasBrackets) {
            return formatedString + "(••••-" + humaHide + ")";
        } else {
            return formatedString + "••••-" + humaHide + "";
        }
    }

    public static String toTemperatureString(float temperature){

        return String.format("%.01f°", temperature);
    }

    // 信用卡卡號 標題，含格式
    public static String getCreditCardTile(Context context, boolean hasHint) {
        if(hasHint)
            return String.format("%1$s  %2$s", context.getString(R.string.credit_card_number_title), context.getString(R.string.credit_card_number_hint));
        else
            return context.getString(R.string.credit_card_number_title);
    }

    //  有效期限 含格式 標題
    public static String getEffectiveDate(Context context, boolean hasHint) {
        if(hasHint)
            return String.format("%1$s %2$s", context.getString(R.string.effective_date), context.getString(R.string.effective_date_hint));
        else
            return context.getString(R.string.effective_date);
    }

    // 生日 含格式 標題
    public static String getDateOfBirthTile(Context context, boolean hasHint) {
        if(hasHint)
            return String.format("%1$s %2$s", context.getString(R.string.date_of_birth), context.getString(R.string.date_format_of_birth));
        else
            return context.getString(R.string.date_of_birth);
    }

    // 暱稱 含格式 標題
    public static String getNickNameTile(Context context, boolean hasHint) {
        if(hasHint)
            return String.format("%1$s %2$s", context.getString(R.string.user_nickname_title), context.getString(R.string.user_nickname_hint));
        else
            return context.getString(R.string.user_nickname_title);
    }

    public static String getPaymentAmountSubTitle(Context context, boolean isMax) {
        if(isMax) {
            return context.getString(R.string.all_amount_title);
        } else {
            return context.getString(R.string.min_amount_title);
        }
    }



    public static String toConsumptionDecimalFormat(Context context, String amt) {
        return toConsumptionDecimalFormat(context, amt, null);
    }

    public static String toConsumptionDecimalFormat(Context context, String amt, String symbol) {
        if(TextUtils.isEmpty(amt)) {
            return "";
        }

        if(TextUtils.isEmpty(symbol)) {
            return FormatUtil.toDecimalFormat(context, amt, true);
        } else {
            return FormatUtil.toDecimalFormat(context, amt, symbol);
        }
    }

    public static String toConsumptionData(Context context, String date, String postdate) {
        if(!TextUtils.isEmpty(date)) {
            date = FormatUtil.toMonthAndDateFormatted(date.substring(4));
        }

        if(!TextUtils.isEmpty(postdate)) {
            postdate = FormatUtil.toMonthAndDateFormatted(postdate.substring(4));
        }

        if(TextUtils.isEmpty(date) && TextUtils.isEmpty(postdate)) {
            return "";
        } else if(TextUtils.isEmpty(date) && !TextUtils.isEmpty(postdate)) {
            return String.format("%1$s %2$s", context.getString(R.string.entry_date), postdate);
        } else if(!TextUtils.isEmpty(date) && TextUtils.isEmpty(postdate)) {
            return String.format("%1$s %2$s", context.getString(R.string.transaction_date), date);
        } else {
            return String.format("%1$s %2$s - %3$s %4$s", context.getString(R.string.transaction_date), date, context.getString(R.string.entry_date), postdate);
        }
    }
}
