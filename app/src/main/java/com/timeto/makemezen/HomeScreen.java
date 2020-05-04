package com.timeto.makemezen;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Build;
import android.os.Bundle;

import com.amplitude.api.Amplitude;

public class HomeScreen extends AppCompatActivity {

    private RecyclerView.Adapter adapter;
    private RecyclerView.LayoutManager layoutManager;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_screen);
        Amplitude.getInstance().initialize(this, "c2f55b5584feb1ea777a22019b80d190").enableForegroundTracking(getApplication());

        Amplitude.getInstance().logEvent("Open Home Screen");
    }
}
