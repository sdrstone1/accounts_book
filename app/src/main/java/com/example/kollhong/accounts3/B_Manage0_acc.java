package com.example.kollhong.accounts3;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
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
import com.github.mikephil.charting.charts.PieChart;


import java.util.*;

import static android.view.View.GONE;

/**
 * Created by KollHong on 25/03/2018.
 */


public class B_Manage0_acc extends Fragment {
    Calendar calendar1 = Calendar.getInstance();

    zDBMan mDB;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        return inflater.inflate(R.layout.b_manage_frag0, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mDB = new zDBMan(getContext(),false);


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
        List<zRecyclerAdapt_Gen.recyclerItem> accItems = new ArrayList<>();

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
        long nextMonth = calendar2.getTimeInMillis() - 1l;


        List<zDBScheme.TransactionsViewItem> transByAcc = mDB.getTransByAcc(thisMonth, nextMonth);
        /*
        zDBScheme.TransactionsViewItem transactionITem;
        while (transByAcc.hasNext()) {
            transItem = new ();

            transactionITem = transByAcc.next();
            transItem.trans_id = transactionITem.tableId;
            transItem.acc_name = transactionITem.getAsString(zDBScheme.TRANSACTIONS_VIEW.ASSET_NAME);
            transItem.amount = transactionITem.getAsFloat(zDBScheme.TRANSACTIONS_VIEW.AMOUNT);
            transItem.asset_id = transactionITem.getAsLong(zDBScheme.TRANSACTIONS_VIEW.ASSET_ID);
            if(transactionITem.containsKey(zDBScheme.TRANSACTIONS_VIEW.REWARD_CACULATED)) {
                transItem.reward = transactionITem.getAsFloat(zDBScheme.TRANSACTIONS_VIEW.REWARD_CACULATED);
                Log.i("데이터 가져오기","실적 : " +transItem.reward);
            }
            transItem.level = transactionITem.getAsLong(zDBScheme.TRANSACTIONS_VIEW.CATEGORY_LEVEL);
                transItems.add(transItem);
        }
        */

        long name = -1;

        //ItemPerAcc itemPerAcc = new ItemPerAcc();
        for (int i = 0; i < transByAcc.size(); i++) {
            if (transByAcc.get(i).assetId != name) {
                zRecyclerAdapt_Gen.ItemPerAcc itemPerAcc = new zRecyclerAdapt_Gen.ItemPerAcc();
                name = transByAcc.get(i).assetId;
                itemPerAcc.assetName = transByAcc.get(i).assetName;

                //사용금액 합산
                if (transByAcc.get(i).categoryLevel == 0 || transByAcc.get(i).categoryLevel == 1 || transByAcc.get(i).categoryLevel == 2) {
                    itemPerAcc.amountIn += transByAcc.get(i).amount;
                }
                else if(transByAcc.get(i).categoryLevel == 3 || transByAcc.get(i).categoryLevel == 4 || transByAcc.get(i).categoryLevel == 5){
                    itemPerAcc.amountOut += transByAcc.get(i).amount;
                }
                else if(transByAcc.get(i).categoryLevel == 6 || transByAcc.get(i).categoryLevel == 7 || transByAcc.get(i).categoryLevel == 8) {
                    itemPerAcc.amountRemain += transByAcc.get(i).amount;
                }
                itemPerAcc.reward += transByAcc.get(i).rewardCalculated;
                accItems.add(itemPerAcc);
            }
        }

        RecyclerView recyclerView = getView().findViewById(R.id.acc_recycler);
        zRecyclerAdapt_Gen.recyclerAdapter accAdapter = new zRecyclerAdapt_Gen.recyclerAdapter(getActivity(),accItems,null);
        recyclerView.setAdapter(accAdapter);
        DividerItemDecoration divider = new DividerItemDecoration(getContext());
        recyclerView.addItemDecoration(divider);
        accAdapter.notifyDataSetChanged();


    }

/*
    private class accAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{
        private final List<ItemPerAcc> list;

        public accAdapter(List<ItemPerAcc> list){
            this.list=list;
        }

        @Override
        public int getItemCount() {
            return list.size();
        }

        @NonNull
        @Override           //한번만 실행
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(getActivity()).inflate(R.layout.b_manage_frag0_recycler, parent, false);
                return new accHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {


            ItemPerAcc itemVO = list.get(position);

            accHolder accHolder =  (accHolder) holder;

            accHolder.name.setText(itemVO.asset_name);
            accHolder.income.setText(String.valueOf(itemVO.amount_in));
            accHolder.expense.setText(String.valueOf(itemVO.amount_out));
            accHolder.remittance.setText(String.valueOf(itemVO.amount_rem));
            CharSequence string = getContext().getResources().getText(R.string.reward);
            String reward = String.valueOf(Float.valueOf(itemVO.reward).intValue());
            accHolder.reward.setText( string + "   " + reward);

        }

        public class accHolder extends RecyclerView.ViewHolder {
            TextView name;
            TextView income;
            TextView expense;
            TextView remittance;
            TextView reward;

            public accHolder(View v) {
                super(v);
                name = v.findViewById(R.id.acc_name);
                income = v.findViewById(R.id.income);
                expense = v.findViewById(R.id.expense);
                remittance = v.findViewById(R.id.remittance);
                reward = v.findViewById(R.id.rewardview);
            }
        }
    }
*/
    private class DividerItemDecoration extends RecyclerView.ItemDecoration {

        private  final int[] ATTRS = new int[]{android.R.attr.listDivider};

        private Drawable divider;

        /**
         * Default divider will be used
         */
        public DividerItemDecoration(Context context) {
            final TypedArray styledAttributes = context.obtainStyledAttributes(ATTRS);
            divider = styledAttributes.getDrawable(0);
            styledAttributes.recycle();
        }

        /**
         * Custom divider will be used
         */
        public DividerItemDecoration(Context context, int resId) {
            divider = ContextCompat.getDrawable(context, resId);
        }

        @Override
        public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state) {
            int left = parent.getPaddingLeft();
            int right = parent.getWidth() - parent.getPaddingRight();

            int childCount = parent.getChildCount();
            for (int i = 0; i < childCount; i++) {
                View child = parent.getChildAt(i);

                RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) child.getLayoutParams();

                int top = child.getBottom() + params.bottomMargin;
                int bottom = top + divider.getIntrinsicHeight();

                divider.setBounds(left, top, right, bottom);
                divider.draw(c);
            }
        }
    }
}