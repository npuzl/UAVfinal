package com.dji.uavapp.activitys;

import android.Manifest;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.dji.uavapp.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import dji.common.realname.AircraftBindingState;
import dji.common.realname.AppActivationState;
import dji.sdk.realname.AppActivationManager;


public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getName();
    protected TextView bindingStateTV;
    protected TextView appActivationStateTV;
    private AppActivationManager appActivationManager;
    private AppActivationState.AppActivationStateListener activationStateListener;
    private AircraftBindingState.AircraftBindingStateListener bindingStateListener;

    public static boolean isAppStarted = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        BottomNavigationView bottomNavigationView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_device, R.id.navigation_gallery, R.id.navigation_my)
                .build();

        NavController navController = Navigation.findNavController(MainActivity.this,R.id.nav_host_fragment);
        //NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(bottomNavigationView, navController);


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.VIBRATE,
                            Manifest.permission.INTERNET, Manifest.permission.ACCESS_WIFI_STATE,
                            Manifest.permission.WAKE_LOCK, Manifest.permission.ACCESS_COARSE_LOCATION,
                            Manifest.permission.ACCESS_NETWORK_STATE, Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.CHANGE_WIFI_STATE, Manifest.permission.MOUNT_UNMOUNT_FILESYSTEMS,
                            Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.SYSTEM_ALERT_WINDOW,
                            Manifest.permission.READ_PHONE_STATE,
                    }
                    , 1);
        }
    }


    @Override
    public void onPause() {
        Log.e(TAG, "onPause");
        super.onPause();
    }

    @Override
    public void onStop() {
        Log.e(TAG, "onStop");
        super.onStop();
    }

    public void onReturn(View view) {
        Log.e(TAG, "onReturn");
        this.finish();
    }


    @Override
    public void onResume() {
        Log.e(TAG, "onResume");
        //setUpListener();
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        Log.e(TAG, "onDestroy");
        isAppStarted=false;
        //tearDownListener();
        super.onDestroy();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode==KeyEvent.KEYCODE_BACK){
            moveTaskToBack(true);
//            Intent setIntent = new Intent(Intent.ACTION_MAIN);
//            setIntent.addCategory(Intent.CATEGORY_HOME);
//            setIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//            startActivity(setIntent);
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    public static boolean isStarted() {
        return isAppStarted;
    }

}