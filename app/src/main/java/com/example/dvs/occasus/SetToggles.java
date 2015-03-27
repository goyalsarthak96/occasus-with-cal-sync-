package com.example.dvs.occasus;


import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.PendingIntent;

import android.content.Context;
import android.content.DialogInterface;

import android.database.Cursor;
import android.media.AudioManager;

import android.os.PowerManager;

import android.widget.Toast;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ToggleButton;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;


public class SetToggles extends ActionBarActivity {

    String bluetooth="no",wifi1="no",mobile_data="no";
    int l;
    String profile="ring";
    int check;
    CharSequence[] items = { " Silent", "Ring", "Vibrate"};

    AudioManager am;

    int flag;
    Cursor c;
    String profile_status;
    int time_iden1=0;
    int time_iden2=0;

    int overlap=0;

    int ret_id;
    String stime;
    String date;
    String name;

    Integer day;
    Integer month;
    Integer year;
    Integer shour;
    Integer ehour;
    Integer sminute;
    Integer eminute;
    Integer add;
    Integer[] ret_days={0,0,0,0,0,0,0};
    Integer[] days={0,0,0,0,0,0,0};
    String[] day_array={"Monday","Tuesday","Wednesday","Thursday","Friday","Saturday","Sunday"};
    int database_rep;





    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_toggles);
        Intent intent = getIntent();
        //initializing audio manager
        am = (AudioManager)getSystemService(Context.AUDIO_SERVICE);

        //overlap=0 means that right now the event that is getting created is not clashing with any other existing event and is a valid event in terms of name and all

        overlap=0;

        //flag received from create event
        //flag=0 means that new event is getting created
        flag=intent.getIntExtra("flag",0);


        //flag=1 means that existing event is being edited
        if(flag==1) {
            //req_name contains the name of the event that is to be eidted
            //it is passed from main activity to create event and then from create event to setotggles
            String req_name= intent.getStringExtra("req_name");

            DBAdapter db1 = new DBAdapter(SetToggles.this);
            db1.open();

            //c contains the detials of the event to be edited
            c = db1.getEventsDetail(req_name);

            c.moveToFirst();

            String status;
            ToggleButton b;


            //set all the toggles buttons to the states that were set while creating the event earlier

            b = (ToggleButton) findViewById(R.id.bluetooth_button);
            //to check what state of bluetooth was set while creating the event earlier
            status= c.getString(c.getColumnIndex("bluetooth"));
            bluetooth=status;
            //status equals yes means bluetooth was supposed to be on so toggle button is turned on by b.setchecked
            if(status.equals("yes"))
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
            status= c.getString(c.getColumnIndex("wifi"));
            wifi1= status;
            //status equals yes means wifi was supposed to be on so toggle button is turned on by b.setchecked
            if(status.equals("yes"))
            {
                b.setChecked(true);
            }
            //status equals no means wifi was supposed to be off so toggle button is turned off by b.setchecked
            else
            {
                b.setChecked(false);
            }
            b = (ToggleButton) findViewById(R.id.mobiledata_button);
            status= c.getString(c.getColumnIndex("mobile_data"));
            mobile_data= status;
            if(status.equals("yes"))
            {
                b.setChecked(true);
            }
            else
            {
                b.setChecked(false);
            }

            profile_status= c.getString(c.getColumnIndex("profile"));
            //profile and profile_status contains the radiobutton that was checked in profile daialog box when event was created earlier
            profile= profile_status;

            //the previous version of the event is deleted
            //the edited version is added to database later
            db1.deleteEvent(req_name);

            db1.close();
        }

    }





        //on click listener for bluetooth toggle switch

    public void bluetooth_settings(View view) {



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
    public void silent_settings(View view) {

       //shows the dialog box containing profile options
        showDialog(0);

    }


    //called by showdialog automatically
        @Override
        protected Dialog onCreateDialog(int id) {



            check=-1;


            //if existing event is getting edited then what was the radiobutton which was checked previous time in profile dailog box
            if(flag==1)
            {
                if(profile_status.equals("silent"))
                    check=0;
                else if(profile_status.equals("ring"))
                    check=1;
                else
                    check=2;

            }



            switch (id) {
                case 0:
                    return new AlertDialog.Builder(this)
                            .setIcon(R.drawable.ic_launcher)
                            .setTitle("This is a dialog with some simple text")

                            .setNegativeButton("Cancel",
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int whichButton) {

                                        }
                                    }
                            )


                                .setSingleChoiceItems(items, check,
                                        new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                //stores which radiobutton was clicked in a variable l
                                                setvalue(which);


                                            }


                                        }
                                )

                            .setPositiveButton("OK",
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int whichButton) {

                                            //if ok is clicked then if l=0 then silent radiobutton was clicked
                                            //if l=1 then ring radiobutton was clicked
                                            //if l=2 then vibrate radiobutton was clicked

                                            if (l == 0) {

                                                profile = "silent";
                                                check=0;


                                            } else if (l == 1) {

                                                profile = "ring";
                                                //may be there is no need to set check to some value
                                                check=1;

                                            } else if (l == 2) {


                                                profile = "vibrate";

                                               check=2;
                                            }


                                        }
                                    }
                            ).create();
            }
            return null;

    }

    //sets l= the radiobutton which was clicked
