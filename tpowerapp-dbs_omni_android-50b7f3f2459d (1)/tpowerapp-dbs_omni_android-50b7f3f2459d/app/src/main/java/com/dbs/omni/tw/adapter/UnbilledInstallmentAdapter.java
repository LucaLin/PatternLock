package com.dbs.omni.tw.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.dbs.omni.tw.R;
import com.dbs.omni.tw.model.home.ConsumptionItem;

import java.util.List;


public class UnbilledInstallmentAdapter extends BaseAdapter {
    private Context context;
    private List<ConsumptionItem> list;
    private LayoutInflater inflater;

    public UnbilledInstallmentAdapter(Context context) {
        this.context = context;
        inflater = LayoutInflater.from(context);
    }

    public UnbilledInstallmentAdapter(List<ConsumptionItem> list, Context context) {
        this.context = context;
        this.list = list;
        inflater = LayoutInflater.from(context);
    }


    @Override
    public int getCount() {
        if(list != null){
            return list.size();
        }
        return 0;
    }

    @Override
    public int getViewTypeCount() {
        return 3;
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ConsumptionItem item = list.get(position);

        // Content
        ViewContentHolder viewContentHolder;
        //取得listItem容器 view
        convertView = inflater.inflate(R.layout.consumption_checkbox_item, null);

        //建構listItem內容view
        viewContentHolder = new ViewContentHolder(convertView);
        //設置容器內容
        convertView.setTag(viewContentHolder);
        setDataViewValue(viewContentHolder, item);

        return convertView;
    }

    // ---
    // Set View Content
    // ----
    private void setDataViewValue (ViewContentHolder viewContentHolder, ConsumptionItem data) {

        viewContentHolder.txtTitle.setText(data.getConsumptionData().getTitle());
        viewContentHolder.txtAmount.setText(data.getConsumptionData().getAmount());
        viewContentHolder.txtDate.setText(data.getConsumptionData().getData());
        viewContentHolder.txtForeign.setText(data.getConsumptionData().getForeign());


    }

    private void setDataViewValue (ViewTitleHolder viewTitleHolder, ConsumptionItem data) {
        viewTitleHolder.txtTitle.setText(data.getHeaderTitle());
    }


    // ----
    // Getter and setter
    // ----
    public List<ConsumptionItem> getTransactionList() {
        return list;
    }

    public void setList(List<ConsumptionItem> transactionOutList) {
        this.list = transactionOutList;
    }

    static class ViewContentHolder {


        public TextView txtTitle;
        public TextView txtAmount;
        public TextView txtForeign;
        public TextView txtDate;

        public ViewContentHolder(View itemView) {

            txtTitle = (TextView) itemView.findViewById(R.id.text_title);
            txtAmount = (TextView) itemView.findViewById(R.id.text_amount);
            txtDate = (TextView) itemView.findViewById(R.id.text_date);
            txtForeign = (TextView) itemView.findViewById(R.id.text_foreign);
        }
    }

    static class  ViewTitleHolder {
        public TextView txtTitle;


        public ViewTitleHolder(View itemView) {
            txtTitle = (TextView) itemView.findViewById(R.id.text_title);
        }
    }
}
