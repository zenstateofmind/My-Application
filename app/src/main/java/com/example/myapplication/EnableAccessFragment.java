package com.example.myapplication;

import android.app.AppOpsManager;
import android.app.usage.UsageEvents;
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
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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

    /**
     * TODO: After getting access to usage data, make sure that you come back straight into the app.
     */
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
//                        printAppUsage();

                    }
                }
            }
        };
        enableUsageButton.setOnClickListener(buttonListener);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void printAppUsage() {
        Log.i(TAG, "Usage access success!");

        UsageStatsManager usageStatsManager = (UsageStatsManager) getActivity().getSystemService(Context.USAGE_STATS_SERVICE);
        HashMap<String, Long> timeSpentPerApp = getTimeWithUsageEvents(usageStatsManager);

    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private HashMap<String, Long> getTimeWithUsageEvents(UsageStatsManager usageStatsManager) {


        PackageManager packageManager = getActivity().getPackageManager();
        ArrayList<String> appsOnDevice = getInstalledApps(packageManager);
        HashMap<String, ArrayList<UsageEvents.Event>> usageInfoEventsPerApp = new HashMap<String, ArrayList<UsageEvents.Event>>();
        HashMap<String, Long> timeSpentPerApp = new HashMap<>();

        // queryEvents takes start time and end time in milli seconds
        UsageEvents usageEvents = usageStatsManager.queryEvents(1585983600000L, 1586069999000L);

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

        for (String appName : timeSpentPerApp.keySet()) {
            Log.i(TAG, "App name: " + appName + " Time spent: " + TimeUnit.MILLISECONDS.toMinutes(timeSpentPerApp.get(appName)));
        }

        return timeSpentPerApp;

    }

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