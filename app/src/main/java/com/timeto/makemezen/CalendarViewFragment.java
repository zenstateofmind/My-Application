package com.timeto.makemezen;

import android.graphics.drawable.Drawable;
import android.os.Bundle;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

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


    /**
     * First day - 7 days ago
     * Seventh day - today
     * @param inflater
     * @param container
     * @param savedInstanceState
     * @return
     */
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
                Drawable firstDayOriginalBackground = firstDayLayout.getBackground();
                firstDayLayout.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.calendar_curved_orange));
                Drawable firstDayCurrBackground = firstDayLayout.getBackground();
                secondDayLayout.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.calendar_curved_shape));
                thirdDayLayout.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.calendar_curved_shape));
                fourthDayLayout.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.calendar_curved_shape));
                fifthDayLayout.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.calendar_curved_shape));
                sixthDayLayout.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.calendar_curved_shape));
                seventhDayLayout.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.calendar_curved_shape));
                if (firstDayCurrBackground != firstDayOriginalBackground) {
                    Calendar startTime = Calendar.getInstance();
                    startTime.add(Calendar.DAY_OF_YEAR, -6);
                    startTime.set(Calendar.HOUR_OF_DAY, 0);
                    startTime.set(Calendar.MINUTE, 0);
                    startTime.set(Calendar.SECOND, 0);
                    startTime.set(Calendar.MILLISECOND, 0);

                    Calendar endTime = Calendar.getInstance();
                    endTime.add(Calendar.DAY_OF_YEAR, -5);
                    endTime.set(Calendar.HOUR_OF_DAY, 0);
                    endTime.set(Calendar.MINUTE, 0);
                    endTime.set(Calendar.SECOND, 0);
                    endTime.set(Calendar.MILLISECOND, 0);

                    Bundle bundle = new Bundle();
                    bundle.putLong(MakeMeZenConstants.START_DATE_MILLISECONDS, startTime.getTimeInMillis());
                    bundle.putLong(MakeMeZenConstants.END_DATE_MILLISECONDS, endTime.getTimeInMillis());
                    AppUsageListFragment appUsageListFragment = new AppUsageListFragment();
                    appUsageListFragment.setArguments(bundle);

                    FragmentTransaction transaction = getFragmentManager().beginTransaction();
                    transaction.replace(R.id.app_usage_list_fragment, appUsageListFragment);
                    transaction.commit();
                }

            }
        });

        secondDayLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Drawable secondDayOriginalBackground = secondDayLayout.getBackground();
                secondDayLayout.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.calendar_curved_orange));
                Drawable secondDayCurrBackground = secondDayLayout.getBackground();
                firstDayLayout.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.calendar_curved_shape));
                thirdDayLayout.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.calendar_curved_shape));
                fourthDayLayout.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.calendar_curved_shape));
                fifthDayLayout.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.calendar_curved_shape));
                sixthDayLayout.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.calendar_curved_shape));
                seventhDayLayout.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.calendar_curved_shape));

                if (secondDayOriginalBackground != secondDayCurrBackground) {
                    Calendar startTime = Calendar.getInstance();
                    startTime.add(Calendar.DAY_OF_YEAR, -5);
                    startTime.set(Calendar.HOUR_OF_DAY, 0);
                    startTime.set(Calendar.MINUTE, 0);
                    startTime.set(Calendar.SECOND, 0);
                    startTime.set(Calendar.MILLISECOND, 0);

                    Calendar endTime = Calendar.getInstance();
                    endTime.add(Calendar.DAY_OF_YEAR, -4);
                    endTime.set(Calendar.HOUR_OF_DAY, 0);
                    endTime.set(Calendar.MINUTE, 0);
                    endTime.set(Calendar.SECOND, 0);
                    endTime.set(Calendar.MILLISECOND, 0);

                    Bundle bundle = new Bundle();
                    bundle.putLong(MakeMeZenConstants.START_DATE_MILLISECONDS, startTime.getTimeInMillis());
                    bundle.putLong(MakeMeZenConstants.END_DATE_MILLISECONDS, endTime.getTimeInMillis());
                    AppUsageListFragment appUsageListFragment = new AppUsageListFragment();
                    appUsageListFragment.setArguments(bundle);

                    FragmentTransaction transaction = getFragmentManager().beginTransaction();
                    transaction.replace(R.id.app_usage_list_fragment, appUsageListFragment);
                    transaction.commit();
                }
            }
        });

        thirdDayLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Drawable thirdDayOriginalBackground = thirdDayLayout.getBackground();
                thirdDayLayout.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.calendar_curved_orange));
                Drawable thirdDayCurrBackground = thirdDayLayout.getBackground();
                firstDayLayout.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.calendar_curved_shape));
                secondDayLayout.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.calendar_curved_shape));
                fourthDayLayout.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.calendar_curved_shape));
                fifthDayLayout.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.calendar_curved_shape));
                sixthDayLayout.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.calendar_curved_shape));
                seventhDayLayout.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.calendar_curved_shape));

                if (thirdDayOriginalBackground != thirdDayCurrBackground) {
                    Calendar startTime = Calendar.getInstance();
                    startTime.add(Calendar.DAY_OF_YEAR, -4);
                    startTime.set(Calendar.HOUR_OF_DAY, 0);
                    startTime.set(Calendar.MINUTE, 0);
                    startTime.set(Calendar.SECOND, 0);
                    startTime.set(Calendar.MILLISECOND, 0);

                    Calendar endTime = Calendar.getInstance();
                    endTime.add(Calendar.DAY_OF_YEAR, -3);
                    endTime.set(Calendar.HOUR_OF_DAY, 0);
                    endTime.set(Calendar.MINUTE, 0);
                    endTime.set(Calendar.SECOND, 0);
                    endTime.set(Calendar.MILLISECOND, 0);

                    Bundle bundle = new Bundle();
                    bundle.putLong(MakeMeZenConstants.START_DATE_MILLISECONDS, startTime.getTimeInMillis());
                    bundle.putLong(MakeMeZenConstants.END_DATE_MILLISECONDS, endTime.getTimeInMillis());
                    AppUsageListFragment appUsageListFragment = new AppUsageListFragment();
                    appUsageListFragment.setArguments(bundle);

                    FragmentTransaction transaction = getFragmentManager().beginTransaction();
                    transaction.replace(R.id.app_usage_list_fragment, appUsageListFragment);
                    transaction.commit();
                }
            }
        });

        fourthDayLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Drawable fourthDayOriginalBackground = fourthDayLayout.getBackground();
                fourthDayLayout.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.calendar_curved_orange));
                Drawable fourthDayCurrBackground = fourthDayLayout.getBackground();
                firstDayLayout.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.calendar_curved_shape));
                secondDayLayout.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.calendar_curved_shape));
                thirdDayLayout.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.calendar_curved_shape));
                fifthDayLayout.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.calendar_curved_shape));
                sixthDayLayout.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.calendar_curved_shape));
                seventhDayLayout.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.calendar_curved_shape));
                if (fourthDayOriginalBackground != fourthDayCurrBackground) {
                    Calendar startTime = Calendar.getInstance();
                    startTime.add(Calendar.DAY_OF_YEAR, -3);
                    startTime.set(Calendar.HOUR_OF_DAY, 0);
                    startTime.set(Calendar.MINUTE, 0);
                    startTime.set(Calendar.SECOND, 0);
                    startTime.set(Calendar.MILLISECOND, 0);

                    Calendar endTime = Calendar.getInstance();
                    endTime.add(Calendar.DAY_OF_YEAR, -2);
                    endTime.set(Calendar.HOUR_OF_DAY, 0);
                    endTime.set(Calendar.MINUTE, 0);
                    endTime.set(Calendar.SECOND, 0);
                    endTime.set(Calendar.MILLISECOND, 0);

                    Bundle bundle = new Bundle();
                    bundle.putLong(MakeMeZenConstants.START_DATE_MILLISECONDS, startTime.getTimeInMillis());
                    bundle.putLong(MakeMeZenConstants.END_DATE_MILLISECONDS, endTime.getTimeInMillis());
                    AppUsageListFragment appUsageListFragment = new AppUsageListFragment();
                    appUsageListFragment.setArguments(bundle);

                    FragmentTransaction transaction = getFragmentManager().beginTransaction();
                    transaction.replace(R.id.app_usage_list_fragment, appUsageListFragment);
                    transaction.commit();
                }
            }
        });

        fifthDayLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Drawable fifthDayOriginalBackground = fifthDayLayout.getBackground();
                fifthDayLayout.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.calendar_curved_orange));
                Drawable fifthDayCurrBackground = fifthDayLayout.getBackground();
                firstDayLayout.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.calendar_curved_shape));
                secondDayLayout.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.calendar_curved_shape));
                fourthDayLayout.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.calendar_curved_shape));
                thirdDayLayout.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.calendar_curved_shape));
                sixthDayLayout.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.calendar_curved_shape));
                seventhDayLayout.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.calendar_curved_shape));
                if (fifthDayOriginalBackground != fifthDayCurrBackground) {
                    Calendar startTime = Calendar.getInstance();
                    startTime.add(Calendar.DAY_OF_YEAR, -2);
                    startTime.set(Calendar.HOUR_OF_DAY, 0);
                    startTime.set(Calendar.MINUTE, 0);
                    startTime.set(Calendar.SECOND, 0);
                    startTime.set(Calendar.MILLISECOND, 0);

                    Calendar endTime = Calendar.getInstance();
                    endTime.add(Calendar.DAY_OF_YEAR, -1);
                    endTime.set(Calendar.HOUR_OF_DAY, 0);
                    endTime.set(Calendar.MINUTE, 0);
                    endTime.set(Calendar.SECOND, 0);
                    endTime.set(Calendar.MILLISECOND, 0);

                    Bundle bundle = new Bundle();
                    bundle.putLong(MakeMeZenConstants.START_DATE_MILLISECONDS, startTime.getTimeInMillis());
                    bundle.putLong(MakeMeZenConstants.END_DATE_MILLISECONDS, endTime.getTimeInMillis());
                    AppUsageListFragment appUsageListFragment = new AppUsageListFragment();
                    appUsageListFragment.setArguments(bundle);

                    FragmentTransaction transaction = getFragmentManager().beginTransaction();
                    transaction.replace(R.id.app_usage_list_fragment, appUsageListFragment);
                    transaction.commit();
                }
            }
        });

        sixthDayLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Drawable sixthDayOriginalBackground = sixthDayLayout.getBackground();
                sixthDayLayout.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.calendar_curved_orange));
                Drawable sixthDayCurrBackground = sixthDayLayout.getBackground();
                firstDayLayout.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.calendar_curved_shape));
                secondDayLayout.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.calendar_curved_shape));
                fourthDayLayout.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.calendar_curved_shape));
                fifthDayLayout.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.calendar_curved_shape));
                thirdDayLayout.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.calendar_curved_shape));
                seventhDayLayout.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.calendar_curved_shape));

                if (sixthDayCurrBackground != sixthDayOriginalBackground) {
                    Calendar startTime = Calendar.getInstance();
                    startTime.add(Calendar.DAY_OF_YEAR, -1);
                    startTime.set(Calendar.HOUR_OF_DAY, 0);
                    startTime.set(Calendar.MINUTE, 0);
                    startTime.set(Calendar.SECOND, 0);
                    startTime.set(Calendar.MILLISECOND, 0);

                    Calendar endTime = Calendar.getInstance();
                    endTime.add(Calendar.DAY_OF_YEAR, 0);
                    endTime.set(Calendar.HOUR_OF_DAY, 0);
                    endTime.set(Calendar.MINUTE, 0);
                    endTime.set(Calendar.SECOND, 0);
                    endTime.set(Calendar.MILLISECOND, 0);

                    Bundle bundle = new Bundle();
                    bundle.putLong(MakeMeZenConstants.START_DATE_MILLISECONDS, startTime.getTimeInMillis());
                    bundle.putLong(MakeMeZenConstants.END_DATE_MILLISECONDS, endTime.getTimeInMillis());
                    AppUsageListFragment appUsageListFragment = new AppUsageListFragment();
                    appUsageListFragment.setArguments(bundle);

                    FragmentTransaction transaction = getFragmentManager().beginTransaction();
                    transaction.replace(R.id.app_usage_list_fragment, appUsageListFragment);
                    transaction.commit();
                }
            }
        });

        seventhDayLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Drawable seventhDayOriginalBackground = seventhDayLayout.getBackground();
                seventhDayLayout.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.calendar_curved_orange));
                Drawable seventhDayCurrBackground = seventhDayLayout.getBackground();
                firstDayLayout.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.calendar_curved_shape));
                secondDayLayout.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.calendar_curved_shape));
                fourthDayLayout.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.calendar_curved_shape));
                fifthDayLayout.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.calendar_curved_shape));
                sixthDayLayout.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.calendar_curved_shape));
                thirdDayLayout.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.calendar_curved_shape));
                if (seventhDayCurrBackground != seventhDayOriginalBackground) {
                    Calendar startTime = Calendar.getInstance();
                    startTime.add(Calendar.DAY_OF_YEAR, 0);
                    startTime.set(Calendar.HOUR_OF_DAY, 0);
                    startTime.set(Calendar.MINUTE, 0);
                    startTime.set(Calendar.SECOND, 0);
                    startTime.set(Calendar.MILLISECOND, 0);

                    Bundle bundle = new Bundle();
                    bundle.putLong(MakeMeZenConstants.START_DATE_MILLISECONDS, startTime.getTimeInMillis());
                    bundle.putLong(MakeMeZenConstants.END_DATE_MILLISECONDS, System.currentTimeMillis());
                    AppUsageListFragment appUsageListFragment = new AppUsageListFragment();
                    appUsageListFragment.setArguments(bundle);

                    FragmentTransaction transaction = getFragmentManager().beginTransaction();
                    transaction.replace(R.id.app_usage_list_fragment, appUsageListFragment);
                    transaction.commit();
                }
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
