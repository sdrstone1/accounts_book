package com.example.kollhong.accounts3;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.prolificinteractive.materialcalendarview.*;
import com.prolificinteractive.materialcalendarview.spans.DotSpan;


import java.util.*;
import java.util.concurrent.Executors;

/**
 * Created by KollHong on 25/03/2018.
 */


public class Transaction_Calendar extends Fragment {
    GlobalFunction globalFunction = new GlobalFunction();
    DB_Controll mDB;
    TextView dateView;
    //Calendar calendar;
    MaterialCalendarView calendarView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        mDB = new DB_Controll(getContext(), true);

        return inflater.inflate(R.layout.a_trans_frag1, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        calendarView = view.findViewById(R.id.calendarView);
        new ApiSimulator(Calendar.getInstance()).executeOnExecutor(Executors.newSingleThreadExecutor());


        //\\calendarView.setOnMonthChangedListener(new onMonthChangedListener());
    }

    public class onDateSelectedListener implements OnDateSelectedListener {
        @Override
        public void onDateSelected(@NonNull MaterialCalendarView widget, @NonNull CalendarDay date, boolean selected) {

            //TODO make bottomsheet
            //TODO bottomsheet
        }
    }

    public class onMonthChangedListener implements OnMonthChangedListener {
        @Override
        public void onMonthChanged(MaterialCalendarView widget, CalendarDay date) {
            // localdate = date.getDate();

        }
    }


    private class ApiSimulator extends AsyncTask<Void, Void, List<CalendarDay>> {

        Calendar calendar;
        boolean shouldDecorate = false;
        boolean transferOnly = true;
        float amount = 0f;

        ApiSimulator(Calendar localDate) {
            this.calendar = localDate;
        }

        @Override
        protected List<CalendarDay> doInBackground(@NonNull Void... voids) {
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            calendar = GlobalFunction.Calendar.getFirstDayOfCalendar(calendar);

            ArrayList<CalendarDay> dates = new ArrayList<>();


            Calendar calendar2 = Calendar.getInstance();
            //month = calendar.get(Calendar.MONTH);


            int lastDayOfMonth = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
            int firstDayOfMonth = calendar.getActualMinimum(Calendar.DAY_OF_MONTH);

            long today;
            long tomorrow;


            for (int day2 = lastDayOfMonth; day2 >= firstDayOfMonth; day2--) {
                calendar.set(Calendar.DAY_OF_MONTH, day2);

                today = calendar.getTimeInMillis();
                calendar2.setTimeInMillis(today);
                calendar2.add(Calendar.DAY_OF_MONTH, 1);
                tomorrow = calendar2.getTimeInMillis() - 1L;
                //calendar2.setTimeInMillis(tomorrow);

                List<DBItem.TransactionsViewItem> transactionHistoryList = mDB.getTransHistory(today, tomorrow);


                if (!transactionHistoryList.isEmpty()) {
                    dates.add(CalendarDay.from(this.calendar));

                    for (DBItem.TransactionsViewItem transactionsViewItem : transactionHistoryList) {
                        if (transactionsViewItem.categoryLevel == 0 || transactionsViewItem.categoryLevel == 1 || transactionsViewItem.categoryLevel == 2) { //0,1,2        3,4,5           6,7,8
                            amount += transactionsViewItem.amount;
                            transferOnly = false;
                        } else if (transactionsViewItem.categoryLevel == 3 || transactionsViewItem.categoryLevel == 4 || transactionsViewItem.categoryLevel == 5) {
                            amount -= transactionsViewItem.amount;
                            transferOnly = false;
                        }
                    }

                    if (amount == 0) {
                        transferOnly = true;
                    }
                }

            }
            return dates;
        }

        @Override
        protected void onPostExecute(@NonNull List<CalendarDay> calendarDays) {
            super.onPostExecute(calendarDays);

            int color = 0;
            if (transferOnly) {
                color = GlobalFunction.Color.getColor(getContext(), android.R.color.darker_gray);
            } else if (amount >= 0) {
                color =  GlobalFunction.Color.getColor(getContext(), android.R.color.holo_blue_light);
            } else if (amount < 0) {
                color = GlobalFunction.Color.getColor(getContext(), android.R.color.holo_red_light);
            } else {
                Log.e("Decoration", "if문 에러");
            }
            calendarView.addDecorator(new EventDecorator(color, calendarDays, getActivity()));
        }
    }

    public class EventDecorator implements DayViewDecorator {

        // private final Drawable drawable;
        private int color;
        private HashSet<CalendarDay> dates;

        public EventDecorator(int color, Collection<CalendarDay> dates, Activity context) {
            //drawable = context.getResources().getDrawable(R.drawable.more);
            this.color = color;
            this.dates = new HashSet<>(dates);
        }

        @Override
        public boolean shouldDecorate(CalendarDay day) {
            return dates.contains(day);
        }

        @Override
        public void decorate(DayViewFacade view) {
            //   view.setSelectionDrawable(drawable);

            view.addSpan(new DotSpan(5, color)); // 날자밑에 점
        }

    }

}

