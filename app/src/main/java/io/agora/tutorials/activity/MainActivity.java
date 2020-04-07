package io.agora.tutorials.activity;

import android.app.ActivityManager;
import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.NotificationCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.llvision.glass3.library.boot.DeviceInfo;
import com.llvision.glass3.library.boot.FirmwareInfo;
import com.llvision.glass3.platform.ConnectionStatusListener;
import com.llvision.glass3.platform.IGlass3Device;
import com.llvision.glass3.platform.LLVisionGlass3SDK;
import com.llvision.glass3.platform.base.BasePermissionActivity;
import com.llvision.glxss.common.exception.BaseException;
import com.llvision.glxss.common.utils.LogUtil;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.agora.tutorials.application.MyApplication;
import io.agora.tutorials.customizedvideosource.R;
import io.agora.tutorials.service.CalledService;
import io.agora.tutorials.utils.CircleTransform;
import io.agora.tutorials.utils.ScreenInfoUtils;
import test.TestActivity;

public class MainActivity extends BasePermissionActivity {

    //个人信息
    @BindView(R.id.ll_msg)
    LinearLayout llMsg;
    //设置
    @BindView(R.id.ll_setting)
    LinearLayout llSetting;
    //退出登录
    @BindView(R.id.ll_log_out)
    LinearLayout llLogOut;
    //设备未连接
    @BindView(R.id.id_show_text)
    TextView idShowText;
    //显示设备信息
    @BindView(R.id.id_showInfo_text)
    TextView idShowInfoText;
    //头像
    @BindView(R.id.iv_head)
    ImageView ivHead;
    //勿扰状态显示
    @BindView(R.id.show_mute)
    TextView showMute;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LogUtil.setDebug(true);
        LogUtil.setLogSaveLocal(true);
        //隐藏状态栏时，获取状态栏高度
        int statusBarHeight = ScreenInfoUtils.getStatusBarHeight(this);
        //隐藏状态栏
        ScreenInfoUtils.fullScreen(this);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        //侧滑栏
        menu(statusBarHeight);
        String head = MyApplication.getInstance().getUserInfo().getPhoto();
        Glide.with(this).load(head).transform(new CircleTransform(this)).into(ivHead);

    }

    @Override
    protected void onStart() {
        super.onStart();
        init();
    }

    private void init() {
//        //获取勿扰模式是否开启
//        SharedPreferences sharedPreferences = getSharedPreferences("login", Context.MODE_PRIVATE);
//        boolean flag = sharedPreferences.getBoolean("mute", false);
//        //获取服务运行状态
//        boolean serviceRunning = isServiceRunning(this, "io.agora.tutorials.service.CalledService");
//        //判断当前勿扰模式是否开着
//        if (flag && serviceRunning) {
//            //勿扰模式开着如果服务在运行就关闭
//            //关闭服务
//            stopService(new Intent(this, CalledService.class));
//        } else if (!flag && !serviceRunning) {
//            //勿扰模式关着,如果服务关闭就开启
//            //开启服务
//            startService(new Intent(this, CalledService.class));
//        }
//        //因为上面可能会执行打开服务操作,所以需要第二次判断,是否真的打开了
//        boolean serviceRunning2 = isServiceRunning(this, "io.agora.tutorials.service.CalledService");
//        if (flag && !serviceRunning2) {
//            showMute.setText(R.string.dnd_mode_open);
//        } else if (!flag && serviceRunning2) {
//            showMute.setText(R.string.wait_called);
//        }
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

    @OnClick({R.id.ll_msg, R.id.ll_setting, R.id.ll_log_out, R.id.show_mute, R.id.id_show_text})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.ll_msg:
                startActivity(new Intent(this, UserInformationActivity.class));
                break;
            case R.id.ll_setting:
                startActivity(new Intent(this, SettingActivity.class));
                break;
            case R.id.ll_log_out:
                showDialog();
                break;
            case R.id.show_mute:
                startActivity(new Intent(this, CameraActivity.class));
                break;
            case R.id.id_show_text:
                startActivity(new Intent(this, FormActivity.class));
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
    private void menu(int statusBarHeight) {
        //初始化状态栏的高度
        View statusbar = (View) findViewById(R.id.view_statusbar);
        ConstraintLayout.LayoutParams params =
                new ConstraintLayout.LayoutParams(ConstraintLayout.LayoutParams.MATCH_PARENT, statusBarHeight);
        statusbar.setLayoutParams(params);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        final DrawerLayout drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);

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
                    idShowText.setTextColor(Color.GREEN);
                    idShowText.setText(R.string.device_connected);
                    try {
                        if (device != null) {
                            //显示设备信息
                            readDeviceInfor(device);
                        }
                    } catch (PackageManager.NameNotFoundException e) {
                        e.printStackTrace();
                    } catch (BaseException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onDeviceDisconnect(IGlass3Device device) {
                    idShowText.setTextColor(Color.RED);
                    idShowText.setText(R.string.device_disconnect);
                    idShowInfoText.setText("");
                }

                @Override
                public void onError(int code, String msg) {

                }
            });
        }
    }

    /**
     * 显示设备信息
     *
     * @param device
     * @throws PackageManager .NameNotFoundException
     * @throws BaseException
     */
    private void readDeviceInfor(IGlass3Device device) throws PackageManager
            .NameNotFoundException, BaseException {
        StringBuffer sb = new StringBuffer();
        sb.append("软件版本号:" + getPackageManager().
                getPackageInfo(getPackageName(), 0).versionName + "\n");
        FirmwareInfo firmwareInfo = device.getFirmwareInfo();
        if (firmwareInfo != null) {
            sb.append("固件版本号:" + firmwareInfo.version + "\n");
            sb.append("固件项目名称:" + firmwareInfo.projectName + "\n");
        }

        DeviceInfo mProductInfo = device.getDeviceInfo();
        if (mProductInfo != null) {
            sb.append("编码版本号:" + mProductInfo.getPlatformID() + "\n");
            sb.append("产品ID:" + mProductInfo.getProductID() + "\n");
            sb.append("厂商ID:" + mProductInfo.getFirmID() + "\n");
            sb.append("主板序列号/BSN:" + mProductInfo.getBsnID() + "\n");
            sb.append("整机序列号/PSN:" + mProductInfo.getPsnID() + "\n");
            sb.append("BOMID:" + mProductInfo.getBomID() + "\n");
            sb.append("ISP版本号:" + mProductInfo.getIspID() + "\n");
            sb.append("子板固件:" + mProductInfo.getFirmwareID() + "\n");
            sb.append("显示器的分辨率宽度:" + mProductInfo.getResolutionWidth() + "\n");
            sb.append("显示器的分辨率高度:" + mProductInfo.getResolutionHeight() + "\n");
            sb.append("GLXSS ID:" + mProductInfo.getGlxssId() + "\n");
            sb.append("Software Version:" + mProductInfo.getSoftwareVersion() + "\n");
        }
        idShowInfoText.setText(sb.toString());
    }

}
