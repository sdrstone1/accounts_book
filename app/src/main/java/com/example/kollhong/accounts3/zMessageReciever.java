package com.example.kollhong.accounts3;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.telephony.SmsMessage;
import android.util.Log;

import java.text.DateFormat;
import java.util.Calendar;
import java.util.Date;

public class zMessageReciever extends BroadcastReceiver {

    String type1;
    String type2;
    String month;
    String day;
    int hour;
    int minute;

    String currency;
    boolean is_CardSMS = false;
    boolean is_BankSMS = false;
    boolean is_Income = false;
    zPrefMan mPrefMan;
    zDBMan mDB;

    zDBScheme.ItemTransactions itemTransactions;

    @Override
    public void onReceive(Context context, Intent intent) {
        // TODO: This method is called when the BroadcastReceiver is receiving

        //TODO 발신인 확인하고
        //TODO 은행 전화번호이면
        //
        //TODO 국민은행 1588-9999
        //TODO 신한카드 1544-7200
        //TODO 날짜 mm/dd 시간 hh:mm 거래 금액 거래처 해외 여부까지 나옴
        // an Intent broadcast.

        //SMS를 수신받으면 발신자에 따라(은행에 따라) 메시지 폼을 분석하고 날짜, 거래처, add_transactions에 전달
        if(intent != null) {
            String action = intent.getAction();
            if(action != null) {
                if(action.equals("android.provider.Telephony.SMS_RECEIVED")) {
                    // DO YOUR STUFF
                } else if (action.equals("android.intent.action.BOOT_COMPLETED")) {
                    mPrefMan = new zPrefMan(context);
                    if(!mPrefMan.getSMSEnabled()) {
                        unregisterReceiverFromManifest(zMessageReciever.class, context);
                        return;
                    }
                }
                else
                    return;
            }
            else return;
        }
        else return;
        mDB = new zDBMan(context,true);
        itemTransactions = new zDBScheme.ItemTransactions();
        Bundle bundle = intent.getExtras();//인텐트의 부가 데이터 수신

        //PDU : Protocol Description Unit : SMS메시지 포맷
        Object messages[] = (Object[])bundle.get("pdus");

        SmsMessage smsMessage[] = new SmsMessage[messages.length];//가져온 Object의 갯수 만큼 sms 객체 생성
        int smsCount = messages.length;
        //createFromPdu()를 통해 Pdu포맷에서 문자메시지를 가져옴
        for(int i = 0 ; i<smsCount;i++) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                String format = bundle.getString("format");
                smsMessage[i] = SmsMessage.createFromPdu((byte[]) messages[i], format);
            } else {
                smsMessage[i] = SmsMessage.createFromPdu((byte[]) messages[i]);
            }
        }

        long time =  smsMessage[0].getTimestampMillis();
        String smsNumber = smsMessage[0].getOriginatingAddress();
        String message = smsMessage[0].getDisplayMessageBody().toString();

        message = message.replace("[Web발신]","")
                .replaceAll("\n\r", " ")
                .replaceAll("\n", " ")
                .replaceAll(System.lineSeparator(), " ")
                .replaceAll("\n", " ")
                .replaceAll("원", " 원");

        if(BuildConfig.isTEST)
            Log.w("SMS Number", smsNumber);

        if ( smsNumber.equals("15447200") || smsNumber.equals("1544-7200")) {
            shinhancard(message);
        }
        else if( smsNumber.equals("15889999") || smsNumber.equals("1588-9999")) {
            kookminbank(message);
            if(BuildConfig.isTEST)
                Log.w("kookminbank","number matched");
        }


        itemTransactions.transaction_id = addTrans();
        //Log.w("SMS수신됨", "수신됨");
        sendNoti(context);

    }

    public long addTrans(){
        //학습에서 기록 확인
        itemTransactions.transaction_id = 0;
        Cursor cursor = mDB.getLearnData(itemTransactions.recipname);   //_id, categoryid, accid, recipientid, budgetexception, perfexceoption, rewardtype, rewardamount
        if(cursor.getCount() != 0){
            cursor.moveToNext();
            //data.trans_id = cursor.getLong(0);
            if(!cursor.isNull(1))
                itemTransactions.category_id =  cursor.getLong(1);
            if(!cursor.isNull(2))
                itemTransactions.asset_id = cursor.getLong(2);

            if(!cursor.isNull(3))
                itemTransactions.franchisee_id = cursor.getLong(3);
            if(!cursor.isNull(4))
                itemTransactions.budget_exception = cursor.getInt(4);
            if(!cursor.isNull(5))
                itemTransactions.reward_exception = cursor.getInt(5);
            if(!cursor.isNull(6))
                itemTransactions.rew_type = cursor.getInt(6);
            if(!cursor.isNull(7))
                itemTransactions.rew_amount = cursor.getFloat(7);
                itemTransactions.rew_amount_calculated = itemTransactions.rew_amount * itemTransactions.amount;
        }else {
            itemTransactions.category_id = 3;       //이체 항목으로 지정
            itemTransactions.asset_id = 0;
            itemTransactions.franchisee_id = 0;
            itemTransactions.budget_exception = 0;
            itemTransactions.reward_exception = 0;
            itemTransactions.rew_type = 0;
            itemTransactions.rew_amount = 0;
            itemTransactions.rew_amount_calculated = 0;
        }
        cursor.close();

        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        calendar.set(Calendar.HOUR_OF_DAY,hour );
        calendar.set(Calendar.MINUTE,minute );
        itemTransactions.timeinmillis = calendar.getTimeInMillis();

        return mDB.addTransactionfromReciever(itemTransactions);

    }


    public void sendNoti(Context context){
        if(is_BankSMS || is_CardSMS) {
            Intent intent = new Intent(context, w_Add_Tran.class);
            //intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

            intent.putExtra("Notification", itemTransactions.transaction_id);

            Log.e("SMS Rec, ", " Trans_id : "+String.valueOf(itemTransactions.transaction_id) );

            //PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);

            // Create the TaskStackBuilder and add the intent, which inflates the back stack
            TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
            stackBuilder.addNextIntentWithParentStack(intent);


            PendingIntent resultPendingIntent =
                    stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);


            createNotificationChannel(context);

            Date date= new Date();
            date.setTime(itemTransactions.timeinmillis);
            DateFormat dateFormat = DateFormat.getDateTimeInstance();

            String dateSTR = dateFormat.format(date);


            //            CharSequence string = getContext().getResources().getText(R.string.reward);
            CharSequence charSequence;
            if(itemTransactions.asset_id == 0) {
                String learn_data = (String) context.getResources().getText(R.string.notification_learn_not_avail);
                String time = (String) context.getResources().getText(R.string.time_text);
                String recip = (String) context.getResources().getText(R.string.recipient);
                charSequence = learn_data +
                        time + " : " + dateSTR + "\n" +
                        recip + " : " + itemTransactions.recipname;
            }else
            {

                String learn_data = (String) context.getResources().getText(R.string.notification_learn_avail);
                String time = (String) context.getResources().getText(R.string.time_text);
                String cat = (String) context.getResources().getText(R.string.category);
                String recip = (String) context.getResources().getText(R.string.recipient);
                String acc = (String) context.getResources().getText(R.string.account);
                String re = (String) context.getResources().getText(R.string.reward);

                charSequence = learn_data +
                        time + " : " + dateSTR;



                String catSTR = mDB.getCategoryName(itemTransactions.category_id);
                if(!catSTR.equals("")){
                    charSequence = charSequence + "\n" + cat + " : " + catSTR;
                }


                Cursor cursor = mDB.getAssetInfo(itemTransactions.asset_id);
                if( cursor.getCount() != 0){
                    cursor.moveToNext();
                    charSequence = charSequence + "\n" + acc + " : " + cursor.getString(2);       //accname
                }
                cursor.close();

                charSequence = charSequence + "\n" + recip + " : "+ itemTransactions.recipname ;//recipient
                String recip_name = mDB.getFranchiseeName(itemTransactions.franchisee_id);
                if(!recip_name.equals("")){
                    charSequence = charSequence + "\n" + re + " : " + recip_name;
                }

            }
            NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context, "5651")
                    .setSmallIcon(R.drawable.ic_notifications_black_24dp)
                    .setContentTitle("가계부")
                    .setContentText("자동으로 거래 기록이 등록되었습니다.")
                    .setStyle(new NotificationCompat.BigTextStyle()
                            .bigText(charSequence))
                    //.setContentIntent(pendingIntent)
                    .setContentIntent(resultPendingIntent)
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                    .setAutoCancel(true);
            NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);

            notificationManager.notify(5651, mBuilder.build());
        }
    }

    private void createNotificationChannel(Context context) {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "App";
            String description = "App Description";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel("5651" , name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    private void unregisterReceiverFromManifest(Class<? extends BroadcastReceiver> clazz, final Context context) {
        final ComponentName component = new ComponentName(context, clazz);
        final int status = context.getPackageManager().getComponentEnabledSetting(component);
        if(status == PackageManager.COMPONENT_ENABLED_STATE_ENABLED) {
            context.getPackageManager()
                    .setComponentEnabledSetting(component, PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
                            PackageManager.DONT_KILL_APP);
        }
    }

    public void shinhancard(String message){
        if(BuildConfig.isTEST)
            Log.w("카드사", "신한 카드");

        String[] split = message.split(" ");
        int len = split.length;
        if(split[0].contains("거절")) {
            //Log.w("Pay Refused", "Not payed");
            is_CardSMS = false;
            return;
        }
            else if(split[1].equals("신한"))
        {

            type1 = split[2];        //카드번호입력승인.
            type2 = split[3];        //이름
            String[] date = split[4].split("/");
            month = date[0];
            day =date[1];

            String[] time = split[5].split(":");
            hour = Integer.parseInt(time[0]);
            minute=Integer.parseInt(time[1]);

            itemTransactions.amount = Float.parseFloat(split[6].replace(",",""));
            currency = split[7];
            for (int i = 8; i < len; i++ )
                itemTransactions.recipname= itemTransactions.recipname + split[i];

            if(BuildConfig.isTEST)
                Log.e("Payed Message",  ""+ itemTransactions.recipname);

            is_CardSMS = true;
            return;
        }
        else {      //신한카드승인 신한해외승인 신한체크승인 신한체크해외승인
            if(BuildConfig.isTEST) {
                Log.w("lenth", len + "");
                for(int i =0; i<len;i++)
                    Log.w("message "+ i, split[i]);
            }

            type1 = split[1];
            type2 = split[2];
            String[] date = split[3].split("/");
            month = date[0];
            day =date[1];
            String[] time = split[4].split(":");
            hour = Integer.parseInt(time[0]);
            minute=Integer.parseInt(time[1]);

            itemTransactions.amount = Float.parseFloat(split[5].replace(",","").replace("(금액)",""));
            currency = split[6];
            for (int i = 7; i < len; i++ )
                itemTransactions.recipname= itemTransactions.recipname + split[i];

            if(BuildConfig.isTEST)
                Log.e("Payed Message",  ""+ itemTransactions.recipname);
            is_CardSMS = true;
            return ;
        }

    }

    public void kookminbank(String message){
        is_CardSMS = false;
        if(BuildConfig.isTEST)
            Log.w("은행사", "국민은행");


        String[] split = message.split(" ");
        int len = split.length;

        if(BuildConfig.isTEST) {
            Log.w("lenth", len + "");
            for(int i =0; i<len;i++)
                Log.w("message "+ i, split[i]);
        }

        type1 = split[1];
        String[] date = split[2].split("/");
        month = date[0];
        day = date[1];

        String[] time =split[3].split(":");
        hour = Integer.parseInt(time[0]);
        minute = Integer.parseInt(time[1]);

        type2 = split[4];
        if( split[9].equals("출금"))
            is_Income = false;
        else if (split[9].equals("입금"))
            is_Income = true;

        itemTransactions.amount = Integer.parseInt(split[10].replace(",",""));
        currency = split[11];

        if(BuildConfig.isTEST)
            Log.e("Payed Message",  ""+ itemTransactions.recipname);
        is_BankSMS = true;
        return;

    }

}
