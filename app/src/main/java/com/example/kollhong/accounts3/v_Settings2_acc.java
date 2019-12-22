package com.example.kollhong.accounts3;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Point;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.transition.Slide;
import android.view.Display;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.Surface;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by KollHong on 31/05/2018.
 */

public class v_Settings2_acc extends AppCompatActivity {
    zDBMan mDB;

    List<zDBMan.ItemAcc> itemAccs = new ArrayList<>();

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        getWindow().requestFeature(Window.FEATURE_CONTENT_TRANSITIONS);
        // set an exit transition
        Slide slide = new Slide();
        slide.setSlideEdge(Gravity.RIGHT);
        getWindow().setEnterTransition(slide);

        super.onCreate(savedInstanceState);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setContentView(R.layout.v_pref2_accounts);

        mDB = new zDBMan(getApplicationContext(),true);



        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.acc_add);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), v_Settings2_acc_add.class);
                startActivity(intent);
            }
        });

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == android.R.id.home) {
            this.finish();
            return true;
        }
        return false;
    }

    @Override
    protected void onResume() {
        super.onResume();
        Account_recycler(mDB.getAssetList());
    }

    private void Account_recycler(Cursor cursor) {

        RecyclerView recyclerView = findViewById(R.id.pref_accounts_recycler);

        itemAccs = new ArrayList<>();

        if (cursor.getCount() != 0) {
            while (cursor.moveToNext()) {
                zDBMan.ItemAcc item =  mDB.getItemAcc();
                item.id = cursor.getLong(0);
                item.type = cursor.getInt(1);
                item.name = cursor.getString(2);
                item.balance = cursor.getFloat(3);
                item.withdrawalaccount = cursor.getInt(4);
                item.withdrawalday = cursor.getInt(5);
                item.cardid = cursor.getLong(6);
                itemAccs.add(item);
            }
        }
        cursor.close();

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        Account_adapter accountadapter = new Account_adapter();
        recyclerView.setAdapter(accountadapter);
        accountadapter.notifyDataSetChanged();



    }


    private void updateAccount(View v){
        //TextView view = (TextView) v;
        zDBMan.ItemAcc itemAcc = (zDBMan.ItemAcc) v.getTag();

        Intent intent = new Intent(getApplicationContext(),v_Settings2_acc_add.class);
        intent.putExtra("isUpdate", true);
        intent.putExtra("id",itemAcc.id);
        startActivity(intent);

    }   //TODO 데이터 무결성 확인

    private  class Account_adapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
        Account_adapter() {}

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = getLayoutInflater().inflate(R.layout.w_add_transactions_category_picker_holder, parent, false);
            return new categoryNameHolder(view);
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

            final zDBMan.ItemAcc itemacc = itemAccs.get(position);

            categoryNameHolder newholder = (categoryNameHolder) holder;
            newholder.textView.setText(itemacc.name);
            View v = (View) newholder.textView;
            v.setTag(itemacc);
            newholder.textView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    updateAccount(v);
                }

            });
        }

        @Override
        public int getItemCount() {
            return itemAccs.size();
        }

        public class categoryNameHolder extends RecyclerView.ViewHolder {
            TextView textView;

            public categoryNameHolder(View v) {
                super(v);
                textView = (TextView) v.findViewById(R.id.add_tran_category_name);
                Point point = new Point();
                Display display = getWindowManager().getDefaultDisplay();
                int rotation = display.getRotation();
                display.getSize(point );

                /*
                float density  = getResources().getDisplayMetrics().density;
                float dpHeight = outMetrics.heightPixels / density;
                float dpWidth  = outMetrics.widthPixels / density;
*/
                ViewGroup.LayoutParams params = v.getLayoutParams();


                if (rotation == Surface.ROTATION_0 || rotation == Surface.ROTATION_180)
                    params.width = point.x ;

                else params.width = point.y ;
            }
        }
    }
}
