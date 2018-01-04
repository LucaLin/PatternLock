package com.dbs.omni.tw.controller.home;

import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.dbs.omni.tw.R;
import com.dbs.omni.tw.adapter.CreditCardRecyclerAdapter;
import com.dbs.omni.tw.controller.ActivityBase;
import com.dbs.omni.tw.element.FeedbackView;
import com.dbs.omni.tw.element.NoteBar;
import com.dbs.omni.tw.element.TopCropImageView;
import com.dbs.omni.tw.model.weather.WeatherInfo;
import com.dbs.omni.tw.setted.OmniApplication;
import com.dbs.omni.tw.util.FormatUtil;
import com.dbs.omni.tw.util.NetworkUtil;
import com.dbs.omni.tw.util.PreferenceUtil;
import com.dbs.omni.tw.util.http.BillHttpUtil;
import com.dbs.omni.tw.util.http.HomeHttpUtil;
import com.dbs.omni.tw.util.http.WeatherHttpUtil;
import com.dbs.omni.tw.util.http.listener.ResponseListener;
import com.dbs.omni.tw.util.http.mode.bill.BillOverview;
import com.dbs.omni.tw.util.http.mode.home.CreditCardData;
import com.dbs.omni.tw.util.http.mode.bill.UnBillOverview;
import com.dbs.omni.tw.util.http.mode.register.ResponseResult;
import com.dbs.omni.tw.util.http.responsebody.BillResponseBodyUtil;
import com.dbs.omni.tw.util.http.responsebody.HomeResponseBodyUtil;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;


public class HomeFragment extends Fragment{

    public static final String TAG = "HomeFragment";
//    private OnFragmentInteractionListener mListener;

    private RecyclerView cardListView;
    private CreditCardRecyclerAdapter creditCardAdapter;


    private ArrayList<CreditCardData> creditCardList;

    private WeatherHttpUtil mWeatherTask;
    private View mView;
    private RelativeLayout mNoteBars;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ((ActivityBase) getActivity()).setHeadHide(true);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
//        // Inflate the layout for this fragment
        mView = inflater.inflate(R.layout.fragment_home, container, false);


        mView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            public void onGlobalLayout() {
                mView.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                LinearLayout textstate = (LinearLayout) mView.findViewById(R.id.linearLayout_subHeader);
                CollapsingToolbarLayout collapsingToolBar = (CollapsingToolbarLayout) mView.findViewById(R.id.collapsingToolBar);

                int minHeight = textstate.getHeight();
                collapsingToolBar.setMinimumHeight(minHeight);

            }
        });

        mNoteBars = (RelativeLayout) mView.findViewById(R.id.note_bar);

        TopCropImageView topCropImageView = (TopCropImageView) mView.findViewById(R.id.topCropImageView);
//        topCropImageView.setImageResource(R.drawable.bg_login_2);

        TextView textHeader = (TextView) mView.findViewById(R.id.text_header);
        textHeader.setText(String.format(getString(R.string.home_nickname_format), PreferenceUtil.getNickname(getActivity())));


//        setFeedbackView(mView);

//        setCardItemsData(getMockData());
//        setNoteView();
        return mView;
    }

    @Override
    public void onResume() {
        super.onResume();
        setWeather();
        toGetCreditCarInfoList();
        toGetUnBillOverview();
        toGetBillOverview();
    }

    @Override
    public void onPause() {
        super.onPause();

        if(getActivity() != null)
            ((ActivityBase) getActivity()).dismissProgressLoading();
    }

    @Override
    public void onStop() {
        super.onStop();

        OmniApplication.onLocationListerent = null;

        if(mWeatherTask != null)
            mWeatherTask.cancel(true);
    }

