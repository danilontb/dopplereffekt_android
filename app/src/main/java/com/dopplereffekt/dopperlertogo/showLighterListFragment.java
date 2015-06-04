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
import android.widget.ActionMenuView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

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
public class showLighterListFragment extends Fragment {
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

        final View rootView = inflater.inflate(R.layout.showlighterlist_fragment, container, false);

        LighterList = (ListView) rootView.findViewById(R.id.lightershowlist);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, ConvertPDF.pdf2AdressArray());
        LighterList.setAdapter(adapter);

        return rootView;
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
}