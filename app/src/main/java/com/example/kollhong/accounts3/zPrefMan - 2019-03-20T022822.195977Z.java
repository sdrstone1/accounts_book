package com.example.kollhong.accounts3;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import java.util.prefs.Preferences;

/**
 * Created by KollHong on 14/04/2018.
 */

public class zPrefMan {
    Preferences mPrefs;
    SharedPreferences mPref;
    SharedPreferences.Editor mPrefEdit;
    Boolean init = false;
    Context context;
    zPrefMan(Context appcontext){
        context = appcontext;
        mPref = PreferenceManager.getDefaultSharedPreferences(context);
        //mPref = context.getSharedPreferences(Pref_name,Context.MODE_PRIVATE);
        mPrefEdit = mPref.edit();
        if (mPref.contains("initialized") == true) {
            init = mPref.getBoolean("initialized", false);
        } else
            init = false;
    }


    public void setInit(){
        mPrefEdit.putBoolean("initialized", true);
        mPrefEdit.commit();
    }

    public boolean getSMSEnabled () {
        if (mPref.contains("SMS") == true) {
            return mPref.getBoolean("SMS", true);

        } else {
            //TODO SMS 사용 여부 없다고 알림 보내고 설정 페이지로 intent
            return false;
        }
    }

    public void setSMSRegistered(boolean set) {

        mPrefEdit.putBoolean("SMSReg", set);
        mPrefEdit.commit();
    }
    public void ClearSharedPref(){      //if(BuildConfig.isTest)
        mPrefEdit.clear();
        mPrefEdit.commit();
    }
}
