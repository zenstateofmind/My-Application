package com.timeto.makemezen;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Build;

import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;

public class AppUsageReviewNotifReceiver extends BroadcastReceiver {

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
    public static Long overuseMinutes = 60L;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onReceive(Context context, Intent intent) {

        TimeSpentEngine engine = new TimeSpentEngine(context);

        ArrayList<AppUsageInfo> appUsageInfos = engine.getTimeSpent();
        ArrayList<String> overusedApps = overusedApps(context, overuseMinutes, appUsageInfos);
        int totalTimeSpent = 0;
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

        if (overusedApps.size() > 0) {
            String overUsedAppStringified = Arrays.toString(overusedApps.toArray()).replace("[", "").replace("]", "");

             notifText = "You have spent more than an hour on " + overUsedAppStringified + " today.";

        } else {

            for(AppUsageInfo appUsageInfo: appUsageInfos) {
                totalTimeSpent += appUsageInfo.getTimeSpentInMilliseconds();
            }

            totalTimeSpent =  (int) TimeUnit.MILLISECONDS.toMinutes(totalTimeSpent);

            int hours = totalTimeSpent / 60;
            int mins = totalTimeSpent % 60;

            if (hours == 0) {
                notifText = "You have spent " + mins + " minutes on your phone today. Learn more!";
            } else if (hours == 1) {
                notifText = "You have spent " + hours + " hour and " + mins + " mins on your phone today. Learn more!";
            } else {
                notifText = "You have spent " + hours + " hours and " + mins + " mins on your phone today. Learn more!";
            }

        }

        Intent homeScreenLaunchIntent = new Intent(context, HomeScreen.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, homeScreenLaunchIntent, 0);

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

        notificationManager.notify(1, dailyNotification);

    }

    // Create a method that returns apps that you have spent more than an x mins during the day (till now).
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public ArrayList<String> overusedApps(Context context, Long minMInutes, ArrayList<AppUsageInfo> appUsageInfos) {
        ArrayList<String> overusedApps = new ArrayList<>();
        for (AppUsageInfo appUsageInfo: appUsageInfos) {
            if (appUsageInfo.getTimeSpentInMinutes() >= minMInutes) {
                overusedApps.add(appUsageInfo.getAppName());
            }
        }
        return overusedApps;
    }
}
