package com.example.r30_a.testlayout.cellBean;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;

/**
 * Created by R30-A on 2017/12/14.
 */

public class SharePreferencesUtil {
    private static SharePreferencesUtil instance;

    private SharedPreferences.Editor editor;
    private SharedPreferences sf ;

    public SharePreferencesUtil(){
       this.sf = PreferenceManager.getDefaultSharedPreferences(TestApplication.getContext());
       this.editor = this.sf.edit();
    }

    public static SharePreferencesUtil getInstance(){
        if(instance == null){
            synchronized (SharePreferencesUtil.class){
                if(instance == null){
                    instance = new SharePreferencesUtil();
                }
            }
        }
        return instance;
    }

    public void saveString(String name, String data){
        editor.putString(name,data);
        editor.commit();


    }

    public String getString(String name){
        return this.sf.getString(name, null);
    }



}
