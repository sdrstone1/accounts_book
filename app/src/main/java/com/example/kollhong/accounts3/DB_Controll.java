package com.example.kollhong.accounts3;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteConstraintException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import com.example.kollhong.accounts3.DBItem.*;

import java.util.ArrayList;
import java.util.List;

import static com.example.kollhong.accounts3.DB_Scheme.*;
import static com.example.kollhong.accounts3.DB_Scheme.ASSET_TABLE.ASSET_TYPE;
import static com.example.kollhong.accounts3.DB_Scheme.CATEGORY_TABLE.PARENT_ID;
import static com.example.kollhong.accounts3.DB_Scheme.LEARN_TABLE.ASSET_ID;
import static com.example.kollhong.accounts3.DB_Scheme.LEARN_TABLE.CATEGORY_ID;
import static com.example.kollhong.accounts3.DB_Scheme.LEARN_TABLE.RECIPIENT;
import static com.example.kollhong.accounts3.DB_Scheme.LEARN_TABLE.*;
import static com.example.kollhong.accounts3.DB_Scheme.TRANSACTIONS_TABLE.NOTE;
import static com.example.kollhong.accounts3.DB_Scheme.TRANSACTIONS_VIEW.*;

/**
 * Created by KollHong on 01/05/2018.
 */

public class DB_Controll {
    private SQLiteDatabase db;
    //여기서 읽기 쓰기 작업 모두 진행
    //여기서 디비 객체 선언


    private DB_Scheme dbHelper;




    DB_Controll(Context context, boolean RW){

        //db = context.openOrCreateDatabase(DB_NAME, MODE_PRIVATE, null); //null of cursorFactory tells to use standard SQLiteCursor
        //db.needUpgrade(int version);
        //db.getVersion();
        //db.setVersion(int version);

        dbHelper = new DB_Scheme(context,DB_NAME,null,DB_VERSION);
        if(RW) {
            db = dbHelper.getWritableDatabase();
        }
        else{
            db= dbHelper.getReadableDatabase();
        }
    }



    List<TransactionsViewItem> getTransHistory(long today, long tomorrow){
        String[] columns = {TABLE_ID, TRANSACTION_TIME,AMOUNT,RECIPIENT,CATEGORY_LEVEL,CATEGORY_NAME,PARENT_CATEGORY_NAME,ASSET_NAME};
        Cursor cursor = zDbIO.getRecordList(db, TABLE_TRANSACTIONS_VIEW, columns,
                TRANSACTION_TIME + " >= " + today + " and " + TRANSACTION_TIME + " <= "+ tomorrow,
                TRANSACTION_TIME + " desc");


        List<TransactionsViewItem> DBtransactionsItemList = new ArrayList<>();
        TransactionsViewItem DBtransactionsItem;
        //1577617134698

        //Log.i("Transaction_History",today + "    " + tomorrow);
        if(cursor.getCount() != 0){
            if(BuildConfig.isTEST){
                Log.i("Transaction_History", "발견 됨 : "+ cursor.getCount());
            }
            while(cursor.moveToNext()){
                DBtransactionsItem = new TransactionsViewItem();
                DBtransactionsItem.tableId = cursor.getInt(cursor.getColumnIndex(TABLE_ID));
                DBtransactionsItem.transactionTime = cursor.getLong(cursor.getColumnIndex(TRANSACTION_TIME));
                DBtransactionsItem.amount = cursor.getFloat(cursor.getColumnIndex(AMOUNT));
                DBtransactionsItem.recipientName = cursor.getString(cursor.getColumnIndex(RECIPIENT));
                DBtransactionsItem.categoryLevel = cursor.getInt(cursor.getColumnIndex(CATEGORY_LEVEL));
                DBtransactionsItem.categoryName = cursor.getString(cursor.getColumnIndex(CATEGORY_NAME));
                DBtransactionsItem.parentCategoryName = cursor.getString(cursor.getColumnIndex(PARENT_CATEGORY_NAME));
                DBtransactionsItem.assetName = cursor.getString(cursor.getColumnIndex(ASSET_NAME));
                DBtransactionsItemList.add(DBtransactionsItem);
            }
        }
        cursor.close();
        return DBtransactionsItemList;
    }

