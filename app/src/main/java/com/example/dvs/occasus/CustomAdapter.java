package com.example.dvs.occasus;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.ToggleButton;

public class CustomAdapter extends ArrayAdapter<String> {

    public CustomAdapter(Context context, String[] toggles) {
        super(context, R.layout.sys_toggle, toggles);
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater toggle_inflater = LayoutInflater.from(getContext());
        View customview = toggle_inflater.inflate(R.layout.sys_toggle, parent, false);

        String single_sys_toggle = getItem(position);
        TextView sys_toggle_text = (TextView) customview.findViewById(R.id.sys_toggle_text);
        ToggleButton sys_toggle_switch = (ToggleButton) customview.findViewById(R.id.sys_toggle_button);

        sys_toggle_text.setText(single_sys_toggle);
        return customview;
    }
}