package com.example.kollhong.accounts3;

import android.app.ActivityOptions;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.transition.Slide;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

/**
 * Created by KollHong on 31/05/2018.
 */

public class Settings_Category extends AppCompatActivity {
    Context context;

    DB_Controll mDB;

    int cat_id;
    int cat_level;

    String tab_name;
    int tab_num;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        if(BuildConfig.isTEST){
            Log.i("Launch Activity : ","Settings_Category");
        }
        getWindow().requestFeature(Window.FEATURE_CONTENT_TRANSITIONS);
        Slide slide = new Slide();
        slide.setSlideEdge(Gravity.END);
        getWindow().setEnterTransition(slide);

        super.onCreate(savedInstanceState);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);  //뒤로 가기 버튼

        setContentView(R.layout.v_pref0_categories);
        context=getApplicationContext();

        mDB = new DB_Controll(context,true);
        TabLayout tabLayout = findViewById(R.id.cat_tab);
        tabLayout.addOnTabSelectedListener(new tabListener());
        TabLayout.Tab tab = tabLayout.getTabAt(1);
        tab.select();

        FloatingActionButton fab = findViewById(R.id.cat_add);
        fab.setOnClickListener(new fabListener());
    }

    @Override
    protected void onResume() {
        super.onResume();
        Category_Recycler(mDB.getChildCategoryList(tab_num));
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == android.R.id.home) {
            this.finish();
            return true;
        }
        return false;
    }

    private class tabListener implements TabLayout.OnTabSelectedListener{
        @Override
        public void onTabSelected(TabLayout.Tab tab) {

            switch (tab.getPosition()) {
                case 0: {
                    tab_num = 1;
                    Category_Recycler(mDB.getChildCategoryList(tab_num));
                    cat_id = 1;
                    cat_level = 0;
                    break;
                }
                case 1: {
                    tab_num = 2;
                    Category_Recycler(mDB.getChildCategoryList(tab_num));
                    cat_id = 2;
                    cat_level = 3;

                    break;

                }
                case 2: {
                    tab_num = 3;
                    Category_Recycler(mDB.getChildCategoryList(tab_num));
                    cat_id = 3;
                    cat_level = 6;

                    break;
                }
            }
        }

        @Override
        public void onTabUnselected(TabLayout.Tab tab) {}

        @Override
        public void onTabReselected(TabLayout.Tab tab) {}

    }

    class categoryClickListener implements View.OnClickListener{
        @Override
        public void onClick(View v) {       //자녀 카테고리가 있으면 다시 다이얼로그 띄우고 자녀가 없으면 완료.

            TextView view = (TextView) v;

            RecyclerItem.CategorySettingsItem recyclerItem = (RecyclerItem.CategorySettingsItem)view.getTag();
            //TODO 아이디로 설정 편집 이동.
            Intent intent = new Intent(context, Settings_CategoryLEVEL2.class);
            intent.putExtra("id", recyclerItem.nameOnlyItem.tableId);

            startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(Settings_Category.this).toBundle());
        }
    }

    private void Category_Recycler(List<DBItem.CategoryItem> contentList) {
        List<RecyclerItem> recyclerItemList = new ArrayList<>();
        ListIterator<DBItem.CategoryItem> contentListIterator = contentList.listIterator();


        RecyclerView recyclerView = findViewById(R.id.pref_categories_recycler);

        for (DBItem.CategoryItem content : contentList) {
            content = contentListIterator.next();
            RecyclerItem.CategorySettingsItem item = new RecyclerItem.CategorySettingsItem();
            item.nameOnlyItem.tableId = content.tableId;
            item.nameOnlyItem.name = content.name;
            recyclerItemList.add(item);
        }

        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        Recycler_Adapter categoryadapter
                = new Recycler_Adapter(this,recyclerItemList, new categoryClickListener());

        recyclerView.setAdapter(categoryadapter);
        categoryadapter.notifyDataSetChanged();

    }

    private class fabListener implements View.OnClickListener{
        @Override
        public void onClick(View v) {
            View editView =  getLayoutInflater().inflate(R.layout.v_pref0_categories_edit_diag,null);
            final EditText editText = editView.findViewById(R.id.diag_text);
            editText.setHint(R.string.title_frag_settings_name_add_hint);

            AlertDialog.Builder builder = new AlertDialog.Builder(Settings_Category.this);
            builder.setTitle(R.string.title_frag_settings_name_add);
            //builder.setMessage(R.string.title_frag_settings_name_add);
            builder.setCancelable(true);

            builder.setView(editView);

            builder.setPositiveButton(R.string.save,
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            SaveinDialog();
                            dialog.cancel();
                        }

                        public void SaveinDialog() {
                            String name = String.valueOf(editText.getText());
                            mDB.addCat(cat_level, cat_id, name);
                            Category_Recycler(mDB.getChildCategoryList(cat_id));

                        }
                    });

            builder.setNegativeButton(R.string.title_alertdialog_cancel_button,
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.cancel();
                        }
                    });

            AlertDialog alertDialog = builder.create();
            alertDialog.show();
        }
    }

}
