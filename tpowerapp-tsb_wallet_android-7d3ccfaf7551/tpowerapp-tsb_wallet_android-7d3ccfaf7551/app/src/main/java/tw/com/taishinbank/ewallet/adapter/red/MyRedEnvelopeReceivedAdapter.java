package tw.com.taishinbank.ewallet.adapter.red;

import android.view.View;

import java.util.ArrayList;

import tw.com.taishinbank.ewallet.imagehelper.ImageLoader;
import tw.com.taishinbank.ewallet.model.red.RedEnvelopeReceivedDetail;
import tw.com.taishinbank.ewallet.model.red.RedEnvelopeReceivedHeader;
import tw.com.taishinbank.ewallet.util.FormatUtil;


public class MyRedEnvelopeReceivedAdapter extends MyRedEnvelopeAdapter<RedEnvelopeReceivedHeader> {
    private ImageLoader imageLoader;
    private ArrayList<RedEnvelopeReceivedHeader> selfList;
    private ArrayList<RedEnvelopeReceivedHeader> detailList;

    public MyRedEnvelopeReceivedAdapter(ArrayList<RedEnvelopeReceivedHeader> selfList, ArrayList<RedEnvelopeReceivedHeader> detailList, ImageLoader imageLoader){
        super(detailList);
        this.selfList = selfList;
        this.imageLoader = imageLoader;
        this.detailList = detailList;
    }

    @Override
    public void onBindViewHolder(MyRedEnvelopeAdapter.ViewHolder holder, int position) {
        // 顯示self list的資料
        RedEnvelopeReceivedHeader item = selfList.get(position);
        // 發出者稱呼
        holder.textName.setText(item.getSender());
        // 紅包金額
        String formattedAmount = FormatUtil.toDecimalFormatFromString(item.getAmount(), true);
        holder.textAmount.setText(formattedAmount);
        // 收到紅包的時間
        String formattedTime = FormatUtil.toTimeFormatted(item.getCreateDate());
        holder.textTime.setText(formattedTime);
        holder.itemView.setTag(position);
        // 設頭像為發送者的
        imageLoader.loadImage(item.getSenderMem(), holder.imagePhoto);

        boolean hasNew = false;
        for (RedEnvelopeReceivedDetail receivedDetail: detailList.get(position).getTxDetailList()) {
            if(receivedDetail.getReadFlag() != null && receivedDetail.getReadFlag().equals("0"))
            {
                hasNew = true;
                break;
            }
        }

        // 設定是否顯示未讀標記
        holder.imageNew.setVisibility( !hasNew ? View.INVISIBLE : View.VISIBLE);

    }
}
