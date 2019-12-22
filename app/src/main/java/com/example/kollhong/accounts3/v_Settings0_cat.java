package com.example.kollhong.accounts3;

import android.app.ActivityOptions;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Point;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
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
import android.widget.EditText;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by KollHong on 31/05/2018.
 */

public class v_Settings0_cat extends AppCompatActivity {
    Context context;

    zDBMan mDB;

    Long cat_id;
    int cat_level;

    String tab_name;
    long tab_num;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        getWindow().requestFeature(Window.FEATURE_CONTENT_TRANSITIONS);
        Slide slide = new Slide();
        slide.setSlideEdge(Gravity.RIGHT);
        getWindow().setEnterTransition(slide);

        super.onCreate(savedInstanceState);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);  //뒤로 가기 버튼

        setContentView(R.layout.v_pref0_categories);
        context=getApplicationContext();

        mDB = new zDBMan(context,true);
        TabLayout tabLayout = findViewById(R.id.cat_tab);
        tabLayout.addOnTabSelectedListener(new tabListener());
        TabLayout.Tab tab = tabLayout.getTabAt(1);
        tab.select();

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.cat_add);
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
                    tab_num = 1l;
                    Category_Recycler(mDB.getChildCategoryList(tab_num));
                    cat_id = 1l;
                    cat_level = 0;
                    break;
                }
                case 1: {
                    tab_num = 2l;
                    Category_Recycler(mDB.getChildCategoryList(tab_num));
                    cat_id = 2l;
                    cat_level = 3;

                    break;

                }
                case 2: {
                    tab_num = 3l;
                    Category_Recycler(mDB.getChildCategoryList(tab_num));
                    cat_id = 3l;
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

    private void Category_Recycler(Cursor cursor) {
        List<zDBMan.ItemCat> itemCats;

        RecyclerView recyclerView = findViewById(R.id.pref_categories_recycler);

        itemCats = new ArrayList<>();
        if (cursor.getCount() != 0) {
            while (cursor.moveToNext()) {
                zDBMan.ItemCat item = mDB.getItemCat();
                item.id = cursor.getLong(0);
                item.name = cursor.getString(1);
                itemCats.add(item);
            }
        }
        cursor.close();

        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        Category_adapter categoryadapter = new Category_adapter(itemCats);
        recyclerView.setAdapter(categoryadapter);
        categoryadapter.notifyDataSetChanged();

    }

    private class fabListener implements View.OnClickListener{
        @Override
        public void onClick(View v) {
            View editView =  getLayoutInflater().inflate(R.layout.v_pref0_categories_edit_diag,null);
            final EditText editText = editView.findViewById(R.id.diag_text);
            editText.setHint(R.string.title_frag_settings_name_add_hint);

            AlertDialog.Builder builder = new AlertDialog.Builder(v_Settings0_cat.this);
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

    private class Category_adapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
        List<zDBMan.ItemCat> items;

        Category_adapter(List<zDBMan.ItemCat> item2) {
            items = item2;
        }

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
            v.setTag(itemcat.id);
            newholder.textView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {       //자녀 카테고리가 있으면 다시 다이얼로그 띄우고 자녀가 없으면 완료.

                    TextView view = (TextView) v;

                    long id = (long)view.getTag();
                    //TODO 아이디로 설정 편집 이동.
                    Intent intent = new Intent(context, v_Settings0_subcat.class);
                    intent.putExtra("id", id);

                    startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(v_Settings0_cat.this).toBundle());



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
