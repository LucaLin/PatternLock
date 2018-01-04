package tw.com.taishinbank.ewallet.adapter;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import tw.com.taishinbank.ewallet.R;
import tw.com.taishinbank.ewallet.imagehelper.ImageLoader;
import tw.com.taishinbank.ewallet.model.LocalContact;
import tw.com.taishinbank.ewallet.model.Selectable;
import tw.com.taishinbank.ewallet.util.FormatUtil;


public class ContactsAdapter extends BaseAdapter implements Filterable{

    private ArrayList<Selectable<LocalContact>> contactsOriginal; // 用來儲存原始的資料列表
    private ArrayList<Selectable<LocalContact>> contactsDisplay; // 用來儲存要顯示的資料列表
    private boolean showCheckbox;
    private OnSelectedItemsChangedListener listener;
    private LayoutInflater inflater;
    private ContactsFilter filter;
    private ImageLoader mImageLoader;
    private int lastSelectedPosition;

    public ContactsAdapter(Context context, boolean showCheckbox, ImageLoader imageLoader){
        inflater = LayoutInflater.from(context);
        this.contactsDisplay = new ArrayList<>();
        contactsOriginal = new ArrayList<>();
        this.showCheckbox = showCheckbox;
        mImageLoader = imageLoader;
    }

    /**
     * 設定資料列表（包含原始與顯示用的）
     */
    public void setContacts(ArrayList<LocalContact> contacts){
        this.contactsDisplay.clear();
        this.contactsOriginal.clear();
        if(contacts != null && contacts.size() > 0){
            for(int i = 0; i < contacts.size(); i++){
                contactsOriginal.add(new Selectable<>(contacts.get(i)));
            }
            this.contactsDisplay = contactsOriginal;
        }

        // 產生新的boolean陣列紀錄是否被點選
        if(showCheckbox){
            notifySelectedItemChange();
        }
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        if(contactsDisplay != null){
            return contactsDisplay.size();
        }
        return 0;
    }

    @Override
    public Selectable<LocalContact> getItem(int position) {
        return contactsDisplay.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;

        // 如果view還沒建立過
        if(convertView == null){
            // 建立view跟viewholder
            convertView = inflater.inflate(R.layout.contact_list_item, parent, false);
            holder = new ViewHolder(convertView);
            // 將viewholder利用view的tag存起來
            convertView.setTag(holder);
        }else{
            // 從view的tag取出viewholder
            holder = (ViewHolder) convertView.getTag();
        }

        LocalContact contact = contactsDisplay.get(position).item;
        // TODO 改成真正的資料綁定

        // 設定顯示名稱
        holder.textName.setText(contact.getDisplayName());

        // 設定電話
        holder.textPhone.setText(FormatUtil.toCellPhoneNumberFormat(contact.getPhoneNumber()));

        // 設定頭像
        mImageLoader.loadImage(contact.getMemNO(), holder.imagePhoto);

        // 設定是否顯示儲值帳戶
        if(contact.isSVAccount()){
            holder.textIsSVAccount.setVisibility(View.VISIBLE);
        }else{
            holder.textIsSVAccount.setVisibility(View.GONE);
        }

        // 根據是否為新增的flag，設定底色
        if(contact.isNewAdded()){
            holder.itemView.setBackgroundColor(Color.parseColor("#FFF1ED"));
        }else{
            holder.itemView.setBackgroundColor(Color.TRANSPARENT);
        }

        // 如果有要顯示checkbox
        if(showCheckbox) {
            // 設定checkbox的狀態並設定tag為位置，在checkedChange時才能知道是誰被改變
            holder.checkBox.setOnCheckedChangeListener(null);
            holder.checkBox.setChecked(contactsDisplay.get(position).isChecked);
            holder.checkBox.setOnCheckedChangeListener(checkedChangeListener);
            holder.checkBox.setTag(position);
        }

        return convertView;
    }

    public int getLastSelectedPosition() {
        return lastSelectedPosition;
    }

    public void setSelection(int position, boolean checked){
        contactsDisplay.get(position).isChecked = checked;
        notifyDataSetChanged();
    }

    /**
     * 根據傳入的值設定全選或全不選
     */
    public void setSelectAll(boolean isSelectall){
        for(int i = 0; i < contactsDisplay.size(); i++){
            contactsDisplay.get(i).isChecked = isSelectall;
        }
        notifyDataSetChanged();
        notifySelectedItemChange();
    }

    /**
     * 設置選中項目變更的listener
     */
    public void setOnSelectedChangedListener(OnSelectedItemsChangedListener listener){
        this.listener = listener;
    }

