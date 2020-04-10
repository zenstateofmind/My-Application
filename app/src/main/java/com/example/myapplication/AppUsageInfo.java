package com.example.myapplication;

import java.util.concurrent.TimeUnit;

public class AppUsageInfo {

    private String appName;
    private Long timeSpent;

    public AppUsageInfo(String name, Long time) {
        this.appName = name;
        this.timeSpent = time;
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

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public void setTimeSpent(Long timeSpent) {
        this.timeSpent = timeSpent;
    }
}
