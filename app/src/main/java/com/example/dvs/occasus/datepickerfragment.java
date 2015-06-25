package com.example.dvs.occasus;


import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.widget.DatePicker;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class datepickerfragment extends DialogFragment implements DatePickerDialog.OnDateSetListener{


    datepickerfragmentlistener actioncommander;
    Date date;
    SimpleDateFormat dateFormat;

    public interface datepickerfragmentlistener{

        public void editext_setdate(String date);
    }


    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try
        {
            actioncommander=(datepickerfragmentlistener)activity;
        }
        catch (ClassCastException e)
        {
            //throw new ClassCastException(getActivity().toString());
        }
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);

        // Create a new instance of DatePickerDialog and return it
        return new DatePickerDialog(getActivity(), this, year, month, day);
    }

    @Override
    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {

        dateFormat= new SimpleDateFormat("dd/MM/yyyy");
        date = new Date(year-1900,monthOfYear,dayOfMonth,0,0);
        actioncommander.editext_setdate(dateFormat.format(date));

    }

}
