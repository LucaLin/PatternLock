package tw.com.taishinbank.ewallet.adapter.red;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import tw.com.taishinbank.ewallet.R;
import tw.com.taishinbank.ewallet.imagehelper.ImageLoader;
import tw.com.taishinbank.ewallet.model.red.RedEnvelopeData;


public class RedEnvelopeDetailConfirmAdapter extends RecyclerView.Adapter<RedEnvelopeDetailConfirmAdapter.ViewHolder> {

    private ArrayList<RedEnvelopeData> items;
    private static final int VIEW_TYPE_FIRST = 1;
    private static final int VIEW_TYPE_NORMAL = 2;
    private static final int VIEW_TYPE_LAST = 3;
    private boolean hidenFirstLayout = false;
    private ImageLoader imageLoader;

    public RedEnvelopeDetailConfirmAdapter(Activity context, ArrayList<RedEnvelopeData> items){
        this.items = items;
        imageLoader = new ImageLoader(context, context.getResources().getDimensionPixelSize(R.dimen.list_photo_size));
    }

    public RedEnvelopeDetailConfirmAdapter(Activity context, ArrayList<RedEnvelopeData> items, boolean bHidenFirstLayout){
        this(context, items);
        this.hidenFirstLayout = bHidenFirstLayout;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        int viewResId;
        if(viewType == VIEW_TYPE_FIRST){
            viewResId = R.layout.red_envelope_detail_confirm_list_item_1st;
        }else{
            viewResId = R.layout.red_envelope_detail_confirm_list_item;
        }
        View v = LayoutInflater.from(parent.getContext())
                .inflate(viewResId, parent, false);

        return new ViewHolder(v, viewType);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        // TODO 改成真正的資料綁定
        RedEnvelopeData item = items.get(position);
        // 設人名
        holder.textName.setText(item.getName());
        // 設金額
        holder.textAmount.setText(item.getAmount());
        // 設定頭像
        imageLoader.loadImage(item.getMemNo(), holder.imagePhoto);

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
        // 如果是第一個
        if(hidenFirstLayout) {
            if (position == getItemCount() - 1) {
                return VIEW_TYPE_LAST;
            }
        }
        else
        {
            if (position == 0) {
                return VIEW_TYPE_FIRST;
                // 如果是最後一個
            } else if (position == getItemCount() - 1) {
                return VIEW_TYPE_LAST;
            }
        }
        return VIEW_TYPE_NORMAL;
    }

    public void setList(ArrayList<RedEnvelopeData> list){
        if(this.items == null){
            this.items = list;
        }else{
            this.items.clear();
            this.items.addAll(list);
        }
        notifyDataSetChanged();
    }

    class ViewHolder extends RecyclerView.ViewHolder{
        public TextView textName;
        public TextView textAmount;
        public View viewFooter;
        private ImageView imagePhoto;

        public ViewHolder(View itemView, int type) {
            super(itemView);
            textName = (TextView)itemView.findViewById(R.id.text_name);
            textAmount = (TextView)itemView.findViewById(R.id.text_amount);
            viewFooter = itemView.findViewById(R.id.view_footer);
            imagePhoto = (ImageView) itemView.findViewById(R.id.image_photo);


            // 如果是最後一個，為避免被擋住，會把footerview設為visible
            if(type == VIEW_TYPE_LAST){
                viewFooter.setVisibility(View.VISIBLE);
            }
        }
    }
}