package com.example.planner.fragment;



import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.example.planner.Adapters.PagerAdapter;
import com.example.planner.R;
import com.google.android.material.tabs.TabLayout;

/*
statisticsFragment : DB에 저장된 사용자의 타이머 데이터를 이용하여 일, 주, 월로 시각화

 */
public class StatisticsFragment extends Fragment {

    ViewPager viewPager;
    PagerAdapter pagerAdapter;


    public StatisticsFragment() {

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_statistics, container, false);

        viewPager = view.findViewById(R.id.viewPager);

        final TabLayout tabLayout = view.findViewById(R.id.tabLayout);
        tabLayout.addTab(tabLayout.newTab().setText("일"));
        tabLayout.addTab(tabLayout.newTab().setText("주"));
        tabLayout.addTab(tabLayout.newTab().setText("월"));
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);

        pagerAdapter = new PagerAdapter(getFragmentManager(),tabLayout.getTabCount());
        viewPager.setAdapter(pagerAdapter);

        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });




        return view;
    }


}
