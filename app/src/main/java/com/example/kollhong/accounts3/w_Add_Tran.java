package com.example.kollhong.accounts3;

import android.content.ContentValues;
import android.content.Context;
import android.os.Bundle;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetDialog;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;

import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
//import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import java.text.DateFormat;
import java.text.ParseException;
import java.util.*;

import static com.example.kollhong.accounts3.zDBScheme.*;
import static com.example.kollhong.accounts3.zDBScheme.ASSET_TABLE.*;
import static com.example.kollhong.accounts3.zDBScheme.ASSET_TABLE.ASSET_TYPE;
import static com.example.kollhong.accounts3.zDBScheme.CARD_INFO_TABLE.*;
import static java.text.DateFormat.getDateInstance;
import static java.text.DateFormat.getDateTimeInstance;
import static java.text.DateFormat.getTimeInstance;

public class w_Add_Tran extends AppCompatActivity {


    TextView cat_view;
    TextView acc_view;
    EditText amount_view;
    EditText recip_view;
    EditText note_view;
    long tab_num;

    VisibleControlView VCV;
    zDBMan mDB;
    zDBScheme.ItemTransactions itemTransactions;

    Calendar calendar = Calendar.getInstance();
    DateFormat df = getDateTimeInstance(DateFormat.MEDIUM, DateFormat.SHORT);

    DialogFragment dialogFragment;
    w_Add_Tran_Datepicker transactionsDatePicker;

    BottomSheetDialog mBottomSheetDialog;

    //Cursor cursor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.w_add_transactions);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle(R.string.title_activity_add_transactions);



