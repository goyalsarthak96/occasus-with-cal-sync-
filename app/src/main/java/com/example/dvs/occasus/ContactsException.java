package com.example.dvs.occasus;



import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;


import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;

import android.app.Activity;
import android.net.Uri;
import android.provider.ContactsContract;
import android.database.Cursor;

import android.graphics.Color;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;


public class ContactsException extends ActionBarActivity {

    private static final int PICK_CONTACT = 3;

    private static int i;

    String id_1 = "na";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contacts_exception);

        //to add logo to action bar
        ActionBar ac=getSupportActionBar();
        ac.setDisplayShowHomeEnabled(true);
        ac.setLogo(R.drawable.occasus1);
        ac.setDisplayUseLogoEnabled(true);

        getSupportActionBar().setDisplayHomeAsUpEnabled(false);

        Intent intent = getIntent();

        Button add_contact = (Button)findViewById(R.id.add_contact);

        Button delete_contact = (Button)findViewById(R.id.delete_contact);

        final ListView toggle_list = (ListView)findViewById(R.id.contacts);

        add_contact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*DBAdapterException db = new DBAdapterException(ContactsException.this);

                int numCount = db.getNumberOfContacts();
                if(numCount < 4) {*/
                    Intent intent = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
                    startActivityForResult(intent, PICK_CONTACT);
                /*}
                else{
                    Toast.makeText(getBaseContext(),"For adding more contacts, get pro version.",Toast.LENGTH_SHORT).show();
                }*/
            }
        });


        delete_contact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(id_1.equals("na")) {
                    Toast.makeText(getBaseContext(),"Please select a contact",Toast.LENGTH_SHORT).show();
                }
                else{
                    DBAdapterException db = new DBAdapterException(ContactsException.this);

                    db.deleteContact(id_1);
                    Intent intent = new Intent(ContactsException.this, ContactsException.class);
                    startActivity(intent);
                }
            }
        });

        i = 0;

        //Declaring strings to store name and id of contacts
        final String[] toggle= new String[100];
        String[] toggle1 = new String[100];
        //Initializing string
        toggle[0]="Hello";
        String check = "";

        //Creating DBAdapterException object
        DBAdapterException db = new DBAdapterException(this);

        //Opening Exception contacts database
        db.open();
        //Getting all contacts
        Cursor c = db.getAllContacts();
        if (c.moveToFirst())
        {
            do {
                toggle[i] = c.getString(c.getColumnIndex("id"));
                toggle1[i] = c.getString(c.getColumnIndex("Name"));
                if(!check.equals(c.getString(c.getColumnIndex("Name")))) {
                    i++;
                }
                check = c.getString(c.getColumnIndex("Name"));
                //DisplayContact(c);
            } while (c.moveToNext());
        }

        //Setting toggle2 length to tbe number of elements
        String[] toggle2 = new String[i];
        int j;

        for(j = 0; j < i; j++) toggle2[j] = toggle1[j];


        if(i!=0){
            //Set list view items to contact names and numbers
            ListAdapter toggle_adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, toggle2);
            toggle_list.setAdapter(toggle_adapter);
            setListViewHeightBasedOnChildren(toggle_list);
        }


        //OnClickListener on listview to change its color on touch
        toggle_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                int color = Color.TRANSPARENT;
                Drawable background = parent.getChildAt(position).getBackground();
                if (background instanceof ColorDrawable)
                    color = ((ColorDrawable) background).getColor();

                if(color == Color.rgb(22,17,81))
                    parent.getChildAt(position).setBackgroundColor(Color.TRANSPARENT);
                else
                    parent.getChildAt(position).setBackgroundColor(Color.rgb(22,17,81));

                id_1 = toggle[position];
            }
        });

        //Close DBAdaper object
        db.close();
    }




    //back button override......the app closes after pressing back button on mainactivity screen
    @Override
    public void onBackPressed() {
        Intent intent= new Intent(ContactsException.this,MainActivity.class);
        startActivity(intent);

    }

//Method to change the scrolling from list view to entire activity
    public static void setListViewHeightBasedOnChildren(ListView listView) {
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


//Activity for selecting a contact from the contacts page and returning it to this activity
    @Override
    public void onActivityResult(int reqCode, int resultCode, Intent
            data) {
        super.onActivityResult(reqCode, resultCode, data);

        switch (reqCode) {
            case (PICK_CONTACT):
                if (resultCode == Activity.RESULT_OK) {
                    Uri contactData = data.getData();
                    //Getting contact data through cursor
                    Cursor c = getContentResolver().query(contactData, null, null, null, null);
                    if (c.moveToFirst()) {

                        String name = c.getString(c.getColumnIndexOrThrow(ContactsContract.Contacts.DISPLAY_NAME));

                        String id = c.getString(c.getColumnIndexOrThrow(ContactsContract.Contacts._ID));

                        String hasPhone = c.getString(c.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER));

                        String cNumber = "";

                        List<String> allNumbers = new ArrayList<String>();

                        int phoneIdx = 0;

                        if (hasPhone.equalsIgnoreCase("1")) {
                            Cursor phones = getContentResolver().query(
                                    ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,
                                    ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = " + id,
                                    null, null);
                            phoneIdx = phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DATA);
                            //phones.moveToFirst()

                            int ans = 0;
                            if (phones.moveToFirst()) {
                                while (!phones.isAfterLast()) {
                                    cNumber = phones.getString(phoneIdx);
                                    Toast.makeText(getBaseContext(),cNumber,Toast.LENGTH_SHORT).show();
                                    allNumbers.add(cNumber);
                                    ans++;
                                    phones.moveToNext();
                                }
                            }

                            final CharSequence[] items = allNumbers.toArray(new String[allNumbers.size()]);
                            /*AlertDialog.Builder builder = new AlertDialog.Builder(ContactsException.this);
                            builder.setTitle("Choose a number");
                            builder.setItems(items, new DialogInterface.OnClickListener() {

                                @Override
                                public void onClick(DialogInterface dialog, int item) {
                                    String selectedNumber = items[item].toString();
                                    selectedNumber = selectedNumber.replace("-", "");
                                    selectedNumber = selectedNumber.replace(" ", "");

                                    PreferenceManager.getDefaultSharedPreferences(getBaseContext()).edit().putString("selected number", selectedNumber).commit();
                                }
                            });
                            AlertDialog alert = builder.create();



                            //Toast.makeText(getBaseContext(),allNumbers.size(),Toast.LENGTH_SHORT).show();
                            if(allNumbers.size() > 1) {
                                alert.show();
                                selectedNumber = PreferenceManager.getDefaultSharedPreferences(getBaseContext()).getString("selected number", "");
                            } else {
                            String selectedNumber = "";
                            selectedNumber = cNumber;
                            selectedNumber = selectedNumber.replace("-", "");
                            selectedNumber = selectedNumber.replace(" ", "");*/

                            //cNumber = phones.getString(phones.getColumnIndex("data1"));
                            //String Number = selectedNumber.replaceAll(" ", "");

                            DBAdapterException db = new DBAdapterException(this);

                            db.open();
                            for(int i = 0; i < ans; i++) {
                                String selectedNumber = items[i].toString();
                                selectedNumber = selectedNumber.replace("-", "");
                                selectedNumber = selectedNumber.replace(" ", "");
                                db.insertContact(name, id + "a" + i, selectedNumber);
                            }
                            db.close();

                            Intent intent = new Intent(ContactsException.this,ContactsException.class);
                            startActivity(intent);
                        }
                    }
                }
        }

    }

}