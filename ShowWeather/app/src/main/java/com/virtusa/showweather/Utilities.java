package com.virtusa.showweather;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

/**
 * Created by Arun Balaji Sampath on 11/7/2017.
 */

public class Utilities {


    private static SingletonLogger SLogger = SingletonLogger.getInstance();

    /**
     * Is "text" valid check.
     * @param text
     * @return
     */
    public static boolean isValid(String text) {
        try {
            SLogger.printLog("Utilities"," text ", text);
            if(text != null){
                return (text.length() > 0);
            }else{
                return false;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }return false;
    }


    /**
     * set shared preference
     * @param key
     * @param str
     * @param context
     */
    public static void setPreference(String key, String str, Context context) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString(key, str);
        editor.commit();
    }

}
