package tw.com.taishinbank.ewallet.adapter.sv;

import android.content.Context;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import tw.com.taishinbank.ewallet.R;
import tw.com.taishinbank.ewallet.controller.sv.TransactionLogFragment;
import tw.com.taishinbank.ewallet.interfaces.TransactionStatus;
import tw.com.taishinbank.ewallet.interfaces.TransactionType;
import tw.com.taishinbank.ewallet.interfaces.TransactionTypeOrStatus;
import tw.com.taishinbank.ewallet.model.sv.SVTransactionOut;
import tw.com.taishinbank.ewallet.util.FormatUtil;

public class TransactionOutLogAdapter extends BaseAdapter {

    private Context context;
    private List<SVTransactionOut> transactionOutList = new ArrayList<>();
    private LayoutInflater inflater;

    public TransactionOutLogAdapter(Context context) {
        this.context = context;
        inflater = LayoutInflater.from(context);
    }

    public TransactionOutLogAdapter(List<SVTransactionOut> list, Context context) {
        this.context = context;
        this.transactionOutList = list;
        inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        if(transactionOutList != null){
            return transactionOutList.size();
        }
        return 0;
    }

    @Override
    public int getItemViewType(int position) {
        SVTransactionOut trx = transactionOutList.get(position);
        return trx.getTxfSeq() > 0 ? 0 : 1;
    }

    @Override
    public int getViewTypeCount() {
        return 2;
    }

    @Override
    public Object getItem(int position) {
        return transactionOutList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        SVTransactionOut trx = transactionOutList.get(position);

        ViewHolder viewHolder;

        if(convertView == null){
            //取得listItem容器 view
            if (trx.getTxfSeq() > 0)
                convertView = inflater.inflate(R.layout.fragment_sv_transaction_log_list_item, null);
            else
                convertView = inflater.inflate(R.layout.fragment_sv_transaction_log_list_item_seperator, null);

            //建構listItem內容view

            viewHolder = new ViewHolder(convertView, trx.getTxfSeq());

            //設置容器內容
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        int paddingBottomPx = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 18, context.getResources().getDisplayMetrics());
        int paddingTopPx = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 36, context.getResources().getDisplayMetrics());
        if (trx.getTxfSeq() > 0) {
            setDataViewValue(viewHolder, trx);
        } else {
            setHeaderViewValue(viewHolder, trx);
        }

        //
        if (trx.getTxfSeq() ==  TransactionLogFragment.SECTION_SEPERATOR_FINISHED_SEQ) {
            convertView.setPadding(0, paddingTopPx, 0, paddingBottomPx);
        } else if (trx.getTxfSeq() ==  TransactionLogFragment.SECTION_SEPERATOR_AWAITING_SEQ) {
            convertView.setPadding(0, 0, 0, paddingBottomPx);
        }


