package com.example.kollhong.accounts3;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteConstraintException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.annotation.Nullable;
import android.util.Log;

import java.text.DateFormat;
import java.util.Calendar;

public final class DB_Scheme extends SQLiteOpenHelper {
    public static final int DB_VERSION = 1;
    public static final int[] DB_VERSION_LIST = {1};
    public static final String DB_NAME = "account_book";
    public static final String TABLE_ASSET = "asset";
    public static final String TABLE_CARD_INFO = "cardinfo";
    public static final String TABLE_CATEGORY = "category";
    public static final String TABLE_LEARN = "learn";
    public static final String TABLE_FRANCHISEE_CODE = "franchisee_code";
    public static final String TABLE_TRANSACTIONS = "transactions";
    public static final String TABLE_TRANSACTIONS_VIEW = "transactions_view";

    public static final int ASSET_TYPE_BANK_BOOK = 0;
    public static final int ASSET_TYPE_DEBIT_CARD = 1;
    public static final int ASSET_TYPE_CREDIT_CARD = 2;

    public static final String TABLE_ID = "_id";


    public static final class ASSET_TABLE{
        public static final String ASSET_TYPE = "asset_type";
        public static final String NAME = "name";
        public static final String NICKNAME = "nickname";
        public static final String BALANCE = "balance";
        public static final String NOTES = "notes";
        public static final String WITHDRAWALACCOUNT = "withdrawalaccount";
        public static final String WITHDRAWALDAY = "withdrawalday";
        public static final String CARD_ID = "cardinfo";
    }

    public static final class CARD_INFO_TABLE {
        public static final String COMPANY = "company";
        public static final String CARD_NAME = "card_name";
        public static final String ASSET_TYPE = "asset_type";
        public static final String REWARD_EXCEPTIONS = "reward_exceptions";
        public static final String REWARD_SECTIONS = "reward_sections";
        public static final String REWARD_FRANCHISEE_1 = "reward_franchisee1";
        public static final String REWARD_AMOUNT_1 = "reward_amount1";
        public static final String REWARD_FRANCHISEE_2 = "reward_franchisee2";
        public static final String REWARD_AMOUNT_2 = "reward_amount2";
        public static final String REWARD_FRANCHISEE_3 = "reward_franchisee3";
        public static final String REWARD_AMOUNT_3 = "reward_amount3";
        public static final String REWARD_FRANCHISEE_4 = "reward_franchisee4";
        public static final String REWARD_AMOUNT_4 = "reward_amount4";

        public static final String REWARD_FRANCHISEE_STRING = "reward_franchisee";
        public static final String REWARD_AMOUNT_STRING = "reward_amount";
    }

    public static final class CATEGORY_TABLE {
        public static final String CAT_LEVEL = "cat_level";
        public static final String PARENT_ID = "parent";
        public static final String NAME = "name";
        public static final String REWARD_EXCEPTION = "reward_exception";
        public static final String BUDGET = "budget";
    }

    public static final class LEARN_TABLE {
        public static final String RECIPIENT = "recipient";
        public static final String CATEGORY_ID = "category_id";
        public static final String ASSET_ID = "asset_id";
        public static final String FRANCHISEE_ID = "franchisee_id";
        public static final String BUDGET_EXCEPTION = "budget_exception";
        public static final String REWARD_EXCEPTION = "reward_exception";
        public static final String REWARD_TYPE = "reward_type";
        public static final String REWARD_PERCENT = "reward_percent";
    }

    public static final class FRANCHISEE_CODE_TABLE {
        public static final String NAME = "name";
    }

    public static final class TRANSACTIONS_TABLE {
        public static final String TRANSACTION_TIME = "transaction_time";
        public static final String CATEGORY_ID = "category_id";
        public static final String AMOUNT = "amount";
        public static final String ASSET_ID = "asset_id";
        public static final String RECIPIENT = "recipient";
        public static final String NOTE = "note";
        public static final String FRANCHISEE_ID = "franchisee_id";
        public static final String BUDGET_EXCEPTION = "budget_exception";
        public static final String REWARD_EXCEPTION = "reward_exception";
        public static final String REWARD_TYPE = "reward_type";
        public static final String REWARD_CACULATED = "reward_calculated";
    }

