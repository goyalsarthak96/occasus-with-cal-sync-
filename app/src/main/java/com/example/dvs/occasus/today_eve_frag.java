package com.example.dvs.occasus;


import android.app.Activity;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class today_eve_frag extends Fragment {

    ListAdapter adap1;
    int today_eve_no=0;
    String req_name;
    int clicked_id;



    today_eve_frag_listener actioncommander;

    public interface today_eve_frag_listener
    {
        void delete_event_today(String req_name1,int clicked_id1);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            actioncommander=(today_eve_frag_listener)activity;
        }catch (ClassCastException e)
        {
            throw new ClassCastException(activity.toString());
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.today_eve_layout, container, false);



        Calendar cal=Calendar.getInstance();
        String today_date;
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        java.util.Date date4 = new java.util.Date(cal.get(Calendar.YEAR)-1900,cal.get(Calendar.MONTH),
                cal.get(Calendar.DAY_OF_MONTH),0,0);
        today_date=sdf.format(date4);


        ListView today_list=(ListView) view.findViewById(R.id.today_list);


        DBAdapter db=new DBAdapter(view.getContext());
        db.open();
        Cursor c=db.getAllEventsDetails();
        c.moveToFirst();

        if(c.getCount()>0)
        {
            do {
                if (c.getString(c.getColumnIndex("next_date")).equals(today_date)) {
                    today_eve_no++;
                }
            } while (c.moveToNext());

            String[] today_eve_name_list = new String[today_eve_no];
            final int[] today_eve_id_list = new int[today_eve_no];
            int i = 0;
            c.moveToFirst();
            do {

                if (c.getString(c.getColumnIndex("next_date")).equals(today_date)) {
                    today_eve_name_list[i] = c.getString(c.getColumnIndex("event_name"));
                    today_eve_id_list[i] = c.getInt(c.getColumnIndex("_id"));
                    i++;
                }
            } while (c.moveToNext());

            db.close();


            adap1 = new ArrayAdapter<String>(view.getContext(), android.R.layout.simple_list_item_1, today_eve_name_list);


            today_list.setAdapter(adap1);



            today_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                    req_name = String.valueOf(parent.getItemAtPosition(position));
                    clicked_id = today_eve_id_list[position];
                    actioncommander.delete_event_today(req_name,clicked_id);
                }
            });

        }

        return view;
    }
}