////region set View function
    private void setUnBillView(UnBillOverview unBillOverview) {
        TextView textUnBillAmount = (TextView) mView.findViewById(R.id.text_un_bill_amount);
        textUnBillAmount.setText(FormatUtil.toDecimalFormat(getContext(), unBillOverview.getUnbillAmt(), true));
    }

    private void setBillView(BillOverview billOverview) {
        TextView textBillAmount = (TextView) mView.findViewById(R.id.text_current_bill_amount);
        textBillAmount.setText(FormatUtil.toDecimalFormat(getContext(), billOverview.getAmtCurrDue(), true));
        setFeedbackView(billOverview.getrPoints(), billOverview.getCrPoints(), billOverview.getmPoints());

        setNoteView(billOverview.getAmtCurrDue(), billOverview.getAmtCurrPayment(), billOverview.getPaymentDueDate());
//        toGetADLink(); // top
    }

    private void setFeedbackView(String dividendPoints, String cashPoints, String filghtMiles ) {
        FeedbackView viewFeedback = (FeedbackView) mView.findViewById(R.id.view_feedback_info);
        viewFeedback.setDividendPoints(Integer.valueOf(dividendPoints));
        viewFeedback.setCashPoints(Integer.valueOf(cashPoints));
        viewFeedback.setFlightMiles(Integer.valueOf(filghtMiles));
    }


    private void setCardList(List<CreditCardData> creditList) {
//        if(GlobalConst.UseLocalMock) {
//            creditCardAdapter = new CreditCardRecyclerAdapter(getMockData(), getActivity());
//        } else {
            creditCardAdapter = new CreditCardRecyclerAdapter(creditList, getActivity());
//        }

        creditCardAdapter.setOnItemClickListener(new CreditCardRecyclerAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                onCardListItmeClick(position);
            }
        });
//        mAdapter = new MyAdapter(myDataset);
//        mRecyclerView = (RecyclerView) findViewById(R.id.list_view);
        final LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);

        cardListView = (RecyclerView) mView.findViewById(R.id.recyclerView);
        cardListView.setLayoutManager(layoutManager);
        cardListView.setAdapter(creditCardAdapter);

