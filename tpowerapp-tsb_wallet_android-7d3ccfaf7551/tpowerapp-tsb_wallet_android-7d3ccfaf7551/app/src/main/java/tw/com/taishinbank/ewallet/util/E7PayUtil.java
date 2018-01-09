package tw.com.taishinbank.ewallet.util;


import android.content.Context;

import com.e7life.e7paysdk.e7pay.model.CreditCardAuthDTO;
import com.e7life.e7paysdk.e7pay.model.CreditCardResponseDTO;
import com.e7life.e7paysdk.e7pay.model.E7PayManager;
import com.e7life.e7paysdk.e7pay.model.ICancelAllCreditCardAuthListener;
import com.e7life.e7paysdk.e7pay.model.ICancelCreditCardAuthListener;
import com.e7life.e7paysdk.e7pay.model.IE7PayModel;
import com.e7life.e7paysdk.e7pay.model.IGetCreditCardAuth2Listener;
import com.e7life.e7paysdk.e7pay.model.IGetCreditCardAuthListener;
import com.e7life.e7paysdk.e7pay.model.IGetCreditCardListListener;
import com.e7life.e7paysdk.e7pay.model.IGetCreditCardResponseListener;
import com.e7life.e7paysdk.e7pay.model.IGetPaymentBarcodeListener;
import com.e7life.e7paysdk.e7pay.model.IGetWalletPymentResponseListener;
import com.e7life.e7paysdk.e7pay.model.PaymentBarcodeDTO;
import com.e7life.e7paysdk.e7pay.model.WalletPymentResponseDTO;

import java.io.UnsupportedEncodingException;
import java.util.List;

import tw.com.taishinbank.ewallet.util.http.HttpUtilBase;

/**
 * Created by Siang on 1/18/16.
 */
public class E7PayUtil {

    public interface OnCardAuthsListener
    {
        void CardAuthRedirect(String AuthKey, String RedirectUrl);
        void CardAuth2Redirect(String AuthKey, String RedirectUrl);
        void CreditCardResponse(String Cardkey, String CardToken, String CardNumberShelter, String CardName, int CardType, String ExpireDate);
        void Error(int ErrorCode, String Message);
    }

    public interface OnCancelCardListener
    {
        void CancelCardAuth(String CardToken);
        void CancelAllCardAuth();
        void Error(int errorCode, String message);
    }

    public interface OnGetCardListListener
    {
        void SendCardList(List<CreditCardResponseDTO> list);
        void Error(int errorCode, String message);
    }

    public interface OnGetPaymentActionListener
    {
        void SendPaymentBarcode(String Barcode, String CardToken);
        void SendWalletPaymentResponse(String storeName, String merchantTradeDate, int tradeAmount, String cardNumberShelter, String cardName, int tradeStatus, String tradeStatusName);
        void GetBarcodeError(int errorCode, String message);
        void UpdateCardToken(String cardToken);
        void GetWalletPymentResponseFailed();
    }


    public static class E7Pay_CardDataMode
    {
        private String MemberID;
        private String PhoneNumber;
        private String CardNumber;
        private String ExpireDate;
        private String Cvv2;
        private String CardName;
        private String DeviceID;

        private String AuthKey;

        public E7Pay_CardDataMode(String memberID, String phoneNumber,
                                  String cardNumber, String expireDate, String cvv2, String cardName, String deviceID) {
            setMemberID(memberID);
            PhoneNumber = phoneNumber;
            CardNumber = cardNumber;
            ExpireDate = expireDate;
            Cvv2 = cvv2;
            CardName = cardName;
            DeviceID = deviceID;
        }


        public String getMemberID() {
            return MemberID;
        }

