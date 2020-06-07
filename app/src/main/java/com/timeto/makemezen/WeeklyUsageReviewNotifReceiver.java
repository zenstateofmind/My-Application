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
import android.util.Log;

import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;

import com.amplitude.api.Amplitude;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class WeeklyUsageReviewNotifReceiver extends BroadcastReceiver {

    private static final String LOG = WeeklyUsageReviewNotifReceiver.class.getSimpleName();
    public static String CHANNEL_ID = "WEEKEND_APP_REMINDER_ID";
    public static String NOTIFICATION_ID = "notification-id";
    private final static AtomicInteger atomic_int_counter = new AtomicInteger(0);

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onReceive(Context context, Intent intent) {
        Amplitude.getInstance().logEvent("Kickstart the Weekly Usage Notification");

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

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void createAndSendNotif(Context context, Intent intent, NotificationManager notificationManager) {

        if (todayIsSunday() && currentHourBetween(9, 12) && !weeklyUsageNotifSent(context)) {

            Long weeklyTimeSpentInMilli = 0L;
            for (int startDay = -6; startDay < 1; startDay++) {
                Long startTime = 0L;
                Long endTime = 0L;

                Calendar startTimeCal = Calendar.getInstance();
                startTimeCal.add(Calendar.DAY_OF_YEAR, startDay);
                startTimeCal.set(Calendar.HOUR_OF_DAY, 0);
                startTimeCal.set(Calendar.MINUTE, 0);
                startTimeCal.set(Calendar.SECOND, 0);
                startTimeCal.set(Calendar.MILLISECOND, 0);
                startTime = startTimeCal.getTimeInMillis();

                if (startDay == 0) {
                    endTime = System.currentTimeMillis();
                } else {
                    Calendar endTimeCal = Calendar.getInstance();
                    endTimeCal.add(Calendar.DAY_OF_YEAR, startDay + 1);
                    endTimeCal.set(Calendar.HOUR_OF_DAY, 0);
                    endTimeCal.set(Calendar.MINUTE, 0);
                    endTimeCal.set(Calendar.SECOND, 0);
                    endTimeCal.set(Calendar.MILLISECOND, 0);
                    endTime = endTimeCal.getTimeInMillis();
                }

                TimeSpentEngine timeSpentEngine = new TimeSpentEngine(context);
                ArrayList<AppUsageInfo> appUsageInfoTuples = timeSpentEngine.getTimeSpent(startTime, endTime);

                for (AppUsageInfo appUsageInfo: appUsageInfoTuples) {
                    weeklyTimeSpentInMilli = weeklyTimeSpentInMilli + appUsageInfo.getTimeSpentInMilliseconds();
                }
            }
            int weeklyTimeSpentInMins = (int) TimeUnit.MILLISECONDS.toMinutes(weeklyTimeSpentInMilli);
            Log.i(LOG, "Time spent this past week on your phone is: " + weeklyTimeSpentInMins);

            int hoursSpent = weeklyTimeSpentInMins/60;
            int minsSpent = weeklyTimeSpentInMins % 60;
            if (weeklyTimeSpentInMins > 0 ) {
                String notifText = "You spent " + hoursSpent + " hours " + minsSpent + " mins on your phone this past week";
                buildAndSendNotif(context, intent, notifText, notificationManager);
                updateWeeklyUsageNotifInfo(context);
            }

            Amplitude.getInstance().logEvent("Sent the Weekly Usage Notification");
        } else {
            Amplitude.getInstance().logEvent("Did not send the Weekly Usage Notification");
        }

    }

    private boolean weeklyUsageNotifSent(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(context.getString(R.string.file_key), Context.MODE_PRIVATE);
        String weeklyNotifSent = sharedPreferences.getString(MakeMeZenUtil.getWeeklyUsageNotifKey(), "");

        if (!weeklyNotifSent.equals("") && weeklyNotifSent.contains(getTodayStartTime()+"")) {
            return true;
        }
        return false;
    }

    private void updateWeeklyUsageNotifInfo(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(context.getString(R.string.file_key), Context.MODE_PRIVATE);
        String weeklyNotifSent = sharedPreferences.getString(MakeMeZenUtil.getWeeklyUsageNotifKey(), "");
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(MakeMeZenUtil.getWeeklyUsageNotifKey(), weeklyNotifSent + MakeMeZenUtil.DIVIDER + getTodayStartTime());
        editor.apply();
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
                .setContentTitle("Weekly Usage Report")
                .setStyle(new NotificationCompat.BigTextStyle().bigText(notifText))
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        Notification dailyNotification = builder.build();

        notificationManager.notify(getID(), dailyNotification);


    }

    public static int getID() {
        return atomic_int_counter.incrementAndGet();
    }

    private boolean todayIsSunday() {
        Calendar calendar = Calendar.getInstance();
        int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
        return (dayOfWeek == Calendar.SUNDAY);
    }

    private Long getTodayStartTime() {
        Calendar c = Calendar.getInstance();
        c.set(Calendar.HOUR_OF_DAY, 0);
        c.set(Calendar.MINUTE, 0);
        c.set(Calendar.SECOND, 0);
        c.set(Calendar.MILLISECOND, 0);
        return c.getTimeInMillis();
    }

    private boolean currentHourBetween(int startTime, int endTime) {
        Calendar cal = Calendar.getInstance();
        int hour = cal.get(Calendar.HOUR_OF_DAY);
        return hour >= startTime && hour <= endTime;
    }
}
