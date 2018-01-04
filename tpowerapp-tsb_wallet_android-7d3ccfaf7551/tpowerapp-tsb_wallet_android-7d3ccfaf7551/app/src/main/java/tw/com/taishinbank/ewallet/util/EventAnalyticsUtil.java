package tw.com.taishinbank.ewallet.util;


import android.content.Context;

import org.json.JSONException;

import java.util.ArrayList;

import tw.com.taishinbank.ewallet.dbhelper.HitRecordDBHelper;
import tw.com.taishinbank.ewallet.dbhelper.SpecialEventsDBHelper;
import tw.com.taishinbank.ewallet.interfaces.ResponseListener;
import tw.com.taishinbank.ewallet.model.ResponseResult;
import tw.com.taishinbank.ewallet.model.log.HitRecord;
import tw.com.taishinbank.ewallet.model.log.HitRecord.*;
import tw.com.taishinbank.ewallet.model.log.SpecialEvent;
import tw.com.taishinbank.ewallet.util.http.GeneralHttpUtil;

public class EventAnalyticsUtil {

    private static final String TAG = "EventAnalyticsUtil";

    private static HitRecordDBHelper hitRecordDBHelper;
    private static SpecialEventsDBHelper specialEventsDBHelper;

    private static ArrayList<HitRecord> hitRecords = new ArrayList<>();
    private static ArrayList<SpecialEvent> specialEvents = new ArrayList<>();

    private static Context context;

    public static Context getContext() {
        return context;
    }

    public static void setContext(Context context) {
        EventAnalyticsUtil.context = context;
    }

    /***
    * HitRecord Util
    ***/
    private static void initHitRecordDBHelper(Context context) {
        if(hitRecordDBHelper == null) {
            hitRecordDBHelper = new HitRecordDBHelper(context);
        }
    }

    public static void addHitRecordEvent(Context context, HitEvent hitEvent, HitType hitType) {
        setContext(context);
        initHitRecordDBHelper(getContext());

        hitRecordDBHelper.insert(hitEvent, hitType);
    }

    public static void uploadHitRecordEvent(Context context, HitEvent hitEvent, HitType hitType) {
        setContext(context);

        ArrayList<HitRecord> hitRecordList = new ArrayList<>();
        hitRecordList.add(new HitRecord(hitEvent.getCode(), hitType.getCode()));
        if (NetworkUtil.isConnected(getContext())) {
            try {
                GeneralHttpUtil.uploadHitRecords(hitRecordList, responseListenerForOneHitRecord, getContext());
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    public static void uploadHitRecordEvents(Context context) {
        setContext(context);
        initHitRecordDBHelper(getContext());

        hitRecords = hitRecordDBHelper.getAll();

        if(hitRecords.size() == 0)
            return;

        // 如果沒有網路連線，顯示提示對話框
        if (NetworkUtil.isConnected(getContext())) {
            try {
                GeneralHttpUtil.uploadHitRecords(hitRecords, responseListenerForHitRecord, getContext());
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    private static ResponseListener responseListenerForOneHitRecord = new ResponseListener() {

        @Override
        public void onResponse(ResponseResult result) {

            String returnCode = result.getReturnCode();

            // 如果returnCode是成功
            if (returnCode.equals(ResponseResult.RESULT_SUCCESS)) {
                // 取得列表
            } else {
                // 執行預設的錯誤處理 
             //   handleResponseError(result, (ActivityBase) getActivity());
            }
        }
    };

    private static ResponseListener responseListenerForHitRecord = new ResponseListener() {

        @Override
        public void onResponse(ResponseResult result) {

            String returnCode = result.getReturnCode();

            // 如果returnCode是成功
            if (returnCode.equals(ResponseResult.RESULT_SUCCESS)) {
                // 取得列表
                deleteHitRecords(hitRecords);
            } else {
                // 執行預設的錯誤處理 
             //   handleResponseError(result, (ActivityBase) getActivity());
            }
        }
    };

    private static void deleteHitRecords(ArrayList<HitRecord> hitRecords) {
        initHitRecordDBHelper(getContext());

        hitRecordDBHelper.deleteList(hitRecords);
    }

    /***
     * SpecialEvent Util
     ***/

    private static void initSpecialEventsDBHelper(Context context) {
        if(specialEventsDBHelper == null) {
            specialEventsDBHelper = new SpecialEventsDBHelper(context);
        }
    }

    public static void addSpecialEvent(Context context, SpecialEvent specialEvent) {
        setContext(context);
        initSpecialEventsDBHelper(getContext());

        specialEventsDBHelper.insert(specialEvent);
    }

    public static void uploadSpecialEvents(Context context) {
        setContext(context);
        initSpecialEventsDBHelper(getContext());

        specialEvents = specialEventsDBHelper.getAll();

        if(specialEvents.size() == 0)
            return;

        // 如果沒有網路連線，顯示提示對話框
        if (NetworkUtil.isConnected(getContext())) {
            try {
                GeneralHttpUtil.uploadSpecialEvents(specialEvents, responseListenerForSpecialEvent, getContext());
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    private static ResponseListener responseListenerForSpecialEvent = new ResponseListener() {

        @Override
        public void onResponse(ResponseResult result) {

            String returnCode = result.getReturnCode();

            // 如果returnCode是成功
            if (returnCode.equals(ResponseResult.RESULT_SUCCESS)) {
                // 取得列表
                deleteSpecialEvents(specialEvents);
            } else {
                // 執行預設的錯誤處理 
            //    handleResponseError(result, (ActivityBase) getActivity());
            }
        }
    };

    private static void deleteSpecialEvents(ArrayList<SpecialEvent> specialEvents) {
        initHitRecordDBHelper(getContext());

        specialEventsDBHelper.deleteList(specialEvents);
    }

    /***
     * Log Format function
     */

    public static String logFormatToAPI(String apiName, String note) {
        return logFormatToAPI(apiName, note, false);
    }

    public static String logFormatToAPI(String apiName, String note, boolean isRequest) {
        // Title
        String title;
        if(isRequest) {
            title = String.format("[%1$s Request]", apiName);
        } else {
            title = String.format("[%1$s Response]", apiName);
        }

        return String.format("%1$s %2$s", title, note);
    }
}
