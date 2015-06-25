package com.example.dvs.occasus;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.view.WindowManager;




public class custom_rep extends ActionBarActivity implements rep_daily.rep_dailylistener, rep_weekly.rep_weeklylistener,
        rep_monthly.rep_monthlylistener ,rep_yearly.rep_yearlylistener,no_of_events.no_of_eventslistener,
        until_date.until_datelistener,datepickerfragment.datepickerfragmentlistener{

    String[] cus_options={"Repeat Daily","Repeat Weekly","Repeat Monthly","Repeat Yearly"};
    String[] until_options={"Forever","Until a date","For a number of events"};
    int cus_options_selected,until_options_selected;

    String until_no_of_events;
    String until_date;

    String daily_no_of_days;
    String monthly_no_of_months;
    String weekly_no_of_weeks;
    String yearly_no_of_years;
    int monthly_radio_selected;
    int weekly_is_mon_selected,weekly_is_tue_selected,weekly_is_wed_selected,weekly_is_thu_selected,
            weekly_is_fri_selected,weekly_is_sat_selected,weekly_is_sun_selected;

    Button b;
    String repeat="";
    String until="";
    String cur_dayofweek_for_cus_monthly_rep;

    public static final String custom_info = "custom_repeat";
    SharedPreferences.Editor custom_editor;
    SharedPreferences custom_sharedpreferences;


    @Override
    public void showDatePickerDialog() {
        DialogFragment newFragment = new datepickerfragment();
        newFragment.show(getSupportFragmentManager(), "datePicker");
    }

    @Override
    public void hide_done() {
        Button b;
        b=(Button) findViewById(R.id.button2);
        b.setVisibility(View.INVISIBLE);
    }

    @Override
    public void show_done() {
        Button b;
        b=(Button) findViewById(R.id.button2);
        b.setVisibility(View.VISIBLE);
    }

    @Override
    public void getdate(String date) {
        until_date=date;
    }

    @Override
    public void editext_setdate(String date) {
        until_date u_date=(until_date) getSupportFragmentManager().findFragmentById(R.id.fragment6);
        u_date.setedit(date,getBaseContext());
    }


    @Override
    public void getdata_no_events(String number) {
        until_no_of_events=number;
    }

    @Override
    public void getdata_yearly(String number) {
        yearly_no_of_years=number;
    }

    @Override
    public void getdata_monthly(String number, int radio,String cur_dayofweek_in_custom_monthly_repeat) {
        monthly_no_of_months=number;
        monthly_radio_selected=radio;
        cur_dayofweek_for_cus_monthly_rep=cur_dayofweek_in_custom_monthly_repeat;
    }

    @Override
    public void getdata_weekly(String number, int mon, int tue, int wed, int thu, int fri, int sat, int sun) {
        weekly_no_of_weeks=number;
        weekly_is_mon_selected=mon;
        weekly_is_tue_selected=tue;
        weekly_is_wed_selected=wed;
        weekly_is_thu_selected=thu;
        weekly_is_fri_selected=fri;
        weekly_is_sat_selected=sat;
        weekly_is_sun_selected=sun;
    }

    @Override
    public void getdata_daily(String number) {
        daily_no_of_days=number;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_custom_rep);

        getSupportActionBar().hide();


       // ActionBar ac=Actio;
       // ac.setIcon(R.drawable.occasus1);
       // getSupportFragmentManager().getActionBar().show();
        //ac.setDisplayShowHomeEnabled(true);
        //ac.setLogo(R.drawable.occasus1);
        //ac.setDisplayUseLogoEnabled(true);
        //getSupportActionBar().setDisplayHomeAsUpEnabled(false);//to hide backbuttton in action bar
   //     ac.show();



        // WindowManager windowManager = (WindowManager)getBaseContext().getSystemService(WINDOW_SERVICE);
        WindowManager.LayoutParams params=getWindow().getAttributes();
        params.x=-20;
        params.height = 800;
        params.width = 550;
        params.y = -10;

        this.getWindow().setAttributes(params);



        custom_sharedpreferences = getBaseContext().getSharedPreferences(custom_info, Context.MODE_PRIVATE);
        custom_editor = custom_sharedpreferences.edit();


        b=(Button)findViewById(R.id.button2);

        Spinner s1 = (Spinner) findViewById(R.id.rep_spinner);
        Spinner s2=(Spinner)findViewById(R.id.until_spinner);

        ArrayAdapter<String> adapter1 = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_dropdown_item, cus_options);

        ArrayAdapter<String> adapter2= new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_dropdown_item,until_options);







        final View view1=findViewById(R.id.fragment1);
        final View view2=findViewById(R.id.fragment2);
        final View view3=findViewById(R.id.fragment3);
        final View view4=findViewById(R.id.fragment4);
        final View view5=findViewById(R.id.fragment5);
        final View view6=findViewById(R.id.fragment6);


        view1.setVisibility(View.VISIBLE);
        view2.setVisibility(View.INVISIBLE);
        view3.setVisibility(View.INVISIBLE);
        view4.setVisibility(View.INVISIBLE);
        view5.setVisibility(View.INVISIBLE);
        view6.setVisibility(View.VISIBLE);

        s1.setAdapter(adapter1);
        s1.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                cus_options_selected=position;
                switch (position) {
                    case 0:
                        view1.setVisibility(View.VISIBLE);
                        view2.setVisibility(View.INVISIBLE);
                        view3.setVisibility(View.INVISIBLE);
                        view4.setVisibility(View.INVISIBLE);
                        cus_options_selected=0;
                        break;
                    case 1:
                        view1.setVisibility(View.INVISIBLE);
                        view2.setVisibility(View.INVISIBLE);
                        view3.setVisibility(View.VISIBLE);
                        view4.setVisibility(View.INVISIBLE);
                        cus_options_selected=1;
                        break;
                    case 2:
                        current_day();
                        view1.setVisibility(View.INVISIBLE);
                        view2.setVisibility(View.INVISIBLE);
                        view3.setVisibility(View.INVISIBLE);
                        view4.setVisibility(View.VISIBLE);
                        cus_options_selected=2;
                        break;
                    case 3:
                        view1.setVisibility(View.INVISIBLE);
                        view2.setVisibility(View.VISIBLE);
                        view3.setVisibility(View.INVISIBLE);
                        view4.setVisibility(View.INVISIBLE);
                        cus_options_selected=3;
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        s2.setAdapter(adapter2);
        s2.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                until_options_selected=position;
                switch (position)
                {
                    case 0:
                        view5.setVisibility(View.INVISIBLE);
                        view6.setVisibility(View.INVISIBLE);

                        break;
                    case 1:
                        view5.setVisibility(View.INVISIBLE);
                        view6.setVisibility(View.VISIBLE);

                        break;
                    case 2:
                        view5.setVisibility(View.VISIBLE);
                        view6.setVisibility(View.INVISIBLE);

                        break;
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

    }



    public void current_day()
    {
        rep_monthly r_month=(rep_monthly) getSupportFragmentManager().findFragmentById(R.id.fragment4);
        r_month.cur_day();
    }

    public void click(View view)
    {
        switch (cus_options_selected)
        {
            case 0:rep_daily r_daily = (rep_daily) getSupportFragmentManager().findFragmentById(R.id.fragment1);
                    r_daily.getdata1();
                    repeat="5_0_";
                    repeat=repeat.concat(daily_no_of_days);
                    break;
            case 1:rep_weekly r_weekly=(rep_weekly) getSupportFragmentManager().findFragmentById(R.id.fragment3);
                    r_weekly.getdata1();
                    repeat="5_1_";
                    repeat=repeat.concat(weekly_no_of_weeks);
                    repeat=repeat.concat("_");
                    repeat=repeat.concat(Integer.toString(weekly_is_mon_selected)+Integer.toString(weekly_is_tue_selected)+
                    Integer.toString(weekly_is_wed_selected)+Integer.toString(weekly_is_thu_selected)+
                    Integer.toString(weekly_is_fri_selected)+Integer.toString(weekly_is_sat_selected)+
                            Integer.toString(weekly_is_sun_selected));
                    break;
            case 2:rep_monthly r_monthly=(rep_monthly)getSupportFragmentManager().findFragmentById(R.id.fragment4);
                r_monthly.getdata1();
                repeat="5_2_";
                repeat=repeat.concat(monthly_no_of_months);
                repeat=repeat.concat("_");
                repeat=repeat.concat(Integer.toString(monthly_radio_selected));//if custom monthly then string is also added to in settoggles
                break;
            case 3:rep_yearly r_yearly=(rep_yearly)getSupportFragmentManager().findFragmentById(R.id.fragment2);
                r_yearly.getdata1();
                repeat="5_3_";
                repeat=repeat.concat(yearly_no_of_years);
                break;
        }
        switch (until_options_selected)
        {
            case 2:no_of_events n_events=(no_of_events) getSupportFragmentManager().findFragmentById(R.id.fragment5);
                n_events.getdata1();
                until="2_";
                until=until.concat(until_no_of_events);
                break;
            case 1:until_date u_date=(until_date) getSupportFragmentManager().findFragmentById(R.id.fragment6);
                u_date.getdata1();
                until="1_";
                until=until.concat(until_date);
                break;
            case 0:until="0";
        }
        custom_editor.putInt("coming_from_custom_repeat", 1);
        custom_editor.commit();
        custom_editor.putString("custom_repeat", repeat);
        custom_editor.commit();
        custom_editor.putString("repeat_until",until);
        custom_editor.commit();
        custom_editor.putString("cur_dayofweek_for_cus_monthly_rep",cur_dayofweek_for_cus_monthly_rep);
        custom_editor.commit();
        Intent intent=new Intent(custom_rep.this,CreateEvent.class);
        startActivity(intent);
    }

}
