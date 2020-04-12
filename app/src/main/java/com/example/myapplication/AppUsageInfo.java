package com.example.myapplication;

import android.graphics.drawable.Drawable;
import android.util.Log;

import java.util.concurrent.TimeUnit;

public class AppUsageInfo {

    private static final String TAG = AppUsageInfo.class.getSimpleName();

    private String appName;
    private Long timeSpent;
    private Drawable appIcon;
    private double percentTimeSpent;

    public AppUsageInfo(String name, Long time) {
        this.appName = name;
        this.timeSpent = time;
        percentTimeSpent = 100.0;
    }

    public AppUsageInfo(String name, Drawable icon) {
        this.appName = name;
        this.appIcon = icon;
        percentTimeSpent = 100.0;
    }

    public String getAppName() {
        return appName;
    }

    public Long getTimeSpentInMilliseconds() {
        return timeSpent;
    }

    public Long getTimeSpentInMinutes() {
        return TimeUnit.MILLISECONDS.toMinutes(timeSpent);
    }

    public Drawable getAppIcon() {
        return appIcon;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public void setPercentTimeSpent(Long maxTimeSpentOnApp) {
        percentTimeSpent = (timeSpent.doubleValue()/maxTimeSpentOnApp);
        Log.i(TAG, "App Name: " + appName + " Max Time Spent: " + maxTimeSpentOnApp + " percent: " + percentTimeSpent);

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
