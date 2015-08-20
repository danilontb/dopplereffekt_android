package com.dopplereffekt.dopperlertogo;


import android.app.Service;
import android.content.Intent;

import android.location.Geocoder;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;

;

/**
 * Created by dsantagata on 11.06.2015.
 */
public class Backgrounddownloading extends Service {

    JSONParser jParser = new JSONParser();
    int aktualisierungszyklus = 1000*15*60;    //immer in ms angeben 3600000ms = 1h
    String downloadwebsite = "http://dopplereffekt.freehostingking.com/readfromdatabase.php";
    String getNumberOfEntity = "http://dopplereffekt.freehostingking.com/getnumberofentity.php";
    List<android.location.Address> addresses;
    String[] lighterOptions = {"fixLighter", "mobileLighter", "laserLighter", "controlePosition"};
    public static boolean readable = false;

    public static List fixLighterAdresse;
    public static List mobileLighterAdresse;
    public static List laserLighterAdresse;
    public static List controlePositionAdresse;
    public static List fixLighterlng;
    public static List mobileLighterlng;
    public static List laserLighterlng;
    public static List controlePositionlng;
    public static List fixLighterlat;
    public static List mobileLighterlat;
    public static List laserLighterlat;
    public static List controlePositionlat;

    @Override
    public void onCreate() {
        fixLighterAdresse = new ArrayList();
        mobileLighterAdresse = new ArrayList();
        laserLighterAdresse = new ArrayList();
        controlePositionAdresse = new ArrayList();

        fixLighterlng= new ArrayList();
        mobileLighterlng = new ArrayList();
        laserLighterlng = new ArrayList();
        controlePositionlng = new ArrayList();

        fixLighterlat = new ArrayList();
        mobileLighterlat = new ArrayList();
        laserLighterlat = new ArrayList();
        controlePositionlat = new ArrayList();


        Log.d("Backgrounddownloading", "oncreate");
        Timer timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            public void run() {
                clearAllLists();
                try {
                    readable = false;
                    readInformation();
                    Log.d("outputgeo", "reading is done");
                  readable = true;

                    Log.d("Backgrounddownloading", "async wurde auger");
                    //write lighterposition to external File.

                } catch (Exception e) {
                    // TODO: handle exception
                    Log.d("readinformation", "anything is going wrong");
                    Log.e("readinformation", e.toString());
                }

            }
        }, 0, aktualisierungszyklus);
    }


    private void clearAllLists() {
        fixLighterAdresse.clear();
        mobileLighterAdresse.clear();
        laserLighterAdresse.clear();
        controlePositionAdresse.clear();
        fixLighterlng.clear();
        mobileLighterlng.clear();
        laserLighterlng.clear();
        controlePositionlng.clear();
        fixLighterlat.clear();
        mobileLighterlat.clear();
        laserLighterlat.clear();
        controlePositionlat.clear();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    @Override
    public void onDestroy() {
        Log.d("Backgrounddownloading", "service stopp");
    }


    private void readInformation() {
        double lat;
        double lng;
        String comment = null;
        JSONObject helpingObject = null;

        for (String option : lighterOptions) {
            List<NameValuePair> list = new ArrayList<NameValuePair>();
            list.add(new BasicNameValuePair("databasename", option));
            JSONObject json = jParser.makeHttpRequest(downloadwebsite, "POST", list);
            if (json != null) {

                try {
                    JSONArray jsonArray = json.getJSONArray(option);
                    for (int i = 0; i < jsonArray.length(); i++) {
                        helpingObject = jsonArray.getJSONObject(i);
                        lat = new Double(helpingObject.getString("latitude"));
                        lng = new Double(helpingObject.getString("longitude"));
                        comment = helpingObject.getString("comment");


                        Geocoder geo = new Geocoder(getApplicationContext(), Locale.getDefault());
                        addresses = geo.getFromLocation(lat, lng, 1);


                        if (addresses.get(0).getLocality() == null) {
                            switch (option) {
                                case "fixLighter": {
                                    fixLighterAdresse.add(addresses.get(0).getSubLocality() + " " + addresses.get(0).getPostalCode() + " " + addresses.get(0).getThoroughfare() + " Kommentar: " + comment);
                                    fixLighterlat.add(lat);
                                    fixLighterlng.add(lng);
                                }
                                break;
                                case "mobileLighter": {
                                    mobileLighterAdresse.add(addresses.get(0).getSubLocality() + " " + addresses.get(0).getPostalCode() + " " + addresses.get(0).getThoroughfare() + " Kommentar: " + comment);
                                    mobileLighterlat.add(lat);
                                    mobileLighterlng.add(lng);
                                }
                                break;
                                case "laserLighter": {
                                    laserLighterAdresse.add(addresses.get(0).getSubLocality() + " " + addresses.get(0).getPostalCode() + " " + addresses.get(0).getThoroughfare() + " Kommentar: " + comment);
                                    laserLighterlat.add(lat);
                                    laserLighterlng.add(lng);
                                }
                                break;
                                case "controlePosition": {
                                    controlePositionAdresse.add(addresses.get(0).getSubLocality() + " " + addresses.get(0).getPostalCode() + " " + addresses.get(0).getThoroughfare() + " Kommentar: " + comment);
                                    controlePositionlat.add(lat);
                                    controlePositionlng.add(lng);
                                }
                                break;
                            }
                        } else if (addresses.get(0).getSubLocality() == null) {
                            Log.d("outputgeo", addresses.get(0).getLocality() + " " + addresses.get(0).getPostalCode() + " " + addresses.get(0).getThoroughfare() + " Kommentar: " + comment);


                            switch (option) {
                                case "fixLighter": {
                                    fixLighterAdresse.add(addresses.get(0).getLocality() + " " + addresses.get(0).getPostalCode() + " " + addresses.get(0).getThoroughfare() + " Kommentar: " + comment);
                                }
                                break;
                                case "mobileLighter": {
                                    mobileLighterAdresse.add(addresses.get(0).getLocality() + " " + addresses.get(0).getPostalCode() + " " + addresses.get(0).getThoroughfare() + " Kommentar: " + comment);
                                }
                                break;
                                case "laserLighter": {
                                    laserLighterAdresse.add(addresses.get(0).getLocality() + " " + addresses.get(0).getPostalCode() + " " + addresses.get(0).getThoroughfare() + " Kommentar: " + comment);
                                }
                                break;
                                case "controlePosition": {
                                    controlePositionAdresse.add(addresses.get(0).getLocality() + " " + addresses.get(0).getPostalCode() + " " + addresses.get(0).getThoroughfare() + " Kommentar: " + comment);
                                }
                                break;
                            }
                        } else {
                            Log.d("problem", "die adresse war leer und wird somit abgebrochen");
                        }
                    }
                } catch (JSONException | IOException e) {

                }
            } else {
                Log.d("Backgrounddownloading", "json ist leer");
            }

        }
    }

}
