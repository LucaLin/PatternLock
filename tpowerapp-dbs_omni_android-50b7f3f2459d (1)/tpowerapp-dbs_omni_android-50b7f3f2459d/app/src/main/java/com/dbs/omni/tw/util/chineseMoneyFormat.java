package com.dbs.omni.tw.util;

/**
 * Created by sherman-thinkpower on 2017/6/8.
 */

public class chineseMoneyFormat {

    private static final char[] MONEY_DIGITS = {
            '零', '一', '二', '三', '四', '五', '六', '七', '八', '九' };

    private static final String[] SECTION_CHARS = {
            "", "十", "百", "千", "萬" };

    public static String toChineseMoneyUpper(long price) {
        price = Math.round(price * 100) / 100;

        StringBuilder sb = new StringBuilder();

        long integerPart = price;
        int wanyiPart = (int)(integerPart / 1000000000000L);
        int yiPart = (int)(integerPart % 1000000000000L / 100000000L);
        int wanPart = (int)(integerPart % 100000000L / 10000L);
        int qianPart = (int)(integerPart % 10000L);
//        int decPart = (int)(price * 100 % 100);

        int zeroCount = 0;
        //兆
        if (integerPart >= 1000000000000L && wanyiPart > 0) {
            zeroCount = parseInteger(sb, wanyiPart, true, zeroCount);
            sb.append("兆");
        }

        //億
        if (integerPart >= 100000000L && yiPart > 0) {
            boolean isFirstSection = integerPart >= 100000000L && integerPart < 1000000000000L;
            zeroCount = parseInteger(sb, yiPart, isFirstSection, zeroCount);
            sb.append("億");
        }

        //萬
        if (integerPart >= 10000L && wanPart > 0) {
            boolean isFirstSection = integerPart >= 1000L && integerPart < 10000000L;
            zeroCount = parseInteger(sb, wanPart, isFirstSection, zeroCount);
            sb.append("萬");
        }

        //千
        if (qianPart > 0) {
            boolean isFirstSection = integerPart < 1000L;
            zeroCount = parseInteger(sb, qianPart, isFirstSection, zeroCount);
        }
        else {
            zeroCount += 1;
        }

        if (integerPart > 0) {
            sb.append("元");
        }

//        //角分
//        if (decPart > 0) {
//            parseDecimal(sb, integerPart, decPart, zeroCount);
//        }
//        else if (decPart <= 0 && integerPart > 0) {
//            sb.append("元");
//        }
//        else {
//            sb.append("元");
//        }

        return sb.toString();
    }

//    private static void parseDecimal(StringBuilder sb, long integerPart, int decPart, int zeroCount) {
//        assert decPart > 0 && decPart <= 99;
//        int jiao = decPart / 10;
//        int fen = decPart % 10;
//
//        if (zeroCount > 0 && (jiao > 0 || fen > 0) && integerPart > 0) {
//            sb.append("又");
//        }
//
//        if (jiao > 0) {
//            sb.append(RMB_DIGITS[jiao]);
//            sb.append("角");
//        }
//        if (zeroCount == 0 && jiao == 0 && fen > 0 && integerPart > 0) {
//            sb.append("又");
//        }
//        if (fen > 0) {
//            sb.append(RMB_DIGITS[fen]);
//            sb.append("分");
//        }
//        else {
//            sb.append("元");
//        }
//    }

    private static int parseInteger(StringBuilder sb, int integer, boolean isFirstSection, int zeroCount) {
        assert integer > 0 && integer <= 9999;
        int nDigits = (int)Math.floor(Math.log10(integer)) + 1;
        if (!isFirstSection && integer < 1000) {
            zeroCount++;
        }
        for (int i = 0; i < nDigits; i++) {
            int factor = (int)Math.pow(10, nDigits - 1 - i);
            assert factor > 0;
            int digit = (int)(integer / factor);
            assert digit >= 0 && digit <= 9;
            if (digit > 0) {
                if (zeroCount > 0) {
                    sb.append("零");
                }
                sb.append(MONEY_DIGITS[digit]);
                sb.append(SECTION_CHARS[nDigits - i - 1]);
                zeroCount = 0;
            }
            else {
                zeroCount++;
            }
            integer -= integer / factor * factor;
        }
        return zeroCount;
    }
}
