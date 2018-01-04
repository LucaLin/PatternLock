package com.dbs.omni.tw.typeMapping;

/**
 * Created by siang on 2017/4/26.
 */

public enum ItemType
{
    TITLE(0),
    CONTENT(1);
    public int getValue()
    {
        return value;
    }
    private int value;
    ItemType(int value)
    {
        this.value = value;
    }
}