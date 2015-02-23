package com.example.dvs.occasus;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.RadioButton;
import android.widget.TextView;

import java.lang.reflect.Field;
import java.lang.reflect.Method;


public class MobileDataSettings extends ActionBarActivity {
    RadioButton on,off;
    TextView t;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

    
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mobile_data_settings);
        Intent intent = getIntent();

        Context context = MobileDataSettings.this;

        final ConnectivityManager conman =
                (ConnectivityManager) context.getSystemService(CONNECTIVITY_SERVICE);


        on = (RadioButton)findViewById(R.id.on_radiobutton);
        off = (RadioButton)findViewById(R.id.off_radiobutton);
        t=(TextView)findViewById(R.id.tv);
        on.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                try {
                    final Class conmanClass = Class.forName(conman.getClass().getName());

                    final Field iConnectivityManagerField = conmanClass.getDeclaredField("mService");
                    if(iConnectivityManagerField==null)
                        t.append("iConnectivityManagerField==null");


                    iConnectivityManagerField.setAccessible(true);

                    final Object iConnectivityManager = iConnectivityManagerField.get(conman);
                    if(iConnectivityManager==null)
                        t.append("iConnectivityManager==null");

                    final Class iConnectivityManagerClass =
                            Class.forName(iConnectivityManager.getClass().getName());

                    final Method setMobileDataEnabledMethod =
                            iConnectivityManagerClass
                                    .getDeclaredMethod("setMobileDataEnabled", Boolean.TYPE);

                    if(setMobileDataEnabledMethod==null)
                        t.append("setMobileDataEnabledMethod==null \n");

                    setMobileDataEnabledMethod.setAccessible(true);
                    setMobileDataEnabledMethod.invoke(iConnectivityManager, true);

                }catch(Exception e){
                    t.setText(e.getMessage());
                }

            }
        });

        off.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                try {
                    final Class conmanClass = Class.forName(conman.getClass().getName());

                    final Field iConnectivityManagerField = conmanClass.getDeclaredField("mService");
                    if(iConnectivityManagerField==null)
                        t.append("iConnectivityManagerField==null");


                    iConnectivityManagerField.setAccessible(true);

                    final Object iConnectivityManager = iConnectivityManagerField.get(conman);
                    if(iConnectivityManager==null)
                        t.append("iConnectivityManager==null");

                    final Class iConnectivityManagerClass =
                            Class.forName(iConnectivityManager.getClass().getName());

                    final Method setMobileDataEnabledMethod =
                            iConnectivityManagerClass
                                    .getDeclaredMethod("setMobileDataEnabled", Boolean.TYPE);

                    if(setMobileDataEnabledMethod==null)
                        t.append("setMobileDataEnabledMethod==null \n");

                    setMobileDataEnabledMethod.setAccessible(true);
                    setMobileDataEnabledMethod.invoke(iConnectivityManager, false);

                }catch(Exception e){
                    t.setText(e.getMessage());
                }

            }
        });


    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_mobile_data_settings, menu);
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
