package com.example.dvs.occasus;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;

import android.content.DialogInterface;
import android.database.Cursor;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.content.Intent;
import android.view.View;


import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import android.app.Dialog;


import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;

import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;

import android.widget.Toast;




public class CreateEvent extends ActionBarActivity {

    int shour, sminute,ehour,eminute;
    int yr, month, day;
    static final int DATE_DIALOG_ID = 1;
    static final int TIME_DIALOG_ID = 0;
    String event_name;
    String desc;
    EditText eve_name1;
    EditText descrip1;
    String start_strtime;
    String end_strtime;
    String eve_date;

    Switch s;
    int flag;

    String req_name;


    Button b;
    CharSequence[] items = { "Monday", "Tuesday", "Wednesday","Thursday","Friday","Saturday","Sunday" };
    boolean[] itemsChecked = new boolean [items.length];
    boolean[] previous_itemsChecked = new boolean [items.length];


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_event);
        Intent intent = getIntent();
        flag=intent.getIntExtra("flag",1);
        Calendar today = Calendar.getInstance();
        yr = today.get(Calendar.YEAR);
        month = today.get(Calendar.MONTH);
        day = today.get(Calendar.DAY_OF_MONTH);
        eve_name1   = (EditText)findViewById(R.id.eve_name);
        descrip1 = (EditText)findViewById(R.id.descrip);
        s=(Switch)findViewById(R.id.switch2);
        TextView textView =(TextView)findViewById(R.id.textView2);



        //to initialize item_checked array.......item_checked used for storing checked values in repeatition dailog box days
        if(flag==0) {
            for (int i = 0; i < 7; i++) {
                itemsChecked[i] = false;
                previous_itemsChecked[i] = false;
            }
        }

        //if edit is called
        if(flag==1)
        {
            textView.setText("Edit Event");


            //req_name is name of the profile to be edited
            //req_name is sent to createvent from mainactivity if user wants to edit the event
            //if a new event is created then req_name contains the value to which it was initialized in mainacitivity
            req_name= intent.getStringExtra("der_name");
            DBAdapter db1 = new DBAdapter(CreateEvent.this);
            db1.open();

            Cursor c;
            //to get details of all events with name=req_name
            c=db1.getEventsDetail(req_name);

            c.moveToFirst();

                //to check the days which were checked earlier
                if(c.getInt(c.getColumnIndex("monday"))==1) {
                    itemsChecked[0] = true;
                    previous_itemsChecked[0] = true;
                }
            else
                {
                    itemsChecked[0] = false;
                    previous_itemsChecked[0] = false;
                }


            if(c.getInt(c.getColumnIndex("tuesday"))==1) {
                itemsChecked[1] = true;
                previous_itemsChecked[1] = true;
            }
            else
            {
                itemsChecked[1] = false;
                previous_itemsChecked[1] = false;
            }


            if(c.getInt(c.getColumnIndex("wednesday"))==1) {
                itemsChecked[2] = true;
                previous_itemsChecked[2] = true;
            }
            else
            {
                itemsChecked[2] = false;
                previous_itemsChecked[2] = false;
            }


            if(c.getInt(c.getColumnIndex("thursday"))==1) {
                itemsChecked[3] = true;
                previous_itemsChecked[3] = true;
            }
            else
            {
                itemsChecked[3] = false;
                previous_itemsChecked[3] = false;
            }


            if(c.getInt(c.getColumnIndex("friday"))==1) {
                itemsChecked[4] = true;
                previous_itemsChecked[4] = true;
            }
            else
            {
                itemsChecked[4] = false;
                previous_itemsChecked[4] = false;
            }


            if(c.getInt(c.getColumnIndex("saturday"))==1) {
                itemsChecked[5] = true;
                previous_itemsChecked[5] = true;
            }
            else
            {
                itemsChecked[5] = false;
                previous_itemsChecked[5] = false;
            }


            if(c.getInt(c.getColumnIndex("sunday"))==1) {
                itemsChecked[6] = true;
                previous_itemsChecked[6] = true;
            }
            else
            {
                itemsChecked[6] = false;
                previous_itemsChecked[6] = false;
            }



            int switch_check=0;
            //to set the switch to on if it was on at time of its creation
            //first check if it was on by checking if the event was supposed to repeat on some day
            for(int i=0;i<7;i++)
            {
                if(itemsChecked[i]==true)
                {
                    switch_check=1;
                    break;
                }
            }


            if(switch_check==1)
            {
                s.setChecked(true);
            }
            else
            {
                s.setChecked(false);
            }

            //to set the fields to the values entered earlier
            EditText name= (EditText)findViewById(R.id.eve_name);
            name.setText(c.getString(c.getColumnIndex("event_name")));

            name= (EditText)findViewById(R.id.descrip);
            name.setText(c.getString(c.getColumnIndex("description")));
            Button bt = (Button)findViewById(R.id.button2);
           bt.setText(c.getString(c.getColumnIndex("event_date")));

            bt = (Button)findViewById(R.id.button3);
            bt.setText(c.getString(c.getColumnIndex("start_time")));

            bt = (Button)findViewById(R.id.button4);
            bt.setText(c.getString(c.getColumnIndex("end_time")));
            event_name= c.getString(c.getColumnIndex("event_name"));
            desc= c.getString(c.getColumnIndex("description"));
            eve_date= c.getString(c.getColumnIndex("event_date"));
            start_strtime=c.getString(c.getColumnIndex("start_time"));
            end_strtime= c.getString(c.getColumnIndex("end_time"));


            db1.close();

        }


        //on click listener for repeation switch
        s.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked)
                {
                    //to set the current checked values to the previously checked values
                    for(int i=0;i<=6;i++)
                    {
                        itemsChecked[i]=previous_itemsChecked[i];
                    }
                    //to show dialog box of repetition of days
                    showDialog(9);
                }
                else {
                    for(int i=0;i<=6;i++)
                    {
                        itemsChecked[i]=false;
                        previous_itemsChecked[i]=false;
                    }

                }

            }
        });

    }



    //back button override......sends the app back to mainactivity screen
    @Override
    public void onBackPressed() {
        Intent intent = new Intent(CreateEvent.this,MainActivity.class);

        startActivity(intent);

    }





    //calls SetToggles activity
    //on click listener for set toggles button
    //sends the data of create event to set toggles
    public void set_toggles(View view){


        //eve_name1 contains the name of event which is getting created
        event_name = eve_name1.getText().toString();

        //descrip1 contains the desc of event which is to be created
         desc = descrip1.getText().toString();


        //intent for settoggles activity is created
        Intent intent = new Intent(CreateEvent.this, SetToggles.class);
        //name description date(in string) starttime(in string) endtime(in string) day(intrger) month(integer) year(integer) start hour(integer)
        //start minute(integer) end minute(integer) end hour(integer)
        intent.putExtra("Name",event_name);
        intent.putExtra("Description",desc);
        intent.putExtra("Date",eve_date);
        intent.putExtra("STime",start_strtime);
        intent.putExtra("ETime",end_strtime);
        intent.putExtra("req_name",req_name);
        intent.putExtra("int_day",day);
        intent.putExtra("int_month",month);
        intent.putExtra("int_year",yr);
        intent.putExtra("int_shour",shour);
        intent.putExtra("int_ehour",ehour);
        intent.putExtra("int_sminute",sminute);
        intent.putExtra("int_eminute",eminute);
        //add initialized to 0
        int add=0;

        //to show if any day was checked in repeat dialog box

        for(int i=0;i<=6;i++)
        {
            if(previous_itemsChecked[i])
            {
               Toast.makeText(getBaseContext(),"day="+items[i],Toast.LENGTH_SHORT).show();
                add=1;
                break;
            }
        }
        //add=0 means that there is no repeat or no day is checked in repeat dialog
        //add=1 means that event is to be repeated


            //previous_items checked[i]=1 means that day no i was checked
        //previous items is sent to set toggles
            if (previous_itemsChecked[0])
                intent.putExtra("int_mon", 1);
            else
                intent.putExtra("int_mon", 0);
            if (previous_itemsChecked[1])
                intent.putExtra("int_tue", 1);
            else
                intent.putExtra("int_tue", 0);
            if (previous_itemsChecked[2])
                intent.putExtra("int_wed", 1);
            else
                intent.putExtra("int_wed", 0);
            if (previous_itemsChecked[3])
                intent.putExtra("int_thu", 1);
            else
                intent.putExtra("int_thu", 0);
            if (previous_itemsChecked[4])
                intent.putExtra("int_fri", 1);
            else
                intent.putExtra("int_fri", 0);
            if (previous_itemsChecked[5])
                intent.putExtra("int_sat", 1);
            else
                intent.putExtra("int_sat", 0);
            if (previous_itemsChecked[6])
                intent.putExtra("int_sun", 1);
            else
                intent.putExtra("int_sun", 0);

       //add is sent to settoggles
        intent.putExtra("add",add);


        //flag=1 means that edit event is to be performed
        //flag=0 means that new event is to be created

        if(flag==1)
        intent.putExtra("flag",1);
        else
        intent.putExtra("flag",0);

        //settoggles intent is called
        startActivity(intent);
    }




