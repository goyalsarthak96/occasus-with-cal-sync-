package com.example.dvs.occasus;


import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.InputType;
import android.text.Spanned;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.RadioButton;
import android.support.v4.app.Fragment;
import android.widget.Toast;


import java.util.Calendar;

public class rep_monthly extends Fragment{
    String[] days={"Sunday","Monday","Tuesday","Wednesday","Thursday","Friday","Saturday"};
    String[] count={"first","second","third","fourth","fifth"};
    RadioButton r1,r2,r3;
    EditText no_of_month_repeat;
    int radio;
    String text="";



    public static final String custom_info = "custom_repeat";
    SharedPreferences.Editor custom_editor;
    SharedPreferences custom_sharedpreferences;



    rep_monthlylistener actioncommander;

    public interface rep_monthlylistener
    {
         void getdata_monthly(String number,int radio,String repeat_monthly_on_particular_day);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            actioncommander=(rep_monthlylistener)activity;
        }catch (ClassCastException e)
        {
            throw new ClassCastException(activity.toString());
        }
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.rep_monthly, container, false);

        no_of_month_repeat=(EditText)view.findViewById(R.id.monthly_editText);

        no_of_month_repeat.setRawInputType(InputType.TYPE_CLASS_NUMBER);

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
                        if(no_of_month_repeat.getText().toString().equals(""))
                            return "1";
                            else
                            return "0";
                    }
                }
                return null;
            }
        };

        no_of_month_repeat.setFilters(new InputFilter[]{filter});

        r2=(RadioButton) view.findViewById(R.id.radioButton2);
        r1=(RadioButton)view.findViewById(R.id.radioButton1);
        r3=(RadioButton) view.findViewById(R.id.radioButton3);
        r3.setVisibility(View.INVISIBLE);
        return view;
    }

    public void cur_day()
    {
        Calendar calnow=Calendar.getInstance();
        //Toast.makeText(getActivity().getBaseContext(),custom_sharedpreferences.getInt("start_date_set",0),Toast.LENGTH_SHORT).show();
        if(custom_sharedpreferences.getInt("start_date_set",0)==1) {
            calnow.set(Calendar.DAY_OF_MONTH, Integer.valueOf(custom_sharedpreferences.getString("start_date", null).substring(0,2)));
            calnow.set(Calendar.MONTH,Integer.valueOf(custom_sharedpreferences.getString("start_date", null).substring(3,5))-1);
            calnow.set(Calendar.YEAR,Integer.valueOf(custom_sharedpreferences.getString("start_date", null).substring(6,10)));
        }
        int c;
        int month_for_radio2;
        month_for_radio2=calnow.get(Calendar.MONTH);
        calnow.add(Calendar.DAY_OF_MONTH,7);
        if(calnow.get(Calendar.MONTH)==month_for_radio2)
        {
            calnow.add(Calendar.DAY_OF_MONTH,-7);
            if (calnow.get(Calendar.DAY_OF_MONTH) % 7 == 0)
                c = (calnow.get(Calendar.DAY_OF_MONTH)) / 7;
            else
                c = (calnow.get(Calendar.DAY_OF_MONTH)) / 7 + 1;

            r2.setText("on every " + count[c - 1] + " " + days[(calnow.get(Calendar.DAY_OF_WEEK)) - 1]);
        }
        else
        {
            calnow.add(Calendar.DAY_OF_MONTH,-7);
            r2.setText("on every last " + days[(calnow.get(Calendar.DAY_OF_WEEK)) - 1]);
        }

        calnow.add(Calendar.DAY_OF_MONTH,1);
        if(calnow.get(Calendar.MONTH)!=month_for_radio2)
        {
            r3.setVisibility(View.VISIBLE);
        }
    }

    public void getdata1()
    {
        if(r1.isChecked())
            radio=1;
        else if(r2.isChecked())
        radio=2;
        else
        radio=3;
        text=r2.getText().toString();
        actioncommander.getdata_monthly(no_of_month_repeat.getText().toString(),radio,text);
    }

}
