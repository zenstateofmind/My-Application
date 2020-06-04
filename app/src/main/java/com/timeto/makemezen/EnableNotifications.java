package com.timeto.makemezen;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.amplitude.api.Amplitude;


/**
 * A simple {@link Fragment} subclass.
 */
public class EnableNotifications extends Fragment {

    public EnableNotifications() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ((AppCompatActivity) getActivity()).getSupportActionBar().hide();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View enableNotificationsView = inflater.inflate(R.layout.fragment_enable_notifications, container, false);

        initializeComponents(enableNotificationsView);

        return enableNotificationsView;
    }

    private void initializeComponents(View enableNotificationsView) {

        // Initialize the components
        final Button enableUsageButton = enableNotificationsView.findViewById(R.id.enable_notifs_button);
        View.OnClickListener buttonListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Amplitude.getInstance().logEvent("Starting Home Screen From Notifications Education Screen");
                Intent intent = new Intent(getActivity(), HomeScreen.class);
                startActivity(intent);
            }
        };

        enableUsageButton.setOnClickListener(buttonListener);
    }

}
