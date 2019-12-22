package com.example.kollhong.accounts3;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteConstraintException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.List;


import static android.content.Context.MODE_PRIVATE;
import static com.example.kollhong.accounts3.zDBScheme.*;

/**
 * Created by KollHong on 01/05/2018.
 */

public class zDBMan {
    private SQLiteDatabase db;
    //여기서 읽기 쓰기 작업 모두 진행
    //여기서 디비 객체 선언
    //TODO 모델 영역





    zDBMan(Context context, boolean RW){
        db = context.openOrCreateDatabase(DB_NAME, MODE_PRIVATE, null); //null of cursorFactory tells to use standard SQLiteCursor
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

    /*
     private double getRecord(long startdate, long enddate) {
        Cursor cursor = mDbMan.getRecordCursor(db, recordTable, new String[]{recordTableVar[0], recordTableVar[1]}, recordTableVar[0] + " BETWEEN '" + startdate + "' AND '" + enddate + "'");
        //SQLiteDatabase db, String table, String[] col, String where
        double record = 0d;
        if (cursor.getCount() != 0) {
            while (cursor.moveToNext()) {

                record = record + cursor.getDouble(1);
            }

        }
        cursor.close();
        return record;
    }
    //add record
    boolean putRecord(Date date, Double measurement) {
        ContentValues values = new ContentValues();
        values.put(recordTableVar[0], date.getTime());
        values.put(recordTableVar[1], measurement);
        return mDbMan.putRecord(db, recordTable, values);
    }
     */

    //TODO return list of objects
    List<ContentValues> getTransHistory(long today00, long today2359){
        String[] columns = {TABLE_ID, TRANSACTIONS_VIEW.TRANSACTON_TIME,TRANSACTIONS_VIEW.AMOUNT,TRANSACTIONS_VIEW.RECIPIENT,TRANSACTIONS_VIEW.CATEGORY_LEVEL,TRANSACTIONS_VIEW.CATEGORY_NAME,TRANSACTIONS_VIEW.PARENT_CATEGORY_NAME,TRANSACTIONS_VIEW.ASSET_NAME};
        List<ContentValues> contentValuesList = zDbIO.getRecordList(db, TABLE_TRANSACTIONS_VIEW, columns,
                "? >= ? and ? <= ?",
                new String[] { TRANSACTIONS_VIEW.TRANSACTON_TIME, Long.toString(today00),TRANSACTIONS_VIEW.TRANSACTON_TIME, Long.toString(today2359)},
                TRANSACTIONS_VIEW.TRANSACTON_TIME + "desc");
        return contentValuesList;
    }

    List<ContentValues> getTransByAcc(long thisMonth, long nextMonth){
        String[] columns ={TABLE_ID, TRANSACTIONS_VIEW.TRANSACTON_TIME, TRANSACTIONS_VIEW.AMOUNT, TRANSACTIONS_VIEW.RECIPIENT, TRANSACTIONS_VIEW.CATEGORY_LEVEL, TRANSACTIONS_VIEW.CATEGORY_ID,TRANSACTIONS_VIEW.ASSET_ID,TRANSACTIONS_VIEW.ASSET_NAME,TRANSACTIONS_VIEW.REWARD_CACULATED};
        List<ContentValues> contentValuesList = zDbIO.getRecordList(db, TABLE_TRANSACTIONS_VIEW, columns,
                "? >= ? and ? <= ?",
                new String[] { TRANSACTIONS_VIEW.TRANSACTON_TIME, Long.toString(thisMonth),TRANSACTIONS_VIEW.TRANSACTON_TIME, Long.toString(nextMonth)},
                TRANSACTIONS_VIEW.ASSET_ID);
        return contentValuesList;
    }

    List<ContentValues> getTransbyCat(long thisMonth, long nextMonth){
        String[] columns ={TABLE_ID, TRANSACTIONS_VIEW.AMOUNT,TRANSACTIONS_VIEW.CATEGORY_ID, TRANSACTIONS_VIEW.CATEGORY_NAME };
        List<ContentValues> contentValuesList = zDbIO.getRecordList(db, TABLE_TRANSACTIONS_VIEW, columns,
                "? >= ? and ? <= ?",
                new String[] { TRANSACTIONS_VIEW.TRANSACTON_TIME, Long.toString(thisMonth),TRANSACTIONS_VIEW.TRANSACTON_TIME, Long.toString(nextMonth)},
                TRANSACTIONS_VIEW.CATEGORY_ID);
        return contentValuesList;
    }

    ContentValues getTransbyID(long id){
        List<ContentValues> contentValuesList = zDbIO.getRecordList(db, TABLE_TRANSACTIONS_VIEW, null, //select all columns
                "? = '?' ", new String[] { TABLE_ID, Long.toString(id)}, null);
        if (contentValuesList.isEmpty()) return null;
        return contentValuesList.get(0);

    }

    String getCategoryName(long id){
        String[] columns ={CATEGORY_TABLE.NAME};
        List<ContentValues> contentValuesList = zDbIO.getRecordList(db, TABLE_CATEGORY, columns, //select all columns
                "? = '?' ", new String[] { TABLE_ID, Long.toString(id)}, null);
        if(contentValuesList.isEmpty()) {
            Log.e("Get Category Name ","By CATEGORY ID Failed : No Matching Category ID");
            return null;
        }

        return contentValuesList.get(0).getAsString(CATEGORY_TABLE.NAME);
    }

    int getCatLevel(long id){
        String[] columns ={CATEGORY_TABLE.CAT_LEVEL};
        List<ContentValues> contentValuesList = zDbIO.getRecordList(db, TABLE_CATEGORY, columns, //select all columns
                "? = '?' ", new String[] { TABLE_ID, Long.toString(id)}, null);
        if(contentValuesList.isEmpty()) {
            Log.e("Get Category Level ","By CATEGORY ID Failed : No Matching Category ID");
            return -1;
        }

        return contentValuesList.get(0).getAsInteger(CATEGORY_TABLE.CAT_LEVEL);
    }

    List<ContentValues> getChildCategoryList(long id){
        String[] columns ={TABLE_ID, CATEGORY_TABLE.NAME};
        List<ContentValues> contentValuesList = zDbIO.getRecordList(db, TABLE_CATEGORY, columns, //select all columns
                "? = '?' ", new String[] { CATEGORY_TABLE.PARENT_ID, Long.toString(id)}, null);
        return contentValuesList;
    }


    List<ContentValues> getAssetList(){
        List<ContentValues> contentValuesList = zDbIO.getRecordList(db, TABLE_ASSET, null, //select all columns
                null,null, null);
        return contentValuesList;
    }

    ContentValues getAssetInfo(long id){
        List<ContentValues> contentValuesList = zDbIO.getRecordList(db, TABLE_ASSET, null, //select all columns
                "? = '?' ", new String[] { TABLE_ID, Long.toString(id)}, null);
        return contentValuesList.get(0);
    }

    List<ContentValues> getBankAssetList_forCard(){
        String[] columns ={TABLE_ID, ASSET_TABLE.NAME};
        List<ContentValues> contentValuesList = zDbIO.getRecordList(db, TABLE_ASSET, columns, //select all columns
                "? = '?' ", new String[] { ASSET_TABLE.ASSET_TYPE, Integer.toString(ASSET_TYPE_BANK_BOOK)}, null);
        return contentValuesList;
    }

    List<ContentValues> getCardinfo(long card_id) {
        List<ContentValues> contentValuesList = zDbIO.getRecordList(db, TABLE_CARD_INFO, null, //select all columns
                "? = '?' ", new String[] { TABLE_ID, Long.toString(card_id)}, null);
        return contentValuesList;
    }

    List<ContentValues> getCardListByType(int type){
        String[] columns ={TABLE_ID, CARD_INFO_TABLE.CARD_NAME, CARD_INFO_TABLE.COMPANY};
        List<ContentValues> contentValuesList = zDbIO.getRecordList(db, TABLE_ASSET, columns, //select all columns
                "? = '?' ", new String[] { CARD_INFO_TABLE.ASSET_TYPE, Integer.toString(type)}, null);
        return contentValuesList;
    }

    String getFranchiseeName(long id){
        String[] columns ={TABLE_ID, FRANCHISEE_CODE_TABLE.NAME};
        List<ContentValues> contentValuesList = zDbIO.getRecordList(db,TABLE_FRANCHISEE_CODE, columns, //select all columns
                "? = '?' ", new String[] { TABLE_ID, Long.toString(id)}, null);
        if(contentValuesList.isEmpty()) return null;
        return contentValuesList.get(0).getAsString(FRANCHISEE_CODE_TABLE.NAME);
    }

    Cursor getLearnData(String name){        //이름과 계좌가 같을 경우
        Cursor cursor = db.rawQuery("select _id, categoryid, accid, recipientid, budgetexception, perfexception, rewardtype, rewardamount " +
                "from learning where recipient = '" + name + "' ", null);
        return cursor;
    }



    private void updateAccBalance(long acc_id, float amount){
        Cursor tmp = getAssetInfo(data.acc_id);
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
        long acc_type;
        String acc_name = null;
        long withdrawlday = 0;
        long withdrawlaccount = 0;
        long cardid = 0;
        float balance;
        long recipid = 0;
        String recipname = " ";
        float rew_amount;
        float rew_amount_calculated;
        long rew_type;
        String note = " ";
        long budgetexception = 0;
        long perfexception;
        long perftype;
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
        long type = 1;
        long cardid;
        float balance;
        long withdrawalaccount;
        long withdrawalday;
        String nickname;
        //_id, type, name, balance, withdrawalaccount, withdrawalday, cardid
    }

    void rawQuery(String query){
        zDbIO.rawQuery(db, query);
    }


}
