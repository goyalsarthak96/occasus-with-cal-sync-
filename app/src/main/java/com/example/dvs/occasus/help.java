package com.example.dvs.occasus;

import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;


import android.widget.TextView;


public class help extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help);


        //to add logo to action bar
        ActionBar ac=getSupportActionBar();
        ac.setDisplayShowHomeEnabled(true);
        ac.setLogo(R.drawable.occasus1);
        ac.setDisplayUseLogoEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        TextView t;
        String s1;

        s1="1) On the home screen, you can create a new event with the help of add new event.";
        t=(TextView)findViewById(R.id.s1_text);
        t.setText(s1);
        String s2="        a) When you click on ADD NEW EVENT button, create event screen appears.";
        t=(TextView)findViewById(R.id.s2_text);
        t.setText(s2);
        String s3="        b) Fill required details and then click on SET TOGGLES.";
        t=(TextView)findViewById(R.id.s3_text);
        t.setText(s3);
        String s4="        c) When you click on SAVE EVENT, home screen appears with newly added event displayed.";
        t=(TextView)findViewById(R.id.s4_text);
        t.setText(s4);
        String s5="2) List of events is shown on home screen.";
        t=(TextView)findViewById(R.id.s5_text);
        t.setText(s5);
        String s6="3) If you click on a event, a window pops up that provides the options of editing,deleting or viewing the event.";
        t=(TextView)findViewById(R.id.s6_text);
        t.setText(s6);
        String s7="4) CONTACT EXCEPTION";
        t=(TextView)findViewById(R.id.s7_text);
        t.setText(s7);
        String s8 ="       a) If an event is running and phone is not in ring mode, then phone will even ring for calls from contacts added in this list.";
        t=(TextView)findViewById(R.id.s8_text);
        t.setText(s8);
        String s9="        b) By clicking on add new contact, you can add new contact.";
        t=(TextView)findViewById(R.id.s9_text);
        t.setText(s9);
        String s10="       c) By selecting a contact from list and then clicking on DELETE CONTACT will delete it.";
        t=(TextView)findViewById(R.id.s10_text);
        t.setText(s10);
        String s11="5) URGENT SMS";
        t=(TextView)findViewById(R.id.s11_text);
        t.setText(s11);
        String s12="       a) If an event is running, then an SMS will be sent on receiving a call from callers present in this list.";
        t=(TextView)findViewById(R.id.s12_text);
        t.setText(s12);
        String s13="       b) same as 4.b)";
        t=(TextView)findViewById(R.id.s13_text);
        t.setText(s13);
        String s14="       c) same as 4.c)";
        t=(TextView)findViewById(R.id.s14_text);
        t.setText(s14);



    }


}
