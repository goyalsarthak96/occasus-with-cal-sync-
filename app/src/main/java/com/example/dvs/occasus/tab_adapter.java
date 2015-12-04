package com.example.dvs.occasus;


import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

public class tab_adapter extends FragmentPagerAdapter {

    public tab_adapter(FragmentManager fm) {
        super(fm);
    }


    @Override
    public Fragment getItem(int i) {
        switch (i) {
            case 0:
                return new today_eve_frag();
            default:
                return new all_eve_frag();
        }
    }

    @Override
    public int getCount() {
        return 2;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        if(position==0)
            return "Today's Events";
        else
            return "All Events";
    }
}
