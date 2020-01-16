package com.example.kollhong.accounts3;

import com.example.kollhong.accounts3.DBItem.AssetItem;
import com.example.kollhong.accounts3.DBItem.NameOnlyItem;
import com.example.kollhong.accounts3.DBItem.TransactionsViewItem;

import java.text.DateFormat;
import java.util.Date;

import static java.text.DateFormat.getDateInstance;

public abstract class RecyclerItem {
    static final int Settings_Asset_Item = 201;
    static final int Settings_Category_Item = 203;
    static final int Transaction_History_HEADER = 206;
    static final int Transaction_History_CONTENT = 207;
    static final int Statistics_Asset_HEADER = 208;
//    static final int B_Manage0_Asset_Content = 209;
    static final int Statistics_Category_HEADER = 211;
    //    static final int B_Manage1_stat_Transaction_Content = 212;
    static final int TransactionAdd_Activity_CATEGORY_ITEM = 213;
    static final int TransactionAdd_Activity_ASSET_ITEM = 214;


    abstract int getType();
    //_id, type, name, balance, withdrawalaccount, withdrawalday, cardid


    static class CategorySettingsItem extends RecyclerItem{
        NameOnlyItem nameOnlyItem = new NameOnlyItem();

        @Override
        int getType() {
            return Settings_Category_Item;
        }
        
    }

    static class AssetSettingsItem extends  RecyclerItem{
        AssetItem item = new AssetItem();
        @Override
        int getType() {
            return Settings_Asset_Item;
        }
    }



    static class dateHeaderItem extends RecyclerItem {
        long transactionTime;
        DateFormat df = getDateInstance(DateFormat.MEDIUM);
        Date date = new Date();
        String format;

        @Override
        int getType() {
            return Transaction_History_HEADER;
        }

    }

    static class HistoryContentItem extends RecyclerItem {
        TransactionsViewItem item = new TransactionsViewItem();

        //amount, recipient, account, category
        @Override
        int getType() {
            return Transaction_History_CONTENT;
        }
    }


    public static class AssetSummaryItem extends RecyclerItem {
        String assetName;

        float IncomeSummary;
        float ExpenseSummary;
        float TransferSummary;
        float RewardSummary = 0;

        @Override
        int getType() {
            return Statistics_Asset_HEADER;
        }
    }

    public static class CategorySummaryItem extends RecyclerItem {
        String assetName;
        float amount;
        //float amount_out;
        //float amount_rem;

        @Override
        int getType() {
            return Statistics_Category_HEADER;
        }

    }


    static class TransactionAddCategoryItem extends RecyclerItem {
        NameOnlyItem nameOnlyItem = new NameOnlyItem();

        @Override
        int getType() {
            return TransactionAdd_Activity_CATEGORY_ITEM;
        }
    }
    static class TransactionAddAssetItem extends RecyclerItem {
        AssetItem item = new AssetItem();

        @Override
        int getType() {
            return TransactionAdd_Activity_ASSET_ITEM;
        }
    }
}
