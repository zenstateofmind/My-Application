package com.example.myapplication;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;


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

        enableComponents(enableNotificationsView);

        return enableNotificationsView;
    }

    private void enableComponents(View enableNotificationsView) {
        Button enableNotifsButton = (Button)enableNotificationsView.findViewById(R.id.enable_notifs_button);
        enableNotifsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Intent intent = new Intent();
//                intent.setAction("android.settings.APP_NOTIFICATION_SETTINGS");
//
//                //for Android 5-7
//                intent.putExtra("app_package", getActivity().getPackageName());
//                intent.putExtra("app_uid", getActivity().getApplicationInfo().uid);
//
//                // for Android 8 and above
//                intent.putExtra("android.provider.extra.APP_PACKAGE", getActivity().getPackageName());
//
//                startActivity(intent);

                Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:"+getActivity().getPackageName()));
                startActivityForResult(intent, 0);

            }
        });
    }
}
