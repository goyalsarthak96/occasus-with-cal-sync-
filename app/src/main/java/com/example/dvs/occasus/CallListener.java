package com.example.dvs.occasus;



import android.content.SharedPreferences;
import android.database.Cursor;
import android.media.AudioManager;
import android.telephony.TelephonyManager;
import android.widget.ListView;
import android.widget.Toast;
import android.content.Context;
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.preference.PreferenceManager;
import android.telephony.SmsManager;
import android.app.PendingIntent;

public class CallListener extends BroadcastReceiver {

    public static final String quickSilent = "quick_silent";
    SharedPreferences quick_sharedpreferences;


    public static final String MyPREFERENCES = "MyPrefs";
    SharedPreferences sharedpreferences;



    @Override
    public void onReceive(Context context, Intent intent)
    {

        sharedpreferences = context.getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        quick_sharedpreferences = context.getSharedPreferences(quickSilent, Context.MODE_PRIVATE);

        // TODO Auto-generated method stub

        if((sharedpreferences.getInt("event_running",0)==1)&&
                (quick_sharedpreferences.getInt("quick_silent_running",0)==0))//0 is the default value here
            //if in shared preferences, field-> event_running = 1 ( event is running....i.e. it wasn't deleted while it was running)
        {

            AudioManager am = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);//getting audio manager

            //Creating an object of DBAdapterException Class
            DBAdapterException db = new DBAdapterException(context);

            //Creating an object of DBAdapterSms Class
            DBAdapterSms db1 = new DBAdapterSms(context);

            // get the phone number
            String incomingNumber = intent.getStringExtra(TelephonyManager.EXTRA_INCOMING_NUMBER);
            String incomingNumber1;


            if (intent.getStringExtra(TelephonyManager.EXTRA_STATE).equals(TelephonyManager.EXTRA_STATE_RINGING))
            {
                // This code will execute when the phone has an incoming call
                db.open();
                //j: flag variable to prevent sending message two times
                PreferenceManager.getDefaultSharedPreferences(context).edit().putString("j", "0").commit();
                //i: Flag variable to enter RINGING_STATE once
                String i = PreferenceManager.getDefaultSharedPreferences(context).getString("i", "0");
                if (i.equals("0"))
                {
                    switch (am.getRingerMode())
                    {
                        case AudioManager.RINGER_MODE_SILENT:
                            PreferenceManager.getDefaultSharedPreferences(context).edit().putString("p", "1").commit(); //p: variable to store phone state
                            break;
                        case AudioManager.RINGER_MODE_VIBRATE:
                            PreferenceManager.getDefaultSharedPreferences(context).edit().putString("p", "2").commit();
                            break;
                        case AudioManager.RINGER_MODE_NORMAL:
                            PreferenceManager.getDefaultSharedPreferences(context).edit().putString("p", "3").commit();
                            break;
                    }
                }

                //Taking the last 10 digits of the incoming number
                incomingNumber1 = incomingNumber.substring(3);


                //Storing the actual number saved in the contacts in Shared Preferences
                PreferenceManager.getDefaultSharedPreferences(context).edit().putString("incoming number actual", incomingNumber).commit();

                //Storing the incoming number in Shared Preferences
                PreferenceManager.getDefaultSharedPreferences(context).edit().putString("incoming no", incomingNumber1).commit();

                //Getting all contacts through cursor
                Cursor c = db.getAllContacts();

                if (c.moveToFirst())
                {
                    do
                    {

                        //Checking if the number added and the incoming number are the same
                        if (c.getString(c.getColumnIndex("PhoneNo")).substring(c.getString(c.getColumnIndex("PhoneNo")).length() - 10).equals(incomingNumber1))
                        {
                            //Setting flag 'i' to 1 once the phone is set to normal mode
                            PreferenceManager.getDefaultSharedPreferences(context).edit().putString("i", "1").commit();
                            am.setRingerMode(AudioManager.RINGER_MODE_NORMAL);
                            int streamMaxVolume = am.getStreamMaxVolume(AudioManager.STREAM_RING); //Setting max volume
                            am.setStreamVolume(AudioManager.STREAM_RING, streamMaxVolume, AudioManager.FLAG_ALLOW_RINGER_MODES | AudioManager.FLAG_PLAY_SOUND);

                            break;
                        }
                    } while (c.moveToNext());
                }
                //Closing contacts exception database
                db.close();



            }
            else if (intent.getStringExtra(TelephonyManager.EXTRA_STATE).equals(
                    TelephonyManager.EXTRA_STATE_IDLE)
                    || intent.getStringExtra(TelephonyManager.EXTRA_STATE).equals(
                    TelephonyManager.EXTRA_STATE_OFFHOOK))
            {
                // This code will execute when the call is disconnected

                db1.open();

                //Getting flag 'p' for the previous phone state from shared preferences
                String p = PreferenceManager.getDefaultSharedPreferences(context).getString("p", "0");
                PreferenceManager.getDefaultSharedPreferences(context).edit().putString("i", "0").commit();

                switch(p) {
                    case "1":
                        am.setRingerMode(AudioManager.RINGER_MODE_SILENT);
                        break;
                    case "2":
                        am.setRingerMode(AudioManager.RINGER_MODE_VIBRATE);
                        break;
                    case "3":
                        am.setRingerMode(AudioManager.RINGER_MODE_NORMAL);
                        break;
                }

                //Setting flag 'j' to 0 so that it enters in the message sending if statement
                String j = PreferenceManager.getDefaultSharedPreferences(context).getString("j", "0");
                String number = PreferenceManager.getDefaultSharedPreferences(context).getString("incoming no", "");
                String actualNo = PreferenceManager.getDefaultSharedPreferences(context).getString("incoming number actual", "");

                Cursor c1 = db1.getAllContacts();
                if (c1.moveToFirst())
                {
                    do
                    {
                        if (c1.getString(c1.getColumnIndex("PhoneNo")).substring(c1.getString(c1.getColumnIndex("PhoneNo")).length() - 10).equals(number) && j.equals("0"))
                        {
                            //Getting message text from Shared Preferences
                            String s = PreferenceManager.getDefaultSharedPreferences(context).getString("message_text", "I am busy, call me later.");
                            Toast.makeText(context, "Message Sent to "+actualNo, Toast.LENGTH_SHORT).show();

                            try
                            {
                                //sending text message
                                SmsManager.getDefault().sendTextMessage(actualNo, null, s, null, null);
                            }
                            catch (Exception e)
                            {
                                String error = e.getMessage();
                                Toast.makeText(context, error, Toast.LENGTH_SHORT).show();
                            }
                            //Changing flag to '1' after sending message once
                            PreferenceManager.getDefaultSharedPreferences(context).edit().putString("j", "1").commit();
                        }
                    } while (c1.moveToNext());
                }

                db1.close();
            }

        }

    }
}