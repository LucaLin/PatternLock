package tw.com.taishinbank.ewallet.util;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.util.Log;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import tw.com.taishinbank.ewallet.R;
import tw.com.taishinbank.ewallet.controller.ActivityBase;
import tw.com.taishinbank.ewallet.controller.AddingContactActivity;
import tw.com.taishinbank.ewallet.controller.MainActivity;
import tw.com.taishinbank.ewallet.controller.SVLoginActivity;
import tw.com.taishinbank.ewallet.controller.WalletApplication;
import tw.com.taishinbank.ewallet.controller.extra.MyTicketFragment;
import tw.com.taishinbank.ewallet.controller.extra.MyTicketResultFragment;
import tw.com.taishinbank.ewallet.controller.extra.ReceiveCouponActivity;
import tw.com.taishinbank.ewallet.controller.red.MyRedEnvelopeDetailFragment;
import tw.com.taishinbank.ewallet.controller.red.ReceiveRedEnvelopeActivity;
import tw.com.taishinbank.ewallet.controller.red.RedEnvelopeFragment;
import tw.com.taishinbank.ewallet.controller.sv.ReceivePaymentActivity;
import tw.com.taishinbank.ewallet.controller.sv.ReceiveRequestActivity;
import tw.com.taishinbank.ewallet.controller.sv.TransactionLogDetailFragment;
import tw.com.taishinbank.ewallet.controller.sv.TransactionLogFragment;
import tw.com.taishinbank.ewallet.controller.wallethome.SystemMessageDetailFragment;
import tw.com.taishinbank.ewallet.controller.wallethome.WebViewActivity;
import tw.com.taishinbank.ewallet.gcm.WalletGcmListenerService;
import tw.com.taishinbank.ewallet.interfaces.ResponseListener;
import tw.com.taishinbank.ewallet.model.ResponseResult;
import tw.com.taishinbank.ewallet.model.extra.Coupon;
import tw.com.taishinbank.ewallet.model.extra.TicketDetailData;
import tw.com.taishinbank.ewallet.model.red.RedEnvelopeReceivedHeader;
import tw.com.taishinbank.ewallet.model.red.RedEnvelopeSentHeader;
import tw.com.taishinbank.ewallet.model.sv.SVTransactionIn;
import tw.com.taishinbank.ewallet.model.sv.SVTransactionOut;
import tw.com.taishinbank.ewallet.model.wallethome.WalletSystemMsg;
import tw.com.taishinbank.ewallet.util.http.ExtraHttpUtil;
import tw.com.taishinbank.ewallet.util.http.GeneralHttpUtil;
import tw.com.taishinbank.ewallet.util.http.HttpUtilBase;
import tw.com.taishinbank.ewallet.util.http.RedEnvelopeHttpUtil;
import tw.com.taishinbank.ewallet.util.http.SVHttpUtil;
import tw.com.taishinbank.ewallet.util.responsebody.ExtraResponseBodyUtil;
import tw.com.taishinbank.ewallet.util.responsebody.GeneralResponseBodyUtil;
import tw.com.taishinbank.ewallet.util.responsebody.RedEnvelopeResponseBodyUtil;
import tw.com.taishinbank.ewallet.util.responsebody.SVResponseBodyUtil;

public class PushMsgHelper {
    private static final String LOG_TAG = "PushMsgHelper";
    private ActivityBase context;
    private String couponType = null;
    private String httpUtilTag;

    public PushMsgHelper(ActivityBase context, String httpUtilTag){
        this.context = context;
        this.httpUtilTag = httpUtilTag;
    }

    public boolean isValidAction(String urlAction){
        boolean hasInDefined = false;
        for (WalletGcmListenerService.MyPayPushType type : WalletGcmListenerService.MyPayPushType.values()) {
            if (urlAction.equals(type.name())) {
                hasInDefined = true;
                break;
            }
        }
        return hasInDefined;
    }

