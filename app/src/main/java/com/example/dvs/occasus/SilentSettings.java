package com.example.dvs.occasus;

import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.RadioButton;


public class SilentSettings extends ActionBarActivity {
    RadioButton ring,vibrate,silent;

    //declaring the audio manager
    AudioManager MyAudioManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_silent_settings);
        Intent intent = getIntent();

       //finding the required buttons in the activity
       silent = (RadioButton)findViewById(R.id.silent_radiobutton);
       ring = (RadioButton)findViewById(R.id.ring_radiobutton);
       vibrate = (RadioButton)findViewById(R.id.vibrate_radioButton);

        //initializing the audio manager
        MyAudioManager = (AudioManager) getBaseContext().getSystemService(Context.AUDIO_SERVICE);

        //on click , setting the required mode of sound - ring,vibrate or silent
        silent.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                MyAudioManager.setRingerMode(AudioManager.RINGER_MODE_SILENT);

        }});

        ring.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                MyAudioManager.setRingerMode(AudioManager.RINGER_MODE_NORMAL);

            }});

        vibrate.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                MyAudioManager.setRingerMode(AudioManager.RINGER_MODE_VIBRATE);

            }});

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_silent_settings, menu);
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
