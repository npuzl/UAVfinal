package com.dji.uavapp.activitys;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.dji.uavapp.R;
import com.dji.uavapp.util.netutil.ServerService;

//import org.opencv.android.BaseLoaderCallback;
//import org.opencv.android.LoaderCallbackInterface;
//import org.opencv.android.OpenCVLoader;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import dji.common.error.DJIError;
import dji.common.error.DJISDKError;
import dji.common.useraccount.UserAccountState;
import dji.common.util.CommonCallbacks;
import dji.sdk.base.BaseComponent;
import dji.sdk.base.BaseProduct;
import dji.sdk.sdkmanager.DJISDKInitEvent;
import dji.sdk.sdkmanager.DJISDKManager;
import dji.sdk.useraccount.UserAccountInformation;
import dji.sdk.useraccount.UserAccountManager;

import static android.os.SystemClock.sleep;

public class LauncherActivity extends AppCompatActivity {

    private static final String TAG = LauncherActivity.class.getName();
    public static final String FLAG_CONNECTION_CHANGE = "dji_sdk_connection_change";
    private Handler mHandler;
    private Handler handler1 = new Handler();
    SharedPreferences.Editor editor;
    SharedPreferences preferences;
    private boolean isLogin;
    /**
     * 权限列表
     *
     * @author zl
     */
    private static final String[] REQUIRED_PERMISSION_LIST = new String[]{
            Manifest.permission.VIBRATE,
            Manifest.permission.INTERNET,
            Manifest.permission.ACCESS_WIFI_STATE,
            Manifest.permission.WAKE_LOCK,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_NETWORK_STATE,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.CHANGE_WIFI_STATE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.BLUETOOTH,
            Manifest.permission.BLUETOOTH_ADMIN,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.READ_PHONE_STATE,
    };
    private List<String> missingPermission = new ArrayList<>();
    private AtomicBoolean isRegistrationInProgress = new AtomicBoolean(false);
    private static final int REQUEST_PERMISSION_CODE = 12345;

    /**
     * 这个方法里面现有的是启动时先检查权限是否都被赋予了，\br\
     * 然后放置布局后启用一个新线程，倒计时后进入MainActivity
     *
     * @param savedInstanceState 保存状态实例
     * @author zl
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        showToast("OnCreate!");
        // When the compile and target version is higher than 22, please request the following permission at runtime to ensure the SDK works well.
        checkAndRequestPermissions();

        setContentView(R.layout.activity_launcher);

        mHandler = new Handler(Looper.getMainLooper());

        editor = getSharedPreferences("UserInfo", Context.MODE_PRIVATE).edit();
        preferences = getSharedPreferences("UserInfo", Context.MODE_PRIVATE);
        if (!preferences.getBoolean("login", false)) {//如果未登录
            new Thread(new Runnable() {
                @Override
                public void run() {
                    showToast("开始登录");
                    loginAccount();
                }
            }).start();
        } else {
            Intent intent = new Intent(LauncherActivity.this, MainActivity.class);
            startActivity(intent);
        }


        editor.apply();

    }

    private void logoutAccount() {
        UserAccountManager.getInstance().logoutOfDJIUserAccount(new CommonCallbacks.CompletionCallback() {
            @Override
            public void onResult(DJIError error) {
                if (null == error) {
                    showToast("Logout Success");
                    editor.putBoolean("login", false);
                    editor.apply();
                } else {
                    editor.putBoolean("login", true);
                    editor.apply();
                    showToast("Logout Error:"
                            + error.getDescription());
                }
                //appActivationStateTV.setText("activation state: " + appActivationManager.getAppActivationState());
            }
        });
    }

    @Override
    protected void onDestroy() {
        DJISDKManager.getInstance().destroy();
        super.onDestroy();
    }

    /**
     * Checks if there is any missing permissions, and
     * requests runtime permission if needed.
     */
    private void checkAndRequestPermissions() {
        // Check for permissions
        for (String eachPermission : REQUIRED_PERMISSION_LIST) {
            if (ContextCompat.checkSelfPermission(this, eachPermission) != PackageManager.PERMISSION_GRANTED) {
                missingPermission.add(eachPermission);
            }
        }
        // Request for missing permissions
        if (missingPermission.isEmpty()) {
            startSDKRegistration();
        } else {
            showToast("Need to grant the permissions!");
            ActivityCompat.requestPermissions(this,
                    missingPermission.toArray(new String[missingPermission.size()]),
                    REQUEST_PERMISSION_CODE);
        }
    }

