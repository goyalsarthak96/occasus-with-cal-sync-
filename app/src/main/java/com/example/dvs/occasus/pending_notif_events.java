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

public class pending_notif_events extends BroadcastReceiver{


    int bluetooth_state;
    int wifi_state;
    int mobiledata_state;
    int profile_state;
    Context context1;

    String database_name;

    int id;

    public static final String quickSilent = "quick_silent";
    SharedPreferences quick_sharedpreferences;
    SharedPreferences.Editor quick_editor;

    public static final String MyPREFERENCES = "MyPrefs";
    SharedPreferences sharedpreferences;

    public static final String notif_MyPREFERENCES = "notifpref";
    SharedPreferences notif_sharedpreferences;
    SharedPreferences.Editor notif_editor;

    @Override
    public void onReceive(Context context, Intent intent) {
        //Toast.makeText(context,"event end",Toast.LENGTH_SHORT).show();

        context1=context;

        //id=intent.getIntExtra("id", 0);//id of event whose pending intent is being called


        DBAdapter db= new DBAdapter(context);
        db.open();
        Cursor c;



        //shared preferences editor declared
        sharedpreferences = context.getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor;
        //initializing shared preferences
        editor = sharedpreferences.edit();



        quick_sharedpreferences = context.getSharedPreferences(quickSilent, Context.MODE_PRIVATE);
        quick_editor = quick_sharedpreferences.edit();


        notif_sharedpreferences = context.getSharedPreferences(notif_MyPREFERENCES, Context.MODE_PRIVATE);
        notif_editor = notif_sharedpreferences.edit();


        id=sharedpreferences.getInt("id",2);

        int running_id=context.getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE).getInt("running_id", 6);


        int run= context.getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE).getInt("event_running", 6);
        //checks if the event is still running
        //i.e. it wasn't deleted while it was running


        try
        {
            c = db.getEventDetail(id);//check if the event still is present in the database
            // i.e. it wasn't deleted before even the event started

            c.moveToFirst();



            if((running_id==id)&&(run==1))//if event is in database and it is running
            {

                database_name=c.getString(c.getColumnIndex("event_name"));

                editor.putInt("event_running", 0);//set event_running field to 0 in shared prefernces
                //coz event is not running now
                editor.commit();


                editor.putInt("event_cancelled_id", id);
                editor.commit();


                if(quick_sharedpreferences.getInt("overlap",0)==0)
                {

                    final MediaPlayer mp = MediaPlayer.create(context, R.raw.notification);//getting media player
                    mp.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                        @Override
                        public void onCompletion(MediaPlayer mp) {
                            // TODO Auto-generated method stub
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
                        displayNotification2();//denoting end of the event
                        mp.start();
                    }
                    else
                    {
                        displayNotification2();//denoting end of the event
                    }



                    //getting the system state that was just before the event started from shared preferences
                    bluetooth_state = context.getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE).getInt("bluetooth_state", 6);
                    wifi_state= context.getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE).getInt("wifi_state", 6);
                    mobiledata_state = context.getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE).getInt("mobiledata_state", 6);
                    profile_state = context.getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE).getInt("profile_state", 6);
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
                    } else //if wifi was off before start of event
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
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                    } else//if mobile data was off before start of event
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
                    MyAudioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);//get audio manager
                    if (profile_state == 1) //if profile was silent before event started
                    {
                        MyAudioManager.setRingerMode(AudioManager.RINGER_MODE_SILENT);//set phone to silent
                    } else if (profile_state == 3) //if profile was ring before event started
                    {
                        MyAudioManager.setRingerMode(AudioManager.RINGER_MODE_NORMAL);//set phone to ring
                    } else//if profile was vibrate before event started
                    {
                        MyAudioManager.setRingerMode(AudioManager.RINGER_MODE_VIBRATE);//set phone to vibrate
                    }

                }


                else {

                    if (quick_sharedpreferences.getInt("quick_silent_running", 0) == 0) {


                        final MediaPlayer mp = MediaPlayer.create(context, R.raw.notification);//getting media player
                        mp.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                            @Override
                            public void onCompletion(MediaPlayer mp) {
                                // TODO Auto-generated method stub
                                mp.reset();
                                mp.release();
                                mp = null;
                            }
                        });

                        //getting the system state that was just before the event started from shared preferences
                        bluetooth_state = context.getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE).getInt("bluetooth_state", 6);
                        wifi_state= context.getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE).getInt("wifi_state", 6);
                        mobiledata_state = context.getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE).getInt("mobiledata_state", 6);
                        profile_state = context.getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE).getInt("profile_state", 6);
                        //6 is the default value

                        /*if(quick_sharedpreferences.getInt("event started before quick", 1) == 1)
                        {

                        }
                        else
                        {
                            displayNotification1();
                            mp.start();
                        }*/




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
                        } else //if wifi was off before start of event
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
                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                        } else//if mobile data was off before start of event
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
                        MyAudioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);//get audio manager
                        if (profile_state == 1) //if profile was silent before event started
                        {
                            MyAudioManager.setRingerMode(AudioManager.RINGER_MODE_SILENT);//set phone to silent
                        } else if (profile_state == 3) //if profile was ring before event started
                        {
                            MyAudioManager.setRingerMode(AudioManager.RINGER_MODE_NORMAL);//set phone to ring
                        } else//if profile was vibrate before event started
                        {
                            MyAudioManager.setRingerMode(AudioManager.RINGER_MODE_VIBRATE);//set phone to vibrate
                        }

                    }
                    else
                    {

                    }
                }


                String rep=sharedpreferences.getString("rep","jk");
                editor.putInt("interval",c.getInt(c.getColumnIndex("days_bw_start_n_end")));
                editor.commit();
                if (rep.charAt(0) != '0') {

                    if (sharedpreferences.getInt("delete_the_event", 0) == 0) {


                    } else {

                        DBAdapter db1 = new DBAdapter(context1);
                        db1.deleteEvent(id);
                    }
                } else {

                    db.deleteEvent(id);//delete the event from database
                }
            }
        }
        catch (Exception e)
        {

        }

    }




    //to display a notification
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
        i.putExtra("notificationID",notificationID);

        PendingIntent pendingIntent = PendingIntent.getActivity(context1,0,i,0);

        NotificationManager nm = (NotificationManager)context1.getSystemService(context1.NOTIFICATION_SERVICE);

        Notification notif = new Notification(
                R.drawable.occasus1,"Event "+database_name+" ends",System.currentTimeMillis()
        );

        CharSequence from = "Occasus";
        CharSequence message = "Event "+database_name+" ends";

        notif.setLatestEventInfo(context1, from, message, pendingIntent);
        nm.notify(notificationID, notif);*/
    }

}
