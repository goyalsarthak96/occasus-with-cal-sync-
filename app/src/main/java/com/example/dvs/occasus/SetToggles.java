package com.example.dvs.occasus;


import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.media.AudioManager;
import android.support.v7.app.ActionBar;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.Toast;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ToggleButton;

import java.util.Calendar;
import java.util.GregorianCalendar;


public class SetToggles extends ActionBarActivity
{

    String bluetooth="no",wifi1="no",mobile_data="no";

    String profile="ring";

    String[] items = { "Silent", "Ring", "Vibrate"};
    AudioManager am;
    int flag;

    String stime;
    int start_day,start_month,start_year,end_day,end_month,end_year;
    String name;
    Integer shour;
    Integer ehour;
    Integer sminute;
    Integer eminute;
    String rep_until;
    Button b1;
    String start_date;
    String end_date;
    String unique_datetime_key;
    int id;
    String cur_dayofweek_for_cus_monthly_rep;
    ListAdapter ringadapter;
    int added_in_mon_date_in_pending_function,added_in_tue_date_in_pending_function,added_in_wed_date_in_pending_function;
    int added_in_thu_date_in_pending_function,added_in_fri_date_in_pending_function,added_in_sat_date_in_pending_function;
    int added_in_sun_date_in_pending_function;


