package tw.com.taishinbank.ewallet.adapter.red;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import tw.com.taishinbank.ewallet.R;
import tw.com.taishinbank.ewallet.imagehelper.ImageLoader;


public abstract class MyRedEnvelopeDetailAdapter<T> extends RecyclerView.Adapter<MyRedEnvelopeDetailAdapter.ViewHolder> {

    protected static final String AMOUNT_FORMAT = "金額\n%1$s";

    protected ArrayList<T> list = new ArrayList<>();

    protected ImageLoader imageLoader;

    public MyRedEnvelopeDetailAdapter(ImageLoader imageLoader, ArrayList<T> list){
        this.list = list;
        this.imageLoader = imageLoader;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_my_envelope_detail_list_item, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public int getItemCount() {
        if(list == null){
            return 0;
        }
        return list.size();
    }

    public void setList(ArrayList<T> list){
        if(this.list == null){
            this.list = list;
        }else{
            this.list.clear();
            this.list.addAll(list);
        }
        notifyDataSetChanged();
    }

    class ViewHolder extends RecyclerView.ViewHolder{

        public TextView textName;
        public TextView textTime;
        public TextView textAmount;
        public TextView textMessage;
        public TextView textReplyAmount;
        public ImageView imagePhoto;

        public ViewHolder(View itemView) {
            super(itemView);
            textName = (TextView) itemView.findViewById(R.id.text_name);
            textTime = (TextView) itemView.findViewById(R.id.text_time);
            textAmount = (TextView) itemView.findViewById(R.id.text_amount);
            textMessage = (TextView) itemView.findViewById(R.id.text_message);
            textReplyAmount = (TextView) itemView.findViewById(R.id.text_amount_reply);
            imagePhoto = (ImageView) itemView.findViewById(R.id.image_photo);
        }
    }
}
