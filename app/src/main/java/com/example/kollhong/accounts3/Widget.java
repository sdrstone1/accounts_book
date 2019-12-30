package com.example.kollhong.accounts3;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.widget.RemoteViews;

import java.text.DateFormatSymbols;
import java.util.Calendar;
import java.util.List;

//import com.example.kollhong.accounts3.theta.R;

/**
 * Implementation of App Widget functionality.
 */
public class Widget extends AppWidgetProvider {

    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                int appWidgetId) {

        CharSequence widgetText = context.getString(R.string.appwidget_text);
        // Construct the RemoteViews object
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.u_widget);
        //views.setTextViewText(R.id.appwidget_text, widgetText);

        DB_Controll mDB = new DB_Controll(context,false);

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
        long nextMonth = calendar2.getTimeInMillis() - 1L;
        float amount = 0F;

        int color_red;
        int color_gray;
        int color_blue;


        color_red = GlobalFunction.Color.getColor(context,android.R.color.holo_red_light);
        color_gray = GlobalFunction.Color.getColor(context,android.R.color.darker_gray);
        color_blue = GlobalFunction.Color.getColor(context,android.R.color.holo_blue_light);


        List<DBItem.TransactionsViewItem> valuesList = mDB.getTransbyCat(thisMonth, nextMonth);


        for (DBItem.TransactionsViewItem values : valuesList) {
            amount += values.amount;
        }

        views.setTextViewText(R.id.expense, amount+"");
        if(amount < 0) {
            views.setTextColor(R.id.expense, color_red);
        }
        else if(amount == 0){
            views.setTextColor(R.id.expense, color_gray);
        }
        else if(amount > 0){
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

