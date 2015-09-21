package com.dopplereffekt.dopperlertogo;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.TypedArray;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

/*
TODO GPS adressen checken ist erledigt. Als�chstes w�re checken, warum immer eine neues FIle heruntergeladen wird.
 */

public class MainActivity extends Activity {

    private ProgressDialog pDialog;
    public static LocationManager locationmanager = null;
    public static Location mylocation = null;

    public static String foldername = "dopplereffekt";
    public static String pdfname = "publicLighter";
    public static String pdfurl = "http://www.kapo.sg.ch/home/informationen/verkehr/_jcr_content/Par/downloadlist_0/DownloadListPar/download.ocFile/2014%2012%2017_Semistation%C3%A4re%20Messanlagen.pdf";


    int newDay = 0;       //Wird in internen Files gespeichert
    int oldDay = 0;       //Wird in internen Files gespeichert
    String oldFiledate = null; //Bei oldFiledate und newFiledate handelt es sich effektiv um das Datum auf dem PDF. Anhand vom aush�ndigungsdatum des File wird entschieden, ob  (n�chste zeile))
    String newFiledate = null; //die Information des Files in eine Externe Datenbank gespeichert wird. --> Nicht das gleiche wie Neuer Tag.
    public static final String PREFS_NAME = "MyPrefsFile";              //so heisst das File, dass jegliche Daten der App enth�lt.
    public static boolean wantRecieveUpdates = false;               //Bit wird gesetzt, wenn der Customer updates w�nscht.

    private static final int CLOSE_APP = 1000;                     //Das ist ein Requestcode. Dieser wird gebraucht um startActivityForResult() zu unterscheiden, von wem es kommt.
    private static final int SET_EVENT = 1001;                     //Das ist ein Requestcode. Dieser wird gebraucht um startActivityForResult() zu unterscheiden, von wem es kommt.


    String[] lighterOptions = {"fixLighter", "mobileLighter", "laserLighter", "controlePosition"};

    public static Intent backgroundservice = null;
    // public static boolean serviceLauft = false;
    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;
    private ActionBarDrawerToggle mDrawerToggle;

    // nav drawer title
    private CharSequence mDrawerTitle;

    // used to store app title
    private CharSequence mTitle;

    // slide menu items
    private String[] navMenuTitles;
    private TypedArray navMenuIcons;

    private ArrayList<NavDrawerItem> navDrawerItems;
    private NavDrawerListAdapter adapter;

    public static int mPosition;
    public static boolean mNavigationItemClicked;

    //initialisierung des Serviceintents
    public static Intent serviceGPSIntent = null;                             //Der service intent, wo immer der gleiche aufgerufen bzw gestoppt wird.

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Log.d("init", "drawer startet");

        //Als aller erster wird  der Drawer gezeichnet damit navigiert werden kann.
        createDrawer(savedInstanceState);

        //initialisierung der Files damit alle wieder auf dem neusetn stand sind. Falls n�tig werden noch dateien Heruntergeladen
        //oder andere n�tigen sachen gemacht.
        Log.d("init", "init sollte startetn");
        new initialism().execute();

