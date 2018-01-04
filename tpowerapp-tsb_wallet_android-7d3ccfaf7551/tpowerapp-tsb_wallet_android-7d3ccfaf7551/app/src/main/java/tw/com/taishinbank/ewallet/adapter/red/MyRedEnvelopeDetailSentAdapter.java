package tw.com.taishinbank.ewallet.adapter.red;

import android.text.TextUtils;
import android.view.View;

import java.util.ArrayList;

import tw.com.taishinbank.ewallet.imagehelper.ImageLoader;
import tw.com.taishinbank.ewallet.model.red.RedEnvelopeSentDetail;
import tw.com.taishinbank.ewallet.util.FormatUtil;


public class MyRedEnvelopeDetailSentAdapter extends MyRedEnvelopeDetailAdapter<RedEnvelopeSentDetail> {

    public MyRedEnvelopeDetailSentAdapter(ImageLoader imageLoader, ArrayList<RedEnvelopeSentDetail> list) {
        super(imageLoader, list);
    }

    @Override
    public void onBindViewHolder(MyRedEnvelopeDetailAdapter.ViewHolder holder, int position) {
        // TODO 改成真正的資料綁定
        RedEnvelopeSentDetail item = list.get(position);
        // 收到者
        holder.textName.setText(item.getName());
        // 紅包金額
        String formattedAmount = String.format(AMOUNT_FORMAT, FormatUtil.toDecimalFormatFromString(item.getAmount(), true));
        holder.textAmount.setText(formattedAmount);
        // 根據收到紅包的時間是否為空，設定是否顯示時間與留言文字
        if(TextUtils.isEmpty(item.getReplyDate())){
            holder.textTime.setVisibility(View.INVISIBLE);
            holder.textMessage.setText("尚無留言");
        }else {
            holder.textTime.setVisibility(View.VISIBLE);
            String formattedTime = FormatUtil.toTimeFormatted(item.getReplyDate());
            holder.textTime.setText(formattedTime);
            holder.textMessage.setText(item.getReplyMessage());
        }
        // 設定頭像
        imageLoader.loadImage(item.getToMem(), holder.imagePhoto);

        // 如果有回覆金額就顯示
        if(!TextUtils.isEmpty(item.getReplyAmount())){
            holder.textReplyAmount.setVisibility(View.VISIBLE);
            formattedAmount = String.format(AMOUNT_FORMAT, FormatUtil.toDecimalFormatFromString(item.getReplyAmount(), true));
            holder.textReplyAmount.setText(formattedAmount);
        }else{
            holder.textReplyAmount.setVisibility(View.INVISIBLE);
        }
    }
}
