package com.example.dvs.occasus;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;


public class SetToggles extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set__toggles);
        Intent intent = getIntent();
    }

    //Calls SoundToggles activity
    public void sound_toggles(View view){
        Intent intent = new Intent(this, SoundToggles.class);
        startActivity(intent);
    }

    //Calls ContactException page
    public void contacts_exception(View view){
        Intent intent = new Intent(this, ContactsException.class);
        startActivity(intent);
    }

    //Calls SystemToggles activity
    public void system_toggles(View view){
        Intent intent = new Intent(this, SystemToggles.class);
        startActivity(intent);
    }

    //Calls Send SMS activity
    public void send_message(View view) {
        Intent intent = new Intent(this, SendMessage.class);
        startActivity(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_set__toggles, menu);
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
