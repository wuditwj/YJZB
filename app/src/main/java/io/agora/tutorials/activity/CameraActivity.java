package io.agora.tutorials.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Window;
import android.view.WindowManager;

import io.agora.tutorials.fragment.CameraFragment;
import io.agora.tutorials.customizedvideosource.R;

/**
 * 眼镜按钮二级界面test
 * Created by elena on 2018/8/30.
 */

public class CameraActivity extends AppCompatActivity {
    public static CameraActivity cameraActivity;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        cameraActivity=this;
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED //锁屏显示
                | WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD //解锁
                | WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON //保持屏幕不息屏
                | WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);//点亮屏幕
        //全屏显示
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.activity_camera);
        //关闭响铃页面
        if(CalledActivity.calledActivity!=null){
            CalledActivity.calledActivity.finish();
        }
        if (savedInstanceState == null) {
            final CameraFragment fragment = new CameraFragment();
            getFragmentManager().beginTransaction().add(R.id.container, fragment).commit();
        }
    }

}
