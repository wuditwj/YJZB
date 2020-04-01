package io.agora.tutorials.activity;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.SurfaceView;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Toast;

import java.util.Arrays;

import javax.microedition.khronos.egl.EGLContext;

import io.agora.rtc.Constants;
import io.agora.rtc.IRtcEngineEventHandler;
import io.agora.rtc.RtcEngine;
import io.agora.rtc.video.AgoraVideoFrame;
import io.agora.rtc.video.VideoCanvas;
import io.agora.rtc.video.VideoEncoderConfiguration; // 2.3.0 and later
import io.agora.tutorials.application.MyApplication;
import io.agora.tutorials.customizedvideosource.R;
import io.agora.tutorials.helper.CustomizedCameraRenderer;

import static io.agora.rtc.Constants.AUDIO_RECORDING_QUALITY_HIGH;

/**
 * 本地相机按钮二级界面
 */
public class VideoChatViewActivity extends AppCompatActivity {

    private static final String LOG_TAG = VideoChatViewActivity.class.getSimpleName();

    private static final boolean DBG = false;

    private static final int PERMISSION_REQ_ID_RECORD_AUDIO = 22;
    private static final int PERMISSION_REQ_ID_CAMERA = PERMISSION_REQ_ID_RECORD_AUDIO + 1;

