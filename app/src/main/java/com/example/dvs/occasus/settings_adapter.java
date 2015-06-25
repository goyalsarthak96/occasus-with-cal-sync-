package com.example.dvs.occasus;


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
        LayoutInflater list_inflater= LayoutInflater.from(getContext());
        View customView= list_inflater.inflate(R.layout.settings_dialog, parent, false);

        String singlefooditem= getItem(position);
        TextView custom_text=(TextView)customView.findViewById(R.id.setting_text);
        
        custom_text.setText(singlefooditem);
        
        return customView;
        }
}
