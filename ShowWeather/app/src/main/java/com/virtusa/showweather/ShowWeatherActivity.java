package com.virtusa.showweather;

import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by Arun Balaji Sampath on 11/7/2017.
 */

/**
 * ShowWeatherActivity - First screen of the app to show weather details by location (zip / city)
 */


public class ShowWeatherActivity extends AppCompatActivity {

    private SingletonLogger SLogger = SingletonLogger.getInstance();

    private String TAG = getClass().getName().toString();

    private TextView textViewWeatherDetails;

    private TextView textViewWeatherTempMax;

    private TextView textViewWeatherTempMin;

    private TextView textViewWeatherLocalName;

    private ImageView imageViewIcon;

    private EditText editTextGetCityName;

    private EditText editTextGetZipCode;

    private Button buttonGetCurrentWeather;

    private final String SEARCH_BY_ZIP = "zip";

    private final String SEARCH_BY_CITY = "city";

    private String searchType = SEARCH_BY_CITY; // if zip or city was chosen

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main_get_weather);  // set screen

        buttonGetCurrentWeather = (Button) findViewById(R.id.buttonShowCurrentWeather);
        textViewWeatherDetails = (TextView) findViewById(R.id.textViewWeatherdetails);
        imageViewIcon = (ImageView) findViewById(R.id.imageViewWeatherIcon);
        editTextGetCityName = (EditText) findViewById(R.id.editTextCityName);
        editTextGetZipCode = (EditText) findViewById(R.id.editTextZipCode);
        textViewWeatherTempMin = (TextView) findViewById(R.id.textViewTempMin);
        textViewWeatherTempMax = (TextView) findViewById(R.id.textViewTempMax);
        textViewWeatherLocalName = (TextView) findViewById(R.id.textViewWeatherLocalName);

        buttonGetCurrentWeather.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (NetworkUtil.isNetworkConnected(getApplicationContext())) { // is Internet available check to get weather data

                    String city;
                    String zip;

                    city = editTextGetCityName.getText().toString().trim();
                    zip = editTextGetZipCode.getText().toString().trim();

                    if (searchType.equals(SEARCH_BY_CITY)) {
                        if (Utilities.isValid(city)) {
                            Utilities.setPreference(Constants.KEY_LAST_SEARCHED_CITY, city, getApplicationContext());// To retain last searched city
                            Utilities.setPreference(Constants.KEY_CITY_OR_ZIP, SEARCH_BY_CITY, getApplicationContext());// To retain what was searched (zip / city )
                            getWeatherData(SEARCH_BY_CITY, city);
                        } else {
                            Toast.makeText(getApplicationContext(), Constants.ERROR_EMPTY_CITY, Toast.LENGTH_SHORT).show();
                        }
                    } else if (searchType.equals(SEARCH_BY_ZIP)) {
                        if (Utilities.isValid(zip)) {
                            Utilities.setPreference(Constants.KEY_LAST_SEARCHED_ZIP, zip, getApplicationContext());// To retain last searched zip
                            Utilities.setPreference(Constants.KEY_CITY_OR_ZIP, SEARCH_BY_ZIP, getApplicationContext());// To retain what was searched (zip / city )
                            getWeatherData(SEARCH_BY_ZIP, zip);
                        } else {
                            Toast.makeText(getApplicationContext(), Constants.ERROR_EMPTY_ZIP, Toast.LENGTH_SHORT).show();
                        }
                    }
                } else {
                    Toast.makeText(getApplicationContext(), Constants.ERROR_NO_INTERNET, Toast.LENGTH_SHORT).show(); // show if no internet connection
                }
            }
        });

        editTextGetCityName.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                if (hasFocus) {
                    editTextGetZipCode.setText("");
                    editTextGetZipCode.setAlpha(0.5f);
                    editTextGetCityName.setAlpha(1.0f);
                    searchType = SEARCH_BY_CITY; // set current search type as city
                }
            }
        });

        editTextGetZipCode.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                if (hasFocus) {
                    editTextGetCityName.setText("");
                    editTextGetCityName.setAlpha(0.5f);
                    editTextGetZipCode.setAlpha(1.0f);
                    searchType = SEARCH_BY_ZIP; // set current search type as zip
                }
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        SharedPreferences sp;
        sp = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        searchType = sp.getString(Constants.KEY_CITY_OR_ZIP, "");   //Restore details of last viewed city/zip based on what was searched.
        if (searchType.equals(SEARCH_BY_CITY)) {
            String city = sp.getString(Constants.KEY_LAST_SEARCHED_CITY, "");
            getWeatherData(searchType, city);           // get weather data for entered city.
            if (findViewById(R.id.editTextCityName) != null) {
                editTextGetCityName.setText(city);
                editTextGetCityName.setFocusableInTouchMode(true);
                editTextGetCityName.requestFocus();
            }
        } else if (searchType.equals(SEARCH_BY_ZIP)) {
            String zip = sp.getString(Constants.KEY_LAST_SEARCHED_ZIP, "");
            getWeatherData(searchType, zip);            // get weather data for entered zip.
            if (findViewById(R.id.editTextZipCode) != null) {
                editTextGetZipCode.setText(zip);
                editTextGetZipCode.setFocusableInTouchMode(true);
                editTextGetZipCode.requestFocus();
            }
        }
    }

    /**
     * JsonTaskWeather : gets weather details
     */
    public class JsonTaskWeather extends AsyncTask<String, String, String> {

        /**
         * @param params passes urls for zip or city
         * @return returns string of weather details
         */
        protected String doInBackground(String... params) {

            HttpURLConnection connection = null;
            BufferedReader buffer = null;

            try {
                URL url = new URL(params[0]);
                connection = (HttpURLConnection) url.openConnection();
                connection.connect();

                InputStream inStream = connection.getInputStream();
                buffer = new BufferedReader(new InputStreamReader(inStream));
                StringBuffer stringBfr = new StringBuffer();
                String line = "";
                while ((line = buffer.readLine()) != null) {
                    stringBfr.append(line);
                }
                String finalJson = stringBfr.toString();  // fetch json

                JSONObject parentObject = new JSONObject(finalJson);
                JSONArray parentArray = parentObject.getJSONArray(Constants.KEY_WEATHER);

                JSONObject finalObject1 = parentArray.getJSONObject(0);
                JSONObject finalObject2 = parentObject.getJSONObject(Constants.KEY_MAIN);
                String weatherLocalName = parentObject.getString(Constants.KEY_NAME);
                int weatherStatusCode = parentObject.getInt(Constants.KEY_STATUS_CODE);

                String weatherIconCode = finalObject1.getString(Constants.KEY_ICON);
                String weatherDescription = finalObject1.getString(Constants.KEY_DESCRIPTION);

                double weatherTempMin = finalObject2.getInt(Constants.KEY_TEMP_MIN);
                double weatherTempMax = finalObject2.getInt(Constants.KEY_TEMP_MAX);

                weatherTempMin = getTempInFarh(weatherTempMin); // K to F calculation
                weatherTempMax = getTempInFarh(weatherTempMax);  // K to F calculation

                String weatherData = weatherStatusCode + "-" + weatherIconCode + "-" + weatherDescription + "-" + weatherTempMin + "-" + weatherTempMax + "-" + weatherLocalName; // could also do with string builder

                SLogger.printLog(TAG, "weatherData", weatherData);

                return weatherData;

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            } finally {
                if (connection != null) {
                    connection.disconnect();
                }

                try {
                    if (buffer != null) {
                        buffer.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return null;
        }

        /**
         * Details in weatherData are set on UI.
         *
         * @param weatherData
         */
        @Override
        protected void onPostExecute(String weatherData) {
            super.onPostExecute(weatherData);

            String[] array = null;
            String weatherIconCode = null;
            String weatherDescription = null;
            String weatherTempMin = null;
            String weatherTempMax = null;
            String weatherLocalName = null;
            int weatherStatusCode = 200;

            try {
                if (Utilities.isValid(weatherData)) {
                    array = weatherData.split("-");

                    if (array != null && array.length >= 2) {
                        weatherStatusCode = Integer.valueOf(array[0]);
                        weatherIconCode = array[1];
                        weatherDescription = array[2];
                        weatherTempMin = array[3];
                        weatherTempMax = array[4];
                        weatherLocalName = array[5];
                    }


//                    Toast.makeText(getApplicationContext(), weatherIconCode + "-" + weatherDescription + "-" + weatherTempMin + "-" + weatherTempMax + "-" + weatherLocalName, Toast.LENGTH_SHORT).show();

                    if (weatherStatusCode != 200) {
                        alertNotFound();
                    } else {
                        textViewWeatherDetails.setText(weatherDescription);
                        textViewWeatherTempMin.setText("min " + weatherTempMin);
                        textViewWeatherTempMax.setText("max " + weatherTempMax);
                        textViewWeatherLocalName.setText(/*"Location: " +*/ weatherLocalName);
                        String url = Constants.URL_GETICON_PART_1 + weatherIconCode + Constants.URL_GETICON_PART_2;
                        Toast.makeText(getApplicationContext(), url, Toast.LENGTH_SHORT).show();
                        new JsonTaskIcon().execute(url);
                    }

                } else {
                    alertNotFound();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Converts Kelvin to Farhenheit
     * @param weatherTempInKelvin
     * @return
     */
    private double getTempInFarh(double weatherTempInKelvin) {
        return 1.8 * (weatherTempInKelvin - 273) + 32;
    }


    /**
     * JsonTaskIcon AsycTask to get and set Icon from URL for icon.
     */
    public class JsonTaskIcon extends AsyncTask<String, String, Bitmap> {

        /**
         * @param params url format "http://openweathermap.org/img/w/03n.png&APPID=asdfasdf";
         * @return Bitmap Image is returned
         */
        protected Bitmap doInBackground(String... params) {

            HttpURLConnection connection = null;
            BufferedReader buffer = null;

            try {
                URL url = new URL(params[0]);
                try {
                    HttpURLConnection httpConnection = (HttpURLConnection) url.openConnection();
                    httpConnection.connect();
                    InputStream inputStream = httpConnection.getInputStream();
                    Bitmap image = BitmapFactory.decodeStream(inputStream);
                    return image;
                } catch (IOException e) {
                    e.printStackTrace();
                    return null;
                } finally {
                    if (connection != null) {
                        connection.disconnect();
                    }
                    try {
                        if (buffer != null) {
                            buffer.close();
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            } catch (MalformedURLException e) {
                e.printStackTrace();
                return null;
            }
        }

        /**
         * Set image as received from the icon URL
         *
         * @param image
         */
        @Override
        protected void onPostExecute(Bitmap image) {
            super.onPostExecute(image);
            imageViewIcon.setImageBitmap(image);
        }
    }

    /**
     * Method builds URL and runs JsonTaskWeather
     *
     * @param userInputType city / zip
     * @param str           user data - city / zip
     */
    private void getWeatherData(String userInputType, String str) {
        String url = "";
        if (userInputType.equals(SEARCH_BY_CITY)) {
            url = Constants.URL_GETWEATHER_CITY_PART_1 + str + Constants.URL_GETWEATHER_PART_2 + Constants.URL_GETWEATHER_KEY;
        } else if (userInputType.equals(SEARCH_BY_ZIP)) {
            url = Constants.URL_GETWEATHER_ZIP_PART_1 + str + Constants.URL_GETWEATHER_PART_2 + Constants.URL_GETWEATHER_KEY;
        }

        SLogger.printLog(TAG, "url", url );
        Toast.makeText(getApplicationContext(), url, Toast.LENGTH_SHORT).show();
        new JsonTaskWeather().execute(url);
    }

    /**
     * Resets UI when there is no data available for requested URL
     */
    private void alertNotFound() {
        Toast.makeText(getApplicationContext(), "No data found", Toast.LENGTH_SHORT).show();
        textViewWeatherDetails.setText("");
        textViewWeatherTempMin.setText("");
        textViewWeatherTempMax.setText("");
        textViewWeatherLocalName.setText("");
        imageViewIcon.setImageResource(android.R.color.transparent);
    }


}
