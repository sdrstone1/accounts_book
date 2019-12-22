package com.example.kollhong.accounts3;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

public class T_Main extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {



    //TODO Cursor close();
    //TODO id좀 간단하게..
    //TODO Function call 순서 재정렬.

    //TODO 위젯 - 한달 지출 표시 가능하면 카테고리별, 예측 리워드 체크
    //TODO 한달 통계(한달 혜택량, 총지출량),
    //TODO 계좌 관리


    //TODO 위젯 사용량
    //TODO 예산 경고-위젯



    //TODO 옮길 수 있는 메서드 옮기기.

    //TODO convertView -> frag Holder 만들기

    //TODO 검색, 필터 메뉴 넣기(앱바),동적 메뉴 표시 -> onprepareoptionsmenu, 아니고 menu inflater.
    //TODO 검색 : 어댑터가 Filterable을 implements 하고, Filter클래스가 Filter를 extend함

    Toolbar toolbar;
    Fragment frag;
    List<Fragment> fragList = new ArrayList<>();
    /*
    fragment 메모리 상주
     */


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        zPrefMan mPrefMan = new zPrefMan(getApplicationContext());
        if (!mPrefMan.init) {
            /*
            첫 실행이면 웰컴 액티비티 실행
             */
            Intent firstlaunch_intent = new Intent(getApplicationContext(), S_FirstLaunch.class);
            firstlaunch_intent.setFlags(Intent.FLAG_ACTIVITY_TASK_ON_HOME);
            startActivity(firstlaunch_intent);
        }
        if (!mPrefMan.getSMSEnabled()) {
            unregisterReceiverFromManifest(zMessageReciever.class, getApplicationContext());
            if (BuildConfig.isTEST)
                Log.w("unregister", "unregister");
        }

        /*
        원래는 마지막에 보면 화면 기억해서 띄우려고 두었던 소스. 구현 안 됨
         */
        int tab_num;
        if (savedInstanceState != null) {
            tab_num = savedInstanceState.getInt("tab", 0);
        } else tab_num = 0;


        fragList.add(new A_Trans());
        fragList.add(new B_Manage());

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.add(R.id.main_frame, fragList.get(0),"trans");
        transaction.add(R.id.main_frame, fragList.get(1),"manage");
        transaction.hide(fragList.get(1));




        setTheme(R.style.AppTheme_NoActionBar);
        super.onCreate(savedInstanceState);

        setContentView(R.layout.t_main_activity);
        toolbar = (Toolbar) findViewById(R.id.main_toolbar);
        setSupportActionBar(toolbar);


        transaction.commit();



        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.t_menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            Intent intent_settings = new Intent(T_Main.this, v_PreferenceActivityTmp.class);
            startActivity(intent_settings);

            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        int fragid = -1;
        //내비게이션 구현


        if (id == R.id.Trans_history) {
            //frag = fragList.get(0);
            fragid = 0;
            A_Trans a_trans = (A_Trans)fragList.get(0);
            TabLayout.Tab tab = a_trans.tabLayout.getTabAt(0);
            tab.select();
        } else if (id == R.id.Trans_calendar) {
            fragid = 0;
            A_Trans a_trans = (A_Trans)fragList.get(0);
            TabLayout.Tab tab = a_trans.tabLayout.getTabAt(1);
            tab.select();
        } else if (id == R.id.Stats) {
            fragid = 1;
                B_Manage b_manage = (B_Manage) fragList.get(1);
                TabLayout.Tab tab = b_manage.tabLayout.getTabAt(1);
                tab.select();

        } else if (id == R.id.Budget) {
            fragid = 1;
                B_Manage b_manage = (B_Manage) fragList.get(1);
                TabLayout.Tab tab = b_manage.tabLayout.getTabAt(2);

        } else if (id == R.id.Card_rewards) {
            fragid = 1;
                B_Manage b_manage = (B_Manage)  fragList.get(1);
                TabLayout.Tab tab = b_manage.tabLayout.getTabAt(4);
                tab.select();

        } else if (id == R.id.Recipient) {
            fragid = 1;
                B_Manage b_manage = (B_Manage)fragList.get(1);
                TabLayout.Tab tab = b_manage.tabLayout.getTabAt(3);
                tab.select();

        } else if (id == R.id.Accounts) {
            fragid = 1;
                B_Manage b_manage = (B_Manage)  fragList.get(1);
                TabLayout.Tab tab = b_manage.tabLayout.getTabAt(0);
                tab.select();
        } else if (id == R.id.Settings) {
            Intent intent_settings = new Intent(T_Main.this, v_PreferenceActivityTmp.class);
            startActivity(intent_settings);
        }else if(id == R.id.nav_send)
        {
            Intent intent = new Intent(Intent.ACTION_SENDTO);
            intent.setData(Uri.parse("mailto:sdrstone3@gmail.com"));
            startActivity(intent);
        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawers();

        if(fragid == 1) {
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.show(fragList.get(1));
            transaction.hide(fragList.get(0));
            transaction.commit();
        }
        else if(fragid == 0 ) {
                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                transaction.show(fragList.get(0));
                transaction.hide(fragList.get(1));
                transaction.commit();
        }
        return false;
    }

    public void onEMailClick(View v){
        Intent intent = new Intent(Intent.ACTION_SENDTO);
        intent.setData(Uri.parse("mailto:sdrstone3@gmail.com"));
        startActivity(intent);
    }

    private void unregisterReceiverFromManifest(Class<? extends BroadcastReceiver> clazz, final Context context) {
        final ComponentName component = new ComponentName(context, clazz);
        final int status = context.getPackageManager().getComponentEnabledSetting(component);
        if (status == PackageManager.COMPONENT_ENABLED_STATE_ENABLED) {
            context.getPackageManager()
                    .setComponentEnabledSetting(component, PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
                            PackageManager.DONT_KILL_APP);
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        //if (BuildConfig.isTEST)
          //  mPrefMan.ClearSharedPref();
    }
}