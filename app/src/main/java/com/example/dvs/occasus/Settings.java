package com.example.dvs.occasus;

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.PendingIntent;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.provider.CalendarContract;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.InputType;
import android.text.Spanned;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;

import java.util.Calendar;


public class Settings extends ActionBarActivity implements profile_fragment.profile_fragmentlistener{

    ListAdapter l_adap,auto_dialog;
    ListView list_view;
    String show,sync_auto;
    int position=0;

    ListAdapter profile_adapter;

    View frag;
    int start=-1;

    public static final String MyPREFERENCES = "MyPrefs";
    SharedPreferences sharedpreferences;
    SharedPreferences.Editor editor;

    String[] settings_options={"Alarm only","Alarm and Notification","Notification only"};
    String[] sync_options={"Automatic","Manual"};
    String[] auto_sync_options=new String[3];

    public static final String sync = "sync";
    SharedPreferences.Editor sync_editor;
    SharedPreferences sync_sharedpreferences;

    String[] dialog_options={"Default Profile","Create New Profile"};

    int auto_hr,auto_min;
    String[] items={ "Silent", "Ring", "Vibrate"};

    boolean[] ischeck=new boolean[1000];

    String bluetooth_status,wifi_status,mobiledata_status,profile_status;

    public static final String[] EVENT_PROJECTION = new String[] {
            CalendarContract.Calendars._ID,                           // 0
            CalendarContract.Calendars.ACCOUNT_NAME,                  // 1
            CalendarContract.Calendars.CALENDAR_DISPLAY_NAME,         // 2
            CalendarContract.Calendars.OWNER_ACCOUNT  ,              // 3
            CalendarContract.Calendars.CALENDAR_COLOR

    };


    ListAdapter adap;


    @Override
    public void show_dialog() {
        profile_adapter = new custom_profile_options(this, items);
        showDialog(7);
    }

    @Override
    public void send_data(String bluetooth_status1, String wifi_status1, String mobiledata_status1, String profile_status1) {
        bluetooth_status=bluetooth_status1;
        wifi_status=wifi_status1;
        mobiledata_status=mobiledata_status1;
        profile_status=profile_status1;
        sync_editor.putString("auto_sync_bluetooth", bluetooth_status);
        sync_editor.commit();
        sync_editor.putString("auto_sync_wifi", wifi_status);
        sync_editor.commit();
        sync_editor.putString("auto_sync_mobiledata", mobiledata_status);
        sync_editor.commit();
        sync_editor.putString("auto_sync_profile", profile_status);
        sync_editor.commit();
        frag.setVisibility(View.INVISIBLE);
        list_view.setVisibility(View.VISIBLE);
        showDialog(3);


    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);