//        cardListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                onCardListItmeClick(position);
//            }
//        });
    }

    private void setWeather() {
        //取得天氣資訊
        mWeatherTask = new WeatherHttpUtil();
        mWeatherTask.setOnWeatherListener(new WeatherHttpUtil.OnWeatherListener() {
            @Override
            public void OnFinish(WeatherInfo weatherInfo) {
                if(OmniApplication.sWallpaperInfo != null) {
                    setWeatherView(mView, OmniApplication.sWallpaperInfo);
                    OmniApplication.removeGPS();
                }
            }
        });
        Location location = OmniApplication.getsCurrentLocation();
        if( location != null) {
            mWeatherTask.execute();
        }
    }

    private void setWeatherView(View view, WeatherInfo weatherInfo) {
        ImageView imageWeatherStatus = (ImageView) view.findViewById(R.id.image_weather_status);
        TextView textWeatherTemperature = (TextView) view.findViewById(R.id.text_weather_temperature);
//        textWeatherTemperature.setTextContent(weatherInfo.getMain().getTemp());
        int imageID = weatherInfo.getImageID(getActivity());
        float temperature = weatherInfo.getMain().getTemp();
        imageWeatherStatus.setBackgroundResource(imageID);
        textWeatherTemperature.setText(FormatUtil.toTemperatureString(temperature));
        setStatusView(imageID, temperature);
    }

    //問候語
    private void setStatusView(int weatherImageID, float temperature) {
        boolean isFirstLogin = true;
        TextView textStatus = (TextView) mView.findViewById(R.id.text_state);

        Date lastLoginTime = FormatUtil.TimeFormattedToDate(PreferenceUtil.getLastLoginTime(getContext()));
        Date loginTime = FormatUtil.TimeFormattedToDate(PreferenceUtil.getLoginTime(getContext()));

        Calendar calendaLastLogin = null;
        Calendar calendaLogin = null;
        if(lastLoginTime != null && loginTime != null) {
            calendaLastLogin = Calendar.getInstance();
            calendaLogin = Calendar.getInstance();

            calendaLastLogin.setTime(lastLoginTime);
            calendaLastLogin.add(Calendar.MONTH, 3);

            calendaLogin.setTime(loginTime);
        }

        if(!isFirstLogin && calendaLastLogin.after(calendaLogin)) {
            //超過三個月沒使用
            textStatus.setText(R.string.greetings_for_over_three_month);
        } else {

            String imageName = getContext().getResources().getResourceName(weatherImageID);
            imageName = imageName.substring(imageName.indexOf("/"));
            String[] imageNameArray = imageName.split("_");
            String status = imageNameArray[1];

            if(status.equalsIgnoreCase("mist")) {
                textStatus.setText(R.string.greetings_for_fog);
            } else if(status.equalsIgnoreCase("cloudy")) {
                if(temperature >= 27) {
                    textStatus.setText(R.string.greetings_for_overcast_up);
                } else {
                    textStatus.setText(R.string.greetings_for_overcast_down);
                }
            } else if(status.equalsIgnoreCase("rainy")) {
                textStatus.setText(R.string.greetings_for_rain);
            } else if(status.equalsIgnoreCase("thunderstorm")) {
                textStatus.setText(R.string.greetings_for_storm);
            } else if(status.equalsIgnoreCase("snow")) {
                textStatus.setText(R.string.greetings_for_snow);
            } else {
                String timePeriod = imageNameArray[2];
                if(timePeriod.equalsIgnoreCase("day")) {
                    if(temperature >= 25) {
                        textStatus.setText(R.string.greetings_for_sunny_25_up);
                    } else {
                        textStatus.setText(R.string.greetings_for_sunny_25_down);
                    }
                } else {
                    textStatus.setText(R.string.greetings_for_sunny_night);
                }
            }
        }

    }

    private void setADNoteView(String url) {

        if(TextUtils.isEmpty(url))
            return;

        NoteBar noteBar_ad = new NoteBar(getContext(), "每月刷卡消費金額，可持續累積現金回饋折抵次月帳款。現在申辦享受5%手續費減免。", url,new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                removeNoteBarView(NoteBar.ENUM_NOTE_TYPE.ADVERTISEMENT);
            }
        }, NoteBar.ENUM_NOTE_TYPE.ADVERTISEMENT);


        mNoteBars.addView(noteBar_ad);
        mNoteBars.setVisibility(View.VISIBLE);
    }

    private void setNoteView(String currentAmount, String paymentAmount, String paymentDeadline) {

        if(TextUtils.isEmpty(currentAmount) || TextUtils.isEmpty(paymentDeadline) || OmniApplication.sBillOverview == null)
            return;

        Calendar calendarToday = Calendar.getInstance();
        Calendar calendarDueDate = FormatUtil.TimeFormattedToCalendar(OmniApplication.sBillOverview.getPaymentDueDate()); //繳款截止日
        Calendar calendarStmtCycleDate = FormatUtil.TimeFormattedToCalendar(OmniApplication.sBillOverview.getStmtCycleDate()); //帳單結帳日
        if(calendarDueDate != null) {
            calendarDueDate.add(Calendar.DATE, 1);
        }

        if(calendarStmtCycleDate != null) {
            calendarStmtCycleDate.add(Calendar.MONDAY, 1);
        }

        String noteString;
//        if(TextUtils.isEmpty(paymentAmount) || paymentAmount.equals("0")) {
//            noteString = String.format(getString(R.string.home_note_message_not_pay), FormatUtil.toDecimalFormat(getContext(), currentAmount, true), FormatUtil.toDateCNFormatted(paymentDeadline));
//        } else {
        if(calendarToday.after(calendarDueDate) && calendarToday.before(calendarStmtCycleDate)) {
            if(!TextUtils.isEmpty(OmniApplication.sBillOverview.getAmtMinPayment()) && !TextUtils.isEmpty(OmniApplication.sBillOverview.getAmtCurrPayment())) {
                Double minPayment = Double.valueOf(OmniApplication.sBillOverview.getAmtMinPayment());
                Double currPayment = Double.valueOf(OmniApplication.sBillOverview.getAmtCurrPayment());
                if(minPayment > currPayment ) {
                    noteString = String.format(getString(R.string.home_note_message_timeout), FormatUtil.toDecimalFormat(getContext(), currentAmount, true), FormatUtil.toDecimalFormat(getContext(), paymentAmount, true));
                } else {
                    noteString = String.format(getString(R.string.home_note_message_has_pay), FormatUtil.toDecimalFormat(getContext(), currentAmount, true), FormatUtil.toDecimalFormat(getContext(), paymentAmount, true), FormatUtil.toDateCNFormatted(paymentDeadline));
                }
            } else {
                noteString = String.format(getString(R.string.home_note_message_has_pay), FormatUtil.toDecimalFormat(getContext(), currentAmount, true), FormatUtil.toDecimalFormat(getContext(), paymentAmount, true), FormatUtil.toDateCNFormatted(paymentDeadline));
            }
        } else {
            noteString = String.format(getString(R.string.home_note_message_has_pay), FormatUtil.toDecimalFormat(getContext(), currentAmount, true), FormatUtil.toDecimalFormat(getContext(), paymentAmount, true), FormatUtil.toDateCNFormatted(paymentDeadline));
        }

//        }

        NoteBar noteBar_notice = new NoteBar(getContext(), noteString, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                removeNoteBarView(NoteBar.ENUM_NOTE_TYPE.NOTICE);
            }
        }, NoteBar.ENUM_NOTE_TYPE.NOTICE);



        mNoteBars.addView(noteBar_notice);
        mNoteBars.setVisibility(View.VISIBLE);
    }

    private void removeNoteBarView(Object tag) {
        View view = mNoteBars.findViewWithTag(tag);

        mNoteBars.removeView(view);
        if(mNoteBars.getChildCount() == 0) {
            mNoteBars.setVisibility(View.GONE);
        }
    }
