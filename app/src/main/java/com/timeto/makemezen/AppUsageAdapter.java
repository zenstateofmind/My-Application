package com.timeto.makemezen;

import android.content.res.Resources;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

public class AppUsageAdapter extends RecyclerView.Adapter {

    private ArrayList<AppUsageInfo> dataset;
    private static final String TAG = AppUsageAdapter.class.getSimpleName();
    private int totalTimeSpent = 0;
    private Resources resources;

    //TODO: THIS NEEDS TO BE FIXED. IS IT IN DIP OR PIXEL? VARIES BY PHONE!
    private static int USAGE_BAR_BASE_WIDTH = 0;

    public static class AppUsageViewHolder extends RecyclerView.ViewHolder {

        public TextView appNameSingleView;
        public TextView timeSpentSingleView;
        public ImageView appIconView;
        public LinearLayout usageBar;

        public AppUsageViewHolder(@NonNull View itemView) {
            super(itemView);
            appNameSingleView = (TextView) itemView.findViewById(R.id.app_name_indiv_view);
            timeSpentSingleView = (TextView) itemView.findViewById(R.id.time_spent_indiv_view);
            appIconView = (ImageView) itemView.findViewById(R.id.app_icon_single_view);
            usageBar = (LinearLayout) itemView.findViewById(R.id.usage_bar);
        }
    }

    public static class HeroAppUsageViewHolder extends RecyclerView.ViewHolder {

        public TextView appNameSingleView;
        public TextView timeSpentSingleView;
        public ImageView appIconView;
        public LinearLayout usageBar;
        public TextView totalTimeSpent;

        public HeroAppUsageViewHolder(@NonNull View itemView) {
            super(itemView);
            appNameSingleView = (TextView) itemView.findViewById(R.id.app_name_indiv_view);
            timeSpentSingleView = (TextView) itemView.findViewById(R.id.time_spent_indiv_view);
            appIconView = (ImageView) itemView.findViewById(R.id.app_icon_single_view);
            usageBar = (LinearLayout) itemView.findViewById(R.id.usage_bar);
            totalTimeSpent = (TextView) itemView.findViewById(R.id.total_time_spent);
        }
    }

    public AppUsageAdapter(ArrayList<AppUsageInfo> dataset, Resources resources, int dpWidth) {
        this.dataset = dataset;
        this.resources = resources;
        USAGE_BAR_BASE_WIDTH = dpWidth;
        initializeTimeSpent();
    }

    private void initializeTimeSpent() {
        for(AppUsageInfo appUsageInfo: dataset) {
            totalTimeSpent += appUsageInfo.getTimeSpentInMilliseconds();
        }
        totalTimeSpent =  (int) TimeUnit.MILLISECONDS.toMinutes(totalTimeSpent);

    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0) {
            return 1;
        } else {
            return 2;
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        RecyclerView.ViewHolder viewHolder;
        if (viewType == 1) {
            View indivAppInfo = inflater.inflate(R.layout.hero_single_app_usage_info, parent, false);
            viewHolder = new HeroAppUsageViewHolder(indivAppInfo);
        } else {
            View indivAppInfo = inflater.inflate(R.layout.single_app_usage_info, parent, false);
            viewHolder = new AppUsageViewHolder(indivAppInfo);
        }
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        AppUsageInfo appUsageInfo = dataset.get(position);

        if (position == 0) {
            ((HeroAppUsageViewHolder)holder).appNameSingleView.setText(appUsageInfo.getAppName());
            ((HeroAppUsageViewHolder)holder).timeSpentSingleView.setText(appUsageInfo.getTimeSpentInMinutes().toString()+"m");
            ((HeroAppUsageViewHolder)holder).appIconView.setImageDrawable(appUsageInfo.getAppIcon());
            int hours = totalTimeSpent/60;
            int mins = totalTimeSpent%60;

            if (hours == 0 ) {
                ((HeroAppUsageViewHolder)holder).totalTimeSpent.setText(mins + "m");
            } else {
                ((HeroAppUsageViewHolder)holder).totalTimeSpent.setText(hours+"h " + mins + "m");
            }


            LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) ((HeroAppUsageViewHolder)holder).usageBar.getLayoutParams();
            Log.i(TAG, "App Name: " + appUsageInfo.getAppName() + " Param width: " + params.width);
//            USAGE_BAR_BASE_WIDTH * resources.getDisplayMetrics().density;

            params.width = (int) (USAGE_BAR_BASE_WIDTH * appUsageInfo.getPercentTimeSpent());
//            params.width = (int) (600);

            Log.i(TAG, "App Name: " + appUsageInfo.getAppName() + " time spent: " + params.width);
            ((HeroAppUsageViewHolder)holder).usageBar.setLayoutParams(params);

        } else {
            ((AppUsageViewHolder)holder).appNameSingleView.setText(appUsageInfo.getAppName());
            ((AppUsageViewHolder)holder).timeSpentSingleView.setText(appUsageInfo.getTimeSpentInMinutes().toString()+"m");
            ((AppUsageViewHolder)holder).appIconView.setImageDrawable(appUsageInfo.getAppIcon());

            LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) ((AppUsageViewHolder)holder).usageBar.getLayoutParams();
            Log.i(TAG, "App Name: " + appUsageInfo.getAppName() + " Param width: " + params.width);
            params.width = (int) (USAGE_BAR_BASE_WIDTH * appUsageInfo.getPercentTimeSpent());
            Log.i(TAG, "App Name: " + appUsageInfo.getAppName() + " time spent: " + params.width);
            ((AppUsageViewHolder)holder).usageBar.setLayoutParams(params);
        }

    }

    @Override
    public int getItemCount() {
        return dataset.size();
    }
}