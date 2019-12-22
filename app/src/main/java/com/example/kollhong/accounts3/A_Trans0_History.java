package com.example.kollhong.accounts3;

import android.app.SearchManager;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
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

import java.text.DateFormat;
import java.util.*;

import static com.example.kollhong.accounts3.zDBMan.DBScheme.*;
import static java.text.DateFormat.getDateInstance;
import static java.text.DateFormat.getDateTimeInstance;

/**
 * Created by KollHong on 25/03/2018.
 */


public class A_Trans0_History extends Fragment {
    Calendar calendar;

    TextView dateView;
    zDBMan mDB;
    int month;

    List<ItemVO> list;
    TransAdapter transAdapter;

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
        list = new ArrayList<>();
        for (int day2 = max_day; day2 >= min_day; day2--) {
            calendar.set(Calendar.DAY_OF_MONTH, day2);

            today00 = calendar.getTimeInMillis();
            calendar2.setTimeInMillis(today00);
            calendar2.add(Calendar.DAY_OF_MONTH, 1);
            today2359 = calendar2.getTimeInMillis() - 1l;
            calendar2.setTimeInMillis(today2359);


            ListIterator<ContentValues> iterator = mDB.getTransHistory(today00, today2359).listIterator();
            ContentValues contentValues;

            if(iterator.hasNext()) {
                HeaderItem headerItem = new HeaderItem();
                headerItem.timeinmillis = today00;
                list.add(headerItem);       //거래 날짜 표시

                while (iterator.hasNext()) {
                    ContentItem item = new ContentItem();
                    contentValues = iterator.next();
                    item.trans_id = contentValues.getAsLong(TABLE_ID);
                    item.amount = contentValues.getAsLong(TRANSACTIONS_VIEW_amount);
                    item.recipient = contentValues.getAsString(TRANSACTIONS_VIEW_recipient);
                    item.category_level =  contentValues.getAsLong(TRANSACTIONS_VIEW_category_level);
                    item.category_name = contentValues.getAsString(TRANSACTIONS_VIEW_category_name);
                    item.parent_category_name = contentValues.getAsString(TRANSACTIONS_VIEW_parent_category_name);
                    item.asset_name = contentValues.getAsString(TRANSACTIONS_VIEW_asset_name);

                    list.add(item);
                }
            }

        }

