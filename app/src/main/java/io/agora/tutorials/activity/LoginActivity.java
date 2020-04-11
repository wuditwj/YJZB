package io.agora.tutorials.activity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.agora.tutorials.application.MyApplication;
import io.agora.tutorials.customizedvideosource.R;
import io.agora.tutorials.db.UserDatabase;
import io.agora.tutorials.entity.LoginInfo;
import io.agora.tutorials.entity.UserInfo;
import io.agora.tutorials.net.NetClient;
import io.agora.tutorials.utils.PermissionHelper;
import io.agora.tutorials.utils.PermissionInterface;
import io.agora.tutorials.utils.ScreenInfoUtils;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity implements PermissionInterface {

    //手机号
    @BindView(R.id.et_mobile)
    EditText etMobile;
    //密码
    @BindView(R.id.et_pwd)
    EditText etPwd;
    //登录按钮
    @BindView(R.id.bt_login)
    RelativeLayout btLogin;

    private String userMobile;
    private String userPassword;
    private String head;
    //权限操作类
    private PermissionHelper mPermissionHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);
        //初始化并发起权限申请
        mPermissionHelper = new PermissionHelper(this, this);
        mPermissionHelper.requestPermissions();
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
                            insertUser(loginInfo.getData().getId(),
                                    loginInfo.getData().getAdmin_id(),
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
    private void insertUser(int user_id, int admin_id, String nickname, String photo, int house_id, String mobile, String password) {
        //登录的用户
        UserInfo userInfo = new UserInfo(user_id, admin_id, nickname, photo, house_id, mobile, password);
        //在数据库里查询有没有这个手机号的用户
        UserInfo userByDB = UserDatabase.getInstance(this).getUserDao().getUserByMobile(mobile);
        //如果数据库没有这个用户就添加到数据库里
        if (userByDB == null) {
            UserDatabase.getInstance(this).getUserDao().insert(userInfo);
        } else {
            //如果有,就查看数据是否一样,如果一样就不添加,如果不一样就替换
            if (!userByDB.equals(userInfo)) {
                userByDB.setInfo(userInfo);
                UserDatabase.getInstance(this).getUserDao().update(userByDB);
            }
        }


    }

    /**
     * 记录登录状态
     */
    private void loginState() {
        SharedPreferences sharedPreferences = getSharedPreferences("login", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        //是否是已登录状态
        editor.putBoolean("loginState", true).commit();
        //手机号
        editor.putString("mobile", userMobile).commit();
        startActivity(new Intent(this, MainActivity.class));
        finish();
    }

    //************************************申请权限**************************************************


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (mPermissionHelper.requestPermissionsResult(requestCode, permissions, grantResults)) {
            //权限请求结果，并已经处理了该回调
            return;
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    /**
     * 设置权限请求requestCode，只有不跟onRequestPermissionsResult方法中的其他请求码冲突即可。
     *
     * @return
     */
    @Override
    public int getPermissionsRequestCode() {
        return 10000;
    }

    /**
     * 设置该界面所需的全部权限
     *
     * @return
     */
    @Override
    public String[] getPermissions() {
        return new String[]{
                Manifest.permission.READ_PHONE_STATE,
                Manifest.permission.CAMERA,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.RECORD_AUDIO
        };
    }

    /**
     * 权限请求用户已经全部允许
     */
    @Override
    public void requestPermissionsSuccess() {
        init();
    }

    /**
     * 权限请求不被用户允许。可以提示并退出或者提示权限的用途并重新发起权限申请。
     */
    @Override
    public void requestPermissionsFail() {
        finish();
    }
}