        return convertView;
    }

    // ---
    // Set View Content
    // ----
    private void setDataViewValue (ViewHolder viewHolder, SVTransactionOut trx) {
        viewHolder.txtName.setText(trx.getName());
        viewHolder.txtAmount.setText(FormatUtil.toDecimalFormatFromString(trx.getAmount(), true));
        viewHolder.txtTime.setText(FormatUtil.toTimeFormatted(trx.getCreateDate()));

        // Set txtStatus -- Text & Background color
        if (TransactionStatus.CANCELLED.getCode().equals(trx.getTxStatus())) {
            viewHolder.txtStatus.setText(TransactionTypeOrStatus.CANCEL.getDescription());
            viewHolder.txtStatus.setBackgroundResource(R.drawable.sv_transaction_status_not_done);
            viewHolder.txtAmount.setTextColor(context.getResources().getColor(R.color.red_envelope_list_amount_text));

        } else {
            if (TransactionType.DEPOSIT.code.equals(trx.getTxType())) {
                viewHolder.txtStatus.setText(TransactionTypeOrStatus.DEPOSITE.getDescription());

            } else if (TransactionType.REQUEST_SINGLE.code.equals(trx.getTxType())) {
                viewHolder.txtStatus.setText(TransactionTypeOrStatus.REQUEST.getDescription());

            } else if (TransactionType.REQUEST_MULTIPLE.code.equals(trx.getTxType())) {
                viewHolder.txtStatus.setText(TransactionTypeOrStatus.REQUEST_MULTIPLE.getDescription());

            } else if (TransactionType.TRANSFER_TO.code.equals(trx.getTxType())) {
                viewHolder.txtStatus.setText(TransactionTypeOrStatus.TRANSFER.getDescription());

            } else if (TransactionType.WITHDRAW.code.equals(trx.getTxType())) {
                viewHolder.txtStatus.setText(TransactionTypeOrStatus.WITHDRAW.getDescription());

            }

            if (TransactionStatus.AWAITING.getCode().equals(trx.getTxStatus())) {
                viewHolder.txtStatus.setBackgroundResource(R.drawable.sv_transaction_status_not_done);
                viewHolder.txtAmount.setTextColor(context.getResources().getColor(R.color.red_envelope_list_amount_text));

            } else {
                viewHolder.txtStatus.setBackgroundResource(R.drawable.sv_transaction_status_done);
                viewHolder.txtAmount.setTextColor(context.getResources().getColor(R.color.trans_log_amount_green_text));
            }
        }

        // Set image
        if (TransactionType.DEPOSIT.code.equals(trx.getTxType())) {
            viewHolder.imgType.setImageResource(R.drawable.ic_e_history_top_up);

        } else if (TransactionType.REQUEST_SINGLE.code.equals(trx.getTxType()) ||
                TransactionType.REQUEST_MULTIPLE.code.equals(trx.getTxType())) {
            viewHolder.imgType.setImageResource(R.drawable.ic_e_history_invited);

        } else if (TransactionType.TRANSFER_TO.code.equals(trx.getTxType())) {
            viewHolder.imgType.setImageResource(R.drawable.ic_e_history_transfer);

        } else if (TransactionType.WITHDRAW.code.equals(trx.getTxType())) {
            viewHolder.imgType.setImageResource(R.drawable.ic_e_history_withdraw);

        }

        if(trx.getReadFlag().equals("0"))
            viewHolder.imgNew.setVisibility(View.VISIBLE);
        else
            viewHolder.imgNew.setVisibility(View.GONE);
    }

    private void setHeaderViewValue (ViewHolder viewHolder, SVTransactionOut trx) {
        if (trx.getTxfSeq() == TransactionLogFragment.SECTION_SEPERATOR_AWAITING_SEQ) {
            viewHolder.txtLegend.setText(R.string.sv_transaction_awaiting);
        } else {
            viewHolder.txtLegend.setText(R.string.sv_transaction_finished);
        }

    }

    // ----
    // Getter and setter
    // ----
    public List<SVTransactionOut> getTransactionOutList() {
        return transactionOutList;
    }

    public void setTransactionOutList(List<SVTransactionOut> transactionOutList) {
        this.transactionOutList = transactionOutList;
    }

    static class ViewHolder {

        //For Data
        public TextView txtName;
        public TextView txtAmount;
        public TextView txtStatus;
        public TextView txtTime;
        public ImageView imgType;
        public ImageView imgNew;

        //For Section Header
        public TextView txtLegend;

        public ViewHolder(View itemView, int type) {
            if (type > 0) {
                txtName = (TextView) itemView.findViewById(R.id.txt_name);
                txtAmount = (TextView) itemView.findViewById(R.id.txt_amount);
                txtStatus = (TextView) itemView.findViewById(R.id.txt_status);
                txtTime = (TextView) itemView.findViewById(R.id.txt_time);
                imgType = (ImageView) itemView.findViewById(R.id.img_type);
                imgNew = (ImageView) itemView.findViewById(R.id.img_new);
            } else if (type < 0) {
                txtLegend = (TextView) itemView.findViewById(R.id.txt_legend);
            }
        }
    }
}
