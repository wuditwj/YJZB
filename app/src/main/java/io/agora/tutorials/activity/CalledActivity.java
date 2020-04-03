package io.agora.tutorials.activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.agora.tutorials.call.CallInServerCenter;
import io.agora.tutorials.customizedvideosource.R;
import io.agora.tutorials.utils.BellUtils;
import io.agora.tutorials.utils.FontIconView;

/**
 * 呼叫弹出页面
 */
public class CalledActivity extends AppCompatActivity {
    public static CalledActivity calledActivity;
    //头像
    @BindView(R.id.client_head)
    ImageView clientHead;
    //挂断
    @BindView(R.id.hang_up)
    FontIconView hangUp;
    //接听
    @BindView(R.id.answer)
    FontIconView answer;
    @BindView(R.id.tv_user_name)
    TextView tvUserName;
    String userName;
    String userHead;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        calledActivity = this;
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED //锁屏显示
                | WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD //解锁
                | WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON //保持屏幕不息屏
                | WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);//点亮屏幕
        setContentView(R.layout.activity_called);
        ButterKnife.bind(this);
        //获取页面传递过来的值
        Intent intent = getIntent();
        Bundle myBundle = intent.getBundleExtra("message");
        userName = myBundle.getString("name");
        userHead = myBundle.getString("head");
        Log.i("--==>>",userHead);
        //加载头像
        Glide.with(this).load(userHead).into(clientHead);
        //显示用户名
        tvUserName.setText(userName);
        //触摸监听
        listener();
        //播放铃声
        BellUtils.startPlay(this);
    }

    //触摸监听
    private void listener() {
        //挂断
        hangUp.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    //按下
                    case MotionEvent.ACTION_DOWN:
                        hangUp.setTextColor(Color.parseColor("#555555"));
                        break;
                    //移动
                    case MotionEvent.ACTION_MOVE:
                        break;
                    //抬起
                    case MotionEvent.ACTION_UP:
                        hangUp.setTextColor(Color.parseColor("#FF0000"));
                        //关闭铃声
                        BellUtils.stopPlay();
                        //停止通话
                        new CallInServerCenter(CalledActivity.this).closeCall();
                        finish();
                        break;
                }
                return true;
            }
        });

        //接听
        answer.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                switch (event.getAction()) {
                    //按下
                    case MotionEvent.ACTION_DOWN:
                        answer.setTextColor(Color.parseColor("#555555"));
                        break;
                    //移动
                    case MotionEvent.ACTION_MOVE:
                        break;
                    //抬起
                    case MotionEvent.ACTION_UP:
                        answer.setTextColor(Color.parseColor("#26FF00" +
                                ""));
                        //关闭铃声
                        BellUtils.stopPlay();
                        //接通通话
                        new CallInServerCenter(CalledActivity.this).startCall();
                        startActivity(new Intent(CalledActivity.this, CameraActivity.class));
                        finish();
                        break;
                }
                return true;
            }
        });
    }

    @Override
    protected void onPause() {
        //关闭铃声
        BellUtils.stopPlay();
        super.onPause();
    }

    @Override
    public void finish() {
        //关闭铃声
        BellUtils.stopPlay();
        super.finish();
    }
}
