package com.example.dvs.occasus;

import android.content.Intent;
import android.database.Cursor;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;


public class Show_details extends ActionBarActivity {

    String name1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_details);
        name1=getIntent().getStringExtra("name");
        DBAdapter db = new DBAdapter(this);
        db.open();
        Cursor c=db.getEventsDetail(name1);
        if(c.moveToFirst())
        {
            TextView name= (TextView)findViewById(R.id.name_text);
            name.setText(c.getString(c.getColumnIndex("event_name")));


            TextView desc= (TextView)findViewById(R.id.description_text);
            desc.setText(c.getString(c.getColumnIndex("description")));

            TextView date= (TextView)findViewById(R.id.date_text);
            date.setText(c.getString(c.getColumnIndex("event_date")));

            TextView stime= (TextView)findViewById(R.id.stime_text);
            stime.setText(c.getString(c.getColumnIndex("start_time")));

            TextView etime= (TextView)findViewById(R.id.etime_text);
            etime.setText(c.getString(c.getColumnIndex("end_time")));

            TextView bluetooth= (TextView)findViewById(R.id.bluetooth_text);
            bluetooth.setText(c.getString(c.getColumnIndex("bluetooth")));

            TextView wifi= (TextView)findViewById(R.id.wifi_text);
            wifi.setText(c.getString(c.getColumnIndex("wifi")));

            TextView profile= (TextView)findViewById(R.id.profile_text);
            profile.setText(c.getString(c.getColumnIndex("profile")));

           TextView mobile_data= (TextView)findViewById(R.id.mobile_data_text);
            mobile_data.setText(c.getString(c.getColumnIndex("mobile_data")));
            //Toast.makeText(getBaseContext(),"show_details",Toast.LENGTH_SHORT).show();

        }
        db.close();


    }

    //back button override......sends the app back to mainactivity screen
    @Override
    public void onBackPressed() {
        Intent intent = new Intent(Show_details.this,MainActivity.class);

        startActivity(intent);

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_show_details, menu);
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
