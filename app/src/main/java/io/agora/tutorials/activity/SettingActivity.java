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
import android.widget.Toast;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.agora.tutorials.application.MyApplication;
import io.agora.tutorials.customizedvideosource.R;
import io.agora.tutorials.entity.CallStatus;
import io.agora.tutorials.entity.MuteInfo;
import io.agora.tutorials.net.NetClient;
import io.agora.tutorials.service.CalledService;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

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
        NetClient.getInstance().getTreatrueApi().getMute(MyApplication.getInstance().getUserInfo().getUser_id()).enqueue(new Callback<MuteInfo>() {
            @Override
            public void onResponse(Call<MuteInfo> call, Response<MuteInfo> response) {
                if (response.isSuccessful()) {
                    MuteInfo muteInfo = response.body();
                    if (muteInfo != null) {
                        //1代表可接通,2代表勿扰
                        if (muteInfo.getData().getRest().equals("2")) {
                            muteSwitch.setChecked(true);
                        } else {
                            muteSwitch.setChecked(false);
                        }
                    } else {
                        Log.i("--==>>", "未知错误");
                    }
                }
            }

            @Override
            public void onFailure(Call<MuteInfo> call, Throwable t) {
                Log.i("--==>>", "查询勿扰状态请求失败" + t.getMessage());
            }
        });
        //判断勿扰状态
        muteSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    NetClient.getInstance().getTreatrueApi().setMute(MyApplication.getInstance().getUserInfo().getUser_id(),
                            2).enqueue(new Callback<CallStatus>() {
                        @Override
                        public void onResponse(Call<CallStatus> call, Response<CallStatus> response) {
                            showMute(response);
                        }

                        @Override
                        public void onFailure(Call<CallStatus> call, Throwable t) {
                            Log.i("--==>>", "更改状态请求失败" + t.getMessage());
                        }
                    });
                } else {
                    //关闭免打扰,就是开启服务
                    NetClient.getInstance().getTreatrueApi().setMute(MyApplication.getInstance().getUserInfo().getUser_id(),
                            1).enqueue(new Callback<CallStatus>() {
                        @Override
                        public void onResponse(Call<CallStatus> call, Response<CallStatus> response) {
                            showMute(response);
                        }

                        @Override
                        public void onFailure(Call<CallStatus> call, Throwable t) {
                            Log.i("--==>>", "更改状态请求失败" + t.getMessage());
                        }
                    });
                }
            }
        });
    }

    private void showMute(Response<CallStatus> response) {
        if (response.isSuccessful()) {
            CallStatus closeCall = response.body();
            if (closeCall != null) {
                String status = closeCall.getStatus();
                Log.i("--==>>", status);
                if (status.equals("success")) {
                    Log.i("--==>>", "更改状态提交成功");
                    Toast.makeText(getApplicationContext(), "设置成功", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getApplicationContext(), "设置失败", Toast.LENGTH_SHORT).show();
                }
            } else {
                Log.i("--==>>", "未知错误");
            }
        }
    }

}
