package tw.com.taishinbank.ewallet.adapter.wallethome;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import tw.com.taishinbank.ewallet.R;
import tw.com.taishinbank.ewallet.model.wallethome.WalletSystemMsg;
import tw.com.taishinbank.ewallet.util.FormatUtil;


public class WalletSystemMessageAdapter extends RecyclerView.Adapter<WalletSystemMessageAdapter.ViewHolder> {

    private ArrayList<WalletSystemMsg> list = new ArrayList<>();
    private Activity context;
    private OnItemClickedListener listener;

    public WalletSystemMessageAdapter(Activity context){
        this.context = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.system_message_list_item, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        // TODO 改成真正的資料綁定
        WalletSystemMsg item = list.get(position);

        setViewContent(holder, item);

        holder.itemView.setTag(position);
    }

    @Override
    public int getItemCount() {
        if(list == null){
            return 0;
        }
        return list.size();
    }
    /**
     * 變更列表資料
     */
    public void setList(ArrayList<WalletSystemMsg> list){
        if(this.list == null){
            this.list = list;
        }else{
            this.list.clear();
            this.list.addAll(list);
        }
        notifyDataSetChanged();
    }

    /**
     * 設定點擊事件處理
     */
    public void setOnItemClickedListener(OnItemClickedListener listener){
        this.listener = listener;
    }

    class ViewHolder extends RecyclerView.ViewHolder{

        public TextView textTime;
        public TextView textMessage;
        public ImageView imagePhoto;
        public ImageView imageNew;

        public ViewHolder(View itemView) {
            super(itemView);
            textTime = (TextView) itemView.findViewById(R.id.text_time);
            textMessage = (TextView) itemView.findViewById(R.id.text_message);

            imagePhoto = (ImageView) itemView.findViewById(R.id.image_photo);
            imageNew = (ImageView) itemView.findViewById(R.id.image_new);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(listener != null){
                        // 通知有點擊事件
                        WalletSystemMsg item = list.get((int) v.getTag());
                        listener.onItemClicked(item);
                    }
                }
            });
        }
    }

    public interface OnItemClickedListener{
        void onItemClicked(WalletSystemMsg item);
    }

    private void setViewContent(ViewHolder holder, WalletSystemMsg item){
        holder.textMessage.setText(item.getTitle());
        // 顯示上線時間
        String formattedTime = null;
        if(!TextUtils.isEmpty(item.getOnLineDate())) {
            formattedTime = FormatUtil.toDateFormatted(item.getOnLineDate());
        }
        holder.textTime.setText(formattedTime);
    }
}
