package com.example.kollhong.accounts3;

import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Point;
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
import android.text.Layout;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.ContextThemeWrapper;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Surface;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import java.text.DateFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import static java.text.DateFormat.getDateInstance;
import static java.text.DateFormat.getDateTimeInstance;
import static java.text.DateFormat.getTimeInstance;

public class w_Add_Tran extends AppCompatActivity {
    static final int BOTTOMSHEET_ACCOUNT = 1;
    static final int BOTTOMSHEET_CATEGORY = 2;

    TextView cat_view;
    TextView acc_view;
    EditText amount_view;
    EditText recip_view;
    EditText note_view;

    VisibleControlView VCV;
    zDBMan mDB;
    zDBMan.SaveData Datas;

    Calendar calendar = Calendar.getInstance();
    DateFormat df = getDateTimeInstance(DateFormat.MEDIUM, DateFormat.SHORT);


    DialogFragment dialogFragment;
    w_Add_Tran_Datepicker ATCF;

    List<zDBMan.ItemCat> itemCats;
    List<zDBMan.ItemAcc> itemAccs;
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
                    Datas.recipname = view.getText().toString();
                    Cursor cursor = mDB.getLearnData(Datas.recipname);
                    if(cursor.getCount() != 0){
                        cursor.moveToNext();
                        Datas.category_id = cursor.getLong(1);
                        Datas.acc_id = cursor.getLong(2);
                        Datas.recipid = cursor.getLong(3);

                        setCategoryOnTab(Datas.category_id);
                        setAccountOnUpdate();

                        if(Datas.acc_type == 2 || Datas.acc_type == 3)
                            setRecispinnerOnUpdate();
                    }
                }
            }
        });
        note_view = findViewById(R.id.acc_acc_note_txt);

        VCV = new VisibleControlView();

        mDB = new zDBMan(this, true);
        Datas = mDB.data;

        Datas.timeinmillis = calendar.getTimeInMillis();

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
            if (Datas.isUpdate){
                mDB.deleteTrans(Datas.trans_id);
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
                Cursor cursor = mDB.getTransbyID(intent.getLongExtra("Notification", 0l));
                Log.e("Recieved SMS Intent, id : ", String.valueOf(intent.getLongExtra("Notification", 0l)));
                UpdateTrans(cursor);

                //Bundle bundle = intent.getBundleExtra("bundle");
                //bundle.getLong("Notification");
                //Log.e("Recieved SMS Intent, id : ", String.valueOf(bundle.getLong("Notification")));
            }
        }
        else if (intent.hasExtra("UpdateTrans")){
            if(intent.getLongExtra("UpdateTrans", 0l) != 0) {
                Cursor cursor = mDB.getTransbyID(intent.getLongExtra("UpdateTrans", 0));
                UpdateTrans(cursor);        //trans 레코드 가져와서 띄우기
            } else {
                Datas.timeinmillis = calendar.getTimeInMillis();
            }
        }
    }

    private void UpdateTrans(Cursor cursor) {
        if (cursor.getCount() != 0) {
            cursor.moveToNext();
            Datas.isUpdate = true;
            Datas.trans_id = cursor.getLong(0);
            Datas.timeinmillis = cursor.getLong(1);
            setTimeText();

            Datas.category_id = cursor.getLong(2);
            setCategoryOnUpdate();

            Datas.amount = cursor.getFloat(3);
            amount_view.setText(String.valueOf(Datas.amount));

            Datas.acc_id = cursor.getLong(4);
            setAccountOnUpdate();

            Datas.recipname = cursor.getString(5);
            recip_view.setText(Datas.recipname);

            Datas.note = cursor.getString(6);
            note_view.setText(Datas.note);

            Datas.recipid = cursor.getLong(7);

            if(Datas.acc_type == 2 || Datas.acc_type == 3)
                setRecispinnerOnUpdate();

            Datas.budgetexception = cursor.getInt(8);
            Datas.rew_amount_calculated = cursor.getFloat(9);
            Datas.perfexception =cursor.getInt(10);
            Datas.rew_type = cursor.getInt(11);


        }
        cursor.close();
    }

    public void onSetDate (View v) throws ParseException {
        Datas.timeinmillis = ATCF.getTimeinmillis();      //timeinmillis는 전역변수이므로 변경 필요
        setTimeText();
    }

    private void onSAVE() {

        EditText amount = findViewById(R.id.add_tran_amount);

        if (String.valueOf(amount.getText()).equals("")) {      //지정 안됨
            Snackbar.make(findViewById(R.id.add_Layout), R.string.snackbar_trans_amount_missing, Snackbar.LENGTH_LONG).show();
            return;
        } else {
            Datas.amount = Float.parseFloat(String.valueOf(amount.getText()));
        }


        if (Datas.acc_id == 0) {
            Snackbar.make(findViewById(R.id.add_Layout), R.string.snackbar_trans_acc_missing, Snackbar.LENGTH_LONG).show();
            return;
        }

        if (Datas.acc_type == 2 || Datas.acc_type == 3) {
            if (Datas.cardid == 0) {
                Snackbar.make(findViewById(R.id.add_Layout), R.string.snackbar_trans_acc_missing, Snackbar.LENGTH_LONG).show();
                return;
            }
        }
        if (Datas.category_id == 0) {
            Snackbar.make(findViewById(R.id.add_Layout), R.string.snackbar_trans_cat_missing, Snackbar.LENGTH_LONG).show();
            return;
        }
        if (Datas.timeinmillis == 0L) {
            Snackbar.make(findViewById(R.id.add_Layout), R.string.snackbar_trans_time_missing, Snackbar.LENGTH_LONG).show();
            return;
        }
        CheckBox checkedTextView = findViewById(R.id.Learn_Checker);
        Datas.learn = checkedTextView.isChecked();

        Datas.recipname = recip_view.getText().toString();
        Datas.note = note_view.getText().toString();
        mDB.addTransactiononSave(Datas);

        finish();
    }

    public void onDatePick(View v) {

        ATCF = new w_Add_Tran_Datepicker();
        dialogFragment = ATCF;

        Bundle args = new Bundle();

        args.putLong("timeinmillis", Datas.timeinmillis);
        //TODO 년월일시분 전달

        dialogFragment.setArguments(args);
        dialogFragment.show(getSupportFragmentManager().beginTransaction(), "tag");

    }

    public void onCategoryClick(View v) {
        Cursor cursor = mDB.getCategoryList(String.valueOf(cat_view.getText()));
        make_bottomSheet(cursor,BOTTOMSHEET_CATEGORY);
    }

    public void onAccountClick(View v) {
        Cursor cursor = mDB.getAccList();//계좌 목록 가져오기
        make_bottomSheet(cursor, BOTTOMSHEET_ACCOUNT);
    }


    private void make_bottomSheet(Cursor cursor, int type){
        mBottomSheetDialog = new BottomSheetDialog(this);
        View sheetView = getLayoutInflater().inflate(R.layout.w_add_transactions_category_picker, null);

        RecyclerView recyclerView = sheetView.findViewById(R.id.add_tran_recategory_recycler);

        if(type == BOTTOMSHEET_ACCOUNT){

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


            Account_adapter accountadapter = new Account_adapter();
            recyclerView.setAdapter(accountadapter);
        }
        else{
            itemCats = new ArrayList<>();

            if (cursor.getCount() != 0) {
                while (cursor.moveToNext()) {
                    zDBMan.ItemCat item = mDB.getItemCat();
                    item.id = cursor.getLong(0);
                    item.name = cursor.getString(1);
                    itemCats.add(item);
                }
            }
            Category_adapter categoryadapter = new Category_adapter(itemCats);
            recyclerView.setAdapter(categoryadapter);
            recyclerView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                }
            });
        }

        cursor.close();
        recyclerView.setLayoutManager(new GridLayoutManager(this, 2));

        mBottomSheetDialog.setContentView(sheetView);
        mBottomSheetDialog.show();

    }

    private void Check_Card() {
        Cursor cursor = mDB.getCardinfo(Datas.cardid);

        List<ItemRecipient> recipList = new ArrayList<>();

        if (cursor.getCount() != 0) {

            recipList.addAll(getCardReciList(cursor));

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
        Check_Card();
    }

    private List<ItemRecipient> getCardReciList(Cursor cursor){
        List<ItemRecipient> recipList = new ArrayList<>();

        ItemRecipient itemRecipients = new ItemRecipient();
        itemRecipients.recipid = 0;
        itemRecipients.recipient = "해당 없음";
        recipList.add(itemRecipients);

        cursor.moveToNext();

        String[] perfsplit = cursor.getString(0).split(":");
        int perflen = perfsplit.length;

        for (int l = 0; l < perflen; l++) {     //10101, 20100
            ItemRecipient itemRecipient = new ItemRecipient();

            itemRecipient.recipid = Long.parseLong(perfsplit[l]);
            itemRecipient.recipient = mDB.getRecipName(itemRecipient.recipid);
            itemRecipient.conditionexception = 1;
            recipList.add(itemRecipient);
        }


        String[] sections = cursor.getString(1).split(":");
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


            String[] recip = cursor.getString((i * 2)+2).split(":");      //3,5번째.
            String[] reward = cursor.getString((i * 2) + 3).split(":");

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
                        recipList.get(p).conditionperformance = conperf;
                        recipList.get(p).rewtype = rewtype;
                        recipList.get(p).rewamount = rewamount;
                        duplicate = true;
                        break;
                    }

                }
                if (!duplicate) {
                    ItemRecipient itemRecipient = new ItemRecipient();
                    itemRecipient.recipid = recipid;
                    itemRecipient.recipient = mDB.getRecipName(recipid);
                    itemRecipient.conditiontype = conditiontype;
                    itemRecipient.conditionperformance = conperf;
                    itemRecipient.rewamount = rewamount;
                    itemRecipient.rewtype = rewtype;
                    recipList.add(itemRecipient);
                    Log.w("카드 정보", "리워드 : " + itemRecipient.rewamount +"", null);
                }
            }
        }
        cursor.close();
        return recipList;
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        data.getLongExtra("timeinmillis", 0);
        if (BuildConfig.isTEST)
            Log.w("timeinmillis returned", Datas.timeinmillis + "");
    }

    private void setTimeText() {

        TextView date_text = findViewById(R.id.add_tran_time);

        calendar.setTimeInMillis(Datas.timeinmillis);     //나중에 사용하기 위한 변수 저장.

        df.setCalendar(calendar);

        Date date = new Date();
        date.setTime(Datas.timeinmillis);

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

        Datas.category_name = name;
        cat_view.setText(name);
        Datas.category_id = id;
    }

    private void setCategoryOnTab(long cat_id) {
        setCategory( mDB.getCategoryName(cat_id + 1), cat_id +1);
        if (BuildConfig.isTEST) {
            Log.w("Cat_View Update", "" + cat_id);
        }
    }

    private void setCategoryOnUpdate(){

        int temp_level = mDB.getCatLevel(Datas.category_id);
        if(temp_level == 0 || temp_level ==1 || temp_level == 2){        setTab(0);}
        else if(temp_level == 3 || temp_level ==4 || temp_level == 5){        setTab(1);}
        else if(temp_level == 6 || temp_level ==7 || temp_level == 8){        setTab(2);}

        setCategory(  mDB.getCategoryName( Datas.category_id), Datas.category_id);
    }

    public void setAccount(View v){
        zDBMan.ItemAcc itemAcc = (zDBMan.ItemAcc) v.getTag();

        Datas.cardid = itemAcc.cardid;
        Datas.acc_id = itemAcc.id;
        Datas.acc_name = itemAcc.name;
        Datas.acc_type = itemAcc.type;
        Datas.withdrawlday = itemAcc.withdrawalday;
        Datas.withdrawlaccount = itemAcc.withdrawalaccount;
        Datas.balance = itemAcc.balance;

        acc_view.setText(Datas.acc_name);

        if (Datas.acc_type == 2) {
            Check_Card();
        } else if (Datas.acc_type == 3) {
            Credit_Card();
        } else {
            setTransInvisible();
        }
    }   //TODO 데이터 무결성 확인

    private void setAccountOnUpdate(){
        Cursor cursor = mDB.getAccInfo(Datas.acc_id);
        if(cursor.getCount() > 0){
            cursor.moveToNext();

            Datas.acc_id = cursor.getLong(0);
            Datas.acc_type = cursor.getInt(1);
            Datas.acc_name = cursor.getString(2);

            Datas.balance = cursor.getFloat(4);
            Datas.withdrawlaccount = cursor.getInt(5);
            Datas.withdrawlday = cursor.getInt(6);
            Datas.cardid = cursor.getLong(7);

            acc_view.setText(Datas.acc_name);

            if (Datas.acc_type == 2) {
                Check_Card();
            } else if (Datas.acc_type == 3) {
                Credit_Card();
            } else {
                setTransInvisible();
            }
        }
        cursor.close();
    }

    private void setRecipient(ItemRecipient itemRecipient){

        Datas.recipid = itemRecipient.recipid;
        Datas.recipname = itemRecipient.recipient;
        Datas.perfexception = itemRecipient.conditionexception;
        Datas.perftype = itemRecipient.conditiontype;
        Datas.perfamount = itemRecipient.conditionperformance;
        Datas.rew_type = itemRecipient.rewtype;
        Datas.rew_amount = itemRecipient.rewamount;
        Datas.rew_amount_calculated = (float) (Datas.rew_amount * 0.01 * Datas.amount);
        Log.e("rew inf","금액 " +Datas.amount +"rew_amount"+Datas.rew_amount+ "calculated" + Datas.rew_amount_calculated);
    }

    private void setRecispinnerOnUpdate(){
        int size = VCV.rew_spin.getAdapter().getCount();
        int i;
        ItemRecipient itemRecipient;
        for(i = 0 ; i<size ; i++){
            itemRecipient = (ItemRecipient) VCV.rew_spin.getItemAtPosition(i);
            if( itemRecipient.recipid == Datas.recipid) {
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

    class ItemRecipient {
        long recipid;
        String recipient;
        int conditionexception;
        int conditiontype =0;   //전월실적(b), 당월 실적(c)
        int conditionperformance= 0;
        int rewtype = 0 ;       //p(oint), d(iscount)
        float rewamount = 0;    //0.7


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
            //text.setTextSize(24);


            final ItemRecipient item = (ItemRecipient) getItem(position);
            text.setText(item.recipient);
            text.setTextSize(TypedValue.COMPLEX_UNIT_SP,20);
            text.setPadding(4, 4, 4, 4);
            text.setTag(item);

            return view;
        }
    
    }

    private  class Account_adapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
        Account_adapter() {

        }

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

        public class categoryNameHolder extends RecyclerView.ViewHolder {
            TextView textView;

            public categoryNameHolder(View v) {
                super(v);
                textView = v.findViewById(R.id.add_tran_category_name);


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
                    params.width = point.x / 2;

                else params.width = point.y / 2;
                v.setLayoutParams(params);

            }
        }
    }


    private  class Category_adapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
        List<zDBMan.ItemCat> items;

        Category_adapter(List<zDBMan.ItemCat> item2) {
            items = item2;
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = getLayoutInflater().inflate(R.layout.w_add_transactions_category_picker_holder, parent, false);


            return new categoryNameHolder(view);
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            final zDBMan.ItemCat itemcat = items.get(position);

            categoryNameHolder newholder = (categoryNameHolder) holder;
            newholder.textView.setText(itemcat.name);
            View v = newholder.textView;
            v.setTag(itemcat.id);
            newholder.textView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {       //자녀 카테고리가 있으면 다시 다이얼로그 띄우고 자녀가 없으면 완료.
                    //onItemClick();
                    mBottomSheetDialog.dismiss();
                    //zDBMan mDB = new zDBMan(getApplicationContext(), false);
                    TextView view = (TextView) v;

                    Cursor cursor = mDB.getCategoryList(String.valueOf(view.getText()));

                    if (cursor.getCount() != 0) {        //자녀 카테고리 표시

                        make_bottomSheet(cursor,BOTTOMSHEET_CATEGORY);

                    } else {
                        TextView vt = (TextView) v;
                        setCategory(vt.getText().toString(), Integer.parseInt(v.getTag().toString()) );


                    }

                }

            });
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

                /*
                float density  = getResources().getDisplayMetrics().density;
                float dpHeight = outMetrics.heightPixels / density;
                float dpWidth  = outMetrics.widthPixels / density;
*/
                ViewGroup.LayoutParams params = v.getLayoutParams();


                if (rotation == Surface.ROTATION_0 || rotation == Surface.ROTATION_180)
                    params.width = point.x / 2;

                else params.width = point.y / 2;

                v.setLayoutParams(params);

                //Log.e("디스플레이 사이즈", density+":" + dpHeight+":"  + dpWidth+":"  + rotation +"");

            }
        }

    }

}