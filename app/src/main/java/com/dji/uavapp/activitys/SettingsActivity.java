package com.dji.uavapp.activitys;

import android.os.Bundle;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.dji.uavapp.R;

public class SettingsActivity extends AppCompatActivity {

    private Toolbar settingsToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_settings);
        settingsToolbar = findViewById(R.id.settings_toolbar);
        setSupportActionBar(settingsToolbar);
        settingsToolbar.setTitle("设置");

        setSupportActionBar(settingsToolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(R.drawable.ic_houtui);
        }
    }
}