//opens the dialog box for setting date of the event
    public void setdate(View view)
    {

        showDialog(DATE_DIALOG_ID);
    }

    //opens the dialog box for setting start time of the event
    public void setstart_time(View view)
    {
        showDialog(TIME_DIALOG_ID);
    }

    //opens the dialog box for setting end time of the event
    public void setend_time(View view)
    {
        showDialog(2);
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
                        yr= year;
                        month= monthOfYear;
                        day = dayOfMonth;
                        //way of formatting date doesn't create any problem
                        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
                        Date date = new Date(yr-1900,month,day,0,0);
                        eve_date = dateFormat.format(date);
                        //this toast shows the date selected
                        Toast.makeText(getBaseContext(),
                                "You have selected : " + eve_date,

                                Toast.LENGTH_SHORT).show();
                        b= (Button)findViewById(R.id.button2);
                        //date selected is displayed on the button
                        b.setText(eve_date);
                    }
                }
                            ,yr,month,day);

            //shows dialog box for end time
            case 2: return  new TimePickerDialog(
                    this, m1TimeSetListener, ehour, eminute , false);

            //shows dialog box for repeat
            case 9: return new AlertDialog.Builder(this)
                    .setIcon(R.drawable.ic_launcher)
                    .setTitle("This is a dialog with some simple text")

                    .setNegativeButton("Cancel",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int whichButton) {


                                }
                            }
                    )


                    .setMultiChoiceItems(items, itemsChecked,
                            new DialogInterface.OnMultiChoiceClickListener() {
                                public void onClick(DialogInterface dialog,
                                                    int which, boolean isChecked) {
                                    //if a  day is selected then set itemchecked true for that day
                                    if(isChecked) {
                                        itemsChecked[which] = true;
                                    }
                                    else
                                    {
                                        itemsChecked[which]=false;
                                    }
                                    //shows the day which is checked or unchecked
                                    Toast.makeText(getBaseContext(),
                                            items[which] + (isChecked ? " checked" :"unchecked"),
                                    Toast.LENGTH_SHORT).show();
                                }
                            }
                    )


                    .setPositiveButton("OK",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int whichButton) {

                                    //when ok button is clicked item checked is copied into previous itemchecked
                                    //so previous item checked contains the final selected items
                                    for(int i=0;i<items.length;i++)
                                    {
                                        previous_itemsChecked[i]=itemsChecked[i];
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
                    Toast.makeText(getBaseContext(),
                            "You have selected "  + start_strtime,
                            Toast.LENGTH_SHORT).show();
                    b= (Button)findViewById(R.id.button3);
                    b.setText(start_strtime);

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
                    Toast.makeText(getBaseContext(),
                            "You have selected "  + end_strtime,
                            Toast.LENGTH_SHORT).show();
                    b= (Button)findViewById(R.id.button4);
                    b.setText(end_strtime);

                }
            };




    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_create_event, menu);
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
