package com.dopplereffekt.dopperlertogo;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;


import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import java.io.IOException;
import java.sql.Date;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

/**
 * Created by dsantagata on 07.06.2015.
 */
public class WriteEvent2DB extends Activity {

    private ProgressDialog pDialog;

    String eventoption      = null;
    String eventlongitude   = null;
    String eventlatitude    = null;
    String eventcomment     = null;
    String eventminutes     = null;
    String eventhour        = null;
    String eventday         = null;
    String eventmonth       = null;
    String website          = "http://dopplereffekt.freehostingking.com/writetodatabase.php";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        final Bundle extras = getIntent().getExtras();

        Log.d("website", "extras erhalten");

        Calendar calendar = GregorianCalendar.getInstance();
        eventday = calendar.get(Calendar.DAY_OF_YEAR) + "";
        eventminutes = calendar.get(Calendar.MINUTE)+ "";
        eventmonth = calendar.get(Calendar.MONTH)+ "";
        eventhour = calendar.get(Calendar.HOUR_OF_DAY)+ "";

        eventoption = extras.getString("eventoption");
        eventlongitude = extras.getString("eventlongitude");
        eventlatitude = extras.getString("eventlatitude");
        eventcomment = extras.getString("eventcomment");

        Log.d("showittome", eventlongitude + " : " + eventlatitude + " um das gehts bitches" );

        Log.d("website", "eventoption :" + eventoption);
        Log.d("website", "eventlng :" + eventlongitude);
        Log.d("website", "eventlat :" + eventlatitude);
        Log.d("website", "eventcomment :" + eventcomment);

        switch (extras.getString("eventoption")) {
            case "Fester Blitzer": {
                eventoption = "fixlighter";
            }
            break;
            case "Mobiler Blitzer": {
                eventoption = "mobilelighter";
            }
            break;
            case "Laser am Strassenrand": {
                eventoption = "laserlighter";
            }
            break;
            case "Verkehrskontrolle": {
                eventoption = "controlposition";
            }
            break;
            default: {
                eventoption = null;
            }
            break;
        }



        if((eventlatitude==null) && (eventlongitude==null)){
            Toast.makeText(this, "Dein GPS modul ist noch nicht bereit", Toast.LENGTH_SHORT).show();

        }else {
            new WriteNewEvent2DB().execute();
            setContentView(R.layout.lighterwhilewriting);
        }

finish();finish();
     // startActivity(new Intent(this, MainActivity.class));
    }

    /**
     * Background Async Task to Create new product
     */
    class WriteNewEvent2DB extends AsyncTask<String, String, String> {

        /**
         * Before starting background thread Show Progress Dialog
         */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(WriteEvent2DB.this);
            pDialog.setMessage("Meldung wird in Datenbank aufgenommen.");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
        }


        /**
         * Read positions
         */
        protected String doInBackground(String... args) {
            // Create a new HttpClien t and Post Header
            HttpClient httpclient = new DefaultHttpClient();
            HttpPost httppost = new HttpPost(website);

            try {

                // Add your data
                List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
                nameValuePairs.add(new BasicNameValuePair("longitude", eventlongitude));
                nameValuePairs.add(new BasicNameValuePair("latitude", eventlatitude));
                nameValuePairs.add(new BasicNameValuePair("comment", eventcomment));
                nameValuePairs.add(new BasicNameValuePair("day", eventday));
                nameValuePairs.add(new BasicNameValuePair("month", eventmonth));
                nameValuePairs.add(new BasicNameValuePair("hour", eventhour));
                nameValuePairs.add(new BasicNameValuePair("minutes", eventminutes));
                nameValuePairs.add(new BasicNameValuePair("eventoption", eventoption));


                httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

                // Execute HTTP Post Request
                HttpResponse response = httpclient.execute(httppost);

                // TODO Auto-generated catch block
            } catch (IOException e) {
            }


            return null;
        }



        /**
         * After completing background task Dismiss the progress dialog
         * *
         */
        protected void onPostExecute(String file_url) {


            // dismiss the dialog once done
            pDialog.dismiss();
            finish();
        }

    }
}
