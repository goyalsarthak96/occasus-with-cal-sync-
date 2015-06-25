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
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;


import java.lang.reflect.Field;
import java.lang.reflect.Method;


public class MainActivity extends ActionBarActivity {


    ListView systemtogglelist;
    ListAdapter eventadapter;
     public String req_name="lklj";
    ListAdapter toggle_adapter;
    Context context;
    Intent intent;
    String[] items = { " Edit", "Delete"," View"};

    int notificationID = 2;
    int clicked_id;


    public static final String custom_info = "custom_repeat";
    SharedPreferences.Editor custom_editor;
    SharedPreferences custom_sharedpreferences;


    public static final String MyPREFERENCES = "MyPrefs";
    SharedPreferences sharedpreferences;
    //shared preferences editor declared
    SharedPreferences.Editor editor;

    TextView te;

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


        eventadapter = new custom_event_options(this, items);

        systemtogglelist = (ListView) findViewById(R.id.event_Listview);


        systemtogglelist.setVisibility(View.INVISIBLE);
        te=(TextView) findViewById(R.id.textView);
        te.setVisibility(View.INVISIBLE);


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


        if(i!=0)
        {
            //list of event names shown
            systemtogglelist.setAdapter(toggle_adapter);

            //adding list view to scrollview creates some problem which is solved by following line
            setListViewHeightBasedOnChildren(systemtogglelist);
            //setListViewHeightBasedOnChilderen function overcomes the problem regarding listview in scrollview
        }
        db.close();



        //on click listener for the event list
        systemtogglelist.setOnItemClickListener(
                new AdapterView.OnItemClickListener() {
                    @Override

                    public void onItemClick(AdapterView<?> parent, View view, int position, long id)
                    {
                        //req_name contains the name of the event which is clicked
                        req_name= String.valueOf(parent.getItemAtPosition(position));
                        clicked_id=id_toggle[position];
                        //shows the edit/delete dialog box
                        showDialog(0);
                    }
                }
        );
    }



