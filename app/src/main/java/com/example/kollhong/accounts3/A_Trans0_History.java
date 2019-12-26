package com.example.kollhong.accounts3;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.SearchView;
import android.widget.TextView;

import java.util.*;

import static java.text.DateFormat.getDateInstance;
import static java.text.DateFormat.getDateTimeInstance;
//import com.example.kollhong.accounts3.DBItem.*;
import com.example.kollhong.accounts3.RecyclerItem.*;

/**
 * Created by KollHong on 25/03/2018.
 */


public class A_Trans0_History extends Fragment {
    Calendar calendar;

    TextView dateView;
    zDBMan mDB;
    int month;

//    List<ItemVO> list;
    zRecyclerAdapt_Gen.RecyclerAdapter transAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        setHasOptionsMenu(true);
        return inflater.inflate(R.layout.a_trans_frag0, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        dateView = view.findViewById(R.id.dateView);
        ImageButton left = view.findViewById(R.id.his_LeftButton);
        ImageButton right = view.findViewById(R.id.his_RightButton);
        left.setOnClickListener(new leftListener());
        right.setOnClickListener(new rightListener());

        calendar = Calendar.getInstance();

        mDB = new zDBMan(getContext(), false);

        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 1);



    }

    @Override
    public void onResume() {
        super.onResume();
        makeRecyclerView();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.a_search, menu);

        /*
        검색 버튼 구현
         */
        SearchManager manager = (SearchManager) getActivity().getSystemService(Context.SEARCH_SERVICE);
        SearchView search = (SearchView) menu.findItem(R.id.app_bar_search).getActionView();
        search.setSearchableInfo(manager.getSearchableInfo(getActivity().getComponentName()));
        search.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                PerfromSearch(newText);
                return false;
            }
        });
        super.onCreateOptionsMenu(menu, inflater);
    }

    void PerfromSearch(String query) {

        transAdapter.filter(query);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

    private class leftListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            calendar.add(Calendar.MONTH, -1);
            makeRecyclerView();
        }
    }

    private class rightListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            calendar.add(Calendar.MONTH, 1);
            makeRecyclerView();
        }
    }

    private void makeRecyclerView() {
        RecyclerView recyclerView = getView().findViewById(R.id.tran_hist_list);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        Calendar calendar2 = Calendar.getInstance();
        month = calendar.get(Calendar.MONTH);

        dateView.setText(calendar.getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.getDefault()));

        int max_day = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
        int min_day = calendar.getActualMinimum(Calendar.DAY_OF_MONTH);

        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);


        long today00;
        long today2359;
        //날짜를 안다 하루씩 빼가면서 찾기
        //하루가 지날 때마다 날짜 헤더 표시하기
        //list = new ArrayList<>();
        List<DBItem.TransactionsViewItem> transactionHistoryList;
        List<RecyclerItem> recyclerItemList = new ArrayList<>();
        for (int day2 = max_day; day2 >= min_day; day2--) {
            calendar.set(Calendar.DAY_OF_MONTH, day2);

            today00 = calendar.getTimeInMillis();
            calendar2.setTimeInMillis(today00);
            calendar2.add(Calendar.DAY_OF_MONTH, 1);
            today2359 = calendar2.getTimeInMillis() - 1l;
            calendar2.setTimeInMillis(today2359);

            transactionHistoryList = mDB.getTransHistory(today00, today2359);
            ListIterator iterator = transactionHistoryList.listIterator();
            DBItem.TransactionsViewItem transactionsViewItem;

            if(iterator.hasNext()) {
                dateHeaderItem HeaderItem = new dateHeaderItem();
                HeaderItem.transactionTime = today00;
                recyclerItemList.add(HeaderItem);       //거래 날짜 표시

                while (iterator.hasNext()) {
                    transactionsViewItem = (DBItem.TransactionsViewItem) iterator.next();
                    HistoryContentItem item = new HistoryContentItem();
                    item.item = transactionsViewItem;
                    recyclerItemList.add(item);
                }
            }
        }

        transAdapter = new zRecyclerAdapt_Gen.RecyclerAdapter(getActivity(), recyclerItemList,new TransactionClickListener());
        recyclerView.setAdapter(transAdapter);
        DividerItemDecoration divider = new DividerItemDecoration(getContext());
        recyclerView.addItemDecoration(divider);
        transAdapter.notifyDataSetChanged();

    }
    public class TransactionClickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {

            HistoryContentItem contentItem = (HistoryContentItem) v.getTag();
            Log.e("Updating Trans, ", "trans_id : " + contentItem.item.tableId);
            Intent intent = new Intent(getContext(), w_Add_Tran.class);
            intent.putExtra("UpdateTrans", contentItem.item.tableId);
            startActivity(intent);
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