    List<TransactionsViewItem> getTransByAcc(long thisMonth, long nextMonth){
        String[] columns ={TABLE_ID, TRANSACTION_TIME, AMOUNT, RECIPIENT, CATEGORY_LEVEL, CATEGORY_ID,ASSET_ID,ASSET_NAME,REWARD_CACULATED};
        Cursor cursor = zDbIO.getRecordList(db, TABLE_TRANSACTIONS_VIEW, columns,
                TRANSACTION_TIME + " >= " + thisMonth + " and " + TRANSACTION_TIME + " <= "+ nextMonth,
                TRANSACTIONS_VIEW.ASSET_ID);
        List<TransactionsViewItem> DBtransactionsItemList = new ArrayList<>();
        TransactionsViewItem DBtransactionsItem;
        if(cursor.getCount() != 0){
            while(cursor.moveToNext()){
                DBtransactionsItem = new TransactionsViewItem();
                DBtransactionsItem.tableId = cursor.getInt(cursor.getColumnIndex(TABLE_ID));
                DBtransactionsItem.transactionTime = cursor.getLong(cursor.getColumnIndex(TRANSACTION_TIME));
                DBtransactionsItem.amount = cursor.getFloat(cursor.getColumnIndex(AMOUNT));
                DBtransactionsItem.recipientName = cursor.getString(cursor.getColumnIndex(RECIPIENT));
                DBtransactionsItem.categoryLevel = cursor.getInt(cursor.getColumnIndex(CATEGORY_LEVEL));
                DBtransactionsItem.categoryId = cursor.getInt(cursor.getColumnIndex(CATEGORY_ID));
                DBtransactionsItem.assetId = cursor.getInt(cursor.getColumnIndex(ASSET_ID));
                DBtransactionsItem.assetName = cursor.getString(cursor.getColumnIndex(ASSET_NAME));

                int index = cursor.getColumnIndexOrThrow(REWARD_CACULATED);
                if( index != -1){
                    DBtransactionsItem.rewardCalculated = cursor.getFloat(index);
                }

                DBtransactionsItemList.add(DBtransactionsItem);
            }
        }
        cursor.close();
        return DBtransactionsItemList;
    }

    List<TransactionsViewItem> getTransbyCat(long thisMonth, long nextMonth){
        String[] columns ={TABLE_ID, AMOUNT,CATEGORY_ID, CATEGORY_NAME };
        Cursor cursor = zDbIO.getRecordList(db, TABLE_TRANSACTIONS_VIEW, columns,
                TRANSACTION_TIME + " >= " + thisMonth + " and " + TRANSACTION_TIME + " <= "+ nextMonth,
                TRANSACTIONS_VIEW.CATEGORY_ID);
        List<TransactionsViewItem> DBtransactionsItemList = new ArrayList<>();
        TransactionsViewItem DBtransactionsItem;
        if(cursor.getCount() != 0){
            while(cursor.moveToNext()){
                DBtransactionsItem = new TransactionsViewItem();
                DBtransactionsItem.tableId = cursor.getInt(cursor.getColumnIndex(TABLE_ID));
                DBtransactionsItem.amount = cursor.getFloat(cursor.getColumnIndex(AMOUNT));
                DBtransactionsItem.categoryId = cursor.getInt(cursor.getColumnIndex(CATEGORY_ID));
                DBtransactionsItem.categoryName = cursor.getString(cursor.getColumnIndex(CATEGORY_NAME));
                DBtransactionsItemList.add(DBtransactionsItem);
            }
        }
        cursor.close();
        return DBtransactionsItemList;
    }

