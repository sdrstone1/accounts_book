package com.example.kollhong.accounts3;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v7.app.AppCompatActivity;
import android.transition.Slide;
import android.util.Log;
import android.util.TypedValue;
import android.view.ContextThemeWrapper;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by KollHong on 06/06/2018.
 */

public class v_Settings2_acc_add extends AppCompatActivity {
    //View Holder
    EditText name_txt;
    EditText nick_txt;
    TextView bal_txt;
    EditText bal_text ;
    TextView with_acc ;
    Spinner with_acc_spin;
    TextView card_id ;
    Spinner card_id_spin;
    TextView with_date;
    TextView with_date_txt ;

    zDBMan.ItemAcc itemAcc ;
    zDBMan mDB;

    boolean isUpdate;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        getWindow().requestFeature(Window.FEATURE_CONTENT_TRANSITIONS);
        Slide slide = new Slide();
        slide.setSlideEdge(Gravity.RIGHT);
        getWindow().setEnterTransition(slide);
        super.onCreate(savedInstanceState);

        setContentView(R.layout.v_pref2_accounts_add);

        getViews();

        mDB = new zDBMan(getApplicationContext(),true);
        Intent intent = getIntent();
        isUpdate = intent.getBooleanExtra("isUpdate", false);

        set_with_acc_spinner();

        itemAcc = mDB.getItemAcc();

        if(isUpdate){
            long id = intent.getLongExtra("id",0l);
            update(id);
        }

        int TAB_INDEX = itemAcc.type - 1;

