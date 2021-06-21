package com.dji.uavapp.activitys;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.InputType;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.afollestad.materialdialogs.MaterialDialog;
import com.dji.uavapp.R;
import com.dji.uavapp.util.netutil.ServerService;


public class UserDetailActivity extends AppCompatActivity {

    private Toolbar detailToolbar;

    // 定义线性布局
    private LinearLayout layout_nickname, layout_sex, layout_email, layout_location;

    private TextView showNickName, showSex, showEmail, showLocation;
    SharedPreferences preferences;
    SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_detail);
        detailToolbar = findViewById(R.id.userData_toolbar);
        detailToolbar.setTitle("个人资料");
        setSupportActionBar(detailToolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(R.drawable.ic_houtui);
        }
        //layout_avatar = findViewById(R.id.lay_avatar);
        layout_nickname = findViewById(R.id.lay_nickname);
        layout_sex = findViewById(R.id.lay_sex);
        layout_email = findViewById(R.id.lay_email);
        layout_location = findViewById(R.id.lay_location);
        showNickName = findViewById(R.id.show_name);
        showSex = findViewById(R.id.show_sex);
        showEmail = findViewById(R.id.show_email);
        showLocation = findViewById(R.id.show_location);

        preferences = getSharedPreferences("UserInfo", Context.MODE_PRIVATE);
        //userId = getIntent().getStringExtra("user_edit_id");
        initData();

    }

    // 初始化数据
    private void initData() {
        //List<UserInfo> infos = LitePal.where("userAccount = ?", userId).find(UserInfo.class);
        String name = preferences.getString("nickname", "defaultname");
        String sex = preferences.getString("sex", "男");
        String Email = preferences.getString("Email", "test@test.com");
        String Location = preferences.getString("Location", "西北工业大学");
        //userInfo = infos.get(0);
        //System.out.println("用户详情界面的信息为" + userInfo);
        showNickName.setText(name);
        showSex.setText(sex);
        showEmail.setText(Email);
        showLocation.setText(Location);
    }

    //在活动由不可见变为可见的时候调用
    @Override
    protected void onResume() {
        super.onResume();
        editor = getSharedPreferences("UserInfo", Context.MODE_PRIVATE).edit();

        layout_nickname.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                new MaterialDialog.Builder(UserDetailActivity.this)
                        .title("修改昵称")
                        .inputRangeRes(2, 8, R.color.black)
                        .inputType(InputType.TYPE_CLASS_TEXT)
                        .input("请输入新昵称", preferences.getString("nickname", "default"), new MaterialDialog.InputCallback() {
                            @Override
                            public void onInput(MaterialDialog dialog, CharSequence input) {
                                editor.putString("nickname", input.toString());
                                editor.apply();
                                showNickName.setText(input.toString());
                                String username=preferences.getString("username","default");
                                try {
                                    while(!ServerService.updateUserInfo(username,"nick_name",input.toString()));
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }

                            }
                        })
                        .positiveText("确定")
                        .show();
            }
        });
        layout_sex.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                String[] contentSex = new String[]{"男", "女"};
                new MaterialDialog.Builder(UserDetailActivity.this)
                        .title("修改性别")
                        .items(contentSex)
                        .itemsCallbackSingleChoice(preferences.getString("sex", "default").equals("女") ? 1 : 0, new MaterialDialog.ListCallbackSingleChoice() {
                            @Override
                            public boolean onSelection(MaterialDialog dialog, View view, int which, CharSequence text) {
                                //System.out.println("选择哪一个" + which);
                                //System.out.println("选择的内容是" + text);
                                editor.putString("sex", text.toString());
                                editor.apply();
                                showSex.setText(text.toString());
                                return true;
                            }
                        })
                        .show();
            }
        });
        layout_email.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                new MaterialDialog.Builder(UserDetailActivity.this)
                        .title("修改邮箱")
                        .inputRangeRes(1, 38, R.color.black)
                        .inputType(InputType.TYPE_CLASS_TEXT)
                        .input("请输入新邮箱", preferences.getString("Email", "default@mail.com"), new MaterialDialog.InputCallback() {
                            @Override
                            public void onInput(MaterialDialog dialog, CharSequence input) {
                                //System.out.println(input.toString());
                                editor.putString("Email", input.toString());
                                editor.apply();
                                showEmail.setText(input.toString());
                            }
                        })
                        .positiveText("确定")
                        .show();
            }
        });
        layout_location.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                new MaterialDialog.Builder(UserDetailActivity.this)
                        .title("修改地区")
                        .inputRangeRes(1, 38, R.color.black)
                        .inputType(InputType.TYPE_CLASS_TEXT)
                        .input("请输入新地区", preferences.getString("Location", "defaultLocation"), new MaterialDialog.InputCallback() {
                            @Override
                            public void onInput(MaterialDialog dialog, CharSequence input) {

                                System.out.println(input.toString());
                                editor.putString("Location", input.toString());
                                editor.apply();
                                showLocation.setText(input.toString());
                            }
                        })
                        .positiveText("确定")
                        .show();
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                // 保存更改的数据
                //userInfo.save();
                Intent intent = new Intent();
                intent.putExtra("nickName", showNickName.getText().toString());
                intent.putExtra("email", showEmail.getText().toString());
                intent.putExtra("location", showLocation.getText().toString());
                // intent.putExtra("imagePath", userInfo.getImagePath());
                setResult(RESULT_OK, intent);
               // System.out.println("当前个人信息活动页被销毁！！！");
                UserDetailActivity.this.finish();
                break;
        }
        return true;
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        //System.out.println("个人信息页面被销毁。。。。。。。。。。");
    }
}
