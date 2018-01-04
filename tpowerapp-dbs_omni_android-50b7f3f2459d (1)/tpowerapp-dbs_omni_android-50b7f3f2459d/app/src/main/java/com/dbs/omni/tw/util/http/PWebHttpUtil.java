package com.dbs.omni.tw.util.http;

import android.app.Activity;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.dbs.omni.tw.setted.GlobalConst;
import com.dbs.omni.tw.util.http.listener.JsonResponseErrorListener;
import com.dbs.omni.tw.util.http.listener.JsonResponseListener;
import com.dbs.omni.tw.util.http.listener.ResponseListener;
import com.google.gson.JsonObject;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by siang on 2017/7/13.
 */

public class PWebHttpUtil extends HttpUtilBase {

    public static void getCreditCardsInfo(Response.Listener<JSONObject> listener,
                                          Response.ErrorListener errorListener,
                                         Activity activity) throws JSONException {
        String apiUrl = GlobalConst.PWEB_SERVER_URL;
        if(mQueue == null)
            mQueue = Volley.newRequestQueue(activity);

        JsonObject tmpBodyParam = new JsonObject();

        tmpBodyParam.addProperty("locale", "zh");
        tmpBodyParam.addProperty("cardType", "all");
        tmpBodyParam.addProperty("segment", "personal");

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST ,apiUrl, new JSONObject(tmpBodyParam.toString()), listener, errorListener);
        request.setRetryPolicy(new DefaultRetryPolicy(TIMEOUT, MAX_RETRY_TIMES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        request.setTag(activity.getClass().getSimpleName());
        mQueue.add(request);
    }

}
