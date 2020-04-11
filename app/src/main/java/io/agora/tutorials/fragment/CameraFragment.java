package io.agora.tutorials.fragment;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Point;
import android.os.Bundle;

import androidx.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Surface;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.llvision.glass3.core.camera.client.CameraException;
import com.llvision.glass3.core.camera.client.CameraStatusListener;
import com.llvision.glass3.core.camera.client.ICameraClient;
import com.llvision.glass3.core.camera.client.ICameraDevice;
import com.llvision.glass3.core.camera.client.IFrameCallback;
import com.llvision.glass3.core.camera.client.PixelFormat;
import com.llvision.glass3.core.lcd.client.IGlassDisplay;
import com.llvision.glass3.core.lcd.client.ILCDClient;
import com.llvision.glass3.platform.ConnectionStatusListener;
import com.llvision.glass3.platform.GlassException;
import com.llvision.glass3.platform.IGlass3Device;
import com.llvision.glass3.platform.LLVisionGlass3SDK;
import com.llvision.glxss.common.exception.BaseException;
import com.llvision.glxss.common.ui.CameraTextureView;
import com.llvision.glxss.common.ui.SurfaceCallback;
import com.llvision.glxss.common.utils.LogUtil;
import com.llvision.glxss.common.utils.ToastUtils;

import java.util.List;


import io.agora.rtc.Constants;
import io.agora.rtc.IRtcEngineEventHandler;
import io.agora.rtc.RtcEngine;
import io.agora.rtc.video.AgoraVideoFrame;
import io.agora.rtc.video.VideoEncoderConfiguration;
import io.agora.tutorials.activity.FormCommitActivity;
import io.agora.tutorials.application.MyApplication;
import io.agora.tutorials.call.CallInServerCenter;
import io.agora.tutorials.customizedvideosource.R;
import io.agora.tutorials.db.UserDatabase;
import io.agora.tutorials.entity.UserInfo;

import static io.agora.rtc.Constants.AUDIO_RECORDING_QUALITY_HIGH;


/**
 * test34
 * Created by elena on 2018/8/30.
 */

public class CameraFragment extends BaseFragment implements View.OnClickListener {

    private static final String TAG = CameraFragment.class.getSimpleName();
    private static final long SECOND_ONE = 1000;

    private IGlass3Device mGlass3Device;
    private ICameraDevice mICameraDevice;
    private ICameraClient mCameraClient;
    private ILCDClient mLcdClient;
    //LCD集成
    private IGlassDisplay mGlassDisplay;

    private CameraTextureView mCameraView;


    private int mWidth = 1280;
    private int mHeight = 720;
    private int mFps = 15;
    private RtcEngine mRtcEngine; // Tutorial Step 1

    //房间号
    private String roomId;