    /**
     * Result of runtime permission request
     */
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        // Check for granted permission and remove from missing list
        if (requestCode == REQUEST_PERMISSION_CODE) {
            for (int i = grantResults.length - 1; i >= 0; i--) {
                if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                    missingPermission.remove(permissions[i]);
                }
            }
        }
        // If there is enough permission, we will start the registration
        if (missingPermission.isEmpty()) {
            startSDKRegistration();
        } else {
            showToast("Missing permissions!!!");
        }
    }

    private void startSDKRegistration() {
        if (isRegistrationInProgress.compareAndSet(false, true)) {
            AsyncTask.execute(new Runnable() {
                @Override
                public void run() {
                    //showToast("registering, pls wait...");
                    DJISDKManager.getInstance().registerApp(LauncherActivity.this.getApplicationContext(), new DJISDKManager.SDKManagerCallback() {
                        @Override
                        public void onRegister(DJIError djiError) {
                            if (djiError == DJISDKError.REGISTRATION_SUCCESS) {
                                //showToast("Register Success");
                                DJISDKManager.getInstance().startConnectionToProduct();
                            } else {
                                showToast("Register sdk fails, please check the bundle id and network connection!");
                            }
                            Log.v(TAG, djiError.getDescription());
                        }

                        @Override
                        public void onProductDisconnect() {
                            Log.d(TAG, "onProductDisconnect");
                            showToast("Product Disconnected");
                            notifyStatusChange();
                        }

                        @Override
                        public void onProductConnect(BaseProduct baseProduct) {
                            Log.d(TAG, String.format("onProductConnect newProduct:%s", baseProduct));
                            showToast("Product Connected");
                            notifyStatusChange();
                        }

                        @Override
                        public void onProductChanged(BaseProduct baseProduct) {

                        }

                        @Override
                        public void onComponentChange(BaseProduct.ComponentKey componentKey, BaseComponent oldComponent,
                                                      BaseComponent newComponent) {

                            if (newComponent != null) {
                                newComponent.setComponentListener(new BaseComponent.ComponentListener() {
                                    @Override
                                    public void onConnectivityChange(boolean isConnected) {
                                        Log.d(TAG, "onComponentConnectivityChanged: " + isConnected);
                                        notifyStatusChange();
                                    }
                                });
                            }
                            Log.d(TAG,
                                    String.format("onComponentChange key:%s, oldComponent:%s, newComponent:%s",
                                            componentKey,
                                            oldComponent,
                                            newComponent));

                        }

                        @Override
                        public void onInitProcess(DJISDKInitEvent djisdkInitEvent, int i) {

                        }

                        @Override
                        public void onDatabaseDownloadProgress(long l, long l1) {

                        }

                    });
                }
            });
        }
    }

    private void notifyStatusChange() {
        mHandler.removeCallbacks(updateRunnable);
        mHandler.postDelayed(updateRunnable, 500);
    }

    private final Runnable updateRunnable = () -> {
        Intent intent = new Intent(FLAG_CONNECTION_CHANGE);
        sendBroadcast(intent);
    };

    private void showToast(final String toastMsg) {
        Handler handler = new Handler(Looper.getMainLooper());
        handler.post(() -> Toast.makeText(getApplicationContext(), toastMsg, Toast.LENGTH_LONG).show());
    }

    private void loginAccount() {
        UserAccountManager.getInstance().logIntoDJIUserAccount(this, new CommonCallbacks.CompletionCallbackWith<UserAccountState>() {
            @Override
            public void onSuccess(UserAccountState userAccountState) {
                editor.putBoolean("login", true);
                editor.apply();
            }

            @Override
            public void onFailure(DJIError error) {
                showToast("登录错误:"
                        + error.getDescription());
                editor.putBoolean("login", false);
                editor.apply();
                loginAccount();
            }
        });
        UserAccountManager.getInstance().getLoggedInDJIUserAccountName(new CommonCallbacks.CompletionCallbackWith<String>() {
            @Override
            public void onSuccess(String s) {
                editor.putString("id", s);
                editor.putBoolean("login", true);
                editor.apply();
                showToast("id:" + s);
            }

            @Override
            public void onFailure(DJIError djiError) {
                editor.putBoolean("login", false);
                editor.apply();
            }
        });
        if (preferences.getBoolean("login", true)) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    showToast("登录成功！进入主界面");
                    Intent intent = new Intent(LauncherActivity.this, MainActivity.class);
                    startActivity(intent);
                }
            });
        }

    }

    @Override
    protected void onRestart() {
        super.onRestart();
    }

//    private BaseLoaderCallback mLoaderCallback = new BaseLoaderCallback(this) {
//
//        @Override
//        public void onManagerConnected(int status) {
//            // TODO Auto-generated method stub
//            switch (status) {
//                case BaseLoaderCallback.SUCCESS:
//                    Log.i(TAG, "成功加载");
//                    break;
//                default:
//                    super.onManagerConnected(status);
//                    Log.i(TAG, "加载失败");
//                    break;
//            }
//
//        }
//    };
//
//    @Override
//    public void onResume() {
//        super.onResume();
////        if (!OpenCVLoader.initDebug()) {
////            Log.d(TAG, "Internal OpenCV library not found. Using OpenCV Manager for initialization");
////            OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION, this, mLoaderCallback);
////        } else {
////            Log.d(TAG, "OpenCV library found inside package. Using it!");
////            mLoaderCallback.onManagerConnected(LoaderCallbackInterface.SUCCESS);
////        }
//    }
}

