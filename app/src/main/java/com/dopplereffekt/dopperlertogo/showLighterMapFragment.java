package com.dopplereffekt.dopperlertogo;


import android.app.Fragment;
import android.app.FragmentManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;


import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

/**
 * Created by dsantagata on 31.05.2015.
 */
public class showLighterMapFragment extends Fragment {

    String datefile = "DateFile";

    GoogleMap googleMap;
    MapView mMapView;

    int anzOffizielleBlitzer = ConvertPDF.pdf2AdressArray().length;
    String [] adressen = null;
    Location[] orte = new Location[anzOffizielleBlitzer];

    Location loca   = null;
    int minutes     = 100;
    int oldminutes  = 100;
    int hour        = 100;
    int oldhour     = 100;
    int day         = 100;
    int oldday      = 100;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if(adressen==null){
            adressen = ConvertPDF.pdf2AdressStringForAPI();
            Log.d("adressenarray", "adressenarry war leer");
        }else{
            Log.d("adressenarray", "adressenarry war nicht leer");
        }


        loadPrefrences();
        loadActualTime();

        for(int i = 0; i<anzOffizielleBlitzer; i++){
            orte[i] = convertAddressToCoor(adressen[i]);
        }

        for(Location loca : orte){
            Log.d("versuch" ,loca.getLatitude() + " " + loca.getLongitude());
        }


        if((minutes < (oldminutes+15))&&(hour == oldhour)&&(day == oldday)&&(orte!=null)){
            //lade nichts neues runter
        }else {
            //lade daten neu
            oldminutes  = minutes;
            oldday      = day;
            oldhour     = hour;

        }



    }

    private void loadPrefrences(){
        SharedPreferences settings = this.getActivity().getSharedPreferences(datefile, Context.MODE_PRIVATE);
        oldday      = settings.getInt("day", 0);
        oldhour     = settings.getInt("hour", 0);
        oldminutes  = settings.getInt("minutes", 0);


    }

    private void loadActualTime(){
        Calendar calendar = GregorianCalendar.getInstance();
        minutes = calendar.get(Calendar.MINUTE);
        hour    = calendar.get(Calendar.HOUR_OF_DAY);
        day     = calendar.get(Calendar.DAY_OF_YEAR);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, final Bundle savedInstanceState) {

        setHasOptionsMenu(true);

        final View rootView = inflater.inflate(R.layout.showlightermap_fragment, container, false);

        Log.d("adressenarray" ,"fettig");
        mMapView = (MapView) rootView.findViewById(R.id.lightershowmap);
        mMapView.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        mMapView.onResume();// needed to get the map to display immediately

        try {
            MapsInitializer.initialize(getActivity().getApplicationContext());
        } catch (Exception e) {
            e.printStackTrace();
        }


        //get the map
        googleMap = mMapView.getMap();
        // Detect location
        googleMap.setMyLocationEnabled(true);
        // Turns traffic layer on
        googleMap.setTrafficEnabled(true);
        // Enables indoor maps
        googleMap.setIndoorEnabled(true);
        // Turns on 3D buildings
        googleMap.setBuildingsEnabled(true);
        // Show Zoom buttons
        googleMap.getUiSettings().setZoomControlsEnabled(true);

        // Sets the map type to be "hybrid"
        googleMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);


        CameraPosition cameraPositionThess = new CameraPosition.Builder().target(new LatLng(47.411448, 9.068251)).zoom(8).build();
        googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPositionThess));

        return rootView;
    }

    //diese Methode löst die Adresse (PLZ + Strasse) nach ihren Längengrad auf.
    //Imfalle wenn die Adresse nicht aufgelöst werden kann oder die übergebene
    //Adresse sei null, wird einfach nicht gemacht und mit der nächsten probiert...Ist nicht professionell aber naja:)
    private Location convertAddressToCoor(String adress) {
        Location lc = new Location("point A");
        Geocoder coder = new Geocoder(getActivity());
        List<Address> address;
        double lat;
        double lng;

        try {
            address = coder.getFromLocationName(adress, 5);
            if (!(address == null)) {
                Address location = address.get(0);
                lng = location.getLongitude();
                lat = location.getLatitude();

                lc.setLatitude(lat);
                lc.setLongitude(lng);


                //  lc.setLatitude(location.getLatitude());

                Log.d("loca", lng + " ");
                Log.d("loca", lat + " ");
            } else {
                Log.d("loca", "Adresse war leer");
            }
        } catch (Exception ex) {
            Log.d("loca", "Adresse konnte nicht umgewandelt werden");
            ex.printStackTrace();
        }

        return lc;
    }

    @Override
    public void onStart() {
        super.onStart();
        new  LoadingLighterPosition().execute();
    }



    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        menu.clear();
        inflater.inflate(R.menu.menu_showlightermap, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Fragment fragment = null;

        // Handle presses on the action bar items
        Log.d("actionbutton", "karte wurde gedrückt.");
        switch (item.getItemId()) {
            case R.id.action_listbutton:
                fragment = new showLighterListFragment();
                break;
        }

        if (fragment != null) {
            FragmentManager fragmentManager = getFragmentManager();
            fragmentManager.beginTransaction()
                    .replace(R.id.frame_container, fragment).commit();


        }
        return true;
    }

    /**
    * Background Async Task to Create new product
    * */
    class LoadingLighterPosition extends AsyncTask<String, MarkerOptions, String> {

        ProgressDialog pDialog;
        /**
         * Before starting background thread Show Progress Dialog
         * */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
             pDialog = new ProgressDialog(getActivity());
            pDialog.setMessage("Aktuelle Standorte werden geladen...");
            pDialog.setTitle("Datenbank download");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
        }


        /**
         * Read positions
         * */
        protected String doInBackground(String... args)
        {
            int i = 0;
            for (String adress : ConvertPDF.pdf2AdressStringForAPI()) {

                loca = convertAddressToCoor(adress);
                Log.d("loca", loca.getLatitude() + " " + loca.getLongitude());


                if (loca != null) {

                    // create marker
                    MarkerOptions thessaloniki = new MarkerOptions().position(new LatLng(loca.getLatitude(), loca.getLongitude())).title(ConvertPDF.pdf2AdressArray()[i]);
                    // Changing marker icon
                    thessaloniki.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE));
           //         googleMap.addMarker(thessaloniki);
                    publishProgress(thessaloniki);
                } else {
                    //Toast.makeText(getActivity(), "Koordinaten sind leer", Toast.LENGTH_SHORT).show();
                }
                i++;
            }

            return null;
        }

        @Override
        protected void onProgressUpdate(MarkerOptions... values) {
            googleMap.addMarker(values[0]);
        }

        /**
         * After completing background task Dismiss the progress dialog
         * **/
        protected void onPostExecute(String file_url) {


            // dismiss the dialog once done
            pDialog.dismiss();

        }

    }
}