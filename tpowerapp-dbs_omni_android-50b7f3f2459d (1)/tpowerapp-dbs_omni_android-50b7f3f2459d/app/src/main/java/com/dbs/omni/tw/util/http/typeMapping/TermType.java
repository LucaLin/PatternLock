package com.dbs.omni.tw.util.http.typeMapping;

/**
 * Created by siang on 2017/7/11.
 */

public enum  TermType {
    REGISTER("A01"),
    USERCODE_INQUIRY("A02"),
    RESET_PASSWORD("A03"),
    EBILL("A04"),
    PRELOGIN_EBILL("A05"),
    TRANSACTION("A06"),
    SINGLE_TRANSACTION("A07"),
    AUTO_TRANSACTION("A08");



    TermType(String code) {
        this.code = code;
    }

    final public String code;

}
