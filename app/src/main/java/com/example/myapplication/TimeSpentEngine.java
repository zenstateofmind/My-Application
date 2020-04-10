package com.example.myapplication;

import android.app.usage.UsageEvents;
import android.app.usage.UsageStatsManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Build;
import android.util.Log;

import androidx.annotation.RequiresApi;

import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

public class TimeSpentEngine {

    private static final String TAG = TimeSpentEngine.class.getSimpleName();
    private final Context context;


    public TimeSpentEngine(Context context) {
        this.context = context;
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public ArrayList<AppUsageInfo> getTimeSpent() {
        Log.i(TAG, "Usage access success!");

        UsageStatsManager usageStatsManager = (UsageStatsManager) context.getSystemService(Context.USAGE_STATS_SERVICE);
        ArrayList<AppUsageInfo> appUsageInfos = getTimeWithUsageEvents(usageStatsManager);
        return appUsageInfos;
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private ArrayList<AppUsageInfo> getTimeWithUsageEvents(UsageStatsManager usageStatsManager) {


        PackageManager packageManager = context.getPackageManager();
        ArrayList<String> appsOnDevice = getInstalledApps(packageManager);
        HashMap<String, ArrayList<UsageEvents.Event>> usageInfoEventsPerApp = new HashMap<String, ArrayList<UsageEvents.Event>>();
        HashMap<String, Long> timeSpentPerApp = new HashMap<>();

        // queryEvents takes start time and end time in milli seconds
        UsageEvents usageEvents = usageStatsManager.queryEvents(1586415600000L, 1586493600000L);

        while (usageEvents.hasNextEvent()) {
            UsageEvents.Event currentEvent = new UsageEvents.Event();
            usageEvents.getNextEvent(currentEvent);

            if(currentEvent.getEventType() == UsageEvents.Event.MOVE_TO_FOREGROUND ||
                    currentEvent.getEventType() == UsageEvents.Event.MOVE_TO_BACKGROUND) {

                try {
                    ApplicationInfo applicationInfo = packageManager.getApplicationInfo(currentEvent.getPackageName(), 0);
                    String appName = applicationInfo.loadLabel(packageManager).toString();

                    Log.i(TAG, "App Name: " + appName +
                            " Event type: " + currentEvent.getEventType() +
                            " Time: " + dateAndTime(currentEvent.getTimeStamp()));

                    // If the usage event belongs to an installed app, add it to our map of <app -> events>
                    if (appsOnDevice.contains(appName)) {
                        ArrayList<UsageEvents.Event> appRelatedEvents = new ArrayList<>();
                        if (usageInfoEventsPerApp.keySet().contains(appName)) {
                            appRelatedEvents = usageInfoEventsPerApp.get(appName);
                        }
                        appRelatedEvents.add(currentEvent);
                        usageInfoEventsPerApp.put(appName, appRelatedEvents);
                    }

                } catch (PackageManager.NameNotFoundException e) {
                    e.printStackTrace();
                }

            }
        }

        timeSpentPerApp = getTimeSpentPerApp(usageInfoEventsPerApp);

        ArrayList<AppUsageInfo> appUsageInfos = new ArrayList<AppUsageInfo>();

        for (String appName : timeSpentPerApp.keySet()) {
            AppUsageInfo appUsageInfo = new AppUsageInfo(appName, timeSpentPerApp.get(appName));
            appUsageInfos.add(appUsageInfo);
            Log.i(TAG, "App name: " + appName + " Time spent: " + TimeUnit.MILLISECONDS.toMinutes(timeSpentPerApp.get(appName)));
        }

        return appUsageInfos;

    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private HashMap<String, Long> getTimeSpentPerApp(HashMap<String, ArrayList<UsageEvents.Event>> usageInfoEventsPerApp) {

        HashMap<String, Long> timeSpentPerApp = new HashMap<>();

        for (String app : usageInfoEventsPerApp.keySet()) {

            long timeSpent = 0;
            int i = 0;
            ArrayList<UsageEvents.Event> appEvents = usageInfoEventsPerApp.get(app);

            // Get two events at once - ideally know thats a foreground task and one thats a
            // background task. However, due to complications, this is not the case all the time,
            // so we *check* if the first task is foreground task and the second task is the
            // background task. If not, there might be some weird stuff happening with the way
            // usage events have been saved. In that case, we increment by 1 and examine the same thing.
            // If it's not the case, we take a time diff of background - foreground, add that
            // to the time spent on the app and then increment by 2.

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

        return timeSpentPerApp;

    }

    private ArrayList<String> getInstalledApps(PackageManager packageManager) {
        Intent intent = new Intent(Intent.ACTION_MAIN, null);
        intent.addCategory(Intent.CATEGORY_LAUNCHER);
        List<ResolveInfo> installedApplications = packageManager.queryIntentActivities(intent, PackageManager.GET_META_DATA);

        ArrayList<String> appsOnDevice = new ArrayList<>();

        // Convert the list of resolved info into app name string list
        for (ResolveInfo appInfo: installedApplications) {
            appsOnDevice.add(appInfo.loadLabel(packageManager).toString());
        }
        return appsOnDevice;
    }

    private String dateAndTime(Long timestamp) {
        Date date = new Date(timestamp);
        SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy h:mm:ss a");
        sdf.setTimeZone(TimeZone.getTimeZone("PST"));
        String formattedDate = sdf.format(date);
        return formattedDate;
    }

}
