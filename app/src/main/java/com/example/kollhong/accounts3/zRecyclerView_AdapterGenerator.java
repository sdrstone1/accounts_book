package com.example.kollhong.accounts3;

import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.graphics.Point;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.Display;
import android.view.Surface;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

public class zRecyclerView_AdapterGenerator {

    zRecyclerView_AdapterGenerator(){
    }

    static class recyclerItem{
        int class_type; //type 0 == asset, type 1 == category selector, type 2 == cagetory settings
        long id;
        String name;
        long type = 1;
        long cardid;
        float balance;
        long withdrawalaccount;
        long withdrawalday;
        String nickname;
        //_id, type, name, balance, withdrawalaccount, withdrawalday, cardid

    }
    Category_adapter getCategoryAdapter(Context cxt,AppCompatActivity ac, List<recyclerItem> items, View.OnClickListener clickListener){

        return new Category_adapter(cxt, ac, items,clickListener);
    }

    public static class Category_adapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
        Context context;
        AppCompatActivity activity;
        List<recyclerItem> items;
        View.OnClickListener listener;


        Category_adapter(Context cxt,AppCompatActivity ac, List<recyclerItem> item, View.OnClickListener clickListener) {
            context = cxt;
            activity = ac;
            items = item;
            listener = clickListener;
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = activity.getLayoutInflater().inflate(R.layout.w_add_transactions_category_picker_holder, parent, false);
            return new viewHolder(view);
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            recyclerItem item = items.get(position);

            viewHolder newholder = (viewHolder) holder;
            newholder.textView.setText(item.name);
            View v = newholder.textView;
            v.setTag(item);
            newholder.textView.setOnClickListener(listener);
        }

        @Override
        public int getItemCount() {
//                return 4;
            return items.size();
        }

        public class viewHolder extends RecyclerView.ViewHolder {
            TextView textView;

            public viewHolder(View v) {
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

    }
}
