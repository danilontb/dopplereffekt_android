package com.dopplereffekt.dopperlertogo;
import android.app.ProgressDialog;
import android.app.Service;
import android.content.Intent;
import android.location.Geocoder;
import android.location.Location;
import android.os.AsyncTask;
import android.os.IBinder;
import android.util.Log;
import android.widget.TextView;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.util.Date;
import java.text.SimpleDateFormat;
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


    public static String updateTimeStamp = "wurde noch nicht übermittelt";
    public static String downloadwebsite = "http://stuxnet.bplaced.net/readfromdatabase.php?databasename=";
    List<android.location.Address> addresses;
    String[] lighterOptions = {"fixLighter", "radarfallen", "laserLighter", "controlePosition"};
    public static boolean readable = false;

    public static List fixLighterAdresse;
    public static List fixLighterAdrAndCom;
    public static List fixLightercomment;
    public static List mobileLighterAdresse;
    public static List mobileLighterAdrAndCom;
    public static List mobileLightercomment;
    public static List laserLighterAdresse;
    public static List laserLighterAdrAndCom;
    public static List laserLightercomment;
    public static List controlePositionAdresse;
    public static List controlePositionAdrAndCom;
    public static List controlePositioncomment;
    public static List fixLighterlng;
    public static List mobileLighterlng;
    public static List laserLighterlng;
    public static List controlePositionlng;
    public static List fixLighterlat;
    public static List mobileLighterlat;
    public static List laserLighterlat;
    public static List controlePositionlat;

    public static List officialLighterAdresse;
    public static List officialLighterLng;
    public static List officialLighterLat;

    @Override
    public void onCreate() {

        Log.d("thread", "im theread eingestiegen.");

        fixLighterAdrAndCom  = new ArrayList();
        mobileLighterAdrAndCom  = new ArrayList();
        laserLighterAdrAndCom  = new ArrayList();
        controlePositionAdrAndCom  = new ArrayList();

        fixLighterAdresse = new ArrayList();
        mobileLighterAdresse = new ArrayList();
        laserLighterAdresse = new ArrayList();
        controlePositionAdresse = new ArrayList();

        fixLightercomment = new ArrayList();
        mobileLightercomment = new ArrayList();
        laserLightercomment = new ArrayList();
        controlePositioncomment = new ArrayList();

        fixLighterlng = new ArrayList();
        mobileLighterlng = new ArrayList();
        laserLighterlng = new ArrayList();
        controlePositionlng = new ArrayList();

        fixLighterlat = new ArrayList();
        mobileLighterlat = new ArrayList();
        laserLighterlat = new ArrayList();
        controlePositionlat = new ArrayList();

        officialLighterAdresse = new ArrayList();
        officialLighterLng = new ArrayList();
        officialLighterLat = new ArrayList();

        clearAllLists();

        new backgroundjsondownload().execute();
        Log.d("Backgrounddownloading", "onCreate");

        int delay = 0; // delay for 0 sec.
        int period = 1000 * 60 * 2; // repeat every 10 sec.
        Timer timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            public void run() {
                new backgroundjsondownload().execute();
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
        officialLighterAdresse.clear();
        officialLighterLat.clear();
        officialLighterLng.clear();
    }

    public void clearExpliciteList(String listName){
        switch (listName)
        {
            case "officialLighter":
            {
                officialLighterAdresse.clear();
                officialLighterLat.clear();
                officialLighterLng.clear();
            }
            break;
            case "fixLighter":
            {
                fixLighterAdresse.clear();
                fixLighterlat.clear();
                fixLighterlng.clear();
                fixLightercomment.clear();
                fixLighterAdrAndCom.clear();
            }
            break;
            case "radarfallen":
            {
                mobileLighterAdresse.clear();
                mobileLighterlat.clear();
                mobileLighterlng.clear();
                mobileLightercomment.clear();
                mobileLighterAdrAndCom.clear();
            }
            break;
            case "laserLighter":
            {
                laserLighterAdresse.clear();
                laserLighterlat.clear();
                laserLighterlng.clear();
                laserLightercomment.clear();
                laserLighterAdrAndCom.clear();
            }
            break;
            case "controlePosition":
            {
                controlePositionAdresse.clear();
                controlePositionlat.clear();
                controlePositionlng.clear();
                controlePositioncomment.clear();
                controlePositionAdrAndCom.clear();
            }
            break;
            default:
            {
                Log.d("clearList", "Position konnte nicht gelöscht werden");
            }
            break;
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
            readable = false;
            JSONArray jsonArray = null;
            JSONObject helpingObject = null;
            String url = "";
            String comment = "";
            double lat;
            double lng;

            SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");
            updateTimeStamp = sdf.format(new Date());

            for(int i=0; i<lighterOptions.length; i++)
            {
                url = downloadwebsite + lighterOptions[i];
                jsonArray = JSONParser.getJson(url);

                clearExpliciteList(lighterOptions[i]);

                if (jsonArray != null) {
                    for (int j = 0; j < jsonArray.length(); j++) {
                        try {
                            helpingObject = jsonArray.getJSONObject(j);
                            lat = new Double(helpingObject.getString("latitude"));
                            lng = new Double(helpingObject.getString("longitude"));
                            comment = helpingObject.getString("comment");


                            Geocoder geo = new Geocoder(getApplicationContext(), Locale.getDefault());
                            addresses = geo.getFromLocation(lat, lng, 1);

                            Log.d("listcheck", "es wird was in die liste geschrieben mit dem switch : " + lighterOptions[i]);

                            if (addresses.get(0).getLocality() == null) {
                                switch (lighterOptions[i]) {
                                    case "fixLighter": {
                                        if(addresses.get(0).getPostalCode() == null && addresses.get(0).getThoroughfare()==null)
                                        {
                                            fixLighterAdresse.add(addresses.get(0).getSubLocality());
                                            fixLighterAdrAndCom.add(addresses.get(0).getSubLocality() + " : " +comment);
                                        }else if (addresses.get(0).getPostalCode() == null)
                                        {
                                            fixLighterAdresse.add(addresses.get(0).getSubLocality() + " " + addresses.get(0).getThoroughfare());
                                            fixLighterAdrAndCom.add(addresses.get(0).getSubLocality() + " " + addresses.get(0).getThoroughfare() + " : " +comment);
                                        }else if(addresses.get(0).getThoroughfare() == null)
                                        {
                                            fixLighterAdresse.add(addresses.get(0).getSubLocality() + " " + addresses.get(0).getPostalCode());
                                            fixLighterAdrAndCom.add(addresses.get(0).getSubLocality() + " " + addresses.get(0).getPostalCode() + " : " +comment);
                                        }else{
                                            fixLighterAdresse.add(addresses.get(0).getSubLocality() + " " + addresses.get(0).getPostalCode() + " " + addresses.get(0).getThoroughfare());
                                            fixLighterAdrAndCom.add(addresses.get(0).getSubLocality() + " " + addresses.get(0).getPostalCode() + " " + addresses.get(0).getThoroughfare() + " : " +comment);
                                        }
                                        fixLightercomment.add(comment);
                                        fixLighterlat.add(lat);
                                        fixLighterlng.add(lng);
                                    }
                                    break;
                                    case "radarfallen": {
                                        if(addresses.get(0).getPostalCode() == null && addresses.get(0).getThoroughfare()==null)
                                        {
                                            mobileLighterAdresse.add(addresses.get(0).getSubLocality());
                                            mobileLighterAdrAndCom.add(addresses.get(0).getSubLocality() + " : " + comment);
                                        }else if (addresses.get(0).getPostalCode() == null)
                                        {
                                            mobileLighterAdresse.add(addresses.get(0).getSubLocality() + " " + addresses.get(0).getThoroughfare());
                                            mobileLighterAdrAndCom.add(addresses.get(0).getSubLocality() + " " + addresses.get(0).getThoroughfare() + " : " + comment);
                                        }else if(addresses.get(0).getThoroughfare() == null)
                                        {
                                            mobileLighterAdresse.add(addresses.get(0).getSubLocality() + " " + addresses.get(0).getPostalCode());
                                            mobileLighterAdrAndCom.add(addresses.get(0).getSubLocality() + " " + addresses.get(0).getPostalCode() + " : " + comment);
                                        }else{
                                            mobileLighterAdresse.add(addresses.get(0).getSubLocality() + " " + addresses.get(0).getPostalCode() + " " + addresses.get(0).getThoroughfare());
                                            mobileLighterAdrAndCom.add(addresses.get(0).getSubLocality() + " " + addresses.get(0).getPostalCode() + " " + addresses.get(0).getThoroughfare() + " : " + comment);
                                        }
                                        mobileLightercomment.add(comment);
                                        mobileLighterlat.add(lat);
                                        mobileLighterlng.add(lng);
                                    }
                                    break;
                                    case "laserLighter": {
                                        if(addresses.get(0).getPostalCode() == null && addresses.get(0).getThoroughfare()==null)
                                        {
                                            laserLighterAdresse.add(addresses.get(0).getSubLocality());
                                            laserLighterAdrAndCom.add(addresses.get(0).getSubLocality() +" : "+comment);
                                        }else if (addresses.get(0).getPostalCode() == null)
                                        {
                                            laserLighterAdresse.add(addresses.get(0).getSubLocality() + " " + addresses.get(0).getThoroughfare());
                                            laserLighterAdrAndCom.add(addresses.get(0).getSubLocality() + " " + addresses.get(0).getThoroughfare() +" : "+comment);
                                        }else if(addresses.get(0).getThoroughfare() == null)
                                        {
                                            laserLighterAdresse.add(addresses.get(0).getSubLocality() + " " + addresses.get(0).getPostalCode());
                                            laserLighterAdrAndCom.add(addresses.get(0).getSubLocality() + " " + addresses.get(0).getPostalCode() +" : "+comment);
                                        }else{
                                            laserLighterAdresse.add(addresses.get(0).getSubLocality() + " " + addresses.get(0).getPostalCode() + " " + addresses.get(0).getThoroughfare());
                                            laserLighterAdrAndCom.add(addresses.get(0).getSubLocality() + " " + addresses.get(0).getPostalCode() + " " + addresses.get(0).getThoroughfare() +" : "+comment);
                                        }
                                        laserLightercomment.add(comment);
                                        laserLighterlat.add(lat);
                                        laserLighterlng.add(lng);
                                    }
                                    break;
                                    case "controlePosition": {
                                        if(addresses.get(0).getPostalCode() == null && addresses.get(0).getThoroughfare()==null)
                                        {
                                            controlePositionAdresse.add(addresses.get(0).getSubLocality());
                                            controlePositionAdrAndCom.add(addresses.get(0).getSubLocality() +" : "+comment);
                                        }else if (addresses.get(0).getPostalCode() == null)
                                        {
                                            controlePositionAdresse.add(addresses.get(0).getSubLocality() + " " + addresses.get(0).getThoroughfare());
                                            controlePositionAdrAndCom.add(addresses.get(0).getSubLocality() + " " + addresses.get(0).getThoroughfare() +" : "+comment);
                                        }else if(addresses.get(0).getThoroughfare() == null)
                                        {
                                            controlePositionAdresse.add(addresses.get(0).getSubLocality() + " " + addresses.get(0).getPostalCode());
                                            controlePositionAdrAndCom.add(addresses.get(0).getSubLocality() + " " + addresses.get(0).getPostalCode() +" : "+comment);
                                        }else{
                                            controlePositionAdresse.add(addresses.get(0).getSubLocality() + " " + addresses.get(0).getPostalCode() + " " + addresses.get(0).getThoroughfare());
                                            controlePositionAdrAndCom.add(addresses.get(0).getSubLocality() + " " + addresses.get(0).getPostalCode() + " " + addresses.get(0).getThoroughfare() +" : "+comment);
                                        }
                                        controlePositioncomment.add(comment);
                                        controlePositionlat.add(lat);
                                        controlePositionlng.add(lng);
                                    }
                                    break;
                                }
                            } else if (addresses.get(0).getSubLocality() == null) {
                                switch (lighterOptions[i]) {
                                    case "fixLighter": {
                                        if(addresses.get(0).getPostalCode() == null && addresses.get(0).getThoroughfare()==null)
                                        {
                                            fixLighterAdresse.add(addresses.get(0).getLocality());
                                            fixLighterAdrAndCom.add(addresses.get(0).getLocality() +" : "+comment);
                                        }else if (addresses.get(0).getPostalCode() == null)
                                        {
                                            fixLighterAdresse.add(addresses.get(0).getLocality() + " " + addresses.get(0).getThoroughfare());
                                            fixLighterAdrAndCom.add(addresses.get(0).getLocality() + " " + addresses.get(0).getThoroughfare() +" : "+comment);
                                        }else if(addresses.get(0).getThoroughfare() == null)
                                        {
                                            fixLighterAdresse.add(addresses.get(0).getLocality() + " " + addresses.get(0).getPostalCode());
                                            fixLighterAdrAndCom.add(addresses.get(0).getLocality() + " " + addresses.get(0).getPostalCode() +" : "+comment);
                                        }else{
                                            fixLighterAdresse.add(addresses.get(0).getLocality() + " " + addresses.get(0).getPostalCode() + " " + addresses.get(0).getThoroughfare());
                                            fixLighterAdrAndCom.add(addresses.get(0).getLocality() + " " + addresses.get(0).getPostalCode() + " " + addresses.get(0).getThoroughfare() +" : "+comment);
                                        }
                                        fixLightercomment.add(comment);
                                        fixLighterlat.add(lat);
                                        fixLighterlng.add(lng);
                                    }
                                    break;
                                    case "radarfallen": {
                                        if(addresses.get(0).getPostalCode() == null && addresses.get(0).getThoroughfare()==null)
                                        {
                                            mobileLighterAdresse.add(addresses.get(0).getLocality());
                                            mobileLighterAdrAndCom.add(addresses.get(0).getLocality() +" : "+comment);
                                        }else if (addresses.get(0).getPostalCode() == null)
                                        {
                                            mobileLighterAdresse.add(addresses.get(0).getLocality() + " " + addresses.get(0).getThoroughfare());
                                            mobileLighterAdrAndCom.add(addresses.get(0).getLocality() + " " + addresses.get(0).getThoroughfare() +" : "+comment);
                                        }else if(addresses.get(0).getThoroughfare() == null)
                                        {
                                            mobileLighterAdresse.add(addresses.get(0).getLocality() + " " + addresses.get(0).getPostalCode());
                                            mobileLighterAdrAndCom.add(addresses.get(0).getLocality() + " " + addresses.get(0).getPostalCode() +" : "+comment);
                                        }else{
                                            mobileLighterAdresse.add(addresses.get(0).getLocality() + " " + addresses.get(0).getPostalCode() + " " + addresses.get(0).getThoroughfare());
                                            mobileLighterAdrAndCom.add(addresses.get(0).getLocality() + " " + addresses.get(0).getPostalCode() + " " + addresses.get(0).getThoroughfare() +" : "+comment);
                                        }
                                        mobileLightercomment.add(comment);
                                        mobileLighterlat.add(lat);
                                        mobileLighterlng.add(lng);
                                    }
                                    break;
                                    case "laserLighter": {
                                        if(addresses.get(0).getPostalCode() == null && addresses.get(0).getThoroughfare()==null)
                                        {
                                            laserLighterAdresse.add(addresses.get(0).getLocality());
                                            laserLighterAdrAndCom.add(addresses.get(0).getLocality() +" : "+comment);
                                        }else if (addresses.get(0).getPostalCode() == null)
                                        {
                                            laserLighterAdresse.add(addresses.get(0).getLocality() + " " + addresses.get(0).getThoroughfare());
                                            laserLighterAdrAndCom.add(addresses.get(0).getLocality() + " " + addresses.get(0).getThoroughfare() +" : "+comment);
                                        }else if(addresses.get(0).getThoroughfare() == null)
                                        {
                                            laserLighterAdresse.add(addresses.get(0).getLocality() + " " + addresses.get(0).getPostalCode());
                                            laserLighterAdrAndCom.add(addresses.get(0).getLocality() + " " + addresses.get(0).getPostalCode() +" : "+comment);
                                        }else{
                                            laserLighterAdresse.add(addresses.get(0).getLocality() + " " + addresses.get(0).getPostalCode() + " " + addresses.get(0).getThoroughfare());
                                            laserLighterAdrAndCom.add(addresses.get(0).getLocality() + " " + addresses.get(0).getPostalCode() + " " + addresses.get(0).getThoroughfare() +" : "+comment);
                                        }
                                        laserLightercomment.add(comment);
                                        laserLighterlat.add(lat);
                                        laserLighterlng.add(lng);
                                    }
                                    break;
                                    case "controlePosition": {
                                        if(addresses.get(0).getPostalCode() == null && addresses.get(0).getThoroughfare()==null)
                                        {
                                            controlePositionAdresse.add(addresses.get(0).getLocality());
                                            controlePositionAdrAndCom.add(addresses.get(0).getLocality() +" : "+comment);
                                        }else if (addresses.get(0).getPostalCode() == null)
                                        {
                                            controlePositionAdresse.add(addresses.get(0).getLocality() + " " + addresses.get(0).getThoroughfare());
                                            controlePositionAdrAndCom.add(addresses.get(0).getLocality() + " " + addresses.get(0).getThoroughfare() +" : "+comment);
                                        }else if(addresses.get(0).getThoroughfare() == null)
                                        {
                                            controlePositionAdresse.add(addresses.get(0).getLocality() + " " + addresses.get(0).getPostalCode());
                                            controlePositionAdrAndCom.add(addresses.get(0).getLocality() + " " + addresses.get(0).getPostalCode() +" : "+comment);
                                        }else{
                                            controlePositionAdresse.add(addresses.get(0).getLocality() + " " + addresses.get(0).getPostalCode() + " " + addresses.get(0).getThoroughfare());
                                            controlePositionAdrAndCom.add(addresses.get(0).getLocality() + " " + addresses.get(0).getPostalCode() + " " + addresses.get(0).getThoroughfare() +" : "+comment);
                                        }
                                        controlePositioncomment.add(comment);
                                        controlePositionlat.add(lat);
                                        controlePositionlng.add(lng);
                                    }
                                    break;
                                }
                            } else {
                                Log.d("problem", "die adresse war leer und wird somit abgebrochen");
                            }

                        } catch (IOException e) {
                            e.printStackTrace();
                            Log.e("jsonfehler", "etwas ging schief mit " + lighterOptions[i] + " " + e.toString());
                        } catch (JSONException k) {
                            k.printStackTrace();
                            Log.e("jsonfehler", "etwas ging schief mit " + lighterOptions[i] + " " + k.toString());
                        }
                    }

                } else {
                    Log.d("Jsontest", "nein json ist leer");
                }
            }
            readable = true;
                        return null;
        }


        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

        }

    }
}
