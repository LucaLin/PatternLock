package com.dbs.omni.tw.model.element;

/**
 * Created by siang on 2017/5/11.
 */

public class SelectItemData {

    private String content;
    private Object amount;
//    private int TAG;
    private boolean isToEdit;
//    private View.OnClickListener onClickListener;

    public SelectItemData(String content, float amount) {
        this.content = content;
        this.amount = amount;
//        this.TAG = TAG;
        this.isToEdit = false;
//        this.onClickListener = onClickListener;
    }

    // 開啟輸入
    public SelectItemData(String content) {
        this.content = content;
        amount = null;
//        this.TAG = TAG;
        this.isToEdit = true;
//        this.onClickListener = onClickListener;
    }

    public SelectItemData(String content, double amount, boolean isToEdit) {
        this.content = content;
        this.amount = amount;
//        this.TAG = TAG;
        this.isToEdit = isToEdit;
//        this.onClickListener = onClickListener;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Object getAmount() {
        return amount;
    }

    public void setAmount(Object amount) {
        this.amount = amount;
    }

//    public int getTAG() {
//        return TAG;
//    }
//
//    public void setTAG(int TAG) {
//        this.TAG = TAG;
//    }

//    public View.OnClickListener getOnClickListener() {
//        return onClickListener;
//    }
//
//    public void setOnClickListener(View.OnClickListener onClickListener) {
//        this.onClickListener = onClickListener;
//    }

    public boolean isToEdit() {
        return isToEdit;
    }

    public void setToEdit(boolean toEdit) {
        isToEdit = toEdit;
    }
}
