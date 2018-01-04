package tw.com.taishinbank.ewallet.adapter.red;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import tw.com.taishinbank.ewallet.R;
import tw.com.taishinbank.ewallet.imagehelper.ImageLoader;
import tw.com.taishinbank.ewallet.model.red.RedEnvelopeSentResultEach;
import tw.com.taishinbank.ewallet.util.FormatUtil;

public class SentResultDetailAdapter extends BaseAdapter {

    protected RedEnvelopeSentResultEach[] list;
    protected LayoutInflater inflater;
    protected ImageLoader imageLoader;

    public SentResultDetailAdapter(RedEnvelopeSentResultEach[] list, Context context, ImageLoader imageLoader){
        this.list = list;
        inflater = LayoutInflater.from(context);
        this.imageLoader = imageLoader;
    }

    @Override
    public int getCount() {
        if(list != null){
            return list.length;
        }
        return 0;
    }

    @Override
    public Object getItem(int position) {
        return list[position];
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;

        if(convertView == null){
            //取得listItem容器 view
            convertView = inflater.inflate(R.layout.fragment_sent_result_detail_list_item, null);

            //建構listItem內容view
            viewHolder = new ViewHolder(convertView);

            //設置容器內容
            convertView.setTag(viewHolder);
        }
        else{
            viewHolder = (ViewHolder) convertView.getTag();
        }

        RedEnvelopeSentResultEach item = list[position];
        // 設定名稱
        viewHolder.textName.setText(item.getName());
        // 設定帳戶
        viewHolder.textAccount.setText(FormatUtil.toAccountFormat(item.getAccount()));
        // 設定金額
        viewHolder.textAmount.setText(FormatUtil.toDecimalFormatFromString(item.getPerAmount(), true));
        // 設定成功或失敗的圖示
        if(item.getResult().equalsIgnoreCase("Y")) {
            viewHolder.imageResult.setImageResource(R.drawable.ic_red_popup_succeed);
        }else {
            viewHolder.imageResult.setImageResource(R.drawable.ic_red_popup_failed);
        }
        // 設頭像
        imageLoader.loadImage(item.getToMem(), viewHolder.imagePhoto);

        return convertView;
    }

    protected class ViewHolder{

        public TextView textName;
        public TextView textAmount;
        public TextView textAccount;
        public ImageView imagePhoto;
        public ImageView imageResult;
        public View layoutAmount;

        public ViewHolder(View itemView) {
            textName = (TextView) itemView.findViewById(R.id.text_name);
            textAccount = (TextView) itemView.findViewById(R.id.text_account);
            textAmount = (TextView) itemView.findViewById(R.id.text_amount);
            imagePhoto = (ImageView) itemView.findViewById(R.id.image_photo);
            imageResult = (ImageView) itemView.findViewById(R.id.image_result);
            layoutAmount = itemView.findViewById(R.id.layout_amount);
        }
    }
}
