package com.example.planner.Adapters;


import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import com.example.planner.fragment.DayStatFragment;
import com.example.planner.fragment.MonthStatFragment;
import com.example.planner.fragment.WeekStatFragment;

public class PagerAdapter extends FragmentStatePagerAdapter {

    int mNumOfTabs;
    //DayStatFragment tab1 = new DayStatFragment();
    WeekStatFragment tab2 = new WeekStatFragment();
    MonthStatFragment tab3 = new MonthStatFragment();

    public PagerAdapter(FragmentManager fm, int NumOfTabs) {
        super(fm);
        this.mNumOfTabs = NumOfTabs;
    }

    @Override
    public Fragment getItem(int position) {

        switch (position) {
            case 0:
                return tab2;
            case 1:
                return tab3;

            default:
                return tab2;
        }
    }

    @Override
    public int getCount() {
        return mNumOfTabs;
    }

    @Override
    public int getItemPosition(@NonNull Object object) {
        return POSITION_NONE;
    }


}