        cat_view = findViewById(R.id.cat_view);
        acc_view = findViewById(R.id.add_acc_bal_txt);
        amount_view = findViewById(R.id.add_tran_amount);
        recip_view = findViewById(R.id.add_tran_recipient);
        recip_view.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(!hasFocus){
                    EditText view = (EditText)v;
                    itemTransactions.recipname = view.getText().toString();
                    ContentValues learnValues = mDB.getLearnData(itemTransactions.recipname);
                    if(learnValues != null){
                        //learnValues();
                        itemTransactions.category_id = learnValues.getAsLong(zDBScheme.LEARN_TABLE.CATEGORY_ID);
                        itemTransactions.asset_id = learnValues.getAsLong(zDBScheme.LEARN_TABLE.ASSET_ID);
                        itemTransactions.franchisee_id = learnValues.getAsLong(zDBScheme.LEARN_TABLE.FRANCHISEE_ID);

                        setCategoryOnTab(itemTransactions.category_id);
                        setAccountOnUpdate();

                        if(itemTransactions.asset_type == ASSET_TYPE_DEBIT_CARD || itemTransactions.asset_type == ASSET_TYPE_CREDIT_CARD)
                            setRecispinnerOnUpdate();
                    }
                }
            }
        });
        note_view = findViewById(R.id.acc_acc_note_txt);

        VCV = new VisibleControlView();

        mDB = new zDBMan(this, true);
        itemTransactions = new zDBScheme.ItemTransactions();

        itemTransactions.timeinmillis = calendar.getTimeInMillis();

        int tab_num = 1;
        setTab(tab_num);
        setTimeText();

        processIntent(getIntent()) ;

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
            onSAVE();
            return true;
        }
        else if(id == android.R.id.home)
        {
            this.finish();
            return true;
        }
        else if(id == R.id.itemDelete){
            if (itemTransactions.isUpdate){
                mDB.deleteTrans(itemTransactions.transaction_id);
            }
            else{
                this.finish();
            }
        }
        return false;
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

        processIntent(intent);

    }

    private void processIntent(Intent intent) {

        if (intent.hasExtra("Notification")){
            if(intent.getLongExtra("Notification", 0L) != 0l) {   //마지막 레코드 가져와서 띄우기
                ContentValues transactionsValues = mDB.getTransbyID(intent.getLongExtra("Notification", 0L));
                Log.e("Recieved SMS Intent", String.valueOf(intent.getLongExtra("Notification", 0l)));
                UpdateDisplay(transactionsValues);

                //Bundle bundle = intent.getBundleExtra("bundle");
                //bundle.getLong("Notification");
                //Log.e("Recieved SMS Intent, id : ", String.valueOf(bundle.getLong("Notification")));
            }
        }
        else if (intent.hasExtra("UpdateTrans")){
            if(intent.getLongExtra("UpdateTrans", 0l) != 0) {
                ContentValues transactionsValues = mDB.getTransbyID(intent.getLongExtra("UpdateTrans", 0));
                UpdateDisplay(transactionsValues);        //trans 레코드 가져와서 띄우기
            } else {
                itemTransactions.timeinmillis = calendar.getTimeInMillis();
            }
        }
    }

    private void UpdateDisplay(ContentValues values) {
        if (values != null) {

            itemTransactions.isUpdate = true;
            itemTransactions.transaction_id = values.getAsLong(zDBScheme.TABLE_ID);
            itemTransactions.timeinmillis = values.getAsLong(zDBScheme.TRANSACTIONS_TABLE.TRANSACTON_TIME);
            setTimeText();

            itemTransactions.category_id = values.getAsLong(zDBScheme.TRANSACTIONS_TABLE.CATEGORY_ID);
            setCategoryOnUpdate();

            itemTransactions.amount = values.getAsFloat(zDBScheme.TRANSACTIONS_TABLE.AMOUNT);
            amount_view.setText(String.valueOf(itemTransactions.amount));

            itemTransactions.asset_id = values.getAsLong(zDBScheme.TRANSACTIONS_TABLE.ASSET_ID);
            setAccountOnUpdate();

            itemTransactions.recipname = values.getAsString(zDBScheme.TRANSACTIONS_TABLE.RECIPIENT);
            recip_view.setText(itemTransactions.recipname);

            itemTransactions.note = values.getAsString(zDBScheme.TRANSACTIONS_TABLE.NOTE);
            note_view.setText(itemTransactions.note);

            itemTransactions.franchisee_id = values.getAsLong(zDBScheme.TRANSACTIONS_TABLE.FRANCHISEE_ID);

            if (itemTransactions.asset_type == ASSET_TYPE_DEBIT_CARD || itemTransactions.asset_type == ASSET_TYPE_CREDIT_CARD)
                setRecispinnerOnUpdate();

            itemTransactions.budget_exception = values.getAsLong(zDBScheme.TRANSACTIONS_TABLE.FRANCHISEE_ID);
            itemTransactions.rew_amount_calculated = values.getAsFloat(zDBScheme.TRANSACTIONS_TABLE.REWARD_CACULATED);
            itemTransactions.reward_exception = values.getAsLong(zDBScheme.TRANSACTIONS_TABLE.REWARD_EXCEPTION);
            itemTransactions.rew_type = values.getAsLong(zDBScheme.TRANSACTIONS_TABLE.REWARD_TYPE);
        }
    }

    public void onSetDate (View v) throws ParseException {
        itemTransactions.timeinmillis = transactionsDatePicker.getTimeinmillis();      //timeinmillis는 전역변수이므로 변경 필요
        setTimeText();
    }

    private void onSAVE() {

        EditText amount = findViewById(R.id.add_tran_amount);

        if (String.valueOf(amount.getText()).equals("")) {      //지정 안됨
            Snackbar.make(findViewById(R.id.add_Layout), R.string.snackbar_trans_amount_missing, Snackbar.LENGTH_LONG).show();
            return;
        } else {
            itemTransactions.amount = Float.parseFloat(String.valueOf(amount.getText()));
        }


        if (itemTransactions.asset_id == 0) {
            Snackbar.make(findViewById(R.id.add_Layout), R.string.snackbar_trans_acc_missing, Snackbar.LENGTH_LONG).show();
            return;
        }

        if (itemTransactions.asset_type == ASSET_TYPE_DEBIT_CARD || itemTransactions.asset_type == ASSET_TYPE_CREDIT_CARD) {
            if (itemTransactions.cardid == 0) {
                Snackbar.make(findViewById(R.id.add_Layout), R.string.snackbar_trans_acc_missing, Snackbar.LENGTH_LONG).show();
                return;
            }
        }
        if (itemTransactions.category_id == 0) {
            Snackbar.make(findViewById(R.id.add_Layout), R.string.snackbar_trans_cat_missing, Snackbar.LENGTH_LONG).show();
            return;
        }
        if (itemTransactions.timeinmillis == 0L) {
            Snackbar.make(findViewById(R.id.add_Layout), R.string.snackbar_trans_time_missing, Snackbar.LENGTH_LONG).show();
            return;
        }
        CheckBox checkedTextView = findViewById(R.id.Learn_Checker);
        itemTransactions.learn = checkedTextView.isChecked();

        itemTransactions.recipname = recip_view.getText().toString();
        itemTransactions.note = note_view.getText().toString();
        mDB.addTransactiononSave(itemTransactions);

        finish();
    }

    public void onDatePick(View v) {

        transactionsDatePicker = new w_Add_Tran_Datepicker();
        dialogFragment = transactionsDatePicker;

        Bundle args = new Bundle();

        args.putLong("timeinmillis", itemTransactions.timeinmillis);
        //TODO 년월일시분 전달

        dialogFragment.setArguments(args);
        dialogFragment.show(getSupportFragmentManager().beginTransaction(), "tag");

    }

    public void onCategoryClick(View v) {
        List<ContentValues> categoryList = mDB.getChildCategoryList(tab_num);

        ListIterator<ContentValues> contentValuesListIterator = categoryList.listIterator();
        List<zRecyclerAdapt_Gen.recyclerItem> catItemList = new ArrayList<>();



        while (contentValuesListIterator.hasNext()) {
            ContentValues contentitem = contentValuesListIterator.next();

            zRecyclerAdapt_Gen.categoryItem recycleritem = new zRecyclerAdapt_Gen.categoryItem();

            recycleritem.id = contentitem.getAsLong(TABLE_ID);
            recycleritem.name = contentitem.getAsString(CATEGORY_TABLE.NAME);
            catItemList.add(recycleritem);
        }

        make_bottomSheet(catItemList,new categoryClickListener());
    }

    public void onAccountClick(View v) {
        List<ContentValues> assetList = mDB.getAssetList();//계좌 목록 가져오기

        ListIterator<ContentValues> contentValuesListIterator = assetList.listIterator();
        List<zRecyclerAdapt_Gen.recyclerItem> assetItemList = new ArrayList<>();

        while (contentValuesListIterator.hasNext()) {
            ContentValues contentitem = contentValuesListIterator.next();
            zRecyclerAdapt_Gen.assetItem recycleritem = new zRecyclerAdapt_Gen.assetItem();

            recycleritem.id = contentitem.getAsLong(TABLE_ID);
            recycleritem.asset_type = contentitem.getAsLong(ASSET_TYPE);
            recycleritem.name = contentitem.getAsString(NAME);
            recycleritem.balance = contentitem.getAsFloat(BALANCE);
            recycleritem.withdrawalaccount = contentitem.getAsLong(WITHDRAWALACCOUNT);
            recycleritem.withdrawalday = contentitem.getAsLong(WITHDRAWALDAY);
            recycleritem.cardid = contentitem.getAsLong(CARD_ID);

            assetItemList.add(recycleritem);

        }
        make_bottomSheet(assetItemList, new assetClickListener());
    }


    private void make_bottomSheet( List<zRecyclerAdapt_Gen.recyclerItem> contentValuesList,  View.OnClickListener clickListener){
        mBottomSheetDialog = new BottomSheetDialog(this);
        View sheetView = getLayoutInflater().inflate(R.layout.w_add_transactions_category_picker, null);

        RecyclerView recyclerView = sheetView.findViewById(R.id.add_tran_recategory_recycler);


        zRecyclerAdapt_Gen.CatAssetAdapter bottomsheetAdapter
                = new zRecyclerAdapt_Gen.CatAssetAdapter(this,contentValuesList, clickListener);

        recyclerView.setAdapter(bottomsheetAdapter);


        recyclerView.setLayoutManager(new GridLayoutManager(this, 2));

        mBottomSheetDialog.setContentView(sheetView);
        mBottomSheetDialog.show();

    }

    private void Check_Card_Info() {
        ContentValues cardinfo = mDB.getCardinfo(itemTransactions.cardid);
        //ListIterator cardinfoIterator = cardinfo.listIterator();
        List<ItemRecipient> recipList = new ArrayList<>();

        if (cardinfo != null) {

            recipList.addAll(getCardFranchiseeList(cardinfo));

            VCV.rew_spin.setAdapter(new spinadapter(this, android.R.layout.simple_spinner_item, recipList));
            VCV.rew_spin.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                    setRecipient((ItemRecipient) view.getTag());
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });
            setTransVisible();
        }
    }

    private void Credit_Card() {
        Check_Card_Info();
    }

    private List<ItemRecipient> getCardFranchiseeList(ContentValues values){
        List<ItemRecipient> recipList = new ArrayList<>();

        ItemRecipient itemRecipient = new ItemRecipient();
        itemRecipient.recipid = 0;
        itemRecipient.recipient = "해당 없음";
        recipList.add(itemRecipient);

        String[] perfsplit = values.getAsString(REWARD_EXCEPTIONS).split(":");
        int perflen = perfsplit.length;

        for (int l = 0; l < perflen; l++) {     //10101, 20100
            itemRecipient = new ItemRecipient();

            itemRecipient.recipid = Long.parseLong(perfsplit[l]);
            itemRecipient.recipient = mDB.getFranchiseeName(itemRecipient.recipid);
            itemRecipient.conditionexception = 1;
            recipList.add(itemRecipient);
        }


        String[] sections = values.getAsString(REWARD_SECTIONS).split(":");
        int size = sections.length;
        for (int i = 0; i < size; i++) {   //첫글자 따기 둘째 글자 따기 //b0, c5
            int conditiontype = 0;
            String condtype = sections[i].substring(0, 1);
            if (condtype.equals("b")) {
                conditiontype = 0;
            } else if (condtype.equals("c")) {
                conditiontype = 1;
            } else {
                Log.w("카드 정보 오류", "실적 조건 유형(전월 현월 파싱 오류.", null);
            }
            int conperf = Integer.parseInt(sections[i].substring(1));


            String[] recip = values.getAsString(REWARD_FRANCHISEE_STRING+i).split(":");      //3,5번째.
            String[] reward = values.getAsString(REWARD_AMOUNT_STRING+i).split(":");

            int reamolen = recip.length;


            for (int k = 0; k < reamolen; k++) {

                long recipid;
                String rew = reward[k].substring(0, 1);
                int rewtype = 0;
                if (rew.equals("p")) {
                    rewtype = 0;
                } else if (rew.equals("d")) {
                    rewtype = 1;
                } else {
                    Log.w("카드 정보 오류", "리워드 타입 파싱 오류. : " + rew, null);
                }

                float rewamount = Float.parseFloat(reward[k].substring(1));

                // ------------중복 확인-------------------
                boolean duplicate = false;
                recipid = Long.parseLong(recip[k]);
                for (int p = 0; p < recipList.size(); p++) {
                    if (recipid == recipList.get(p).recipid) {       //중복
                        recipList.get(p).conditiontype = conditiontype;
                        recipList.get(p).conditionAmount = conperf;
                        recipList.get(p).rewtype = rewtype;
                        recipList.get(p).rewamount = rewamount;
                        duplicate = true;
                        break;
                    }

                }
                if (!duplicate) {
                    itemRecipient = new ItemRecipient();
                    itemRecipient.recipid = recipid;
                    itemRecipient.recipient = mDB.getFranchiseeName(recipid);
                    itemRecipient.conditiontype = conditiontype;
                    itemRecipient.conditionAmount = conperf;
                    itemRecipient.rewamount = rewamount;
                    itemRecipient.rewtype = rewtype;
                    recipList.add(itemRecipient);
                    Log.w("카드 정보", "리워드 : " + itemRecipient.rewamount +"", null);
                }
            }
        }



        return recipList;
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        data.getLongExtra("timeinmillis", 0);
        if (BuildConfig.isTEST)
            Log.w("timeinmillis returned", itemTransactions.timeinmillis + "");
    }

    private void setTimeText() {

        TextView date_text = findViewById(R.id.add_tran_time);

        calendar.setTimeInMillis(itemTransactions.timeinmillis);     //나중에 사용하기 위한 변수 저장.

        df.setCalendar(calendar);

        Date date = new Date();
        date.setTime(itemTransactions.timeinmillis);

        String dff = df.format(date);


        date_text.setText(dff);
        if (BuildConfig.isTEST)
            Log.w(" onSET  ", dff);
    }

    private void setTransVisible() {
        //VCV.perf_view.setVisibility(View.VISIBLE);
        VCV.rew_view.setVisibility(View.VISIBLE);
        VCV.rew_spin.setVisibility(View.VISIBLE);
        //VCV.perf_spin.setVisibility(View.VISIBLE);
    }

    private void setTransInvisible() {
        VCV.rew_view.setVisibility(View.GONE);
        //VCV.perf_view.setVisibility(View.INVISIBLE);
        VCV.rew_spin.setVisibility(View.GONE);
        //VCV.perf_spin.setVisibility(View.INVISIBLE);
    }

    private void setCategory(String name, long id) {

        itemTransactions.category_name = name;
        cat_view.setText(name);
        itemTransactions.category_id = id;
    }

    private void setCategoryOnTab(long cat_id) {
        setCategory( mDB.getCategoryName(cat_id + 1), cat_id +1);
        tab_num = cat_id+1l;
        if (BuildConfig.isTEST) {
            Log.w("Cat_View Update", "" + cat_id);
        }
    }

    private void setCategoryOnUpdate(){

        int temp_level = mDB.getCatLevel(itemTransactions.category_id);
        if(temp_level == 0 || temp_level ==1 || temp_level == 2){        setTab(0);}
        else if(temp_level == 3 || temp_level ==4 || temp_level == 5){        setTab(1);}
        else if(temp_level == 6 || temp_level ==7 || temp_level == 8){        setTab(2);}

        setCategory(  mDB.getCategoryName( itemTransactions.category_id), itemTransactions.category_id);
    }

    public void setAccount(View v){
        zRecyclerAdapt_Gen.assetItem item = (zRecyclerAdapt_Gen.assetItem) v.getTag();

        itemTransactions.cardid = item.cardid;
        itemTransactions.asset_id = item.id;
        itemTransactions.asset_name = item.name;
        itemTransactions.asset_type = item.asset_type;
        itemTransactions.withdrawlday = item.withdrawalday;
        itemTransactions.withdrawlaccount = item.withdrawalaccount;
        itemTransactions.balance = item.balance;

        acc_view.setText(itemTransactions.asset_name);

        if (itemTransactions.asset_type == 2) {
            Check_Card_Info();
        } else if (itemTransactions.asset_type == 3) {
            Credit_Card();
        } else {
            setTransInvisible();
        }
    }   //TODO 데이터 무결성 확인

    private void setAccountOnUpdate(){
        ContentValues cursor = mDB.getAssetInfo(itemTransactions.asset_id);

        itemTransactions.asset_id = cursor.getAsLong(TABLE_ID);
        itemTransactions.asset_type = cursor.getAsLong(ASSET_TYPE);
        itemTransactions.asset_name = cursor.getAsString(NAME);

        itemTransactions.balance = cursor.getAsFloat(BALANCE);
        itemTransactions.withdrawlaccount = cursor.getAsLong(WITHDRAWALACCOUNT);
        itemTransactions.withdrawlday = cursor.getAsLong(WITHDRAWALDAY);
        itemTransactions.cardid = cursor.getAsLong(CARD_ID);

        acc_view.setText(itemTransactions.asset_name);

        if (itemTransactions.asset_type == 2) {
            Check_Card_Info();
        } else if (itemTransactions.asset_type == 3) {
            Credit_Card();
        } else {
            setTransInvisible();
        }

    }

    private void setRecipient(ItemRecipient itemRecipient){

        itemTransactions.franchisee_id = itemRecipient.recipid;
        itemTransactions.recipname = itemRecipient.recipient;
        itemTransactions.reward_exception = itemRecipient.conditionexception;
        itemTransactions.perftype = itemRecipient.conditiontype;
        itemTransactions.conditionAmount = itemRecipient.conditionAmount;
        itemTransactions.rew_type = itemRecipient.rewtype;
        itemTransactions.rew_amount = itemRecipient.rewamount;
        itemTransactions.rew_amount_calculated = (float) (itemTransactions.rew_amount * 0.01 * itemTransactions.amount);
        Log.e("rew inf","금액 " + itemTransactions.amount +"rew_amount"+ itemTransactions.rew_amount+ "calculated" + itemTransactions.rew_amount_calculated);
    }

    private void setRecispinnerOnUpdate(){
        int size = VCV.rew_spin.getAdapter().getCount();
        int i;
        ItemRecipient itemRecipient;
        for(i = 0 ; i<size ; i++){
            itemRecipient = (ItemRecipient) VCV.rew_spin.getItemAtPosition(i);
            if( itemRecipient.recipid == itemTransactions.franchisee_id) {
                //i -= 1;
                break;
            }
        }
        if(i >= size) {
            return;
        }

        VCV.rew_spin.setSelection(i);
    }

    private void setTab(int tab_num){
        TabLayout tabLayout = findViewById(R.id.add_tran_tablayout);
        TabLayout.Tab tab = tabLayout.getTabAt(tab_num);

        tabLayout.clearOnTabSelectedListeners();

        tabLayout.addOnTabSelectedListener(new tabListener());
        tab.select();
    }

    class VisibleControlView {
        TextView rew_view;
        Spinner rew_spin;

        VisibleControlView() {
            rew_view = findViewById(R.id.add_acc_bal);
            rew_spin = findViewById(R.id.add_acc_with_acc_spin);
        }
    }



    private class tabListener implements TabLayout.OnTabSelectedListener {
        @Override
        public void onTabSelected(TabLayout.Tab tab) {
            setCategoryOnTab(tab.getPosition());
        }

        @Override
        public void onTabUnselected(TabLayout.Tab tab) {

        }

        @Override
        public void onTabReselected(TabLayout.Tab tab) {

        }
    }


    class categoryClickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {       //자녀 카테고리가 있으면 다시 다이얼로그 띄우고 자녀가 없으면 완료.
            //onItemClick();
            mBottomSheetDialog.dismiss();
            //zDBMan mDB = new zDBMan(getApplicationContext(), false);
            TextView view = (TextView) v;

            List<ContentValues> childCategoryList = mDB.getChildCategoryList(String.valueOf(view.getText()));

            if (childCategoryList.isEmpty()) {        //자녀 카테고리 표시
                TextView vt = (TextView) v;
                setCategory(vt.getText().toString(), Integer.parseInt(v.getTag().toString()));
            } else {

                ListIterator<ContentValues> contentValuesListIterator = childCategoryList.listIterator();
                List<zRecyclerAdapt_Gen.recyclerItem> catItemList = new ArrayList<>();
                while (contentValuesListIterator.hasNext()) {
                    ContentValues contentitem = contentValuesListIterator.next();

                    zRecyclerAdapt_Gen.categoryItem recycleritem = new zRecyclerAdapt_Gen.categoryItem();

                    recycleritem.id = contentitem.getAsLong(TABLE_ID);
                    recycleritem.name = contentitem.getAsString(CATEGORY_TABLE.NAME);
                    catItemList.add(recycleritem);
                }
                make_bottomSheet(catItemList, this);
            }
        }
    }
    class assetClickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {

            mBottomSheetDialog.dismiss();
            setAccount(v);
        }
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
            //text.setTextSize(24);


            final ItemRecipient item = (ItemRecipient) getItem(position);
            text.setText(item.recipient);
            text.setTextSize(TypedValue.COMPLEX_UNIT_SP,20);
            text.setPadding(4, 4, 4, 4);
            text.setTag(item);

            return view;
        }
    
    }

   /* private  class Account_adapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
        Account_adapter() {

        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = getLayoutInflater().inflate(R.layout.w_add_transactions_category_picker_holder, parent, false);
            return new categoryNameHolder(view);
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

            final ItemAcc itemacc = itemAccs.get(position);

            categoryNameHolder newholder = (categoryNameHolder) holder;
            newholder.textView.setText(itemacc.name);
            View v = newholder.textView;
            v.setTag(itemacc);

            newholder.textView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    mBottomSheetDialog.dismiss();
                    setAccount(v);
                }

            });
        }

        @Override
        public int getItemCount() {
            return itemAccs.size();
        }

    }


    private  class Category_adapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
        List<ItemCat> items;

        Category_adapter(List<ItemCat> item2) {
            items = item2;
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = getLayoutInflater().inflate(R.layout.w_add_transactions_category_picker_holder, parent, false);


            return new categoryNameHolder(view);
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            final ItemCat itemcat = items.get(position);

            categoryNameHolder newholder = (categoryNameHolder) holder;
            newholder.textView.setText(itemcat.name);
            View v = newholder.textView;
            v.setTag(itemcat.id);
            newholder.textView.setOnClickListener(
        }

        @Override
        public int getItemCount() {
            return items.size();
        }

        public class categoryNameHolder extends RecyclerView.ViewHolder {
            TextView textView;

            public categoryNameHolder(View v) {
                super(v);
                textView = v.findViewById(R.id.add_tran_category_name);

                Point point = new Point();
                Display display = getWindowManager().getDefaultDisplay();
                int rotation = display.getRotation();
                display.getSize(point );


                ViewGroup.LayoutParams params = v.getLayoutParams();


                if (rotation == Surface.ROTATION_0 || rotation == Surface.ROTATION_180)
                    params.width = point.x / 2;

                else params.width = point.y / 2;

                v.setLayoutParams(params);

                //Log.e("디스플레이 사이즈", density+":" + dpHeight+":"  + dpWidth+":"  + rotation +"");

            }
        }

    }
*/
}