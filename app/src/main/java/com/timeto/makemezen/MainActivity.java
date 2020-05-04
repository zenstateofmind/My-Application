package com.timeto.makemezen;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.amplitude.api.Amplitude;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Amplitude.getInstance().initialize(this, "c2f55b5584feb1ea777a22019b80d190").enableForegroundTracking(getApplication());

        Amplitude.getInstance().logEvent("Time to enable usage access");
    }
}
