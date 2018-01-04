package com.dbs.omni.tw.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.dbs.omni.tw.R;
import com.dbs.omni.tw.model.payment.PaidBankData;
import com.dbs.omni.tw.util.BitmapUtil;

import java.util.List;


public class PaidBankAdapter extends BaseAdapter {
    private Context context;
    private List<PaidBankData> bankList;
    private LayoutInflater inflater;

    public PaidBankAdapter(Context context) {
        this.context = context;
        inflater = LayoutInflater.from(context);
    }

    public PaidBankAdapter(List<PaidBankData> list, Context context) {
        this.context = context;
        this.bankList = list;
        inflater = LayoutInflater.from(context);
    }


    @Override
    public int getCount() {
        if(bankList != null){
            return bankList.size();
        }
        return 0;
    }

    @Override
    public int getViewTypeCount() {
        return 3;
    }

    @Override
    public Object getItem(int position) {
        return bankList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        PaidBankData trx = bankList.get(position);

        ViewHolder viewHolder;

        if(convertView == null){
            //取得listItem容器 view
            convertView = inflater.inflate(R.layout.element_choose_bank, null);

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
    private void setDataViewValue (ViewHolder viewHolder, PaidBankData data) {

        viewHolder.text_bank_name.setText(data.getBankName());
        viewHolder.text_bank_code.setText(context.getString(R.string.code) + data.getBankNo());

        if(!TextUtils.isEmpty(data.getBankIcon())){
            Bitmap bitmap = BitmapUtil.base64ToBitmap(data.getBankIcon());
            viewHolder.imageView_bank_icon.setImageBitmap(bitmap);
            viewHolder.text_bank_icon.setVisibility(View.GONE);
        }else{
            viewHolder.imageView_bank_icon.setImageResource(R.drawable.ic_choose_bank_default);
            String string = data.getBankName().substring(0, 1);
            viewHolder.text_bank_icon.setText(string);
            viewHolder.text_bank_icon.setVisibility(View.VISIBLE);
        }
    }

    // ----
    // Getter and setter
    // ----
    public List<PaidBankData> getTransactionList() {
        return bankList;
    }

    public void setList(List<PaidBankData> transactionOutList) {
        this.bankList = transactionOutList;
    }

    static class ViewHolder {
        //has  type
        public TextView text_bank_name;
        public TextView text_bank_code;
        public ImageView imageView_bank_icon;
        public TextView text_bank_icon;

        public ViewHolder(View itemView) {
            text_bank_name = (TextView) itemView.findViewById(R.id.text_account_name);
            text_bank_code = (TextView) itemView.findViewById(R.id.text_account_number);
            imageView_bank_icon = (ImageView) itemView.findViewById(R.id.imageView_bank_icon);
            text_bank_icon = (TextView) itemView.findViewById(R.id.text_bank_icon);
        }
    }
}