    private CustomizedCameraRenderer mCustomizedCameraRenderer; // Tutorial Step 3
    //声网对象
    private RtcEngine mRtcEngine; // Tutorial Step 1
    //房间号
    private String roomId = "123";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_chat_view);

        if (checkSelfPermission(Manifest.permission.RECORD_AUDIO, PERMISSION_REQ_ID_RECORD_AUDIO) && checkSelfPermission(Manifest.permission.CAMERA, PERMISSION_REQ_ID_CAMERA)) {
            initAgoraEngineAndJoinChannel();
        }
    }

    /**
     * 获取权限
     *
     * @param permission
     * @param requestCode
     * @return
     */
    public boolean checkSelfPermission(String permission, int requestCode) {
        Log.i(LOG_TAG, "checkSelfPermission " + permission + " " + requestCode);
        if (ContextCompat.checkSelfPermission(this,
                permission)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,
                    new String[]{permission},
                    requestCode);
            return false;
        }
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String permissions[], @NonNull int[] grantResults) {
        Log.i(LOG_TAG, "onRequestPermissionsResult " + grantResults[0] + " " + requestCode);

        switch (requestCode) {
            case PERMISSION_REQ_ID_RECORD_AUDIO: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    checkSelfPermission(Manifest.permission.CAMERA, PERMISSION_REQ_ID_CAMERA);
                } else {
                    showLongToast("No permission for " + Manifest.permission.RECORD_AUDIO);
                    finish();
                }
                break;
            }
            case PERMISSION_REQ_ID_CAMERA: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    initAgoraEngineAndJoinChannel();
                } else {
                    showLongToast("No permission for " + Manifest.permission.CAMERA);
                    finish();
                }
                break;
            }
        }
    }

    public final void showLongToast(final String msg) {
        this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_LONG).show();
            }
        });
    }

    private void initAgoraEngineAndJoinChannel() {
        //第一步:初始化Agora，创建 RtcEngine 对象
        initializeAgoraEngine();
        //第二步:打开视频模式，并设置本地视频属性
        setupVideoProfile();
        //第三步:设置本地视频显示属性
        setupLocalVideo(getApplicationContext());
    }

    /**
     * 第一步
     * 初始化Agora，创建 RtcEngine 对象
     */
    private void initializeAgoraEngine() {
        try {
            //创建 RtcEngine 实例
            mRtcEngine = RtcEngine.create(getBaseContext(), getString(R.string.agora_app_id), mRtcEventHandler);
        } catch (Exception e) {
            Log.e(LOG_TAG, Log.getStackTraceString(e));

            throw new RuntimeException("Agora初始化失败了，检查一下是哪儿出错了\n" + Log.getStackTraceString(e));
        }
        //设置频道场景
        mRtcEngine.setChannelProfile(Constants.CHANNEL_PROFILE_LIVE_BROADCASTING);
    }

    /**
     * IRtcEngineEventHandler 类用于向应用程序发送回调通知
     */
    private final IRtcEngineEventHandler mRtcEventHandler = new IRtcEngineEventHandler() { // Tutorial Step 1
        @Override
        public void onFirstRemoteVideoDecoded(final int uid, int width, int height, int elapsed) { // Tutorial Step 5
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    //设置远端视频显示属性
                    setupRemoteVideo(uid);
                }
            });
        }

        //远端用户离开当前频道回调
        @Override
        public void onUserOffline(int uid, int reason) { // Tutorial Step 7
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    //其他用户离开当前频道回调
                    onRemoteUserLeft();
                }
            });
        }
    };

    /**
     * 其他用户离开当前频道调用此方法
     */
    private void onRemoteUserLeft() {
        FrameLayout container = (FrameLayout) findViewById(R.id.remote_video_view_container);
        container.removeAllViews();
        //显示文案
        View tipMsg = findViewById(R.id.quick_tips_when_use_agora_sdk);
        tipMsg.setVisibility(View.VISIBLE);
    }

    /**
     * 第二步
     * 打开视频模式，并设置本地视频属性
     */
    private void setupVideoProfile() {
        //启用视频模块,打开视频模式
        mRtcEngine.enableVideo();
        //检查视频是否支持 Texture 编码
        if (mRtcEngine.isTextureEncodeSupported()) {
            //配置外部视频源
            mRtcEngine.setExternalVideoSource(true, true, true);
        } else {
            throw new RuntimeException("不能在不支持Texture 编码的设备上工作" + mRtcEngine.isTextureEncodeSupported());
        }
        //设置视频编码配置
        mRtcEngine.setVideoEncoderConfiguration(new VideoEncoderConfiguration(
                VideoEncoderConfiguration.VD_1280x720,
                VideoEncoderConfiguration.FRAME_RATE.FRAME_RATE_FPS_15,
                VideoEncoderConfiguration.STANDARD_BITRATE,
                VideoEncoderConfiguration.ORIENTATION_MODE.ORIENTATION_MODE_ADAPTIVE));
    }

    private volatile boolean mJoined = false;

    /**
     * 第三步
     * 设置本地视频显示属性
     */
    private CustomizedCameraRenderer setupLocalVideo(Context ctx) {
        FrameLayout container = (FrameLayout) findViewById(R.id.local_video_view_container);
        CustomizedCameraRenderer surfaceV = new CustomizedCameraRenderer(ctx);

        mCustomizedCameraRenderer = surfaceV;
        mCustomizedCameraRenderer.setOnFrameAvailableHandler(new CustomizedCameraRenderer.OnFrameAvailableListener() {
            @Override
            public void onFrameAvailable(int texture, EGLContext eglContext, int rotation, float[] matrix) {
                AgoraVideoFrame vf = new AgoraVideoFrame();
                vf.format = AgoraVideoFrame.FORMAT_TEXTURE_2D;
                vf.timeStamp = System.currentTimeMillis();
                vf.stride = 1280;
                vf.height = 702;
                vf.textureID = texture;
                vf.syncMode = true;
                vf.eglContext11 = eglContext;
                vf.transform = matrix;

                boolean result = mRtcEngine.pushExternalVideoFrame(vf);

                Log.d(LOG_TAG, "onFrameAvailable " + eglContext + " " + rotation + " " + texture + " " + result + " " + Arrays.toString(matrix));
                if (DBG) {
                }
            }
        });

        mCustomizedCameraRenderer.setOnEGLContextHandler(new CustomizedCameraRenderer.OnEGLContextListener() {
            @Override
            public void onEGLContextReady(EGLContext eglContext) {
                Log.d(LOG_TAG, "onEGLContextReady " + eglContext + " " + mJoined);

                if (!mJoined) {
                    //加入一个频道
                    joinChannel(); // Tutorial Step 4
                    mJoined = true;
                }
            }
        });

        surfaceV.setZOrderMediaOverlay(true);

        container.addView(surfaceV);
        return surfaceV;
    }

    /**
     * 第四步
     * 加入一个频道
     */
    private void joinChannel() {
        //设置直播场景下的用户角色
        mRtcEngine.setClientRole(Constants.CLIENT_ROLE_BROADCASTER);
        //加入频道,如果不指定UID，Agroa将自动生成并分配一个UID
        mRtcEngine.joinChannel(null, roomId, "Extra Optional Data", 0); // if you do not specify the uid, we will generate the uid for you
        //开始录音
        mRtcEngine.startAudioRecording(saveAudio(),AUDIO_RECORDING_QUALITY_HIGH);
    }

    private String saveAudio(){

        String SDCard = MyApplication.getInstance().getFilePath();
        //获取当前时间戳
        long timeMillis = System.currentTimeMillis();
        //拼接名字
        String fileName=SDCard+"/audio_"+timeMillis+".WAV";
        return fileName;
    }

    /**
     * 第五步
     * 设置远端视频显示属性
     */
    private void setupRemoteVideo(int uid) {
        FrameLayout container = (FrameLayout) findViewById(R.id.remote_video_view_container);
        //如果子view超过一个
        if (container.getChildCount() >= 1) {
            return;
        }
        //创建渲染视图
        SurfaceView surfaceView = RtcEngine.CreateRendererView(getBaseContext());
        container.addView(surfaceView);
        //设置远端用户视图
        mRtcEngine.setupRemoteVideo(new VideoCanvas(surfaceView, VideoCanvas.RENDER_MODE_ADAPTIVE, uid));
        surfaceView.setTag(uid); // for mark purpose
        View tipMsg = findViewById(R.id.quick_tips_when_use_agora_sdk); // optional UI
        tipMsg.setVisibility(View.GONE);
    }

    /**
     * 第六步
     * 离开当前频道
     */
    private void leaveChannel() {
        mRtcEngine.leaveChannel();
    }

    /**
     * 左边按钮的点击事件
     * 将自己静音
     *
     * @param view
     */
    public void onLocalAudioMuteClicked(View view) {
        ImageView iv = (ImageView) view;
        if (iv.isSelected()) {
            iv.setSelected(false);
            //清除图片样式效果
            iv.clearColorFilter();
        } else {
            iv.setSelected(true);
            iv.setColorFilter(getResources().getColor(R.color.colorPrimary), PorterDuff.Mode.MULTIPLY);
        }
        //停止/恢复发送本地音频流
        mRtcEngine.muteLocalAudioStream(iv.isSelected());
    }

    /**
     * 中间按钮的点击事件
     * 放大缩小画面
     *
     * @param view
     */
    public void onLocalViewHidden(View view) {
        ImageView iv = (ImageView) view;
        if (iv.isSelected()) {
            iv.setSelected(false);
            iv.clearColorFilter();
            mCustomizedCameraRenderer.setViewHiddenStatus(false);
        } else {
            iv.setSelected(true);
            iv.setColorFilter(getResources().getColor(R.color.colorPrimary), PorterDuff.Mode.MULTIPLY);
            mCustomizedCameraRenderer.setViewHiddenStatus(true);
        }
    }

    /**
     * 右边按钮的点击事件
     * 离开当前频道
     *
     * @param view
     */
    public void onEncCallClicked(View view) {
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        leaveChannel();
        RtcEngine.destroy();

        if (mCustomizedCameraRenderer != null) {
            mCustomizedCameraRenderer.deinitCameraTexture();
            mCustomizedCameraRenderer = null;
        }
        mRtcEngine.stopAudioRecording();
        mRtcEngine = null;
    }
}