void setvalue(int y)
{
    l=y;
}



    //onclick listener for shutdown toggle switch
    public void shutdown_settings(View view){

        PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
        PowerManager.WakeLock wl = pm.newWakeLock(PowerManager.SCREEN_DIM_WAKE_LOCK, "My Tag");
        wl.acquire();
        wl.release();

    }

    //onclick listener for wifi toggle switch
    public void wifi_settings(View view){


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
        overlap=0;//overlap=0 means event is valid

        //name description date(string) start time(string) end time(sting) add(int) retrieved from create event class
        //these values are inserted into database later in save_event method
         name = getIntent().getStringExtra("Name");
        String desc = getIntent().getStringExtra("Description");
         date = getIntent().getStringExtra("Date");
         stime = getIntent().getStringExtra("STime");
        String etime = getIntent().getStringExtra("ETime");


        add=getIntent().getIntExtra("add",1);
        //add=1 means that event repeats


        //day(int) month(int)year(int) shour(int) ehour(int) sminute(int) eminute(int) retrieved from create_event class
         day= getIntent().getIntExtra("int_day",1);
         month= getIntent().getIntExtra("int_month",1);
         year= getIntent().getIntExtra("int_year",1);
         shour= getIntent().getIntExtra("int_shour",1);
         ehour= getIntent().getIntExtra("int_ehour",1);
         sminute= getIntent().getIntExtra("int_sminute",1);
         eminute= getIntent().getIntExtra("int_eminute",1);


        //days[i]=1 if ith day was checked in repeat dialog box 0n previous screen
            days[0] = getIntent().getIntExtra("int_mon", 1);
            days[1] = getIntent().getIntExtra("int_tue", 1);
            days[2] = getIntent().getIntExtra("int_wed", 1);
            days[3] = getIntent().getIntExtra("int_thu", 1);
           days[4] = getIntent().getIntExtra("int_fri", 1);
            days[5] = getIntent().getIntExtra("int_sat", 1);
            days[6] = getIntent().getIntExtra("int_sun", 1);



        DBAdapter db = new DBAdapter(this);
        //initizing ret_name just for assurance purpose
        String ret_name="-1";
        db.open();




        int yr = year;
        int month1 = month;
        int day1 = day - 1;


        //way of formatting date doesn't create any problem

        Date date1 = new Date(yr, month1, day1, 0, 0);
        //date1 contains the date at which the event is supposed to start

        SimpleDateFormat outFormat = new SimpleDateFormat("EEEE");
        //for finding day at date1
         String goal = outFormat.format(date1);





       // Toast.makeText(getBaseContext(), goal, Toast.LENGTH_SHORT).show();


        int y;
        //checks if starttime is greater than end time of the event we are creating
        y=time_identifier1(stime,etime);

        //retrieves all the existing events details from the database
        Cursor c1 = db.getAllEventsDetails();

            if (c1.moveToFirst()) {
                do {
                    //ret_endtime contains the endtime of a event already present in database
                    String ret_endtime = c1.getString(c1.getColumnIndex("end_time"));
                    String ret_starttime = c1.getString(c1.getColumnIndex("start_time"));
                    String ret_date = c1.getString(c1.getColumnIndex("event_date"));
                    ret_name = c1.getString(c1.getColumnIndex("event_name"));


                    //ret_days[i]=1 means the event in database repeats on ith day
                    ret_days[0] = c1.getInt(c1.getColumnIndex("monday"));
                    ret_days[1] = c1.getInt(c1.getColumnIndex("tuesday"));
                    ret_days[2] = c1.getInt(c1.getColumnIndex("wednesday"));
                    ret_days[3] = c1.getInt(c1.getColumnIndex("thursday"));
                    ret_days[4] = c1.getInt(c1.getColumnIndex("friday"));
                    ret_days[5] = c1.getInt(c1.getColumnIndex("saturday"));
                    ret_days[6] = c1.getInt(c1.getColumnIndex("sunday"));

                    database_rep=0;
                    for(int i=0;i<7;i++)
                    {
                        if(ret_days[i]==1)
                        {
                            database_rep=1;
                            break;
                        }
                    }

                    int database_Date=(ret_date.charAt(0)-48)*10+ret_date.charAt(1)-48;
                    int database_Month=(ret_date.charAt(3)-48)*10+ret_date.charAt(4)-48;
                    int database_Year=(ret_date.charAt(6)-48)*1000+(ret_date.charAt(7)-48)*100+(ret_date.charAt(8)-48)*10+(ret_date.charAt(9)-48);

                       database_Date--;
                        database_Month--;


                    //Toast.makeText(getBaseContext(), "add=" + add, Toast.LENGTH_SHORT).show();

                    //if the name of the event we are creating clashes with name of existing event
                    if (ret_name.equals(name)) {
                        overlap = 2;
                        break;
                    }
                    //if start time of the event we are creating is more than the end time of the event
                    else if (y == 2) {
                        overlap = 3;

                        break;
                    }
                    //for new events with repetition
                    if (add == 1) {
                        //if retrieved event repeats
                        if(database_rep==1) {
                            for (int i = 0; i <= 6; i++) {
                                //if on any day both existing event and event to be created occur
                                if ((days[i] == 1) && (ret_days[i] == 1)) {


                                    time_iden1 = time_identifier1(stime, ret_endtime);
                                    time_iden2 = time_identifier1(ret_starttime, etime);
                                    //if(both clash acc to time)
                                    if ((time_iden1 == 1) && (time_iden2 == 1)) {
                                        overlap = 1;
                                       // Toast.makeText(getBaseContext(), "overlap at day=" + Integer.toString(i + 1), Toast.LENGTH_SHORT).show();
                                       // Toast.makeText(getBaseContext(), "ret_days=" + Integer.toString(ret_days[i]), Toast.LENGTH_SHORT).show();
                                       // Toast.makeText(getBaseContext(), "days=" + Integer.toString(days[i]), Toast.LENGTH_SHORT).show();
                                        break;
                                    }


                                }


                            }
                            if(overlap==1)
                                break;
                        }
                        //if retrieved event does not repeat
                        else {
                            Date date2 = new Date(database_Year, database_Month, database_Date, 0, 0);
                            //date2 contains the date at which the event in database is supposed to start

                            SimpleDateFormat outFormat1 = new SimpleDateFormat("EEEE");
                            //for finding day at date2
                            String goal1 = outFormat1.format(date2);
                            //Toast.makeText(getBaseContext(),"day of existing event="+goal1,Toast.LENGTH_SHORT).show();


                            for(int i=0;i<=6;i++)
                            {
                                if((goal1.equals(day_array[i]))&&(days[i]==1))
                                {

                                    int y1=date_identifier1(ret_date,date);
                                    if(y1==2)
                                    {
                                        time_iden1 = time_identifier1(stime, ret_endtime);
                                        time_iden2 = time_identifier1(ret_starttime, etime);
                                        //both occur on same time
                                        if ((time_iden1 == 1) && (time_iden2 == 1)) {
                                            overlap = 1;
                                            break;
                                        }
                                    }
                                }
                            }
                            if(overlap==1)
                                break;

                        }
                    }
                    //for new events without repeat
                    else
                    {
                        //if retrieved event repeats
                        if(database_rep==1)
                        {
                            for(int i=0;i<=6;i++)
                            {
                             //goal contains the day of the event to be created
                             //if goal equals ith day and at that day existing event is also repeated
                             if((goal.equals(day_array[i]))&&(ret_days[i]==1))
                             {
                                    int date_clash=date_identifier1(date,ret_date);
                                 //if event to be created occurs on a date after the starting date of an existing event
                                 if(date_clash==2)
                                 {
                                     time_iden1 = time_identifier1(stime, ret_endtime);
                                     time_iden2 = time_identifier1(ret_starttime, etime);
                                     //both occur on same time
                                        if ((time_iden1 == 1) && (time_iden2 == 1)) {
                                         overlap = 1;
                                         break;
                                     }
                                 }

                                }
                            }
                            if(overlap==1)
                                break;
                        }
                        //if retrieved event does not repeat
                        else
                        {
                            if(date.equals(ret_date))
                            {
                                time_iden1 = time_identifier1(stime, ret_endtime);
                                time_iden2 = time_identifier1(ret_starttime, etime);
                                //both occur on same time
                                if ((time_iden1 == 1) && (time_iden2 == 1)) {
                                    overlap = 1;
                                    break;
                                }
                            }
                        }


                    }

                    //ret_days inialized just for assurance purpose
                   /* for(int i=0;i<=6;i++)
                    {
                        ret_days[i]=0;
                    }*/

                } while (c1.moveToNext());


            }
            else
            {
                //if start time of the event we are creating is more than the end time of the event
                 if (y == 2) {
                overlap = 3;


            }
            }


            //overlap=1 time clashing
            if (overlap == 1) {
                Toast.makeText(getBaseContext(), "event clashing with existing event '" + ret_name +"'", Toast.LENGTH_SHORT).show();
            }
            //overlap=2 name already exists
            else if (overlap == 2) {
                Toast.makeText(getBaseContext(), "event name already exists", Toast.LENGTH_SHORT).show();
            }
            //overlap=3 means starttime>end time of the event to be created
            else if (overlap == 3) {
                Toast.makeText(getBaseContext(), "start time is greater than end time", Toast.LENGTH_SHORT).show();
            }


            //no conflict .....event can be created
           else {
                if(flag==1)
                    Toast.makeText(getBaseContext(),"event successfully edited",Toast.LENGTH_SHORT).show();
                else
            Toast.makeText(getBaseContext(),"event successfully created",Toast.LENGTH_SHORT).show();
               // Toast.makeText(getBaseContext(),"days="+Integer.toString(days[0]),Toast.LENGTH_SHORT).show();
               // Toast.makeText(getBaseContext(),"ret_days="+Integer.toString(ret_days[0]),Toast.LENGTH_SHORT).show();
            //event details inserted into database
                db.insertevent(name, desc, date, stime, etime, bluetooth, wifi1, profile, mobile_data,days[0],days[1],days[2],days[3],days[4],days[5],days[6]);


            Calendar calNow = Calendar.getInstance();
            Calendar calSet = (Calendar) calNow.clone();

            //calset contains the calender instance of the time when event should start
            calSet.set(Calendar.YEAR, year);
            calSet.set(Calendar.MONTH, month);
            calSet.set(Calendar.DAY_OF_MONTH, day);
            calSet.set(Calendar.HOUR_OF_DAY, shour);
            calSet.set(Calendar.MINUTE, sminute);
            calSet.set(Calendar.SECOND, 0);
            calSet.set(Calendar.MILLISECOND, 0);

            Calendar calSet1 = (Calendar) calNow.clone();

                //calset1 contains the calender instance of the time when event should end
            calSet1.set(Calendar.YEAR, year);
            calSet1.set(Calendar.MONTH, month);
            calSet1.set(Calendar.DAY_OF_MONTH, day);
            calSet1.set(Calendar.HOUR_OF_DAY, ehour);
            calSet1.set(Calendar.MINUTE, eminute);
            calSet1.set(Calendar.SECOND, 0);
            calSet1.set(Calendar.MILLISECOND, 0);




              // if(add==0) {
                   //if the event start time has not yet passed

                   //if current year is smaller than start time year
                   if (calNow.get(Calendar.YEAR) < calSet.get(Calendar.YEAR)) {


                       setAlarm(calSet, add);

                       endeve(calSet1, add);
                   }
                   //if current year is same as start time year
                   else if (calNow.get(Calendar.YEAR) == calSet.get(Calendar.YEAR)) {
                       if (calNow.get(Calendar.MONTH) < calSet.get(Calendar.MONTH)) {
                           //setAlarm creates pending intent start of event
                           setAlarm(calSet, add);

                           //endeve creates pending intent for end of event
                           endeve(calSet1, add);
                       } else if (calNow.get(Calendar.MONTH) == calSet.get(Calendar.MONTH)) {
                           if (calNow.get(Calendar.DAY_OF_MONTH) < calSet.get(Calendar.DAY_OF_MONTH)) {
                               setAlarm(calSet, add);

                               endeve(calSet1, add);
                           } else if (calNow.get(Calendar.DAY_OF_MONTH) == calSet.get(Calendar.DAY_OF_MONTH)) {
                               if (calNow.get(Calendar.HOUR_OF_DAY) < calSet.get(Calendar.HOUR_OF_DAY)) {
                                   setAlarm(calSet, add);

                                   endeve(calSet1, add);
                               } else if (calNow.get(Calendar.HOUR_OF_DAY) == calSet.get(Calendar.HOUR_OF_DAY)) {
                                   if (calNow.get(Calendar.MINUTE) < calSet.get(Calendar.MINUTE)) {
                                       setAlarm(calSet, add);

                                       endeve(calSet1, add);
                                       //   Toast.makeText(getBaseContext(),"working",Toast.LENGTH_SHORT).show();
                                       //    Toast.makeText(getBaseContext(),"calset1 hour="+calSet1.get(Calendar.HOUR_OF_DAY),Toast.LENGTH_SHORT).show();
                                       //   Toast.makeText(getBaseContext(),"calset1 minute="+calSet1.get(Calendar.MINUTE),Toast.LENGTH_SHORT).show();


                                   } else if (calNow.get(Calendar.MINUTE) == calSet.get(Calendar.MINUTE)) {
                                       setAlarm(calSet, add);

                                       endeve(calSet1, add);
                                   }
                               }
                           }
                       }
                   }

              // }
               //if event repeats
              /*  else
               {
                   setAlarm(calSet,add);
                   endeve(calSet1,add);
               }*/




            db.close();
                //screen restored to mainscreen
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);


        }


    }





     //returns 1 if time1<time2
    //returns 2 if time1>time2
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
        if(asci1<asci2)
            return 1;
        else
            return 2;



    }