    TransactionsItem getTransbyID(long id){
        Cursor cursor = zDbIO.getRecordList(db, TABLE_TRANSACTIONS_VIEW, null, //select all columns
                TABLE_ID + " = " + id , null);

        if(cursor.getCount() != 0){

            cursor.moveToNext();
            TransactionsItem DBtransactionsItem = new TransactionsItem();
            DBtransactionsItem.tableId = cursor.getInt(cursor.getColumnIndex(TABLE_ID));
            DBtransactionsItem.transactionTime = cursor.getLong(cursor.getColumnIndex(TRANSACTION_TIME));
            DBtransactionsItem.categoryId = cursor.getInt(cursor.getColumnIndex(CATEGORY_ID));
            DBtransactionsItem.amount = cursor.getFloat(cursor.getColumnIndex(AMOUNT));
            DBtransactionsItem.assetId = cursor.getLong(cursor.getColumnIndex(ASSET_ID));
            DBtransactionsItem.recipientName = cursor.getString(cursor.getColumnIndex(RECIPIENT));
            if(!cursor.isNull(cursor.getColumnIndex(NOTE))) {
                DBtransactionsItem.notes = cursor.getString(cursor.getColumnIndex(NOTE));
            }
            if(!cursor.isNull(cursor.getColumnIndex(FRANCHISEE_ID))) {
                DBtransactionsItem.franchiseeId = cursor.getLong(cursor.getColumnIndex(FRANCHISEE_ID));
            }
            if(!cursor.isNull(cursor.getColumnIndex(BUDGET_EXCEPTION))) {
                DBtransactionsItem.budgetException = cursor.getInt(cursor.getColumnIndex(BUDGET_EXCEPTION));
            }
            if(!cursor.isNull(cursor.getColumnIndex(REWARD_EXCEPTION))) {
                DBtransactionsItem.rewardException = cursor.getInt(cursor.getColumnIndex(REWARD_EXCEPTION));
            }
            if(!cursor.isNull(cursor.getColumnIndex(REWARD_TYPE))) {
                DBtransactionsItem.rewardType = cursor.getInt(cursor.getColumnIndex(REWARD_TYPE));
            }
            if(!cursor.isNull(cursor.getColumnIndex(REWARD_CACULATED))) {
                DBtransactionsItem.rewardCalculated = cursor.getFloat(cursor.getColumnIndex(REWARD_CACULATED));
            }
            cursor.close();

            return DBtransactionsItem;
        }
        else{
            cursor.close();
            return null;
        }

    }

    String getCategoryName(long id){
        String[] columns ={CATEGORY_TABLE.NAME};
        Cursor cursor = zDbIO.getRecordList(db, TABLE_CATEGORY, columns, //select all columns
                TABLE_ID + " = " + id , null);

        if(cursor.getCount() != 0){
            cursor.moveToNext();

            String name = cursor.getString(cursor.getColumnIndex(CATEGORY_TABLE.NAME));
            cursor.close();

            return name;
        }
        else{
            cursor.close();
            Log.e("Get Category Name ","By CATEGORY ID Failed : No Matching Category ID");
            return null;
        }

    }

    int getCatLevel(long id){
        String[] columns ={CATEGORY_TABLE.CAT_LEVEL};
        Cursor cursor = zDbIO.getRecordList(db, TABLE_CATEGORY, columns, //select all columns
                TABLE_ID + " = " + id , null);

        if(cursor.getCount() != 0){
            cursor.moveToNext();

            int level = cursor.getInt(cursor.getColumnIndex(CATEGORY_TABLE.CAT_LEVEL));
            cursor.close();

            return level;
        }
        else {
            cursor.close();
            Log.e("Get Category Level ", "By CATEGORY ID Failed : No Matching Category ID");
            return -1;
        }
    }

    List<CategoryItem> getChildCategoryList(long id){
        String[] columns ={TABLE_ID, CATEGORY_TABLE.NAME};
        if(BuildConfig.isTEST){
            Log.i("Get Category List : ","Category ID : "+id);
        }
        Cursor cursor = zDbIO.getRecordList(db, TABLE_CATEGORY, columns, //select all columns
                PARENT_ID + " = " + id , null);
        List<CategoryItem> DBCategoryItemList = new ArrayList<>();
        CategoryItem DBCategoryItem;
        if(cursor.getCount() != 0){
            while(cursor.moveToNext()){
                DBCategoryItem = new CategoryItem();
                DBCategoryItem.tableId = cursor.getInt(cursor.getColumnIndex(TABLE_ID));
                DBCategoryItem.name = cursor.getString(cursor.getColumnIndex(CATEGORY_TABLE.NAME));
                DBCategoryItemList.add(DBCategoryItem);
            }
        }
        cursor.close();
        return DBCategoryItemList;
    }

