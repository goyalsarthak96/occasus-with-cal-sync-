package com.example.dvs.occasus;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.ListView;


public class Settings extends ActionBarActivity {

    ListAdapter l_adap;
    ListView list_view;
    String show;
    int position=0;

    public static final String MyPREFERENCES = "MyPrefs";
    SharedPreferences sharedpreferences;
    SharedPreferences.Editor editor;

    String[] settings_options={"Alarm only","Alarm and Notification","Notification only"};


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        sharedpreferences = getBaseContext().getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        editor = sharedpreferences.edit();

        show=sharedpreferences.getString("notif_alarm", "Alarm and Notification");

        list_view=(ListView) findViewById(R.id.settings_listView);


        String[] settings_selected = {show};
        l_adap=new settings_adapter(this,settings_selected);
        list_view.setAdapter(l_adap);


        list_view.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(position==0)
                {
                    showDialog(0);
                }
            }
        });

    }


    @Override
    protected Dialog onCreateDialog(int id) {

        if(id==0)
        {
            return new AlertDialog.Builder(this)
                    .setTitle("Remind By...")
                    .setIcon(R.drawable.ic_launcher)
                    .setSingleChoiceItems(settings_options, position, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                            editor.putString("notif_alarm", settings_options[which]);
                            editor.commit();

                        }
                    })
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Intent intent = new Intent(Settings.this, Settings.class);
                            startActivity(intent);
                        }
                    }).create();
        }
        return null;
    }


    @Override
    public void onBackPressed() {
        Intent intent = new Intent(this,MainActivity.class);
        startActivity(intent);
    }
}
