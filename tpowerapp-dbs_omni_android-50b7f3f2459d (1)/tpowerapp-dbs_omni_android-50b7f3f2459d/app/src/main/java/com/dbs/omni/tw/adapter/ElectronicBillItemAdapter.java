package com.dbs.omni.tw.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.dbs.omni.tw.R;
import com.dbs.omni.tw.util.FormatUtil;
import com.dbs.omni.tw.util.http.mode.bill.EBillFileData;

import java.util.List;


public class ElectronicBillItemAdapter extends BaseAdapter {
    private Context context;
    private List<EBillFileData> cardList;
    private LayoutInflater inflater;

    public ElectronicBillItemAdapter(Context context) {
        this.context = context;
        inflater = LayoutInflater.from(context);
    }

    public ElectronicBillItemAdapter(List<EBillFileData> list, Context context) {
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
        EBillFileData trx = cardList.get(position);

        ViewHolder viewHolder;

        if(convertView == null){
            //取得listItem容器 view
            convertView = inflater.inflate(R.layout.electronic_bill_item, null);

            //建構listItem內容view
            viewHolder = new ViewHolder(convertView);

            //設置容器內容
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        setDataViewValue(viewHolder, trx);

        return convertView;
    }

    // ---
    // Set View Content
    // ----
    private void setDataViewValue (ViewHolder viewHolder, EBillFileData data) {
        viewHolder.txtTitle.setText(FormatUtil.toDateHeaderFormatted(data.geteBillYYYY()+data.geteBillMM(), false));


    }

    // ----
    // Getter and setter
    // ----
    public List<EBillFileData> getTransactionList() {
        return cardList;
    }

    public void setList(List<EBillFileData> transactionOutList) {
        this.cardList = transactionOutList;
    }

    static class ViewHolder {

        public TextView txtTitle;

        public ImageView imageIcon;
        public ImageView imageNextIcon;

        public ViewHolder(View itemView) {

            txtTitle = (TextView) itemView.findViewById(R.id.text_title);
            imageIcon = (ImageView) itemView.findViewById(R.id.image_icon);
            imageNextIcon = (ImageView) itemView.findViewById(R.id.image_next);
        }
    }
}
