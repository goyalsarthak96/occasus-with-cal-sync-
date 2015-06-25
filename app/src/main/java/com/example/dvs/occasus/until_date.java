package com.example.dvs.occasus;



import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;


public class until_date extends Fragment {

    EditText e;
    Date date;
    SimpleDateFormat dateFormat;

    until_datelistener actioncommander;
    Calendar myCalendar;




    public interface until_datelistener{
        void showDatePickerDialog();
         void getdate(String date);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try{
            actioncommander=(until_datelistener)activity;
        }catch (ClassCastException e)
        {
            throw new ClassCastException(activity.toString());
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.until_date,container,false);

        e=(EditText)view.findViewById(R.id.until_date);



        e.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                actioncommander.showDatePickerDialog();

            }
        });


        myCalendar=Calendar.getInstance();
        dateFormat= new SimpleDateFormat("dd/MM/yyyy");
        date = new Date(myCalendar.get(Calendar.YEAR)-1900,myCalendar.get(Calendar.MONTH),myCalendar.get(Calendar.DAY_OF_MONTH),0,0);
        String cur_date=dateFormat.format(date);
        e.setText(cur_date);


        return view;
    }


    /*public void showDatePickerDialog(View v)
    {
        DialogFragment newFragment = new datepickerfragment();
        newFragment.show(getFragmentManager(), "datePicker");
    }*/

    public void setedit(String s,Context context)
    {
        myCalendar=Calendar.getInstance();
        dateFormat= new SimpleDateFormat("dd/MM/yyyy");
        date = new Date(myCalendar.get(Calendar.YEAR)-1900,myCalendar.get(Calendar.MONTH),myCalendar.get(Calendar.DAY_OF_MONTH),0,0);
        String cur_date=dateFormat.format(date);
        int y=date_identifier1(cur_date,s);
        if(y==2)
            Toast.makeText(context,"Date entered has already passed",Toast.LENGTH_SHORT).show();
        else
        e.setText(s);
    }

    public void getdata1()
    {
          actioncommander.getdate(e.getText().toString());
    }

    //returns 3 if equal
    //returns 1 if date1<date2
    //returns2 if date1>date2
    public int date_identifier1(String date1,String date2) {

        int asci1 = 0, asci2 = 0, as;
        int i;
        for (i = 6; i <= 9; i++)
        {

            char ch = date1.charAt(i);

            as = (int) ch;
            as = as - 48;
            int j;
            j = (int) Math.pow(10, 14 - i);
            as = as * j;
            asci1 = asci1 + as;

        }


        char ch = date1.charAt(3);

        as = (int) ch;
        as = as - 48;
        int j;
        j = (int) Math.pow(10, 3);
        as = as * j;
        asci1 = asci1 + as;

        ch = date1.charAt(4);

        as = (int) ch;
        as = as - 48;

        j = (int) Math.pow(10, 2);
        as = as * j;
        asci1 = asci1 + as;

        ch = date1.charAt(0);

        as = (int) ch;
        as = as - 48;

        j = (int) Math.pow(10, 1);
        as = as * j;
        asci1 = asci1 + as;

        ch = date1.charAt(1);

        as = (int) ch;
        as = as - 48;

        j = (int) Math.pow(10, 0);
        as = as * j;
        asci1 = asci1 + as;

        for (i = 6; i <= 9; i++)
        {

            ch = date2.charAt(i);

            as = (int) ch;
            as = as - 48;

            j = (int) Math.pow(10, 14 - i);
            as = as * j;
            asci2 = asci2 + as;

        }


        ch = date2.charAt(3);

        as = (int) ch;
        as = as - 48;

        j = (int) Math.pow(10, 3);
        as = as * j;
        asci2 = asci2 + as;

        ch = date2.charAt(4);

        as = (int) ch;
        as = as - 48;

        j = (int) Math.pow(10, 2);
        as = as * j;
        asci2 = asci2+ as;

        ch = date2.charAt(0);

        as = (int) ch;
        as = as - 48;

        j = (int) Math.pow(10, 1);
        as = as * j;
        asci2 = asci2 + as;

        ch = date2.charAt(1);

        as = (int) ch;
        as = as - 48;

        j = (int) Math.pow(10, 0);
        as = as * j;
        asci2 = asci2 + as;

        if(asci1==asci2)
            return 3;
        else if(asci1<asci2)
            return 1;
        else
            return 2;

    }


}