    /*List<CategoryItem> getChildCategoryList(String name){
        String[] columns ={TABLE_ID, CATEGORY_TABLE.NAME};
        Cursor cursor = zDbIO.getRecordList(db, TABLE_CATEGORY, columns, //select all columns
                PARENT_CATEGORY_NAME + " = '" + name + "' ", null);
        List<CategoryItem> DBCategoryItemList = new ArrayList<>();
        CategoryItem DBCategoryItem;
        if(cursor.getCount() != 0){
            while(cursor.moveToNext()){
                DBCategoryItem = new CategoryItem();
                DBCategoryItem.tableId = cursor.getInt(cursor.getColumnIndex(TABLE_ID));
                DBCategoryItem.name = cursor.getString(cursor.getColumnIndex(CATEGORY_TABLE.NAME));
                DBCategoryItemList.add(DBCategoryItem);
            }
        }
        cursor.close();
        return DBCategoryItemList;
    }
*/


    List<AssetItem> getAssetList(){
        Cursor cursor = zDbIO.getRecordList(db, TABLE_ASSET, null, //select all columns
                 null, null);
        List<AssetItem> DBAssetItemList = new ArrayList<>();
        AssetItem DBAssetItem;
        if(cursor.getCount() != 0){
            while(cursor.moveToNext()){
                DBAssetItem = new AssetItem();
                DBAssetItem.tableId = cursor.getInt(cursor.getColumnIndex(TABLE_ID));
                DBAssetItem.assetType = cursor.getInt(cursor.getColumnIndex(ASSET_TYPE));
                DBAssetItem.name = cursor.getString(cursor.getColumnIndex(ASSET_TABLE.NAME));
                DBAssetItem.nickname = cursor.getString(cursor.getColumnIndex(ASSET_TABLE.NICKNAME));
                DBAssetItem.balance = cursor.getInt(cursor.getColumnIndex(ASSET_TABLE.BALANCE));
                DBAssetItem.notes = cursor.getString(cursor.getColumnIndex(ASSET_TABLE.NOTES));
                DBAssetItem.withdrawalAccount = cursor.getInt(cursor.getColumnIndex(ASSET_TABLE.WITHDRAWALACCOUNT));
                DBAssetItem.withdrawalDay = cursor.getInt(cursor.getColumnIndex(ASSET_TABLE.WITHDRAWALDAY));
                DBAssetItem.cardId = cursor.getInt(cursor.getColumnIndex(ASSET_TABLE.CARD_ID));
                DBAssetItemList.add(DBAssetItem);
            }
        }
        cursor.close();
        return DBAssetItemList;
    }

    AssetItem getAssetInfo(long id){
        Cursor cursor = zDbIO.getRecordList(db, TABLE_ASSET, null, //select all columns
                TABLE_ID + " = " + id , null);

        if(cursor.getCount() != 0){
            cursor.moveToNext();
            AssetItem DBAssetItem = new AssetItem();
            DBAssetItem.tableId = cursor.getInt(cursor.getColumnIndex(TABLE_ID));
            DBAssetItem.assetType = cursor.getInt(cursor.getColumnIndex(ASSET_TYPE));
            DBAssetItem.name = cursor.getString(cursor.getColumnIndex(ASSET_TABLE.NAME));
            DBAssetItem.nickname = cursor.getString(cursor.getColumnIndex(ASSET_TABLE.NICKNAME));
            DBAssetItem.balance = cursor.getInt(cursor.getColumnIndex(ASSET_TABLE.BALANCE));
            DBAssetItem.notes = cursor.getString(cursor.getColumnIndex(ASSET_TABLE.NOTES));
            DBAssetItem.withdrawalAccount = cursor.getInt(cursor.getColumnIndex(ASSET_TABLE.WITHDRAWALACCOUNT));
            DBAssetItem.withdrawalDay = cursor.getInt(cursor.getColumnIndex(ASSET_TABLE.WITHDRAWALDAY));
            DBAssetItem.cardId = cursor.getInt(cursor.getColumnIndex(ASSET_TABLE.CARD_ID));
            cursor.close();

            return DBAssetItem;
        }
        else{
            cursor.close();
            return null;
        }
    }

