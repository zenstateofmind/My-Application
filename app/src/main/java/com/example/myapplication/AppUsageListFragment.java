package com.example.myapplication;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 */
public class AppUsageListFragment extends Fragment {

    private RecyclerView.Adapter adapter;
    private RecyclerView.LayoutManager layoutManager;

    public AppUsageListFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ((AppCompatActivity) getActivity()).getSupportActionBar().hide();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View appUsageListFragmentView = inflater.inflate(R.layout.fragment_app_usage_list, container, false);
        setUpTimeSpentRecycleView(appUsageListFragmentView);
        return appUsageListFragmentView;
    }

    private void setUpTimeSpentRecycleView(View appUsageListFragmentView) {
        TimeSpentEngine timeSpentEngine = new TimeSpentEngine(getContext());
        ArrayList<AppUsageInfo> timeSpentPerApp = timeSpentEngine.getTimeSpent();

        RecyclerView recyclerView = (RecyclerView) appUsageListFragmentView.findViewById(R.id.app_usage_data_recycler_view);
        recyclerView.setHasFixedSize(true);

        layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);

        adapter = new AppUsageAdapter(timeSpentPerApp);
        recyclerView.setAdapter(adapter);
    }

}
