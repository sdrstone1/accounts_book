package com.example.kollhong.accounts3;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteConstraintException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import static android.database.Cursor.*;

public final class zDBScheme {
    public static final String DB_NAME = "account_book";
    public static final String TABLE_ASSET = "asset";
    public static final String TABLE_CARD_INFO = "cardinfo";
    public static final String TABLE_CATEGORY = "category";
    public static final String TABLE_LEARN = "learn";
    public static final String TABLE_FRANCHISEE_CODE = "franchisee_code";
    public static final String TABLE_TRANSACTIONS = "transactions";
    public static final String TABLE_TRANSACTIONS_VIEW = "transactions_view";

    public static final int ASSET_TYPE_BANK_BOOK = 1;
    public static final int ASSET_TYPE_DEBIT_CARD = 2;
    public static final int ASSET_TYPE_CREDIT_CARD = 3;

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
        public static final String REAWRD_SECTIONS = "reawrd_sections";
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
        public static final String REWARD = "reward_amount";
    }

    public static final class FRANCHISEE_CODE_TABLE {
        public static final String NAME = "name";
    }

    public static final class TRANSACTIONS_TABLE {
        public static final String TRANSACTON_TIME = "transacton_time";
        public static final String CATEGORY_ID = "category_id";
        public static final String AMOUNT = "amount";
        public static final String ASSET_ID = "asset_id";
        public static final String RECIPIENT = "recipient";
        public static final String NOTE = "note";
        public static final String FRANCHISEE_ID = "franchisee_id";
        public static final String BUDGET_EXCEPTION = "budget_exception";
        public static final String REWARD_EXCEPTION = "reward_exception";
        public static final String REWARD_TYPE = "reward_type";
        public static final String REWARD_CACULATED = "reward_caculated";
    }


    public static final class TRANSACTIONS_VIEW {
        public static final String TRANSACTON_TIME = "transacton_time";
        public static final String AMOUNT = "amount";
        public static final String CATEGORY_ID = "category_id";
        public static final String CATEGORY_LEVEL = "category_level";
        public static final String CATEGORY_NAME = "category_name";
        public static final String PARENT_CATEGORY_NAME = "parent_category_name";
        public static final String ASSET_ID = "asset_id";
        public static final String ASSET_NAME = "asset_name";
        public static final String RECIPIENT = "recipient";
        public static final String REWARD_CACULATED = "reward_caculated";
    }

    public static class ItemRecipient {
        long recipid;
        String recipient;
        int conditionexception;
        int conditiontype =0;   //전월실적(b), 당월 실적(c)
        int conditionAmount = 0;
        int rewtype = 0 ;       //p(oint), d(iscount)
        float rewamount = 0;    //0.7


    }

    public static class ItemTransactions {
        boolean isUpdate = false;
        long transaction_id;
        long category_id = 0L;
        String category_name = null;
        long timeinmillis = 0L;
        float amount;
        long asset_id = 0L;
        long asset_type;
        String asset_name = null;
        long withdrawlday = 0L;
        long withdrawlaccount = 0L;
        long cardid = 0L;
        float balance;
        long franchisee_id = 0L;
        String recipname = " ";
        float rew_amount;
        float rew_amount_calculated;
        long rew_type;
        String note = " ";
        long budget_exception = 0L;
        long reward_exception;
        long perftype;
        float conditionAmount; //==rew_amount_calculated
        boolean learn;


        //_id, type, balance, withdrawalday, cardid
    }

    static class ItemAcc {
        long id;
        String name;
        long type = 1;
        long cardid;
        float balance;
        long withdrawalaccount;
        long withdrawalday;
        String nickname;
        //_id, type, name, balance, withdrawalaccount, withdrawalday, cardid
    }

    static class zDbIO {
        static boolean creTable(SQLiteDatabase db, String table, String tablearg) {
            try {
                db.execSQL("CREATE TABLE IF NOT EXISTS " + table + " ( " + tablearg + " ); ");
            } catch (SQLException e) {
                e.getLocalizedMessage();
                return false;
            }
            return true;
        }

        static long putRecord(SQLiteDatabase db, String table, ContentValues values) {
            try {
                return db.insertOrThrow(table, null, values);
            } catch (SQLiteConstraintException e) {
                Log.e("PUT record error", e.getMessage());
                return -1L;
            }
        }

        static boolean updateRecord(SQLiteDatabase db, String table, ContentValues values,  String where, String[] whereArgs) {
            try {
                db.update(table, values, where, whereArgs);
            } catch (SQLiteConstraintException e) {
                Log.e("Update record error", e.getMessage());
                return false;
            }
            return true;
        }

        static List<ContentValues> getRecordList(SQLiteDatabase db, String table, String[] columns, String where, String[] whereArgs, String orderBy) {
            Cursor cursor = db.query(table, columns, where, whereArgs, null, null, orderBy);
            List<ContentValues> contentValuesList = new ArrayList<>();
            if (cursor.getCount() != 0) {
                while (cursor.moveToNext()) {
                    ContentValues contentValues = new ContentValues();   //거래 기록 표시
                    for (int i = 0; i < cursor.getColumnCount(); i++) {
                        switch( cursor.getType(i)) {

                            case FIELD_TYPE_INTEGER:
                                if( BuildConfig.isTEST){
                                    Log.i("Get DB Record : ", "Long");
                                }
                                contentValues.put(cursor.getColumnName(i), cursor.getLong(i));
                                break;
                            case FIELD_TYPE_STRING:
                                if( BuildConfig.isTEST){
                                    Log.i("Get DB Record : ", "String");
                                }
                                contentValues.put(cursor.getColumnName(i), cursor.getString(i));
                                break;
                            case FIELD_TYPE_FLOAT:
                                if( BuildConfig.isTEST){
                                    Log.i("Get DB Record : ", "Float");
                                }
                                contentValues.put(cursor.getColumnName(i), cursor.getFloat(i));
                                break;
                            case FIELD_TYPE_BLOB:
                                Log.e("Get DB Record Error : ", "Column Type Blob");
                                break;
                            case FIELD_TYPE_NULL:
                                Log.e("Get DB Record Error : ", "Column NULL");
                                break;
                        }
                    }
                    contentValuesList.add(contentValues);
                }
            }
            cursor.close();
            return contentValuesList;
        }

        static void delRecord(SQLiteDatabase db, String table, long id) {
            db.delete(table, "? = '?'", new String[]{TABLE_ID, Long.toString(id)});
           // return true;
        }

        static void rawQuery(SQLiteDatabase db, String query){
            db.rawQuery(query, null);
        }
    }
}
