package com.dopplereffekt.dopperlertogo;


import android.app.Fragment;
import android.app.FragmentManager;
import android.app.ProgressDialog;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Toast;

import org.apache.http.NameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Created by dsantagata on 31.05.2015.
 */
public class showLighterListFragment extends Fragment {
    // public WarningFragment(){}
    private ProgressDialog pDialog1;
    private ProgressDialog pDialog;
    ListView publicLighterlist;
    ListView fixLighterList;
    ListView mobileLighterList;
    ListView laserLigterList;
    ListView controlePostitionListView;

   /* String[] fixLighterAdresse = MainActivity.fixLighterAdresse;
    String[] mobileLighterAdresse = MainActivity.mobileLighterAdresse;
    String[] laserLighterAdresse = MainActivity.laserLighterAdresse;
    String[] controlePositionAdresse = MainActivity.controlePositionAdresse;*/
    String[] placeHolderArray = {"leer", "leer", "leer", "leer", "leer", "leer", "leer", "leer"};

    List<android.location.Address> addresses;

    public boolean flag = false;
    JSONParser jParser = new JSONParser();
    // products JSONArray
    JSONArray products = null;


    String downloadwebsite = "http://dopplereffekt.freehostingking.com/readfromdatabase.php";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, final Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.showlighterlist_fragment, container, false);

        setHasOptionsMenu(true);

        publicLighterlist = (ListView) rootView.findViewById(R.id.publiclighterlist);
        fixLighterList = (ListView) rootView.findViewById(R.id.fixlighterlist);
        mobileLighterList = (ListView) rootView.findViewById(R.id.mobilelighterlist);
        laserLigterList = (ListView) rootView.findViewById(R.id.laserlighterlist);


        publicLighterlist.setAdapter(new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, ConvertPDF.pdf2AdressArray()));
        publicLighterlist.setBackgroundColor(getResources().getColor(R.color.listbackground_public));

        fixLighterList.setAdapter(new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, MainActivity.fixLighterAdresse));
        fixLighterList.setBackgroundColor(getResources().getColor(R.color.listbachground_fixlighter));

        mobileLighterList.setAdapter(new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, MainActivity.mobileLighterAdresse));
        mobileLighterList.setBackgroundColor(getResources().getColor(R.color.listbachground_mobilelighter));

        laserLigterList.setAdapter(new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, MainActivity.laserLighterAdresse));
        laserLigterList.setBackgroundColor(getResources().getColor(R.color.listbachground_laserlighter));


        setDynamicHeight(publicLighterlist);
        setDynamicHeight(fixLighterList);
        setDynamicHeight(mobileLighterList);
        setDynamicHeight(laserLigterList);


        return rootView;
    }


    public static void setDynamicHeight(ListView mListView) {
        ListAdapter mListAdapter = mListView.getAdapter();
        if (mListAdapter == null) {
            // when adapter is null
            return;
        }
        int height = 0;
        int desiredWidth = View.MeasureSpec.makeMeasureSpec(mListView.getWidth(), View.MeasureSpec.UNSPECIFIED);
        for (int i = 0; i < mListAdapter.getCount(); i++) {
            View listItem = mListAdapter.getView(i, null, mListView);
            listItem.measure(desiredWidth, View.MeasureSpec.UNSPECIFIED);
            height += listItem.getMeasuredHeight();
        }
        ViewGroup.LayoutParams params = mListView.getLayoutParams();
        params.height = height + (mListView.getDividerHeight() * (mListAdapter.getCount() - 1));
        mListView.setLayoutParams(params);
        mListView.requestLayout();
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        menu.clear();
        inflater.inflate(R.menu.menu_showlighterlist, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Fragment fragment = null;

        // Handle presses on the action bar items
        Log.d("actionbutton", "karte wurde gedrückt.");
        switch (item.getItemId()) {
            case R.id.action_mapbutton:
                fragment = new showLighterMapFragment();
                break;
            default:
                Toast.makeText(getActivity(), "Leider ist ein Fehler aufgetreten.", Toast.LENGTH_SHORT).show();
        }

        if (fragment != null) {
            FragmentManager fragmentManager = getFragmentManager();
            fragmentManager.beginTransaction()
                    .replace(R.id.frame_container, fragment).commit();
        }
        return true;
    }



    /**
     *StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
     StrictMode.setThreadPolicy(policy);
     */

    class downloadEntityFormDatabase extends AsyncTask<String, String, String> {


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(getActivity());
            pDialog.setMessage("Adressen werden gedownloaded");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
        }



        protected String doInBackground(String... args) {
            // Building Parameters
   /*         double lng;
            double lat;
            String comment = null;
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            // getting JSON string from URL
            JSONObject json = jParser.makeHttpRequest(downloadwebsite, "GET", params);
            if(json!=null) {
                // Check your log cat for JSON reponse
                Log.d("All Products: ", json.toString());
            }else{
                Log.d("download", "json ist leer");
            }
            try {
                JSONArray jsonArray = json.getJSONArray("fixLighter");
                Log.d("download", jsonArray.toString());
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject helpingObject = jsonArray.getJSONObject(i);
                    lat = new Double(helpingObject.getString("latitude"));
                    lng = new Double(helpingObject.getString("longitude"));
                    comment = helpingObject.getString("comment");
                    Log.d("objekte", lat + " , " + lng + " , " + comment);

                    try {

                        Geocoder geo = new Geocoder(getActivity().getApplicationContext(), Locale.getDefault());
                        addresses = geo.getFromLocation(lat, lng, 1);
                    } catch (IOException e) {

                    }


                    if (addresses.get(0).getLocality() == null) {
                        fixLighterAdresse[i] = addresses.get(0).getSubLocality() + " " + addresses.get(0).getPostalCode() + " " + addresses.get(0).getThoroughfare() + " Kommentar: " + comment;                          //  Log.d("objekte",  addresses.get(0).getSubLocality() + ", " + addresses.get(0).getPostalCode() + " , " + addresses.get(0).getThoroughfare() + ", usercomment: " +comment);
                    } else if (addresses.get(0).getSubLocality() == null) {
                        fixLighterAdresse[i] = addresses.get(0).getLocality() + " " + addresses.get(0).getPostalCode() + " " + addresses.get(0).getThoroughfare() + " Kommentar: " + comment;
                        //  Log.d("objekte",  addresses.get(0).getLocality() + ", " + addresses.get(0).getPostalCode() + " , " + addresses.get(0).getThoroughfare() + ", usercomment: " +comment);
                    }
                }
            } catch (JSONException e) {
                Log.d("download", "etwas ging gründlich in die Hosen");
            }


            // Check your log cat for JSON reponse


            for (String adressenausdb : fixLighterAdresse) {
                if (adressenausdb != null) {
                    Log.d("ausgabefix", adressenausdb);
                }
            }

    */        return null;
        }



        protected void onPostExecute(String file_url) {


            // dismiss the dialog once done
            pDialog.dismiss();
            Log.d("download", "ende download");


        }

    }

}