//returns 1 if date1<date2
    //returns2 if date1>date2
    public int date_identifier1(String date1,String date2) {

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

        if(asci1<asci2)
            return 1;
        else
            return 2;

    }


    //creates pending intent for start time
    private void setAlarm(Calendar targetCal,Integer rep){

        DBAdapter db1= new DBAdapter(this);
        Cursor c;
        //details of the event retrieved from database
        c=db1.getEventsDetail(name);

        c.moveToFirst();
        //id retrieved from database
        ret_id=c.getInt(c.getColumnIndex("_id"));
        Intent intent = new Intent(getBaseContext(), Activate_event.class);
        //name sent to activate_event class....there it is used to retrieve all the details of the event
        intent.putExtra("event_name",name);
        intent.putExtra("event_id",ret_id);
        //ret_id used as a unique key


        //if new event is repeated
        if(rep==1) {
            intent.putExtra("rep",rep);
            if (days[0] == 1) {

                GregorianCalendar date = new GregorianCalendar(year, month, day);

                while (date.get(Calendar.DAY_OF_WEEK) != Calendar.MONDAY)
                    date.add(Calendar.DATE, 1);

                Calendar pending_calSet = (Calendar) targetCal.clone();

                pending_calSet.set(Calendar.YEAR, date.get(Calendar.YEAR));
                pending_calSet.set(Calendar.MONTH, date.get(Calendar.MONTH));
                pending_calSet.set(Calendar.DAY_OF_MONTH, date.get(Calendar.DAY_OF_MONTH));



                PendingIntent pendingIntent = PendingIntent.getBroadcast(getBaseContext(), ret_id, intent, 0);
                AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
                alarmManager.set(AlarmManager.RTC_WAKEUP, pending_calSet.getTimeInMillis(), pendingIntent);

                // Toast.makeText(getBaseContext(),"latest monday="+Integer.toString(date.get(Calendar.DAY_OF_MONTH))+"-"+Integer.toString(date.get(Calendar.MONTH)+1)+"-"+Integer.toString(date.get(Calendar.YEAR)),Toast.LENGTH_SHORT).show();
            }


            if (days[1] == 1) {

                GregorianCalendar date = new GregorianCalendar(year, month, day);

                while (date.get(Calendar.DAY_OF_WEEK) != Calendar.TUESDAY)
                    date.add(Calendar.DATE, 1);


                Calendar pending_calSet = (Calendar) targetCal.clone();

                pending_calSet.set(Calendar.YEAR, date.get(Calendar.YEAR));
                pending_calSet.set(Calendar.MONTH, date.get(Calendar.MONTH));
                pending_calSet.set(Calendar.DAY_OF_MONTH, date.get(Calendar.DAY_OF_MONTH));

                PendingIntent pendingIntent = PendingIntent.getBroadcast(getBaseContext(), ret_id, intent, 0);
                AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
                alarmManager.set(AlarmManager.RTC_WAKEUP, pending_calSet.getTimeInMillis(), pendingIntent);

                // Toast.makeText(getBaseContext(),"latest monday="+Integer.toString(date.get(Calendar.DAY_OF_MONTH))+"-"+Integer.toString(date.get(Calendar.MONTH)+1)+"-"+Integer.toString(date.get(Calendar.YEAR)),Toast.LENGTH_SHORT).show();
            }


            if (days[2] == 1) {

                GregorianCalendar date = new GregorianCalendar(year, month, day);

                while (date.get(Calendar.DAY_OF_WEEK) != Calendar.WEDNESDAY)
                    date.add(Calendar.DATE, 1);


                Calendar pending_calSet = (Calendar) targetCal.clone();

                pending_calSet.set(Calendar.YEAR, date.get(Calendar.YEAR));
                pending_calSet.set(Calendar.MONTH, date.get(Calendar.MONTH));
                pending_calSet.set(Calendar.DAY_OF_MONTH, date.get(Calendar.DAY_OF_MONTH));

                PendingIntent pendingIntent = PendingIntent.getBroadcast(getBaseContext(), ret_id, intent, 0);
                AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
                alarmManager.set(AlarmManager.RTC_WAKEUP, pending_calSet.getTimeInMillis(), pendingIntent);

                // Toast.makeText(getBaseContext(),"latest monday="+Integer.toString(date.get(Calendar.DAY_OF_MONTH))+"-"+Integer.toString(date.get(Calendar.MONTH)+1)+"-"+Integer.toString(date.get(Calendar.YEAR)),Toast.LENGTH_SHORT).show();
            }


            if (days[3] == 1) {

                GregorianCalendar date = new GregorianCalendar(year, month, day);

                while (date.get(Calendar.DAY_OF_WEEK) != Calendar.THURSDAY)
                    date.add(Calendar.DATE, 1);


                Calendar pending_calSet = (Calendar) targetCal.clone();

                pending_calSet.set(Calendar.YEAR, date.get(Calendar.YEAR));
                pending_calSet.set(Calendar.MONTH, date.get(Calendar.MONTH));
                pending_calSet.set(Calendar.DAY_OF_MONTH, date.get(Calendar.DAY_OF_MONTH));

                PendingIntent pendingIntent = PendingIntent.getBroadcast(getBaseContext(), ret_id, intent, 0);
                AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
                alarmManager.set(AlarmManager.RTC_WAKEUP, pending_calSet.getTimeInMillis(), pendingIntent);

                // Toast.makeText(getBaseContext(),"latest monday="+Integer.toString(date.get(Calendar.DAY_OF_MONTH))+"-"+Integer.toString(date.get(Calendar.MONTH)+1)+"-"+Integer.toString(date.get(Calendar.YEAR)),Toast.LENGTH_SHORT).show();
            }


            if (days[4] == 1) {

                GregorianCalendar date = new GregorianCalendar(year, month, day);

                while (date.get(Calendar.DAY_OF_WEEK) != Calendar.FRIDAY)
                    date.add(Calendar.DATE, 1);


                Calendar pending_calSet = (Calendar) targetCal.clone();

                pending_calSet.set(Calendar.YEAR, date.get(Calendar.YEAR));
                pending_calSet.set(Calendar.MONTH, date.get(Calendar.MONTH));
                pending_calSet.set(Calendar.DAY_OF_MONTH, date.get(Calendar.DAY_OF_MONTH));

                PendingIntent pendingIntent = PendingIntent.getBroadcast(getBaseContext(), ret_id, intent, 0);
                AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
                alarmManager.set(AlarmManager.RTC_WAKEUP, pending_calSet.getTimeInMillis(), pendingIntent);

                // Toast.makeText(getBaseContext(),"latest monday="+Integer.toString(date.get(Calendar.DAY_OF_MONTH))+"-"+Integer.toString(date.get(Calendar.MONTH)+1)+"-"+Integer.toString(date.get(Calendar.YEAR)),Toast.LENGTH_SHORT).show();
            }


            if (days[5] == 1) {

                GregorianCalendar date = new GregorianCalendar(year, month, day);

                while (date.get(Calendar.DAY_OF_WEEK) != Calendar.SATURDAY)
                    date.add(Calendar.DATE, 1);


                Calendar pending_calSet = (Calendar) targetCal.clone();

                pending_calSet.set(Calendar.YEAR, date.get(Calendar.YEAR));
                pending_calSet.set(Calendar.MONTH, date.get(Calendar.MONTH));
                pending_calSet.set(Calendar.DAY_OF_MONTH, date.get(Calendar.DAY_OF_MONTH));

                PendingIntent pendingIntent = PendingIntent.getBroadcast(getBaseContext(), ret_id, intent, 0);
                AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
                alarmManager.set(AlarmManager.RTC_WAKEUP, pending_calSet.getTimeInMillis(), pendingIntent);

                // Toast.makeText(getBaseContext(),"latest monday="+Integer.toString(date.get(Calendar.DAY_OF_MONTH))+"-"+Integer.toString(date.get(Calendar.MONTH)+1)+"-"+Integer.toString(date.get(Calendar.YEAR)),Toast.LENGTH_SHORT).show();
            }


            if (days[6] == 1) {

                GregorianCalendar date = new GregorianCalendar(year, month, day);

                while (date.get(Calendar.DAY_OF_WEEK) != Calendar.SUNDAY)
                    date.add(Calendar.DATE, 1);


                Calendar pending_calSet = (Calendar) targetCal.clone();

                pending_calSet.set(Calendar.YEAR, date.get(Calendar.YEAR));
                pending_calSet.set(Calendar.MONTH, date.get(Calendar.MONTH));
                pending_calSet.set(Calendar.DAY_OF_MONTH, date.get(Calendar.DAY_OF_MONTH));

                PendingIntent pendingIntent = PendingIntent.getBroadcast(getBaseContext(), ret_id, intent, 0);
                AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
                alarmManager.set(AlarmManager.RTC_WAKEUP, pending_calSet.getTimeInMillis(), pendingIntent);

                // Toast.makeText(getBaseContext(),"latest monday="+Integer.toString(date.get(Calendar.DAY_OF_MONTH))+"-"+Integer.toString(date.get(Calendar.MONTH)+1)+"-"+Integer.toString(date.get(Calendar.YEAR)),Toast.LENGTH_SHORT).show();
            }
        }

        //if new event is not getting repeated
        if(rep==0) {
            intent.putExtra("rep",0);
            PendingIntent pendingIntent = PendingIntent.getBroadcast(getBaseContext(), ret_id, intent, 0);
            AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
            alarmManager.set(AlarmManager.RTC_WAKEUP, targetCal.getTimeInMillis(), pendingIntent);
        }
    }





        //creates pending intent for end time
    private void endeve(Calendar targetCal,int rep){

        DBAdapter db1= new DBAdapter(this);
        Cursor c;
        c=db1.getEventsDetail(name);


        c.moveToFirst();
        ret_id=c.getInt(c.getColumnIndex("_id"));
        Intent intent = new Intent(getBaseContext(), deactivate_event.class);
        intent.putExtra("event_id",ret_id);
        intent.putExtra("name",name);
        //if new event is repeated
        if(rep==1) {
            intent.putExtra("rep",rep);
            if (days[0] == 1) {

                GregorianCalendar date = new GregorianCalendar(year, month, day);

                while (date.get(Calendar.DAY_OF_WEEK) != Calendar.MONDAY)
                    date.add(Calendar.DATE, 1);

                Calendar pending_calSet = (Calendar) targetCal.clone();

                pending_calSet.set(Calendar.YEAR, date.get(Calendar.YEAR));
                pending_calSet.set(Calendar.MONTH, date.get(Calendar.MONTH));
                pending_calSet.set(Calendar.DAY_OF_MONTH, date.get(Calendar.DAY_OF_MONTH));

                PendingIntent pendingIntent = PendingIntent.getBroadcast(getBaseContext(), ret_id, intent, 0);
                AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
                alarmManager.set(AlarmManager.RTC_WAKEUP, pending_calSet.getTimeInMillis(), pendingIntent);

                // Toast.makeText(getBaseContext(),"latest monday="+Integer.toString(date.get(Calendar.DAY_OF_MONTH))+"-"+Integer.toString(date.get(Calendar.MONTH)+1)+"-"+Integer.toString(date.get(Calendar.YEAR)),Toast.LENGTH_SHORT).show();
            }


            if (days[1] == 1) {

                GregorianCalendar date = new GregorianCalendar(year, month, day);

                while (date.get(Calendar.DAY_OF_WEEK) != Calendar.TUESDAY)
                    date.add(Calendar.DATE, 1);


                Calendar pending_calSet = (Calendar) targetCal.clone();

                pending_calSet.set(Calendar.YEAR, date.get(Calendar.YEAR));
                pending_calSet.set(Calendar.MONTH, date.get(Calendar.MONTH));
                pending_calSet.set(Calendar.DAY_OF_MONTH, date.get(Calendar.DAY_OF_MONTH));

                PendingIntent pendingIntent = PendingIntent.getBroadcast(getBaseContext(), ret_id, intent, 0);
                AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
                alarmManager.set(AlarmManager.RTC_WAKEUP, pending_calSet.getTimeInMillis(), pendingIntent);

                // Toast.makeText(getBaseContext(),"latest monday="+Integer.toString(date.get(Calendar.DAY_OF_MONTH))+"-"+Integer.toString(date.get(Calendar.MONTH)+1)+"-"+Integer.toString(date.get(Calendar.YEAR)),Toast.LENGTH_SHORT).show();
            }


            if (days[2] == 1) {

                GregorianCalendar date = new GregorianCalendar(year, month, day);

                while (date.get(Calendar.DAY_OF_WEEK) != Calendar.WEDNESDAY)
                    date.add(Calendar.DATE, 1);


                Calendar pending_calSet = (Calendar) targetCal.clone();

                pending_calSet.set(Calendar.YEAR, date.get(Calendar.YEAR));
                pending_calSet.set(Calendar.MONTH, date.get(Calendar.MONTH));
                pending_calSet.set(Calendar.DAY_OF_MONTH, date.get(Calendar.DAY_OF_MONTH));

                PendingIntent pendingIntent = PendingIntent.getBroadcast(getBaseContext(), ret_id, intent, 0);
                AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
                alarmManager.set(AlarmManager.RTC_WAKEUP, pending_calSet.getTimeInMillis(), pendingIntent);

                // Toast.makeText(getBaseContext(),"latest monday="+Integer.toString(date.get(Calendar.DAY_OF_MONTH))+"-"+Integer.toString(date.get(Calendar.MONTH)+1)+"-"+Integer.toString(date.get(Calendar.YEAR)),Toast.LENGTH_SHORT).show();
            }


            if (days[3] == 1) {

                GregorianCalendar date = new GregorianCalendar(year, month, day);

                while (date.get(Calendar.DAY_OF_WEEK) != Calendar.THURSDAY)
                    date.add(Calendar.DATE, 1);


                Calendar pending_calSet = (Calendar) targetCal.clone();

                pending_calSet.set(Calendar.YEAR, date.get(Calendar.YEAR));
                pending_calSet.set(Calendar.MONTH, date.get(Calendar.MONTH));
                pending_calSet.set(Calendar.DAY_OF_MONTH, date.get(Calendar.DAY_OF_MONTH));

                PendingIntent pendingIntent = PendingIntent.getBroadcast(getBaseContext(), ret_id, intent, 0);
                AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
                alarmManager.set(AlarmManager.RTC_WAKEUP, pending_calSet.getTimeInMillis(), pendingIntent);

                // Toast.makeText(getBaseContext(),"latest monday="+Integer.toString(date.get(Calendar.DAY_OF_MONTH))+"-"+Integer.toString(date.get(Calendar.MONTH)+1)+"-"+Integer.toString(date.get(Calendar.YEAR)),Toast.LENGTH_SHORT).show();
            }


            if (days[4] == 1) {

                GregorianCalendar date = new GregorianCalendar(year, month, day);

                while (date.get(Calendar.DAY_OF_WEEK) != Calendar.FRIDAY)
                    date.add(Calendar.DATE, 1);

               // Toast.makeText(getBaseContext(),"time in targetcal ="+Integer.toString(targetCal.get(Calendar.HOUR_OF_DAY))+"-"+Integer.toString(targetCal.get(Calendar.MINUTE)+1)+"-"+Integer.toString(targetCal.get(Calendar.SECOND)),Toast.LENGTH_SHORT).show();

                Calendar pending_calSet = (Calendar) targetCal.clone();
                //Toast.makeText(getBaseContext(),"time in pendingcal="+Integer.toString(pending_calSet.get(Calendar.HOUR_OF_DAY))+"-"+Integer.toString(pending_calSet.get(Calendar.MINUTE)+1)+"-"+Integer.toString(pending_calSet.get(Calendar.SECOND)),Toast.LENGTH_SHORT).show();


                pending_calSet.set(Calendar.YEAR, date.get(Calendar.YEAR));
                pending_calSet.set(Calendar.MONTH, date.get(Calendar.MONTH));
                pending_calSet.set(Calendar.DAY_OF_MONTH, date.get(Calendar.DAY_OF_MONTH));

                //Toast.makeText(getBaseContext(),"time="+Integer.toString(date.get(Calendar.HOUR_OF_DAY))+"-"+Integer.toString(date.get(Calendar.MINUTE)+1)+"-"+Integer.toString(date.get(Calendar.SECOND)),Toast.LENGTH_SHORT).show();

                PendingIntent pendingIntent = PendingIntent.getBroadcast(getBaseContext(), ret_id, intent, 0);
                AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
                alarmManager.set(AlarmManager.RTC_WAKEUP, pending_calSet.getTimeInMillis(), pendingIntent);


                //Toast.makeText(getBaseContext(),"time in pendingcal="+Integer.toString(pending_calSet.get(Calendar.HOUR_OF_DAY))+"-"+Integer.toString(pending_calSet.get(Calendar.MINUTE))+"-"+Integer.toString(pending_calSet.get(Calendar.SECOND)),Toast.LENGTH_SHORT).show();

                //Toast.makeText(getBaseContext(),"latest friday="+Integer.toString(date.get(Calendar.DAY_OF_MONTH))+"-"+Integer.toString(date.get(Calendar.MONTH)+1)+"-"+Integer.toString(date.get(Calendar.YEAR)),Toast.LENGTH_SHORT).show();
                //Toast.makeText(getBaseContext(),"month in date="+Integer.toString(date.get(Calendar.MONTH)),Toast.LENGTH_SHORT).show();
               // Toast.makeText(getBaseContext(),"date="+Integer.toString(pending_calSet.get(Calendar.DAY_OF_MONTH))+"-"+Integer.toString(pending_calSet.get(Calendar.MONTH)+1)+"-"+Integer.toString(pending_calSet.get(Calendar.YEAR)),Toast.LENGTH_SHORT).show();

            }


            if (days[5] == 1) {

                GregorianCalendar date = new GregorianCalendar(year, month, day);

                while (date.get(Calendar.DAY_OF_WEEK) != Calendar.SATURDAY)
                    date.add(Calendar.DATE, 1);


                Calendar pending_calSet = (Calendar) targetCal.clone();

                pending_calSet.set(Calendar.YEAR, date.get(Calendar.YEAR));
                pending_calSet.set(Calendar.MONTH, date.get(Calendar.MONTH));
                pending_calSet.set(Calendar.DAY_OF_MONTH, date.get(Calendar.DAY_OF_MONTH));

                PendingIntent pendingIntent = PendingIntent.getBroadcast(getBaseContext(), ret_id, intent, 0);
                AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
                alarmManager.set(AlarmManager.RTC_WAKEUP, pending_calSet.getTimeInMillis(), pendingIntent);

               // Toast.makeText(getBaseContext(),"time in pendingcal="+Integer.toString(pending_calSet.get(Calendar.HOUR_OF_DAY))+"-"+Integer.toString(pending_calSet.get(Calendar.MINUTE))+"-"+Integer.toString(pending_calSet.get(Calendar.SECOND)),Toast.LENGTH_SHORT).show();

//                Toast.makeText(getBaseContext(),"latest saturday="+Integer.toString(date.get(Calendar.DAY_OF_MONTH))+"-"+Integer.toString(date.get(Calendar.MONTH)+1)+"-"+Integer.toString(date.get(Calendar.YEAR)),Toast.LENGTH_SHORT).show();


                // Toast.makeText(getBaseContext(),"latest monday="+Integer.toString(date.get(Calendar.DAY_OF_MONTH))+"-"+Integer.toString(date.get(Calendar.MONTH)+1)+"-"+Integer.toString(date.get(Calendar.YEAR)),Toast.LENGTH_SHORT).show();
            }


            if (days[6] == 1) {

                GregorianCalendar date = new GregorianCalendar(year, month, day);

                while (date.get(Calendar.DAY_OF_WEEK) != Calendar.SUNDAY)
                    date.add(Calendar.DATE, 1);


                Calendar pending_calSet = (Calendar) targetCal.clone();

                pending_calSet.set(Calendar.YEAR, date.get(Calendar.YEAR));
                pending_calSet.set(Calendar.MONTH, date.get(Calendar.MONTH));
                pending_calSet.set(Calendar.DAY_OF_MONTH, date.get(Calendar.DAY_OF_MONTH));

                PendingIntent pendingIntent = PendingIntent.getBroadcast(getBaseContext(), ret_id, intent, 0);
                AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
                alarmManager.set(AlarmManager.RTC_WAKEUP, pending_calSet.getTimeInMillis(), pendingIntent);

                // Toast.makeText(getBaseContext(),"latest monday="+Integer.toString(date.get(Calendar.DAY_OF_MONTH))+"-"+Integer.toString(date.get(Calendar.MONTH)+1)+"-"+Integer.toString(date.get(Calendar.YEAR)),Toast.LENGTH_SHORT).show();
            }
        }
        if(rep==0) {
            PendingIntent pendingIntent = PendingIntent.getBroadcast(getBaseContext(), ret_id, intent, 0);
            AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
            alarmManager.set(AlarmManager.RTC_WAKEUP, targetCal.getTimeInMillis(), pendingIntent);
        }

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









    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_system_toggles, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}



