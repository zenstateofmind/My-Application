package com.timeto.makemezen;

import android.os.Bundle;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.Calendar;


/**
 * A simple {@link Fragment} subclass.
 */
public class CalendarViewFragment extends Fragment {

    private static final String TAG = CalendarViewFragment.class.getSimpleName();

    public CalendarViewFragment() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View calendarViewFragment = inflater.inflate(R.layout.fragment_calendar_view, container, false);

        setupDateTime(calendarViewFragment);
        setOnClickListenersForCalendarObjects(calendarViewFragment);
        return calendarViewFragment;
    }

    private void setOnClickListenersForCalendarObjects(View calendarViewFragment) {
        final LinearLayout firstDayLayout = calendarViewFragment.findViewById(R.id.first_day);
        final LinearLayout secondDayLayout = calendarViewFragment.findViewById(R.id.second_day);
        final LinearLayout thirdDayLayout = calendarViewFragment.findViewById(R.id.third_day);
        final LinearLayout fourthDayLayout = calendarViewFragment.findViewById(R.id.fourth_day);
        final LinearLayout fifthDayLayout = calendarViewFragment.findViewById(R.id.fifth_day);
        final LinearLayout sixthDayLayout = calendarViewFragment.findViewById(R.id.sixth_day);
        final LinearLayout seventhDayLayout = calendarViewFragment.findViewById(R.id.seventh_day);

        firstDayLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                firstDayLayout.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.calendar_curved_orange));
                secondDayLayout.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.calendar_curved_shape));
                thirdDayLayout.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.calendar_curved_shape));
                fourthDayLayout.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.calendar_curved_shape));
                fifthDayLayout.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.calendar_curved_shape));
                sixthDayLayout.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.calendar_curved_shape));
                seventhDayLayout.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.calendar_curved_shape));
            }
        });

        secondDayLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                secondDayLayout.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.calendar_curved_orange));
                firstDayLayout.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.calendar_curved_shape));
                thirdDayLayout.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.calendar_curved_shape));
                fourthDayLayout.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.calendar_curved_shape));
                fifthDayLayout.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.calendar_curved_shape));
                sixthDayLayout.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.calendar_curved_shape));
                seventhDayLayout.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.calendar_curved_shape));
            }
        });

        thirdDayLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                thirdDayLayout.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.calendar_curved_orange));
                firstDayLayout.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.calendar_curved_shape));
                secondDayLayout.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.calendar_curved_shape));
                fourthDayLayout.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.calendar_curved_shape));
                fifthDayLayout.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.calendar_curved_shape));
                sixthDayLayout.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.calendar_curved_shape));
                seventhDayLayout.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.calendar_curved_shape));
            }
        });

        fourthDayLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fourthDayLayout.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.calendar_curved_orange));
                firstDayLayout.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.calendar_curved_shape));
                secondDayLayout.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.calendar_curved_shape));
                thirdDayLayout.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.calendar_curved_shape));
                fifthDayLayout.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.calendar_curved_shape));
                sixthDayLayout.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.calendar_curved_shape));
                seventhDayLayout.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.calendar_curved_shape));
            }
        });

        fifthDayLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fifthDayLayout.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.calendar_curved_orange));
                firstDayLayout.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.calendar_curved_shape));
                secondDayLayout.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.calendar_curved_shape));
                fourthDayLayout.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.calendar_curved_shape));
                thirdDayLayout.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.calendar_curved_shape));
                sixthDayLayout.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.calendar_curved_shape));
                seventhDayLayout.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.calendar_curved_shape));
            }
        });

        sixthDayLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sixthDayLayout.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.calendar_curved_orange));
                firstDayLayout.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.calendar_curved_shape));
                secondDayLayout.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.calendar_curved_shape));
                fourthDayLayout.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.calendar_curved_shape));
                fifthDayLayout.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.calendar_curved_shape));
                thirdDayLayout.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.calendar_curved_shape));
                seventhDayLayout.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.calendar_curved_shape));
            }
        });

        seventhDayLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                seventhDayLayout.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.calendar_curved_orange));
                firstDayLayout.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.calendar_curved_shape));
                secondDayLayout.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.calendar_curved_shape));
                fourthDayLayout.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.calendar_curved_shape));
                fifthDayLayout.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.calendar_curved_shape));
                sixthDayLayout.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.calendar_curved_shape));
                thirdDayLayout.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.calendar_curved_shape));
            }
        });

    }

    private void setupDateTime(View calendarViewFragment) {
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DAY_OF_YEAR, -7);

        int dayOfMonth = -1;
        String dayOfWeek = "";
        TextView dayOfWeekView;
        TextView dayOfMonthView;

        //6 days ago
        cal.add(Calendar.DAY_OF_YEAR, 1);
        dayOfMonth = cal.get(Calendar.DAY_OF_MONTH);
        dayOfWeek = getDayOfWeek(cal.get(Calendar.DAY_OF_WEEK));

        dayOfWeekView = (TextView) calendarViewFragment.findViewById(R.id.first_day_name);
        dayOfWeekView.setText(dayOfWeek);
        dayOfMonthView = (TextView) calendarViewFragment.findViewById(R.id.first_day_date);
        dayOfMonthView.setText(dayOfMonth+"");

        //5 days ago
        cal.add(Calendar.DAY_OF_YEAR, 1);
        dayOfMonth = cal.get(Calendar.DAY_OF_MONTH);
        dayOfWeek = getDayOfWeek(cal.get(Calendar.DAY_OF_WEEK));

        dayOfWeekView = (TextView) calendarViewFragment.findViewById(R.id.second_day_name);
        dayOfWeekView.setText(dayOfWeek);
        dayOfMonthView = (TextView) calendarViewFragment.findViewById(R.id.second_day_date);
        dayOfMonthView.setText(dayOfMonth+"");

        //4 days ago
        cal.add(Calendar.DAY_OF_YEAR, 1);
        dayOfMonth = cal.get(Calendar.DAY_OF_MONTH);
        dayOfWeek = getDayOfWeek(cal.get(Calendar.DAY_OF_WEEK));

        dayOfWeekView = (TextView) calendarViewFragment.findViewById(R.id.third_day_name);
        dayOfWeekView.setText(dayOfWeek);
        dayOfMonthView = (TextView) calendarViewFragment.findViewById(R.id.third_day_date);
        dayOfMonthView.setText(dayOfMonth+"");

        //3 days ago
        cal.add(Calendar.DAY_OF_YEAR, 1);
        dayOfMonth = cal.get(Calendar.DAY_OF_MONTH);
        dayOfWeek = getDayOfWeek(cal.get(Calendar.DAY_OF_WEEK));

        dayOfWeekView = (TextView) calendarViewFragment.findViewById(R.id.fourth_day_name);
        dayOfWeekView.setText(dayOfWeek);
        dayOfMonthView = (TextView) calendarViewFragment.findViewById(R.id.fourth_day_date);
        dayOfMonthView.setText(dayOfMonth+"");

        //2 days ago
        cal.add(Calendar.DAY_OF_YEAR, 1);
        dayOfMonth = cal.get(Calendar.DAY_OF_MONTH);
        dayOfWeek = getDayOfWeek(cal.get(Calendar.DAY_OF_WEEK));

        dayOfWeekView = (TextView) calendarViewFragment.findViewById(R.id.fifth_day_name);
        dayOfWeekView.setText(dayOfWeek);
        dayOfMonthView = (TextView) calendarViewFragment.findViewById(R.id.fifth_day_date);
        dayOfMonthView.setText(dayOfMonth+"");

        //1 day ago
        cal.add(Calendar.DAY_OF_YEAR, 1);
        dayOfMonth = cal.get(Calendar.DAY_OF_MONTH);
        dayOfWeek = getDayOfWeek(cal.get(Calendar.DAY_OF_WEEK));

        dayOfWeekView = (TextView) calendarViewFragment.findViewById(R.id.sixth_day_name);
        dayOfWeekView.setText(dayOfWeek);
        dayOfMonthView = (TextView) calendarViewFragment.findViewById(R.id.sixth_day_date);
        dayOfMonthView.setText(dayOfMonth+"");

        //Today
        cal.add(Calendar.DAY_OF_YEAR, 1);
        dayOfMonth = cal.get(Calendar.DAY_OF_MONTH);
        dayOfWeek = getDayOfWeek(cal.get(Calendar.DAY_OF_WEEK));

        dayOfWeekView = (TextView) calendarViewFragment.findViewById(R.id.seventh_day_name);
        dayOfWeekView.setText(dayOfWeek);
        dayOfMonthView = (TextView) calendarViewFragment.findViewById(R.id.seventh_day_date);
        dayOfMonthView.setText(dayOfMonth+"");


        // Get the days for the past 7 days


        // Get the dates for the past 7 days
        // Set the right headline
    }

    private String getDayOfWeek(int dayOfWeekValue) {

        String dayOfWeek = "";

        switch(dayOfWeekValue) {
            case Calendar.SUNDAY:
                dayOfWeek = "Sun";
                break;
            case Calendar.MONDAY:
                dayOfWeek = "Mon";
                break;
            case Calendar.TUESDAY:
                dayOfWeek = "Tue";
                break;
            case Calendar.WEDNESDAY:
                dayOfWeek = "Wed";
                break;
            case Calendar.THURSDAY:
                dayOfWeek = "Thu";
                break;
            case Calendar.FRIDAY:
                dayOfWeek = "Fri";
                break;
            case Calendar.SATURDAY:
                dayOfWeek = "Sat";
                break;
        }

        return dayOfWeek;
    }
}
