package com.dbs.omni.tw.adapter;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.dbs.omni.tw.R;
import com.dbs.omni.tw.util.FormatUtil;
import com.dbs.omni.tw.util.http.mode.home.CreditCardData;

import java.util.List;


public class CreditCardRecyclerAdapter extends RecyclerView.Adapter<CreditCardRecyclerAdapter.ViewHolder> {
    private Context context;
    private List<CreditCardData> cardList;
//    private LayoutInflater inflater;

    private int index;

    private OnItemClickListener onItemClickListener;
    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }
    public interface OnItemClickListener {
        void onItemClick(View view, int position);
    }

    public CreditCardRecyclerAdapter(Context context) {
        this.context = context;
    }

    public CreditCardRecyclerAdapter(List<CreditCardData> list, Context context) {
        this.context = context;
        this.cardList = list;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.home_card_item, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        CreditCardData trx = cardList.get(position);
        setDataViewValue(holder,trx);
        index = position;
        holder.itemView.setTag(position);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(onItemClickListener != null) {
                    onItemClickListener.onItemClick(v, (int) v.getTag());
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return cardList.size();
    }

    // ---
    // Set View Content
    // ----
    private void setDataViewValue (ViewHolder viewHolder, CreditCardData data) {


        int ccStatus = 2;
        if(!TextUtils.isEmpty(data.getCcStatus()) && TextUtils.isDigitsOnly(data.getCcStatus())) {
            ccStatus = Integer.valueOf(data.getCcStatus());
        }

        if(ccStatus != 1) {
            viewHolder.txtNotActive.setVisibility(View.GONE);
            viewHolder.imageOverlayNotActive.setVisibility(View.GONE);

            String titleString;
            if(data.getCcFlag().equalsIgnoreCase("M")) {
                setHasMainItem(viewHolder, data, true);
            } else if(data.getCcFlag().equalsIgnoreCase("S")) {
                setHasMainItem(viewHolder, data, false);
            } else {
                viewHolder.relativeLayoutHasType.setVisibility(View.GONE);
                viewHolder.relativeLayoutHasNotType.setVisibility(View.VISIBLE);

                titleString = String.format("%1$s (%2$s)", data.getCardName(), FormatUtil.toHideCardNumberShortString(data.getCcNO()));

                viewHolder.txtTitle.setText(titleString);
                viewHolder.txtTitle.setTextColor(ContextCompat.getColor(context,R.color.colorGrayPrimaryDark));
                viewHolder.imageNextIcon.setVisibility(View.VISIBLE);
            }


        } else {
            viewHolder.txtNotActive.setVisibility(View.VISIBLE);
            viewHolder.imageOverlayNotActive.setVisibility(View.VISIBLE);

            viewHolder.relativeLayoutHasType.setVisibility(View.GONE);
            viewHolder.relativeLayoutHasNotType.setVisibility(View.VISIBLE);

            viewHolder.imageNextIcon.setVisibility(View.GONE);
            String titleString = String.format("%s", FormatUtil.toHideCardNumberShortString(data.getCcNO(), true));
            viewHolder.txtTitle.setText(titleString);
//            viewHolder.txtTitle.setText(context.getString(R.string.credit_card_not_enable_message));
//            viewHolder.txtTitle.setTextColor(ContextCompat.getColor(context,R.color.colorRedPrimary));
        }

    }

    private void setHasMainItem(ViewHolder viewHolder, CreditCardData data, boolean isMainCard ) {
        viewHolder.relativeLayoutHasType.setVisibility(View.VISIBLE);
        viewHolder.relativeLayoutHasNotType.setVisibility(View.GONE);
        if(isMainCard) {
            viewHolder.textCardType.setText(R.string.card_type_main);
        } else {
            viewHolder.textCardType.setText(R.string.card_type_attached);
        }
        viewHolder.textCardName.setText(data.getCardName());
        viewHolder.textCardHuma.setText(FormatUtil.toHideCardNumberShortString(data.getCcNO(), true));
    }

    // ----
    // Getter and setter
    // ----
    public List<CreditCardData> getTransactionList() {
        return cardList;
    }

    public void setList(List<CreditCardData> cardList) {
        this.cardList = cardList;
    }


    static class ViewHolder extends RecyclerView.ViewHolder {

        //For Data
        public RelativeLayout relativeLayoutHasType, relativeLayoutHasNotType;

        //has not type
        public TextView txtTitle, txtNotActive;

        //has  type
        public TextView textCardName, textCardHuma;
        public TextView textCardType;

        public ImageView imageIcon;
        public ImageView imageOverlayNotActive;
        public ImageView imageNextIcon;

        public ViewHolder(View itemView) {
            super(itemView);
            relativeLayoutHasType = (RelativeLayout) itemView.findViewById(R.id.relativeLayout_has_type);
            relativeLayoutHasNotType = (RelativeLayout) itemView.findViewById(R.id.relativeLayout_has_not_type);

            txtTitle = (TextView) itemView.findViewById(R.id.text_title);
            txtNotActive = (TextView) itemView.findViewById(R.id.text_not_active);

            textCardType = (TextView) itemView.findViewById(R.id.text_type);
            textCardName = (TextView) itemView.findViewById(R.id.text_card_name);
            textCardHuma = (TextView) itemView.findViewById(R.id.text_card_huma);

            imageIcon = (ImageView) itemView.findViewById(R.id.image_icon);
            imageNextIcon = (ImageView) itemView.findViewById(R.id.image_next);
            imageOverlayNotActive = (ImageView) itemView.findViewById(R.id.image_overlay_not_active);

        }
    }
}
