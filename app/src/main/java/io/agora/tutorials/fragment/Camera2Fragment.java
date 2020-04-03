package io.agora.tutorials.fragment;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Point;
import android.os.Bundle;
import android.support.annotation.Nullable;
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
import com.tencent.liteav.TXLiteAVCode;
import com.tencent.trtc.TRTCCloud;
import com.tencent.trtc.TRTCCloudDef;
import com.tencent.trtc.TRTCCloudListener;

import java.util.List;

import io.agora.rtc.video.AgoraVideoFrame;
import io.agora.tutorials.application.MyApplication;
import io.agora.tutorials.call.CallInServerCenter;
import io.agora.tutorials.customizedvideosource.R;
import io.agora.tutorials.db.UserDatabase;
import io.agora.tutorials.entity.UserInfo;
import io.agora.tutorials.utils.GenerateTestUserSig;

import static com.tencent.trtc.TRTCCloudDef.TRTC_APP_SCENE_VIDEOCALL;
import static com.tencent.trtc.TRTCCloudDef.TRTC_VIDEO_RENDER_MODE_FIT;

public class Camera2Fragment extends BaseFragment implements View.OnClickListener {

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

    //用户手机
    private String mobile;

    //房间号
    private String roomId;

    private TRTCCloud mTRTCCloud;

