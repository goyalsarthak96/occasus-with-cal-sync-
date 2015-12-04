package com.example.dvs.occasus;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;


import android.app.ActionBar;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;


public class default_profile_adapter extends ArrayAdapter<String> {

    public default_profile_adapter(Context context, String options[])
    {
        super(context,R.layout.default_profile_adapter, options);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater list_inflater = LayoutInflater.from(getContext());

        View customView = list_inflater.inflate(R.layout.default_profile_adapter, parent, false);

        String singlefooditem = getItem(position);
        TextView custom_text = (TextView) customView.findViewById(R.id.profile_type);
        custom_text.setText(singlefooditem);

        ImageView bluetooth = (ImageView) customView.findViewById(R.id.default_bluetooth);
        ImageView wifi = (ImageView) customView.findViewById(R.id.default_wifi);
        ImageView mobile_data = (ImageView) customView.findViewById(R.id.default_mobiledata);
        ImageView profile = (ImageView) customView.findViewById(R.id.default_profile);
        if (position == 0) {
            bluetooth.setImageResource(R.drawable.default_bluetooth);
            wifi.setImageResource(R.drawable.default_wifi);
            mobile_data.setImageResource(R.drawable.default_mobiledata);
            profile.setImageResource(R.drawable.default_silent);
        }
        else if(position==1){
            bluetooth.setVisibility(View.INVISIBLE);
            wifi.setVisibility(View.INVISIBLE);
            mobile_data.setVisibility(View.INVISIBLE);
            profile.setVisibility(View.INVISIBLE);
        }

        return customView;
    }
}

