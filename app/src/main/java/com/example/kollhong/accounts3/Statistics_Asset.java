package com.example.kollhong.accounts3;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import com.example.kollhong.accounts3.DBItem.TransactionsViewItem;
import com.example.kollhong.accounts3.RecyclerItem.AssetContentItem;
import com.example.kollhong.accounts3.RecyclerItem.AssetSummaryItem;
import com.github.mikephil.charting.charts.PieChart;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import static android.view.View.GONE;

/**
 * Created by KollHong on 25/03/2018.
 */


public class Statistics_Asset extends Fragment {
    Calendar calendar1 = Calendar.getInstance();

    DB_Controll mDB;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        return inflater.inflate(R.layout.b_manage_frag0, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mDB = new DB_Controll(getContext(),false);


        RecyclerView recyclerView;
        recyclerView = view.findViewById(R.id.acc_recycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        TextView dateView = view.findViewById(R.id.dateView);
        ImageButton left = view.findViewById(R.id.his_LeftButton);
        ImageButton right = view.findViewById(R.id.his_RightButton);

        left.setOnClickListener(new leftListener());
        right.setOnClickListener(new rightListener());

        PieChart pieChart = view.findViewById(R.id.pieChart);
        pieChart.setVisibility(GONE);


    }

    @Override
    public void onResume() {
        makeRecyclerView();
        super.onResume();
    }

    private class leftListener implements View.OnClickListener{
        @Override
        public void onClick(View v) {
            calendar1.add(Calendar.MONTH, - 1);
            makeRecyclerView();
        }
    }
    private class rightListener implements View.OnClickListener{
        @Override
        public void onClick(View v) {
            calendar1.add(Calendar.MONTH, 1);
            makeRecyclerView();
        }
    }

    private void makeRecyclerView() {
        //List<TransItem> transByAcc = new ArrayList<>();
        List<RecyclerItem> recyclerItemList = new ArrayList<>();

        Calendar calendar2 = Calendar.getInstance();

        TextView dateView = getView().findViewById(R.id.dateView);

        calendar1.set(Calendar.DATE, 1);
        calendar1.set(Calendar.HOUR_OF_DAY, 0);
        calendar1.set(Calendar.MINUTE, 0);
        calendar1.set(Calendar.SECOND, 0);
        calendar1.set(Calendar.MILLISECOND, 1);


        dateView.setText(calendar1.getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.getDefault()));


        long thisMonth = calendar1.getTimeInMillis();
        calendar2.setTimeInMillis(thisMonth);
        calendar2.add(Calendar.MONTH, 1);
        long nextMonth = calendar2.getTimeInMillis() - 1L;


        List<TransactionsViewItem> transByAcc = mDB.getTransByAcc(thisMonth, nextMonth);
        //ListIterator listIterator = transByAcc.listIterator();

        long assetId = -1;
        float amountIncome = 0, amountExpend = 0, amountTransfer=0, reward = 0;
        for (TransactionsViewItem transactionItem : transByAcc) {
            //AssetContentItem contentItem = new AssetContentItem();

           // transactionItem = (TransactionsViewItem) listIterator.next();


            if (transactionItem.assetId != assetId) {
                AssetSummaryItem assetSummaryItem = new AssetSummaryItem();

                assetSummaryItem.assetName = transactionItem.assetName;
                assetSummaryItem.IncomeSummary = amountIncome;
                assetSummaryItem.ExpenseSummary = amountExpend;
                assetSummaryItem.TransferSummary = amountTransfer;
                assetSummaryItem.RewardSummary = reward;

                recyclerItemList.add(assetSummaryItem);

                amountExpend = amountIncome = amountTransfer = reward = 0;
                assetId = transactionItem.assetId;
            }

            //사용금액 합산
            if (transactionItem.categoryLevel == 0 || transactionItem.categoryLevel == 1 || transactionItem.categoryLevel == 2) {
                amountIncome += transactionItem.amount;
            }
            else if(transactionItem.categoryLevel == 3 || transactionItem.categoryLevel == 4 || transactionItem.categoryLevel == 5){
                amountExpend += transactionItem.amount;
            }
            else if(transactionItem.categoryLevel == 6 || transactionItem.categoryLevel == 7 || transactionItem.categoryLevel == 8) {
                amountTransfer += transactionItem.amount;
            }
            reward += transactionItem.rewardCalculated;

            //recyclerItemList.add(contentItem);
        }

        RecyclerView recyclerView = getView().findViewById(R.id.acc_recycler);
        Recycler_Adapter accAdapter = new Recycler_Adapter(getActivity(),recyclerItemList,null);
        recyclerView.setAdapter(accAdapter);
        Recycler_Adapter.DividerItemDecoration divider = new Recycler_Adapter.DividerItemDecoration(getContext());
        recyclerView.addItemDecoration(divider);
        accAdapter.notifyDataSetChanged();


    }

}