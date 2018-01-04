package tw.com.taishinbank.ewallet.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import tw.com.taishinbank.ewallet.R;
import tw.com.taishinbank.ewallet.imagehelper.ImageLoader;
import tw.com.taishinbank.ewallet.model.LocalContact;
import tw.com.taishinbank.ewallet.model.Selectable;


public class ContactsSeletedAdapter extends RecyclerView.Adapter<ContactsSeletedAdapter.ViewHolder> {

    private ArrayList<Selectable<LocalContact>> contacts;
    private OnItemRemovedListener listener;
    private ImageLoader imageLoader;

    public ContactsSeletedAdapter(ImageLoader imageLoader) {
        this.imageLoader = imageLoader;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.contact_selected_list_item, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        LocalContact item = contacts.get(position).item;

        // 設定名稱
        holder.textName.setText(item.getDisplayName());
        // 設定顯示頭像
        imageLoader.loadImage(item.getMemNO(), holder.imagePhoto);

        holder.buttonDelete.setTag(position);
    }

    @Override
    public int getItemCount() {
        if(contacts != null){
            return contacts.size();
        }
        return 0;
    }

    public ArrayList<LocalContact> getContacts() {
        ArrayList<LocalContact> tempContacts = new ArrayList<LocalContact>();
        for (Selectable<LocalContact>  selectable : contacts) {
            tempContacts.add(selectable.item);
        }
        return tempContacts;
    }

    public void setContacts(ArrayList<Selectable<LocalContact>> newContacts){
        contacts = newContacts;
        notifyDataSetChanged();
    }

    class ViewHolder extends RecyclerView.ViewHolder{

        public TextView textName;
        public ImageView imagePhoto;
        public ImageButton buttonDelete;

        public ViewHolder(View itemView) {
            super(itemView);
            textName = (TextView) itemView.findViewById(R.id.text_name);
            imagePhoto = (ImageView) itemView.findViewById(R.id.image_photo);
            buttonDelete = (ImageButton) itemView.findViewById(R.id.button_delete);
            buttonDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = (int) v.getTag();
                    contacts.get(position).isChecked = false;
                    if(listener != null){
                        listener.OnItemRemoved();
                    }
                }
            });
        }
    }

    /**
     * 設置選中項目變更的listener
     */
    public void setOnItemRemovedListener(OnItemRemovedListener listener){
        this.listener = listener;
    }

    public interface OnItemRemovedListener{
        void OnItemRemoved();
    }
}
