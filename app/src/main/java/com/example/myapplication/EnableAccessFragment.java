package com.example.myapplication;

import android.app.AppOpsManager;
import android.app.usage.UsageEvents;
import android.app.usage.UsageStatsManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.os.Handler;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;


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

    public EnableAccessFragment() {
        // Required empty public constructor
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ((AppCompatActivity) getActivity()).getSupportActionBar().hide();
        mHandler = new Handler();
        mStatusChecker.run();
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
                    Log.i(TAG, "Yay! Seems like we got the access needed! Ideally we will jump to the next initiative " +
                            "from here");
                    if (goToNotifEducation) {
                        Intent intent = new Intent(getActivity(), NotificationEducation.class);
                        startActivity(intent);
                    } else {
                        goToNotifEducation = false;
                        Intent intent = new Intent(getActivity(), HomeScreen.class);
                        startActivity(intent);

                    }
                }
            }
        }
    };

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