package com.dopplereffekt.dopperlertogo;


import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

/**
 * Created by dsantagata on 31.05.2015.
 */
public class InformationOtherFragment extends Fragment implements GoogleMap.OnMapLongClickListener, GoogleMap.OnMapClickListener {
    // public WarningFragment(){}
    GoogleMap googleMap;

    MapView mMapView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.informoterhs_fragment, container, false);

        mMapView = (MapView) rootView.findViewById(R.id.map);
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
        // latitude and longitude
        double latitude = 47.411448;
        double longitude = 9.068251;

        // create marker
        MarkerOptions thessaloniki = new MarkerOptions().position(new LatLng(latitude, longitude)).title("Thessaloniki");
        //----------------
        MarkerOptions LeykosPyrgos = new MarkerOptions().position(new LatLng(40.626401, 22.948352)).title("Leykos Pyrgos");

        // Changing marker icon
        thessaloniki.icon(BitmapDescriptorFactory .defaultMarker(BitmapDescriptorFactory.HUE_AZURE));
        //----------------
        LeykosPyrgos.icon(BitmapDescriptorFactory .fromResource(R.drawable.mapmarker));

        // adding marker
        googleMap.addMarker(thessaloniki);
        CameraPosition cameraPositionThess = new CameraPosition.Builder() .target(new LatLng(47.411448, 9.068251)).zoom(12).build();
        googleMap.animateCamera(CameraUpdateFactory .newCameraPosition(cameraPositionThess));
        //----------------
        /*
        googleMap.addMarker(LeykosPyrgos);
        CameraPosition cameraPositionLeykosPyrgos = new CameraPosition.Builder() .target(new LatLng(40.626401, 22.948352)).zoom(2).build();
        googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPositionLeykosPyrgos));
*/

        googleMap.setOnMapLongClickListener(this);
        googleMap.setOnMapClickListener(this);

        // Perform any camera updates here
        return rootView;
    }



    @Override
    public void onMapClick(LatLng point) {
        Toast.makeText(getActivity(),point.toString(), Toast.LENGTH_SHORT).show();
        googleMap.animateCamera(CameraUpdateFactory.newLatLng(point));
    }

    @Override
    public void onMapLongClick(LatLng point) {
        googleMap.addMarker(new MarkerOptions()
                .position(point)
                .title("You are here")
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));
    }



}







