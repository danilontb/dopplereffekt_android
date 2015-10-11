package com.dopplereffekt.dopperlertogo;


import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
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
import android.widget.TextView;
import android.widget.Toast;

import java.lang.reflect.Array;
import java.util.ArrayList;


/**
 * Created by dsantagata on 31.05.2015.
 */
public class showLighterListFragment extends Fragment{

    public static Boolean refreshing;
    private ProgressDialog pDialog;
    public static ListView publicLighterlist;
    public static ListView fixLighterList;
    public static ListView mobileLighterList;
    public static ListView laserLigterList;
    public static ListView controleList;
    public static TextView tv_aktualisierung;
    public static TextView tv_updateDate_FixLighter;
    public static TextView tv_updateDate_mobileLighter;
    public static TextView tv_updateDate_laserLighter;
    public static TextView tv_updateDate_controlePosition;

    public static SwipeRefreshLayout swipeRefreshLayout;

    public static ArrayAdapter officialLighterAdapter;
    public static ArrayAdapter fixLighterAdapter;
    public static ArrayAdapter mobileLighterAdapter;
    public static ArrayAdapter laserLighterAdapter;
    public static ArrayAdapter controlePositionAdapter;

    public static Activity activity;



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, final Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.showlighterlist_fragment, container, false);
        setHasOptionsMenu(true);

        // Retrieve the SwipeRefreshLayout and ListView instances
        swipeRefreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.activity_main_swipe_refresh_layout);

         activity = (Activity)rootView.getContext();

        tv_aktualisierung               = (TextView)rootView.findViewById(R.id.tv_updateDate);
        tv_updateDate_FixLighter        = (TextView)rootView.findViewById(R.id.tv_updateDateFixLighter);
        tv_updateDate_mobileLighter     = (TextView)rootView.findViewById(R.id.tv_updateDateMobileLighter);
        tv_updateDate_laserLighter      = (TextView)rootView.findViewById(R.id.tv_updateDate_laserLighter);
        tv_updateDate_controlePosition  = (TextView)rootView.findViewById(R.id.tv_updateDate_controlePosition);

        tv_aktualisierung.setText(ConvertPDF.getUpdateDate());
        tv_updateDate_FixLighter.setText(" update am : " + Backgrounddownloading.updateTimeStamp);
        tv_updateDate_mobileLighter.setText(" update am : " + Backgrounddownloading.updateTimeStamp);
        tv_updateDate_laserLighter.setText(" update am : " + Backgrounddownloading.updateTimeStamp);
        tv_updateDate_controlePosition.setText(" update am : " + Backgrounddownloading.updateTimeStamp);

        publicLighterlist = (ListView) rootView.findViewById(R.id.publiclighterlist);
        fixLighterList = (ListView) rootView.findViewById(R.id.fixlighterlist);
        mobileLighterList = (ListView) rootView.findViewById(R.id.mobilelighterlist);
        laserLigterList = (ListView) rootView.findViewById(R.id.laserlighterlist);
        controleList = (ListView) rootView.findViewById(R.id.controleList);

        officialLighterAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, ConvertPDF.pdf2AdressArray());
        fixLighterAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, Backgrounddownloading.fixLighterAdrAndCom);
        mobileLighterAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, Backgrounddownloading.mobileLighterAdrAndCom);
        laserLighterAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, Backgrounddownloading.laserLighterAdrAndCom);
        controlePositionAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, Backgrounddownloading.controlePositionAdrAndCom);

        publicLighterlist.requestLayout();
        fixLighterList.requestLayout();
        mobileLighterList.requestLayout();
        laserLigterList.requestLayout();
        controleList.requestLayout();

        publicLighterlist.setAdapter(officialLighterAdapter);
        publicLighterlist.setBackgroundColor(getResources().getColor(R.color.listbackground_public));

        fixLighterList.setAdapter(fixLighterAdapter);
        fixLighterList.setBackgroundColor(getResources().getColor(R.color.listbackground_fixlighter));

        mobileLighterList.setAdapter(mobileLighterAdapter);
        mobileLighterList.setBackgroundColor(getResources().getColor(R.color.listbackground_mobileLighter));

        laserLigterList.setAdapter(laserLighterAdapter);
        laserLigterList.setBackgroundColor(getResources().getColor(R.color.listbackground_laserLighter));

        controleList.setAdapter(controlePositionAdapter);
        controleList.setBackgroundColor(getResources().getColor(R.color.listbackground_controleposition));


        return rootView;
    }

    @Override
    public void onStart() {
        super.onStart();
        //start here the async
        new waiting().execute();
    }


    public static void refreshLists(){


    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setListViewHeightBasedOnItems(publicLighterlist);
        setListViewHeightBasedOnItems(fixLighterList);
        setListViewHeightBasedOnItems(mobileLighterList);
        setListViewHeightBasedOnItems(laserLigterList);
        setListViewHeightBasedOnItems(controleList);

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                Log.i("refresh", "onRefresh called from SwipeRefreshLayout");
                refreshing = swipeRefreshLayout.isRefreshing();
                initiateRefresh();
            }
        });
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

    /**
     * By abstracting the refresh process to a single method, the app allows both the
     * SwipeGestureLayout onRefresh() method and the Refresh action item to refresh the content.
     */
    private void initiateRefresh() {
        Log.i("refresh", "initiateRefresh");

        /**
         * Execute the background task, which uses {@link android.os.AsyncTask} to load the data.
         */
        new Backgrounddownloading.backgroundjsondownload().execute();
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
        Log.d("actionbutton", "karte wurde gedr�ckt.");
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
     * �ber die innere Klasse kann ich nicht viel sagen. Sie wird ben�tigt um Inhalte aus dem Web zu downloaden. Diese Klasse wird paralell ausgef�hrt.
     */
    private class waiting extends AsyncTask<String, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            Log.d("init", "init gestartett");
            pDialog = new ProgressDialog(getActivity());
            pDialog.setTitle("Aktualisierung");
            pDialog.setMessage("Daten werden heruntergeladen.");
            pDialog.setProgressStyle(R.style.AppTheme);
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();
        }

        @Override
        protected Void doInBackground(String... strings) {

            while (!Backgrounddownloading.readable) {
            }


            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            pDialog.dismiss();
            publicLighterlist.invalidateViews();
            fixLighterList.invalidateViews();
            mobileLighterList.invalidateViews();
            laserLigterList.invalidateViews();
            controleList.invalidateViews();

        }

    }


}

