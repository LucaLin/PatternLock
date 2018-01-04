package com.dbs.omni.tw.util;

import android.content.Context;

import com.dbs.omni.tw.R;
import com.dbs.omni.tw.controller.ActivityBase;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by siang on 2017/6/12.
 */

public class ValidateUtil {

    public static boolean isLengthLimit(String string, int minLength, int maxLength) {
        int length = string.length();

        if(length < minLength) {
            return false;
        } else if(length > maxLength) {
            return false;
        } else {
            return true;
        }

    }

    public static boolean isOnlyNumber(String string) {
        String matchesString = String.format("[0-9]{%d}", string.length());

        if(string.length() == 0) {
            return false;
        }

        if(string.matches(matchesString)) {
            return true;
        } else {
            return false;
        }
    }

    // 三位 相同且連續英文字或數字
    public static boolean hasSameAndContinuousChar(String string) {

        char[] chars = string.toCharArray();

        for(int i = 0; i <= chars.length - 3 ; i++) {
            char one = chars[i];
            char two = chars[i+1];
            char three = chars[i+2];

            if( one == two && two == three && one == three) {
                return true;
            }
        }

        return false;
    }

    public static boolean hasSpecialORSpace(String string) {
        char[] chars = string.toCharArray();

        for(int i = 0; i < chars.length ; i++) {
            if(!Character.isLetterOrDigit(string.charAt(i))) {
                return true;
            }
        }

        return false;
    }

    public static boolean checkBirthday(String dateString) {
        SimpleDateFormat sdfRead = new SimpleDateFormat("yyyy/MM/dd");
        Date birthdayDate;
        try {
            sdfRead.setLenient(false);
            birthdayDate = sdfRead.parse(dateString);
        } catch (ParseException e) {
            return false;
        }


        Date nowDate = Calendar.getInstance().getTime();

        if(nowDate.after(birthdayDate)) {
            return true;
        } else {
            return false;
        }
    }

    public static boolean checkBirthday(Context context, String dateString, boolean isShowAlert) {
        if(isShowAlert) {
            if(checkBirthday(dateString)) {
                return true;
            } else {
                ((ActivityBase) context).showAlertDialog(context.getString(R.string.validate_birthday_fail));
                return false;
            }
        } else {
            return checkBirthday(dateString);
        }
    }

    public static boolean checkCreditCardNumber(String string) {
        string = string.replace("-", "");
        if(string.length() == 16) { //  卡號有16個數字

            if(isOnlyNumber(string)) {
                return true;
            } else {
                return false;
            }

        } else {
            return false;
        }
    }

    public static boolean checkCreditCardNumber(Context context, String string, boolean isShowAlert) {
        if(isShowAlert) {
            if(checkCreditCardNumber(string)) {
                return true;
            } else {
                ((ActivityBase) context).showAlertDialog(context.getString(R.string.validate_credit_card_number_fail));
                return false;
            }
        } else {
            return checkCreditCardNumber(string);
        }
    }

    public static boolean checkEffectiveDate(String string) {
        SimpleDateFormat formatter =
                new SimpleDateFormat("MM/yy");
        Calendar expiryDateDate = Calendar.getInstance();
        try {
            expiryDateDate.setTime(formatter.parse(string));
        } catch (ParseException e) {
            return false;
        }
        return true;
    }

    public static boolean checkEffectiveDate(Context context, String string, boolean isShowAlert) {
        if(isShowAlert) {
            if(checkEffectiveDate(string)) {
                return true;
            } else {
                ((ActivityBase) context).showAlertDialog(context.getString(R.string.validate_effective_date_fail));
                return false;
            }
        } else {
            return checkEffectiveDate(string);
        }
    }
}
