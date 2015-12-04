package com.example.dvs.occasus;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.content.Intent;
import android.view.View;
import android.app.Dialog;
import android.view.inputmethod.InputMethodManager;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;



public class CreateEvent extends ActionBarActivity {

    int shour, sminute,ehour,eminute;
    int start_yr, start_month, start_day,end_yr,end_month,end_day;


    int flag;
    int id_to_be_edited;
    CharSequence [] repeat_options1={"   Does not Repeat","   Every Day","   Every Week","   Every Month","   Every Year","   Custom..."};




    String event_name;
    String desc="";
    String start_strtime="00:00";
    String end_strtime="24:00";
    String eve_start_date="yo";
    String eve_end_date="yo";
    String final_repeat="";
    String repeat_until="";
    String bluetooth="no",wifi1="no",mobile_data="no";
    String profile_status;
    String cur_dayofweek_for_cus_monthly_rep="";




    EditText editText_name;
    EditText editText_desc;
    EditText editText_start_date;
    EditText editText_end_date;
    EditText editText_stime;
    EditText editText_etime;
    EditText editText_repeat;




    public static final String custom_info = "custom_repeat";
    SharedPreferences.Editor custom_editor;
    SharedPreferences custom_sharedpreferences;



    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_event);

        //to add logo to action bar
        ActionBar ac=getSupportActionBar();
        ac.setDisplayShowHomeEnabled(true);
        ac.setLogo(R.drawable.occasus1);
        ac.setDisplayUseLogoEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);//to hide back button on action bar



        Calendar today = Calendar.getInstance();//today contains current date and time when event is being created
        start_yr = today.get(Calendar.YEAR);  //yr initialized
        start_month = today.get(Calendar.MONTH);  //month initialized
        start_day = today.get(Calendar.DAY_OF_MONTH);  //date initialized
        shour=today.get(Calendar.HOUR_OF_DAY);
        sminute=today.get(Calendar.MINUTE);

        Calendar hour_later=Calendar.getInstance();
        hour_later.add(Calendar.HOUR_OF_DAY,1);
        end_yr = hour_later.get(Calendar.YEAR);
        end_month = hour_later.get(Calendar.MONTH);
        end_day = hour_later.get(Calendar.DAY_OF_MONTH);
        ehour=hour_later.get(Calendar.HOUR_OF_DAY);
        eminute=hour_later.get(Calendar.MINUTE);





        editText_name = (EditText) findViewById(R.id.eve_name);
        editText_desc = (EditText) findViewById(R.id.descrip);
        editText_start_date = (EditText) findViewById(R.id.start_date);
        editText_end_date = (EditText) findViewById(R.id.end_date);
        editText_stime = (EditText) findViewById(R.id.stime);
        editText_etime = (EditText) findViewById(R.id.etime);
        editText_repeat = (EditText) findViewById(R.id.repeat);



        custom_sharedpreferences = getBaseContext().getSharedPreferences(custom_info, Context.MODE_PRIVATE);
        custom_editor = custom_sharedpreferences.edit();

        int coming_from_custom_repeat=custom_sharedpreferences.getInt("coming_from_custom_repeat",0);
        if(coming_from_custom_repeat==0) {



            Intent intent = getIntent();
            flag = intent.getIntExtra("flag", 0);//flag passed from main activity class to create event class
            // flag=0 if new event is being created
            //flag=1 if event is being edited






            if (flag == 0)//if new event is being created
            {
                //start_time_set = 0;
                //end_time_set = 0;//end_time_set=0 => user hasn't entered any time....used in checking if  etime>stime
                //start_date_set = 0;
                //end_date_set = 0;
                repeat_until="0";
                final_repeat="0";
                cur_dayofweek_for_cus_monthly_rep="";
                editText_repeat.setText("Does not Repeat");



                SimpleDateFormat sdf1 = new SimpleDateFormat("dd/MM/yyyy");
                java.util.Date d1 = new java.util.Date(start_yr - 1900, start_month, start_day, 0, 0);
                eve_start_date=sdf1.format(d1);
                editText_start_date.setText(eve_start_date);
                java.util.Date d2 = new java.util.Date(end_yr - 1900, end_month, end_day, 0, 0);
                eve_end_date=sdf1.format(d2);
                editText_end_date.setText(eve_end_date);
                SimpleDateFormat sdf2 = new SimpleDateFormat("HH:mm");
                java.util.Date d3 = new java.util.Date(0, 0, 0, shour, sminute);
                start_strtime=sdf2.format(d3);
                editText_stime.setText(start_strtime);
                java.util.Date d4 = new java.util.Date(0, 0, 0, ehour, eminute);
                end_strtime=sdf2.format(d4);
                editText_etime.setText(end_strtime);
            }


            if (flag == 1)//if event is being edited
            {
                TextView textView = (TextView) findViewById(R.id.textView2);// textview -> reference object to title
                textView.setText("Edit Event");//title set to "edit event"

                //start_time_set = 1;
                //end_time_set = 1;//user entered some end time(when event was created)
                //start_date_set = 1;
                //end_date_set = 1;

                id_to_be_edited = intent.getIntExtra("clicked_id",0);
                //clicked_id is id of the profile to be edited
                //clicked_id is sent to createvent from mainactivity if user wants to edit the event


                DBAdapter db1 = new DBAdapter(CreateEvent.this);
                db1.open();//database open containing event details
                Cursor c;//to get details of all events with id=edit_id
                c = db1.getEventDetail(id_to_be_edited);
                c.moveToFirst();
                event_name = c.getString(c.getColumnIndex("event_name"));//event_name= previous name of event from database
                desc = c.getString(c.getColumnIndex("description"));//desc= previous description of event from database
                eve_start_date = c.getString(c.getColumnIndex("event_start_date"));//eve_date= previous date of event from database
                eve_end_date = c.getString(c.getColumnIndex("event_end_date"));
                start_strtime = c.getString(c.getColumnIndex("start_time"));//start_strtime= previous stime from database
                end_strtime = c.getString(c.getColumnIndex("end_time"));//end_strtime = previous etime from database
                bluetooth = c.getString(c.getColumnIndex("bluetooth"));//to check the previously "entered" bluetooth state
                wifi1 = c.getString(c.getColumnIndex("wifi"));//getting earlier wifi toggle button state
                mobile_data = c.getString(c.getColumnIndex("mobile_data"));//getting earlier mobile data toggle button state
                profile_status = c.getString(c.getColumnIndex("profile"));//getting profile selected earlier
                final_repeat = c.getString(c.getColumnIndex("repeat"));
                repeat_until=c.getString(c.getColumnIndex("repeat_until"));
                cur_dayofweek_for_cus_monthly_rep=c.getString(c.getColumnIndex("cur_dayofweek_for_cus_monthly_rep"));


                //to set the fields to the values entered earlier
                editText_name.setText(event_name);//eve_name1(textfield)= name of the event
                editText_desc.setText(desc);//descrip1(textfield)= description of the event
                editText_start_date.setText(eve_start_date);
                editText_end_date.setText(eve_end_date);
                editText_stime.setText(start_strtime);
                editText_etime.setText(end_strtime);
                if(final_repeat.charAt(0)=='0')
                {
                    editText_repeat.setText("Does not Repeat");
                }
                else if(final_repeat.charAt(0)=='1')
                {
                    editText_repeat.setText("Every Day");
                }
                else if(final_repeat.charAt(0)=='2')
                {
                    editText_repeat.setText("Every Week");
                }
                else if(final_repeat.charAt(0)=='3')
                {
                    editText_repeat.setText("Every Month");
                }
                else if(final_repeat.charAt(0)=='4')
                {
                    editText_repeat.setText("Every Year");
                }
                else
                {
                    String rep_edittext_string="";
                    switch (final_repeat.charAt(2))
                    {
                        case '0':if(final_repeat.substring(4).equals("1"))
                            rep_edittext_string="Repeats daily";
                        else
                            rep_edittext_string="Repeats every "+final_repeat.substring(4)+" days";

                            break;



                        case '1':if((final_repeat.charAt(4)=='1')&&(final_repeat.charAt(5)=='_'))
                            rep_edittext_string="Repeats weekly on ";
                        else
                            rep_edittext_string="Repeats every "+final_repeat.substring(4,final_repeat.length()-8)+" weeks on ";
                            int index=final_repeat.length()-7;
                            int comma=0;
                            if(final_repeat.charAt(index)=='1'){
                                rep_edittext_string=rep_edittext_string.concat("Mon");
                                comma=1;
                            }
                            index++;
                            if(final_repeat.charAt(index)=='1'){
                                if(comma==1)
                                {
                                    rep_edittext_string=rep_edittext_string.concat(",Tue");
                                }
                                else{
                                    rep_edittext_string=rep_edittext_string.concat("Tue");
                                    comma=1;
                                }
                            }
                            index++;
                            if(final_repeat.charAt(index)=='1'){
                                if(comma==1){
                                    rep_edittext_string=rep_edittext_string.concat(",Wed");
                                }
                                else{
                                    rep_edittext_string=rep_edittext_string.concat("Wed");
                                    comma=1;
                                }
                            }
                            index++;
                            if(final_repeat.charAt(index)=='1'){
                                if(comma==1){
                                    rep_edittext_string=rep_edittext_string.concat(",Thu");
                                }
                                else{
                                    rep_edittext_string=rep_edittext_string.concat("Thu");
                                    comma=1;
                                }
                            }
                            index++;
                            if(final_repeat.charAt(index)=='1'){
                                if(comma==1){
                                    rep_edittext_string=rep_edittext_string.concat(",Fri");
                                }
                                else{
                                    rep_edittext_string=rep_edittext_string.concat("Fri");
                                    comma=1;
                                }
                            }
                            index++;
                            if(final_repeat.charAt(index)=='1'){
                                if(comma==1){
                                    rep_edittext_string=rep_edittext_string.concat(",Sat");
                                }
                                else{
                                    rep_edittext_string=rep_edittext_string.concat("Sat");
                                    comma=1;
                                }
                            }
                            index++;
                            if(final_repeat.charAt(index)=='1'){
                                if(comma==1){
                                    rep_edittext_string=rep_edittext_string.concat(",Sun");
                                }
                                else{
                                    rep_edittext_string=rep_edittext_string.concat("Sun");
                                }
                            }

                            break;


                        case '2':if((final_repeat.charAt(4)=='1')&&(final_repeat.charAt(5)=='_'))
                            rep_edittext_string="Repeats monthly";
                        else
                            rep_edittext_string="Repeats every "+final_repeat.substring(4,final_repeat.length()-2)+" months ";

                            if(final_repeat.charAt(final_repeat.length()-1)=='2') {

                                rep_edittext_string = rep_edittext_string.concat(cur_dayofweek_for_cus_monthly_rep);
                            }
                            break;


                        case '3':if(final_repeat.substring(4).equals("1"))
                            rep_edittext_string="Repeats yearly";
                        else
                            rep_edittext_string="Repeats every "+final_repeat.substring(4)+" years";

                            break;

                    }


                    switch (repeat_until.charAt(0)) {
                        case '1':
                            rep_edittext_string = rep_edittext_string.concat(";until " + repeat_until.substring(2));
                            break;
                        case '2':
                            rep_edittext_string = rep_edittext_string.concat(";for " + repeat_until.substring(2) + " times");
                            break;
                    }
                    editText_repeat.setText(rep_edittext_string);

                }


                //in this way we are able to show all the previously entered information so that user doesn't have to rewrite everything


                shour = start_strtime.charAt(1) - '0' + (start_strtime.charAt(0) - '0') * 10;//shour = start hour in integer
                sminute = start_strtime.charAt(4) - '0' + (start_strtime.charAt(3) - '0') * 10;//sminute= start min in int
                ehour = end_strtime.charAt(1) - '0' + (end_strtime.charAt(0) - '0') * 10;//ehour= end hour in int
                eminute = end_strtime.charAt(4) - '0' + (end_strtime.charAt(3) - '0') * 10;//eminute= end min in int

                start_yr = (eve_start_date.charAt(6) - '0') * 1000 + (eve_start_date.charAt(7) - '0') * 100 + (eve_start_date.charAt(8) - '0') * 10 + (eve_start_date.charAt(9) - '0');
                //yr= year in int
                start_month = (eve_start_date.charAt(3) - '0') * 10 + (eve_start_date.charAt(4) - '0');//month= month in int
                start_month--;//month is 1 less by default in android
                start_day = (eve_start_date.charAt(0) - '0') * 10 + (eve_start_date.charAt(1) - '0');//day= date in int


                end_yr = (eve_end_date.charAt(6) - '0') * 1000 + (eve_end_date.charAt(7) - '0') * 100 + (eve_end_date.charAt(8) - '0') * 10 + (eve_end_date.charAt(9) - '0');
                //yr= year in int
                end_month = (eve_end_date.charAt(3) - '0') * 10 + (eve_end_date.charAt(4) - '0');//month= month in int
                end_month--;//month is 1 less by default in android
                end_day = (eve_end_date.charAt(0) - '0') * 10 + (eve_end_date.charAt(1) - '0');//day= date in int


                db1.close();//closing the database

            }
        }
        else
        {

            flag= custom_sharedpreferences.getInt("flag", 0);
            if(flag==1)
            {
                TextView textView = (TextView) findViewById(R.id.textView2);// textview -> reference object to title
                textView.setText("Edit Event");//title set to "edit event"
            }
            event_name=custom_sharedpreferences.getString("name", null);
            desc=custom_sharedpreferences.getString("description", null);
            eve_start_date=custom_sharedpreferences.getString("start_date", null);
            eve_end_date=custom_sharedpreferences.getString("end_date", null);
            start_strtime=custom_sharedpreferences.getString("start_time", null);
            end_strtime=custom_sharedpreferences.getString("end_time", null);
            //start_date_set=custom_sharedpreferences.getInt("start_date_set", 0);
            //end_date_set=custom_sharedpreferences.getInt("end_date_set", 0);
            //start_time_set=custom_sharedpreferences.getInt("start_time_set", 0);
            //end_time_set=custom_sharedpreferences.getInt("end_time_set", 0);
            id_to_be_edited=custom_sharedpreferences.getInt("id_to_be_edited", 0);
            final_repeat=custom_sharedpreferences.getString("custom_repeat",null);
            repeat_until=custom_sharedpreferences.getString("repeat_until",null);
            cur_dayofweek_for_cus_monthly_rep=custom_sharedpreferences.getString("cur_dayofweek_for_cus_monthly_rep",null);

            bluetooth=custom_sharedpreferences.getString("bluetooth",null);
            wifi1=custom_sharedpreferences.getString("wifi",null);
            mobile_data=custom_sharedpreferences.getString("mobile_data",null);
            profile_status=custom_sharedpreferences.getString("profile",null);




            //to set the fields to the values entered earlier
            editText_name.setText(event_name);//eve_name1(textfield)= name of the event
            editText_desc.setText(desc);//descrip1(textfield)= description of the event
            editText_start_date.setText(eve_start_date);
            editText_end_date.setText(eve_end_date);
            editText_stime.setText(start_strtime);
            editText_etime.setText(end_strtime);
            String rep_edittext_string="";
            switch (final_repeat.charAt(2))
            {
                case '0':if(final_repeat.substring(4).equals("1"))
                            rep_edittext_string="Repeats daily";
                        else
                            rep_edittext_string="Repeats every "+final_repeat.substring(4)+" days";

                    break;



                case '1':if(final_repeat.substring(4).equals("1"))
                            rep_edittext_string="Repeats weekly on ";
                        else
                            rep_edittext_string="Repeats every "+final_repeat.substring(4,final_repeat.length()-8)+" weeks on ";
                            int index=final_repeat.length()-7;
                            int comma=0;
                            if(final_repeat.charAt(index)=='1'){
                                rep_edittext_string=rep_edittext_string.concat("Mon");
                                comma=1;
                            }
                            index++;
                            if(final_repeat.charAt(index)=='1'){
                                if(comma==1)
                                {
                                    rep_edittext_string=rep_edittext_string.concat(",Tue");
                                }
                                else{
                                    rep_edittext_string=rep_edittext_string.concat("Tue");
                                    comma=1;
                                }
                            }
                            index++;
                            if(final_repeat.charAt(index)=='1'){
                                if(comma==1){
                                    rep_edittext_string=rep_edittext_string.concat(",Wed");
                                }
                                else{
                                    rep_edittext_string=rep_edittext_string.concat("Wed");
                                    comma=1;
                                }
                            }
                            index++;
                            if(final_repeat.charAt(index)=='1'){
                                if(comma==1){
                                    rep_edittext_string=rep_edittext_string.concat(",Thu");
                                }
                                else{
                                    rep_edittext_string=rep_edittext_string.concat("Thu");
                                    comma=1;
                                }
                            }
                            index++;
                            if(final_repeat.charAt(index)=='1'){
                                if(comma==1){
                                    rep_edittext_string=rep_edittext_string.concat(",Fri");
                                }
                                else{
                                    rep_edittext_string=rep_edittext_string.concat("Fri");
                                comma=1;
                                }
                            }
                            index++;
                            if(final_repeat.charAt(index)=='1'){
                                if(comma==1){
                                    rep_edittext_string=rep_edittext_string.concat(",Sat");
                                }
                                else{
                                    rep_edittext_string=rep_edittext_string.concat("Sat");
                                comma=1;
                                }
                            }
                            index++;
                            if(final_repeat.charAt(index)=='1'){
                                if(comma==1){
                                    rep_edittext_string=rep_edittext_string.concat(",Sun");
                                }
                                else{
                                    rep_edittext_string=rep_edittext_string.concat("Sun");
                                }
                            }

                    break;


                case '2':if(final_repeat.substring(4).equals("1"))
                            rep_edittext_string="Repeats monthly";
                        else
                            rep_edittext_string="Repeats every "+final_repeat.substring(4,final_repeat.length()-2)+" months ";

                    if(final_repeat.charAt(final_repeat.length()-1)=='2') {

                        rep_edittext_string = rep_edittext_string.concat(cur_dayofweek_for_cus_monthly_rep);
                    }
                    break;


                case '3':if(final_repeat.substring(4).equals("1"))
                            rep_edittext_string="Repeats yearly";
                        else
                            rep_edittext_string="Repeats every "+final_repeat.substring(4)+" years";

                    break;

            }


        switch (repeat_until.charAt(0)) {
            case '1':
                rep_edittext_string = rep_edittext_string.concat(";until " + repeat_until.substring(2));
                break;
            case '2':
                rep_edittext_string = rep_edittext_string.concat(";for " + repeat_until.substring(2) + " times");
                break;
        }
            editText_repeat.setText(rep_edittext_string);



            if(!start_strtime.equals("")) {
                shour = start_strtime.charAt(1) - '0' + (start_strtime.charAt(0) - '0') * 10;//shour = start hour in integer
                sminute = start_strtime.charAt(4) - '0' + (start_strtime.charAt(3) - '0') * 10;//sminute= start min in int
            }
            if(!end_strtime.equals("")) {
                ehour = end_strtime.charAt(1) - '0' + (end_strtime.charAt(0) - '0') * 10;//ehour= end hour in int
                eminute = end_strtime.charAt(4) - '0' + (end_strtime.charAt(3) - '0') * 10;//eminute= end min in int
            }
            if(!eve_start_date.equals("")) {
                start_yr = (eve_start_date.charAt(6) - '0') * 1000 + (eve_start_date.charAt(7) - '0') * 100 + (eve_start_date.charAt(8) - '0') * 10 + (eve_start_date.charAt(9) - '0');
                //yr= year in int
                start_month = (eve_start_date.charAt(3) - '0') * 10 + (eve_start_date.charAt(4) - '0');//month= month in int
                start_month--;//month is 1 less by default in android
                start_day = (eve_start_date.charAt(0) - '0') * 10 + (eve_start_date.charAt(1) - '0');//day= date in int
            }

            if(!eve_end_date.equals("")) {
                end_yr = (eve_end_date.charAt(6) - '0') * 1000 + (eve_end_date.charAt(7) - '0') * 100 + (eve_end_date.charAt(8) - '0') * 10 + (eve_end_date.charAt(9) - '0');
                //yr= year in int
                end_month = (eve_end_date.charAt(3) - '0') * 10 + (eve_end_date.charAt(4) - '0');//month= month in int
                end_month--;//month is 1 less by default in android
                end_day = (eve_end_date.charAt(0) - '0') * 10 + (eve_end_date.charAt(1) - '0');//day= date in int
            }


        }


    }




    //back button override......sends the app back to mainactivity screen
    @Override
    public void onBackPressed()
    {
        Intent intent = new Intent(CreateEvent.this,MainActivity.class);
        startActivity(intent);
    }







    //calls SetToggles activity
    //on click listener for set toggles button
    //sends the data of create event to set toggles
    public void set_toggles(View view)
    {

        Intent intent = new Intent(CreateEvent.this, SetToggles.class);//intent for settoggles activity is created



        event_name = editText_name.getText().toString();//event_name contains the name of event (which is getting created) or (edited name)
        desc = editText_desc.getText().toString();//desc contains the desc of event (which is to be created) or (edited desc)





        //name description date(in string) starttime(in string) endtime(in string) day(intrger) month(integer) year(integer) start hour(integer)
        //start minute(integer) end minute(integer) end hour(integer)
        intent.putExtra("Name", event_name);
        intent.putExtra("Description", desc);
        intent.putExtra("start_date", eve_start_date);
        intent.putExtra("end_date",eve_end_date);
        intent.putExtra("STime", start_strtime);//all details sent to settoggles
        intent.putExtra("ETime", end_strtime);
        intent.putExtra("int_start_day", start_day);
        intent.putExtra("int_end_day",end_day);
        intent.putExtra("int_start_month", start_month);
        intent.putExtra("int_end_month",end_month);
        intent.putExtra("int_start_year", start_yr);
        intent.putExtra("int_end_year",end_yr);
        intent.putExtra("int_shour", shour);
        intent.putExtra("int_ehour", ehour);
        intent.putExtra("int_sminute", sminute);
        intent.putExtra("int_eminute", eminute);
        intent.putExtra("custom_repeat",final_repeat);
        intent.putExtra("repeat_until",repeat_until);
        intent.putExtra("cur_dayofweek_for_cus_monthly_rep",cur_dayofweek_for_cus_monthly_rep);



        if (flag == 1)//if event was edited then send the other details for fields on toggles page too
        {
            intent.putExtra("id_to_be_edited",id_to_be_edited);
            intent.putExtra("bluetooth", bluetooth);
            intent.putExtra("wifi", wifi1);//these values have been obtained from database
            intent.putExtra("mobile_data", mobile_data);
            intent.putExtra("profile", profile_status);
        }



        //flag=1 means that edit event is to be performed
        //flag=0 means that new event is to be created

        intent.putExtra("flag", flag);





        if(event_name.equals("")) //if event name hasn't been entered
        {
            Toast.makeText(getBaseContext(), "Please enter the name of the event", Toast.LENGTH_SHORT).show();
        }
        else
        {

            Calendar calNow = Calendar.getInstance();
            Calendar calSet = (Calendar) calNow.clone();//calset contains the calender instance of the time when event should start
            calSet.set(Calendar.YEAR, start_yr);
            calSet.set(Calendar.MONTH, start_month);
            calSet.set(Calendar.DAY_OF_MONTH, start_day);
            calSet.set(Calendar.HOUR_OF_DAY, shour);
            calSet.set(Calendar.MINUTE, sminute);
            calSet.set(Calendar.SECOND, 0);
            calSet.set(Calendar.MILLISECOND, 0);

             //if the event start time has not yet passed
             SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
            Date cur_date=new Date(calNow.get(Calendar.YEAR)-1900,calNow.get(Calendar.MONTH),calNow.get(Calendar.DAY_OF_MONTH));
            String cur_date1=dateFormat.format(cur_date);


            SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm");
            Date cur_time = new Date(0,0,0, calNow.get(Calendar.HOUR_OF_DAY),calNow.get(Calendar.MINUTE));
            //start_time contains start time in string format
            String cur_time1 = timeFormat.format(cur_time);

            if(date_identifier1(eve_start_date, cur_date1)==2)
            {
                int check=checkoverlap();
                if(check==0)
                {
                    startActivity(intent);
                }
            }
            else if(date_identifier1(eve_start_date,cur_date1)==3)
            {
                if(time_identifier1(start_strtime,cur_time1)!=1)
                {
                    int check=checkoverlap();
                    if(check==0)
                    {
                        startActivity(intent);
                    }
                }
                else
                {
                    Toast.makeText(getBaseContext(), "Starting time has already passed", Toast.LENGTH_SHORT).show();
                }
            }
            else
            {
                Toast.makeText(getBaseContext(), "Starting time has already passed", Toast.LENGTH_SHORT).show();
            }



                           /* //if current year is smaller than start time year
                            if (calNow.get(Calendar.YEAR) < calSet.get(Calendar.YEAR))
                            {
                                int check=checkoverlap();
                                if(check==0) {
                                    startActivity(intent);
                                }
                            }
                            //if current year is same as start time year
                            else if (calNow.get(Calendar.YEAR) == calSet.get(Calendar.YEAR))
                            {
                                if (calNow.get(Calendar.MONTH) < calSet.get(Calendar.MONTH))
                                {
                                    int check=checkoverlap();
                                    if(check==0) {
                                        startActivity(intent);
                                    }
                                }
                                else if (calNow.get(Calendar.MONTH) == calSet.get(Calendar.MONTH))
                                {
                                    if (calNow.get(Calendar.DAY_OF_MONTH) < calSet.get(Calendar.DAY_OF_MONTH))
                                    {
                                        int check=checkoverlap();
                                        if(check==0) {
                                            startActivity(intent);
                                        }
                                    }
                                    else if (calNow.get(Calendar.DAY_OF_MONTH) == calSet.get(Calendar.DAY_OF_MONTH))
                                    {
                                        if (calNow.get(Calendar.HOUR_OF_DAY) < calSet.get(Calendar.HOUR_OF_DAY))
                                        {
                                            int check=checkoverlap();
                                            if(check==0) {
                                                startActivity(intent);
                                            }
                                        }
                                        else if (calNow.get(Calendar.HOUR_OF_DAY) == calSet.get(Calendar.HOUR_OF_DAY))
                                        {
                                            if (calNow.get(Calendar.MINUTE) < calSet.get(Calendar.MINUTE))
                                            {
                                                int check=checkoverlap();
                                                if(check==0) {
                                                    startActivity(intent);
                                                }
                                            }
                                            else if (calNow.get(Calendar.MINUTE) == calSet.get(Calendar.MINUTE))
                                            {
                                                int check=checkoverlap();
                                                if(check==0) {
                                                    startActivity(intent);
                                                }
                                            }
                                            else
                                                Toast.makeText(getBaseContext(), "Starting time has already passed", Toast.LENGTH_SHORT).show();
                                        }
                                        else
                                            Toast.makeText(getBaseContext(), "Starting time has already passed", Toast.LENGTH_SHORT).show();
                                    }
                                    else
                                        Toast.makeText(getBaseContext(), "Starting time has already passed", Toast.LENGTH_SHORT).show();
                                }
                                else
                                    Toast.makeText(getBaseContext(), "Starting time has already passed", Toast.LENGTH_SHORT).show();
                            }
                            else
                                Toast.makeText(getBaseContext(), "Starting time has already passed", Toast.LENGTH_SHORT).show();*/




        }
    }




    public int checkoverlap()
    {
        int overlap=0;

        Date start_date1 = new Date(start_yr, start_month, start_day-1, 0, 0);//date1 contains the current entered date of the event
        SimpleDateFormat outFormat = new SimpleDateFormat("EEEE");
        String start_goal = outFormat.format(start_date1);//for finding day at current date

        Date end_date1 = new Date(end_yr, end_month, end_day-1, 0, 0);//date1 contains the current entered date of the event
        String end_goal = outFormat.format(end_date1);//for finding day at current date





        DBAdapter db = new DBAdapter(this);
        db.open();//open database containing event details
        Cursor c1 = db.getAllEventsDetails();//retrieves all the existing events details from the database
        db.close();//database was opened somewhere above


        return overlap;
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
        if(asci1==asci2)
            return 3;
        else if(asci1<asci2)
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

        if(asci1==asci2)
            return 3;
        else if(asci1<asci2)
            return 1;
        else
            return 2;

    }





    //opens the dialog box for setting date of the event
    public void set_start_date(View view)
    {
        InputMethodManager im = (InputMethodManager)getSystemService(getBaseContext().INPUT_METHOD_SERVICE);
        im.hideSoftInputFromWindow(editText_start_date.getWindowToken(), 0);
        showDialog(1);

    }

    //opens the dialog box for setting date of the event
    public void set_end_date(View view)
    {
        InputMethodManager im = (InputMethodManager)getSystemService(getBaseContext().INPUT_METHOD_SERVICE);
        im.hideSoftInputFromWindow(editText_end_date.getWindowToken(), 0);
        showDialog(4);

    }


    //opens the dialog box for setting start time of the event
    public void setstart_time(View view)
    {
        InputMethodManager im = (InputMethodManager)getSystemService(getBaseContext().INPUT_METHOD_SERVICE);
        im.hideSoftInputFromWindow(editText_stime.getWindowToken(), 0);
        showDialog(0);
    }

    //opens the dialog box for setting end time of the event
    public void setend_time(View view)
    {
        InputMethodManager im = (InputMethodManager)getSystemService(getBaseContext().INPUT_METHOD_SERVICE);
        im.hideSoftInputFromWindow(editText_etime.getWindowToken(), 0);
        showDialog(2);
    }

    public void set_repeat(View view)
    {
        InputMethodManager im = (InputMethodManager)getSystemService(getBaseContext().INPUT_METHOD_SERVICE);
        im.hideSoftInputFromWindow(editText_repeat.getWindowToken(), 0);
        showDialog(5);
    }


    //called when dialog box is created
    @Override
    protected Dialog onCreateDialog(int id)
    {
        switch (id) {
            //shows dialog box for setting start time
            case 0:
                return new TimePickerDialog(
                        this, mTimeSetListener, shour, sminute, false);
            //shows dialog box for date
            case 1:
                return new DatePickerDialog(
                        this, new DatePickerDialog.OnDateSetListener(){
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        start_yr= year;
                        start_month= monthOfYear;
                        start_day = dayOfMonth;
                        //way of formatting date doesn't create any problem
                        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
                        Date date = new Date(start_yr-1900,start_month,start_day,0,0);
                        eve_start_date = dateFormat.format(date);

                        int date_check=date_identifier1(eve_start_date,eve_end_date);
                        if(date_check==3)
                        {
                            int time_check=time_identifier1(start_strtime,end_strtime);
                            if((time_check==1)||(time_check==3))
                            {
                                editText_start_date.setText(eve_start_date);
                            }
                            else
                            {
                                Toast.makeText(getApplicationContext(), "Start time should be smaller or equal to end time", Toast.LENGTH_SHORT).show();
                                Toast.makeText(getApplicationContext(), "Start time should be smaller or equal to end time", Toast.LENGTH_SHORT).show();
                             }
                        }
                        else if(date_check==1)
                        {
                            editText_start_date.setText(eve_start_date);
                        }
                        else
                        {
                            Toast.makeText(getApplicationContext(), "Start date should be smaller or equal to end date", Toast.LENGTH_SHORT).show();
                            Toast.makeText(getApplicationContext(), "Start date should be smaller or equal to end date", Toast.LENGTH_SHORT).show();

                        }



                    }
                }
                            ,start_yr,start_month,start_day);

            //shows dialog box for end time
            case 2: return  new TimePickerDialog(
                    this, m1TimeSetListener, ehour, eminute , false);




            case 4: return new DatePickerDialog(
                    this, new DatePickerDialog.OnDateSetListener(){
                @Override
                public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                    end_yr= year;
                    end_month= monthOfYear;
                    end_day = dayOfMonth;
                    //way of formatting date doesn't create any problem
                    SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
                    Date date = new Date(end_yr-1900,end_month,end_day,0,0);


                    eve_end_date = dateFormat.format(date);


                    int date_check=date_identifier1(eve_start_date,eve_end_date);
                    if(date_check==3)
                    {
                        int time_check=time_identifier1(start_strtime,end_strtime);
                        if((time_check==1)||(time_check==3))
                        {
                            editText_end_date.setText(eve_end_date);
                        }
                        else
                        {
                            Toast.makeText(getApplicationContext(), "Start time should be smaller or equal to end time", Toast.LENGTH_SHORT).show();
                            Toast.makeText(getApplicationContext(), "Start time should be smaller or equal to end time", Toast.LENGTH_SHORT).show();
                        }

                    }
                    else if(date_check==1)
                    {
                        editText_end_date.setText(eve_end_date);
                    }
                    else
                    {
                        Toast.makeText(getApplicationContext(), "Start date should be smaller or equal to end date", Toast.LENGTH_SHORT).show();
                        Toast.makeText(getApplicationContext(), "Start date should be smaller or equal to end date", Toast.LENGTH_SHORT).show();
                    }



                }
            }
                    ,end_yr,end_month,end_day);


            case 5:  return new AlertDialog.Builder(this)


                    .setItems(repeat_options1, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    final_repeat="";

                                    if (which == 5) {
                                        custom_editor.putString("name", editText_name.getText().toString());
                                        custom_editor.commit();
                                        custom_editor.putString("description", editText_desc.getText().toString());
                                        custom_editor.commit();
                                        custom_editor.putString("start_date", editText_start_date.getText().toString());
                                        custom_editor.commit();
                                        custom_editor.putString("end_date", editText_end_date.getText().toString());
                                        custom_editor.commit();
                                        custom_editor.putString("start_time", editText_stime.getText().toString());
                                        custom_editor.commit();
                                        custom_editor.putString("end_time", editText_etime.getText().toString());
                                        custom_editor.commit();
                                        //custom_editor.putInt("start_time_set", start_time_set);
                                        //custom_editor.commit();
                                        //custom_editor.putInt("end_time_set", end_time_set);
                                        //custom_editor.commit();
                                        custom_editor.putInt("flag", flag);
                                        custom_editor.commit();
                                        custom_editor.putInt("id_to_be_edited", id_to_be_edited);
                                        custom_editor.commit();
                                        custom_editor.putString("bluetooth", bluetooth);
                                        custom_editor.commit();
                                        custom_editor.putString("wifi",wifi1);
                                        custom_editor.commit();
                                        custom_editor.putString("mobile_data",mobile_data);
                                        custom_editor.commit();
                                        custom_editor.putString("profile",profile_status);
                                        custom_editor.commit();

                                        Intent intent=new Intent(CreateEvent.this,custom_rep.class);
                                        startActivity(intent);


                                    } else {
                                        final_repeat=Integer.toString(which);
                                        switch (which) {
                                            case 0:
                                                editText_repeat.setText("Does not Repeat");
                                                break;
                                            case 1:
                                                editText_repeat.setText("Every Day");
                                                break;
                                            case 2:
                                                editText_repeat.setText("Every Week");
                                                break;
                                            case 3:
                                                editText_repeat.setText("Every Month");
                                                break;
                                            case 4:
                                                editText_repeat.setText("Every Year");
                                                break;
                                        }
                                    }
                                }
                            }
                    ).create();



        }
        return null;
    }




    //onclick listener for start time setting dialog window
    private TimePickerDialog.OnTimeSetListener mTimeSetListener =
            new TimePickerDialog.OnTimeSetListener()
            {
                public void onTimeSet(
                        TimePicker view, int hourOfDay, int minuteOfHour)
                {
                    //shour contains hour at which event is to be started
                    shour = hourOfDay;
                    //sminute contains minute at which event should start
                    sminute = minuteOfHour;
                    SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm");
                    Date date = new Date(0,0,0, shour, sminute);
                    //start_time contains start time in string format
                     start_strtime = timeFormat.format(date);
                    if(eve_start_date.equals(eve_end_date))
                    {
                        int time_check=time_identifier1(start_strtime,end_strtime);
                        if((time_check==1)||(time_check==3))
                        {
                            editText_stime.setText(start_strtime);
                        }
                        else
                        {
                            Toast.makeText(getApplicationContext(), "Start time should be smaller or equal to end time", Toast.LENGTH_SHORT).show();
                            Toast.makeText(getApplicationContext(), "Start time should be smaller or equal to end time", Toast.LENGTH_SHORT).show();
                        }
                    }
                    else
                    {
                        editText_stime.setText(start_strtime);
                    }

                }
            };



    //on click for setting end time
    //works in same way as onclick for start time
    private TimePickerDialog.OnTimeSetListener m1TimeSetListener =
            new TimePickerDialog.OnTimeSetListener()
            {
                public void onTimeSet(
                        TimePicker view, int hourOfDay, int minuteOfHour)
                {
                    ehour = hourOfDay;
                    eminute = minuteOfHour;
                    SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm");
                    Date date = new Date(0,0,0, ehour, eminute);
                    end_strtime = timeFormat.format(date);

                    if(eve_start_date.equals(eve_end_date))
                    {
                        int time_check=time_identifier1(start_strtime,end_strtime);
                        if((time_check==1)||(time_check==3))
                        {
                            editText_etime.setText(end_strtime);
                        }
                        else
                        {
                            Toast.makeText(getApplicationContext(), "Start time should be smaller or equal to end time", Toast.LENGTH_SHORT).show();
                            Toast.makeText(getApplicationContext(), "Start time should be smaller or equal to end time", Toast.LENGTH_SHORT).show();
                        }
                    }
                    else
                    {
                        editText_etime.setText(end_strtime);
                    }
                }
            };

}
