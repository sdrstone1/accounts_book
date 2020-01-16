package com.example.kollhong.accounts3;

import android.app.Activity;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.*;
import android.widget.TextView;
import com.example.kollhong.accounts3.RecyclerItem.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import static com.example.kollhong.accounts3.RecyclerItem.*;

public class Recycler_Adapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{

    //Context context;
    Activity activity;
    List<RecyclerItem> items;
    List<RecyclerItem> itemsSearched;
    View.OnClickListener listener;
    //int context;

    Recycler_Adapter(Activity ac, List<RecyclerItem> item, View.OnClickListener clickListener) {
        super();
        activity = ac;
        items = item;
        listener = clickListener;
        itemsSearched = new ArrayList<>();
        itemsSearched.addAll(items);
        //this.context = context;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        switch (viewType) {
            case Settings_Asset_Item:
            case Settings_Category_Item:
            case TransactionAdd_Activity_ASSET_ITEM:
            case TransactionAdd_Activity_CATEGORY_ITEM:
                view = activity.getLayoutInflater().inflate(R.layout.w_add_transactions_category_picker_holder, parent, false);
                return new settingsViewholder(view);
            case Transaction_History_HEADER:
                view = LayoutInflater.from(activity).inflate(R.layout.a_trans_frag0_header, parent, false);
                return new assetSummaryHeaderHolder(view);

            case Transaction_History_CONTENT:
                view = LayoutInflater.from(activity).inflate(R.layout.a_trans_frag0_content, parent, false);
                return new ContentViewHolder(view);

            case Statistics_Asset_HEADER:
                view = LayoutInflater.from(activity).inflate(R.layout.b_manage_frag0_recycler, parent, false);
                return new assetSummaryHolder(view);

            case Statistics_Category_HEADER:
                view = LayoutInflater.from(activity).inflate(R.layout.b_manage_frag0_recycler, parent, false);
                return new categorySummaryHolder(view);

            default:

                Log.w("리사이클러뷰", "에러");
                return null;

        }
    }

    @Override
    public int getItemViewType(int position) {
        return items.get(position).getType();
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        RecyclerItem item = items.get(position);
        switch (item.getType()) {
            case Settings_Asset_Item: {
                AssetSettingsItem assetitem = (AssetSettingsItem) item;

                settingsViewholder viewholder = (settingsViewholder) holder;
                viewholder.textView.setText(assetitem.item.name);
                View v = viewholder.textView;
                v.setTag(item);
                viewholder.textView.setOnClickListener(listener);
                break;
            }

            case Settings_Category_Item: {
                CategorySettingsItem categoryitem = (CategorySettingsItem) item;
                settingsViewholder viewholder = (settingsViewholder) holder;
                viewholder.textView.setText(categoryitem.nameOnlyItem.name);
                View v = viewholder.textView;
                v.setTag(item);
                viewholder.textView.setOnClickListener(listener);
                break;
            }
            case Transaction_History_HEADER: {
                assetSummaryHeaderHolder viewHolder = (assetSummaryHeaderHolder) holder;
                dateHeaderItem dateHeaderItem = (dateHeaderItem) item;

                dateHeaderItem.date.setTime(dateHeaderItem.transactionTime);
                dateHeaderItem.format = dateHeaderItem.df.format(dateHeaderItem.date);

                viewHolder.time.setText(dateHeaderItem.format);
                break;
            }
            case Transaction_History_CONTENT: {

                ContentViewHolder contentViewHolder = (ContentViewHolder) holder;

                HistoryContentItem historyContentItem = (HistoryContentItem) item;

                holder.itemView.setTag(historyContentItem);
                holder.itemView.setOnClickListener(listener);


                if (historyContentItem.item.categoryLevel == 0 || historyContentItem.item.categoryLevel == 1) { //0,1,2        3,4,5           6,7,8
                    setTextLevel1(contentViewHolder, historyContentItem, contentViewHolder.color_blue);
                } else if (historyContentItem.item.categoryLevel == 2) {
                    setTextLevel2(contentViewHolder, historyContentItem, contentViewHolder.color_blue);
                } else if (historyContentItem.item.categoryLevel == 3 || historyContentItem.item.categoryLevel == 4) {
                    setTextLevel1(contentViewHolder, historyContentItem, contentViewHolder.color_red);
                } else if (historyContentItem.item.categoryLevel == 5) {
                    setTextLevel2(contentViewHolder, historyContentItem, contentViewHolder.color_red);
                } else if (historyContentItem.item.categoryLevel == 6 || historyContentItem.item.categoryLevel == 7) {
                    setTextLevel1(contentViewHolder, historyContentItem, contentViewHolder.color_gray);
                } else if (historyContentItem.item.categoryLevel == 8) {
                    setTextLevel1(contentViewHolder, historyContentItem, contentViewHolder.color_gray);
                }

                contentViewHolder.list_amount.setText(String.valueOf(historyContentItem.item.amount));
                contentViewHolder.list_accounts.setText(historyContentItem.item.assetName);
                contentViewHolder.list_reci.setText(historyContentItem.item.recipientName);
                break;
            }
            case Statistics_Asset_HEADER: {

                AssetSummaryItem assetItem = (AssetSummaryItem) item;
                assetSummaryHolder assetSummaryHolder = (assetSummaryHolder) holder;

                assetSummaryHolder.name.setText(assetItem.assetName);
                assetSummaryHolder.income.setText(String.valueOf(assetItem.IncomeSummary));
                assetSummaryHolder.expense.setText(String.valueOf(assetItem.ExpenseSummary));
                assetSummaryHolder.remittance.setText(String.valueOf(assetItem.TransferSummary));
                CharSequence string = activity.getApplicationContext().getResources().getText(R.string.reward);
                String reward = String.valueOf(Float.valueOf(assetItem.RewardSummary).intValue());
                assetSummaryHolder.reward.setText(string + "   " + reward);

                break;
            }
            case Statistics_Category_HEADER: {

                categorySummaryHolder catSummaryHolder = (categorySummaryHolder) holder;
                CategorySummaryItem catSummaryItem = (CategorySummaryItem) item;

                catSummaryHolder.name.setText(catSummaryItem.assetName);
                //accHolder.income.setText(String.valueOf(itemVO.amount_in));
                catSummaryHolder.expense.setText(String.valueOf(catSummaryItem.amount));
                //accHolder.remittance.setText(String.valueOf(itemVO.amount_rem));

                break;
            }
            case TransactionAdd_Activity_ASSET_ITEM: {
                TransactionAddAssetItem transactionAddAssetItem = (TransactionAddAssetItem) item;

                settingsViewholder newholder = (settingsViewholder) holder;
                newholder.textView.setText(transactionAddAssetItem.item.name);
                View v = newholder.textView;
                v.setTag(transactionAddAssetItem);
                newholder.textView.setOnClickListener(listener);
                break;
            }
            case TransactionAdd_Activity_CATEGORY_ITEM: {
                TransactionAddCategoryItem transactionAddCategoryItem = (TransactionAddCategoryItem) item;

                settingsViewholder newholder = (settingsViewholder) holder;
                newholder.textView.setText(transactionAddCategoryItem.nameOnlyItem.name);
                View v = newholder.textView;
                v.setTag(transactionAddCategoryItem);
                newholder.textView.setOnClickListener(listener);
                break;
            }
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
        for (RecyclerItem wp : itemsSearched) {
            if (wp.getType() == Transaction_History_HEADER) {
                items.add(wp);
            } else if (wp.getType() == Transaction_History_CONTENT) {
                //wp = (ContentItem) wp;
                if (((HistoryContentItem) wp).item.recipientName.toLowerCase(Locale.getDefault())
                        .contains(query)) {
                    items.add(wp);

                }
            }
        }
        notifyDataSetChanged();
    }


    private void setTextLevel1(ContentViewHolder contentViewHolder, HistoryContentItem historyContentItem, int color){
        contentViewHolder.list_cat.setText(historyContentItem.item.categoryName);
        contentViewHolder.list_cat.setTextColor(color);
        contentViewHolder.list_cat2nd.setText("");
        contentViewHolder.list_amount.setTextColor(color);
    }

    private void setTextLevel2(ContentViewHolder contentViewHolder, HistoryContentItem historyContentItem, int color){
        contentViewHolder.list_cat.setText(historyContentItem.item.parentCategoryName);
        contentViewHolder.list_cat.setTextColor(color);
        contentViewHolder.list_cat2nd.setText(historyContentItem.item.categoryName);
        contentViewHolder.list_amount.setTextColor(color);
    }


    public class settingsViewholder extends RecyclerView.ViewHolder {
        TextView textView;

        public settingsViewholder(View v) {
            super(v);
            textView =  v.findViewById(R.id.add_tran_category_name);

            Point point = new Point();
            Display display = activity.getWindowManager().getDefaultDisplay();
            int rotation = display.getRotation();
            display.getSize(point);

            /*
            디스플레이 정보 가져오기- 사용하지는 않았지만 좋은 정보여서 남겨 둠
            float density  = getResources().getDisplayMetrics().density;
            float dpHeight = outMetrics.heightPixels / density;
            float dpWidth  = outMetrics.widthPixels / density;
*/
            ViewGroup.LayoutParams params = v.getLayoutParams();


            if (rotation == Surface.ROTATION_0 || rotation == Surface.ROTATION_180)
                params.width = point.x ;

            else params.width = point.y;
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
            color_blue = GlobalFunction.Color.getColor(activity, android.R.color.holo_blue_light);
            color_red = GlobalFunction.Color.getColor(activity, android.R.color.holo_red_light);
            color_gray = GlobalFunction.Color.getColor(activity, android.R.color.darker_gray);

        }

    }


    private static class assetSummaryHeaderHolder extends RecyclerView.ViewHolder {
        public TextView time;

        public assetSummaryHeaderHolder(View v) {
            super(v);
            time = v.findViewById(R.id.Time);

        }
    }

    public static class assetSummaryHolder extends RecyclerView.ViewHolder {
        TextView name;
        TextView income;
        TextView expense;
        TextView remittance;
        TextView reward;

        public assetSummaryHolder(View v) {
            super(v);
            name = v.findViewById(R.id.acc_name);
            income = v.findViewById(R.id.income);
            expense = v.findViewById(R.id.expense);
            remittance = v.findViewById(R.id.remittance);
            reward = v.findViewById(R.id.rewardview);
        }
    }


    public static class categorySummaryHolder extends RecyclerView.ViewHolder {
        TextView name;
        //TextView income;
        TextView expense;
        //TextView remittance;

        public categorySummaryHolder(View v) {
            super(v);
            name = v.findViewById(R.id.acc_name);
            v.findViewById(R.id.income).setVisibility(View.INVISIBLE);
            expense = v.findViewById(R.id.expense);
            v.findViewById(R.id.remittance).setVisibility(View.INVISIBLE);
            v.findViewById(R.id.inc).setVisibility(View.INVISIBLE);
            v.findViewById(R.id.remmi).setVisibility(View.INVISIBLE);
        }
    }




    static public class DividerItemDecoration extends RecyclerView.ItemDecoration {

        private Drawable divider;

        /**
         * Default divider will be used
         */
        public DividerItemDecoration(Context context) {
            int[] ATTRS = new int[]{android.R.attr.listDivider};
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

