package com.example.kollhong.accounts3;

        import android.content.Intent;
        import android.content.res.AssetManager;
        import android.database.sqlite.SQLiteDatabase;
        import android.os.Build;
        import android.os.Bundle;
        import android.support.v7.app.AppCompatActivity;

        import java.io.File;
        import java.io.FileOutputStream;
        import java.io.IOException;
        import java.io.InputStream;
        import static com.example.kollhong.accounts3.zDBScheme.DB_NAME;

public class S_Splash extends AppCompatActivity {

    //TODO ㄷㅣ비 버전 관리. card와ㅡ reciplists 테이블 업그레이드 / 복사 기능.
    //TODO 커서로 읽어와서 insert


    /*
    첫 실행 액티비티
     */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent;
        zPrefMan mPrefMan = new zPrefMan(getApplicationContext());
        if (!mPrefMan.init) {
            /*
            첫 실행이면 웰컴 액티비티 실행
             */
            intent = new Intent(this, S_FirstLaunch.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_TASK_ON_HOME);
            openOrCreateDatabase(DB_NAME, MODE_PRIVATE, null);
            createDatabase();
        }
        else {
            intent = new Intent(this, T_Main.class);
        }
        try {
            Thread.sleep(200);      //스플래시 보여주기
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        startActivity(intent);
        finish();
    }

