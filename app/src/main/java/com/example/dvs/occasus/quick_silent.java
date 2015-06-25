package com.example.dvs.occasus;

import android.app.AlarmManager;
import android.app.Dialog;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.text.InputFilter;
import android.text.InputType;
import android.text.Spanned;
import android.view.*;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;


import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;


public class quick_silent extends ActionBarActivity {

    public static final String quickSilent = "quick_silent";
    SharedPreferences quick_sharedpreferences;
    //shared preferences editor declared
    SharedPreferences.Editor quick_editor;
    //initializing shared preferences


    Button b1,b2,b3,b4,b5,b6;
    EditText hr,min,time;
    TextView t,t2,t_hour,t_min;
    int set_hour,set_min;

    MenuItem menu_item;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quick_silent);

        //to add logo to action bar
        ActionBar ac=getSupportActionBar();
        ac.setDisplayShowHomeEnabled(true);
        ac.setLogo(R.drawable.occasus1);
        ac.setDisplayUseLogoEnabled(true);
        ac.setTitle("Quick Silent");
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);//to hide the back button in action bar


        quick_sharedpreferences = getBaseContext().getSharedPreferences(quickSilent, Context.MODE_PRIVATE);
        quick_editor = quick_sharedpreferences.edit();


        menu_item=(MenuItem) findViewById(R.id.by_change);

        hr=(EditText) findViewById(R.id.time_hour);
        min=(EditText) findViewById(R.id.time_min);

        b1=(Button) findViewById(R.id.time_button1);
        b2=(Button) findViewById(R.id.time_button2);
        b3=(Button) findViewById(R.id.time_button3);
        b4=(Button) findViewById(R.id.time_button4);
        b5=(Button) findViewById(R.id.time_button5);
        b6=(Button) findViewById(R.id.time_button6);


        t=(TextView) findViewById(R.id.time_textView3);
        t2=(TextView) findViewById(R.id.time_textView2);
        t_hour=(TextView) findViewById(R.id.textView4_hr);
        t_min=(TextView) findViewById(R.id.textView4_min);

        time=(EditText) findViewById(R.id.time_editText3);
        time.setVisibility(View.INVISIBLE);


        hr.setRawInputType(InputType.TYPE_CLASS_NUMBER);
        min.setRawInputType(InputType.TYPE_CLASS_NUMBER);


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



    }


    public void set_time(View view)
    {
        InputMethodManager im = (InputMethodManager)getSystemService(getBaseContext().INPUT_METHOD_SERVICE);
        im.hideSoftInputFromWindow(time.getWindowToken(), 0);
        showDialog(0);
    }





    @Override
    protected Dialog onCreateDialog(int id)
    {
        switch (id) {
            //shows dialog box for setting start time
            case 0:
                return new TimePickerDialog(
                        this, mTimeSetListener, set_hour, set_min, false);
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
                    set_hour = hourOfDay;
                    //sminute contains minute at which event should start
                    set_min = minuteOfHour;
                    SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm");
                    Date date = new Date(0,0,0, set_hour, set_min);
                    //start_time contains start time in string format
                    String strtime = timeFormat.format(date);

                    time.setText(strtime);

                }
            };






    public void min_15(View view)
    {
           hr.setText("00");
            min.setText("15");
    }
    public void min_30(View view)
    {
        hr.setText("00");
        min.setText("30");
    }
    public void hour_1(View view)
    {
        hr.setText("01");
        min.setText("00");
    }
    public void hour_2(View view)
    {
        hr.setText("02");
        min.setText("00");
    }
    public void hour_5(View view)
    {
        hr.setText("05");
        min.setText("00");
    }
    public void hour_12(View view)
    {
        hr.setText("12");
        min.setText("00");
    }



    public void silent(View view)
    {
        if(time.getVisibility()==View.VISIBLE)
        {
            if(time.getText().toString().equals(""))
                Toast.makeText(getBaseContext(),"Enter a time",Toast.LENGTH_SHORT).show();
            else
            {


                quick_editor.putInt("options_selected",2);
                quick_editor.commit();
                quick_editor.putString("time",time.getText().toString());
                quick_editor.commit();

                Calendar cal_start= Calendar.getInstance();

                Intent intent1 = new Intent(getBaseContext(),activate_quick_silent.class);
                PendingIntent pendingIntent1 = PendingIntent.getBroadcast(getBaseContext(), 0, intent1, PendingIntent.FLAG_CANCEL_CURRENT);
                AlarmManager alarmManager1 = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
                alarmManager1.set(AlarmManager.RTC_WAKEUP, cal_start.getTimeInMillis(), pendingIntent1);


                Calendar cal_end=Calendar.getInstance();
                cal_end.set(Calendar.HOUR_OF_DAY,set_hour);
                cal_end.set(Calendar.MINUTE,set_min);
                cal_end.set(Calendar.SECOND,0);
                cal_end.set(Calendar.MILLISECOND,0);

                Intent intent2 = new Intent(getBaseContext(),deactivate_quick_silent.class);
                PendingIntent pendingIntent2 = PendingIntent.getBroadcast(getBaseContext(), 1, intent2, PendingIntent.FLAG_CANCEL_CURRENT);
                AlarmManager alarmManager2 = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
                alarmManager2.set(AlarmManager.RTC_WAKEUP, cal_end.getTimeInMillis(), pendingIntent2);


                Intent intent3=new Intent(this,MainActivity.class);
                startActivity(intent3);

            }
        }
        else
        {

            if((hr.getText().toString().equals(""))||(min.getText().toString().equals("")))
            {
                Toast.makeText(getBaseContext(),"Enter some duration",Toast.LENGTH_SHORT).show();
            }
            else
            {

                quick_editor.putInt("options_selected",1);
                quick_editor.commit();
                quick_editor.putString("hour",hr.getText().toString());
                quick_editor.commit();
                quick_editor.putString("min",min.getText().toString());
                quick_editor.commit();

                Calendar cal_start= Calendar.getInstance();

                Intent intent1 = new Intent(getBaseContext(),activate_quick_silent.class);
                PendingIntent pendingIntent1 = PendingIntent.getBroadcast(getBaseContext(), 0, intent1, PendingIntent.FLAG_UPDATE_CURRENT);
                AlarmManager alarmManager1 = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
                alarmManager1.set(AlarmManager.RTC_WAKEUP, cal_start.getTimeInMillis(), pendingIntent1);


                Calendar cal_end=Calendar.getInstance();
                cal_end.add(Calendar.HOUR_OF_DAY, Integer.valueOf(hr.getText().toString()));
                cal_end.add(Calendar.MINUTE, Integer.valueOf(min.getText().toString()));

                Intent intent2 = new Intent(getBaseContext(),deactivate_quick_silent.class);
                PendingIntent pendingIntent2 = PendingIntent.getBroadcast(getBaseContext(), 1, intent2, PendingIntent.FLAG_UPDATE_CURRENT);
                AlarmManager alarmManager2 = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
                alarmManager2.set(AlarmManager.RTC_WAKEUP, cal_end.getTimeInMillis(), pendingIntent2);


                Intent intent3=new Intent(this,MainActivity.class);
                startActivity(intent3);


            }
        }
    }



    //for showing menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_quick_silent, menu);
        return true;
    }


    //for setting what happens when items in menu are clicked
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.

        switch (item.getItemId())
        {
            case R.id.by_change://if clicked item is help
                if(item.getTitle().toString().equals("By Time"))
                {
                    t.setText(" By  Time  ");
                    item.setTitle("By Duration");
                    time.setText("");
                    b1.setVisibility(View.INVISIBLE);
                    b2.setVisibility(View.INVISIBLE);
                    b3.setVisibility(View.INVISIBLE);
                    b4.setVisibility(View.INVISIBLE);
                    b5.setVisibility(View.INVISIBLE);
                    b6.setVisibility(View.INVISIBLE);

                    hr.setVisibility(View.INVISIBLE);
                    min.setVisibility(View.INVISIBLE);

                    t_hour.setVisibility(View.INVISIBLE);
                    t_min.setVisibility(View.INVISIBLE);
                    t2.setVisibility(View.INVISIBLE);

                    time.setVisibility(View.VISIBLE);
                }
                else
                {
                    item.setTitle("By Time");
                    t.setText(" By Duration ");
                    b1.setVisibility(View.VISIBLE);
                    b2.setVisibility(View.VISIBLE);
                    b3.setVisibility(View.VISIBLE);
                    b4.setVisibility(View.VISIBLE);
                    b5.setVisibility(View.VISIBLE);
                    b6.setVisibility(View.VISIBLE);


                    hr.setVisibility(View.VISIBLE);
                    min.setVisibility(View.VISIBLE);

                    t2.setVisibility(View.VISIBLE);
                    t_hour.setVisibility(View.VISIBLE);
                    t_min.setVisibility(View.VISIBLE);

                    time.setVisibility(View.INVISIBLE);
                }
                break;
        }
        return super.onOptionsItemSelected(item);

    }

}
