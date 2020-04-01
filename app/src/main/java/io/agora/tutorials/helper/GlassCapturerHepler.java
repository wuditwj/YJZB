package io.agora.tutorials.helper;

import android.content.Context;

import com.llvision.glxss.common.utils.ToastUtils;


import io.agora.rtc.mediaio.IVideoFrameConsumer;
import io.agora.rtc.mediaio.IVideoSource;
import io.agora.rtc.video.AgoraVideoFrame;

public class GlassCapturerHepler implements IVideoSource {
    private IVideoFrameConsumer mIVideoFrameConsumer;
    private byte[] frame;

    private boolean mHasStarted;
    private Context context;


    public void setFrame(byte[] frame) {
        this.frame = frame;
    }

    public GlassCapturerHepler(Context context) {
        this.context = context;
    }

    @Override
    public boolean onInitialize(IVideoFrameConsumer consumer) {
        ToastUtils.showShort(context,"consumer");
        try {
            this.mIVideoFrameConsumer = consumer;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    @Override
    public boolean onStart() {
        mHasStarted = true;
        setByteArrayFrame();
        ToastUtils.showShort(context,"onStart");
        return true;
    }

    @Override
    public void onStop() {
        mHasStarted = false;
        ToastUtils.showShort(context,"onStop");
    }

    @Override
    public void onDispose() {
        ToastUtils.showShort(context,"onDispose");
        mIVideoFrameConsumer = null;
    }

    @Override
    public int getBufferType() {
        return AgoraVideoFrame.FORMAT_NV21;
    }

    private void setByteArrayFrame() {
        if (mHasStarted && mIVideoFrameConsumer != null) {
            mIVideoFrameConsumer.consumeByteArrayFrame(frame, AgoraVideoFrame.FORMAT_NV21, 1280, 702, 0,
                    System.currentTimeMillis());
        }
    }

}
