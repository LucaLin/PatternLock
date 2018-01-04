package tw.com.taishinbank.ewallet.adapter.red;

import android.graphics.Color;
import android.view.View;

import java.util.ArrayList;

import tw.com.taishinbank.ewallet.R;
import tw.com.taishinbank.ewallet.imagehelper.ImageLoader;
import tw.com.taishinbank.ewallet.model.red.RedEnvelopeSentDetail;
import tw.com.taishinbank.ewallet.model.red.RedEnvelopeSentHeader;
import tw.com.taishinbank.ewallet.util.FormatUtil;


public class MyRedEnvelopeSentAdapter extends MyRedEnvelopeAdapter<RedEnvelopeSentHeader> {
    private ImageLoader imageLoader;
    public  MyRedEnvelopeSentAdapter(ArrayList<RedEnvelopeSentHeader> list, ImageLoader imageLoader){
        super(list);
        this.imageLoader = imageLoader;
    }

    @Override
    public void onBindViewHolder(MyRedEnvelopeAdapter.ViewHolder holder, int position) {
        // TODO 改成真正的資料綁定
        RedEnvelopeSentHeader item = list.get(position);
        // 收到者
        holder.textName.setText(item.getMergedName());
        // 紅包金額
        String formattedAmount = FormatUtil.toDecimalFormatFromString(item.getAmount(), true);
        holder.textAmount.setText(formattedAmount);
        // 收到紅包的時間
        String formattedTime = FormatUtil.toTimeFormatted(item.getCreateDate());
        holder.textTime.setText(formattedTime);
        holder.itemView.setTag(position);
        // 設頭像，如果不只一人，用台新的icon
        if(item.getTxDetailList().size() > 1){
            holder.imagePhoto.setImageResource(R.drawable.img_taishin_photo);
            holder.imagePhoto.setBackgroundColor(Color.TRANSPARENT);
        // 如果只有一人，設那一個人的頭像
        }else if(item.getTxDetailList().size() > 0){
            holder.imagePhoto.setBackgroundResource(R.drawable.img_default_photo_gary);
            imageLoader.loadImage(item.getTxDetailList().get(0).getToMem(), holder.imagePhoto);
        }

        boolean hasNew = false;
        for (RedEnvelopeSentDetail receivedDetail: item.getTxDetailList()) {
            if(receivedDetail.getReadFlag() != null  && receivedDetail.getReadFlag().equals("0"))
            {
                hasNew = true;
                break;
            }
        }

        // 設定是否顯示未讀標記
        holder.imageNew.setVisibility( !hasNew ? View.INVISIBLE : View.VISIBLE);
    }
}
