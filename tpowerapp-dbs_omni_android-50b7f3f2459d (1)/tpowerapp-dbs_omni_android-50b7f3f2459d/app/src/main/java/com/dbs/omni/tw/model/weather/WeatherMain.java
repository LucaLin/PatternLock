package com.dbs.omni.tw.model.weather;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by siang on 2017/4/11.
 */

public class WeatherMain implements Parcelable {
    private float temp;
    private int pressure;
    private int humidity;
    private int temp_min;
    private int temp_max;

    protected WeatherMain(Parcel in) {
        temp = in.readFloat();
        pressure = in.readInt();
        humidity = in.readInt();
        temp_min = in.readInt();
        temp_max = in.readInt();
    }

    public static final Creator<WeatherMain> CREATOR = new Creator<WeatherMain>() {
        @Override
        public WeatherMain createFromParcel(Parcel in) {
            return new WeatherMain(in);
        }

        @Override
        public WeatherMain[] newArray(int size) {
            return new WeatherMain[size];
        }
    };

    public float getTemp() {
        return temp;
    }

    public void setTemp(float temp) {
        this.temp = temp;
    }

    public int getPressure() {
        return pressure;
    }

    public void setPressure(int pressure) {
        this.pressure = pressure;
    }

    public int getHumidity() {
        return humidity;
    }

    public void setHumidity(int humidity) {
        this.humidity = humidity;
    }

    public int getTemp_min() {
        return temp_min;
    }

    public void setTemp_min(int temp_min) {
        this.temp_min = temp_min;
    }

    public int getTemp_max() {
        return temp_max;
    }

    public void setTemp_max(int temp_max) {
        this.temp_max = temp_max;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeFloat(temp);
        dest.writeInt(pressure);
        dest.writeInt(humidity);
        dest.writeInt(temp_min);
        dest.writeInt(temp_max);
    }
}
