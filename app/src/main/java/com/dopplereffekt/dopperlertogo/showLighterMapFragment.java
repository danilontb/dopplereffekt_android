package com.dopplereffekt.dopperlertogo;


import android.app.Fragment;
import android.app.FragmentManager;
import android.app.ProgressDialog;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
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

import java.util.List;

/**
 * Created by dsantagata on 31.05.2015.
 */
public class showLighterMapFragment extends Fragment {



    GoogleMap googleMap;
    MapView mMapView;
    int anzArrayInhalt = ConvertPDF.pdf2AdressArray().length;

    Location loca = null;


    // double[] coordinate = {47.398797, 9.610752,47.435966, 9.071049, 47.291788, 9.125980, 47.248224, 9.344076};
    @Override
    public void onStart() {
        super.onStart();



    new  LoadingLighterPosition().execute();



    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, final Bundle savedInstanceState) {

        setHasOptionsMenu(true);

        final View rootView = inflater.inflate(R.layout.showlightermap_fragment, container, false);

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

    //diese Methode l�st die Adresse (PLZ + Strasse) nach ihren L�ngengrad auf.
    //Imfalle wenn die Adresse nicht aufgel�st werden kann oder die �bergebene
    //Adresse sei null, wird einfach nicht gemacht und mit der n�chsten probiert...Ist nicht professionell aber naja:)
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
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        menu.clear();
        inflater.inflate(R.menu.menu_showlightermap, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Fragment fragment = null;

        // Handle presses on the action bar items
        Log.d("actionbutton", "karte wurde gedr�ckt.");
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

    /*
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