        public void setMemberID(String memberID) {

            try {
                MemberID = sharedMethods.AESEncrypt(memberID);
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }

        public String getAuthKey() {
            return AuthKey;
        }

        public void setAuthKey(String authKey) {
            AuthKey = authKey;
        }

        public String getPhoneNumber() {
            return PhoneNumber;
        }

        public void setPhoneNumber(String phoneNumber) {
            PhoneNumber = phoneNumber;
        }

        public String getCardNumber() {
            return CardNumber;
        }

        public void setCardNumber(String cardNumber) {
            CardNumber = cardNumber;
        }

        public String getExpireDate() {
            return ExpireDate;
        }

        public void setExpireDate(String expireDate) {
            ExpireDate = expireDate;
        }

        public String getCvv2() {
            return Cvv2;
        }

        public void setCvv2(String cvv2) {
            Cvv2 = cvv2;
        }

        public String getCardName() {
            return CardName;
        }

        public void setCardName(String cardName) {
            CardName = cardName;
        }

        public String getDeviceID() {
            return DeviceID;
        }

        public void setDeviceID(String deviceID) {
            DeviceID = deviceID;
        }
    }

    private String testMemberID = "123456";
    private String serverIP = HttpUtilBase.E7_SERVER_IP;
    private String appID = "AND2016010420"; // 給E7pay sdk識別使用它的app
    private String PlatformID = "2";
    private IE7PayModel e7PayModel = null; //E7PayModel
    private boolean debugMode = true;
    private OnCardAuthsListener onCardAuthsListener;
    private OnCancelCardListener onCancelCardListener;
    private OnGetCardListListener onCardListListener;
    private OnGetPaymentActionListener onPaymentActionListener;

    private Context context;

    public E7PayUtil(Context context){
        E7PayManager.setAppID(appID);        //設定APP ID
        E7PayManager.enableDebug(debugMode); // 設定模式
        E7PayManager.setAuthority(serverIP); // 設定server IP
        E7PayManager.setIsHttps(HttpUtilBase.E7_SERVER_IS_HTTPS);     // 設定 true: https / false: http
        if(e7PayModel == null)
            E7PayModelInstance();

        this.context = context;

    }

    private void E7PayModelInstance()
    {
        e7PayModel = E7PayManager.getInstance();
    }

    public void setOnCardAuthsListener(OnCardAuthsListener listeren)
    {
        onCardAuthsListener = listeren;
    }

    public void setOnCancelCardListener(OnCancelCardListener listeren)
    {
        onCancelCardListener = listeren;
    }

    public void setGetCardListListener(OnGetCardListListener listeren)
    {
        onCardListListener = listeren;
    }

    public void setGetPaymentActionListener(OnGetPaymentActionListener listeren)
    {
        onPaymentActionListener = listeren;
    }

    public void GetCreditCardAuth(E7Pay_CardDataMode cardDataMode, int checkType) {
//        if(e7PayModel == null)
//            E7PayModelInstance();
//
        e7PayModel.registerGetCreditCardAuthListener(getCardAuthListener);
        e7PayModel.startGetCreditCardAuth(PlatformID, cardDataMode.getMemberID(), cardDataMode.getPhoneNumber(),
                cardDataMode.getCardNumber(), cardDataMode.getExpireDate(), cardDataMode.getCvv2(), checkType,
                cardDataMode.getCardName(), cardDataMode.getDeviceID());


//        e7PayModel.unregisterGetCreditCardAuthListener();
    }

    public void GetCreditCardAuth2(E7Pay_CardDataMode cardDataMode)
    {
        e7PayModel.registerGetCreditCardAuth2Listener(getCardAuth2Listener);
        e7PayModel.startGetCreditCardAuth2(PlatformID, cardDataMode.getMemberID(), cardDataMode.getPhoneNumber(),
                cardDataMode.getCardNumber(), cardDataMode.getExpireDate(), cardDataMode.getCvv2(), cardDataMode.getAuthKey());
    }

    public void GetCreditCardResponse(E7Pay_CardDataMode cardDataMode,  String retCode)
    {
        e7PayModel.registerGetCreditCardResponseListener(getCardResponseListener);
        e7PayModel.startGetCreditCardResponse(cardDataMode.getMemberID(), cardDataMode.getAuthKey(), cardDataMode.getDeviceID(), retCode);
    }

