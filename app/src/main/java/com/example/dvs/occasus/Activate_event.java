package com.example.dvs.occasus;


import android.app.AlarmManager;
import android.app.Notification;
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
import android.util.EventLogTags;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;


public class Activate_event extends BroadcastReceiver {

    Context context1;

    String rep;


    Calendar calSet1=Calendar.getInstance();
    int notificationID = 2;
    String database_name;
    int id;


    public static final String quickSilent = "quick_silent";
    SharedPreferences quick_sharedpreferences;
    SharedPreferences.Editor quick_editor;


    SharedPreferences.Editor editor;
    public static final String MyPREFERENCES = "MyPrefs";


    public static final String sync = "sync";
    SharedPreferences.Editor sync_editor;
    SharedPreferences sync_sharedpreferences;

    String description=null;

    @SuppressWarnings("deprecation")
    public void onReceive(final Context context, Intent intent)
    {



        //database for event details opened
        DBAdapter db = new DBAdapter(context);
        DBAdapter db3=new DBAdapter(context);
        DBAdapter db2=new DBAdapter(context);
        db.open();

        Cursor c;


        //ret_id has the id of the event whose pending intent is being executed.....it is passed from settoggles class to this class

        id=intent.getIntExtra("id",0);


        //shared preferences declared
        SharedPreferences sharedpreferences;
        //shared preferences editor declared
        sharedpreferences = context.getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);

        //initializing shared preferences
        editor = sharedpreferences.edit();




        quick_sharedpreferences = context.getSharedPreferences(quickSilent, Context.MODE_PRIVATE);
        quick_editor = quick_sharedpreferences.edit();



