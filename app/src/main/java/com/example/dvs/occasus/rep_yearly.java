package com.example.dvs.occasus;


import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.InputFilter;
import android.text.InputType;
import android.text.Spanned;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;


public class rep_yearly extends Fragment {

    EditText no_of_year_repeat;


    rep_yearlylistener actioncommander;

    public interface rep_yearlylistener
    {
         void getdata_yearly(String number);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            actioncommander=(rep_yearlylistener)activity;
        }catch (ClassCastException e)
        {
            throw new ClassCastException(activity.toString());
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.rep_yearly,container,false);
        no_of_year_repeat=(EditText)view.findViewById(R.id.yearly_editText);

        no_of_year_repeat.setRawInputType(InputType.TYPE_CLASS_NUMBER);
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
                        if(no_of_year_repeat.getText().toString().equals(""))
                            return "1";
                        else
                            return "0";
                    }
                }
                return null;
            }
        };

        no_of_year_repeat.setFilters(new InputFilter[]{filter});

        return view;
    }



    public void getdata1()
    {
        actioncommander.getdata_yearly(no_of_year_repeat.getText().toString());
    }

}
