package com.example.kollhong.accounts3;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteConstraintException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by KollHong on 01/05/2018.
 */

public class zDBMan {
    private SQLiteDatabase db;
    ItemTransactions data;
    Cursor cursor;
    zDbIO dbIO = new zDbIO();
    //여기서 읽기 쓰기 작업 모두 진행
    //여기서 디비 객체 선언
    //TODO 모델 영역

    public static final String DB_NAME = "account_book";
    public static final String[] TABLE_NAME = {"_id","asset","cardinfo","category","learn","franchisee_code","transactions"};
    public static final String[] ASSET_COLUMN ={"_id","asset_type","name","nickname","balance","notes","withdrawalaccount","withdrawalday","cardinfo"};    //withdrawalaccount->if
    public static final String[] CARDINFO_COLUMN ={"_id","company","card_name","asset_type","reward_exceptions","reawrd_sections","reward_franchisee1","reward_amount1","reward_franchisee2","reward_amount2","reward_franchisee3","reward_amount3","reward_franchisee4","reward_amount4"};
    public static final String[] CATEGORY_COLUMN ={"_id","cat_level","parent","name","reward_exception","budget"};
    public static final String[] LEARN_COLUMN ={"_id","recipient","category_id","asset_id","franchise_id","budget_exception","reward_exception"};
    public static final String[] FRANCHISEE_CODE_COLUMN ={"_id","name"};
    public static final String[] TRANSACTIONS_COLUMN ={"_id","transacton_time","category_id","amount","asset_id","recipient","note","franchisee_id","budget_exception","reward_exception","reward_type","reward_caculated"};

    public static final int ASSET_TYPE_CASH = 1;
    public static final int ASSET_TYPE_DEBIT_CARD = 2;
    public static final int ASSET_TYPE_CREDIT_CARD = 3;


    zDBMan(Context context, boolean RW){
        db = context.openOrCreateDatabase(DB_NAME, MODE_PRIVATE, null); //null of cursorFactory tells to use standard SQLiteCursor
        data = new ItemTransactions();
    }

    //TODO 뷰 영역
    //TODO 컨트롤러 영역은 UI부분임
    //TODO 커서 리턴하지 말기!
    //TODO 쿼리 대신 ContentValues 사용하면 함수 전부 다 합칠 수 잇을 듯
    ItemCat getItemCat(){
        return new ItemCat();
    }
    ItemAcc getItemAcc(){
        return new ItemAcc();
    }

    Cursor getTransHistory(long today00, long today2359){
        cursor = db.rawQuery("select c.name, t.amount, t.recipient, a.name, parent.name, c.level, t._id " +
                "from trans as t " +
                "left join category as c on ( t.categoryid = c._id ) " +
                "left join accounts as a on ( t.accountid = a._id ) " +
                "left join category as parent on ( c.parent = parent._id) " +
                "where t.time >= '" + today00 + "' and t.time <= '" + today2359 +
                "' order by t.time desc", null);
        return cursor;
    }

    Cursor getTransByAcc(long thisMonth, long nextMonth){

        cursor = db.rawQuery("SELECT t._id, t.categoryid, a.name as accname, t.amount, t.accountid, t.recipient, t.rewardamount, c.level " +
                "FROM trans as t " +
                "left join category as c on (t.categoryid = c._id) " +
                "left join accounts as a on ( t.accountid = a._id ) " +
                "WHERE t.time >= '" + thisMonth + "' and t.time <= '" + nextMonth +"' " +
                "order by t.accountid ",null);
        return cursor;
    }

    Cursor getTransbyCat(long thisMonth, long nextMonth){
        cursor = db.rawQuery("SELECT t._id, t.categoryid, c.name, t.amount " +
                "FROM trans as t " +
                "left join category as c on (t.categoryid = c._id) " +
                "WHERE t.time >= '" + thisMonth + "' and t.time <= '" + nextMonth +"' " +
                "order by c._id ",null);
        return cursor;

    }

