package com.dbs.omni.tw.model.weather;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by siang on 2017/4/11.
 */

public class WeatherDetail implements Parcelable {
    private int id;
    private String main;
    private String description;
    private String icon;

    protected WeatherDetail(Parcel in) {
        id = in.readInt();
        main = in.readString();
        description = in.readString();
        icon = in.readString();
    }

    public static final Creator<WeatherDetail> CREATOR = new Creator<WeatherDetail>() {
        @Override
        public WeatherDetail createFromParcel(Parcel in) {
            return new WeatherDetail(in);
        }

        @Override
        public WeatherDetail[] newArray(int size) {
            return new WeatherDetail[size];
        }
    };

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getMain() {
        return main;
    }

    public void setMain(String main) {
        this.main = main;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(main);
        dest.writeString(description);
        dest.writeString(icon);
    }
}
