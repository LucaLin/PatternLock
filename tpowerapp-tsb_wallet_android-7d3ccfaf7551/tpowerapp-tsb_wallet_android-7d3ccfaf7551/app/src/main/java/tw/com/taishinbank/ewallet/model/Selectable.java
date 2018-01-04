package tw.com.taishinbank.ewallet.model;


public class Selectable<T>{

    // 任意型態的物件
    public T item;
    // 是否被選中
    public boolean isChecked;

    public Selectable(T item){
        this.item = item;
        isChecked = false;
    }
}