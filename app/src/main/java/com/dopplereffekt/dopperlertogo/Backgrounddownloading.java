package com.dopplereffekt.dopperlertogo;
import android.app.ProgressDialog;
import android.app.Service;
import android.content.Intent;
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
import java.net.HttpURLConnection;
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
    public static String downloadwebsite = "http://stuxnet.bplaced.net/readfromdatabase.php?databasename=";
    List<android.location.Address> addresses;
    String[] lighterOptions = {"fixLighter", "radarfallen", "laserLighter", "controlePosition"};
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

        Log.d("thread", "im theread eingestiegen.");
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

        clearAllLists();

        new backgroundjsondownload().execute();

        Log.d("Backgrounddownloading", "onCreate");
        int delay = 0; // delay for 0 sec.
        int period = 1000 * 60 * 2; // repeat every 10 sec.
        Timer timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            public void run() {
                Log.d("thread", "run läuft " + i);
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

    public void clearExpliciteList(String listName){
        switch (listName)
        {
            case "fixLighter":
            {
                fixLighterAdresse.clear();
                fixLighterlat.clear();
                fixLighterlng.clear();
            }
            break;
            case "radarfallen":
            {
                mobileLighterAdresse.clear();
                mobileLighterlat.clear();
                mobileLighterlng.clear();
            }
            break;
            case "laserLighter":
            {
                laserLighterAdresse.clear();
                laserLighterlat.clear();
                laserLighterlng.clear();
            }
            break;
            case "controlePosition":
            {
                controlePositionAdresse.clear();
                controlePositionlat.clear();
                controlePositionlng.clear();
            }
            break;
            default:
            {
                Log.d("clearList", "Position konnte nicht gelöscht werden");
            }
            break;
        }
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
            readable = false;
            JSONArray jsonArray = null;
            JSONObject helpingObject = null;
            String url = "";
            String comment = "";
            double lat;
            double lng;

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
                                        fixLighterAdresse.add(addresses.get(0).getSubLocality() + " " + addresses.get(0).getPostalCode() + " " + addresses.get(0).getThoroughfare() + " : " + comment);
                                        fixLighterlat.add(lat);
                                        fixLighterlng.add(lng);
                                    }
                                    break;
                                    case "radarfallen": {

                                        mobileLighterAdresse.add(addresses.get(0).getSubLocality() + " " + addresses.get(0).getPostalCode() + " " + addresses.get(0).getThoroughfare() + " : " + comment);
                                        mobileLighterlat.add(lat);
                                        mobileLighterlng.add(lng);
                                    }
                                    break;
                                    case "laserLighter": {
                                        laserLighterAdresse.add(addresses.get(0).getSubLocality() + " " + addresses.get(0).getPostalCode() + " " + addresses.get(0).getThoroughfare() + " : " + comment);
                                        laserLighterlat.add(lat);
                                        laserLighterlng.add(lng);
                                    }
                                    break;
                                    case "controlePosition": {
                                        controlePositionAdresse.add(addresses.get(0).getSubLocality() + " " + addresses.get(0).getPostalCode() + " " + addresses.get(0).getThoroughfare() + " : " + comment);
                                        controlePositionlat.add(lat);
                                        controlePositionlng.add(lng);
                                    }
                                    break;
                                }
                            } else if (addresses.get(0).getSubLocality() == null) {
                                Log.d("outputgeo", addresses.get(0).getLocality() + " " + addresses.get(0).getPostalCode() + " " + addresses.get(0).getThoroughfare() + " : " + comment);


                                switch (lighterOptions[i]) {
                                    case "fixLighter": {
                                        fixLighterAdresse.add(addresses.get(0).getLocality() + " " + addresses.get(0).getPostalCode() + " " + addresses.get(0).getThoroughfare() + " : " + comment);
                                        fixLighterlat.add(lat);
                                        fixLighterlng.add(lng);
                                    }
                                    break;
                                    case "radarfallen": {
                                        mobileLighterAdresse.add(addresses.get(0).getLocality() + " " + addresses.get(0).getPostalCode() + " " + addresses.get(0).getThoroughfare() + " : " + comment);
                                        mobileLighterlat.add(lat);
                                        mobileLighterlng.add(lng);
                                    }
                                    break;
                                    case "laserLighter": {
                                        laserLighterAdresse.add(addresses.get(0).getLocality() + " " + addresses.get(0).getPostalCode() + " " + addresses.get(0).getThoroughfare() + " : " + comment);
                                        laserLighterlat.add(lat);
                                        laserLighterlng.add(lng);
                                    }
                                    break;
                                    case "controlePosition": {
                                        controlePositionAdresse.add(addresses.get(0).getLocality() + " " + addresses.get(0).getPostalCode() + " " + addresses.get(0).getThoroughfare() + " : " + comment);
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
