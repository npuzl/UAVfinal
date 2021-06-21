package com.dji.uavapp.UI.my;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.dji.uavapp.R;
import com.dji.uavapp.activitys.MoreInfoActivity;
import com.dji.uavapp.activitys.SettingsActivity;
import com.dji.uavapp.activitys.UserDetailActivity;

import org.jetbrains.annotations.NotNull;

import dji.common.error.DJIError;
import dji.common.useraccount.UserAccountState;
import dji.common.util.CommonCallbacks;
import dji.sdk.useraccount.UserAccountManager;


//飞行里程与飞行时间的更新？
//头像的更新

public class MyFragment extends Fragment {

    private static final String TAG = "MyActivity";

    private Button userInfoBtn;
    private Button settingBtn;
    private Button moreBtn;
    private Button logOutBtn;
    private Button toImgTest;
    //private TextView userNickName, userSignature;
    private TextView userNickName, Duration, Mileage;

    SharedPreferences preferences;
    SharedPreferences.Editor editor;

    @Nullable
    @Override
    public View onCreateView(@NonNull @NotNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_my, container, false);
        preferences = getActivity().getSharedPreferences("UserInfo", Context.MODE_PRIVATE);
        userInfoBtn = (Button) root.findViewById(R.id.tv_person_information);
        settingBtn = (Button) root.findViewById(R.id.tv_settings);
        moreBtn = (Button) root.findViewById(R.id.tv_more_information);
        logOutBtn = (Button) root.findViewById(R.id.tv_logout);
        userNickName = (TextView) root.findViewById(R.id.username);
        Duration = (TextView) root.findViewById(R.id.duration);
        Mileage = (TextView) root.findViewById(R.id.Mileage);
        toImgTest=(Button)root.findViewById(R.id.to_imgtest);
        editor = getActivity().getSharedPreferences("UserInfo", Context.MODE_PRIVATE).edit();
        editor.apply();
        //showToast("oncreateview成功");
        return root;
    }

    //在活动由不可见变为可见的时候调用
    @Override
    public void onResume() {
        super.onResume();
        //showToast("当前MyActivity活动又被加载onStart");
        boolean flag = preferences.getBoolean("login", false);
        if (flag) {
            userNickName.setText(preferences.getString("nickname", "default"));
            int flytime = Integer.parseInt(preferences.getString("flytime", "0"));
            //showToast(flytime+"");
            int hour = flytime/ 60;
            int min = flytime - 60 * hour;
            String durationText = "飞行总时间 " + hour + "小时" + min + "分钟";

            Duration.setText(durationText);
            Mileage.setText("飞行总里程 " + preferences.getString("flymiles", "0") + "公里");
        } else {
            userNickName.setText("未登录");
            Duration.setText("");
            Mileage.setText("");
        }

        userInfoBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (flag) {
                    String userNickNameText = userNickName.getText().toString();
                    //第一个是关键词，第二个是关键词对应的内容
                    editor.putString("nickname", userNickNameText);
                    editor.apply();
                    //个人资料
                    Intent intent = new Intent(getActivity(), UserDetailActivity.class);
                    // startActivity(intent);
                    startActivityForResult(intent, 3);
                } else {
                    loginAccount();
                }
            }
        });
        settingBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //设置
                Intent intent = new Intent(getActivity(), SettingsActivity.class);
                startActivity(intent);
            }
        });

        moreBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //更多
                Intent intent = new Intent(getActivity(), MoreInfoActivity.class);
                startActivity(intent);
            }
        });
        logOutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logoutAccount();
                userNickName.setText("未登录");
                Duration.setText("");
                Mileage.setText("");
            }
        });
        toImgTest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(),ImgTestActivity.class);
                startActivity(intent);
            }
        });
    }

    private void logoutAccount() {
        UserAccountManager.getInstance().logoutOfDJIUserAccount(new CommonCallbacks.CompletionCallback() {
            @Override
            public void onResult(DJIError error) {
                if (null == error) {
                    showToast("Logout Success");
                    editor.putString("login", "未登录");
                    editor.apply();
                } else {
                    editor.putString("login", "未登录");
                    editor.apply();
                    showToast("Logout Error:"
                            + error.getDescription());
                }
                //appActivationStateTV.setText("activation state: " + appActivationManager.getAppActivationState());
            }
        });
    }

    private void showToast(final String toastMsg) {
        Handler handler = new Handler(Looper.getMainLooper());
        handler.post(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(getActivity().getApplicationContext(), toastMsg, Toast.LENGTH_LONG).show();
            }
        });

    }

    private void loginAccount() {
        CommonCallbacks.CompletionCallbackWith<UserAccountState> completionCallbackWith = new CommonCallbacks.CompletionCallbackWith<UserAccountState>() {
            @Override
            public void onSuccess(final UserAccountState userAccountState) {
                //showToast("登录成功");
                editor.putBoolean("login", true);
                editor.apply();
            }

            @Override
            public void onFailure(DJIError error) {
                showToast("登录错误:"
                        + error.getDescription());
                editor.putBoolean("login",false);
                editor.apply();
            }
        };
        UserAccountManager.getInstance().logIntoDJIUserAccount(getActivity(), completionCallbackWith);
    }

}
