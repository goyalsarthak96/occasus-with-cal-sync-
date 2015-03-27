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

import android.widget.Toast;


import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Calendar;



public class Activate_event extends BroadcastReceiver {


    public  Integer profile_state=0;
    Context context1;
    int ret_id;
    String ret_name;
    Calendar calSet1;
    int notificationID = 1;
    String database_name;


    public static final String MyPREFERENCES = "MyPrefs";

    public void onReceive(final Context context, Intent intent) {


        //Toast.makeText(context,"activate called",Toast.LENGTH_SHORT).show();

        DBAdapter db = new DBAdapter(context);
        db.open();
        Cursor c;
       // Toast.makeText(context, "hi", Toast.LENGTH_SHORT).show();


        //  eve_name = intent.getStringExtra("event_name");
        //Toast.makeText(context,eve_name,Toast.LENGTH_SHORT).show();

        ret_name = intent.getStringExtra("event_name");
         try{

        c = db.getEventsDetail(ret_name);
        c.moveToFirst();
       // Toast.makeText(context, "name=" + ret_name, Toast.LENGTH_SHORT).show();
        if (c != null) {


            database_name = c.getString(c.getColumnIndex("event_name"));
            context1 = context;

            displayNotification();

            //shared preferences declared

            SharedPreferences sharedpreferences;
            ret_id = intent.getIntExtra("event_id", 0);
            Calendar calNow = Calendar.getInstance();
            calSet1 = (Calendar) calNow.clone();

            //end_minute=intent.getIntExtra("event_emin",0);
            // end_hour=intent.getIntExtra("event_ehour",0);
            // calSet1.set(Calendar.HOUR_OF_DAY,end_hour);
            // calSet1.set(Calendar.SECOND, 0);
            // calSet1.set(Calendar.MILLISECOND, 0);

            //shared preferences editor declared
            SharedPreferences.Editor editor;
            //initializing shared preferences
            sharedpreferences = context.getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
            editor = sharedpreferences.edit();

            BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
            if (mBluetoothAdapter.isEnabled()) {
                Toast.makeText(context, "hi1", Toast.LENGTH_SHORT).show();
                editor.putInt("bluetooth_state", 1);
                editor.commit();

                //  bluetooth_state = context.getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE).getInt("bluetooth_state", 6);//6 is the default value
                // Toast.makeText(context,"bluetooth="+Integer.toString(bluetooth_state),Toast.LENGTH_SHORT).show();

            } else {
                Toast.makeText(context, "hi0", Toast.LENGTH_SHORT).show();
                editor.putInt("bluetooth_state", 0);
                editor.commit();

                // bluetooth_state = context.getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE).getInt("bluetooth_state", 6);
                //  Toast.makeText(context,"bluetooth="+Integer.toString(bluetooth_state),Toast.LENGTH_SHORT).show();

            }

            WifiManager wifi;
            wifi = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);

            if (wifi.isWifiEnabled()) {
                editor.putInt("wifi_state", 1);
                editor.commit();
                //   wifi_state = context.getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE).getInt("wifi_state", 6);

            } else {
                editor.putInt("wifi_state", 0);
                editor.commit();
                // wifi_state = context.getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE).getInt("wifi_state", 6);

            }


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

            if (mobileDataEnabled == true) {
                editor.putInt("mobiledata_state", 1);
                editor.commit();
                //mobiledata_state = context.getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE).getInt("mobiledata_state", 6);

            } else {
                editor.putInt("mobiledata_state", 0);
                editor.commit();
                //  mobiledata_state = context.getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE).getInt("mobiledata_state", 6);

            }


            AudioManager am = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);

            switch (am.getRingerMode()) {
                case AudioManager.RINGER_MODE_SILENT:
                    editor.putInt("profile_state", 1);
                    editor.commit();
                    profile_state = context.getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE).getInt("profile_state", 6);

                    break;
                case AudioManager.RINGER_MODE_VIBRATE:
                    editor.putInt("profile_state", 2);
                    editor.commit();
                    profile_state = context.getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE).getInt("profile_state", 6);

                    break;
                case AudioManager.RINGER_MODE_NORMAL:
                    editor.putInt("profile_state", 3);
                    editor.commit();
                    profile_state = context.getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE).getInt("profile_state", 6);

                    break;
            }


            //Toast.makeText(context,"id= "+ret_id,Toast.LENGTH_SHORT).show();


            final MediaPlayer mp = MediaPlayer.create(context, R.raw.whistle);

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


            if (c.getString(c.getColumnIndex("bluetooth")).equals("yes")) {

                mBluetoothAdapter.enable();
            } else {

                mBluetoothAdapter.disable();
            }


            if (c.getString(c.getColumnIndex("wifi")).equals("yes")) {


                wifi.setWifiEnabled(true);
            } else {


                wifi.setWifiEnabled(false);
            }


            final ConnectivityManager conman =
                    (ConnectivityManager) context.getSystemService(context.CONNECTIVITY_SERVICE);

            if (c.getString(c.getColumnIndex("mobile_data")).equals("yes")) {

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
            if (c.getString(c.getColumnIndex("profile")).equals("silent")) {
                MyAudioManager.setRingerMode(AudioManager.RINGER_MODE_SILENT);
            } else if (c.getString(c.getColumnIndex("profile")).equals("ring")) {
                MyAudioManager.setRingerMode(AudioManager.RINGER_MODE_NORMAL);
            } else {
                MyAudioManager.setRingerMode(AudioManager.RINGER_MODE_VIBRATE);
            }

            //Toast.makeText(context,"activate done without repeat",Toast.LENGTH_SHORT).show();
            int rep = intent.getIntExtra("rep", 0);
            if (rep == 1) {

                calSet1.add(Calendar.DAY_OF_MONTH, 7);

                setAlarm(calSet1);
            }
            //Toast.makeText(context,"activate complete with repeat",Toast.LENGTH_SHORT).show();
              }
              }
            catch(Exception e)
            {

            }

        }




    public void displayNotification(){
    Intent i = new Intent(context1, NotificationView.class);
    i.putExtra("notificationID",notificationID);
      //  Toast.makeText(context1,"if",Toast.LENGTH_SHORT).show();

    PendingIntent pendingIntent = PendingIntent.getActivity(context1,0,i,0);

    NotificationManager nm = (NotificationManager)context1.getSystemService(context1.NOTIFICATION_SERVICE);

    Notification notif = new Notification(
            R.drawable.ic_launcher,"Reminder:Meeting starts in 5 minutes",System.currentTimeMillis()
    );

    CharSequence from = "SystemAlarm";
    CharSequence message ="event " +database_name+" starts";


    notif.setLatestEventInfo(context1,from,message,pendingIntent);
    nm.notify(notificationID,notif);
}


    //creates pending intent for start time
    private void setAlarm(Calendar targetCal){


        Intent intent = new Intent(context1, Activate_event.class);
        //name sent to activate_event class....there it is used to retrieve all the details of the event
        intent.putExtra("event_name",ret_name);
        //ret_id used as a unique key
        intent.putExtra("rep",1);




            PendingIntent pendingIntent = PendingIntent.getBroadcast(context1, ret_id, intent, 0);
            AlarmManager alarmManager = (AlarmManager) context1.getSystemService(Context.ALARM_SERVICE);
            alarmManager.set(AlarmManager.RTC_WAKEUP, targetCal.getTimeInMillis(), pendingIntent);
        }


}