//endregion

//region Action
    private void onCardListItmeClick(int position) {
        CreditCardData data = creditCardList.get(position);

        int ccStatus = 2;
        if(!TextUtils.isEmpty(data.getCcStatus())) {
            ccStatus = Integer.valueOf(data.getCcStatus());
        }

        if(ccStatus == 1) {
            //goto 開卡
            if(data.getCcFlag().equalsIgnoreCase("M")) {
                goToCardActive(data);
            }
        } else {
            goToCardDetailPage(data);

        }
    }

    private void goToCardDetailPage(CreditCardData data) {
        Intent intent = new Intent(getActivity(), CreditCardunBilledListActivity.class);
        intent.putExtra(CreditCardunBilledListActivity.EXTRA_CARD_DATA, data);
        startActivity(intent);
    }

    private void goToCardActive(CreditCardData data) {
        Intent intent = new Intent(getActivity(), CreditCardActiveActivity.class);
        intent.putExtra(CreditCardunBilledListActivity.EXTRA_CARD_DATA, data);
        startActivity(intent);
    }
//endregion

//region Mock
//    private ArrayList<CreditCardData> getMockData() {
//        creditCardList = new ArrayList<>();
//
//        creditCardList.add(new CreditCardData("肥行卡", "1111-1111-1111-1111","O", true));
//        creditCardList.add(new CreditCardData("肥行卡", "1111-1111-1111-2222","O", false));
//        creditCardList.add(new CreditCardData("肥行卡", "1111-1111-1111-3333","M", true));
//        creditCardList.add(new CreditCardData("肥行卡", "1111-1111-1111-4444","S", true));
//        creditCardList.add(new CreditCardData("肥行卡", "1111-1111-1111-5555","O", false));
//        creditCardList.add(new CreditCardData("肥行卡", "1111-1111-1111-6666","O", true));
//        creditCardList.add(new CreditCardData("肥行卡", "1111-1111-1111-7777","O", true));
//        creditCardList.add(new CreditCardData("肥行卡", "1111-1111-1111-8888","O", true));
//        creditCardList.add(new CreditCardData("肥行卡", "1111-1111-1111-1111","O", true));
//        creditCardList.add(new CreditCardData("肥行卡", "1111-1111-1111-2222","O", false));
//        creditCardList.add(new CreditCardData("肥行卡", "1111-1111-1111-3333","M", true));
//        creditCardList.add(new CreditCardData("肥行卡", "1111-1111-1111-4444","S", true));
//        creditCardList.add(new CreditCardData("肥行卡", "1111-1111-1111-5555","O", false));
//        creditCardList.add(new CreditCardData("肥行卡", "1111-1111-1111-6666","O", true));
//        creditCardList.add(new CreditCardData("肥行卡", "1111-1111-1111-7777","O", true));
////        creditCardList.add(new CreditCardData("肥行卡", "1111-1111-1111-8888","O", true));
//
//        return creditCardList;
//    }
//endregion


