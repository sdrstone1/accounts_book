package com.example.kollhong.accounts3;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import static com.example.kollhong.accounts3.DB_Scheme.DB_NAME;

public class Splash_OneTimeInit extends AppCompatActivity {
    int radio_checked = 0;
    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    private SectionsPagerAdapter mSectionsPagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.s_bfirst_launch);

        //Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        //setSupportActionBar(toolbar);
        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        TabLayout tabLayout = findViewById(R.id.tabDots);
        tabLayout.setupWithViewPager(mViewPager, true);


        openOrCreateDatabase(DB_NAME, MODE_PRIVATE, null);
        createDatabase();
    }

    public void onFinish(View view) {
        if(radio_checked == 2) {
            Preferences_Controll mPref = new Preferences_Controll(getApplicationContext());
            mPref.setInit();
            mPref.setSMSRegistered(true);
            getSMSPerm();

        }
        else if (radio_checked == 1) {
            Preferences_Controll mPref = new Preferences_Controll(getApplicationContext());
            mPref.setInit();
            mPref.setSMSRegistered(false);
            Intent intent = new Intent(this, Main.class);
            startActivity(intent);
            finish();
        }
        else
            makeSnackbar(R.string.snackbar_fl_choose_options);
    }

    public void SMSEnable(View view){
    radio_checked = 2;
    }
    public void SMSDisable(View view){
    radio_checked = 1;
    }

/*
백버튼 잠금
 */
    @Override
    public void onBackPressed() {
        makeSnackbar(R.string.snackbar_fl_back_lock);
    }

    private void makeSnackbar(int id){
        Snackbar.make(findViewById(R.id.main_content),id,Snackbar.LENGTH_LONG).show();
    }

    /**
     * A placeholder fragment containing a simple view.
     */

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            Fragment fragment;
            if(position == 0)
                fragment = new welcomFragment();
            else
                fragment = new infoFragment();
            return fragment;
        }

        @Override
        public int getCount() {
            // Show 3 total pages.
            return 2;
        }
    }

    public static class welcomFragment extends  Fragment{
        public welcomFragment(){        }

        @Nullable
        @Override
        public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
            return inflater.inflate(R.layout.s_first_launch_frag0, container, false);
        }
    }
    public static class infoFragment extends  Fragment{
        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            return inflater.inflate(R.layout.s_first_launch_frag1, container,false);
        }
    }



    private void getSMSPerm(){
        int SMS = ContextCompat.checkSelfPermission(this, Manifest.permission.RECEIVE_SMS);
        if(SMS == PackageManager.PERMISSION_GRANTED) {
            Intent intent = new Intent(this, Main.class);
            startActivity(intent);
            finish();
        }
        else{
            makeSnackbar(R.string.snackbar_fl_req_sms_perm);

            //ActivityCompat.shouldShowRequestPermissionRationale
            //사용자에게 이전에 거부했다면 true

            if(ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.RECEIVE_SMS))
                //권한이 거부 되어 있음
                makeSnackbar(R.string.snackbar_fl_req_sms_perm);
            else {
                //requestPermissions() 호출하여 권한 부여 요청
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.RECEIVE_SMS}, 5651);
            }
        }

    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if(requestCode == 5651){
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                Toast.makeText(this, R.string.toast_fl_sms_granted, Toast.LENGTH_LONG).show();
            else
                Toast.makeText(this, R.string.toast_fl_sms_not_granted, Toast.LENGTH_LONG).show();

            Intent intent = new Intent(this, Main.class);
            startActivity(intent);
            finish();
            return;
        }
        Intent intent = new Intent(this, Main.class);
        startActivity(intent);
        finish();
    }

    private void createDatabase(){


        DB_Controll dbMan = new DB_Controll(getApplicationContext(),true);


    }
}
