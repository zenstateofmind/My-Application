package com.timeto.makemezen;

import android.graphics.drawable.Drawable;
import android.util.Log;

import java.util.concurrent.TimeUnit;

public class AppUsageInfo {

    private static final String TAG = AppUsageInfo.class.getSimpleName();

    private String appName;
    private Long timeSpent;
    private Drawable appIcon;
    private double percentTimeSpent;
    private String packageName;

    public AppUsageInfo(String appName, Drawable icon, String packageName) {
        this.appName = appName;
        this.appIcon = icon;
        this.packageName = packageName;
        percentTimeSpent = 100.0;
    }

    public String getAppName() {
        return appName;
    }

    public String getPackageName() {return packageName;}

    public Long getTimeSpentInMilliseconds() {
        return timeSpent;
    }

    public Long getTimeSpentInMinutes() {
        return TimeUnit.MILLISECONDS.toMinutes(timeSpent);
    }

    public Drawable getAppIcon() {
        return appIcon;
    }

    public void setPercentTimeSpent(Long maxTimeSpentOnApp) {
        percentTimeSpent = (getTimeSpentInMinutes().doubleValue()/TimeUnit.MILLISECONDS.toMinutes(maxTimeSpentOnApp));
        Log.i(TAG, "App Name: " + appName + " Max Time Spent: " + maxTimeSpentOnApp + " percent: " + percentTimeSpent);
    }

    public void setPercentTimeSpentFromSharedPrefData(double percentTimeSpent) {
        this.percentTimeSpent = percentTimeSpent;
    }

    public double getPercentTimeSpent() {
        return percentTimeSpent;
    }

    public void setTimeSpent(Long timeSpent) {
        this.timeSpent = timeSpent;
    }

    public int compareTo(AppUsageInfo appUsageInfo) {
        int compareTo = appUsageInfo.getTimeSpentInMinutes().intValue();

        return compareTo - getTimeSpentInMinutes().intValue();
    }

}
