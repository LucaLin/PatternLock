package com.dbs.omni.tw.controller.setting.applyCreditCard;


import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.dbs.omni.tw.R;
import com.dbs.omni.tw.adapter.CreditCardPagerAdapter;
import com.dbs.omni.tw.controller.ActivityBase;
import com.dbs.omni.tw.element.CreditCardItemView;
import com.dbs.omni.tw.element.CreditCardSelectedItemView;
import com.dbs.omni.tw.model.setting.ApplyCreditCardData;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 */
public class ChooseCreditCardFragment extends Fragment {

    private LayoutInflater inflater;
    private LinearLayout linearLayoutForPagerDot, linearLayoutCardSelected;
    private TextView txtHint1, txtHint2;
    private Button btnApplyNow;
    private Button mPreSelectedBt;

//    private ArrayList<ApplyCreditCardData> mCardList = new ArrayList<>();
//    private ArrayList<CreditCardItemView> mCardViewList = new ArrayList<>();
    private ArrayList<View> mCardPagerItems;
    private int selectCount = 0;

//    private ArrayList<String> mSelectCardList = new ArrayList<>();

    private OnEventListener onEventListener;

    public interface OnEventListener {
        void OnNextEvent();
    }

    public void setOnEventListener (OnEventListener listener) {
        this.onEventListener = listener;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ((ActivityBase) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ((ActivityBase) getActivity()).setHeadHide(false);

        inflater = getActivity().getLayoutInflater();
    }

    @Override
    public void onResume() {
        super.onResume();
        selectCount = ((ApplyCreditCardActivity) getActivity()).mSelectCardList.size();
        isEnableApplyButton();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_choose_credit_card, container, false);

        btnApplyNow = (Button)view.findViewById(R.id.btnApplyNow);
        btnApplyNow.setOnClickListener(btnListener);
        btnApplyNow.setEnabled(false);

        txtHint1 = (TextView) view.findViewById(R.id.txtHint1);
        txtHint2 = (TextView) view.findViewById(R.id.txtHint2);

        linearLayoutCardSelected = (LinearLayout) view.findViewById(R.id.linearLayoutCardSelected);

//      addItemView(linearLayoutCardSelected);
        if(((ApplyCreditCardActivity) getActivity()).mCardList == null || ((ApplyCreditCardActivity) getActivity()).mCardList.size() == 0) {
            ((ApplyCreditCardActivity) getActivity()).mCardList = getCardListMock();
        }
        setVeiwPager(view);

        //如果陣列已經有值 , 就在下方scroll view add
        if(((ApplyCreditCardActivity) getActivity()).mSelectCardList.size() != 0) {
            isHiddenHint(true);

            selectCount = ((ApplyCreditCardActivity) getActivity()).mSelectCardList.size();

            for(int i=0 ; i<((ApplyCreditCardActivity) getActivity()).mSelectCardList.size() ; i++){
                if(!((ApplyCreditCardActivity) getActivity()).mSelectCardList.get(i).isEmpty()){
                    addItemView(linearLayoutCardSelected, Integer.valueOf(((ApplyCreditCardActivity) getActivity()).mSelectCardList.get(i)));
                }
            }
        }

        return view;
    }

    private void isHiddenHint(boolean isHidden) {
        if(isHidden) {
            txtHint1.setVisibility(View.GONE);
            txtHint2.setVisibility(View.GONE);
        } else {
            txtHint1.setVisibility(View.VISIBLE);
            txtHint2.setVisibility(View.VISIBLE);
        }
    }

    private Button.OnClickListener btnListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            onEventListener.OnNextEvent();
        }
    };


