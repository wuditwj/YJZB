package io.agora.tutorials.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.agora.tutorials.customizedvideosource.R;
import io.agora.tutorials.db.UserDatabase;
import io.agora.tutorials.entity.LoginInfo;
import io.agora.tutorials.entity.UserInfo;
import io.agora.tutorials.net.NetClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {

    //手机号
    @BindView(R.id.et_mobile)
    EditText etMobile;
    //密码
    @BindView(R.id.et_pwd)
    EditText etPwd;
    //登录按钮
    @BindView(R.id.bt_login)
    Button btLogin;

    private String userMobile;
    private String userPassword;
    private String head;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);
        init();
    }

    private void init() {
        SharedPreferences sharedPreferences = getSharedPreferences("login", Context.MODE_PRIVATE);
        boolean flag = sharedPreferences.getBoolean("loginState", false);
        if (flag) {
            startActivity(new Intent(this, MainActivity.class));
            finish();
        }
    }

    @OnClick(R.id.bt_login)
    public void onViewClicked() {
        if (isEmpty()) {
            login(userMobile, userPassword);
        }
    }

    /**
     * 判断是否空,获取填写的手机号密码
     */
    private boolean isEmpty() {
        boolean flag = false;
        userMobile = etMobile.getText().toString().trim();
        userPassword = etPwd.getText().toString().trim();
        if (!userMobile.equals("") && !userPassword.equals("")) {
            flag = true;
        } else {
            if (userMobile.equals("")) {
                flag = false;
                etMobile.setHint(R.string.mobile_empty);
            }
            if (userPassword.equals("")) {
                flag = false;
                etPwd.setHint(R.string.psd_empty);

            }
        }
        return flag;
    }

    private void show() {
        Toast.makeText(this, R.string.mp_error, Toast.LENGTH_SHORT).show();
    }

    /**
     * 登录
     */
    private void login(String userMobile, final String userPassword) {
        Log.i("--==>>", "可以登录,手机号为:" + userMobile + "    密码为:" + userPassword);
        NetClient.getInstance().getTreatrueApi().login(userMobile, userPassword).enqueue(new Callback<LoginInfo>() {
            @Override
            public void onResponse(Call<LoginInfo> call, Response<LoginInfo> response) {
                if (response != null) {
                    LoginInfo loginInfo = response.body();
                    if (loginInfo != null) {
                        if (loginInfo.getStatus().equals("success")) {
                            Log.i("--==>>", "登录成功:" + loginInfo.toString());
                            head = loginInfo.getData().getPhoto();
                            insertUser(loginInfo.getData().getAdmin_id(),
                                    loginInfo.getData().getNickname(),
                                    loginInfo.getData().getPhoto(),
                                    loginInfo.getData().getHouse_id(),
                                    loginInfo.getData().getMobile(),
                                    userPassword);
                            loginState();
                        } else {
                            Log.i("--==>>", "登录失败");
                            show();
                        }
                    } else {
                        Log.i("--==>>", "未知错误");
                    }
                }
            }

            @Override
            public void onFailure(Call<LoginInfo> call, Throwable t) {
                Log.i("--==>>", "登录请求失败" + t.getMessage());
                show();
            }
        });
    }

    /**
     * 把用户数据添加进数据库
     */
    private void insertUser(int admin_id,String nickname, String photo, int house_id, String mobile, String password) {
        UserInfo userInfo = new UserInfo(admin_id,nickname, photo, house_id, mobile, password);
        UserDatabase.getInstance(this).getUserDao().insert(userInfo);
    }

    /**
     * 记录登录状态
     */
    private void loginState() {
        SharedPreferences sharedPreferences = getSharedPreferences("login", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        //是否是已登录状态
        editor.putBoolean("loginState", true);
        //手机号
        editor.putString("mobile", userMobile);
        editor.commit();
        startActivity(new Intent(this, MainActivity.class));
        finish();
    }


}
