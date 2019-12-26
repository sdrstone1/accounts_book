package com.example.kollhong.accounts3;

import java.text.DateFormat;
import java.util.Date;

import static java.text.DateFormat.getDateInstance;
import com.example.kollhong.accounts3.DBItem.*;

public abstract class RecyclerItem {
    static final int ASSET = 201;
    static final int CATEGORY = 202;
    static final int v_Settings0_cat = 203;
    static final int dateHeader = 204;
    static final int A_Trans0_History_Header = 205;
    static final int A_Trans0_History_Content = 206;
    static final int B_Manage0_Asset_Header = 207;
    static final int B_Manage0_Asset_Content = 208;
    static final int B_Manage1_stat_Asset = 209;
    static final int B_Manage1_stat_Category_Header = 210;
    static final int B_Manage1_stat_Transaction_Content = 211;

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
        FranchiseeItem nameOnlyItem = new FranchiseeItem();

        @Override
        int getType() {
            return CATEGORY;
        }
    }
    static class CategorySettingsItem extends RecyclerItem{
        FranchiseeItem nameOnlyItem = new FranchiseeItem();

        @Override
        int getType() {
            return v_Settings0_cat;
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
}