//region PagerView
    //設定頁面
    private void setVeiwPager(View view) {
        if(((ApplyCreditCardActivity) getActivity()).mCardViewList == null) {
            createPagers(((ApplyCreditCardActivity) getActivity()).mCardList);
        }

        if(mCardPagerItems == null)
            return;

        ViewPager viewPager = (ViewPager)view.findViewById(R.id.viewpager_applyCreditCard);

        CreditCardPagerAdapter adapter = new CreditCardPagerAdapter(getContext(), mCardPagerItems);
        viewPager.setAdapter(adapter);

        // 下方點點
        linearLayoutForPagerDot = (LinearLayout) view.findViewById(R.id.linearLayoutForPagerDot);

        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.ic_pager_dot_gray);

        //依照陣列長度增加點狀按鈕
        for (int i = 0; i < adapter.getCount(); i++) {
            Button btDot = new Button(getActivity());
            btDot.setLayoutParams(new ViewGroup.LayoutParams(bitmap.getWidth(),bitmap.getHeight()));
            btDot.setBackgroundResource(R.drawable.ic_pager_dot_gray);

            //剛進入時 , 將第一個點設為紅點
            if(i == 0) {
                btDot.setBackgroundResource(R.drawable.ic_pager_dot_red);
                mPreSelectedBt = btDot;
            }
            linearLayoutForPagerDot.addView(btDot);

            //調整間距
            setMargins(btDot , 15 , 0 , 15, 0);
        }

        //偵測翻頁來設定紅色點狀按鈕
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {}

            @Override
            public void onPageSelected(int position) {
                if(mPreSelectedBt != null){
                    mPreSelectedBt.setBackgroundResource(R.drawable.ic_pager_dot_gray);
                }

                Button currentBt = (Button)linearLayoutForPagerDot.getChildAt(position);
                currentBt.setBackgroundResource(R.drawable.ic_pager_dot_red);
                mPreSelectedBt = currentBt;

            }

            @Override
            public void onPageScrollStateChanged(int state) {}
        });
    }

    //調整點狀按鈕的間距
    private void setMargins (View view, int left, int top, int right, int bottom) {
        if (view.getLayoutParams() instanceof ViewGroup.MarginLayoutParams) {
            ViewGroup.MarginLayoutParams p = (ViewGroup.MarginLayoutParams) view.getLayoutParams();
            p.setMargins(left, top, right, bottom);
            view.requestLayout();
        }
    }

    //region add scroll view item的method
    private void addItemView(final LinearLayout rootLayout, int selcetedIndex){

        if(((ApplyCreditCardActivity) getActivity()).mCardList == null || ((ApplyCreditCardActivity) getActivity()).mCardList.size() < selcetedIndex)
            return;

        ApplyCreditCardData data = ((ApplyCreditCardActivity) getActivity()).mCardList.get(selcetedIndex);

        CreditCardSelectedItemView itemView = new CreditCardSelectedItemView(getContext(), data, selcetedIndex);
        itemView.isEnableCancelButton(true);
        itemView.setOnItemCancelListener(new CreditCardSelectedItemView.OnItemCancelListener() {
            @Override
            public void OnCancelSelect(View view, int selectIndex) {
                selectCount -= 1;

                ((ApplyCreditCardActivity) getActivity()).mSelectCardList.remove(String.valueOf(selectIndex));
                ((ApplyCreditCardActivity) getActivity()).mCardViewList.get(selectIndex).cancelSelect();
                rootLayout.removeView(view);

                if(((ApplyCreditCardActivity) getActivity()).mSelectCardList.size() == 0) {
                    isHiddenHint(false);
                }

                isEnableApplyButton();
            }
        });


        rootLayout.addView(itemView);
    }

    private ArrayList<ApplyCreditCardData> getCardListMock() {
//        ArrayList<ApplyCreditCardAdapterData> list = new ArrayList<>();

        ArrayList<ApplyCreditCardData> cardDatas1 = new ArrayList<>();
        cardDatas1.add(new ApplyCreditCardData("DBS VISA 飛行卡", onItemEventListener));
        cardDatas1.add(new ApplyCreditCardData("DBS Eminent VISA 白金卡", onItemEventListener));
        cardDatas1.add(new ApplyCreditCardData("DBS Eminent VISA Signature Card", onItemEventListener));
        cardDatas1.add(new ApplyCreditCardData("COMPASS VISA 白金卡", onItemEventListener));
        cardDatas1.add(new ApplyCreditCardData("飛行卡4", onItemEventListener));
        cardDatas1.add(new ApplyCreditCardData("飛行卡5", onItemEventListener));
        cardDatas1.add(new ApplyCreditCardData("飛行卡6", onItemEventListener));
        cardDatas1.add(new ApplyCreditCardData("飛行卡7", onItemEventListener));
        cardDatas1.add(new ApplyCreditCardData("飛行卡0", onItemEventListener));
        cardDatas1.add(new ApplyCreditCardData("飛行卡1", onItemEventListener));
        cardDatas1.add(new ApplyCreditCardData("飛行卡2", onItemEventListener));
        cardDatas1.add(new ApplyCreditCardData("飛行卡3", onItemEventListener));
        cardDatas1.add(new ApplyCreditCardData("飛行卡4", onItemEventListener));
        cardDatas1.add(new ApplyCreditCardData("飛行卡5", onItemEventListener));
//        mCardList.addAll(cardDatas1);
//        mCardList.addAll(cardDatas2);
//        mCardList.addAll(cardDatas1);
//        mCardList.addAll(cardDatas2);
//
//        list.add(new ApplyCreditCardAdapterData(cardDatas1));
//        list.add(new ApplyCreditCardAdapterData(cardDatas2));
//        list.add(new ApplyCreditCardAdapterData(cardDatas1));
//        list.add(new ApplyCreditCardAdapterData(cardDatas2));

        return cardDatas1;
    }

    private void createPagers(ArrayList<ApplyCreditCardData> list) {
        ((ApplyCreditCardActivity) getActivity()).mCardViewList = new ArrayList<>();
        mCardPagerItems = new ArrayList<>();


        int onePagerItemIndex = 0;
        ArrayList<CreditCardItemView> subViewList = null;
        View view = null;
        for(int i = 0; i < list.size() ; i++) {


            if(onePagerItemIndex == 0) {
                view = inflater.inflate(R.layout.credit_card_choose, null);

                subViewList = new ArrayList<>();
                CreditCardItemView itemView1 = (CreditCardItemView) view.findViewById(R.id.elementCreditCard1);
                CreditCardItemView itemView2 = (CreditCardItemView) view.findViewById(R.id.elementCreditCard2);
                CreditCardItemView itemView3 = (CreditCardItemView) view.findViewById(R.id.elementCreditCard3);
                CreditCardItemView itemView4 = (CreditCardItemView) view.findViewById(R.id.elementCreditCard4);
                subViewList.add(itemView1);
                subViewList.add(itemView2);
                subViewList.add(itemView3);
                subViewList.add(itemView4);
                ((ApplyCreditCardActivity) getActivity()).mCardViewList.addAll(subViewList);
            }

            if(subViewList != null) {
                subViewList.get(onePagerItemIndex).setVisibility(View.VISIBLE);
                subViewList.get(onePagerItemIndex).setCreditCardItem(list.get(i), i);
            }



            if(i == list.size() - 1) {
                if(view != null) {
                    mCardPagerItems.add(view);
                }
                break;
            }else if(onePagerItemIndex == 3) {
                if(view != null) {
                    mCardPagerItems.add(view);
                }
                onePagerItemIndex = 0;
            } else {
                onePagerItemIndex++;
            }
        }
    }

    private CreditCardItemView.OnItemEventListener onItemEventListener = new CreditCardItemView.OnItemEventListener() {
        @Override
        public void OnSelectItem(final int index) {
            //如果選擇卡超過三張 , 跳出提警告窗
            if(selectCount >= 3){
                ((ApplyCreditCardActivity) getActivity()).mCardViewList.get(index).cancelSelect();

                ((ActivityBase) getActivity()).showAlertDialog(getString(R.string.alert_select_over), android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }, true);
            }else{
                selectCount += 1;

                ((ApplyCreditCardActivity) getActivity()).mSelectCardList.add(String.valueOf(index));
                addItemView(linearLayoutCardSelected, index);

                if(((ApplyCreditCardActivity) getActivity()).mSelectCardList.size() != 0) {
                    isHiddenHint(true);
                }

                isEnableApplyButton();
            }
        }

        @Override
        public void OnShowMore(int index) {
            ((ActivityBase) getActivity()).showCardDetailAlertDialog(new ActivityBase.OnCardDetailListener() {
                @Override
                public void OnClose(AlertDialog dialog) {
                    dialog.dismiss();
                }

                @Override
                public void OnMoreDetail(AlertDialog dialog) {
                    //顯示webview
                }
            });
        }
    };

    //檢查輸入內容
    private void isEnableApplyButton() {
        if(selectCount > 0){
            btnApplyNow.setEnabled(true);
        }else{
            btnApplyNow.setEnabled(false);
        }
    }

//endregion
}


