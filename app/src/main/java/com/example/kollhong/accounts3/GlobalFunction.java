package com.example.kollhong.accounts3;

import android.content.Context;
import android.os.Build;
import android.support.v4.content.ContextCompat;

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
        //POSIX Time Can't represent Leap Second.
        //So It might be OK to use fixed time.
        public static long ONEDAY_IN_MILLIS = 86399999L;

        public static java.util.Calendar getFirstDayOfCalendar(java.util.Calendar calendar) {
            calendar = getMidnightOfDay(calendar);
            calendar.set(java.util.Calendar.DAY_OF_MONTH, 1);
            return calendar;
        }

        public static java.util.Calendar getMidnightOfDay(java.util.Calendar calendar) {
            calendar.set(java.util.Calendar.HOUR_OF_DAY, 0);
            calendar.set(java.util.Calendar.MINUTE, 0);
            calendar.set(java.util.Calendar.SECOND, 0);
            calendar.set(java.util.Calendar.MILLISECOND, 0);
            return calendar;
        }
    }
}
