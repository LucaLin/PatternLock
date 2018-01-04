package tw.com.taishinbank.ewallet.listener;

import android.util.Log;

import com.android.volley.Response;

import org.json.JSONException;
import org.json.JSONObject;

import tw.com.taishinbank.ewallet.interfaces.ResponseListener;
import tw.com.taishinbank.ewallet.model.ResponseResult;


public class JsonResponseListener implements Response.Listener<JSONObject>{

    private static final String TAG = "JsonResponseListener";

    private String apiName;

    private ResponseListener resultListener;
    public JsonResponseListener(String apiName, ResponseListener resultListener){
        this.apiName = apiName;
        this.resultListener = resultListener;
    }

    @Override
    public void onResponse(JSONObject response) {
        ResponseResult responseResult = new ResponseResult();
        try {
            responseResult.setApiName(apiName);
            // 取得response物件
            JSONObject objectResponse = response.getJSONObject(apiName + "response");
            // 取得header
            JSONObject objectHeader = objectResponse.getJSONObject("header");
            Log.d(TAG, apiName + " header = " + objectHeader.toString());
            responseResult.setReturnCode(objectHeader.getString("returnCode"));
            responseResult.setReturnMessage(objectHeader.getString("returnMessage"));
            responseResult.setSvTokenID(objectHeader.getString("svTokenID"));
            responseResult.setTokenID(objectHeader.getString("tokenID"));
            // 取得body
            JSONObject objectBody = objectResponse.getJSONObject("body");
            Log.d(TAG, "body = " + objectBody.toString());
            responseResult.setBody(objectBody);
        } catch (JSONException e) {
            e.printStackTrace();
            responseResult.setReturnCode(ResponseResult.RESULT_JSON_ERROR);
            responseResult.setReturnMessage("資料解析發生錯誤，請稍候再試。");
        }
        if(resultListener != null) {
            resultListener.onResponse(responseResult);
        }
    }
}
