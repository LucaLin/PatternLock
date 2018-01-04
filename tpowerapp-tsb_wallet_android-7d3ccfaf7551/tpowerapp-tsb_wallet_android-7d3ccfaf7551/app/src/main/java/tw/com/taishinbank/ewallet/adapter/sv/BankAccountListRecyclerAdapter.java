package tw.com.taishinbank.ewallet.adapter.sv;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import tw.com.taishinbank.ewallet.R;
import tw.com.taishinbank.ewallet.model.sv.BankAccount;
import tw.com.taishinbank.ewallet.model.sv.DesignateAccount;
import tw.com.taishinbank.ewallet.util.FormatUtil;

/**
 * 銀行帳戶列表用的adapter
 */
public class BankAccountListRecyclerAdapter extends RecyclerView.Adapter<BankAccountListRecyclerAdapter.ViewHolder> {

    protected List<BankAccount> bankAccounts = new ArrayList<> ();
    public final static int NO_CHECKED_ITEM = -1;
    private int checkedPosition = NO_CHECKED_ITEM;
    private OnSelectedItemsChangedListener listener;
    private String otherTitle;

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_sv_bk_account_list_item, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        // 如果是最後一筆，顯示為其他帳戶
        if(position == getItemCount()-1) {
            holder.txtAccountNo.setText(otherTitle);
            holder.txtBankTitle.setVisibility(View.GONE);

        // 其他則顯示帳號的資料
        }else{
            DesignateAccount item = bankAccounts.get(position);
            holder.txtBankTitle.setVisibility(View.VISIBLE);
            holder.txtBankTitle.setText(item.getBankTitle());
            String formattedAccount = FormatUtil.toAccountFormat(item.getAccount());
            holder.txtAccountNo.setText(formattedAccount);
        }

        // 設定checkbox狀態
        holder.chkAccount.setOnCheckedChangeListener(null);
        holder.chkAccount.setChecked(position == checkedPosition);
        holder.chkAccount.setOnCheckedChangeListener(checkedChangeListener);
        holder.chkAccount.setTag(position);
    }

    @Override
    public int getItemCount() {
        if(bankAccounts == null){
            return 1;
        }
        return bankAccounts.size() + 1;
    }

    /**
     * checkbox勾選變更listener
     */
    private CompoundButton.OnCheckedChangeListener checkedChangeListener = new CompoundButton.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            // 有變更時，更新model列表狀態，發出選中項目的變更
            int position = (int)buttonView.getTag();
            if(checkedPosition != position){
                checkedPosition = position;
            }else{
                checkedPosition = NO_CHECKED_ITEM;
            }
            notifyDataSetChanged();
            notifySelectedItemChange();
        }
    };

    /**
     * 通知選中資料更新
     */
    public void notifySelectedItemChange(){
        if(listener != null) {
            // 通知選中項目有變更
            listener.OnSelectedItemsChanged(checkedPosition);
        }
    }

    public interface OnSelectedItemsChangedListener{
        void OnSelectedItemsChanged(int checkedPosition);
    }

    // ----
    // Getter and Setter
    // ----
    public List<BankAccount> getBankAccounts() {
        return bankAccounts;
    }

    public void setAccounts(List<BankAccount> bankAccounts) {
        this.bankAccounts = bankAccounts;
    }

    public void setCheckedPosition(int checkedPosition){
        this.checkedPosition = checkedPosition;
    }

    public BankAccount getItem(int position){
        return bankAccounts.get(position);
    }

    public String getOtherTitle() {
        return otherTitle;
    }

    public void setOtherTitle(String otherTitle) {
        this.otherTitle = otherTitle;
    }

    public void setOnSelectedItemsChangedListener(OnSelectedItemsChangedListener listener) {
        this.listener = listener;
    }

    public int getCheckedPosition() {
        return checkedPosition;
    }

    // ----
    // View Holder
    // ----
    class ViewHolder extends RecyclerView.ViewHolder{

        public TextView txtBankTitle;
        public TextView txtAccountNo;
        public CheckBox chkAccount;

        public ViewHolder(View itemView) {
            super(itemView);
            txtBankTitle = (TextView) itemView.findViewById(R.id.txt_bank_title);
            txtAccountNo = (TextView) itemView.findViewById(R.id.txt_account_no);
            chkAccount = (CheckBox) itemView.findViewById(R.id.chk_account);
            chkAccount.setOnCheckedChangeListener(checkedChangeListener);
        }
    }

}
