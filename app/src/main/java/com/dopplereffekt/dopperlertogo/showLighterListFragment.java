package com.dopplereffekt.dopperlertogo;


import android.app.Fragment;
import android.app.FragmentManager;
import android.app.ProgressDialog;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
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
import java.util.Arrays;
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



    String[] placeHolderArray = {"download ist in arbeit", "download ist in arbeit", "download ist in arbeit"};

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


        publicLighterlist.setAdapter(new ArrayAdapter<String>(getActivity(),android.R.layout.simple_list_item_1,Backgrounddownloading.fixLighterAdresse));
        publicLighterlist.setBackgroundColor(getResources().getColor(R.color.listbackground_public));

        fixLighterList.setAdapter(new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1,Backgrounddownloading.fixLighterAdresse));
        fixLighterList.setBackgroundColor(getResources().getColor(R.color.listbachground_fixlighter));

        mobileLighterList.setAdapter(new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, Backgrounddownloading.mobileLighterAdresse));
        mobileLighterList.setBackgroundColor(getResources().getColor(R.color.listbachground_mobilelighter));

        laserLigterList.setAdapter(new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, Backgrounddownloading.laserLighterAdresse));
        laserLigterList.setBackgroundColor(getResources().getColor(R.color.listbachground_laserlighter));

        return rootView;
    }


    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setListViewHeightBasedOnItems(publicLighterlist);
        setListViewHeightBasedOnItems(fixLighterList);
        setListViewHeightBasedOnItems(mobileLighterList);
        setListViewHeightBasedOnItems(laserLigterList);
    }

    public static boolean setListViewHeightBasedOnItems(ListView listView) {

        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter != null) {

            int numberOfItems = listAdapter.getCount();

            // Get total height of all items.
            int totalItemsHeight = 0;
            for (int itemPos = 0; itemPos < numberOfItems; itemPos++) {

                Log.d("item", itemPos + "");
                    View item = listAdapter.getView(itemPos, null, listView);
                    item.measure(0, 0);
                    totalItemsHeight += item.getMeasuredHeight();
            }

            // Get total height of all item dividers.
            int totalDividersHeight = listView.getDividerHeight() *
                    (numberOfItems - 1);

            // Set list height.
            ViewGroup.LayoutParams params = listView.getLayoutParams();
            params.height = totalItemsHeight + totalDividersHeight;
            listView.setLayoutParams(params);
            listView.requestLayout();

            return true;

        } else {
            return false;
        }

    }

    public static void setDynamicHeight(ListView mListView) {
        ListAdapter mListAdapter = mListView.getAdapter();
        View listItem = null;
        if (mListAdapter == null) {
            // when adapter is null
            Log.e("view", "mListAdapter ist leer");
            return;
        }
        int height = 0;
        int desiredWidth = View.MeasureSpec.makeMeasureSpec(mListView.getWidth(), View.MeasureSpec.UNSPECIFIED);
        for (int i = 0; i < mListAdapter.getCount(); i++) {
            Log.d("dynamic", mListAdapter.toString());
            Log.d("dynamic", "mlistview : " + mListView);
            listItem = mListAdapter.getView(i, null, mListView);
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


}