        TabLayout tabLayout = findViewById(R.id.add_acc_tablayout);
        tabLayout.addOnTabSelectedListener(new tabListener());
        TabLayout.Tab tab = tabLayout.getTabAt(TAB_INDEX);
        tab.select();


    }

    private class tabListener implements TabLayout.OnTabSelectedListener{
        @Override
        public void onTabSelected(TabLayout.Tab tab) {
            changeViewOnTab(tab.getPosition());
        }

        @Override
        public void onTabUnselected(TabLayout.Tab tab) {
            changeViewOnTab(tab.getPosition());
        }

        @Override
        public void onTabReselected(TabLayout.Tab tab) {
            changeViewOnTab(tab.getPosition());
        }

    }

    private void changeViewOnTab(int position){
        switch(position) {
            case 0: {       //현금 -> 출금계좌, 출금일, 카드 선택 숨기기
                itemAcc.type = 1;

                bal_txt.setText(R.string.balance);
                bal_txt.setVisibility(View.VISIBLE);
                bal_text.setVisibility(View.VISIBLE);
                with_acc.setVisibility(View.INVISIBLE);
                with_acc_spin.setVisibility(View.INVISIBLE);
                card_id.setVisibility(View.INVISIBLE);
                card_id_spin.setVisibility(View.INVISIBLE);
                with_date.setVisibility(View.INVISIBLE);
                with_date_txt.setVisibility(View.INVISIBLE);
                break;
            }
            case 1: {       //체크카드 ->잔액, 출금일 숨기기
                itemAcc.type = 2;

                bal_txt.setText(R.string.balance);
                bal_txt.setVisibility(View.INVISIBLE);
                bal_text.setVisibility(View.INVISIBLE);
                with_acc.setVisibility(View.VISIBLE);
                with_acc_spin.setVisibility(View.VISIBLE);
                card_id.setVisibility(View.VISIBLE);
                card_id_spin.setVisibility(View.VISIBLE);
                with_date.setVisibility(View.INVISIBLE);
                with_date_txt.setVisibility(View.INVISIBLE);
                // 카드 스피너 가져오기 ->세팅
                set_card_spinner(2);
                set_card_id_spinner_onUpdate();
                break;
            }
            case 2: {       //신용카드 -> 잔액 ->한도로 변
                itemAcc.type = 3;
                bal_txt.setText(R.string.maxed_out);
                bal_txt.setVisibility(View.VISIBLE);
                bal_text.setVisibility(View.VISIBLE);
                with_acc.setVisibility(View.VISIBLE);
                with_acc_spin.setVisibility(View.VISIBLE);
                card_id.setVisibility(View.VISIBLE);
                card_id_spin.setVisibility(View.VISIBLE);
                with_date.setVisibility(View.VISIBLE);
                with_date_txt.setVisibility(View.VISIBLE);
                // 카드 스피너 가져오기 ->세팅
                set_card_spinner(3);
                set_card_id_spinner_onUpdate();
                break;
            }
        }
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.x_save_toolbar,menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if(id == R.id.menu_save_but)
        {
            itemAcc.name = name_txt.getText()+"";
            itemAcc.nickname = nick_txt.getText()+"";
            itemAcc.balance = Float.valueOf(bal_text.getText()+"") ;
            mDB.addAcc(isUpdate,itemAcc);
            finish();
            return true;
        }
        else if (id == android.R.id.home) {
            this.finish();
            return true;
        }
        else if(id == R.id.itemDelete){
            if (isUpdate){

                mDB.deleteAcc(itemAcc.id);
                this.finish();
            }

        }
        return super.onOptionsItemSelected(item);
    }

    public void onWithDayDialog(View v) {

        AlertDialog.Builder builder = new AlertDialog.Builder(v_Settings2_acc_add.this);
        builder.setTitle(R.string.withdrawal_date);
        builder.setCancelable(true);


        View view = getLayoutInflater().inflate(R.layout.v_pref2_accounts_add_diag,null);
        final NumberPicker datePicker = view.findViewById(R.id.date_picker);
        datePicker.setMaxValue(30);
        datePicker.setMinValue(1);
        datePicker.setValue(itemAcc.withdrawalday);

        builder.setView(view);

        builder.setPositiveButton(R.string.save,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        SaveinDialog();
                        dialog.cancel();
                    }

                    public void SaveinDialog() {
                        itemAcc.withdrawalday =datePicker.getValue();
                        with_date_txt.setText(itemAcc.withdrawalday+"");
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

    private void update(long id){

        Cursor cursor = mDB.getAccInfo(id);
        if(cursor.getCount() != 0){
            cursor.moveToNext();
            //_id, type, name, nickname, balance, withdrawalaccount, withdrawalday, cardid
            itemAcc.id = cursor.getLong(0);
            itemAcc.type = cursor.getInt(1);
            itemAcc.name = cursor.getString(2);
            itemAcc.nickname = cursor.getString(3);
            itemAcc.balance = cursor.getFloat(4);
            itemAcc.withdrawalaccount = cursor.getInt(5);
            itemAcc.withdrawalday = cursor.getInt(6);
            itemAcc.cardid = cursor.getLong(7);

            bal_text.setText(String.valueOf(itemAcc.balance));
            //어카운트 스피너 가져오기 -> 셋

            name_txt.setText(itemAcc.name);
            nick_txt.setText(itemAcc.nickname);

            set_with_acc_spinner_onUpdate();


            with_date_txt.setText(String.valueOf(itemAcc.withdrawalday));
        }
        cursor.close();
    }

    private void set_with_acc_spinner(){
        Cursor cursor = mDB.getAccBankList();

        final List<ItemSpinner> itemBanks = makeadapter(cursor);

        spinadapter spinadapters = new spinadapter(this,android.R.layout.simple_spinner_item,itemBanks);
        with_acc_spin.setAdapter(spinadapters);
        with_acc_spin.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                ItemSpinner itemSpinner = itemBanks.get(position);

                itemAcc.withdrawalaccount = Integer.parseInt(itemSpinner.id+"");
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        cursor.close();
        spinadapters.notifyDataSetChanged();

    }

    private void set_card_spinner(int type){
        Cursor cursor = mDB.getCardList(type-1);
        final List<ItemSpinner> itemCards = makeadapter(cursor);

        spinadapter spinadapters = new spinadapter(this,android.R.layout.simple_spinner_item,itemCards);
        card_id_spin.setAdapter(spinadapters);
        card_id_spin.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                ItemSpinner itemSpinner = itemCards.get(position);

                itemAcc.cardid = Integer.parseInt(String.valueOf(itemSpinner.id));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        cursor.close();
        spinadapters.notifyDataSetChanged();
    }

    private List<ItemSpinner> makeadapter(Cursor cursor){
        List<ItemSpinner> items = new ArrayList<>();
        if (cursor.getCount() != 0) {
            while (cursor.moveToNext()) {
                ItemSpinner item =  new ItemSpinner();
                item.id = cursor.getLong(0);
                item.name = cursor.getString(2);
                items.add(item);
            }
        }
        return items;

    }

    private void set_with_acc_spinner_onUpdate(){
        int size = with_acc_spin.getAdapter().getCount();
        int i;
        ItemSpinner itemRecipient;
        for(i = 0 ; i<size ; i++){
            itemRecipient = (ItemSpinner) with_acc_spin.getItemAtPosition(i);
            if( itemRecipient.id == itemAcc.withdrawalaccount) {
                //i -= 1;
                break;
            }
        }
        if(i >= size) {
            return;
        }

        with_acc_spin.setSelection(i,true);
    }

    private void set_card_id_spinner_onUpdate(){
        int size = card_id_spin.getAdapter().getCount();
        int i;
        ItemSpinner itemRecipient;
        for(i = 0 ; i<size ; i++){
            itemRecipient = (ItemSpinner) card_id_spin.getItemAtPosition(i);
            if( itemRecipient.id == itemAcc.cardid) {
                i -= 1;
                break;
            }
        }
        if(i >= size) {
            return;
        }

        card_id_spin.setSelection(i,true);
    }

    private void getViews() {
        name_txt = findViewById(R.id.add_acc_name_txt);
        nick_txt = findViewById(R.id.add_acc_nick_txt);
        bal_txt = findViewById(R.id.add_acc_bal);
        bal_text = findViewById(R.id.add_acc_bal_txt);
        with_acc = findViewById(R.id.add_acc_with_acc);
        with_acc_spin = findViewById(R.id.add_acc_with_acc_spinner);
        card_id = findViewById(R.id.add_acc_card_id);
        card_id_spin = findViewById(R.id.add_acc_with_card_spinner);
        with_date = findViewById(R.id.add_card_with_day);
        with_date_txt = findViewById(R.id.add_card_with_day_txt);
    }

    class ItemSpinner {
        long id;
        String name;
    }

    private class spinadapter extends ArrayAdapter {
        LayoutInflater mInflater;
        private final int mResource;
        private int mDropDownResource;
        private final Context mContext;
        private LayoutInflater mDropDownInflater;

        public spinadapter(@NonNull Context context, int resource, @NonNull List objects) {
            super(context, resource, objects);

            mContext = context;
            mInflater = LayoutInflater.from(context);
            mResource = mDropDownResource = resource;

//            mFieldId = textViewResourceId;
            mInflater = LayoutInflater.from(context);

        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {      //스피너 본체에 나오는
            return createViewFromResource(mInflater, position, convertView, parent, mResource);
        }

        @Override
        public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            final LayoutInflater inflater = mDropDownInflater == null ? mInflater : mDropDownInflater;
            return createViewFromResource(inflater, position, convertView, parent, mDropDownResource);
        }

        @Override
        public void setDropDownViewTheme(@Nullable Resources.Theme theme) {
            if (theme == null) {
                mDropDownInflater = null;
            } else if (theme == mInflater.getContext().getTheme()) {
                mDropDownInflater = mInflater;
            } else {
                final Context context = new ContextThemeWrapper(mContext, theme);
                mDropDownInflater = LayoutInflater.from(context);
            }
        }

        private @NonNull
        View createViewFromResource(@NonNull LayoutInflater inflater, int position,
                                    @Nullable View convertView, @NonNull ViewGroup parent, int resource) {
            final View view;
            final TextView text;
            int mFieldId = 0;//textview id
            if (convertView == null) {
                view = inflater.inflate(resource, parent, false);
            } else {
                view = convertView;
            }

            try {
                if (mFieldId == 0) {
                    //  If no custom field is assigned, assume the whole resource is a TextView
                    text = (TextView) view;
                } else {
                    //  Otherwise, find the TextView field within the layout
                    text = view.findViewById(mFieldId);

                    if (text == null) {
                        throw new RuntimeException("Failed to find view with ID "
                                + mContext.getResources().getResourceName(mFieldId)
                                + " in item layout");
                    }
                }
            } catch (ClassCastException e) {
                Log.e("ArrayAdapter", "You must supply a resource ID for a TextView");
                throw new IllegalStateException(
                        "ArrayAdapter requires the resource ID to be a TextView", e);
            }


            final ItemSpinner item = (ItemSpinner) getItem(position);
            text.setText(item.name);
            text.setTextSize(TypedValue.COMPLEX_UNIT_SP,20);
            text.setPadding(4, 4, 4, 4);
            text.setTag(item);

            return view;
        }

    }

}
