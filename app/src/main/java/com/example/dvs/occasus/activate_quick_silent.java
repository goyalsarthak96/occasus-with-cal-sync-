package com.example.dvs.occasus;


import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.ConnectivityManager;
import android.net.wifi.WifiManager;

import java.lang.reflect.Method;


public class activate_quick_silent extends BroadcastReceiver{

    public static final String quickSilent = "quick_silent";
    SharedPreferences quick_sharedpreferences;
    //shared preferences editor declared
    SharedPreferences.Editor quick_editor;
    //initializing shared preferences



    public static final String MyPREFERENCES = "MyPrefs";
    SharedPreferences sharedpreferences;
    SharedPreferences.Editor editor;

    Context context1;

    @Override
    public void onReceive(Context context, Intent intent) {

        context1=context;

        quick_sharedpreferences = context.getSharedPreferences(quickSilent, Context.MODE_PRIVATE);
        quick_editor = quick_sharedpreferences.edit();

        quick_editor.putInt("quick_silent_running", 1);
        quick_editor.commit();


        sharedpreferences = context.getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        editor = sharedpreferences.edit();


        AudioManager am = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);


        if(sharedpreferences.getInt("event_running",0)==1)
        {
            quick_editor.putInt("overlap", 1);
            quick_editor.commit();
            quick_editor.putInt("quick_bluetooth_state", sharedpreferences.getInt("bluetooth_state", 0));
            quick_editor.commit();
            quick_editor.putInt("quick_wifi_state",sharedpreferences.getInt("wifi_state",0));
            quick_editor.commit();
            quick_editor.putInt("quick_mobiledata_state",sharedpreferences.getInt("mobiledata_state",0));
            quick_editor.commit();
            quick_editor.putInt("quick_profile_state",sharedpreferences.getInt("profile_state",0));
            quick_editor.commit();
        }
        else {

            quick_editor.putInt("overlap", 0);
            quick_editor.commit();


            BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
            if (mBluetoothAdapter.isEnabled())//if bluetooth was enabled just before the start of event
            {
                quick_editor.putInt("quick_bluetooth_state", 1);
                //in shared preferences, field -> bluetooth state = 1....it means bluetooth was on just before the event started
                quick_editor.commit();//changes committed in shared preferences
            } else//if bluetooth was not enabled just before event started
            {
                quick_editor.putInt("quick_bluetooth_state", 0);
                //in shared preferences, field -> bluetooth state = 0....it means bluetooth was off just before the event started
                quick_editor.commit();//changes committed in shared preferences
            }


            //getting wifi manager
            WifiManager wifi;
            wifi = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
            if (wifi.isWifiEnabled())//if wifi was enabled just before event started
            {
                quick_editor.putInt("quick_wifi_state", 1);
                //in shared preferences, field -> wifi_state = 1....it means wifi was on just before start of the event
                quick_editor.commit();//changes committed in shared preferences
            } else//if wifi wasn't enabled just before start of the event
            {
                quick_editor.putInt("quick_wifi_state", 0);
                //in shared preferences field, wifi_state = 1.....it means wifi was on just before start of event
                quick_editor.commit();//changes committed in shared preferences

            }


            //checking if mobile data was on or off before event starts
            boolean mobileDataEnabled = false; // Assume disabled
            ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            try {
                Class cmClass = Class.forName(cm.getClass().getName());
                Method method = cmClass.getDeclaredMethod("getMobileDataEnabled");
                method.setAccessible(true); // Make the method callable
                // get the setting for "mobile data"
                mobileDataEnabled = (Boolean) method.invoke(cm);
            } catch (Exception e) {
                // Some problem accessible private API
                // TODO do whatever error handling you want here
            }
            if (mobileDataEnabled)//if mobile data was on before event started
            {
                quick_editor.putInt("quick_mobiledata_state", 1);
                //in shared preferences, field -> mobiledata_state = 1.....means mobile data was on before start of event
                quick_editor.commit();//changes committed in shared preferences
            } else {
                quick_editor.putInt("quick_mobiledata_state", 0);
                //in shared preferences, field -> mobiledata_state = 0.....means mobile data was off before start of event
                quick_editor.commit();//changes committed in shared preferences
            }


            //getting audio manager
            switch (am.getRingerMode())//getRingerMode returns the ringing mode currently(just before start of event)
            {

                case AudioManager.RINGER_MODE_SILENT://if phone is on silent just before event starts
                    quick_editor.putInt("quick_profile_state", 1);
                    //in shared preferences, field -> profile_state = 1....means phone was on silent just before event started
                    quick_editor.commit();//changes committed in shared preferences
                    break;

                case AudioManager.RINGER_MODE_VIBRATE://if phone is on vibrate just before event starts
                    quick_editor.putInt("quick_profile_state", 2);
                    //in shared preferences, field -> profile_state = 2.....means phone was on vibrate just before event started
                    quick_editor.commit();//changes committed in shared preferences
                    break;

                case AudioManager.RINGER_MODE_NORMAL://if phones is on normal mode just before event starts
                    quick_editor.putInt("quick_profile_state", 3);
                    //in shared preferences, field -> profile_state = 3.....means phone was on normal mode just before event started
                    quick_editor.commit();//changes committed in shared preferences
                    break;
            }

        }

        am.setRingerMode(AudioManager.RINGER_MODE_SILENT);//set phone to silent mode

        final MediaPlayer mp = MediaPlayer.create(context, R.raw.notification);
        mp.setOnCompletionListener(new MediaPlayer.OnCompletionListener()//called when mp3 ends
        {
            @Override
            public void onCompletion(MediaPlayer mp) {
                mp.reset();
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

    }


    @SuppressWarnings("deprecation")
    public void displayNotification()
    {
        Intent i = new Intent(context1, NotificationView.class);//moves control to notificationView class
        i.putExtra("notificationID",1);

        PendingIntent pendingIntent = PendingIntent.getActivity(context1,0,i,0);

        NotificationManager nm = (NotificationManager)context1.getSystemService(context1.NOTIFICATION_SERVICE);


        String output;
        if(quick_sharedpreferences.getInt("options_selected",0)==1)
        {
            output="Silent for "+quick_sharedpreferences.getString("hour","")+" hours "+ quick_sharedpreferences.getString("min","")+" min";
        }
        else
        {
            output="Silent till "+quick_sharedpreferences.getString("time","");
        }
        Notification notif = new Notification(
                R.drawable.occasus1,"Quick Silent Starts",System.currentTimeMillis()
        );

        CharSequence from = "Occasus";


        notif.setLatestEventInfo(context1, from, output, pendingIntent);
        nm.notify(1, notif);
    }
}
