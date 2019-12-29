package com.example.kollhong.accounts3;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import com.example.kollhong.accounts3.DBItem.TransactionsViewItem;
import com.example.kollhong.accounts3.RecyclerItem.CategorySummaryItem;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;

import java.text.DateFormatSymbols;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * Created by KollHong on 25/03/2018.
 */


public class Statistics_Category extends Fragment {
    Calendar calendar1 = Calendar.getInstance();

    DB_Controll mDB;

    List<RecyclerItem> catSummaryItemList = new ArrayList<>();


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        return inflater.inflate(R.layout.b_manage_frag0, container, false);
    }


    //  @Override
    public void onViewCreated(@NonNull  View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mDB = new DB_Controll(getContext(),false);

//리사이클러뷰 구현
        RecyclerView recyclerView;
        recyclerView = view.findViewById(R.id.acc_recycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        TextView dateView = view.findViewById(R.id.dateView);
        ImageButton left = view.findViewById(R.id.his_LeftButton);
        ImageButton right = view.findViewById(R.id.his_RightButton);

        left.setOnClickListener(new leftListener());
        right.setOnClickListener(new rightListener());


    }

    @Override
    public void onResume() {

        makeRecyclerView();
        makePieChart();
        super.onResume();
    }
//이전월, 다음 월 버튼
    private class leftListener implements View.OnClickListener{
        @Override
        public void onClick(View v) {
            calendar1.add(Calendar.MONTH, - 1);
            makeRecyclerView();
            makePieChart();
        }
    }
    private class rightListener implements View.OnClickListener{
        @Override
        public void onClick(View v) {
            calendar1.add(Calendar.MONTH, 1);
            makeRecyclerView();
            makePieChart();
        }
    }
//리스트로 차트 만들기
    public void makePieChart(){
        List<PieEntry> entries = new ArrayList<>();
        for (RecyclerItem recyclerItem : catSummaryItemList) {
            CategorySummaryItem categorySummaryItem = (CategorySummaryItem) recyclerItem;
            entries.add(new PieEntry(categorySummaryItem.amount, categorySummaryItem.assetName));

        }
        Context context = getContext();
        PieChart pieChart = getView().findViewById(R.id.pieChart);
        PieDataSet pieDataSet = new PieDataSet(entries, "카테고리별 지출");
        List<Integer> colorList = new ArrayList<>();
        colorList.add(getColor(context,android.R.color.holo_blue_dark ));
        colorList.add(getColor(context,android.R.color.holo_green_dark));
        colorList.add(getColor(context,android.R.color.holo_orange_dark));
        colorList.add(getColor(context,android.R.color.holo_red_dark));
        colorList.add(getColor(context, R.color.colorAccent));

        pieDataSet.setColors(colorList);
        PieData pieData = new PieData(pieDataSet);
        pieChart.setData(pieData);
        pieChart.invalidate();
    }

    public static int getColor(Context context, int id) {
        final int version = Build.VERSION.SDK_INT;
        if (version >= 23) {
            return ContextCompat.getColor(context, id);
        } else {
            return context.getResources().getColor(id);
        }
    }

    public void makeRecyclerView() {
        List<RecyclerItem> recyclerItemList = new ArrayList<>();
        Calendar calendar2 = Calendar.getInstance();

        TextView dateView = getView().findViewById(R.id.dateView);

        int month = calendar1.get(Calendar.MONTH);
        String[] monthFormat = DateFormatSymbols.getInstance().getMonths();

        dateView.setText(monthFormat[month]);

        calendar1.set(Calendar.DATE, 1);
        calendar1.set(Calendar.HOUR_OF_DAY, 0);
        calendar1.set(Calendar.MINUTE, 0);
        calendar1.set(Calendar.SECOND, 0);
        calendar1.set(Calendar.MILLISECOND, 0);
        calendar1.set(Calendar.MONTH, month);


        long thisMonth = calendar1.getTimeInMillis();
        calendar2.setTimeInMillis(thisMonth);
        calendar2.add(Calendar.MONTH, 1);
        long nextMonth = calendar2.getTimeInMillis() - 1L;


        catSummaryItemList.clear();
        List<TransactionsViewItem> valuesList = mDB.getTransbyCat(thisMonth, nextMonth);
        //ListIterator valuesListIter = valuesList.listIterator();

        long list[] = {0L};
        long catId = -1;
        float amount = 0;
        for (TransactionsViewItem values : valuesList) {
            if (values.categoryId != catId) {
                CategorySummaryItem summaryItem = new CategorySummaryItem();

                summaryItem.assetName = values.categoryName;
                summaryItem.amount = amount;

                catSummaryItemList.add(summaryItem);

                catId = values.categoryId;
                amount = 0;
            }

            amount += values.amount;
        }




        RecyclerView recyclerView = getView().findViewById(R.id.acc_recycler);
        Recycler_Adapter myadapter = new Recycler_Adapter(getActivity(),catSummaryItemList,null);
        recyclerView.setAdapter(myadapter);
        Recycler_Adapter.DividerItemDecoration divider = new Recycler_Adapter.DividerItemDecoration(getContext());
        recyclerView.addItemDecoration(divider);
        myadapter.notifyDataSetChanged();

    }



}
