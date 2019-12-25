package com.example.kollhong.accounts3;

import android.content.ContentValues;
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
import java.util.ListIterator;

import static com.example.kollhong.accounts3.zDBScheme.ASSET_TABLE.*;
import static com.example.kollhong.accounts3.zDBScheme.TABLE_ID;

/**
 * Created by KollHong on 31/05/2018.
 */

public class v_Settings2_acc extends AppCompatActivity {
    zDBMan mDB;

    //List<zDBMan.ItemAcc> itemAccs = new ArrayList<>();

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

    private void Account_recycler(List<ContentValues> valuesList) {

        RecyclerView recyclerView = findViewById(R.id.pref_accounts_recycler);

        //itemAccs = new ArrayList<>();
        ListIterator valuesListIter = valuesList.listIterator();

        List<zRecyclerAdapt_Gen.recyclerItem> assetItemList = new ArrayList<>();


        while (valuesListIter.hasNext()) {
            ContentValues values = (ContentValues) valuesListIter.next();
            zRecyclerAdapt_Gen.assetItem item =  new zRecyclerAdapt_Gen.assetItem();
            item.id = values.getAsLong(TABLE_ID);
            item.asset_type = values.getAsLong(ASSET_TYPE);
            item.name = values.getAsString(NAME);
            item.balance = values.getAsFloat(BALANCE);
            item.withdrawalaccount = values.getAsLong(WITHDRAWALACCOUNT);
            item.withdrawalday = values.getAsLong(WITHDRAWALDAY);
            item.cardid = values.getAsLong(CARD_ID);
            assetItemList.add(item);
        }


        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        zRecyclerAdapt_Gen.CatAssetAdapter assetAdapter = new zRecyclerAdapt_Gen.CatAssetAdapter(this, assetItemList,new assetOnClickListener());
        recyclerView.setAdapter(assetAdapter);
        assetAdapter.notifyDataSetChanged();



    }


    private void updateAccount(View v){
        //TextView view = (TextView) v;
        zRecyclerAdapt_Gen.assetItem itemAcc = (zRecyclerAdapt_Gen.assetItem) v.getTag();

        Intent intent = new Intent(getApplicationContext(),v_Settings2_acc_add.class);
        intent.putExtra("isUpdate", true);
        intent.putExtra("id",itemAcc.id);
        startActivity(intent);

    }   //TODO 데이터 무결성 확인

    private class assetOnClickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            updateAccount(v);
        }
    }

    /*
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

                ViewGroup.LayoutParams params = v.getLayoutParams();


                if (rotation == Surface.ROTATION_0 || rotation == Surface.ROTATION_180)
                    params.width = point.x ;

                else params.width = point.y ;
            }
        }

    }
    */
}
