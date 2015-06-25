package com.example.dvs.occasus;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class custom_adap_calendar extends ArrayAdapter<String>{

    String button_color[];
    public custom_adap_calendar(Context context, String options[],String color[])
    {
        super(context,R.layout.custom_adap_cal, options);
        button_color=color;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater list_inflater= LayoutInflater.from(getContext());
        View customView= list_inflater.inflate(R.layout.custom_adap_cal, parent, false);

        String calendar_name= getItem(position);
        TextView cal_name=(TextView)customView.findViewById(R.id.cal_name);
        TextView cal_color=(TextView) customView.findViewById(R.id.cal_color1);
        cal_name.setText(calendar_name);
        cal_color.setBackgroundColor(Integer.valueOf(button_color[position]));
        return customView;
    }
}
