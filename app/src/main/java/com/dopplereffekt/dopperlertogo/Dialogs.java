package com.dopplereffekt.dopperlertogo;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

/**
 * Created by dsantagata
 */
public class Dialogs extends Activity {

    String d = "babeliba";

    public static String eventoption        = null;
    public static String eventcomment       = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        final Bundle extras = getIntent().getExtras();

        //Switch case so muss dem Intent nur ein Wert "Dialog" mitgegeben werden und die Klasse Dialogs handelt den richtigen Dialog.
        switch(extras.getInt("Dialog")){
            case 1:{
                turnGpsOnDialogForWarning();
            }break;
            case 2:{
                dialogBeforDestroy();
            }break;
            case 3:{
                createNewEvent();
            }break;
            case 4:{
                turnGpsOn();
            }break;
            default:{

            }break;
        }
    }

    //Diese Methode wird bei aufgerufen, wenn man in der letzten Activity ist und den Backpressbutton betätigt.
    //Für Android wird dann die App zerstört (onDestroy). Der Benutzer wird vorher noch von uns gewarnt.
    private void dialogBeforDestroy(){  AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
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
                });;
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


        final AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setTitle(R.string.titletext_event);
        builder.setIcon(R.drawable.newevent);
        builder.setView(R.layout.publishevent_layout);

        final Spinner dropdownmenu = (Spinner)findViewById(R.id.dropdownEvents);

        final TextView tv_eventcomment = (TextView)findViewById(R.id.eventsComment);

        dropdownmenu.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                eventoption = String.valueOf(dropdownmenu.getSelectedItem());
                Toast.makeText(getApplicationContext(), eventoption, Toast.LENGTH_SHORT).show();
            }
        });


        builder.setCancelable(false)
                .setPositiveButton(R.string.buttontext_commit, new DialogInterface.OnClickListener() {
                    public void onClick(@SuppressWarnings("unused") final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
         /*               eventcomment  = tv_eventcomment.getText().toString();
                        Intent intent = new Intent(getApplicationContext(), WriteEvent2DB.class);
                        intent.putExtra("event", eventoption);
                        intent.putExtra("comment", eventcomment);

                        startActivity(intent);
           */

                        dialog.cancel();

                    }
                })
                .setNegativeButton(R.string.buttontext_cancel, new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                        dialog.cancel();

                    }
                });
        final AlertDialog alert = builder.create();
        alert.show();
    }



    //getResponseActivity liefert zurück zur activity von wo sie gestartet wurde und gibt zudem eine Antwort.
    public void getResponseActivity(boolean answer){
        Log.d(d,"in der responseactivity anser: " + answer);
            final Intent intent = new Intent();
            intent.putExtra("ResponseActivity", answer);
            setResult(RESULT_OK, intent);
            finish();finish();
    }

}
