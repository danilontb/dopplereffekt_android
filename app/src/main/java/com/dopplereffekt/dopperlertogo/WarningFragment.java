package com.dopplereffekt.dopperlertogo;


import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.Toast;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by dsantagata on 31.05.2015.
 */


// TODO hacken muss gespeichert werden. ausser man schliesst die app komplett.
//TODO
//Diese Klasse schaltet nur das GPS ein, wenn der User entsprechend updates verlangt.
public class WarningFragment extends Fragment {
    // public WarningFragment(){}
    CheckBox checkInfo;
    boolean wantsUpdates;
    boolean isGPSEnabled;
    LocationManager service;
    public static boolean serviceLaeuft;

    private static final int ENABLE_GPS = 999;                      //Das ist ein Requestcode. Dieser wird gebraucht um startActivityForResult() zu unterscheiden, von wem es kommt.
    public static Intent serviceIntent = null;                             //Der service intent, wo immer der gleiche aufgerufen bzw gestoppt wird.

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        //init das der service noch nicht läuft
        serviceLaeuft = false;

        View rootView = inflater.inflate(R.layout.warning_fragment, container, false);
        //Initialisierung der Checkbox und eine Listener wird erstellt, welchen an dieser checkbox "horcht"
        checkInfo = (CheckBox) rootView.findViewById(R.id.checkBoxUpdates);

        //initialisierung des Serviceintents
        serviceIntent = new Intent(getActivity(), GPSService.class);

        //Der LocationManager wird benötigt, um abzufragen, ob der User das GPS eingeschaltet hat.
        service = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
        isGPSEnabled = service.isProviderEnabled(LocationManager.GPS_PROVIDER);

        //Aus dem Internen File wird gelesen, ob der User informationen erhalten möchte. Dieser status wird dann in die checkbox übertragen.
        SharedPreferences settings = getActivity().getSharedPreferences(MainActivity.PREFS_NAME, 0);
        wantsUpdates = settings.getBoolean("Updates", false);
        checkInfo.setChecked(wantsUpdates);


        checkInfo.setOnClickListener(new View.OnClickListener() {

            //Diese Methode "onCLick" wird aufgerufen sobald sich der Zustand der Checkbox ändert.
            @Override
            public void onClick(View v) {
                LocationManager service = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
                isGPSEnabled = service.isProviderEnabled(LocationManager.GPS_PROVIDER);
                if (checkInfo.isChecked()) {

                    if (isGPSEnabled) {

                                wantsUpdates = true;
                        writeUpdateState(wantsUpdates);
                        checkInfo.setChecked(wantsUpdates);
                        getActivity().startService(serviceIntent);
                        serviceLaeuft = true;

                    } else {
                        Intent intent = new Intent(getActivity(), Dialogs.class);
                        intent.putExtra("Dialog", 1);
                        startActivityForResult(intent, ENABLE_GPS);

                    }
                } else {
                    getActivity().stopService(serviceIntent);
                    writeUpdateState(false);
                    Toast.makeText(getActivity(), R.string.gps_is_turning_off, Toast.LENGTH_SHORT).show();
                    wantsUpdates = false;
                    writeUpdateState(wantsUpdates);
                }

            }
        });



        return rootView;
    }



    //mit dieser Methode wird gespeichert ob, der User informiert werden möchte oder nicht.
    public void writeUpdateState(boolean state) {
        SharedPreferences settings = getActivity().getSharedPreferences(MainActivity.PREFS_NAME, 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putBoolean("Updates", state);
        editor.commit();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {

            switch (requestCode) {
                case ENABLE_GPS: {
                 if (data.getExtras().getBoolean("ResponseActivity")){
                     isGPSEnabled = true;
                 }else{
                   isGPSEnabled = false;
                 }

                }
                    break;

                default:
                    break;
            }
        }

        checkInfo.setChecked(serviceLaeuft && isGPSEnabled);
        super.onActivityResult(requestCode, resultCode, data);
    }


    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        Log.d("Fragment", "im onAttach()");
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("Fragment", "im onCreate()");
    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Log.d("Fragment", "im onActivityCreated");
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.d("Fragment", "im onStart");
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d("Fragment", "im onResume -- -- -- Fragment is now activated");
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.d("Fragment", "im onPause");
        //Hier kann den status der Checkbox gespeichert werden.

    }

    @Override
    public void onStop() {
        super.onStop();
        Log.d("Fragment", "im onStop");
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Log.d("Fragment", "im onDestroyView");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d("Fragment", "im onDestroy");
    }

    @Override
    public void onDetach() {
        super.onDetach();
        Log.d("Fragment", "im onDetach");
    }
}