//to overcome the issue of listview in scrollview
    public static void setListViewHeightBasedOnChildren(ListView listView)
    {
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null)
            return;

        int desiredWidth = View.MeasureSpec.makeMeasureSpec(listView.getWidth(), View.MeasureSpec.UNSPECIFIED);
        int totalHeight = 0;
        View view = null;
        for (int i = 0; i < listAdapter.getCount(); i++) {
            view = listAdapter.getView(i, view, listView);
            if (i == 0)
                view.setLayoutParams(new ViewGroup.LayoutParams(desiredWidth, AbsListView.LayoutParams.WRAP_CONTENT));

            view.measure(desiredWidth, View.MeasureSpec.UNSPECIFIED);
            totalHeight += view.getMeasuredHeight();
        }
        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
        listView.setLayoutParams(params);
        listView.requestLayout();
    }





    //back button override......the app closes after pressing back button on mainactivity screen
    @Override
    public void onBackPressed() {
        Intent homeIntent = new Intent(Intent.ACTION_MAIN);
        homeIntent.addCategory( Intent.CATEGORY_HOME );
        homeIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(homeIntent);

    }



    @Override
    protected Dialog onCreateDialog(int id)
    {
        switch (id)
        {
            case 0:
                //dialog box for edit/delete
                return new AlertDialog.Builder(this)
                        .setIcon(R.drawable.ic_launcher)
                        .setTitle("Select one of the options")
                        .setAdapter(eventadapter, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        //if edit is clicked
                                        if (which == 0) {

                                            //the name of the event clicked sent to create_event class through req_name
                                            Intent intent = new Intent(MainActivity.this, CreateEvent.class);
                                            intent.putExtra("clicked_id", clicked_id);
                                            intent.putExtra("flag", 1);

                                            custom_editor.putInt("coming_from_custom_repeat", 0);
                                            custom_editor.commit();
                                            startActivity(intent);
                                            //if delete clicked
                                        } else if (which == 1) {
                                            //dailog box showing warning
                                            showDialog(1);
                                        }

                                        //if view clicked
                                        else if (which == 2) {
                                            Intent intent = new Intent(MainActivity.this, Show_details.class);
                                            intent.putExtra("clicked_id", clicked_id);


                                            startActivity(intent);
                                        }

                                    }
                                }

                        ).create();

            case 1:
                //dialog box for delete warning
                return new AlertDialog.Builder(this)
                        .setIcon(R.drawable.ic_launcher)
                        .setTitle("Are You Sure You Want To Delete")

                        .setNegativeButton("No",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int whichButton) {

                                    }
                                }
                        )

                        //event has to be deleted...(event is supposed to end as soon as we delete if it was running at the time of delettion
                        .setPositiveButton("Yes",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int whichButton)
                                    {


                                        DBAdapter db = new DBAdapter(context);
                                        db.open();

                                        if(context.getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE).getInt("event_running", 6) == 1)
                                        //if some event was running when it was deleted
                                        {
                                        int running_id = context.getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE).getInt("running_id", 0);


                                            if (clicked_id==running_id)
                                        //if running event has same id as event to be deleted
                                        {


                                            final MediaPlayer mp = MediaPlayer.create(context, R.raw.notification);
                                            mp.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                                                @Override
                                                public void onCompletion(MediaPlayer mp) {
                                                    // TODO Auto-generated method stub
                                                    mp.reset();
                                                    mp.release();
                                                    mp = null;
                                                }
                                            });


                                            if(sharedpreferences.getString("notif_alarm","Alarm and Notification")
                                                    .equals("Alarm only"))
                                            {
                                                mp.start();
                                            }
                                            else if(sharedpreferences.getString("notif_alarm","Alarm and Notification")
                                                    .equals("Alarm and Notification"))
                                            {
                                                displayNotification();//denoting end of the event
                                                mp.start();
                                            }
                                            else
                                            {
                                                displayNotification();//denoting end of the event
                                            }



                                            editor.putInt("event_running", 0);//make event running 0 since no event is running now
                                            editor.commit();





                                            //revert phone to settings which were before event started
                                            int bluetooth_state = context.getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE).getInt("bluetooth_state", 6);
                                            int wifi_state = context.getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE).getInt("wifi_state", 6);

                                            int mobiledata_state = context.getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE).getInt("mobiledata_state", 6);
                                            int profile_state = context.getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE).getInt("profile_state", 6);
                                            BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
                                            if (bluetooth_state == 1) {
                                                mBluetoothAdapter.enable();
                                            } else {
                                                mBluetoothAdapter.disable();
                                            }


                                            WifiManager wifi;
                                            wifi = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
                                            if (wifi_state == 1) {
                                                wifi.setWifiEnabled(true);
                                            } else {
                                                wifi.setWifiEnabled(false);
                                            }


                                            final ConnectivityManager conman =
                                                    (ConnectivityManager) context.getSystemService(context.CONNECTIVITY_SERVICE);

                                            if (mobiledata_state == 1) {

                                                try {
                                                    final Class conmanClass = Class.forName(conman.getClass().getName());

                                                    final Field iConnectivityManagerField = conmanClass.getDeclaredField("mService");


                                                    iConnectivityManagerField.setAccessible(true);

                                                    final Object iConnectivityManager = iConnectivityManagerField.get(conman);


                                                    final Class iConnectivityManagerClass =
                                                            Class.forName(iConnectivityManager.getClass().getName());

                                                    final Method setMobileDataEnabledMethod =
                                                            iConnectivityManagerClass
                                                                    .getDeclaredMethod("setMobileDataEnabled", Boolean.TYPE);


                                                    setMobileDataEnabledMethod.setAccessible(true);
                                                    setMobileDataEnabledMethod.invoke(iConnectivityManager, true);


                                                } catch (Exception e) {
                                                    e.printStackTrace();
                                                }

                                            } else {

                                                try {
                                                    final Class conmanClass = Class.forName(conman.getClass().getName());

                                                    final Field iConnectivityManagerField = conmanClass.getDeclaredField("mService");


                                                    iConnectivityManagerField.setAccessible(true);

                                                    final Object iConnectivityManager = iConnectivityManagerField.get(conman);


                                                    final Class iConnectivityManagerClass =
                                                            Class.forName(iConnectivityManager.getClass().getName());

                                                    final Method setMobileDataEnabledMethod =
                                                            iConnectivityManagerClass
                                                                    .getDeclaredMethod("setMobileDataEnabled", Boolean.TYPE);


                                                    setMobileDataEnabledMethod.setAccessible(true);
                                                    setMobileDataEnabledMethod.invoke(iConnectivityManager, false);

                                                } catch (Exception e) {
                                                    e.printStackTrace();
                                                }
                                            }


                                            AudioManager MyAudioManager;

                                            MyAudioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
                                            if (profile_state == 1)
                                            {
                                                MyAudioManager.setRingerMode(AudioManager.RINGER_MODE_SILENT);
                                            }
                                            else if (profile_state == 3)
                                            {
                                                MyAudioManager.setRingerMode(AudioManager.RINGER_MODE_NORMAL);
                                            }
                                            else
                                            {
                                                MyAudioManager.setRingerMode(AudioManager.RINGER_MODE_VIBRATE);
                                            }


                                        }
                                        }
                                        db.close();
                                        //deletes the event
                                        db.deleteEvent(clicked_id);


                                        //brings back to mainactivity screen and refreshes the event list
                                        Intent intent = new Intent(MainActivity.this, MainActivity.class);
                                        startActivity(intent);

                                    }
                                }
                        ).create();

        }
        return null;

    }


    //displays notification that event has ended
    public void displayNotification(){
        Intent i = new Intent(getBaseContext(), NotificationView.class);
        i.putExtra("notificationID",notificationID);

        PendingIntent pendingIntent = PendingIntent.getActivity(getBaseContext(),0,i,0);

        NotificationManager nm = (NotificationManager)getBaseContext().getSystemService(getBaseContext().NOTIFICATION_SERVICE);

        Notification notif = new Notification(
                R.drawable.occasus1,"Event "+req_name+" ends",System.currentTimeMillis()
        );

        CharSequence from = "Occasus";
        CharSequence message = "Event "+req_name+" ends";

        notif.setLatestEventInfo(getBaseContext(),from,message,pendingIntent);
        nm.notify(notificationID,notif);
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