    Cursor getTransbyID(long id){
        cursor = db.rawQuery("SELECT _id, time, categoryid, amount, accountid, recipient, notes, rewardrecipientid, budgetexception, rewardamount, perfexception, rewardtype FROM trans WHERE _id = '" + id + "' " ,null);
        return cursor;
    }

    String getCategoryName(long id){
        cursor = db.rawQuery("select c.name " +
                "from category c " +
                "where c._id = '" + id +
                "'", null);
        if (cursor.getCount() != 0) {
            cursor.moveToNext();
            return cursor.getString(0);
        }
        return "";
    }

    int getCatLevel(long id){
        cursor = db.rawQuery("select level " +
                "from category  " +
                "where _id = '" + id +
                "'", null);
        if (cursor.getCount() != 0) {
            cursor.moveToNext();
            return cursor.getInt(0);
        }

        return 0;
    }

    Cursor getCategoryList(String name){
        cursor = db.rawQuery("select _id from category where name = '" + name +"' ", null);
        if(cursor.getCount() != 0) {
            cursor.moveToNext(); int id = cursor.getInt(0);
            cursor = db.rawQuery("select _id, name " +
                    "from category " +
                    "where parent == '" + id +
                    "' ", null);
            return cursor;
        }else return  null;
    }


    Cursor getAccList(){
        cursor = db.rawQuery("select _id, type, name, balance, withdrawalaccount, withdrawalday, cardid " +
                "from accounts ", null);
        return cursor;
    }

    Cursor getAccInfo(long id){
        cursor = db.rawQuery("select _id, type, name, nickname, balance, withdrawalaccount, withdrawalday, cardid " +
                "from accounts " +
                "where _id = '" + id + "' ", null);

            return cursor;

    }

    Cursor getAccBankList(){
        cursor = db.rawQuery("select _id, type, name, balance, withdrawalaccount, withdrawalday, cardid " +
                "from accounts where type == '1' ", null);
        return cursor;
    }

    Cursor getCardinfo(long card_id) {
        cursor = db.rawQuery("select performanceexceptions, sections, " +
                " rewardrecip1, rewardamount1,  rewardrecip2, rewardamount2, rewardrecip3, rewardamount3, rewardrecip4, rewardamount4, rewardrecip5, rewardamount5, rewardrecip6, rewardamount6, rewardrecip7, rewardamount7, rewardrecip8, rewardamount8 " +
                "from card where _id = '" + card_id + "' ", null);
        return cursor;
    }

    Cursor getCardList(int type){
        cursor = db.rawQuery("select _id, card_name, company " +
                "from card where type = '" + type + "' ", null);
        return cursor;
    }

    String getRecipName(long id){
        cursor = db.rawQuery("select name " +
                "from reciplists " +
                "where _id = '" + id +
                "'", null);
        if (cursor.getCount() != 0) {
            cursor.moveToNext();
            return cursor.getString(0);
        }

        else return "";
    }

    Cursor getLearnData(String name){        //이름과 계좌가 같을 경우
        cursor = db.rawQuery("select _id, categoryid, accid, recipientid, budgetexception, perfexception, rewardtype, rewardamount " +
                "from learning where recipient = '" + name + "' ", null);
        return cursor;
    }



    private void updateAccBalance(long acc_id, float amount){
        Cursor tmp = getAccInfo(data.acc_id);
        float tmp_amount;
        if(tmp.getCount() != 0 ){
            tmp.moveToNext();
            tmp_amount = tmp.getFloat(4);
            amount = tmp_amount + amount;
        }
        db.execSQL("update accounts set balance = balance - '"+ amount + "' where _id = '" + acc_id + "' ");
    }