        try
        {

            String rep_until=intent.getStringExtra("rep_until");
            rep = intent.getStringExtra("rep");//rep passed from settoggles class to this class



            //we check if the event still exists in database......i.e. it hasn't been deleted from database
            c = db.getEventDetail(id);
            c.moveToFirst();
            //if event has been deleted then nothing happens(due to try catch) otherwise the following code is executed


            int no_of_times_event_ran=sharedpreferences.getInt("no_of_times_event_ran" + Integer.toString(id), 0);



            //if c=null => no event exist with same id in database or it has been deleted....so if statement is not executed
            if (c != null) {

                if(c.getString(c.getColumnIndex("description"))!=null)
                    description=c.getString(c.getColumnIndex("description"));


                database_name = c.getString(c.getColumnIndex("event_name"));
                //cursor c stores the details of the event whose pending intent is being executed
                //database name stores the name of the event whose pending intent is being executed


                context1 = context;
                //context1 stores the context


                no_of_times_event_ran++;

                editor.putInt("no_of_times_event_ran" + Integer.toString(id), no_of_times_event_ran);
                editor.commit();

                editor.putInt("event_running", 1);
                //in shared preferences, field -> event_running = 1....means some event is currently running
                editor.commit();//commit the changes in shared preferences

                editor.putInt("running_id", id);
                editor.commit();


                if(quick_sharedpreferences.getInt("quick_silent_running",0)==0) {

                    quick_editor.putInt("quick_id", id);
                    quick_editor.commit();

                    quick_editor.putInt("event started before quick",1);
                    quick_editor.commit();

                    quick_editor.putInt("overlap",0);
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
                        displayNotification1();//denoting end of the event
                        mp.start();
                    }
                    else
                    {
                        displayNotification1();//denoting end of the event
                    }


                    //getting bluetooth adapter
                    BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
                    if (mBluetoothAdapter.isEnabled())//if bluetooth was enabled just before the start of event
                    {
                        editor.putInt("bluetooth_state", 1);
                        //in shared preferences, field -> bluetooth state = 1....it means bluetooth was on just before the event started
                        editor.commit();//changes committed in shared preferences
                    } else//if bluetooth was not enabled just before event started
                    {
                        editor.putInt("bluetooth_state", 0);
                        //in shared preferences, field -> bluetooth state = 0....it means bluetooth was off just before the event started
                        editor.commit();//changes committed in shared preferences
                    }


                    //getting wifi manager
                    WifiManager wifi;
                    wifi = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
                    if (wifi.isWifiEnabled())//if wifi was enabled just before event started
                    {
                        editor.putInt("wifi_state", 1);
                        //in shared preferences, field -> wifi_state = 1....it means wifi was on just before start of the event
                        editor.commit();//changes committed in shared preferences
                    } else//if wifi wasn't enabled just before start of the event
                    {
                        editor.putInt("wifi_state", 0);
                        //in shared preferences field, wifi_state = 1.....it means wifi was on just before start of event
                        editor.commit();//changes committed in shared preferences

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
                        editor.putInt("mobiledata_state", 1);
                        //in shared preferences, field -> mobiledata_state = 1.....means mobile data was on before start of event
                        editor.commit();//changes committed in shared preferences
                    } else {
                        editor.putInt("mobiledata_state", 0);
                        //in shared preferences, field -> mobiledata_state = 0.....means mobile data was off before start of event
                        editor.commit();//changes committed in shared preferences
                    }


                    //getting audio manager
                    AudioManager am = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
                    switch (am.getRingerMode())//getRingerMode returns the ringing mode currently(just before start of event)
                    {

                        case AudioManager.RINGER_MODE_SILENT://if phone is on silent just before event starts
                            editor.putInt("profile_state", 1);
                            //in shared preferences, field -> profile_state = 1....means phone was on silent just before event started
                            editor.commit();//changes committed in shared preferences
                            break;

                        case AudioManager.RINGER_MODE_VIBRATE://if phone is on vibrate just before event starts
                            editor.putInt("profile_state", 2);
                            //in shared preferences, field -> profile_state = 2.....means phone was on vibrate just before event started
                            editor.commit();//changes committed in shared preferences
                            break;

                        case AudioManager.RINGER_MODE_NORMAL://if phones is on normal mode just before event starts
                            editor.putInt("profile_state", 3);
                            //in shared preferences, field -> profile_state = 3.....means phone was on normal mode just before event started
                            editor.commit();//changes committed in shared preferences
                            break;
                    }





                    //to set the bluetooth state when event starts
                    if (c.getString(c.getColumnIndex("bluetooth")).equals("yes"))//if bluetooth is "yes" in database for the running event
                    {
                        mBluetoothAdapter.enable();//turn bluetooth on
                    } else//if bluetooth is "no" in database for the running event
                    {
                        mBluetoothAdapter.disable();//turn bluetooth off
                    }


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


                }
                else {

                    quick_editor.putInt("overlap",1);
                    quick_editor.commit();
                    quick_editor.putInt("event started before quick", 0);
                    quick_editor.commit();
                    quick_editor.putInt("quick_id", id);
                    quick_editor.commit();

                    editor.putInt("bluetooth_state", quick_sharedpreferences.getInt("quick_bluetooth_state", 0));
                    editor.commit();
                    editor.putInt("wifi_state",quick_sharedpreferences.getInt("quick_wifi_state",0));
                    editor.commit();
                    editor.putInt("mobiledata_state",quick_sharedpreferences.getInt("quick_mobiledata_state",0));
                    editor.commit();
                    editor.putInt("profile_state",quick_sharedpreferences.getInt("quick_profile_state",0));
                    editor.commit();
                    /*editor.putInt("bluetooth_state", quick_sharedpreferences.getInt("quick_bluetooth_state", 0));
                    editor.commit();
                    editor.putInt("profile_state", quick_sharedpreferences.getInt("quick_profile_state", 0));
                    editor.commit();
                    editor.putInt("mobiledata_state", quick_sharedpreferences.getInt("quick_mobiledata_state", 0));
                    editor.commit();
                    editor.putInt("wifi_state", quick_sharedpreferences.getInt("quick_wifi_state",0));
                    editor.commit();*/

                }


                /*if(c.getInt(c.getColumnIndex("from_sync"))==1)
                {
                    sync_sharedpreferences = context1.getSharedPreferences(sync, Context.MODE_PRIVATE);
                    sync_editor = sync_sharedpreferences.edit();
                    String pending_start_date=sync_sharedpreferences.getString("pending_start_date","");
                    String pending_start_time=sync_sharedpreferences.getString("pending_start_time","");
                    int start_day=Integer.valueOf(pending_start_date.substring(0, 2));
                    int start_month=Integer.valueOf(pending_start_date.substring(3,5))-1;
                    int start_year=Integer.valueOf(pending_start_date.substring(6));
                    int start_hour=Integer.valueOf(pending_start_time.substring(0,2));
                    int start_min=Integer.valueOf(pending_start_time.substring(3));
                    calSet1.set(Calendar.DAY_OF_MONTH,start_day);
                    calSet1.set(Calendar.MONTH,start_month);
                    calSet1.set(Calendar.YEAR,start_year);
                    calSet1.set(Calendar.HOUR_OF_DAY,start_hour);
                    calSet1.set(Calendar.MINUTE,start_min);
                    calSet1.set(Calendar.SECOND,0);
                    calSet1.set(Calendar.MILLISECOND,0);
                }
                else
                {*/
                    calSet1 = Calendar.getInstance();//calSet1 contains the current date and time
                    calSet1.set(Calendar.SECOND, 0);
                    calSet1.set(Calendar.MILLISECOND, 0);
                //}



                int interval = c.getInt(c.getColumnIndex("days_bw_start_n_end"));
                if(interval>=1)
                {
                    Calendar next_cal=Calendar.getInstance();
                    next_cal.add(Calendar.DAY_OF_MONTH,1);
                    next_cal.set(Calendar.HOUR_OF_DAY, 0);
                    next_cal.set(Calendar.MINUTE, 0);
                    next_cal.set(Calendar.SECOND,0);
                    next_cal.set(Calendar.MILLISECOND,0);
                    Intent in=new Intent(context1,nextday_update.class);
                    PendingIntent pending = PendingIntent.getBroadcast(context1,id , in, 0);//ret_id is key here
                    //pending intent behaves differently for different keys.....though key doesn't plays any role

                    editor.putString("nextday_update_end_date",c.getString(c.getColumnIndex("event_end_date")));
                    editor.commit();
                    editor.putString("nextday_update_end_time",c.getString(c.getColumnIndex("end_time")));
                    editor.commit();
                    AlarmManager alarmManager = (AlarmManager) context1.getSystemService(Context.ALARM_SERVICE);
                    alarmManager.set(AlarmManager.RTC_WAKEUP, next_cal.getTimeInMillis(), pending);
                }


                String cur_dayofweek_for_cus_monthly_rep = intent.getStringExtra("cur_dayofweek_for_cus_monthly_rep");




                if (rep.charAt(0) != '0')
                {
                    if (rep.charAt(0) == '1')
                    {
                        calSet1.add(Calendar.DAY_OF_MONTH, 1);
                    }
                    else if (rep.charAt(0) == '2')
                    {
                        calSet1.add(Calendar.DAY_OF_MONTH, 7);
                    }
                    else if (rep.charAt(0) == '3')
                    {
                        int day=calSet1.get(Calendar.DAY_OF_MONTH);
                        calSet1.add(Calendar.DAY_OF_MONTH,1);
                        int add=1;
                        while(calSet1.get(Calendar.DAY_OF_MONTH)!=day)
                        {
                            calSet1.add(Calendar.DAY_OF_MONTH,1);
                            add++;
                        }
                        /*int month=calSet1.get(Calendar.MONTH);
                        int year=calSet1.get(Calendar.YEAR);
                        int add=1;
                        while(true)
                        {
                            calSet1.add(Calendar.MONTH, add);
                            if(calSet1.get(Calendar.DAY_OF_MONTH)==day)
                            {
                                break;
                            }
                            else
                            {
                                calSet1.set(Calendar.DAY_OF_MONTH,day);
                                calSet1.set(Calendar.MONTH,month);
                                calSet1.set(Calendar.YEAR,year);
                                add++;
                            }
                        }*/
                        editor.putInt("month_added"+id,add);
                        editor.commit();
                       /* if(calSet1.get(Calendar.MONTH)==0)
                        {
                            if((calSet1.get(Calendar.DAY_OF_MONTH)==30)||(calSet1.get(Calendar.DAY_OF_MONTH)==31))
                                calSet1.add(Calendar.MONTH,2);
                            else if(calSet1.get(Calendar.DAY_OF_MONTH)==29)
                            {
                                if(calSet1.get(Calendar.YEAR)%4==0)
                                    calSet1.add(Calendar.MONTH,1);
                                else
                                    calSet1.add(Calendar.MONTH,2);
                            }
                            else
                                calSet1.add(Calendar.MONTH,1);
                        }
                        else if((calSet1.get(Calendar.MONTH)==2)||(calSet1.get(Calendar.MONTH)==4)||(calSet1.get(Calendar.MONTH)==7)||(calSet1.get(Calendar.MONTH)==9))
                        {
                            if(calSet1.get(Calendar.DAY_OF_MONTH)==31)
                                calSet1.add(Calendar.MONTH,2);
                            else
                                calSet1.add(Calendar.MONTH,1);
                        }
                        else
                        calSet1.add(Calendar.MONTH, 1);*/
                    }
                    else if (rep.charAt(0) == '4')
                    {
                        int day=calSet1.get(Calendar.DAY_OF_MONTH);
                        int month=calSet1.get(Calendar.MONTH);
                        calSet1.add(Calendar.DAY_OF_MONTH,1);
                        int add=1;
                        while((calSet1.get(Calendar.DAY_OF_MONTH)!=day)||(calSet1.get(Calendar.MONTH)!=month))
                        {
                            calSet1.add(Calendar.DAY_OF_MONTH,1);
                            add++;
                        }
                       /* while(true)
                        {displayNotification();
                            calSet1.add(Calendar.YEAR, add);
                            if(calSet1.get(Calendar.DAY_OF_MONTH)==day)
                            {
                                break;
                            }
                            else
                            {
                                calSet1.set(Calendar.DAY_OF_MONTH,day);
                                calSet1.set(Calendar.MONTH,month);
                                calSet1.set(Calendar.YEAR,year);
                                add++;
                            }
                        }*/
                        editor.putInt("year_added"+id,add);
                        editor.commit();
                    }
                    else if (rep.charAt(0) == '5')
                    {
                        if (rep.charAt(2) == '0')
                        {
                            calSet1.add(Calendar.DAY_OF_MONTH, Integer.valueOf(rep.substring(4)));
                        }
                        else if (rep.charAt(2) == '1')
                        {
                            int var;
                            var = 7 * Integer.valueOf(rep.substring(4, rep.length() - 8));
                            calSet1.add(Calendar.DAY_OF_MONTH, var);
                        }
                        else if (rep.charAt(2) == '2')
                        {
                            if (rep.charAt(rep.length() - 4) == '1')
                            {
                                int day=Integer.valueOf(rep.substring(rep.length()-2));
                                int month=calSet1.get(Calendar.MONTH);
                                int year=calSet1.get(Calendar.YEAR);
                                int to_be_added=Integer.valueOf(rep.substring(4, rep.length() - 5));
                                int add=to_be_added;

                                if(calSet1.get(Calendar.DAY_OF_MONTH)<Integer.valueOf(rep.substring(rep.length()-2)))
                                {
                                    calSet1.set(Calendar.DAY_OF_MONTH,day);
                                    if(month==calSet1.get(Calendar.MONTH))
                                    {
                                        add=0;
                                    }
                                    else
                                    {
                                        while(true)
                                        {
                                            calSet1.set(Calendar.DAY_OF_MONTH,1);
                                            calSet1.set(Calendar.MONTH,month);
                                            calSet1.set(Calendar.YEAR,year);
                                            calSet1.add(Calendar.MONTH, add);
                                            month=calSet1.get(Calendar.MONTH);
                                            year=calSet1.get(Calendar.YEAR);
                                            calSet1.set(Calendar.DAY_OF_MONTH,day);
                                            if(calSet1.get(Calendar.DAY_OF_MONTH)==day)
                                            {
                                                break;
                                            }
                                            else
                                            {

                                            }
                                        }
                                    }
                                }
                                else
                                {
                                    while(true)
                                    {
                                        calSet1.set(Calendar.DAY_OF_MONTH,1);
                                        calSet1.set(Calendar.MONTH,month);
                                        calSet1.set(Calendar.YEAR,year);
                                        calSet1.add(Calendar.MONTH, add);
                                        month=calSet1.get(Calendar.MONTH);
                                        year=calSet1.get(Calendar.YEAR);
                                        calSet1.set(Calendar.DAY_OF_MONTH,day);
                                        if(calSet1.get(Calendar.DAY_OF_MONTH)==day)
                                        {
                                            break;
                                        }
                                        else
                                        {

                                        }
                                    }
                                }
                               /* int day=calSet1.get(Calendar.DAY_OF_MONTH);
                                int month=calSet1.get(Calendar.MONTH);
                                int year=calSet1.get(Calendar.YEAR);
                                int to_be_added=Integer.valueOf(rep.substring(4, rep.length() - 5));
                                int add=to_be_added;
                                while(true)
                                {
                                    calSet1.add(Calendar.MONTH, add);
                                    if(calSet1.get(Calendar.DAY_OF_MONTH)==day)
                                    {
                                        break;
                                    }
                                    else
                                    {
                                        calSet1.set(Calendar.DAY_OF_MONTH,day);
                                        calSet1.set(Calendar.MONTH,month);
                                        calSet1.set(Calendar.YEAR,year);
                                        add=add+to_be_added;
                                    }
                                }*/
                            }
                            else if(rep.charAt(rep.length() - 4) == '2')
                            {
                                int initial_day=calSet1.get(Calendar.DAY_OF_MONTH);
                                int initial_month=calSet1.get(Calendar.MONTH);
                                int initial_year=calSet1.get(Calendar.YEAR);
                                calSet1.set(Calendar.DAY_OF_MONTH, 1);
                                int l = calSet1.get(Calendar.DAY_OF_WEEK);
                                int o = 0;
                                switch (cur_dayofweek_for_cus_monthly_rep.charAt(cur_dayofweek_for_cus_monthly_rep.length() - 6))
                                {
                                    case 'm':
                                        o = (9 - l) % 7;
                                        break;
                                    case 'u':
                                        if (cur_dayofweek_for_cus_monthly_rep.charAt(cur_dayofweek_for_cus_monthly_rep.length() - 7) == 'T')
                                        {
                                            o = (10 - l) % 7;
                                        }
                                        else
                                        {
                                            o = (12 - l) % 7;
                                        }
                                        break;
                                    case 'n':
                                        o = (11 - l) % 7;
                                        break;
                                    case 'F':
                                        o = (13 - l) % 7;
                                        break;
                                    case 't':
                                        o = (14 - l) % 7;
                                        break;
                                    case 'S':
                                        o = (15 - l) % 7;
                                        break;
                                }
                                calSet1.add(Calendar.DAY_OF_MONTH, o);
                                switch (cur_dayofweek_for_cus_monthly_rep.charAt(11))
                                {
                                    case 'r':
                                        break;
                                    case 'c':
                                        calSet1.add(Calendar.DAY_OF_MONTH, 7);
                                        break;
                                    case 'i':
                                        calSet1.add(Calendar.DAY_OF_MONTH, 14);
                                        break;
                                    case 'u':
                                        calSet1.add(Calendar.DAY_OF_MONTH, 21);
                                        break;
                                    case 's':int day=calSet1.get(Calendar.DAY_OF_MONTH);
                                        int month=calSet1.get(Calendar.MONTH);
                                        int year=calSet1.get(Calendar.YEAR);
                                        calSet1.add(Calendar.DAY_OF_MONTH, 28);
                                        if(calSet1.get(Calendar.MONTH)!=month)
                                        {
                                            calSet1.set(Calendar.DAY_OF_MONTH,day);
                                            calSet1.set(Calendar.MONTH,month);
                                            calSet1.set(Calendar.YEAR,year);
                                            calSet1.add(Calendar.DAY_OF_MONTH,21);
                                        }
                                        break;
                                }
                                if(calSet1.get(Calendar.DAY_OF_MONTH)<=initial_day)
                                {
                                    calSet1.set(Calendar.DAY_OF_MONTH, 1);
                                    calSet1.add(Calendar.MONTH, Integer.valueOf(rep.substring(4, rep.length() - 5)));
                                    int l1 = calSet1.get(Calendar.DAY_OF_WEEK);
                                    int o1 = 0;
                                    switch (cur_dayofweek_for_cus_monthly_rep.charAt(cur_dayofweek_for_cus_monthly_rep.length() - 6))
                                    {
                                        case 'm':
                                            o1 = (9 - l1) % 7;
                                            break;
                                        case 'u':
                                            if (cur_dayofweek_for_cus_monthly_rep.charAt(cur_dayofweek_for_cus_monthly_rep.length() - 7) == 'T')
                                            {
                                                o1 = (10 - l1) % 7;
                                            }
                                            else
                                            {
                                                o1 = (12 - l1) % 7;
                                            }
                                            break;
                                        case 'n':
                                            o1 = (11 - l1) % 7;
                                            break;
                                        case 'F':
                                            o1 = (13 - l1) % 7;
                                            break;
                                        case 't':
                                            o1 = (14 - l1) % 7;
                                            break;
                                        case 'S':
                                            o1 = (15 - l1) % 7;
                                            break;
                                    }
                                    calSet1.add(Calendar.DAY_OF_MONTH, o1);
                                    switch (cur_dayofweek_for_cus_monthly_rep.charAt(11))
                                    {
                                        case 'r':
                                            break;
                                        case 'c':
                                            calSet1.add(Calendar.DAY_OF_MONTH, 7);
                                            break;
                                        case 'i':
                                            calSet1.add(Calendar.DAY_OF_MONTH, 14);
                                            break;
                                        case 'u':
                                            calSet1.add(Calendar.DAY_OF_MONTH, 21);
                                            break;
                                        case 's':int day=calSet1.get(Calendar.DAY_OF_MONTH);
                                            int month=calSet1.get(Calendar.MONTH);
                                            int year=calSet1.get(Calendar.YEAR);
                                            calSet1.add(Calendar.DAY_OF_MONTH, 28);
                                            if(calSet1.get(Calendar.MONTH)!=month)
                                            {
                                                calSet1.set(Calendar.DAY_OF_MONTH,day);
                                                calSet1.set(Calendar.MONTH,month);
                                                calSet1.set(Calendar.YEAR,year);
                                                calSet1.add(Calendar.DAY_OF_MONTH,21);
                                            }
                                            break;
                                    }
                                }
                                else
                                {

                                }
                            }
                            else
                            {
                                int initial_day=calSet1.get(Calendar.DAY_OF_MONTH);
                                int initial_month=calSet1.get(Calendar.MONTH);
                                int initial_year=calSet1.get(Calendar.YEAR);
                                calSet1.add(Calendar.DAY_OF_MONTH,1);
                                if(calSet1.get(Calendar.MONTH)==initial_month)
                                {
                                    calSet1.set(Calendar.DAY_OF_MONTH,1);
                                    calSet1.add(Calendar.MONTH, 1);
                                    calSet1.add(Calendar.DAY_OF_MONTH,-1);
                                }
                                else
                                {
                                    calSet1.add(Calendar.DAY_OF_MONTH,-1);
                                    int add = Integer.valueOf(rep.substring(4, rep.length() - 5));
                                    calSet1.set(Calendar.DAY_OF_MONTH, 1);
                                    calSet1.add(Calendar.MONTH, add + 1);
                                    calSet1.add(Calendar.DAY_OF_MONTH, -1);
                                }
                            }
                        }
                        else
                        {
                            int day=calSet1.get(Calendar.DAY_OF_MONTH);
                            int month=calSet1.get(Calendar.MONTH);
                            int year=calSet1.get(Calendar.YEAR);
                            int to_be_added=Integer.valueOf(rep.substring(4));
                            int add=to_be_added;
                            while(true)
                            {
                                calSet1.add(Calendar.YEAR, add);
                                if(calSet1.get(Calendar.DAY_OF_MONTH)==day)
                                {
                                    break;
                                }
                                else {
                                    calSet1.set(Calendar.DAY_OF_MONTH,day);
                                    calSet1.set(Calendar.MONTH,month);
                                    calSet1.set(Calendar.YEAR,year);
                                    add=add+to_be_added;
                                }
                            }
                        }
                    }
                    editor.putInt("calset_day", calSet1.get(Calendar.DAY_OF_MONTH));
                    editor.commit();
                    editor.putInt("calset_month", calSet1.get(Calendar.MONTH));
                    editor.commit();
                    editor.putInt("calset_year",calSet1.get(Calendar.YEAR));
                    editor.commit();
                    DateFormat dateFormat= new SimpleDateFormat("dd/MM/yyyy");
                    Date date = new Date(calSet1.get(Calendar.YEAR)-1900,calSet1.get(Calendar.MONTH),calSet1.get(Calendar.DAY_OF_MONTH),0,0);
                    String cur_date = dateFormat.format(date);



                    if(
                            (rep.charAt(0)!='5')||
                                    ((rep.charAt(0)=='5')&&(rep_until.charAt(0)=='0'))||
                                    (
                                            (rep.charAt(0)=='5')&& (rep_until.charAt(0)=='1')&&
                                                    (dateidentifier(rep_until.substring(2),cur_date)!=1)
                                    )||
                                    (
                                            (rep.charAt(0)=='5')&&(rep_until.charAt(0)=='2')&&
                                                    (no_of_times_event_ran<Integer.valueOf(rep_until.substring(2)))
                                    )
                            )
                    {

                        String next_date;
                        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
                        java.util.Date date4 = new java.util.Date(calSet1.get(Calendar.YEAR)-1900,calSet1.get(Calendar.MONTH),
                                calSet1.get(Calendar.DAY_OF_MONTH),0,0);
                        next_date=sdf.format(date4);



                        db2.open();
                        Cursor cur = db2.getEventDetail(id);
                        cur.moveToFirst();
                        //db3.open();



                        /*db3.update_Database(id, cur.getString(cur.getColumnIndex("event_name")),
                                cur.getString(cur.getColumnIndex("description")),
                                cur.getString(cur.getColumnIndex("event_start_date")),
                                cur.getString(cur.getColumnIndex("event_end_date")),
                                cur.getString(cur.getColumnIndex("start_time")), cur.getString(cur.getColumnIndex("end_time")),
                                cur.getString(cur.getColumnIndex("event_start_date_time")),
                                cur.getString(cur.getColumnIndex("bluetooth")),
                                cur.getString(cur.getColumnIndex("wifi")), cur.getString(cur.getColumnIndex("profile")),
                                cur.getString(cur.getColumnIndex("mobile_data")), cur.getString(cur.getColumnIndex("repeat")),
                                cur.getString(cur.getColumnIndex("repeat_until")),
                                cur.getString(cur.getColumnIndex("cur_dayofweek_for_cus_monthly_rep")),
                                cur.getInt(cur.getColumnIndex("days_bw_start_n_end")),
                                next_date,cur.getInt(cur.getColumnIndex("from_sync")));*/


                        setAlarm(calSet1, rep, rep_until, cur_dayofweek_for_cus_monthly_rep);
                    }
                    else
                    {
                        editor.putInt("delete_the_event",1);
                        editor.commit();

                    }
                }

            }


        }
        catch(Exception e)
        {

        }

        db.close();
        db2.close();
        //db3.close();

    }



    //displays notification indicating start of the event
    @SuppressWarnings("deprecation")
    public void displayNotification1()
    {

        Intent i=new Intent(context1,pending_notif_events.class);
        editor.putInt("id",id);
        editor.commit();

        editor.putString("rep", rep);
        editor.commit();
        //i.putExtra("id",id);
        PendingIntent pending_event=PendingIntent.getBroadcast(context1, 0, i, 0);

        NotificationCompat.Builder mBuilder;
        if(description==null)
        {
             mBuilder= new NotificationCompat.Builder(context1)
                            .setSmallIcon(R.drawable.occasus1)
                            .setContentTitle("Occasus")
                            .setContentText("Event "+database_name+" starts")
                                    // if(description!=null)
                            //.setSubText(description)
                            .addAction(R.drawable.stop, "Cancel", pending_event);
        }
        else
        {
            mBuilder = new NotificationCompat.Builder(context1)
                            .setSmallIcon(R.drawable.occasus1)
                            .setContentTitle("Occasus")
                            .setContentText("Event "+database_name+" starts")
                                    // if(description!=null)
                            .setSubText(description)
                            .addAction(R.drawable.stop, "Cancel", pending_event);
        }


        int mNotificationId = 2;
        NotificationManager mNotifyMgr =
                (NotificationManager) context1.getSystemService(context1.NOTIFICATION_SERVICE);
        mNotifyMgr.notify(mNotificationId, mBuilder.build());


       /* Intent i = new Intent(context1, NotificationView.class);//moves control to notificationView class
        i.putExtra("notificationID",notificationID);

        PendingIntent pendingIntent = PendingIntent.getActivity(context1,0,i,0);

        NotificationManager nm = (NotificationManager)context1.getSystemService(context1.NOTIFICATION_SERVICE);

        Notification notif = new Notification(
            R.drawable.occasus1,"Event "+database_name+" starts",System.currentTimeMillis()
        );

        CharSequence from = "Occasus";
        CharSequence message ="Event " +database_name+" starts";//message appearing at notification

        notif.setLatestEventInfo(context1,from,message,pendingIntent);
        nm.notify(notificationID,notif);*/
    }




    //creates pending intent for start time
    private void setAlarm(Calendar targetCal,String rep,String rep_until,String cur_dayofweek_for_cus_monthly_rep)
    {
        Intent intent = new Intent(context1, Activate_event.class);
        intent.putExtra("id",id);

        //name sent to activate_event class....there it is used to retrieve all the details of the event
        intent.putExtra("rep",rep);
        intent.putExtra("rep_until",rep_until);
        intent.putExtra("cur_dayofweek_for_cus_monthly_rep",cur_dayofweek_for_cus_monthly_rep);

        PendingIntent pendingIntent = PendingIntent.getBroadcast(context1,id , intent, 0);//ret_id is key here
        //pending intent behaves differently for different keys.....though here key doesn't plays any role

        AlarmManager alarmManager = (AlarmManager) context1.getSystemService(Context.ALARM_SERVICE);
        alarmManager.set(AlarmManager.RTC_WAKEUP, targetCal.getTimeInMillis(), pendingIntent);
        //housekeeping stuff for the starting pending intent for next week
    }


    //returns 3 if equal
    //returns 1 if date1<date2
    //returns2 if date1>date2
    public int dateidentifier(String date1,String date2) {

        int asci1 = 0, asci2 = 0, as;
        int i;
        for (i = 6; i <= 9; i++)
        {

            char ch = date1.charAt(i);

            as = (int) ch;
            as = as - 48;
            int j;
            j = (int) Math.pow(10, 14 - i);
            as = as * j;
            asci1 = asci1 + as;

        }


        char ch = date1.charAt(3);

        as = (int) ch;
        as = as - 48;
        int j;
        j = (int) Math.pow(10, 3);
        as = as * j;
        asci1 = asci1 + as;

        ch = date1.charAt(4);

        as = (int) ch;
        as = as - 48;

        j = (int) Math.pow(10, 2);
        as = as * j;
        asci1 = asci1 + as;

        ch = date1.charAt(0);

        as = (int) ch;
        as = as - 48;

        j = (int) Math.pow(10, 1);
        as = as * j;
        asci1 = asci1 + as;

        ch = date1.charAt(1);

        as = (int) ch;
        as = as - 48;

        j = (int) Math.pow(10, 0);
        as = as * j;
        asci1 = asci1 + as;

        for (i = 6; i <= 9; i++)
        {

            ch = date2.charAt(i);

            as = (int) ch;
            as = as - 48;

            j = (int) Math.pow(10, 14 - i);
            as = as * j;
            asci2 = asci2 + as;

        }


        ch = date2.charAt(3);

        as = (int) ch;
        as = as - 48;

        j = (int) Math.pow(10, 3);
        as = as * j;
        asci2 = asci2 + as;

        ch = date2.charAt(4);

        as = (int) ch;
        as = as - 48;

        j = (int) Math.pow(10, 2);
        as = as * j;
        asci2 = asci2+ as;

        ch = date2.charAt(0);

        as = (int) ch;
        as = as - 48;

        j = (int) Math.pow(10, 1);
        as = as * j;
        asci2 = asci2 + as;

        ch = date2.charAt(1);

        as = (int) ch;
        as = as - 48;

        j = (int) Math.pow(10, 0);
        as = as * j;
        asci2 = asci2 + as;

        if(asci1==asci2)
            return 3;
        else if(asci1<asci2)
            return 1;
        else
            return 2;

    }



}
