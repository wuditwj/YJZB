package io.agora.tutorials.net;

import android.util.Log;

import io.agora.tutorials.application.MyApplication;
import io.agora.tutorials.entity.CallStatus;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * 登录请求
 */
public class LoginRequest {
    private static String TAG="--==>>";


    public void setLoginStatus(int id,int login) {
        NetClient.getInstance().getTreatrueApi().setLogin(id, login).enqueue(new Callback<CallStatus>() {
            @Override
            public void onResponse(Call<CallStatus> call, Response<CallStatus> response) {
                if (response.isSuccessful()) {
                    CallStatus closeCall = response.body();
                    if (closeCall != null) {
                        String status = closeCall.getStatus();
                        if (status.equals("success")) {
                            Log.i(TAG, "修改登录状态成功");
                        } else {
                            Log.i(TAG, "修改登录状态失败");
                        }
                    } else {
                        Log.i(TAG, "未知错误");
                    }
                }
            }

            @Override
            public void onFailure(Call<CallStatus> call, Throwable t) {
                Log.i(TAG, "修改登录状态请求失败" + t.getMessage());
            }
        });
    }
}