    public CameraFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i("--==>>", "进OnCreate");
        Point point = new Point();
        getActivity().getWindowManager().getDefaultDisplay().getSize(point);
        mWidth = point.x;
        mHeight = point.y;
        //获取ICameraClient
        LLVisionGlass3SDK.getInstance().registerConnectionListener(mConnectionStatusListener);
        try {
            if (LLVisionGlass3SDK.getInstance().isServiceConnected()) {
                Log.i("--==>>", "硬件连接");
                List<IGlass3Device> glass3DeviceList = LLVisionGlass3SDK.getInstance()
                        .getGlass3DeviceList();
                if (glass3DeviceList != null && glass3DeviceList.size() > 0) {
                    Log.i("--==>>", "驱动连接");
                    mGlass3Device = glass3DeviceList.get(0);
                    mCameraClient = (ICameraClient) LLVisionGlass3SDK.getInstance().getGlass3Client(
                            IGlass3Device.Glass3DeviceClient.CAMERA);
                    mLcdClient = (ILCDClient) LLVisionGlass3SDK.getInstance().getGlass3Client(
                            IGlass3Device.Glass3DeviceClient.LCD);
                    mGlassDisplay = mLcdClient.getGlassDisplay(mGlass3Device);
                }
            } else {
//                LogUtil.e("服务尚未连接.");
                Log.i("--==>>","服务尚未连接");
            }
        } catch (GlassException e) {
            e.printStackTrace();
        } catch (BaseException e) {
            e.printStackTrace();
        }

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle
            savedInstanceState) {
        Log.i("--==>>", "进OnCreateView");
        View rootView = inflater.inflate(R.layout.fragment_camera, container, false);
        mCameraView = rootView.findViewById(R.id.camera_view);
        rootView.findViewById(R.id.iv_close).setOnClickListener(this);
        //添加、删除 Surface、设置预览框尺寸
        mCameraView.setSurfaceCallback(mSurfaceCallback);
        mCameraView.setAspectRatio(mWidth / (float) mHeight);

        //打开摄像头
        if (mGlass3Device != null) {
            try {
                mICameraDevice = mCameraClient.openCamera(mGlass3Device, mCameraStatusListener);
                if (mICameraDevice != null) {
                    mICameraDevice.setPreviewSize(mWidth, mHeight, mFps);
                    mICameraDevice.connect();
                }
            } catch (CameraException e) {
                e.printStackTrace();
            } catch (BaseException e) {
                e.printStackTrace();
            }
        }
        roomId = getRoomId(getActivity());
        //初始化Agora，创建 RtcEngine 对象
        initializeAgoraEngine();
        //打开视频模式，并设置本地视频属性
        setupVideoConfig();
        //设置外部设备录制视频
        setupVideoProfile();
        //加入一个频道
        joinChannel();
        return rootView;
    }

    private String getRoomId(Context context) {
        //获取当前登录的手机号
        SharedPreferences sharedPreferences = context.getSharedPreferences("login", Context.MODE_PRIVATE);
        String mobile = sharedPreferences.getString("mobile", "");
        //获取用户信息
        UserInfo userByName = UserDatabase.getInstance(context).getUserDao().getUserByMobile(mobile);
        //拼接房间号
        String roomId = userByName.getMobile() + userByName.getHouse_id();
//        String roomId="123";
        return roomId;
    }

    //**************************************声网****************************************************

    /**
     * 初始化Agora，创建 RtcEngine 对象
     */
    private void initializeAgoraEngine() {
        try {
            //创建 RtcEngine 实例
            mRtcEngine = RtcEngine.create(getActivity(), "e9cbe168d89941938a333e51f6aa327c", mRtcEventHandler);
        } catch (Exception e) {

            throw new RuntimeException("Agora初始化失败了，检查一下是哪儿出错了\n" + Log.getStackTraceString(e));
        }
        //设置频道场景
        mRtcEngine.setChannelProfile(Constants.CHANNEL_PROFILE_LIVE_BROADCASTING);
        //设置直播场景下的用户角色
        mRtcEngine.setClientRole(1);
    }

    /**
     * 打开视频模式，并设置本地视频属性
     */
    private void setupVideoConfig() {
        mRtcEngine.enableVideo();

        mRtcEngine.setVideoEncoderConfiguration(new VideoEncoderConfiguration(
                VideoEncoderConfiguration.VD_1280x720,
                VideoEncoderConfiguration.FRAME_RATE.FRAME_RATE_FPS_15,
                VideoEncoderConfiguration.STANDARD_BITRATE,
                VideoEncoderConfiguration.ORIENTATION_MODE.ORIENTATION_MODE_FIXED_PORTRAIT));
    }

    /**
     * 开启外部视频采集设备
     * 开启外部音频源模式
     */
    private void setupVideoProfile() {
        if (mRtcEngine.isTextureEncodeSupported()) {
            //指定外部视频采集设备
            mRtcEngine.setExternalVideoSource(true, true, true);
        } else {
            throw new RuntimeException("不能在不支持Texture 编码的设备上工作" + mRtcEngine.isTextureEncodeSupported());
        }
    }

    /**
     * 加入一个频道
     */
    private void joinChannel() {
        //设置直播场景下的用户角色
        mRtcEngine.setClientRole(Constants.CLIENT_ROLE_BROADCASTER);
        //加入频道,如果不指定UID，Agroa将自动生成并分配一个UID
        mRtcEngine.joinChannel(null, roomId, "Extra Optional Data", 0);
        //开始录音
        mRtcEngine.startAudioRecording(saveAudio(), AUDIO_RECORDING_QUALITY_HIGH);
    }

    private String saveAudio() {

        String SDCard = MyApplication.getInstance().getFilePath();
        //获取当前时间戳
        long timeMillis = System.currentTimeMillis();
        //拼接名字
        String fileName = SDCard + "/audio_" + timeMillis + ".WAV";
        return fileName;
    }


    /**
     * IRtcEngineEventHandler 类用于向应用程序发送回调通知
     */
    private final IRtcEngineEventHandler mRtcEventHandler = new IRtcEngineEventHandler() { // Tutorial Step 1
        @Override
        public void onFirstRemoteVideoDecoded(final int uid, int width, int height, int elapsed) { // Tutorial Step 5
        }

        //远端用户加入当前频道回调
        @Override
        public void onUserJoined(int uid, int elapsed) {
            super.onUserJoined(uid, elapsed);
            Log.i("--==>>", "远端用户加入房间 Id:" + uid);
        }

        //远端用户离开当前频道回调
        @Override
        public void onUserOffline(int uid, int reason) {
            Log.i("--==>>", "远端用户离开房间 Id:" + uid);
            getActivity().finish();
        }
    };


    //****************************************眼镜**************************************************
    /**
     * 添加、删除 Surface、设置预览框尺寸
     */
    private SurfaceCallback mSurfaceCallback = new SurfaceCallback() {
        @Override
        public void onSurfaceCreated(Surface surface) {
//            LogUtil.i(TAG, "onSurfaceCreated");
            if (mICameraDevice != null) {
                try {
                    mICameraDevice.addSurface(surface, true);
                } catch (CameraException e) {
                    e.printStackTrace();
                }

            }
        }

        @Override
        public void onSurfaceChanged(Surface surface, int width, int height) {
//            LogUtil.i(TAG, "onSurfaceChanged");

        }

        @Override
        public void onSurfaceDestroy(Surface surface) {
//            LogUtil.i(TAG, "onSurfaceDestroy");
            if (mICameraDevice != null) {
                try {
                    if (mCameraView.getSurface() != null) {
                        mICameraDevice.removeSurface(mCameraView.getSurface());
                    }
                } catch (CameraException e) {
                    e.printStackTrace();
                }
            }
        }

        @Override
        public void onSurfaceUpdate(Surface surface) {
        }
    };

    /**
     * 连接状态监听
     */
    CameraStatusListener mCameraStatusListener = new CameraStatusListener() {
        @Override
        public void onCameraOpened() {
        }

        @Override
        public void onCameraConnected() {
            //******************************************************************************************************************************
            Log.i("--==>>", "连接成功");
            try {
                openDisplay();
            } catch (BaseException e) {
                e.printStackTrace();
            }
            if (mICameraDevice != null) {
                try {
                    if (mCameraView.getSurface() != null) {
                        mICameraDevice.addSurface(mCameraView.getSurface(), false);
                        mICameraDevice.setFrameCallback(mIFrameCallback, PixelFormat.PIXEL_FORMAT_NV21);
                    } else {
                    }
                } catch (CameraException e) {
                    e.printStackTrace();
                    Log.i("--==>>", e.getMessage());
                }
            }
        }

        @Override
        public void onCameraDisconnected() {
            Log.i("--==>>", "断开连接");
            if (mICameraDevice != null) {
                try {
                    if (mCameraView.getSurface() != null) {
                        mICameraDevice.removeSurface(mCameraView.getSurface());
                    }
                } catch (CameraException e) {
                    e.printStackTrace();
                }
            }
        }

        @Override
        public void onCameraClosed() {
            Log.i("--==>>", "摄像头关闭");
        }

        @Override
        public void onError(int code) {
            Log.i("--==>>", "出错");
        }
    };

    /**
     * 获取 Camera 预览数据
     */
    private IFrameCallback mIFrameCallback = new IFrameCallback() {
        //frame 为帧数据
        @Override
        public void onFrameAvailable(final byte[] frame) {
            //在获得视频数据的时候调用 Push 方法将数据传送出去
            AgoraVideoFrame vf = new AgoraVideoFrame();
            vf.format = AgoraVideoFrame.FORMAT_NV21;
            vf.timeStamp = System.currentTimeMillis();
            vf.stride = 1280;
            vf.height = 720;
            vf.syncMode = true;
            vf.buf = frame;
            vf.rotation = 270;
            //声网通过pushExternalVideoFrame将视频发送给SDK
            boolean result = mRtcEngine.pushExternalVideoFrame(vf);
//            Log.i("--==>>", "推流" + result);
        }
    };


    private void openDisplay() throws BaseException {
        if (mLcdClient == null) {
            mLcdClient = (ILCDClient) LLVisionGlass3SDK.getInstance().getGlass3Client
                    (IGlass3Device.Glass3DeviceClient.LCD);
        }
        if (mGlassDisplay == null) {
            mGlassDisplay = mLcdClient.getGlassDisplay(mGlass3Device);
        }
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.layout_glass_screen, null);
        mGlassDisplay.createCaptureScreen(getActivity(), view);
        //回音噪音消除
        mGlass3Device.setAudioEchoCancelation(true);
    }

    /**
     * 服务连接与断开、设备插入与拔出状态监听
     */
    private ConnectionStatusListener mConnectionStatusListener = new ConnectionStatusListener() {
        //服务连接状态回调
        @Override
        public void onServiceConnected(List<IGlass3Device> glass3Devices) {
            ToastUtils.showLong(getActivity(), "onServiceConnected");
//            LogUtil.i(TAG, "onServiceConnected");
            Log.i("--==>>", "服务连接");
        }

        //服务断开状态回调
        @Override
        public void onServiceDisconnected() {
            ToastUtils.showLong(getActivity(), "onServiceDisconnected");
//            LogUtil.i(TAG, "onServiceDisconnected");
            Log.i("--==>>", "服务断开");
        }

        //设备插入连接状态回调
        @Override
        public void onDeviceConnect(IGlass3Device device) {
            ToastUtils.showLong(getActivity(), "onDeviceConnect");
//            LogUtil.i(TAG, "onDeviceConnect");
            Log.i("--==>>", "设备插入");
            //断开摄像头
            if (mICameraDevice != null) {
                try {
                    mICameraDevice.disconnect();
                } catch (CameraException e) {
                    e.printStackTrace();
                }
                mICameraDevice.release();
                mICameraDevice = null;
            }
            //停止眼镜端
            if (mGlassDisplay != null) {
                mGlassDisplay.stopCaptureScreen();
                mGlassDisplay = null;
            }
            //open new camera
            mGlass3Device = device;
            if (device != null && mCameraClient != null) {
                try {
                    mICameraDevice = mCameraClient.openCamera(device, mCameraStatusListener);
                    mICameraDevice.setPreviewSize(mWidth, mHeight, mFps);
                    mCameraView.setAspectRatio(mWidth / (float) mHeight);
                    mICameraDevice.connect();
                } catch (CameraException e) {
                    LogUtil.e(TAG, e);
                } catch (BaseException e) {
                    e.printStackTrace();
                }
            }
        }

        //设备拔出状态回调
        @Override
        public void onDeviceDisconnect(IGlass3Device device) {
            if (device != null && mGlass3Device != null) {
                ToastUtils.showLong(getActivity(), "onDeviceDisconnect deviceId = " +
                        device.getDeviceId() + " mGlass3Device = " + mGlass3Device.getDeviceId());
//                LogUtil.i("CameraClient", "onDeviceDisconnect deviceId = " +
//                        device.getDeviceId() + " mGlass3Device = " + mGlass3Device.getDeviceId());
            }
            if (mICameraDevice != null) {
//                LogUtil.i(TAG, "onDeviceDisconnect deviceId = " + mICameraDevice.isCameraOpened());
            }
            //停止眼镜端
            if (mGlassDisplay != null) {
                mGlassDisplay.stopCaptureScreen();
                mGlassDisplay = null;
            }
            if (mICameraDevice != null) {
                mICameraDevice.release();
                mICameraDevice = null;
            }
            mGlass3Device = null;
        }

        //出错
        @Override
        public void onError(int code, String msg) {
            ToastUtils.showLong(getActivity(), "onError");
//            LogUtil.i(TAG, "onError");
            Log.i("--==>>", "服务出错");
        }
    };

    @Override
    public void onDestroyView() {
        Toast.makeText(getActivity(), "用户已离开通话", Toast.LENGTH_SHORT).show();
        // 结束录音
        mRtcEngine.stopAudioRecording();
        //离开当前频道
        mRtcEngine.leaveChannel();
        mRtcEngine = null;
        super.onDestroyView();
    }

    @Override
    public synchronized void onDestroy() {
        super.onDestroy();
        if (mICameraDevice != null) {
            mICameraDevice.release();
            mICameraDevice = null;
        }
        if (mGlassDisplay != null) {
            mGlassDisplay.stopCaptureScreen();
            mGlassDisplay = null;
        }
        LLVisionGlass3SDK.getInstance().unRegisterConnectionListener(mConnectionStatusListener);
        mGlass3Device = null;
        //停止通话
        new CallInServerCenter(getActivity()).closeCall();
        getActivity().finish();
        startActivity(new Intent(getActivity(), FormCommitActivity.class));
        Log.i("--==>>", "资源释放完毕");
    }

    @Override
    public void onClick(View v) {
        //停止通话
        new CallInServerCenter(getActivity()).closeCall();
        getActivity().finish();
    }


}
