package com.example.dvs.occasus;



import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import android.database.Cursor;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.ConnectivityManager;
import android.net.wifi.WifiManager;
import android.widget.Toast;


import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Calendar;
import java.util.GregorianCalendar;

public class deactivate_event extends BroadcastReceiver {

    public static final String MyPREFERENCES = "MyPrefs";
    int bluetooth_state;
    int wifi_state;
    int mobiledata_state;
    int profile_state;
    Calendar calSet1;
    Context context1;
    int ret_id;
    String database_name;

    int notificationID = 1;

    String ret_name;
    public void onReceive(final Context context, Intent intent) {


        DBAdapter db= new DBAdapter(context);
        db.open();
        Cursor c=null;

        Calendar calNow = Calendar.getInstance();

          //  Toast.makeText(context,"deactivate called",Toast.LENGTH_SHORT).show();
        calSet1 = (Calendar) calNow.clone();
        context1=context;
        ret_id=intent.getIntExtra("event_id",ret_id);
        ret_name=intent.getStringExtra("name");

        try {

            c = db.getEventsDetail(ret_name);


            if((c.moveToFirst())&&(c!=null))
            {



                database_name=c.getString(c.getColumnIndex("event_name"));
                displayNotification();
                final MediaPlayer mp = MediaPlayer.create(context, R.raw.notification);

                //final MediaPlayer mp = MediaPlayer.create(this, R.raw.whistle);

                mp.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {


                    @Override
                    public void onCompletion(MediaPlayer mp) {
                        // TODO Auto-generated method stub
                        mp.reset();
                        mp.release();
                        mp = null;
                        // Toast.makeText(context, "mp3 done", Toast.LENGTH_SHORT).show();

                    }

                });
                mp.start();




//        Toast.makeText(context,"current time ="+calNow.get(Calendar.HOUR_OF_DAY)+":"+calNow.get(Calendar.MINUTE),Toast.LENGTH_SHORT).show();

                bluetooth_state = context.getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE).getInt("bluetooth_state", 6);
                // Toast.makeText(context, "bluetooth=" + Integer.toString(bluetooth_state), Toast.LENGTH_SHORT).show();
                // Toast.makeText(context, "bluetooth=", Toast.LENGTH_SHORT).show();
                wifi_state = context.getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE).getInt("wifi_state", 6);

                mobiledata_state = context.getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE).getInt("mobiledata_state", 6);
                profile_state = context.getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE).getInt("profile_state", 6);
                BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
                if (bluetooth_state == 1) {
                    mBluetoothAdapter.enable();
                } else {
                    mBluetoothAdapter.disable();
                }


                WifiManager wifi;
                wifi = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
                if (wifi_state == 1) {
                    wifi.setWifiEnabled(true);
                } else {
                    wifi.setWifiEnabled(false);
                }


                final ConnectivityManager conman =
                        (ConnectivityManager) context.getSystemService(context.CONNECTIVITY_SERVICE);

                if (mobiledata_state == 1) {

                    try {
                        final Class conmanClass = Class.forName(conman.getClass().getName());

                        final Field iConnectivityManagerField = conmanClass.getDeclaredField("mService");


                        iConnectivityManagerField.setAccessible(true);

                        final Object iConnectivityManager = iConnectivityManagerField.get(conman);


                        final Class iConnectivityManagerClass =
                                Class.forName(iConnectivityManager.getClass().getName());

                        final Method setMobileDataEnabledMethod =
                                iConnectivityManagerClass
                                        .getDeclaredMethod("setMobileDataEnabled", Boolean.TYPE);


                        setMobileDataEnabledMethod.setAccessible(true);
                        setMobileDataEnabledMethod.invoke(iConnectivityManager, true);


                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                } else {

                    try {
                        final Class conmanClass = Class.forName(conman.getClass().getName());

                        final Field iConnectivityManagerField = conmanClass.getDeclaredField("mService");


                        iConnectivityManagerField.setAccessible(true);

                        final Object iConnectivityManager = iConnectivityManagerField.get(conman);


                        final Class iConnectivityManagerClass =
                                Class.forName(iConnectivityManager.getClass().getName());

                        final Method setMobileDataEnabledMethod =
                                iConnectivityManagerClass
                                        .getDeclaredMethod("setMobileDataEnabled", Boolean.TYPE);


                        setMobileDataEnabledMethod.setAccessible(true);
                        setMobileDataEnabledMethod.invoke(iConnectivityManager, false);

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }


                AudioManager MyAudioManager;

                MyAudioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
                if (profile_state == 1) {
                    MyAudioManager.setRingerMode(AudioManager.RINGER_MODE_SILENT);
                } else if (profile_state == 3) {
                    MyAudioManager.setRingerMode(AudioManager.RINGER_MODE_NORMAL);
                } else {
                    MyAudioManager.setRingerMode(AudioManager.RINGER_MODE_VIBRATE);
                }
                //Toast.makeText(context,"repeat not done in deactivate",Toast.LENGTH_SHORT).show();


                int rep = intent.getIntExtra("rep", 0);
                if (rep == 1) {

                    calSet1.add(Calendar.DAY_OF_MONTH, 7);

                    endeve(calSet1);
                }
            }
      //  Toast.makeText(context,"repeat done in deactivate",Toast.LENGTH_SHORT).show();
        }
        catch (Exception e)
        {

        }
    }



    public void displayNotification(){
        Intent i = new Intent(context1, NotificationView.class);
        i.putExtra("notificationID",notificationID);

        PendingIntent pendingIntent = PendingIntent.getActivity(context1,0,i,0);

        NotificationManager nm = (NotificationManager)context1.getSystemService(context1.NOTIFICATION_SERVICE);

        Notification notif = new Notification(
                R.drawable.ic_launcher,"Reminder:Meeting starts in 5 minutes",System.currentTimeMillis()
        );

        CharSequence from = "SystemAlarm";
        CharSequence message = "event "+database_name+" ends";

        notif.setLatestEventInfo(context1,from,message,pendingIntent);
        nm.notify(notificationID,notif);
    }

    //creates pending intent for end time
    private void endeve(Calendar targetCal){



        Intent intent = new Intent(context1, deactivate_event.class);
        intent.putExtra("name",ret_name);
        intent.putExtra("rep",1);


        PendingIntent pendingIntent = PendingIntent.getBroadcast(context1,ret_id , intent, 0);
        AlarmManager alarmManager = (AlarmManager)context1.getSystemService(Context.ALARM_SERVICE);
        alarmManager.set(AlarmManager.RTC_WAKEUP, targetCal.getTimeInMillis(), pendingIntent);

    }


}
