package com.example.kollhong.accounts3;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.database.Cursor;
import android.widget.RemoteViews;

//import com.example.kollhong.accounts3.theta.R;

import java.text.DateFormatSymbols;
import java.util.Calendar;

/**
 * Implementation of App Widget functionality.
 */
public class U_Widget extends AppWidgetProvider {

    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                int appWidgetId) {

        CharSequence widgetText = context.getString(R.string.appwidget_text);
        // Construct the RemoteViews object
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.u_widget);
        //views.setTextViewText(R.id.appwidget_text, widgetText);

        zDBMan mDB = new zDBMan(context,false);

        Calendar calendar1 = Calendar.getInstance();
        Calendar calendar2 = Calendar.getInstance();

        int month = calendar1.get(Calendar.MONTH);
        String[] monthFormat = DateFormatSymbols.getInstance().getMonths();

        views.setTextViewText(R.id.acc_name, monthFormat[month]);
        //dateView.setText(monthFormat[month]);

        calendar1.set(Calendar.DATE, 1);
        calendar1.set(Calendar.HOUR_OF_DAY, 0);
        calendar1.set(Calendar.MINUTE, 0);
        calendar1.set(Calendar.SECOND, 0);
        calendar1.set(Calendar.MILLISECOND, 0);
        calendar1.set(Calendar.MONTH, month);


        long thisMonth = calendar1.getTimeInMillis();
        calendar2.setTimeInMillis(thisMonth);
        calendar2.add(Calendar.MONTH, 1);
        long nextMonth = calendar2.getTimeInMillis() - 1l;
        float amount = 0l;

        Cursor cursor = mDB.getTransbyCat(thisMonth, nextMonth);
        if(cursor.getCount() != 0){
            while(cursor.moveToNext()){
                amount += cursor.getFloat(3);
            }
        }
        views.setTextViewText(R.id.expense, amount+"");
        if(amount < 0) {

            int color_red = context.getResources().getColor(android.R.color.holo_red_light,null);

            views.setTextColor(R.id.expense, color_red);
        }
        else if(amount == 0){
            int color_gray = context.getResources().getColor(android.R.color.darker_gray,null);
            views.setTextColor(R.id.expense, color_gray);
        }
        else if(amount > 0){

            int color_blue = context.getResources().getColor(android.R.color.holo_blue_light,null);
            views.setTextColor(R.id.expense, color_blue);
        }
        // Instruct the widget manager to update the widget
        appWidgetManager.updateAppWidget(appWidgetId, views);
        //appWidgetManager.notifyAll();
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        // There may be multiple widgets active, so update all of them
        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId);
        }
    }

    @Override
    public void onEnabled(Context context) {
        // Enter relevant functionality for when the first widget is created

    }

    @Override
    public void onDisabled(Context context) {
        // Enter relevant functionality for when the last widget is disabled
    }
}

