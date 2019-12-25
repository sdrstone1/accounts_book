package com.example.kollhong.accounts3;

import android.app.Activity;
import android.graphics.Point;
import android.os.Build;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.*;
import android.widget.TextView;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import static java.text.DateFormat.getDateInstance;

public class zRecyclerAdapt_Gen {
    static final int ASSET = 0;
    static final int CATEGORY = 1;
    static final int SETTINGS = 2;
    static final int A_Trans0_History_Header = 3;
    static final int A_Trans0_History_Content = 4;
    static final int B_Manage0_acc_Asset = 5;

    zRecyclerAdapt_Gen(){
    }

    abstract static class recyclerItem{


        //int class_type; //type 0 == asset, type 1 == category selector, type 2 == cagetory settings
        abstract int getType();
//        abstract String getName();
    }

    //_id, type, name, balance, withdrawalaccount, withdrawalday, cardid
    static class assetItem extends recyclerItem{
        int id;
        String name;
        int assetType = 1;
        int cardId;
        float balance;
        int withdrawalAccount;
        int withdrawalDay;
        String nickname;

        @Override
        int getType() {
            return ASSET;
        }

    }
    static class categoryItem extends recyclerItem {
        int id;
        String name;

        @Override
        int getType() {
            return CATEGORY;
        }
    }
    static class settingsItem extends recyclerItem{
        int id;
        String name;

        @Override
        int getType() {
            return SETTINGS;
        }
    }

    static class HeaderItem extends recyclerItem {
        long transactionTime;
        DateFormat df = getDateInstance(DateFormat.MEDIUM);
        Date date = new Date();
        String format;

        @Override
        int getType() {
            return A_Trans0_History_Header;
        }
    }

    static class ContentItem extends recyclerItem {
        int transId;
        String recipientName;
        String assetName;
        String categoryName;
        String parentCategoryName;
        float amount;
        int categoryLevel;

        //amount, recipient, account, category
        @Override
        int getType() {
            return A_Trans0_History_Content;
        }
    }



    public static class ItemPerAcc extends recyclerItem{
        String assetName;

        float amountIn;
        float amountOut;
        float amountRemain;
        float reward = 0;

        @Override
        int getType() {
            return 0;
        }
    }

    public static class recyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
        //Context context;
        Activity activity;
        List<recyclerItem> items;
        List<recyclerItem> itemsSearched;
        View.OnClickListener listener;


        recyclerAdapter(Activity ac, List<recyclerItem> item, View.OnClickListener clickListener) {

            activity = ac;
            items = item;
            listener = clickListener;
            itemsSearched = new ArrayList<>();
            itemsSearched.addAll(items);

        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            if(viewType == ASSET || viewType == CATEGORY || viewType == SETTINGS) {
                View view = activity.getLayoutInflater().inflate(R.layout.w_add_transactions_category_picker_holder, parent, false);
                return new settingsViewholder(view);
            }
            else if (viewType == A_Trans0_History_Header) {      //헤더
                View view = LayoutInflater.from(activity).inflate(R.layout.a_trans_frag0_header, parent, false);
                return new HeaderViewHolder(view);
            } else if (viewType == A_Trans0_History_Content) {
                View view = LayoutInflater.from(activity).inflate(R.layout.a_trans_frag0_content, parent, false);

                return new ContentViewHolder(view);
            } else if(viewType == B_Manage0_acc_Asset) {
                View view = LayoutInflater.from(activity).inflate(R.layout.b_manage_frag0_recycler, parent, false);
                return new assetHolder(view);
            } else {
                Log.w("리사이클러뷰", "에러");
                return null;
            }
        }

