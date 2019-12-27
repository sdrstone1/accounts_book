package com.example.kollhong.accounts3;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
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


public class Transaction_View extends Fragment {

    private SectionsPagerAdapter TSectionsPagerAdapter;
    private ViewPager TViewPager;

    TabLayout tabLayout;



    FragmentManager TFM;
    int tab_num = 0;


    /*
    프래그먼트 메모리 상주용
     */
    List<Fragment> fragList = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        fragList.add(new Transaction_History());
        fragList.add(new Transaction_Calendar());
        return inflater.inflate(R.layout.a_trans_frag, container, false);
    }

      @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        TFM =  getChildFragmentManager();
        TSectionsPagerAdapter = new SectionsPagerAdapter(TFM);

        // Set up the ViewPager with the sections adapter.
        TViewPager = getView().findViewById(R.id.trans_container);
        TViewPager.setAdapter(TSectionsPagerAdapter);

        tabLayout = getView().findViewById(R.id.trans_tab);

        TViewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.addOnTabSelectedListener(new TabLayout.ViewPagerOnTabSelectedListener(TViewPager){
            final int titles[]={ R.string.title_frag_trans_0_history,R.string.title_frag_trans_1_cal};

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

        FloatingActionButton fab = getView().findViewById(R.id.trans_fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent add_trans = new Intent(getContext(), TransactionAdd_Activity.class);
                startActivity(add_trans);
            }
        });


          //tab_num = getArguments().getInt("tab");
          TabLayout.Tab tab = tabLayout.getTabAt(tab_num);
          tab.select();
    }

    @Override
    public void onResume() {
        super.onResume();

    }

    public void setTab_num(int num){
        tab_num = num;

    }
    private class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return super.getPageTitle(position);
        }

        @Override
        public Fragment getItem(int id) {

            return fragList.get(id);
        }

        @Override
        public int getCount() {
            return 2;
        }
    }

}
