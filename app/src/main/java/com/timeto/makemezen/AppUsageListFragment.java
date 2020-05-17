package com.timeto.makemezen;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.DisplayMetrics;
import android.util.Log;
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

    private AlarmManager alarmManager;
    private PendingIntent alarmIntent;

    public AppUsageListFragment() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
//        kickStartAlarmManager();
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

        if (lastUpdateTimeForTodaysDataGreaterThan(System.currentTimeMillis(), DELTA_UPDATE)) {
            setupDataAndView(getView(), c.getTimeInMillis(), System.currentTimeMillis());
        }

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

        if (lastUpdateTimeForTodaysDataGreaterThan(System.currentTimeMillis(), DELTA_UPDATE)) {
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
                    lastUpdateTimeForTodaysDataGreaterThan(System.currentTimeMillis(), DELTA_UPDATE)) {

                new UpdateTodaysDataUsageTask().execute(startTimeForData, endTimeForData);
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

            String appUsageInfoObjectsString = MakeMeZenUtil.getAppUsageInfoObjectsString(timeSpentPerApp);
            String key = MakeMeZenUtil.createKey(startTimeForData);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString(key, appUsageInfoObjectsString);


            if (startTimeForData == getTodayStartTimeCal().getTimeInMillis()) {
                editor.putString(MakeMeZenUtil.lastUpdateKey(), MakeMeZenUtil.lastUpdateTodayData(endTimeForData));
            }

            editor.commit();

            setUpTimeSpentRecycleView(appUsageListFragmentView, timeSpentPerApp);
        }

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
        String dataForTheDay = sharedPreferences.getString(MakeMeZenUtil.createKey(startTime), "");
        if (!dataForTheDay.equals("")) {
            return true;
        }
        return false;
    }

    private boolean lastUpdateTimeForTodaysDataGreaterThan(Long currentTimeInMilli, Long delta) {
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences(getString(R.string.file_key), Context.MODE_PRIVATE);
        Long lastSavedTimeForTodaysData = Long.parseLong(sharedPreferences.getString(MakeMeZenUtil.lastUpdateKey(), "0"));

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

    private class UpdateTodaysDataUsageTask extends AsyncTask<Long, Void, ArrayList<AppUsageInfo>> {

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
                editor.putString(MakeMeZenUtil.lastUpdateKey(), MakeMeZenUtil.lastUpdateTodayData(endTimeForData));
            }

            editor.apply();

            return timeSpentPerApp;
        }

        @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
        @Override
        protected void onPostExecute(ArrayList<AppUsageInfo> appUsageInfos) {
            super.onPostExecute(appUsageInfos);
            if (getView() != null) {
                setUpTimeSpentRecycleView(getView(), appUsageInfos);
            }

        }
    }
}
