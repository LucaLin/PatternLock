package tw.com.taishinbank.ewallet.listener;

import android.util.Log;

import com.android.volley.Response;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;

import tw.com.taishinbank.ewallet.interfaces.ResponseListener;
import tw.com.taishinbank.ewallet.model.ResponseResult;


public class JsonResponseErrorListener implements Response.ErrorListener {

    protected String TAG = JsonResponseErrorListener.class.getSimpleName();
    protected ResponseListener responseListener;

    private String apiName;

    public JsonResponseErrorListener(String apiName, ResponseListener responseListener){
        this.responseListener = responseListener;
        this.apiName = apiName;
    }

    @Override
    public void onErrorResponse(VolleyError error) {
        ResponseResult responseResult = new ResponseResult();
        responseResult.setApiName(apiName);
        Log.d(TAG, "Error! LocalizedMessage: " + error.getLocalizedMessage());
        Log.d(TAG, "Error! Message: " + error.getMessage());
        if (error.networkResponse != null) {
            Log.d(TAG, "NetworkResponse Error! Status code: " + error.networkResponse.statusCode);
            responseResult.setReturnCode(ResponseResult.RESULT_HTTP_RESPONSE_ERROR);
            responseResult.setReturnMessage("伺服器無回應，請稍候再試。");
        } else {
            if (error.getClass().equals(TimeoutError.class)) {
                responseResult.setReturnCode(ResponseResult.RESULT_CONNECTION_TIMEOUT);
                responseResult.setReturnMessage("連線逾期。");
            } else {
                responseResult.setReturnCode(ResponseResult.RESULT_CONNECTION_ERROR);
                responseResult.setReturnMessage("無法建立連線，請稍候再試。");
            }
        }
        if(responseListener != null) {
            responseListener.onResponse(responseResult);
        }
    }
}
