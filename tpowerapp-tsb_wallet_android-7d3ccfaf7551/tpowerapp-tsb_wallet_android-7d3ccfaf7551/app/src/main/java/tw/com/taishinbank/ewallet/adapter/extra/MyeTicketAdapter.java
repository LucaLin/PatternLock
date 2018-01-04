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
import tw.com.taishinbank.ewallet.imagehelper.ImageLoader;
import tw.com.taishinbank.ewallet.interfaces.CouponType;
import tw.com.taishinbank.ewallet.model.extra.TicketListData;
import tw.com.taishinbank.ewallet.util.ContactUtil;
import tw.com.taishinbank.ewallet.util.FormatUtil;

public class MyeTicketAdapter extends BaseAdapter {

    // ----
    //
    // ----
    private Context context;
    private LayoutInflater inflater;
    private ImageLoader imageLoader;

    // ----
    // Data Model
    // ----
    private List<TicketListData> ticketList;

    public MyeTicketAdapter(Context context) {
        this.context = context;
        inflater = LayoutInflater.from(context);
        imageLoader = new ImageLoader(((Activity) context), 200);
    }

    public MyeTicketAdapter(List<TicketListData> list, Context context) {
        this.context = context;
        this.ticketList = list;
        inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        if(ticketList != null){
            return ticketList.size();
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
        return ticketList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        TicketListData ticket = ticketList.get(position);

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

        setDataViewValue(viewHolder, ticket);

        return convertView;
    }

    // ---
    // Set View Content
    // ----
    private void setDataViewValue (ViewHolder viewHolder, TicketListData ticket) {
        viewHolder.txtTime.setText(FormatUtil.toTimeFormatted(ticket.getLastUpdate()));
        viewHolder.txtProd.setText(ticket.getTitle());
        viewHolder.txtContent.setText(ticket.getTitle());

        viewHolder.txtType.setVisibility(View.GONE);

        if(ticket.getReadFlag().equals("0")) {
            viewHolder.imgNew.setVisibility(View.VISIBLE);
        } else {
            viewHolder.imgNew.setVisibility(View.GONE);
        }

        if(!TextUtils.isEmpty(ticket.getIconUrl())) {
            File imgURL = new File(ticket.getIconUrl());
            String imgFilePath = ContactUtil.TicketFolderPath + File.separator + imgURL.getName();

            File imgFile = new File(imgFilePath);
            if (imgFile.exists()) {
                Bitmap imgBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
                viewHolder.imgBanner.setImageBitmap(imgBitmap);
            } else {
                viewHolder.imgBanner.setImageResource(R.drawable.img_banner_default);
            }
        } else {
            viewHolder.imgBanner.setImageResource(R.drawable.img_banner_default);
        }



        viewHolder.txtContent.setVisibility(View.GONE);
    }

    // ----
    // Getter and setter
    // ----
    public List<TicketListData> getTicketList() {
        return ticketList;
    }

    public void setTicketList(List<TicketListData> ticketList) {
        this.ticketList = ticketList;
    }


    // ----
    // View Holder
    // ----
    static class ViewHolder {

        //For Data
        public ImageView imgNew;

        public TextView txtTime;
        public ImageView imgBanner;

        public TextView txtProd;
        public TextView txtContent;
        public TextView txtType;

        public ViewHolder(View itemView) {
            imgNew          = (ImageView) itemView.findViewById(R.id.img_new);

            txtTime         = (TextView) itemView.findViewById(R.id.txt_time);
            imgBanner = (ImageView) itemView.findViewById(R.id.img_coupon_banner);
            txtProd = (TextView) itemView.findViewById(R.id.txt_coupon_prod);
            txtContent = (TextView) itemView.findViewById(R.id.txt_coupon_content);
            txtType         = (TextView) itemView.findViewById(R.id.txt_type);
        }
    }

}