    public static final String MyPREFERENCES = "MyPrefs";

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_toggles);


        //to add logo to action bar
        ActionBar ac=getSupportActionBar();
        ac.setDisplayShowHomeEnabled(true);
        ac.setLogo(R.drawable.occasus1);
        ac.setDisplayUseLogoEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);//to hide backbuttton in action bar


        Intent intent = getIntent();
        //initializing audio manager




        ringadapter = new custom_profile_options(this, items);


        b1=(Button)findViewById(R.id.profile_button);
        b1.setText("Ring");




        am = (AudioManager)getSystemService(Context.AUDIO_SERVICE);//getting audio manager






        //flag received from create event
        //flag=0 means that new event is getting created
        flag=intent.getIntExtra("flag",0);
        //flag=1 means that existing event is being edited
        if(flag==1)
        {

            ToggleButton b;
            b=(ToggleButton) findViewById(R.id.bluetooth_button);

            bluetooth=intent.getStringExtra("bluetooth");
            //status equals yes means bluetooth was supposed to be on so toggle button is turned on by b.setchecked
            if(bluetooth.equals("yes"))
            {
                b.setChecked(true);
            }
            //status equals no means bluetooth was supposed to be off so toggle button is turned off by b.setchecked
            else
            {
                b.setChecked(false);
            }
            b = (ToggleButton) findViewById(R.id.wifi_button);



            //getting earlier wifi toggle button state

            wifi1= intent.getStringExtra("wifi");
            //status equals yes means wifi was supposed to be on so toggle button is turned on by b.setchecked
            if(wifi1.equals("yes"))
            {
                b.setChecked(true);
            }
            //status equals no means wifi was supposed to be off so toggle button is turned off by b.setchecked
            else
            {
                b.setChecked(false);
            }
            b = (ToggleButton) findViewById(R.id.mobiledata_button);



            mobile_data= intent.getStringExtra("mobile_data");
            if(mobile_data.equals("yes"))
            {
                b.setChecked(true);
            }
            else
            {
                b.setChecked(false);
            }




            profile= intent.getStringExtra("profile");
            //profile contains the radiobutton that was checked in profile daialog box when event was created earlier
            b1.setText(profile);



        }
    }





    //on click listener for bluetooth toggle switch
    public void bluetooth_settings(View view)
    {

        boolean on = ((ToggleButton) view).isChecked();
        //on==true means that bluetooth toggle button is set to on
        if (on)
        {
            //bluetooth=yes means that bluetooth toggle switch is set to yes
            bluetooth="yes";
         }
        else
        {
            //bluetooth=no means that bluetooth toggle switch is set to no
            bluetooth="no";
        }
    }



    //onclick listener for profile button
    public void silent_settings(View view)
    {
       //shows the dialog box containing profile options
        showDialog(0);
    }





    //onclick listener for mobiledata toggle button
    public void mobiledata_settings(View view){

        boolean on = ((ToggleButton) view).isChecked();

        if(on)
        {
            mobile_data= "yes";
        }
        else
        {
            mobile_data="no";
        }
    }






    //called by showdialog automatically
        @Override
        protected Dialog onCreateDialog(int id) {


            switch (id) {
                case 0:
                    return new AlertDialog.Builder(this)
                            .setIcon(R.drawable.ring_dialog)
                            .setTitle("Choose a profile")

                            .setAdapter(ringadapter,new DialogInterface.OnClickListener(){
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            if (which == 0)
                                            {
                                                profile = "silent";

                                                b1.setText("Silent");
                                            }
                                            else if (which == 1)
                                            {
                                                profile = "ring";
                                                //may be there is no need to set check to some value

                                                b1.setText("Ring");
                                            }
                                            else if (which == 2)
                                            {
                                                profile = "vibrate";

                                                b1.setText("Vibrate");
                                            }
                                        }
                                    }
                            ).create();
            }
            return null;
    }




    //onclick listener for wifi toggle switch
    public void wifi_settings(View view)
    {
        boolean on = ((ToggleButton) view).isChecked();
        //on==true means wifi toggle button is set on
        if(on)
        {
            wifi1="yes";
        }
        else
        {
            wifi1="no";
        }
    }




    //onclick for save button
    public void save_event(View view)
    {
        //name description date(string) start time(string) end time(sting) add(int) retrieved from create event class
        //these values are inserted into database later in save_event method
         name = getIntent().getStringExtra("Name");
        String desc = getIntent().getStringExtra("Description");
         start_date = getIntent().getStringExtra("start_date");
        end_date=getIntent().getStringExtra("end_date");
         stime = getIntent().getStringExtra("STime");
        String etime = getIntent().getStringExtra("ETime");

        String rep=getIntent().getStringExtra("custom_repeat");
        rep_until=getIntent().getStringExtra("repeat_until");
        cur_dayofweek_for_cus_monthly_rep=getIntent().getStringExtra("cur_dayofweek_for_cus_monthly_rep");

        //add=1 means that event repeats


        //day(int) month(int)year(int) shour(int) ehour(int) sminute(int) eminute(int) retrieved from create_event class
         start_day= getIntent().getIntExtra("int_start_day",1);
        end_day=getIntent().getIntExtra("int_end_day",1);
        start_month= getIntent().getIntExtra("int_start_month", 1);
        end_month= getIntent().getIntExtra("int_end_month",1);
        start_year=getIntent().getIntExtra("int_start_year", 1);
         end_year= getIntent().getIntExtra("int_end_year",1);
         shour= getIntent().getIntExtra("int_shour",1);
         ehour= getIntent().getIntExtra("int_ehour", 1);
        sminute= getIntent().getIntExtra("int_sminute",1);
         eminute= getIntent().getIntExtra("int_eminute", 1);




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
        int interval;
        interval=Integer.valueOf(Long.toString(diff));




        unique_datetime_key=start_date.concat(stime);

        if((rep.charAt(0)=='5')&&(rep.charAt(2)=='2'))
        {
            rep=rep.concat("_"+Integer.toString(start_day));
        }

        DBAdapter db=new DBAdapter(this);
        db.open();
        if(flag==1)//if event is getting edit
        {
            int id_to_be_edited = getIntent().getIntExtra("id_to_be_edited", 0);
            db.deleteEvent(id_to_be_edited);//delete the older version
            db.open();
            db.insertevent(name, desc, start_date, end_date, stime, etime, unique_datetime_key,
                    bluetooth, wifi1, profile, mobile_data, rep,rep_until,cur_dayofweek_for_cus_monthly_rep,interval,start_date,0);
           //inserts the new version
           Toast.makeText(getBaseContext(), "event successfully edited", Toast.LENGTH_SHORT).show();

        }
        else
        {
                    //event details inserted into database
            try
            {
                db.insertevent(name, desc, start_date, end_date, stime, etime, unique_datetime_key,
                        bluetooth, wifi1, profile, mobile_data, rep, rep_until,cur_dayofweek_for_cus_monthly_rep,interval,
                        start_date,0);
                Toast.makeText(getBaseContext(), "event successfully created", Toast.LENGTH_SHORT).show();

            }
            catch (Exception e)
            {
                String ec = e.getMessage();
                Toast.makeText(getBaseContext(), ec, Toast.LENGTH_SHORT).show();
            }
        }




        Calendar calNow = Calendar.getInstance();
        Calendar calSet = (Calendar) calNow.clone();

        //calset contains the calender instance of the time when event should start
        calSet.set(Calendar.YEAR, start_year);
        calSet.set(Calendar.MONTH, start_month);
        calSet.set(Calendar.DAY_OF_MONTH, start_day);
        calSet.set(Calendar.HOUR_OF_DAY, shour);
        calSet.set(Calendar.MINUTE, sminute);
        calSet.set(Calendar.SECOND, 0);
        calSet.set(Calendar.MILLISECOND, 0);




        Calendar calSet1 = (Calendar) calNow.clone();

        //calset1 contains the calender instance of the time when event should end
        calSet1.set(Calendar.YEAR, end_year);
        calSet1.set(Calendar.MONTH, end_month);
        calSet1.set(Calendar.DAY_OF_MONTH, end_day);//create ending pending intent for one day after the enter date
        calSet1.set(Calendar.HOUR_OF_DAY, ehour);
        calSet1.set(Calendar.MINUTE, eminute);
        calSet1.set(Calendar.SECOND, 0);
        calSet1.set(Calendar.MILLISECOND, 0);



        setAlarm(calSet, rep,rep_until,cur_dayofweek_for_cus_monthly_rep);//creates pending intent for start of event
        endeve(calSet1, rep,rep_until,cur_dayofweek_for_cus_monthly_rep);//crates pending intent for end of event


        db.close();


        //screen restored to mainscreen
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);


    }






    //creates pending intent for start time
    private void setAlarm(Calendar targetCal,String rep,String rep_until,String cur_dayofweek_for_cus_monthly_rep)
    {

        int cur_day;

        cur_day=targetCal.get(Calendar.DAY_OF_WEEK);
        int happens_after;

        Intent intent = new Intent(getBaseContext(), Activate_event.class);
        //name sent to activate_event class....there it is used to retrieve all the details of the event

        //ret_id used as a unique key

        DBAdapter db= new DBAdapter(SetToggles.this);
        db.open();
        Cursor c=db.getEventDetail1(unique_datetime_key);
        if(c.moveToFirst())
        {
            id=c.getInt(c.getColumnIndex("_id"));
        }


        //shared preferences declared
        SharedPreferences sharedpreferences;
        //shared preferences editor declared
        sharedpreferences = getBaseContext().getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor;
        //initializing shared preferences
        editor = sharedpreferences.edit();


        editor.putInt("no_of_times_event_ran"+Integer.toString(id), 0);
        editor.commit();




        intent.putExtra("id",id);
        //if new event is repeated
        intent.putExtra("rep",rep);
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
                targetCal.set(Calendar.DAY_OF_MONTH,date.get(Calendar.DAY_OF_MONTH));
                targetCal.set(Calendar.MONTH,date.get(Calendar.MONTH));
                targetCal.set(Calendar.YEAR,date.get(Calendar.YEAR));




                PendingIntent pendingIntent = PendingIntent.getBroadcast(getBaseContext(), id, intent, 0);
                AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
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
                targetCal.set(Calendar.DAY_OF_MONTH,date.get(Calendar.DAY_OF_MONTH));
                targetCal.set(Calendar.MONTH,date.get(Calendar.MONTH));
                targetCal.set(Calendar.YEAR,date.get(Calendar.YEAR));



                PendingIntent pendingIntent = PendingIntent.getBroadcast(getBaseContext(), id, intent, 0);
                AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
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

                targetCal.set(Calendar.DAY_OF_MONTH,date.get(Calendar.DAY_OF_MONTH));
                targetCal.set(Calendar.MONTH,date.get(Calendar.MONTH));
                targetCal.set(Calendar.YEAR,date.get(Calendar.YEAR));




                PendingIntent pendingIntent = PendingIntent.getBroadcast(getBaseContext(), id, intent, 0);
                AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
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
                targetCal.set(Calendar.DAY_OF_MONTH,date.get(Calendar.DAY_OF_MONTH));
                targetCal.set(Calendar.MONTH,date.get(Calendar.MONTH));
                targetCal.set(Calendar.YEAR,date.get(Calendar.YEAR));



                PendingIntent pendingIntent = PendingIntent.getBroadcast(getBaseContext(), id, intent, 0);
                AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
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

                targetCal.set(Calendar.DAY_OF_MONTH,date.get(Calendar.DAY_OF_MONTH));
                targetCal.set(Calendar.MONTH,date.get(Calendar.MONTH));
                targetCal.set(Calendar.YEAR,date.get(Calendar.YEAR));





                PendingIntent pendingIntent = PendingIntent.getBroadcast(getBaseContext(), id, intent, 0);
                AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
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

                targetCal.set(Calendar.DAY_OF_MONTH,date.get(Calendar.DAY_OF_MONTH));
                targetCal.set(Calendar.MONTH,date.get(Calendar.MONTH));
                targetCal.set(Calendar.YEAR,date.get(Calendar.YEAR));





                PendingIntent pendingIntent = PendingIntent.getBroadcast(getBaseContext(), id, intent, 0);
                AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
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
                targetCal.set(Calendar.DAY_OF_MONTH,date.get(Calendar.DAY_OF_MONTH));
                targetCal.set(Calendar.MONTH,date.get(Calendar.MONTH));
                targetCal.set(Calendar.YEAR,date.get(Calendar.YEAR));




                PendingIntent pendingIntent = PendingIntent.getBroadcast(getBaseContext(), id, intent, 0);
                AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
                alarmManager.set(AlarmManager.RTC_WAKEUP, targetCal.getTimeInMillis(), pendingIntent);

            }

        }

        else
        {
            PendingIntent pendingIntent = PendingIntent.getBroadcast(getBaseContext(), id, intent, 0);
            AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
            alarmManager.set(AlarmManager.RTC_WAKEUP, targetCal.getTimeInMillis(), pendingIntent);
        }
    }





    //creates pending intent for end time
    private void endeve(Calendar targetCal,String rep,String rep_until,String cur_dayofweek_for_cus_monthly_rep)
    {

        Intent intent = new Intent(getBaseContext(), deactivate_event.class);


        DBAdapter db= new DBAdapter(SetToggles.this);
        db.open();
        Cursor c=db.getEventDetail1(unique_datetime_key);
        if(c.moveToFirst())
        {
            id=c.getInt(c.getColumnIndex("_id"));
        }
        intent.putExtra("id",id);


        //if new event is repeated
        intent.putExtra("rep",rep);
        intent.putExtra("rep_until",rep_until);
        intent.putExtra("cur_dayofweek_for_cus_monthly_rep",cur_dayofweek_for_cus_monthly_rep);

        if((rep.charAt(0)=='5')&&(rep.charAt(2)=='1'))
        {
            if (rep.charAt(rep.length()-7)=='1')
            {


                targetCal.add(Calendar.DAY_OF_MONTH, added_in_mon_date_in_pending_function);



                PendingIntent pendingIntent = PendingIntent.getBroadcast(getBaseContext(),id , intent, 0);
                AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
                alarmManager.set(AlarmManager.RTC_WAKEUP, targetCal.getTimeInMillis(), pendingIntent);

                added_in_mon_date_in_pending_function=(-1)*added_in_mon_date_in_pending_function;
                        targetCal.add(Calendar.DAY_OF_MONTH,added_in_mon_date_in_pending_function);
            }

            if (rep.charAt(rep.length()-6)=='1')
            {


                targetCal.add(Calendar.DAY_OF_MONTH,added_in_tue_date_in_pending_function);






                PendingIntent pendingIntent = PendingIntent.getBroadcast(getBaseContext(), id, intent, 0);
                AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
                alarmManager.set(AlarmManager.RTC_WAKEUP, targetCal.getTimeInMillis(), pendingIntent);

                added_in_tue_date_in_pending_function=(-1)*added_in_tue_date_in_pending_function;
                targetCal.add(Calendar.DAY_OF_MONTH, added_in_tue_date_in_pending_function);

            }

            if (rep.charAt(rep.length()-5)=='1')
            {

                targetCal.add(Calendar.DAY_OF_MONTH,added_in_wed_date_in_pending_function);




                PendingIntent pendingIntent = PendingIntent.getBroadcast(getBaseContext(), id, intent, 0);
                AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
                alarmManager.set(AlarmManager.RTC_WAKEUP, targetCal.getTimeInMillis(), pendingIntent);

                added_in_wed_date_in_pending_function=(-1)*added_in_wed_date_in_pending_function;
                targetCal.add(Calendar.DAY_OF_MONTH, added_in_wed_date_in_pending_function);

            }


            if (rep.charAt(rep.length()-4)=='1') {



                targetCal.add(Calendar.DAY_OF_MONTH,added_in_thu_date_in_pending_function);




                PendingIntent pendingIntent = PendingIntent.getBroadcast(getBaseContext(), id, intent, 0);
                AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
                alarmManager.set(AlarmManager.RTC_WAKEUP, targetCal.getTimeInMillis(), pendingIntent);

                added_in_thu_date_in_pending_function=(-1)*added_in_thu_date_in_pending_function;
                targetCal.add(Calendar.DAY_OF_MONTH, added_in_thu_date_in_pending_function);

            }


            if (rep.charAt(rep.length()-3)=='1') {



                targetCal.add(Calendar.DAY_OF_MONTH,added_in_fri_date_in_pending_function);



                PendingIntent pendingIntent = PendingIntent.getBroadcast(getBaseContext(), id, intent, 0);
                AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
                alarmManager.set(AlarmManager.RTC_WAKEUP, targetCal.getTimeInMillis(), pendingIntent);

                added_in_fri_date_in_pending_function=(-1)*added_in_fri_date_in_pending_function;
                targetCal.add(Calendar.DAY_OF_MONTH, added_in_fri_date_in_pending_function);

            }


            if (rep.charAt(rep.length()-2)=='1')
            {

                targetCal.add(Calendar.DAY_OF_MONTH,added_in_sat_date_in_pending_function);




                PendingIntent pendingIntent = PendingIntent.getBroadcast(getBaseContext(), id, intent, 0);
                AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
                alarmManager.set(AlarmManager.RTC_WAKEUP, targetCal.getTimeInMillis(), pendingIntent);


                added_in_sat_date_in_pending_function=(-1)*added_in_sat_date_in_pending_function;
                targetCal.add(Calendar.DAY_OF_MONTH, added_in_sat_date_in_pending_function);
            }


            if (rep.charAt(rep.length()-1)=='1') {


                targetCal.add(Calendar.DAY_OF_MONTH,added_in_sun_date_in_pending_function);




                PendingIntent pendingIntent = PendingIntent.getBroadcast(getBaseContext(), id, intent, 0);
                AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
                alarmManager.set(AlarmManager.RTC_WAKEUP, targetCal.getTimeInMillis(), pendingIntent);

                added_in_sun_date_in_pending_function=(-1)*added_in_sun_date_in_pending_function;
                targetCal.add(Calendar.DAY_OF_MONTH, added_in_sun_date_in_pending_function);

            }
        }
        else //if event doesn't repeat, simply create pending intent for end
        {
            PendingIntent pendingIntent = PendingIntent.getBroadcast(getBaseContext(), id, intent, 0);
            AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
            alarmManager.set(AlarmManager.RTC_WAKEUP, targetCal.getTimeInMillis(), pendingIntent);
        }
    }



}



