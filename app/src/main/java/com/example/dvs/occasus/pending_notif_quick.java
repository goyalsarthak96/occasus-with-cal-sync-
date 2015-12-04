package com.example.dvs.occasus;


import android.app.AlarmManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.ConnectivityManager;
import android.net.wifi.WifiManager;
import android.support.v4.app.NotificationCompat;
import android.widget.Toast;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Calendar;

public class pending_notif_quick extends BroadcastReceiver{

    Context context1;
    public static final String quickSilent = "quick_silent";
    SharedPreferences quick_sharedpreferences;
    SharedPreferences.Editor quick_editor;
    //initializing shared preferences

    String database_name="";
    String description=null;


    int bluetooth_state;
    int wifi_state;
    int mobiledata_state;
    int profile_state;

    public static final String MyPREFERENCES = "MyPrefs";
    SharedPreferences sharedpreferences;
    SharedPreferences.Editor editor;


    public static final String notif_MyPREFERENCES = "notifpref";
    SharedPreferences notif_sharedpreferences;
    SharedPreferences.Editor notif_editor;

    @Override
    public void onReceive(Context context, Intent intent) {

        context1=context;

        sharedpreferences = context.getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        editor = sharedpreferences.edit();


        quick_sharedpreferences = context.getSharedPreferences(quickSilent, Context.MODE_PRIVATE);
        quick_editor = quick_sharedpreferences.edit();

        notif_sharedpreferences = context.getSharedPreferences(notif_MyPREFERENCES, Context.MODE_PRIVATE);
        notif_editor = notif_sharedpreferences.edit();

        quick_editor.putInt("quick_silent_running", 0);
        quick_editor.commit();

        quick_editor.putInt("quick_cancelled",1);
        quick_editor.commit();


        //getting mediaplayer
        final MediaPlayer mp = MediaPlayer.create(context, R.raw.notification);
        //mp set to play notification.mp3(stored in res/raw)
        mp.setOnCompletionListener(new MediaPlayer.OnCompletionListener()//called when mp3 ends
        {
            @Override
            public void onCompletion(MediaPlayer mp) {
                // TODO Auto-generated method stub
                mp.reset();             //reset release and setting null necessary....otherwise some problems might occur
                mp.release();
                mp = null;
            }
        });


        if(sharedpreferences.getString("notif_alarm","Alarm and Notification")
                .equals("Alarm only"))
        {
            mp.start();
        }
        else if(sharedpreferences.getString("notif_alarm","Alarm and Notification")
                .equals("Alarm and Notification"))
        {
            displayNotification();//denoting end of the event
            mp.start();
        }
        else
        {
            displayNotification();//denoting end of the event
        }


        if(quick_sharedpreferences.getInt("overlap",0)==1)
        {
            if (sharedpreferences.getInt("event_running", 0) == 1)
            {
                Cursor c;
                DBAdapter db = new DBAdapter(context);
                db.open();
                c = db.getEventDetail(quick_sharedpreferences.getInt("quick_id", 0));
                c.moveToFirst();
                database_name = c.getString(c.getColumnIndex("event_name"));
                if(c.getString(c.getColumnIndex("description"))!=null)
                    description=c.getString(c.getColumnIndex("description"));

                if (quick_sharedpreferences.getInt("event started before quick", 0) == 0) {

                    if(sharedpreferences.getString("notif_alarm","Alarm and Notification")
                            .equals("Alarm only"))
                    {
                        mp.start();
                    }
                    else if(sharedpreferences.getString("notif_alarm","Alarm and Notification")
                            .equals("Alarm and Notification"))
                    {
                        displayNotification1();//denoting end of the event
                        mp.start();
                    }
                    else
                    {
                        displayNotification1();//denoting end of the event
                    }

                }

                BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
                //to set the bluetooth state when event starts
                if (c.getString(c.getColumnIndex("bluetooth")).equals("yes"))//if bluetooth is "yes" in database for the running event
                {
                    mBluetoothAdapter.enable();//turn bluetooth on
                } else//if bluetooth is "no" in database for the running event
                {
                    mBluetoothAdapter.disable();//turn bluetooth off
                }


                WifiManager wifi;
                wifi = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
                //to set the wifi state when event starts
                if (c.getString(c.getColumnIndex("wifi")).equals("yes"))//if wifi is "yes" in database for the running event
                {
                    wifi.setWifiEnabled(true);//turn wifi on
                } else//if wifi is "no" in database for the running event
                {
                    wifi.setWifiEnabled(false);//turn wifi off
                }


                //to set the mobile data when event starts
                final ConnectivityManager conman =
                        (ConnectivityManager) context.getSystemService(context.CONNECTIVITY_SERVICE);
                if (c.getString(c.getColumnIndex("mobile_data")).equals("yes"))
                //if mobile data is "yes" in database for the running event
                {
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
                        setMobileDataEnabledMethod.invoke(iConnectivityManager, true);//turn mobile data on

                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                } else//if mobile data is "no" in database for the running event
                {
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
                        setMobileDataEnabledMethod.invoke(iConnectivityManager, false);//turn mobile data off

                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }


                AudioManager MyAudioManager;
                MyAudioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);//getting audio manager
                if (c.getString(c.getColumnIndex("profile")).equals("silent"))//if profile is "silent" for running event in database
                {
                    MyAudioManager.setRingerMode(AudioManager.RINGER_MODE_SILENT);//set phone to silent mode
                } else if (c.getString(c.getColumnIndex("profile")).equals("ring"))//if profile is "ring" for running event in database
                {
                    MyAudioManager.setRingerMode(AudioManager.RINGER_MODE_NORMAL);//set phone to ring mode
                } else//if profile is "vibrate" for running event in database
                {
                    MyAudioManager.setRingerMode(AudioManager.RINGER_MODE_VIBRATE);//set phone to vibrate mode
                }


                db.close();

            }
            else {


                if (quick_sharedpreferences.getInt("event started before quick", 0) == 1) {

                }
                else
                {

                    if(sharedpreferences.getString("notif_alarm","Alarm and Notification")
                            .equals("Alarm only"))
                    {
                        mp.start();
                    }
                    else if(sharedpreferences.getString("notif_alarm","Alarm and Notification")
                            .equals("Alarm and Notification"))
                    {
                        displayNotification1();//denoting end of the event
                        mp.start();
                    }
                    else
                    {
                        displayNotification1();//denoting end of the event
                    }


                }

                if(sharedpreferences.getString("notif_alarm","Alarm and Notification")
                        .equals("Alarm only"))
                {
                    mp.start();
                }
                else if(sharedpreferences.getString("notif_alarm","Alarm and Notification")
                        .equals("Alarm and Notification"))
                {
                    displayNotification2();//denoting end of the event
                    mp.start();
                }
                else
                {
                    displayNotification2();//denoting end of the event
                }


                //getting the system state that was just before the event started from shared preferences
                bluetooth_state = quick_sharedpreferences.getInt("quick_bluetooth_state", 0);
                wifi_state = quick_sharedpreferences.getInt("quick_wifi_state",0);
                mobiledata_state = quick_sharedpreferences.getInt("quick_mobiledata_state",0);
                profile_state = quick_sharedpreferences.getInt("quick_profile_state", 0);
                //6 is the default value



                BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
                if (bluetooth_state == 1) //if bluetooth was on before start of event
                {
                    mBluetoothAdapter.enable();//turn on bluetooth
                } else //if bluetooth was on before start of event
                {
                    mBluetoothAdapter.disable(); //turn off bluetooth
                }


                WifiManager wifi;
                wifi = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
                if (wifi_state == 1) //if wifi was on before start of event
                {
                    wifi.setWifiEnabled(true);//turn on wifi
                }
                else //if wifi was off before start of event
                {
                    wifi.setWifiEnabled(false);//turn off wifi
                }


                final ConnectivityManager conman =
                        (ConnectivityManager) context.getSystemService(context.CONNECTIVITY_SERVICE);
                if (mobiledata_state == 1) //if mobile data was on before start of event
                {
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
                        setMobileDataEnabledMethod.invoke(iConnectivityManager, true);//turn mobile data on
                    }
                    catch (Exception e) {
                        e.printStackTrace();
                    }

                }
                else//if mobile data was off before start of event
                {
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
                        setMobileDataEnabledMethod.invoke(iConnectivityManager, false);//turn mobile data off
                    }
                    catch (Exception e) {
                        e.printStackTrace();
                    }
                }


