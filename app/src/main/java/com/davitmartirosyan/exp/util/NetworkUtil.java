package com.davitmartirosyan.exp.util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class NetworkUtil {

    private static final String LOG_TAG = NetworkUtil.class.getSimpleName();


    private static NetworkUtil sInstance;


    private NetworkUtil() {
    }

    public static NetworkUtil getInstance() {
        if (sInstance == null) {
            sInstance = new NetworkUtil();
        }
        return sInstance;
    }

    public boolean isConnected(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager)
                context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnectedOrConnecting();
    }

}