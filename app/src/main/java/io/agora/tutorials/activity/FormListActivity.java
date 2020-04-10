package io.agora.tutorials.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;

import com.google.gson.JsonObject;

import io.agora.tutorials.application.MyApplication;
import io.agora.tutorials.customizedvideosource.R;
import io.agora.tutorials.entity.ClientInfo;
import io.agora.tutorials.entity.FormListInfo;
import io.agora.tutorials.net.NetClient;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FormListActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_form_list);
        Log.i("--==>>",MyApplication.getInstance().getUserInfo().getId()+"");

        NetClient.getInstance().getTreatrueApi().getFormList(MyApplication.getInstance().getUserInfo().getId(), 1).enqueue(new Callback<FormListInfo>() {
            @Override
            public void onResponse(Call<FormListInfo> call, Response<FormListInfo> response) {
                if (response.isSuccessful()) {
                    FormListInfo formListInfo = response.body();
                    if (formListInfo != null) {
                        Log.i("--==>>",formListInfo.toString());
                    }else {
                        Log.i("--==>>", "未知错误");
                    }
                }else{
                    Log.i("--==>>","rs:"+response.code());
                }
            }

            @Override
            public void onFailure(Call<FormListInfo> call, Throwable t) {
                Log.i("--==>>", "查询表单列表信息请求失败" + t.getMessage());
            }
        });
    }
}
