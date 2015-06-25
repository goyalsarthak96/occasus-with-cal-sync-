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

public class no_of_events extends Fragment {

    EditText e;
    no_of_eventslistener actioncommander;

    public interface no_of_eventslistener
    {
        public void getdata_no_events(String number);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            actioncommander=(no_of_eventslistener)activity;
        }catch (ClassCastException e)
        {
            throw new ClassCastException(activity.toString());
        }
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.no_of_events,container,false);

        e=(EditText)view.findViewById(R.id.editText2);
        e.setRawInputType(InputType.TYPE_CLASS_NUMBER);



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
                        if(e.getText().toString().equals(""))
                            return "1";
                        else
                            return "0";
                    }
                }
                return null;
            }
        };

        e.setFilters(new InputFilter[]{filter});

        return view;
    }
    public void getdata1()
    {
        actioncommander.getdata_no_events(e.getText().toString());
    }
}
