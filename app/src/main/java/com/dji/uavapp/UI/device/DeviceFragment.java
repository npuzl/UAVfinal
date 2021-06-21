package com.dji.uavapp.UI.device;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;

import com.dji.uavapp.R;
import com.dji.uavapp.activitys.CameraMapActivity;
import com.dji.uavapp.activitys.MainActivity;
import com.dji.uavapp.base.DJIApplication;
import com.dji.uavapp.util.netutil.ServerService;

import dji.common.error.DJIError;
import dji.common.realname.AppActivationState;
import dji.common.useraccount.UserAccountState;
import dji.common.util.CommonCallbacks;
import dji.sdk.base.BaseProduct;
import dji.sdk.products.Aircraft;
import dji.sdk.realname.AppActivationManager;
import dji.sdk.sdkmanager.DJISDKManager;
import dji.sdk.useraccount.UserAccountManager;

public class DeviceFragment extends Fragment {
    private DeviceViewModel deviceViewModel;

    private static final String TAG = MainActivity.class.getName();
    protected Button btn_fpv;
    //protected TextView bindingStateTV;
    protected TextView appActivationStateTV;
    protected TextView connectionStateTV;
    protected TextView productInfoTV;
    private AppActivationManager appActivationManager;
    private AppActivationState.AppActivationStateListener activationStateListener;
    //private AircraftBindingState.AircraftBindingStateListener bindingStateListener;
    private View root;
    SharedPreferences.Editor editor;
    SharedPreferences preferences;
    boolean status;

    @SuppressLint("CommitPrefEdits")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        deviceViewModel = new ViewModelProvider(this).get(DeviceViewModel.class);
        root = inflater.inflate(R.layout.fragment_device, container, false);
        btn_fpv = root.findViewById(R.id.toCameraActivity);
        editor = getActivity().getSharedPreferences("UserInfo", Context.MODE_PRIVATE).edit();
        preferences = getActivity().getSharedPreferences("UserInfo", Context.MODE_PRIVATE);

        IntentFilter filter = new IntentFilter();
        filter.addAction(DJIApplication.FLAG_CONNECTION_CHANGE);
        getActivity().registerReceiver(mReceiver, filter);

        return root;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        deviceViewModel = ViewModelProviders.of(this).get(DeviceViewModel.class);
        btn_fpv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), CameraMapActivity.class);
                startActivity(intent);
            }
        });
        MainActivity.isAppStarted = true;
        initUI();
        initData();

    }

    @Override
    public void onResume() {
        super.onResume();
        status = preferences.getBoolean("login", false);
        appActivationStateTV.setText("登录状态:" + ((status)?"已登录":"未登录"));
        refreshSDKRelativeUI();
    }

    private void initUI() {

        productInfoTV = root.findViewById(R.id.tv_product_info);
        connectionStateTV = root.findViewById(R.id.tv_connection_state);
        //bindingStateTV = root.findViewById(R.id.tv_binding_state_info);
        appActivationStateTV = root.findViewById(R.id.tv_activation_state_info);
        //btn_fpv.setEnabled(false);
        status = preferences.getBoolean("login", false);
        refreshSDKRelativeUI();

    }

    private void initData() {
        setUpListener();

        appActivationManager = DJISDKManager.getInstance().getAppActivationManager();

        if (appActivationManager != null) {
            appActivationManager.addAppActivationStateListener(activationStateListener);
            //appActivationManager.addAircraftBindingStateListener(bindingStateListener);
            getActivity().runOnUiThread(new Runnable() {
                @SuppressLint("SetTextI18n")
                @Override
                public void run() {
                    appActivationStateTV.setText("登录状态: " + appActivationManager.getAppActivationState());
                    //bindingStateTV.setText("binding state: " + appActivationManager.getAircraftBindingState());
                    Log.e(TAG, "appTest " + appActivationManager.getAppActivationState());
                }
            });
        }
    }


    private void setUpListener() {
        // Example of Listener
        activationStateListener = new AppActivationState.AppActivationStateListener() {
            @Override
            public void onUpdate(final AppActivationState appActivationState) {

                getActivity().runOnUiThread(new Runnable() {
                    @SuppressLint("SetTextI18n")
                    @Override
                    public void run() {
                        appActivationStateTV.setText("登录状态： " + appActivationState);
                        if (appActivationState == AppActivationState.ACTIVATED) {
                            editor.putBoolean("login",true);
                            editor.apply();
                        } else {
                            editor.putBoolean("login", false);
                            editor.apply();
                        }
                    }
                });
            }
        };
    }

    private void showToast(final String toastMsg) {

        Handler handler = new Handler(Looper.getMainLooper());
        handler.post(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(getActivity(), toastMsg, Toast.LENGTH_LONG).show();
            }
        });
    }

    protected BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            refreshSDKRelativeUI();
        }
    };

    private void refreshSDKRelativeUI() {
        BaseProduct mProduct = DJIApplication.getProductInstance();

        if (null != mProduct && mProduct.isConnected()) {
            Log.v(TAG, "refreshSDK: True");

            String str = mProduct instanceof Aircraft ? "DJIAircraft" : "DJIHandHeld";
            connectionStateTV.setText("连接状态: " + str + " 连接成功");

            if (null != mProduct.getModel()) {
                productInfoTV.setText("" + mProduct.getModel().getDisplayName());
                //btn_fpv.setEnabled(true);
            } else {
                productInfoTV.setText("连接状态：无连接");
                //btn_fpv.setEnabled(false);
            }

        } else {
            Log.v(TAG, "refreshSDK: False");
            productInfoTV.setText("未连接无人机");
            connectionStateTV.setText("连接状态：无连接");
            //btn_fpv.setEnabled(false);
        }
    }


}
