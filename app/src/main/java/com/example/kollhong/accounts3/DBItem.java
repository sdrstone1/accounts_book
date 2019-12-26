package com.example.kollhong.accounts3;

import java.text.DateFormat;
import java.util.Date;

import static java.text.DateFormat.getDateInstance;

public abstract class DBItem {
    public static final int TYPE_ASSET = 101;
    public static final int TYPE_CARD_INFO = 102;
    public static final int TYPE_CATEGORY = 103;
    public static final int TYPE_LEARN = 104;
    public static final int TYPE_FRANCHISEE_CODE = 105;
    public static final int TYPE_TRANSACTIONS = 106;
    public static final int TYPE_TRANSACTIONS_VIEW = 107;
    public static final int TYPE_DATA_RECIPIENT = 106;
    public static final int TYPE_DATA_TRANSACTIONS = 107;
    public static final int TYPE_DATE_HEADER = 108;
    public static final int TYPE_ASSET_SUMMARY = 109;
    public static final int TYPE_CATEGORY_SUMMARY = 110;




    //int context;
    int tableId;

    /*
    DBItem(int context) {
        this.context = context;
    }
*/
    abstract int getType();
    //int getContext(){ return context;}

    static class AssetItem extends DBItem{
        int assetType;
        String name;
        String nickname;
        float balance;
        String notes;
        int withdrawalAccount;
        int withdrawalDay;
        int cardId;

        @Override
        int getType() {
            return TYPE_ASSET;
        }
/*
        AssetItem(int context){
            super(context);
        }*/
    }

    static class CardInfoItem extends DBItem{
        String company;
        String cardName;
        int assetType;
        String rewardExceptions;//10003:10050
        String rewardSections;//b10:c90
        String franchisee_1;//10050:50000
        String amount_1;//0.8p
        String franchisee_2;
        String amount_2;//1.2d
        String franchisee_3;
        String amount_3;
        String franchisee_4;
        String amount_4;

        @Override
        int getType() {
            return TYPE_CARD_INFO;
        }
/*
        CardInfoItem(int context){
            super(context);
        }*/
    }

    static class CategoryItem extends DBItem{
        int catLevel;
        int parentId;
        String name;
        String rewardExceptions;
        float budget;

        @Override
        int getType() {
            return TYPE_CATEGORY;
        }

      /*  CategoryItem(int context){
            super(context);
        }*/
    }
    static class LearnItem extends DBItem{
        String recipientName;
        int categoryId;
        int assetId;
        int franchiseeId;
        String budgetExceptions;
        String rewardExceptions;
        String rewardType;
        float rewardPercent;

        @Override
        int getType() {
            return TYPE_LEARN;
        }
/*
        LearnItem(int context){
            super(context);
        }*/
    }
    static class FranchiseeItem extends DBItem{
        String name;
        @Override
        int getType() {
            return TYPE_FRANCHISEE_CODE;
        }

       /* FranchiseeItem(int context){
            super(context);
        }*/
    }
    static class TransactionsItem extends DBItem{
        long transactionTime;
        int categoryId;
        float amount;
        int assetId;
        String recipientName;
        String notes;
        int franchiseeId;
        String budgetExceptions;
        String rewardExceptions;
        String rewardType;
        float rewardCalculated;

        @Override
        int getType() {
            return TYPE_TRANSACTIONS;
        }

       /* TransactionsItem(int context){
            super(context);
        }*/
    }

    static class TransactionsViewItem extends DBItem{
        long transactionTime;
        float amount;
        int assetId;
        String assetName;
        int categoryId;
        int categoryLevel;
        String categoryName;
        String parentCategoryName;
        String recipientName;
        float rewardCalculated = 1.175494e-38F;

        @Override
        int getType() {
            return TYPE_TRANSACTIONS_VIEW;
        }

        /*TransactionsViewItem(int context){
            super(context);
        }*/
    }


    public static class ItemRecipient extends DBItem{ // May be Franchisee Table or Learn Table
        int recipientId;
        String recipientName;
        int rewardExceptions;
        int conditiontype =0;   //전월실적(b), 당월 실적(c)
        float conditionAmount = 0;
        int rewardType = 0 ;       //p(oint), d(iscount)
        float rewardPercent = 0;    //0.7

        @Override
        int getType() {
            return TYPE_DATA_RECIPIENT;
        }

       /* ItemRecipient(int context){
            super(context);
        }*/
    }
    public static class ItemTransactions extends DBItem{    //Combination of tables
        boolean isUpdate = false;
        boolean learn;

        int transactionId;
        int categoryId = 0;
        String categoryName = null;
        long transactionTime = 0L;
        float amount;
        int assetId = 0;
        int assetType;
        String assetName = null;
        int withdrawlDay = 0;
        int withdrawlAccount = 0;
        int cardId = 0;
        float balance;
        int franchiseeId = 0;
        String recipname = " ";
        float rewardAmount;
        float rewardAmountCalculated;
        int rewardType;//'P'oint 'D'iscount
        String notes = " ";
        int budgetException = 0;
        int rewardException;
        int conditionType;   //전월실적 당월실적
        float conditionAmount;


        @Override
        int getType() {
            return TYPE_DATA_TRANSACTIONS;
        }

/*        ItemTransactions(int context){
            super(context);
        }*/
    }

}
