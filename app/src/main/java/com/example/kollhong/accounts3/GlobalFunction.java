package com.example.kollhong.accounts3;

import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.support.v4.content.ContextCompat;

import java.util.Calendar;

public class GlobalFunction {
    public static class Color {
        public static int getColor(Context cxt, int res) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                return ContextCompat.getColor(cxt, res);
            } else {
                return cxt.getResources().getColor(res);
            }
        }
    }

    public static class Calendar {
        public static java.util.Calendar getFirstDayOfCalendar(java.util.Calendar calendar){
            calendar.set(java.util.Calendar.HOUR_OF_DAY, 0);
            calendar.set(java.util.Calendar.MINUTE, 0);
            calendar.set(java.util.Calendar.SECOND, 0);
            calendar.set(java.util.Calendar.MILLISECOND, 0);
            calendar.set(java.util.Calendar.DAY_OF_MONTH, 1);
            return calendar;
        }
    }
}
