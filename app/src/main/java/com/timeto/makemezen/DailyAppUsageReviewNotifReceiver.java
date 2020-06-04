package com.timeto.makemezen;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.os.Build;

import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;

import com.amplitude.api.Amplitude;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class DailyAppUsageReviewNotifReceiver extends BroadcastReceiver {

    /**
     * Goal: Notify the user of the apps that have been used for a long time.
     * 1. We will notify the user once a day
     * 2. The apps that we will notify the user about are the ones that they have used a lot
     *    during the day.
     * 3. These apps are the ones that have been used for more than an hour.
     * 4. So are we going to explicitly state the amount of time that they have spent a lot of time on?
     *    No - we will just say that the user has spent a lot of time on x apps by just highlighting the apps...
     *         and maybe the hours? So for ex: "You have spent more than 1 hour on "x, y, z" today. Tap to see more
     */

    public static String CHANNEL_ID = "APP_REMINDER_ID";
    public static String NOTIFICATION_ID = "notification-id";
    private final static AtomicInteger atomic_int_counter = new AtomicInteger(0);


    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onReceive(Context context, Intent intent) {

        Amplitude.getInstance().logEvent("Kickstart the daily app usage notifier");

        String notifText = "";

        NotificationManager notificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            CharSequence name = "phone_usage_reminders";
            String description = "Send you reminders of how often you use your phone";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);

            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            notificationManager.createNotificationChannel(channel);

        }

        createAndSendNotif(context, intent, notificationManager);

    }

    private void buildAndSendNotif(Context context, Intent intent, String notifText, NotificationManager notificationManager) {
        Intent homeScreenLaunchIntent = new Intent(context, HomeScreen.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        int requestCode = (int)System.currentTimeMillis();
        PendingIntent pendingIntent = PendingIntent.getActivity(context, requestCode, homeScreenLaunchIntent, 0);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.vector_drawable_group102)
                .setLargeIcon(BitmapFactory.decodeResource(context.getResources(),
                        R.mipmap.ic_launcher_round))
                .setContentTitle("Usage Report")
                .setStyle(new NotificationCompat.BigTextStyle().bigText(notifText))
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        Notification dailyNotification = builder.build();

        notificationManager.notify(getID(), dailyNotification);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void createAndSendNotif(Context context, Intent intent, NotificationManager notificationManager) {

        String notifText = "";
        int totalTimeSpent = 0;

        Calendar today = Calendar.getInstance();
        today.set(Calendar.HOUR_OF_DAY, 0);
        today.set(Calendar.MINUTE, 0);
        today.set(Calendar.SECOND, 0);
        today.set(Calendar.MILLISECOND, 0);

        TimeSpentEngine engine = new TimeSpentEngine(context);

        ArrayList<AppUsageInfo> appUsageInfos = engine.getTimeSpent(today.getTimeInMillis(), System.currentTimeMillis());

        for (AppUsageInfo appUsageInfo : appUsageInfos) {
            totalTimeSpent += appUsageInfo.getTimeSpentInMilliseconds();
        }
        int minsSpent = (int) TimeUnit.MILLISECONDS.toMinutes(totalTimeSpent);
        int hoursSpent = minsSpent / 60;

        if (hoursSpent >= MakeMeZenUtil.MILESTONE_PHONE_USAGE_ONE_TIME &&
                hoursSpent < MakeMeZenUtil.MILESTONE_PHONE_USAGE_TWO_TIME) {
            if (!dailyNotifPhoneUsageSent(today.getTimeInMillis(), MakeMeZenUtil.MILESTONE_PHONE_USAGE_ONE, context)) {
                notifText = "You have spent more than " + hoursSpent + " hours on your phone today. Learn more!";
                buildAndSendNotif(context, intent, notifText, notificationManager);
                updatePhoneUsageDataInSharedPreferences(today.getTimeInMillis(), context, MakeMeZenUtil.MILESTONE_PHONE_USAGE_ONE);
                Amplitude.getInstance().logEvent(MakeMeZenUtil.MILESTONE_PHONE_USAGE_ONE_TIME + " hour phone usage notification sent");
            } else {
                Amplitude.getInstance().logEvent("Not sending " + MakeMeZenUtil.MILESTONE_PHONE_USAGE_ONE_TIME + " hour phone usage notification sent. Previously sent");
            }
            return;
        } else if (hoursSpent >= MakeMeZenUtil.MILESTONE_PHONE_USAGE_TWO_TIME &&
                hoursSpent < MakeMeZenUtil.MILESTONE_PHONE_USAGE_THREE_TIME) {
            if (!dailyNotifPhoneUsageSent(today.getTimeInMillis(), MakeMeZenUtil.MILESTONE_PHONE_USAGE_TWO, context)) {
                notifText = "You have spent more than " + hoursSpent + " hours on your phone today. Learn more!";
                buildAndSendNotif(context, intent, notifText, notificationManager);
                updatePhoneUsageDataInSharedPreferences(today.getTimeInMillis(), context, MakeMeZenUtil.MILESTONE_PHONE_USAGE_TWO);
                Amplitude.getInstance().logEvent(MakeMeZenUtil.MILESTONE_PHONE_USAGE_TWO_TIME + " hour phone usage notification sent");

            } else {
                Amplitude.getInstance().logEvent("Not sending " + MakeMeZenUtil.MILESTONE_PHONE_USAGE_TWO_TIME + " hour phone usage notification sent. Previously sent");
            }
            return;

        } else if (hoursSpent >= MakeMeZenUtil.MILESTONE_PHONE_USAGE_THREE_TIME &&
                hoursSpent < MakeMeZenUtil.MILESTONE_PHONE_USAGE_FOUR_TIME) {
            if (!dailyNotifPhoneUsageSent(today.getTimeInMillis(), MakeMeZenUtil.MILESTONE_PHONE_USAGE_THREE, context)) {
                notifText = "You have spent more than " + hoursSpent + " hours on your phone today. Learn more!";
                buildAndSendNotif(context, intent, notifText, notificationManager);
                updatePhoneUsageDataInSharedPreferences(today.getTimeInMillis(), context, MakeMeZenUtil.MILESTONE_PHONE_USAGE_THREE);
                Amplitude.getInstance().logEvent(MakeMeZenUtil.MILESTONE_PHONE_USAGE_THREE_TIME + " hour phone usage notification sent");

            } else {
                Amplitude.getInstance().logEvent("Not sending " + MakeMeZenUtil.MILESTONE_PHONE_USAGE_THREE_TIME + " hour phone usage notification sent. Previously sent");
            }
            return;
        } else if (hoursSpent >= MakeMeZenUtil.MILESTONE_PHONE_USAGE_FOUR_TIME) {
            if (!dailyNotifPhoneUsageSent(today.getTimeInMillis(), MakeMeZenUtil.MILESTONE_PHONE_USAGE_FOUR, context)) {
                notifText = "You have spent more than " + hoursSpent + " hours on your phone today. Learn more!";
                buildAndSendNotif(context, intent, notifText, notificationManager);
                updatePhoneUsageDataInSharedPreferences(today.getTimeInMillis(), context, MakeMeZenUtil.MILESTONE_PHONE_USAGE_FOUR);
                Amplitude.getInstance().logEvent(MakeMeZenUtil.MILESTONE_PHONE_USAGE_FOUR_TIME + " hour phone usage notification sent");

            } else {
                Amplitude.getInstance().logEvent("Not sending " + MakeMeZenUtil.MILESTONE_PHONE_USAGE_FOUR_TIME + " hour phone usage notification sent. Previously sent");
            }
            return;
        }


        ArrayList<AppUsageInfo> overusedApps = overusedApps(context, MakeMeZenUtil.APP_OVERUSE_MINUTES, appUsageInfos);

        if (overusedApps.size() > 0) {
            for (AppUsageInfo overUsedApp : overusedApps) {

                int hoursSpentOnApp = (int) (overUsedApp.getTimeSpentInMinutes()/60);
                if (hoursSpentOnApp >= MakeMeZenUtil.MILESTONE_APP_USAGE_ONE_TIME &&
                        hoursSpentOnApp < MakeMeZenUtil.MILESTONE_APP_USAGE_TWO_TIME) {
                    if (!dailyNotifAppUsageSent(today.getTimeInMillis(), overUsedApp.getAppName(), MakeMeZenUtil.MILESTONE_APP_USAGE_ONE, context)) {
                        notifText = "You have spent more than " + hoursSpentOnApp + " hours on " + overUsedApp.getAppName() + " today. Learn more!";
                        buildAndSendNotif(context, intent, notifText, notificationManager);
                        updateAppUsageDataInSharedPreferences(today.getTimeInMillis(), overUsedApp.getAppName(), MakeMeZenUtil.MILESTONE_APP_USAGE_ONE, context);
                        Amplitude.getInstance().logEvent(MakeMeZenUtil.MILESTONE_APP_USAGE_ONE_TIME + " hour app usage notification sent for " + overUsedApp.getAppName());

                    } else {
                        Amplitude.getInstance().logEvent("Not sending " + MakeMeZenUtil.MILESTONE_APP_USAGE_ONE_TIME + " hour app usage notification sent for " + overUsedApp.getAppName() +". Previously sent");
                    }
                    return;
                } else if (hoursSpentOnApp >= MakeMeZenUtil.MILESTONE_APP_USAGE_TWO_TIME &&
                        hoursSpentOnApp < MakeMeZenUtil.MILESTONE_APP_USAGE_THREE_TIME) {
                    if (!dailyNotifAppUsageSent(today.getTimeInMillis(), overUsedApp.getAppName(), MakeMeZenUtil.MILESTONE_APP_USAGE_TWO, context)) {
                        notifText = "You have spent more than " + hoursSpentOnApp + " hours on " + overUsedApp.getAppName() + " today. Learn more!";
                        buildAndSendNotif(context, intent, notifText, notificationManager);
                        updateAppUsageDataInSharedPreferences(today.getTimeInMillis(), overUsedApp.getAppName(), MakeMeZenUtil.MILESTONE_APP_USAGE_TWO, context);
                        Amplitude.getInstance().logEvent(MakeMeZenUtil.MILESTONE_APP_USAGE_TWO_TIME + " hour app usage notification sent for " + overUsedApp.getAppName());

                    } else {
                        Amplitude.getInstance().logEvent("Not sending " + MakeMeZenUtil.MILESTONE_APP_USAGE_TWO_TIME + " hour app usage notification sent for " + overUsedApp.getAppName() +". Previously sent");
                    }
                    return;

                } else if (hoursSpentOnApp >= MakeMeZenUtil.MILESTONE_APP_USAGE_THREE_TIME &&
                        hoursSpentOnApp < MakeMeZenUtil.MILESTONE_APP_USAGE_FOUR_TIME) {
                    if (!dailyNotifAppUsageSent(today.getTimeInMillis(), overUsedApp.getAppName(), MakeMeZenUtil.MILESTONE_APP_USAGE_THREE, context)) {
                        notifText = "You have spent more than " + hoursSpentOnApp + " hours on " + overUsedApp.getAppName() + " today. Learn more!";
                        buildAndSendNotif(context, intent, notifText, notificationManager);
                        updateAppUsageDataInSharedPreferences(today.getTimeInMillis(), overUsedApp.getAppName(), MakeMeZenUtil.MILESTONE_APP_USAGE_THREE, context);
                        Amplitude.getInstance().logEvent(MakeMeZenUtil.MILESTONE_APP_USAGE_THREE_TIME + " hour app usage notification sent for " + overUsedApp.getAppName());

                    } else {
                        Amplitude.getInstance().logEvent("Not sending " + MakeMeZenUtil.MILESTONE_APP_USAGE_THREE_TIME + " hour app usage notification sent for " + overUsedApp.getAppName() +". Previously sent");
                    }
                    return;

                } else if (hoursSpentOnApp >= MakeMeZenUtil.MILESTONE_APP_USAGE_FOUR_TIME) {
                    if (!dailyNotifAppUsageSent(today.getTimeInMillis(), overUsedApp.getAppName(), MakeMeZenUtil.MILESTONE_APP_USAGE_FOUR, context)) {
                        notifText = "You have spent more than " + hoursSpentOnApp + " hours on " + overUsedApp.getAppName() + " today. Learn more!";
                        buildAndSendNotif(context, intent, notifText, notificationManager);
                        updateAppUsageDataInSharedPreferences(today.getTimeInMillis(), overUsedApp.getAppName(), MakeMeZenUtil.MILESTONE_APP_USAGE_FOUR, context);
                        Amplitude.getInstance().logEvent(MakeMeZenUtil.MILESTONE_APP_USAGE_FOUR_TIME + " hour app usage notification sent for " + overUsedApp.getAppName());

                    } else {
                        Amplitude.getInstance().logEvent("Not sending " + MakeMeZenUtil.MILESTONE_APP_USAGE_FOUR_TIME + " hour app usage notification sent for " + overUsedApp.getAppName() +". Previously sent");
                    }
                    return;
                }

            }
        }
    }

    private void updatePhoneUsageDataInSharedPreferences(long timeInMillis, Context context, String milestone) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(context.getString(R.string.file_key), Context.MODE_PRIVATE);
        String key = MakeMeZenUtil.createDailyPhoneUsageKey(timeInMillis);
        String dataForTheDay = sharedPreferences.getString(key, "");
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(key, dataForTheDay + MakeMeZenUtil.DIVIDER + milestone);
        editor.apply();
    }

    private void updateAppUsageDataInSharedPreferences(long timeInMillis, String appName, String milestone, Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(context.getString(R.string.file_key), Context.MODE_PRIVATE);
        String key = MakeMeZenUtil.createDailyAppUsageKey(timeInMillis);
        String dataForTheDay = sharedPreferences.getString(key, "");
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(key, dataForTheDay + MakeMeZenUtil.DIVIDER + MakeMeZenUtil.getAppUsageValue(appName, milestone));
        editor.apply();
    }

    private boolean dailyNotifPhoneUsageSent(long todayInMilli, String milestone, Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(context.getString(R.string.file_key), Context.MODE_PRIVATE);
        String dataForTheDay = sharedPreferences.getString(MakeMeZenUtil.createDailyPhoneUsageKey(todayInMilli), "");
        if (!dataForTheDay.equals("") && dataForTheDay.contains(milestone)) {
            return true;
        }
        return false;
    }

    private boolean dailyNotifAppUsageSent(long todayInMilli, String appName, String milestone, Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(context.getString(R.string.file_key), Context.MODE_PRIVATE);
        String appUsageMilestones = sharedPreferences.getString(MakeMeZenUtil.createDailyAppUsageKey(todayInMilli), "");
        String expectedValue = MakeMeZenUtil.getAppUsageValue(appName, milestone);
        if (!appUsageMilestones.equals("") && appUsageMilestones.contains(expectedValue)) {
            return true;
        }
        return false;
    }

    public static int getID() {
        return atomic_int_counter.incrementAndGet();
    }

    // Create a method that returns apps that you have spent more than an x mins during the day (till now).
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public ArrayList<AppUsageInfo> overusedApps(Context context, Long minMInutes, ArrayList<AppUsageInfo> appUsageInfos) {

        ArrayList<AppUsageInfo> overusedApps = new ArrayList<>();
        for (AppUsageInfo appUsageInfo: appUsageInfos) {
            if (appUsageInfo.getTimeSpentInMinutes() >= minMInutes) {
                overusedApps.add(appUsageInfo);
            }
        }
        return overusedApps;
    }
}
