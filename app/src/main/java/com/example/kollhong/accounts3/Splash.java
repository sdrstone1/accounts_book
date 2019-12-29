package com.example.kollhong.accounts3;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import static com.example.kollhong.accounts3.DB_Scheme.DB_NAME;

public class Splash extends AppCompatActivity {

    //TODO ㄷㅣ비 버전 관리. card와ㅡ reciplists 테이블 업그레이드 / 복사 기능.
    //TODO 커서로 읽어와서 insert


    /*
    첫 실행 액티비티
     */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent;
        Preferences_Controll mPrefMan = new Preferences_Controll(getApplicationContext());
        if (!mPrefMan.init) {
            /*
            첫 실행이면 웰컴 액티비티 실행
             */
            intent = new Intent(this, Splash_OneTimeInit.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_TASK_ON_HOME);

        }
        else {
            intent = new Intent(this, Main.class);
        }
        try {
            Thread.sleep(200);      //스플래시 보여주기
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        startActivity(intent);
        finish();
    }
}