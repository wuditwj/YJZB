package io.agora.tutorials.activity;

import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;

import com.bumptech.glide.Glide;
import com.gyf.immersionbar.ImmersionBar;
import com.llvision.glass3.platform.ConnectionStatusListener;
import com.llvision.glass3.platform.IGlass3Device;
import com.llvision.glass3.platform.LLVisionGlass3SDK;
import com.llvision.glass3.platform.base.BasePermissionActivity;
import com.llvision.glxss.common.utils.LogUtil;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.agora.tutorials.application.MyApplication;
import io.agora.tutorials.customizedvideosource.R;
import io.agora.tutorials.entity.MuteInfo;
import io.agora.tutorials.net.NetClient;
import io.agora.tutorials.service.CalledService;
import io.agora.tutorials.utils.CircleTransform;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends BasePermissionActivity {

    //个人信息
    @BindView(R.id.ll_msg)
    LinearLayout llMsg;
    //用户预约表单
    @BindView(R.id.ll_form)
    LinearLayout llForm;
    //设置
    @BindView(R.id.ll_setting)
    LinearLayout llSetting;
    //退出登录
    @BindView(R.id.ll_log_out)
    LinearLayout llLogOut;
    //设备未连接
    @BindView(R.id.id_show_text)
    TextView idShowText;
    //头像
    @BindView(R.id.iv_head)
    ImageView ivHead;
    //勿扰状态显示
    @BindView(R.id.show_mute)
    TextView showMute;
    //勿扰图标
    @BindView(R.id.iv_mute)
    ImageView ivMute;
    //名字
    @BindView(R.id.tv_uer_name)
    TextView tvUerName;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON); //保持屏幕不息屏
        LogUtil.setDebug(true);
        LogUtil.setLogSaveLocal(true);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        //侧滑栏
        menu();
        String head = MyApplication.getInstance().getUserInfo().getPhoto();
        Glide.with(this).load(head).transform(new CircleTransform(this)).into(ivHead);
        tvUerName.setText(MyApplication.getInstance().getUserInfo().getNickname());

    }

    @Override
    protected void onStart() {
        super.onStart();
        init();
    }

    private void init() {
        //获取勿扰状态
        NetClient.getInstance().getTreatrueApi().getMute(MyApplication.getInstance().getUserInfo().getUser_id()).enqueue(new Callback<MuteInfo>() {
            @Override
            public void onResponse(Call<MuteInfo> call, Response<MuteInfo> response) {
                if (response.isSuccessful()) {
                    MuteInfo muteInfo = response.body();
                    if (muteInfo != null) {
                        //1代表可接通,2代表勿扰
                        if (muteInfo.getData().getRest().equals("2")) {
                            ivMute.setImageResource(R.mipmap.call_off);
                            showMute.setText(R.string.dnd_mode_open);
                        } else {
                            ivMute.setImageResource(R.mipmap.call_on);
                            showMute.setText(R.string.wait_called);
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
        //获取服务运行状态
        boolean serviceRunning = isServiceRunning(this, "io.agora.tutorials.service.CalledService");
        if (!serviceRunning) {
            //勿扰模式开着如果服务在运行就关闭
            //关闭服务
            startService(new Intent(this, CalledService.class));
        }
        //因为上面可能会执行打开服务操作,所以需要第二次判断,是否真的打开了
        boolean serviceRunning2 = isServiceRunning(this, "io.agora.tutorials.service.CalledService");
        if (!serviceRunning2) {
            showMute.setText(R.string.dnd_mode_open);
        } else {
            showMute.setText(R.string.wait_called);
        }
    }

    @Override
    protected void onResume() {
        //竖屏
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);//强制竖屏
        super.onResume();
    }

    @OnClick({R.id.ll_msg, R.id.ll_form, R.id.ll_setting, R.id.ll_log_out, R.id.show_mute})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            //个人信息
            case R.id.ll_msg:
                startActivity(new Intent(this, UserInformationActivity.class));
                break;
            //个人信息
            case R.id.ll_form:
                startActivity(new Intent(this, FormListActivity.class));
                break;
            //设置
            case R.id.ll_setting:
                startActivity(new Intent(this, SettingActivity.class));
                break;
            //退出登录
            case R.id.ll_log_out:
                showDialog();
                break;
            //等待呼叫...
            case R.id.show_mute:
                startActivity(new Intent(this, CameraActivity.class));
                break;
        }
    }

    /**
     * 显示弹窗
     */
    private void showDialog() {
        AlertDialog alertDialog = new AlertDialog.Builder(this)
                .setTitle(R.string.dialog_title)
                .setPositiveButton(R.string.dialog_ok, new DialogInterface.OnClickListener() {//添加"Yes"按钮
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        logout();
                    }
                })

                .setNegativeButton(R.string.dialog_no, new DialogInterface.OnClickListener() {//添加取消
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                    }
                })
                .create();
        alertDialog.show();
    }

    /**
     * 退出登录
     */
    private void logout() {
        SharedPreferences sharedPreferences = getSharedPreferences("login", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        //修改已登录状态
        editor.putBoolean("loginState", false);
        editor.commit();
        //关闭服务
        stopService(new Intent(this, CalledService.class));
        startActivity(new Intent(this, LoginActivity.class));
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (LLVisionGlass3SDK.getInstance().isServiceConnected()) {
            //解绑服务
            LLVisionGlass3SDK.getInstance().destroy();
        }
    }

    /**
     * 侧滑栏
     */
    private void menu() {
        //初始化状态栏的高度
        View statusbar = (View) findViewById(R.id.main_status_bar);
        //沉浸式状态栏
        ImmersionBar.with(this).statusBarView(statusbar).init();

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        final DrawerLayout drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        toolbar.setTitle("");
        //将ToolBar与ActionBar关联
        setSupportActionBar(toolbar);
        //另外openDrawerContentDescRes 打开图片   closeDrawerContentDescRes 关闭图片
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawerLayout, toolbar, 0, 0);
        //初始化状态
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        //蒙层颜色
        drawerLayout.setScrimColor(Color.TRANSPARENT);
        drawerLayout.addDrawerListener(new DrawerLayout.DrawerListener() {
            @Override
            public void onDrawerStateChanged(int newState) {
            }

            @Override
            public void onDrawerSlide(@NonNull View drawerView, float slideOffset) {
                // 滑动的过程中执行 slideOffset：从0到1
                //主页内容
                View content = drawerLayout.getChildAt(0);
                //侧边栏
                View menu = drawerView;
                //
                float scale = 1 - slideOffset;//1~0
                float leftScale = (float) (1 - 0.3 * scale);
                float rightScale = (float) (0.7f + 0.3 * scale);//0.7~1
//                menu.setScaleX(leftScale);//1~0.7
                menu.setScaleY(leftScale);//1~0.7

//                content.setScaleX(rightScale);
                content.setScaleY(rightScale);
                content.setTranslationX(menu.getMeasuredWidth() * slideOffset);//0~width
            }

            @Override
            public void onDrawerOpened(@NonNull View drawerView) {
            }

            @Override
            public void onDrawerClosed(@NonNull View drawerView) {
            }
        });
    }

    //**********************************眼镜********************************************************

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

    /**
     * 检测设备连接状态
     *
     * @param isAccepted
     */
    @Override
    protected void onPermissionAccepted(boolean isAccepted) {
        if (isAccepted) {
            //绑定“GlXSS 智能眼镜服务”
            LLVisionGlass3SDK.getInstance().init(this, new ConnectionStatusListener() {
                @Override
                public void onServiceConnected(List<IGlass3Device> glass3Devices) {

                }

                @Override
                public void onServiceDisconnected() {

                }

                @Override
                public void onDeviceConnect(final IGlass3Device device) {
                    idShowText.setText(R.string.device_connected);
//                    try {
//                        if (device != null) {
//                            //显示设备信息
//                            readDeviceInfor(device);
//                        }
//                    } catch (PackageManager.NameNotFoundException e) {
//                        e.printStackTrace();
//                    } catch (BaseException e) {
//                        e.printStackTrace();
//                    }
                }

                @Override
                public void onDeviceDisconnect(IGlass3Device device) {
                    idShowText.setText(R.string.device_disconnect);
//                    idShowInfoText.setText("");
                }

                @Override
                public void onError(int code, String msg) {

                }
            });
        }
    }

//    /**
//     * 显示设备信息
//     *
//     * @param device
//     * @throws PackageManager .NameNotFoundException
//     * @throws BaseException
//     */
//    private void readDeviceInfor(IGlass3Device device) throws PackageManager
//            .NameNotFoundException, BaseException {
//        StringBuffer sb = new StringBuffer();
//        sb.append("软件版本号:" + getPackageManager().
//                getPackageInfo(getPackageName(), 0).versionName + "\n");
//        FirmwareInfo firmwareInfo = device.getFirmwareInfo();
//        if (firmwareInfo != null) {
//            sb.append("固件版本号:" + firmwareInfo.version + "\n");
//            sb.append("固件项目名称:" + firmwareInfo.projectName + "\n");
//        }
//
//        DeviceInfo mProductInfo = device.getDeviceInfo();
//        if (mProductInfo != null) {
//            sb.append("编码版本号:" + mProductInfo.getPlatformID() + "\n");
//            sb.append("产品ID:" + mProductInfo.getProductID() + "\n");
//            sb.append("厂商ID:" + mProductInfo.getFirmID() + "\n");
//            sb.append("主板序列号/BSN:" + mProductInfo.getBsnID() + "\n");
//            sb.append("整机序列号/PSN:" + mProductInfo.getPsnID() + "\n");
//            sb.append("BOMID:" + mProductInfo.getBomID() + "\n");
//            sb.append("ISP版本号:" + mProductInfo.getIspID() + "\n");
//            sb.append("子板固件:" + mProductInfo.getFirmwareID() + "\n");
//            sb.append("显示器的分辨率宽度:" + mProductInfo.getResolutionWidth() + "\n");
//            sb.append("显示器的分辨率高度:" + mProductInfo.getResolutionHeight() + "\n");
//            sb.append("GLXSS ID:" + mProductInfo.getGlxssId() + "\n");
//            sb.append("Software Version:" + mProductInfo.getSoftwareVersion() + "\n");
//        }
//        idShowInfoText.setText(sb.toString());
//    }

}
