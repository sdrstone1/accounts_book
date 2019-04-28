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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;

import java.text.DateFormatSymbols;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import static android.view.View.GONE;

/**
 * Created by KollHong on 25/03/2018.
 */


public class B_Manage1_stat extends Fragment {
    Calendar calendar1 = Calendar.getInstance();

    zDBMan mDB;

    List<ItemperCat> catItems = new ArrayList<>();


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        return inflater.inflate(R.layout.b_manage_frag0, container, false);
    }


    //  @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mDB = new zDBMan(getContext(),false);

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
        for(int i = 0 ; i < catItems.size() ; i++ ) {
            entries.add( new PieEntry(catItems.get(i).amount,catItems.get(i).acc_name));

        }

        PieChart pieChart = getView().findViewById(R.id.pieChart);
        PieDataSet pieDataSet = new PieDataSet(entries, "카테고리별 지출");
        List<Integer> colorList = new ArrayList<>();
        colorList.add(getResources().getColor( android.R.color.holo_blue_dark, null));
        colorList.add(getResources().getColor( android.R.color.holo_green_dark, null));
        colorList.add(getResources().getColor( android.R.color.holo_orange_dark, null));
        colorList.add(getResources().getColor( android.R.color.holo_red_dark, null));
        colorList.add(getResources().getColor( R.color.colorAccent, null));

        pieDataSet.setColors(colorList);
        PieData pieData = new PieData(pieDataSet);
        pieChart.setData(pieData);
        pieChart.invalidate();
    }

    public void makeRecyclerView() {
        List<TransItem> transItems = new ArrayList<>();
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
        long nextMonth = calendar2.getTimeInMillis() - 1l;


        catItems.clear();
        Cursor cursor = mDB.getTransbyCat(thisMonth, nextMonth);
        long list[] = {0l};

        if (cursor.getCount() != 0) {
            while (cursor.moveToNext()) {
                TransItem transItem = new TransItem();
                //t._id, t.categoryid, c.name, t.amount
                transItem.trans_id = cursor.getLong(0);
                transItem.cat_id = cursor.getLong(1);
                transItem.cat_name = cursor.getString(2);
                transItem.amount = cursor.getFloat(3);
                transItems.add(transItem);
            }

            long name = -1;

            for (int i = 0; i < transItems.size(); i++) {
                if (transItems.get(i).cat_id != name) {
                    ItemperCat itemPerCat = new ItemperCat();

                    itemPerCat.acc_name = transItems.get(i).cat_name;
                    catItems.add(itemPerCat);
                    name = transItems.get(i).cat_id;

                }

                catItems.get(catItems.size() - 1).amount += transItems.get(i).amount;


            }
        }


        RecyclerView recyclerView = getView().findViewById(R.id.acc_recycler);
        Myadapter myadapter = new Myadapter(catItems);
        recyclerView.setAdapter(myadapter);
        DividerItemDecoration divider = new DividerItemDecoration(getContext());
        recyclerView.addItemDecoration(divider);
        myadapter.notifyDataSetChanged();

    }

    public class TransItem{
        long trans_id;
        long cat_id;
        String cat_name;
        float amount;
    }

    public class ItemperCat {
        String acc_name;
        float amount;
        //float amount_out;
        //float amount_rem;

    }

    private class Myadapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{
        private final List<ItemperCat> list;

        public Myadapter(List<ItemperCat> list){
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
            return new Myadapter.accHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {


            ItemperCat itemVO = list.get(position);

            Myadapter.accHolder accHolder =  (Myadapter.accHolder) holder;

            accHolder.name.setText(itemVO.acc_name);
            //accHolder.income.setText(String.valueOf(itemVO.amount_in));
            accHolder.expense.setText(String.valueOf(itemVO.amount));
            //accHolder.remittance.setText(String.valueOf(itemVO.amount_rem));


        }

        public class accHolder extends RecyclerView.ViewHolder {
            TextView name;
            //TextView income;
            TextView expense;
            //TextView remittance;

            public accHolder(View v) {
                super(v);
                name = v.findViewById(R.id.acc_name);
                v.findViewById(R.id.income).setVisibility(View.INVISIBLE);
                expense = v.findViewById(R.id.expense);
                v.findViewById(R.id.remittance).setVisibility(View.INVISIBLE);
                v.findViewById(R.id.inc).setVisibility(View.INVISIBLE);
                v.findViewById(R.id.remmi).setVisibility(View.INVISIBLE);
            }
        }
    }

    public class DividerItemDecoration extends RecyclerView.ItemDecoration {

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
