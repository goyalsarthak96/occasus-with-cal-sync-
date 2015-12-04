package com.example.dvs.occasus;


import android.app.ActionBar;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;


public class settings_adapter extends ArrayAdapter<String> {

    public settings_adapter(Context context, String options[])
        {
        super(context,R.layout.settings_dialog, options);
        }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater list_inflater = LayoutInflater.from(getContext());

        View customView = list_inflater.inflate(R.layout.settings_dialog, parent, false);

        String singlefooditem = getItem(position);
        TextView custom_text = (TextView) customView.findViewById(R.id.setting_text);
        custom_text.setText(singlefooditem);

        TextView custom_head = (TextView) customView.findViewById(R.id.setting_heading);
        if (position == 0) {
            if ((singlefooditem.substring(0, 3).equals("Ala")) || (singlefooditem.substring(0, 3).equals("Not"))) {
                custom_head.setText("Remind By");
            } else {
                custom_head.setText("Synced Calendars");
            }
        }
        else if(position==1){
            if((singlefooditem.charAt(0)=='M')||(singlefooditem.charAt(0)=='A'))
                custom_head.setText("Sync");
            else
                custom_head.setText("Profile");
        }
        else
        {
            custom_head.setText("Sync Interval");
        }
        return customView;
        }
}
