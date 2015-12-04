package com.example.dvs.occasus;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.provider.CalendarContract;
import android.widget.Toast;


import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;

public class auto_sync_start extends BroadcastReceiver{

    Context context1;
    int already_synced;

    public static final String[] EVENT_PROJECTION = new String[] {
            CalendarContract.Calendars._ID,                           // 0
            CalendarContract.Calendars.ACCOUNT_NAME,                  // 1
            CalendarContract.Calendars.CALENDAR_DISPLAY_NAME,         // 2
            CalendarContract.Calendars.OWNER_ACCOUNT  ,              // 3
            CalendarContract.Calendars.CALENDAR_COLOR

    };

    public static final String[] Event_projection=new String[]{
            CalendarContract.Events._ID,
            CalendarContract.Events.ACCOUNT_NAME,
            CalendarContract.Events.ACCOUNT_TYPE,
            CalendarContract.Events.TITLE,
            CalendarContract.Events.DTSTART,
            CalendarContract.Events.DTEND,
            CalendarContract.Events.DESCRIPTION,
            CalendarContract.Events.RRULE,
            CalendarContract.Events.DELETED,
            CalendarContract.Events.DURATION


    };

    public static final String[] INSTANCE_PROJECTION = new String[] {
            CalendarContract.Instances.EVENT_ID,      // 0
            CalendarContract.Instances.BEGIN,         // 1
            CalendarContract.Instances.END

    };

    public static final String sync = "sync";
    SharedPreferences.Editor sync_editor;
    SharedPreferences sync_sharedpreferences;


    int start_day,start_month,start_year,end_day,end_month,end_year;
    int added_in_mon_date_in_pending_function,added_in_tue_date_in_pending_function,added_in_wed_date_in_pending_function;
    int added_in_thu_date_in_pending_function,added_in_fri_date_in_pending_function,added_in_sat_date_in_pending_function;
    int added_in_sun_date_in_pending_function;


    public static final String MyPREFERENCES = "MyPrefs";


    String first_pending_start_date,first_pending_start_time,first_pending_end_date,first_pending_end_time;
    int create_pending_intent;
    int id;
    String unique_datetime_key;

    @Override
    public void onReceive(Context context, Intent intent) {
        context1=context;
        check_checked_cal();
    }


