package com.timeto.makemezen;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.SystemClock;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.amplitude.api.Amplitude;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.concurrent.TimeUnit;


/**
 * A simple {@link Fragment} subclass.
 */
public class AppUsageListFragment extends Fragment {

    private RecyclerView.Adapter adapter;
    private RecyclerView.LayoutManager layoutManager;
    public static String CHANNEL_ID = "APP_REMINDER_ID";
    public static String NOTIFICATION_ID = "notification-id";
    private static String LOG  = AppUsageListFragment.class.getSimpleName();
    private static Long DELTA_UPDATE = 3L;


    public AppUsageListFragment() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ((AppCompatActivity) getActivity()).getSupportActionBar().hide();

        // finally change the color
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getActivity().getWindow();

            // clear FLAG_TRANSLUCENT_STATUS flag:
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

            // add FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS flag to the window
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);

            window.setStatusBarColor(getActivity().getResources().getColor(R.color.red));
        }
    }


    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onResume() {
        super.onResume();
        Amplitude.getInstance().logEvent("Resume app usage list");

        Calendar c = getTodayStartTimeCal();

        Drawable currentDayBackground = getFragmentManager().findFragmentById(R.id.calendar_view_fragment_container).getView().findViewById(R.id.seventh_day).getBackground();

        setupDataAndView(getView(), c.getTimeInMillis(), System.currentTimeMillis());


    }

    /**
     * Three types of notifications:
     * 1. Week end review - how many hours of the app used over the entire week
     * 2. Morning review - Encouragement push
     *                      If hours spent yesterday than the day before, praise!
     *                      TODO: This will be added after we enable features to show time spent across different days
     * 3. Evening push - If any app has more than > x hours -> call that out. Else total amount of
     *                  time spent on the app so far.
     */
    private void kickStartAlarmManager() {


        AlarmManager morningMotivationAlarm = (AlarmManager)getContext().getSystemService(Context.ALARM_SERVICE);
        Intent morningMotivationIntent = new Intent(getContext(), MorningMotivationNotifReceiver.class);
        int id1 = (int)System.currentTimeMillis() + 9;
        PendingIntent morningMotivationPendingIntent = PendingIntent.getBroadcast(getContext(), id1 , morningMotivationIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        morningMotivationAlarm.setInexactRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP, SystemClock.elapsedRealtime(),
                MakeMeZenUtil.TWO_HOUR_INTERVAL, morningMotivationPendingIntent);


        AlarmManager dailyUsageAlarmManager = (AlarmManager)getContext().getSystemService(Context.ALARM_SERVICE);
        Intent dailyUsageIntent = new Intent(getContext(), DailyAppUsageReviewNotifReceiver.class);
        int id2 = (int)System.currentTimeMillis() + 11;
        PendingIntent dailyUsagePendingIntent = PendingIntent.getBroadcast(getContext(), 0, dailyUsageIntent, PendingIntent.FLAG_UPDATE_CURRENT);


        dailyUsageAlarmManager.setInexactRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP,
                SystemClock.elapsedRealtime(),
                MakeMeZenUtil.ONE_AND_A_HALF_HOUR_INTERVAL,
                dailyUsagePendingIntent);


        AlarmManager weeklyUsageAlarmManager = (AlarmManager)getContext().getSystemService(Context.ALARM_SERVICE);
        Intent weeklyUsageAlarmIntent = new Intent(getContext(), WeeklyUsageReviewNotifReceiver.class);
        int id8 = (int)System.currentTimeMillis() + 1231;
        PendingIntent weeklyUsagePendingIntent = PendingIntent.getBroadcast(getContext(), 0, weeklyUsageAlarmIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        weeklyUsageAlarmManager.setInexactRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP, SystemClock.elapsedRealtime(),
                MakeMeZenUtil.TWO_HOUR_INTERVAL, weeklyUsagePendingIntent);

    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View appUsageListFragmentView = inflater.inflate(R.layout.fragment_app_usage_list, container, false);

        Calendar c = getTodayStartTimeCal();

        if (dataUpdatedNeeded(System.currentTimeMillis(), DELTA_UPDATE)) {
            setupDataAndView(appUsageListFragmentView, c.getTimeInMillis(), System.currentTimeMillis());
        }

