package com.example.myapplication;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class AppUsageAdapter extends RecyclerView.Adapter<AppUsageAdapter.AppUsageViewHolder> {

    private ArrayList<AppUsageInfo> dataset;

    public static class AppUsageViewHolder extends RecyclerView.ViewHolder {

        public TextView appNameSingleView;
        public TextView timeSpentSingleView;
        public ImageView appIconView;

        public AppUsageViewHolder(@NonNull View itemView) {
            super(itemView);
            appNameSingleView = (TextView) itemView.findViewById(R.id.app_name_indiv_view);
            timeSpentSingleView = (TextView) itemView.findViewById(R.id.time_spent_indiv_view);
            appIconView = (ImageView) itemView.findViewById(R.id.app_icon_single_view);
        }
    }

    public AppUsageAdapter(ArrayList<AppUsageInfo> dataset) {
        this.dataset = dataset;
    }

    @NonNull
    @Override
    public AppUsageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View indivAppInfo = inflater.inflate(R.layout.single_app_usage_info, parent, false);

        AppUsageViewHolder viewHolder = new AppUsageViewHolder(indivAppInfo);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull AppUsageAdapter.AppUsageViewHolder holder, int position) {
        AppUsageInfo appUsageInfo = dataset.get(position);

        holder.appNameSingleView.setText(appUsageInfo.getAppName());
        holder.timeSpentSingleView.setText(appUsageInfo.getTimeSpentInMinutes().toString()+"m");
        holder.appIconView.setImageDrawable(appUsageInfo.getAppIcon());
    }

    @Override
    public int getItemCount() {
        return dataset.size();
    }
}