    List<AssetItem> getBankAssetList_forCard(){
        String[] columns ={TABLE_ID, ASSET_TABLE.NAME};
        Cursor cursor = zDbIO.getRecordList(db, TABLE_ASSET, columns, //select all columns
                ASSET_TYPE + " = " + ASSET_TYPE_BANK_BOOK, null);
        List<AssetItem> DBAssetItemList = new ArrayList<>();
        AssetItem DBAssetItem;
        if(cursor.getCount() != 0){
            while(cursor.moveToNext()){
                DBAssetItem = new AssetItem();
                DBAssetItem.tableId = cursor.getInt(cursor.getColumnIndex(TABLE_ID));
                DBAssetItem.name = cursor.getString(cursor.getColumnIndex(ASSET_TABLE.NAME));
                DBAssetItemList.add(DBAssetItem);
            }
        }
        cursor.close();
        return DBAssetItemList;
    }

    CardInfoItem getCardinfo(long card_id) {
        Cursor cursor = zDbIO.getRecordList(db, TABLE_CARD_INFO, null, //select all columns
                TABLE_ID + " = " + card_id, null);

        if(cursor.getCount() != 0){
            cursor.moveToNext();
            CardInfoItem DBcardInfoItem = new CardInfoItem();

            DBcardInfoItem.tableId = cursor.getInt(cursor.getColumnIndex(TABLE_ID));
            DBcardInfoItem.company = cursor.getString(cursor.getColumnIndex(CARD_INFO_TABLE.COMPANY));
            DBcardInfoItem.cardName = cursor.getString(cursor.getColumnIndex(CARD_INFO_TABLE.CARD_NAME));
            DBcardInfoItem.assetType = cursor.getInt(cursor.getColumnIndex(CARD_INFO_TABLE.ASSET_TYPE));
            DBcardInfoItem.rewardExceptions = cursor.getString(cursor.getColumnIndex(CARD_INFO_TABLE.REWARD_EXCEPTIONS));
            DBcardInfoItem.rewardSections = cursor.getString(cursor.getColumnIndex(CARD_INFO_TABLE.REWARD_SECTIONS));

            for (int i = 0 ; i<4; i++) {
                if(!cursor.isNull(cursor.getColumnIndex(CARD_INFO_TABLE.REWARD_FRANCHISEE_STRING+ (i + 1)))) {
                    DBcardInfoItem.franchisee[i] = cursor.getString(cursor.getColumnIndex(CARD_INFO_TABLE.REWARD_FRANCHISEE_STRING + (i + 1)));
                    DBcardInfoItem.amount[i] = cursor.getString(cursor.getColumnIndex(CARD_INFO_TABLE.REWARD_AMOUNT_STRING + (i + 1)));
                }
                /* DBcardInfoItem.franchisee_2 = cursor.getString(cursor.getColumnIndex(CARD_INFO_TABLE.REWARD_FRANCHISEE_2));
                DBcardInfoItem.amount_2 = cursor.getString(cursor.getColumnIndex(CARD_INFO_TABLE.REWARD_AMOUNT_2));
                DBcardInfoItem.franchisee_3 = cursor.getString(cursor.getColumnIndex(CARD_INFO_TABLE.REWARD_FRANCHISEE_3));
                DBcardInfoItem.amount_3 = cursor.getString(cursor.getColumnIndex(CARD_INFO_TABLE.REWARD_AMOUNT_3));
                DBcardInfoItem.franchisee_4 = cursor.getString(cursor.getColumnIndex(CARD_INFO_TABLE.REWARD_FRANCHISEE_4));
                DBcardInfoItem.amount_4 = cursor.getString(cursor.getColumnIndex(CARD_INFO_TABLE.REWARD_AMOUNT_4));*/
            }
            cursor.close();

            return DBcardInfoItem;
        }
        else{
            cursor.close();
            return null;
        }



    }

