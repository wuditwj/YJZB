package io.agora.tutorials.activity;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.Switch;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.agora.tutorials.customizedvideosource.R;
import io.agora.tutorials.service.CalledService;

public class SettingActivity extends AppCompatActivity {
    //标题栏
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    //开关
    @BindView(R.id.mute_switch)
    Switch muteSwitch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        ButterKnife.bind(this);
        init();
    }

    private void init() {
        setSupportActionBar(toolbar);
        //设置是否有NvagitionIcon（返回图标）
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        //点击返回图标
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        //判断勿扰状态
        SharedPreferences sharedPreferences = getSharedPreferences("login", Context.MODE_PRIVATE);
        boolean flag = sharedPreferences.getBoolean("mute", false);
        muteSwitch.setChecked(flag);
        muteSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    //开启免打扰,就是关闭服务
                    if (isServiceRunning(SettingActivity.this, "io.agora.tutorials.service.CalledService")) {
                        //关闭服务
                        stopService(new Intent(SettingActivity.this, CalledService.class));
                        setMute(true);
                    } else {
                    }
                } else {
                    //关闭免打扰,就是开启服务
                    if (isServiceRunning(SettingActivity.this, "io.agora.tutorials.service.CalledService")) {
                    } else {
                        //开启服务
                        startService(new Intent(SettingActivity.this, CalledService.class));
                        setMute(false);
                    }
                }
            }
        });
    }

    /**
     * 存储勿扰状态
     * @param flag
     */
    private void setMute(boolean flag){
        SharedPreferences sharedPreferences = getSharedPreferences("login", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("mute", flag);
        editor.commit();
    }

    /**
     * 判断服务是否开启
     *
     * @return
     */
    public static boolean isServiceRunning(Context context, String ServiceName) {
        if (TextUtils.isEmpty(ServiceName)) {
            return false;
        }
        ActivityManager myManager = (ActivityManager) context
                .getSystemService(Context.ACTIVITY_SERVICE);
        ArrayList<ActivityManager.RunningServiceInfo> runningService = (ArrayList<ActivityManager.RunningServiceInfo>) myManager
                .getRunningServices(30);
        for (int i = 0; i < runningService.size(); i++) {
            if (runningService.get(i).service.getClassName().toString()
                    .equals(ServiceName)) {
                return true;
            }
        }
        return false;
    }
}
