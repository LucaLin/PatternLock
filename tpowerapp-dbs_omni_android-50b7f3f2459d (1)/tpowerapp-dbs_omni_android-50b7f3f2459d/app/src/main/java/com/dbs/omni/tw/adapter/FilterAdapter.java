package com.dbs.omni.tw.adapter;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.dbs.omni.tw.R;
import com.dbs.omni.tw.model.element.FilterItemData;

import java.util.List;


public class FilterAdapter extends BaseAdapter {
    private Context context;
    private List<FilterItemData> cardList;
    private LayoutInflater inflater;

    public FilterAdapter(Context context) {
        this.context = context;
        inflater = LayoutInflater.from(context);
    }

    public FilterAdapter(List<FilterItemData> list, Context context) {
        this.context = context;
        this.cardList = list;
        inflater = LayoutInflater.from(context);
    }


    @Override
    public int getCount() {
        if(cardList != null){
            return cardList.size();
        }
        return 0;
    }

    @Override
    public int getViewTypeCount() {
        return 3;
    }

    @Override
    public Object getItem(int position) {
        return cardList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        FilterItemData data = cardList.get(position);

        ViewHolder viewHolder;

        if(convertView == null){
            //取得listItem容器 view
            convertView = inflater.inflate(R.layout.element_filter_item, null);

            //建構listItem內容view
            viewHolder = new ViewHolder(convertView);

            //設置容器內容
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        setDataViewValue(viewHolder, data);

        return convertView;
    }

    // ---
    // Set View Content
    // ----
    private void setDataViewValue (ViewHolder viewHolder, FilterItemData data) {
        viewHolder.txtTitle.setText(data.getContent());
        if(data.isSelect()) {
            viewHolder.txtTitle.setTextColor(ContextCompat.getColor(context,R.color.colorRedPrimary));
            viewHolder.imageSelect.setVisibility(View.VISIBLE);
        } else {
            viewHolder.txtTitle.setTextColor(ContextCompat.getColor(context, R.color.colorGrayPrimaryDark));
            viewHolder.imageSelect.setVisibility(View.GONE);
        }
    }

    // ----
    // Getter and setter
    // ----
    public List<FilterItemData> getTransactionList() {
        return cardList;
    }

    public void setList(List<FilterItemData> transactionOutList) {
        this.cardList = transactionOutList;
    }

    static class ViewHolder {


        //has not type
        public TextView txtTitle;

        public ImageView imageSelect;

        public ViewHolder(View itemView) {

            txtTitle = (TextView) itemView.findViewById(R.id.text_title);
            imageSelect = (ImageView) itemView.findViewById(R.id.image_selected);
        }
    }
}
