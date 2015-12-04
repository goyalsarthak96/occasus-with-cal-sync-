package com.example.dvs.occasus;

import android.app.ActionBar;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.ConnectivityManager;
import android.net.wifi.WifiManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.NotificationCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;


import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.Calendar;


public class show_events_list extends ActionBarActivity implements today_eve_frag.today_eve_frag_listener,all_eve_frag.all_eve_frag_listener{

    tab_adapter mAppSectionsPagerAdapter;
    ViewPager mViewPager;

    String req_name;
    int clicked_id;

    ListAdapter eventadapter;

    public static final String custom_info = "custom_repeat";
    SharedPreferences.Editor custom_editor;
    SharedPreferences custom_sharedpreferences;

    public static final String MyPREFERENCES = "MyPrefs";
    SharedPreferences sharedpreferences;
    SharedPreferences.Editor editor;


    public static final String notif_MyPREFERENCES = "notifpref";
    SharedPreferences notif_sharedpreferences;
    SharedPreferences.Editor notif_editor;


    String[] items = { " Edit", "Delete"," View"};

    @Override
    public void delete_event_today(String req_name1, int clicked_id1) {
        req_name=req_name1;
        clicked_id=clicked_id1;
        eventadapter = new custom_event_options(this, items);

        custom_sharedpreferences = getBaseContext().getSharedPreferences(custom_info, Context.MODE_PRIVATE);
        custom_editor = custom_sharedpreferences.edit();

        sharedpreferences = getBaseContext().getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        editor = sharedpreferences.edit();


        notif_sharedpreferences = getBaseContext().getSharedPreferences(notif_MyPREFERENCES, Context.MODE_PRIVATE);
        notif_editor = notif_sharedpreferences.edit();


        showDialog(0);
    }


    @Override
    public void delete_event_all(String req_name1, int clicked_id1) {
        req_name=req_name1;
        clicked_id=clicked_id1;
        eventadapter = new custom_event_options(this, items);

        custom_sharedpreferences = getBaseContext().getSharedPreferences(custom_info, Context.MODE_PRIVATE);
        custom_editor = custom_sharedpreferences.edit();

        sharedpreferences = getBaseContext().getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        editor = sharedpreferences.edit();
        showDialog(0);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_events_list);

