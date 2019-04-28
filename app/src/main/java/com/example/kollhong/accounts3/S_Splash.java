package com.example.kollhong.accounts3;

        import android.content.Intent;
        import android.content.res.AssetManager;
        import android.os.Build;
        import android.os.Bundle;
        import android.support.v7.app.AppCompatActivity;

        import java.io.File;
        import java.io.FileOutputStream;
        import java.io.IOException;
        import java.io.InputStream;

public class S_Splash extends AppCompatActivity {

    //TODO ㄷㅣ비 버전 관리. card와ㅡ reciplists 테이블 업그레이드 / 복사 기능.
    //TODO 커서로 읽어와서 insert


    /*
    첫 실행 액티비티
     */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        String ROOT_DIR;
        if (Build.VERSION.SDK_INT >= 24) {
            ROOT_DIR = getApplicationContext().getDataDir().getAbsolutePath();
        }
        else{
            ROOT_DIR = getApplication().getFilesDir().getAbsolutePath();
        }

        //String ROOT_DIR = getApplicationContext().getDataDir().getAbsolutePath();
        String DATABASE_NAME = "data.db";

        if(BuildConfig.isTEST)
        {
            //File file = new File(ROOT_DIR, "/database/data.db");
            //boolean deleted = file.delete();
        }

        /*
        asset에 저장된 디비 복사
         */

        File folder = new File(ROOT_DIR + "/database");
        folder.mkdirs();
        File outfile = new File(ROOT_DIR + "/database/" + DATABASE_NAME);
        if (outfile.length() <= 0) {
            AssetManager assetManager = getResources().getAssets();
            try {
                InputStream input = assetManager.open(DATABASE_NAME, AssetManager.ACCESS_BUFFER);
                long filesize = input.available();
                byte[] tempdata = new byte[(int) filesize];
                input.read(tempdata);
                input.close();

                outfile.createNewFile();
                FileOutputStream fo = new FileOutputStream(outfile);
                fo.write(tempdata);
                fo.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }


        try {
            Thread.sleep(200);      //스플래시 보여주기
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        startActivity(new Intent(S_Splash.this, T_Main.class));
        finish();
    }
}