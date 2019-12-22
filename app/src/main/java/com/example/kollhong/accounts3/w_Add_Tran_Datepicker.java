package com.example.kollhong.accounts3;

import android.widget.NumberPicker;
import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;


import java.text.DateFormat;
import java.text.DateFormatSymbols;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import static java.text.DateFormat.getDateInstance;

/**
 * Created by KollHong on 22/05/2018.
 */

public class w_Add_Tran_Datepicker extends DialogFragment{

        String dates[];
        NumberPicker datePicker, hourPicker, minutePicker, ampmPicker;
        Button doneButton;
        Calendar calendar = Calendar.getInstance();
        long timeinmillis = 0;

        String[] ampm;


        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            DateFormatSymbols time_symbols =  DateFormatSymbols.getInstance();
            ampm = time_symbols.getAmPmStrings();

            if(BuildConfig.isTEST)
                Log.i( " symbol symbol " ,time_symbols.getLocalPatternChars());
            try{
                timeinmillis = getArguments().getLong("timeinmillis");
            } catch(Exception e){
                timeinmillis = 0;
            }
            if( timeinmillis > 0) {
                calendar.setTimeInMillis(timeinmillis);
            }
            else {
                timeinmillis = calendar.getTimeInMillis();
            }
            dates = getDatesFromCalender();


        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            Dialog getDialog = new Dialog(getActivity());


            View view = inflater.inflate(R.layout.w_add_transactions_datepicker, container);
            datePicker = view.findViewById(R.id.custom_picker_date);
            hourPicker = view.findViewById(R.id.custom_picker_hour);
            minutePicker = view.findViewById(R.id.custom_picker_minute);
            ampmPicker = view.findViewById(R.id.custom_picker_ampm);
            doneButton = view.findViewById(R.id.custom_picker_set);


            datePicker.setMinValue(0);
            datePicker.setMaxValue(dates.length - 1);
            datePicker.setFormatter(new NumberPicker.Formatter() {
                @Override
                public String format(int value) {
                    return dates[value];
                }
            });
            datePicker.setDisplayedValues(dates);
            //datePicker.setValue(60);

            NumberPicker.OnValueChangeListener listener1 = new NumberPicker.OnValueChangeListener(){
                @Override
                public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                    Log.w("old and new", oldVal + " : " + newVal);
                    if (oldVal == 11 && newVal == 0) {
                        ampmPicker.setValue(ampmPicker.getValue()+1);
                    }
                    else if(oldVal == 0 && newVal == 11 )
                        ampmPicker.setValue(ampmPicker.getValue()-1);

                }

            };
            ampmPicker.setFormatter(new NumberPicker.Formatter() {
                @Override
                public String format(int value) {

                    return ampm[value];
                }
            });

            hourPicker.setMinValue(0);
            hourPicker.setMaxValue(11);
            hourPicker.setValue(calendar.get(Calendar.HOUR));
            hourPicker.setOnValueChangedListener(listener1);


            ampmPicker.setMinValue(0);
            ampmPicker.setMaxValue(1);
            ampmPicker.setDisplayedValues(ampm);
            ampmPicker.setValue(calendar.get(Calendar.AM_PM));


            NumberPicker.OnValueChangeListener listener2 = new NumberPicker.OnValueChangeListener(){
                @Override
                public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                    Log.w("old and new", oldVal + " : " + newVal);
                    if (oldVal == 59 && newVal == 0) {
                        hourPicker.setValue(hourPicker.getValue()+1);
                    }
                    else if(oldVal == 0 && newVal == 59 )
                        hourPicker.setValue(hourPicker.getValue()-1);
                }

            };

            minutePicker.setMinValue(0);
            minutePicker.setMaxValue(59);
            minutePicker.setValue(calendar.get(Calendar.MINUTE));
            minutePicker.setOnValueChangedListener(listener2);

            getDialog.setTitle(R.string.title_add_trans_datepicker_dialog);
            return view;
        }

        private String[] getDatesFromCalender() {
            Calendar c1 = Calendar.getInstance();
            c1.setTimeInMillis(timeinmillis);
            Calendar c2 = Calendar.getInstance();
            c2.setTimeInMillis(timeinmillis);


            List<String> dates = new ArrayList<String>();
            java.text.DateFormat dateFormat = getDateInstance();
            dates.add(dateFormat.format(c1.getTime()));

            for (int i = 0; i < 60; i++) {
                c1.add(Calendar.DATE, 1);
                dates.add(dateFormat.format(c1.getTime()));
            }
            c2.add(Calendar.DATE, -60);

            for (int i = 0; i < 60; i++) {
                c2.add(Calendar.DATE, 1);
                dates.add(dateFormat.format(c2.getTime()));
            }
            return dates.toArray(new String[dates.size() - 1]);
        }


        public long getTimeinmillis() throws ParseException {

            //TODO 날짜 반환
            String date = dates[datePicker.getValue()];       //형식 apr 10, 2018 or 2018.4.11.
            DateFormat dateFormat = getDateInstance();
            dateFormat.parse(date);
            calendar = dateFormat.getCalendar();

            //calendar.add(Calendar.DATE, date);

            int hour = hourPicker.getValue();
            int minute = minutePicker.getValue();
            int ampm = ampmPicker.getValue();


            calendar.set(Calendar.HOUR, hour);
            calendar.set(Calendar.MINUTE, minute);
            calendar.set(Calendar.AM_PM, ampm);

            timeinmillis = calendar.getTimeInMillis();

            if(BuildConfig.isTEST)
                Log.w("result date", timeinmillis+"");

            dismiss();
            return timeinmillis;
        }

}
