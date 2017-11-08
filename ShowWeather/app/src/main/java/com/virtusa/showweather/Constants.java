package com.virtusa.showweather;

/**
 * Created by ASam0001 on 11/7/2017.
 */

public class Constants {

    //URL CONSTANTS
    public static final String URL_GETWEATHER_CITY_PART_1 = "http://api.openweathermap.org/data/2.5/weather?q=";
    public static final String URL_GETWEATHER_ZIP_PART_1 = "http://api.openweathermap.org/data/2.5/weather?zip=";
    public static final String URL_GETWEATHER_PART_2 = ",us&APPID=";
    public static final String URL_GETWEATHER_KEY = "9bcb2b127c8ea6a6977c1bd5a2004b91";
    public static final String URL_GETICON_PART_1 = "http://openweathermap.org/img/w/";
    public static final String URL_GETICON_PART_2 = ".png";

    //KEY CONSTANTS
    public static final String KEY_ICON = "icon";
    public static final String KEY_DESCRIPTION = "description";
    public static final String KEY_WEATHER = "weather";
    public static final String KEY_TEMP_MIN = "temp_min";
    public static final String KEY_TEMP_MAX = "temp_max";
    public static final String KEY_MAIN = "main";
    public static final String KEY_NAME = "name";
    public static final String KEY_STATUS_CODE = "cod";

    //INDEX
    public static final int INDEX_KEY_MAIN = 3;

    //ERROR
    public static final String ERROR_EMPTY_CITY = "City Cannot be Empty";
    public static final String ERROR_EMPTY_ZIP = "Zip Cannot be Empty";
    public static final String ERROR_NO_INTERNET = "Getting weather needs internet connection";

    //SHARED PREFERENCE kEY
    public static final String KEY_LAST_SEARCHED_CITY = "last_searched_city";
    public static final String KEY_LAST_SEARCHED_ZIP = "last_searched_zip";
    public static final String KEY_CITY_OR_ZIP = "last_searched_city_or_zip";

}
