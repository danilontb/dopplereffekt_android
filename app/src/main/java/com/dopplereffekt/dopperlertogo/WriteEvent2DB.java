package com.dopplereffekt.dopperlertogo;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;

/**
 * Created by dsantagata on 07.06.2015.
 */
public class WriteEvent2DB extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        final Bundle extras = getIntent().getExtras();

        Log.d("write2db", "etwas ist angekommen" + extras.getString("event") + " " + extras.getString("comment"));
    }
}
