package tw.com.taishinbank.ewallet.util;

public class DateTimeUtil {

    public static String convertMilliSecondsToMmSs(long milliseconds) {
        milliseconds /= 1000;
        long second = milliseconds % 60;
        long minute = (milliseconds / 60) % 60;
//        long h = (milliseconds / (60 * 60));
//        return String.format("%d:%02d:%02d", h,m,s);
        return String.format("%02d:%02d", minute, second);
    }
}
