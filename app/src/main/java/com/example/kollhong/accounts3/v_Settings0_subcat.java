package com.example.kollhong.accounts3;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.database.Cursor;
import android.graphics.Point;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Display;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Surface;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by KollHong on 01/06/2018.
 */

public class v_Settings0_subcat extends AppCompatActivity {
    zDBMan mDB;

    long cat_id;
    int cat_level;
    TextView namefield;




    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.v_pref0_categories_edit);
        cat_id = getIntent().getLongExtra("id", 0);

        mDB = new zDBMan(getApplicationContext(),true);
        cat_level =mDB.getCatLevel(cat_id);

        String name = mDB.getCategoryName(cat_id);
        namefield= findViewById(R.id.cat_edit_name);
        namefield.setText(name);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Category_Recycler(mDB.getChildCategoryList(name));


        final FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.cat_fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                View listener =  getLayoutInflater().inflate(R.layout.v_pref0_categories_edit_diag,null);
                final EditText editText = listener.findViewById(R.id.diag_text);
                editText.setHint(R.string.title_frag_settings_name_add_hint);

                AlertDialog.Builder builder = new AlertDialog.Builder(v_Settings0_subcat.this);
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
                                mDB.addCat(cat_level, cat_id, name);       //부모 카테고리의 id
                                String name2 = mDB.getCategoryName(cat_id);
                                Category_Recycler(mDB.getChildCategoryList(name2));
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


            AlertDialog.Builder builder2 = new AlertDialog.Builder(v_Settings0_subcat.this);
            builder2.setTitle(R.string.deleteask);
            builder2.setMessage(R.string.deleteaskmsg);
            builder2.setCancelable(true);

            builder2.setPositiveButton(R.string.delete,
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            mDB.deleteAcc(cat_id);
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
        mDB.updateCat(cat_id, name);
        finish();
    }

    private void Category_Recycler(Cursor cursor) {
        List<zDBMan.ItemCat> itemCats = new ArrayList<>();
        RecyclerView recyclerView = findViewById(R.id.pref_categories_edit_recycler);

        if (cursor.getCount() != 0) {
            while (cursor.moveToNext()) {
                zDBMan.ItemCat item = mDB.getItemCat();
                item.id = cursor.getLong(0);
                item.name = cursor.getString(1);
                itemCats.add(item);
            }
        }
        cursor.close();

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        Category_adapter categoryadapter = new Category_adapter(itemCats);
        recyclerView.setAdapter(categoryadapter);
        categoryadapter.notifyDataSetChanged();
    }

    private  class Category_adapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
        List<zDBMan.ItemCat> items;
        Category_adapter(List<zDBMan.ItemCat> item2) {
            items = item2;
        }
        String name;
        long idL;
        EditText editText;

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = getLayoutInflater().inflate(R.layout.w_add_transactions_category_picker_holder, parent, false);
            return new Category_adapter.categoryNameHolder(view);
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            final zDBMan.ItemCat itemcat = items.get(position);


            Category_adapter.categoryNameHolder newholder = (Category_adapter.categoryNameHolder) holder;
            newholder.textView.setText(itemcat.name);
            View v = newholder.textView;
            v.setTag(itemcat.id+"");
            newholder.textView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {       //
                    //텍스트의 이름과 번호 받아서 다이얼로그 생성
                    TextView view = (TextView) v;


                    idL = Long.parseLong( view.getTag().toString());

                    name = String.valueOf(view.getText());
                    //TODO 이름 변경 팝업 띄우기


                    buildDialog();


                }

                private void buildDialog() {
                    View view =  getLayoutInflater().inflate(R.layout.v_pref0_categories_edit_diag,null);
                    editText = view.findViewById(R.id.diag_text);
                    editText.setText(name);

                    AlertDialog.Builder builder = new AlertDialog.Builder(v_Settings0_subcat.this);
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
                                    name = String.valueOf(editText.getText());
                                    mDB.updateCat(idL,name);
                                    String name = mDB.getCategoryName(cat_id);
                                    Category_Recycler(mDB.getChildCategoryList(name));

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
                                    mDB.deleteCat(idL);
                                    dialog.dismiss();
                                }
                            });

                    AlertDialog alertDialog = builder.create();
                    alertDialog.show();

                }

            });
        }

        @Override
        public int getItemCount() {
//                return 4;
            return items.size();
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
                    params.width = point.x;

                else params.width = point.y ;
            }
        }

    }
}
