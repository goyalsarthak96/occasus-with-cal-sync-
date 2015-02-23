package com.example.dvs.occasus;
//yo
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;


public class BluetoothSettings extends ActionBarActivity {
    //shared preferences declared
    SharedPreferences sharedpreferences;

    public static final String on = "off";

    //name of the shared preferences file
    public static final String MyPREFERENCES = "MyPrefs";

    //shared preferences editor declared
    SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bluetooth_settings);
        Intent intent = getIntent();

        //initializing shared preferences
        sharedpreferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);

            //checking the status of the radiobuttons and restoring it with help of shared preferences
            RadioButton on_radiobutton = (RadioButton)findViewById(R.id.yes_button);
            RadioButton off_radiobutton = (RadioButton)findViewById(R.id.no_button);
            String restoredText = sharedpreferences.getString(on,"error");
            if(restoredText.equals("on") == true)
                on_radiobutton.setChecked(true);
            else if(restoredText.equals("on") == false)
                off_radiobutton.setChecked(true);

    }

    //Turns the Bluetooth on
    public void enable(View view){
        BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        //initializing the editor
        editor = sharedpreferences.edit();

        //saving the state of bluetooth in shared preferences
        editor.putString(on, "on");
        editor.commit();

        if (!mBluetoothAdapter.isEnabled()){
            mBluetoothAdapter.enable();
        }
    }


    //Turns the Bluetooth off
    public void disable(View view){
        BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        //initializing the editor
        editor = sharedpreferences.edit();

        //saving the state of bluetooth in shared preferences
        editor.putString(on, "off");
        editor.commit();

        if (mBluetoothAdapter.isEnabled()){
            mBluetoothAdapter.disable();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_bluetooth_settings, menu);
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