    public void CancelCreditCardAuth(String cardToken)
    {
        e7PayModel.registerCancelCreditCardAuthListener(cancelCardAuthListener);
        e7PayModel.startCancelCreditCardAuth(cardToken);

    }

    public void CancelAllCreditCardAuth(String memberID)
    {
        e7PayModel.registerCancelAllCreditCardAuthListener(cancelAllCardAuthListener);
        try {
            e7PayModel.startCancelAllCreditCardAuth(sharedMethods.AESEncrypt(memberID), PreferenceUtil.getDeviceID(context));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

    }


    public void GetCreditCardList(String memberID)
    {
        e7PayModel.registerGetCreditCardListListener(getCardListListener);
        try {
            e7PayModel.startGetCreditCardList(PlatformID, sharedMethods.AESEncrypt(memberID), PreferenceUtil.getDeviceID(context));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    public void GetPaymentBarcode(String cardToken, String memberID)
    {
        e7PayModel.registerGetPaymentBarcodeListener(getPaymentBarcodeListener);
        try {
            String aesMemID = sharedMethods.AESEncrypt(memberID);
            e7PayModel.startGetPaymentBarcode(cardToken, aesMemID);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    public void GetWalletPymentResponse(String cardToken, String barcode)
    {
        e7PayModel.registerGetWalletPymentResponseListener(getWalletPymentResponseListener);
        e7PayModel.startGetWalletPymentResponse(cardToken, barcode);
    }

    /***   Listener     ***/
    /**
     * 綁定信用卡 第一個驗證
     * GetCreditCardAuth Listener
     */
    private IGetCreditCardAuthListener getCardAuthListener = new IGetCreditCardAuthListener() {
        @Override
        public void onGetCreditCardAuthSuccess(CreditCardAuthDTO creditCardAuthDTO) {
            onCardAuthsListener.CardAuthRedirect(creditCardAuthDTO.getAuthKey(), creditCardAuthDTO.getRedirectUrl());
            e7PayModel.unregisterGetCreditCardAuthListener();
        }

        @Override
        public void onGetCreditCardAuthFailed(int i, String s) {
            onCardAuthsListener.Error(i,s);
            e7PayModel.unregisterGetCreditCardAuthListener();
        }
    };

    /**
     * 綁定信用卡 第二個驗證
     * GetCreditCardAuth2 Listener
     */
    private IGetCreditCardAuth2Listener getCardAuth2Listener = new IGetCreditCardAuth2Listener() { //回來的AuthKey是空的，需拿第一次驗證的到的
        @Override
        public void onGetCreditCardAuth2Success(CreditCardAuthDTO creditCardAuthDTO) {
            onCardAuthsListener.CardAuth2Redirect(creditCardAuthDTO.getAuthKey(), creditCardAuthDTO.getRedirectUrl());
            e7PayModel.unregisterGetCreditCardAuth2Listener();
        }

        @Override
        public void onGetCreditCardAuth2Failed(int i, String s) {
            onCardAuthsListener.Error(i,s);
            e7PayModel.unregisterGetCreditCardAuth2Listener();
        }
    };

    /**
     * 驗證通過後 取得Card key
     * GetCreditCardResponse Listener
     */
    private IGetCreditCardResponseListener getCardResponseListener = new IGetCreditCardResponseListener() {
        @Override
        public void onGetCreditCardResponseSuccess(CreditCardResponseDTO creditCardResponseDTO) {
            onCardAuthsListener.CreditCardResponse(creditCardResponseDTO.getCardKey(), creditCardResponseDTO.getCardToken(),
                    creditCardResponseDTO.getCardNumberShelter(), creditCardResponseDTO.getCardName(), creditCardResponseDTO.getCardType(), creditCardResponseDTO.getExpiredDate());
            e7PayModel.unregisterGetCreditCardResponseListener();
        }

        @Override
        public void onGetCreditCardResponseFailed(int i, String s) {
            onCardAuthsListener.Error(i,s);
            e7PayModel.unregisterGetCreditCardResponseListener();
        }
    };


    private ICancelCreditCardAuthListener cancelCardAuthListener = new ICancelCreditCardAuthListener() {
        @Override
        public void onCancelCreditCardAuthSuccess(String s) {
            onCancelCardListener.CancelCardAuth(s);
            e7PayModel.unregisterCancelCreditCardAuthListener();
        }

        @Override
        public void onCancelCreditCardAuthFailed(int i, String s) {
            onCancelCardListener.Error(i,s);
            e7PayModel.unregisterCancelCreditCardAuthListener();
        }
    };

    private ICancelAllCreditCardAuthListener cancelAllCardAuthListener = new ICancelAllCreditCardAuthListener() {
        @Override
        public void onCancelAllCreditCardAuthSuccess() {
            onCancelCardListener.CancelAllCardAuth();
            e7PayModel.unregisterCancelAllCreditCardAuthListener();
        }

        @Override
        public void onCancelAllCreditCardAuthFailed(int i, String s) {
            onCancelCardListener.Error(i,s);
            e7PayModel.unregisterCancelAllCreditCardAuthListener();
        }
    };

    private IGetCreditCardListListener getCardListListener = new IGetCreditCardListListener() {
        @Override
        public void onGetCreditCardListSuccess(List<CreditCardResponseDTO> list) {
            onCardListListener.SendCardList(list);
            e7PayModel.unregisterGetCreditCardListListener();
        }

        @Override
        public void onGetCreditCardListFailed(int i, String s) {
            onCardListListener.Error(i,s);
            e7PayModel.unregisterGetCreditCardListListener();
        }
    };

    private IGetPaymentBarcodeListener getPaymentBarcodeListener = new IGetPaymentBarcodeListener() {
        @Override
        public void onGetPaymentBarcodeSuccess(PaymentBarcodeDTO paymentBarcodeDTO) { //回來的cardToken是空的
            onPaymentActionListener.SendPaymentBarcode(paymentBarcodeDTO.getBarcode(), paymentBarcodeDTO.getCardToken());
            e7PayModel.unregisterGetPaymentBarcodeListener();
        }

        @Override
        public void onGetPaymentBarcodeFailed(int i, String s) {
            onPaymentActionListener.GetBarcodeError(i,s);
            e7PayModel.unregisterGetPaymentBarcodeListener();
        }

        @Override
        public void onNeedToChangeCardToken(String s) {
            //過期
            onPaymentActionListener.UpdateCardToken(s);
            e7PayModel.unregisterGetPaymentBarcodeListener();
//            e7PayModel.startGetPaymentBarcode(s, PreferenceUtil.getMemNO(context));


        }
    };

    private IGetWalletPymentResponseListener getWalletPymentResponseListener = new IGetWalletPymentResponseListener() {
        @Override
        public void onGetWalletPymentResponseSuccess(WalletPymentResponseDTO walletPymentResponseDTO) {
            onPaymentActionListener.SendWalletPaymentResponse(walletPymentResponseDTO.getStoreName(), walletPymentResponseDTO.getMerchantTradeDate(), walletPymentResponseDTO.getTradeAmount(),
                    walletPymentResponseDTO.getCardNumberShelter(), walletPymentResponseDTO.getCardName(), walletPymentResponseDTO.getTradeStatus(),
                    walletPymentResponseDTO.getTradeStatusName());
            e7PayModel.unregisterGetWalletPymentResponseListener();
        }

        @Override
        public void onGetWalletPymentResponseFailed(int i, String s) {
            onPaymentActionListener.GetWalletPymentResponseFailed();
            e7PayModel.unregisterGetWalletPymentResponseListener();
        }
    };
}
