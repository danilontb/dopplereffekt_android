package com.dopplereffekt.dopperlertogo;


import android.app.ProgressDialog;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.os.IBinder;
import android.util.Log;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

;

/**
 * Created by dsantagata on 11.06.2015.
 */
public class Backgrounddownloading extends Service {

    private ProgressDialog pDialog;
    JSONParser jParser = new JSONParser();
    String downloadwebsite = "http://dopplereffekt.freehostingking.com/readfromdatabase.php";
    String getNumberOfEntity = "http://dopplereffekt.freehostingking.com/getnumberofentity.php";
    List<android.location.Address> addresses;
    String[] lighterOptions = {"fixLighter", "mobileLighter", "laserLighter", "controlePosition"};


    @Override
    public void onCreate() {
        Log.d("Backgrounddownloading", "oncreate");
        Timer timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            public void run() {

                try {

                    new readInformation().execute();
                    Log.d("Backgrounddownloading", "async wurde auger");
                    //write lighterposition to external File.


                    writePostions(MainActivity.fixLighterAdresse, "fixLighterAdresse");
                    writePostions(MainActivity.mobileLighterAdresse, "mobileLighterAdresse");
                    writePostions(MainActivity.laserLighterAdresse, "laserLighterAdresse");
                    writePostions(MainActivity.controlePositionAdresse, "controlePositionAdresse");

                } catch (Exception e) {
                    // TODO: handle exception
                }

            }
        }, 0, 15000);
    }


    public void writePostions(String[] array, String arrayName) {
        SharedPreferences settings = getSharedPreferences(MainActivity.PREFS_NAME, getApplicationContext().MODE_PRIVATE);
        SharedPreferences.Editor editor = settings.edit();
        editor.putInt(arrayName + "_size", array.length);
        for (int i = 0; i < array.length; i++)
            editor.putString(arrayName + "_" + i, array[i]);
        editor.commit();
    }


    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    @Override
    public void onDestroy() {
        Log.d("Backgrounddownloading", "service stopp");
    }


    /**
     * Background Async Task to Create new product
     */
    class readInformation extends AsyncTask<String, String, String> {

        double lat;
        double lng;
        String comment = null;

        /**
         * Read positions
         */
        protected String doInBackground(String... args) {

            for (String option : lighterOptions) {
                List<NameValuePair> list = new ArrayList<NameValuePair>();
                list.add(new BasicNameValuePair("databasename", option));
                JSONObject json = jParser.makeHttpRequest(downloadwebsite, "POST", list);
                if (json != null) {
                    // Check your log cat for JSON reponse
                    //  Log.d("Backgrounddownloading", json.toString());


                    try {
                        //array werden mit anzahl Blitzer bestückt.
                        int anzahl = json.getInt("anzLighter");
                        switch (option) {
                            case "fixLighter":
                                MainActivity.fixLighterAdresse = new String[anzahl];
                                break;
                            case "mobileLighter":
                                MainActivity.mobileLighterAdresse = new String[anzahl];
                                break;
                            case "laserLighter":
                                MainActivity.laserLighterAdresse = new String[anzahl];
                                break;
                            case "controlePosition":
                                MainActivity.controlePositionAdresse = new String[anzahl];
                                break;
                        }

                        JSONArray jsonArray = json.getJSONArray(option);
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject helpingObject = jsonArray.getJSONObject(i);
                            lat = new Double(helpingObject.getString("latitude"));
                            lng = new Double(helpingObject.getString("longitude"));
                            comment = helpingObject.getString("comment");


                            Geocoder geo = new Geocoder(getApplicationContext(), Locale.getDefault());
                            addresses = geo.getFromLocation(lat, lng, 1);

                            if (addresses.get(0).getLocality() == null) {
                                switch (option) {
                                    case "fixLighter": {
                                        MainActivity.fixLighterAdresse[i] = addresses.get(0).getSubLocality() + " " + addresses.get(0).getPostalCode() + " " + addresses.get(0).getThoroughfare() + " Kommentar: " + comment;
                                    }
                                    break;
                                    case "mobileLighter": {
                                        MainActivity.mobileLighterAdresse[i] = addresses.get(0).getSubLocality() + " " + addresses.get(0).getPostalCode() + " " + addresses.get(0).getThoroughfare() + " Kommentar: " + comment;
                                    }
                                    break;
                                    case "laserLighter": {
                                        MainActivity.laserLighterAdresse[i] = addresses.get(0).getSubLocality() + " " + addresses.get(0).getPostalCode() + " " + addresses.get(0).getThoroughfare() + " Kommentar: " + comment;
                                    }
                                    break;
                                    case "controlePosition": {
                                        MainActivity.controlePositionAdresse[i] = addresses.get(0).getSubLocality() + " " + addresses.get(0).getPostalCode() + " " + addresses.get(0).getThoroughfare() + " Kommentar: " + comment;
                                    }
                                    break;
                                }
                            } else if (addresses.get(0).getSubLocality() == null) {
                                switch (option) {
                                    case "fixLighter": {
                                        MainActivity.fixLighterAdresse[i] = addresses.get(0).getLocality() + " " + addresses.get(0).getPostalCode() + " " + addresses.get(0).getThoroughfare() + " Kommentar: " + comment;
                                    }
                                    break;
                                    case "mobileLighter": {
                                        MainActivity.mobileLighterAdresse[i] = addresses.get(0).getLocality() + " " + addresses.get(0).getPostalCode() + " " + addresses.get(0).getThoroughfare() + " Kommentar: " + comment;
                                    }
                                    break;
                                    case "laserLighter": {
                                        MainActivity.laserLighterAdresse[i] = addresses.get(0).getLocality() + " " + addresses.get(0).getPostalCode() + " " + addresses.get(0).getThoroughfare() + " Kommentar: " + comment;
                                    }
                                    break;
                                    case "controlePosition": {
                                        MainActivity.controlePositionAdresse[i] = addresses.get(0).getLocality() + " " + addresses.get(0).getPostalCode() + " " + addresses.get(0).getThoroughfare() + " Kommentar: " + comment;
                                    }
                                    break;
                                }
                            }


                            Log.d("Backgrounddownloading", option + " anzahl : " + anzahl);
                            Log.d("Backgrounddownloading", lat + " , " + lng + " , " + comment);


                        }
                    } catch (JSONException | IOException e) {

                    }


                } else {
                    Log.d("Backgrounddownloading", "json ist leer");
                }

            }
            return null;
        }


    }
}
