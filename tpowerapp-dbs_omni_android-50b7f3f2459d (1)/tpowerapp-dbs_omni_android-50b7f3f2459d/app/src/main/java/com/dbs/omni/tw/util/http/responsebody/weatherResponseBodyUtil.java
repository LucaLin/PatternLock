package com.dbs.omni.tw.util.http.responsebody;

import com.dbs.omni.tw.model.weather.WeatherDetail;
import com.dbs.omni.tw.model.weather.WeatherInfo;
import com.dbs.omni.tw.model.weather.WeatherMain;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by siang on 2017/4/11.
 */

public class weatherResponseBodyUtil {



    /**
     * 取得天氣資訊
     */
    public static WeatherInfo getWeatherInfo(JSONObject object){
        WeatherInfo weatherInfo = new WeatherInfo();

        try {
            JSONArray tmpList = object.getJSONArray("weather");
            Gson gson = new Gson();
            ArrayList<WeatherDetail> weatherDetails = gson.fromJson(tmpList.toString(), new TypeToken<ArrayList<WeatherDetail>>() {
            }.getType());

            JSONObject tmpMain =  object.getJSONObject("main");
            WeatherMain weatherMain = gson.fromJson(tmpMain.toString(), WeatherMain.class);

            weatherInfo.setWeather(weatherDetails);
            weatherInfo.setMain(weatherMain);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return weatherInfo;
    }
}