    public void check_checked_cal()
    {
        final Cursor cur1;
        ContentResolver cr = context1.getContentResolver();
        Uri uri1 = CalendarContract.Calendars.CONTENT_URI;
        //String selection1="((" + CalendarContract.Calendars.CALENDAR_DISPLAY_NAME + " =?))";
        String selection1 = "((" + CalendarContract.Calendars.CALENDAR_DISPLAY_NAME + " = " + CalendarContract.Calendars.ACCOUNT_NAME + ")AND(" + CalendarContract.Calendars.ACCOUNT_TYPE + " =?))";
        //String[] selectionArgs = new String[] {"sisdbest@gmail.com","com.google","sisdbest@gmail.com"};
        String[] selectionArgs = new String[]{"com.google"};
        // Submit the query and get a Cursor object back.


        sync_sharedpreferences = context1.getSharedPreferences(sync, Context.MODE_PRIVATE);
        sync_editor = sync_sharedpreferences.edit();

        int i = 0;
        cur1 = cr.query(uri1, EVENT_PROJECTION, selection1, selectionArgs, null);
        cur1.moveToFirst();
        do {
            if (sync_sharedpreferences.getBoolean(Integer.toString(i), false)) {
                String cal_name = cur1.getString(2);
                start_sync(cal_name,context1);
            }
            i++;
        } while (cur1.moveToNext());

        int hr,min;
        String time=sync_sharedpreferences.getString("auto_sync_time","01hr 00min");
        int j,l;
        for(j=0;j<time.length();j++)
        {
            if(time.charAt(j)=='h')
                break;
        }
        hr=Integer.valueOf(time.substring(0,j));
        for(l=j;l<time.length();l++)
        {
            if(time.charAt(l)=='m')
                break;
        }
        min=Integer.valueOf(time.substring(j + 3, l));
        Calendar cal=Calendar.getInstance();
        cal.add(Calendar.HOUR_OF_DAY,hr);
        cal.add(Calendar.MINUTE,min);
        Intent intent=new Intent(context1, auto_sync_start.class);
        PendingIntent pending= PendingIntent.getBroadcast(context1,0,intent,0);
        AlarmManager alarmManager = (AlarmManager) context1.getSystemService(Context.ALARM_SERVICE);
        alarmManager.set(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), pending);
    }



    public void start_sync(String cal_name,Context context)
    {

        Cursor cur2;
        ContentResolver cr = context.getContentResolver();

        //Toast.makeText(getActivity().context1, clicked_cal, Toast.LENGTH_SHORT).show();
        Uri uri2= CalendarContract.Events.CONTENT_URI;
        String selection2 = "((" + CalendarContract.Events.CALENDAR_DISPLAY_NAME + " = ?) AND (" + CalendarContract.Events.DELETED + " = 0))";
        String[] selectionArgs2 = new String[]{cal_name};
        cur2=cr.query(uri2,Event_projection,selection2,selectionArgs2,null);
        cur2.moveToFirst();

        if(cur2.getCount()!=0)
        {
            do
            {
                create_pending_intent=0;

                String name = cur2.getString(3);
                String description = cur2.getString(6);

                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
                SimpleDateFormat formatter = new SimpleDateFormat("HH:mm");

                long epoch_start;
                epoch_start = Long.valueOf(cur2.getString(4));
                Date date = new Date(epoch_start);
                String start_date = sdf.format(date);
                String start_time = formatter.format(date);

                String end_date,end_time;
                try
                {
                    long epoch_end;
                    epoch_end = Long.valueOf(cur2.getString(5));
                    Date date1 = new Date(epoch_end);
                    end_date = sdf.format(date1);
                    end_time = formatter.format(date1);
                }
                catch (Exception e)
                {
                    int cal_date = Integer.valueOf(start_date.substring(0, 2));
                    int cal_month = Integer.valueOf(start_date.substring(3, 5)) - 1;
                    int cal_year = Integer.valueOf(start_date.substring(6));
                    int cal_hour = Integer.valueOf(start_time.substring(0, 2));
                    int cal_min = Integer.valueOf(start_time.substring(3));
                    Calendar cal = Calendar.getInstance();
                    cal.set(Calendar.SECOND, 0);
                    cal.set(Calendar.MILLISECOND, 0);
                    cal.set(Calendar.DAY_OF_MONTH, cal_date);
                    cal.set(Calendar.MONTH, cal_month);
                    cal.set(Calendar.YEAR, cal_year);
                    cal.set(Calendar.HOUR_OF_DAY, cal_hour);
                    cal.set(Calendar.MINUTE, cal_min);

                    int duration;
                    duration = Integer.valueOf(cur2.getString(9).substring(1, cur2.getString(9).length() - 1));
                    duration = duration / 3600;
                    cal.add(Calendar.HOUR_OF_DAY, duration);

                    java.util.Date date4 = new java.util.Date(cal.get(Calendar.YEAR) - 1900, cal.get(Calendar.MONTH),
                            cal.get(Calendar.DAY_OF_MONTH), 0, 0);
                    end_date = sdf.format(date4);
                    java.util.Date date3 = new java.util.Date(0, 0, 0, cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE));
                    end_time = formatter.format(date3);
                }

                unique_datetime_key = start_date.concat(start_time);
                String repeat_rules,cur_day_monthly;
                String repeat="",repeat_until="";



                 start_day=Integer.valueOf(start_date.substring(0,2));
                 start_month=Integer.valueOf(start_date.substring(3,5))-1;
                 start_year=Integer.valueOf(start_date.substring(6));
                 end_day=Integer.valueOf(end_date.substring(0,2));
                 end_month=Integer.valueOf(end_date.substring(3,5))-1;
                 end_year=Integer.valueOf(end_date.substring(6));

                Calendar c1=Calendar.getInstance();
                c1.set(Calendar.DAY_OF_MONTH,start_day);
                c1.set(Calendar.MONTH,start_month);
                c1.set(Calendar.YEAR,start_year);
                c1.set(Calendar.HOUR_OF_DAY,0);
                c1.set(Calendar.MINUTE,0);
                c1.set(Calendar.SECOND,0);
                c1.set(Calendar.MILLISECOND,0);

                Calendar c2=Calendar.getInstance();
                c2.set(Calendar.DAY_OF_MONTH, end_day);
                c2.set(Calendar.MONTH,end_month);
                c2.set(Calendar.YEAR,end_year);
                c2.set(Calendar.HOUR_OF_DAY,0);
                c2.set(Calendar.MINUTE,0);
                c2.set(Calendar.SECOND,0);
                c2.set(Calendar.MILLISECOND,0);

                long d1,d2,diff;
                d1=c1.getTimeInMillis();
                d2=c2.getTimeInMillis();
                diff=(d2-d1)/1000;
                diff=diff/3600;
                diff=diff/24;
                int interval=Integer.valueOf(Long.toString(diff));

                repeat_rules = cur2.getString(7);
                cur_day_monthly = "";
                if (repeat_rules == null)
                {
                    repeat = "0";
                    repeat_until = "0";
                }
                else
                {


                    if((repeat_rules.charAt(5)=='W')&&(repeat_rules.charAt(12)=='U')&&(repeat_rules.length()==42))
                    {
                        int year_1=Integer.valueOf(repeat_rules.substring(18,22));
                        int month_1=Integer.valueOf(repeat_rules.substring(22,24))-1;
                        int day_1=Integer.valueOf(repeat_rules.substring(24,26));
                        //java.util.Date da = new java.util.Date(year_1 - 1900, month_1, day_1, 0, 0);
                        Calendar cal_2=Calendar.getInstance();
                        cal_2.set(Calendar.YEAR,year_1);
                        cal_2.set(Calendar.MONTH,month_1);
                        cal_2.set(Calendar.DAY_OF_MONTH,day_1);

                        int day_of_week=cal_2.get(Calendar.DAY_OF_WEEK);
                        if(day_of_week==0)
                            repeat_rules=repeat_rules.concat(";BYDAY=SU");
                        else if(day_of_week==1)
                            repeat_rules=repeat_rules.concat(";BYDAY=MO");
                        else if(day_of_week==2)
                            repeat_rules=repeat_rules.concat(";BYDAY=TU");
                        else if(day_of_week==3)
                            repeat_rules=repeat_rules.concat(";BYDAY=WE");
                        else if(day_of_week==4)
                            repeat_rules=repeat_rules.concat(";BYDAY=TH");
                        else if(day_of_week==5)
                            repeat_rules=repeat_rules.concat(";BYDAY=FR");
                        else if(day_of_week==6)
                            repeat_rules=repeat_rules.concat(";BYDAY=SA");

                        cal_2.add(Calendar.DAY_OF_MONTH,-7);
                        java.util.Date change_month_date = new java.util.Date(cal_2.get(Calendar.YEAR) - 1900, cal_2.get(Calendar.MONTH),
                                cal_2.get(Calendar.DAY_OF_MONTH), 0, 0);
                        String end_change_date = sdf.format(change_month_date);
                        String edit_date_in_until=end_change_date.substring(6)+end_change_date.substring(3,5)+end_change_date.substring(0,2);
                        repeat_rules=repeat_rules.substring(0,18)+edit_date_in_until+repeat_rules.substring(26);
                       // Toast.makeText(getBaseContext(), repeat_rules, Toast.LENGTH_SHORT).show();
                    }

                    else if((repeat_rules.charAt(5)=='M')&&(repeat_rules.charAt(13)=='U')&&(repeat_rules.length()==43))
                    {
                        int year_1=Integer.valueOf(repeat_rules.substring(19,23));
                        int month_1=Integer.valueOf(repeat_rules.substring(23,25))-1;
                        int day_1=Integer.valueOf(repeat_rules.substring(25,27));
                        //java.util.Date da = new java.util.Date(year_1 - 1900, month_1, day_1, 0, 0);
                        Calendar cal_2=Calendar.getInstance();
                        cal_2.set(Calendar.YEAR,year_1);
                        cal_2.set(Calendar.MONTH,month_1);
                        cal_2.set(Calendar.DAY_OF_MONTH,day_1);

                        int month_day=Integer.valueOf(start_date.substring(0, 2));
                        repeat_rules=repeat_rules.concat(";BYMONTHDAY="+Integer.toString(month_day));

                        cal_2.add(Calendar.MONTH,-1);
                        java.util.Date change_month_date = new java.util.Date(cal_2.get(Calendar.YEAR) - 1900, cal_2.get(Calendar.MONTH),
                                cal_2.get(Calendar.DAY_OF_MONTH), 0, 0);
                        String end_change_date = sdf.format(change_month_date);
                        String edit_date_in_until=end_change_date.substring(6)+end_change_date.substring(3,5)+end_change_date.substring(0,2);
                        repeat_rules=repeat_rules.substring(0,19)+edit_date_in_until+repeat_rules.substring(27);
                      //  Toast.makeText(getBaseContext(),repeat_rules,Toast.LENGTH_SHORT).show();
                    }





                    switch (repeat_rules.charAt(5))
                    {
                        case 'D'://repeat="1";
                            if (repeat_rules.charAt(11) == 'C')
                            {
                                int p;
                                for (p = 16; p < repeat_rules.length(); p++)
                                {
                                    if (repeat_rules.charAt(p) == ';')
                                    {
                                        break;
                                    }
                                }
                                repeat_until = "2_" + repeat_rules.substring(17, p);
                                if (repeat_rules.charAt(p + 1) == 'I')
                                {
                                    int y;
                                    for (y = p + 9; y < repeat_rules.length(); y++)
                                    {
                                        if (repeat_rules.charAt(y) == ';')
                                            break;
                                    }
                                    repeat = "5_0_" + repeat_rules.substring(p + 10, y);
                                }
                                else
                                {
                                    repeat = "5_0_1";
                                }
                            }
                            else if (repeat_rules.charAt(11) == 'U')
                            {
                                SimpleDateFormat sdf1 = new SimpleDateFormat("dd/MM/yyyy");
                                int day = Integer.valueOf(repeat_rules.substring(23, 25)) + 1;
                                int month = Integer.valueOf(repeat_rules.substring(21, 23)) - 1;
                                int year = Integer.valueOf(repeat_rules.substring(17, 21));
                                java.util.Date d = new java.util.Date(year - 1900, month, day, 0, 0);
                                String s = sdf1.format(d);
                                repeat_until = "1_" + s;
                                if (repeat_rules.charAt(34) == 'I') {
                                    int p;
                                    for (p = 34; p < repeat_rules.length(); p++) {
                                        if (repeat_rules.charAt(p) == ';')
                                            break;
                                    }
                                    repeat = "5_0_" + repeat_rules.substring(43, p);
                                } else {
                                    repeat = "5_0_1";
                                }
                            } else {
                                repeat_until = "0";
                                if (repeat_rules.charAt(11) == 'I') {
                                    int p;
                                    for (p = 20; p < repeat_rules.length(); p++) {
                                        if (repeat_rules.charAt(p) == ';')
                                            break;
                                    }
                                    repeat = "5_0_" + repeat_rules.substring(20, p);
                                } else
                                    repeat = "5_0_1";
                            }
                            break;
                        case 'W'://repeat="2";
                            if (repeat_rules.charAt(12) == 'C') {
                                int p;
                                char[] days_selected = new char[7];
                                for (int ip = 0; ip < 7; ip++) {
                                    days_selected[ip] = '0';
                                }
                                for (p = 18; p < repeat_rules.length(); p++) {
                                    if (repeat_rules.charAt(p) == ';')
                                        break;
                                }
                                repeat_until = "2_" + repeat_rules.substring(18, p);
                                if (repeat_rules.charAt(p + 1) == 'I') {
                                    int y;
                                    for (y = p + 9; y < repeat_rules.length(); y++) {
                                        if (repeat_rules.charAt(y) == ';')
                                            break;
                                    }
                                    int h = y + 15;
                                    while (h <= repeat_rules.length() - 1) {
                                        if (repeat_rules.charAt(h) == 'M')
                                            days_selected[0] = '1';
                                        else if ((repeat_rules.charAt(h) == 'T') && (repeat_rules.charAt(h + 1) == 'U'))
                                            days_selected[1] = '1';
                                        else if ((repeat_rules.charAt(h) == 'W'))
                                            days_selected[2] = '1';
                                        else if ((repeat_rules.charAt(h) == 'T') && (repeat_rules.charAt(h + 1) == 'H'))
                                            days_selected[3] = '1';
                                        else if (repeat_rules.charAt(h) == 'F')
                                            days_selected[4] = '1';
                                        else if ((repeat_rules.charAt(h) == 'S') && (repeat_rules.charAt(h + 1) == 'A'))
                                            days_selected[5] = '1';
                                        else
                                            days_selected[6] = '1';
                                        h = h + 3;
                                    }
                                    repeat = "5_1_" + repeat_rules.substring(p + 10, y) + "_" + days_selected[0] + days_selected[1] +
                                            days_selected[2] + days_selected[3] + days_selected[4] + days_selected[5] + days_selected[6];
                                } else {
                                    int h;
                                    h = p + 15;
                                    while (h < repeat_rules.length()) {
                                        if (repeat_rules.charAt(h) == 'M')
                                            days_selected[0] = '1';
                                        else if ((repeat_rules.charAt(h) == 'T') && (repeat_rules.charAt(h + 1) == 'U'))
                                            days_selected[1] = '1';
                                        else if ((repeat_rules.charAt(h) == 'W'))
                                            days_selected[2] = '1';
                                        else if ((repeat_rules.charAt(h) == 'T') && (repeat_rules.charAt(h + 1) == 'H'))
                                            days_selected[3] = '1';
                                        else if (repeat_rules.charAt(h) == 'F')
                                            days_selected[4] = '1';
                                        else if ((repeat_rules.charAt(h) == 'S') && (repeat_rules.charAt(h + 1) == 'A'))
                                            days_selected[5] = '1';
                                        else
                                            days_selected[6] = '1';
                                        h = h + 3;
                                    }
                                    repeat = "5_1_1_" + days_selected[0] + days_selected[1] + days_selected[2] + days_selected[3] +
                                            days_selected[4] + days_selected[5] + days_selected[6];
                                }
                            } else if (repeat_rules.charAt(12) == 'U') {
                                char[] days_selected = new char[7];
                                SimpleDateFormat sdf1 = new SimpleDateFormat("dd/MM/yyyy");
                                int day = Integer.valueOf(repeat_rules.substring(24, 26)) + 1;
                                int month = Integer.valueOf(repeat_rules.substring(22, 24)) - 1;
                                int year = Integer.valueOf(repeat_rules.substring(18, 22));
                                java.util.Date d = new java.util.Date(year - 1900, month, day, 0, 0);
                                String s = sdf1.format(d);
                                repeat_until = "1_" + s;
                                for (int ip = 0; ip < 7; ip++) {
                                    days_selected[ip] = '0';
                                }
                                if (repeat_rules.charAt(35) == 'I') {
                                    int y;
                                    for (y = 44; y < repeat_rules.length(); y++) {
                                        if (repeat_rules.charAt(y) == ';')
                                            break;
                                    }
                                    int h;
                                    h = y + 15;
                                    while (h < repeat_rules.length()) {
                                        if (repeat_rules.charAt(h) == 'M')
                                            days_selected[0] = '1';
                                        else if ((repeat_rules.charAt(h) == 'T') && (repeat_rules.charAt(h + 1) == 'U'))
                                            days_selected[1] = '1';
                                        else if ((repeat_rules.charAt(h) == 'W'))
                                            days_selected[2] = '1';
                                        else if ((repeat_rules.charAt(h) == 'T') && (repeat_rules.charAt(h + 1) == 'H'))
                                            days_selected[3] = '1';
                                        else if (repeat_rules.charAt(h) == 'F')
                                            days_selected[4] = '1';
                                        else if ((repeat_rules.charAt(h) == 'S') && (repeat_rules.charAt(h + 1) == 'A'))
                                            days_selected[5] = '1';
                                        else
                                            days_selected[6] = '1';
                                        h = h + 3;
                                    }
                                    repeat = "5_1_" + repeat_rules.substring(44, y) + "_" + days_selected[0] + days_selected[1] +
                                            days_selected[2] + days_selected[3] + days_selected[4] + days_selected[5] + days_selected[6];
                                } else {
                                    int h = 49;
                                    while (h < repeat_rules.length()) {
                                        if (repeat_rules.charAt(h) == 'M')
                                            days_selected[0] = '1';
                                        else if ((repeat_rules.charAt(h) == 'T') && (repeat_rules.charAt(h + 1) == 'U'))
                                            days_selected[1] = '1';
                                        else if ((repeat_rules.charAt(h) == 'W'))
                                            days_selected[2] = '1';
                                        else if ((repeat_rules.charAt(h) == 'T') && (repeat_rules.charAt(h + 1) == 'H'))
                                            days_selected[3] = '1';
                                        else if (repeat_rules.charAt(h) == 'F')
                                            days_selected[4] = '1';
                                        else if ((repeat_rules.charAt(h) == 'S') && (repeat_rules.charAt(h + 1) == 'A'))
                                            days_selected[5] = '1';
                                        else
                                            days_selected[6] = '1';
                                        h = h + 3;
                                    }
                                    repeat = "5_1_1_" + days_selected[0] + days_selected[1] + days_selected[2] + days_selected[3] +
                                            days_selected[4] + days_selected[5] + days_selected[6];
                                }
                            } else {
                                char[] days_selected = new char[7];
                                repeat_until = "0";
                                for (int ip = 0; ip < 7; ip++) {
                                    days_selected[ip] = '0';
                                }
                                if (repeat_rules.charAt(12) == 'I') {
                                    int p;
                                    for (p = 21; p < repeat_rules.length(); p++) {
                                        if (repeat_rules.charAt(p) == ';')
                                            break;
                                    }
                                    int h;
                                    h = p + 15;
                                    while (h < repeat_rules.length()) {
                                        if (repeat_rules.charAt(h) == 'M')
                                            days_selected[0] = '1';
                                        else if ((repeat_rules.charAt(h) == 'T') && (repeat_rules.charAt(h + 1) == 'U'))
                                            days_selected[1] = '1';
                                        else if ((repeat_rules.charAt(h) == 'W'))
                                            days_selected[2] = '1';
                                        else if ((repeat_rules.charAt(h) == 'T') && (repeat_rules.charAt(h + 1) == 'H'))
                                            days_selected[3] = '1';
                                        else if (repeat_rules.charAt(h) == 'F')
                                            days_selected[4] = '1';
                                        else if ((repeat_rules.charAt(h) == 'S') && (repeat_rules.charAt(h + 1) == 'A'))
                                            days_selected[5] = '1';
                                        else
                                            days_selected[6] = '1';
                                        h = h + 3;
                                    }
                                    repeat = "5_1_" + repeat_rules.substring(21, p) + "_" + days_selected[0] + days_selected[1] +
                                            days_selected[2] + days_selected[3] + days_selected[4] + days_selected[5] + days_selected[6];
                                }
                                else {
                                    if (repeat_rules.length() == 19) {
                                        repeat = "2";
                                    } else {
                                        int h = 26;
                                        while (h < repeat_rules.length()) {
                                            if (repeat_rules.charAt(h) == 'M')
                                                days_selected[0] = '1';
                                            else if ((repeat_rules.charAt(h) == 'T') && (repeat_rules.charAt(h + 1) == 'U'))
                                                days_selected[1] = '1';
                                            else if ((repeat_rules.charAt(h) == 'W'))
                                                days_selected[2] = '1';
                                            else if ((repeat_rules.charAt(h) == 'T') && (repeat_rules.charAt(h + 1) == 'H'))
                                                days_selected[3] = '1';
                                            else if (repeat_rules.charAt(h) == 'F')
                                                days_selected[4] = '1';
                                            else if ((repeat_rules.charAt(h) == 'S') && (repeat_rules.charAt(h + 1) == 'A'))
                                                days_selected[5] = '1';
                                            else
                                                days_selected[6] = '1';
                                            h = h + 3;
                                        }
                                        repeat = "5_1_1" + days_selected[0] + days_selected[1] + days_selected[2] + days_selected[3] +
                                                days_selected[4] + days_selected[5] + days_selected[6];
                                    }
                                }
                            }
                            break;
                        case 'M'://repeat="3";
                            int radio;
                            if (repeat_rules.charAt(13) == 'C') {                                                                                                                      int p;
                                for (p = 19; p < repeat_rules.length(); p++)
                                {
                                    if (repeat_rules.charAt(p) == ';')
                                        break;
                                }
                                repeat_until = "2_" + repeat_rules.substring(19, p);
                                if (repeat_rules.charAt(p + 1) == 'I') {
                                    int y;
                                    for (y = p + 10; y < repeat_rules.length(); y++) {
                                        if (repeat_rules.charAt(y) == ';')
                                            break;
                                    }
                                    if (repeat_rules.charAt(y + 11) == 'M') {
                                        if (repeat_rules.charAt(y + 20) == '-')
                                            radio = 3;
                                        else
                                            radio = 1;
                                    } else {
                                        radio = 2;
                                        if (repeat_rules.charAt(15 + y) == '-') {
                                            cur_day_monthly = "on every last ";
                                            if (repeat_rules.charAt(y + 17) == 'M')
                                                cur_day_monthly = cur_day_monthly + "Monday";
                                            else if ((repeat_rules.charAt(y + 17) == 'T') && (repeat_rules.charAt(y + 18) == 'U'))
                                                cur_day_monthly = cur_day_monthly + "Tuesday";
                                            else if ((repeat_rules.charAt(y + 17) == 'W'))
                                                cur_day_monthly = cur_day_monthly + "Wednesday";
                                            else if ((repeat_rules.charAt(y + 17) == 'T') && (repeat_rules.charAt(y + 18) == 'H'))
                                                cur_day_monthly = cur_day_monthly + "Thursday";
                                            else if (repeat_rules.charAt(y + 17) == 'F')
                                                cur_day_monthly = cur_day_monthly + "Friday";
                                            else if ((repeat_rules.charAt(y + 17) == 'S') && (repeat_rules.charAt(y + 18) == 'A'))
                                                cur_day_monthly = cur_day_monthly + "Saturday";
                                            else
                                                cur_day_monthly = cur_day_monthly + "Sunday";
                                        } else {
                                            cur_day_monthly = "on every ";
                                            if (repeat_rules.charAt(y + 15) == '1')
                                                cur_day_monthly = cur_day_monthly + "first ";
                                            else if (repeat_rules.charAt(y + 15) == '2')
                                                cur_day_monthly = cur_day_monthly + "second ";
                                            else if (repeat_rules.charAt(y + 15) == '3')
                                                cur_day_monthly = cur_day_monthly + "third";
                                            else if (repeat_rules.charAt(y + 15) == '4')
                                                cur_day_monthly = cur_day_monthly + "fourth ";

                                            if (repeat_rules.charAt(y + 16) == 'M')
                                                cur_day_monthly = cur_day_monthly + "Monday";
                                            else if ((repeat_rules.charAt(y + 16) == 'T') && (repeat_rules.charAt(y + 17) == 'U'))
                                                cur_day_monthly = cur_day_monthly + "Tuesday";
                                            else if ((repeat_rules.charAt(y + 16) == 'W'))
                                                cur_day_monthly = cur_day_monthly + "Wednesday";
                                            else if ((repeat_rules.charAt(y + 16) == 'T') && (repeat_rules.charAt(y + 17) == 'H'))
                                                cur_day_monthly = cur_day_monthly + "Thursday";
                                            else if (repeat_rules.charAt(y + 16) == 'F')
                                                cur_day_monthly = cur_day_monthly + "Friday";
                                            else if ((repeat_rules.charAt(y + 16) == 'S') && (repeat_rules.charAt(y + 17) == 'A'))
                                                cur_day_monthly = cur_day_monthly + "Saturday";
                                            else
                                                cur_day_monthly = cur_day_monthly + "Sunday";
                                        }
                                    }
                                    repeat = "5_2_" + repeat_rules.substring(19, p) + "_" + Integer.toString(radio) + "_" + start_date.substring(0, 2);
                                } else {
                                    if (repeat_rules.charAt(p + 11) == 'M') {
                                        if (repeat_rules.charAt(p + 20) == '-')
                                            radio = 3;
                                        else
                                            radio = 1;
                                    } else {
                                        radio = 2;
                                        if (repeat_rules.charAt(p + 15) == '-') {
                                            cur_day_monthly = "on every last ";
                                            if (repeat_rules.charAt(p + 17) == 'M')
                                                cur_day_monthly = cur_day_monthly + "Monday";
                                            else if ((repeat_rules.charAt(p + 17) == 'T') && (repeat_rules.charAt(p + 18) == 'U'))
                                                cur_day_monthly = cur_day_monthly + "Tuesday";
                                            else if ((repeat_rules.charAt(p + 17) == 'W'))
                                                cur_day_monthly = cur_day_monthly + "Wednesday";
                                            else if ((repeat_rules.charAt(p + 17) == 'T') && (repeat_rules.charAt(p + 18) == 'H'))
                                                cur_day_monthly = cur_day_monthly + "Thursday";
                                            else if (repeat_rules.charAt(p + 17) == 'F')
                                                cur_day_monthly = cur_day_monthly + "Friday";
                                            else if ((repeat_rules.charAt(p + 17) == 'S') && (repeat_rules.charAt(p + 18) == 'A'))
                                                cur_day_monthly = cur_day_monthly + "Saturday";
                                            else
                                                cur_day_monthly = cur_day_monthly + "Sunday";
                                        } else {
                                            cur_day_monthly = "on every ";
                                            if (repeat_rules.charAt(p + 15) == '1')
                                                cur_day_monthly = cur_day_monthly + "first ";
                                            else if (repeat_rules.charAt(p + 15) == '2')
                                                cur_day_monthly = cur_day_monthly + "second ";
                                            else if (repeat_rules.charAt(p + 15) == '3')
                                                cur_day_monthly = cur_day_monthly + "third";
                                            else if (repeat_rules.charAt(p + 15) == '4')
                                                cur_day_monthly = cur_day_monthly + "fourth ";

                                            if (repeat_rules.charAt(p + 16) == 'M')
                                                cur_day_monthly = cur_day_monthly + "Monday";
                                            else if ((repeat_rules.charAt(p + 16) == 'T') && (repeat_rules.charAt(p + 17) == 'U'))
                                                cur_day_monthly = cur_day_monthly + "Tuesday";
                                            else if ((repeat_rules.charAt(p + 16) == 'W'))
                                                cur_day_monthly = cur_day_monthly + "Wednesday";
                                            else if ((repeat_rules.charAt(p + 16) == 'T') && (repeat_rules.charAt(p + 17) == 'H'))
                                                cur_day_monthly = cur_day_monthly + "Thursday";
                                            else if (repeat_rules.charAt(p + 16) == 'F')
                                                cur_day_monthly = cur_day_monthly + "Friday";
                                            else if ((repeat_rules.charAt(p + 16) == 'S') && (repeat_rules.charAt(p + 17) == 'A'))
                                                cur_day_monthly = cur_day_monthly + "Saturday";
                                            else
                                                cur_day_monthly = cur_day_monthly + "Sunday";
                                        }

                                    }
                                    repeat = "5_2_1_" + Integer.toString(radio) + "_" + start_date.substring(0, 2);
                                }
                            } else if (repeat_rules.charAt(13) == 'U') {
                                SimpleDateFormat sdf1 = new SimpleDateFormat("dd/MM/yyyy");
                                int day = Integer.valueOf(repeat_rules.substring(25, 27)) + 1;
                                int month = Integer.valueOf(repeat_rules.substring(23, 25)) - 1;
                                int year = Integer.valueOf(repeat_rules.substring(19, 23));
                                java.util.Date d = new java.util.Date(year - 1900, month, day, 0, 0);
                                String s = sdf1.format(d);
                                repeat_until = "1_" + s;
                                if (repeat_rules.charAt(36) == 'I') {
                                    int p;
                                    for (p = 45; p < repeat_rules.length(); p++) {
                                        if (repeat_rules.charAt(p) == ';')
                                            break;
                                    }
                                    if (repeat_rules.charAt(p + 11) == 'M') {
                                        if (repeat_rules.charAt(p + 20) == '-')
                                            radio = 3;
                                        else
                                            radio = 1;
                                    } else {
                                        radio = 2;
                                        if (repeat_rules.charAt(15 + p) == '-') {
                                            cur_day_monthly = "on every last ";
                                            if (repeat_rules.charAt(p + 17) == 'M')
                                                cur_day_monthly = cur_day_monthly + "Monday";
                                            else if ((repeat_rules.charAt(p + 17) == 'T') && (repeat_rules.charAt(p + 18) == 'U'))
                                                cur_day_monthly = cur_day_monthly + "Tuesday";
                                            else if ((repeat_rules.charAt(p + 17) == 'W'))
                                                cur_day_monthly = cur_day_monthly + "Wednesday";
                                            else if ((repeat_rules.charAt(p + 17) == 'T') && (repeat_rules.charAt(p + 18) == 'H'))
                                                cur_day_monthly = cur_day_monthly + "Thursday";
                                            else if (repeat_rules.charAt(p + 17) == 'F')
                                                cur_day_monthly = cur_day_monthly + "Friday";
                                            else if ((repeat_rules.charAt(p + 17) == 'S') && (repeat_rules.charAt(p + 18) == 'A'))
                                                cur_day_monthly = cur_day_monthly + "Saturday";
                                            else
                                                cur_day_monthly = cur_day_monthly + "Sunday";
                                        } else {
                                            cur_day_monthly = "on every ";
                                            if (repeat_rules.charAt(p + 15) == '1')
                                                cur_day_monthly = cur_day_monthly + "first ";
                                            else if (repeat_rules.charAt(p + 15) == '2')
                                                cur_day_monthly = cur_day_monthly + "second ";
                                            else if (repeat_rules.charAt(p + 15) == '3')
                                                cur_day_monthly = cur_day_monthly + "third";
                                            else if (repeat_rules.charAt(p + 15) == '4')
                                                cur_day_monthly = cur_day_monthly + "fourth ";

                                            if (repeat_rules.charAt(p + 16) == 'M')
                                                cur_day_monthly = cur_day_monthly + "Monday";
                                            else if ((repeat_rules.charAt(p + 16) == 'T') && (repeat_rules.charAt(p + 17) == 'U'))
                                                cur_day_monthly = cur_day_monthly + "Tuesday";
                                            else if ((repeat_rules.charAt(p + 16) == 'W'))
                                                cur_day_monthly = cur_day_monthly + "Wednesday";
                                            else if ((repeat_rules.charAt(p + 16) == 'T') && (repeat_rules.charAt(p + 17) == 'H'))
                                                cur_day_monthly = cur_day_monthly + "Thursday";
                                            else if (repeat_rules.charAt(p + 16) == 'F')
                                                cur_day_monthly = cur_day_monthly + "Friday";
                                            else if ((repeat_rules.charAt(p + 16) == 'S') && (repeat_rules.charAt(p + 17) == 'A'))
                                                cur_day_monthly = cur_day_monthly + "Saturday";
                                            else
                                                cur_day_monthly = cur_day_monthly + "Sunday";
                                        }
                                    }
                                    repeat = "5_2_" + repeat_rules.substring(45, p) + "_" + Integer.toString(radio) + "_" + start_date.substring(0, 2);
                                } else {
                                    if (repeat_rules.charAt(46) == 'M') {
                                        if (repeat_rules.charAt(55) == '-')
                                            radio = 3;
                                        else
                                            radio = 1;
                                    } else {
                                        radio = 2;
                                        if (repeat_rules.charAt(50) == '-') {
                                            cur_day_monthly = "on every last ";
                                            if (repeat_rules.charAt(52) == 'M')
                                                cur_day_monthly = cur_day_monthly + "Monday";
                                            else if ((repeat_rules.charAt(52) == 'T') && (repeat_rules.charAt(53) == 'U'))
                                                cur_day_monthly = cur_day_monthly + "Tuesday";
                                            else if ((repeat_rules.charAt(52) == 'W'))
                                                cur_day_monthly = cur_day_monthly + "Wednesday";
                                            else if ((repeat_rules.charAt(52) == 'T') && (repeat_rules.charAt(53) == 'H'))
                                                cur_day_monthly = cur_day_monthly + "Thursday";
                                            else if (repeat_rules.charAt(52) == 'F')
                                                cur_day_monthly = cur_day_monthly + "Friday";
                                            else if ((repeat_rules.charAt(52) == 'S') && (repeat_rules.charAt(53) == 'A'))
                                                cur_day_monthly = cur_day_monthly + "Saturday";
                                            else
                                                cur_day_monthly = cur_day_monthly + "Sunday";
                                        } else {
                                            cur_day_monthly = "on every ";
                                            if (repeat_rules.charAt(50) == '1')
                                                cur_day_monthly = cur_day_monthly + "first ";
                                            else if (repeat_rules.charAt(50) == '2')
                                                cur_day_monthly = cur_day_monthly + "second ";
                                            else if (repeat_rules.charAt(50) == '3')
                                                cur_day_monthly = cur_day_monthly + "third";
                                            else if (repeat_rules.charAt(50) == '4')
                                                cur_day_monthly = cur_day_monthly + "fourth ";

                                            if (repeat_rules.charAt(51) == 'M')
                                                cur_day_monthly = cur_day_monthly + "Monday";
                                            else if ((repeat_rules.charAt(51) == 'T') && (repeat_rules.charAt(52) == 'U'))
                                                cur_day_monthly = cur_day_monthly + "Tuesday";
                                            else if ((repeat_rules.charAt(51) == 'W'))
                                                cur_day_monthly = cur_day_monthly + "Wednesday";
                                            else if ((repeat_rules.charAt(51) == 'T') && (repeat_rules.charAt(52) == 'H'))
                                                cur_day_monthly = cur_day_monthly + "Thursday";
                                            else if (repeat_rules.charAt(51) == 'F')
                                                cur_day_monthly = cur_day_monthly + "Friday";
                                            else if ((repeat_rules.charAt(51) == 'S') && (repeat_rules.charAt(52) == 'A'))
                                                cur_day_monthly = cur_day_monthly + "Saturday";
                                            else
                                                cur_day_monthly = cur_day_monthly + "Sunday";
                                        }
                                    }
                                    repeat = "5_2_1_" + Integer.toString(radio) + "_" + start_date.substring(0, 2);
                                }
                            } else {
                                repeat_until = "0";
                                if (repeat_rules.charAt(13) == 'I') {
                                    int p;
                                    for (p = 22; p < repeat_rules.length(); p++) {
                                        if (repeat_rules.charAt(p) == ';')
                                            break;
                                    }
                                    if (repeat_rules.charAt(p + 11) == 'M') {
                                        if (repeat_rules.charAt(p + 20) == '-')
                                            radio = 3;
                                        else
                                            radio = 1;
                                    } else {
                                        radio = 2;
                                        if (repeat_rules.charAt(p + 15) == '-') {
                                            cur_day_monthly = "on every last ";
                                            if (repeat_rules.charAt(p + 17) == 'M')
                                                cur_day_monthly = cur_day_monthly + "Monday";
                                            else if ((repeat_rules.charAt(p + 17) == 'T') && (repeat_rules.charAt(p + 18) == 'U'))
                                                cur_day_monthly = cur_day_monthly + "Tuesday";
                                            else if ((repeat_rules.charAt(p + 17) == 'W'))
                                                cur_day_monthly = cur_day_monthly + "Wednesday";
                                            else if ((repeat_rules.charAt(p + 17) == 'T') && (repeat_rules.charAt(p + 18) == 'H'))
                                                cur_day_monthly = cur_day_monthly + "Thursday";
                                            else if (repeat_rules.charAt(p + 17) == 'F')
                                                cur_day_monthly = cur_day_monthly + "Friday";
                                            else if ((repeat_rules.charAt(p + 17) == 'S') && (repeat_rules.charAt(p + 18) == 'A'))
                                                cur_day_monthly = cur_day_monthly + "Saturday";
                                            else
                                                cur_day_monthly = cur_day_monthly + "Sunday";
                                        } else {
                                            cur_day_monthly = "on every ";
                                            if (repeat_rules.charAt(p + 15) == '1')
                                                cur_day_monthly = cur_day_monthly + "first ";
                                            else if (repeat_rules.charAt(p + 15) == '2')
                                                cur_day_monthly = cur_day_monthly + "second ";
                                            else if (repeat_rules.charAt(p + 15) == '3')
                                                cur_day_monthly = cur_day_monthly + "third";
                                            else if (repeat_rules.charAt(p + 15) == '4')
                                                cur_day_monthly = cur_day_monthly + "fourth ";

                                            if (repeat_rules.charAt(p + 16) == 'M')
                                                cur_day_monthly = cur_day_monthly + "Monday";
                                            else if ((repeat_rules.charAt(p + 16) == 'T') && (repeat_rules.charAt(p + 17) == 'U'))
                                                cur_day_monthly = cur_day_monthly + "Tuesday";
                                            else if ((repeat_rules.charAt(p + 16) == 'W'))
                                                cur_day_monthly = cur_day_monthly + "Wednesday";
                                            else if ((repeat_rules.charAt(p + 16) == 'T') && (repeat_rules.charAt(p + 17) == 'H'))
                                                cur_day_monthly = cur_day_monthly + "Thursday";
                                            else if (repeat_rules.charAt(p + 16) == 'F')
                                                cur_day_monthly = cur_day_monthly + "Friday";
                                            else if ((repeat_rules.charAt(p + 16) == 'S') && (repeat_rules.charAt(p + 17) == 'A'))
                                                cur_day_monthly = cur_day_monthly + "Saturday";
                                            else
                                                cur_day_monthly = cur_day_monthly + "Sunday";
                                        }
                                    }
                                    repeat = "5_2_" + repeat_rules.substring(22, p) + "_" + Integer.toString(radio) + "_" + start_date.substring(0, 2);
                                } else {

                                    if(repeat_rules.length()==20)
                                    {
                                        repeat="3";
                                    }
                                    else {
                                        if (repeat_rules.charAt(23) == 'M') {
                                            if (repeat_rules.charAt(32) == '-')
                                                radio = 3;
                                            else
                                                radio = 1;
                                        } else {
                                            radio = 2;
                                            if (repeat_rules.charAt(27) == '-') {
                                                cur_day_monthly = "on every last ";
                                                if (repeat_rules.charAt(29) == 'M')
                                                    cur_day_monthly = cur_day_monthly + "Monday";
                                                else if ((repeat_rules.charAt(29) == 'T') && (repeat_rules.charAt(30) == 'U'))
                                                    cur_day_monthly = cur_day_monthly + "Tuesday";
                                                else if ((repeat_rules.charAt(29) == 'W'))
                                                    cur_day_monthly = cur_day_monthly + "Wednesday";
                                                else if ((repeat_rules.charAt(29) == 'T') && (repeat_rules.charAt(30) == 'H'))
                                                    cur_day_monthly = cur_day_monthly + "Thursday";
                                                else if (repeat_rules.charAt(29) == 'F')
                                                    cur_day_monthly = cur_day_monthly + "Friday";
                                                else if ((repeat_rules.charAt(29) == 'S') && (repeat_rules.charAt(30) == 'A'))
                                                    cur_day_monthly = cur_day_monthly + "Saturday";
                                                else
                                                    cur_day_monthly = cur_day_monthly + "Sunday";
                                            } else {
                                                cur_day_monthly = "on every ";
                                                if (repeat_rules.charAt(27) == '1')
                                                    cur_day_monthly = cur_day_monthly + "first ";
                                                else if (repeat_rules.charAt(27) == '2')
                                                    cur_day_monthly = cur_day_monthly + "second ";
                                                else if (repeat_rules.charAt(27) == '3')
                                                    cur_day_monthly = cur_day_monthly + "third";
                                                else if (repeat_rules.charAt(27) == '4')
                                                    cur_day_monthly = cur_day_monthly + "fourth ";

                                                if (repeat_rules.charAt(28) == 'M')
                                                    cur_day_monthly = cur_day_monthly + "Monday";
                                                else if ((repeat_rules.charAt(28) == 'T') && (repeat_rules.charAt(29) == 'U'))
                                                    cur_day_monthly = cur_day_monthly + "Tuesday";
                                                else if ((repeat_rules.charAt(28) == 'W'))
                                                    cur_day_monthly = cur_day_monthly + "Wednesday";
                                                else if ((repeat_rules.charAt(28) == 'T') && (repeat_rules.charAt(29) == 'H'))
                                                    cur_day_monthly = cur_day_monthly + "Thursday";
                                                else if (repeat_rules.charAt(28) == 'F')
                                                    cur_day_monthly = cur_day_monthly + "Friday";
                                                else if ((repeat_rules.charAt(28) == 'S') && (repeat_rules.charAt(29) == 'A'))
                                                    cur_day_monthly = cur_day_monthly + "Saturday";
                                                else
                                                    cur_day_monthly = cur_day_monthly + "Sunday";
                                            }
                                        }
                                        repeat = "5_2_1_" + Integer.toString(radio) + "_" + start_date.substring(0, 2);

                                    }

                                }
                            }
                            break;
                        case 'Y'://repeat="4";
                            if (repeat_rules.charAt(12) == 'C') {
                                int p;
                                for (p = 18; p < repeat_rules.length(); p++) {
                                    if (repeat_rules.charAt(p) == ';')
                                        break;
                                }
                                repeat_until = "2_" + repeat_rules.substring(18, p);
                                if (repeat_rules.charAt(p + 1) == 'I') {
                                    int y;
                                    for (y = p + 10; y < repeat_rules.length(); y++) {
                                        if (repeat_rules.charAt(y) == ';')
                                            break;
                                    }
                                    repeat = "5_3_" + repeat_rules.substring(p + 10, y);
                                } else {
                                    repeat = "5_3_1";
                                }
                            } else if (repeat_rules.charAt(12) == 'U') {
                                SimpleDateFormat sdf1 = new SimpleDateFormat("dd/MM/yyyy");
                                int day = Integer.valueOf(repeat_rules.substring(24, 26)) + 1;
                                int month = Integer.valueOf(repeat_rules.substring(22, 24)) - 1;
                                int year = Integer.valueOf(repeat_rules.substring(18, 22));
                                java.util.Date d = new java.util.Date(year - 1900, month, day, 0, 0);
                                String s = sdf1.format(d);
                                repeat_until = "1_" + s;
                                if (repeat_rules.charAt(35) == 'I') {
                                    int y;
                                    for (y = 44; y < repeat_rules.length(); y++) {
                                        if (repeat_rules.charAt(y) == ';')
                                            break;
                                    }
                                    repeat = "5_3_" + repeat_rules.substring(44, y);
                                } else
                                    repeat = "5_3_1";
                            } else {
                                repeat_until = "0";
                                if (repeat_rules.charAt(12) == 'I') {
                                    int p;
                                    for (p = 21; p < repeat_rules.length(); p++) {
                                        if (repeat_rules.charAt(p) == ';')
                                            break;
                                    }
                                    repeat = "5_3_" + repeat_rules.substring(21, p);
                                } else
                                    repeat = "5_3_1";
                            }
                            break;
                    }
                }

                DBAdapter db1 = new DBAdapter(context);
                db1.open();
                Cursor c;
                c = db1.getAllEventsDetails();
                already_synced=0;



                if (c.getCount() > 0)
                {

                    c.moveToFirst();
                    do
                    {

                        if (
                                (name.equals(c.getString(c.getColumnIndex("event_name"))))  &&
                                        (start_date.equals(c.getString(c.getColumnIndex("event_start_date")))) &&
                                        (end_date.equals(c.getString(c.getColumnIndex("event_end_date")))) &&
                                        (start_time.equals(c.getString(c.getColumnIndex("start_time")))) &&
                                        (end_time.equals(c.getString(c.getColumnIndex("end_time")))) &&
                                        (repeat.equals(c.getString(c.getColumnIndex("repeat")))) &&
                                        (repeat_until.equals(c.getString(c.getColumnIndex("repeat_until"))))&&
                                        (
                                                ((description==null)&&(c.getString(c.getColumnIndex("description"))==null)) ||
                                                        (description.equals(c.getString(c.getColumnIndex("description")))))
                                )
                        {
                            already_synced=1;

                            break;
                        }
                    } while (c.moveToNext());
                    if(already_synced==0)
                    {


                        DBAdapter db= new DBAdapter(context);
                        db.open();
                        String bluetooth_status=sync_sharedpreferences.getString("auto_sync_bluetooth","");
                        String wifi_status=sync_sharedpreferences.getString("auto_sync_wifi","");
                        String mobiledata_status=sync_sharedpreferences.getString("auto_sync_mobiledata","");
                        String profile_status=sync_sharedpreferences.getString("auto_sync_profile","");

                        db.insertevent(name, description, start_date, end_date, start_time, end_time, unique_datetime_key,
                                bluetooth_status, wifi_status, profile_status, mobiledata_status, repeat, repeat_until,
                                cur_day_monthly, interval,start_date,1);
                        db.close();
                        
                        
                        
                        
                        
                        String get_id=cur2.getString(0);
                        Calendar beginTime = Calendar.getInstance();
                        beginTime.set(1990, 5, 26, 8, 0);
                        long startMillis = beginTime.getTimeInMillis();
                        Calendar endTime = Calendar.getInstance();
                        endTime.set(4000, 1, 6, 8, 0);
                        long endMillis = endTime.getTimeInMillis();

                        String selection3 = "((" + CalendarContract.Instances.CALENDAR_DISPLAY_NAME + " = ?) AND (" + CalendarContract.Instances.EVENT_ID + " = ?))";
                        String[] selectionArgs3 = new String[]{cal_name,get_id};

// Construct the query with the desired date range.
                        Uri.Builder builder = CalendarContract.Instances.CONTENT_URI.buildUpon();
                        ContentUris.appendId(builder, startMillis);
                        ContentUris.appendId(builder, endMillis);

// Submit the query
                        Cursor cur3 = context1.getContentResolver().query(builder.build(),
                                INSTANCE_PROJECTION,
                                selection3,
                                selectionArgs3,
                                null);
                        cur3.moveToFirst();
                        if(cur3.getCount()>0) {
                            do {

                                long epoch_rep_end;
                                epoch_rep_end = Long.valueOf(cur3.getString(2));
                                Date date5 = new Date(epoch_rep_end);
                                first_pending_end_date = sdf.format(date5);
                                //Toast.makeText(context1,rep_end_date,Toast.LENGTH_SHORT).show();
                                SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm");
                                first_pending_end_time = timeFormat.format(date5);


                                Calendar calnow = Calendar.getInstance();
                                java.util.Date d = new java.util.Date(calnow.get(Calendar.YEAR) - 1900, calnow.get(Calendar.MONTH),
                                        calnow.get(Calendar.DAY_OF_MONTH), calnow.get(Calendar.HOUR_OF_DAY), calnow.get(Calendar.MINUTE));
                                String today_date = sdf.format(d);
                                String today_time = timeFormat.format(d);

                                //Toast.makeText(context1,today_date,Toast.LENGTH_SHORT).show();

                                if (dateidentifier(first_pending_end_date, today_date) == 2) {


                                    long epoch_rep_start;
                                    epoch_rep_start = Long.valueOf(cur3.getString(1));
                                    Date date6 = new Date(epoch_rep_start);
                                    first_pending_start_date = sdf.format(date6);
                                    first_pending_start_time = timeFormat.format(date5);
                                    sync_editor.putString("pending_start_date", first_pending_start_date);
                                    sync_editor.commit();
                                    sync_editor.putString("pending_start_time", first_pending_start_time);
                                    sync_editor.commit();


                                    create_pending_intent = 1;

                                    break;
                                } else if (dateidentifier(first_pending_end_date, today_date) == 3) {
                                    if (time_identifier1(first_pending_end_time, today_time) == 2) {
                                        long epoch_rep_start;
                                        epoch_rep_start = Long.valueOf(cur3.getString(1));
                                        Date date6 = new Date(epoch_rep_start);
                                        first_pending_start_date = sdf.format(date6);
                                        first_pending_start_time = timeFormat.format(date5);
                                        sync_editor.putString("pending_start_date", first_pending_start_date);
                                        sync_editor.commit();
                                        sync_editor.putString("pending_start_time", first_pending_start_time);
                                        sync_editor.commit();


                                        create_pending_intent = 1;

                                        break;
                                    }
                                }


                            } while (cur3.moveToNext());

                        }

                        if(create_pending_intent==1)
                        {

                            Calendar start=Calendar.getInstance();
                            start_day=Integer.valueOf(first_pending_start_date.substring(0, 2));
                            start_month=Integer.valueOf(first_pending_start_date.substring(3,5))-1;
                            start_year=Integer.valueOf(first_pending_start_date.substring(6));
                            int start_hour1=Integer.valueOf(first_pending_start_time.substring(0,2));
                            int start_min1=Integer.valueOf(first_pending_start_time.substring(3));
                            start.set(Calendar.DAY_OF_MONTH,start_day);
                            start.set(Calendar.MONTH,start_month);
                            start.set(Calendar.YEAR,start_year);
                            start.set(Calendar.HOUR_OF_DAY,start_hour1);
                            start.set(Calendar.MINUTE,start_min1);
                            start.set(Calendar.SECOND,0);
                            start.set(Calendar.MILLISECOND,0);

                            start_eve(start,repeat,repeat_until,cur_day_monthly);




                            Calendar end=Calendar.getInstance();
                             end_day=Integer.valueOf(first_pending_end_date.substring(0, 2));
                             end_month=Integer.valueOf(first_pending_end_date.substring(3,5))-1;
                             end_year=Integer.valueOf(first_pending_end_date.substring(6));
                            int end_hour1=Integer.valueOf(first_pending_end_time.substring(0,2));
                            int end_min1=Integer.valueOf(first_pending_end_time.substring(3));
                            end.set(Calendar.DAY_OF_MONTH,end_day);
                            end.set(Calendar.MONTH,end_month);
                            end.set(Calendar.YEAR,end_year);
                            end.set(Calendar.HOUR_OF_DAY,end_hour1);
                            end.set(Calendar.MINUTE,end_min1);
                            end.set(Calendar.MILLISECOND,0);
                            end.set(Calendar.SECOND, 0);

                            end_eve(end,repeat,repeat_until,cur_day_monthly);


                        }
                        
                        
                        
                        

                    }

                }
                else
                {

                    DBAdapter db= new DBAdapter(context);
                    db.open();
                    String bluetooth_status=sync_sharedpreferences.getString("auto_sync_bluetooth","");
                    String wifi_status=sync_sharedpreferences.getString("auto_sync_wifi","");
                    String mobiledata_status=sync_sharedpreferences.getString("auto_sync_mobiledata","");
                    String profile_status=sync_sharedpreferences.getString("auto_sync_profile","");

                    db.insertevent(name, description, start_date, end_date, start_time, end_time, unique_datetime_key,
                            bluetooth_status, wifi_status, profile_status, mobiledata_status, repeat, repeat_until,
                            cur_day_monthly, interval,start_date,1);
                    db.close();

                    
                    

                    String get_id=cur2.getString(0);
                    Calendar beginTime = Calendar.getInstance();
                    beginTime.set(1990, 5, 26, 8, 0);
                    long startMillis = beginTime.getTimeInMillis();
                    Calendar endTime = Calendar.getInstance();
                    endTime.set(4000, 1, 6, 8, 0);
                    long endMillis = endTime.getTimeInMillis();

                    String selection3 = "((" + CalendarContract.Instances.CALENDAR_DISPLAY_NAME + " = ?) AND (" + CalendarContract.Instances.EVENT_ID + " = ?))";
                    String[] selectionArgs3 = new String[]{cal_name,get_id};

// Construct the query with the desired date range.
                    Uri.Builder builder = CalendarContract.Instances.CONTENT_URI.buildUpon();
                    ContentUris.appendId(builder, startMillis);
                    ContentUris.appendId(builder, endMillis);

// Submit the query
                    Cursor cur3 = context1.getContentResolver().query(builder.build(),
                            INSTANCE_PROJECTION,
                            selection3,
                            selectionArgs3,
                            null);
                    cur3.moveToFirst();
                    if(cur3.getCount()>0) {
                        do {

                            long epoch_rep_end;
                            epoch_rep_end = Long.valueOf(cur3.getString(2));
                            Date date5 = new Date(epoch_rep_end);
                            first_pending_end_date = sdf.format(date5);
                            //Toast.makeText(context1,rep_end_date,Toast.LENGTH_SHORT).show();
                            SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm");
                            first_pending_end_time = timeFormat.format(date5);


                            Calendar calnow = Calendar.getInstance();
                            java.util.Date d = new java.util.Date(calnow.get(Calendar.YEAR) - 1900, calnow.get(Calendar.MONTH),
                                    calnow.get(Calendar.DAY_OF_MONTH), calnow.get(Calendar.HOUR_OF_DAY), calnow.get(Calendar.MINUTE));
                            String today_date = sdf.format(d);
                            String today_time = timeFormat.format(d);

                            //Toast.makeText(context1,today_date,Toast.LENGTH_SHORT).show();

                            if (dateidentifier(first_pending_end_date, today_date) == 2) {


                                long epoch_rep_start;
                                epoch_rep_start = Long.valueOf(cur3.getString(1));
                                Date date6 = new Date(epoch_rep_start);
                                first_pending_start_date = sdf.format(date6);
                                first_pending_start_time = timeFormat.format(date5);
                                sync_editor.putString("pending_start_date", first_pending_start_date);
                                sync_editor.commit();
                                sync_editor.putString("pending_start_time", first_pending_start_time);
                                sync_editor.commit();


                                create_pending_intent = 1;

                                break;
                            } else if (dateidentifier(first_pending_end_date, today_date) == 3) {
                                if (time_identifier1(first_pending_end_time, today_time) == 2) {
                                    long epoch_rep_start;
                                    epoch_rep_start = Long.valueOf(cur3.getString(1));
                                    Date date6 = new Date(epoch_rep_start);
                                    first_pending_start_date = sdf.format(date6);
                                    first_pending_start_time = timeFormat.format(date5);
                                    sync_editor.putString("pending_start_date", first_pending_start_date);
                                    sync_editor.commit();
                                    sync_editor.putString("pending_start_time", first_pending_start_time);
                                    sync_editor.commit();


                                    create_pending_intent = 1;

                                    break;
                                }
                            }


                        } while (cur3.moveToNext());
                    }



                    if(create_pending_intent==1)
                    {

                        Calendar start=Calendar.getInstance();
                        start_day=Integer.valueOf(first_pending_start_date.substring(0, 2));
                        start_month=Integer.valueOf(first_pending_start_date.substring(3,5))-1;
                        start_year=Integer.valueOf(first_pending_start_date.substring(6));
                        int start_hour1=Integer.valueOf(first_pending_start_time.substring(0,2));
                        int start_min1=Integer.valueOf(first_pending_start_time.substring(3));
                        start.set(Calendar.DAY_OF_MONTH,start_day);
                        start.set(Calendar.MONTH,start_month);
                        start.set(Calendar.YEAR,start_year);
                        start.set(Calendar.HOUR_OF_DAY,start_hour1);
                        start.set(Calendar.MINUTE,start_min1);
                        start.set(Calendar.SECOND,0);
                        start.set(Calendar.MILLISECOND,0);

                        start_eve(start,repeat,repeat_until,cur_day_monthly);




                        Calendar end=Calendar.getInstance();
                         end_day=Integer.valueOf(first_pending_end_date.substring(0, 2));
                         end_month=Integer.valueOf(first_pending_end_date.substring(3,5))-1;
                         end_year=Integer.valueOf(first_pending_end_date.substring(6));
                        int end_hour=Integer.valueOf(first_pending_end_time.substring(0,2));
                        int end_min1=Integer.valueOf(first_pending_end_time.substring(3));
                        end.set(Calendar.DAY_OF_MONTH,end_day);
                        end.set(Calendar.MONTH,end_month);
                        end.set(Calendar.YEAR,end_year);
                        end.set(Calendar.HOUR_OF_DAY,end_hour);
                        end.set(Calendar.MINUTE,end_min1);
                        end.set(Calendar.MILLISECOND,0);
                        end.set(Calendar.SECOND, 0);

                        end_eve(end,repeat,repeat_until,cur_day_monthly);


                    }


                }
                db1.close();

            } while (cur2.moveToNext());
        }
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




    //returns 1 if time1<time2
    //returns 2 if time1>time2
    //returns 3 if equal
    public int time_identifier1(String time1,String time2) {

        int asci1 = 0, asci2 = 0, as;
        int i;
        for (i = 0; i <= 4; i++)
        {
            //ch gets the char at position i in string time1
            char ch = time1.charAt(i);
            if(ch!=':')
            {
                as = (int) ch;
                as = as - 48;
                int j;
                j = (int) Math.pow(10, 4 - i);
                as = as * j;
                asci1 = asci1 + as;
            }
        }
        for (i = 0; i <= 4; i++)
        {

            char ch = time2.charAt(i);
            if(ch!=':')
            {
                as = (int) ch;
                as = as - 48;
                int j;
                j = (int) Math.pow(10, 4 - i);
                as = as * j;
                asci2 = asci2 + as;
            }
        }
        //asci1 contains the polynomial hashing value of time1 string
        //asci2 contains the polynomial hashing value of time2 string
        if(asci1==asci2)
            return 3;
        else if(asci1<asci2)
            return 1;
        else
            return 2;



    }







    //creates pending intent for start time
    private void start_eve(Calendar targetCal,String rep,String rep_until,String cur_dayofweek_for_cus_monthly_rep)
    {

        int cur_day;

        cur_day=targetCal.get(Calendar.DAY_OF_WEEK);
        int happens_after;

        Intent intent = new Intent(context1, Activate_event.class);
        //name sent to activate_event class....there it is used to retrieve all the details of the event

        //ret_id used as a unique key

        DBAdapter db= new DBAdapter(context1);
        db.open();
        Cursor c=db.getEventDetail1(unique_datetime_key);
        if(c.moveToFirst())
        {
            id=c.getInt(c.getColumnIndex("_id"));
        }


        //shared preferences declared
        SharedPreferences sharedpreferences;
        //shared preferences editor declared
        sharedpreferences = context1.getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor;
        //initializing shared preferences
        editor = sharedpreferences.edit();


        editor.putInt("no_of_times_event_ran" + Integer.toString(id), 0);
        editor.commit();




        intent.putExtra("id",id);
        //if new event is repeated
        intent.putExtra("rep", rep);

        intent.putExtra("rep_until",rep_until);
        intent.putExtra("cur_dayofweek_for_cus_monthly_rep",cur_dayofweek_for_cus_monthly_rep);

        if((rep.charAt(0)=='5')&&(rep.charAt(2)=='1'))
        {
            happens_after=0;
            if(cur_day==1)
            {
                happens_after=1;
            }
            else
            {
                for (int i = rep.length() + cur_day - 9; i <= rep.length() - 2; i++)
                {
                    if (rep.charAt(i) == '1')
                    {
                        happens_after = 1;
                        break;
                    }

                }
            }
            if (rep.charAt(rep.length()-7)=='1')
            {
                added_in_mon_date_in_pending_function=0;
                GregorianCalendar date = new GregorianCalendar(start_year, start_month, start_day);
                while (date.get(Calendar.DAY_OF_WEEK) != Calendar.MONDAY)
                {
                    date.add(Calendar.DATE, 1);//get the date of the latest monday from date entered
                    added_in_mon_date_in_pending_function++;
                }

                if((cur_day>2)&&(happens_after==1))
                {
                    added_in_mon_date_in_pending_function=added_in_mon_date_in_pending_function+7*(Integer.valueOf(rep.substring(4, rep.length() - 8))-1);
                    date.add(Calendar.DAY_OF_MONTH,7*(Integer.valueOf(rep.substring(4, rep.length() - 8))-1));
                }
                targetCal.set(Calendar.DAY_OF_MONTH, date.get(Calendar.DAY_OF_MONTH));
                targetCal.set(Calendar.MONTH, date.get(Calendar.MONTH));
                targetCal.set(Calendar.YEAR, date.get(Calendar.YEAR));



                PendingIntent pendingIntent = PendingIntent.getBroadcast(context1, id, intent, 0);
                AlarmManager alarmManager = (AlarmManager) context1.getSystemService(Context.ALARM_SERVICE);
                alarmManager.set(AlarmManager.RTC_WAKEUP, targetCal.getTimeInMillis(), pendingIntent);

            }

            if (rep.charAt(rep.length()-6)=='1')
            {
                added_in_tue_date_in_pending_function=0;
                GregorianCalendar date = new GregorianCalendar(start_year, start_month, start_day);
                while (date.get(Calendar.DAY_OF_WEEK) != Calendar.TUESDAY)
                {
                    date.add(Calendar.DATE, 1);//get the date of the latest tuesday from date entered
                    added_in_tue_date_in_pending_function++;
                }

                if((cur_day>3)&&(happens_after==1))
                {
                    added_in_tue_date_in_pending_function=added_in_tue_date_in_pending_function+7*(Integer.valueOf(rep.substring(4, rep.length() - 8))-1);
                    date.add(Calendar.DAY_OF_MONTH,7*(Integer.valueOf(rep.substring(4, rep.length() - 8))-1));
                }
                targetCal.set(Calendar.DAY_OF_MONTH, date.get(Calendar.DAY_OF_MONTH));
                targetCal.set(Calendar.MONTH, date.get(Calendar.MONTH));
                targetCal.set(Calendar.YEAR, date.get(Calendar.YEAR));




                PendingIntent pendingIntent = PendingIntent.getBroadcast(context1, id, intent, 0);
                AlarmManager alarmManager = (AlarmManager) context1.getSystemService(Context.ALARM_SERVICE);
                alarmManager.set(AlarmManager.RTC_WAKEUP, targetCal.getTimeInMillis(), pendingIntent);


            }

            if (rep.charAt(rep.length()-5)=='1')
            {
                added_in_wed_date_in_pending_function=0;
                GregorianCalendar date = new GregorianCalendar(start_year, start_month, start_day);

                while (date.get(Calendar.DAY_OF_WEEK) != Calendar.WEDNESDAY)
                {
                    added_in_wed_date_in_pending_function++;
                    date.add(Calendar.DATE, 1);//get the date of the latest wednesday from date entered
                }
                if((cur_day>4)&&(happens_after==1))
                {
                    added_in_wed_date_in_pending_function=added_in_wed_date_in_pending_function+7*(Integer.valueOf(rep.substring(4, rep.length() - 8))-1);
                    date.add(Calendar.DAY_OF_MONTH,7*(Integer.valueOf(rep.substring(4, rep.length() - 8))-1));
                }

                targetCal.set(Calendar.DAY_OF_MONTH, date.get(Calendar.DAY_OF_MONTH));
                targetCal.set(Calendar.MONTH, date.get(Calendar.MONTH));
                targetCal.set(Calendar.YEAR, date.get(Calendar.YEAR));



                PendingIntent pendingIntent = PendingIntent.getBroadcast(context1, id, intent, 0);
                AlarmManager alarmManager = (AlarmManager) context1.getSystemService(Context.ALARM_SERVICE);
                alarmManager.set(AlarmManager.RTC_WAKEUP, targetCal.getTimeInMillis(), pendingIntent);

            }

            if (rep.charAt(rep.length()-4)=='1')
            {
                added_in_thu_date_in_pending_function=0;
                GregorianCalendar date = new GregorianCalendar(start_year, start_month, start_day);

                while (date.get(Calendar.DAY_OF_WEEK) != Calendar.THURSDAY)
                {
                    added_in_thu_date_in_pending_function ++;
                    date.add(Calendar.DATE, 1);//get the date of the latest thursday from date entered
                }
                if((cur_day>5)&&(happens_after==1))
                {
                    added_in_thu_date_in_pending_function=added_in_thu_date_in_pending_function+7*(Integer.valueOf(rep.substring(4, rep.length() - 8))-1);
                    date.add(Calendar.DAY_OF_MONTH,7*(Integer.valueOf(rep.substring(4, rep.length() - 8))-1));

                }
                targetCal.set(Calendar.DAY_OF_MONTH, date.get(Calendar.DAY_OF_MONTH));
                targetCal.set(Calendar.MONTH, date.get(Calendar.MONTH));
                targetCal.set(Calendar.YEAR, date.get(Calendar.YEAR));



                PendingIntent pendingIntent = PendingIntent.getBroadcast(context1, id, intent, 0);
                AlarmManager alarmManager = (AlarmManager) context1.getSystemService(Context.ALARM_SERVICE);
                alarmManager.set(AlarmManager.RTC_WAKEUP, targetCal.getTimeInMillis(), pendingIntent);

            }

            if (rep.charAt(rep.length()-3)=='1')
            {
                added_in_fri_date_in_pending_function=0;
                GregorianCalendar date = new GregorianCalendar(start_year, start_month, start_day);
                while (date.get(Calendar.DAY_OF_WEEK) != Calendar.FRIDAY) {
                    added_in_fri_date_in_pending_function++;
                    date.add(Calendar.DATE, 1);//get the date of the latest friday from date entered
                }
                if((cur_day>6)&&(happens_after==1))
                {
                    date.add(Calendar.DAY_OF_MONTH,7*(Integer.valueOf(rep.substring(4, rep.length() - 8))-1));
                    added_in_fri_date_in_pending_function=added_in_fri_date_in_pending_function+7*(Integer.valueOf(rep.substring(4, rep.length() - 8))-1);
                }

                targetCal.set(Calendar.DAY_OF_MONTH, date.get(Calendar.DAY_OF_MONTH));
                targetCal.set(Calendar.MONTH, date.get(Calendar.MONTH));
                targetCal.set(Calendar.YEAR, date.get(Calendar.YEAR));



                PendingIntent pendingIntent = PendingIntent.getBroadcast(context1, id, intent, 0);
                AlarmManager alarmManager = (AlarmManager) context1.getSystemService(Context.ALARM_SERVICE);
                alarmManager.set(AlarmManager.RTC_WAKEUP, targetCal.getTimeInMillis(), pendingIntent);

            }

            if (rep.charAt(rep.length()-2)=='1')
            {
                added_in_sat_date_in_pending_function=0;
                GregorianCalendar date = new GregorianCalendar(start_year, start_month, start_day);
                while (date.get(Calendar.DAY_OF_WEEK) != Calendar.SATURDAY) {
                    added_in_sat_date_in_pending_function++;
                    date.add(Calendar.DATE, 1);//get the date of the latest saturday from date entered
                }

                targetCal.set(Calendar.DAY_OF_MONTH, date.get(Calendar.DAY_OF_MONTH));
                targetCal.set(Calendar.MONTH, date.get(Calendar.MONTH));
                targetCal.set(Calendar.YEAR, date.get(Calendar.YEAR));




                PendingIntent pendingIntent = PendingIntent.getBroadcast(context1, id, intent, 0);
                AlarmManager alarmManager = (AlarmManager) context1.getSystemService(Context.ALARM_SERVICE);
                alarmManager.set(AlarmManager.RTC_WAKEUP, targetCal.getTimeInMillis(), pendingIntent);

            }

            if (rep.charAt(rep.length()-1)=='1')
            {
                added_in_sun_date_in_pending_function=0;
                GregorianCalendar date = new GregorianCalendar(start_year, start_month, start_day);
                while (date.get(Calendar.DAY_OF_WEEK) != Calendar.SUNDAY)
                {
                    added_in_sun_date_in_pending_function++;
                    date.add(Calendar.DATE, 1);//get the date of the latest sunday from date entered
                }
                if((cur_day>1)&&(happens_after==1))
                {
                    date.add(Calendar.DAY_OF_MONTH,7*(Integer.valueOf(rep.substring(4, rep.length() - 8))-1));
                    added_in_sun_date_in_pending_function=added_in_sun_date_in_pending_function+7*(Integer.valueOf(rep.substring(4, rep.length() - 8))-1);
                }
                targetCal.set(Calendar.DAY_OF_MONTH, date.get(Calendar.DAY_OF_MONTH));
                targetCal.set(Calendar.MONTH, date.get(Calendar.MONTH));
                targetCal.set(Calendar.YEAR, date.get(Calendar.YEAR));




                PendingIntent pendingIntent = PendingIntent.getBroadcast(context1, id, intent, 0);
                AlarmManager alarmManager = (AlarmManager) context1.getSystemService(Context.ALARM_SERVICE);
                alarmManager.set(AlarmManager.RTC_WAKEUP, targetCal.getTimeInMillis(), pendingIntent);

            }

        }

        else
        {
            PendingIntent pendingIntent = PendingIntent.getBroadcast(context1, id, intent, 0);
            AlarmManager alarmManager = (AlarmManager) context1.getSystemService(Context.ALARM_SERVICE);
            alarmManager.set(AlarmManager.RTC_WAKEUP, targetCal.getTimeInMillis(), pendingIntent);


        }
    }





    //creates pending intent for end time
    private void end_eve(Calendar targetCal,String rep,String rep_until,String cur_dayofweek_for_cus_monthly_rep)
    {

        Intent intent = new Intent(context1, deactivate_event.class);


        DBAdapter db= new DBAdapter(context1);
        db.open();
        Cursor c=db.getEventDetail1(unique_datetime_key);
        if(c.moveToFirst())
        {
            id=c.getInt(c.getColumnIndex("_id"));
        }
        intent.putExtra("id", id);

        //if new event is repeated
        intent.putExtra("rep", rep);
        intent.putExtra("rep_until", rep_until);
        intent.putExtra("cur_dayofweek_for_cus_monthly_rep", cur_dayofweek_for_cus_monthly_rep);

        if((rep.charAt(0)=='5')&&(rep.charAt(2)=='1'))
        {
            if (rep.charAt(rep.length()-7)=='1')
            {


                targetCal.add(Calendar.DAY_OF_MONTH, added_in_mon_date_in_pending_function);


                PendingIntent pendingIntent = PendingIntent.getBroadcast(context1,id , intent, 0);
                AlarmManager alarmManager = (AlarmManager) context1.getSystemService(Context.ALARM_SERVICE);
                alarmManager.set(AlarmManager.RTC_WAKEUP, targetCal.getTimeInMillis(), pendingIntent);

                added_in_mon_date_in_pending_function=(-1)*added_in_mon_date_in_pending_function;
                targetCal.add(Calendar.DAY_OF_MONTH,added_in_mon_date_in_pending_function);
            }

            if (rep.charAt(rep.length()-6)=='1')
            {


                targetCal.add(Calendar.DAY_OF_MONTH, added_in_tue_date_in_pending_function);





                PendingIntent pendingIntent = PendingIntent.getBroadcast(context1, id, intent, 0);
                AlarmManager alarmManager = (AlarmManager) context1.getSystemService(Context.ALARM_SERVICE);
                alarmManager.set(AlarmManager.RTC_WAKEUP, targetCal.getTimeInMillis(), pendingIntent);

                added_in_tue_date_in_pending_function=(-1)*added_in_tue_date_in_pending_function;
                targetCal.add(Calendar.DAY_OF_MONTH, added_in_tue_date_in_pending_function);

            }

            if (rep.charAt(rep.length()-5)=='1')
            {

                targetCal.add(Calendar.DAY_OF_MONTH, added_in_wed_date_in_pending_function);



                PendingIntent pendingIntent = PendingIntent.getBroadcast(context1, id, intent, 0);
                AlarmManager alarmManager = (AlarmManager) context1.getSystemService(Context.ALARM_SERVICE);
                alarmManager.set(AlarmManager.RTC_WAKEUP, targetCal.getTimeInMillis(), pendingIntent);

                added_in_wed_date_in_pending_function=(-1)*added_in_wed_date_in_pending_function;
                targetCal.add(Calendar.DAY_OF_MONTH, added_in_wed_date_in_pending_function);

            }


            if (rep.charAt(rep.length()-4)=='1') {



                targetCal.add(Calendar.DAY_OF_MONTH, added_in_thu_date_in_pending_function);




                PendingIntent pendingIntent = PendingIntent.getBroadcast(context1, id, intent, 0);
                AlarmManager alarmManager = (AlarmManager) context1.getSystemService(Context.ALARM_SERVICE);
                alarmManager.set(AlarmManager.RTC_WAKEUP, targetCal.getTimeInMillis(), pendingIntent);

                added_in_thu_date_in_pending_function=(-1)*added_in_thu_date_in_pending_function;
                targetCal.add(Calendar.DAY_OF_MONTH, added_in_thu_date_in_pending_function);

            }


            if (rep.charAt(rep.length()-3)=='1') {



                targetCal.add(Calendar.DAY_OF_MONTH, added_in_fri_date_in_pending_function);




                PendingIntent pendingIntent = PendingIntent.getBroadcast(context1, id, intent, 0);
                AlarmManager alarmManager = (AlarmManager) context1.getSystemService(Context.ALARM_SERVICE);
                alarmManager.set(AlarmManager.RTC_WAKEUP, targetCal.getTimeInMillis(), pendingIntent);

                added_in_fri_date_in_pending_function=(-1)*added_in_fri_date_in_pending_function;
                targetCal.add(Calendar.DAY_OF_MONTH, added_in_fri_date_in_pending_function);

            }


            if (rep.charAt(rep.length()-2)=='1')
            {

                targetCal.add(Calendar.DAY_OF_MONTH, added_in_sat_date_in_pending_function);



                PendingIntent pendingIntent = PendingIntent.getBroadcast(context1, id, intent, 0);
                AlarmManager alarmManager = (AlarmManager) context1.getSystemService(Context.ALARM_SERVICE);
                alarmManager.set(AlarmManager.RTC_WAKEUP, targetCal.getTimeInMillis(), pendingIntent);


                added_in_sat_date_in_pending_function=(-1)*added_in_sat_date_in_pending_function;
                targetCal.add(Calendar.DAY_OF_MONTH, added_in_sat_date_in_pending_function);
            }


            if (rep.charAt(rep.length()-1)=='1') {


                targetCal.add(Calendar.DAY_OF_MONTH, added_in_sun_date_in_pending_function);



                PendingIntent pendingIntent = PendingIntent.getBroadcast(context1, id, intent, 0);
                AlarmManager alarmManager = (AlarmManager) context1.getSystemService(Context.ALARM_SERVICE);
                alarmManager.set(AlarmManager.RTC_WAKEUP, targetCal.getTimeInMillis(), pendingIntent);

                added_in_sun_date_in_pending_function=(-1)*added_in_sun_date_in_pending_function;
                targetCal.add(Calendar.DAY_OF_MONTH, added_in_sun_date_in_pending_function);

            }
        }
        else //if event doesn't repeat, simply create pending intent for end
        {
            PendingIntent pendingIntent = PendingIntent.getBroadcast(context1, id, intent, 0);
            AlarmManager alarmManager = (AlarmManager) context1.getSystemService(Context.ALARM_SERVICE);
            alarmManager.set(AlarmManager.RTC_WAKEUP, targetCal.getTimeInMillis(), pendingIntent);
        }
    }




}
