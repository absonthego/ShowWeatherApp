package com.virtusa.showweather;

import android.content.Context;
import android.net.ConnectivityManager;
import android.util.Log;

/**
 * Created by Arun Balaji Sampath on 11/7/2017.
 */

public class NetworkUtil {

    private static SingletonLogger SLogger = SingletonLogger.getInstance();

    /**
     * Check if network connectivity is available
     * @param context
     * @return
     */
    public static boolean isNetworkConnected(Context context){

        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if(cm.getActiveNetworkInfo()!=null){

            SLogger.printLog("NetworkUtil","network", "available");
            return true;
        }
        else{
            SLogger.printLog("NetworkUtil","network", "unavailable");
            return false;
        }
    }

}
