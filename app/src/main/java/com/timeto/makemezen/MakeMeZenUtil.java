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

    public static final String MILESTONE_PHONE_USAGE_ONE = "phone_usage_milestone_1";
    public static final String MILESTONE_PHONE_USAGE_TWO = "phone_usage_milestone_2";
    public static final String MILESTONE_PHONE_USAGE_THREE = "phone_usage_milestone_3";
    public static final String MILESTONE_PHONE_USAGE_FOUR = "phone_usage_milestone_4";


    public static final String MILESTONE_APP_USAGE_ONE = "app_usage_milestone_1";
    public static final String MILESTONE_APP_USAGE_TWO = "app_usage_milestone_2";
    public static final String MILESTONE_APP_USAGE_THREE = "app_usage_milestone_3";
    public static final String MILESTONE_APP_USAGE_FOUR = "app_usage_milestone_4";

    private static final String DAILY_NOTIF_PHONE_USAGE_KEY = "daily_notification_phone_usage";
    private static final String DAILY_NOTIF_APP_USAGE_KEY = "daily_notification_phone_usage";
    public static final String DIVIDER = "&&&";
    private static final String APP_USAGE_INFO_OBJECTS_DIVIDER = "@@@@@@@#@@@@@@@@@@@@";
    private static final String LAST_UPDATE_KEY = "LAST_UPDATED_DATE_DATA";
    private static final String MOTIVATION_KEY = "motivation_key";

    public static String createKey(Long startDate) {
        return startDate + "";
    }

    public static String createDailyPhoneUsageKey(Long todayInMillis) {
        return todayInMillis + DAILY_NOTIF_PHONE_USAGE_KEY;
    }

    public static String getMotivationKey() {
        return MOTIVATION_KEY;
    }

    public static String createDailyAppUsageKey(Long todayInMillis) {
        return todayInMillis + DAILY_NOTIF_APP_USAGE_KEY;
    }

    public static String getAppUsageValue(String appName, String milestone) {
        return appName + milestone;
    }

    public static String lastUpdateKey(Long dateInMilli) {
        return dateInMilli + LAST_UPDATE_KEY;
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
