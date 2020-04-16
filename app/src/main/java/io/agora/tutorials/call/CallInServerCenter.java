package io.agora.tutorials.call;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;

import io.agora.tutorials.activity.CalledActivity;
import io.agora.tutorials.activity.CameraActivity;
import io.agora.tutorials.activity.MainActivity;
import io.agora.tutorials.application.MyApplication;
import io.agora.tutorials.db.UserDatabase;
import io.agora.tutorials.entity.CalledInfo;
import io.agora.tutorials.entity.CallStatus;
import io.agora.tutorials.entity.ClientInfo;
import io.agora.tutorials.entity.UserInfo;
import io.agora.tutorials.net.NetClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * 处理呼叫逻辑
 */
public class CallInServerCenter {
    private String TAG="--==>";
    //房间号
    private String roomId;
    private int houseId;
    //Uid
    private int uId;
    private Context context;
    //第几次被呼叫
    private int num = 0;

    public CallInServerCenter(Context context) {
        this.context = context;
        getRoomId(context);
    }

    private void getRoomId(Context context){
        //获取当前登录的手机号
        SharedPreferences sharedPreferences = context.getSharedPreferences("login", Context.MODE_PRIVATE);
        String mobile = sharedPreferences.getString("mobile", "");
        //获取用户信息
        UserInfo userByName = UserDatabase.getInstance(context).getUserDao().getUserByMobile(mobile);
        if(userByName!=null){
            //拼接房间号
            roomId=userByName.getMobile();
            houseId=userByName.getHouse_id();
        }else{
            roomId="0";
            houseId=0;
        }
    }

    /**
     * 监测是否有用户请求呼叫
     */
    public void calledListener() {
//        Log.i("--==>","roomId:"+roomId+" house_id:"+houseId);
        NetClient.getInstance().getTreatrueApi().called(roomId, houseId).enqueue(new Callback<CalledInfo>() {
            @Override
            public void onResponse(Call<CalledInfo> call, Response<CalledInfo> response) {
                if (response.isSuccessful()) {
                    CalledInfo body = response.body();
                    if (body != null) {
                        if(MainActivity.mainActivity!=null){
                            MainActivity.mainActivity.setNet(true);
                        }
                        switch (body.getData()) {
                            //有用户请求
                            case 1:
                                num++;
                                uId = body.getUid();
                                Log.i(TAG, "用户Id:" + uId);
                                if (num == 1) {
                                    //查询用户信息,跳转页面
                                    getUserInfo(body.getUid());
                                }
                                break;
                            //员工确认接通
                            case 2:
                                Log.i(TAG, "正在通话中...");
                                break;
                            //关闭
                            case 3:
                                Log.i(TAG, "没有用户请求,继续监听...");
                                num=0;
                                //如果响铃页面开着就关闭响铃页面
                                if (CalledActivity.calledActivity != null) {
                                    CalledActivity.calledActivity.finish();
                                }
                                if (CameraActivity.cameraActivity != null) {
                                    CameraActivity.cameraActivity.finish();
                                }
                                break;
                        }
                    } else {
                        Log.i(TAG, "未知错误");
                    }

                }
            }

            @Override
            public void onFailure(Call<CalledInfo> call, Throwable t) {
                if(MainActivity.mainActivity!=null){
                    MainActivity.mainActivity.setNet(false);
                }
                Log.i(TAG, "监听用户呼叫请求失败" + t.getMessage());
            }
        });
    }

    /**
     * 根据用户Id查询用户信息
     */
    private void getUserInfo(int uId) {
        NetClient.getInstance().getTreatrueApi().getUserInfo(uId).enqueue(new Callback<ClientInfo>() {
            @Override
            public void onResponse(Call<ClientInfo> call, Response<ClientInfo> response) {
                if (response.isSuccessful()) {
                    ClientInfo clientInfo = response.body();
                    if (clientInfo != null) {
//                        Log.i(TAG, clientInfo.toString());
                        MyApplication.getInstance().setClientInfo(clientInfo);
                        Intent intent = new Intent(context, CalledActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
                        context.startActivity(intent);
                    } else {
                        Log.i(TAG, "未知错误");
                    }
                }
            }

            @Override
            public void onFailure(Call<ClientInfo> call, Throwable t) {
                Log.i(TAG, "查询用户信息请求失败" + t.getMessage());
            }
        });
    }

    /**
     * 关闭通话
     */
    public void closeCall() {
        NetClient.getInstance().getTreatrueApi().closeCall(roomId, houseId, uId).enqueue(new Callback<CallStatus>() {
            @Override
            public void onResponse(Call<CallStatus> call, Response<CallStatus> response) {
                if (response.isSuccessful()) {
                    CallStatus closeCall = response.body();
                    if (closeCall != null) {
                        String status = closeCall.getStatus();
                        if (status.equals("success")) {
                            Log.i(TAG, "通话关闭成功");
                        } else {
                            Log.i(TAG, "通话关闭失败");
                        }
                    } else {
                        Log.i(TAG, "未知错误");
                    }
                }
            }

            @Override
            public void onFailure(Call<CallStatus> call, Throwable t) {
                Log.i(TAG, "关闭通话请求失败" + t.getMessage());
            }
        });
    }

    /**
     * 接通通话
     */
    public void startCall() {
        NetClient.getInstance().getTreatrueApi().startCall(roomId, houseId, uId).enqueue(new Callback<CallStatus>() {
            @Override
            public void onResponse(Call<CallStatus> call, Response<CallStatus> response) {
                if (response.isSuccessful()) {
                    CallStatus closeCall = response.body();
                    if (closeCall != null) {
                        String status = closeCall.getStatus();
                        if (status.equals("success")) {
                            Log.i(TAG, "接通通话成功");
                        } else {
                            Log.i(TAG, "接通通话失败");
                        }
                    } else {
                        Log.i(TAG, "未知错误");
                    }
                }
            }

            @Override
            public void onFailure(Call<CallStatus> call, Throwable t) {
                Log.i(TAG, "接通通话请求失败" + t.getMessage());
            }
        });
    }


}
