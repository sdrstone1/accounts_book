package com.example.kollhong.accounts3;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
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
import android.util.Log;
import android.util.TypedValue;
import android.view.*;
import android.widget.*;

import java.text.DateFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import static com.example.kollhong.accounts3.DBItem.*;
import static com.example.kollhong.accounts3.DB_Scheme.ASSET_TYPE_CREDIT_CARD;
import static com.example.kollhong.accounts3.DB_Scheme.ASSET_TYPE_DEBIT_CARD;
import static java.text.DateFormat.getDateTimeInstance;

//import android.support.v7.widget.Toolbar;

public class TransactionAdd_Activity extends AppCompatActivity {


    TextView cat_view;
    TextView acc_view;
    EditText amount_view;
    EditText recip_view;
    EditText note_view;
    int tab_num;

    VisibleControlView VCV;
    DB_Controll mDB;
    ItemTransactions itemTransactions;

    Calendar calendar = Calendar.getInstance();
    DateFormat df = getDateTimeInstance(DateFormat.MEDIUM, DateFormat.SHORT);

    DialogFragment dialogFragment;
    TransactionAdd_Activity_DatePicker transactionsDatePicker;

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
                    itemTransactions.recipientName = view.getText().toString();
                    LearnItem learnValues = mDB.getLearnData(itemTransactions.recipientName);
                    if(learnValues != null){
                        //learnValues();
                        itemTransactions.categoryId = learnValues.categoryId;
                        itemTransactions.assetId = learnValues.assetId;
                        itemTransactions.franchiseeId = learnValues.franchiseeId;

                        setCategoryOnTab(itemTransactions.categoryId);
                        setAccountOnUpdate();

                        if(itemTransactions.assetType == ASSET_TYPE_DEBIT_CARD || itemTransactions.assetType == ASSET_TYPE_CREDIT_CARD)
                            setRecispinnerOnUpdate();
                    }
                }
            }
        });
        note_view = findViewById(R.id.acc_acc_note_txt);

        VCV = new VisibleControlView();

        mDB = new DB_Controll(this, true);
        itemTransactions = new ItemTransactions();

        itemTransactions.transactionTime = calendar.getTimeInMillis();

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
                mDB.deleteTrans(itemTransactions.transactionId);
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
            if(intent.getLongExtra("Notification", 0L) != 0) {   //마지막 레코드 가져와서 띄우기
                TransactionsItem transactionsValues = mDB.getTransbyID(intent.getLongExtra("Notification", 0L));
                Log.e("Recieved SMS Intent", String.valueOf(intent.getLongExtra("Notification", 0L)));
                UpdateDisplay(transactionsValues);

                //Bundle bundle = intent.getBundleExtra("bundle");
                //bundle.getLong("Notification");
                //Log.e("Recieved SMS Intent, id : ", String.valueOf(bundle.getLong("Notification")));
            }
        }
        else if (intent.hasExtra("UpdateTrans")){
            if(intent.getLongExtra("UpdateTrans", 0L) != 0) {
                TransactionsItem transactionsValues = mDB.getTransbyID(intent.getLongExtra("UpdateTrans", 0));
                UpdateDisplay(transactionsValues);        //trans 레코드 가져와서 띄우기
            } else {
                itemTransactions.transactionTime = calendar.getTimeInMillis();
            }
        }
    }

    private void UpdateDisplay(TransactionsItem values) {
        if (values != null) {

            itemTransactions.isUpdate = true;
            itemTransactions.transactionId = values.tableId;
            itemTransactions.transactionTime = values.transactionTime;
            setTimeText();

            itemTransactions.categoryId = values.categoryId;
            setCategoryOnUpdate();

            itemTransactions.amount = values.amount;
            amount_view.setText(String.valueOf(itemTransactions.amount));

            itemTransactions.assetId = values.assetId;
            setAccountOnUpdate();

            itemTransactions.recipientName = values.recipientName;
            recip_view.setText(itemTransactions.recipientName);

            itemTransactions.notes = values.notes;
            note_view.setText(itemTransactions.notes);

            itemTransactions.franchiseeId = values.franchiseeId;

            if (itemTransactions.assetType == ASSET_TYPE_DEBIT_CARD || itemTransactions.assetType == ASSET_TYPE_CREDIT_CARD)
                setRecispinnerOnUpdate();

            itemTransactions.budgetException = values.budgetException;
            itemTransactions.rewardAmountCalculated = values.rewardCalculated;
            itemTransactions.rewardException = values.rewardException;
            itemTransactions.rewardType = values.rewardType;
        }
    }

    public void onSetDate (View v) throws ParseException {
        itemTransactions.transactionTime = transactionsDatePicker.getTimeinmillis();      //timeinmillis는 전역변수이므로 변경 필요
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


        if (itemTransactions.assetId == 0) {
            Snackbar.make(findViewById(R.id.add_Layout), R.string.snackbar_trans_acc_missing, Snackbar.LENGTH_LONG).show();
            return;
        }

        if (itemTransactions.assetType == ASSET_TYPE_DEBIT_CARD || itemTransactions.assetType == ASSET_TYPE_CREDIT_CARD) {
            if (itemTransactions.cardId == 0) {
                Snackbar.make(findViewById(R.id.add_Layout), R.string.snackbar_trans_acc_missing, Snackbar.LENGTH_LONG).show();
                return;
            }
        }
        if (itemTransactions.categoryId == 0) {
            Snackbar.make(findViewById(R.id.add_Layout), R.string.snackbar_trans_cat_missing, Snackbar.LENGTH_LONG).show();
            return;
        }
        if (itemTransactions.transactionTime == 0L) {
            Snackbar.make(findViewById(R.id.add_Layout), R.string.snackbar_trans_time_missing, Snackbar.LENGTH_LONG).show();
            return;
        }
        CheckBox checkedTextView = findViewById(R.id.Learn_Checker);
        itemTransactions.learn = checkedTextView.isChecked();

        itemTransactions.recipientName = recip_view.getText().toString();
        itemTransactions.notes = note_view.getText().toString();
        mDB.addTransactiononSave(itemTransactions);

        finish();
    }

    public void onDatePick(View v) {

        transactionsDatePicker = new TransactionAdd_Activity_DatePicker();
        dialogFragment = transactionsDatePicker;

        Bundle args = new Bundle();

        args.putLong("timeinmillis", itemTransactions.transactionTime);
        //TODO 년월일시분 전달

        dialogFragment.setArguments(args);
        dialogFragment.show(getSupportFragmentManager().beginTransaction(), "tag");

    }

    public void onCategoryClick(View v) {
        List<CategoryItem> categoryList = mDB.getChildCategoryList(tab_num);

        //ListIterator<CategoryItem> ListIterator = categoryList.listIterator();
        List<RecyclerItem> catItemList = new ArrayList<>();



        for (CategoryItem contentitem : categoryList) {
           //  = ListIterator.next();

            RecyclerItem.TransactionAddCategoryItem recycleritem = new RecyclerItem.TransactionAddCategoryItem();

            recycleritem.nameOnlyItem.tableId = contentitem.tableId;
            recycleritem.nameOnlyItem.name = contentitem.name;
            catItemList.add(recycleritem);
        }

        make_bottomSheet(catItemList,new categoryClickListener());
    }

    public void onAccountClick(View v) {
        List<AssetItem> assetList = mDB.getAssetList();//계좌 목록 가져오기
        //ListIterator<AssetItem> ListIterator = assetList.listIterator();

        List<RecyclerItem> assetItemList = new ArrayList<>();

        for (AssetItem contentitem : assetList) {
         //    = ListIterator.next();
            RecyclerItem.TransactionAddAssetItem recycleritem = new RecyclerItem.TransactionAddAssetItem();

            recycleritem.item = contentitem;

            assetItemList.add(recycleritem);

        }
        make_bottomSheet(assetItemList, new assetClickListener());
    }


    private void make_bottomSheet(List<RecyclerItem> contentValuesList, View.OnClickListener clickListener){
        mBottomSheetDialog = new BottomSheetDialog(this);
        View sheetView = getLayoutInflater().inflate(R.layout.w_add_transactions_category_picker, null);

        RecyclerView recyclerView = sheetView.findViewById(R.id.add_tran_recategory_recycler);


        Recycler_Adapter.RecyclerAdapter bottomsheetAdapter
                = new Recycler_Adapter.RecyclerAdapter(this,contentValuesList, clickListener);

        recyclerView.setAdapter(bottomsheetAdapter);


        recyclerView.setLayoutManager(new GridLayoutManager(this, 2));

        mBottomSheetDialog.setContentView(sheetView);
        mBottomSheetDialog.show();

    }

    private void Check_Card_Info() {
        CardInfoItem cardinfo = mDB.getCardinfo(itemTransactions.cardId);
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

    private List<ItemRecipient> getCardFranchiseeList(CardInfoItem cardInfoItem){
        List<ItemRecipient> recipientList = new ArrayList<>();

        ItemRecipient itemRecipient = new ItemRecipient();
        itemRecipient.tableId = 0;
        itemRecipient.recipientName = "해당 없음";
        recipientList.add(itemRecipient);



        recipientList.addAll(cardInfoItem.getRewardExceptionList(mDB));
        int ExceptionLength = recipientList.size();
        recipientList.addAll(cardInfoItem.getRewardRecipientList(mDB));

        // ------------중복 확인-------------------
        for (int i = ExceptionLength; i< recipientList.size(); i++){
            ItemRecipient checkingItem = recipientList.get(i);
            //int checkingId = checkingItem.tableId;
            for (int p = 0; p < i; p++) {
                ItemRecipient checkeeItem = recipientList.get(p);
                if (checkingItem.tableId == checkeeItem.tableId) {       //중복
                    checkeeItem.conditiontype = checkingItem.conditiontype;
                    checkeeItem.conditionAmount = checkingItem.conditionAmount;
                    checkeeItem.rewardType = checkingItem.rewardType;
                    checkeeItem.rewardPercent = checkingItem.rewardPercent;
                    recipientList.remove(i);
                    break;
                }

            }
        }

        return recipientList;
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        data.getLongExtra("timeinmillis", 0);
        if (BuildConfig.isTEST)
            Log.w("timeinmillis returned", itemTransactions.transactionTime + "");
    }

    private void setTimeText() {

        TextView date_text = findViewById(R.id.add_tran_time);

        calendar.setTimeInMillis(itemTransactions.transactionTime);     //나중에 사용하기 위한 변수 저장.

        df.setCalendar(calendar);

        Date date = new Date();
        date.setTime(itemTransactions.transactionTime);

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

    private void setCategory(String name, int id) {

        itemTransactions.categoryName = name;
        cat_view.setText(name);
        itemTransactions.categoryId = id;
    }

    private void setCategoryOnTab(int cat_id) {
        setCategory( mDB.getCategoryName(cat_id + 1), cat_id +1);
        tab_num = cat_id+1;
        if (BuildConfig.isTEST) {
            Log.w("Cat_View Update", "" + cat_id);
        }
    }

    private void setCategoryOnUpdate(){

        int temp_level = mDB.getCatLevel(itemTransactions.categoryId);
        if(temp_level == 0 || temp_level ==1 || temp_level == 2){        setTab(0);}
        else if(temp_level == 3 || temp_level ==4 || temp_level == 5){        setTab(1);}
        else if(temp_level == 6 || temp_level ==7 || temp_level == 8){        setTab(2);}

        setCategory(  mDB.getCategoryName( itemTransactions.categoryId), itemTransactions.categoryId);
    }

    public void setAccount(View v){
        RecyclerItem.TransactionAddAssetItem item = (RecyclerItem.TransactionAddAssetItem) v.getTag();

        itemTransactions.cardId = item.item.cardId;
        itemTransactions.assetId = item.item.tableId;
        itemTransactions.assetName = item.item.name;
        itemTransactions.assetType = item.item.assetType;
        itemTransactions.withdrawlDay = item.item.withdrawalDay;
        itemTransactions.withdrawlAccount = item.item.withdrawalAccount;
        itemTransactions.balance = item.item.balance;

        acc_view.setText(itemTransactions.assetName);

        if (itemTransactions.assetType == 2) {
            Check_Card_Info();
        } else if (itemTransactions.assetType == 3) {
            Credit_Card();
        } else {
            setTransInvisible();
        }
    }   //TODO 데이터 무결성 확인

    private void setAccountOnUpdate(){
        AssetItem assetInfo = mDB.getAssetInfo(itemTransactions.assetId);

        itemTransactions.assetId = assetInfo.tableId;
        itemTransactions.assetType = assetInfo.assetType;
        itemTransactions.assetName = assetInfo.name;

        itemTransactions.balance = assetInfo.balance;
        itemTransactions.withdrawlAccount = assetInfo.withdrawalAccount;
        itemTransactions.withdrawlDay = assetInfo.withdrawalDay;
        itemTransactions.cardId = assetInfo.cardId;

        acc_view.setText(itemTransactions.assetName);

        if (itemTransactions.assetType == 2) {
            Check_Card_Info();
        } else if (itemTransactions.assetType == 3) {
            Credit_Card();
        } else {
            setTransInvisible();
        }

    }

    private void setRecipient(ItemRecipient itemRecipient){

        itemTransactions.franchiseeId = itemRecipient.tableId;
        itemTransactions.recipientName = itemRecipient.recipientName;
        itemTransactions.rewardException = itemRecipient.rewardExceptions;
        itemTransactions.conditionType = itemRecipient.conditiontype;
        itemTransactions.conditionAmount = itemRecipient.conditionAmount;
        itemTransactions.rewardType = itemRecipient.rewardType;
        itemTransactions.rewardAmount = itemRecipient.rewardPercent;
        itemTransactions.rewardAmountCalculated = (float) (itemTransactions.rewardAmount * 0.01 * itemTransactions.amount);
        Log.e("rew inf","금액 " + itemTransactions.amount +"rew_amount"+ itemTransactions.rewardAmount + "calculated" + itemTransactions.rewardAmountCalculated);
    }

    private void setRecispinnerOnUpdate(){
        int size = VCV.rew_spin.getAdapter().getCount();
        int i;
        ItemRecipient itemRecipient;
        for(i = 0 ; i<size ; i++){
            itemRecipient = (ItemRecipient) VCV.rew_spin.getItemAtPosition(i);
            if( itemRecipient.tableId == itemTransactions.franchiseeId) {
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

            List<CategoryItem> childCategoryList = mDB.getChildCategoryList(String.valueOf(view.getText()));

            if (childCategoryList.isEmpty()) {        //자녀 카테고리 표시
                TextView vt = (TextView) v;
                setCategory(vt.getText().toString(), Integer.parseInt(v.getTag().toString()));
            } else {

                //ListIterator<CategoryItem> listIterater = childCategoryList.listIterator();
                List<RecyclerItem> catItemList = new ArrayList<>();
                for (CategoryItem categoryItem : childCategoryList) {
                   // CategoryItem categoryItem = listIterater.next();

                    RecyclerItem.TransactionAddCategoryItem recycleritem = new RecyclerItem.TransactionAddCategoryItem();

                    recycleritem.nameOnlyItem.tableId = categoryItem.tableId;
                    recycleritem.nameOnlyItem.name = categoryItem.name;
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
            text.setText(item.recipientName);
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