        @Override
        public int getItemViewType(int position) {
            return items.get(position).getType();
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            recyclerItem item = items.get(position);
            if(item.getType() == ASSET) {
                assetItem assetitem = (assetItem)item;

                settingsViewholder viewholder = (settingsViewholder) holder;
                viewholder.textView.setText(assetitem.name);
                View v = viewholder.textView;
                v.setTag(item);
                viewholder.textView.setOnClickListener(listener);
            }
            else if(item.getType() == CATEGORY){
                categoryItem categoryitem = (categoryItem) item;

                settingsViewholder viewholder = (settingsViewholder) holder;
                viewholder.textView.setText(categoryitem.name);
                View v = viewholder.textView;
                v.setTag(item);
                viewholder.textView.setOnClickListener(listener);
            }
            else if(item.getType() == SETTINGS){
                settingsItem settingsitem = (settingsItem)item;

                settingsViewholder viewholder = (settingsViewholder) holder;
                viewholder.textView.setText(settingsitem.name);
                View v = viewholder.textView;
                v.setTag(item);
                viewholder.textView.setOnClickListener(listener);
            }
            else if(item.getType() == A_Trans0_History_Header){

                HeaderViewHolder viewHolder = (HeaderViewHolder) holder;
                HeaderItem headerItem = (HeaderItem) item;


                headerItem.date.setTime(headerItem.transactionTime);
                headerItem.format = headerItem.df.format(headerItem.date);

                viewHolder.time.setText(headerItem.format);
            }
            else if(item.getType() == A_Trans0_History_Content){
                ContentViewHolder contentViewHolder = (ContentViewHolder) holder;

                ContentItem contentItem = (ContentItem) item;

                holder.itemView.setTag(contentItem.transId);
                holder.itemView.setOnClickListener(listener);

                if (contentItem.categoryLevel == 0l || contentItem.categoryLevel == 1l) { //0,1,2        3,4,5           6,7,8
                    contentViewHolder.list_cat.setText(contentItem.categoryName);
                    contentViewHolder.list_amount.setTextColor(contentViewHolder.color_blue);
                }
                else if (contentItem.categoryLevel == 2l) {
                    contentViewHolder.list_cat.setText(contentItem.parentCategoryName);
                    contentViewHolder.list_cat2nd.setText(contentItem.categoryName);
                    contentViewHolder.list_amount.setTextColor(contentViewHolder.color_blue);
                }
                else if (contentItem.categoryLevel == 3l || contentItem.categoryLevel == 4l) {
                    contentViewHolder.list_cat.setText(contentItem.categoryName);
                    contentViewHolder.list_amount.setTextColor(contentViewHolder.color_red);
                }
                else if (contentItem.categoryLevel == 5l) {
                    contentViewHolder.list_cat.setText(contentItem.parentCategoryName);
                    contentViewHolder.list_cat2nd.setText(contentItem.categoryName);
                    contentViewHolder.list_amount.setTextColor(contentViewHolder.color_red);
                }
                else if (contentItem.categoryLevel == 6l || contentItem.categoryLevel == 7l) {
                    contentViewHolder.list_cat.setText(contentItem.categoryName);
                    contentViewHolder.list_amount.setTextColor(contentViewHolder.color_gray);
                }
                else if (contentItem.categoryLevel == 8l) {

                    contentViewHolder.list_cat.setText(contentItem.parentCategoryName);
                    contentViewHolder.list_cat2nd.setText(contentItem.categoryName);
                    contentViewHolder.list_amount.setTextColor(contentViewHolder.color_gray);
                }

                contentViewHolder.list_amount.setText(contentItem.amount + "");
                contentViewHolder.list_accounts.setText(contentItem.assetName);
                contentViewHolder.list_reci.setText(contentItem.recipientName);

            }
            else if(item.getType() == B_Manage0_acc_Asset) {
                ItemPerAcc assetItem = (ItemPerAcc) item;
                assetHolder assetHolder =  (assetHolder) holder;

                assetHolder.name.setText(assetItem.assetName);
                assetHolder.income.setText(String.valueOf(assetItem.amountIn));
                assetHolder.expense.setText(String.valueOf(assetItem.amountOut));
                assetHolder.remittance.setText(String.valueOf(assetItem.amountRemain));
                CharSequence string = activity.getApplicationContext().getResources().getText(R.string.reward);
                String reward = String.valueOf(Float.valueOf(assetItem.reward).intValue());
                assetHolder.reward.setText( string + "   " + reward);
            }
        }

        @Override
        public int getItemCount() {
//                return 4;
            return items.size();
        }


        public void filter(String query) {
            query = query.toLowerCase(Locale.getDefault());
            items.clear();
            if (query.length() == 0) {
                items.addAll(itemsSearched);
            }
            for (recyclerItem wp : itemsSearched) {
                if (wp.getType() == A_Trans0_History_Header) {
                    items.add(wp);
                } else if(wp.getType() == A_Trans0_History_Content){
                    //wp = (ContentItem) wp;
                    if (((ContentItem) wp).recipientName.toLowerCase(Locale.getDefault())
                            .contains(query)) {
                        items.add(wp);

                    }
                }
            }
            notifyDataSetChanged();
        }

        public class settingsViewholder extends RecyclerView.ViewHolder {
            TextView textView;

            public settingsViewholder(View v) {
                super(v);
                textView = (TextView) v.findViewById(R.id.add_tran_category_name);

                Point point = new Point();
                Display display = activity.getWindowManager().getDefaultDisplay();
                int rotation = display.getRotation();
                display.getSize(point );

                /*
                디스플레이 정보 가져오기- 사용하지는 않았지만 좋은 정보여서 남겨 둠
                float density  = getResources().getDisplayMetrics().density;
                float dpHeight = outMetrics.heightPixels / density;
                float dpWidth  = outMetrics.widthPixels / density;
*/
                ViewGroup.LayoutParams params = v.getLayoutParams();


                if (rotation == Surface.ROTATION_0 || rotation == Surface.ROTATION_180)
                    params.width = point.x ;

                else params.width = point.y ;
                v.setLayoutParams(params);
            }
        }


        public class ContentViewHolder extends RecyclerView.ViewHolder {
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
                    color_blue = ContextCompat.getColor(activity.getApplicationContext(),android.R.color.holo_blue_light);
                    color_red = ContextCompat.getColor(activity.getApplicationContext(),android.R.color.holo_red_light);
                    color_gray = ContextCompat.getColor(activity.getApplicationContext(),android.R.color.darker_gray);
                }
                else{
                    color_blue = v.getResources().getColor(android.R.color.holo_blue_light);
                    color_red = v.getResources().getColor(android.R.color.holo_red_light);
                    color_gray = v.getResources().getColor(android.R.color.darker_gray);
                }

            }

        }


        private class HeaderViewHolder extends RecyclerView.ViewHolder {
            public TextView time;

            public HeaderViewHolder(View v) {
                super(v);
                time = v.findViewById(R.id.Time);

            }
        }

        public class assetHolder extends RecyclerView.ViewHolder {
            TextView name;
            TextView income;
            TextView expense;
            TextView remittance;
            TextView reward;

            public assetHolder(View v) {
                super(v);
                name = v.findViewById(R.id.acc_name);
                income = v.findViewById(R.id.income);
                expense = v.findViewById(R.id.expense);
                remittance = v.findViewById(R.id.remittance);
                reward = v.findViewById(R.id.rewardview);
            }

    }
}
