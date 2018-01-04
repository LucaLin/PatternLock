package tw.com.taishinbank.ewallet.adapter.red;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import tw.com.taishinbank.ewallet.R;
import tw.com.taishinbank.ewallet.imagehelper.ImageLoader;
import tw.com.taishinbank.ewallet.interfaces.RedEnvelopeType;
import tw.com.taishinbank.ewallet.model.red.RedEnvelopeHomeListData;
import tw.com.taishinbank.ewallet.util.FormatUtil;
import tw.com.taishinbank.ewallet.util.PreferenceUtil;


public class RedEnvelopeAdapter extends RecyclerView.Adapter<RedEnvelopeAdapter.ViewHolder> {

    private ArrayList<RedEnvelopeHomeListData> list = new ArrayList<>();
    private String memNo;
    private Activity context;
    private OnClickedListener listener;
    private boolean showAmount = false;

    private ImageLoader imageLoader;

    public RedEnvelopeAdapter(Activity context, ImageLoader imageLoader){
        this.context = context;
        this.memNo = PreferenceUtil.getMemNO(context);

        // 設定頭像
        this.imageLoader = imageLoader;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_red_envelope_list_item, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        // TODO 改成真正的資料綁定
        RedEnvelopeHomeListData item = list.get(position);

        // 收到的紅包
        if(memNo.equals(item.getToMem())){
            // 發出者稱呼
            String displayName = item.getSender() + context.getString(R.string.sent_you);
            if(item.getTxType().equals(RedEnvelopeType.TYPE_MONEY_GOD)){
                displayName += context.getString(R.string.money_god_red_envelope);
            }else{
                displayName += context.getString(R.string.red_envelope);
            }
            holder.textName.setText(displayName);

            // 紅包金額
            String formattedAmount = FormatUtil.toDecimalFormatFromString(item.getAmount(), true);
            holder.textAmount.setText(formattedAmount);
            // 收到紅包的時間
            String formattedTime = FormatUtil.toTimeFormatted(item.getCreateDate());
            holder.textTime.setText(formattedTime);
            // 發出者的訊息
            holder.textMessage.setText(item.getSenderMessage());
            // 若有replyTime：不顯示「回覆」按鈕
            if(TextUtils.isEmpty(item.getReplyTime())) {
                holder.buttonReply.setVisibility(View.VISIBLE);
                holder.buttonReply.setTag(position);
            }else {
                holder.buttonReply.setVisibility(View.INVISIBLE);
            }
            imageLoader.loadImage(item.getSenderMem(), holder.imagePhoto);

            // 發出的紅包
        }else{
            String displayName = item.getToMemName() + context.getString(R.string.replied_your_red_envelope);
            // 收到者稱呼
            holder.textName.setText(displayName);
            // 紅包金額
            String formattedAmount = FormatUtil.toDecimalFormatFromString(item.getAmount(), true);
            holder.textAmount.setText(formattedAmount);
            // 收到回覆的時間
            String formattedTime = FormatUtil.toTimeFormatted(item.getReplyTime());
            holder.textTime.setText(formattedTime);
            // 回覆的訊息
            holder.textMessage.setText(item.getReplyMessage());

            holder.buttonReply.setVisibility(View.INVISIBLE);
            imageLoader.loadImage(item.getToMem(), holder.imagePhoto);

        }

        // 設定是否揭露金額
        if(showAmount){
            holder.textAmount.setVisibility(View.VISIBLE);
        }else{
            holder.textAmount.setVisibility(View.INVISIBLE);
        }

        // 設定是否顯示未讀標記
        holder.imageNew.setVisibility(item.getReadFlag().equals("1") ? View.INVISIBLE : View.VISIBLE);

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
    public void setList(ArrayList<RedEnvelopeHomeListData> list){
        if(this.list == null){
            this.list = list;
        }else{
            this.list.clear();
            this.list.addAll(list);
        }
        notifyDataSetChanged();
    }

    /**
     * 設定是否揭露金額
     */
    public void setShowAmount(boolean showAmount){
        this.showAmount = showAmount;
        notifyDataSetChanged();
    }

    /**
     * 設定點擊事件處理
     */
    public void setOnClickedListener(OnClickedListener listener){
        this.listener = listener;
    }

    class ViewHolder extends RecyclerView.ViewHolder{

        public TextView textName;
        public TextView textTime;
        public TextView textAmount;
        public TextView textMessage;
        public Button buttonReply;
        public ImageView imagePhoto;
        public ImageView imageNew;

        public ViewHolder(View itemView) {
            super(itemView);
            textName = (TextView) itemView.findViewById(R.id.text_name);
            textTime = (TextView) itemView.findViewById(R.id.text_time);
            textAmount = (TextView) itemView.findViewById(R.id.text_amount);
            textMessage = (TextView) itemView.findViewById(R.id.text_message);
            buttonReply = (Button) itemView.findViewById(R.id.button_reply);
            buttonReply.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null) {
                        // 通知有點擊事件
                        RedEnvelopeHomeListData item = list.get((int) v.getTag());
                        listener.onReplyClicked(item);
                    }
                }
            });
            imagePhoto = (ImageView) itemView.findViewById(R.id.image_photo);
            imageNew = (ImageView) itemView.findViewById(R.id.image_new);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(listener != null){
                        // 通知有點擊事件
                        RedEnvelopeHomeListData item = list.get((int) v.getTag());
                        if(memNo.equals(item.getToMem())) {
                            listener.onReceivedItemClicked(item.getTxfSeq(), item.getTxfdSeq());
                        }else{
                            listener.onSentItemClicked(item.getTxfSeq(), item.getTxfdSeq());
                        }
                    }
                }
            });
        }
    }

    public interface OnClickedListener{
        void onSentItemClicked(String txfSeq, String txfdSeq);
        void onReceivedItemClicked(String txfSeq, String txfdSeq);
        void onReplyClicked(RedEnvelopeHomeListData data);
    }
}