        //to add logo to action bar
        ActionBar ac=getSupportActionBar();
        ac.setDisplayShowHomeEnabled(true);
        ac.setLogo(R.drawable.occasus1);
        ac.setDisplayUseLogoEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);//to hide back button on action bar


        adap=new default_profile_adapter(this,dialog_options);


        frag=findViewById(R.id.set_auto_profile_frag);
        frag.setVisibility(View.INVISIBLE);

        sharedpreferences = getBaseContext().getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        editor = sharedpreferences.edit();


        sync_sharedpreferences = getBaseContext().getSharedPreferences(sync, Context.MODE_PRIVATE);
        sync_editor = sync_sharedpreferences.edit();

        show=sharedpreferences.getString("notif_alarm", "Alarm and Notification");
        sync_auto=sync_sharedpreferences.getString("sync_auto_or_manual","Manual");
        list_view=(ListView) findViewById(R.id.settings_listView);
        list_view.setVisibility(View.VISIBLE);


        String[] settings_selected = {show,sync_auto};

        l_adap=new settings_adapter(this,settings_selected);
        list_view.setAdapter(l_adap);








        list_view.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0) {
                    showDialog(0);
                } else
                    showDialog(1);
            }
        });

        for(int j=0;j<1000;j++)
        {
            ischeck[j]=false;
        }
        for(int j=0;j<sync_sharedpreferences.getInt("gcal_account_avail_count",0);j++)
        {
            if(sync_sharedpreferences.getBoolean(Integer.toString(j),false))
            {
                ischeck[j]=true;
            }
            else
                ischeck[j]=false;
        }

    }


    @Override
    protected void onPrepareDialog(int id, Dialog dialog) {

            int no_of_cal = sync_sharedpreferences.getInt("auto_no_of_cal_to_be_sync", 0);
            String no;
            if (no_of_cal == 0)
                no = "None";
            else
                no = Integer.toString(no_of_cal) + " Calendars Selected";
            String auto_profile = sync_sharedpreferences.getString("auto_sync_profile_default_or_custom", "Default Profile");
            String auto_time = sync_sharedpreferences.getString("auto_sync_time", "1hr 0min");
            auto_sync_options[0] = no;
            auto_sync_options[1] = auto_profile;
            auto_sync_options[2] = auto_time;
            auto_dialog = new settings_adapter(this, auto_sync_options);

    }

    @Override
    protected Dialog onCreateDialog(int id) {

        switch (id)
        {
            case 0:return new AlertDialog.Builder(this)
                    .setTitle("Remind By...")
                    .setIcon(R.drawable.sound_notif)
                    .setSingleChoiceItems(settings_options, position, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                            editor.putString("notif_alarm", settings_options[which]);
                            editor.commit();

                        }
                    })
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Intent intent = new Intent(Settings.this, Settings.class);
                            startActivity(intent);
                        }
                    }).create();


            case 1:  AlertDialog.Builder alert1=new AlertDialog.Builder(this)
                    .setTitle("Sync")
                    .setIcon(R.drawable.sync_dialog)
                    .setSingleChoiceItems(sync_options,start, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            sync_editor.putString("sync_auto_or_manual", sync_options[which]);
                            sync_editor.commit();

                            if (which == 0) {
                                dismissDialog(1);
                                showDialog(3);
                            } else {
                                Intent i=new Intent(Settings.this, auto_sync_start.class);
                                PendingIntent pending= PendingIntent.getBroadcast(getBaseContext(),0,i,PendingIntent.FLAG_CANCEL_CURRENT);
                                pending.cancel();
                                AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
                                alarmManager.cancel(pending);
                                Intent intent = new Intent(Settings.this, Settings.class);
                                startActivity(intent);
                            }
                        }
                    });
                Dialog dialog1=alert1.create();
                return  dialog1;


            case 2:return new AlertDialog.Builder(this)
                    .setTitle("No Calendars Found")
                    .setIcon(R.drawable.not_found)
                    .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            showDialog(3);
                        }
                    }).create();


            case 3:

                 AlertDialog.Builder alert=new AlertDialog.Builder(this);

                    alert.setTitle("Automatic Sync");
                    alert.setIcon(R.drawable.sync_dialog);
                    alert.setAdapter(auto_dialog, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            if (which == 0) {
                                showDialog(4);
                            } else if (which == 1) {
                                showDialog(6);
                            } else {
                                 showDialog(5);
                            }
                        }
                    });
                    alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Calendar cal=Calendar.getInstance();
                            cal.add(Calendar.MILLISECOND,10);
                            Intent i=new Intent(Settings.this, auto_sync_start.class);
                            PendingIntent pending= PendingIntent.getBroadcast(getBaseContext(),0,i,PendingIntent.FLAG_CANCEL_CURRENT);
                            AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
                            alarmManager.set(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), pending);

                            Intent intent = new Intent(Settings.this, Settings.class);
                            startActivity(intent);
                        }
                    });
                Dialog dialog=alert.create();
                onPrepareDialog(3, dialog);
                return dialog;





            case 4:final Cursor cur1 ;
                ContentResolver cr = getContentResolver();
                Uri uri1 = CalendarContract.Calendars.CONTENT_URI;
                //String selection1="((" + CalendarContract.Calendars.CALENDAR_DISPLAY_NAME + " =?))";
                String selection1 = "((" + CalendarContract.Calendars.CALENDAR_DISPLAY_NAME + " = "+CalendarContract.Calendars.ACCOUNT_NAME+")AND("+CalendarContract.Calendars.ACCOUNT_TYPE+" =?))";
                //String[] selectionArgs = new String[] {"sisdbest@gmail.com","com.google","sisdbest@gmail.com"};
                String[] selectionArgs = new String[] {"com.google"};
                // Submit the query and get a Cursor object back.

                cur1 = cr.query(uri1, EVENT_PROJECTION, selection1, selectionArgs, null);
                sync_editor.putInt("gcal_account_avail_count",cur1.getCount());
                sync_editor.commit();
                if(cur1.getCount()==0)
                    showDialog(2);
                else
                {
                    cur1.moveToFirst();
                    int i = 0;
                    String[] calendars_list = new String[cur1.getCount()];
                    do {
                        //cal_list[i] = cur1.getString(2);
                        //calendars_list[i] = cal_list[i];
                        calendars_list[i]=cur1.getString(2);
                        i++;
                    } while (cur1.moveToNext());

                                return new AlertDialog.Builder(this)
                                        .setTitle("Select the Calendars")
                                .setIcon(R.drawable.calendar_dialog)
                                .setMultiChoiceItems(calendars_list, ischeck, new DialogInterface.OnMultiChoiceClickListener() {
                                    @Override
                                public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                                    if(isChecked)
                                        ischeck[which]=true;
                                    else
                                        ischeck[which]=false;
                                }
                            })
                            .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    int count=0;
                                    for(int i=0;i<cur1.getCount();i++)
                                    {
                                        sync_editor.putBoolean(Integer.toString(i),ischeck[i]);
                                        sync_editor.commit();
                                        if(ischeck[i])
                                            count++;
                                    }
                                    sync_editor.putInt("auto_no_of_cal_to_be_sync",count);
                                    sync_editor.commit();
                                    showDialog(3);
                                }
                            }).create();
                }
                break;

            case 5:LayoutInflater inflater = getLayoutInflater();
                View v=inflater.inflate(R.layout.auto_sync_time, null);

                final EditText hr=(EditText) v.findViewById(R.id.sync_hr);
                final EditText min=(EditText) v.findViewById(R.id.sync_min);


                hr.setRawInputType(InputType.TYPE_CLASS_NUMBER);
                min.setRawInputType(InputType.TYPE_CLASS_NUMBER);

                String time=sync_sharedpreferences.getString("auto_sync_time","01hr 00min");
                int j,l;
                for(j=0;j<time.length();j++)
                {
                    if(time.charAt(j)=='h')
                        break;
                }
                hr.setText(time.substring(0,j));
                for(l=j;l<time.length();l++)
                {
                    if(time.charAt(l)=='m')
                        break;
                }
                min.setText(time.substring(j+3,l));
                InputFilter filter=new InputFilter() {
                    @Override
                    public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
                        for(int i=start;i<end;i++)
                        {
                            if((source.charAt(i)=='.')||(source.charAt(i)==' ')||(source.charAt(i)=='-')||
                                    (source.charAt(i)==',')||(source.charAt(i)==10))
                            {
                                return "";
                            }
                        }
                        return null;
                    }
                };

                hr.setFilters(new InputFilter[]{filter});
                min.setFilters(new InputFilter[]{filter});



                return new AlertDialog.Builder(this)
                        .setTitle("Sync Time")
                        .setIcon(R.drawable.clock_dialog)
                        .setView(v)
                        .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                auto_hr=Integer.valueOf(hr.getText().toString());
                                auto_min=Integer.valueOf(min.getText().toString());
                                sync_editor.putString("auto_sync_time",Integer.toString(auto_hr)+"hr "+Integer.toString(auto_min)+"min");
                                sync_editor.commit();
                                showDialog(3);
                            }
                        }).create();


            case 6:return new AlertDialog.Builder(this)
                    .setIcon(R.drawable.profile_dialog)
                    .setTitle("Select a profile")
                    .setAdapter(adap, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            if (which == 0) {
                                sync_editor.putString("auto_sync_profile_default_or_custom", "Default Profile");
                                sync_editor.commit();
                                sync_editor.putString("auto_sync_bluetooth", "no");
                                sync_editor.commit();
                                sync_editor.putString("auto_sync_wifi", "no");
                                sync_editor.commit();
                                sync_editor.putString("auto_sync_mobiledata", "no");
                                sync_editor.commit();
                                sync_editor.putString("auto_sync_profile", "silent");
                                sync_editor.commit();
                                showDialog(3);

                            } else {
                                sync_editor.putString("auto_sync_profile_default_or_custom","Custom Profile");
                                sync_editor.commit();
                                frag.setVisibility(View.VISIBLE);
                                list_view.setVisibility(View.INVISIBLE);
                            }
                        }
                    })
                    /*.setItems(dialog_options, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            if (which == 0) {
                                sync_editor.putString("auto_sync_profile_default_or_custom", "Default Profile");
                                sync_editor.commit();
                                sync_editor.putString("auto_sync_bluetooth", "no");
                                sync_editor.commit();
                                sync_editor.putString("auto_sync_wifi", "no");
                                sync_editor.commit();
                                sync_editor.putString("auto_sync_mobiledata", "no");
                                sync_editor.commit();
                                sync_editor.putString("auto_sync_profile", "silent");
                                sync_editor.commit();
                                showDialog(3);

                            } else {
                                sync_editor.putString("auto_sync_profile_default_or_custom","Custom Profile");
                                sync_editor.commit();
                                frag.setVisibility(View.VISIBLE);
                                list_view.setVisibility(View.INVISIBLE);
                            }
                        }
                    })*/.create();

            case 7:return new AlertDialog.Builder(this)
                    .setIcon(R.drawable.ring_dialog)
                    .setTitle("Choose a profile")
                    .setAdapter(profile_adapter, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            profile_fragment prof=(profile_fragment) getSupportFragmentManager().findFragmentById(R.id.set_auto_profile_frag);
                            prof.change_button_text(which);
                        }
                    }).create();

        }
        return null;
    }


    @Override
    public void onBackPressed() {
        Intent intent = new Intent(this,MainActivity.class);
        startActivity(intent);
    }





    /*public void show_cal_list()
    {

        Cursor cur1 ;
        ContentResolver cr = getContentResolver();
        Uri uri1 = CalendarContract.Calendars.CONTENT_URI;
        //String selection1="((" + CalendarContract.Calendars.CALENDAR_DISPLAY_NAME + " =?))";
        String selection1 = "((" + CalendarContract.Calendars.CALENDAR_DISPLAY_NAME + " = "+CalendarContract.Calendars.ACCOUNT_NAME+")AND("+CalendarContract.Calendars.ACCOUNT_TYPE+" =?))";
        //String[] selectionArgs = new String[] {"sisdbest@gmail.com","com.google","sisdbest@gmail.com"};
        String[] selectionArgs = new String[] {"com.google"};
        // Submit the query and get a Cursor object back.

        cur1 = cr.query(uri1, EVENT_PROJECTION, selection1, selectionArgs, null);
        if(cur1.getCount()==0)
            showDialog(2);
        else
        {
            cur1.moveToFirst();
            int i = 0;
            String[] calendars_list = new String[cur1.getCount()];
            do {
                cal_list[i] = cur1.getString(2);
                calendars_list[i] = cal_list[i];
                i++;
            } while (cur1.moveToNext());
            showDialog(3);
        }

    }*/
}



