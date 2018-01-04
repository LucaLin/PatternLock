package com.dbs.omni.tw.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.dbs.omni.tw.R;
import com.dbs.omni.tw.model.home.ConsumptionItem;

import java.util.List;


public class CreditCardUnBilledRecyclerAdapter extends RecyclerView.Adapter<CreditCardUnBilledRecyclerAdapter.ViewHolder> {
    private Context context;
    private List<ConsumptionItem> consumptionItems;
//    private LayoutInflater inflater;

    private int index;

    private OnItemClickListener onItemClickListener;
    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }
    public interface OnItemClickListener {
        void onItemClick(View view, int position);
    }

    public CreditCardUnBilledRecyclerAdapter(Context context) {
        this.context = context;
    }

    public CreditCardUnBilledRecyclerAdapter(List<ConsumptionItem> list, Context context) {
        this.context = context;
        this.consumptionItems = list;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.consumption_recycler_item, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        ConsumptionItem itemData = consumptionItems.get(position);

        setDataViewValue(holder,itemData);
    }



    @Override
    public int getItemCount() {
        return consumptionItems.size();
    }

    // ---
    // Set View Content
    // ----
    private void setDataViewValue (ViewHolder viewHolder, ConsumptionItem data) {

        switch (data.getItemType()) {
            case TITLE:
                viewHolder.txtHeaderTitle.setVisibility(View.VISIBLE);
                viewHolder.relativeLayoutItem.setVisibility(View.GONE);
                viewHolder.txtHeaderTitle.setText(data.getHeaderTitle());
                break;

            default:
                viewHolder.txtHeaderTitle.setVisibility(View.GONE);
                viewHolder.relativeLayoutItem.setVisibility(View.VISIBLE);

                viewHolder.txtTitle.setText(data.getConsumptionData().getTitle());
                viewHolder.txtAmount.setText(data.getConsumptionData().getAmount());
                viewHolder.txtDate.setText(data.getConsumptionData().getData());
                viewHolder.txtForeign.setText(data.getConsumptionData().getForeign());
                break;
        }
    }


    // ----
    // Getter and setter
    // ----
    public List<ConsumptionItem> getTransactionList() {
        return consumptionItems;
    }

    public void setList(List<ConsumptionItem> cardList) {
        this.consumptionItems = cardList;
    }


    static class ViewHolder extends RecyclerView.ViewHolder {

        public TextView txtHeaderTitle;
        public TextView txtTitle;
        public TextView txtAmount;
        public TextView txtForeign;
        public TextView txtDate;

        public RelativeLayout relativeLayoutItem;

        public ViewHolder(View itemView) {
            super(itemView);

            txtHeaderTitle = (TextView) itemView.findViewById(R.id.text_header_title);


            relativeLayoutItem = (RelativeLayout) itemView.findViewById(R.id.relativeLayout_item);
            txtTitle = (TextView) itemView.findViewById(R.id.text_title);
            txtAmount = (TextView) itemView.findViewById(R.id.text_amount);
            txtDate = (TextView) itemView.findViewById(R.id.text_date);
            txtForeign = (TextView) itemView.findViewById(R.id.text_foreign);
        }
    }

//    static class ViewTitleHolder extends RecyclerView.ViewHolder {
//        public TextView txtTitle;
//
//
//        public ViewTitleHolder(View itemView) {
//            super(itemView);
//            txtTitle = (TextView) itemView.findViewById(R.id.text_title);
//        }
//    }
}
