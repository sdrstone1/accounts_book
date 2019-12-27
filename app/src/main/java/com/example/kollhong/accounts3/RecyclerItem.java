package com.example.kollhong.accounts3;

import com.example.kollhong.accounts3.DBItem.AssetItem;
import com.example.kollhong.accounts3.DBItem.NameOnlyItem;
import com.example.kollhong.accounts3.DBItem.TransactionsViewItem;

import java.text.DateFormat;
import java.util.Date;

import static java.text.DateFormat.getDateInstance;

public abstract class RecyclerItem {
    static final int ASSET = 201;
    static final int CATEGORY = 202;
    static final int v_Settings0_cat = 203;
    static final int v_Settings2_ass = 204;
    static final int dateHeader = 205;
    static final int A_Trans0_History_Header = 206;
    static final int A_Trans0_History_Content = 207;
    static final int B_Manage0_Asset_Header = 208;
    static final int B_Manage0_Asset_Content = 209;
    static final int B_Manage1_stat_Asset = 210;
    static final int B_Manage1_stat_Category_Header = 211;
    static final int B_Manage1_stat_Transaction_Content = 212;
    static final int addTransactionsCategoryItem = 213;
    static final int addTransactionsAssetItem = 214;


    abstract int getType();
    //_id, type, name, balance, withdrawalaccount, withdrawalday, cardid


    static class assetItem extends RecyclerItem{
        AssetItem item = new AssetItem();

        @Override
        int getType() {
            return ASSET;
        }
    }

    static class categoryItem extends RecyclerItem {
        NameOnlyItem nameOnlyItem = new NameOnlyItem();

        @Override
        int getType() {
            return CATEGORY;
        }
    }

    static class CategorySettingsItem extends RecyclerItem{
        NameOnlyItem nameOnlyItem = new NameOnlyItem();

        @Override
        int getType() {
            return v_Settings0_cat;
        }
    }

    static class AssetSettingsItem extends  RecyclerItem{
        AssetItem item = new AssetItem();
        @Override
        int getType() {
            return 0;
        }
    }



    static class dateHeaderItem extends RecyclerItem {
        long transactionTime;
        DateFormat df = getDateInstance(DateFormat.MEDIUM);
        Date date = new Date();
        String format;

        @Override
        int getType() {
            return A_Trans0_History_Header;
        }

    }

    static class HistoryContentItem extends RecyclerItem {
        TransactionsViewItem item = new TransactionsViewItem();

        //amount, recipient, account, category
        @Override
        int getType() {
            return A_Trans0_History_Content;
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
            return B_Manage0_Asset_Header;
        }
    }


    public static class AssetContentItem extends RecyclerItem{
        TransactionsViewItem item = new TransactionsViewItem();

        @Override
        int getType() {
            return B_Manage0_Asset_Content;
        }
    }

    public static class CategorySummaryItem extends RecyclerItem {
        String assetName;
        float amount;
        //float amount_out;
        //float amount_rem;

        @Override
        int getType() {
            return B_Manage1_stat_Category_Header;
        }

    }
    public static class CategoryContentItem extends RecyclerItem{
        TransactionsViewItem item = new TransactionsViewItem();

        @Override
        int getType() {
            return B_Manage1_stat_Transaction_Content;
        }
    }


    static class TransactionAddCategoryItem extends RecyclerItem {
        NameOnlyItem nameOnlyItem = new NameOnlyItem();

        @Override
        int getType() {
            return addTransactionsCategoryItem;
        }
    }
    static class TransactionAddAssetItem extends RecyclerItem {
        AssetItem item = new AssetItem();

        @Override
        int getType() {
            return addTransactionsAssetItem;
        }
    }
}
