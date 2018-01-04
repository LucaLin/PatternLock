package tw.com.taishinbank.ewallet.adapter.extra;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import tw.com.taishinbank.ewallet.R;
import tw.com.taishinbank.ewallet.imagehelper.ImageLoader;
import tw.com.taishinbank.ewallet.model.extra.CouponSend;


public class MyCouponSendConfirmAdapter extends RecyclerView.Adapter<MyCouponSendConfirmAdapter.ViewHolder> {

    private List<CouponSend> items;
    private ImageLoader imageLoader;

    public MyCouponSendConfirmAdapter(Activity context) {
        imageLoader = new ImageLoader(context, context.getResources().getDimensionPixelSize(R.dimen.list_photo_size));
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_extra_my_coupon_send_confirm_list_item, parent, false);

        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        // TODO 改成真正的資料綁定
        CouponSend item = items.get(position);
        // 設人名
        holder.txtName.setText(item.getReceiver().getNickname());
        // 設優惠券 title1
        holder.txtTitle1.setText(item.getCoupon().getTitle());
        // 設優惠券 title2
        holder.txtTitle2.setText(item.getCoupon().getSubTitle());

        //設頭像
        imageLoader.loadImage(item.getReceiver().getMemNO(), holder.imgPhoto);

    }

    @Override
    public int getItemCount() {
        if(items != null) {
            return items.size();
        }
        return 0;
    }

    @Override
    public int getItemViewType(int position) {
        return 0;
    }

    public void setList(List<CouponSend> list){
        this.items = list;
    }

    class ViewHolder extends RecyclerView.ViewHolder{
        public TextView txtName;
        public TextView txtTitle1;
        public TextView txtTitle2;
        private ImageView imgPhoto;

        public ViewHolder(View itemView) {
            super(itemView);
            txtName = (TextView)itemView.findViewById(R.id.txt_name);
            txtTitle1 = (TextView)itemView.findViewById(R.id.txt_title1);
            txtTitle2 = (TextView) itemView.findViewById(R.id.txt_title2);
            imgPhoto = (ImageView) itemView.findViewById(R.id.img_photo);
        }
    }
}
