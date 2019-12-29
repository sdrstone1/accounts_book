package com.example.kollhong.accounts3;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.transition.Slide;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

/**
 * Created by KollHong on 31/05/2018.
 */

public class Settings_Asset extends AppCompatActivity {
    DB_Controll mDB;

    //List<zDBMan.ItemAcc> itemAccs = new ArrayList<>();

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        getWindow().requestFeature(Window.FEATURE_CONTENT_TRANSITIONS);
        // set an exit transition
        Slide slide = new Slide();
        slide.setSlideEdge(Gravity.END);
        getWindow().setEnterTransition(slide);

        super.onCreate(savedInstanceState);
        try {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }catch(NullPointerException e){
            Log.e("SettingAsset OnCreate","setDisplayHomeAsUpEnabled Error");
            e.printStackTrace();
        }
        setContentView(R.layout.v_pref2_assets);

        mDB = new DB_Controll(getApplicationContext(),true);



        FloatingActionButton fab =  findViewById(R.id.acc_add);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), Settings_AssetAdd.class);
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

    private void Account_recycler(List<DBItem.AssetItem> valuesList) {

        RecyclerView recyclerView = findViewById(R.id.pref_accounts_recycler);

        //itemAccs = new ArrayList<>();
        ListIterator valuesListIter = valuesList.listIterator();

        List<RecyclerItem> assetItemList = new ArrayList<>();

        for(DBItem.AssetItem values : valuesList){
            RecyclerItem.AssetSettingsItem item =  new RecyclerItem.AssetSettingsItem();
            item.item= values;
            assetItemList.add(item);
        }


        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        Recycler_Adapter assetAdapter = new Recycler_Adapter(this, assetItemList,new assetOnClickListener());
        recyclerView.setAdapter(assetAdapter);
        assetAdapter.notifyDataSetChanged();



    }


    private void updateAccount(View v){
        //TextView view = (TextView) v;
        RecyclerItem.AssetSettingsItem itemAsset = (RecyclerItem.AssetSettingsItem) v.getTag();

        Intent intent = new Intent(getApplicationContext(), Settings_AssetAdd.class);
        intent.putExtra("isUpdate", true);
        intent.putExtra("id",itemAsset.item.tableId);
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
