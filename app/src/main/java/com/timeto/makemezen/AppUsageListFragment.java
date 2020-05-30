package com.timeto.makemezen;

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

import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

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
        kickStartAlarmManager();
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
//        testNotify();
    }


    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onResume() {
        super.onResume();
        Amplitude.getInstance().logEvent("Resume app usage list");

        Calendar c = getTodayStartTimeCal();

        Drawable currentDayBackground = getFragmentManager().findFragmentById(R.id.calendar_view_fragment_container).getView().findViewById(R.id.seventh_day).getBackground();

        setupDataAndView(getView(), c.getTimeInMillis(), System.currentTimeMillis());
//        }

//        if (this.getArguments() == null) {
//            if (lastUpdateTimeForTodaysDataGreaterThan(System.currentTimeMillis(), DELTA_UPDATE)) {
//                setupDataAndView(getView(), c.getTimeInMillis(), System.currentTimeMillis());
//            }
//        } else {
//            Long startTimeForData = this.getArguments().getLong(MakeMeZenUtil.START_DATE_MILLISECONDS);
//
//            if (startTimeForData == c.getTimeInMillis() && lastUpdateTimeForTodaysDataGreaterThan(System.currentTimeMillis(), DELTA_UPDATE)) {
//                setupDataAndView(getView(), c.getTimeInMillis(), System.currentTimeMillis());
//            }
//        }

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

        // Set the alarm to start at approximately 2:00 p.m.
        Calendar notifWeekday1 = Calendar.getInstance();
        notifWeekday1.setTimeInMillis(System.currentTimeMillis());
        notifWeekday1.set(Calendar.HOUR_OF_DAY, 9);
        notifWeekday1.set(Calendar.MINUTE, 00);

        AlarmManager alarmManager1 = (AlarmManager)getContext().getSystemService(Context.ALARM_SERVICE);
        Intent intent1 = new Intent(getContext(), MorningMotivationNotifReceiver.class);
        int id1 = (int)System.currentTimeMillis() + 9;
        PendingIntent alarmIntent1 = PendingIntent.getBroadcast(getContext(), id1 , intent1, PendingIntent.FLAG_UPDATE_CURRENT);

        alarmManager1.setInexactRepeating(AlarmManager.RTC_WAKEUP, notifWeekday1.getTimeInMillis(),
                AlarmManager.INTERVAL_DAY, alarmIntent1);

        Calendar notifWeekday2 = Calendar.getInstance();
        notifWeekday2.setTimeInMillis(System.currentTimeMillis());
        notifWeekday2.set(Calendar.HOUR_OF_DAY, 11);
        notifWeekday2.set(Calendar.MINUTE, 00);

        AlarmManager alarmManager2 = (AlarmManager)getContext().getSystemService(Context.ALARM_SERVICE);
        Intent intent2 = new Intent(getContext(), DailyAppUsageReviewNotifReceiver.class);
        int id2 = (int)System.currentTimeMillis() + 11;
        PendingIntent alarmIntent2 = PendingIntent.getBroadcast(getContext(), id2, intent2, PendingIntent.FLAG_UPDATE_CURRENT);
        alarmManager2.setInexactRepeating(AlarmManager.RTC_WAKEUP, notifWeekday2.getTimeInMillis(),
                AlarmManager.INTERVAL_DAY, alarmIntent2);

        Calendar notifWeekday3 = Calendar.getInstance();
        notifWeekday3.setTimeInMillis(System.currentTimeMillis());
        notifWeekday3.set(Calendar.HOUR_OF_DAY, 13);
        notifWeekday3.set(Calendar.MINUTE, 00);

        AlarmManager alarmManager3 = (AlarmManager)getContext().getSystemService(Context.ALARM_SERVICE);
        Intent intent3 = new Intent(getContext(), DailyAppUsageReviewNotifReceiver.class);
        int id3 = (int)System.currentTimeMillis() + 13;
        PendingIntent alarmIntent3 = PendingIntent.getBroadcast(getContext(), id3, intent3, PendingIntent.FLAG_UPDATE_CURRENT);
        alarmManager3.setInexactRepeating(AlarmManager.RTC_WAKEUP, notifWeekday3.getTimeInMillis(),
                AlarmManager.INTERVAL_DAY, alarmIntent3);

        Calendar notifWeekday4 = Calendar.getInstance();
        notifWeekday4.setTimeInMillis(System.currentTimeMillis());
        notifWeekday4.set(Calendar.HOUR_OF_DAY, 15);
        notifWeekday4.set(Calendar.MINUTE, 00);

        AlarmManager alarmManager4 = (AlarmManager)getContext().getSystemService(Context.ALARM_SERVICE);
        Intent intent4 = new Intent(getContext(), DailyAppUsageReviewNotifReceiver.class);
        int id4 = (int)System.currentTimeMillis() + 15;
        PendingIntent alarmIntent4 = PendingIntent.getBroadcast(getContext(), id4, intent4, PendingIntent.FLAG_UPDATE_CURRENT);
        alarmManager4.setInexactRepeating(AlarmManager.RTC_WAKEUP, notifWeekday4.getTimeInMillis(),
                AlarmManager.INTERVAL_DAY, alarmIntent4);

        Calendar notifWeekday5 = Calendar.getInstance();
        notifWeekday5.setTimeInMillis(System.currentTimeMillis());
        notifWeekday5.set(Calendar.HOUR_OF_DAY, 17);
        notifWeekday5.set(Calendar.MINUTE, 00);

        AlarmManager alarmManager5 = (AlarmManager)getContext().getSystemService(Context.ALARM_SERVICE);
        Intent intent5 = new Intent(getContext(), DailyAppUsageReviewNotifReceiver.class);
        int id5 = (int)System.currentTimeMillis() + 17;
        PendingIntent alarmIntent5 = PendingIntent.getBroadcast(getContext(), id5, intent5, PendingIntent.FLAG_UPDATE_CURRENT);
        alarmManager5.setInexactRepeating(AlarmManager.RTC_WAKEUP, notifWeekday5.getTimeInMillis(),
                AlarmManager.INTERVAL_DAY, alarmIntent5);
//        alarmManager5.set

        Calendar notifWeekday6 = Calendar.getInstance();
        notifWeekday6.setTimeInMillis(System.currentTimeMillis());
        notifWeekday6.set(Calendar.HOUR_OF_DAY, 19);
        notifWeekday6.set(Calendar.MINUTE, 00);

        AlarmManager alarmManager6 = (AlarmManager)getContext().getSystemService(Context.ALARM_SERVICE);
        Intent intent6 = new Intent(getContext(), DailyAppUsageReviewNotifReceiver.class);
        int id6 = (int)System.currentTimeMillis() + 19;
        PendingIntent alarmIntent6 = PendingIntent.getBroadcast(getContext(), id6, intent6, PendingIntent.FLAG_UPDATE_CURRENT);
        alarmManager6.setInexactRepeating(AlarmManager.RTC_WAKEUP, notifWeekday6.getTimeInMillis(),
                AlarmManager.INTERVAL_DAY, alarmIntent6);

        Calendar notifWeekday7 = Calendar.getInstance();
        notifWeekday7.setTimeInMillis(System.currentTimeMillis());
        notifWeekday7.set(Calendar.HOUR_OF_DAY, 21);
        notifWeekday7.set(Calendar.MINUTE, 00);

        AlarmManager alarmManager7 = (AlarmManager)getContext().getSystemService(Context.ALARM_SERVICE);
        Intent intent7 = new Intent(getContext(), DailyAppUsageReviewNotifReceiver.class);
        int id7 = (int)System.currentTimeMillis() + 21;
        PendingIntent alarmIntent7 = PendingIntent.getBroadcast(getContext(), id7, intent7, PendingIntent.FLAG_UPDATE_CURRENT);
        alarmManager7.setInexactRepeating(AlarmManager.RTC_WAKEUP, notifWeekday7.getTimeInMillis(),
                AlarmManager.INTERVAL_DAY, alarmIntent7);

        Calendar notifWeekend = Calendar.getInstance();
        notifWeekend.setTimeInMillis(System.currentTimeMillis());
        notifWeekend.set(Calendar.HOUR_OF_DAY, 13);
        notifWeekend.set(Calendar.MINUTE, 00);

        AlarmManager alarmManager8 = (AlarmManager)getContext().getSystemService(Context.ALARM_SERVICE);
        Intent intent8 = new Intent(getContext(), WeeklyUsageReviewNotifReceiver.class);
        int id8 = (int)System.currentTimeMillis() + 1231;
        PendingIntent alarmIntent8 = PendingIntent.getBroadcast(getContext(), id8, intent8, PendingIntent.FLAG_UPDATE_CURRENT);
        alarmManager8.setInexactRepeating(AlarmManager.RTC_WAKEUP, notifWeekend.getTimeInMillis(),
                AlarmManager.INTERVAL_DAY, alarmIntent8);


    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View appUsageListFragmentView = inflater.inflate(R.layout.fragment_app_usage_list, container, false);

        Long startTimeForData = 0L;
        Long endTimeForData = 0L;

//        if (this.getArguments() != null) {
//            startTimeForData = this.getArguments().getLong(MakeMeZenUtil.START_DATE_MILLISECONDS);
//            endTimeForData = this.getArguments().getLong(MakeMeZenUtil.END_DATE_MILLISECONDS);
//
//        } else {
//            Calendar c = getTodayStartTimeCal();
//
//            startTimeForData = c.getTimeInMillis();
//            endTimeForData = System.currentTimeMillis();
//        }

        Calendar c = getTodayStartTimeCal();

        startTimeForData = c.getTimeInMillis();
        endTimeForData = System.currentTimeMillis();

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

        SharedPreferences sharedPreferences = getActivity().getSharedPreferences(getString(R.string.file_key), Context.MODE_PRIVATE);

        if (dataStored(startTimeForData)) {
            String dataForTheDay = sharedPreferences.getString(MakeMeZenUtil.createKey(startTimeForData), "");
            timeSpentPerApp = MakeMeZenUtil.getAppUsageInfoList(dataForTheDay, getContext());
            setUpTimeSpentRecycleView(appUsageListFragmentView, timeSpentPerApp);

            if (startTimeForData == getTodayStartTimeCal().getTimeInMillis() &&
                    dataUpdatedNeeded(System.currentTimeMillis(), DELTA_UPDATE)) {

                new UpdateTodayDataUsageTask().execute(startTimeForData, endTimeForData);
//                TimeSpentEngine timeSpentEngine = new TimeSpentEngine(getContext());
//                timeSpentPerApp = timeSpentEngine.getTimeSpent(startTimeForData, endTimeForData);
//
//                String appUsageInfoObjectsString = MakeMeZenUtil.getAppUsageInfoObjectsString(timeSpentPerApp);
//                String key = MakeMeZenUtil.createKey(startTimeForData);
//                SharedPreferences.Editor editor = sharedPreferences.edit();
//                editor.putString(key, appUsageInfoObjectsString);
//                editor.apply();
//
//                setUpTimeSpentRecycleView(appUsageListFragmentView, timeSpentPerApp);
            }
        } else {

            TimeSpentEngine timeSpentEngine = new TimeSpentEngine(getContext());
            timeSpentPerApp = timeSpentEngine.getTimeSpent(startTimeForData, endTimeForData);

            if (!timeSpentPerApp.isEmpty()) {

                String appUsageInfoObjectsString = MakeMeZenUtil.getAppUsageInfoObjectsString(timeSpentPerApp);
                String key = MakeMeZenUtil.createKey(startTimeForData);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString(key, appUsageInfoObjectsString);

                editor.putString(MakeMeZenUtil.lastUpdateKey(startTimeForData), System.currentTimeMillis() + "");

                editor.commit();
            }

            if (startTimeForData == getTodayStartTimeCal().getTimeInMillis()) {
                updateDataForNotifs(startTimeForData, timeSpentPerApp);
            }

            setUpTimeSpentRecycleView(appUsageListFragmentView, timeSpentPerApp);
        }

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
                return;
            }
        } else if (hoursSpent >= MakeMeZenUtil.MILESTONE_PHONE_USAGE_TWO_TIME &&
                hoursSpent < MakeMeZenUtil.MILESTONE_PHONE_USAGE_THREE_TIME) {
            if (!dailyNotifPhoneUsageSent(startDateInMilli, MakeMeZenUtil.MILESTONE_PHONE_USAGE_TWO )) {
                updatePhoneUsageDataInSharedPreferences(startDateInMilli, MakeMeZenUtil.MILESTONE_PHONE_USAGE_TWO);
                return;
            }

        } else if (hoursSpent >= MakeMeZenUtil.MILESTONE_PHONE_USAGE_THREE_TIME &&
                hoursSpent < MakeMeZenUtil.MILESTONE_PHONE_USAGE_FOUR_TIME) {
            if (!dailyNotifPhoneUsageSent(startDateInMilli, MakeMeZenUtil.MILESTONE_PHONE_USAGE_THREE)) {
                updatePhoneUsageDataInSharedPreferences(startDateInMilli, MakeMeZenUtil.MILESTONE_PHONE_USAGE_THREE);
                return;
            }
        } else if (hoursSpent >= MakeMeZenUtil.MILESTONE_PHONE_USAGE_FOUR_TIME) {
            if (!dailyNotifPhoneUsageSent(startDateInMilli, MakeMeZenUtil.MILESTONE_PHONE_USAGE_FOUR)) {
                updatePhoneUsageDataInSharedPreferences(startDateInMilli, MakeMeZenUtil.MILESTONE_PHONE_USAGE_FOUR);
                return;
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
                        return;
                    }
                } else if (hoursSpentOnApp >= MakeMeZenUtil.MILESTONE_APP_USAGE_TWO_TIME &&
                        hoursSpentOnApp < MakeMeZenUtil.MILESTONE_APP_USAGE_THREE_TIME) {
                    if (!dailyNotifAppUsageSent(startDateInMilli, overUsedApp.getAppName(), MakeMeZenUtil.MILESTONE_APP_USAGE_TWO)) {
                        updateAppUsageDataInSharedPreferences(startDateInMilli, overUsedApp.getAppName(), MakeMeZenUtil.MILESTONE_APP_USAGE_TWO);
                        return;
                    }

                } else if (hoursSpentOnApp >= MakeMeZenUtil.MILESTONE_APP_USAGE_THREE_TIME &&
                        hoursSpentOnApp < MakeMeZenUtil.MILESTONE_APP_USAGE_FOUR_TIME) {
                    if (!dailyNotifAppUsageSent(startDateInMilli, overUsedApp.getAppName(), MakeMeZenUtil.MILESTONE_APP_USAGE_THREE)) {
                        updateAppUsageDataInSharedPreferences(startDateInMilli, overUsedApp.getAppName(), MakeMeZenUtil.MILESTONE_APP_USAGE_THREE);
                        return;
                    }

                } else if (hoursSpentOnApp >= MakeMeZenUtil.MILESTONE_APP_USAGE_FOUR_TIME) {
                    if (!dailyNotifAppUsageSent(startDateInMilli, overUsedApp.getAppName(), MakeMeZenUtil.MILESTONE_APP_USAGE_FOUR)) {
                        updateAppUsageDataInSharedPreferences(startDateInMilli, overUsedApp.getAppName(), MakeMeZenUtil.MILESTONE_APP_USAGE_FOUR);
                        return;
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
    public void testUpdateData(Long startTime, Long endTime) {
        setupDataAndView(getView(), startTime, endTime);
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

            return timeSpentPerApp;
        }

        @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
        @Override
        protected void onPostExecute(ArrayList<AppUsageInfo> appUsageInfos) {
            super.onPostExecute(appUsageInfos);
            if (getView() != null && getFragmentManager() != null && getResources() != null) {

                getFragmentManager().findFragmentById(R.id.calendar_view_fragment_container).getView().findViewById(R.id.seventh_day).setBackground(ContextCompat.getDrawable(getContext(), R.drawable.calendar_curved_orange));
                getFragmentManager().findFragmentById(R.id.calendar_view_fragment_container).getView().findViewById(R.id.first_day).setBackground(ContextCompat.getDrawable(getContext(), R.drawable.calendar_curved_shape));
                getFragmentManager().findFragmentById(R.id.calendar_view_fragment_container).getView().findViewById(R.id.second_day).setBackground(ContextCompat.getDrawable(getContext(), R.drawable.calendar_curved_shape));
                getFragmentManager().findFragmentById(R.id.calendar_view_fragment_container).getView().findViewById(R.id.third_day).setBackground(ContextCompat.getDrawable(getContext(), R.drawable.calendar_curved_shape));
                getFragmentManager().findFragmentById(R.id.calendar_view_fragment_container).getView().findViewById(R.id.fourth_day).setBackground(ContextCompat.getDrawable(getContext(), R.drawable.calendar_curved_shape));
                getFragmentManager().findFragmentById(R.id.calendar_view_fragment_container).getView().findViewById(R.id.fifth_day).setBackground(ContextCompat.getDrawable(getContext(), R.drawable.calendar_curved_shape));
                getFragmentManager().findFragmentById(R.id.calendar_view_fragment_container).getView().findViewById(R.id.sixth_day).setBackground(ContextCompat.getDrawable(getContext(), R.drawable.calendar_curved_shape));
                setUpTimeSpentRecycleView(getView(), appUsageInfos);

                updateDataForNotifs(getTodayStartTimeCal().getTimeInMillis(), appUsageInfos);
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
}
