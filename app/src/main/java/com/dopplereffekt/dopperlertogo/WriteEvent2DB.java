package com.dopplereffekt.dopperlertogo;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;
import java.io.OutputStreamWriter;
import org.apache.http.NameValuePair;

import org.apache.http.message.BasicNameValuePair;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

/**
 * Created by dsantagata on 07.06.2015.
 */
public class WriteEvent2DB extends Activity {

    private ProgressDialog pDialog;

    String eventoption = null;
    String eventlongitude = null;
    String eventlatitude = null;
    String eventcomment = null;
    String eventtime = null;
    String eventdate = null;
    String website = "http://stuxnet.bplaced.net/writetodatabase.php";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        final Bundle extras = getIntent().getExtras();

        Log.d("website", "extras erhalten");

        Calendar calendar = GregorianCalendar.getInstance();
        if (calendar.get(Calendar.MINUTE) < 10) {
            eventtime = calendar.get(Calendar.HOUR) + ":0" + calendar.get(Calendar.MINUTE);
        } else {
            eventtime = calendar.get(Calendar.HOUR) + ":" + calendar.get(Calendar.MINUTE);
        }

        eventdate = calendar.get(Calendar.DAY_OF_MONTH) + "/" + (calendar.get(Calendar.MONTH) + 1) + "/" + calendar.get(Calendar.YEAR);

        eventoption = extras.getString("eventoption");
        eventlongitude = extras.getString("eventlongitude");
        eventlatitude = extras.getString("eventlatitude");
        eventcomment = extras.getString("eventcomment");

        Log.d("showittome", eventlongitude + " : " + eventlatitude + " um das gehts bitches");

        Log.d("website", "eventoption :" + eventoption);
        Log.d("website", "eventlng :" + eventlongitude);
        Log.d("website", "eventlat :" + eventlatitude);
        Log.d("website", "eventcomment :" + eventcomment);

        switch (extras.getString("eventoption")) {
            case "Fester Blitzer": {
                eventoption = "fixLighter";
            }
            break;
            case "Mobiler Blitzer": {
                eventoption = "LighterMobile";
            }
            break;
            case "Laser am Strassenrand": {
                eventoption = "laserLighter";
            }
            break;
            case "Verkehrskontrolle": {
                eventoption = "controlPosition";
            }
            break;
            default: {
                eventoption = null;
            }
            break;
        }


        if ((eventlatitude == null) || (eventlongitude == null)) {
            Toast.makeText(this, "Dein GPS modul ist noch nicht bereit", Toast.LENGTH_SHORT).show();
        } else {
            new WriteNewEvent2DB().execute();
            setContentView(R.layout.lighterwhilewriting);
        }

        finish();
        // startActivity(new Intent(this, MainActivity.class));
    }

    private String getQuery(List<NameValuePair> params) throws UnsupportedEncodingException {
        StringBuilder result = new StringBuilder();
        boolean first = true;

        for (NameValuePair pair : params)
        {
            if (first) {
                first = false;
            }else {
                result.append("&");
            }

            result.append(URLEncoder.encode(pair.getName(), "UTF-8"));
            result.append("=");
            result.append(URLEncoder.encode(pair.getValue(), "UTF-8"));
        }

        return result.toString();
    }


    /**
     * Background Async Task to Create new product
     */
    class WriteNewEvent2DB extends AsyncTask<String, String, String> {

        /**
         * Before starting background thread Show Progress Dialog
         */
   /*     @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(WriteEvent2DB.this);
            pDialog.setMessage("Meldung wird in Datenbank aufgenommen.");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
        }
*/

        /**
         * Read positions
         */
        protected String doInBackground(String... args) {
            // Create a new HttpClien t and Post Header
            HttpURLConnection connection;
            OutputStreamWriter request = null;
            URL url = null;
            String response = null;
            String parameters = "";

            List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
            nameValuePairs.add(new BasicNameValuePair("longitude", eventlongitude));
            nameValuePairs.add(new BasicNameValuePair("latitude", eventlatitude));
            nameValuePairs.add(new BasicNameValuePair("comment", eventcomment));
            nameValuePairs.add(new BasicNameValuePair("time", eventtime));
            nameValuePairs.add(new BasicNameValuePair("date", eventdate));
            nameValuePairs.add(new BasicNameValuePair("eventoption", eventoption));


            try
            {
                url = new URL(website);
                connection = (HttpURLConnection) url.openConnection();
                connection.setDoOutput(true);
                connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                connection.setRequestMethod("POST");

                request = new OutputStreamWriter(connection.getOutputStream());


                parameters = getQuery(nameValuePairs);

                Log.d("website parameters : " , parameters);
                request.write(parameters);
                request.flush();
                request.close();
                String line = "";
                InputStreamReader isr = new InputStreamReader(connection.getInputStream());
                BufferedReader reader = new BufferedReader(isr);
                StringBuilder sb = new StringBuilder();
                while ((line = reader.readLine()) != null)
                {
                    sb.append(line + "\n");
                }
                // Response from server after login process will be stored in response variable.
                response = sb.toString();
                // You can perform UI operations here

                Log.d("website", response);
                isr.close();
                reader.close();

            }
            catch(IOException e)
            {
                // Error
            }

          /*  try {

                // Add your data
                List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
                nameValuePairs.add(new BasicNameValuePair("longitude", eventlongitude));
                nameValuePairs.add(new BasicNameValuePair("latitude", eventlatitude));
                nameValuePairs.add(new BasicNameValuePair("comment", eventcomment));
                nameValuePairs.add(new BasicNameValuePair("time", eventtime));
                nameValuePairs.add(new BasicNameValuePair("date", eventdate));
                nameValuePairs.add(new BasicNameValuePair("eventoption", eventoption));


                Log.d("website", "option :" + eventoption);
                Log.d("website", "longitude :" + eventlongitude);
                Log.d("website", "latitude :" + eventlatitude);
                Log.d("website", "comment :" + eventcomment);
                Log.d("website", "time :" + eventtime);
                Log.d("website", "date :" + eventdate);

                httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));



                // Execute HTTP Post Request
                HttpResponse response = httpclient.execute(httppost);
                String json = EntityUtils.toString(response.getEntity());
                Log.d("writetodatabase", json.toString());
                try {
                    JSONObject jsonObject = new JSONObject(json);
                    if(jsonObject.getInt("success")==1 )
                    {
                        Log.d("writetodatabase", "erfolgreich eingetragen");
                    }else
                    {
                        Log.d("writetodatabase", "eintragung fehlgeschlagen");
                    }
                } catch (JSONException e) {
                        Log.d("writetodatabase", "Json konnte nicht erstellt werden");
                }

                // TODO Auto-generated catch block
            } catch (IOException e) {
            }
*/

            return null;
        }


        /**
         * After completing background task Dismiss the progress dialog
         * *
         */
  /*      protected void onPostExecute(String file_url) {


            // dismiss the dialog once done
            pDialog.dismiss();

        }
*/
    }
}
