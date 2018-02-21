package com.example.r30_a.patternlock.Controller;

import com.example.r30_a.patternlock.PatternLockCheckingActivity;
import com.example.r30_a.patternlock.PatternLockSettingActivity;
import com.example.r30_a.patternlock.SettingPageActivity;

import java.util.List;

/**
 * Created by Luca on 2018/2/14.
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
    private String tmpPwd;//存取暫時的密碼用
    private String convertPwd="";

    //設定圖形碼的地方，這裡需繪製兩次
    public void forsetting(List<Integer> hitList){
        isFinish = false;
        isOK = false;
        PatternLockSettingActivity.savepwd = "";
        //如果點到的球數小於最低要求
        if((hitList == null) || (hitList.size() < SettingPageActivity.RangeBall)){
            tmpPwd =null;
            message = getSizeErrorMsg();
            return;
        }
        //如果成功的話，第一次
        if(tmpPwd == null|| this.tmpPwd.length()==0){
            tmpPwd = convertToString(hitList);
            message = getReDrawMsg();
            isOK = true;
            return;
        }
        //畫第二次圖形，判斷是否跟第一次相同
        if(tmpPwd.equals(convertToString(hitList))){
            message = getSetSuccessMsg();
            //saveToStorage(this.tmpPwd);
            PatternLockSettingActivity.savepwd = tmpPwd;
            isOK = true;
            isFinish = true;
        }else{
            tmpPwd = null;
            message = getDiffPreErrorMsg();
        }
    }
    //檢查存好的圖形碼是否等於使用者畫的
    public void forChecking(List<Integer> hitList){
        isOK = false;
        //如果點中的小球小於最低應選取
        if((hitList == null)||(hitList.size() < SettingPageActivity.RangeBall)){
            times++;
            isFinish = this.times >= MAXTimes;//times變成5時才設定結束
            message = getPwdErrorMsg();
            return;
        }
        //與取得的先前存入的數字做比對
        if(PatternLockCheckingActivity.checkpwd.equals(convertToString(hitList))){
            message = getCheckSuccessMsg();
            isOK = true;
            isFinish = true;
        }else{//不相同的時候
            times++;
            isFinish = this.times == MAXTimes;
            message = getPwdErrorMsg();
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
    private String getSizeErrorMsg(){return String.format("至少要連接 %d 個點喔",SettingPageActivity.RangeBall);}
    private String getDiffPreErrorMsg(){return "與上一次畫的不一樣喔！請重新繪製";}
    private String getPwdErrorMsg(){return String.format("密碼不正確，您還剩 %d 次機會",getRemainTimes());}


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
