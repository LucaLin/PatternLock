package com.dbs.omni.tw.util.http.typeMapping;

/**
 * Created by siang on 2017/6/27.
 */

public enum  SwitchImageType {
    LOGIN_MAIN("S02", "登入頁"),
    FIRST_LOGIN("S03", "登入輸入帳密頁"),
    RE_LOGIN("S04", "登入記住帳號頁");

    SwitchImageType(String code, String description) {
        this.code = code;
        this.description = description;
    }

    final public String code;
    final public String description;

    @Override
    public String toString() {
        return code + ' ' + description;
    }
}
