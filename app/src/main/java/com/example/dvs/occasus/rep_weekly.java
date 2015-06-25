package com.example.dvs.occasus;


import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.InputFilter;
import android.text.InputType;
import android.text.Spanned;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ToggleButton;

import java.util.Calendar;

public class rep_weekly extends Fragment {

    EditText no_of_week_repeat;
    ToggleButton mon,tue,wed,thu,fri,sat,sun;
    int mon_check,tue_check,wed_check,thu_check,fri_check,sat_check,sun_check;


    public static final String custom_info = "custom_repeat";
    SharedPreferences.Editor custom_editor;
    SharedPreferences custom_sharedpreferences;


    rep_weeklylistener actioncommander;

    public interface rep_weeklylistener
    {
         void getdata_weekly(String number,int mon,int tue,int wed,int thu,int fri,int sat,int sun);
        void hide_done();
        void show_done();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            actioncommander=(rep_weeklylistener)activity;
        }catch (ClassCastException e)
        {
            throw new ClassCastException(activity.toString());
        }
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.rep_weekly,container,false);
        no_of_week_repeat=(EditText)view.findViewById(R.id.weekly_editText);

        no_of_week_repeat.setRawInputType(InputType.TYPE_CLASS_NUMBER);

        custom_sharedpreferences = getActivity().getBaseContext().getSharedPreferences(custom_info, Context.MODE_PRIVATE);



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
                        if(no_of_week_repeat.getText().toString().equals(""))
                            return "1";
                        else
                            return "0";
                    }
                }
                return null;
            }
        };

        no_of_week_repeat.setFilters(new InputFilter[]{filter});

        mon=(ToggleButton) view.findViewById(R.id.mon);
        tue=(ToggleButton) view.findViewById(R.id.tue);
        wed=(ToggleButton) view.findViewById(R.id.wed);
        thu=(ToggleButton) view.findViewById(R.id.thu);
        fri=(ToggleButton) view.findViewById(R.id.fri);
        sat=(ToggleButton) view.findViewById(R.id.sat);
        sun=(ToggleButton) view.findViewById(R.id.sun);




        Calendar calnow=Calendar.getInstance();
        if(custom_sharedpreferences.getInt("start_date_set",0)==1) {
            calnow.set(Calendar.DAY_OF_MONTH, Integer.valueOf(custom_sharedpreferences.getString("start_date", null).substring(0,2)));
            calnow.set(Calendar.MONTH,Integer.valueOf(custom_sharedpreferences.getString("start_date", null).substring(3,5))-1);
            calnow.set(Calendar.YEAR,Integer.valueOf(custom_sharedpreferences.getString("start_date", null).substring(6,10)));
        }


        switch((calnow.get(Calendar.DAY_OF_WEEK)-1))
        {
            case 0:sun.setChecked(true);

                break;
            case 1:mon.setChecked(true);

                break;
            case 2:tue.setChecked(true);

                break;
            case 3:wed.setChecked(true);

                break;
            case 4:thu.setChecked(true);

                break;
            case 5:fri.setChecked(true);

                break;
            case 6:sat.setChecked(true);

                break;
        }




        mon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if((!mon.isChecked())&&(!tue.isChecked())&&(!wed.isChecked())&&(!thu.isChecked())&&(!fri.isChecked())&&
                        (!sat.isChecked())&&(!sun.isChecked()))
                {
                    actioncommander.hide_done();
                }
                else
                {
                    actioncommander.show_done();
                }
            }
        });

        tue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if((!mon.isChecked())&&(!tue.isChecked())&&(!wed.isChecked())&&(!thu.isChecked())&&(!fri.isChecked())&&
                        (!sat.isChecked())&&(!sun.isChecked()))
                {
                    actioncommander.hide_done();
                }
                else
                {
                    actioncommander.show_done();
                }
            }
        });

        wed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if((!mon.isChecked())&&(!tue.isChecked())&&(!wed.isChecked())&&(!thu.isChecked())&&(!fri.isChecked())&&
                        (!sat.isChecked())&&(!sun.isChecked()))
                {
                    actioncommander.hide_done();
                }
                else
                {
                    actioncommander.show_done();
                }
            }
        });

        thu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if((!mon.isChecked())&&(!tue.isChecked())&&(!wed.isChecked())&&(!thu.isChecked())&&(!fri.isChecked())&&
                        (!sat.isChecked())&&(!sun.isChecked()))
                {
                    actioncommander.hide_done();
                }
                else
                {
                    actioncommander.show_done();
                }
            }
        });

        fri.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if((!mon.isChecked())&&(!tue.isChecked())&&(!wed.isChecked())&&(!thu.isChecked())&&(!fri.isChecked())&&
                        (!sat.isChecked())&&(!sun.isChecked()))
                {
                    actioncommander.hide_done();
                }
                else
                {
                    actioncommander.show_done();
                }
            }
        });

        sat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if((!mon.isChecked())&&(!tue.isChecked())&&(!wed.isChecked())&&(!thu.isChecked())&&(!fri.isChecked())&&
                        (!sat.isChecked())&&(!sun.isChecked()))
                {
                    actioncommander.hide_done();
                }
                else
                {
                    actioncommander.show_done();
                }
            }
        });

        sun.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if((!mon.isChecked())&&(!tue.isChecked())&&(!wed.isChecked())&&(!thu.isChecked())&&(!fri.isChecked())&&
                        (!sat.isChecked())&&(!sun.isChecked()))
                {
                    actioncommander.hide_done();
                }
                else
                {
                    actioncommander.show_done();
                }
            }
        });



        return view;
    }

    public void getdata1()
    {
        if(mon.isChecked())
            mon_check=1;
        else
        mon_check=0;


        if(tue.isChecked())
            tue_check=1;
        else
            tue_check=0;


        if(wed.isChecked())
            wed_check=1;
        else
            wed_check=0;


        if(thu.isChecked())
            thu_check=1;
        else
            thu_check=0;


        if(fri.isChecked())
            fri_check=1;
        else
            fri_check=0;


        if(sat.isChecked())
            sat_check=1;
        else
            sat_check=0;


        if(sun.isChecked())
            sun_check=1;
        else
            sun_check=0;


        actioncommander.getdata_weekly(no_of_week_repeat.getText().toString(),mon_check,tue_check,wed_check,thu_check,fri_check,sat_check,sun_check);
    }
}
