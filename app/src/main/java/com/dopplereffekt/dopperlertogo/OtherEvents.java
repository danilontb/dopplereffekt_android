package com.dopplereffekt.dopperlertogo;


import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by dsantagata on 31.05.2015.
 */
public class OtherEvents extends Fragment{
   // public WarningFragment(){}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.otherevents_fragment, container, false);

        return rootView;
    }
}