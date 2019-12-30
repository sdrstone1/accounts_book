package com.example.kollhong.accounts3;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by KollHong on 01/06/2018.
 */

public class Settings_CategoryLEVEL2 extends AppCompatActivity {
    DB_Controll mDB;

    long parent_cat_id;
    int parent_cat_level;
    TextView namefield;




    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.v_pref0_categories_edit);
        parent_cat_id = getIntent().getLongExtra("id", 0);

        mDB = new DB_Controll(getApplicationContext(),true);
        parent_cat_level =mDB.getCatLevel(parent_cat_id);

        String name = mDB.getCategoryName(parent_cat_id);
        namefield= findViewById(R.id.cat_edit_name);
        namefield.setText(name);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Category_Recycler(mDB.getChildCategoryList(parent_cat_id));


        final FloatingActionButton fab =  findViewById(R.id.cat_fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                View listener =  getLayoutInflater().inflate(R.layout.v_pref0_categories_edit_diag,null);
                final EditText editText = listener.findViewById(R.id.diag_text);
                editText.setHint(R.string.title_frag_settings_name_add_hint);

                AlertDialog.Builder builder = new AlertDialog.Builder(Settings_CategoryLEVEL2.this);
                builder.setTitle(R.string.title_frag_settings_name_add);
                builder.setCancelable(true);

                builder.setView(listener);

                builder.setPositiveButton(R.string.save,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                SaveinDialog();
                                dialog.cancel();
                            }

                            public void SaveinDialog() {
                                String name = String.valueOf(editText.getText());
                                mDB.addCat(parent_cat_level, parent_cat_id, name);       //부모 카테고리의 id
                                //String name2 = mDB.getCategoryName(cat_id);
                                Category_Recycler(mDB.getChildCategoryList(parent_cat_id));
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
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.x_save_toolbar, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();


        if (id == R.id.menu_save_but) {
            onSave();

            return true;
        }
        else if( id == android.R.id.home){
            this.finish();
            return true;
        }
        else if(id == R.id.itemDelete){


            AlertDialog.Builder builder2 = new AlertDialog.Builder(Settings_CategoryLEVEL2.this);
            builder2.setTitle(R.string.deleteask);
            builder2.setMessage(R.string.deleteaskmsg);
            builder2.setCancelable(true);

            builder2.setPositiveButton(R.string.delete,
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            mDB.deleteAcc(parent_cat_id);
                            dialog.dismiss();
                            finish();
                        }
                    });

            builder2.setNegativeButton(R.string.title_alertdialog_cancel_button,
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.cancel();
                        }
                    });
            AlertDialog alertDialog1 = builder2.create();
            alertDialog1.show();





        }
        return super.onOptionsItemSelected(item);
    }

    public void onSave(){
        String name = namefield.getText() + "";
        mDB.updateCat(parent_cat_id, name);
        finish();
    }

    private void Category_Recycler(List<DBItem.CategoryItem> contentList) {
        List<RecyclerItem> recyclerItemList = new ArrayList<>();
        //ListIterator<DBItem.CategoryItem> contentListIterator = contentList.listIterator();


        RecyclerView recyclerView = findViewById(R.id.pref_categories_edit_recycler);

        for(DBItem.CategoryItem content : contentList){
            RecyclerItem.CategorySettingsItem item = new RecyclerItem.CategorySettingsItem();
            item.nameOnlyItem.tableId = content.tableId;
            item.nameOnlyItem.name = content.name;
            recyclerItemList.add(item);
        }

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        Recycler_Adapter categoryadapter = new Recycler_Adapter(this,recyclerItemList,new categoryClickListener());
        recyclerView.setAdapter(categoryadapter);
        categoryadapter.notifyDataSetChanged();
    }



    private class categoryClickListener implements View.OnClickListener{

        DBItem.NameOnlyItem nameOnlyItem;
        EditText editText;


        @Override
        public void onClick(View v) {//
            //텍스트의 이름과 번호 받아서 다이얼로그 생성
            TextView view = (TextView) v;

            RecyclerItem.CategorySettingsItem categoryitem = (RecyclerItem.CategorySettingsItem) v.getTag();

            nameOnlyItem = categoryitem.nameOnlyItem;

            buildDialog();
        }

        private void buildDialog() {
            View view = getLayoutInflater().inflate(R.layout.v_pref0_categories_edit_diag, null);
            editText = view.findViewById(R.id.diag_text);
            editText.setText(nameOnlyItem.name);

            AlertDialog.Builder builder = new AlertDialog.Builder(Settings_CategoryLEVEL2.this);
            builder.setTitle(R.string.title_frag_settings_name_update);
            builder.setCancelable(true);

            builder.setView(view);

            builder.setPositiveButton(R.string.title_alertdialog_update_button,
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            SaveinDialog();
                            dialog.cancel();
                        }

                        private void SaveinDialog() {
                            nameOnlyItem.name = String.valueOf(editText.getText());
                            mDB.updateCat(nameOnlyItem.tableId, nameOnlyItem.name);
                            //String name = mDB.getCategoryName(cat_id);
                            Category_Recycler(mDB.getChildCategoryList(parent_cat_id));

                        }
                    });

            builder.setNegativeButton(R.string.title_alertdialog_cancel_button,
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.cancel();
                        }
                    });
            builder.setNeutralButton(R.string.delete,
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            mDB.deleteCat(nameOnlyItem.tableId);
                            dialog.dismiss();
                        }
                    });
            AlertDialog alertDialog = builder.create();
            alertDialog.show();
        }
    }
}