    private void createDatabase(){
        String query = "CREATE TABLE `asset` (\n" +
                "\t `_id` INTEGER NOT NULL,\n" +
                "\t `asset_type` INTEGER NOT NULL,\n" +
                "\t `name` TEXT,\n" +
                "\t `nickname` TEXT,\n" +
                "\t `balance` real NOT NULL DEFAULT 0,\n" +
                "\t `notes` TEXT,\n" +
                "\t `withdrawalaccount` INTEGER,\n" +
                "\t `withdrawalday` INTEGER,\n" +
                "\t `cardinfo` INTEGER,\n" +
                "\tPRIMARY KEY(`_id`),\n" +
                "\tFOREIGN KEY (`cardinfo`) REFERENCES `card` (`_id`) ON DELETE SET NULL ON UPDATE CASCADE,\n" +
                "\tUNIQUE (_id ASC),\n" +
                "\tCHECK (withdrawalaccount != _id),\n" +
                "\tCHECK (withdrawalday < 32),\n" +
                "\tCONSTRAINT `CardCheck` CHECK (NOT (\n" +
                "\t( (asset_type = 1 or asset_type = 2) and cardinfo = NULL)\n" +
                "\tand ( (asset_type = 1 or asset_type = 2) AND withdrawalaccount = NULL )\n" +
                "\tand ( asset_type = 2 and withdrawalday = null)\n" +
                "\t))\n" +
                ");\n" +
                "\n" +
                "CREATE INDEX `asset_index` ON asset (asset_type DESC, name ASC);\n" +
                "\n" +
                "\n" +
                "CREATE TABLE `cardinfo` (\n" +
                "\t`_id`\tINTEGER NOT NULL UNIQUE,\n" +
                "\t`company`\tTEXT,\n" +
                "\t`card_name`\tTEXT,\n" +
                "\t`asset_type`\tINTEGER NOT NULL DEFAULT 1,\n" +
                "\t`reward_exceptions`\tTEXT,\n" +
                "\t`reward_sections`\tTEXT,\n" +
                "\t`reward_franchisee1`\tTEXT,\n" +
                "\t`reward_amount1`\tTEXT,\n" +
                "\t`reward_franchisee2`\tTEXT,\n" +
                "\t`reward_amount2`\tTEXT,\n" +
                "\t`reward_franchisee3`\tTEXT,\n" +
                "\t`reward_amount3`\tTEXT,\n" +
                "\t`reward_franchisee4`\tTEXT,\n" +
                "\t`reward_amount4`\tTEXT,\n" +
                "\tPRIMARY KEY(`_id`)\n" +
                ");\n" +
                "\n" +
                "CREATE INDEX `cardinfo_index` ON `cardinfo` (\n" +
                "\t`company`\tDESC,\n" +
                "\t`card_name`\tDESC\n" +
                ");\n" +
                "\n" +
                "CREATE TABLE `category` (\n" +
                "\t `_id` INTEGER NOT NULL,\n" +
                "\t `cat_level` INTEGER NOT NULL,\n" +
                "\t `parent` INTEGER,\n" +
                "\t `name` TEXT NOT NULL,\n" +
                "\t `reward_exception` INTEGER NOT NULL DEFAULT 0,\n" +
                "\t `budget` real NOT NULL ON CONFLICT REPLACE DEFAULT 0,\n" +
                "\tPRIMARY KEY(`_id`),\n" +
                "\tCONSTRAINT `parent` FOREIGN KEY (`_id`) REFERENCES `category` (`parent`) ON DELETE CASCADE ON UPDATE CASCADE,\n" +
                "\tUNIQUE (_id ASC),\n" +
                "\tCHECK (cat_level < 9),\n" +
                "\tCHECK (parent != _id)\n" +
                ");\n" +
                "\n" +
                "CREATE INDEX `category_index` ON category (cat_level ASC, parent ASC);\n" +
                "\n" +
                "CREATE TABLE `learn` (\n" +
                "\t `_id` INTEGER NOT NULL,\n" +
                "\t `recipient` TEXT NOT NULL,\n" +
                "\t `category_id` INTEGER,\n" +
                "\t `asset_id` integer,\n" +
                "\t `franchisee_id` INTEGER,\n" +
                "\t `budget_exception` INTEGER DEFAULT 0,\n" +
                "\t `reward_exception` INTEGER DEFAULT 0,\n" +
                "\t `reward_type` INTEGER DEFAULT 0,\n" +
                "\t `reward_amount` INTEGER DEFAULT 0,\n" +
                "\tPRIMARY KEY(`_id`),\n" +
                "\tFOREIGN KEY (`franchisee_id`) REFERENCES `franchisee_code` (`_id`) ON DELETE SET NULL ON UPDATE CASCADE,\n" +
                "\t\tFOREIGN KEY (`asset_id`) REFERENCES `asset` (`_id`) ON DELETE SET NULL ON UPDATE CASCADE,\n" +
                "\tFOREIGN KEY (`category_id`) REFERENCES `category` (`_id`) ON DELETE SET NULL ON UPDATE CASCADE,\n" +
                "\tUNIQUE (_id ASC),\n" +
                "\tUNIQUE (recipient ASC)\n" +
                ");\n" +
                "\n" +
                "CREATE INDEX `learn_index` ON learn (recipient DESC);\n" +
                "\n" +
                "\n" +
                "CREATE TABLE `franchisee_code` (\n" +
                "\t`_id`\tINTEGER UNIQUE,\n" +
                "\t`name`\tTEXT,\n" +
                "\tPRIMARY KEY(`_id`)\n" +
                ");\n" +
                "\n" +
                "CREATE INDEX `franchisee_code_index` ON `franchisee_code` (\n" +
                "\t`_id`\tASC\n" +
                ");\n" +
                "\n" +
                "\n" +
                "CREATE TABLE `transactions` (\n" +
                "\t `_id` INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,\n" +
                "\t `transacton_time` INTEGER NOT NULL,\n" +
                "\t `category_id` INTEGER,\n" +
                "\t `amount` real NOT NULL DEFAULT 0,\n" +
                "\t `asset_id` INTEGER,\n" +
                "\t `recipient` TEXT,\n" +
                "\t `note` TEXT,\n" +
                "\t `franchisee_id` INTEGER,\n" +
                "\t `budget_exception` INTEGER NOT NULL DEFAULT 0,\n" +
                "\t `reward_exception` integer,\n" +
                "\t `reward_type` integer,\n" +
                "\t `reward_calculated` integer,\n" +
                "\tFOREIGN KEY (`category_id`) REFERENCES `category` (`_id`) ON DELETE SET NULL ON UPDATE CASCADE,\n" +
                "\tFOREIGN KEY (`asset_id`) REFERENCES `asset` (`_id`) ON DELETE SET NULL ON UPDATE CASCADE,\n" +
                "\tFOREIGN KEY (`franchisee_id`) REFERENCES `franchisee_code` (`_id`) ON DELETE SET NULL ON UPDATE CASCADE,\n" +
                "\tUNIQUE (_id ASC),\n" +
                "\tCHECK (budget_exception < 2)\n" +
                ");\n" +
                "\n" +
                "CREATE INDEX `transactions_index` ON transactions (transacton_time DESC);\n" +
                "CREATE VIEW transactions_view AS SELECT t._id as _id,  t.transacton_time as transacton_time, t.amount as amount, t.category_id as category_id, c.cat_level as category_level, c.name as category_name, parent.name as parent_category_name, t.asset_id as asset_id, a.name as asset_name,  t.recipient as recipient, t.reward_caculated as reward_calculated\n" +
                "                FROM transactions as t \n" +
                "                left join category as c on (t.category_id = c._id) \n" +
                "                left join asset as a on ( t.asset_id = a._id ) \n" +
                "                left join category as parent on ( c.parent = parent._id);\n";

        zDBMan dbMan = new zDBMan(this,true);
        dbMan.rawQuery(query);

    }
}