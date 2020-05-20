package com.timeto.makemezen;

import android.app.AlarmManager;
import android.app.AppOpsManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import java.util.Calendar;


/**
 * A simple {@link Fragment} subclass.
 * Use the  factory method to
 * create an instance of this fragment.
 */
public class EnableAccessFragment extends Fragment {

    private static final String TAG = EnableAccessFragment.class.getSimpleName();
    private int mInterval = 5;
    private Handler mHandler;
    private boolean goToNotifEducation = false;

    private AlarmManager alarmManager;
    private PendingIntent alarmIntent;

    public EnableAccessFragment() {
        // Required empty public constructor
    }

    @Override
    public void onResume() {
        super.onResume();
        ((AppCompatActivity) getActivity()).getSupportActionBar().hide();
        mHandler = new Handler();
        mStatusChecker.run();
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ((AppCompatActivity) getActivity()).getSupportActionBar().hide();

        if (accessAllowed()) {
            goToNextScreen();
        } else {
            mHandler = new Handler();
            mStatusChecker.run();
        }

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mHandler.removeCallbacks(mStatusChecker);
    }

    Runnable mStatusChecker = new Runnable() {

        @Override
        public void run() {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                if (!accessAllowed()) {
                    goToNotifEducation = true;
                    mHandler.postDelayed(mStatusChecker, mInterval);
                    Log.i(TAG, "We will continue to go down the route of checking");
                } else {
                    goToNextScreen();
                }
            }
        }
    };

    private void goToNextScreen() {
        Log.i(TAG, "Yay! Seems like we got the access needed! Ideally we will jump to the next initiative " +
                "from here");
        if (goToNotifEducation) {
            kickStartAlarmManager();
            Intent intent = new Intent(getActivity(), NotificationEducation.class);
            startActivity(intent);
//                        getActivity().finish();
        } else {
            goToNotifEducation = false;
            Intent intent = new Intent(getActivity(), HomeScreen.class);
            startActivity(intent);
//                        getActivity().finish();

        }
    }

    /**
     * Three types of notifications:
     * 1. Week end review - how many hours of the app used over the entire week
     * 2. Morning review - Encouragement push
     *                      If hours spent yesterday than the day before, praise!
     *                      TODO: This will be added after we enable features to show time spent across different days
     * 3. Evening push - If any app has more than > x hours -> call that out. Else total amount of
     *                  time spent on the app so far.
     */
    private void kickStartAlarmManager() {

        // Set the alarm to start at approximately 2:00 p.m.
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.set(Calendar.HOUR_OF_DAY, 20);
        calendar.set(Calendar.MINUTE, 00);

        alarmManager = (AlarmManager)getContext().getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(getContext(), AppUsageReviewNotifReceiver.class);
        alarmIntent = PendingIntent.getBroadcast(getContext(), 0, intent, 0);

        alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(),
                AlarmManager.INTERVAL_DAY, alarmIntent);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View fragmentEnableAccessView = inflater.inflate(R.layout.fragment_enable_access, container, false);

        initializeComponents(fragmentEnableAccessView);

        return fragmentEnableAccessView;
    }

    /**
     * TODO: After getting access to usage data, make sure that you come back straight into the app.
     */
    private void initializeComponents(View fragmentEnableAccessView) {

        // Initialize the components
        final Button enableUsageButton = fragmentEnableAccessView.findViewById(R.id.enable_usage_button);
        View.OnClickListener buttonListener = new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void onClick(View v) {
                if (!accessAllowed()) {
                    Intent intent = new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS);
                    startActivity(intent);
                } else {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//                        printAppUsage();
//                        Fragment enableNotificationsFragment = new EnableNotifications();
//                        FragmentTransaction transaction = getFragmentManager().beginTransaction();
//                        transaction.replace(R.id.main_activity_fragment_container, enableNotificationsFragment ); // give your fragment container id in first parameter
//                        transaction.addToBackStack(null);  // if written, this transaction will be added to backstack
//                        transaction.commit();

                        Intent intent = new Intent(getActivity(), NotificationEducation.class);
                        startActivity(intent);
//                        getActivity().finish();

                    }
                }
            }
        };
        enableUsageButton.setOnClickListener(buttonListener);
    }


    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private boolean accessAllowed() {
        try {
            ApplicationInfo applicationInfo = getActivity().getPackageManager().getApplicationInfo(getActivity().getPackageName(), 0);
            AppOpsManager appOpsManager = (AppOpsManager) getActivity().getSystemService(Context.APP_OPS_SERVICE);
            int mode = appOpsManager.checkOpNoThrow(AppOpsManager.OPSTR_GET_USAGE_STATS, applicationInfo.uid, applicationInfo.packageName);
            Log.i(TAG, "Do we have access to see usage data? " + (mode == AppOpsManager.MODE_ALLOWED));
            return (mode == AppOpsManager.MODE_ALLOWED);

        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }
    }

}