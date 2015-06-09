package com.dopplereffekt.dopperlertogo;


import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Intent;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
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

    // latitude and longitude
    double zuich_latitude       = 47.379022;
    double zurich_longitude     =  8.541001;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.informoterhs_fragment, container, false);

        mMapView = (MapView) rootView.findViewById(R.id.map);

        // Perform any camera updates here
        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {


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




        CameraPosition cameraPositionThess = new CameraPosition.Builder() .target(new LatLng(zuich_latitude, zurich_longitude)).zoom(8).build();
        googleMap.animateCamera(CameraUpdateFactory .newCameraPosition(cameraPositionThess));
        //----------------
        /*
        googleMap.addMarker(LeykosPyrgos);
        CameraPosition cameraPositionLeykosPyrgos = new CameraPosition.Builder() .target(new LatLng(40.626401, 22.948352)).zoom(2).build();
        googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPositionLeykosPyrgos));
*/
        googleMap.setOnMapLongClickListener(this);
        googleMap.setOnMapClickListener(this);

        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onMapClick(LatLng point) {
        Toast.makeText(getActivity(),point.toString(), Toast.LENGTH_SHORT).show();
        googleMap.animateCamera(CameraUpdateFactory.newLatLng(point));
    }

    @Override
    public void onMapLongClick(LatLng point) {
      /*  googleMap.addMarker(new MarkerOptions()
                .position(point)
                .title("You are here")
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));
    */

        Intent intent = new Intent(getActivity(), Dialogs.class);
        intent.putExtra("mylongitude",point.longitude + " ");
        intent.putExtra("mylatitude", point.latitude + " ");
        Log.d("dialogtest", point.latitude + " , " + point.longitude);
        intent.putExtra("Dialog", 3);
        getActivity().startActivity(intent);
    }



    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        menu.clear();
        inflater.inflate(R.menu.global, menu);
    }

}







