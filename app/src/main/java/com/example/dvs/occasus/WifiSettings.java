package com.example.dvs.occasus;

import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiManager;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.RadioButton;


public class WifiSettings extends ActionBarActivity {
    RadioButton rb1,rb2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wifi_settings);
        Intent intent = getIntent();

       //on click , turns on the bluetooth
       rb1 = (RadioButton)findViewById(R.id.turnon_button);
        rb1.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                WifiManager wifi =(WifiManager)getSystemService(Context.WIFI_SERVICE);
                wifi.setWifiEnabled(true);

            }});

        //on click ,turns off the bluetooth
        rb2 = (RadioButton)findViewById(R.id.turnoff_button);
        rb2.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                WifiManager wifi =(WifiManager)getSystemService(Context.WIFI_SERVICE);
                wifi.setWifiEnabled(false);

            }});



    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_wifi_settings, menu);
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
