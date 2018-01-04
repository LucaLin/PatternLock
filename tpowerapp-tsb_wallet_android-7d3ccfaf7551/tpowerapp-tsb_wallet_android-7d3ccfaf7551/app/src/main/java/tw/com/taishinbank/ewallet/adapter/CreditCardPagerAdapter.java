package tw.com.taishinbank.ewallet.adapter;


import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import tw.com.taishinbank.ewallet.R;
import tw.com.taishinbank.ewallet.model.creditcard.CreditCardData;
import tw.com.taishinbank.ewallet.util.FormatUtil;


public class CreditCardPagerAdapter extends PagerAdapter {
    private int pager_size = 3;
    private LayoutInflater layoutInflater;
    private ArrayList<CreditCardData> creditCardList;

    int index_maincard = 0;
    boolean updateCurrentItem = false;


    public interface EventCallBack
    {
        void OnClick();
        void OnChangeCardPage(int index);
    }

    public EventCallBack eventCallBack;
//
//    public boolean isMain() {
//        return isMain;
//    }
//
//    public void setIsMain(boolean isMain) {
//        this.isMain = isMain;
//    }

//    private boolean isMain = false;
    public CreditCardPagerAdapter(Context context, ArrayList<CreditCardData> list){
        layoutInflater = LayoutInflater.from(context);
        this.pager_size = list.size();
        creditCardList = new ArrayList<>();
        creditCardList.addAll(list);
    }

    public void updateAdapter(ArrayList<CreditCardData> list)
    {
        pager_size = list.size();
        creditCardList.clear();
        creditCardList.addAll(list);
        notifyDataSetChanged();

    }

    @Override
    public int getCount() {
        return pager_size;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return (view == object);
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
//        if(null != container.getChildAt(position))
//            return null;

        View view = layoutInflater.inflate(R.layout.creditcard_layout_item, null);
        TextView title = (TextView) view.findViewById(android.R.id.title);
        title.setText(FormatUtil.toCreditCardShelterFormat(creditCardList.get(position).getCardNumber()));
//        String[] cardNumber;
//        if(!creditCardList.get(position).getCardNumber().equals("")) {
//            cardNumber = creditCardList.get(position).getCardNumber().split("-");
//            title.setText("****-****-****-" + cardNumber[3]);
//        }
//        else
//            title.setText("");


        ImageView image = (ImageView) view.findViewById(android.R.id.background);
        switch (creditCardList.get(position).getCardType())
        {
            case MasterCard:
                image.setBackgroundResource(R.drawable.img_credit_card_master_card);
                break;
            case Visa:
                image.setBackgroundResource(R.drawable.img_credit_card_visa_);
                break;
            case JCB:
                image.setBackgroundResource(R.drawable.img_credit_card_jcb);
                break;
            default:
                image.setBackgroundResource(R.drawable.img_credit_card_empty);
                break;

        }

        image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                eventCallBack.OnClick();
            }
        });

        if(container.getChildCount() > position)
            container.addView(view, position);
        else
            container.addView(view);

        if(creditCardList.get(position).getSettedMain())
        {
            index_maincard =position;
            updateCurrentItem = true;
        }
        return view;

    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }

    @Override
    public void finishUpdate(ViewGroup container) {
        if(updateCurrentItem) {
            eventCallBack.OnChangeCardPage(index_maincard);
            updateCurrentItem = false;
        }
    }
}

