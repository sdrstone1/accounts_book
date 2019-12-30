package com.example.kollhong.accounts3;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import java.util.prefs.Preferences;

/**
 * Created by KollHong on 14/04/2018.
 */

public class Preferences_Controll {
    Preferences mPrefs;
    private SharedPreferences mPref;
    private SharedPreferences.Editor mPrefEdit;
    Boolean init;
    Context context;
    Preferences_Controll(Context appcontext){
        context = appcontext;
        mPref = PreferenceManager.getDefaultSharedPreferences(context);
        //mPref = context.getSharedPreferences(Pref_name,Context.MODE_PRIVATE);


        if (mPref.contains("initialized")) {
            init = mPref.getBoolean("initialized", false);
        } else
            init = false;
    }


    void setInit(){
        mPrefEdit = mPref.edit();
        mPrefEdit.putBoolean("initialized", true);
        mPrefEdit.apply();
    }

    boolean getSMSEnabled() {
        if (mPref.contains("SMS")) {
            return mPref.getBoolean("SMS", true);

        } else {
            //TODO SMS 사용 여부 없다고 알림 보내고 설정 페이지로 intent
            return false;
        }
    }

    void setSMSRegistered(boolean set) {
        mPrefEdit = mPref.edit();
        mPrefEdit.putBoolean("SMSReg", set);
        mPrefEdit.apply();
    }
    public void ClearSharedPref(){      //if(BuildConfig.isTest)
        mPrefEdit = mPref.edit();
        mPrefEdit.clear();
        mPrefEdit.apply();
    }
}