        Log.d("init", "backgroundservice startet");
        //start service to download data from database
        backgroundservice = new Intent(this, Backgrounddownloading.class);
        startService(backgroundservice);
    }


    //diese Methode löst die Adresse (PLZ + Strasse) nach ihren Lngengrad auf.
    //Imfalle wenn die Adresse nicht aufgelst werden kann oder die bergebene
    //Adresse sei null, wird einfach nicht gemacht und mit der nchsten probiert...Ist nicht professionell aber naja:)
    public Location convertAddressToCoor(String[] apiadress) {

        Location lc = new Location("point A");
        Geocoder coder = new Geocoder(getApplication());
        List<Address> address;
        double lat;
        double lng;
        Backgrounddownloading.officialLighterAdresse = new ArrayList();
        Backgrounddownloading.officialLighterLng = new ArrayList();
        Backgrounddownloading.officialLighterLat = new ArrayList();

        for(String api : apiadress) {

            try {
                address = coder.getFromLocationName(api, 5);
                if (!(address == null)) {
                    android.location.Address location = address.get(0);
                    lng = location.getLongitude();
                    lat = location.getLatitude();

                    lc.setLatitude(lat);
                    lc.setLongitude(lng);

                    Backgrounddownloading.officialLighterAdresse.add(api);
                    Backgrounddownloading.officialLighterLat.add(lat);
                    Backgrounddownloading.officialLighterLng.add(lng);

                    //  lc.setLatitude(location.getLatitude());

                    Log.d("loca", lng + " " + lat);
                } else {
                    Log.d("loca", "Adresse war leer");
                }
            } catch (Exception ex) {
                Log.d("loca", "Adresse konnte nicht umgewandelt werden");
                ex.printStackTrace();
            }
        }
        return lc;
    }


    public void initialization() {
        //Die Klasse SharedPefrences hilft kleine Datens�tze in der Internen Datenbank zu Speichern.
        //Wir speichern den aktuellen Tag rein damit das PDF nur 1x am tag heruntergeladen wird.
        //Sobald die Klasse ShowLighterList aufgerufen wird. wird der Letzte gespeicherte Tag aus er Datenbank geholt und in "alterTag" gespeichert.
        //Auch der wunsch �ber Updates wird gespeichert solange wie die Activity lebt.
        SharedPreferences settings = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        oldDay = settings.getInt("Tag", 0);
        wantRecieveUpdates = settings.getBoolean("Updates", false);
        oldFiledate = settings.getString("oldFiledate", " ");
        newFiledate = settings.getString("newFiledate", " ");

        serviceGPSIntent = new Intent(this, GPSService.class);
        Log.d("filereader", " newFiledate: " + newFiledate);
        Log.d("filereader", " oldFiledate: " + oldFiledate);

        showLighterListFragment.refreshing = false;

        //�berpr�fung ob das PDF schon existiert oder ob ein neues heruntergeladen werden muss.
        if (ConvertPDF.pdfExists()) {
            Log.d("pdfcheck", "pdf existiert");
            if (ConvertPDF.contentIsIn()) {
                //�berpr�fung ob der Tag mit dem Letzten gespeicherten Tag �bereinander stimmt.
                if (neuerTag()) {
                    //ist es ein neuer Tag, wird sofort eine PDF runtergeladen.
                    download();
                    //Toast sind kurzlebige "popups" die gebraucht werden k�nnen um den Benutzer �ber irgendwelche zustands�nderungen oder
                    //Erfolge bzw Misserfolge des Prozesses zu informieren.
            //        Toast.makeText(this, "Neues File wurde heruntergeladen", Toast.LENGTH_SHORT).show();
                    Log.d("pdfcheck", "Neues File wurde heruntergeladen");
                    if (neuesFile()) {
                        // startActivity(new Intent(this, WriteAdressesInDB.class));
                        Log.d("pdfcheck", "It is a new File. I want to put the Addresses to the public database");
                    } else {
            //            Toast.makeText(this, "File ist noch immer aktuell", Toast.LENGTH_SHORT).show();
                        Log.d("pdfcheck", " File noch immer aktuell");
                    }
                } else {
                    //sollte das PDF heruntergeladen worden sein, jedoch der Inhalt noch nicht extrahiert werden konnte, wird die
                    //app geschlossen, und man muss sie manuel nochmals starten.
            //        Toast.makeText(this, "File sind alle auf dem neusten Stand", Toast.LENGTH_SHORT).show();
                    Log.d("pdfcheck", " File sind alle auf dem neusten Stand");
                }
            } else {
            //    Toast.makeText(this, "Files sind Inhaltslos. Reparatur in Arbeit.", Toast.LENGTH_SHORT).show();
                Log.d("pdfcheck", "maketoast File sind Inhaltslos Reparatur in arbeit");
                download();
            }
        } else {
            Log.d("PDF", "pdf existierte noch nicht.");
            download();
        }

        convertAddressToCoor(ConvertPDF.pdf2AdressStringForAPI());
    }

    private void createDrawer(Bundle savedInstanceState) {

        mTitle = mDrawerTitle = getTitle();
        // load slide menu items
        navMenuTitles = getResources().getStringArray(R.array.nav_drawer_items);

        // nav drawer icons from resources
        navMenuIcons = getResources()
                .obtainTypedArray(R.array.nav_drawer_icons);

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerList = (ListView) findViewById(R.id.list_slidermenu);

        navDrawerItems = new ArrayList<NavDrawerItem>();

        // adding nav drawer items to array
        // Mich warnen
        navDrawerItems.add(new NavDrawerItem(navMenuTitles[0], navMenuIcons.getResourceId(0, -1)));
        // Zeige �ffentliche Blitzer
        navDrawerItems.add(new NavDrawerItem(navMenuTitles[1], navMenuIcons.getResourceId(1, -1)));
        // Blitzer melden
        navDrawerItems.add(new NavDrawerItem(navMenuTitles[2], navMenuIcons.getResourceId(2, -1)));
        // Biltzer anschauen
        navDrawerItems.add(new NavDrawerItem(navMenuTitles[3], navMenuIcons.getResourceId(3, -1), true, "258"));
        // deine Meinung
        navDrawerItems.add(new NavDrawerItem(navMenuTitles[4], navMenuIcons.getResourceId(4, -1)));
        // �ber uns
        navDrawerItems.add(new NavDrawerItem(navMenuTitles[5], navMenuIcons.getResourceId(5, -1), true, "50+"));


        // Recycle the typed array
        navMenuIcons.recycle();

        mDrawerList.setOnItemClickListener(new SlideMenuClickListener());

        // setting the nav drawer list adapter
        adapter = new NavDrawerListAdapter(getApplicationContext(),
                navDrawerItems);
        mDrawerList.setAdapter(adapter);

        // enabling action bar app icon and behaving it as toggle button
        getActionBar().setDisplayHomeAsUpEnabled(true);
        getActionBar().setHomeButtonEnabled(true);

        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout,
                R.drawable.ic_drawer, //nav menu toggle icon
                R.string.app_name, // nav drawer open - description for accessibility
                R.string.app_name // nav drawer close - description for accessibility
        ) {
            public void onDrawerClosed(View view) {
                getActionBar().setTitle(mTitle);
                // calling onPrepareOptionsMenu() to show action bar icons

                if (mNavigationItemClicked) {
                    displayView(mPosition);
                }
                mNavigationItemClicked = false;
                //   invalidateOptionsMenu();
            }

            public void onDrawerOpened(View drawerView) {
                getActionBar().setTitle(mDrawerTitle);
                // calling onPrepareOptionsMenu() to hide action bar icons

                invalidateOptionsMenu();
            }
        };
        mDrawerLayout.setDrawerListener(mDrawerToggle);


        if (savedInstanceState == null) {
            // on first time display view for first nav item
            displayView(0);
        }


    }

    public void onButtonClick(View view) {

        switch (view.getId()) {
            case R.id.btn_position: {
                Log.d("buttonausfragment", "button position gedr�ckt. ");
                LocationManager service;
                //Der LocationManager wird ben�tigt, um abzufragen, ob der User das GPS eingeschaltet hat.
                service = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
                if (service.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                    locationmanager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
                    mylocation = locationmanager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                    Intent intent = new Intent(this, Dialogs.class);
                    intent.putExtra("mylongitude", String.valueOf(mylocation.getLongitude()));
                    intent.putExtra("mylatitude", String.valueOf(mylocation.getLatitude()));
                    Log.d("showittome", mylocation.getLongitude() + " : " + mylocation.getLatitude() + " um das gehts bitches");
                    intent.putExtra("Dialog", 3);
                    startActivity(intent);
                } else {
                    Intent intent = new Intent(this, Dialogs.class);
                    intent.putExtra("Dialog", 4);
                    startActivity(intent);
                }
            }
            break;
            case R.id.testbutton: {
            }
            break;
        }
    }


    /**
     * Slide menu item click listener
     */
    private class SlideMenuClickListener implements ListView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position,
                                long id) {
            // display view for selected nav drawer item
            mPosition = position;
            mNavigationItemClicked = true;
            mDrawerLayout.closeDrawer(mDrawerList);
            //      displayView(position);
        }
    }


    /**
     * �berpr�ft ob heute diese Methode das erste mal oder nicht aufgerufen wird.
     * sollte sie das erste mal aufgerufen worden sein, gibt sie True zur�ck und man weiss, heute ist ein neur tag.
     * Gleichzeitig wird der heutige Tag als alter Tag eingetragen, weil die Methode nun schon einmal aufgerufen worden ist.
     * Im Falle von false wurde die Methode schon mal aufgerufen.
     */
    public boolean neuerTag() {
        Date date = new Date();
        Calendar calendar = GregorianCalendar.getInstance();
        calendar.setTime(date);
        newDay = calendar.get(Calendar.DAY_OF_WEEK);
        writedownloadDay(newDay);
        if (oldDay == newDay) {
            return false;
        }else{
            oldDay = newDay;
            return true;
        }

    }

    /**
     * Diese Method �erpr�ft ob der Datumsst�mpfel mit dem Datumsstempfel des Letzten mal �bereinstimmt. Sollte dies der Fall sein wird nichts unternommen.
     * Andernfalls wird das oldFiledate geupdatet und die neu heruntergeladene Adressen werden in eine Datenbank gepeichert. oldFiledate und newFiledate werden lokal in der Datenbank gepeichert.
     */
    public boolean neuesFile() {
        newFiledate = ConvertPDF.getUpdateDate().replace(" ", "");
        if (newFiledate.equals(oldFiledate)) {
            writeUpdateDates(newFiledate, oldFiledate);
            return false;
        } else {
            oldFiledate = newFiledate;
            writeUpdateDates(newFiledate, oldFiledate);
            return true;
        }

    }


    /**
     * Mit dieser Methode wird der Status, ob man updates erhalten will, gespeichert.
     * Sollte die App in den Ruhemodus, onStop versetzt werden, wird der Status gespeichert. Nicht so wenn onDestroy aufgerufen wird.
     */
    public void writeUpdateDates(String newFiledate, String oldFiledate) {
        SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putString("newFiledate", newFiledate);
        editor.putString("oldFiledate", oldFiledate);
        // Commit the edits!
        editor.commit();
    }

    /**
     * Mit dieser Methode wird der Tag gespeichert, dan dem man ein file heruntergeladen hat. Sollte das Smartphone die App abschiessen und neu
     * starten, wird gecheckt, ob das File nochmals heruntergeladen werden muss.
     */
    public void writedownloadDay(int newday) {
        SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putInt("Tag", newday);
        // Commit the edits!
        editor.commit();
    }


    /**
     * Diese Methode wird aufgerufen, wen sogenannte Key_Events passieren. Diese Spezifische Methode meldet
     * sich sobald der KEYCODE_BACK (Zur�ckbutton) get�tigt wird...Jedoch nur, wenn diese Activity im Fokus steht.
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (event.getKeyCode()) {
            case KeyEvent.KEYCODE_BACK: {
                writeUpdateState(true);
                Intent intent = new Intent(this, Dialogs.class);
                intent.putExtra("Dialog", 2);
                startActivityForResult(intent, CLOSE_APP);
            }
            break;
            default: {
            }
            break;
        }
        return super.onKeyDown(keyCode, event);
    }


    /**
     * Diese Methode wird gestartet, wenn vorher ein startActivityForResult get�tigt wurde.
     * Sobald die gestartet Activity "stribt" geht der Focus zur�ck und die Methode onActivityResult
     * wird aufgerufen.
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            switch (requestCode) {
                case CLOSE_APP: {
                    if (data.getExtras().getBoolean("ResponseActivity")) {
                        if (WarningFragment.serviceLaeuft) {
                            wantRecieveUpdates = false;
                            writeUpdateState(wantRecieveUpdates);
                            getApplicationContext().stopService(serviceGPSIntent);
                            WarningFragment.serviceLaeuft = false;
                        }
                        finish();
                    }
                }
                break;
                case SET_EVENT: {

                }
                break;
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // toggle nav drawer on selecting action bar app icon/title
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        // Handle action bar actions click
        switch (item.getItemId()) {
            case R.id.action_settings: {
                return true;
            }
            case R.id.action_example: {
                Log.d("Example", "der button werude gedr�ckt.");
            }
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /**
     * Called when invalidateOptionsMenu() is triggered
     */
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        // if nav drawer is opened, hide the action items
        boolean drawerOpen = mDrawerLayout.isDrawerOpen(mDrawerList);
        menu.findItem(R.id.action_settings).setVisible(!drawerOpen);
        return super.onPrepareOptionsMenu(menu);
    }

    /**
     * Diplaying fragment view for selected nav drawer list item
     */
    private void displayView(int position) {
        // update the main content by replacing fragments
        Fragment fragment = null;
        switch (position) {
            case 0:
                fragment = new WarningFragment();
                break;
            case 1:
               fragment = new showLighterListFragment();
                break;
            case 2:
                fragment = new InformationOtherFragment();
                break;
            case 3:
                fragment = new OtherEvents();
                break;
            case 4:
                //fragment = new PagesFragment();
                break;
            case 5:
                //        fragment = new WhatsHotFragment();
                break;

            default:
                break;
        }

        if (fragment != null) {
            FragmentManager fragmentManager = getFragmentManager();
            fragmentManager.beginTransaction()
                    .replace(R.id.frame_container, fragment).commit();

            // update selected item and title, then close the drawer
            mDrawerList.setItemChecked(position, true);
            mDrawerList.setSelection(position);
            setTitle(navMenuTitles[position]);
            mDrawerLayout.closeDrawer(mDrawerList);
        } else {
            // error in creating fragment
            Log.e("MainActivity", "Error in creating fragment");
        }
    }


    @Override
    public void setTitle(CharSequence title) {
        mTitle = title;
        getActionBar().setTitle(mTitle);
    }

    /**
     * When using the ActionBarDrawerToggle, you must call it during
     * onPostCreate() and onConfigurationChanged()...
     */

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        // Pass any configuration change to the drawer toggls
        mDrawerToggle.onConfigurationChanged(newConfig);
    }


    private void writeUpdateState(boolean state) {
        SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putBoolean("Updates", state);
        editor.commit();
    }



    @Override
    protected void onDestroy() {

        Log.d("activity", "im onDestroy");
        writeUpdateState(false);
        writedownloadDay(newDay);
        super.onDestroy();
    }

    /**
     * Die Methode download() �bergibt einer inneren Klasse den Webpfad, wo das pdf zu finden ist, und gleichzeitig wie das pdf genannt werden muss.
     */
    public void download() {
        new DownloadFile().execute(pdfurl, pdfname);
    }

    /**
     * �ber die innere Klasse kann ich nicht viel sagen. Sie wird ben�tigt um Inhalte aus dem Web zu downloaden. Diese Klasse wird paralell ausgef�hrt.
     */
    private class DownloadFile extends AsyncTask<String, Void, Void> {
        @Override
        protected void onPreExecute() {
 /*           super.onPreExecute();
            pDialog = new ProgressDialog(MainActivity.this);
            pDialog.setTitle("Initialisierung");
            pDialog.setMessage("Vorbereitungen in Arbeit");
            pDialog.setProgressStyle(R.style.AppTheme);
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
*/
        }

        @Override
        protected Void doInBackground(String... strings) {
            String fileUrl = strings[0];   // -> pdfurl
            String fileName = strings[1];  // -> ghost.pdf
            String extStorageDirectory = Environment.getExternalStorageDirectory().toString();
            File folder = new File(extStorageDirectory, "dopplereffekt");
            folder.mkdir();
            File pdfFile = new File(folder, fileName);
            try {
                pdfFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
            FileDownloader.downloadFile(fileUrl, pdfFile);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
 //           pDialog.dismiss();
        }
    }

    /**
         * �ber die innere Klasse kann ich nicht viel sagen. Sie wird ben�tigt um Inhalte aus dem Web zu downloaden. Diese Klasse wird paralell ausgef�hrt.
         */
        private class initialism extends AsyncTask<String, Void, Void> {
            @Override
          protected void onPreExecute() {
               super.onPreExecute();
               Log.d("init", "init gestartett");
               pDialog = new ProgressDialog(MainActivity.this);
               pDialog.setTitle("Initialisierung");
               pDialog.setMessage("Daten werden vorbereitet.");
               pDialog.setProgressStyle(R.style.AppTheme);
               pDialog.setIndeterminate(false);
               pDialog.setCancelable(false);
               pDialog.show();
           }

        @Override
        protected Void doInBackground(String... strings) {


        initialization();
               return null;
            }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            pDialog.dismiss();
        }

    }



}
