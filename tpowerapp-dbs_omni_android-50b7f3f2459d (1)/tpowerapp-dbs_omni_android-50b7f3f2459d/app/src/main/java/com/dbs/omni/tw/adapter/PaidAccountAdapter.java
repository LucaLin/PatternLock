package com.dbs.omni.tw.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.dbs.omni.tw.R;
import com.dbs.omni.tw.model.payment.PaidAccountData;
import com.dbs.omni.tw.util.FormatUtil;

import java.util.List;


public class PaidAccountAdapter extends BaseAdapter {
    private Context context;
    private List<PaidAccountData> accountList;
    private LayoutInflater inflater;

    public PaidAccountAdapter(Context context) {
        this.context = context;
        inflater = LayoutInflater.from(context);
    }

    public PaidAccountAdapter(List<PaidAccountData> list, Context context) {
        this.context = context;
        this.accountList = list;
        inflater = LayoutInflater.from(context);
    }


    @Override
    public int getCount() {
        if(accountList != null){
            return accountList.size();
        }
        return 0;
    }

    @Override
    public int getViewTypeCount() {
        return 3;
    }

    @Override
    public Object getItem(int position) {
        return accountList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        PaidAccountData trx = accountList.get(position);

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
    private void setDataViewValue (ViewHolder viewHolder, PaidAccountData data) {

        viewHolder.text_account_name.setText(data.getAccountName());
        viewHolder.text_account_number.setText(data.getAccountNumber());
        viewHolder.text_balance.setText(FormatUtil.toDecimalFormat(context, data.getBalance(), true));
        viewHolder.imageView_bank_icon.setImageResource(R.drawable.ic_choose_bank_dbs);

        if(data.isBalanceNotEnough() == true){
            viewHolder.text_balance_not_enough.setText(R.string.balance_not_enough);
        }
    }

    // ----
    // Getter and setter
    // ----
    public List<PaidAccountData> getTransactionList() {
        return accountList;
    }

    public void setList(List<PaidAccountData> transactionOutList) {
        this.accountList = transactionOutList;
    }

    static class ViewHolder {
        //has  type
        public TextView text_account_name;
        public TextView text_account_number;
        public TextView text_balance;
        public TextView text_balance_not_enough;
        public ImageView imageView_bank_icon;

        public ViewHolder(View itemView) {
            text_account_name = (TextView) itemView.findViewById(R.id.text_account_name);
            text_account_number = (TextView) itemView.findViewById(R.id.text_account_number);
            text_balance = (TextView) itemView.findViewById(R.id.text_balance);
            text_balance_not_enough = (TextView) itemView.findViewById(R.id.text_balance_not_enough);
            imageView_bank_icon = (ImageView)itemView.findViewById(R.id.imageView_bank_icon);
        }
    }
}
