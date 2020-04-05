package com.example.myapplication;

import android.app.AppOpsManager;
import android.app.usage.UsageEvents;
import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import java.sql.Date;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;


/**
 * A simple {@link Fragment} subclass.
 * Use the  factory method to
 * create an instance of this fragment.
 */
public class EnableAccessFragment extends Fragment {

    private static final String TAG = EnableAccessFragment.class.getSimpleName();

    public EnableAccessFragment() {
        // Required empty public constructor
    }


    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ((AppCompatActivity) getActivity()).getSupportActionBar().hide();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View fragmentEnableAccessView = inflater.inflate(R.layout.fragment_enable_access, container, false);

        initializeComponents(fragmentEnableAccessView);

        return fragmentEnableAccessView;
    }

    private void initializeComponents(View fragmentEnableAccessView) {

        // Initialize the components
        final Button enableUsageButton = fragmentEnableAccessView.findViewById(R.id.enable_usage_button);
        View.OnClickListener buttonListener = new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void onClick(View v) {
                if (!accessAllowed()) {
                    Intent intent = new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS);
                    startActivity(intent);
                } else {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        printAppUsage();

                    }
                }
            }
        };
        enableUsageButton.setOnClickListener(buttonListener);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void printAppUsage() {
        Log.i(TAG, "Usage access success!");

        // set up usage stats manager

        // today
        Calendar cal = new GregorianCalendar();
        // reset hour, minutes, seconds and millis
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);


        UsageStatsManager usageStatsManager = (UsageStatsManager) getActivity().getSystemService(Context.USAGE_STATS_SERVICE);


        getTimeWithUsageEvents(cal, usageStatsManager);

    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void getTimeWithUsageEvents(Calendar cal, UsageStatsManager usageStatsManager) {

//        UsageEvents usageEvents = usageStatsManager.queryEvents(cal.getTimeInMillis(), System.currentTimeMillis());
        UsageEvents usageEvents = usageStatsManager.queryEvents(1585638000000L, 1585724399000L);
        PackageManager packageManager = getActivity().getPackageManager();

//        List<ApplicationInfo> installedApplications = packageManager.getInstalledApplications(PackageManager.GET_META_DATA);
        final PackageManager pm = getActivity().getPackageManager();
        Intent intent = new Intent(Intent.ACTION_MAIN, null);
        intent.addCategory(Intent.CATEGORY_LAUNCHER);

        ArrayList<String> appsOnDevice = new ArrayList<>();

        List<ResolveInfo> installedApplications = pm.queryIntentActivities(intent, PackageManager.GET_META_DATA);

        for (ResolveInfo appInfo: installedApplications) {
            appsOnDevice.add(appInfo.loadLabel(packageManager).toString());
//            Log.i(TAG, "Installed app: " + appInfo.loadLabel(packageManager).toString());
        }

        HashMap<String, Integer> numberOfOpens = new HashMap<>();

        HashMap<String, ArrayList<UsageEvents.Event>> usageInfoEventsPerApp = new HashMap<String, ArrayList<UsageEvents.Event>>();

        ArrayList<EventInfo> allUsageEventsTemp = new ArrayList<>();

        while (usageEvents.hasNextEvent()) {
            UsageEvents.Event currentEvent = new UsageEvents.Event();
            usageEvents.getNextEvent(currentEvent);

            if(currentEvent.getEventType() == UsageEvents.Event.MOVE_TO_FOREGROUND ||
                    currentEvent.getEventType() == UsageEvents.Event.MOVE_TO_BACKGROUND) {

                try {
                    ApplicationInfo applicationInfo = packageManager.getApplicationInfo(currentEvent.getPackageName(), 0);
                    String appName = applicationInfo.loadLabel(packageManager).toString();

//                    numberOfOpens = iterateNumberOfOpensPerApp(numberOfOpens, currentEvent, appName);
//                    usageInfoEventsPerApp = organizeUsageEvents(usageInfoEventsPerApp, currentEvent, appName);

                    Log.i(TAG, "App Name: " + appName +
                            " Event type: " + currentEvent.getEventType() +
                            " Time: " + dateAndTime(currentEvent.getTimeStamp()));

                    if (appsOnDevice.contains(appName)) {
                        ArrayList<UsageEvents.Event> appRelatedEvents = new ArrayList<>();
                        if (usageInfoEventsPerApp.keySet().contains(appName)) {
                            appRelatedEvents = usageInfoEventsPerApp.get(appName);
                        }
                        appRelatedEvents.add(currentEvent);
                        usageInfoEventsPerApp.put(appName, appRelatedEvents);


                        allUsageEventsTemp.add(new EventInfo(currentEvent, appName));
                    }

                } catch (PackageManager.NameNotFoundException e) {
                    e.printStackTrace();
                }

            }

        }


        HashMap<String, Long> timeSpentPerApp = new HashMap<>();

        for (String app : usageInfoEventsPerApp.keySet()) {
            long timeSpent = 0;
            ArrayList<UsageEvents.Event> appEvents = usageInfoEventsPerApp.get(app);

            int i = 0;
            while (i < appEvents.size() - 1) {
                UsageEvents.Event firstEvent = appEvents.get(i);
                UsageEvents.Event secondEvent = appEvents.get(i + 1);
                if (firstEvent.getEventType() == UsageEvents.Event.MOVE_TO_FOREGROUND &&
                        secondEvent.getEventType() == UsageEvents.Event.MOVE_TO_BACKGROUND) {
                    timeSpent += secondEvent.getTimeStamp() - firstEvent.getTimeStamp();
                    Log.i(TAG, "App name: " + app +
                            " first event: " + firstEvent.getEventType() +
                            " first event time: " + dateAndTime(firstEvent.getTimeStamp()) +
                            " second event: " + secondEvent.getEventType() +
                            " second event time: " + dateAndTime(secondEvent.getTimeStamp()) +
                            " Time Spent Calculation: " + TimeUnit.MILLISECONDS.toMinutes(timeSpent));
                    i = i + 2;
                } else {
                    i = i + 1;
                }
            }

            timeSpentPerApp.put(app, timeSpent);
        }

        for (String appName : timeSpentPerApp.keySet()) {
            Log.i(TAG, "App name: " + appName + " Time spent: " + TimeUnit.MILLISECONDS.toMinutes(timeSpentPerApp.get(appName)));
        }


    }


    private class EventInfo {

        private UsageEvents.Event event;
        private String appName;

        public EventInfo(UsageEvents.Event event, String appName){
            this.event = event;
            this.appName = appName;
        }

        public UsageEvents.Event getEvent() {
            return event;
        }

        public String getAppName() {
            return appName;
        }
    }

    private class ForegroundSession {
        private UsageEvents.Event foregroundEvent;
        private UsageEvents.Event backgroundEvent;

        public ForegroundSession() {
            foregroundEvent = null;
            backgroundEvent = null;
        }

        public void setBackgroundEvent(UsageEvents.Event backgroundEvent) {
            this.backgroundEvent = backgroundEvent;
        }

        public void setForegroundEvent(UsageEvents.Event foregroundEvent) {
            this.foregroundEvent = foregroundEvent;
        }

        public UsageEvents.Event getBackgroundEvent() {
            return backgroundEvent;
        }

        public UsageEvents.Event getForegroundEvent() {
            return foregroundEvent;
        }

        @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
        public long timeInForegroundInMilliSeconds() {
            if (foregroundEvent == null || backgroundEvent == null) {
                return 0;
            } else {
                return backgroundEvent.getTimeStamp() - foregroundEvent.getTimeStamp();
            }
        }

        @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
        public String toString() {
            if (foregroundEvent != null && backgroundEvent != null) {
                return "Start Time: " + dateAndTime(foregroundEvent.getTimeStamp()) + " End Time: " +
                        dateAndTime(backgroundEvent.getTimeStamp());
            }
            return "";
        }


    }


    private String dateAndTime(Long timestamp) {
        Date date = new Date(timestamp);
        SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy h:mm:ss a");
        sdf.setTimeZone(TimeZone.getTimeZone("PST"));
        String formattedDate = sdf.format(date);
        return formattedDate;
    }


    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private boolean accessAllowed() {
        try {
            ApplicationInfo applicationInfo = getActivity().getPackageManager().getApplicationInfo(getActivity().getPackageName(), 0);
            AppOpsManager appOpsManager = (AppOpsManager) getActivity().getSystemService(Context.APP_OPS_SERVICE);
            int mode = appOpsManager.checkOpNoThrow(AppOpsManager.OPSTR_GET_USAGE_STATS, applicationInfo.uid, applicationInfo.packageName);
            Log.i(TAG, "Do we have access to see usage data? " + (mode == AppOpsManager.MODE_ALLOWED));
            return (mode == AppOpsManager.MODE_ALLOWED);

        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }
    }

}