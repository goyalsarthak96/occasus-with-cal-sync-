package com.example.dvs.occasus;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Toast;




public class MainActivity extends ActionBarActivity {

    private static int i;
    ListView systemtogglelist;
     public String req_name="lklj";
    ListAdapter toggle_adapter;
    Context context;
    Intent intent;
    CharSequence[] items = { " Edit", "Delete","View"};
    @Override

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        DBAdapter db1= new DBAdapter(this);
        db1.open();




        systemtogglelist = (ListView) findViewById(R.id.event_Listview);


        i=0;
        req_name="jlklk";
         intent= new Intent(this,CreateEvent.class);

        context= MainActivity.this;

        DBAdapter db = new DBAdapter(this);
        db.open();


        Cursor c1 = db.getAllEventsDetails();
//all events retrieved
        if (c1.moveToFirst()) {
            do {
                i++;
            } while (c1.moveToNext());
        }
        Cursor c = db.getAllEventsDetails();
        String[] toggle1=new String[i];
        i=0;
        if (c.moveToFirst()) {
            do {
                //all events name stored in toggle1[]
                toggle1[i++] = c.getString(c.getColumnIndex("event_name"));
            } while (c.moveToNext());
        }
        toggle_adapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1 ,toggle1);


        if(i!=0)
        {
            //list of event names shown
            systemtogglelist.setAdapter(toggle_adapter);
        }
        db.close();


        //on click listener for the event list
        systemtogglelist.setOnItemClickListener(
                new AdapterView.OnItemClickListener() {
                    @Override

                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        //req_name contains the name of the event which is clicked
                        req_name= String.valueOf(parent.getItemAtPosition(position));
                        Toast.makeText(getBaseContext(),req_name,Toast.LENGTH_SHORT).show();
                        //shows the edit/delete dialog box
                        showDialog(0);

                    }
                }
        );

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
    protected Dialog onCreateDialog(int id){


        switch (id) {
            case 0:
                //dialog box for edit/delete
                return new AlertDialog.Builder(this)
                        .setIcon(R.drawable.ic_launcher)
                        .setTitle("This is a dialog with some simple text")


                                .setSingleChoiceItems(items, -1,
                                        new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {

                                                //if edit is clicked
                                                if (which == 0) {

                                                    //the name of the event clicked sent to create_event class through req_name
                                                     Intent intent= new Intent(MainActivity.this,CreateEvent.class);
                                                intent.putExtra("der_name",req_name);
                                                intent.putExtra("flag",1);
                                                startActivity(intent);

                                                 //if delete clicked
                                                } else if (which == 1)
                                                 {
                                                     //dailog box showing warning
                                                    showDialog(1);

                                                }

                                                //if view clicked
                                                else if(which==2)
                                                {
                                                    //Toast.makeText(getBaseContext(),"view clicked",Toast.LENGTH_SHORT).show();

                                                    Intent intent= new Intent(MainActivity.this,Show_details.class);
                                                    intent.putExtra("name",req_name);
                                                    startActivity(intent);

                                                }
                                                /*Toast.makeText(getBaseContext(),
                                                        items[which] + ("checked!"),
                                                        Toast.LENGTH_SHORT).show();*/
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
                                       // Toast.makeText(getBaseContext(),
                                         //       "NO clicked!", Toast.LENGTH_SHORT).show();


                                    }
                                }
                        )

                        .setPositiveButton("Yes",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int whichButton) {
                                       // Toast.makeText(getBaseContext(),
                                         //       "YES clicked!", Toast.LENGTH_SHORT).show();


                                        DBAdapter db = new DBAdapter(context);

                                        //deletes the event
                                        db.deleteEvent(req_name);






                                        //brings back to mainactivity screen and refreshes the event list
                                        Intent intent = new Intent(MainActivity.this,MainActivity.class);
                                       startActivity(intent);

                                    }
                                }
                        ).create();

        }
        return null;

    }










    //calls createEvent activity when new event is created
    public void create_event(View view){

        //flag=0 means new event is getting creating....we are not editing a existing event
        intent.putExtra("flag",0);
        intent.putExtra("der_name",req_name);
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
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