    public void doActionAccordingUrl(String url){
        if (TextUtils.isEmpty(url)) {
            Log.d(LOG_TAG, " No action from this push....");
            return;
        }

        //看是不是定義的type
        String urlAction;
        String urlData = null;
        int indexOfColon = url.indexOf(':');
        if(indexOfColon < 0){
            urlAction = url;
        }else{
            urlAction = url.substring(0, indexOfColon);
            urlData = url.substring(indexOfColon + 1);
        }
        Log.d(LOG_TAG, " Action is " + urlAction);
        Log.d(LOG_TAG, " Data is " + urlData);

        if (!isValidAction(urlAction)) {
            Log.d(LOG_TAG, " Action " + urlAction + " is not in our definition list.");
            return;
        }


        /*  webview:${網址}
            webview:http://google.com
            以webview開啟網頁 */
        if(urlAction.equals(WalletGcmListenerService.MyPayPushType.webview.name())){
            // 如果後面網址為空字串或null，則開註冊儲值網址
            if(TextUtils.isEmpty(urlData) || urlData.equalsIgnoreCase("null")){
                urlData = HttpUtilBase.getSvRegisterUrl(context);
            }
            Intent intent = new Intent(context, WebViewActivity.class);
            intent.putExtra(WebViewActivity.EXTRA_URL, urlData);
            context.startActivity(intent);

        /*  redhome
            redhome
            開啟紅包首頁 */
        } else if (urlAction.equals(WalletGcmListenerService.MyPayPushType.redhome.name())) {
            Fragment fragment = new RedEnvelopeFragment();
            gotoFragment(fragment, true);

        /* redrecordin:${txfSeq}|${txfdSeq} */
        } else if (urlAction.equals(WalletGcmListenerService.MyPayPushType.redrecordin.name())) {
            String[] data = (urlData == null) ? null : urlData.split("\\|");
            if(data == null || data.length < 2){
                Log.d(LOG_TAG, "urlAction " + urlAction + " data not correct!");
                return ;
            }
            onReceivedItemClicked(data[0], data[1]);

        /* redrecordout:${txfSeq}|${txfdSeq} */
        } else if (urlAction.equals(WalletGcmListenerService.MyPayPushType.redrecordout.name())) {
            String[] data = (urlData == null) ? null : urlData.split("\\|");
            if(data == null || data.length < 2){
                Log.d(LOG_TAG, "urlAction " + urlAction + " data not correct!");
                return ;
            }
            onSentItemClicked(data[0], data[1]);

        /* receive:${txfSeq}|${txfdSeq} */
        } else if (urlAction.equals(WalletGcmListenerService.MyPayPushType.receive.name())) {
            String[] data = (urlData == null) ? null : urlData.split("\\|");
            if(data == null || data.length < 2){
                Log.d(LOG_TAG, "urlAction " + urlAction + " data not correct!");
                return ;
            }
            inquiryTransactionInItem(data[1], data[0]);

        /* outgoing:${txfSeq}|${txfdSeq} */
        } else if (urlAction.equals(WalletGcmListenerService.MyPayPushType.outgoing.name())) {
            String[] data = (urlData == null) ? null : urlData.split("\\|");
            if(data == null || data.length < 2){
                Log.d(LOG_TAG, "urlAction " + urlAction + " data not correct!");
                return ;
            }
            inquiryTransactionOutItem(data[1], data[0], responseListenerOutgoing);

        /* payreq:${txfSeq}|${txfdSeq} */
        } else if (urlAction.equals(WalletGcmListenerService.MyPayPushType.payreq.name())) {
            String[] data = (urlData == null) ? null : urlData.split("\\|");
            if(data == null || data.length < 2){
                Log.d(LOG_TAG, "urlAction " + urlAction + " data not correct!");
                return ;
            }
            inquiryTransactionOutItem(data[1], data[0], responseListenerTransactionOut);

        /* svin
         * 開啟帳戶紀錄-帳戶收入 */
        } else if (urlAction.equals(WalletGcmListenerService.MyPayPushType.svin.name())) {
            // 如果需要儲值登入
            if(((WalletApplication)context.getApplication()).needSVLogin()) {
                context.startActivityForResult(new Intent(context, SVLoginActivity.class), SVLoginActivity.REQUEST_LOGIN_SV);
                return ;
            }
            Fragment fragment = TransactionLogFragment.newInstance(TransactionLogFragment.TRX_IN);
            gotoFragment(fragment, true);

        /* svout
         * 開啟帳戶紀錄-帳戶支出 */
        } else if (urlAction.equals(WalletGcmListenerService.MyPayPushType.svout.name())) {
            // 如果需要儲值登入
            if(((WalletApplication)context.getApplication()).needSVLogin()) {
                context.startActivityForResult(new Intent(context, SVLoginActivity.class), SVLoginActivity.REQUEST_LOGIN_SV);
                return ;
            }
            Fragment fragment = TransactionLogFragment.newInstance(TransactionLogFragment.TRX_OUT);
            gotoFragment(fragment, true);

        /* coupon:${cpSeq}|${msmSeq}|${status} */
        } else if (urlAction.equals(WalletGcmListenerService.MyPayPushType.coupon.name())) {
            String[] data = (urlData == null) ? null : urlData.split("\\|");
            if(data == null || data.length < 3){
                Log.d(LOG_TAG, "urlAction " + urlAction + " data not correct!");
                return ;
            }
            /* coupon:2222|33333|1 （優惠活動） */
            /* coupon:2222|33333|2 （好友贈送）& 沒回覆訊息 才顯示回覆按鈕 */
            /* coupon:2222|33333|3 （轉送後留言回覆） */
            couponType = data[2];

            // 如果沒有網路連線，顯示提示對話框
            if (!NetworkUtil.isConnected(context)) {
                context.showAlertDialog(context.getString(R.string.msg_no_available_network), android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }, true);
            } else {// 呼叫api取得收到的紅包
                try {
                    ExtraHttpUtil.queryCouponDetail(data[0], data[1], responseListenerCoupon, context, httpUtilTag);
                    context.showProgressLoading();
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }


        /* http、https:${網址} */
        } else if (urlAction.equals(WalletGcmListenerService.MyPayPushType.http.name()) || urlAction.equals(WalletGcmListenerService.MyPayPushType.https.name())) {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            context.startActivity(intent);

        /* newfriend:${self.memPhone}|yyyyMMddHHmmss(createDate) */
        } else if (urlAction.equals(WalletGcmListenerService.MyPayPushType.newfriend.name())) {
            String[] data = (urlData == null) ? null : urlData.split("\\|");
            if(data == null || data.length < 1){
                Log.d(LOG_TAG, "urlAction " + urlAction + " data not correct!");
                return ;
            }
            if(data.length < 2){
                gotoAddingContactWithPhone(data[0]);
            } else {
                // 如果沒有網路連線，顯示提示對話框
                if (!NetworkUtil.isConnected(context)) {
                    context.showAlertDialog(context.getString(R.string.msg_no_available_network), android.R.string.ok, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    }, true);
                    return;
                }
                // 呼叫api做手機號碼認證
                try {
                    RedEnvelopeHttpUtil.getAccountCheckByPhone(data[0], data[1], url, new CheckPhoneResponseListener(data[0]), context, httpUtilTag);
                    context.showProgressLoading();
                } catch (JSONException e) {
                    e.printStackTrace();
                    // TODO
                }
            }
//            DatabaseHelper dbHelper = new DatabaseHelper(context);
//            ArrayList<LocalContact> searchedContacts = dbHelper.searchByPhone(urlData);
            // 1.已是好友：顯示好友詳情
//            if(searchedContacts != null && searchedContacts.size() > 0){
//                Intent intent = new Intent(context, ContactDetailActivity.class);
//                intent.putExtra(ContactDetailActivity.EXTRA_CONTACT_DATA, searchedContacts.get(0));
//                context.startActivity(intent);
//                // 2.非好友：顯示電話搜尋結果
//            }else{
//                Intent intent = new Intent(context, AddingContactActivity.class);
//                intent.putExtra(AddingContactActivity.EXTRA_SEARCH_PHONE, urlData);
//                context.startActivity(intent);
//            }

        /* nc:${bbType} */
        } else if (urlAction.equals(WalletGcmListenerService.MyPayPushType.nc.name())) {
            /* nc:1 (優惠公告) */
            /* nc:2 (邀請註冊儲值) */
            /* nc:3 (邀請綁定信用卡) */
            String[] data = (urlData == null) ? null : urlData.split("\\|");
            if(data == null || data.length < 1){
                Log.d(LOG_TAG, "urlAction " + urlAction + " data not correct!");
                return ;
            }
            Integer bbSeq = null;
            if(data.length >= 2){
                bbSeq = Integer.valueOf(data[1]);
            }
            querySystemMessage(data[0], bbSeq);

            /* etkreturn:$(odrSeq) */
        } else if (urlAction.equals(WalletGcmListenerService.MyPayPushType.etkreturn.name())) {

            queryReturnTicketMessage(urlData);
        }
        //
    }

    // ------
    // 收付款牌卡
    // ------

    // 取得付款紀錄
    private void inquiryTransactionOutItem(String txfdSeq, String txfSeq, ResponseListener listener) {
        // 如果需要儲值登入
        if(((WalletApplication)context.getApplication()).needSVLogin()) {
            context.startActivityForResult(new Intent(context, SVLoginActivity.class), SVLoginActivity.REQUEST_LOGIN_SV);
            return ;
        }

        // 如果沒有網路連線，顯示提示對話框
        if (!NetworkUtil.isConnected(context)) {
            context.showAlertDialog(context.getString(R.string.msg_no_available_network), android.R.string.ok, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            }, true);
        } else {
            // 呼叫api取得資料
            try {
                // TODO 改成正確api request
                SVHttpUtil.queryExpendTxLog(HttpUtilBase.MonthOption.NA, txfdSeq, txfSeq, listener, context, httpUtilTag);
                context.showProgressLoading();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    // 取得付款紀錄的listener
    private class CustomResponseListener extends ResponseListener {

        public static final int REQUEST_OUTGOING = 1;
        public static final int REQUEST_PAYREQ = 2;
        private int requestCode;

        public CustomResponseListener(int requestCode){
            this.requestCode = requestCode;
        }

        @Override
        public void onResponse(ResponseResult result) {
            if(context == null)
                return;

            context.dismissProgressLoading();
            String returnCode = result.getReturnCode();

            // 如果returnCode是成功
            if (returnCode.equals(ResponseResult.RESULT_SUCCESS)) {
                // 取得列表
                List<SVTransactionOut> list = SVResponseBodyUtil.parseTransactionOutList(result.getBody());
                if(list.size() > 0) {
                    if(requestCode == REQUEST_PAYREQ) {
                        Intent intent = new Intent(context, ReceiveRequestActivity.class);
                        intent.putExtra(ReceiveRequestActivity.EXTRA_SV_TRX_OUT, list.get(0));
                        context.startActivity(intent);
                    }else if(requestCode == REQUEST_OUTGOING){
                        TransactionLogDetailFragment fragment = TransactionLogDetailFragment.newInstance(list.get(0));
                        gotoFragment(fragment, true);
                    }
                }
                // TODO 需要列表為空的處理？
            } else {
                // 執行預設的錯誤處理 
                handleResponseError(result, context);
            }
        }
    }

    private CustomResponseListener responseListenerTransactionOut = new CustomResponseListener(CustomResponseListener.REQUEST_PAYREQ);
    private CustomResponseListener responseListenerOutgoing = new CustomResponseListener(CustomResponseListener.REQUEST_OUTGOING);

    // 查詢收款紀錄
    private void inquiryTransactionInItem(String txfdSeq, String txfSeq) {
        // 如果需要儲值登入
        if(((WalletApplication)context.getApplication()).needSVLogin()) {
            context.startActivityForResult(new Intent(context, SVLoginActivity.class), SVLoginActivity.REQUEST_LOGIN_SV);
            return ;
        }

        // 如果沒有網路連線，顯示提示對話框
        if(!NetworkUtil.isConnected(context)){
            context.showAlertDialog(context.getString(R.string.msg_no_available_network), android.R.string.ok, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            }, true);
            return ;
        }

        // 呼叫api取得收到的付款資訊
        try {
            // TODO 改成正確api request
            SVHttpUtil.queryIncomeTxLog(HttpUtilBase.MonthOption.NA, txfdSeq, txfSeq, responseListenerTransactionIn, context, httpUtilTag);
            context.showProgressLoading();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    // 呼叫收款紀錄的listener
    private ResponseListener responseListenerTransactionIn = new ResponseListener() {

        @Override
        public void onResponse(ResponseResult result) {
            if(context == null)
                return;

            context.dismissProgressLoading();
            String returnCode = result.getReturnCode();
            // 如果returnCode是成功
            if (returnCode.equals(ResponseResult.RESULT_SUCCESS)) {
                // 取得列表
                List<SVTransactionIn> list = SVResponseBodyUtil.parseTransactionInList(result.getBody());
                if(list.size() > 0) {
                    Intent intent = new Intent(context, ReceivePaymentActivity.class);
                    intent.putExtra(ReceivePaymentActivity.EXTRA_SV_TRX_IN, list.get(0));
                    context.startActivity(intent);
                }
                // TODO 需要列表為空的處理？

            } else {
                // 執行預設的錯誤處理 
                handleResponseError(result, context);
            }
        }
    };

    // ------
    // 紅包牌卡
    // ------
    public void onReceivedItemClicked(String txfSeq, String txfdSeq) {
        // 如果需要儲值登入
        if(((WalletApplication)context.getApplication()).needSVLogin()) {
            context.startActivityForResult(new Intent(context, SVLoginActivity.class), SVLoginActivity.REQUEST_LOGIN_SV);
            return ;
        }

        // 如果沒有網路連線，顯示提示對話框
        if(!NetworkUtil.isConnected(context)){
            context.showAlertDialog(context.getString(R.string.msg_no_available_network), android.R.string.ok, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            }, true);
            return ;
        }

        // 呼叫api查單筆，直接顯示該則紅包的收到詳情
        try {
            RedEnvelopeHttpUtil.getRedEnvelopeReceived("", "", txfdSeq, txfSeq, responseListenerReceived, context, httpUtilTag);
            context.showProgressLoading();
        } catch (JSONException e) {
            e.printStackTrace();
            // TODO
        }
    }

    public void onSentItemClicked(String txfSeq, String txfdSeq) {
        // 如果需要儲值登入
        if(((WalletApplication)context.getApplication()).needSVLogin()) {
            context.startActivityForResult(new Intent(context, SVLoginActivity.class), SVLoginActivity.REQUEST_LOGIN_SV);
            return ;
        }

        // 如果沒有網路連線，顯示提示對話框
        if(!NetworkUtil.isConnected(context)){
            context.showAlertDialog(context.getString(R.string.msg_no_available_network), android.R.string.ok, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            }, true);
            return ;
        }

        // 呼叫api查單筆，直接顯示該則紅包的發送詳情
        try {
            RedEnvelopeHttpUtil.getRedEnvelopeSend("", "", txfdSeq, txfSeq, responseListenerSent, context, httpUtilTag);
            context.showProgressLoading();
        } catch (JSONException e) {
            e.printStackTrace();
            // TODO
        }
    }

    // 呼叫查詢收到紅包api的listener
    private ResponseListener responseListenerReceived = new ResponseListener() {

        @Override
        public void onResponse(ResponseResult result) {
            if(context == null)
                return;

            context.dismissProgressLoading();
            String returnCode = result.getReturnCode();
            // 如果returnCode是成功
            if(returnCode.equals(ResponseResult.RESULT_SUCCESS)) {
                // 開啟收到的紅包詳情
                ArrayList<RedEnvelopeReceivedHeader> list = RedEnvelopeResponseBodyUtil.getRedEnvelopReceivedList(result.getBody());
                if(list != null && list.size() > 0) {
                    RedEnvelopeReceivedHeader item = list.get(0);
                    Intent intent = new Intent(context, ReceiveRedEnvelopeActivity.class);
                    intent.putExtra(ReceiveRedEnvelopeActivity.EXTRA_RECEIVED_HEADER, item);
                    context.startActivity(intent);
                }else{
                    // TODO parse data 或 取資料有問題？
                }
            }else{
                // 執行預設的錯誤處理 
                handleResponseError(result, context);
            }
        }
    };

    // 呼叫查詢發送紅包api的listener
    private ResponseListener responseListenerSent = new ResponseListener() {

        @Override
        public void onResponse(ResponseResult result) {
            if(context == null)
                return;

            context.dismissProgressLoading();
            String returnCode = result.getReturnCode();
            // 如果returnCode是成功
            if(returnCode.equals(ResponseResult.RESULT_SUCCESS)) {
                // 開啟發送的紅包詳情
                ArrayList<RedEnvelopeSentHeader> list = RedEnvelopeResponseBodyUtil.getRedEnvelopSentList(result.getBody());
                if(list != null && list.size() > 0){
                    RedEnvelopeSentHeader item = list.get(0);
                    MyRedEnvelopeDetailFragment fragment = MyRedEnvelopeDetailFragment.newInstance(item);
                    gotoFragment(fragment, true);
                }else{
                    // TODO parse data 或 取資料有問題？
                }
            }else{
                // 執行預設的錯誤處理 
                handleResponseError(result, context);
            }
        }
    };


    // 呼叫查詢優惠卷詳情api的listener
    private ResponseListener responseListenerCoupon = new ResponseListener() {

        @Override
        public void onResponse(ResponseResult result) {
            if(context == null)
                return;

            context.dismissProgressLoading();
            String returnCode = result.getReturnCode();
            // 如果returnCode是成功
            if(returnCode.equals(ResponseResult.RESULT_SUCCESS)) {
                Coupon coupon = ExtraResponseBodyUtil.getCoupon(result.getBody());
                Intent intent = new Intent(context, ReceiveCouponActivity.class);
                intent.putExtra(ReceiveCouponActivity.EXTRA_COUPON, coupon);
                context.startActivity(intent);
            }else{
                // 執行預設的錯誤處理 
                handleResponseError(result, context);
            }
        }
    };

    // 手機號碼驗證的response listener
    private class CheckPhoneResponseListener extends ResponseListener {
        private String phoneNumber;

        public CheckPhoneResponseListener(String phoneNumber){
            this.phoneNumber = phoneNumber;
        }

        @Override
        public void onResponse(ResponseResult result) {
            if(context == null)
                return;

            context.dismissProgressLoading();
            String returnCode = result.getReturnCode();
            // 如果returnCode是成功
            if(returnCode.equals(ResponseResult.RESULT_SUCCESS)) {
                gotoAddingContactWithPhone(phoneNumber);
            }else{
                // 執行預設的錯誤處理 
                handleResponseError(result, context);
            }
        }
    }

    private void gotoAddingContactWithPhone(String phoneNumber){
        Intent intent = new Intent(context, AddingContactActivity.class);
        intent.putExtra(AddingContactActivity.EXTRA_SEARCH_PHONE, phoneNumber);
        context.startActivity(intent);
    }

    private void querySystemMessage(String bbType, Integer bbSeq) {
        // 如果沒有網路連線，顯示提示對話框
        if (!NetworkUtil.isConnected(context)) {
            context.showAlertDialog(context.getString(R.string.msg_no_available_network), android.R.string.ok, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            }, true);
        } else {
            // 呼叫api取得系統訊息詳情
            try {
                GeneralHttpUtil.querySystemMessage(bbSeq, new SystemMessageResponseListener(bbType), context, httpUtilTag);
                context.showProgressLoading();
            } catch (JSONException e) {
                e.printStackTrace();
                // TODO
            }
        }
    }

    // 呼叫系統公告詳情的listener
    private class SystemMessageResponseListener extends ResponseListener {
        private String bbType;

        public SystemMessageResponseListener(String bbType){
            this.bbType = bbType;
        }

        @Override
        public void onResponse(ResponseResult result) {
            if(context == null){
                return ;
            }
            context.dismissProgressLoading();

            String returnCode = result.getReturnCode();
            // 如果returnCode是成功
            if (returnCode.equals(ResponseResult.RESULT_SUCCESS)) {
                // 取得列表
                ArrayList<WalletSystemMsg> systemMsgList = GeneralResponseBodyUtil.parseSystemMessageList(result.getBody());
                if(systemMsgList.size() > 0) {
                    // 檢查是否有type一樣的系統訊息，要顯示詳情
                    for (WalletSystemMsg item : systemMsgList) {
                        if(item.getBbType().equals(bbType)){
                            // 檢查並存入Local列表
                            Set<String> readSet = PreferenceUtil.getSystemMessageReadList(context);
                            if (!TextUtils.isEmpty(item.getCreateDate()) && !readSet.contains(item.getCreateDate())) {
                                readSet.add(item.getCreateDate());
                            }
                            PreferenceUtil.setSystemMessageReadList(context, readSet);
                            // 檢查是否有type一樣的系統訊息，要顯示詳情
                            Fragment fragment = SystemMessageDetailFragment.newInstance(item);
                            gotoFragment(fragment, true);
                            break;
                        }
                    }
                }
            } else {
                // 執行預設的錯誤處理 
                handleResponseError(result, context);
            }
        }
    }

    private void queryReturnTicketMessage(String odrSeq) {
        // 如果沒有網路連線，顯示提示對話框
        if (!NetworkUtil.isConnected(context)) {
            context.showAlertDialog(context.getString(R.string.msg_no_available_network), android.R.string.ok, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            }, true);
        } else {
            // 呼叫api取得系統訊息
            try {
//                GeneralHttpUtil.querySystemMessage(new SystemMessageResponseListener(bbType), context, httpUtilTag);
                ExtraHttpUtil.queryReturnOrderForTicket(odrSeq, responseListenerReturnTicket, context, httpUtilTag);
                context.showProgressLoading();
            } catch (JSONException e) {
                e.printStackTrace();
                // TODO
            }
        }
    }

    // 呼叫查詢優惠卷詳情api的listener
    private ResponseListener responseListenerReturnTicket = new ResponseListener() {

        @Override
        public void onResponse(ResponseResult result) {
            if(context == null)
                return;

            context.dismissProgressLoading();
            String returnCode = result.getReturnCode();
            // 如果returnCode是成功
            if(returnCode.equals(ResponseResult.RESULT_SUCCESS)) {

                TicketDetailData ticketDetailData = ExtraResponseBodyUtil.getTicketDetail(result.getBody());
                goToOrderRetutnResult(ticketDetailData);
//                Coupon coupon = ExtraResponseBodyUtil.getCoupon(result.getBody());
//                Intent intent = new Intent(context, ReceiveCouponActivity.class);
//                intent.putExtra(ReceiveCouponActivity.EXTRA_COUPON, coupon);
//                context.startActivity(intent);
            }else{
                // 執行預設的錯誤處理 
                handleResponseError(result, context);
            }
        }
    };


    /**
     * 切換到指定的fragment
     */
    private void gotoFragment(Fragment fragment, boolean withAnimation){
        FragmentTransaction ft = context.getSupportFragmentManager().beginTransaction();
        if(withAnimation) {
            ft.setCustomAnimations(R.anim.in_right_to_left, R.anim.out_right_to_left, R.anim.in_left_to_right, R.anim.out_left_to_right);
        }
        ft.replace(android.R.id.tabcontent, fragment);
        ft.addToBackStack(null);
        ft.commit();
    }

    private void goToOrderRetutnResult(TicketDetailData ticketData) {

        context.getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        MyTicketResultFragment myTicketResultFragment = MyTicketResultFragment.newInstance(MyTicketResultFragment.ENUM_MODE_TYPE.RETURN, ticketData);
        myTicketResultFragment.setOnEventListener(new MyTicketResultFragment.OnEventListener() {
            @Override
            public void OnResultNextClickEvent(int switchTo) {
                goToTicketList(switchTo);
            }
        });

        FragmentTransaction ft = context.getSupportFragmentManager().beginTransaction();
        ft.setCustomAnimations(R.anim.in_right_to_left, R.anim.out_right_to_left, R.anim.in_left_to_right, R.anim.out_left_to_right);
        ft.replace(android.R.id.tabcontent, myTicketResultFragment);
//        ft.addToBackStack(TAG);
        ft.commit();
    }

    private void goToTicketList(int switchTo) {
        Intent intent = new Intent(context, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra(MainActivity.EXTRA_GO_TICKET_LIST, "");
        intent.putExtra(MyTicketFragment.EXTRA_SWITCH_TO, switchTo);
        context.startActivity(intent);
    }

}
