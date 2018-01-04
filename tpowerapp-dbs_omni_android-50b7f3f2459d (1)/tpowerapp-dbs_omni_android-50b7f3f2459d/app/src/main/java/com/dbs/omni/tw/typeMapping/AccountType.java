package com.dbs.omni.tw.typeMapping;

/**
 * Created by siang on 2017/4/26.
 */

public enum AccountType
{
    ALL(1),
    DBS(2),
    ANZ(3);
    public int getValue()
    {
        return value;
    }
    private int value;
    AccountType(int value)
    {
        this.value = value;
    }

    public static AccountType valueOf(int id) {
       if(id == ANZ.getValue()) {
           return ANZ;
       } else if(id == DBS.getValue()) {
           return DBS;
       } else {
           return ALL;
       }

    }
}