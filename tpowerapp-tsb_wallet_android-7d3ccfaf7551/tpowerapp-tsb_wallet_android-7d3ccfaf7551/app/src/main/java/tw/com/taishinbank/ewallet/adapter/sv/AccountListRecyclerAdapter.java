package tw.com.taishinbank.ewallet.adapter.sv;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import tw.com.taishinbank.ewallet.R;
import tw.com.taishinbank.ewallet.model.sv.DesignateAccount;
import tw.com.taishinbank.ewallet.util.FormatUtil;

/**
 * 約定帳戶列表用的adapter
 */
public class AccountListRecyclerAdapter extends RecyclerView.Adapter<AccountListRecyclerAdapter.ViewHolder> {


    protected List<DesignateAccount> bankAccounts = new ArrayList<> ();

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_sv_bk_account_list_item, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        // 顯示約定帳號的資料
        DesignateAccount item = bankAccounts.get(position);
        holder.txtBankTitle.setText(item.getBankTitle());
        String formattedAccount = FormatUtil.toAccountFormat(item.getAccount());
        holder.txtAccountNo.setText(formattedAccount);

        holder.itemView.setTag(position);
    }

    @Override
    public int getItemCount() {
        return bankAccounts.size();
    }

    // ----
    // Getter and Setter
    // ----
    public List<DesignateAccount> getBankAccounts() {
        return bankAccounts;
    }

    public void setAccounts(List<DesignateAccount> bankAccounts) {
        this.bankAccounts = bankAccounts;
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
            chkAccount.setVisibility(View.GONE);
        }
    }

}
