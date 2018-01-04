package tw.com.taishinbank.ewallet.adapter.extra;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;
import java.util.List;

import tw.com.taishinbank.ewallet.R;
import tw.com.taishinbank.ewallet.controller.extra.MyCouponActivity;
import tw.com.taishinbank.ewallet.imagehelper.ImageLoader;
import tw.com.taishinbank.ewallet.interfaces.CouponType;
import tw.com.taishinbank.ewallet.model.extra.Coupon;
import tw.com.taishinbank.ewallet.util.ContactUtil;
import tw.com.taishinbank.ewallet.util.FormatUtil;

public class MyCouponAdapter extends BaseAdapter {

    // ----
    //
    // ----
    private Context context;
    private LayoutInflater inflater;
    private ImageLoader imageLoader;

    // ----
    // Data Model
    // ----
    private List<Coupon> couponList;

    public MyCouponAdapter(Context context) {
        this.context = context;
        inflater = LayoutInflater.from(context);
        imageLoader = new ImageLoader(((Activity) context), 200);
    }

    public MyCouponAdapter(List<Coupon> list, Context context) {
        this.context = context;
        this.couponList = list;
        inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        if(couponList != null){
            return couponList.size();
        }
        return 0;
    }

    @Override
    public int getItemViewType(int position) {
        return 0;
    }

    @Override
    public int getViewTypeCount() {
        return 1;
    }

    @Override
    public Object getItem(int position) {
        return couponList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Coupon coupon = couponList.get(position);

        ViewHolder viewHolder;

        if(convertView == null){
            convertView = inflater.inflate(R.layout.fragment_extra_my_coupon_list_item, null);

            //建構listItem內容view
            viewHolder = new ViewHolder(convertView);

            //設置容器內容
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        setDataViewValue(viewHolder, coupon);

        return convertView;
    }

    // ---
    // Set View Content
    // ----
    private void setDataViewValue (ViewHolder viewHolder, Coupon coupon) {
        viewHolder.txtTime.setText(FormatUtil.toTimeFormatted(coupon.getLastUpdate()));
        viewHolder.txtCouponProd.setText(coupon.getTitle());
        viewHolder.txtCouponContent.setText(coupon.getSubTitle());

        if (CouponType.ACT.code.equals(coupon.getStatus())) {
            viewHolder.txtType.setText(CouponType.ACT.description);
            viewHolder.txtType.setBackgroundResource(R.drawable.extra_my_coupon_type_act);

        } else if (CouponType.RECEIVED.code.equals(coupon.getStatus())) {
            viewHolder.txtType.setText(CouponType.RECEIVED.description);
            viewHolder.txtType.setBackgroundResource(R.drawable.extra_my_coupon_type_invite);

        } else if (CouponType.SENT.code.equals(coupon.getStatus())) {
            viewHolder.txtType.setText(CouponType.SENT.description);
            viewHolder.txtType.setBackgroundResource(R.drawable.extra_my_coupon_type_sent);

        } else if (CouponType.TRADED.code.equals(coupon.getStatus())) {
            viewHolder.txtType.setText(CouponType.TRADED.description);
            viewHolder.txtType.setBackgroundResource(R.drawable.extra_my_coupon_type_traded);
        }

        if(coupon.getReadFlag().equals("0"))
        {
            viewHolder.imgNew.setVisibility(View.VISIBLE);
        }

        String imgFilePath = null;
        switch (((MyCouponActivity) context).imageSize.toString())
        {
            case "LARGE":
                if(!TextUtils.isEmpty(coupon.getImagePathL())) {
                    imgFilePath = ContactUtil.FolderPath + File.separator + coupon.getImagePathL();
                }
                break;

            case "MEDIUM":
                if(!TextUtils.isEmpty(coupon.getImagePathM())) {
                    imgFilePath = ContactUtil.FolderPath + File.separator + coupon.getImagePathM();
                }
                break;

            case "SMALL":
                if(!TextUtils.isEmpty(coupon.getImagePathS())) {
                    imgFilePath =  ContactUtil.FolderPath + File.separator + coupon.getImagePathS();
                }
                break;

        }

        if(imgFilePath != null) {
            File imgFile = new File(imgFilePath);
            if (imgFile.exists()) {
                Bitmap imgBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
                viewHolder.imgCouponBanner.setImageBitmap(imgBitmap);
            } else {
                viewHolder.imgCouponBanner.setImageResource(R.drawable.img_banner_default);
            }
        } else {
            viewHolder.imgCouponBanner.setImageResource(R.drawable.img_banner_default);
        }

//        coupon.getImagePathL()
    }

    // ----
    // Getter and setter
    // ----
    public List<Coupon> getCouponList() {
        return couponList;
    }

    public void setCouponList(List<Coupon> couponList) {
        this.couponList = couponList;
    }


    // ----
    // View Holder
    // ----
    static class ViewHolder {

        //For Data
        public ImageView imgNew;

        public TextView txtTime;
        public ImageView imgCouponBanner;

        public TextView txtCouponProd;
        public TextView txtCouponContent;
        public TextView txtType;

        public ViewHolder(View itemView) {
            imgNew          = (ImageView) itemView.findViewById(R.id.img_new);

            txtTime         = (TextView) itemView.findViewById(R.id.txt_time);
            imgCouponBanner = (ImageView) itemView.findViewById(R.id.img_coupon_banner);
            txtCouponProd   = (TextView) itemView.findViewById(R.id.txt_coupon_prod);
            txtCouponContent= (TextView) itemView.findViewById(R.id.txt_coupon_content);
            txtType         = (TextView) itemView.findViewById(R.id.txt_type);
        }
    }

}
