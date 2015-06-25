package com.example.dvs.occasus;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.provider.CalendarContract;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.view.ActionMode;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.TextView;
import android.widget.Toast;


public class sync extends ActionBarActivity {


    public static final String[] EVENT_PROJECTION = new String[] {
            CalendarContract.Calendars._ID,                           // 0
            CalendarContract.Calendars.ACCOUNT_NAME,                  // 1
            CalendarContract.Calendars.CALENDAR_DISPLAY_NAME,         // 2
            CalendarContract.Calendars.OWNER_ACCOUNT  ,              // 3
            CalendarContract.Calendars.CALENDAR_COLOR

    };


    ListAdapter cal_adapter;
    String[] cal_list=new String[100];
    int i;
    String clicked_cal;

    TextView no_cal;


    public static final String sync = "sync";
    SharedPreferences.Editor sync_editor;
    SharedPreferences sync_sharedpreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sync);


    }


    @Override
    protected void onStart() {
        super.onStart();

        no_cal=(TextView) findViewById(R.id.no_cal_found);


        sync_sharedpreferences = getBaseContext().getSharedPreferences(sync, Context.MODE_PRIVATE);
        sync_editor = sync_sharedpreferences.edit();


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
        {
            no_cal.setVisibility(View.VISIBLE);
        }
        else {

            no_cal.setVisibility(View.INVISIBLE);
            cur1.moveToFirst();
            i = 0;
            String[] calendars_list = new String[cur1.getCount()];
            String[] cal_color = new String[cur1.getCount()];
            do {
                cal_list[i] = cur1.getString(2);
                calendars_list[i] = cal_list[i];
                cal_color[i] = cur1.getString(4);
                i++;
            } while (cur1.moveToNext());

            //cal_adapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1 ,calendars_list);
            cal_adapter = new custom_adap_calendar(this, calendars_list, cal_color);

            showDialog(0);
        }

    }




    @Override
    protected Dialog onCreateDialog(int id) {

        switch (id)
        {
            case 0:return new AlertDialog.Builder(this)
                    .setTitle("Calendars")
                    .setAdapter(cal_adapter, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            clicked_cal = cal_list[which];
                            showevents();
                        }
                    }).create();

        }

        return null;
    }


    public void showevents()
    {
        sync_editor.putString("cal_name", clicked_cal);
        sync_editor.commit();

        Intent intent=new Intent(this,sync_event_list.class);
        startActivity(intent);
    }





}
