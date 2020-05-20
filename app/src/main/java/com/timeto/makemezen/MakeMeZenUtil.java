package com.timeto.makemezen;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;

import androidx.recyclerview.widget.DividerItemDecoration;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class MakeMeZenUtil {

    public static final String START_DATE_MILLISECONDS = "start_date_milliseconds";
    public static final String END_DATE_MILLISECONDS = "end_date_milliseconds";
    private static final String DIVIDER = "&&&";
    private static final String APP_USAGE_INFO_OBJECTS_DIVIDER = "@@@@@@@#@@@@@@@@@@@@";
    private static final String LAST_UPDATE_KEY = "LAST_UPDATED_DATE_TODAYS_DATA";

    public static String createKey(Long startDate) {
        return startDate + "";
    }

    public static String lastUpdateKey() {
        return LAST_UPDATE_KEY;
    }

    public static String lastUpdateTodayData(Long lastDate) {
        return lastDate + "";
    }

    public static String getAppUsageInfoString(AppUsageInfo appUsageInfo) {
        return appUsageInfo.getAppName() + DIVIDER +
                appUsageInfo.getPackageName() + DIVIDER +
                appUsageInfo.getTimeSpentInMilliseconds() + DIVIDER
                + appUsageInfo.getPercentTimeSpent();
    }

    public static String getAppUsageInfoObjectsString(ArrayList<AppUsageInfo> appUsageInfos) {

        String appUsageInfoObjectsStr = "";

        for (int i = 0; i < appUsageInfos.size() - 1; i++) {
            appUsageInfoObjectsStr = appUsageInfoObjectsStr + getAppUsageInfoString(appUsageInfos.get(i)) + APP_USAGE_INFO_OBJECTS_DIVIDER;
        }

        appUsageInfoObjectsStr = appUsageInfoObjectsStr + getAppUsageInfoString(appUsageInfos.get(appUsageInfos.size() - 1));

        return appUsageInfoObjectsStr;
    }

    public static ArrayList<AppUsageInfo> getAppUsageInfoList(String appUsageInfoObjects, Context context) {
        String[] appUsageInfoObjectList = appUsageInfoObjects.split(APP_USAGE_INFO_OBJECTS_DIVIDER);
        ArrayList<AppUsageInfo> appUsageInfos = new ArrayList<AppUsageInfo>();
        for (String appUsageInfoObject : appUsageInfoObjectList) {
            AppUsageInfo appUsageInfo = getAppUsageInfoObject(appUsageInfoObject, context);
            if (appUsageInfo != null) {
                appUsageInfos.add(appUsageInfo);
            }
        }
        return appUsageInfos;
    }


    public static AppUsageInfo getAppUsageInfoObject(String appUsageInfoString, Context context) {
        String[] splitAppUsageInfoString = appUsageInfoString.split(DIVIDER);
        String appName = splitAppUsageInfoString[0];
        String packageName = splitAppUsageInfoString[1];
        Long timeSpent = Long.parseLong(splitAppUsageInfoString[2]);
        double percentTimeSpent = Double.valueOf(splitAppUsageInfoString[3]);

        AppUsageInfo appUsageInfo;

        try {
            PackageManager packageManager = context.getPackageManager();
            ApplicationInfo applicationInfo = packageManager.getApplicationInfo(packageName, 0);
            Drawable applicationIcon = packageManager.getApplicationIcon(applicationInfo);

            appUsageInfo = new AppUsageInfo(appName, applicationIcon, packageName);
            appUsageInfo.setTimeSpent(timeSpent);
            appUsageInfo.setPercentTimeSpentFromSharedPrefData(percentTimeSpent);

            return appUsageInfo;
        } catch (PackageManager.NameNotFoundException e) {
            return null;
        }


    }
}
