package com.example.dvs.occasus;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class nextday_update extends BroadcastReceiver{


    SharedPreferences sharedpreferences;
    SharedPreferences.Editor editor;
    public static final String MyPREFERENCES = "MyPrefs";

    int today_day,today_month,today_year,today_hour,today_min;
    String end_time;
    String today_date,today_time;

    @Override
    public void onReceive(Context context, Intent intent) {
        //Toast.makeText(context,"hell yeah",Toast.LENGTH_SHORT).show();


        sharedpreferences = context.getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        end_time=sharedpreferences.getString("nextday_update_end_date", "");


        Calendar now=Calendar.getInstance();
        today_day=now.get(Calendar.DAY_OF_MONTH);
        today_month=now.get(Calendar.MONTH);
        today_year=now.get(Calendar.YEAR);
        today_hour=now.get(Calendar.HOUR_OF_DAY);
        today_min=now.get(Calendar.MINUTE);


        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm");
        Date date = new Date(today_year-1900,today_month,today_day,0,0);
        Date date1=new Date(0,0,0,today_hour,today_min);
        today_date = dateFormat.format(date);
        today_time=timeFormat.format(date1);


    }
}
