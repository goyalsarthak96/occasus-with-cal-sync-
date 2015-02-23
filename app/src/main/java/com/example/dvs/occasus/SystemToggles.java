package com.example.dvs.occasus;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;


public class SystemToggles extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_system_toggles);
        Intent intent = getIntent();
    }

   //Calls BluetoothSettings Activity
   public void bluetooth_settings(View view){
       Intent intent = new Intent(this, BluetoothSettings.class);
       startActivity(intent);
   }

    //Calls SilentSettings Activity
    public void silent_settings(View view){
        Intent intent = new Intent(this, SilentSettings.class);
        startActivity(intent);
    }

    //Calls ShutdownSettings Activity
    public void shutdown_settings(View view){
        Intent intent = new Intent(this, ShutDownSettings.class);
        startActivity(intent);
    }

    //Calls WifiSettings Activity
    public void wifi_settings(View view){
        Intent intent = new Intent(this, WifiSettings.class);
        startActivity(intent);
    }

    //Calls MobileDataSettings Activity
    public void mobiledata_settings(View view){
        Intent intent = new Intent(this, MobileDataSettings.class);
        startActivity(intent);
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
