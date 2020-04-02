package io.agora.tutorials.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;

import io.agora.tutorials.customizedvideosource.R;
import io.agora.tutorials.fragment.Camera2Fragment;

public class Camera2Activity extends AppCompatActivity {
    public static Camera2Activity cameraActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        cameraActivity = this;
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED //锁屏显示
                | WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD //解锁
                | WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON //保持屏幕不息屏
                | WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);//点亮屏幕
        //全屏显示
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.activity_camera2);
        //关闭响铃页面
        if (CalledActivity.calledActivity != null) {
            CalledActivity.calledActivity.finish();
        }
        if (savedInstanceState == null) {
            final Camera2Fragment fragment = new Camera2Fragment();
            getFragmentManager().beginTransaction().add(R.id.container2, fragment).commit();
        }
    }
}
