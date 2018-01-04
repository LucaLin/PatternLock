package tw.com.taishinbank.ewallet.adapter.sv;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import tw.com.taishinbank.ewallet.adapter.red.SentResultDetailAdapter;
import tw.com.taishinbank.ewallet.imagehelper.ImageLoader;
import tw.com.taishinbank.ewallet.model.red.RedEnvelopeSentResultEach;

public class PaymentResultDetailAdapter extends SentResultDetailAdapter {


    public PaymentResultDetailAdapter(RedEnvelopeSentResultEach[] list, Context context, ImageLoader imageLoader){
        super(list, context, imageLoader);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = super.getView(position, convertView, parent);
        ViewHolder viewHolder = (ViewHolder) view.getTag();
        // 隱藏金額與狀態圖示
        viewHolder.layoutAmount.setVisibility(View.GONE);
        viewHolder.imageResult.setVisibility(View.GONE);

        return view;
    }

}
