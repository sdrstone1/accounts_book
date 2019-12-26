package com.example.kollhong.accounts3;

import java.text.DateFormat;
import java.util.Date;

import static java.text.DateFormat.getDateInstance;

public abstract class DBItem {
    public static final int TABLE_ASSET = 101;
    public static final int TABLE_CARD_INFO = 102;
    public static final int TABLE_CATEGORY = 103;
    public static final int TABLE_LEARN = 104;
    public static final int TABLE_FRANCHISEE_CODE = 105;
    public static final int TABLE_TRANSACTIONS = 106;
    public static final int TABLE_TRANSACTIONS_VIEW = 107;
    static final int ASSET = 201;
    static final int CATEGORY = 202;
    static final int SETTINGS = 203;
    static final int A_Trans0_History_Header = 204;
    static final int A_Trans0_History_Content = 205;
    static final int B_Manage0_acc_Asset = 206;

    int tableId;
    abstract int getType();

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
            return TABLE_ASSET;
        }
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
            return TABLE_CARD_INFO;
        }
    }

    static class CategoryItem extends DBItem{
        int catLevel;
        int parentId;
        String name;
        String rewardExceptions;
        float budget;

        @Override
        int getType() {
            return TABLE_ASSET;
        }
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
            return TABLE_ASSET;
        }
    }
    static class FranchiseeItem extends DBItem{
        String name;
        @Override
        int getType() {
            return TABLE_ASSET;
        }
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
            return TABLE_ASSET;
        }
    }
    static class TransactionsViewItem extends DBItem{
        long transactionTime;
        float amount;
        int categoryId;
        int categoryLevel;
        String categoryName;
        String parentCategoryName;
        int assetId;
        String assetName;
        String recipientName;
        float rewardCalculated;

        @Override
        int getType() {
            return TABLE_ASSET;
        }
    }


    //_id, type, name, balance, withdrawalaccount, withdrawalday, cardid
    static class assetItem extends DBItem{
        int id;
        String name;
        int assetType = 1;
        int cardId;
        float balance;
        int withdrawalAccount;
        int withdrawalDay;
        String nickname;

        @Override
        int getType() {
            return ASSET;
        }

    }
    static class categoryItem extends DBItem {
        int id;
        String name;

        @Override
        int getType() {
            return CATEGORY;
        }
    }
    static class settingsItem extends DBItem{
        int id;
        String name;

        @Override
        int getType() {
            return SETTINGS;
        }
    }

    static class HeaderItem extends DBItem {
        long transactionTime;
        DateFormat df = getDateInstance(DateFormat.MEDIUM);
        Date date = new Date();
        String format;

        @Override
        int getType() {
            return A_Trans0_History_Header;
        }
    }

    static class ContentItem extends DBItem {
        int transId;
        String recipientName;
        String assetName;
        String categoryName;
        String parentCategoryName;
        float amount;
        int categoryLevel;

        //amount, recipient, account, category
        @Override
        int getType() {
            return A_Trans0_History_Content;
        }
    }



    public static class ItemPerAcc extends DBItem{
        String assetName;

        float amountIn;
        float amountOut;
        float amountRemain;
        float reward = 0;

        @Override
        int getType() {
            return B_Manage0_acc_Asset;
        }
    }

}
