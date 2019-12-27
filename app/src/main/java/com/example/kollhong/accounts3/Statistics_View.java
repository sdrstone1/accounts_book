package com.example.kollhong.accounts3;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by KollHong on 25/03/2018.
 */

public class Statistics_View extends Fragment {
    private SectionsPagerAdapter MSectionsPagerAdapter;
    private ViewPager MViewPager;

    TabLayout tabLayout;

    FragmentManager MFM;
    int tab_num = 0;

    List<Fragment> fragList = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        fragList.add(new Statistics_Asset());
        fragList.add(new Statistics_Category());
        fragList.add(new Statistics_Budget());
        fragList.add(new Statistics_Recipient());
        fragList.add((new Statistics_Reward()));

        return inflater.inflate(R.layout.b_manage_frag, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
//        tab_num = getArguments().getInt("tab", 0);

        MFM =  getChildFragmentManager();
        MSectionsPagerAdapter = new SectionsPagerAdapter(MFM);

        MViewPager =  getView().findViewById(R.id.manage_container);
        MViewPager.setAdapter(MSectionsPagerAdapter);

        tabLayout =  getView().findViewById(R.id.manage_tab);
        TabLayout.Tab tab = tabLayout.getTabAt(tab_num);
        //tabLayout.clearOnTabSelectedListeners();

        MViewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.addOnTabSelectedListener(new TabLayout.ViewPagerOnTabSelectedListener(MViewPager){
            final int titles[]={ R.string.title_frag_accounts, R.string.title_frag_manage_0_stat, R.string.title_frag_manage_1_budget, R.string.title_frag_manage_2_recipient, R.string.title_frag_manage_3_reward};

            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                super.onTabSelected(tab);
                setTitle(tab.getPosition());

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
                super.onTabReselected(tab);
                setTitle(tab.getPosition());

            }
            private void setTitle(int position){
                tabLayout.setNextFocusRightId(position);
                getActivity().setTitle(titles[position]);
            }
        });
        tab.select();



    }

    private class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int id) {
            return  fragList.get(id);
        }

        @Override
        public int getCount() {
            return 5;
        }
    }

}
