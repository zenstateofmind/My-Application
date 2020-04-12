package com.example.myapplication;

import android.graphics.drawable.Drawable;

import java.util.concurrent.TimeUnit;

public class AppUsageInfo {

    private String appName;
    private Long timeSpent;
    private Drawable appIcon;

    public AppUsageInfo(String name, Long time) {
        this.appName = name;
        this.timeSpent = time;
    }

    public AppUsageInfo(String name, Drawable icon) {
        this.appName = name;
        this.appIcon = icon;
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

    public void setTimeSpent(Long timeSpent) {
        this.timeSpent = timeSpent;
    }

    public int compareTo(AppUsageInfo appUsageInfo) {
        int compareTo = appUsageInfo.getTimeSpentInMinutes().intValue();

        return compareTo - getTimeSpentInMinutes().intValue();
    }
}
