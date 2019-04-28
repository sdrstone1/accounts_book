package com.example.kollhong.accounts3;

import android.content.Context;
import android.content.res.TypedArray;
import android.database.Cursor;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import com.github.mikephil.charting.charts.PieChart;


import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

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
        List<TransItem> transItems = new ArrayList<>();
        List<ItemPerAcc> accItems = new ArrayList<>();

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

        Cursor cursor = mDB.getTransByAcc(thisMonth, nextMonth);
        if (cursor.getCount() != 0) {
            while (cursor.moveToNext()) {
                TransItem transItem = new TransItem();

                transItem.trans_id = cursor.getLong(0);
                transItem.acc_name = cursor.getString(2);
                transItem.amount = cursor.getFloat(3);
                transItem.acc_id = cursor.getLong(4);
                if(!cursor.isNull(6)) {
                    transItem.reward = cursor.getFloat(6);
                    Log.e("데이터 가져오기","실적 : " +transItem.reward);
                }
                transItem.level = cursor.getInt(7);
                transItems.add(transItem);
            }
            cursor.close();
            long name = -1;

            //ItemPerAcc itemPerAcc = new ItemPerAcc();
            for (int i = 0; i < transItems.size(); i++) {
                if (transItems.get(i).acc_id != name) {
                    ItemPerAcc itemPerAcc = new ItemPerAcc();
                    name = transItems.get(i).acc_id;
                    itemPerAcc.acc_name = transItems.get(i).acc_name;
                    accItems.add(itemPerAcc);

                }
                //사용금액 합산
                switch (transItems.get(i).level) {
                    case 0:
                    case 1:
                    case 2:
                        accItems.get(accItems.size() - 1).amount_in += transItems.get(i).amount;
                        break;
                    case 3:
                    case 4:
                    case 5:
                        accItems.get(accItems.size() - 1).amount_out += transItems.get(i).amount;
                        break;
                    case 6:
                    case 7:
                    case 8:
                        accItems.get(accItems.size() - 1).amount_rem += transItems.get(i).amount;
                        break;
                }
                accItems.get(accItems.size() - 1).reward += transItems.get(i).reward;
            }

        }


        RecyclerView recyclerView = getView().findViewById(R.id.acc_recycler);
        accAdapter accAdapter = new accAdapter(accItems);
        recyclerView.setAdapter(accAdapter);
        DividerItemDecoration divider = new DividerItemDecoration(getContext());
        recyclerView.addItemDecoration(divider);
        accAdapter.notifyDataSetChanged();

    }

    public class TransItem{
        //t._id, t.categoryid, a.name as accname, t.amount, t.accountid, t.recipient, t.rewardamount, c.level
        long trans_id;
        //long cat_id;
        String acc_name;
        float amount;
        float reward = 0f;
        long acc_id;
        int level;
    }

    public class ItemPerAcc{
        String acc_name;
        float amount_in;
        float amount_out;
        float amount_rem;
        float reward = 0;

    }
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

            accHolder.name.setText(itemVO.acc_name);
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