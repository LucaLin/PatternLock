package tw.com.taishinbank.ewallet.adapter.creditcard;

import android.content.Context;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import tw.com.taishinbank.ewallet.R;
import tw.com.taishinbank.ewallet.interfaces.creditcard.TransactionStatus;
import tw.com.taishinbank.ewallet.model.creditcard.CreditCardTransaction;
import tw.com.taishinbank.ewallet.util.FormatUtil;

public class TransactionLogAdapter extends BaseAdapter {
    private Context context;
    private List<CreditCardTransaction> transactionList;
    private LayoutInflater inflater;

    public TransactionLogAdapter(Context context) {
        this.context = context;
        inflater = LayoutInflater.from(context);
    }

    public TransactionLogAdapter(List<CreditCardTransaction> list, Context context) {
        this.context = context;
        this.transactionList = list;
        inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        if(transactionList != null){
            return transactionList.size();
        }
        return 0;
    }


    @Override
    public int getViewTypeCount() {
        return 2;
    }

    @Override
    public Object getItem(int position) {
        return transactionList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        CreditCardTransaction trx = transactionList.get(position);

        ViewHolder viewHolder;

        if(convertView == null){
            //取得listItem容器 view
            convertView = inflater.inflate(R.layout.fragment_sv_transaction_log_list_item, null);

            //建構listItem內容view
            viewHolder = new ViewHolder(convertView);

            //設置容器內容
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        int paddingBottomPx = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 18, context.getResources().getDisplayMetrics());
        int paddingTopPx = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 36, context.getResources().getDisplayMetrics());
        setDataViewValue(viewHolder, trx);

        return convertView;
    }

    // ---
    // Set View Content
    // ----
    private void setDataViewValue (ViewHolder viewHolder, CreditCardTransaction trx) {

        viewHolder.txtName.setText(trx.getStoreName());
        viewHolder.txtAmount.setText(FormatUtil.toDecimalFormat(trx.getTradeAmount(), true));

        try {
            String strTime = trx.getMerchantTradeDate();
//            Log.d("check time: ", strTime);
            strTime = strTime.replace(" ", "");
            strTime = strTime.substring(0, 14);
            viewHolder.txtTime.setText(FormatUtil.toTimeFormatted(strTime));
        } catch (Exception ex) {
            ex.printStackTrace();
            viewHolder.txtTime.setText(trx.getMerchantTradeDate());
        }
        // Set txtStatus -- Text & Background color
        if(TransactionStatus.SUCCESS.getCode().equals(String.valueOf(trx.getTradeStatus())))
        {
            viewHolder.txtStatus.setText(TransactionStatus.CodeToEnum(String.valueOf(trx.getTradeStatus())).getDescription());
            viewHolder.txtStatus.setBackgroundResource(R.drawable.sv_transaction_status_done);
            viewHolder.txtAmount.setTextColor(context.getResources().getColor(R.color.trans_log_amount_green_text));
        }
        else
        {
            viewHolder.txtStatus.setText(TransactionStatus.CodeToEnum(String.valueOf(trx.getTradeStatus())).getDescription());
            viewHolder.txtStatus.setBackgroundResource(R.drawable.sv_transaction_status_not_done);
            viewHolder.txtAmount.setTextColor(context.getResources().getColor(R.color.colorRedPrimary));
        }


        // Set image
        viewHolder.imgType.setBackgroundResource(R.drawable.img_default_photo_gary);
        viewHolder.imgType.setImageResource(R.drawable.img_taishin_photo);

    }

    // ----
    // Getter and setter
    // ----
    public List<CreditCardTransaction> getTransactionList() {
        return transactionList;
    }

    public void setTransactionList(List<CreditCardTransaction> transactionOutList) {
        this.transactionList = transactionOutList;
    }

    static class ViewHolder {

        //For Data
        public TextView txtName;
        public TextView txtAmount;
        public TextView txtStatus;
        public TextView txtTime;
        public ImageView imgType;

        //For Section Header
        public TextView txtLegend;

        public ViewHolder(View itemView) {
            txtName = (TextView) itemView.findViewById(R.id.txt_name);
            txtAmount = (TextView) itemView.findViewById(R.id.txt_amount);
            txtStatus = (TextView) itemView.findViewById(R.id.txt_status);
            txtTime = (TextView) itemView.findViewById(R.id.txt_time);
            imgType = (ImageView) itemView.findViewById(R.id.img_type);
        }
    }
}
