package com.example.dvs.occasus;

import android.content.Intent;
import android.database.Cursor;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;


import android.widget.TextView;



public class Show_details extends ActionBarActivity {

    int clicked_id;
    String st;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_details);


        //to add logo to action bar
        ActionBar ac=getSupportActionBar();
        ac.setDisplayShowHomeEnabled(true);
        ac.setLogo(R.drawable.occasus1);
        ac.setDisplayUseLogoEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        clicked_id=getIntent().getIntExtra("clicked_id",0);
        DBAdapter db = new DBAdapter(this);
        db.open();
        Cursor c=db.getEventDetail(clicked_id);
        if(c.moveToFirst())
        {
            TextView name= (TextView)findViewById(R.id.name_text);
            name.setText(c.getString(c.getColumnIndex("event_name")));


            TextView desc= (TextView)findViewById(R.id.description_text);
            desc.setText(c.getString(c.getColumnIndex("description")));

            TextView start_date= (TextView)findViewById(R.id.start_date_text);
            start_date.setText(c.getString(c.getColumnIndex("event_start_date")));

            TextView end_date= (TextView) findViewById(R.id.end_date_text);
            end_date.setText(c.getString(c.getColumnIndex("event_end_date")));

            TextView stime= (TextView)findViewById(R.id.stime_text);
            stime.setText(c.getString(c.getColumnIndex("start_time")));

            TextView etime= (TextView)findViewById(R.id.etime_text);
            st=c.getString(c.getColumnIndex("end_time"));
            etime.setText(st);


            TextView bluetooth= (TextView)findViewById(R.id.bluetooth_text);
            bluetooth.setText(c.getString(c.getColumnIndex("bluetooth")));
            

            TextView wifi= (TextView)findViewById(R.id.wifi_text);
            wifi.setText(c.getString(c.getColumnIndex("wifi")));

            TextView profile= (TextView)findViewById(R.id.profile_text);
            profile.setText(c.getString(c.getColumnIndex("profile")));

           TextView mobile_data= (TextView)findViewById(R.id.mobile_data_text);
            mobile_data.setText(c.getString(c.getColumnIndex("mobile_data")));



            String final_repeat=c.getString(c.getColumnIndex("repeat"));
            String cur_dayofweek_for_cus_monthly_rep=c.getString(c.getColumnIndex("cur_dayofweek_for_cus_monthly_rep"));
            String repeat_until=c.getString(c.getColumnIndex("repeat_until"));
            TextView repeat=(TextView) findViewById(R.id.repeat_text);
            if(final_repeat.charAt(0)=='0')
            {
                repeat.setText("Does not Repeat");
            }
            else if(final_repeat.charAt(0)=='1')
            {
                repeat.setText("Every Day");
            }
            else if(final_repeat.charAt(0)=='2')
            {
                repeat.setText("Every Week");
            }
            else if(final_repeat.charAt(0)=='3')
            {
                repeat.setText("Every Month");
            }
            else if(final_repeat.charAt(0)=='4')
            {
                repeat.setText("Every Year");
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
                repeat.setText(rep_edittext_string);

            }


        }
        db.close();


    }

    //back button override......sends the app back to mainactivity screen
    @Override
    public void onBackPressed() {
        Intent intent = new Intent(Show_details.this,show_events_list.class);

        startActivity(intent);

    }


}
