package com.dbs.omni.tw.model.home;

/**
 * Created by siang on 2017/4/26.
 */

public class ConsumptionData {

    private String title;
    private String amount;
    private String data;
    private String foreign;

    public ConsumptionData(String title, String amount, String data, String foreign) {
        this.title = title;
        this.amount = amount;
        this.data = data;
        this.foreign = foreign;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public String getForeign() {
        return foreign;
    }

    public void setForeign(String foreign) {
        this.foreign = foreign;
    }
}
