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
import android.widget.AdapterView;
import android.widget.Spinner;
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

    int anzOffizielleBlitzer = ConvertPDF.pdf2AdressArray().length;
    String[] adressen = null;
    Location[] orte = new Location[anzOffizielleBlitzer];

    Location loca = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

            if(adressen==null)
            {
                adressen = ConvertPDF.pdf2AdressStringForAPI();
            }
            //im falle das die offiziellen Blitzer angezeigt werden müssen, sind sie schon heruntergeladen.
            for (int i = 0; i < anzOffizielleBlitzer; i++) {
                orte[i] = convertAddressToCoor(adressen[i]);
            }
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, final Bundle savedInstanceState) {

        setHasOptionsMenu(true);

        final View rootView = inflater.inflate(R.layout.showlightermap_fragment, container, false);


        //dieser Spinner sagt welche Icons gezeigt werden müssen.
        Spinner spinner = (Spinner) rootView.findViewById(R.id.eventSpinnerMap);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
                Object item = parent.getItemAtPosition(pos);
                Log.d("mapfragment", item.toString());
                googleMap.clear();
                new LoadingLighterPosition().execute(item.toString());
            }

            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

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

    //diese Methode lst die Adresse (PLZ + Strasse) nach ihren Lngengrad auf.
    //Imfalle wenn die Adresse nicht aufgelst werden kann oder die bergebene
    //Adresse sei null, wird einfach nicht gemacht und mit der nchsten probiert...Ist nicht professionell aber naja:)
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


    /**
     * Background Async Task to Draw Marker on a Map
     */
    class LoadingLighterPosition extends AsyncTask<String, MarkerOptions, String> {

        ProgressDialog pDialog;

        /**
         * Before starting background thread Show Progress Dialog
         */
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

        public void drawMarkerOfficial() {
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
                }
                i++;
            }
        }

        public void drawOtherMarker(String lighter) {
            switch (lighter) {
                case "officialLighter": {
                    for (int i = 0; i < Backgrounddownloading.officialLighterAdresse.size(); i++) {

                        // create marker
                        MarkerOptions thessaloniki = new MarkerOptions().position(new LatLng((Double)Backgrounddownloading.officialLighterLat.get(i),
                                (Double)Backgrounddownloading.officialLighterLng.get(i))).title((String)Backgrounddownloading.officialLighterAdresse.get(i));
                        // Changing marker icon
                        thessaloniki.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE));
                        //         googleMap.addMarker(thessaloniki);
                        publishProgress(thessaloniki);
                    }
                }break;
                case "fixLighter": {
                    for (int i = 0; i < Backgrounddownloading.fixLighterAdresse.size(); i++) {

                        // create marker
                        MarkerOptions thessaloniki = new MarkerOptions().position(new LatLng((Double)Backgrounddownloading.fixLighterlat.get(i),
                                (Double)Backgrounddownloading.fixLighterlng.get(i))).title((String)Backgrounddownloading.fixLighterAdresse.get(i)).snippet((String) Backgrounddownloading.fixLightercomment.get(i));
                        // Changing marker icon
                        thessaloniki.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE));
                        //         googleMap.addMarker(thessaloniki);
                        publishProgress(thessaloniki);

                    }
                }break;
                case "mobileLighter": {
                    for (int i = 0; i < Backgrounddownloading.mobileLighterAdresse.size(); i++) {
                        // create marker
                        MarkerOptions thessaloniki = new MarkerOptions().position(new LatLng((Double)Backgrounddownloading.mobileLighterlat.get(i),
                                (Double)Backgrounddownloading.mobileLighterlng.get(i))).title((String) Backgrounddownloading.mobileLighterAdresse.get(i)).snippet((String) Backgrounddownloading.mobileLightercomment.get(i));
                        // Changing marker icon
                        thessaloniki.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
                        //         googleMap.addMarker(thessaloniki);
                        publishProgress(thessaloniki);
                    }
                }break;
                case "laserLighter": {
                        for (int i = 0; i < Backgrounddownloading.laserLighterAdresse.size(); i++) {
                            // create marker
                            MarkerOptions thessaloniki = new MarkerOptions().position(new LatLng((Double)Backgrounddownloading.laserLighterlat.get(i),
                                    (Double)Backgrounddownloading.laserLighterlng.get(i))).title((String) Backgrounddownloading.laserLighterAdresse.get(i)).snippet((String) Backgrounddownloading.laserLightercomment.get(i));
                            // Changing marker icon
                            thessaloniki.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_VIOLET));
                            //         googleMap.addMarker(thessaloniki);
                            publishProgress(thessaloniki);
                    }
                }break;
                case "controlPosition": {
                    for (int i = 0; i < Backgrounddownloading.controlePositionAdresse.size(); i++) {
                        // create marker
                        MarkerOptions thessaloniki = new MarkerOptions().position(new LatLng((Double)Backgrounddownloading.controlePositionlat.get(i),
                                (Double)Backgrounddownloading.controlePositionlng.get(i))).title((String) Backgrounddownloading.controlePositionAdresse.get(i)).snippet((String) Backgrounddownloading.controlePositioncomment.get(i));
                        // Changing marker icon
                        thessaloniki.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
                        //         googleMap.addMarker(thessaloniki);
                        publishProgress(thessaloniki);
                    }
                }break;
            }

        }

        /**
         * Read positions
         */
        protected String doInBackground(String... args) {

            if (args.length > 0) {
                switch (args[0]) {
                    case "offizielle Blitzer": {
                        drawOtherMarker("officialLighter");
                    }
                    break;
                    case "Fest instllierte Blitzer": {
                        Log.d("maptest", "bin im switch");
                        drawOtherMarker("fixLighter");
                    }
                    break;
                    case "Mobile Blitzer": {
                        drawOtherMarker("mobileLighter");
                    }
                    break;
                    case "Lasermessungen": {
                        Log.d("maptest", "bin im switch");
                        drawOtherMarker("laserLighter");
                    }
                    break;
                    case "Verkehrskontrollen" :{
                        drawOtherMarker("controlPosition");
                    }

                }
            }
        return null;

    }

    @Override
    protected void onProgressUpdate(MarkerOptions... values) {
        googleMap.addMarker(values[0]);
    }

    /**
     * After completing background task Dismiss the progress dialog
     **/
    protected void onPostExecute(String file_url) {
        // dismiss the dialog once done
        pDialog.dismiss();

    }

}
}