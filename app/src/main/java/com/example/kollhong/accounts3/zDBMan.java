package com.example.kollhong.accounts3;

import android.content.ContentValues;
import android.content.Context;
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


    //TODO return list of objects
    List<ContentValues> getTransHistory(long today00, long today2359){
        String[] columns = {TABLE_ID, TRANSACTIONS_VIEW.TRANSACTON_TIME,TRANSACTIONS_VIEW.AMOUNT,TRANSACTIONS_VIEW.RECIPIENT,TRANSACTIONS_VIEW.CATEGORY_LEVEL,TRANSACTIONS_VIEW.CATEGORY_NAME,TRANSACTIONS_VIEW.PARENT_CATEGORY_NAME,TRANSACTIONS_VIEW.ASSET_NAME};
        Cursor cursor = zDbIO.getRecordList(db, TABLE_TRANSACTIONS_VIEW, columns,
                "? >= ? and ? <= ?",
                new String[] { TRANSACTIONS_VIEW.TRANSACTON_TIME, Long.toString(today00),TRANSACTIONS_VIEW.TRANSACTON_TIME, Long.toString(today2359)},
                TRANSACTIONS_VIEW.TRANSACTON_TIME + "desc");
        if(cursor.getCount != 0){
            while(cursor.moveToNext()){
                object.id = cursor.getString(cursor.getColumnIndex(TABLE_ID));

            }
        }
        cursor.close();
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

    List<ContentValues> getChildCategoryList(String name){
        String[] columns ={TABLE_ID, CATEGORY_TABLE.NAME};
        List<ContentValues> contentValuesList = zDbIO.getRecordList(db, TABLE_CATEGORY, columns, //select all columns
                "? = '?' ", new String[] { CATEGORY_TABLE.PARENT_ID, name}, null);
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
        if(contentValuesList.isEmpty()) return null;
        return contentValuesList.get(0);
    }

    List<ContentValues> getBankAssetList_forCard(){
        String[] columns ={TABLE_ID, ASSET_TABLE.NAME};
        List<ContentValues> contentValuesList = zDbIO.getRecordList(db, TABLE_ASSET, columns, //select all columns
                "? = '?' ", new String[] { ASSET_TABLE.ASSET_TYPE, Integer.toString(ASSET_TYPE_BANK_BOOK)}, null);
        return contentValuesList;
    }

    ContentValues getCardinfo(long card_id) {
        List<ContentValues> contentValuesList = zDbIO.getRecordList(db, TABLE_CARD_INFO, null, //select all columns
                "? = '?' ", new String[] { TABLE_ID, Long.toString(card_id)}, null);
        if (contentValuesList.isEmpty())return null;
        return contentValuesList.get(0);
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

    //TODO perfexception 칼럼 없어짐
    ContentValues getLearnData(String name){        //이름과 계좌가 같을 경우
        List<ContentValues> contentValuesList = zDbIO.getRecordList(db,TABLE_LEARN, null, //select all columns
                "? = '?' ", new String[] { LEARN_TABLE.RECIPIENT, name}, null);
        if(contentValuesList.isEmpty()) return null;
        return contentValuesList.get(0);
    }




//TODO ItemTransaction를 모두 ContentValues로 바꾸기
    private void updateTransaction(ItemTransactions inputData){
        ContentValues values = new ContentValues();
        values.put(TRANSACTIONS_TABLE.TRANSACTON_TIME, inputData.timeinmillis);
        values.put(TRANSACTIONS_TABLE.CATEGORY_ID, inputData.category_id);
        values.put(TRANSACTIONS_TABLE.AMOUNT, inputData.amount);
        values.put(TRANSACTIONS_TABLE.ASSET_ID, inputData.asset_id);
        values.put(TRANSACTIONS_TABLE.RECIPIENT, inputData.recipname);
        values.put(TRANSACTIONS_TABLE.NOTE, inputData.note);
        values.put(TRANSACTIONS_TABLE.FRANCHISEE_ID, inputData.franchisee_id);
        values.put(TRANSACTIONS_TABLE.BUDGET_EXCEPTION, inputData.budget_exception);
        values.put(TRANSACTIONS_TABLE.REWARD_CACULATED, inputData.rew_amount_calculated);
        values.put(TRANSACTIONS_TABLE.REWARD_EXCEPTION, inputData.reward_exception);
        values.put(TRANSACTIONS_TABLE.REWARD_TYPE, inputData.rew_type);


        // 잔액 변동 체크
        ContentValues transbyID = getTransbyID(inputData.transaction_id);
        if(transbyID== null){
            return;
        }
        float tmp_amount = transbyID.getAsFloat(TRANSACTIONS_TABLE.AMOUNT);
        tmp_amount = tmp_amount - inputData.amount;
        if( inputData.asset_type == 1L ){       //체크카드 출금계좌에서 잔액 보정.
            updateAssetBalance(inputData.withdrawlaccount, tmp_amount);
        }
        /*else {
            updateAssetBalance(inputData.acc_id, tmp_amount);
        }
*/
        //db.update("trans", values, "_id = " + inputData.transaction_id, null );
        zDbIO.updateRecord(db, TABLE_TRANSACTIONS, values, "? = '?' ", new String[] {TABLE_ID, Long.toString(inputData.transaction_id) });
    }

    private void updateAssetBalance( long acc_id, float amount){
        ContentValues assetInfo = getAssetInfo(acc_id);
        if(assetInfo == null ){ //Balance
            Log.e("getAssetInfo : ","error");
            return;
        }
        float balance = assetInfo.getAsFloat(ASSET_TABLE.BALANCE);
        balance = balance + amount;
        ContentValues updateValues = new ContentValues();
        updateValues.put(ASSET_TABLE.BALANCE, Float.toString(balance));
        zDbIO.updateRecord(db, TABLE_ASSET, assetInfo, "? = '?' ", new String[] {TABLE_ID, Long.toString(acc_id) });
    }

    void updateCat(long id, String name){
        ContentValues values = new ContentValues();
        values.put("name",name);
        zDbIO.updateRecord(db, TABLE_CATEGORY, values, "? = '?' ", new String[] {TABLE_ID, Long.toString(id) });
    }


    void addAsset(boolean isUpdate, ItemAsset itemAsset){
        ContentValues values = new ContentValues();
        values.put(ASSET_TABLE.NAME, itemAsset.name);

        values.put(ASSET_TABLE.CARD_ID, itemAsset.cardid);
        values.put(ASSET_TABLE.NICKNAME, itemAsset.nickname);

        if(itemAsset.type == 1L ){     //현금
            values.put(ASSET_TABLE.BALANCE, itemAsset.balance);
        }
        else if (itemAsset.type == 2L){       //체크카드
            values.put(ASSET_TABLE.WITHDRAWALACCOUNT, itemAsset.withdrawalaccount);
        }
        else if (itemAsset.type == 3L){       //신용카드
            values.put(ASSET_TABLE.BALANCE, itemAsset.balance);
            values.put(ASSET_TABLE.WITHDRAWALACCOUNT, itemAsset.withdrawalaccount);
            values.put(ASSET_TABLE.WITHDRAWALDAY, itemAsset.withdrawalday);
        }

        if(isUpdate) {      //id확인
            zDbIO.updateRecord(db, TABLE_ASSET, values, "? = '?' ", new String[] {TABLE_ID, Long.toString(itemAsset.id) });
        }
        else{
            //db.insert("accounts", null, values);
            zDbIO.putRecord(db,TABLE_ASSET,values);
        }
        //타입에 따라 바꾸기
    }

    void addCat(int cat_Level, Long parent, String name){

        ContentValues values = new ContentValues();
        values.put(CATEGORY_TABLE.CAT_LEVEL, cat_Level + 1);
        values.put(CATEGORY_TABLE.PARENT_ID, parent);
        values.put(CATEGORY_TABLE.NAME,name);
        zDbIO.putRecord(db,TABLE_CATEGORY,values);
    }


    private void addTransaction(ItemTransactions data){
        ContentValues values = new ContentValues();
        values.put(TRANSACTIONS_TABLE.TRANSACTON_TIME, data.timeinmillis);
        values.put(TRANSACTIONS_TABLE.CATEGORY_ID, data.category_id);
        values.put(TRANSACTIONS_TABLE.AMOUNT, data.amount);
        values.put(TRANSACTIONS_TABLE.ASSET_ID, data.asset_id);
        values.put(TRANSACTIONS_TABLE.RECIPIENT, data.recipname);
        values.put(TRANSACTIONS_TABLE.NOTE, data.note);
        values.put(TRANSACTIONS_TABLE.FRANCHISEE_ID, data.franchisee_id);
        values.put(TRANSACTIONS_TABLE.BUDGET_EXCEPTION, data.budget_exception);
        values.put(TRANSACTIONS_TABLE.REWARD_CACULATED, data.rew_amount_calculated);
        values.put(TRANSACTIONS_TABLE.REWARD_EXCEPTION, data.reward_exception);
        values.put(TRANSACTIONS_TABLE.REWARD_TYPE, data.rew_type);


        zDbIO.putRecord(db,TABLE_TRANSACTIONS,values);
    }

    long addTransactionfromReciever(ItemTransactions data) {
        ContentValues values = new ContentValues();

        values.put(TRANSACTIONS_TABLE.TRANSACTON_TIME, data.timeinmillis);
        values.put(TRANSACTIONS_TABLE.CATEGORY_ID, data.category_id);
        values.put(TRANSACTIONS_TABLE.AMOUNT, data.amount);

        values.put(TRANSACTIONS_TABLE.RECIPIENT, data.recipname);
        values.put(TRANSACTIONS_TABLE.FRANCHISEE_ID, data.franchisee_id);
        values.put(TRANSACTIONS_TABLE.BUDGET_EXCEPTION, data.budget_exception);
        values.put(TRANSACTIONS_TABLE.REWARD_CACULATED, data.rew_amount_calculated);
        values.put(TRANSACTIONS_TABLE.REWARD_EXCEPTION, data.reward_exception);
        values.put(TRANSACTIONS_TABLE.REWARD_TYPE, data.rew_type);
        if (data.asset_id != 0L ) {
            values.put(TRANSACTIONS_TABLE.ASSET_ID, data.asset_id);
            updateAssetBalance(data.asset_id, data.amount);
        }


        return zDbIO.putRecord(db,TABLE_TRANSACTIONS,values);
    }



    void addTransactiononSave(ItemTransactions data){     //잔액 조절, 카드 확인, 기록 추가
        int tmp = getCatLevel(data.cardid);
        switch (tmp){
            case 0:
            case 1:
            case 2: {       //수입 -> data.amount만큼 잔액 더함.

                if (data.asset_type == 1L) {       //체크카드 출금계좌에서 잔액 보정.
                    updateAssetBalance( data.withdrawlaccount, data.amount);
                } else {
                    updateAssetBalance( data.asset_id, data.amount);
                }
                break;
            }
            case 3:
            case 4:
            case 5:
            case 6:
            case 7:
            case 8:{
                if (data.asset_type == 1L) {       //체크카드 출금계좌에서 잔액 보정.
                    updateAssetBalance(data.withdrawlaccount, -data.amount);
                } else {
                    updateAssetBalance(data.asset_id, -data.amount);
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
        values.put(LEARN_TABLE.RECIPIENT, data.recipname);
        values.put(LEARN_TABLE.CATEGORY_ID, data.category_id);
        values.put(LEARN_TABLE.ASSET_ID, data.asset_id);
        values.put(LEARN_TABLE.FRANCHISEE_ID, data.franchisee_id);
        values.put(LEARN_TABLE.BUDGET_EXCEPTION, data.budget_exception);
        values.put(LEARN_TABLE.REWARD_EXCEPTION, data.reward_exception);
        values.put(LEARN_TABLE.REWARD_TYPE, data.rew_type);
        values.put(LEARN_TABLE.REWARD, data.rew_amount);



        try{db.insertOrThrow(TABLE_LEARN,null, values);}
        catch (SQLiteConstraintException e){
            db.update(TABLE_LEARN, values, "? = '?'",new String[]{LEARN_TABLE.RECIPIENT, data.recipname});
        }
    }


    void deleteTrans(long id){
        zDbIO.delRecord(db,TABLE_TRANSACTIONS,id);
    }

    void deleteAcc(long id){
        zDbIO.delRecord(db,TABLE_ASSET, id);
    }

    void deleteCat(long id){
        zDbIO.delRecord(db,TABLE_CATEGORY, id);
    }


    public void rawQuery(String query) {
        zDbIO.rawQuery(db,query);
    }
}
