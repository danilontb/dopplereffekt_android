package com.dopplereffekt.dopperlertogo;


import android.app.Fragment;
import android.app.FragmentManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

/**
 * Created by dsantagata on 31.05.2015.
 */
public class showLighterMapFragment extends Fragment {
    // public WarningFragment(){}
    ListView LighterList;
    GoogleMap googleMap;
    MapView mMapView;

    String[] test = {"wer", "wie", "was"};

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        //      publicLighterText.append( " " + ConvertPDF.getUpdateDate());

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             final Bundle savedInstanceState) {

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
        //googleMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
        // Turns traffic layer on
        googleMap.setTrafficEnabled(true);
        // Enables indoor maps
        googleMap.setIndoorEnabled(true);
        // Enables indoor maps
        googleMap.setIndoorEnabled(true);
        // Turns on 3D buildings
        googleMap.setBuildingsEnabled(true);
        // Show Zoom buttons
        googleMap.getUiSettings().setZoomControlsEnabled(true);

        // Sets the map type to be "hybrid"
        googleMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);


        CameraPosition cameraPositionThess = new CameraPosition.Builder() .target(new LatLng(47.411448, 9.068251)).zoom(12).build();
        googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPositionThess));

        return rootView;
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
}