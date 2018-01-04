package com.dbs.omni.tw.model.weather;

import android.content.Context;

import com.dbs.omni.tw.R;

import java.util.ArrayList;

/**
 * Created by siang on 2017/4/11.
 */

public class WeatherInfo {

    private ArrayList<WeatherDetail> weather;
    private WeatherMain main;

    public ArrayList<WeatherDetail> getWeather() {
        return weather;
    }

    public void setWeather(ArrayList<WeatherDetail> weather) {
        this.weather = weather;
    }

    public WeatherMain getMain() {
        return main;
    }

    public void setMain(WeatherMain main) {
        this.main = main;
    }


    public int getImageID(Context context) {
        String iconID = getWeather().get(0).getIcon();

        if(iconID.equals("01n")) {
            return R.drawable.ic_sunny_night;
        } else if(iconID.equals("02d")) {
            return R.drawable.ic_cloudy_day;
        } else if(iconID.equals("02n")) {
            return R.drawable.ic_cloudy_night;
        } else if(iconID.equals("03d")) {
            return R.drawable.ic_cloudy_day;
        } else if(iconID.equals("03n")) {
            return R.drawable.ic_cloudy_night;
        } else if(iconID.equals("04d")) {
            return R.drawable.ic_cloudy_day;
        } else if(iconID.equals("04n")) {
            return R.drawable.ic_cloudy_night;
        } else if(iconID.equals("09d")) {
            return R.drawable.ic_rainy_day;
        } else if(iconID.equals("09n")) {
            return R.drawable.ic_rainy_night;
        } else if(iconID.equals("10d")) {
            return R.drawable.ic_rainy_day;
        } else if(iconID.equals("10n")) {
            return R.drawable.ic_rainy_night;
        } else if(iconID.equals("11d")) {
            return R.drawable.ic_thunderstorm_day;
        } else if(iconID.equals("11n")) {
            return R.drawable.ic_thunderstorm_night;
        } else if(iconID.equals("13d")) {
            return R.drawable.ic_snow_day;
        } else if(iconID.equals("13n")) {
            return R.drawable.ic_snow_night;
        } else if(iconID.equals("50d")) {
            return R.drawable.ic_mist_day;
        } else if(iconID.equals("50n")) {
            return R.drawable.ic_mist_night;
        } else {
            //if(iconID.equals("01d")) {
            return R.drawable.ic_sunny_day;
        }
//        if(status.equalsIgnoreCase("clear")) {
//            return R.drawable.ic_sunny_day;
//        } else if(  status.equalsIgnoreCase("rain") ||
//                    status.equalsIgnoreCase("snow") || status.equalsIgnoreCase("drizzle")) {
//            return R.drawable.ic_rainy;
//        } else if(  status.equalsIgnoreCase("clouds") ||
//                    status.equalsIgnoreCase("mist") || status.equalsIgnoreCase("Atmosphere")) {
//            return R.drawable.ic_cloudy;
//        } else if(status.equalsIgnoreCase("thunderstorm")){
//            return R.drawable.ic_thunderstorm;
//        }
//        return R.drawable.ic_sunny_day;

    }



}