//region api
    private void toGetCreditCarInfoList() {
        // 如果沒有網路連線，顯示提示對話框
        if (!NetworkUtil.isConnected(getContext())) {
            ((ActivityBase)getActivity()).showAlertDialog(getString(R.string.msg_no_available_network), android.R.string.ok, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            }, true);
        } else {
            try {
                HomeHttpUtil.getCreditCarInfoList(responseListener, getActivity());
                ((ActivityBase)getActivity()).showProgressLoading();
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
    }

    private ResponseListener responseListener = new ResponseListener() {

        @Override
        public void onResponse(ResponseResult result) {
            if(getActivity() == null)
                return;
            ((ActivityBase)getActivity()).dismissProgressLoading();

            String returnCode = result.getReturnCode();
            // 如果returnCode是成功
            if (returnCode.equals(ResponseResult.RESULT_SUCCESS)) {
                creditCardList = HomeResponseBodyUtil.getCreditCardList(result.getBody());
                setCardList(creditCardList);
            } else {
                handleResponseError(result, ((ActivityBase)getActivity()));
            }

        }
    };

    private void toGetBillOverview() {
        // 如果沒有網路連線，顯示提示對話框
        if (!NetworkUtil.isConnected(getContext())) {
            ((ActivityBase)getActivity()).showAlertDialog(getString(R.string.msg_no_available_network), android.R.string.ok, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            }, true);
        } else {
            try {
                BillHttpUtil.getBillOverview(responseListener_getBillOverview, getActivity());
                ((ActivityBase)getActivity()).showProgressLoading();
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
    }

    private ResponseListener responseListener_getBillOverview = new ResponseListener() {

        @Override
        public void onResponse(ResponseResult result) {
            if(getActivity() == null)
                return;
            ((ActivityBase)getActivity()).dismissProgressLoading();

            String returnCode = result.getReturnCode();
            // 如果returnCode是成功
            if (returnCode.equals(ResponseResult.RESULT_SUCCESS)) {
                OmniApplication.sBillOverview = BillResponseBodyUtil.getBilledOverview(result.getBody());
                setBillView(OmniApplication.sBillOverview);
            } else {
                handleResponseError(result, ((ActivityBase)getActivity()));
            }

        }
    };

    private void toGetUnBillOverview() {
        // 如果沒有網路連線，顯示提示對話框
        if (!NetworkUtil.isConnected(getContext())) {
            ((ActivityBase)getActivity()).showAlertDialog(getString(R.string.msg_no_available_network), android.R.string.ok, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            }, true);
        } else {
            try {
                BillHttpUtil.getUnBilledOverview(responseListener_getUnBillOverview, getActivity());
                ((ActivityBase)getActivity()).showProgressLoading();
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
    }

    private ResponseListener responseListener_getUnBillOverview = new ResponseListener() {

        @Override
        public void onResponse(ResponseResult result) {
            if(getActivity() == null)
                return;
            ((ActivityBase)getActivity()).dismissProgressLoading();

            String returnCode = result.getReturnCode();
            // 如果returnCode是成功
            if (returnCode.equals(ResponseResult.RESULT_SUCCESS)) {
                UnBillOverview unBilledOvervie = BillResponseBodyUtil.getUnBilledOverview(result.getBody());
                setUnBillView(unBilledOvervie);
            } else {
                handleResponseError(result, ((ActivityBase)getActivity()));
            }

        }
    };


//    private void toGetADLink() {
//        // 如果沒有網路連線，顯示提示對話框
//        if (!NetworkUtil.isConnected(getContext())) {
//            ((ActivityBase)getActivity()).showAlertDialog(getString(R.string.msg_no_available_network), android.R.string.ok, new DialogInterface.OnClickListener() {
//                @Override
//                public void onClick(DialogInterface dialog, int which) {
//                    dialog.dismiss();
//                }
//            }, true);
//        } else {
//            try {
//                HomeHttpUtil.getADLink(responseListener_getADLink, getActivity(), TAG);
//                ((ActivityBase)getActivity()).showProgressLoading();
//            } catch (JSONException e) {
//                e.printStackTrace();
//            }
//
//        }
//    }
//
//    private ResponseListener responseListener_getADLink = new ResponseListener() {
//
//        @Override
//        public void onResponse(ResponseResult result) {
//
//            ((ActivityBase)getActivity()).dismissProgressLoading();
//
//            String returnCode = result.getReturnCode();
//            // 如果returnCode是成功
//            if (returnCode.equals(ResponseResult.RESULT_SUCCESS)) {
//                String adLink = HomeResponseBodyUtil.getADLinkURL(result.getBody());
//                setADNoteView(adLink);
//            } else {
//                handleResponseError(result, ((ActivityBase)getActivity()));
//            }
//
//        }
//    };
//endregion

}