    List<CardInfoItem> getCardListByType(int type){
        String[] columns ={TABLE_ID, CARD_INFO_TABLE.CARD_NAME, CARD_INFO_TABLE.COMPANY};
        Cursor cursor = zDbIO.getRecordList(db, TABLE_CARD_INFO, columns, //select all columns
                CARD_INFO_TABLE.ASSET_TYPE + " = " + type , null);
        List<CardInfoItem> DBcardInfoItemList = new ArrayList<>();
        CardInfoItem DBcardInfoItem;
        if(cursor.getCount() != 0){


            while(cursor.moveToNext()){
                DBcardInfoItem = new CardInfoItem();
                DBcardInfoItem.tableId = cursor.getInt(cursor.getColumnIndex(TABLE_ID));
                DBcardInfoItem.cardName = cursor.getString(cursor.getColumnIndex(CARD_INFO_TABLE.CARD_NAME));
                DBcardInfoItem.company = cursor.getString(cursor.getColumnIndex(CARD_INFO_TABLE.COMPANY));
                DBcardInfoItemList.add(DBcardInfoItem);
            }
        }
        cursor.close();
        return DBcardInfoItemList;
    }

    String getFranchiseeName(long id){
        String[] columns ={TABLE_ID, FRANCHISEE_CODE_TABLE.NAME};
        Cursor cursor = zDbIO.getRecordList(db,TABLE_FRANCHISEE_CODE, columns, //select all columns
                TABLE_ID + " = " + id , null);
        if(cursor.getCount() != 0){
            cursor.moveToNext();
            String name = cursor.getString(cursor.getColumnIndex(FRANCHISEE_CODE_TABLE.NAME));
            cursor.close();
            return name;
        }
        else{
            cursor.close();
            return null;
        }
    }

    //TODO perfexception 칼럼 없어짐
    LearnItem getLearnData(String name){        //이름과 계좌가 같을 경우
        Cursor cursor = zDbIO.getRecordList(db,TABLE_LEARN, null, //select all columns
                RECIPIENT + " = '" + name + "' ", null);

        if(cursor.getCount() != 0){
            cursor.moveToNext();
            LearnItem DBlearnItem = new LearnItem();
            DBlearnItem.tableId = cursor.getInt(cursor.getColumnIndex(TABLE_ID));
            DBlearnItem.recipientName = cursor.getString(cursor.getColumnIndex(RECIPIENT));
            DBlearnItem.categoryId = cursor.getInt(cursor.getColumnIndex(CATEGORY_ID));
            DBlearnItem.assetId = cursor.getInt(cursor.getColumnIndex(ASSET_ID));
            DBlearnItem.franchiseeId = cursor.getInt(cursor.getColumnIndex(FRANCHISEE_ID));
            DBlearnItem.budgetException = cursor.getInt(cursor.getColumnIndex(BUDGET_EXCEPTION));
            DBlearnItem.rewardException = cursor.getInt(cursor.getColumnIndex(REWARD_EXCEPTION));
            DBlearnItem.rewardType = cursor.getInt(cursor.getColumnIndex(REWARD_TYPE));
            DBlearnItem.rewardPercent = cursor.getFloat(cursor.getColumnIndex(REWARD_PERCENT));
            cursor.close();

            return DBlearnItem;
        }
        else{
            cursor.close();
            return null;
        }
    }



