package io.agora.tutorials.onepiexl;

import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.util.Log;

import io.agora.tutorials.application.MyApplication;
import io.agora.tutorials.service.CalledService;

public class MyBroadcastReceiver extends BroadcastReceiver {

    private boolean flag;
    private boolean loginState;

    @Override
    public void onReceive(Context context, Intent intent) {
        boolean isServiceRunning = false;
        ActivityManager manager = (ActivityManager) MyApplication.getInstance().getApplicationContext().getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            Log.i("--==>>", "检查服务是否运行");
            //获取勿扰模式是否开启
            SharedPreferences sharedPreferences = MyApplication.getInstance().getApplicationContext().getSharedPreferences("login", Context.MODE_PRIVATE);
            flag = sharedPreferences.getBoolean("mute", false);
            loginState = sharedPreferences.getBoolean("loginState", false);


            if ("io.agora.tutorials.service.CalledService".equals(service.service.getClassName())) {
                isServiceRunning = true;
            } else {
                isServiceRunning = false;
            }
        }
        if (loginState && !flag) {
            if (!isServiceRunning) {
                Intent i = new Intent(context, CalledService.class);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    context.startForegroundService(i);
                } else {
                    context.startService(i);
                }
            }
        }
    }
}
