package com.xianwei.smartreminder.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.xianwei.smartreminder.fragment.LocationReminderListFragment;
import com.xianwei.smartreminder.fragment.TimeReminderListFragment;

/**
 * Created by xianwei li on 10/18/2017.
 */

public class ReminderPagerAdapter extends FragmentPagerAdapter {

    private static final int NUM_ITEMS = 2;

    public ReminderPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return new TimeReminderListFragment();
            case 1:
                return new LocationReminderListFragment();
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return NUM_ITEMS;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return null;
    }
}
