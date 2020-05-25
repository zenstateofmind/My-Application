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
import java.util.Random;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class MorningMotivationNotifReceiver extends BroadcastReceiver {

    public static String CHANNEL_ID = "LOOKBACK_APP_REMINDER_ID";
    private final static AtomicInteger atomic_int_counter = new AtomicInteger(0);
    private static final String LOG = MorningMotivationNotifReceiver.class.getSimpleName();

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onReceive(Context context, Intent intent) {
        Amplitude.getInstance().logEvent("Notify the user");

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
                .setContentTitle("Be Mindful! Stay Productive!")
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

        // Get time spent on the phone yesterday
        // Get time spent on phone the day before
        // If time spent yesterday less than the day before -> Notify
        // Else -> Motivation on reducing phone usage --> Create a bunch of sentences and pick one at random

        Log.i(LOG, "Going to start building the notification!");

        long todayInMilli = getCalendar(0).getTimeInMillis();
        if (!motivationNotifSent(context, todayInMilli)) {

            Calendar dayBeforeStartTime = getCalendar(-2);
            Calendar dayBeforeEndTime = getCalendar(-1);

            Calendar yesterdayStartTime = getCalendar(-1);
            Calendar yesterdayEndTime = getCalendar(0);

            TimeSpentEngine engine = new TimeSpentEngine(context);

            ArrayList<AppUsageInfo> dayBeforeAppUsageData = engine.getTimeSpent(dayBeforeStartTime.getTimeInMillis(), dayBeforeEndTime.getTimeInMillis());
            ArrayList<AppUsageInfo> yesterdayAppUsageData = engine.getTimeSpent(yesterdayStartTime.getTimeInMillis(), yesterdayEndTime.getTimeInMillis());

            int dayBeforeTotalTimeSpent = 0;
            int yesterdayTotalTimeSpent = 0;

            for (AppUsageInfo appUsageInfo : dayBeforeAppUsageData) {
                dayBeforeTotalTimeSpent += appUsageInfo.getTimeSpentInMilliseconds();
            }

            for (AppUsageInfo appUsageInfo : yesterdayAppUsageData) {
                yesterdayTotalTimeSpent += appUsageInfo.getTimeSpentInMilliseconds();
            }

            Log.i(LOG, "Time spent on the app day before: " + TimeUnit.MILLISECONDS.toHours(dayBeforeTotalTimeSpent));
            Log.i(LOG, "Time spent on the app yesterday: " + TimeUnit.MILLISECONDS.toHours(yesterdayTotalTimeSpent));

            if (yesterdayTotalTimeSpent < dayBeforeTotalTimeSpent) {
                notifText = "Good Job! Fantastic! You spent less time on your phone yesterday than the day before!";
            } else {
                notifText = getMotivationalQuote();
                Log.i(LOG, "The motivational quote is: " + notifText);
            }

            buildAndSendNotif(context, intent, notifText, notificationManager);
            updateMorningMotivationData(context, todayInMilli);
        }

    }

    private String getMotivationalQuote() {

        ArrayList<String> motivationalQuotes = new ArrayList<>();
        motivationalQuotes.add("Time is the most valuable thing a person can spend. Spend your time on your phone wisely!");
        motivationalQuotes.add("The key is in not spending time, but in investing it. Make the best use of your time!");
        motivationalQuotes.add("The way we spend our time defines who we are. Make the best use of your time!");
        motivationalQuotes.add("Change your 24 hours and you will change your life. Make the best use of your time!");
        motivationalQuotes.add("All we have to decide is what to do with the time that is given us. Make the best use of your time!");
        motivationalQuotes.add("It’s really clear that the most precious resource we all have is time. Make the best use of your time!");
        Random random = new Random();
        int index = random.nextInt(motivationalQuotes.size());
        return motivationalQuotes.get(index);

    }

    private void updateMorningMotivationData(Context context, long dateInMilli) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(context.getString(R.string.file_key), Context.MODE_PRIVATE);
        String key = MakeMeZenUtil.getMotivationKey();
        String currentVal = sharedPreferences.getString(key, "");
        SharedPreferences.Editor editor = sharedPreferences.edit();
        String newVal = "";
        if (!currentVal.isEmpty()) {
            newVal = currentVal + MakeMeZenUtil.DIVIDER + dateInMilli;
        } else {
            newVal = dateInMilli + "";
        }
        editor.putString(key, newVal);
        editor.apply();
    }

    private boolean motivationNotifSent(Context context, long dateInMilli) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(context.getString(R.string.file_key), Context.MODE_PRIVATE);
        String currentVal = sharedPreferences.getString(MakeMeZenUtil.getMotivationKey(), "");

        if (!currentVal.isEmpty()) {
            if (currentVal.contains(dateInMilli+"")) {
                Log.i(LOG, "Motivation Notification has already been sent! ");
                return true;
            }
        }

        Log.i(LOG, "Motivation Notification has not been sent! ");
        return false;
    }



    private Calendar getCalendar(int amount) {
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DAY_OF_YEAR, amount);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        return cal;
    }

    public static int getID() {
        return atomic_int_counter.incrementAndGet();
    }

}