    private void updateTransaction(ItemTransactions inputData){
        ContentValues values = new ContentValues();
        values.put(TRANSACTIONS_TABLE.TRANSACTION_TIME, inputData.transactionTime);
        values.put(TRANSACTIONS_TABLE.CATEGORY_ID, inputData.categoryId);
        values.put(TRANSACTIONS_TABLE.AMOUNT, inputData.amount);
        values.put(TRANSACTIONS_TABLE.ASSET_ID, inputData.assetId);
        values.put(TRANSACTIONS_TABLE.RECIPIENT, inputData.recipientName);
        values.put(NOTE, inputData.notes);
        values.put(TRANSACTIONS_TABLE.FRANCHISEE_ID, inputData.franchiseeId);
        values.put(TRANSACTIONS_TABLE.BUDGET_EXCEPTION, inputData.budgetException);
        values.put(TRANSACTIONS_TABLE.REWARD_CACULATED, inputData.rewardAmountCalculated);
        values.put(TRANSACTIONS_TABLE.REWARD_EXCEPTION, inputData.rewardException);
        values.put(TRANSACTIONS_TABLE.REWARD_TYPE, inputData.rewardType);


        // 잔액 변동 체크
        TransactionsItem transbyID = getTransbyID(inputData.transactionId);
        if(transbyID != null){
            float tmp_amount = transbyID.amount;
            tmp_amount = tmp_amount - inputData.amount;
            if( inputData.assetType == 1 ){       //체크카드 출금계좌에서 잔액 보정.
                updateAssetBalance(inputData.withdrawlAccount, tmp_amount);
            }
        }


        zDbIO.updateRecord(db, TABLE_TRANSACTIONS, values, TABLE_ID + " = " + inputData.transactionId);
    }

    private void updateAssetBalance( long acc_id, float amount){
        AssetItem assetInfo = getAssetInfo(acc_id);
        if(assetInfo == null ){ //Balance
            Log.e("getAssetInfo : ","error");
            return;
        }
        float balance = assetInfo.balance + amount;
//      balance = balance + amount;
        ContentValues updateValues = new ContentValues();
        updateValues.put(ASSET_TABLE.BALANCE, Float.toString(balance));
        zDbIO.updateRecord(db, TABLE_ASSET, updateValues, TABLE_ID + " = " + acc_id);
    }

    void updateCat(long id, String name){
        ContentValues values = new ContentValues();
        values.put("name",name);
        zDbIO.updateRecord(db, TABLE_CATEGORY, values, TABLE_ID + " = " + id);
    }


    void addAsset(boolean isUpdate, AssetItem itemAsset){
        ContentValues values = new ContentValues();
        values.put(ASSET_TABLE.NAME, itemAsset.name);

        values.put(ASSET_TABLE.CARD_ID, itemAsset.cardId);
        values.put(ASSET_TABLE.NICKNAME, itemAsset.nickname);

        if(itemAsset.assetType == 1 ){     //현금
            values.put(ASSET_TABLE.BALANCE, itemAsset.balance);
        }
        else if (itemAsset.assetType == 2){       //체크카드
            values.put(ASSET_TABLE.WITHDRAWALACCOUNT, itemAsset.withdrawalAccount);
        }
        else if (itemAsset.assetType == 3){       //신용카드
            values.put(ASSET_TABLE.BALANCE, itemAsset.balance);
            values.put(ASSET_TABLE.WITHDRAWALACCOUNT, itemAsset.withdrawalAccount);
            values.put(ASSET_TABLE.WITHDRAWALDAY, itemAsset.withdrawalDay);
        }

        if(isUpdate) {      //id확인
            zDbIO.updateRecord(db, TABLE_ASSET, values, TABLE_ID + " = " + itemAsset.tableId);
        }
        else{
            //db.insert("accounts", null, values);
            zDbIO.putRecord(db,TABLE_ASSET,values);
        }
        //타입에 따라 바꾸기
    }

    void addCat(int cat_Level, long parent, String name){

        ContentValues values = new ContentValues();
        values.put(CATEGORY_TABLE.CAT_LEVEL, cat_Level + 1);
        values.put(PARENT_ID, parent);
        values.put(CATEGORY_TABLE.NAME,name);
        zDbIO.putRecord(db,TABLE_CATEGORY,values);
    }


