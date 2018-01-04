package com.dbs.omni.tw.util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.util.Log;

import com.dbs.omni.tw.setted.OmniApplication;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.URL;
import java.util.Collections;
import java.util.List;

public class NetworkUtil {
    public static boolean isConnected(Context context) {
        ConnectivityManager connMgr = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        return (networkInfo != null && networkInfo.isConnected());
    }

    public static String getDeviceIPAddress() {
        try {
            List<NetworkInterface> networkInterfaces = Collections.list(NetworkInterface.getNetworkInterfaces());
            for (NetworkInterface networkInterface : networkInterfaces) {
                List<InetAddress> inetAddresses = Collections.list(networkInterface.getInetAddresses());
                for (InetAddress inetAddress : inetAddresses) {
                    if (!inetAddress.isLoopbackAddress()) {
                        return inetAddress.getHostAddress().toUpperCase();
                    }
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return "";
    }




    public static class IPAddressHttpUtil extends AsyncTask<Void, Void, String> {
        //    class RetrieveFeedTask  {


        private OnIPAddressListener onIPAddressListener;
        public interface OnIPAddressListener {
            void OnFinish(String IP);
        }

        public void setOnIPAddressListener(OnIPAddressListener listener) {
            onIPAddressListener = listener;
        }

        protected void onPreExecute() {

        }

        protected String doInBackground(Void... urls) {

            String uri = "http://api.ipify.org/";
            try {
                URL url = new URL(uri);
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                try {
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                    StringBuilder stringBuilder = new StringBuilder();
                    String line;
                    while ((line = bufferedReader.readLine()) != null) {
                        stringBuilder.append(line);
                    }
                    bufferedReader.close();
                    return stringBuilder.toString();
                } finally{
                    urlConnection.disconnect();
                }
            } catch(Exception e) {
                Log.e("ERROR", e.getMessage(), e);
                return null;
            }
        }

        protected void onPostExecute(String response) {
            if (response == null) {
                OmniApplication.sIPAddress = "";
                onIPAddressListener.OnFinish("");
            } else {
                OmniApplication.sIPAddress = response;
                onIPAddressListener.OnFinish(response);
            }


        }
    }
}
