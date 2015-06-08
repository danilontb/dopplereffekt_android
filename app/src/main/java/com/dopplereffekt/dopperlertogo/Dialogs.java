package com.dopplereffekt.dopperlertogo;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.app.Dialog;

import org.w3c.dom.Text;

/**
 * Created by dsantagata
 */
public class Dialogs extends Activity {

    String d = "babeliba";

    public static String eventoption = null;
    public static String eventcomment = null;
    public static double mylongitude;
    public static double mylatitude;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        final Bundle extras = getIntent().getExtras();

        mylongitude = extras.getDouble("mylongitude");
        mylatitude  = extras.getDouble("mylatitude");

        //Switch case so muss dem Intent nur ein Wert "Dialog" mitgegeben werden und die Klasse Dialogs handelt den richtigen Dialog.
        switch (extras.getInt("Dialog")) {
            case 1: {
                turnGpsOnDialogForWarning();
            }
            break;
            case 2: {
                dialogBeforDestroy();
            }
            break;
            case 3: {
                createNewEvent();
            }
            break;
            case 4: {
                turnGpsOn();
            }
            break;
            default: {

            }
            break;
        }
    }

    //Diese Methode wird bei aufgerufen, wenn man in der letzten Activity ist und den Backpressbutton betätigt.
    //Für Android wird dann die App zerstört (onDestroy). Der Benutzer wird vorher noch von uns gewarnt.
    private void dialogBeforDestroy() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        // set title
        alertDialogBuilder.setTitle("Achtung");
        alertDialogBuilder.setIcon(R.drawable.attention);

        // set dialog message
        alertDialogBuilder
                .setMessage("Sind Sie sicher die App beenden zu wollen?")
                .setCancelable(false)
                .setPositiveButton("Ja", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // if this button is clicked, close
                        // current activity
                        dialog.cancel();
                        getResponseActivity(true);
                    }
                })
                .setNegativeButton("Nein", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                        dialog.cancel();
                        getResponseActivity(false);
                    }
                });
        ;
        // create alert dialog
        AlertDialog alertDialog = alertDialogBuilder.create();

        // show it
        alertDialog.show();
    }

    private void turnGpsOnDialogForWarning() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setTitle("Standortfreigabe erforderlich");
        builder.setIcon(R.drawable.gps_icon);

        builder.setMessage("GPS ist ausgeschaltet. Du solltest GPS einschalten um präzise Meldungen über Blitzer in deiner Nähe zu erhalten. Möchtest du GPS einschalten?")
                .setCancelable(false)
                .setPositiveButton("Ja", new DialogInterface.OnClickListener() {
                    public void onClick(@SuppressWarnings("unused") final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                        startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                        dialog.cancel();
                        getResponseActivity(true);
                    }
                })
                .setNegativeButton("Nein", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                        dialog.cancel();
                        getResponseActivity(false);
                    }
                });
        final AlertDialog alert = builder.create();
        alert.show();
    }

    private void turnGpsOn() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setTitle("Standortfreigabe erforderlich");
        builder.setIcon(R.drawable.gps_icon);

        builder.setMessage("Um mit deiner Position ein Erreignis zu markieren, muss das GPS eingeschaltet sein.")
                .setCancelable(false)
                .setPositiveButton("Ja", new DialogInterface.OnClickListener() {
                    public void onClick(@SuppressWarnings("unused") final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                        startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                        dialog.cancel();
                        getResponseActivity(true);
                    }
                })
                .setNegativeButton("Nein", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                        dialog.cancel();
                        getResponseActivity(false);
                    }
                });
        final AlertDialog alert = builder.create();
        alert.show();
    }


    private void createNewEvent() {


        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View v = inflater.inflate(R.layout.publishevent_layout, null);

        final TextView tv_comment = (TextView) v.findViewById(R.id.tv_eventcomment);
        Spinner spinner = (Spinner) v.findViewById(R.id.spinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.event_possibility, android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                //   Log.d("funkioniere", parent.getItemAtPosition(position) + " ");
                eventoption = parent.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        builder.setView(v);


        builder.setPositiveButton("Markieren", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                eventcomment = tv_comment.getText().toString();
                Log.d("funktionieren", "das steht im kommentar: " + eventcomment);
                Log.d("coordiante", mylatitude + " : " + mylongitude);
                //Log.d("funktionieren", eventoption);
                if ((eventoption != null) && (!(eventoption.equals("Wählen Sie ein Erreignis")))) {
                    Intent intent = new Intent(getApplicationContext(), WriteEvent2DB.class);
                    if(eventcomment.equals("")) {
                        intent.putExtra("eventcomment", "es wurde leider kein Hinweis hinterlassen");
                    }else{
                        intent.putExtra("eventcomment", eventcomment);
                    }
                    intent.putExtra("eventoption", eventoption);
                    startActivity(intent);

                    Log.d("funktionieren", "alle optionen wruden erfüllt");
                } else {
                    Log.d("funktionieren", "es gibt sachen, die nicht ausgefüllt wurden");
                }

                finish();
            }
        });


        builder.setNegativeButton("Abbrechen", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                finish();
            }
        });


        builder.show();

    }


    //getResponseActivity liefert zurück zur activity von wo sie gestartet wurde und gibt zudem eine Antwort.
    public void getResponseActivity(boolean answer) {
        Log.d(d, "in der responseactivity anser: " + answer);
        final Intent intent = new Intent();
        intent.putExtra("ResponseActivity", answer);
        setResult(RESULT_OK, intent);
        finish();
        finish();
    }

}