    class ViewHolder extends RecyclerView.ViewHolder{

        public TextView textName;
        public TextView textPhone;
        public TextView textIsSVAccount;
        public CheckBox checkBox;
        public ImageView imagePhoto;

        public ViewHolder(View itemView) {
            super(itemView);
            textName = (TextView)itemView.findViewById(R.id.text_name);
            textPhone = (TextView)itemView.findViewById(R.id.text_phone);
            textIsSVAccount = (TextView)itemView.findViewById(R.id.text_sv_account);
            checkBox = (CheckBox)itemView.findViewById(android.R.id.checkbox);
            imagePhoto = (ImageView)itemView.findViewById(R.id.image_photo);

            // 設定是否顯示checkbox
            if(showCheckbox){
                checkBox.setVisibility(View.VISIBLE);
            }
        }
    }

    /**
     * checkbox勾選變更listener
     */
    private CompoundButton.OnCheckedChangeListener checkedChangeListener = new CompoundButton.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            // 有變更時，更新model列表狀態，發出選中項目的變更
            int position = (int) buttonView.getTag();
            // 計算選中數量
//            if(maxSelectLimit > 0) {
//                int count = 0;
//                if (isChecked) {
//                    ArrayList<Selectable<LocalContact>> selectedContacts = new ArrayList<>();
//                    for (int i = 0; i < contactsOriginal.size(); i++) {
//                        if (contactsOriginal.get(i).isChecked) {
//                            count++;
//                        }
//                    }
//                }
//                if (maxSelectLimit > count) {
//                    contactsDisplay.get(position).isChecked = isChecked;
//                    notifySelectedItemChange();
//                } else {
//                    notifyDataSetChanged();
//                }
//            }else{
            if(isChecked){
                lastSelectedPosition = position;
            }
                contactsDisplay.get(position).isChecked = isChecked;
                notifySelectedItemChange();
//            }
        }
    };
//
//    public interface OnSelectedItemsChangedListener{
//        void OnSelectedItemsChanged(ArrayList<Selectable<LocalContact>> selectedContacts);
//    }

    /**
     * 通知選中資料更新
     */
    public void notifySelectedItemChange(){
        if(listener != null) {
            // 計算選中數量
            ArrayList<Selectable<LocalContact>> selectedContacts = new ArrayList<>();
            for (int i = 0; i < contactsOriginal.size(); i++) {
                if(contactsOriginal.get(i).isChecked) {
                    selectedContacts.add(contactsOriginal.get(i));
                }
            }
            listener.OnSelectedItemsChanged(selectedContacts);
        }
    }

    public interface OnSelectedItemsChangedListener{
        void OnSelectedItemsChanged(ArrayList<Selectable<LocalContact>> selectedContacts);
    }

    // Filterable必須實作的方法
    @Override
    public Filter getFilter() {
        if(filter == null){
            filter = new ContactsFilter();
        }
        return filter;
    }

    /**
     * 自定義的fiter類別，主要用在聯絡人搜尋
     */
    private class ContactsFilter extends Filter{

        @Override
        protected FilterResults performFiltering(CharSequence constraint) {

            // 建立一個過濾結果的物件，接下來會用來儲存過濾結果
            FilterResults result = new FilterResults();

            // 如果搜尋的輸入字串不為空
            if(constraint != null && constraint.toString().length() > 0)
            {
                ArrayList<Selectable<LocalContact>> filteredItems = new ArrayList<>();
                for(int i = 0, l = contactsOriginal.size(); i < l; i++)
                {
                    LocalContact localContact = contactsOriginal.get(i).item;
                    // 如果顯示名稱或電話號碼有包含搜尋字串
                    if((localContact.getDisplayName() != null && localContact.getDisplayName().contains(constraint))
                        || (localContact.getPhoneNumber() != null && localContact.getPhoneNumber().contains(constraint))) {
                        // 將目前資料加入過濾結果列表
                        filteredItems.add(contactsOriginal.get(i));
                    }
                }
                // 將搜尋結果列表與數量設到搜尋結果
                result.count = filteredItems.size();
                result.values = filteredItems;
            }
            else // 如果搜尋字串為空，直接使用原本的列表
            {
                synchronized(this)
                {
                    result.values = contactsOriginal;
                    result.count = contactsOriginal.size();
                }
            }
            return result;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            // 將顯示列表設為過濾結果，並通知列表更新
            contactsDisplay = (ArrayList<Selectable<LocalContact>>) results.values;
            notifyDataSetChanged();
        }
    }
}