                AudioManager MyAudioManager;
                MyAudioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);//get audio manager
                if (profile_state == 1) //if profile was silent before event started
                {
                    MyAudioManager.setRingerMode(AudioManager.RINGER_MODE_SILENT);//set phone to silent
                }
                else if (profile_state == 3) //if profile was ring before event started
                {
                    MyAudioManager.setRingerMode(AudioManager.RINGER_MODE_NORMAL);//set phone to ring
                }
                else//if profile was vibrate before event started
                {
                    MyAudioManager.setRingerMode(AudioManager.RINGER_MODE_VIBRATE);//set phone to vibrate
                }




                //mp3 starts playing indicating the start of the event

                /*} else {

                    AudioManager am = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);


                    switch (quick_sharedpreferences.getInt("quick_profile_state", 0)) {
                        case 1:
                            am.setRingerMode(AudioManager.RINGER_MODE_SILENT);//set phone to silent
                            break;
                        case 2:
                            am.setRingerMode(AudioManager.RINGER_MODE_VIBRATE);
                            break;
                        case 3:
                            am.setRingerMode(AudioManager.RINGER_MODE_NORMAL);
                            break;
                    }

                    if (quick_sharedpreferences.getInt("event started before quick", 3) == 0) {
                        displayNotification1();
                        mp.start();//mp3 starts playing indicating the start of the event


                        displayNotification2();
                        mp.start();//mp3 starts playing indicating the start of the event
                    }
                }*/

            }
        }
        else {

            AudioManager am = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);


            switch (quick_sharedpreferences.getInt("quick_profile_state", 0)) {
                case 1:
                    am.setRingerMode(AudioManager.RINGER_MODE_SILENT);//set phone to silent
                    break;
                case 2:
                    am.setRingerMode(AudioManager.RINGER_MODE_VIBRATE);
                    break;
                case 3:
                    am.setRingerMode(AudioManager.RINGER_MODE_NORMAL);
                    break;
            }
        }

    }




    @SuppressWarnings("deprecation")
    public void displayNotification()
    {
        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(context1)
                        .setSmallIcon(R.drawable.occasus1)
                        .setContentTitle("Occasus")
                        .setContentText("Quick Silent ends");

        int mNotificationId = 1;
        NotificationManager mNotifyMgr =
                (NotificationManager) context1.getSystemService(context1.NOTIFICATION_SERVICE);
        mNotifyMgr.notify(mNotificationId, mBuilder.build());


        Calendar cancel_cal=Calendar.getInstance();
        cancel_cal.add(Calendar.MINUTE, 5);
        Intent intent=new Intent(context1,cancel_notification.class);
        notif_editor.putInt("notif_id",1);
        notif_editor.commit();
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context1, 1, intent, 0);
        AlarmManager alarmManager = (AlarmManager) context1.getSystemService(Context.ALARM_SERVICE);
        alarmManager.set(AlarmManager.RTC_WAKEUP, cancel_cal.getTimeInMillis(), pendingIntent);
        /*Intent i = new Intent(context1, NotificationView.class);//moves control to notificationView class
        i.putExtra("notificationID",1);

        PendingIntent pendingIntent = PendingIntent.getActivity(context1,0,i,0);

        NotificationManager nm = (NotificationManager)context1.getSystemService(context1.NOTIFICATION_SERVICE);


        String output="Quick Silent ends";

        Notification notif = new Notification(
                R.drawable.occasus1,output,System.currentTimeMillis()
        );

        CharSequence from = "Occasus";


        notif.setLatestEventInfo(context1, from, output, pendingIntent);
        nm.notify(1, notif);*/
    }



    @SuppressWarnings("deprecation")
    public void displayNotification1()
    {
        Intent i=new Intent(context1,pending_notif_events.class);
        PendingIntent pending_event=PendingIntent.getBroadcast(context1, 0, i, 0);
        NotificationCompat.Builder mBuilder;
                if(description==null) {
                    mBuilder=
                    new NotificationCompat.Builder(context1)
                            .setSmallIcon(R.drawable.occasus1)
                            .setContentTitle("Occasus")
                            .setContentText("Event " + database_name + " starts")
                            .addAction(R.drawable.ic_launcher, "Cancel", pending_event);
                }
                else
                {
                    mBuilder=
                            new NotificationCompat.Builder(context1)
                                    .setSmallIcon(R.drawable.occasus1)
                                    .setContentTitle("Occasus")
                                    .setContentText("Event " + database_name + " starts")
                                    .setSubText(description)
                                    .addAction(R.drawable.ic_launcher, "Cancel", pending_event);
                }

        int mNotificationId = 2;
        NotificationManager mNotifyMgr =
                (NotificationManager) context1.getSystemService(context1.NOTIFICATION_SERVICE);
        mNotifyMgr.notify(mNotificationId, mBuilder.build());

        /*Intent i = new Intent(context1, NotificationView.class);//moves control to notificationView class
        i.putExtra("notificationID",2);

        PendingIntent pendingIntent = PendingIntent.getActivity(context1,0,i,0);

        NotificationManager nm = (NotificationManager)context1.getSystemService(context1.NOTIFICATION_SERVICE);

        Notification notif = new Notification(
                R.drawable.occasus1,"Event "+database_name+" starts",System.currentTimeMillis()
        );

        CharSequence from = "Occasus";
        CharSequence message ="Event " +database_name+" starts";//message appearing at notification

        notif.setLatestEventInfo(context1,from,message,pendingIntent);
        nm.notify(2,notif);*/
    }


    @SuppressWarnings("deprecation")
    public void displayNotification2()
    {
        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(context1)
                        .setSmallIcon(R.drawable.occasus1)
                        .setContentTitle("Occasus")
                        .setContentText("Event "+database_name+" ends");

        int mNotificationId = 2;
        NotificationManager mNotifyMgr =
                (NotificationManager) context1.getSystemService(context1.NOTIFICATION_SERVICE);
        mNotifyMgr.notify(mNotificationId, mBuilder.build());


        Calendar cancel_cal=Calendar.getInstance();
        cancel_cal.add(Calendar.MINUTE, 5);
        Intent intent=new Intent(context1,cancel_notification.class);
        notif_editor.putInt("notif_id",2);
        notif_editor.commit();
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context1, 2, intent, 0);
        AlarmManager alarmManager = (AlarmManager) context1.getSystemService(Context.ALARM_SERVICE);
        alarmManager.set(AlarmManager.RTC_WAKEUP, cancel_cal.getTimeInMillis(), pendingIntent);
        /*Intent i = new Intent(context1, NotificationView.class);
        i.putExtra("notificationID",2);

        PendingIntent pendingIntent = PendingIntent.getActivity(context1,0,i,0);

        NotificationManager nm = (NotificationManager)context1.getSystemService(context1.NOTIFICATION_SERVICE);

        Notification notif = new Notification(
                R.drawable.occasus1,"Event "+database_name+" ends",System.currentTimeMillis()
        );

        CharSequence from = "Occasus";
        CharSequence message = "Event "+database_name+" ends";

        notif.setLatestEventInfo(context1, from, message, pendingIntent);
        nm.notify(2, notif);*/
    }
}
