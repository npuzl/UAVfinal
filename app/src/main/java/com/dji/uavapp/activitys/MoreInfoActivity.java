package com.dji.uavapp.activitys;

import android.os.Bundle;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.dji.uavapp.R;

public class MoreInfoActivity extends AppCompatActivity {

    private Toolbar moreinfoToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_moreinfo);
        moreinfoToolbar = findViewById(R.id.moreinfo_toolbar);
        setSupportActionBar(moreinfoToolbar);
        moreinfoToolbar.setTitle("更多");
        setSupportActionBar(moreinfoToolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(R.drawable.ic_houtui);
        }
    }

}
