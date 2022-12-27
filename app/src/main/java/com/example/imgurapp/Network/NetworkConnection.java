package com.example.imgurapp.Network;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class NetworkConnection {

    public static final String TYPE_WIFI = "WIFI";
    public static final String TYPE_MOBILE = "MOBILE_DATA";
    public static final String TYPE_NOT_CONNECTED = "NO_CONNECTION";

    /**
     *
     * @param context The context that is passed
     * @return boolean
     */
    public static boolean isOnline(Context context) {
        ConnectivityManager cm =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnected();
    }

    /**
     *
     * @param context The context that is passed
     * @return boolean
     */
    public static boolean isConnecting(Context context) {
        ConnectivityManager cm =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }

    /**
     *
     * @param context The context that is passed
     * @return String
     */
    public static String getConnectionType(Context context) {
        ConnectivityManager cm =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        if (activeNetwork != null) {
            if (activeNetwork.getType() == ConnectivityManager.TYPE_WIFI) return TYPE_WIFI;
            if (activeNetwork.getType() == ConnectivityManager.TYPE_MOBILE) return TYPE_MOBILE;
        }
        return TYPE_NOT_CONNECTED;
    }

}
