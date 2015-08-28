package com.dopplereffekt.dopperlertogo;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import java.util.List;

/**
 * Created by dsantagata
 */
public class GPSService extends Service {
    LocationManager locationManager;
    public LocationListener myLocationlistener = null;
    String[] adressemNormal = null;
    String[] adressenForConvert = null;
    float[] lngArray = null;
    float[] latArray = null;
    double[] strecke = null;
    int anfangsradius;
    int endradius = 100;
    boolean readyForUpdate = true;

    @Override
    public void onCreate() {
        super.onCreate();

        adressemNormal = ConvertPDF.pdf2AdressArray();
        //man holt sich die Bliteradresse im Format, mit dem der Geocoder damit arbeiten kann.
        adressenForConvert = ConvertPDF.pdf2AdressStringForAPI();
        //Latitude unt Longitude array werden angelegt.
        lngArray = new float[adressenForConvert.length];
        latArray = new float[adressenForConvert.length];
        strecke = new double[adressenForConvert.length];
        //aus jeder Adresse wird die Longitude und die Latitude gewonnen und ins Array gespeichert.
        for (int i = 0; i < adressenForConvert.length; i++) {
            lngArray[i] = convertAddressToLng(adressenForConvert[i]);
            latArray[i] = convertAddressToLat(adressenForConvert[i]);
        }
    }


    //diese Methode löst die Adresse (PLZ + Strasse) nach ihren Längengrad auf.
    //Imfalle wenn die Adresse nicht aufgelöst werden kann oder die übergebene
    //Adresse sei null, wird einfach nicht gemacht und mit der nächsten probiert...Ist nicht professionell aber naja:)
    private float convertAddressToLng(String adress) {
        Geocoder coder = new Geocoder(GPSService.this);
        List<Address> address;
        float lng = 9999;
        try {
            address = coder.getFromLocationName(adress, 5);
            if (!(address == null)) {
                Address location = address.get(0);
                lng = (float) location.getLongitude();
            } else {
                Log.d("loca", "Adresse war leer");
            }
        } catch (Exception ex) {
            Log.d("loca", "Adresse konnte nicht umgewandelt werden");
            ex.printStackTrace();
        }
        return lng;
    }

    //ähnlich wie convertAddressToLng() nur das der Rückgabewert dieses mal die Latitude ist und nicht Longitude.
    private float convertAddressToLat(String adress) {
        Geocoder coder = new Geocoder(GPSService.this);
        List<Address> address;
        float lat = 9999;
        try {
            address = coder.getFromLocationName(adress, 5);
            if (!(address == null)) {
                Address location = address.get(0);
                lat = (float) location.getLatitude();
            } else {
                Log.d("loca", "Adresse war leer");
            }
        } catch (Exception ex) {
            Log.d("loca", "Adresse konnte nicht umgewandelt werden");
            ex.printStackTrace();
        }
        return lat;
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        //Eine Instanz der Klasse Location. Wird später verwendet um die Distanz zu den
        //Blitzern zu ermitteln.
        final Location lighterLocation = new Location("");
        String svcName = Context.LOCATION_SERVICE;
        locationManager = (LocationManager) getSystemService(svcName);
        String provider = LocationManager.GPS_PROVIDER;

        //Aktualisierungen des Standortes werden abgerufen, sobald eine von 30000ms verstrichen ist
        //oder man sich mehr als 5m vom letzten Standort entfernt hat.
        int time = 3000;
        final int distanceChange = 5;


        myLocationlistener = new LocationListener() {

            @Override
            public void onLocationChanged(Location location) {

                //Abhängig von der Geschwindigkeit des Usere, wird der vorher oder nachher
                //benachrichtigt. Nicht, dass er schon beim Blitzer ist, und der LocationListener
                //hat es noch nicht bemerkt.
                if (location.getSpeed() < 60) {
                    anfangsradius = 400;
                } else {
                    anfangsradius = 600;
                }


                for (int i = 0; i < adressenForConvert.length; i++) {
                    //Die Blitzerkoordinaten werden in den Datentyp "Location" gewandelt
                    // --> brauchen wir um die Methode .distanceTo(Location location) zu verwenden
                    lighterLocation.setLatitude(latArray[i]);
                    lighterLocation.setLongitude(lngArray[i]);
                    strecke[i] = location.distanceTo(lighterLocation);


                    if ((strecke[i] >= endradius) && (strecke[i] <= anfangsradius) && readyForUpdate) {
                        Toast.makeText(getBaseContext(), "Sie fahre mit : " +
                                location.getSpeed() * 3.6 + "km/h", Toast.LENGTH_SHORT).show();
                        Intent intent1 = new Intent(getBaseContext(), NotifyMessage.class);
                        intent1.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        intent1.putExtra("uebergabe", 1);
                        intent1.putExtra("ort", adressemNormal[i]);
                        intent1.putExtra("speed", location.getSpeed() * 3.6);
                        getApplication().startActivity(intent1);
                        readyForUpdate = false;
                    }
                }

                //ist die IF anweisung true, ist man wieder fähig neue Updates zu erhalten.
                if (checkAllDistance(strecke) && (readyForUpdate == false)) {
                    readyForUpdate = true;
                }
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }

            @Override
            public void onProviderEnabled(String provider) {

            }

            @Override
            public void onProviderDisabled(String provider) {
            }
        };

        locationManager.requestLocationUpdates(provider, time, distanceChange, myLocationlistener);
        return START_NOT_STICKY;
    }

    //diese Methode checkt, ob es eine distanz gibt, die kleiner alls 1000meter ist. Sollte das
    //der Fall sein, ,wird nicht über neue Blitzer informiert. Sind alle Blitzer weiter weg.
    // als 1000meter. Ab da ist das System wieder fähig neue Updates zu erhalten.
    // Das wird gemacht, damit man nicht von Notification überschwemmt wird.
    // Sobald eine Notification ausgelöst wurden, wird keine Mehr generiert,
    // bis man sich genug weit  voM Blitzer entfernt hat.
    private boolean checkAllDistance(double[] strecke) {
        for (double zahl : strecke) {
            if (zahl < 1000) {
                return false;
            }
        }
        return true;
    }

    @Override
    public IBinder onBind(Intent intent) {

        Log.d("serviceTest", "I'm in the onBind");
        return null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d("serviceTest", "onDestroy wurde aufgerufen");
        //Diese Methode .removeUpdates() zerstért den LocationListener Thread
        if (myLocationlistener != null) {
            locationManager.removeUpdates(myLocationlistener);
            Log.d("thread", "locatin listenet wurde geloescht");
        }

    }
}
