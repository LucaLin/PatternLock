package tw.com.taishinbank.ewallet.controller;

import android.app.Application;
import android.text.TextUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import tw.com.taishinbank.ewallet.model.SVAccountInfo;
import tw.com.taishinbank.ewallet.model.creditcard.CreditCardData;
import tw.com.taishinbank.ewallet.util.PreferenceUtil;

public class WalletApplication extends Application {

    public ArrayList<CreditCardData> CreditCardList;
    public static ArrayList<String> GlobalHeadImageList;
    private SVAccountInfo svAccountInfo;

    @Override
    public void onCreate() {
        super.onCreate();
        WalletApplication.GlobalHeadImageList = new ArrayList<>();
    }

    /**
     * 回傳距離上次登入儲值帳戶是否超過指定時間（10分鐘）
     */
    public boolean isSVLoginTimeExpired(){
        String svLoginTime = PreferenceUtil.getSVLoginTime(this);
        if(!TextUtils.isEmpty(svLoginTime)){
            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
            try {
                // 將上次登入時間字串轉成date物件
                Date lastLoginTime = sdf.parse(svLoginTime);

                // 取得現在時間
                Calendar c = Calendar.getInstance();
                // 判斷是否已經超過上次登入時間10分鐘
                c.add(Calendar.MINUTE, -10);
                if(lastLoginTime.after(c.getTime())){
                    return false;
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        return true;
    }

    public boolean needSVLogin(){
        svAccountInfo = PreferenceUtil.getSVAccountInfo(this);
        return !(svAccountInfo != null && !isSVLoginTimeExpired());
    }
}
