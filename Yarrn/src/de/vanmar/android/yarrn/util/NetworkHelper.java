package de.vanmar.android.yarrn.util;

import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import org.androidannotations.annotations.EBean;
import org.androidannotations.annotations.SystemService;

@EBean
public class NetworkHelper {

    @SystemService
    ConnectivityManager connectivityManager;

    public boolean networkAvailable() {
        final NetworkInfo networkInfo = connectivityManager
                .getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnected()
                && networkInfo.isAvailable();
    }

    public boolean wifiAvailable() {
        final NetworkInfo networkInfo = connectivityManager
                .getActiveNetworkInfo();
        return networkInfo != null
                && networkInfo.getType() == ConnectivityManager.TYPE_WIFI
                && networkInfo.isConnected() && networkInfo.isAvailable();
    }

}
