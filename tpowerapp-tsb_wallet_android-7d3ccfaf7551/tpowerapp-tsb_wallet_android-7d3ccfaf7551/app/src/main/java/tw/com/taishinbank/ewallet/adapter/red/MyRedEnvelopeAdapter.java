package tw.com.taishinbank.ewallet.adapter.red;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import tw.com.taishinbank.ewallet.R;


public abstract class MyRedEnvelopeAdapter<T> extends RecyclerView.Adapter<MyRedEnvelopeAdapter.ViewHolder> {

    protected ArrayList<T> list = new ArrayList<>();
    protected OnItemClickedListener listener;

    public MyRedEnvelopeAdapter(ArrayList<T> list){
        this.list = list;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.my_red_envelope_list_item, parent, false);
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

    /**
     * 回傳指定位置的item資料
     */
    public T getItem(int position){
        if(list != null && position < list.size()){
            return list.get(position);
        }
        return null;
    }

    public void setOnItemClickedListener(OnItemClickedListener listener){
        this.listener = listener;
    }

    class ViewHolder extends RecyclerView.ViewHolder{

        public TextView textName;
        public TextView textTime;
        public TextView textAmount;
        public ImageView imagePhoto;
        public ImageView imageNew;

        public ViewHolder(View itemView) {
            super(itemView);
            textName = (TextView) itemView.findViewById(R.id.text_name);
            textTime = (TextView) itemView.findViewById(R.id.text_time);
            textAmount = (TextView) itemView.findViewById(R.id.text_amount);
            imagePhoto = (ImageView) itemView.findViewById(R.id.image_photo);
            imageNew = (ImageView) itemView.findViewById(R.id.image_new);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(listener != null){
                        listener.onItemClicked((int)v.getTag());
                    }
                }
            });
        }
    }

    public interface OnItemClickedListener{
        void onItemClicked(int position);
    }
}
