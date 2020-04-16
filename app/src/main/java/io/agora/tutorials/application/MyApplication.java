package io.agora.tutorials.application;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Environment;

import java.io.File;

import io.agora.tutorials.db.UserDatabase;
import io.agora.tutorials.entity.ClientInfo;
import io.agora.tutorials.entity.UserInfo;
import io.agora.tutorials.broadcast.MyBroadcastReceiver;
import io.agora.tutorials.service.CalledService;

public class MyApplication extends Application {
    private static MyApplication instance;

    private String filePath;

    private ClientInfo clientInfo;

    public ClientInfo getClientInfo() {
        return clientInfo;
    }

    public void setClientInfo(ClientInfo clientInfo) {
        this.clientInfo = clientInfo;
    }

    public String getFilePath() {
        return filePath;
    }

    public static void setInstance(MyApplication instance) {
        MyApplication.instance = instance;
    }

    public static MyApplication getInstance() {
        return instance;
    }


    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        IntentFilter filter = new IntentFilter(Intent.ACTION_TIME_TICK);
//        //广播
//        MyBroadcastReceiver receiver = new MyBroadcastReceiver();
//        registerReceiver(receiver, filter);
        newFile();
    }

    private void newFile(){
        File recordFileDirctory = new File(Environment.getExternalStorageDirectory()+"/LiveCar/record/");
        if(!recordFileDirctory.exists()){
            recordFileDirctory.mkdirs();
        }
        filePath=recordFileDirctory.getPath();
    }

    /**
     * 获取用户信息
     * @return
     */
    public UserInfo getUserInfo(){
        //获取当前登录的手机号
        SharedPreferences sharedPreferences = getSharedPreferences("login", Context.MODE_PRIVATE);
        String mobile = sharedPreferences.getString("mobile", "");
        //获取用户信息
        UserInfo userByName = UserDatabase.getInstance(this).getUserDao().getUserByMobile(mobile);
        return userByName;
    }

    public String getServicePackageName(){
        return CalledService.class.getName();
    }

}