        //to add logo to action bar
        android.support.v7.app.ActionBar ac=getSupportActionBar();
        ac.setDisplayShowHomeEnabled(true);
        ac.setLogo(R.drawable.occasus1);
        ac.setDisplayUseLogoEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);//to hide back button on action bar



        mAppSectionsPagerAdapter = new tab_adapter(getSupportFragmentManager());

        final android.support.v7.app.ActionBar actionBar = ((ActionBarActivity)this).getSupportActionBar();
        actionBar.setHomeButtonEnabled(false);
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

        // Set up the ViewPager, attaching the adapter and setting up a listener for when the
        // user swipes between sections.
        mViewPager = (ViewPager) findViewById(R.id.pager1);
        mViewPager.setAdapter(mAppSectionsPagerAdapter);
        mViewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i1) {

            }

            @Override
            public void onPageSelected(int position) {
                actionBar.setSelectedNavigationItem(position);
            }

            @Override
            public void onPageScrollStateChanged(int i) {

            }
        });

        // For each of the sections in the app, add a tab to the action bar.
        for (int i = 0; i < mAppSectionsPagerAdapter.getCount(); i++) {

            actionBar.addTab(
                    actionBar.newTab()
                            .setText(mAppSectionsPagerAdapter.getPageTitle(i))
                            .setTabListener(tabListener));
        }


    }


    android.support.v7.app.ActionBar.TabListener tabListener=new android.support.v7.app.ActionBar.TabListener() {
        @Override
        public void onTabSelected(android.support.v7.app.ActionBar.Tab tab, android.support.v4.app.FragmentTransaction fragmentTransaction) {
            mViewPager.setCurrentItem(tab.getPosition());
        }

        @Override
        public void onTabUnselected(android.support.v7.app.ActionBar.Tab tab, android.support.v4.app.FragmentTransaction fragmentTransaction) {

        }

        @Override
        public void onTabReselected(android.support.v7.app.ActionBar.Tab tab, android.support.v4.app.FragmentTransaction fragmentTransaction) {

        }
    };

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_show_events_list, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.refresh) {
            Intent i=new Intent(this,show_events_list.class);
            startActivity(i);
            //mViewPager.setCurrentItem(tab.getPosition());
        }

        return super.onOptionsItemSelected(item);
    }




























    /*int today_eve_no=0,later_eve_no=0;

    String req_name;
    int clicked_id;

    ListView l1,l2;
    ListAdapter adap1,adap2,eventadapter;



    public static final String custom_info = "custom_repeat";
    SharedPreferences.Editor custom_editor;
    SharedPreferences custom_sharedpreferences;


    public static final String MyPREFERENCES = "MyPrefs";
    SharedPreferences sharedpreferences;
    //shared preferences editor declared
    SharedPreferences.Editor editor;

    String[] items = { " Edit", "Delete"," View"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_events_list);

        l1=(ListView) findViewById(R.id.today_eve);
        l2=(ListView) findViewById(R.id.later_eve);

        sharedpreferences = getBaseContext().getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        editor = sharedpreferences.edit();



        custom_sharedpreferences = getBaseContext().getSharedPreferences(custom_info, Context.MODE_PRIVATE);
        custom_editor = custom_sharedpreferences.edit();

        eventadapter = new custom_event_options(this, items);


        Calendar cal=Calendar.getInstance();
        String today_date;
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        java.util.Date date4 = new java.util.Date(cal.get(Calendar.YEAR)-1900,cal.get(Calendar.MONTH),
                cal.get(Calendar.DAY_OF_MONTH),0,0);
        today_date=sdf.format(date4);



        DBAdapter db=new DBAdapter(this);
        db.open();
        Cursor c=db.getAllEventsDetails();
        c.moveToFirst();

        if(c.getCount()>0)
        {

            do {
                if (c.getString(c.getColumnIndex("next_date")).equals(today_date)) {
                    today_eve_no++;
                } else {
                    later_eve_no++;
                }
            } while (c.moveToNext());

            String[] today_eve_name_list = new String[today_eve_no];
            final int[] today_eve_id_list = new int[today_eve_no];
            String[] later_eve_name_list = new String[later_eve_no];
            final int[] later_eve_id_list = new int[later_eve_no];
            int i = 0, j = 0;
            c.moveToFirst();
            do {

                if (c.getString(c.getColumnIndex("next_date")).equals(today_date)) {
                    today_eve_name_list[i] = c.getString(c.getColumnIndex("event_name"));
                    today_eve_id_list[i] = c.getInt(c.getColumnIndex("_id"));
                    i++;
                } else {
                    later_eve_name_list[j] = c.getString(c.getColumnIndex("event_name"));
                    later_eve_id_list[j] = c.getInt(c.getColumnIndex("_id"));
                    j++;
                }
            } while (c.moveToNext());


            adap1 = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, today_eve_name_list);
            adap2 = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, later_eve_name_list);

            l1.setAdapter(adap1);
            l2.setAdapter(adap2);


            l1.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                    req_name = String.valueOf(parent.getItemAtPosition(position));
                    clicked_id = today_eve_id_list[position];
                    showDialog(0);
                }
            });

            l2.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                    req_name = String.valueOf(parent.getItemAtPosition(position));
                    clicked_id = later_eve_id_list[position];
                    showDialog(0);
                }
            });
        }

    }*/


    @Override
    public void onBackPressed() {
        Intent intent=new Intent(this,MainActivity.class);
        startActivity(intent);
    }

    @Override
    protected Dialog onCreateDialog(int id)
    {
        switch (id)
        {
            case 0:
                //dialog box for edit/delete
                return new AlertDialog.Builder(this)
                        .setIcon(R.drawable.edit_delete_view)
                        .setTitle("Select one of the options")
                        .setAdapter(eventadapter, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        //if edit is clicked
                                        if (which == 0) {

                                            //the name of the event clicked sent to create_event class through req_name
                                            Intent intent = new Intent(show_events_list.this, CreateEvent.class);
                                            intent.putExtra("clicked_id", clicked_id);
                                            intent.putExtra("flag", 1);

                                            custom_editor.putInt("coming_from_custom_repeat", 0);
                                            custom_editor.commit();
                                            startActivity(intent);
                                            //if delete clicked
                                        } else if (which == 1) {
                                            //dailog box showing warning
                                            showDialog(1);
                                        }

                                        //if view clicked
                                        else if (which == 2) {
                                            Intent intent = new Intent(show_events_list.this, Show_details.class);
                                            intent.putExtra("clicked_id", clicked_id);


                                            startActivity(intent);
                                        }

                                    }
                                }

                        ).create();

            case 1:
                //dialog box for delete warning
                return new AlertDialog.Builder(this)
                        .setIcon(R.drawable.delete_confirm)
                        .setTitle("Are You Sure You Want To Delete")

                        .setNegativeButton("No",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int whichButton) {

                                    }
                                }
                        )

                                //event has to be deleted...(event is supposed to end as soon as we delete if it was running at the time of delettion
                        .setPositiveButton("Yes",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int whichButton)
                                    {


                                        DBAdapter db = new DBAdapter(getBaseContext());
                                        db.open();

                                        if(getBaseContext().getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE).getInt("event_running", 6) == 1)
                                        //if some event was running when it was deleted
                                        {
                                            int running_id = getBaseContext().getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE).getInt("running_id", 0);


                                            if (clicked_id==running_id)
                                            //if running event has same id as event to be deleted
                                            {


                                                final MediaPlayer mp = MediaPlayer.create(getBaseContext(), R.raw.notification);
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
                                                    displayNotification();//denoting end of the event
                                                    mp.start();
                                                }
                                                else
                                                {
                                                    displayNotification();//denoting end of the event
                                                }



                                                editor.putInt("event_running", 0);//make event running 0 since no event is running now
                                                editor.commit();





                                                //revert phone to settings which were before event started
                                                int bluetooth_state = getBaseContext().getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE).getInt("bluetooth_state", 6);
                                                int wifi_state = getBaseContext().getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE).getInt("wifi_state", 6);

                                                int mobiledata_state = getBaseContext().getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE).getInt("mobiledata_state", 6);
                                                int profile_state = getBaseContext().getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE).getInt("profile_state", 6);
                                                BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
                                                if (bluetooth_state == 1) {
                                                    mBluetoothAdapter.enable();
                                                } else {
                                                    mBluetoothAdapter.disable();
                                                }


                                                WifiManager wifi;
                                                wifi = (WifiManager) getBaseContext().getSystemService(Context.WIFI_SERVICE);
                                                if (wifi_state == 1) {
                                                    wifi.setWifiEnabled(true);
                                                } else {
                                                    wifi.setWifiEnabled(false);
                                                }


                                                final ConnectivityManager conman =
                                                        (ConnectivityManager) getBaseContext().getSystemService(getBaseContext().CONNECTIVITY_SERVICE);

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

                                                MyAudioManager = (AudioManager) getBaseContext().getSystemService(Context.AUDIO_SERVICE);
                                                if (profile_state == 1)
                                                {
                                                    MyAudioManager.setRingerMode(AudioManager.RINGER_MODE_SILENT);
                                                }
                                                else if (profile_state == 3)
                                                {
                                                    MyAudioManager.setRingerMode(AudioManager.RINGER_MODE_NORMAL);
                                                }
                                                else
                                                {
                                                    MyAudioManager.setRingerMode(AudioManager.RINGER_MODE_VIBRATE);
                                                }


                                            }
                                        }
                                        db.close();
                                        //deletes the event
                                        db.deleteEvent(clicked_id);


                                        //brings back to mainactivity screen and refreshes the event list
                                        Intent intent = new Intent(show_events_list.this, show_events_list.class);
                                        startActivity(intent);

                                    }
                                }
                        ).create();

        }
        return null;

    }


    //displays notification that event has ended
    public void displayNotification(){

        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(getBaseContext())
                        .setSmallIcon(R.drawable.occasus1)
                        .setContentTitle("Occasus")
                        .setContentText("Event "+req_name+" ends");


        int mNotificationId = 2;
        NotificationManager mNotifyMgr =
                (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        mNotifyMgr.notify(mNotificationId, mBuilder.build());


        Calendar cancel_cal=Calendar.getInstance();
        cancel_cal.add(Calendar.MINUTE,5);
        Intent intent=new Intent(getBaseContext(),cancel_notification.class);
        notif_editor.putInt("notif_id",2);
        notif_editor.commit();
        PendingIntent pendingIntent = PendingIntent.getBroadcast(getBaseContext(), 2, intent, 0);
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        alarmManager.set(AlarmManager.RTC_WAKEUP, cancel_cal.getTimeInMillis(), pendingIntent);


        /*Intent i = new Intent(getBaseContext(), NotificationView.class);
        i.putExtra("notificationID",2);

        PendingIntent pendingIntent = PendingIntent.getActivity(getBaseContext(),0,i,0);

        NotificationManager nm = (NotificationManager)getBaseContext().getSystemService(getBaseContext().NOTIFICATION_SERVICE);

        Notification notif = new Notification(
                R.drawable.occasus1,"Event "+req_name+" ends",System.currentTimeMillis()
        );

        CharSequence from = "Occasus";
        CharSequence message = "Event "+req_name+" ends";

        notif.setLatestEventInfo(getBaseContext(),from,message,pendingIntent);
        nm.notify(2,notif);*/
    }


}
