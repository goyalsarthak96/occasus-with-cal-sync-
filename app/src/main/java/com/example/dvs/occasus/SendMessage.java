package com.example.dvs.occasus;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.provider.ContactsContract;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;


public class SendMessage extends ActionBarActivity {


    private static final int PICK_CONTACT = 3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_message);

        //Intent intent = getIntent();

        Button add_contact = (Button)findViewById(R.id.add_contact_button);

        add_contact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
                startActivityForResult(intent, PICK_CONTACT);
            }
        });
    }

    @Override
    public void onActivityResult(int reqCode, int resultCode, Intent data){
        super.onActivityResult(reqCode, resultCode, data);

        String contact_data;

        switch(reqCode){
            case (PICK_CONTACT):
                if (resultCode == Activity.RESULT_OK){
                    Uri contactData = data.getData();
                    contact_data = contactData.toString();
                    /*if (c.moveToFirst()){
                        // other data is available for the Contact.  I have decided
                        //    to only get the name of the Contact.
                        String name = c.getString(c.getColumnIndexOrThrow(Contacts.DISPLAY_NAME));
                        Toast.makeText(getApplicationContext(), name, Toast.LENGTH_SHORT).show();
                    }*/

                }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_send_message, menu);
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
