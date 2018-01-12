package com.example.r30_a.testlayout.cellBean;

import android.app.Activity;
import android.app.Application;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;

import java.util.List;



/**
 * Created by R30-A on 2017/12/14.
 */

public class PatternHelper {
    public static final String key = "key";
    //最多可連接的小球數

    //重試的次數
    public static final int MAXTimes = 5;

    private int times;
    private boolean isFinish;
    private boolean isOK;

    private String message;
    private String storagePwd;//存取密碼用
    private String tmpPwd;//存取暫時的密碼用
    private String convertPwd="";

//設定圖形碼的地方，這裡需繪製兩次
    public void forsetting(List<Integer> hitList){
        this.isFinish = false;
        this.isOK = false;
        CellBeanSettingActivity.savepwd = "";
        //如果點到的球數小於最低要求
        if((hitList == null) || (hitList.size() < CellSettingPageActivity.RangeBall)){
            this.tmpPwd =null;
            this.message = getSizeErrorMsg();
            return;
        }
        //如果成功的話，第一次
        if(this.tmpPwd == null|| this.tmpPwd.length()==0){
            this.tmpPwd = convertToString(hitList);
            this.message = getReDrawMsg();
            this.isOK = true;
            return;
        }
        //畫第二次圖形，判斷是否跟第一次相同
        if(this.tmpPwd.equals(convertToString(hitList))){
            this.message = getSetSuccessMsg();
            //saveToStorage(this.tmpPwd);
           CellBeanSettingActivity.savepwd = tmpPwd;
            this.isOK = true;
            this.isFinish = true;
        }else{
            this.tmpPwd = null;
            this.message = getDiffPreErrorMsg();
        }
    }
//檢查存好的圖形碼是否等於使用者畫的
    public void forChecking(List<Integer> hitList){
        this.isOK = false;
        //如果點中的小球小於最低應選取
        if((hitList == null)||(hitList.size() < CellSettingPageActivity.RangeBall)){
            this.times++;
            this.isFinish = this.times >= MAXTimes;//times變成5時才設定結束
            this.message = getPwdErrorMsg();
            return;
        }
        //與取得的先前存入的數字做比對
        if(CellBeanCheckingActivity.checkpwd.equals(convertToString(hitList))){
            this.message = getCheckSuccessMsg();
            this.isOK = true;
            this.isFinish = true;
        }else{//不相同的時候
            this.times++;
            this.isFinish = this.times == MAXTimes;
            this.message = getPwdErrorMsg();
        }
    }

    public String getMessage(){
        return this.message;
    }
    public boolean isFinish(){return isFinish;}
    public boolean isOK(){return isOK;}
    private String getReDrawMsg(){return "請再畫一次";}
    private String getSetSuccessMsg(){return "設定成功！";}
    private String getCheckSuccessMsg(){return "解鎖成功！";}
    private String getSizeErrorMsg(){return String.format("至少要連接 %d 個點喔",CellSettingPageActivity.RangeBall);}
    private String getDiffPreErrorMsg(){return "與上一次畫的不一樣喔！請重新繪製";}
    private String getPwdErrorMsg(){return String.format("密碼不正確，您還剩 %d 次機會",getRemainTimes());}

    /*private void saveToStorage(String geturePwd){
                SharePreferencesUtil.getInstance().saveString(key, geturePwd);
    }

    private String getFromStorage(){
        final String result = SharePreferencesUtil.getInstance().getString(key);
                return result;
    }*/
    //驗證時使用轉換成字串比對
    private String convertToString(List<Integer> hitlist){
        convertPwd = "";
        for(int i = 0;i<hitlist.size();i++){
            convertPwd += hitlist.get(i).toString();

    }
        hitlist.clear();
        return convertPwd;

    }

    private int getRemainTimes(){return (times < 5) ? (MAXTimes-times) : 0;}

}


