package com.dopplereffekt.dopperlertogo;


import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.media.RingtoneManager;
import android.os.Bundle;

/**
 * Created by dsantagata
 */

// TODO evtl wenn möglich eine grössere Notification erstellen

    public class NotifyMessage extends Activity {
    int color = 0xff3333; //starkes Rot
    String adresse;
    double speed;
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);

            final Bundle extras = getIntent().getExtras();

            adresse = extras.getString("ort");
            speed = extras.getDouble("speed");

            switch (extras.getInt("uebergabe")){
                case 1:{
                    createNotification();
                }break;

                default:{}break;
            }
            finish();
        }


    //Das ist der Code für die erstellung einer Notification. Dazu ist es nicht eine "übliche" notification
    //es ist vom Style "bigTextStyle" das heisst es kann mehr Information als gewöhnlich mitgegeben werden.
    public void createNotification(){
        final Notification.Builder builder = new Notification.Builder(this);
        builder.setStyle(new Notification.BigTextStyle(builder)
                .bigText(adresse)
                .setBigContentTitle("Achtung")
                .setSummaryText("Ihre Geschwindikgeit: " + (int)speed + "km/h"))
                .setContentTitle("Achtung")
                .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                .setContentText("In Ihrer Näh befindet sich ein Blitzer")
                .setSmallIcon(R.drawable.doppler)
                .setColor(color)
                .setVibrate(new long[]{100, 200, 100, 500});

        final NotificationManager nm = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        nm.notify(0, builder.build());
    }

}
