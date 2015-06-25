package com.example.dvs.occasus;


import android.app.Activity;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.InputType;
import android.text.Spanned;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.support.v4.app.Fragment;
import android.widget.EditText;


public class rep_daily extends Fragment {

    EditText no_of_days_daily_repeat;

    rep_dailylistener actioncommander;

    public interface rep_dailylistener
    {
             void getdata_daily(String number);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            actioncommander=(rep_dailylistener)activity;
        }catch (ClassCastException e)
        {
            throw new ClassCastException(activity.toString());
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.rep_daily, container, false);

        no_of_days_daily_repeat=(EditText)view.findViewById(R.id.daily_editText);

        no_of_days_daily_repeat.setRawInputType(InputType.TYPE_CLASS_NUMBER);

        InputFilter filter=new InputFilter() {
            @Override
            public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
                for(int i=start;i<end;i++)
                {
                    if((source.charAt(i)=='.')||(source.charAt(i)==' ')||(source.charAt(i)=='-')||
                            (source.charAt(i)==',')||(source.charAt(i)==10))
                    {
                        return "";
                    }
                    else if(source.charAt(i)=='0')
                    {
                        if(no_of_days_daily_repeat.getText().toString().equals(""))
                            return "1";
                        else
                            return "0";
                    }
                }
                return null;
            }
        };

        no_of_days_daily_repeat.setFilters(new InputFilter[]{filter});


        return view;
    }

    public void getdata1()
    {
        actioncommander.getdata_daily(no_of_days_daily_repeat.getText().toString());
    }



}
