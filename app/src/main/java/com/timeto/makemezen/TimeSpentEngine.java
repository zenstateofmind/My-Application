package com.timeto.makemezen;

import android.app.usage.UsageEvents;
import android.app.usage.UsageStatsManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.Log;

import androidx.annotation.RequiresApi;

import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.TimeZone;

public class TimeSpentEngine {

    private static final String TAG = TimeSpentEngine.class.getSimpleName();
    private final Context context;


    public TimeSpentEngine(Context context) {
        this.context = context;
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public ArrayList<AppUsageInfo> getTimeSpent(Long startTime, Long endTime) {

        Log.i(TAG, "Usage access success!");

        UsageStatsManager usageStatsManager = (UsageStatsManager) context.getSystemService(Context.USAGE_STATS_SERVICE);
        ArrayList<AppUsageInfo> appUsageInfos = getTimeWithUsageEvents(usageStatsManager, startTime, endTime);
//        ArrayList<AppUsageInfo> appUsageInfos = new ArrayList<>();
//
        return appUsageInfos;
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private ArrayList<AppUsageInfo> getTimeWithUsageEvents(UsageStatsManager usageStatsManager, Long startTime, Long endTime) {

        PackageManager packageManager = context.getPackageManager();
        ArrayList<String> appsOnDevice = getInstalledApps(packageManager);
        HashMap<String, ArrayList<UsageEvents.Event>> usageInfoEventsPerApp = new HashMap<String, ArrayList<UsageEvents.Event>>();
        HashMap<String, Long> timeSpentPerApp = new HashMap<>();
        ArrayList<AppUsageInfo> appUsageInfos = new ArrayList<AppUsageInfo>();

        // queryEvents takes start time and end time in milli seconds
        Calendar c = Calendar.getInstance();
        c.set(Calendar.HOUR_OF_DAY, 0);
        c.set(Calendar.MINUTE, 0);
        c.set(Calendar.SECOND, 0);
        c.set(Calendar.MILLISECOND, 0);
        UsageEvents usageEvents = usageStatsManager.queryEvents(startTime, endTime);

        while (usageEvents.hasNextEvent()) {
            UsageEvents.Event currentEvent = new UsageEvents.Event();
            usageEvents.getNextEvent(currentEvent);

            if(currentEvent.getEventType() == UsageEvents.Event.MOVE_TO_FOREGROUND ||
                    currentEvent.getEventType() == UsageEvents.Event.MOVE_TO_BACKGROUND) {

                try {
                    ApplicationInfo applicationInfo = packageManager.getApplicationInfo(currentEvent.getPackageName(), 0);
                    String appName = applicationInfo.loadLabel(packageManager).toString();
                    Drawable applicationIcon = packageManager.getApplicationIcon(applicationInfo);

//                    Log.i(TAG, "App Name: " + appName +
//                            " Event type: " + currentEvent.getEventType() +
//                            " Time: " + dateAndTime(currentEvent.getTimeStamp()));

                    // If the usage event belongs to an installed app, add it to our map of <app -> events>
                    if (appsOnDevice.contains(appName)) {
                        ArrayList<UsageEvents.Event> appRelatedEvents = new ArrayList<>();
                        if (usageInfoEventsPerApp.keySet().contains(appName)) {
                            appRelatedEvents = usageInfoEventsPerApp.get(appName);
                        } else {
                            // If we have not come across this app yet...
                            AppUsageInfo appUsageInfo = new AppUsageInfo(appName, applicationIcon, currentEvent.getPackageName());
                            appUsageInfos.add(appUsageInfo);
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

        for (AppUsageInfo appUsageInfo: appUsageInfos) {
            Long timeSpent = timeSpentPerApp.get(appUsageInfo.getAppName());
            appUsageInfo.setTimeSpent(timeSpent);
        }

//        for (String appName : timeSpentPerApp.keySet()) {
//
//            AppUsageInfo appUsageInfo = new AppUsageInfo(appName, timeSpentPerApp.get(appName));
//            appUsageInfos.add(appUsageInfo);
////            Log.i(TAG, "App name: " + appName + " Time spent: " + TimeUnit.MILLISECONDS.toMinutes(timeSpentPerApp.get(appName)));
//        }

        appUsageInfos = removeUnusedAppsData(appUsageInfos);

        Collections.sort(appUsageInfos, new AppUsageInfoComparator());

        // Fill the percentage time spent per app. Since we have already sorted the list, we can
        // assume that the top most is the one where we have spent most time
        if (appUsageInfos.size() > 0) {
            Long mostSpentTimePerApp = appUsageInfos.get(0).getTimeSpentInMilliseconds();
            for (AppUsageInfo appUsageInfo : appUsageInfos) {
                appUsageInfo.setPercentTimeSpent(mostSpentTimePerApp);
            }
        }

        return appUsageInfos;
    }

    private ArrayList<AppUsageInfo> removeUnusedAppsData(ArrayList<AppUsageInfo> appUsageInfos) {
        ArrayList<AppUsageInfo> zeroesRemovedInfos = new ArrayList<>();

        for (AppUsageInfo appInfo: appUsageInfos) {
            if (appInfo.getTimeSpentInMinutes() > 0 ) {
                zeroesRemovedInfos.add(appInfo);
            }
        }

        return zeroesRemovedInfos;
    }

    private class AppUsageInfoComparator implements Comparator<AppUsageInfo> {

        @Override
        public int compare(AppUsageInfo o1, AppUsageInfo o2) {

            Long timeSpentInMinutes1 = o1.getTimeSpentInMinutes();
            Long timeSpentInMinutes2 = o2.getTimeSpentInMinutes();

            if (timeSpentInMinutes1 == timeSpentInMinutes2) {
                return 0;
            } else if (timeSpentInMinutes1 < timeSpentInMinutes2) {
                return 1;
            } else {
                return -1;
            }
        }
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
//                    Log.i(TAG, "App name: " + app +
//                            " first event: " + firstEvent.getEventType() +
//                            " first event time: " + dateAndTime(firstEvent.getTimeStamp()) +
//                            " second event: " + secondEvent.getEventType() +
//                            " second event time: " + dateAndTime(secondEvent.getTimeStamp()) +
//                            " Time Spent Calculation: " + TimeUnit.MILLISECONDS.toMinutes(timeSpent));
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