    private void addTransaction(ItemTransactions data){
        ContentValues values = new ContentValues();
        values.put(TRANSACTIONS_TABLE.TRANSACTION_TIME, data.transactionTime);
        values.put(TRANSACTIONS_TABLE.CATEGORY_ID, data.categoryId);
        values.put(TRANSACTIONS_TABLE.AMOUNT, data.amount);
        values.put(TRANSACTIONS_TABLE.ASSET_ID, data.assetId);
        values.put(TRANSACTIONS_TABLE.RECIPIENT, data.recipientName);
        values.put(NOTE, data.notes);
        values.put(TRANSACTIONS_TABLE.FRANCHISEE_ID, data.franchiseeId);
        values.put(TRANSACTIONS_TABLE.BUDGET_EXCEPTION, data.budgetException);
        values.put(TRANSACTIONS_TABLE.REWARD_CACULATED, data.rewardAmountCalculated);
        values.put(TRANSACTIONS_TABLE.REWARD_EXCEPTION, data.rewardException);
        values.put(TRANSACTIONS_TABLE.REWARD_TYPE, data.rewardType);


        zDbIO.putRecord(db,TABLE_TRANSACTIONS,values);
    }

    int addTransactionfromReciever(ItemTransactions data) {
        ContentValues values = new ContentValues();

        values.put(TRANSACTIONS_TABLE.TRANSACTION_TIME, data.transactionTime);
        values.put(TRANSACTIONS_TABLE.CATEGORY_ID, data.categoryId);
        values.put(TRANSACTIONS_TABLE.AMOUNT, data.amount);

        values.put(TRANSACTIONS_TABLE.RECIPIENT, data.recipientName);
        values.put(TRANSACTIONS_TABLE.FRANCHISEE_ID, data.franchiseeId);
        values.put(TRANSACTIONS_TABLE.BUDGET_EXCEPTION, data.budgetException);
        values.put(TRANSACTIONS_TABLE.REWARD_CACULATED, data.rewardAmountCalculated);
        values.put(TRANSACTIONS_TABLE.REWARD_EXCEPTION, data.rewardException);
        values.put(TRANSACTIONS_TABLE.REWARD_TYPE, data.rewardType);
        if (data.assetId != 0L ) {
            values.put(TRANSACTIONS_TABLE.ASSET_ID, data.assetId);
            updateAssetBalance(data.assetId, data.amount);
        }


        return zDbIO.putRecord(db,TABLE_TRANSACTIONS,values);
    }



    void addTransactiononSave(ItemTransactions data){     //잔액 조절, 카드 확인, 기록 추가
        int tmp = getCatLevel(data.cardId);
        switch (tmp){
            case 0:
            case 1:
            case 2: {       //수입 -> data.amount만큼 잔액 더함.

                if (data.assetType == 1L) {       //체크카드 출금계좌에서 잔액 보정.
                    updateAssetBalance( data.withdrawlAccount, data.amount);
                } else {
                    updateAssetBalance( data.assetId, data.amount);
                }
                break;
            }
            case 3:
            case 4:
            case 5:
            case 6:
            case 7:
            case 8:{
                if (data.assetType == 1L) {       //체크카드 출금계좌에서 잔액 보정.
                    updateAssetBalance(data.withdrawlAccount, -data.amount);
                } else {
                    updateAssetBalance(data.assetId, -data.amount);
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
        values.put(RECIPIENT, data.recipientName);
        values.put(CATEGORY_ID, data.categoryId);
        values.put(ASSET_ID, data.assetId);
        values.put(LEARN_TABLE.FRANCHISEE_ID, data.franchiseeId);
        values.put(LEARN_TABLE.BUDGET_EXCEPTION, data.budgetException);
        values.put(REWARD_EXCEPTION, data.rewardException);
        values.put(REWARD_TYPE, data.rewardType);
        values.put(REWARD_PERCENT, data.rewardAmount);



        try{db.insertOrThrow(TABLE_LEARN,null, values);}
        catch (SQLiteConstraintException e){
            e.printStackTrace();
            zDbIO.updateRecord(db, TABLE_LEARN, values, RECIPIENT + " = '" + data.recipientName + "' ");
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