    public static final class TRANSACTIONS_VIEW {
        public static final String TRANSACTION_TIME = "transaction_time";
        public static final String AMOUNT = "amount";
        public static final String ASSET_ID = "asset_id";
        public static final String ASSET_NAME = "asset_name";
        public static final String CATEGORY_ID = "category_id";
        public static final String CATEGORY_LEVEL = "category_level";
        public static final String CATEGORY_NAME = "category_name";
        public static final String PARENT_CATEGORY_NAME = "parent_category_name";
        public static final String RECIPIENT = "recipient";
        public static final String REWARD_CACULATED = "reward_calculated";
    }


    public DB_Scheme(@Nullable Context context, @Nullable String name, @Nullable SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String query = "CREATE TABLE 'asset' (\n" +
                "\t '_id' INTEGER NOT NULL,\n" +
                "\t 'asset_type' INTEGER NOT NULL,\n" +
                "\t 'name' TEXT,\n" +
                "\t 'nickname' TEXT,\n" +
                "\t 'balance' real NOT NULL DEFAULT 0,\n" +
                "\t 'notes' TEXT,\n" +
                "\t 'withdrawalaccount' INTEGER,\n" +
                "\t 'withdrawalday' INTEGER,\n" +
                "\t 'cardinfo' INTEGER,\n" +
                "\tPRIMARY KEY('_id'),\n" +
                "\tFOREIGN KEY ('cardinfo') REFERENCES 'card' ('_id') ON DELETE SET NULL ON UPDATE CASCADE,\n" +
                "\tUNIQUE (_id ASC),\n" +
                "\tCHECK (withdrawalaccount != _id),\n" +
                "\tCHECK (withdrawalday < 32),\n" +
                "\tCONSTRAINT 'CardCheck' CHECK (NOT (\n" +
                "\t( (asset_type = 1 or asset_type = 2) and cardinfo = NULL)\n" +
                "\tand ( (asset_type = 1 or asset_type = 2) AND withdrawalaccount = NULL )\n" +
                "\tand ( asset_type = 2 and withdrawalday = null)\n" +
                "\t))\n" +
                ");\n";
        Log.i("Create Tables",query);
        zDbIO.rawQuery(db, query);

        query = "CREATE INDEX 'asset_index' ON asset (asset_type DESC, name ASC);\n";
        zDbIO.rawQuery(db, query);

        query = "CREATE TABLE 'cardinfo' (\n" +
                "\t'_id'\tINTEGER NOT NULL UNIQUE,\n" +
                "\t'company'\tTEXT,\n" +
                "\t'card_name'\tTEXT,\n" +
                "\t'asset_type'\tINTEGER NOT NULL DEFAULT 1,\n" +
                "\t'reward_exceptions'\tTEXT,\n" +
                "\t'reward_sections'\tTEXT,\n" +
                "\t'reward_franchisee1'\tTEXT,\n" +
                "\t'reward_amount1'\tTEXT,\n" +
                "\t'reward_franchisee2'\tTEXT,\n" +
                "\t'reward_amount2'\tTEXT,\n" +
                "\t'reward_franchisee3'\tTEXT,\n" +
                "\t'reward_amount3'\tTEXT,\n" +
                "\t'reward_franchisee4'\tTEXT,\n" +
                "\t'reward_amount4'\tTEXT,\n" +
                "\tPRIMARY KEY('_id')\n" +
                ");\n";
        Log.i("Create Tables",query);
        zDbIO.rawQuery(db, query);

        query = "CREATE INDEX 'cardinfo_index' ON 'cardinfo' (\n" +
                "\t'company'\tDESC,\n" +
                "\t'card_name'\tDESC\n" +
                ");\n" ;
        Log.i("Create Tables",query);
        zDbIO.rawQuery(db, query);

        query =  "CREATE TABLE 'category' (\n" +
                "\t '_id' INTEGER NOT NULL,\n" +
                "\t 'cat_level' INTEGER NOT NULL,\n" +
                "\t 'parent' INTEGER,\n" +
                "\t 'name' TEXT NOT NULL,\n" +
                "\t 'reward_exception' INTEGER NOT NULL DEFAULT 0,\n" +
                "\t 'budget' real NOT NULL ON CONFLICT REPLACE DEFAULT 0,\n" +
                "\tPRIMARY KEY('_id'),\n" +
                "\tCONSTRAINT 'parent' FOREIGN KEY ('_id') REFERENCES 'category' ('parent') ON DELETE CASCADE ON UPDATE CASCADE,\n" +
                "\tUNIQUE (_id ASC),\n" +
                "\tCHECK (cat_level < 9),\n" +
                "\tCHECK (parent != _id)\n" +
                ");\n" ;
        Log.i("Create Tables",query);
        zDbIO.rawQuery(db, query);

        query = "CREATE INDEX 'category_index' ON category (cat_level ASC, parent ASC);\n" ;
        Log.i("Create Tables",query);
        zDbIO.rawQuery(db, query);

        query = "CREATE TABLE 'learn' (\n" +
                "\t '_id' INTEGER NOT NULL,\n" +
                "\t 'recipient' TEXT NOT NULL,\n" +
                "\t 'category_id' INTEGER,\n" +
                "\t 'asset_id' integer,\n" +
                "\t 'franchisee_id' INTEGER,\n" +
                "\t 'budget_exception' INTEGER DEFAULT 0,\n" +
                "\t 'reward_exception' INTEGER DEFAULT 0,\n" +
                "\t 'reward_type' INTEGER DEFAULT 0,\n" +
                "\t 'reward_percent' INTEGER DEFAULT 0,\n" +
                "\tPRIMARY KEY('_id'),\n" +
                "\tFOREIGN KEY ('franchisee_id') REFERENCES 'franchisee_code' ('_id') ON DELETE SET NULL ON UPDATE CASCADE,\n" +
                "\t\tFOREIGN KEY ('asset_id') REFERENCES 'asset' ('_id') ON DELETE SET NULL ON UPDATE CASCADE,\n" +
                "\tFOREIGN KEY ('category_id') REFERENCES 'category' ('_id') ON DELETE SET NULL ON UPDATE CASCADE,\n" +
                "\tUNIQUE (_id ASC),\n" +
                "\tUNIQUE (recipient ASC)\n" +
                ");\n" ;
        Log.i("Create Tables",query);
        zDbIO.rawQuery(db, query);

        query = "CREATE INDEX 'learn_index' ON learn (recipient DESC);\n";
        Log.i("Create Tables",query);
        zDbIO.rawQuery(db, query);

        query = "CREATE TABLE 'franchisee_code' (\n" +
                "\t'_id'\tINTEGER UNIQUE,\n" +
                "\t'name'\tTEXT,\n" +
                "\tPRIMARY KEY('_id')\n" +
                ");\n";
        Log.i("Create Tables",query);
        zDbIO.rawQuery(db, query);
        query = "CREATE INDEX 'franchisee_code_index' ON 'franchisee_code' (\n" +
                "\t'_id'\tASC\n" +
                ");\n";
        Log.i("Create Tables",query);
        zDbIO.rawQuery(db, query);

        query = "CREATE TABLE 'transactions' (\n" +
                "\t '_id' INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,\n" +
                "\t 'transaction_time' INTEGER NOT NULL,\n" +
                "\t 'category_id' INTEGER,\n" +
                "\t 'amount' real NOT NULL DEFAULT 0,\n" +
                "\t 'asset_id' INTEGER,\n" +
                "\t 'recipient' TEXT,\n" +
                "\t 'note' TEXT DEFAULT '',\n" +
                "\t 'franchisee_id' INTEGER,\n" +
                "\t 'budget_exception' INTEGER NOT NULL DEFAULT 0,\n" +
                "\t 'reward_exception' integer,\n" +
                "\t 'reward_type' integer,\n" +
                "\t 'reward_calculated' integer,\n" +
                "\tFOREIGN KEY ('category_id') REFERENCES 'category' ('_id') ON DELETE SET NULL ON UPDATE CASCADE,\n" +
                "\tFOREIGN KEY ('asset_id') REFERENCES 'asset' ('_id') ON DELETE SET NULL ON UPDATE CASCADE,\n" +
                "\tFOREIGN KEY ('franchisee_id') REFERENCES 'franchisee_code' ('_id') ON DELETE SET NULL ON UPDATE CASCADE,\n" +
                "\tUNIQUE (_id ASC),\n" +
                "\tCHECK (budget_exception < 2)\n" +
                ");\n" ;
        Log.i("Create Tables",query);
        zDbIO.rawQuery(db, query);

        query = "CREATE INDEX 'transactions_index' ON transactions (transaction_time DESC);\n";
        Log.i("Create Tables",query);
        zDbIO.rawQuery(db, query);

        query = "CREATE VIEW transactions_view AS SELECT t._id as _id,  t.transaction_time as transaction_time, t.amount as amount, t.category_id as category_id, c.cat_level as category_level, c.name as category_name, parent.name as parent_category_name, t.asset_id as asset_id, a.name as asset_name,  t.recipient as recipient, t.reward_calculated as reward_calculated \n" +
                "                FROM transactions as t \n" +
                "                left join category as c on (t.category_id = c._id) \n" +
                "                left join asset as a on ( t.asset_id = a._id ) \n" +
                "                left join category as parent on ( c.parent = parent._id);\n";

        Log.i("Create Tables",query);
        zDbIO.rawQuery(db, query);



        query = "INSERT INTO category ('_id',cat_level,name,reward_exception,budget)\n" +
                "\tVALUES (1,0,'Income',0,0);";
        Log.i("Create Tables",query);
        zDbIO.rawQuery(db, query);

        query = "INSERT INTO category ('_id',cat_level,name,reward_exception,budget)\n" +
                "\tVALUES (2,3,'Expenditure',0,0);\n";
        Log.i("Create Tables",query);
        zDbIO.rawQuery(db, query);

        query = "INSERT INTO category ('_id',cat_level,name,reward_exception,budget)\n" +
                "\tVALUES (3,6,'Transfer',0,0);\n" ;
        Log.i("Create Tables",query);
        zDbIO.rawQuery(db, query);

        query = "INSERT INTO category ('_id',cat_level,parent,name)\n" +
                "\tVALUES (4,1,1,'월급');\n" ;
        Log.i("Create Tables",query);
        zDbIO.rawQuery(db, query);

        query = "INSERT INTO category ('_id',cat_level,parent,name)\n" +
                "\tVALUES (5,1,1,'금융 소득');\n" ;
        Log.i("Create Tables",query);
        zDbIO.rawQuery(db, query);

        query = "INSERT INTO category ('_id',cat_level,parent,name)\n" +
                "\tVALUES (6,1,1,'기타 부수입');\n" ;
        Log.i("Create Tables",query);
        zDbIO.rawQuery(db, query);

        query = "INSERT INTO category ('_id',cat_level,parent,name)\n" +
                "\tVALUES (7,4,2,'필수 고정 지출');\n";
        Log.i("Create Tables",query);
        zDbIO.rawQuery(db, query);

        query = "INSERT INTO category ('_id',cat_level,parent,name)\n" +
                "\tVALUES (8,4,2,'필수 변동 지출');\n";
        Log.i("Create Tables",query);
        zDbIO.rawQuery(db, query);

        query = "INSERT INTO category ('_id',cat_level,parent,name)\n" +
                "\tVALUES (9,4,2,'필요 변동 지출');\n";
        Log.i("Create Tables",query);
        zDbIO.rawQuery(db, query);

        query = "INSERT INTO category ('_id',cat_level,parent,name)\n" +
                "\tVALUES (10,4,2,'비정기 변동 지출');\n";
        Log.i("Create Tables",query);
        zDbIO.rawQuery(db, query);

        query = "INSERT INTO category ('_id',cat_level,parent,name)\n" +
                "\tVALUES (11,5,7,'세금');\n";
        Log.i("Create Tables",query);
        zDbIO.rawQuery(db, query);

        query = "INSERT INTO category ('_id',cat_level,parent,name)\n" +
                "\tVALUES (12,5,8,'식비');\n";
        Log.i("Create Tables",query);
        zDbIO.rawQuery(db, query);

        query = "INSERT INTO category ('_id',cat_level,parent,name)\n" +
                "\tVALUES (13,5,8,'교통비');\n";
        Log.i("Create Tables",query);
        zDbIO.rawQuery(db, query);

        query = "INSERT INTO category ('_id',cat_level,parent,name)\n" +
                "\tVALUES (14,5,9,'취미');\n";
        Log.i("Create Tables",query);
        zDbIO.rawQuery(db, query);

        query = "INSERT INTO category ('_id',cat_level,parent,name)\n" +
                "\tVALUES (15,5,10,'군것질');\n";
        Log.i("Create Tables",query);
        zDbIO.rawQuery(db, query);

        query = "INSERT INTO cardinfo ('_id',company,card_name,asset_type,reward_exceptions,reward_sections,reward_franchisee1,reward_amount1,reward_franchisee2,reward_amount2,reward_franchisee3,reward_amount3,reward_franchisee4,reward_amount4) VALUES (\n" +
                "1,'신한카드','네이버페이',1,'10101:20100','b0:c5','11000:12001','p0.7:d1.2','10100:12002','p1.2:d1.2',NULL,NULL,NULL,NULL);\n";
        Log.i("Create Tables",query);
        zDbIO.rawQuery(db, query);

        query = "INSERT INTO cardinfo ('_id',company,card_name,asset_type,reward_exceptions,reward_sections,reward_franchisee1,reward_amount1,reward_franchisee2,reward_amount2,reward_franchisee3,reward_amount3,reward_franchisee4,reward_amount4) VALUES (\n" +
                "2,'현대카드','ZERO',2,'10101:20100','b20:b30:b40','11002:12000','p1.0:p1.0','11001:11003','p2.0:p0.5','10100','p5',NULL,NULL);\n";
        Log.i("Create Tables",query);
        zDbIO.rawQuery(db, query);

        query = "INSERT INTO franchisee_code ('_id',name) VALUES (\n" +
                "0,NULL);\n";
        Log.i("Create Tables",query);
        zDbIO.rawQuery(db, query);

        query = "INSERT INTO franchisee_code ('_id',name) VALUES (\n" +
                "10100,'교통비');\n";
        Log.i("Create Tables",query);
        zDbIO.rawQuery(db, query);

        query = "INSERT INTO franchisee_code ('_id',name) VALUES (\n" +
                "10101,'후불교통카드');\n";
        Log.i("Create Tables",query);
        zDbIO.rawQuery(db, query);

        query = "INSERT INTO franchisee_code ('_id',name) VALUES (\n" +
                "11000,'편의점');\n";
        Log.i("Create Tables",query);
        zDbIO.rawQuery(db, query);

        query = "INSERT INTO franchisee_code ('_id',name) VALUES (\n" +
                "11001,'CU');\n";
        Log.i("Create Tables",query);
        zDbIO.rawQuery(db, query);

        query = "INSERT INTO franchisee_code ('_id',name) VALUES (\n" +
                "11002,'세븐일레븐');\n";
        Log.i("Create Tables",query);
        zDbIO.rawQuery(db, query);

        query = "INSERT INTO franchisee_code ('_id',name) VALUES (\n" +
                "11003,'GS25');\n";
        Log.i("Create Tables",query);
        zDbIO.rawQuery(db, query);

        query = "INSERT INTO franchisee_code ('_id',name) VALUES (\n" +
                "12000,'대형할인점');\n";
        Log.i("Create Tables",query);
        zDbIO.rawQuery(db, query);

        query = "INSERT INTO franchisee_code ('_id',name) VALUES (\n" +
                "12001,'롯데마트');\n";
        Log.i("Create Tables",query);
        zDbIO.rawQuery(db, query);

        query = "INSERT INTO franchisee_code ('_id',name) VALUES (\n" +
                "12002,'이마트');\n";
        Log.i("Create Tables",query);
        zDbIO.rawQuery(db, query);

        query = "INSERT INTO franchisee_code ('_id',name) VALUES (\n" +
                "20100,'해외 이용');\n";
        Log.i("Create Tables",query);
        zDbIO.rawQuery(db, query);

        query = "INSERT INTO asset ('_id','asset_type',name,nickname,balance,notes,withdrawalaccount,withdrawalday,cardinfo) VALUES (\n" +
                "1,0,'KB국민ONE자유입출금통장','생활비통장',100000,'생활비통장',NULL,NULL,NULL);\n";
        Log.i("Create Tables",query);
        zDbIO.rawQuery(db, query);

        query = "INSERT INTO asset ('_id','asset_type',name,nickname,balance,notes,withdrawalaccount,withdrawalday,cardinfo) VALUES (\n" +
                "2,1,'신한네이버체크','메인카드',0,'국민은행에서 출금',1,NULL,1);\n";
        Log.i("Create Tables",query);
        zDbIO.rawQuery(db, query);

        query = "INSERT INTO asset ('_id','asset_type',name,nickname,balance,notes,withdrawalaccount,withdrawalday,cardinfo) VALUES (\n" +
                "3,2,'현대카드ZERO','테스트용 카드',150000,'국민은행에서 출금',1,20,2);\n";
        Log.i("Create Tables",query);
        zDbIO.rawQuery(db, query);

        query = "INSERT INTO learn ('_id',recipient,category_id,franchisee_id,budget_exception,reward_type, reward_percent) VALUES (\n" +
                "1,'신한체크교',13,10100,0,0,1);\n" ;
        Log.i("Create Tables",query);
        zDbIO.rawQuery(db, query);

        long time = Calendar.getInstance().getTimeInMillis();
        query = "INSERT INTO transactions (transaction_time,category_id,amount,asset_id,recipient,note,franchisee_id,budget_exception,reward_exception,reward_type,reward_calculated) VALUES (\n" +
                time + ",13,65000,2,'신한체크교','후불교통카드 이용',10100,0,0,0,650);\n";
        Log.i("Create Tables",query);
        zDbIO.rawQuery(db, query);

        query = "INSERT INTO transactions (transaction_time,category_id,amount,asset_id,recipient,note,franchisee_id,budget_exception,reward_exception,reward_type,reward_calculated) VALUES (\n" +
                (time+ 100L) + ",15,2000,2,'씨유방학점','콜라 먹음',11001,0,0,1,20);\n";
        Log.i("Create Tables",query);
        zDbIO.rawQuery(db, query);

        query = "INSERT INTO transactions (transaction_time,category_id,amount,asset_id,recipient,note,franchisee_id,budget_exception) VALUES (\n" +
                (time+ 500L) + ",7,500000,3,'DB손해보험','보험금',NULL,1);\n";
        Log.i("Create Tables",query);
        zDbIO.rawQuery(db, query);

        query = "INSERT INTO transactions (transaction_time,category_id,amount,asset_id,recipient,note,franchisee_id,budget_exception) VALUES (\n" +
                (time+ 100000L) + ",1,50000000,1,'월급','',NULL,1);\n" ;
        Log.i("Create Tables",query);
        zDbIO.rawQuery(db, query);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    static class zDbIO {
        static int putRecord(SQLiteDatabase db, String table, ContentValues values) {
            try {
                return (int) db.insertOrThrow(table, null, values);
            } catch (SQLiteConstraintException e) {
                Log.e("PUT record error", e.getMessage());
                return -1;
            }
        }

        static boolean updateRecord(SQLiteDatabase db, String table, ContentValues values,  String where) {
            try {
                db.update(table, values, where, null);
            } catch (SQLiteConstraintException e) {
                Log.e("Update record error", e.getMessage());
                return false;
            }
            return true;
        }

        static Cursor getRecordList(SQLiteDatabase db, String table, String[] columns, String where, String orderBy) {
            return db.query(table, columns, where, null, null, null, orderBy);
        }

        static void delRecord(SQLiteDatabase db, String table, long id) {
            db.delete(table, TABLE_ID + " = " + id,null );
        }

        static void rawQuery(SQLiteDatabase db, String query){
            try {
                db.execSQL(query);
            }
            catch (SQLException e){
                e.printStackTrace();
                Log.e("rawQuery Error : ",e.getLocalizedMessage());
            }
        }
    }
}