        transAdapter = new TransAdapter(list);
        recyclerView.setAdapter(transAdapter);
        DividerItemDecoration divider = new DividerItemDecoration(getContext());
        recyclerView.addItemDecoration(divider);
        transAdapter.notifyDataSetChanged();

    }

    abstract class ItemVO {
        public static final int HEADER = 0;
        public static final int CONTENT = 1;


        abstract int getType();
    }

    class HeaderItem extends ItemVO {
        long timeinmillis;
        DateFormat df = getDateInstance(DateFormat.MEDIUM);
        Date date = new Date();
        String format;

        @Override
        int getType() {
            return 0;
        }
    }

    class ContentItem extends ItemVO {
        long trans_id;
        String recipient;
        String asset_name;
        String category_name;
        String parent_category_name;
        long amount;
        long category_level;

        //amount, recipient, account, category
        @Override
        int getType() {
            return 1;
        }
    }


    private class HeaderViewHolder extends RecyclerView.ViewHolder {
        public TextView time;

        public HeaderViewHolder(View v) {
            super(v);
            time = v.findViewById(R.id.Time);

        }
    }

    private class ContentViewHolder extends RecyclerView.ViewHolder {
        public TextView list_cat;
        public TextView list_cat2nd;
        public TextView list_reci;
        public TextView list_accounts;
        public TextView list_amount;
        public int color_blue;
        public int color_red;
        public int color_gray;

        public ContentViewHolder(View v) {
            super(v);
            list_cat = v.findViewById(R.id.list_cat);
            list_cat2nd = v.findViewById(R.id.list_cat2nd);
            list_reci = v.findViewById(R.id.list_reci);
            list_accounts = v.findViewById(R.id.list_accounts);
            list_amount = v.findViewById(R.id.list_amount);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                color_blue = ContextCompat.getColor(getContext(),android.R.color.holo_blue_light);
                color_red = ContextCompat.getColor(getContext(),android.R.color.holo_red_light);
                color_gray = ContextCompat.getColor(getContext(),android.R.color.darker_gray);
            }
            else{
                color_blue = v.getResources().getColor(android.R.color.holo_blue_light);
                color_red = v.getResources().getColor(android.R.color.holo_red_light);
                color_gray = v.getResources().getColor(android.R.color.darker_gray);
            }

        }
    }

    private class TransAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
        private final List<ItemVO> list;
        private final List<ItemVO> listsearched;
        //private List<ContentItem> listtosearch = new ArrayList<>();

        public TransAdapter(List<ItemVO> list) {
            this.list = list;
            this.listsearched = new ArrayList<ItemVO>();
            this.listsearched.addAll(list);
        }

        @Override
        public int getItemViewType(int position) {
            return list.get(position).getType();
        }

        @Override
        public int getItemCount() {
            return list.size();
        }

        @NonNull
        @Override           //한번만 실행
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            if (viewType == 0) {      //헤더
                View view = LayoutInflater.from(getActivity()).inflate(R.layout.a_trans_frag0_header, parent, false);
                return new HeaderViewHolder(view);
            } else if (viewType == 1) {
                View view = LayoutInflater.from(getActivity()).inflate(R.layout.a_trans_frag0_content, parent, false);

                return new ContentViewHolder(view);
            } else {
                Log.w("리사이클러뷰", "에러");
                return null;
            }
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {


            ItemVO itemVO = list.get(position);
            if (itemVO.getType() == 0) {      //header
                HeaderViewHolder viewHolder = (HeaderViewHolder) holder;
                HeaderItem headerItem = (HeaderItem) itemVO;


                headerItem.date.setTime(headerItem.timeinmillis);
                headerItem.format = headerItem.df.format(headerItem.date);

                viewHolder.time.setText(headerItem.format);
            } else if (itemVO.getType() == 1) {
                ContentViewHolder contentViewHolder = (ContentViewHolder) holder;

                ContentItem contentItem = (ContentItem) itemVO;

                holder.itemView.setTag(contentItem.trans_id);
                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        long trans_id = (long) v.getTag();
                        Log.e("Updating Trans, ", "trans_id : " + trans_id);
                        Intent intent = new Intent(getContext(), w_Add_Tran.class);
                        intent.putExtra("UpdateTrans", trans_id);
                        startActivity(intent);
                    }
                });

                if (contentItem.category_level == 0l || contentItem.category_level == 1l) { //0,1,2        3,4,5           6,7,8
                    contentViewHolder.list_cat.setText(contentItem.category_name);
                    contentViewHolder.list_amount.setTextColor(contentViewHolder.color_blue);
                }
                else if (contentItem.category_level == 2l) {
                    contentViewHolder.list_cat.setText(contentItem.parent_category_name);
                    contentViewHolder.list_cat2nd.setText(contentItem.category_name);
                    contentViewHolder.list_amount.setTextColor(contentViewHolder.color_blue);
                }
                else if (contentItem.category_level == 3l || contentItem.category_level == 4l) {
                    contentViewHolder.list_cat.setText(contentItem.category_name);
                    contentViewHolder.list_amount.setTextColor(contentViewHolder.color_red);
                }
                else if (contentItem.category_level == 5l) {
                    contentViewHolder.list_cat.setText(contentItem.parent_category_name);
                    contentViewHolder.list_cat2nd.setText(contentItem.category_name);
                    contentViewHolder.list_amount.setTextColor(contentViewHolder.color_red);
                }
                else if (contentItem.category_level == 6l || contentItem.category_level == 7l) {
                    contentViewHolder.list_cat.setText(contentItem.category_name);
                    contentViewHolder.list_amount.setTextColor(contentViewHolder.color_gray);
                }
                else if (contentItem.category_level == 8l) {

                    contentViewHolder.list_cat.setText(contentItem.parent_category_name);
                    contentViewHolder.list_cat2nd.setText(contentItem.category_name);
                    contentViewHolder.list_amount.setTextColor(contentViewHolder.color_gray);
                }

                contentViewHolder.list_amount.setText(contentItem.amount + "");
                contentViewHolder.list_accounts.setText(contentItem.asset_name);
                contentViewHolder.list_reci.setText(contentItem.recipient);

            }
        }


        public void filter(String query) {
            query = query.toLowerCase(Locale.getDefault());
            list.clear();
            if (query.length() == 0) {
                list.addAll(listsearched);
            }
            for (ItemVO wp : listsearched) {
                if (wp.getType() == 0) {
                    list.add(wp);
                } else {
                    //wp = (ContentItem) wp;
                    if (((ContentItem) wp).recipient.toLowerCase(Locale.getDefault())
                            .contains(query)) {
                        list.add(wp);

                    }
                }
            }
            notifyDataSetChanged();
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