    private void updateTransaction(ItemTransactions data){
        ContentValues values = new ContentValues();
        values.put("time", data.timeinmillis);
        values.put("categoryid", data.category_id);
        values.put("amount", data.amount);
        values.put("accountid", data.acc_id);
        values.put("recipient", data.recipname);
        values.put("notes", data.note);
        values.put("rewardrecipientid", data.recipid );
        values.put("budgetexception", data.budgetexception);
        values.put("rewardamount", data.rew_amount_calculated);
        values.put("perfexception", data.perfexception);
        values.put("rewardtype", data.rew_type);
        float tmp_amount = 0f;
        Cursor tmp = getTransbyID(data.trans_id);
        if(tmp.getCount() != 0){
            tmp.moveToNext();
            tmp_amount = tmp.getFloat(3);
            tmp_amount = tmp_amount - data.amount;
        }
        if( data.acc_type == 1 ){       //체크카드 출금계좌에서 잔액 보정.
            updateAccBalance(data.withdrawlaccount, tmp_amount);
        }else {
            updateAccBalance(data.acc_id, tmp_amount);
        }

        db.update("trans", values, "_id = " + data.trans_id, null );

    }

    void updateCat(long id, String name){
        ContentValues values = new ContentValues();
        values.put("name",name);

        db.update("category",values,"_id = " + id,null);
    }


    void addAcc(boolean isUpdate, ItemAcc itemAcc){
        /*
                long id;
        String name;
        int type = 1;
        long cardid;
        float balance;
        int withdrawalaccount;
        int withdrawalday;
        String nickname;
         */

        ContentValues values = new ContentValues();
        values.put("name", itemAcc.name);

        values.put("cardid", itemAcc.cardid);
        values.put("nickname",itemAcc.nickname);

        if(itemAcc.type == 1 ){     //현금
            values.put("balance", itemAcc.balance);
        }
        else if (itemAcc.type == 2){       //체크카드
            values.put("withdrawalaccount", itemAcc.withdrawalaccount);
        }
        else if (itemAcc.type == 3){       //신용카드
            values.put("balance", itemAcc.balance);
            values.put("withdrawalaccount", itemAcc.withdrawalaccount);
            values.put("withdrawalday", itemAcc.withdrawalday);
        }

        if(isUpdate) {      //id확인


            db.update("accounts", values, " _id = '" + itemAcc.id + "' ",null);
        }
        else{
            db.insert("accounts", null, values);
        }
        //타입에 따라 바꾸기
    }

    void addCat(int cat_Level, Long parent, String name){

        ContentValues values = new ContentValues();
        values.put("level", cat_Level + 1);
        values.put("parent", parent);
        values.put("name",name);

        db.insert("category",null, values);
    }


    private void addTransaction(ItemTransactions data){
        ContentValues values = new ContentValues();
        values.put("time", data.timeinmillis);
        values.put("categoryid", data.category_id);
        values.put("amount", data.amount);
        values.put("accountid", data.acc_id);
        values.put("recipient", data.recipname);
        values.put("notes", data.note);
        values.put("rewardrecipientid", data.recipid );
        values.put("budgetexception", data.budgetexception);
        values.put("rewardamount", data.rew_amount_calculated);
        values.put("perfexception", data.perfexception);
        values.put("rewardtype", data.rew_type);


        db.insert("trans",null, values);
    }

    long addTransactionfromReciever(ItemTransactions data) {
        ContentValues values = new ContentValues();

        values.put("time", data.timeinmillis);
        values.put("categoryid", data.category_id);
        values.put("amount", data.amount);

        values.put("recipient", data.recipname);
        values.put("rewardrecipientid", data.recipid );
        values.put("budgetexception", data.budgetexception);
        values.put("perfexception", data.perfexception);
        values.put("rewardtype", data.rew_type);
        values.put("rewardamount", data.rew_amount_calculated);
        if (data.acc_id != 0 ) {
            values.put("accountid", data.acc_id);
            updateAccBalance(data.acc_id, data.amount);
        }


        return db.insert("trans",null, values);
    }



