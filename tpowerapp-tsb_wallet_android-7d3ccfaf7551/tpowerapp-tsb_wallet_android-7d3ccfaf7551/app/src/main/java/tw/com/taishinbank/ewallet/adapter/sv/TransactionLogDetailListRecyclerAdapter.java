package tw.com.taishinbank.ewallet.adapter.sv;

import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import tw.com.taishinbank.ewallet.R;
import tw.com.taishinbank.ewallet.imagehelper.ImageLoader;
import tw.com.taishinbank.ewallet.model.sv.SVTransactionPayer;
import tw.com.taishinbank.ewallet.util.FormatUtil;

/**
 * 銀行帳戶列表用的adapter
 */
public class TransactionLogDetailListRecyclerAdapter extends RecyclerView.Adapter<TransactionLogDetailListRecyclerAdapter.ViewHolder> {

    protected List<SVTransactionPayer> list = new ArrayList<> ();

    protected ImageLoader imageLoader;

    public TransactionLogDetailListRecyclerAdapter(ImageLoader imageLoader) {
        this.imageLoader = imageLoader;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_sv_transaction_log_detail_list_item, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        SVTransactionPayer item = list.get(position);

        // 收到者
        holder.textName.setText(item.getName());
        // 收到紅包的時間
        holder.textTime.setText(item.getCreateDate());
        holder.textMessage.setText(item.getReplyMessage());
        // 根據收到紅包的時間是否為空，設定是否顯示時間與留言文字
        if (TextUtils.isEmpty(item.getReplyTime())) {
            holder.textTime.setVisibility(View.INVISIBLE);
            holder.textMessage.setText("尚無留言");
        } else {
            holder.textTime.setVisibility(View.VISIBLE);
            String formattedTime = FormatUtil.toTimeFormatted(item.getReplyTime());
            holder.textTime.setText(formattedTime);
            holder.textMessage.setText(item.getReplyMessage());
        }
        // 設定頭像
        if (item.getMemNO() != null)
            imageLoader.loadImage(item.getMemNO(), holder.imagePhoto);

//        holder.textReplyAmount.setText();
//        imagePhoto = (ImageView) itemView.findViewById(R.id.image_photo);
    }

    @Override
    public int getItemCount() {
        if(list == null){
            return 0;
        }
        return list.size();
    }

    // ----
    // Getter and Setter
    // ----


    public List<SVTransactionPayer> getList() {
        return list;
    }

    public void setList(List<SVTransactionPayer> list) {
        this.list = list;
    }

    // ----
    // View Holder
    // ----
    public static class ViewHolder extends RecyclerView.ViewHolder{

        public TextView textName;
        public TextView textTime;
        public TextView textAmount;
        public TextView textMessage;
        public TextView textReplyAmount;
        public ImageView imagePhoto;

        public ViewHolder(View itemView) {
            super(itemView);
            textName = (TextView) itemView.findViewById(R.id.text_name);
            textTime = (TextView) itemView.findViewById(R.id.text_time);
            textAmount = (TextView) itemView.findViewById(R.id.text_amount);
            // 不在此顯示金額
            textAmount.setVisibility(View.INVISIBLE);
            textMessage = (TextView) itemView.findViewById(R.id.text_message);
            textReplyAmount = (TextView) itemView.findViewById(R.id.text_amount_reply);
            imagePhoto = (ImageView) itemView.findViewById(R.id.image_photo);
        }
    }

}
