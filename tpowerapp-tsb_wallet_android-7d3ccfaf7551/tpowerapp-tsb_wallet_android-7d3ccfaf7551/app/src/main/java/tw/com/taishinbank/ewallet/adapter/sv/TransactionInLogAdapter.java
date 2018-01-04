package tw.com.taishinbank.ewallet.adapter.sv;

import android.content.Context;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.util.List;

import tw.com.taishinbank.ewallet.R;
import tw.com.taishinbank.ewallet.controller.sv.TransactionLogFragment;
import tw.com.taishinbank.ewallet.interfaces.TransactionStatus;
import tw.com.taishinbank.ewallet.interfaces.TransactionType;
import tw.com.taishinbank.ewallet.interfaces.TransactionTypeOrStatus;
import tw.com.taishinbank.ewallet.model.sv.SVTransactionIn;
import tw.com.taishinbank.ewallet.util.FormatUtil;

public class TransactionInLogAdapter extends BaseAdapter {

    private Context context;
    private List<SVTransactionIn> transactionInList;
    private LayoutInflater inflater;

    public TransactionInLogAdapter(Context context) {
        this.context = context;
        inflater = LayoutInflater.from(context);
    }

    public TransactionInLogAdapter(List<SVTransactionIn> list, Context context) {
        this.context = context;
        this.transactionInList = list;
        inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        if(transactionInList != null){
            return transactionInList.size();
        }
        return 0;
    }

    @Override
    public int getItemViewType(int position) {
        SVTransactionIn trx = transactionInList.get(position);
        return trx.getTxfSeq() > 0 ? 0 : 1;
    }

    @Override
    public int getViewTypeCount() {
        return 2;
    }

    @Override
    public Object getItem(int position) {
        return transactionInList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        SVTransactionIn trx = transactionInList.get(position);

        TransactionOutLogAdapter.ViewHolder viewHolder;

        if(convertView == null){
            //取得listItem容器 view
            if (trx.getTxfSeq() > 0)
                convertView = inflater.inflate(R.layout.fragment_sv_transaction_log_list_item, null);
            else
                convertView = inflater.inflate(R.layout.fragment_sv_transaction_log_list_item_seperator, null);

            //建構listItem內容view

            viewHolder = new TransactionOutLogAdapter.ViewHolder(convertView, trx.getTxfSeq());

            //設置容器內容
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (TransactionOutLogAdapter.ViewHolder) convertView.getTag();
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
    private void setDataViewValue (TransactionOutLogAdapter.ViewHolder viewHolder, SVTransactionIn trx) {
        viewHolder.txtName.setText(trx.getTxMemName());
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

    private void setHeaderViewValue (TransactionOutLogAdapter.ViewHolder viewHolder, SVTransactionIn trx) {
        if (trx.getTxfSeq() == TransactionLogFragment.SECTION_SEPERATOR_AWAITING_SEQ) {
            viewHolder.txtLegend.setText(R.string.sv_transaction_awaiting);
        } else {
            viewHolder.txtLegend.setText(R.string.sv_transaction_finished);
        }

    }

    // ----
    // Getter and setter
    // ----
    public List<SVTransactionIn> getTransactionInList() {
        return transactionInList;
    }

    public void setTransactionInList(List<SVTransactionIn> transactionInList) {
        this.transactionInList = transactionInList;
    }

}
