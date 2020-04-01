package io.agora.tutorials;

import android.content.Context;
import android.util.Log;

import io.agora.rtc.mediaio.TextureSource;

import static android.os.SystemClock.elapsedRealtime;
import static io.agora.rtc.mediaio.MediaIO.PixelFormat.TEXTURE_OES;

public class GlassTextureCamera extends TextureSource {
    private static final String TAG = "GlassTextureCamera";
    private byte[] data;

    public GlassTextureCamera(Context context, int width, int height,byte[] type) {

        super(null, width, height);
        Log.i(TAG, "GlassTextureCamera");
        this.data = type;

    }

    @Override
    public void onTextureFrameAvailable(int oesTextureId, float[] transformMatrix, long timestampNs) {
        super.onTextureFrameAvailable(oesTextureId, transformMatrix, timestampNs);

        timestampNs = elapsedRealtime();
        int rotation = 0;

        if (mConsumer != null && mConsumer.get() != null) {
            mConsumer.get().consumeByteArrayFrame(data, TEXTURE_OES.intValue(), mWidth, mHeight, rotation, timestampNs);
        }


    }

    @Override
    protected boolean onCapturerOpened() {
        Log.e(TAG, "onCapturerOpened");
        return true;
    }

    @Override
    protected boolean onCapturerStarted() {
        Log.e(TAG, "onCapturerStarted");
        return true;
    }

    @Override
    protected void onCapturerStopped() {
        Log.e(TAG, "onCapturerStopped");
    }

    @Override
    protected void onCapturerClosed() {
        Log.e(TAG, "onCapturerClosed");
    }
}