    void addTransactiononSave(ItemTransactions data){     //잔액 조절, 카드 확인, 기록 추가
        int tmp = getCatLevel(data.cardid);
        switch (tmp){
            case 0:
            case 1:
            case 2: {       //수입 -> data.amount만큼 잔액 더함.

                if (data.acc_type == 1) {       //체크카드 출금계좌에서 잔액 보정.
                    updateAccBalance(data.withdrawlaccount, data.amount);
                } else {
                    updateAccBalance(data.acc_id, data.amount);
                }
                break;
            }
            case 3:
            case 4:
            case 5:
            case 6:
            case 7:
            case 8:{
                if (data.acc_type == 1) {       //체크카드 출금계좌에서 잔액 보정.
                    updateAccBalance(data.withdrawlaccount, -data.amount);
                } else {
                    updateAccBalance(data.acc_id, -data.amount);
                }
                break;
            }
        }

        if(data.isUpdate) {
            updateTransaction(data);//TODO ifUpdate -> id 값도 받아서 sql update하기
        }else{
            addTransaction(data);
        }
        if(data.learn){
            updateLearn(data);
        }
    }

    private void updateLearn(ItemTransactions data){
        ContentValues values = new ContentValues();
        values.put("recipient", data.recipname);
        values.put("categoryid", data.category_id);
        values.put("accid", data.acc_id);
        values.put("recipientid", data.recipid);
        values.put("budgetexception", data.budgetexception);
        values.put("perfexception", data.perfexception);
        values.put("rewardtype", data.rew_type);
        values.put("rewardamount", data.rew_amount);


        try{db.insertOrThrow("learning",null, values);}
        catch (SQLiteConstraintException e){
            db.update("learning", values, "recipient = '"+ data.recipname+ "' ",null);
        }
    }


    void deleteTrans(long id){
        db.delete("Trans","_id = '" + id +"' ",null);
    }

    void deleteAcc(long id){
        db.delete("accounts","_id = '" + id +"' ",null);
    }

    void deleteCat(long id){
        db.delete("category","_id = '" + id+ "' ",null);
    }

    class ItemTransactions {
        boolean isUpdate = false;
        long trans_id;
        long category_id = 0;
        String category_name = null;
        long timeinmillis = 0L;
        float amount;
        long acc_id = 0;
        int acc_type;
        String acc_name = null;
        int withdrawlday = 0;
        int withdrawlaccount = 0;
        long cardid = 0;
        float balance;
        long recipid = 0;
        String recipname = " ";
        float rew_amount;
        float rew_amount_calculated;
        int rew_type;
        String note = " ";
        int budgetexception = 0;
        int perfexception;
        int perftype;
        float perfamount;
        boolean learn;


        //_id, type, balance, withdrawalday, cardid
    }

    class ItemCat {
        long id;
        String name;
    }

    class ItemAcc {
        long id;
        String name;
        int type = 1;
        long cardid;
        float balance;
        int withdrawalaccount;
        int withdrawalday;
        String nickname;
        //_id, type, name, balance, withdrawalaccount, withdrawalday, cardid
    }
    void rawQuery(String query){
        db.rawQuery(query, null);
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

        static boolean putRecord(SQLiteDatabase db, String table, ContentValues values) {
            try {
                db.insertOrThrow(table, null, values);
            } catch (SQLiteConstraintException e) {
                Log.e("PUT record error", e.getMessage());
                return false;
            }
            return true;
        }


        static Cursor getRecordCursor(SQLiteDatabase db, String table, String[] col, String where) {
            return db.query(table, col, where, null, null, null, null);
        }

        static boolean delRecord(SQLiteDatabase db, String table, String where) {
            db.delete(table, where, null);
            return true;
        }

        static void rawQuery(SQLiteDatabase db, String query){
            db.rawQuery(query, null);
        }
    }
}
