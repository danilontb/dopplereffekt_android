package com.dopplereffekt.dopperlertogo;


import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

/**
 * Created by dsantagata on 31.05.2015.
 */
public class publicLighterFragment extends Fragment{
   // public WarningFragment(){}
    TextView publicLighterText;
    ListView publicLighterList;

    String[] test = {"wer","wie","was"};
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        publicLighterText.append( " " + ConvertPDF.getUpdateDate());

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.publiclighter_fragment, container, false);

        publicLighterText = (TextView) rootView.findViewById(R.id.publicLighterText);
        publicLighterList = (ListView) rootView.findViewById(R.id.publicLighterList);

        //StringArray können nicht einfach einer Liste übergeben werden. Dazu werden Adapter verwendet wie in den nächsten 2 Zeilen.
        //Mitgegeben wird auch die Art des Layouts der einzelnen Einträge. Wir wollen nur unsere Arrayelemente angezeigt bekommen.
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(rootView.getContext(), android.R.layout.simple_list_item_1, ConvertPDF.pdf2AdressArray());
        publicLighterList.setAdapter(adapter);

        return rootView;

    }

}