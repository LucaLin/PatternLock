package com.dbs.omni.tw.util.http;

import android.os.AsyncTask;
import android.util.Log;

import com.dbs.omni.tw.model.weather.WeatherInfo;
import com.dbs.omni.tw.setted.OmniApplication;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import static com.dbs.omni.tw.util.http.responsebody.weatherResponseBodyUtil.getWeatherInfo;

/**
 * Created by sherman-thinkpower on 2017/4/11.
 */

public class WeatherHttpUtil extends AsyncTask<Void, Void, JSONObject> {
    //    class RetrieveFeedTask  {
    private static final String weatherMapAPIKey = "8703b16eb843ed65643766f23a8af53f";


    private OnWeatherListener onWeatherListener;
    public interface OnWeatherListener {
        void OnFinish(WeatherInfo weatherInfo);
    }

    public void setOnWeatherListener(OnWeatherListener onWeatherListener) {
        this.onWeatherListener = onWeatherListener;
    }

    protected void onPreExecute() {

    }

    protected JSONObject doInBackground(Void... urls) {


        if (OmniApplication.sCurrentLocation == null) {
            return null;
        }

        String uri = String.format("http://api.openweathermap.org/data/2.5/weather?lat=%1$s&lon=%2$s&units=metric&APPID=%3$s", String.valueOf(OmniApplication.sCurrentLocation.getLatitude()), String.valueOf(OmniApplication.sCurrentLocation.getLongitude()), weatherMapAPIKey);
        HttpURLConnection urlConnection = null;
        URL url = null;
        try {
            url = new URL(uri);

            urlConnection = (HttpURLConnection) url.openConnection();
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
            StringBuilder stringBuilder = new StringBuilder();
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                stringBuilder.append(line).append("\n");
            }
            bufferedReader.close();
            JSONObject json = new JSONObject(stringBuilder.toString());
            return json;

        } catch (MalformedURLException e) {
            Log.e("ERROR", e.getMessage(), e);

            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            return null;
        } catch (Exception e) {
            Log.e("ERROR", e.getMessage(), e);

            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            return null;
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
        }
    }

    protected void onPostExecute(JSONObject response) {
        if(response == null) {
//                response = "THERE WAS AN ERROR";
        } else {
            OmniApplication.sWallpaperInfo = getWeatherInfo(response);
            if (this.onWeatherListener != null) {
                this.onWeatherListener.OnFinish(OmniApplication.sWallpaperInfo);
            }
        }
//        Log.i("INFO , 溫度為", String.valueOf(weatherInfo.getMain().getTemp()));
    }


}




