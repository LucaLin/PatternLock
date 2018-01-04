package tw.com.taishinbank.ewallet.util;

import android.content.Context;

import com.e7life.e7paysdk.e7pay.model.CreditCardResponseDTO;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import tw.com.taishinbank.ewallet.controller.WalletApplication;
import tw.com.taishinbank.ewallet.dbhelper.CreditCardDBHelper;
import tw.com.taishinbank.ewallet.interfaces.CreditCardEntry;
import tw.com.taishinbank.ewallet.model.creditcard.CreditCardData;

public class CreditCardUtil {

    private static CreditCardDBHelper cardDBHelper;
    private static WalletApplication globalVariableUtil;
    public static SimpleDateFormat QueryDateFormat = new SimpleDateFormat("yyyyMMdd");

    public static Context getContext() {
        return context;
    }

    public static void setContext(Context context) {
        CreditCardUtil.context = context;
    }

    private static Context context;

    public static void InitGlobalGetCreditCardList(Context context)
    {
        if(globalVariableUtil == null)
            globalVariableUtil = (WalletApplication)context.getApplicationContext();

        if(globalVariableUtil.CreditCardList == null)
        {
            if(cardDBHelper == null)
                cardDBHelper = new CreditCardDBHelper(context);

            globalVariableUtil.CreditCardList = new ArrayList<>();
            globalVariableUtil.CreditCardList.addAll(cardDBHelper.getAll());
        }
    }

    public static void ClearGlobalGetCreditCardList(Context context)
    {
        if(globalVariableUtil == null)
            globalVariableUtil = (WalletApplication)context.getApplicationContext();

        if(globalVariableUtil.CreditCardList != null) {
            globalVariableUtil.CreditCardList.clear();
            globalVariableUtil.CreditCardList = null;
        }
    }

    public static ArrayList<CreditCardData> GetCreditCardList(Context context)
    {

        InitGlobalGetCreditCardList(context);

        return globalVariableUtil.CreditCardList;
    }

    public static void UpdateGlobalCreditCardList(Context context, CreditCardDBHelper cardDBHelper)
    {
        InitGlobalGetCreditCardList(context);
        //cardDBHelper = new CreditCardDBHelper(context);
        globalVariableUtil.CreditCardList.clear();
        globalVariableUtil.CreditCardList = cardDBHelper.getAll();
    }

    //Login時，向17 sever取得已綁定信用卡，初時DB 信用卡資料
    public static void Load17ServerCreditCardListToDB(Context context) {
        setContext(context);
        E7PayUtil e7PayUtil = new E7PayUtil(context);
        e7PayUtil.setGetCardListListener(onGetCardListListener);
        e7PayUtil.GetCreditCardList(PreferenceUtil.getMemNO(context));
    }

    public static void DB_Updata(Context context, CreditCardData cardData)
    {
        if(cardDBHelper == null)
            cardDBHelper = new CreditCardDBHelper(context);

        cardDBHelper.update(cardData, CreditCardEntry._ID + " = " + cardData.getCardID(), null);
        UpdateGlobalCreditCardList(context, cardDBHelper);
    }

    public static void DB_insert(Context context, CreditCardData cardData)
    {
        if(cardDBHelper == null)
            cardDBHelper = new CreditCardDBHelper(context);

        cardDBHelper.insert(cardData);
        UpdateGlobalCreditCardList(context, cardDBHelper);
    }


    public static int DB_delete(Context context, CreditCardData cardData)
    {
        if(cardDBHelper == null)
            cardDBHelper = new CreditCardDBHelper(context);



        int deleteNumber = cardDBHelper.delete(cardData);
        UpdateGlobalCreditCardList(context, cardDBHelper);

        if( CreditCardUtil.GetCreditCardList(context).size() > 0 && cardData.getSettedMain() )
        {
            CreditCardData setting_mainCard = CreditCardUtil.GetCreditCardList(context).get(0);
            setting_mainCard.setSettedMain(true);
            CreditCardUtil.DB_Updata(context, setting_mainCard);
        }

        return deleteNumber;
    }

    //今天
    public static String getToday(Calendar calendar) {
        return QueryDateFormat.format(calendar.getTime());
    }

    //每個月的第一天日期
    public static String getFirstMonthDay(Calendar calendar) {
        calendar.set(Calendar.DATE, calendar.getActualMinimum(Calendar.DATE));
        return QueryDateFormat.format(calendar.getTime());
    }


    //每個月的最後一天日期
    public static String getLastMonthDay(Calendar calendar) {
        calendar.set(Calendar.DATE, calendar.getActualMaximum(Calendar.DATE));
        return QueryDateFormat.format(calendar.getTime());
    }

    static E7PayUtil.OnGetCardListListener onGetCardListListener = new E7PayUtil.OnGetCardListListener() {
        @Override
        public void SendCardList(List<CreditCardResponseDTO> list) {
            int count = list.size();

            if(cardDBHelper == null)
                cardDBHelper = new CreditCardDBHelper(context);

            cardDBHelper.deleteAll();
            //int cardID, String cardName, String cardNumber, String cardExpireDate, ENUM_CARD_TYPE cardType, String cardBank, String cardKey, String token, String tokenExpire, Boolean settedMain) {
            for(int i = count - 1 ; i >= 0 ; i-- ) {
                int cardID = 1;
                CreditCardData.ENUM_CARD_TYPE cardType = CreditCardData.ENUM_CARD_TYPE.valueOf(list.get(i).getCardType());

                String creditCardNumer = FormatUtil.toCreditCardFormat(list.get(i).getCardNumberShelter());
                String expiredDate = list.get(i).getExpiredDate();
                expiredDate = String.format("%s/%s", expiredDate.substring(0,2), expiredDate.substring(2,4));

                CreditCardData cardData = new CreditCardData(cardID, list.get(i).getCardName(), creditCardNumer, expiredDate, cardType, "台新銀行", list.get(i).getCardKey(), list.get(i).getCardToken(), "", false );
                if(i == count - 1 ) {
                    cardData.setSettedMain(true);
                } else {
                    cardData.setSettedMain(false);
                }
                CreditCardUtil.DB_insert(getContext(), cardData);
                cardID++;
            }
            UpdateGlobalCreditCardList(getContext(), cardDBHelper);
        }

        @Override
        public void Error(int errorCode, String message) {

        }
    };
}