    public Camera2Fragment() {
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
                LogUtil.e("服务尚未连接.");
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
        rootView.findViewById(R.id.icon_close).setOnClickListener(this);
        //添加、删除 Surface、设置预览框尺寸
        mCameraView.setSurfaceCallback(mSurfaceCallback);
        mCameraView.setAspectRatio(mWidth / (float) mHeight);

        //打开摄像头
        if (mGlass3Device != null) {
            Log.i("--==>>", "mGlass3Device不空");
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
        getRoomId(getActivity());
        //腾讯云服务
        initTRTCCloud();
        return rootView;
    }

    //****************************************眼镜**************************************************
    /**
     * 添加、删除 Surface、设置预览框尺寸
     */
    private SurfaceCallback mSurfaceCallback = new SurfaceCallback() {
        @Override
        public void onSurfaceCreated(Surface surface) {
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

        }

        @Override
        public void onSurfaceDestroy(Surface surface) {
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
            // TODO: 2020/4/2
            TRTCCloudDef.TRTCVideoFrame trtcVideoFrame=new TRTCCloudDef.TRTCVideoFrame();
            //视频像素格式
            trtcVideoFrame.pixelFormat=TRTCCloudDef.TRTC_VIDEO_PIXEL_FORMAT_NV21;
            //视频数据包装格式
            trtcVideoFrame.bufferType=TRTCCloudDef.TRTC_VIDEO_BUFFER_TYPE_BYTE_ARRAY;
            trtcVideoFrame.data=frame;
//            trtcVideoFrame.texture=new TRTCCloudDef.TRTCTexture();
            trtcVideoFrame.width=1280;
            trtcVideoFrame.height=720;
            trtcVideoFrame.timestamp=0;
            mTRTCCloud.sendCustomVideoData(trtcVideoFrame);
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
            Log.i("--==>>", "服务连接");
        }

        //服务断开状态回调
        @Override
        public void onServiceDisconnected() {
            ToastUtils.showLong(getActivity(), "onServiceDisconnected");
            Log.i("--==>>", "服务断开");
        }

        //设备插入连接状态回调
        @Override
        public void onDeviceConnect(IGlass3Device device) {
            ToastUtils.showLong(getActivity(), "onDeviceConnect");
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
                LogUtil.i("CameraClient", "onDeviceDisconnect deviceId = " +
                        device.getDeviceId() + " mGlass3Device = " + mGlass3Device.getDeviceId());
            }
            if (mICameraDevice != null) {
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
            Log.i("--==>>", "服务出错");
        }
    };


    //**************************腾讯云**************************************************************

    private String saveAudio() {

        String SDCard = MyApplication.getInstance().getFilePath();
        //获取当前时间戳
        long timeMillis = System.currentTimeMillis();
        //拼接名字
        String fileName = SDCard + "/audio_" + timeMillis + ".WAV";
        return fileName;
    }

    private void getRoomId(Context context) {
        //获取当前登录的手机号
        SharedPreferences sharedPreferences = context.getSharedPreferences("login", Context.MODE_PRIVATE);
        mobile = sharedPreferences.getString("mobile", "");
        //获取用户信息
        UserInfo userByName = UserDatabase.getInstance(context).getUserDao().getUserByMobile(mobile);
        //拼接房间号
        roomId = mobile + userByName.getHouse_id();
//       roomId="123";
    }

    private void initTRTCCloud() {
        // 创建 trtcCloud 实例
        mTRTCCloud = TRTCCloud.sharedInstance(getActivity().getApplicationContext());
        mTRTCCloud.setListener(new TRTCCloudListener() {
            //错误通知监听，错误通知意味着 SDK 不能继续运行
            @Override
            public void onError(int i, String s, Bundle bundle) {
                Log.d("--==>>", "sdk 不能继续运行");
                if (getActivity() != null) {
                    Toast.makeText(getActivity(), "不支持腾讯云服务", Toast.LENGTH_SHORT).show();
                    if (i == TXLiteAVCode.ERR_ROOM_ENTER_FAIL) {
                        getActivity().finish();
                    }
                }
            }

            //进房间状态监听
            @Override
            public void onEnterRoom(long l) {
                if (l > 0) {
                    Log.i("--==>>", "进房成功，总计耗时" + l + "ms");
                } else {
                    Log.i("--==>>", "进房失败，错误码" + l);
                }
            }

            //当房间中有其他用户在上行音频数据时，SDK 会自动播放远端用户的声音。
            @Override
            public void onUserAudioAvailable(String s, boolean b) {
                super.onUserAudioAvailable(s, b);
            }

            //有用户加入房间
            @Override
            public void onRemoteUserEnterRoom(String s) {
                Log.i("--==>>","用户加入房间id:"+s);
                //远端用户旋转画面
                mTRTCCloud.setRemoteViewRotation(s, TRTCCloudDef.TRTC_VIDEO_ROTATION_90);
                //图像铺满屏幕，超出显示视窗的视频部分将被裁剪
                mTRTCCloud.setRemoteViewFillMode(s,TRTCCloudDef.TRTC_VIDEO_RENDER_MODE_FILL);
            }

            //有用户离开房间
            @Override
            public void onRemoteUserLeaveRoom(String s, int i) {
                String str="";
                switch (i){
                    case 1:
                        str="用户主动退出房间";
                        break;
                    case 2:
                        str="用户超时退出";
                        break;
                    case 3:
                        str="用户被踢出房间";
                        break;
                }
                Log.i("--==>>","用户离开房间：原因:"+str);
                getActivity().finish();
            }

            //退房监听
            @Override
            public void onExitRoom(int i) {
                Log.i("--==>>", "退出房间成功");
            }

            //SDK跟服务器断开
            @Override
            public void onConnectionLost() {
                Toast.makeText(getActivity().getApplicationContext(),"SDK跟服务器断开",Toast.LENGTH_SHORT).show();
                Log.i("--==>>","SDK跟服务器断开");
                getActivity().finish();
            }
        });
        //创建房间并进入
        enterRoom();
    }

    private void enterRoom() {
        //开启自定义视频采集
        mTRTCCloud.enableCustomVideoCapture(true);
        TRTCCloudDef.TRTCParams trtcParams = new TRTCCloudDef.TRTCParams();
        //应用 ID
        trtcParams.sdkAppId = 1400344863;
        trtcParams.userId = mobile;
        //基于 userId 可以计算出 userSig
        trtcParams.roomId = 123;
        //房间号
        trtcParams.userSig = GenerateTestUserSig.genTestUserSig(mobile);
        //创建并进入房间
        mTRTCCloud.enterRoom(trtcParams, TRTC_APP_SCENE_VIDEOCALL);
        //可以设定本地视频画面的显示模式
        mTRTCCloud.setLocalViewFillMode(TRTC_VIDEO_RENDER_MODE_FIT);
        //开启本地的摄像头，并将采集到的画面编码并发送出去。
//        mTRTCCloud.startLocalPreview(mIsFrontCamera, mLocalView);
        //开启本地的麦克风采集，并将采集到的声音编码并发送出去。
        mTRTCCloud.startLocalAudio();
        //保存录音
        TRTCCloudDef.TRTCAudioRecordingParams trtcAudioRecordingParams=new TRTCCloudDef.TRTCAudioRecordingParams();
        trtcAudioRecordingParams.filePath=saveAudio();
        mTRTCCloud.startAudioRecording(trtcAudioRecordingParams);
    }


    @Override
    public void onDestroyView() {
        Toast.makeText(getActivity(), "用户已离开通话", Toast.LENGTH_SHORT).show();
        // 结束录音
//        mTRTCCloud.stopAudioRecording();
        super.onDestroyView();
        if (mICameraDevice != null) {
            mICameraDevice.release();
            mICameraDevice = null;
        }
        if (mGlassDisplay != null) {
            mGlassDisplay.stopCaptureScreen();
            mGlassDisplay = null;
        }
        //停止通话
        new CallInServerCenter(getActivity()).closeCall();
        getActivity().finish();
    }

    @Override
    public synchronized void onDestroy() {
        super.onDestroy();
        LLVisionGlass3SDK.getInstance().unRegisterConnectionListener(mConnectionStatusListener);
        mGlass3Device = null;
        //离开当前频道
        // 调用退房后请等待 onExitRoom 事件回调
        mTRTCCloud.exitRoom();
        Log.i("--==>>", "资源释放完毕");
    }

    @Override
    public void onClick(View v) {
        //停止通话
        new CallInServerCenter(getActivity()).closeCall();
        getActivity().finish();
    }
}
