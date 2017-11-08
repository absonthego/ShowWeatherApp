package com.virtusa.showweather;

import android.util.Log;

/**
 * Created by Arun Balaji Sampath on 11/8/2017.
 */

public class SingletonLogger {

    /**
     * Non instantiable constructor
     */
    private SingletonLogger(){};

    private static SingletonLogger instance = new SingletonLogger();

    /**
     * Get the only instance of SingletonLogger
     * @return instance
     */
    public static SingletonLogger getInstance(){
        if(instance==null){
            SingletonLogger instance = new SingletonLogger();
        }
        return instance;
    }

    /**
     * Log print using the following params
     * @param TAG the Class logger runs in.
     * @param log Send what is the log is about.
     * @param str Actual log.
     */
    public void printLog(String TAG, String log, String str){
        Log.i(TAG,"SLogger " + log + " is " + str);
    }
}
