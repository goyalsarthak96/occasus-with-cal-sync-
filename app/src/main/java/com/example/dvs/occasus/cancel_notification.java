package com.example.dvs.occasus;


import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.widget.Toolbar;
import android.widget.Toast;

public class cancel_notification extends BroadcastReceiver{


    public static final String notif_MyPREFERENCES = "notifpref";
    SharedPreferences notif_sharedpreferences;


    @Override
    public void onReceive(Context context, Intent intent) {
        NotificationManager nm = (NotificationManager) context.getSystemService(context.NOTIFICATION_SERVICE);
        //---cancel the notification that we started---

        notif_sharedpreferences = context.getSharedPreferences(notif_MyPREFERENCES, Context.MODE_PRIVATE);

        int id=notif_sharedpreferences.getInt("notif_id",0);

        //Toast.makeText(context,"hellyeah",Toast.LENGTH_LONG).show();


        nm.cancel(id);
        //nm.cancel(id);

    }
}
