package com.example.dvs.occasus;

import android.content.Context;


import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

class custom_profile_options extends ArrayAdapter<String> {
    public custom_profile_options(Context context, String[] options) {
        super(context,R.layout.custom_layout, options);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater list_inflater= LayoutInflater.from(getContext());
        View customView= list_inflater.inflate(R.layout.custom_layout, parent, false);

        String singlefooditem= getItem(position);
        TextView custom_text=(TextView)customView.findViewById(R.id.custom_text);
        ImageView custom_image=(ImageView)customView.findViewById(R.id.custom_image);
        custom_text.setText(singlefooditem);
        if(position==0)
            custom_image.setImageResource(R.drawable.silent);
        else if(position==1)
            custom_image.setImageResource(R.drawable.ring);
        else if(position==2)
            custom_image.setImageResource(R.drawable.vibrate);
        return customView;
    }
}
