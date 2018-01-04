package tw.com.taishinbank.ewallet.adapter.wallethome;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Set;

import tw.com.taishinbank.ewallet.R;
import tw.com.taishinbank.ewallet.gcm.WalletGcmListenerService.MyPayPushType;
import tw.com.taishinbank.ewallet.imagehelper.ImageLoader;
import tw.com.taishinbank.ewallet.model.wallethome.WalletHomePushMsg;
import tw.com.taishinbank.ewallet.util.FormatUtil;
import tw.com.taishinbank.ewallet.util.PreferenceUtil;


public class WalletHomeAdapter extends RecyclerView.Adapter<WalletHomeAdapter.ViewHolder> {

    private static final int VIEW_TYPE_NORMAL = 2;
    private static final int VIEW_TYPE_LAST = 3;
    private ArrayList<WalletHomePushMsg> list = new ArrayList<>();
    private OnItemClickedListener onItemClickedListener;
    private OnMoreClickedListener onMoreClickedListener;
    private boolean showAmount = false;
    private boolean showMore = false;

    private ImageLoader imageLoader;
    private Set<String> readSet;

    public WalletHomeAdapter(Activity context, ImageLoader imageLoader){

        // 設定頭像
        this.imageLoader = imageLoader;
        readSet = PreferenceUtil.getSystemMessageReadList(context);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_wallethome_list_item, parent, false);
        return new ViewHolder(v, viewType);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        WalletHomePushMsg item = list.get(position);

        String urlString = item.getUrl();
        setViewContentByType(urlString, holder, item);

        holder.itemView.setTag(position);
    }

    private void setViewContentByType(String actionType, ViewHolder holder, WalletHomePushMsg item){
        // 初始數值
        int pushSender = item.getPushSender();
        String pushMessage = item.getPushMessage();
        String senderMessage = item.getSenderMessage();
        String pushDate = FormatUtil.toTimeFormatted(item.getPushDate());
        String amount = (item.getAmount() > 0) ? FormatUtil.toDecimalFormat(item.getAmount(), true) : null;
        int visibilityOfNew = item.hasRead() ? View.GONE : View.VISIBLE;
        int visibilityOfAreaBelowDivider = View.VISIBLE;
        int itemBackgroundResId = R.drawable.img_h_home_normal_card_360;

        // 根據類型設定
        if(actionType.startsWith(MyPayPushType.coupon.name())){
            itemBackgroundResId = R.drawable.img_h_home_tickets_card_360;

            // 紅包類
        }else if(actionType.startsWith(MyPayPushType.redrecordin.name()) || actionType.startsWith(MyPayPushType.redrecordout.name())){
            itemBackgroundResId = R.drawable.img_h_home_red_card_360;

            // 系統訊息類
        }else if(actionType.startsWith(MyPayPushType.nc.name())){
//            visibilityOfAreaBelowDivider = View.GONE;
            pushSender = -1;
            // 用createDate比對
            visibilityOfNew = readSet.contains(item.getCreateDate()) ? View.GONE : View.VISIBLE;
            pushDate = FormatUtil.toDateFormatted(item.getPushDate().substring(0,8));
        }

        // 設定ViewContent
        // 設定是否揭露金額
        if(showAmount){
            holder.textAmount.setVisibility(View.VISIBLE);
        }else{
            holder.textAmount.setVisibility(View.INVISIBLE);
        }

        if(pushSender > 0) {
            imageLoader.loadImage(String.valueOf(pushSender), holder.imagePhoto);
        }else{
            holder.imagePhoto.setImageResource(R.drawable.img_taishin_photo);
        }
        holder.textName.setText(pushMessage);
        holder.textMessage.setText(senderMessage);
        holder.textTime.setText(pushDate);
        holder.textAmount.setText(amount);
        holder.imageNew.setVisibility(visibilityOfNew);
        holder.imageCardBackgound.setImageResource(itemBackgroundResId);

        holder.layoutBottom.setVisibility(visibilityOfAreaBelowDivider);

    }


    @Override
    public int getItemCount() {
        if(list == null){
            return 0;
        }
        return list.size();
    }

    @Override
    public int getItemViewType(int position) {
            // 如果是第一個
        if (position == getItemCount() - 1) {
            return VIEW_TYPE_LAST;
        }
        return VIEW_TYPE_NORMAL;
    }

    /**
     * 變更列表資料
     */
    public void setList(ArrayList<WalletHomePushMsg> list){
        if(this.list == null){
            this.list = list;
        }else{
            this.list.clear();
            this.list.addAll(list);
        }
        notifyDataSetChanged();
    }

    public void setList(ArrayList<WalletHomePushMsg> list, boolean showMore){
        this.showMore = showMore;
        setList(list);
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
    public void setOnItemClickedListener(OnItemClickedListener listener){
        this.onItemClickedListener = listener;
    }

    public void setOnMoreClickedListener(OnMoreClickedListener listener){
        this.onMoreClickedListener = listener;
    }

    class ViewHolder extends RecyclerView.ViewHolder{

        public TextView textName;
        public TextView textTime;
        public TextView textAmount;
        public TextView textMessage;
        public Button buttonReply;
        public ImageView imagePhoto;
        public ImageView imageNew;
        public ImageView imageCardBackgound;
        public View layoutBottom;
        public Button buttonMore;

        public ViewHolder(View itemView, int type) {
            super(itemView);
            textName = (TextView) itemView.findViewById(R.id.text_name);
            textTime = (TextView) itemView.findViewById(R.id.text_time);
            textAmount = (TextView) itemView.findViewById(R.id.text_amount);
            textMessage = (TextView) itemView.findViewById(R.id.text_message);
            buttonReply = (Button) itemView.findViewById(R.id.button_reply);

            imagePhoto = (ImageView) itemView.findViewById(R.id.image_photo);
            imageNew = (ImageView) itemView.findViewById(R.id.image_new);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(onItemClickedListener != null){
                        // 通知有點擊事件
                        WalletHomePushMsg item = list.get((int) v.getTag());
                        onItemClickedListener.onItemClicked(item);
                    }
                }
            });
            imageCardBackgound = (ImageView) itemView.findViewById(R.id.image_card_background);
            layoutBottom = itemView.findViewById(R.id.layout_bottom);
            buttonMore = (Button) itemView.findViewById(R.id.button_more);

            // 如果是最後一個，為避免被擋住，會把footerview設為visible
            if(type == VIEW_TYPE_LAST && showMore){
                buttonMore.setVisibility(View.VISIBLE);
                buttonMore.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(onMoreClickedListener != null){
                            onMoreClickedListener.onClicked();
                        }
                    }
                });
            }
        }
    }

    public interface OnItemClickedListener{
        void onItemClicked(WalletHomePushMsg item);
    }

    public interface OnMoreClickedListener{
        void onClicked();
    }

}
