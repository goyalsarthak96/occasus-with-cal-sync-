package com.example.dvs.occasus;


import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ToggleButton;

public class profile_fragment extends Fragment{

    ToggleButton bluetooth_button,wifi_button,mobile_data_button;
    Button profile_button,set_button;

    String blutooth_status,wifi_status,mobiledata_status,profile_status;

    profile_fragmentlistener actioncommander;

    public interface profile_fragmentlistener
    {
        void show_dialog();
        void send_data(String bluetooth_status1,String wifi_status1,String mobiledata_status1,String profile_status1);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            actioncommander=(profile_fragmentlistener)activity;
        }catch (ClassCastException e)
        {
            throw new ClassCastException(activity.toString());
        }
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.profile_fragment, container, false);

        bluetooth_button=(ToggleButton) view.findViewById(R.id.sync_bluetooth_button);
        wifi_button=(ToggleButton) view.findViewById(R.id.sync_wifi_button);
        mobile_data_button=(ToggleButton) view.findViewById(R.id.sync_mobiledata_button);
        profile_button=(Button) view.findViewById(R.id.sync_profile_button);
        set_button=(Button) view.findViewById(R.id.sync_save_btn);



        bluetooth_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        wifi_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        mobile_data_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        profile_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                actioncommander.show_dialog();
            }
        });

        set_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (bluetooth_button.isChecked())
                    blutooth_status = "yes";
                else
                    blutooth_status = "no";

                if (wifi_button.isChecked())
                    wifi_status = "yes";
                else
                    wifi_status = "no";

                if (mobile_data_button.isChecked())
                    mobiledata_status = "yes";
                else
                    mobiledata_status = "no";

                if (profile_button.getText().toString().equals("Silent"))
                    profile_status = "silent";
                else if (profile_button.getText().toString().equals("Ring"))
                    profile_status = "ring";
                else
                    profile_status = "vibrate";

                actioncommander.send_data(blutooth_status, wifi_status, mobiledata_status, profile_status);

            }
        });

        return view;
    }

    public void change_button_text(int position)
    {
        if (position == 0)
        {
            profile_button.setText("Silent");
        }
        else if (position == 1)
        {
            profile_button.setText("Ring");
        }
        else if (position == 2)
        {
            profile_button.setText("Vibrate");
        }
    }
}
