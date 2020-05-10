package com.timeto.makemezen;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

import com.amplitude.api.Amplitude;

import java.util.ArrayList;
import java.util.Calendar;


/**
 * A simple {@link Fragment} subclass.
 */
public class AppUsageListFragment extends Fragment {

    private RecyclerView.Adapter adapter;
    private RecyclerView.LayoutManager layoutManager;
    public static String CHANNEL_ID = "APP_REMINDER_ID";
    public static String NOTIFICATION_ID = "notification-id";

    private AlarmManager alarmManager;
    private PendingIntent alarmIntent;

    public AppUsageListFragment() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
//        kickStartAlarmManager();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ((AppCompatActivity) getActivity()).getSupportActionBar().hide();

        // finally change the color
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getActivity().getWindow();

            // clear FLAG_TRANSLUCENT_STATUS flag:
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

            // add FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS flag to the window
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);

            window.setStatusBarColor(getActivity().getResources().getColor(R.color.red));
        }
//        testNotify();
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onResume() {
        super.onResume();
        Amplitude.getInstance().logEvent("Resume app usage list");
        setUpTimeSpentRecycleView(getView());
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View appUsageListFragmentView = inflater.inflate(R.layout.fragment_app_usage_list, container, false);
        setUpTimeSpentRecycleView(appUsageListFragmentView);
        Amplitude.getInstance().logEvent("Create app usage list");
        return appUsageListFragmentView;
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void setUpTimeSpentRecycleView(View appUsageListFragmentView) {
        TimeSpentEngine timeSpentEngine = new TimeSpentEngine(getContext());
        ArrayList<AppUsageInfo> timeSpentPerApp = timeSpentEngine.getTimeSpent();

        RecyclerView recyclerView = (RecyclerView) appUsageListFragmentView.findViewById(R.id.app_usage_data_recycler_view);
        recyclerView.setHasFixedSize(true);

        layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);

        DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
        int pxWidth = (int)(displayMetrics.widthPixels * .5);
        float dpWidth = (float)((displayMetrics.widthPixels) / displayMetrics.density * .51);
//
//        final float scale = getContext().getResources().getDisplayMetrics().density;
//        int pixels = (int) (dpWidth * scale + 0.5f);

        adapter = new AppUsageAdapter(timeSpentPerApp, getResources(), (int)pxWidth);
        recyclerView.setAdapter(adapter);


    }

}