//        setupDataAndView(appUsageListFragmentView, startTimeForData, endTimeForData);
        Amplitude.getInstance().logEvent("Create app usage list");
        return appUsageListFragmentView;
    }

    private Calendar getTodayStartTimeCal() {
        Calendar c = Calendar.getInstance();
        c.set(Calendar.HOUR_OF_DAY, 0);
        c.set(Calendar.MINUTE, 0);
        c.set(Calendar.SECOND, 0);
        c.set(Calendar.MILLISECOND, 0);
        return c;
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    // Get the data from the database

    // Get the data from engine

    // if data is stored for that day -> get it from database
    // else (a) get it from engine (b) store it in the database


    // This holds true for all days except for today.
    // For today -> even if it exists in the database, get it from the database render and then call
    // (a) get it from engine (b) store it in the database

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void setupDataAndView(View appUsageListFragmentView, Long startTimeForData, Long endTimeForData) {

        ArrayList<AppUsageInfo> timeSpentPerApp = new ArrayList<>();

        if (startTimeForData == getTodayStartTimeCal().getTimeInMillis()) {
            updateDataForNotifs(startTimeForData, timeSpentPerApp);
        }

        SharedPreferences sharedPreferences = getActivity().getSharedPreferences(getString(R.string.file_key), Context.MODE_PRIVATE);

        if (dataStored(startTimeForData)) {

            Amplitude.getInstance().logEvent("Pulling existing data.");
            String dataForTheDay = sharedPreferences.getString(MakeMeZenUtil.createKey(startTimeForData), "");
            timeSpentPerApp = MakeMeZenUtil.getAppUsageInfoList(dataForTheDay, getContext());
            setUpTimeSpentRecycleView(appUsageListFragmentView, timeSpentPerApp);

            if (startTimeForData == getTodayStartTimeCal().getTimeInMillis()) {

                Amplitude.getInstance().logEvent("Updating today's data.");

                if (dataUpdatedNeeded(System.currentTimeMillis(), DELTA_UPDATE)) {

                    new UpdateTodayDataUsageTask().execute(startTimeForData, endTimeForData);
                } else {
                    setCalendarViewForToday();
                    updateDataForNotifs(startTimeForData, timeSpentPerApp);
                    kickStartAlarmManager();
                }
            }
            Amplitude.getInstance().logEvent("Updating today's data.");
        } else {

            Amplitude.getInstance().logEvent("Getting fresh data for today.");

            new GetFreshDataTask().execute(startTimeForData, endTimeForData);

            Amplitude.getInstance().logEvent("Got fresh data for today.");

        }

        kickStartAlarmManager();
    }

    // 1 - check if time spent is greater than a milestone:
    //     if so - see if notification has already been sent for the mileston
    //     if not - update the db to say notification has been sent -> since the user has already seen it
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public void updateDataForNotifs(Long startDateInMilli, ArrayList<AppUsageInfo> appUsageInfoTuples) {

        int totalTimeSpent = 0;

        for (AppUsageInfo appUsageInfo : appUsageInfoTuples) {
            totalTimeSpent += appUsageInfo.getTimeSpentInMilliseconds();
        }
        int minsSpent = (int) TimeUnit.MILLISECONDS.toMinutes(totalTimeSpent);
        int hoursSpent = minsSpent / 60;

        if (hoursSpent >= MakeMeZenUtil.MILESTONE_PHONE_USAGE_ONE_TIME &&
                hoursSpent < MakeMeZenUtil.MILESTONE_PHONE_USAGE_TWO_TIME) {
            if (!dailyNotifPhoneUsageSent(startDateInMilli, MakeMeZenUtil.MILESTONE_PHONE_USAGE_ONE)) {
                updatePhoneUsageDataInSharedPreferences(startDateInMilli, MakeMeZenUtil.MILESTONE_PHONE_USAGE_ONE);
            }
        } else if (hoursSpent >= MakeMeZenUtil.MILESTONE_PHONE_USAGE_TWO_TIME &&
                hoursSpent < MakeMeZenUtil.MILESTONE_PHONE_USAGE_THREE_TIME) {
            if (!dailyNotifPhoneUsageSent(startDateInMilli, MakeMeZenUtil.MILESTONE_PHONE_USAGE_TWO )) {
                updatePhoneUsageDataInSharedPreferences(startDateInMilli, MakeMeZenUtil.MILESTONE_PHONE_USAGE_TWO);
            }

        } else if (hoursSpent >= MakeMeZenUtil.MILESTONE_PHONE_USAGE_THREE_TIME &&
                hoursSpent < MakeMeZenUtil.MILESTONE_PHONE_USAGE_FOUR_TIME) {
            if (!dailyNotifPhoneUsageSent(startDateInMilli, MakeMeZenUtil.MILESTONE_PHONE_USAGE_THREE)) {
                updatePhoneUsageDataInSharedPreferences(startDateInMilli, MakeMeZenUtil.MILESTONE_PHONE_USAGE_THREE);
            }
        } else if (hoursSpent >= MakeMeZenUtil.MILESTONE_PHONE_USAGE_FOUR_TIME) {
            if (!dailyNotifPhoneUsageSent(startDateInMilli, MakeMeZenUtil.MILESTONE_PHONE_USAGE_FOUR)) {
                updatePhoneUsageDataInSharedPreferences(startDateInMilli, MakeMeZenUtil.MILESTONE_PHONE_USAGE_FOUR);
            }
        }

        ArrayList<AppUsageInfo> overusedApps = overusedApps(MakeMeZenUtil.APP_OVERUSE_MINUTES, appUsageInfoTuples);

        if (overusedApps.size() > 0) {
            for (AppUsageInfo overUsedApp : overusedApps) {

                int hoursSpentOnApp = (int) (overUsedApp.getTimeSpentInMinutes()/60);
                if (hoursSpentOnApp >= MakeMeZenUtil.MILESTONE_APP_USAGE_ONE_TIME &&
                        hoursSpentOnApp < MakeMeZenUtil.MILESTONE_APP_USAGE_TWO_TIME) {
                    if (!dailyNotifAppUsageSent(startDateInMilli, overUsedApp.getAppName(), MakeMeZenUtil.MILESTONE_APP_USAGE_ONE)) {
                        updateAppUsageDataInSharedPreferences(startDateInMilli, overUsedApp.getAppName(), MakeMeZenUtil.MILESTONE_APP_USAGE_ONE);
                    }
                } else if (hoursSpentOnApp >= MakeMeZenUtil.MILESTONE_APP_USAGE_TWO_TIME &&
                        hoursSpentOnApp < MakeMeZenUtil.MILESTONE_APP_USAGE_THREE_TIME) {
                    if (!dailyNotifAppUsageSent(startDateInMilli, overUsedApp.getAppName(), MakeMeZenUtil.MILESTONE_APP_USAGE_TWO)) {
                        updateAppUsageDataInSharedPreferences(startDateInMilli, overUsedApp.getAppName(), MakeMeZenUtil.MILESTONE_APP_USAGE_TWO);
                    }

                } else if (hoursSpentOnApp >= MakeMeZenUtil.MILESTONE_APP_USAGE_THREE_TIME &&
                        hoursSpentOnApp < MakeMeZenUtil.MILESTONE_APP_USAGE_FOUR_TIME) {
                    if (!dailyNotifAppUsageSent(startDateInMilli, overUsedApp.getAppName(), MakeMeZenUtil.MILESTONE_APP_USAGE_THREE)) {
                        updateAppUsageDataInSharedPreferences(startDateInMilli, overUsedApp.getAppName(), MakeMeZenUtil.MILESTONE_APP_USAGE_THREE);
                    }

                } else if (hoursSpentOnApp >= MakeMeZenUtil.MILESTONE_APP_USAGE_FOUR_TIME) {
                    if (!dailyNotifAppUsageSent(startDateInMilli, overUsedApp.getAppName(), MakeMeZenUtil.MILESTONE_APP_USAGE_FOUR)) {
                        updateAppUsageDataInSharedPreferences(startDateInMilli, overUsedApp.getAppName(), MakeMeZenUtil.MILESTONE_APP_USAGE_FOUR);
                    }
                }

            }
        }

    }

    // Create a method that returns apps that you have spent more than an x mins during the day (till now).
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public ArrayList<AppUsageInfo> overusedApps(Long minMInutes, ArrayList<AppUsageInfo> appUsageInfos) {

        ArrayList<AppUsageInfo> overusedApps = new ArrayList<>();
        for (AppUsageInfo appUsageInfo: appUsageInfos) {
            if (appUsageInfo.getTimeSpentInMinutes() >= minMInutes) {
                overusedApps.add(appUsageInfo);
            }
        }
        return overusedApps;
    }

    private String getDateFromMilliSeconds(Long milliSeconds) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(milliSeconds);

        int mYear = calendar.get(Calendar.YEAR);
        int mMonth = calendar.get(Calendar.MONTH);
        int mDay = calendar.get(Calendar.DAY_OF_MONTH);

        return mMonth + "/" + mDay + "/" + mYear;
    }

    private void updateAppUsageDataInSharedPreferences(long timeInMillis, String appName, String milestone) {
        SharedPreferences sharedPreferences = getContext().getSharedPreferences(getContext().getString(R.string.file_key), Context.MODE_PRIVATE);
        String key = MakeMeZenUtil.createDailyAppUsageKey(timeInMillis);
        String dataForTheDay = sharedPreferences.getString(key, "");
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(key, dataForTheDay + MakeMeZenUtil.DIVIDER + MakeMeZenUtil.getAppUsageValue(appName, milestone));
        editor.apply();
    }

    private boolean dailyNotifAppUsageSent(long todayInMilli, String appName, String milestone) {
        SharedPreferences sharedPreferences = getContext().getSharedPreferences(getContext().getString(R.string.file_key), Context.MODE_PRIVATE);
        String appUsageMilestones = sharedPreferences.getString(MakeMeZenUtil.createDailyAppUsageKey(todayInMilli), "");
        String expectedValue = MakeMeZenUtil.getAppUsageValue(appName, milestone);
        if (!appUsageMilestones.equals("") && appUsageMilestones.contains(expectedValue)) {
            return true;
        }
        return false;
    }

    private boolean dailyNotifPhoneUsageSent(long startDateInMillis, String milestone) {
        SharedPreferences sharedPreferences = getContext().getSharedPreferences(getContext().getString(R.string.file_key), Context.MODE_PRIVATE);
        String dataForTheDay = sharedPreferences.getString(MakeMeZenUtil.createDailyPhoneUsageKey(startDateInMillis), "");
        if (!dataForTheDay.equals("") && dataForTheDay.contains(milestone)) {
            return true;
        }
        return false;
    }

    private void updatePhoneUsageDataInSharedPreferences(long timeInMillis, String milestone) {
        SharedPreferences sharedPreferences = getContext().getSharedPreferences(getContext().getString(R.string.file_key), Context.MODE_PRIVATE);
        String key = MakeMeZenUtil.createDailyPhoneUsageKey(timeInMillis);
        String dataForTheDay = sharedPreferences.getString(key, "");
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(key, dataForTheDay + MakeMeZenUtil.DIVIDER + milestone);
        editor.apply();
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void setUpTimeSpentRecycleView(View appUsageListFragmentView, ArrayList<AppUsageInfo> timeSpentPerApp) {

        RecyclerView recyclerView = (RecyclerView) appUsageListFragmentView.findViewById(R.id.app_usage_data_recycler_view);
        recyclerView.setHasFixedSize(true);

        layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);

        DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
        int pxWidth = (int)(displayMetrics.widthPixels * .5);
        float dpWidth = (float)((displayMetrics.widthPixels) / displayMetrics.density * .51);

        adapter = new AppUsageAdapter(timeSpentPerApp, getResources(), (int)pxWidth);
        recyclerView.setAdapter(adapter);
        adapter.notifyDataSetChanged();


    }

    private boolean dataStored(Long startTime) {
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences(getString(R.string.file_key), Context.MODE_PRIVATE);

        if (startTime != getTodayStartTimeCal().getTimeInMillis()) {
            String lastUpdateDataInMilli = sharedPreferences.getString(MakeMeZenUtil.lastUpdateKey(startTime), "");
            if (lastUpdateDataInMilli.equals("")) {
                return false;
            } else {
                Long lastUpdateDateForStartTimeInMilli = Long.parseLong(lastUpdateDataInMilli);
                Long lastUpdateDataInHours = TimeUnit.MILLISECONDS.toHours(lastUpdateDateForStartTimeInMilli);
                Long startTimeInHours = TimeUnit.MILLISECONDS.toHours(startTime);
                if (lastUpdateDataInHours - startTimeInHours < 24) {
                    return false;
                }
            }

        }

        String dataForTheDay = sharedPreferences.getString(MakeMeZenUtil.createKey(startTime), "");
        if (!dataForTheDay.equals("")) {
            return true;
        }
        return false;
    }

    private boolean dataUpdatedNeeded(Long currentTimeInMilli, Long delta) {
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences(getString(R.string.file_key), Context.MODE_PRIVATE);
        Long lastSavedTimeForTodaysData = Long.parseLong(sharedPreferences.getString(MakeMeZenUtil.lastUpdateKey(getTodayStartTimeCal().getTimeInMillis()), "0"));

        if (lastSavedTimeForTodaysData != 0) {
            Long currentTimeInMins = TimeUnit.MILLISECONDS.toMinutes(currentTimeInMilli);
            Long lastSavedTime = TimeUnit.MILLISECONDS.toMinutes(lastSavedTimeForTodaysData);
            if (currentTimeInMins - lastSavedTime > delta) {
                return true;
            } else {
                return false;
            }
        }

        return true;
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public void updateData(Long startTime, Long endTime) {
        setupDataAndView(getView(), startTime, endTime);
    }

    private class GetFreshDataTask extends AsyncTask<Long, Void, ArrayList<AppUsageInfo>> {

        @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
        @Override
        protected ArrayList<AppUsageInfo> doInBackground(Long... timings) {
            SharedPreferences sharedPreferences = getActivity().getSharedPreferences(getString(R.string.file_key), Context.MODE_PRIVATE);
            TimeSpentEngine timeSpentEngine = new TimeSpentEngine(getContext());
            Long startTimeInMilli = timings[0];
            Long endTimeInMilli = timings[1];

            ArrayList<AppUsageInfo> appUsageInfos = timeSpentEngine.getTimeSpent(startTimeInMilli, endTimeInMilli);

            if (!appUsageInfos.isEmpty()) {

                String appUsageInfoObjectsString = MakeMeZenUtil.getAppUsageInfoObjectsString(appUsageInfos);
                String key = MakeMeZenUtil.createKey(startTimeInMilli);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString(key, appUsageInfoObjectsString);

                editor.putString(MakeMeZenUtil.lastUpdateKey(startTimeInMilli), System.currentTimeMillis() + "");

                editor.commit();
            }
            updateDataForNotifs(startTimeInMilli, appUsageInfos);
            kickStartAlarmManager();
            return appUsageInfos;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            View appUsageListFragmentView = getFragmentManager().findFragmentById(R.id.app_usage_list_fragment).getView();
            if (appUsageListFragmentView != null) {
                appUsageListFragmentView.findViewById(R.id.progressbar).setVisibility(View.VISIBLE);
            }
        }

        @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
        @Override
        protected void onPostExecute(ArrayList<AppUsageInfo> appUsageInfos) {
            super.onPostExecute(appUsageInfos);

            View appUsageListFragmentView = getFragmentManager().findFragmentById(R.id.app_usage_list_fragment).getView();

            if (appUsageListFragmentView != null) {
                setUpTimeSpentRecycleView(appUsageListFragmentView, appUsageInfos);
                appUsageListFragmentView.findViewById(R.id.progressbar).setVisibility(View.INVISIBLE);
            }

        }
    }

    private class UpdateTodayDataUsageTask extends AsyncTask<Long, Void, ArrayList<AppUsageInfo>> {

        @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
        @Override
        protected ArrayList<AppUsageInfo> doInBackground(Long... timings) {
            SharedPreferences sharedPreferences = getActivity().getSharedPreferences(getString(R.string.file_key), Context.MODE_PRIVATE);
            ArrayList<AppUsageInfo> timeSpentPerApp = new ArrayList<>();
            Long startTimeForData = timings[0];
            Long endTimeForData = timings[1];

            TimeSpentEngine timeSpentEngine = new TimeSpentEngine(getContext());
            timeSpentPerApp = timeSpentEngine.getTimeSpent(startTimeForData, endTimeForData);

            String appUsageInfoObjectsString = MakeMeZenUtil.getAppUsageInfoObjectsString(timeSpentPerApp);
            String key = MakeMeZenUtil.createKey(startTimeForData);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString(key, appUsageInfoObjectsString);

            if (startTimeForData == getTodayStartTimeCal().getTimeInMillis()) {
                editor.putString(MakeMeZenUtil.lastUpdateKey(startTimeForData), endTimeForData + "");
            }

            editor.apply();

            updateDataForNotifs(startTimeForData, timeSpentPerApp);
            kickStartAlarmManager();

            return timeSpentPerApp;
        }

        @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
        @Override
        protected void onPostExecute(ArrayList<AppUsageInfo> appUsageInfos) {
            super.onPostExecute(appUsageInfos);
            if (getView() != null && getFragmentManager() != null && getResources() != null) {

                setCalendarViewForToday();
                setUpTimeSpentRecycleView(getView(), appUsageInfos);

//                Drawable seventhDayBackground = getFragmentManager().findFragmentById(R.id.calendar_view_fragment_container).getView().findViewById(R.id.seventh_day).getBackground();
//
//                //TODO: HORRIBLE WAY OF WRITING CODE, PLEASE TRY TO FIND ANOTHER ALTERNATIVE HERE
//                if (R.drawable.calendar_curved_orange == getResources().getIdentifier("calendar_curved_orange", "drawable", getActivity().getPackageName())) {
//                    setUpTimeSpentRecycleView(getView(), appUsageInfos);
//                }
//                if (seventhDayBackground == getResources().getDrawable(R.drawable.calendar_curved_orange, null)) {
//                    setUpTimeSpentRecycleView(getView(), appUsageInfos);
//                }

            }

        }
    }

    private void setCalendarViewForToday() {
        getFragmentManager().findFragmentById(R.id.calendar_view_fragment_container).getView().findViewById(R.id.seventh_day).setBackground(ContextCompat.getDrawable(getContext(), R.drawable.calendar_curved_orange));
        getFragmentManager().findFragmentById(R.id.calendar_view_fragment_container).getView().findViewById(R.id.first_day).setBackground(ContextCompat.getDrawable(getContext(), R.drawable.calendar_curved_shape));
        getFragmentManager().findFragmentById(R.id.calendar_view_fragment_container).getView().findViewById(R.id.second_day).setBackground(ContextCompat.getDrawable(getContext(), R.drawable.calendar_curved_shape));
        getFragmentManager().findFragmentById(R.id.calendar_view_fragment_container).getView().findViewById(R.id.third_day).setBackground(ContextCompat.getDrawable(getContext(), R.drawable.calendar_curved_shape));
        getFragmentManager().findFragmentById(R.id.calendar_view_fragment_container).getView().findViewById(R.id.fourth_day).setBackground(ContextCompat.getDrawable(getContext(), R.drawable.calendar_curved_shape));
        getFragmentManager().findFragmentById(R.id.calendar_view_fragment_container).getView().findViewById(R.id.fifth_day).setBackground(ContextCompat.getDrawable(getContext(), R.drawable.calendar_curved_shape));
        getFragmentManager().findFragmentById(R.id.calendar_view_fragment_container).getView().findViewById(R.id.sixth_day).setBackground(ContextCompat.getDrawable(getContext(), R.drawable.calendar_curved_shape));
        TextView calendarDay = getFragmentManager().findFragmentById(R.id.calendar_view_fragment_container).getView().findViewById(R.id.calendar_day);
        calendarDay.setText("Today");
    }
}
