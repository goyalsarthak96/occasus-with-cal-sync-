package com.example.dvs.occasus;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.ConnectivityManager;
import android.net.wifi.WifiManager;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;


import java.lang.reflect.Field;
import java.lang.reflect.Method;


public class MainActivity extends ActionBarActivity {



    ListAdapter eventadapter;
     public String req_name="lklj";
    ListAdapter toggle_adapter;
    Context context;
    Intent intent;
    String[] items = { " Edit", "Delete"," View"};


    int clicked_id;


    public static final String custom_info = "custom_repeat";
    SharedPreferences.Editor custom_editor;
    SharedPreferences custom_sharedpreferences;


    public static final String MyPREFERENCES = "MyPrefs";
    SharedPreferences sharedpreferences;
    //shared preferences editor declared
    SharedPreferences.Editor editor;


    public static final String sync = "sync";
    SharedPreferences.Editor sync_editor;
    SharedPreferences sync_sharedpreferences;

    TextView te;
    Button b;

    @Override

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //to add logo to action bar
        ActionBar ac=getSupportActionBar();
        ac.setDisplayShowHomeEnabled(true);
        ac.setLogo(R.drawable.occasus1);
        ac.setDisplayUseLogoEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);//to hide the back button in action bar



        //initializing shared preferences
        sharedpreferences = getBaseContext().getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        editor = sharedpreferences.edit();



        custom_sharedpreferences = getBaseContext().getSharedPreferences(custom_info, Context.MODE_PRIVATE);
        custom_editor = custom_sharedpreferences.edit();


        sync_sharedpreferences = getSharedPreferences(sync, Context.MODE_PRIVATE);
        sync_editor = sync_sharedpreferences.edit();


        eventadapter = new custom_event_options(this, items);





        te=(TextView) findViewById(R.id.textView);
        te.setVisibility(View.INVISIBLE);

        b=(Button) findViewById(R.id.sync);
        if(sync_sharedpreferences.getString("sync_auto_or_manual","Manual").equals("Automatic"))
            b.setVisibility(View.INVISIBLE);
        else
            b.setVisibility(View.VISIBLE);


        int i;
        i=0;
        req_name="jlklk";
        intent= new Intent(this,CreateEvent.class);
        context= MainActivity.this;



        DBAdapter db = new DBAdapter(this);
        db.open();
        Cursor c1 = db.getAllEventsDetails();//all events retrieved

        if (c1.moveToFirst())
        {
            do
            {
                i++;   // to count how many records are present in database
            } while (c1.moveToNext());
        }


        final int[] id_toggle=new int[i];
        String[] toggle1=new String[i];

        i=0;
        if (c1.moveToFirst())
        {
            do
            {
                int key=c1.getInt(c1.getColumnIndex("_id"));
                id_toggle[i]=key;
                //all events name stored in toggle1[]
                toggle1[i] = c1.getString(c1.getColumnIndex("event_name"));  //toggle array stores the name of all the events
                i++;

            } while (c1.moveToNext());
        }

        toggle_adapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1 ,toggle1);
        //adapter set for showing events list on main screen


        db.close();




    }








    //back button override......the app closes after pressing back button on mainactivity screen
    @Override
    public void onBackPressed() {
        Intent homeIntent = new Intent(Intent.ACTION_MAIN);
        homeIntent.addCategory( Intent.CATEGORY_HOME );
        homeIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(homeIntent);

    }




    //calls createEvent activity when new event is created
    public void create_event(View view){

        //flag=0 means new event is getting creating....we are not editing a existing event
        intent.putExtra("flag",0);
        intent.putExtra("clicked_id",clicked_id);
        custom_editor.putInt("coming_from_custom_repeat",0);
        custom_editor.commit();
        startActivity(intent);
    }




    //calls the contact_exception activity
    public void contacts_exception(View view)
    {
        Intent intent = new Intent(this, ContactsException.class);
        startActivity(intent);
    }




    //calls the send_message activity
    public void send_message(View view)
    {
        Intent intent = new Intent(this, SendMessage.class);
        startActivity(intent);
    }





    public void sync(View view)
    {
        Intent intent=new Intent(this,sync.class);
        startActivity(intent);
    }








    public void quick_silent(View view)
    {
        editor.putInt("quick_silent_running", 1);
        editor.commit();
        Intent intent=new Intent(this,quick_silent.class);
        startActivity(intent);
    }




    public void show_eve(View view)
    {
        Intent intent=new Intent(this,show_events_list.class);
        startActivity(intent);
    }





    //for showing menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }


    //for setting what happens when items in menu are clicked
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.



        switch (item.getItemId())
        {
            case R.id.help://if clicked item is help
                Intent intent = new Intent(this, help.class);
                startActivity(intent);
            break;
            case R.id.action_settings:
                Intent intent1=new Intent(this,Settings.class);
                startActivity(intent1);
                break;

        }
        return super.onOptionsItemSelected(item);


    }
}
