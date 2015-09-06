package com.dopplereffekt.dopperlertogo;

import android.app.Activity;
import android.app.DownloadManager;
import android.app.ListActivity;
import android.app.ProgressDialog;
import android.app.Service;
import android.content.Intent;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;

import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.ListAdapter;
import android.widget.SimpleAdapter;


import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;

import org.apache.http.ProtocolException;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import org.apache.http.params.CoreProtocolPNames;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;


/**
 * Created by dsantagata on 11.06.2015.
 */
public class Backgrounddownloading extends Service {
    int i = 0;

    HttpURLConnection urlConnection;
    // Progress Dialog
    private ProgressDialog pDialog;

    // Creating JSON Parser object
    JSONParser jParser = new JSONParser();

    ArrayList<HashMap<String, String>> productsList;

    // products JSONArray
    JSONArray products = null;

    int aktualisierungszyklus = 1000 * 15 * 60;    //immer in ms angeben 3600000ms = 1h
    public static String downloadwebsite = "http://dopplereffekt.freehostingking.com/readfromdatabase.php";
    public static String downloadwebsite2 = "http://dopplereffekt.freehostingking.com/readfromdatabase2.php";
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

        fixLighterlng = new ArrayList();
        mobileLighterlng = new ArrayList();
        laserLighterlng = new ArrayList();
        controlePositionlng = new ArrayList();

        fixLighterlat = new ArrayList();
        mobileLighterlat = new ArrayList();
        laserLighterlat = new ArrayList();
        controlePositionlat = new ArrayList();



        Log.d("Backgrounddownloading", "onCreate");
        int delay = 0; // delay for 0 sec.
        int period = 1000 * 60; // repeat every 10 sec.
        Timer timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            public void run() {
                Log.d("Backgrounddownloading", "run läuft " + i);
                // readInformation();
                new backgroundjsondownload().execute();

                i++;
            }
        }, delay, period);
    }


    @Override
    public IBinder onBind(Intent intent) {
        return null;
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

    private void readInformation() {
        double lat;
        double lng;
        String comment = null;
        JSONObject helpingObject = null;

        for (String option : lighterOptions) {
            List<NameValuePair> list = new ArrayList<NameValuePair>();
            list.add(new BasicNameValuePair("databasename", option));
            JSONObject json = jParser.makeHttpRequest(downloadwebsite, "GET", list);
            if (json != null) {
                Log.d("Backgrounddownloading", "json ist voll");
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






    /**
     * �ber die innere Klasse kann ich nicht viel sagen. Sie wird ben�tigt um Inhalte aus dem Web zu downloaden. Diese Klasse wird paralell ausgef�hrt.
     */
    private class backgroundjsondownload extends AsyncTask<String, Void, Void> {
        @Override
        protected void onPreExecute() {

        }

        @Override
        protected Void doInBackground(String... strings) {
            JSONArray jsonArray = null;
            String url = "http://stuxnet.bplaced.net/readfromdatabase.php?databasename=fixLighter";
            JSONObject helpingObject = null;
            jsonArray = JSONParser.getJson(url);
            String comment = "";
            double lat;
            double lng;

            if (jsonArray != null) {
                Log.d("async", "ja es gibt ein json : " + jsonArray.length());
                for (int i = 0; i<jsonArray.length(); i++)
                {
                    try {
                        helpingObject = jsonArray.getJSONObject(i);
                        lat = new Double(helpingObject.getString("latitude"));
                        lng = new Double(helpingObject.getString("longitude"));
                        comment = helpingObject.getString("comment");


                        Geocoder geo = new Geocoder(getApplicationContext(), Locale.getDefault());
                        addresses = geo.getFromLocation(lat, lng, 1);

                        Log.d("address", addresses.get(0).getSubLocality() + " " + addresses.get(0).getPostalCode() + " " + addresses.get(0).getThoroughfare() + " Kommentar: " + comment);
                        Log.d("address", lat + " : " + lng + " -->  " + comment);
                    }catch (IOException e){
                        e.printStackTrace();
                    }catch (JSONException j){
                        
                        j.printStackTrace();
                    }
                }

            }else{
                Log.d("async", "nein json ist leer");
            }
                        return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

        }

    }
}
